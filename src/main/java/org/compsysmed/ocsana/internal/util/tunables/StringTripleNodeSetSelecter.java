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
    public String sourceNodes = "";

    @Tunable(description="Target nodes",
             tooltip="Enter as a comma-separated list",
             gravity=142)
    public String targetNodes = "";

    @Tunable(description="Off-target nodes",
             tooltip="Enter as a comma-separated list",
             gravity=143)
    public String offTargetNodes = "";

    public StringTripleNodeSetSelecter (CyNetwork network,
                                        Collection<CyNode> nodes,
                                        CyColumn nodeNameColumn) {
        super(network, nodes, nodeNameColumn);
    }

    public StringTripleNodeSetSelecter (CyNetwork network,
                                        Collection<CyNode> nodes,
                                        CyColumn nodeNameColumn,
                                        Collection<CyNode> selectedSourceNodes,
                                        Collection<CyNode> selectedTargetNodes,
                                        Collection<CyNode> selectedOffTargetNodes) {
        this(network, nodes, nodeNameColumn);

        setSelectedSourceNodes(selectedSourceNodes);
        setSelectedTargetNodes(selectedTargetNodes);
        setSelectedOffTargetNodes(selectedOffTargetNodes);
    }

    public StringTripleNodeSetSelecter (AbstractTripleNodeSetSelecter other) {
        this(other.network, other.nodes, other.nodeNameColumn, other.getSelectedSourceNodes(), other.getSelectedTargetNodes(), other.getSelectedOffTargetNodes());
    }

    @Override
    public Collection<CyNode> getSelectedSourceNodes () {
        return getSelectedNodes(sourceNodes);
    }

    @Override
    public Collection<String> getSelectedSourceNodeNames () {
        return getSelectedNodeNames(sourceNodes);
    }

    @Override
    protected void setSelectedSourceNodes (Collection<CyNode> selectedSourceNodes) {
        sourceNodes = selectedNodeString(selectedSourceNodes);
    }

    @Override
    public Collection<CyNode> getSelectedTargetNodes () {
        return getSelectedNodes(targetNodes);
    }

    @Override
    public Collection<String> getSelectedTargetNodeNames () {
        return getSelectedNodeNames(targetNodes);
    }

    @Override
    protected void setSelectedTargetNodes (Collection<CyNode> selectedTargetNodes) {
        targetNodes = selectedNodeString(selectedTargetNodes);
    }

    @Override
    public Collection<CyNode> getSelectedOffTargetNodes () {
        return getSelectedNodes(offTargetNodes);
    }

    @Override
    public Collection<String> getSelectedOffTargetNodeNames () {
        return getSelectedNodeNames(offTargetNodes);
    }

    @Override
    protected void setSelectedOffTargetNodes (Collection<CyNode> selectedOffTargetNodes) {
        offTargetNodes = selectedNodeString(selectedOffTargetNodes);
    }

    private Collection<CyNode> getSelectedNodes (String nodeList) {
        return getSelectedNodeNames(nodeList).stream().map(nodeName -> getNode(nodeName)).filter(node -> node != null).collect(Collectors.toSet());
    }

    private Collection<String> getSelectedNodeNames (String nodeList) {
        return Arrays.asList(nodeList.trim().split(",")).stream().map(nodeName -> nodeName.trim()).filter(nodeName -> !nodeName.isEmpty()).collect(Collectors.toSet());
    }

    private String selectedNodeString (Collection<CyNode> nodes) {
        return nodes.stream().map(node -> getNodeName(node)).collect(Collectors.joining(", "));
    }
}
