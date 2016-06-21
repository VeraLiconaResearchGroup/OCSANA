/**
 * Implementation of the OCSANA "greedy" algorithm for finding minimal
 * hitting sets
 *
 * Copyright Vera-Licona Research Group (C) 2016
 *
 * This software is licensed under the Artistic License 2.0, see the
 * LICENSE file or
 * http://www.opensource.org/licenses/artistic-license-2.0.php for
 * details
 **/

package org.compsysmed.ocsana.internal.algorithms.mhs;

// Java imports
import java.util.*;
import java.util.stream.Collectors;
import java.util.stream.Stream;

// Cytoscape imports
import org.cytoscape.work.Tunable;
import org.cytoscape.work.util.BoundedInteger;

import org.cytoscape.model.CyNetwork;
import org.cytoscape.model.CyNode;

// OCSANA imports
import org.compsysmed.ocsana.internal.algorithms.scoring.OCSANAScoringAlgorithm;

import org.compsysmed.ocsana.internal.util.results.OCSANAScores;

/**
 * "Greedy" algorithm for finding minimal hitting sets with a
 * weighting heuristic
 **/
public class OCSANAGreedyAlgorithm
    extends AbstractMHSAlgorithm
    implements OCSANAScoringAlgorithm.OCSANAScoresListener {
    private static final String NAME = "Greedy heuristic algorithm";
    private static final String SHORTNAME = "GREEDY";

    //Tunables
    @Tunable(description = "Bound CI size",
             gravity = 350,
             tooltip="Unbounded search may take a very long time!")
             public Boolean useMaxCardinality = true;

    @Tunable(description = "Maximum CI size",
             gravity = 351,
             dependsOn = "useMaxCardinality=true")
             public BoundedInteger maxCardinalityBInt = new BoundedInteger(1, 6, 20, false, false);

    @Tunable(description = "Bound number of candidates",
             gravity = 360,
             tooltip="Unbounded search may take a very long time!")
             public Boolean useMaxCandidates = true;

    @Tunable(description = "Maximum number of candidates (millions)",
             gravity = 361,
             dependsOn = "useMaxCandidates=true")
             public Integer maxCandidates = 6;

    // Internal data
    private CyNetwork network;
    private OCSANAScores ocsanaScores;

    public OCSANAGreedyAlgorithm (CyNetwork network) {
        Objects.requireNonNull(network, "Network cannot be null");
        this.network = network;
    }

    @Override
    public void receiveScores (OCSANAScores ocsanaScores) {
        Objects.requireNonNull(ocsanaScores, "OCSANA scores cannot be null");

        if (!network.equals(ocsanaScores.getNetwork())) {
            throw new IllegalArgumentException("OCSANA scores must match declared network");
        }

        this.ocsanaScores = ocsanaScores;
    }

    @Override
    public Collection<Set<CyNode>> MHSes (Collection<Set<CyNode>> sets) {
        Objects.requireNonNull(sets, "Collection of sets cannot be null");
        Objects.requireNonNull(ocsanaScores, "OCSANA scores must be set before running this algorithm");

        // Construct temporary lists containing copies of the nodes and sets
        Set<CyNode> largeSetNodeSet = sets.stream().flatMap(Set::stream).collect(Collectors.toSet());
        Set<CyNode> singletonNodeSet = new HashSet<>();

        List<Set<CyNode>> largeSets = new ArrayList<>(); // Sets of size at least two (we look for MHSes of this family, then combine with the singletons)

        for (Set<CyNode> set: sets) {
            // Empty sets are ignored
            if (set.size() == 1) {
                CyNode node = set.iterator().next();
                largeSetNodeSet.remove(node);
                singletonNodeSet.add(node);
            } else if (set.size() > 1) {
                largeSets.add(new HashSet<>(set));
            }
        }

        // Short-circuit if there are no large sets
        if (largeSets.isEmpty()) {
            return Collections.singletonList(singletonNodeSet);
        }

        // Create sorted lists of nodes

        List<CyNode> largeSetNodes = new ArrayList<>(largeSetNodeSet);
        largeSetNodes.sort((left, right) -> -1 * Double.compare(ocsanaScores.OCSANA(left), ocsanaScores.OCSANA(right)));

        List<CyNode> singletonNodes = new ArrayList<>(singletonNodeSet);
        singletonNodes.sort((left, right) -> -1 * Double.compare(ocsanaScores.OCSANA(left), ocsanaScores.OCSANA(right)));

        // Search for hitting sets
        List<Set<CyNode>> foundMHSesOfLargeSets = new ArrayList<>();
        List<Set<CyNode>> candidates = largeSetNodes.stream().map(Collections::singleton).collect(Collectors.toList()); // Singleton set of each node found in a large set

        Integer candidatesChecked = 0;
        Integer currentCardinality = 1;

        while (!candidates.isEmpty() && maxCandidatesNotMet(candidatesChecked) && maxCardinalityNotExceeded(currentCardinality, singletonNodes.size())) {
            // Sort candidates in descending OCSANA score order
            candidates.sort((left, right) -> -1 * Double.compare(ocsanaScores.OCSANA(left), ocsanaScores.OCSANA(right))); // Negate comparator for descending sort

            // Check whether any candidate is a hitting set
            List<Set<CyNode>> newHSes = new ArrayList<>();

            Iterator<Set<CyNode>> candidateIterator = candidates.iterator();
            while (candidateIterator.hasNext() && maxCandidatesNotMet(candidatesChecked)) {
                candidatesChecked += 1;

                Set<CyNode> candidate = candidateIterator.next();
                assert candidate.size() == currentCardinality;

                // Test whether the candidate intersects every large set
                if (largeSets.stream().allMatch(s -> s.stream().anyMatch(e -> candidate.contains(e)))) {
                    candidateIterator.remove();
                    newHSes.add(candidate);
                }
            }

            // Check whether any new HSes are minimal and store them
            for (Set<CyNode> newHS: newHSes) {
                if (foundMHSesOfLargeSets.stream().noneMatch(s -> newHS.containsAll(s))) {
                    foundMHSesOfLargeSets.add(newHS);
                }
            }

            // Build new candidates
            if (maxCandidatesNotMet(candidatesChecked)) {
                // For each candidate of size k, construct candidates of size k+1 by adding each elementary node not already in the candidate
                candidates = candidates.stream()
                    .map(candidate -> largeSetNodes.stream().filter(node -> !candidate.contains(node)).map(node -> Stream.concat(candidate.stream(), Stream.of(node)).collect(Collectors.toSet())))
                    .flatMap(s -> s)
                    .collect(Collectors.toList());
            }

            // Iterate
            currentCardinality += 1;
        }

        // Combine MHSes of large sets with singleton sets and return
        List<Set<CyNode>> foundMHSes = foundMHSesOfLargeSets.stream().map(MHS -> Stream.concat(MHS.stream(), singletonNodes.stream()).collect(Collectors.toSet())).collect(Collectors.toList());
        return foundMHSes;
    }

    private Boolean maxCandidatesNotMet (Integer candidatesChecked) {
        return (!useMaxCandidates || candidatesChecked < maxCandidates * 1e9);
    }

    private Boolean maxCardinalityNotExceeded (Integer candidateCardinality,
                                               Integer singletonNodesSize) {
        return (!useMaxCardinality || candidateCardinality + singletonNodesSize <= maxCardinalityBInt.getValue());
    }

    private static <T> Boolean setsIntersect (Set<? extends T> left, Set<? extends T> right) {
        return left.stream().anyMatch(e -> right.contains(e));
    }

    @Override
    public String fullName () {
        return NAME;
    }

    @Override
    public String shortName () {
        return SHORTNAME;
    }

    @Override
    public String description () {
        StringBuilder result = new StringBuilder(fullName());

        result.append(" (");

        if (useMaxCardinality) {
            result.append(String.format("max CI size: %d; ", maxCardinalityBInt.getValue()));
        } else {
            result.append("no max CI size; ");
        }

        if (useMaxCandidates) {
            result.append(String.format("maximum candidates: %d million", maxCandidates));
        } else {
            result.append("no max candidate count");
        }

        result.append(")");
        return result.toString();
    }
}
