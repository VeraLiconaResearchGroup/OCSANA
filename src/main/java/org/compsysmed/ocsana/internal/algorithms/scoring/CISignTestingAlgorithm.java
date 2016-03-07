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

public class CISignTestingAlgorithm
    extends AbstractOCSANAAlgorithm {
    public static final String NAME = "CI sign testing";
    public static final String SHORTNAME = "CI-sign";

    private Set<CyNode> sources;
    private Set<CyNode> targets;
    private BiFunction<CyNode, CyNode, Double> effectOnTarget;

    /**
     * Algorithm to find best sign assignment for a set of sources to
     * activate a set of targets
     *
     * @param sources  the source nodes
     * @param targets  the target nodes
     * @param effectOnTarget  function which computes for input
     * (source, target) the EFFECT_ON_TARGETS score of source on
     * target
     **/
    public CISignTestingAlgorithm (Set<CyNode> sources,
                                   Set<CyNode> targets,
                                   BiFunction<CyNode, CyNode, Double> effectOnTarget) {
        this.sources = sources;
        this.targets = targets;
        this.effectOnTarget = effectOnTarget;
    }

    /**
     * Find all sets of sources to activate that maximize the number
     * of targets that are activated
     **/
    public Collection<SignedIntervention> bestInterventions () {
        // Use lists of the source and target nodes to ensure consistent ordering
        List<CyNode> sourceList = new ArrayList<>(sources);
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
        Map<BitSet, Vector> bestSignsSoFar = new HashMap<>();

        mostPositiveTermsSoFar = numberOfPositiveTerms(interventionEffect);
        bestSignsSoFar.put((BitSet) signs.clone(), interventionEffect);

        Integer numberOfPossibleAssignments = 1 << sourceList.size(); // Java doesn't have an exponent operator? It's 2016!
        for (int i = 1; i < numberOfPossibleAssignments; i++) {
            // We use a simple Gray code to scan the possible assignments
            int bitToFlip = Integer.numberOfTrailingZeros(i);
            signs.flip(bitToFlip);
            if (signs.get(bitToFlip)) {
                interventionEffect = interventionEffect.add(effectVectors.get(bitToFlip).multiply(2));
            } else {
                interventionEffect = interventionEffect.subtract(effectVectors.get(bitToFlip).multiply(2));
            }

            if (numberOfPositiveTerms(interventionEffect) > mostPositiveTermsSoFar){
                bestSignsSoFar = new HashMap<>();

                mostPositiveTermsSoFar = numberOfPositiveTerms(interventionEffect);
                bestSignsSoFar.put((BitSet) signs.clone(), interventionEffect);

            } else if (numberOfPositiveTerms(interventionEffect) == mostPositiveTermsSoFar) {
                bestSignsSoFar.put((BitSet) signs.clone(), interventionEffect);
            }
        }

        Collection<SignedIntervention> interventions = new ArrayList<>();
        for (Map.Entry<BitSet, Vector> result: bestSignsSoFar.entrySet()) {
            BitSet resultSigns = result.getKey();
            Set<CyNode> activatedSources = activatedSources(sourceList, resultSigns);

            Vector effect = result.getValue();
            Map<CyNode, Double> targetEffects = new HashMap<>();
            for (int i = 0; i < targetList.size(); i++) {
                targetEffects.put(targetList.get(i), effect.get(i));
            }

            SignedIntervention intervention = new SignedIntervention(sources, activatedSources, targetEffects);
            interventions.add(intervention);
        }

        return interventions;
    }

    /**
     * Class representing a signed intervention of a particular CI
     **/
    public static class SignedIntervention {
        private final Set<CyNode> interventionNodes;
        private final Set<CyNode> interventionNodesToActivate;
        private final Set<CyNode> interventionNodesToInhibit;

        private final Set<CyNode> targetNodes;
        private final Map<CyNode, Double> effectsOnTargets;

        public SignedIntervention (Set<CyNode> interventionNodes,
                                   Set<CyNode> interventionNodesToActivate,
                                   Map<CyNode, Double> effectsOnTargets) {
            assert(interventionNodes.containsAll(interventionNodesToActivate));

            this.interventionNodes = interventionNodes;
            this.interventionNodesToActivate = interventionNodesToActivate;
            this.effectsOnTargets = effectsOnTargets;

            this.targetNodes = effectsOnTargets.keySet();
            this.interventionNodesToInhibit = new HashSet<>(interventionNodes);
            this.interventionNodesToInhibit.removeAll(interventionNodesToActivate);
        }

        /**
         * Return the nodes involved in the intervention
         **/
        public Set<CyNode> getInterventionNodes () {
            return interventionNodes;
        }

        /**
         * Return the nodes to be activated in the intervention
         **/
        public Set<CyNode> getInterventionNodesToActivate () {
            return interventionNodesToActivate;
        }

        /**
         * Return the nodes to be inhibited in the intervention
         **/
        public Set<CyNode> getInterventionNodesToInhibit () {
            return interventionNodesToInhibit;
        }

        /**
         * Return the number of targets which are effected correctly
         * by this intervention
         **/
        public Long numberOfCorrectEffects () {
            return effectsOnTargets.values().stream().filter(val -> val > 0).count();
        }

        /**
         * Return the signed EFFECT_ON_TARGET score on a given target
         * node (Positive indicates that the desired effect was
         * achieved, negative indicates its opposite)
         **/
        public Double effectOnTarget (CyNode target) {
            return effectsOnTargets.get(target);
        }

        public String toString () {
            return String.format("Activating nodes %s and inhibiting nodes %s drives %d nodes correctly with effect %s", interventionNodesToActivate, interventionNodesToInhibit, numberOfCorrectEffects(), effectsOnTargets);
        }
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
