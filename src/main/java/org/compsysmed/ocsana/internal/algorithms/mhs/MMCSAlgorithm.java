/**
 * Implementation of the MMCS algorithm for finding minimal hitting sets
 *
 * Copyright Vera-Licona Research Group (C) 2015
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
import org.cytoscape.work.util.BoundedInteger;

import org.cytoscape.model.CyNode;
import org.cytoscape.model.CyNetwork;

// OCSANA imports

/**
 * The 'MMCS' algorithm for finding minimal hitting sets
 **/

public class MMCSAlgorithm extends AbstractMHSAlgorithm {
    public static final String NAME = "MMCS algorithm";
    public static final String SHORTNAME = "MMCS";

    // Tunables for threading
    @Tunable(description = "Specify number of threads",
             gravity = 410,
             tooltip="By default, all CPUs will be utilized",
             groups = {AbstractMHSAlgorithm.CONFIG_GROUP + ": " + SHORTNAME})
    public Boolean configureThreads = false;

    @Tunable(description = "Number of threads",
             gravity = 411,
             dependsOn = "configureThreads=true",
             groups={AbstractMHSAlgorithm.CONFIG_GROUP + ": " + SHORTNAME})
    public BoundedInteger numThreads;

    // Tunables for bounded-cardinality search
    @Tunable(description = "Restrict search to small CIs",
             gravity = 420,
             tooltip="Unbounded search may take a very long time!",
             groups = {AbstractMHSAlgorithm.CONFIG_GROUP + ": " + SHORTNAME})
    public Boolean useMaxCardinality = true;

    @Tunable(description = "Maximum size of CI to find",
             gravity = 421,
             dependsOn = "useMaxCardinality=true",
             groups = {AbstractMHSAlgorithm.CONFIG_GROUP + ": " + SHORTNAME})
    public BoundedInteger maxCardinalityBInt;

    // Tunables for bounded-length search
    @Tunable(description = "Consider a restricted number of candidates",
             gravity = 430,
             groups = {AbstractMHSAlgorithm.CONFIG_GROUP + ": " + SHORTNAME})
    public Boolean useMaxCandidates = false;

    @Tunable(description = "Maximum number of candidates to consider",
             gravity = 431,
             dependsOn = "useMaxCandidates=true",
             groups = {AbstractMHSAlgorithm.CONFIG_GROUP + ": " + SHORTNAME})
    public BoundedInteger maxCandidatesBInt;

    public MMCSAlgorithm () {
        super();
        numThreads = new BoundedInteger(1, 1, Runtime.getRuntime().availableProcessors(), false, false);
        maxCardinalityBInt = new BoundedInteger(1, 6, 20, false, false);
        maxCandidatesBInt = new BoundedInteger(1, 1, 99999999, false, false);
    }

