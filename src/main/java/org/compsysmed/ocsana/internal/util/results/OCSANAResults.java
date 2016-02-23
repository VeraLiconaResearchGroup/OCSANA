/**
 * Container to hold results of an OCSANA run
 *
 * Copyright Vera-Licona Research Group (C) 2015
 *
 * This software is licensed under the Artistic License 2.0, see the
 * LICENSE file or
 * http://www.opensource.org/licenses/artistic-license-2.0.php for
 * details
 **/

package org.compsysmed.ocsana.internal.util.results;

// Java imports
import java.util.*;
import java.util.stream.Collectors;

// Cytoscape imports
import org.cytoscape.model.CyNode;
import org.cytoscape.model.CyEdge;
import org.cytoscape.model.CyNetwork;

// OCSANA imports
import org.compsysmed.ocsana.internal.tasks.nodeselection.NodeSetSelecter;
import org.compsysmed.ocsana.internal.tasks.edgeprocessing.EdgeProcessor;

import org.compsysmed.ocsana.internal.algorithms.path.AbstractPathFindingAlgorithm;

import org.compsysmed.ocsana.internal.algorithms.mhs.AbstractMHSAlgorithm;

import org.compsysmed.ocsana.internal.algorithms.scoring.OCSANAScoringAlgorithm;
import org.compsysmed.ocsana.internal.algorithms.scoring.DrugBankScoringAlgorithm;

public class OCSANAResults {
    // User inputs
    public CyNetwork network;
    public EdgeProcessor edgeProcessor;
    public NodeSetSelecter nodeSetSelecter;

    // Paths data
    public AbstractPathFindingAlgorithm pathFindingAlg;
    public Boolean pathFindingCanceled = false;
    public Collection<List<CyEdge>> pathsToTargets;
    public Collection<List<CyEdge>> pathsToOffTargets;

    public Double pathsToTargetsExecutionSeconds;
    public Double pathsToOffTargetsExecutionSeconds;

    // Scoring data
    public OCSANAScoringAlgorithm ocsanaAlg;
    public Double OCSANAScoringExecutionSeconds;

    public DrugBankScoringAlgorithm drugBankAlg;
    public Double drugBankScoringExecutionSeconds;

    // MHS data
    public AbstractMHSAlgorithm mhsAlg;
    public Boolean mhsFindingCanceled = false;
    public Collection<Set<CyNode>> MHSes;
    public Boolean includeEndpointsInCIs;

    public Double mhsExecutionSeconds;

    // Report data
    private List<String> reportLines;

    // Output generation
    /**
     * Get a string representation of a node
     *
     * @param node  the node
     **/
    public String nodeString (CyNode node) {
        return nodeSetSelecter.getNodeName(node);
    }

    /**
     * Get a string representation of a set of nodes
     *
     * The current format is "[node1, node2, node3]".
     *
     * @param nodes  the Collection of nodes
     **/
    public String nodeSetString(Collection<CyNode> nodes) {
        if (nodes == null) {
            return "";
        }

        List<String> nodeStrings = new ArrayList<>();
        for (CyNode node: nodes) {
            nodeStrings.add(nodeString(node));
        }

        Collections.sort(nodeStrings);

        return "[" + String.join(", ", nodeStrings) + "]";
    }

    /**
     * Get a string representation of a path of (directed) edges
     *
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
            result.append(nodeSetSelecter.getNodeName(firstNode));
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
            result.append(nodeSetSelecter.getNodeName(edge.getTarget()));
        }

        return result.toString();
    }

    /**
     * Get a report of the results of an OCSANA run (from the cache if
     * possible)
     **/
    public List<String> getReportLines () {
        if (reportLines == null) {
            generateReportLines();
        }

        return reportLines;
    }

