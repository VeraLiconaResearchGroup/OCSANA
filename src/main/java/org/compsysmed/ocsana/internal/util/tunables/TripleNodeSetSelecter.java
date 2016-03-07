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

package org.compsysmed.ocsana.internal.util.tunables;

// Java imports
import java.util.*;
import java.util.stream.Collectors;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

// Cytoscape imports
import org.cytoscape.work.ContainsTunables;
import org.cytoscape.work.Tunable;

import org.cytoscape.work.util.ListChangeListener;
import org.cytoscape.work.util.ListSelection;
import org.cytoscape.work.util.ListSingleSelection;

import org.cytoscape.model.CyColumn;
import org.cytoscape.model.CyNode;
import org.cytoscape.model.CyNetwork;
import org.cytoscape.model.CyTable;

// OCSANA imports
import org.compsysmed.ocsana.internal.util.tunables.AbstractTripleNodeSetSelecter;
import org.compsysmed.ocsana.internal.util.tunables.ListTripleNodeSetSelecter;
import org.compsysmed.ocsana.internal.util.tunables.StringTripleNodeSetSelecter;

/**
 * Interface handler for selecting node sets in OCSANA
 *
 * @param network  the network to compute on
 **/

public class TripleNodeSetSelecter
    implements ListChangeListener {
    public static final String UPDATE_EVENT_COMMAND = "Triple node selecter update";

    private static final String CONFIG_GROUP_LIST = "List";
    private static final String CONFIG_GROUP_STRING = "String";

    @Tunable(description = "Node name column",
             tooltip = "<html><b>Warning</b>: changing this may be slow if your network has a lot of vertices!</html>",
             gravity = 120)
    public ListSingleSelection<CyColumn> nodeNameColumnSelecter;

    private CyColumn currentNodeNameColumn;

    @Tunable(description = "Node selection mode",
             gravity = 121)
    public ListSingleSelection<String> modeSelecter;
    private String currentMode;

    @ContainsTunables
    public AbstractTripleNodeSetSelecter tripleNodeSetSelecter;

    private CyNetwork network;
    private Map<String, CyNode> nodeNamesMap;

    private Collection<ActionListener> listeners = new HashSet<>();

    public TripleNodeSetSelecter (CyNetwork network) {
        this.network = network;

        modeSelecter = new ListSingleSelection<>(CONFIG_GROUP_LIST, CONFIG_GROUP_STRING);
        modeSelecter.addListener(this);

        CyTable nodeTable = network.getDefaultNodeTable();
        List<CyColumn> nodeColumns = new ArrayList<>(nodeTable.getColumns());
        List<CyColumn> nodeStringColumns = new ArrayList<>();
        for (CyColumn column: nodeColumns) {
            if (String.class.isAssignableFrom(column.getType())) {
                nodeStringColumns.add(column);
            }
        }
        nodeNameColumnSelecter = new ListSingleSelection<>(nodeStringColumns);
        nodeNameColumnSelecter.addListener(this);

        populateSelecters();
    }

    private void populateSelecters () {
        String mode = modeSelecter.getSelectedValue();
        CyColumn nodeNameColumn = nodeNameColumnSelecter.getSelectedValue();

        if (currentMode != null && currentMode == mode && currentNodeNameColumn != null && currentNodeNameColumn == nodeNameColumn) {
            return;
        }

        currentMode = mode;
        currentNodeNameColumn = nodeNameColumn;

        List<CyNode> nodes = network.getNodeList();

        switch (mode) {
        case CONFIG_GROUP_LIST:
            if (tripleNodeSetSelecter == null) {
                tripleNodeSetSelecter = new ListTripleNodeSetSelecter(network, nodes, nodeNameColumn);
            } else {
                tripleNodeSetSelecter = new ListTripleNodeSetSelecter(tripleNodeSetSelecter);
            }
            break;

        case CONFIG_GROUP_STRING:
            if (tripleNodeSetSelecter == null) {
                tripleNodeSetSelecter = new StringTripleNodeSetSelecter(network, nodes, nodeNameColumn);
            } else {
                tripleNodeSetSelecter = new StringTripleNodeSetSelecter(tripleNodeSetSelecter);
            }
            break;

        default:
            throw new IllegalStateException(String.format("Unknown selection type %s", mode));
        }

        notifyListenersOfUpdate();
    }

    public CyColumn getNodeNameColumn () {
        return currentNodeNameColumn;
    }

    public Collection<CyNode> getSourceNodes () {
        return tripleNodeSetSelecter.getSelectedSourceNodes();
    }

    public Set<CyNode> getSourceNodeSet () {
        return new HashSet<>(getSourceNodes());
    }

    public Collection<CyNode> getTargetNodes () {
        return tripleNodeSetSelecter.getSelectedTargetNodes();
    }

    public Set<CyNode> getTargetNodeSet () {
        return new HashSet<>(getTargetNodes());
    }

    public Collection<CyNode> getOffTargetNodes () {
        return tripleNodeSetSelecter.getSelectedOffTargetNodes();
    }

    public Set<CyNode> getOffTargetNodeSet () {
        return new HashSet<>(getOffTargetNodes());
    }

    public String getNodeName (CyNode node) {
        return tripleNodeSetSelecter.getNodeName(node);
    }

    public void listChanged (ListSelection source) {
        throw new IllegalStateException("Something changed the lists in a NodeSetSelecter?");
    }

    public void selectionChanged (ListSelection source) {
        populateSelecters();
    }

    private void notifyListenersOfUpdate () {
        ActionEvent updateEvent = new ActionEvent(this, ActionEvent.ACTION_PERFORMED, UPDATE_EVENT_COMMAND);
        for (ActionListener listener: listeners) {
            listener.actionPerformed(updateEvent);
        }
    }

    public void addActionListener (ActionListener listener) {
        listeners.add(listener);
    }

    public void removeActionListener (ActionListener listener) {
        listeners.remove(listener);
    }
}
