/**
 * Widget to let users select a set of nodes with a string
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

import javax.swing.BoxLayout;
import javax.swing.JLabel;
import javax.swing.JTextField;

// Cytoscape imports
import org.cytoscape.model.CyNode;

// OCSANA imports
import org.compsysmed.ocsana.internal.util.tunables.NodeHandler;

public class StringNodeSetSelecter
    extends AbstractNodeSetSelecter {
    private JTextField nodeSetStringField;
    private Set<CyNode> availableNodes;

    public StringNodeSetSelecter (String label,
                                  Set<CyNode> availableNodes,
                                  Set<CyNode> selectedNodes,
                                  NodeHandler nodeHandler) {
        super(label, availableNodes, selectedNodes, nodeHandler);
        draw();
    }

    public StringNodeSetSelecter (String label,
                                  Set<CyNode> availableNodes,
                                  NodeHandler nodeHandler) {
        super(label, availableNodes, nodeHandler);
        draw();
    }

    public StringNodeSetSelecter (AbstractNodeSetSelecter other) {
        super(other);
        draw();
    }

    public StringNodeSetSelecter (AbstractNodeSetSelecter other,
                                  NodeHandler nodeHandler) {
        super(other, nodeHandler);
        draw();
    }

    /**
     * Build the JPanel after the constructors populate the data
     **/
    private void draw () {
        setLayout(new BoxLayout(this, BoxLayout.X_AXIS));

        JLabel title = new JLabel(label);
        add(title);

        add(nodeSetStringField);
    }

    @Override
    protected void handleAvailableNodesUpdate () {
        nodeSetStringField = new JTextField(20);
    }

    @Override
    public Set<CyNode> getSelectedNodes () {
        Set<String> selectedNodeNames = Arrays.asList(nodeSetStringField.getText().trim().split(",")).stream().map(nodeName -> nodeName.trim()).filter(nodeName -> !nodeName.isEmpty()).collect(Collectors.toSet());
        Set<CyNode> selectedNodes = selectedNodeNames.stream().map(nodeName -> nodeHandler.getNodeByName(nodeName)).filter(node -> node != null).collect(Collectors.toSet());
        return selectedNodes;
    }

    @Override
    public void setSelectedNodes (Set<CyNode> selectedNodes) {
        Objects.requireNonNull(selectedNodes, "Set of selected nodes must not be empty");
        if (!getAvailableNodes().containsAll(selectedNodes)) {
            throw new IllegalArgumentException("Selected nodes must be in set of available nodes");
        }

        String nodeSetString = selectedNodes.stream().map(node -> nodeHandler.getNodeName(node)).collect(Collectors.joining(", "));
        nodeSetStringField.setText(nodeSetString);
    }
}
