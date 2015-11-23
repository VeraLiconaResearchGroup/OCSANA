/**
 * Task to present results to user in OCSANA
 *
 * Copyright Vera-Licona Research Group (C) 2015
 * @author Andrew Gainer-Dewar, Ph.D. <andrew.gainer.dewar@gmail.com>
 *
 * This software is licensed under the Artistic License 2.0, see the
 * LICENSE file or
 * http://www.opensource.org/licenses/artistic-license-2.0.php for
 * details
 **/

package org.compsysmed.ocsana.internal.tasks.results;

// Java imports
import java.util.*;

// Cytoscape imports
import org.cytoscape.work.TaskMonitor;

import org.cytoscape.model.CyNode;
import org.cytoscape.model.CyEdge;
import org.cytoscape.model.CyIdentifiable;
import org.cytoscape.model.CyNetwork;

// OCSANA imports
import org.compsysmed.ocsana.internal.algorithms.path.AbstractPathFindingAlgorithm;
import org.compsysmed.ocsana.internal.algorithms.mhs.AbstractMHSAlgorithm;

import org.compsysmed.ocsana.internal.tasks.AbstractOCSANATask;
import org.compsysmed.ocsana.internal.tasks.OCSANAStep;

public class PresentResultsTask extends AbstractOCSANATask {
    private static final OCSANAStep algStep = OCSANAStep.PRESENT_RESULTS;

// User inputs
    protected Set<CyNode> sourceNodes;
    protected Set<CyNode> targetNodes;
    protected Set<CyNode> offTargetNodes;

// Paths data
    protected AbstractPathFindingAlgorithm pathAlg;
    protected Iterable<? extends Iterable<CyEdge>> pathsToTargets;
    protected Iterable<? extends Iterable<CyEdge>> pathsToOffTargets;

// MHS data
    protected AbstractMHSAlgorithm mhsAlg;
    protected Iterable<? extends Iterable<CyNode>> MHSes;

    public PresentResultsTask (CyNetwork network,
                               Set<CyNode> sourceNodes,
                               Set<CyNode> targetNodes,
                               Set<CyNode> offTargetNodes,
                               AbstractPathFindingAlgorithm pathAlg,
                               Iterable<? extends Iterable<CyEdge>> pathsToTargets,
                               Iterable<? extends Iterable<CyEdge>> pathsToOffTargets,
                               AbstractMHSAlgorithm mhsAlg,
                               Iterable<? extends Iterable<CyNode>> MHSes) {
        super(network);
        this.sourceNodes = sourceNodes;
        this.targetNodes = targetNodes;
        this.offTargetNodes = offTargetNodes;
        this.pathAlg = pathAlg;
        this.pathsToTargets = pathsToTargets;
        this.pathsToOffTargets = pathsToOffTargets;
        this.mhsAlg = mhsAlg;
        this.MHSes = MHSes;
    }

    public void run (TaskMonitor taskMonitor) {
        taskMonitor.setTitle("OCSANA results");

        // User inputs
        taskMonitor.showMessage(TaskMonitor.Level.INFO, "Source nodes: " + printNodes(sourceNodes));
        taskMonitor.showMessage(TaskMonitor.Level.INFO, "Target nodes: " + printNodes(targetNodes));
        taskMonitor.showMessage(TaskMonitor.Level.INFO, "Off-Target nodes: " + printNodes(offTargetNodes));

        // Paths
        taskMonitor.showMessage(TaskMonitor.Level.INFO, "Paths to off-targets: " + printEdgeSets(pathsToOffTargets));
        taskMonitor.showMessage(TaskMonitor.Level.INFO, "Paths to targets: " + printEdgeSets(pathsToTargets));

        // CIs
        taskMonitor.showMessage(TaskMonitor.Level.INFO, "Minimal CIs: " + printNodeSets(MHSes));
    }

    public <T> T getResults (Class<? extends T> type) {
        if (type.isAssignableFrom(OCSANAStep.class)) {
            return (T) algStep;
        } else {
            throw new IllegalArgumentException("Invalid results type for presenter.");
        }
    }

    // Helper functions for common printing tasks
    private String nodeName(CyNode node) {
        return network.getRow(node).get(CyNetwork.NAME, String.class);
    }

    private String printNodes(Iterable<CyNode> nodes) {
        if (nodes == null) {
            return new String();
        }

        List<String> strings = new ArrayList<>();
        for (CyNode node: nodes) {
            strings.add(nodeName(node));
        }

        return "[" + String.join(", ", strings) + "]";
    }

    private String printNodeSets(Iterable<? extends Iterable<CyNode>> nodeSets) {
        String result = new String();
        for (Iterable<CyNode> nodeSet: nodeSets) {
            result += printNodes(nodeSet) + "\n";
        }
        return result;
    }

    private String printEdges(Iterable<CyEdge> edges) {
        if (edges == null) {
            return new String();
        }

        String result = new String("[");

        // Handle first node
        try {
            CyNode firstNode = edges.iterator().next().getSource();
            result += nodeName(firstNode);
        } catch (NoSuchElementException e) {
            return "[]";
        }

        // Each other node is a target
        for (CyEdge edge: edges) {
            // TODO: Handle activation and inhibition symbols
            result += " -> ";
            result += nodeName(edge.getTarget());
        }

        result += "]";
        return result;
    }

    private String printEdgeSets(Iterable<? extends Iterable<CyEdge>> edgeSets) {
        String result = new String();
        for (Iterable<CyEdge> edgeSet: edgeSets) {
            result += printEdges(edgeSet) + "\n";
        }
        return result;
    }
}