    // No docstring because the interface has one
    @Override
    public List<Set<CyNode>> MHSes (Collection<? extends Collection<CyNode>> sets) {
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
    public Hypergraph transversalHypergraph (Hypergraph H) {
        // Generate inputs to algorithm
        Hypergraph T = H.transpose();
        SHDCounters counters = new SHDCounters();
        ConcurrentLinkedQueue<BitSet> results = new ConcurrentLinkedQueue<>();

        // Handle argument processing
        int maxCardinality;
        if (useMaxCardinality) {
            maxCardinality = maxCardinalityBInt.getValue();
        } else {
            maxCardinality = 0;
        }

        int maxCandidates;
        if (useMaxCandidates) {
            maxCandidates = maxCardinalityBInt.getValue();
        } else {
            maxCandidates = 0;
        }

        // Candidate hitting set, initially empty
        BitSet S = new BitSet(H.numVerts());

        // Eligible vertices, initially full
        BitSet CAND = new BitSet(H.numVerts());
        CAND.set(0, H.numVerts());

        // Which edges each vertex is critical for (initially all empty)
        Hypergraph crit = new Hypergraph (H.numEdges(), H.numVerts());

        // Which edges are uncovered (initially full)
        BitSet uncov = new BitSet(H.numEdges());
        uncov.set(0, H.numEdges());

        // Set up and run the calculation
        MMCSRecursiveTask calculation = new MMCSRecursiveTask(H, T, S, CAND, crit, uncov, maxCardinality, maxCandidates, counters, results);

        ForkJoinPool pool;
        if (configureThreads) {
            pool = new ForkJoinPool(numThreads.getValue());
        } else {
            pool = new ForkJoinPool();
        }

        pool.invoke(calculation);

        // Wait for all algorithms to complete
        pool.invoke(new TaskWaiter());

        // Construct a Hypergraph with the resulting MHSes
        Hypergraph MHSes = new Hypergraph(H.numVerts());
        for (BitSet edge: results) {
            MHSes.add(edge);
        }
        MHSes.updateNumVerts();

        return MHSes;
    }

    private class MMCSRecursiveTask extends SHDRecursiveTask {
        BitSet CAND;

        /**
         * Recursive task for the MMCS algorithm
         *
         * @param H  {@code Hypergraph} to process
         * @param T  transversal hypergraph of H
         * @param S  candidate hitting set to process
         * @param CAND  vertices which are eligible to add to S (must be nonempty)
         * @param crit for each vertex v of H, crit[v] records the edges
         * for which v is critical
         * @param uncov  which edges are uncovered (must be nonempty)
         * @param maxCardinality  the maximum size of MHS to consider
         * @param maxCandidates  the maximum number of candidates to
         * consider before returning
         * @param counters  to store counts of various subalgorithms
         * @param confirmedMHSes  to store any confirmed MHSes
         **/
        MMCSRecursiveTask (Hypergraph H,
                           Hypergraph T,
                           BitSet S,
                           BitSet CAND,
                           Hypergraph crit,
                           BitSet uncov,
                           Integer maxCardinality,
                           Integer maxCandidates,
                           SHDCounters counters,
                           ConcurrentLinkedQueue<BitSet> confirmedMHSes) {
            this.H = H;
            this.T = T;
            this.S = S;
            this.CAND = CAND;
            this.crit = crit;
            this.uncov = uncov;
            this.maxCardinality = maxCardinality;
            this.maxCandidates = maxCandidates;
            this.counters = counters;
            this.confirmedMHSes = confirmedMHSes;

            // Argument checking
            if (H.numEdges() == 0) {
                // Edgeless case is handled in compute()
                return;
            }

            if (CAND.isEmpty()) {
                throw new IllegalArgumentException("CAND cannot be empty.");
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
        }

        /**
         * Run the algorithm.
         **/
        @Override
        protected void compute () {
            // Handle empty hypergraph case
            if (H.numEdges() == 0) {
                return;
            }

            // Handle cancellation
            if (isCanceled()) {
                return;
            }

            // Handle maxCandidates
            if ((counters.iterations.getAndIncrement() >= maxCandidates) && (maxCandidates > 0)) {
                return;
            }

            // Prune the vertices to search
            // Per M+U, find the uncovered edge e with the smallest intersection
            // with CAND
            // We name this intersection C for easy reference
            BitSet C = new BitSet(H.numVerts());
            C.set(0, H.numVerts() - 1);
            for (int e = uncov.nextSetBit(0); e >= 0; e = uncov.nextSetBit(e+1)) {
                BitSet searchIntersection = (BitSet) H.get(e).clone();
                searchIntersection.and(CAND);
                if (searchIntersection.cardinality() < C.cardinality()) {
                    C = searchIntersection;
                }
            }

            // Temporarily remove these vertices from CAND
            CAND.andNot(C);

            // Record which vertices of C were violating for S
            BitSet violators = new BitSet(H.numVerts());

            // Iterate through the vertices in the intersection (in reverse order)
            for (int v = C.length(); (v = C.previousSetBit(v-1)) >= 0; ) {
                counters.updateLoopRuns.getAndIncrement();
                // First, check for violators
                if (vertexWouldViolate(v)) {
                    counters.violators.getAndIncrement();
                    violators.set(v);
                    continue;
                }

                // Add v to S and update crit and uncov
                Map<Integer, BitSet> critMark = updateCritAndUncov(v);
                S.set(v);

                // Process the new candidate S
                if ((uncov.isEmpty()) && ((maxCardinality == 0) || (S.cardinality() <= maxCardinality))) {
                    // S is a genuine MHS, so we store it and move on
                    BitSet cloneS = (BitSet) S.clone();
                    confirmedMHSes.add(cloneS);
                } else if ((!CAND.isEmpty()) && ((maxCardinality == 0) || (S.cardinality() < maxCardinality))) {
                    // S is a viable candidate, so we fork a new job to process it
                    if ((getQueuedTaskCount() < 4) && (uncov.cardinality() > 2)) {
                        // Spawn a new task if the queue is getting
                        // low.  We define "low" as "has fewer than
                        // four algorithms waiting", which is entirely
                        // ad-hoc and should be tuned before serious
                        // use.

                        // Make defensive copies of mutable variables
                        BitSet newS = (BitSet) S.clone();
                        BitSet newCAND = (BitSet) CAND.clone();
                        Hypergraph newcrit = new Hypergraph(crit);
                        BitSet newuncov = (BitSet) uncov.clone();

                        MMCSRecursiveTask child = new MMCSRecursiveTask(H, T, newS, newCAND, newcrit, newuncov, maxCardinality, maxCandidates, counters, confirmedMHSes);
                        child.fork();
                    } else {
                        // Do the work in this thread without forking or copying
                        MMCSRecursiveTask child = new MMCSRecursiveTask(H, T, S, CAND, crit, uncov, maxCardinality, maxCandidates, counters, confirmedMHSes);
                        child.invoke();
                    }
                }

                // Finally, we update CAND, crit, uncov and S
                CAND.set(v);
                S.clear(v);
                restoreCritAndUncov(critMark, v);

            }

            // Restore the violators to CAND before any other run uses it
            CAND.or(violators);
        }
    }

    @Override
    public String fullName () {
        return this.NAME;
    }

    @Override
    public String shortName () {
        return this.SHORTNAME;
    }

    @Override
    public String toString () {
        return this.shortName();
    }
}
