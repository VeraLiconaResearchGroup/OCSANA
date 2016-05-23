/**
 * Context for the CI stage of OCSANA
 *
 * Copyright Vera-Licona Research Group (C) 2016
 *
 * This software is licensed under the Artistic License 2.0, see the
 * LICENSE file or
 * http://www.opensource.org/licenses/artistic-license-2.0.php for
 * details
 **/

package org.compsysmed.ocsana.internal.stages.generation;

// Java imports
import java.util.*;
import java.util.stream.Collectors;

// Cytoscape imports
import org.cytoscape.model.CyEdge;
import org.cytoscape.model.CyNetwork;
import org.cytoscape.model.CyNode;

// OCSANA imports
import org.compsysmed.ocsana.internal.util.tunables.NodeNameHandler;
import org.compsysmed.ocsana.internal.util.tunables.EdgeProcessor;

import org.compsysmed.ocsana.internal.algorithms.path.AbstractPathFindingAlgorithm;
import org.compsysmed.ocsana.internal.algorithms.mhs.AbstractMHSAlgorithm;
import org.compsysmed.ocsana.internal.algorithms.scoring.OCSANAScoringAlgorithm;

/**
 * Context for the CI stage of OCSANA
 *
 * This class stores the configuration required to run the CI stage. A
 * populated instance will be passed to a CIStageRunner at the
 * beginning of a run.
 * <p>
 * This class is immutable by design. Instances should be constructed using
 * {@link GenerationContextBuilder}.
 **/
public final class GenerationContext {
    private final CyNetwork network;

    private final Set<CyNode> sourceNodes;
    private final Set<CyNode> targetNodes;
    private final Set<CyNode> offTargetNodes;

    private final NodeNameHandler nodeNameHandler;
    private final EdgeProcessor edgeProcessor;
    private final boolean includeEndpointsInCIs;

    private final AbstractPathFindingAlgorithm pathFindingAlgorithm;
    private final AbstractMHSAlgorithm mhsAlgorithm;
    private final OCSANAScoringAlgorithm ocsanaAlgorithm;

    public GenerationContext (CyNetwork network,
                              Set<CyNode> sourceNodes,
                              Set<CyNode> targetNodes,
                              Set<CyNode> offTargetNodes,
                              NodeNameHandler nodeNameHandler,
                              EdgeProcessor edgeProcessor,
                              boolean includeEndpointsInCIs,
                              AbstractPathFindingAlgorithm pathFindingAlgorithm,
                              AbstractMHSAlgorithm mhsAlgorithm,
                              OCSANAScoringAlgorithm ocsanaAlgorithm) {
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

        // Assignments
        if (network == null) {
            throw new IllegalArgumentException("Network cannot be null");
        }
        this.network = network;

        if (sourceNodes == null) {
            throw new IllegalArgumentException("Source node set cannot be null");
        }
        this.sourceNodes = sourceNodes;

        if (targetNodes == null) {
            throw new IllegalArgumentException("Target node set cannot be null");
        }
        this.targetNodes = targetNodes;

        if (offTargetNodes == null) {
            throw new IllegalArgumentException("Off-target node set cannot be null");
        }
        this.offTargetNodes = offTargetNodes;

        if (nodeNameHandler == null) {
            throw new IllegalArgumentException("Node name handler cannot be null");
        }
        this.nodeNameHandler = nodeNameHandler;

        if (edgeProcessor == null) {
            throw new IllegalArgumentException("Edge processor cannot be null");
        }
        this.edgeProcessor = edgeProcessor;

        this.includeEndpointsInCIs = includeEndpointsInCIs;

        if (pathFindingAlgorithm == null) {
            throw new IllegalArgumentException("Path-finding algorithm cannot be null");
        }
        this.pathFindingAlgorithm = pathFindingAlgorithm;

        if (mhsAlgorithm == null) {
            throw new IllegalArgumentException("MHS algorithm cannot be null");
        }
        this.mhsAlgorithm = mhsAlgorithm;

        if (ocsanaAlgorithm == null) {
            throw new IllegalArgumentException("OCSANA scoring algorithm cannot be null");
        }
        this.ocsanaAlgorithm = ocsanaAlgorithm;
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
        return nodeNameHandler.getNodeNameColumnName();
    }

    /**
     * Return the source nodes
     **/
    public Set<CyNode> getSourceNodes () {
        return sourceNodes;
    }

    /**
     * Return the names of the source nodes
     **/
    public Collection<String> getSourceNodeNames () {
        return sourceNodes.stream().map(node -> getNodeName(node)).collect(Collectors.toList());
    }

    /**
     * Return the target nodes
     **/
    public Set<CyNode> getTargetNodes () {
        return targetNodes;
    }

    /**
     * Return the names of the target nodes
     **/
    public Collection<String> getTargetNodeNames () {
        return targetNodes.stream().map(node -> getNodeName(node)).collect(Collectors.toList());
    }

    /**
     * Return the off-target nodes
     **/
    public Set<CyNode> getOffTargetNodes () {
        return offTargetNodes;
    }

    /**
     * Return the names of the off-target nodes
     **/
    public Collection<String> getOffTargetNodeNames () {
        return offTargetNodes.stream().map(node -> getNodeName(node)).collect(Collectors.toList());
    }

    /**
     * Return the node name handler
     **/
    public NodeNameHandler getNodeNameHandler () {
        return nodeNameHandler;
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
     * Get a string representation of a path of (directed) edges
     * <p>
     * The current format is "node1 -> node2 -| node3".
     *
     * @param path  the path
     **/
    public String pathString(List<CyEdge> path) {
        if (path == null) {
            return "";
        }

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
        return nodeNameHandler.getNodeName(node);
    }
}
