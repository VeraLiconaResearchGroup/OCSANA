/**
 * Implementation of the RS algorithm for finding minimal hitting sets
 *
 * Copyright Vera-Licona Research Group (C) 2015
 * @author Andrew Gainer-Dewar, Ph.D. <andrew.gainer.dewar@gmail.com>
 *
 * This software is licensed under the Artistic License 2.0, see the
 * LICENSE file or
 * http://www.opensource.org/licenses/artistic-license-2.0.php for
 * details
 **/

package org.compsysmed.ocsana.internal.algorithms.mhs;

// Java imports
import java.util.*;

import java.util.concurrent.ConcurrentLinkedQueue;
import java.util.concurrent.ForkJoinPool;

// Cytoscape imports
import org.cytoscape.work.Tunable;

import org.cytoscape.model.CyNode;
import org.cytoscape.model.CyNetwork;

// OCSANA imports

/**
 * The 'RS' algorithm for finding minimal hitting sets.
 **/

public class RSAlgorithm extends AbstractMHSAlgorithm {
    public static final String NAME = "RS algorithm";
    public static final String SHORTNAME = "RS";

    // Tunables for threading
    // TODO: Add note that not specifying will use *all* hardware units
    @Tunable(description = "Specify number of threads",
             gravity = 401,
             tooltip="By default, all CPUs will be utilized",
             groups = {AbstractMHSAlgorithm.CONFIG_GROUP + ": " + SHORTNAME})
    public Boolean configureThreads = false;

    protected Integer numThreads = 1;
    @Tunable(description = "Number of threads",
             gravity = 401.1,
             dependsOn = "configureThreads=true",
             groups = {AbstractMHSAlgorithm.CONFIG_GROUP + ": " + SHORTNAME})
    public Integer getNumThreads () {
        return numThreads;
    }

    public void setNumThreads (Integer numThreads) {
        if (numThreads == null) {
            throw new NullPointerException("Number of threads is null.");
        }

        synchronized(this) {
            if (numThreads <= 0) {
                throw new IllegalArgumentException("Number of threads must be positive!");
            } else {
                this.numThreads = numThreads;
            }
        }
    }

    // Tunables for bounded-cardinality search
    // TODO: Add note that not using this may take a long time
    @Tunable(description = "Restrict search to small CIs",
             gravity = 402,
             tooltip="Unbounded search may take a very long time!",
             groups = {AbstractMHSAlgorithm.CONFIG_GROUP + ": " + SHORTNAME})
    public Boolean useMaxCardinality = true;

    protected Integer maxCardinality = 5;
    @Tunable(description = "Maximum size of CI to find",
             gravity = 402.1,
             dependsOn = "useMaxCardinality=true",
             groups = {AbstractMHSAlgorithm.CONFIG_GROUP + ": " + SHORTNAME})
    public Integer getMaxCardinality () {
        return maxCardinality;
    }

    public void setMaxCardinality (Integer maxCardinality) {
        if (maxCardinality == null) {
            throw new NullPointerException("Maximum cardinality is null.");
        }

        synchronized(this) {
            if (maxCardinality <= 0) {
                throw new IllegalArgumentException("Maximum size must be positive!");
            } else {
                this.maxCardinality = maxCardinality;
            }
        }
    }

    // Tunables for bounded-length search
    @Tunable(description = "Consider a restricted number of candidates",
             gravity = 403,
             groups = {AbstractMHSAlgorithm.CONFIG_GROUP + ": " + SHORTNAME})
    public Boolean useMaxCandidates = false;

    protected Integer maxCandidates = 1000000;
    @Tunable(description = "Maximum number of candidates to consider",
             gravity = 403.1,
             dependsOn = "useMaxCandidates=true",
             groups = {AbstractMHSAlgorithm.CONFIG_GROUP + ": " + SHORTNAME})
    public Integer getMaxCandidates () {
        return maxCandidates;
    }

    public void setMaxCandidates (Integer maxCandidates) {
        if (maxCandidates == null) {
            throw new NullPointerException("Maximum candidates is null.");
        }

        synchronized(this) {
            if (maxCandidates <= 0) {
                throw new IllegalArgumentException("Maximum candidate count must be positive!");
            } else {
                this.maxCandidates = maxCandidates;
            }
        }
    }

