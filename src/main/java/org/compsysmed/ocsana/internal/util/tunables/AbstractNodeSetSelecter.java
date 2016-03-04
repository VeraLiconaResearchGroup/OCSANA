/**
 * Abstract interface for node selecters
 *
 * Copyright Vera-Licona Research Group (C) 2015
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

public class AbstractNodeSetSelecter {
    private CyNetwork network;
    private CyColumn nodeNameColumn;

    protected Collection<CyNode> nodes;
    protected Map<String, CyNode> nodeNamesMap;

    public AbstractNodeSetSelecter (CyNetwork network,
                                    Collection<CyNode> nodes,
                                    CyColumn nodeNameColumn) {
        this.network = network;
        this.nodes = nodes;
        this.nodeNameColumn = nodeNameColumn;

        nodeNamesMap = new HashMap<>(nodes.size());

        for (CyNode node: nodes) {
            String nodeName = getNodeName(node);
            nodeNamesMap.put(nodeName, node);
        }
    }

    public CyColumn getNodeNameColumn () {
        return nodeNameColumn;
    }

    public Collection<CyNode> getAllNodes () {
        return nodeNamesMap.values();
    }

    public Collection<String> getAllNodeNames () {
        return nodeNamesMap.keySet();
    }

    public String getNodeName (CyNode node) {
        return network.getRow(node).get(nodeNameColumn.getName(), String.class);
    }

    public CyNode getNode (String nodeName) {
        if (nodeNamesMap.containsKey(nodeName)) {
            return nodeNamesMap.get(nodeName);
        } else {
            throw new IllegalArgumentException(String.format("No node is named \"%s\"", nodeName));
        }
    }
}
