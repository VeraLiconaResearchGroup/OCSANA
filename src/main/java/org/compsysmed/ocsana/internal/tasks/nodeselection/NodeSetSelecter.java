/**
 * Interface handler for selecting node sets in OCSANA
 *
 * Copyright Vera-Licona Research Group (C) 2015
 * @author Andrew Gainer-Dewar, Ph.D. <andrew.gainer.dewar@gmail.com>
 *
 * This software is licensed under the Artistic License 2.0, see the
 * LICENSE file or
 * http://www.opensource.org/licenses/artistic-license-2.0.php for
 * details
 **/

package org.compsysmed.ocsana.internal.tasks.nodeselection;

// Java imports
import java.util.*;

// Cytoscape imports
import org.cytoscape.work.Tunable;
import org.cytoscape.work.util.ListSingleSelection;
import org.cytoscape.work.util.ListMultipleSelection;

import org.cytoscape.model.CyNode;
import org.cytoscape.model.CyNetwork;

// OCSANA imports

/**
 * Interface handler for selecting node sets in OCSANA
 *
 * @param network  the network to compute on
 **/

public class NodeSetSelecter {
    public static final String configGroup = "Select nodes";
    public static final String configGroupSources = "Source nodes";
    public static final String configGroupTargets = "Target nodes";
    public static final String configGroupOffTargets = "Off-target nodes";

    protected static final String listSelectDescription = "Use Ctrl+click to select multiple nodes:";
    protected static final String stringSelectDescription = "Comma-separated list of node names:";

    @Tunable(description = "Node selection mode",
             gravity = 110,
             groups = {configGroup})
    public ListSingleSelection<String> selectMode = new ListSingleSelection<>("List", "String");

    @Tunable(gravity = 141,
             description = listSelectDescription,
             groups = {configGroup, configGroupSources},
             dependsOn = "selectMode=List")
    public ListMultipleSelection<NodeWithName> sourceNodeList;

    @Tunable(gravity = 142,
             description = stringSelectDescription,
             groups = {configGroup, configGroupSources},
             dependsOn = "selectMode=String")
    public String sourceNodeString;

    @Tunable(gravity = 151,
             description = listSelectDescription,
             groups = {configGroup, configGroupTargets},
             dependsOn = "selectMode=List")
    public ListMultipleSelection<NodeWithName> targetNodeList;

    @Tunable(gravity = 152,
             description = stringSelectDescription,
             groups = {configGroup, configGroupTargets},
             dependsOn = "selectMode=String")
    public String targetNodeString;

    @Tunable(gravity = 161,
             description = listSelectDescription,
             groups = {configGroup, configGroupOffTargets},
             dependsOn = "selectMode=List")
    public ListMultipleSelection<NodeWithName> offTargetNodeList;

    @Tunable(gravity = 162,
             description = stringSelectDescription,
             groups = {configGroup, configGroupOffTargets},
             dependsOn = "selectMode=String")
    public String offTargetNodeString;

    private CyNetwork network;
    private List<NodeWithName> nodesWithNames;
    private Map<String, CyNode> nodeNamesMap;

    public NodeSetSelecter (CyNetwork network) {
        this.network = network;

        List<CyNode> nodes = network.getNodeList();
        nodesWithNames = new ArrayList<>(nodes.size());
        nodeNamesMap = new HashMap<>(nodes.size());

        for (CyNode node: nodes) {
            String nodeName = network.getRow(node).get(CyNetwork.NAME, String.class);
            NodeWithName namedNode = new NodeWithName(node, nodeName);
            nodesWithNames.add(namedNode);
            nodeNamesMap.put(nodeName, node);
        }

        // Sort the nodes alphabetically for display
        Collections.sort(nodesWithNames);

        sourceNodeList = new ListMultipleSelection<>(nodesWithNames);
        sourceNodeString = "";

        targetNodeList = new ListMultipleSelection<>(nodesWithNames);
        targetNodeString = "";

        offTargetNodeList = new ListMultipleSelection<>(nodesWithNames);
        offTargetNodeString = "";
    }

    public List<CyNode> getSourceNodes () {
        switch (selectMode.getSelectedValue()) {
        case "List":
            return getNodesFromList(sourceNodeList.getSelectedValues());

        case "String":
            return getNodesFromString(sourceNodeString);

        default:
            throw new IllegalStateException("Invalid node selection mode");
        }
    }

    public List<CyNode> getTargetNodes () {
        switch (selectMode.getSelectedValue()) {
        case "List":
            return getNodesFromList(targetNodeList.getSelectedValues());

        case "String":
            return getNodesFromString(targetNodeString);

        default:
            throw new IllegalStateException("Invalid node selection mode");
        }
    }

    public List<CyNode> getOffTargetNodes () {
        switch (selectMode.getSelectedValue()) {
        case "List":
            return getNodesFromList(offTargetNodeList.getSelectedValues());

        case "String":
            return getNodesFromString(offTargetNodeString);

        default:
            throw new IllegalStateException("Invalid node selection mode");
        }
    }

    private List<CyNode> getNodesFromList (List<NodeWithName> nodesWithNames) {
        List<CyNode> nodes = new ArrayList<>(nodesWithNames.size());
        for (NodeWithName nodeWithName: nodesWithNames) {
            nodes.add(nodeWithName.node);
        }
        return nodes;
    }

    private List<CyNode> getNodesFromString (String nodeNames) {
        List<String> selectedNodeNames = Arrays.asList(nodeNames.trim().split(","));
        List<CyNode> nodes = new ArrayList<>(selectedNodeNames.size());
        for (String nodeName: selectedNodeNames) {
            String trimmedName = nodeName.trim();
            if (trimmedName.isEmpty()) {
                continue;
            }

            CyNode node = nodeNamesMap.get(trimmedName);
            if (node != null) {
                nodes.add(node);
            } else {
                System.out.println(nodeNames);
                throw new IllegalArgumentException("No node named " + trimmedName + " could be found.");
            }
        }
        return nodes;
    }
}

class NodeWithName implements Comparable<NodeWithName> {
    public CyNode node;
    public String name;

    public NodeWithName (CyNode node, String name) {
        this.node = node;
        this.name = name;
    }

    public String toString () {
        return name;
    }

    public int compareTo (NodeWithName otherNode) {
        return name.compareTo(otherNode.name);
    }
}