    // No docstring because the interface has one
    public List<Set<CyNode>> MHSes (Iterable<? extends Iterable<CyNode>> sets) {
        HypergraphOfSetsOfCyNodes inputHypergraph
            = new HypergraphOfSetsOfCyNodes(sets);

        Hypergraph resultHypergraph = transversalHypergraph(inputHypergraph);

        List<Set<CyNode>> result =
            inputHypergraph.getCyNodeSetsFromHypergraph(resultHypergraph);

        return result;
    };

    /**
     * Compute MHSes of a given hypergraph.
     *
     * @param H  the hypergraph whose MHSes we should find
     **/
    public Hypergraph transversalHypergraph(Hypergraph H) {
        // Generate inputs to algorithm
        Hypergraph T = H.transpose();
        SHDCounters counters = new SHDCounters();
        ConcurrentLinkedQueue<BitSet> results = new ConcurrentLinkedQueue<>();

        // Candidate hitting set, initially empty
        BitSet S = new BitSet(H.numVerts());

        // Which edges each vertex is critical for (initially all empty)
        Hypergraph crit = new Hypergraph (H.numEdges(), H.numVerts());

        // Which edges are uncovered (initially full)
        BitSet uncov = new BitSet(H.numEdges());
        uncov.set(0, H.numEdges());

        // Which vertices are known to be violating (initially empty)
        BitSet violatingVertices = new BitSet (H.numVerts());

        // Set up and run the calculation
        RSRecursiveTask calculation = new RSRecursiveTask(H, T, S, crit, uncov, violatingVertices, maxCardinality, maxCandidates, counters, results);

        ForkJoinPool pool;
        if (numThreads > 0) {
            pool = new ForkJoinPool (numThreads);
        } else {
            pool = new ForkJoinPool ();
        }
        pool.invoke(calculation);

        // Wait for all algorithms to complete
        pool.invoke(new TaskWaiter());

        // Construct a Hypergraph with the resulting MHSes
        Hypergraph MHSes = new Hypergraph(H.numVerts());
        Iterator<BitSet> itr = results.iterator();
        while (itr.hasNext()) {
            MHSes.add(itr.next());
        }

        return MHSes;
    }

    private class RSRecursiveTask extends SHDRecursiveTask {
        BitSet violatingVertices;

        /**
         * Recursive task for the RS algorithm
         *
         * @param H  {@code Hypergraph} to process
         * @param T  transversal hypergraph of H
         * @param S  candidate hitting set to process
         * @param crit for each vertex v of H, crit[v] records the edges
         * for which v is critical
         * @param uncov  which edges are uncovered (must be nonempty)
         * @param violatingVertices  which vertices are known to be
         * violating for S
         * @param maxCardinality largest size hitting set to consider
         * (0 to find all, must be larger than {@code S.cardinality()}
         * otherwise)
         * @param maxCandidates  largest number of candidates to consider before
         * termination (0 to run to termination)
         * @param counters  to store counts of various subalgorithms
         * @param confirmedMHSes  to store any confirmed MHSes
         **/
        RSRecursiveTask (Hypergraph H,
                         Hypergraph T,
                         BitSet S,
                         Hypergraph crit,
                         BitSet uncov,
                         BitSet violatingVertices,
                         Integer maxCardinality,
                         Integer maxCandidates,
                         SHDCounters counters,
                         ConcurrentLinkedQueue<BitSet> confirmedMHSes) {
            this.H = H;
            this.T = T;
            this.S = S;
            this.crit = crit;
            this.uncov = uncov;
            this.violatingVertices = violatingVertices;
            this.maxCardinality = maxCardinality;
            this.maxCandidates = maxCandidates;
            this.counters = counters;
            this.confirmedMHSes = confirmedMHSes;

            // Argument checking
            if (H.numEdges() == 0) {
                throw new IllegalArgumentException("Cannot process an edgeless hypergraph.");
            }

            if (uncov.isEmpty()) {
                throw new IllegalArgumentException("uncov cannot be empty.");
            }

            if ((maxCardinality > 0) && (maxCardinality < S.cardinality())) {
                throw new IllegalArgumentException("S must be no larger than than maxCardinality.");
            }

            if (maxCandidates < 0) {
                throw new IllegalArgumentException("maxCandidates must be non-negative.");
            }

            if (violatingVertices.intersects(S)) {
                throw new IllegalArgumentException("Vertices in S cannot be violating.");
            }
        }

