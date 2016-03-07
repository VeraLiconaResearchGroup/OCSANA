/**
 * Interface handler for selecting nodes from a list
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
import java.lang.reflect.*;
import java.lang.annotation.*;

import java.util.*;
import java.util.stream.Collectors;

// Cytoscape imports
import org.cytoscape.work.Tunable;

import org.cytoscape.work.util.ListMultipleSelection;

import org.cytoscape.model.CyColumn;
import org.cytoscape.model.CyNode;
import org.cytoscape.model.CyNetwork;
import org.cytoscape.model.CyTable;

public class ListTripleNodeSetSelecter
    extends AbstractTripleNodeSetSelecter {
    // User options
    @Tunable(description="Source nodes",
             tooltip="Ctrl+click to select multiple nodes",
             gravity=141)
    public ListMultipleSelection<String> sourceNodes;

    @Tunable(description="Target nodes",
             tooltip="Ctrl+click to select multiple nodes",
             gravity=142)
    public ListMultipleSelection<String> targetNodes;

    @Tunable(description="Off-target nodes",
             tooltip="Ctrl+click to select multiple nodes",
             gravity=143)
    public ListMultipleSelection<String> offTargetNodes;

    public ListTripleNodeSetSelecter (CyNetwork network,
                                      Collection<CyNode> nodes,
                                      CyColumn nodeNameColumn) {
        super(network, nodes, nodeNameColumn);

        List<String> nodeNames = new ArrayList<>(getAllNodeNames());
        Collections.sort(nodeNames);

        sourceNodes = new ListMultipleSelection<>(nodeNames);
        targetNodes = new ListMultipleSelection<>(nodeNames);
        offTargetNodes = new ListMultipleSelection<>(nodeNames);
    }

    public ListTripleNodeSetSelecter (CyNetwork network,
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

    public ListTripleNodeSetSelecter (AbstractTripleNodeSetSelecter other) {
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
        setSelectedNodes(sourceNodes, selectedSourceNodes);
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
        setSelectedNodes(targetNodes, selectedTargetNodes);
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
        setSelectedNodes(offTargetNodes, selectedOffTargetNodes);
    }

    private Collection<CyNode> getSelectedNodes (ListMultipleSelection<String> nodes) {
        return nodes.getSelectedValues().stream().map(name -> getNode(name)).collect(Collectors.toSet());
    }

    private Collection<String> getSelectedNodeNames (ListMultipleSelection<String> nodes) {
        return nodes.getSelectedValues();
    }

    private void setSelectedNodes (ListMultipleSelection<String> selecter,
                                   Collection<CyNode> nodes) {
        List<String> selectedNodeNames = nodes.stream().map(node -> getNodeName(node)).collect(Collectors.toList());
        selecter.setSelectedValues(selectedNodeNames);
    }
}
