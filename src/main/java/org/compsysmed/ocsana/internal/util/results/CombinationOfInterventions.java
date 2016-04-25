/**
 * Class representing a combination of interventions in a signaling network
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
import java.util.function.*;
import java.util.stream.Collectors;

// Cytoscape imports
import org.cytoscape.model.CyNode;

/**
 * Class representing a combination of interventions in a signaling
 * network
 **/
public class CombinationOfInterventions {
    private final Set<CyNode> ciNodes;
    private final Set<CyNode> targetNodes;
    private Set<CyNode> targetNodesToActivate;

    private final Function<CyNode, String> nodeNameFunction;

    private Double classicalOCSANAScore;

    private Collection<SignedIntervention> optimalSignings;

    /**
     * Constructor
     *
     * @param ciNodes  the nodes in this CI
     * @param targetNodes  the target nodes that this CI dominates
     * @param nodeNameFunction  function returning the name of a given
     * node (if null, use Cytoscape's automatic name, which is based
     * on SUID)
     **/
    public CombinationOfInterventions (Set<CyNode> ciNodes,
                                       Set<CyNode> targetNodes,
                                       Function<CyNode, String> nodeNameFunction) {
        this.ciNodes = ciNodes;
        this.targetNodes = targetNodes;
        this.nodeNameFunction = nodeNameFunction;
    }

    /**
     * Copy constructor
     *
     * @param other  the CombinationOfInterventions to copy
     **/
    public CombinationOfInterventions (CombinationOfInterventions other) {
        ciNodes = new HashSet<>(other.ciNodes);
        targetNodes = new HashSet<>(other.targetNodes);

        if (other.targetNodesToActivate != null) {
            targetNodesToActivate = new HashSet<>(other.targetNodesToActivate);
        }

        nodeNameFunction = other.nodeNameFunction;

        classicalOCSANAScore = other.classicalOCSANAScore;

        if (other.optimalSignings != null) {
            optimalSignings = new HashSet<>(other.optimalSignings);
        }
    }

    /**
     * Get the nodes in this CI
     **/
    public Set<CyNode> getNodes () {
        return ciNodes;
    }

    /**
     * Get the targets of this CI
     **/
    public Set<CyNode> getTargets () {
        return targetNodes;
    }

    /**
     * Get the size of this CI
     **/
    public Integer size () {
        return ciNodes.size();
    }

    /**
     * Set the classical OCSANA score of this CI
     **/
    public void setClassicalOCSANAScore (Double classicalOCSANAScore) {
        this.classicalOCSANAScore = classicalOCSANAScore;
    }

    /**
     * Get the classical OCSANA score of this CI
     **/
    public Double getClassicalOCSANAScore () {
        return classicalOCSANAScore;
    }

    /**
     * Set the targets which are to be activated by this CI
     **/
    public void setTargetsToActivate (Set<CyNode> targetNodesToActivate) {
        if (!targetNodes.containsAll(targetNodesToActivate)) {
            throw new IllegalArgumentException("Cannot activate nodes which are not targets.");
        }

        this.targetNodesToActivate = targetNodesToActivate;
    }

    /**
     * Get a string representation of the nodes of this CI.
     *
     * @see #nodeSetString
     **/
    public String interventionNodesString () {
        return nodeSetString(getNodes());
    }

    /**
     * Return the name of a node
     **/
    public String nodeName (CyNode node) {
        if (nodeNameFunction != null) {
            return nodeNameFunction.apply(node);
        } else {
            return node.toString();
        }
    }

    /**
     * Get a string representation of a collection of nodes
     *
     * The current format is "[node1, node2, node3]".
     *
     * @param nodes  the Collection of nodes
     **/
    public String nodeSetString(Collection<CyNode> nodes) {
        return nodes.stream().map(node -> nodeName(node)).collect(Collectors.joining(", ", "[", "]"));
    }

}