        /**
         * Run the algorithm.
         **/
        @Override
        protected void compute() {
            // Handle maxCandidates
            if ((counters.iterations.getAndIncrement() >= maxCandidates) && (maxCandidates > 0)) {
                return;
            }

            // Get an uncovered edge
            Integer searchEdgeIndex = uncov.nextSetBit(0);
            BitSet searchEdge = (BitSet) H.get(searchEdgeIndex).clone();

            // Remove known violating vertices
            searchEdge.andNot(violatingVertices);

            // Check remaining vertices for violation and store the
            // results in a new BitSet
            BitSet newViolatingVertices = (BitSet) violatingVertices.clone();
            for (int v = searchEdge.nextSetBit(0); v >= 0; v = searchEdge.nextSetBit(v+1)) {
                if (vertexWouldViolate(v)) {
                    // Remove newfound violators from the search edge
                    counters.violators.getAndIncrement();
                    newViolatingVertices.set(v);
                    searchEdge.clear(v);
                }
            }

            // Iterate through the vertices in the search edge in reverse order
            for (int v = searchEdge.length(); (v = searchEdge.previousSetBit(v-1)) >= 0; ) {
                counters.updateLoopRuns.getAndIncrement();

                // Update crit and uncov
                Map<Integer, BitSet> critMark = updateCritAndUncov(v);

                // Check the critical edge condition
                if (anyEdgeCriticalAfter(searchEdgeIndex)) {
                    restoreCritAndUncov(critMark, v);
                    continue;
                }

                // If we made it this far, S+v is valid
                S.set(v);

                // Process the new candidate S
                if ((uncov.isEmpty()) && ((maxCardinality == 0) || (S.cardinality() <= maxCardinality))) {
                    // S is a genuine MHS, so we store it and move on
                    BitSet cloneS = (BitSet) S.clone();
                    confirmedMHSes.add(cloneS);
                } else if ((maxCardinality == 0) || (S.cardinality() < maxCardinality)) {
                    // S is a viable candidate, so we fork a new job to process it
                    if ((getQueuedTaskCount() < 4) && (uncov.cardinality() > 2)) {
                        // Spawn a new task if the queue is getting
                        // low.  We define "low" as "has fewer than
                        // four algorithms waiting", which is entirely
                        // ad-hoc and should be tuned before serious
                        // use.

                        // Make defensive copies of mutable variables
                        BitSet cloneS = (BitSet) S.clone();
                        Hypergraph cloneCrit = new Hypergraph(crit);
                        BitSet cloneUncov = (BitSet) uncov.clone();
                        BitSet cloneViolatingVertices = (BitSet) newViolatingVertices.clone();

                        RSRecursiveTask child = new RSRecursiveTask(H, T, cloneS, cloneCrit, cloneUncov, cloneViolatingVertices, maxCardinality, maxCandidates, counters, confirmedMHSes);
                        child.fork();
                    } else {
                        // Do the work in this thread without forking or copying
                        RSRecursiveTask child = new RSRecursiveTask(H, T, S, crit, uncov, newViolatingVertices, maxCardinality, maxCandidates, counters, confirmedMHSes);
                        child.invoke();
                    }
                }

                // Restore helper variables and proceed to the next vertex
                S.clear(v);
                restoreCritAndUncov(critMark, v);
            }
        }

        /**
         * Determine whether any vertex in S has its first critical
         * edge after v.
         *
         * @param v  the vertex to search from
         **/
        protected Boolean anyEdgeCriticalAfter(Integer v) {
            // Iterate through vertices in S
            for (int i = S.nextSetBit(0); i >= 0; i = S.nextSetBit(i+1)) {
                // Check first critical edge for vertex i
                int iFirstCritEdge = crit.get(i).nextSetBit(0);
                if (iFirstCritEdge < 0) {
                    throw new IllegalArgumentException("Vertex in S has no critical edges.");
                } else if (iFirstCritEdge >= v) {
                    return true;
                }
            }

            return false;
        }
    }

    public String fullName () {
        return this.NAME;
    }

    public String shortName () {
        return this.SHORTNAME;
    }

    public String toString () {
        return this.shortName();
    }
}
