/**
 * Widget to process node names
 *
 * Copyright Vera-Licona Research Group (C) 2016
 *
 * This software is licensed under the Artistic License 2.0, see the
 * LICENSE file or
 * http://www.opensource.org/licenses/artistic-license-2.0.php for
 * details
 **/

package org.compsysmed.ocsana.internal.util.tunables;

// Java imports
import java.util.*;

// Cytoscape imports
import org.cytoscape.model.CyColumn;
import org.cytoscape.model.CyNode;
import org.cytoscape.model.CyNetwork;
import org.cytoscape.model.CyTable;

/**
 * Widget to process node names based on a user-selected table column
 * <p>
 * By default, we use the "SUID" column, since this is guaranteed to exist.
 **/
public class NodeNameHandler {
    private CyNetwork network;
    private CyColumn nodeNameColumn;

    private Map<String, CyNode> nodeNamesMap;

    /**
     * Constructor
     *
     * @param network  the network from which nodes are drawn
     **/
    public NodeNameHandler (CyNetwork network) {
        this.network = network;
        setNodeNameColumn(network.getDefaultNodeTable().getColumn("SUID"));
    }

    /**
     * Set the column in the node table which contains the names of nodes
     **/
    public void setNodeNameColumn (CyColumn nodeNameColumn) {
        this.nodeNameColumn = nodeNameColumn;

        nodeNamesMap = new HashMap<>(network.getNodeCount());

        List<CyNode> nodes = network.getNodeList();
        for (CyNode node: nodes) {
            String nodeName = getNodeName(node);
            nodeNamesMap.put(nodeName, node);
        }

        System.out.println(String.format("Populated node name handler with %d nodes with names from column %s (example: %s)", nodeNamesMap.size(), nodeNameColumn.getName(), nodeNamesMap.keySet().stream().findFirst().get()));
    }

    /**
     * Get the name of a node
     *
     * @param node  the node
     * @return a string representation of the node
     **/
    public String getNodeName (CyNode node) {
        return network.getRow(node).get(nodeNameColumn.getName(), Object.class).toString();
    }

    /**
     * Get the node with a given name
     *
     * @param nodeName  the name
     * @return the node
     * @throws IllegalArgumentException if the specified name is not
     * found in the network
     **/
    public CyNode getNode (String nodeName) {
        if (nodeNamesMap.containsKey(nodeName)) {
            return nodeNamesMap.get(nodeName);
        } else {
            throw new IllegalArgumentException(String.format("No node is named \"%s\"", nodeName));
        }
    }
}
