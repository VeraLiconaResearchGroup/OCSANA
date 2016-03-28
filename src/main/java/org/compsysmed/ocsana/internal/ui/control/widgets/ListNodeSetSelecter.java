/**
 * Widget to let users select a set of nodes with a list
 *
 * Copyright Vera-Licona Research Group (C) 2016
 *
 * This software is licensed under the Artistic License 2.0, see the
 * LICENSE file or
 * http://www.opensource.org/licenses/artistic-license-2.0.php for
 * details
 **/

package org.compsysmed.ocsana.internal.ui.control.widgets;

// Java imports
import java.util.*;
import java.util.stream.Collectors;

import java.awt.Component;

import javax.swing.BoxLayout;
import javax.swing.DefaultListCellRenderer;
import javax.swing.JLabel;
import javax.swing.JList;
import javax.swing.JScrollPane;
import javax.swing.ListCellRenderer;
import javax.swing.ListModel;
import javax.swing.ListSelectionModel;

// Cytoscape imports
import org.cytoscape.model.CyNode;

// OCSANA imports
import org.compsysmed.ocsana.internal.util.tunables.NodeNameHandler;

public class ListNodeSetSelecter
    extends AbstractNodeSetSelecter {
    private JList<CyNode> nodeSetListField;

    public ListNodeSetSelecter (String label,
                                Collection<CyNode> availableNodes,
                                Set<CyNode> selectedNodes,
                                NodeNameHandler nodeNameHandler) {
        super(label, availableNodes, selectedNodes, nodeNameHandler);
        draw();
    }

    public ListNodeSetSelecter (String label,
                                Collection<CyNode> availableNodes,
                                NodeNameHandler nodeNameHandler) {
        super(label, availableNodes, nodeNameHandler);
        draw();
    }

    public ListNodeSetSelecter (AbstractNodeSetSelecter other) {
        super(other);
        draw();
    }

    public ListNodeSetSelecter (AbstractNodeSetSelecter other,
                                NodeNameHandler nodeNameHandler) {
        super(other, nodeNameHandler);
        draw();
    }

    /**
     * Build the JPanel after the constructors populate the data
     **/
    private void draw () {
        setLayout(new BoxLayout(this, BoxLayout.LINE_AXIS));
        JLabel title = new JLabel(label);
        add(title);

        JScrollPane listPane = new JScrollPane(nodeSetListField);
        add(listPane);
    }

    @Override
    public Set<CyNode> getSelectedNodes () {
        return nodeSetListField.getSelectedValuesList().stream().collect(Collectors.toSet());
    }

    @Override
    public void setSelectedNodes (Set<CyNode> selectedNodes) {
        // Build selecter if needed
        if (nodeSetListField == null) {
            nodeSetListField = new JList<>(availableNodes.toArray(new CyNode[availableNodes.size()]));
            nodeSetListField.setSelectionMode(ListSelectionModel.MULTIPLE_INTERVAL_SELECTION);
        }

        // Install cell renderer to show correct names
        nodeSetListField.setCellRenderer(new NodeListCellRenderer(nodeNameHandler));

        // Select specified nodes
        nodeSetListField.clearSelection();

        ListModel<CyNode> nodeSetListModel = nodeSetListField.getModel();
        List<Integer> selectedIndices = new ArrayList<>();
        for (int i = 0; i < nodeSetListModel.getSize(); i++) {
            if (selectedNodes.contains(nodeSetListModel.getElementAt(i))) {
                selectedIndices.add(i);
            }
        }

        int[] indices = selectedIndices.stream().mapToInt(i -> i).toArray();
        nodeSetListField.setSelectedIndices(indices);
    }

    private class NodeListCellRenderer
        extends DefaultListCellRenderer {
        private NodeNameHandler nodeNameHandler;
        public NodeListCellRenderer (NodeNameHandler nodeNameHandler) {
            this.nodeNameHandler = nodeNameHandler;
        }

        @Override
        public Component getListCellRendererComponent(JList<? extends Object> list,
                                                      Object nodeObj,
                                                      int index,
                                                      boolean isSelected,
                                                      boolean cellHasFocus) {
            JLabel cell = (JLabel) super.getListCellRendererComponent(list, nodeObj, index, isSelected, cellHasFocus);

            CyNode node = (CyNode) nodeObj;
            cell.setText(nodeNameHandler.getNodeName(node));

            return cell;
        }
    }
}
