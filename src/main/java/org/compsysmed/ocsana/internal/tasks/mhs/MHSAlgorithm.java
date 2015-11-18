/**
 * Common interface for minimal hitting set generation algorithms
 *
 * Copyright Vera-Licona Research Group (C) 2015
 * @author Andrew Gainer-Dewar, Ph.D. <andrew.gainer.dewar@gmail.com>
 **/

package org.compsysmed.ocsana.internal.tasks.mhs;

import java.util.*;

/**
 * Common interface for minimal hitting set generation algorithms
 **/

public interface MHSAlgorithm {
    /**
     * Generate minimal hitting sets of the specified set family.
     *
     * @param sets  the family of sets to be hit (format: {@code List}
     * of edges, each a {@code List} of {@code Integer} indices of
     * vertices)
     * @param setScores  the OCSANA scores of the sets of the
     * hypergraph, indexed to match {@code sets} (set all equal to
     * ignore)
     * @param maxCardinality  the maximum size of hitting set to
     * consider (0 indicates no cutoff, otherwise positive)
     * @param maxCandidates  the maximum number of candidates to
     * consider (0 indicates to run search to completion, otherwise
     * positive)
     * @param numThreads  the number of threads to use for calculation (0
     * indicates automatic choice)
     * @param randomSeed  the seed value for random number generator (if
     * algorithm is non-deterministic)
     * @return a {@code List} of the hitting sets, each of which is a
     * {@code List} of {@code Integer} indices of the elements of that
     * set
     **/
    public List<List<Integer>> transversal(List<List<Integer>> sets,
                                           List<Float> setScores,
                                           Integer maxCardinality,
                                           Integer maxCandidates,
                                           Integer numThreads,
                                           Integer randomSeed);

    /**
     * Indicate whether this algorithm supports restricted-cardinality
     * search.
     *
     * @return true if the algorithm supports restricted-cardinality
     * search, false otherwise.
     **/
    public Boolean supportsMaxCardinality();

    /**
     * Indicate whether this algorithm supports searching only a
     * specified number of candidates.
     *
     * @return true if the algorithm supports restricted number of
     * candidates, false otherwise.
     **/
    public Boolean supportMaxCandidates();

    /**
     * Indicate whether this algorithm supports multiple threads.
     *
     * @return true if the algorithm supports multiple threads, false
     * otherwise.
     **/
    public Boolean supportsMultipleThreads();

    /**
     * Indicate whether this algorithm supports use of random seeds.
     *
     * @return true if the algorithm supports random seeds, false
     * otherwise.
     **/
    public Boolean supportsRandomSeed();

    /**
     *  The name of this algorithm, formatted suitably for a UI.
     *
     * @return the name of the algorithm
     **/
     public String name();
}
