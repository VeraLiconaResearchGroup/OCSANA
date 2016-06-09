/**
 * Context for an OCSANA run
 *
 * Copyright Vera-Licona Research Group (C) 2016
 *
 * This software is licensed under the Artistic License 2.0, see the
 * LICENSE file or
 * http://www.opensource.org/licenses/artistic-license-2.0.php for
 * details
 **/

package org.compsysmed.ocsana.internal.util.context;

// Java imports
import java.util.*;
import java.util.stream.Collectors;

// Cytoscape imports
import org.cytoscape.model.CyEdge;
import org.cytoscape.model.CyNetwork;
import org.cytoscape.model.CyNode;

// OCSANA imports
import org.compsysmed.ocsana.internal.util.tunables.NodeHandler;
import org.compsysmed.ocsana.internal.util.tunables.EdgeProcessor;

import org.compsysmed.ocsana.internal.algorithms.signassignment.AbstractCISignAssignmentAlgorithm;
import org.compsysmed.ocsana.internal.algorithms.drugability.AbstractSignedInterventionScoringAlgorithm;
import org.compsysmed.ocsana.internal.algorithms.path.AbstractPathFindingAlgorithm;
import org.compsysmed.ocsana.internal.algorithms.mhs.AbstractMHSAlgorithm;
import org.compsysmed.ocsana.internal.algorithms.scoring.OCSANAScoringAlgorithm;

/**
 * Context for an OCSANA run
 *
 * This class stores the configuration required to run OCSANA. A
 * populated instance will be passed to a RunnerTask at the
 * beginning of a run.
 * <p>
 * This class is immutable by design. Instances should be constructed using
 * {@link ContextBundleBuilder}.
 **/
public final class ContextBundle {
    private final CyNetwork network;

    private final Set<CyNode> sourceNodes;
    private final Set<CyNode> targetNodes;
    private final Set<CyNode> offTargetNodes;

    private final NodeHandler nodeHandler;
    private final EdgeProcessor edgeProcessor;
    private final boolean includeEndpointsInCIs;

    private final AbstractPathFindingAlgorithm pathFindingAlgorithm;
    private final AbstractMHSAlgorithm mhsAlgorithm;
    private final OCSANAScoringAlgorithm ocsanaAlgorithm;

    private final Set<CyNode> targetsToActivate;
    private final Set<CyNode> targetsToDeactivate;

    private final AbstractCISignAssignmentAlgorithm ciSignAlgorithm;
    private final AbstractSignedInterventionScoringAlgorithm siScoringAlgorithm;

    public ContextBundle (CyNetwork network,
                          Set<CyNode> sourceNodes,
                          Set<CyNode> targetNodes,
                          Set<CyNode> offTargetNodes,
                          NodeHandler nodeHandler,
                          EdgeProcessor edgeProcessor,
                          boolean includeEndpointsInCIs,
                          AbstractPathFindingAlgorithm pathFindingAlgorithm,
                          AbstractMHSAlgorithm mhsAlgorithm,
                          OCSANAScoringAlgorithm ocsanaAlgorithm,
                          Set<CyNode> targetsToActivate,
                          AbstractCISignAssignmentAlgorithm ciSignAlgorithm,
                          AbstractSignedInterventionScoringAlgorithm siScoringAlgorithm) {
        // Assignments
        Objects.requireNonNull(network, "Network cannot be null");
        this.network = network;

        Objects.requireNonNull(sourceNodes, "Source node set cannot be null");
        this.sourceNodes = sourceNodes;

        Objects.requireNonNull(targetNodes, "Target node set cannot be null");
        this.targetNodes = targetNodes;

        Objects.requireNonNull(offTargetNodes, "Off-target node set cannot be null");
        this.offTargetNodes = offTargetNodes;

        Objects.requireNonNull(nodeHandler, "Node name handler cannot be null");
        this.nodeHandler = nodeHandler;

        Objects.requireNonNull(edgeProcessor, "Edge processor cannot be null");
        this.edgeProcessor = edgeProcessor;

        this.includeEndpointsInCIs = includeEndpointsInCIs;

        Objects.requireNonNull(pathFindingAlgorithm, "Path-finding algorithm cannot be null");
        this.pathFindingAlgorithm = pathFindingAlgorithm;

        Objects.requireNonNull(mhsAlgorithm, "MHS algorithm cannot be null");
        this.mhsAlgorithm = mhsAlgorithm;

        Objects.requireNonNull(ocsanaAlgorithm, "OCSANA scoring algorithm cannot be null");
        this.ocsanaAlgorithm = ocsanaAlgorithm;

        Objects.requireNonNull(targetsToActivate, "Set of targets to activate cannot be empty");
        this.targetsToActivate = targetsToActivate;
        targetsToDeactivate = new HashSet<>(targetNodes);
        targetsToDeactivate.removeAll(targetsToActivate);

        Objects.requireNonNull(ciSignAlgorithm, "CI sign assignment algorithm cannot be null");
        this.ciSignAlgorithm = ciSignAlgorithm;

        Objects.requireNonNull(siScoringAlgorithm, "SI scoring algorithm cannot be null");
        this.siScoringAlgorithm = siScoringAlgorithm;

        // Sanity checks
        if (sourceNodes.stream().anyMatch(node -> !network.containsNode(node))) {
            throw new IllegalArgumentException("All source nodes must come from underlying network");
        }

        if (targetNodes.stream().anyMatch(node -> !network.containsNode(node))) {
            throw new IllegalArgumentException("All target nodes must come from underlying network");
        }

        if (offTargetNodes.stream().anyMatch(node -> !network.containsNode(node))) {
            throw new IllegalArgumentException("All off-target nodes must come from underlying network");
        }

        if (!Collections.disjoint(sourceNodes, targetNodes)) {
            throw new IllegalArgumentException("Source and target nodes must be disjoint");
        }

        if (!Collections.disjoint(sourceNodes, offTargetNodes)) {
            throw new IllegalArgumentException("Source and off-target nodes must be disjoint");
        }

        if (!Collections.disjoint(targetNodes, offTargetNodes)) {
            throw new IllegalArgumentException("Target and off-target nodes must be disjoint");
        }

        if (!targetNodes.containsAll(targetsToActivate)) {
            throw new IllegalArgumentException("Targets to activate must be in target set");
        }
    }

