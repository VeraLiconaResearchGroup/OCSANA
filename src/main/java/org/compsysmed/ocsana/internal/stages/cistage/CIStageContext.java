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

package org.compsysmed.ocsana.internal.stages.cistage;

// Java imports
import java.util.*;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

// Cytoscape imports
import org.cytoscape.model.CyColumn;
import org.cytoscape.model.CyNetwork;
import org.cytoscape.model.CyEdge;
import org.cytoscape.model.CyNode;

import org.cytoscape.work.ContainsTunables;

// OCSANA imports
import org.compsysmed.ocsana.internal.util.tunables.NodeNameHandler;
import org.compsysmed.ocsana.internal.util.tunables.EdgeProcessor;

import org.compsysmed.ocsana.internal.algorithms.path.AbstractPathFindingAlgorithm;
import org.compsysmed.ocsana.internal.algorithms.path.AllNonSelfIntersectingPathsAlgorithm;
import org.compsysmed.ocsana.internal.algorithms.path.ShortestPathsAlgorithm;

import org.compsysmed.ocsana.internal.algorithms.mhs.AbstractMHSAlgorithm;
import org.compsysmed.ocsana.internal.algorithms.mhs.BergeAlgorithm;
import org.compsysmed.ocsana.internal.algorithms.mhs.MMCSAlgorithm;
import org.compsysmed.ocsana.internal.algorithms.mhs.RSAlgorithm;

import org.compsysmed.ocsana.internal.algorithms.scoring.OCSANAScoringAlgorithm;

/**
 * Context for the CI stage of OCSANA
 *
 * This class stores the configuration required to run the CI stage. A
 * populated instance will be passed to a CIStageRunner at the
 * beginning of a run.
 **/
public class CIStageContext {
    public NodeNameHandler nodeNameHandler;

    public Set<CyNode> sourceNodes;
    public Set<CyNode> targetNodes;
    public Set<CyNode> offTargetNodes;

    public EdgeProcessor edgeProcessor;

    public AbstractPathFindingAlgorithm pathFindingAlg;

    public AbstractMHSAlgorithm mhsAlg;
    public Boolean includeEndpointsInCIs = false;

    public OCSANAScoringAlgorithm ocsanaAlg;

    // Internal data
    private CyNetwork network;
    private Collection<ActionListener> listeners = new HashSet<>();

    public CIStageContext (CyNetwork network) {
        this.network = network;

        if (network == null) {
            return;
        }

        nodeNameHandler = new NodeNameHandler(network);

        sourceNodes = new HashSet<>();
        targetNodes = new HashSet<>();
        offTargetNodes = new HashSet<>();

        edgeProcessor = new EdgeProcessor(network);

        pathFindingAlg = new AllNonSelfIntersectingPathsAlgorithm(network);
        mhsAlg = new MMCSAlgorithm();
        ocsanaAlg = new OCSANAScoringAlgorithm(network);
    }

    /**
     * Return the CyNetwork used by this context
     **/
    public CyNetwork getNetwork () {
        return network;
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
            result.append(nodeNameHandler.getNodeName(firstNode));
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
            result.append(nodeNameHandler.getNodeName(edge.getTarget()));
        }

        return result.toString();
    }
}
