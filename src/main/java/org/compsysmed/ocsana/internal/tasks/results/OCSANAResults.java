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

package org.compsysmed.ocsana.internal.tasks.results;

// Java imports
import java.util.*;

// Cytoscape imports
import org.cytoscape.model.CyNode;
import org.cytoscape.model.CyEdge;
import org.cytoscape.model.CyIdentifiable;
import org.cytoscape.model.CyNetwork;

// OCSANA imports
import org.compsysmed.ocsana.internal.tasks.edgeprocessing.EdgeProcessor;

import org.compsysmed.ocsana.internal.algorithms.path.AbstractPathFindingAlgorithm;

import org.compsysmed.ocsana.internal.algorithms.mhs.AbstractMHSAlgorithm;

import org.compsysmed.ocsana.internal.algorithms.scoring.OCSANAScoringAlgorithm;
import org.compsysmed.ocsana.internal.algorithms.scoring.DrugBankScoringAlgorithm;

public class OCSANAResults {
    // User inputs
    public CyNetwork network;
    public EdgeProcessor edgeProcessor;
    public Set<CyNode> sourceNodes;
    public Set<CyNode> targetNodes;
    public Set<CyNode> offTargetNodes;

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

    public OCSANAResults () {};

    // Output generation
    /**
     * Get the name of a node
     *
     * @param node  the node
     **/
    public String nodeName(CyNode node) {
        return network.getRow(node).get(CyNetwork.NAME, String.class);
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
            return new String();
        }

        List<String> nodeStrings = new ArrayList<>();
         for (CyNode node: nodes) {
            nodeStrings.add(nodeName(node));
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
            return new String();
        }

        String result = new String();

        // Handle first node
        try {
            CyNode firstNode = path.iterator().next().getSource();
            result += nodeName(firstNode);
        } catch (NoSuchElementException e) {
            return result;
        }

        // Each other node is a target
        for (CyEdge edge: path) {
            if (edgeProcessor.edgeIsInhibition(edge)) {
                result += " -| ";
            } else {
                result += " -> ";
            }
            result += nodeName(edge.getTarget());
        }

        return result;
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

        if (sourceNodes != null) {
            List<String> sourceNodeNames = new ArrayList<>();
            for (CyNode node: sourceNodes) {
                sourceNodeNames.add(nodeName(node));
            }
            String sourceNodeString = "Source nodes: " + String.join(", ", sourceNodeNames);
            reportLines.add(sourceNodeString);

        }

        if (targetNodes != null) {
            List<String> targetNodeNames = new ArrayList<>();
            for (CyNode node: targetNodes) {
                targetNodeNames.add(nodeName(node));
            }
            String targetNodeString = "Target nodes: " + String.join(", ", targetNodeNames);
            reportLines.add(targetNodeString);

        }

        if (offTargetNodes != null) {
            List<String> offTargetNodeNames = new ArrayList<>();
            for (CyNode node: offTargetNodes) {
                offTargetNodeNames.add(nodeName(node));
            }
            String offTargetNodeString = "Side effect nodes: " + String.join(", ", offTargetNodeNames);
            reportLines.add(offTargetNodeString);
        }

        reportLines.add("");

        Set<CyNode> elementaryNodes = new HashSet<>();
        for (List<CyEdge> path: pathsToTargets) {
            for (CyEdge edge: path) {
                elementaryNodes.add(edge.getSource());
                elementaryNodes.add(edge.getTarget());
            }
        }

        reportLines.add(String.format("Found %d elementary paths with search strategy %s and %d elementary nodes.", pathsToTargets.size(), pathFindingAlg.shortName(), elementaryNodes.size()));
        reportLines.add(String.format("Search times: %fs. for targets, %fs. for off-targets.", pathsToTargetsExecutionSeconds, pathsToOffTargetsExecutionSeconds));
        reportLines.add("");

        for (List<CyEdge> path: pathsToTargets) {
            reportLines.add(pathString(path));
        }
        reportLines.add("");

        if (ocsanaAlg.hasScores()) {
            reportLines.add(String.format("Computed OCSANA scores in %fs.", OCSANAScoringExecutionSeconds));
            reportLines.add("");
        } else {
            reportLines.add("OCSANA scoring disabled.");
            reportLines.add("");
        }

        if (drugBankAlg.hasScores()) {
            reportLines.add(String.format("Computed DrugBank scores in %fs.", drugBankScoringExecutionSeconds));
            reportLines.add("");
        } else {
            reportLines.add("DrugBank scoring disabled.");
            reportLines.add("");
        }

        String ciInclusionVerb = (includeEndpointsInCIs) ? "including" : "excluding";
        reportLines.add(String.format("Found %d CIs using MHS algorithm %s (%s endpoints) in %fs.", MHSes.size(), mhsAlg.shortName(), ciInclusionVerb, mhsExecutionSeconds));
        reportLines.add("");

        for (Set<CyNode> mhs: MHSes) {
            String scoreReport = new String();

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
    }
}
