/**
 * Class representing a signed intervention of a particular CI
 *
 * Copyright Vera-Licona Research Group (C) 2016
 *
 * This software is licensed under the Artistic License 2.0, see the
 * LICENSE file or
 * http://www.opensource.org/licenses/artistic-license-2.0.php for
 * details
 **/

package org.compsysmed.ocsana.internal.util.results;

// Java imports
import java.util.*;

// Cytoscape imports
import org.cytoscape.model.CyNode;

// OCSANA imports
import org.compsysmed.ocsana.internal.util.results.ScoredTargetNode;
import org.compsysmed.ocsana.internal.util.results.SignedInterventionNode;

import org.compsysmed.ocsana.internal.util.science.InteractionSign;

/**
 * Class representing a signed intervention of a CI and its effects on
 * certain targets
 **/
public class SignedIntervention {
    private final CombinationOfInterventions ci;
    private final Set<CyNode> interventionNodesToActivate;
    private final Set<CyNode> interventionNodesToInhibit;
    private final Set<SignedInterventionNode> signedInterventionNodes;

    private final Set<CyNode> targetNodes;
    private final Map<CyNode, Double> effectsOnTargets;
    private final Set<ScoredTargetNode> scoredTargetNodes;

    /**
     * Constructor
     *
     * @param ci  the CI
     * @param interventionNodesToActivate the nodes which are
     * activated in this CI (must be a subset of the nodes in the CI)
     * @param effectsOnTargets sends each target node to its total
     * EFFECT_ON_TARGET score from this signed CI, with the convention
     * that a positive score means the user's desired intervention was
     * achieved and a negative score means the opposite was achieved
     **/
    public SignedIntervention (CombinationOfInterventions ci,
                               Set<CyNode> interventionNodesToActivate,
                               Map<CyNode, Double> effectsOnTargets) {
        if (!ci.getNodes().containsAll(interventionNodesToActivate)) {
            throw new IllegalArgumentException("Nodes to activate must be a subset of CI nodes");
        }

        this.ci = ci;
        this.interventionNodesToActivate = interventionNodesToActivate;
        this.effectsOnTargets = effectsOnTargets;

        this.interventionNodesToInhibit = new HashSet<>(ci.getNodes());
        this.interventionNodesToInhibit.removeAll(interventionNodesToActivate);

        signedInterventionNodes = new HashSet<>();
        for (CyNode node: interventionNodesToActivate) {
            SignedInterventionNode signedNode = new SignedInterventionNode(node, InteractionSign.POSITIVE, ci.nodeName(node));
            signedInterventionNodes.add(signedNode);
        }

        for (CyNode node: interventionNodesToInhibit) {
            SignedInterventionNode signedNode = new SignedInterventionNode(node, InteractionSign.NEGATIVE, ci.nodeName(node));
            signedInterventionNodes.add(signedNode);
        }

        this.targetNodes = effectsOnTargets.keySet();
        scoredTargetNodes = new HashSet<>();
        for (CyNode target: targetNodes) {
            ScoredTargetNode scoredTarget = new ScoredTargetNode(target, effectOnTarget(target), ci.nodeName(target));
            scoredTargetNodes.add(scoredTarget);
        }
    }

    /**
     * Return the underlying CI
     **/
    public CombinationOfInterventions getCI () {
        return ci;
    }

    /**
     * Return the target nodes of this intervention
     **/
    public Set<CyNode> getTargetNodes () {
        return targetNodes;
    }

    /**
     * Return the scored target nodes of this intervention
     **/
    public Set<ScoredTargetNode> getScoredTargetNodes () {
        return scoredTargetNodes;
    }

    /**
     * Return the nodes which are activated in this intervention
     **/
    public Set<CyNode> getInterventionNodesToActivate () {
        return interventionNodesToActivate;
    }

    /**
     * Return the nodes which are inhibited in this intervention
     **/
    public Set<CyNode> getInterventionNodesToInhibit () {
        return interventionNodesToInhibit;
    }

    /**
     * Return the signed nodes of the intervention
     **/
    public Set<SignedInterventionNode> getSignedInterventionNodes () {
        return signedInterventionNodes;
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

    /**
     * Return a cumulative effect score for this signed intervention
     *
     * NOTE: this score can be compared with the scores of other sign
     * assignments of the same CI; positive scores indicate overall
     * achievement of the intervention goal, and higher scores
     * indicate a stronger effect. However, these scores <b>cannot</b>
     * be compared between different CIs.
     **/
    public Double cumulativeEffectOnTargets () {
        return targetNodes.stream().mapToDouble(this::effectOnTarget).sum();
    }

    public String toString () {
        return String.format("Activating nodes %s and inhibiting nodes %s drives %d nodes correctly with effect %s", ci.nodeSetString(interventionNodesToActivate), ci.nodeSetString(interventionNodesToInhibit), numberOfCorrectEffects(), effectsOnTargets);
    }
}