    /**
     * Return the CyNetwork used by this context
     **/
    public CyNetwork getNetwork () {
        return network;
    }

    /**
     * Return the name of the CyNetwork used by this context
     **/
    public String getNetworkName () {
        return network.getRow(network).get(CyNetwork.NAME, String.class);
    }

    /**
     * Get the name of the column that contains the node names
     **/
    public String getNodeNameColumnName () {
        return nodeHandler.getNodeNameColumnName();
    }

    /**
     * Return the source nodes
     **/
    public Set<CyNode> getSourceNodes () {
        return sourceNodes;
    }

    /**
     * Return the target nodes
     **/
    public Set<CyNode> getTargetNodes () {
        return targetNodes;
    }

    /**
     * Return the target nodes which are to be activated
     **/
    public Set<CyNode> getTargetsToActivate () {
        return targetsToActivate;
    }

    /**
     * Return the target nodes which are to be deactivated
     **/
    public Set<CyNode> getTargetsToDeactivate () {
        return targetsToDeactivate;
    }

    /**
     * Return the off-target nodes
     **/
    public Set<CyNode> getOffTargetNodes () {
        return offTargetNodes;
    }

    /**
     * Helper method to convert a collection of nodes to their names
     **/
    public Collection<String> getNodeNames (Collection<CyNode> nodes) {
        return nodes.stream().map(node -> getNodeName(node)).collect(Collectors.toList());
    }

    /**
     * Return the node name handler
     **/
    public NodeHandler getNodeHandler () {
        return nodeHandler;
    }

    /**
     * Return the edge processor
     **/
    public EdgeProcessor getEdgeProcessor () {
        return edgeProcessor;
    }

    /**
     * Return whether to include endpoints in CIs
     **/
    public boolean getIncludeEndpointsInCIs () {
        return includeEndpointsInCIs;
    }

    /**
     * Return the path-finding algorithm
     **/
    public AbstractPathFindingAlgorithm getPathFindingAlgorithm () {
        return pathFindingAlgorithm;
    }

    /**
     * Return the MHS algorithm
     **/
    public AbstractMHSAlgorithm getMHSAlgorithm () {
        return mhsAlgorithm;
    }

    /**
     * Return the OCSANA scoring algorithm
     **/
    public OCSANAScoringAlgorithm getOCSANAAlgorithm () {
        return ocsanaAlgorithm;
    }

    /**
     * Return the CI sign assignment algorithm
     **/
    public AbstractCISignAssignmentAlgorithm getCISignAlgorithm () {
        return ciSignAlgorithm;
    }

    /**
     * Return the signed intervention scoring algorithm
     **/
    public AbstractSignedInterventionScoringAlgorithm getSIScoringAlgorithm () {
        return siScoringAlgorithm;
    }

    /**
     * Get a string representation of a path of (directed) edges
     * <p>
     * The current format is "node1 -> node2 -| node3".
     *
     * @param path  the path
     **/
    public String pathString(List<CyEdge> path) {
    	Objects.requireNonNull(path, "Cannot convert a null path to a string");

        StringBuilder result = new StringBuilder();

        // Handle first node
        try {
            CyNode firstNode = path.iterator().next().getSource();
            result.append(getNodeName(firstNode));
        } catch (NoSuchElementException e) {
            return result.toString();
        }

        // Each other node is a target
        for (CyEdge edge: path) {
            if (edgeProcessor.edgeIsInhibition(edge)) {
                result.append(" -| ");
            } else {
                result.append(" -> ");
            }
            result.append(getNodeName(edge.getTarget()));
        }

        return result.toString();
    }

    /**
     * Get the name of a node
     *
     * @param node  the node
     *
     * @return the node's name
     **/
    public String getNodeName (CyNode node) {
        return nodeHandler.getNodeName(node);
    }
}
