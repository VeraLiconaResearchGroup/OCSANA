/**
 * Interface handler for selecting node sets in OCSANA
 *
 * Copyright Vera-Licona Research Group (C) 2015
 *
 * This software is licensed under the Artistic License 2.0, see the
 * LICENSE file or
 * http://www.opensource.org/licenses/artistic-license-2.0.php for
 * details
 **/

package org.compsysmed.ocsana.internal.tasks.nodeselection;

// Java imports
import java.util.*;
import java.util.stream.Collectors;

// Cytoscape imports
import org.cytoscape.work.Tunable;
import org.cytoscape.work.util.ListSingleSelection;
import org.cytoscape.work.util.ListMultipleSelection;

import org.cytoscape.model.CyColumn;
import org.cytoscape.model.CyNode;
import org.cytoscape.model.CyNetwork;
import org.cytoscape.model.CyTable;

// OCSANA imports

/**
 * Interface handler for selecting node sets in OCSANA
 *
 * @param network  the network to compute on
 **/

public class NodeSetSelecter {
    private static final String CONFIG_GROUP = "Select nodes";
    private static final String CONFIG_GROUP_LIST = "List";
    private static final String CONFIG_GROUP_STRING = "String";
    private static final String CONFIG_GROUP_SOURCES = "Source nodes";
    private static final String CONFIG_GROUP_TARGETS = "Target nodes";
    private static final String CONFIG_GROUP_OFF_TARGETS = "Off-target nodes";

    private static final String CONFIG_GROUP_TITLE_PARAM = "groupTitles=displayed,hidden,displayed";

    private static final String LIST_SELECT_DESCRIPTION = "Use Ctrl+click to select multiple nodes:";
    private static final String STRING_SELECT_DESCRIPTION = "Comma-separated list of node names:";

    @Tunable(description = "Column containing node names",
             tooltip = "<html><b>Warning</b>: changing this may be slow if your network has a lot of vertices!</html>",
             gravity = 140)
    public ListSingleSelection<CyColumn> getNodeNameColumnSelecter () {
        return nodeNameColumnSelecter;
    }

    public void setNodeNameColumnSelecter (ListSingleSelection<CyColumn> nodeNameColumnSelecter) {
        this.nodeNameColumnSelecter = nodeNameColumnSelecter;
        populateWithNodeNames();
    }

    private ListSingleSelection<CyColumn> nodeNameColumnSelecter;

    public CyColumn getNodeNameColumn () {
        return nodeNameColumnSelecter.getSelectedValue();
    }

    @Tunable(description = "Node selection mode",
             gravity = 141,
             groups = {CONFIG_GROUP},
             xorChildren = true)
    public ListSingleSelection<String> selectMode = new ListSingleSelection<>(CONFIG_GROUP_LIST, CONFIG_GROUP_STRING);

    @Tunable(gravity = 151,
             description = LIST_SELECT_DESCRIPTION,
             groups = {CONFIG_GROUP, CONFIG_GROUP_LIST, CONFIG_GROUP_SOURCES},
             params = CONFIG_GROUP_TITLE_PARAM,
             xorKey = CONFIG_GROUP_LIST,
             listenForChange = "NodeNameColumnSelecter")
    public ListMultipleSelection<String> sourceNodeList;

    @Tunable(gravity = 151,
             description = STRING_SELECT_DESCRIPTION,
             groups = {CONFIG_GROUP, CONFIG_GROUP_STRING, CONFIG_GROUP_SOURCES},
             params = CONFIG_GROUP_TITLE_PARAM,
             xorKey = CONFIG_GROUP_STRING)
    public String sourceNodeString = "";

    @Tunable(gravity = 152,
             description = LIST_SELECT_DESCRIPTION,
             groups = {CONFIG_GROUP, CONFIG_GROUP_LIST, CONFIG_GROUP_TARGETS},
             params = CONFIG_GROUP_TITLE_PARAM,
             xorKey = CONFIG_GROUP_LIST,
             listenForChange = "NodeNameColumnSelecter")
    public ListMultipleSelection<String> targetNodeList;

