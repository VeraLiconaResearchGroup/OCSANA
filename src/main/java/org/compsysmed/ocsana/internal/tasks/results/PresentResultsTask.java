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
        taskMonitor.setTitle("Generating OCSANA results");

        for (String line: reportLines()) {
            taskMonitor.showMessage(TaskMonitor.Level.INFO, line);
        }
    }

    public <T> T getResults (Class<? extends T> type) {
        if (type.isAssignableFrom(OCSANAStep.class)) {
            return (T) algStep;
        } else {
            throw new IllegalArgumentException("Invalid results type for presenter.");
        }
    }

    // Helper functions for generating report sections
    private String report () {
        List<String> reportLines = reportLines();
        String report = String.join("\n", reportLines);
        return report;
    }

    private List<String> reportLines () {
        List<String> lines = new ArrayList<> ();

        lines.addAll(inputReportLines());
        lines.add(SECTION_SEPARATOR);

        lines.addAll(pathReportLines());
        lines.add(SECTION_SEPARATOR);

        lines.addAll(CIReportLines());

        return lines;
    }

    private List<String> inputReportLines () {
        List<String> lines = new ArrayList<> ();

        String headerLine = "User arguments";
        lines.add(headerLine);

        String sourceLine = "Source nodes: " + nodeSetString(sourceNodes);
        lines.add(sourceLine);

        String targetLine = "Target nodes: " + nodeSetString(targetNodes);
        lines.add(targetLine);

        String offTargetLine = "Off-target nodes: " + nodeSetString(offTargetNodes);
        lines.add(offTargetLine);

        return lines;
    }

    private List<String> pathReportLines() {
        List<String> lines = new ArrayList<> ();

        String algLine = "Path-finding algorithm: " + pathAlg.fullName();
        lines.add(algLine);

        lines.add("Paths to targets:");
        for (Iterable<CyEdge> path: pathsToTargets) {
            lines.add(edgeSetString(path));
        }
        lines.add(MINOR_SEPARATOR);

        lines.add("Paths to off-targets:");
        for (Iterable<CyEdge> path: pathsToOffTargets) {
            lines.add(edgeSetString(path));
        }

        return lines;
    }

    private List<String> CIReportLines() {
        List<String> lines = new ArrayList<> ();

        String algLine = "MHS algorithm: " + mhsAlg.fullName();
        lines.add(algLine);

        lines.add("Optimal CIs:");
        for (Iterable<CyNode> MHS: MHSes) {
            lines.add(nodeSetString(MHS));
        }

        return lines;
    }

    // Helper functions for generating strings from structures
    private String nodeName(CyNode node) {
        return network.getRow(node).get(CyNetwork.NAME, String.class);
    }

    private String nodeSetString(Iterable<CyNode> nodes) {
        if (nodes == null) {
            return new String();
        }

        List<String> strings = new ArrayList<>();
        for (CyNode node: nodes) {
            strings.add(nodeName(node));
        }

        return "[" + String.join(", ", strings) + "]";
    }

    private String edgeSetString(Iterable<CyEdge> edges) {
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

    private static final String MINOR_SEPARATOR = "\n";
    private static final String SECTION_SEPARATOR = "\n******\n";
}
