/**
 * Interface handler for selecting nodes with a string
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
import java.util.stream.Collectors;

// Cytoscape imports
import org.cytoscape.work.Tunable;

import org.cytoscape.model.CyColumn;
import org.cytoscape.model.CyNode;
import org.cytoscape.model.CyNetwork;
import org.cytoscape.model.CyTable;

public class StringTripleNodeSetSelecter
    extends AbstractTripleNodeSetSelecter {
    // User options
    @Tunable(description="Source nodes",
             tooltip="Enter as a comma-separated list",
             gravity=141)
    public String sourceNodeList = "";

    @Tunable(description="Target nodes",
             tooltip="Enter as a comma-separated list",
             gravity=142)
    public String targetNodeList = "";

    @Tunable(description="Off-target nodes",
             tooltip="Enter as a comma-separated list",
             gravity=143)
    public String offTargetNodeList = "";

    public StringTripleNodeSetSelecter (CyNetwork network,
                                        Collection<CyNode> nodes,
                                        CyColumn nodeNameColumn) {
        super(network, nodes, nodeNameColumn);
    }

    @Override
    public List<CyNode> getSelectedSourceNodes () {
        return getSelectedNodes(sourceNodeList);
    }

    @Override
    public List<String> getSelectedSourceNodeNames () {
        return getSelectedNodeNames(sourceNodeList);
    }

    @Override
    public List<CyNode> getSelectedTargetNodes () {
        return getSelectedNodes(targetNodeList);
    }

    @Override
    public List<String> getSelectedTargetNodeNames () {
        return getSelectedNodeNames(targetNodeList);
    }

    @Override
    public List<CyNode> getSelectedOffTargetNodes () {
        return getSelectedNodes(offTargetNodeList);
    }

    @Override
    public List<String> getSelectedOffTargetNodeNames () {
        return getSelectedNodeNames(offTargetNodeList);
    }

    public List<CyNode> getSelectedNodes (String nodeList) {
        return getSelectedNodeNames(nodeList).stream().map(nodeName -> getNode(nodeName)).filter(node -> node != null).collect(Collectors.toList());
    }

    public List<String> getSelectedNodeNames (String nodeList) {
        return Arrays.asList(nodeList.trim().split(",")).stream().map(nodeName -> nodeName.trim()).filter(nodeName -> !nodeName.isEmpty()).collect(Collectors.toList());
    }
}