    /**
     * Generate a report of the results of an OCSANA run and store it in
     * reportLines
     *
     * This should only be called by getReportLines, which handles
     * caching the results of this construction
     **/
    private void generateReportLines () {
        // Format based on original OCSANA
        reportLines = new ArrayList<>();

        reportLines.add("--- Optimal cut set search report ---");
        reportLines.add("");

        reportLines.add(String.format("Network name: %s", network.getRow(network).get(CyNetwork.NAME, String.class)));
        reportLines.add("");

        reportLines.add(String.format("Node names from column: %s", nodeSetSelecter.getNodeNameColumn()));

        List<CyNode> sourceNodes = nodeSetSelecter.getSourceNodes();
        if (sourceNodes != null) {
            String sourceNodeNameList = sourceNodes.stream().map(node -> nodeSetSelecter.getNodeName(node)).collect(Collectors.joining(", "));
            reportLines.add(String.format("Source nodes: %s", sourceNodeNameList));
        }

        List<CyNode> targetNodes = nodeSetSelecter.getTargetNodes();
        if (targetNodes != null) {
            String targetNodeNameList = targetNodes.stream().map(node -> nodeSetSelecter.getNodeName(node)).collect(Collectors.joining(", "));
            reportLines.add(String.format("Target nodes: %s", targetNodeNameList));
        }

        List<CyNode> offTargetNodes = nodeSetSelecter.getOffTargetNodes();
        if (offTargetNodes != null) {
            String offTargetNodeNameList = offTargetNodes.stream().map(node -> nodeSetSelecter.getNodeName(node)).collect(Collectors.joining(", "));
            reportLines.add(String.format("Off-target nodes: %s", offTargetNodeNameList));
        }

        reportLines.add("");

        Set<CyNode> elementaryNodes = new HashSet<>();
        for (List<CyEdge> path: pathsToTargets) {
            for (CyEdge edge: path) {
                elementaryNodes.add(edge.getSource());
                elementaryNodes.add(edge.getTarget());
            }
        }

        reportLines.add(String.format("Path-finding algorithm: %s", pathFindingAlg.description()));
        reportLines.add(String.format("Found %d elementary paths and %d elementary nodes.", pathsToTargets.size(), elementaryNodes.size()));
        reportLines.add(String.format("Search times: %fs. for targets, %fs. for off-targets.", pathsToTargetsExecutionSeconds, pathsToOffTargetsExecutionSeconds));
        reportLines.add("");

        for (List<CyEdge> path: pathsToTargets) {
            reportLines.add(pathString(path));
        }
        reportLines.add("");

        reportLines.add(edgeProcessor.description());
        reportLines.add("");

        if (ocsanaAlg.hasScores()) {
            reportLines.add(String.format("Scoring algorithm: %s", ocsanaAlg.description()));
            reportLines.add(String.format("Computed OCSANA scores in %fs.", OCSANAScoringExecutionSeconds));
            reportLines.add("");
        } else {
            reportLines.add("OCSANA scoring disabled.");
            reportLines.add("");
        }

        if (drugBankAlg.hasScores()) {
            reportLines.add(String.format("Scoring algorithm: %s", drugBankAlg.description()));
            reportLines.add(String.format("Computed DrugBank scores in %fs.", drugBankScoringExecutionSeconds));
            reportLines.add("");
        } else {
            reportLines.add("DrugBank scoring disabled.");
            reportLines.add("");
        }

        reportLines.add(String.format("CI-finding algorithm: %s", mhsAlg.description()));
        reportLines.add(String.format("Found %d CIs in %fs.", MHSes.size(), mhsExecutionSeconds));
        reportLines.add("");

        for (Set<CyNode> mhs: MHSes) {
            String scoreReport = "";

            if (ocsanaAlg.hasScores()) {
                scoreReport += String.format("OCSANA: %f", ocsanaAlg.scoreNodeSet(mhs));
            }

            if (ocsanaAlg.hasScores() && drugBankAlg.hasScores()) {
                scoreReport += ", ";
            }

            if (drugBankAlg.hasScores()) {
                scoreReport += String.format("DrugBank: %f", drugBankAlg.scoreNodeSet(mhs));
            }

            reportLines.add(String.format("%s (%s)", nodeSetString(mhs), scoreReport));
        }

        reportLines.add("");

        reportLines.add("EFFECT_ON_TARGETS × SET score matrix");
        reportLines.add("Rows: elementary node, columns: target nodes");
        reportLines.add("");

        reportLines.add("Elementary node / Target node\t" + targetNodes.stream().map(node -> nodeString(node)).collect(Collectors.joining("\t")));

        for (CyNode elementaryNode: ocsanaAlg.elementaryNodes()) {
            StringJoiner elementaryNodeLine = new StringJoiner("\t");
            elementaryNodeLine.add(nodeString(elementaryNode));
            for (CyNode targetNode: targetNodes) {
                Double effectScore = ocsanaAlg.effectOnTargetsScore(elementaryNode, targetNode);
                Integer setScore = ocsanaAlg.nodeSubPathsToTarget(elementaryNode, targetNode).size();

                System.out.println(String.format("Node %s, target %s: effect %f, SET %d", nodeString(elementaryNode), nodeString(targetNode), effectScore, setScore));

                Double totalScore = effectScore * setScore;
                elementaryNodeLine.add(totalScore.toString());
            }
            reportLines.add(elementaryNodeLine.toString());
        }
    }
}
