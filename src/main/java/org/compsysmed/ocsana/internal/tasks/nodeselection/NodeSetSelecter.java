/**
 * Interface handler for node selection
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
import org.cytoscape.work.util.ListMultipleSelection;

import org.cytoscape.model.CyNode;
import org.cytoscape.model.CyNetwork;

// OCSANA imports

/**
 * Interface handler for node selection
 *
 * @param network  the network to compute on
 **/

public class NodeSetSelecter {
    public static final String configGroup = "Select nodes";

    @Tunable(description = "Source nodes",
             gravity = 140,
             groups = {configGroup})
    public ListMultipleSelection<NodeWithName> sourceNodeStrings;

    @Tunable(description = "Target nodes",
             gravity = 150,
             groups = {configGroup})
    public ListMultipleSelection<NodeWithName> targetNodeStrings;

    @Tunable(description = "Off-target nodes",
             gravity = 160,
             groups = {configGroup})
    public ListMultipleSelection<NodeWithName> offTargetNodeStrings;

    private CyNetwork network;
    private List<CyNode> nodes;
    private List<NodeWithName> nodesWithNames;

    public NodeSetSelecter (CyNetwork network) {
        this.network = network;

        nodes = network.getNodeList();
        nodesWithNames = new ArrayList<>(nodes.size());

        for (CyNode node: nodes) {
            String nodeName = network.getRow(node).get(CyNetwork.NAME, String.class);
            nodesWithNames.add(new NodeWithName(node, nodeName));
        }

        // Sort the nodes alphabetically for display
        Collections.sort(nodesWithNames);

        sourceNodeStrings = new ListMultipleSelection<>(nodesWithNames);
        targetNodeStrings = new ListMultipleSelection<>(nodesWithNames);
        offTargetNodeStrings = new ListMultipleSelection<>(nodesWithNames);
    }

    public List<CyNode> getSourceNodes () {
        return getNodesFromList(sourceNodeStrings.getSelectedValues());
    }

    public List<CyNode> getTargetNodes () {
        return getNodesFromList(targetNodeStrings.getSelectedValues());
    }

    public List<CyNode> getOffTargetNodes () {
        return getNodesFromList(offTargetNodeStrings.getSelectedValues());
    }

    private List<CyNode> getNodesFromList (List<NodeWithName> nodesWithNames) {
        List<CyNode> nodes = new ArrayList<>(nodesWithNames.size());
        for (NodeWithName nodeWithName: nodesWithNames) {
            nodes.add(nodeWithName.node);
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
