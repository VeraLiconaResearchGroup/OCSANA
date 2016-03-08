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

/**
 * Class representing a signed intervention of a CI and its effects on
 * certain targets
 **/
public class SignedIntervention {
    private final Set<CyNode> interventionNodes;
    private final Set<CyNode> interventionNodesToActivate;
    private final Set<CyNode> interventionNodesToInhibit;

    private final Set<CyNode> targetNodes;
    private final Map<CyNode, Double> effectsOnTargets;

    /**
     * Constructor
     *
     * @param interventionNodes  the nodes in the CI
     * @param interventionNodesToActivate the nodes which are
     * activated in this CI (must be a subset of interventionNodes)
     * @param effectsOnTargets sends each target node to its total
     * EFFECT_ON_TARGET score from this signed CI, with the convention
     * that a positive score means the user's desired intervention was
     * achieved and a negative score means the opposite was achieved
     **/
    public SignedIntervention (Set<CyNode> interventionNodes,
                               Set<CyNode> interventionNodesToActivate,
                               Map<CyNode, Double> effectsOnTargets) {
        if (!interventionNodes.containsAll(interventionNodesToActivate)) {
            throw new IllegalArgumentException("Nodes to activate must be a subset of CI nodes");
        }

        this.interventionNodes = interventionNodes;
        this.interventionNodesToActivate = interventionNodesToActivate;
        this.effectsOnTargets = effectsOnTargets;

        this.targetNodes = effectsOnTargets.keySet();
        this.interventionNodesToInhibit = new HashSet<>(interventionNodes);
        this.interventionNodesToInhibit.removeAll(interventionNodesToActivate);
    }

    /**
     * Return all nodes involved in the intervention
     **/
    public Set<CyNode> getInterventionNodes () {
        return interventionNodes;
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