    @Tunable(gravity = 152,
             description = STRING_SELECT_DESCRIPTION,
             groups = {CONFIG_GROUP, CONFIG_GROUP_STRING, CONFIG_GROUP_TARGETS},
             params = CONFIG_GROUP_TITLE_PARAM,
             xorKey = CONFIG_GROUP_STRING)
    public String targetNodeString = "";

    @Tunable(gravity = 153,
             description = LIST_SELECT_DESCRIPTION,
             groups = {CONFIG_GROUP, CONFIG_GROUP_LIST, CONFIG_GROUP_OFF_TARGETS},
             params = CONFIG_GROUP_TITLE_PARAM,
             xorKey = CONFIG_GROUP_LIST,
             listenForChange = "NodeNameColumnSelecter")
    public ListMultipleSelection<String> offTargetNodeList;

    @Tunable(gravity = 153,
             description = STRING_SELECT_DESCRIPTION,
             groups = {CONFIG_GROUP, CONFIG_GROUP_STRING, CONFIG_GROUP_OFF_TARGETS},
             params = CONFIG_GROUP_TITLE_PARAM,
             xorKey = CONFIG_GROUP_STRING)
    public String offTargetNodeString = "";

    private CyNetwork network;
    private Map<String, CyNode> nodeNamesMap;

    public NodeSetSelecter (CyNetwork network) {
        this.network = network;

        CyTable nodeTable = network.getDefaultNodeTable();
        List<CyColumn> nodeColumns = new ArrayList<>(nodeTable.getColumns());
        List<CyColumn> nodeStringColumns = new ArrayList<>();
        for (CyColumn column: nodeColumns) {
            if (String.class.isAssignableFrom(column.getType())) {
                nodeStringColumns.add(column);
            }
        }
        nodeNameColumnSelecter = new ListSingleSelection<>(nodeStringColumns);

        populateWithNodeNames();
    }

    private void populateWithNodeNames () {
        List<CyNode> nodes = network.getNodeList();
        List<String> nodeNames = new ArrayList<>(nodes.size());
        nodeNamesMap = new HashMap<>(nodes.size());

        for (CyNode node: nodes) {
            String nodeName = getNodeName(node);
            nodeNames.add(nodeName);
            nodeNamesMap.put(nodeName, node);
        }

        // Sort the nodes alphabetically for display
        Collections.sort(nodeNames);

        sourceNodeList = new ListMultipleSelection<>(nodeNames);
        targetNodeList = new ListMultipleSelection<>(nodeNames);
        offTargetNodeList = new ListMultipleSelection<>(nodeNames);
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

    public Set<CyNode> getSourceNodeSet () {
        return new HashSet<>(getSourceNodes());
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

    public Set<CyNode> getTargetNodeSet () {
        return new HashSet<>(getTargetNodes());
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

    public Set<CyNode> getOffTargetNodeSet () {
        return new HashSet<>(getOffTargetNodes());
    }

    public String getNodeName (CyNode node) {
        String nodeNameColumn = nodeNameColumnSelecter.getSelectedValue().getName();
        return network.getRow(node).get(nodeNameColumn, String.class);
    }

    private List<CyNode> getNodesFromList (List<String> nodeNames) {
        List<CyNode> nodes = new ArrayList<>(nodeNames.size());
        for (String nodeName: nodeNames) {
            if (nodeNamesMap.containsKey(nodeName)) {
                nodes.add(nodeNamesMap.get(nodeName));
            } else {
                throw new IllegalArgumentException(String.format("No node named %s", nodeName));
            }
        }
        return nodes;
    }

    private List<CyNode> getNodesFromString (String commaSeparatedNodeNames) {
        // Split input string at commas and trim whitespace and empty entries
        List<String> selectedNodeNames = Arrays.stream(commaSeparatedNodeNames.trim().split(",")).map(name -> name.trim()).filter(name -> !name.isEmpty()).collect(Collectors.toList());
        return getNodesFromList(selectedNodeNames);
    }
}
