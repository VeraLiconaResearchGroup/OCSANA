/**
 * The OCSANA CI sign-testing algorithm
 *
 * Copyright Vera-Licona Research Group (C) 2015
 *
 * This software is licensed under the Artistic License 2.0, see the
 * LICENSE file or
 * http://www.opensource.org/licenses/artistic-license-2.0.php for
 * details
 **/

package org.compsysmed.ocsana.internal.algorithms.scoring;

// Java imports
import java.util.*;
import java.util.function.*;
import java.util.stream.Collectors;

// la4j imports
import org.la4j.Vector;
import org.la4j.vector.dense.BasicVector;

// Cytoscape imports
import org.cytoscape.model.CyNetwork;
import org.cytoscape.model.CyEdge;
import org.cytoscape.model.CyNode;

// OCSANA imports
import org.compsysmed.ocsana.internal.algorithms.AbstractOCSANAAlgorithm;

import org.compsysmed.ocsana.internal.util.results.CombinationOfInterventions;
import org.compsysmed.ocsana.internal.util.results.SignedIntervention;

/**
 * Algorithm to find best sign assignment for a set of sources to
 * activate a set of targets.
 *
 * NOTE: The current implementation is an exhaustive search through
 * all possible sign assignments of the CI, so the running time is
 * exponential in the size of the CI. Be careful if running this with
 * more than ~14 terms in a CI.
 **/
