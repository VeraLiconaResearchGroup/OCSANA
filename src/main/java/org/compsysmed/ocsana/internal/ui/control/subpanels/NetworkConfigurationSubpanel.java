/**
 * Subpanel containing network configuration for OCSANA
 *
 * Copyright Vera-Licona Research Group (C) 2016
 *
 * This software is licensed under the Artistic License 2.0, see the
 * LICENSE file or
 * http://www.opensource.org/licenses/artistic-license-2.0.php for
 * details
 **/

package org.compsysmed.ocsana.internal.ui.control.subpanels;

// Java imports
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import java.util.*;

// Cytoscape imports
import org.cytoscape.model.CyColumn;
import javax.swing.BoxLayout;
import javax.swing.JComboBox;
import javax.swing.JLabel;
import javax.swing.JPanel;

import org.cytoscape.work.swing.PanelTaskManager;

// OCSANA imports
import org.compsysmed.ocsana.internal.util.context.ContextBundleBuilder;

import org.compsysmed.ocsana.internal.ui.control.OCSANAControlPanel;

import org.compsysmed.ocsana.internal.ui.control.widgets.*;

/**
 * Subpanel for user configuration of network parameters
 **/
public class NetworkConfigurationSubpanel
    extends AbstractControlSubpanel
    implements ActionListener {
    private final ContextBundleBuilder contextBundleBuilder;
    private final PanelTaskManager taskManager;

    // UI elements
    private JPanel modePanel;
    private JComboBox<SelectionMode> nodeSelectionModeSelecter;

    private JPanel columnPanel;
    private JComboBox<CyColumn> nodeNameColumnSelecter;
    private JComboBox<CyColumn> nodeIDColumnSelecter;

    private JPanel nodeSetsPanel;

    private AbstractNodeSetSelecter sourceNodeSelecter;
    private AbstractNodeSetSelecter targetNodeSelecter;
    private AbstractNodeSetSelecter offTargetNodeSelecter;

    /**
     * Constructor
     *
     * @param contextBundleBuilder  the builder for context bundles
     * @param taskManager  a PanelTaskManager to provide @Tunable panels
     **/
    public NetworkConfigurationSubpanel (OCSANAControlPanel controlPanel,
                                         ContextBundleBuilder contextBundleBuilder,
                                         PanelTaskManager taskManager) {
        super(controlPanel);

        // Initial setup
        this.contextBundleBuilder = contextBundleBuilder;
        this.taskManager = taskManager;

        setLayout(new BoxLayout(this, BoxLayout.Y_AXIS));

        // Selection mode selection widgets
        JLabel header = makeHeader("Configure network processing");
        add(header);

        modePanel = new JPanel();
        add(modePanel);

        modePanel.add(new JLabel("Selection mode"));

        SelectionMode[] modes = {SelectionMode.listMode, SelectionMode.stringMode};
        nodeSelectionModeSelecter = new JComboBox<>(modes);
        nodeSelectionModeSelecter.addActionListener(this);
        modePanel.add(nodeSelectionModeSelecter);

        columnPanel = new JPanel();
        columnPanel.setLayout(new BoxLayout(columnPanel, BoxLayout.Y_AXIS));
        add(columnPanel);

        CyColumn[] nodeNameColumns = contextBundleBuilder.getNetwork().getDefaultNodeTable().getColumns().stream().toArray(CyColumn[]::new);
        columnPanel.add(new JLabel("Node name column"));
        nodeNameColumnSelecter = new JComboBox<>(nodeNameColumns);
        nodeNameColumnSelecter.addActionListener(this);
        columnPanel.add(nodeNameColumnSelecter);

        CyColumn[] nodeIDColumns = contextBundleBuilder.getNetwork().getDefaultNodeTable().getColumns().stream().toArray(CyColumn[]::new);
        columnPanel.add(new JLabel("Node biomolecule ID column"));
        nodeIDColumnSelecter = new JComboBox<>(nodeIDColumns);
        nodeIDColumnSelecter.addActionListener(this);
        columnPanel.add(nodeIDColumnSelecter);

        // Node set selection widgets
        nodeSetsPanel = new JPanel();
        nodeSetsPanel.setLayout(new BoxLayout(nodeSetsPanel, BoxLayout.Y_AXIS));

        add(nodeSetsPanel);

        populateSetsPanelWithListSelecters(nodeSetsPanel);

        // Edge processor
        add(taskManager.getConfiguration(null, contextBundleBuilder.getEdgeProcessor()));
    }

    @Override
    public void actionPerformed (ActionEvent e) {
        if (e.getSource().equals(nodeNameColumnSelecter)) {
            CyColumn nodeNameColumn = (CyColumn) nodeNameColumnSelecter.getSelectedItem();
            contextBundleBuilder.getNodeHandler().setNodeNameColumn(nodeNameColumn);
            rebuildNodeSetSelecters();
        } else if (e.getSource().equals(nodeIDColumnSelecter)) {
            CyColumn nodeIDColumn = (CyColumn) nodeIDColumnSelecter.getSelectedItem();
            contextBundleBuilder.getNodeHandler().setNodeIDColumn(nodeIDColumn);
        } else if (e.getSource().equals(nodeSelectionModeSelecter)) {
            rebuildNodeSetSelecters();
        } else {
            throw new IllegalStateException("Unknown source of action event: " + e);
        }
    }

    private void rebuildNodeSetSelecters () {
        SelectionMode mode = (SelectionMode) nodeSelectionModeSelecter.getSelectedItem();
        switch (mode) {
        case listMode:
            populateSetsPanelWithListSelecters(nodeSetsPanel);
            break;

        case stringMode:
            populateSetsPanelWithStringSelecters(nodeSetsPanel);
            break;

        default:
            throw new IllegalStateException("Unknown selection mode selected");
        }
    }


    private void populateSetsPanelWithListSelecters (JPanel nodeSetsPanel) {
        nodeSetsPanel.removeAll();

        if (sourceNodeSelecter == null) {
            sourceNodeSelecter = new ListNodeSetSelecter("Source nodes", new HashSet<>(contextBundleBuilder.getNetwork().getNodeList()), contextBundleBuilder.getNodeHandler());
        } else {
            sourceNodeSelecter = new ListNodeSetSelecter(sourceNodeSelecter, contextBundleBuilder.getNodeHandler());
        }
        nodeSetsPanel.add(sourceNodeSelecter);

        if (targetNodeSelecter == null) {
            targetNodeSelecter = new ListNodeSetSelecter("Target nodes", new HashSet<>(contextBundleBuilder.getNetwork().getNodeList()), contextBundleBuilder.getNodeHandler());
        } else {
            targetNodeSelecter = new ListNodeSetSelecter(targetNodeSelecter, contextBundleBuilder.getNodeHandler());
        }
        nodeSetsPanel.add(targetNodeSelecter);

        if (offTargetNodeSelecter == null) {
            offTargetNodeSelecter = new ListNodeSetSelecter("Off-target nodes", new HashSet<>(contextBundleBuilder.getNetwork().getNodeList()), contextBundleBuilder.getNodeHandler());
        } else {
            offTargetNodeSelecter = new ListNodeSetSelecter(offTargetNodeSelecter, contextBundleBuilder.getNodeHandler());
        }
        nodeSetsPanel.add(offTargetNodeSelecter);

        nodeSetsPanel.revalidate();
        nodeSetsPanel.repaint();
    }

    private void populateSetsPanelWithStringSelecters (JPanel nodeSetsPanel) {
        nodeSetsPanel.removeAll();

        if (sourceNodeSelecter == null) {
            sourceNodeSelecter = new StringNodeSetSelecter("Source nodes", new HashSet<>(contextBundleBuilder.getNetwork().getNodeList()), contextBundleBuilder.getNodeHandler());
        } else {
            sourceNodeSelecter = new StringNodeSetSelecter(sourceNodeSelecter, contextBundleBuilder.getNodeHandler());
        }
        nodeSetsPanel.add(sourceNodeSelecter);

        if (targetNodeSelecter == null) {
            targetNodeSelecter = new StringNodeSetSelecter("Target nodes", new HashSet<>(contextBundleBuilder.getNetwork().getNodeList()), contextBundleBuilder.getNodeHandler());
        } else {
            targetNodeSelecter = new StringNodeSetSelecter(targetNodeSelecter, contextBundleBuilder.getNodeHandler());
        }
        nodeSetsPanel.add(targetNodeSelecter);

        if (offTargetNodeSelecter == null) {
            offTargetNodeSelecter = new StringNodeSetSelecter("Off-target nodes", new HashSet<>(contextBundleBuilder.getNetwork().getNodeList()), contextBundleBuilder.getNodeHandler());
        } else {
            offTargetNodeSelecter = new StringNodeSetSelecter(offTargetNodeSelecter, contextBundleBuilder.getNodeHandler());
        }
        nodeSetsPanel.add(offTargetNodeSelecter);

        nodeSetsPanel.revalidate();
        nodeSetsPanel.repaint();
    }

    @Override
    public void updateContextBuilder () {
        contextBundleBuilder.setSourceNodes(sourceNodeSelecter.getSelectedNodes());
        contextBundleBuilder.setTargetNodes(targetNodeSelecter.getSelectedNodes());
        contextBundleBuilder.setOffTargetNodes(offTargetNodeSelecter.getSelectedNodes());
    }

    private static enum SelectionMode {
        listMode("List"),
        stringMode("String");

        private final String label;
        private SelectionMode (String label) {
            this.label = label;
        }

        public String toString () {
            return label;
        }
    }
}