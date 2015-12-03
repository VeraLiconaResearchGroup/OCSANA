/**
 * Container to hold results of an OCSANA run
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
import org.cytoscape.model.CyNode;
import org.cytoscape.model.CyEdge;
import org.cytoscape.model.CyIdentifiable;
import org.cytoscape.model.CyNetwork;

// OCSANA imports
import org.compsysmed.ocsana.internal.algorithms.path.AbstractPathFindingAlgorithm;
import org.compsysmed.ocsana.internal.algorithms.mhs.AbstractMHSAlgorithm;
import org.compsysmed.ocsana.internal.algorithms.scoring.OCSANAScoringAlgorithm;

public class OCSANAResults {
    // User inputs
    public CyNetwork network;
    public Set<CyNode> sourceNodes;
    public Set<CyNode> targetNodes;
    public Set<CyNode> offTargetNodes;

    // Paths data
    public AbstractPathFindingAlgorithm pathFindingAlg;
    public Boolean pathFindingCanceled;
    public Collection<? extends List<CyEdge>> pathsToTargets;
    public Collection<? extends List<CyEdge>> pathsToOffTargets;

    // Scoring data
    public OCSANAScoringAlgorithm ocsanaAlg;
    public Boolean scoringCanceled;

    // MHS data
    public AbstractMHSAlgorithm mhsAlg;
    public Boolean mhsFindingCanceled;
    public Collection<? extends Collection<CyNode>> MHSes;

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

        List<String> strings = new ArrayList<>();
         for (CyNode node: nodes) {
            strings.add(nodeName(node));
         }

        return "[" + String.join(", ", strings) + "]";
    }

    /**
     * Get a string representation of a path of (directed) edges
     *
     * The current format is "node1 -> node2 -> node3".
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
            // TODO: Handle activation and inhibition symbols
            result += " -> ";
            result += nodeName(edge.getTarget());
        }

        return result;
    }

    /**
     * Generate a report of the results of an OCSANA run
     **/
    public List<String> getReportLines() {
        // Format based on original OCSANA
        List<String> reportLines = new ArrayList<>();

        reportLines.add("--- Optimal cut set search report ---");
        reportLines.add("");

        if (sourceNodes != null) {
            List<String> sourceNodeNames = new ArrayList<>();
            for (CyNode node: sourceNodes) {
                sourceNodeNames.add(nodeName(node));
            }
            String sourceNodeString = "Source nodes: " + String.join(" ", sourceNodeNames);
            reportLines.add(sourceNodeString);

        }

        if (targetNodes != null) {
            List<String> targetNodeNames = new ArrayList<>();
            for (CyNode node: targetNodes) {
                targetNodeNames.add(nodeName(node));
            }
            String targetNodeString = "Target nodes: " + String.join(" ", targetNodeNames);
            reportLines.add(targetNodeString);

        }

        if (offTargetNodes != null) {
            List<String> offTargetNodeNames = new ArrayList<>();
            for (CyNode node: offTargetNodes) {
                offTargetNodeNames.add(nodeName(node));
            }
            String offTargetNodeString = "Side effect nodes: " + String.join(" ", offTargetNodeNames);
            reportLines.add(offTargetNodeString);
        }

        reportLines.add("");

        // TODO: handle activation conversion

        Set<CyNode> elementaryNodes = new HashSet<>();
        for (List<CyEdge> path: pathsToTargets) {
            for (CyEdge edge: path) {
                elementaryNodes.add(edge.getSource());
                elementaryNodes.add(edge.getTarget());
            }
        }

        String pathSummaryString = "Found " + pathsToTargets.size() + " elementary paths with search strategy " + pathFindingAlg.shortName() +  " and " + elementaryNodes.size() + " elementary nodes.";
        reportLines.add(pathSummaryString);
        reportLines.add("");

        for (List<CyEdge> path: pathsToTargets) {
            reportLines.add(pathString(path));
        }
        reportLines.add("");

        String mhsSummaryString = "Found " + MHSes.size() + " CIs using MHS algorithm " + mhsAlg.shortName();
        reportLines.add(mhsSummaryString);
        reportLines.add("");

        for (Collection<CyNode> mhs: MHSes) {
            // TODO: handle scoring information
            reportLines.add(nodeSetString(mhs));
        }

        return reportLines;
    }
}