public class CISignTestingAlgorithm
    extends AbstractOCSANAAlgorithm {
    public static final String NAME = "CI sign testing";
    public static final String SHORTNAME = "CI-sign";

    private CombinationOfInterventions ci;
    private Set<CyNode> targets;
    private BiFunction<CyNode, CyNode, Double> effectOnTarget;

    /**
     * Constructor
     *
     * @param ci  the CI
     * @param targets  the target nodes
     * @param effectOnTarget  function which computes for input
     * (source, target) the EFFECT_ON_TARGETS score of source on
     * target
     **/
    public CISignTestingAlgorithm (CombinationOfInterventions ci,
                                   Set<CyNode> targets,
                                   BiFunction<CyNode, CyNode, Double> effectOnTarget) {
        this.ci = ci;
        this.targets = targets;
        this.effectOnTarget = effectOnTarget;
    }

    /**
     * Find all sets of sources to activate that maximize the number
     * of targets that are activated
     * <p>
     * {@code paretoOptimalOnly} defaults to true
     *
     * @see #bestInterventions(Boolean)
     **/
    public Collection<SignedIntervention> bestInterventions () {
        return bestInterventions(true);
    }

    /**
     * Find all sets of sources to activate that maximize the number
     * of targets that are activated
     *
     * @param paretoOptimalOnly if true, filter out sign assignments
     * which are sub-optimal by total effect score (NOTE: this may be
     * expensive if the original set is large)
     **/
    public Collection<SignedIntervention> bestInterventions (Boolean paretoOptimalOnly) {
        // Use lists of the source and target nodes to ensure consistent ordering
        List<CyNode> sourceList = new ArrayList<>(ci.getNodes());
        List<CyNode> targetList = new ArrayList<>(targets);

        // For each source, build a Vector storing its effects on the targets
        List<Vector> effectVectors = new ArrayList<>(sourceList.size());
        for (CyNode source: sourceList) {
            Vector effectVector = new BasicVector(targetList.size());
            for (int i = 0; i < targetList.size(); i++) {
                CyNode target = targetList.get(i);
                effectVector.set(i, effectOnTarget.apply(source, target));
            }
            effectVectors.add(effectVector);
        }

        // Search for a suitable sign assignment of the source nodes
        BitSet signs = new BitSet(); // Initially all negative
        signs.set(0, sourceList.size());

        // The corresponding score vector is the negative sum of all the components
        Vector interventionEffect = new BasicVector(targetList.size());
        for (Vector effectVector: effectVectors) {
            interventionEffect = interventionEffect.add(effectVector);
        }

        int mostPositiveTermsSoFar = 0;
        Collection<BitSetWithEffect> bestSignsSoFar = new ArrayList<>();

        mostPositiveTermsSoFar = numberOfPositiveTerms(interventionEffect);
        bestSignsSoFar.add(new BitSetWithEffect(signs, interventionEffect));

        Integer numberOfPossibleAssignments = 1 << sourceList.size(); // Java doesn't have an exponent operator? It's 2016!
        for (int i = 1; i < numberOfPossibleAssignments; i++) {
            if (isCanceled()) {
                break;
            }

            // We use a simple Gray code to scan the possible assignments
            int bitToFlip = Integer.numberOfTrailingZeros(i);
            signs.flip(bitToFlip);
            if (signs.get(bitToFlip)) {
                interventionEffect = interventionEffect.add(effectVectors.get(bitToFlip).multiply(2));
            } else {
                interventionEffect = interventionEffect.subtract(effectVectors.get(bitToFlip).multiply(2));
            }

            if (numberOfPositiveTerms(interventionEffect) > mostPositiveTermsSoFar){
                bestSignsSoFar = new ArrayList<>();

                mostPositiveTermsSoFar = numberOfPositiveTerms(interventionEffect);
                bestSignsSoFar.add(new BitSetWithEffect(signs, interventionEffect));

            } else if (numberOfPositiveTerms(interventionEffect) == mostPositiveTermsSoFar) {
                bestSignsSoFar.add(new BitSetWithEffect(signs, interventionEffect));
            }
        }

        // Filter the list if requested
        if (paretoOptimalOnly) {
            // This algorithm requires quadratically checks of the
            // assignments, but I don't think that can be improved…
            Collection<BitSetWithEffect> trimmedOptimalAssignments = new HashSet<>();

        candidateTestingLoop:
            for (BitSetWithEffect newCandidate: bestSignsSoFar) {
                Collection<BitSetWithEffect> assignmentsInferiorToCandidate = new HashSet<>();
                for (BitSetWithEffect oldAssignment: trimmedOptimalAssignments) {
                    switch (compareVectors(newCandidate.effect, oldAssignment.effect)) {
                    case LESS_THAN:
                    case EQUAL:
                        assert assignmentsInferiorToCandidate.isEmpty();
                        continue candidateTestingLoop;

                    case GREATER_THAN:
                        assignmentsInferiorToCandidate.add(oldAssignment);
                        break;

                    case INCOMPARABLE:
                        break;
                    }
                }

                trimmedOptimalAssignments.removeAll(assignmentsInferiorToCandidate);

                trimmedOptimalAssignments.add(newCandidate);
            }

            bestSignsSoFar = trimmedOptimalAssignments;
        }

        // Convert the resulting bitsets into sets of source nodes and
        // construct the corresponding SignedInterventions
        Collection<SignedIntervention> interventions = new ArrayList<>();
        for (BitSetWithEffect result: bestSignsSoFar) {
            BitSet resultSigns = result.bitset;
            Set<CyNode> activatedSources = activatedSources(sourceList, resultSigns);

            Vector effect = result.effect;
            Map<CyNode, Double> targetEffects = new HashMap<>();
            for (int i = 0; i < targetList.size(); i++) {
                targetEffects.put(targetList.get(i), effect.get(i));
            }

            SignedIntervention intervention = new SignedIntervention(ci, activatedSources, targetEffects);
            interventions.add(intervention);
        }

        return interventions;
    }

    private class BitSetWithEffect {
        public BitSet bitset;
        public Vector effect;

        public BitSetWithEffect (BitSet bitset,
                                 Vector effect) {
            this.bitset = (BitSet) bitset.clone();
            this.effect = effect;
        }

        public String toString () {
            return effect.toString();
        }
    }

    private enum PosetRelation {
        LESS_THAN, GREATER_THAN, EQUAL, INCOMPARABLE
    }

    private static PosetRelation compareVectors (Vector left, Vector right) {
        if (left == null || right == null) {
            throw new IllegalArgumentException("Cannot compare a null vector.");
        }

        if (left.length() != right.length()) {
            throw new IllegalArgumentException("Cannot compare vectors of different lengths.");
        }

        PosetRelation relation = PosetRelation.EQUAL;

        for (int i = 0; i < left.length(); i++) {
            Double leftTerm = left.get(i);
            Double rightTerm = right.get(i);

            // Do nothing if they're equal
            if (leftTerm < rightTerm) {
                if (relation == PosetRelation.GREATER_THAN) {
                    relation = PosetRelation.INCOMPARABLE;
                    break;
                }

                relation = PosetRelation.LESS_THAN;
            } else if (leftTerm > rightTerm) {
                if (relation == PosetRelation.LESS_THAN) {
                    relation = PosetRelation.INCOMPARABLE;
                    break;
                }

                relation = PosetRelation.GREATER_THAN;
            }
        }

        return relation;
    }

    /**
     * Find the number of positive terms in a vector
     **/
    private static Integer numberOfPositiveTerms (Vector vector) {
        Integer termCount = 0;

        for (Double value: vector) {
            if (value > 0) {
                termCount++;
            }
        }

        return termCount;
    }

    /**
     * Generate a set of sources from a bitset
     **/
    private static Set<CyNode> activatedSources (List<CyNode> sourceList,
                                                 BitSet signs) {
        Set<CyNode> result = new HashSet<>();

        for (int i = 0; i < sourceList.size(); i++) {
            if (signs.get(i)) {
                    result.add(sourceList.get(i));
                }
        }

        return result;
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
    public String toString () {
        return this.shortName();
    }

    @Override
    public String description () {
        return fullName();
    }
}
