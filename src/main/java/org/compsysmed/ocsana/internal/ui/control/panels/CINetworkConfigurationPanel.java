/**
 * Panel containing network configuration for CI stage
 *
 * Copyright Vera-Licona Research Group (C) 2016
 *
 * This software is licensed under the Artistic License 2.0, see the
 * LICENSE file or
 * http://www.opensource.org/licenses/artistic-license-2.0.php for
 * details
 **/

package org.compsysmed.ocsana.internal.ui.control.panels;

// Java imports
import java.util.*;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

// Cytoscape imports
import org.cytoscape.model.CyColumn;
import org.cytoscape.model.CyNetwork;

import javax.swing.BoxLayout;
import javax.swing.JComboBox;
import javax.swing.JLabel;
import javax.swing.JPanel;

import org.cytoscape.work.swing.PanelTaskManager;

// OCSANA imports
import org.compsysmed.ocsana.internal.stages.generation.GenerationContextBuilder;

import org.compsysmed.ocsana.internal.ui.control.widgets.*;

/**
 * Subpanel for user configuration of network parameters in CI stage
 **/
public class CINetworkConfigurationPanel
    extends AbstractControlSubPanel
    implements ActionListener {
    private GenerationContextBuilder generationContextBuilder;
    private PanelTaskManager taskManager;

    // UI elements
    private static final String listMode = "List";
    private static final String stringMode = "String";

    private JPanel modePanel;
    private JComboBox<String> nodeSelectionModeSelecter;

    private JPanel columnPanel;
    private JComboBox<CyColumn> nodeNameColumnSelecter;

    private JPanel nodeSetsPanel;

    private AbstractNodeSetSelecter sourceNodeSelecter;
    private AbstractNodeSetSelecter targetNodeSelecter;
    private AbstractNodeSetSelecter offTargetNodeSelecter;

    /**
     * Constructor
     *
     * @param generationContextBuilder  the context builder for the generation stage
     * @param taskManager  a PanelTaskManager to provide @Tunable panels
     **/
    public CINetworkConfigurationPanel (GenerationContextBuilder generationContextBuilder,
                                        PanelTaskManager taskManager) {
        // Initial setup
        this.generationContextBuilder = generationContextBuilder;
        this.taskManager = taskManager;

        setLayout(new BoxLayout(this, BoxLayout.Y_AXIS));

        // Selection mode selection widgets
        JLabel header = makeHeader("Configure network processing");
        add(header);

        modePanel = new JPanel();
        add(modePanel);

        modePanel.add(new JLabel("Selection mode"));

        String[] modes = {listMode, stringMode};
        nodeSelectionModeSelecter = new JComboBox<>(modes);
        nodeSelectionModeSelecter.addActionListener(this);
        modePanel.add(nodeSelectionModeSelecter);

        columnPanel = new JPanel();
        add(columnPanel);

        columnPanel.add(new JLabel("Node name column"));
        CyColumn[] nodeNameColumns = generationContextBuilder.getNetwork().getDefaultNodeTable().getColumns().stream().toArray(CyColumn[]::new);
        nodeNameColumnSelecter = new JComboBox<>(nodeNameColumns);
        nodeNameColumnSelecter.addActionListener(this);
        columnPanel.add(nodeNameColumnSelecter);

        // Node set selection widgets
        nodeSetsPanel = new JPanel();
        nodeSetsPanel.setLayout(new BoxLayout(nodeSetsPanel, BoxLayout.Y_AXIS));

        add(nodeSetsPanel);

        populateSetsPanelWithListSelecters(nodeSetsPanel);

        // Edge processor
        add(taskManager.getConfiguration(null, generationContextBuilder.getEdgeProcessor()));
    }

    @Override
    public void actionPerformed (ActionEvent e) {
        CyColumn nodeNameColumn = (CyColumn) nodeNameColumnSelecter.getSelectedItem();
        generationContextBuilder.getNodeNameHandler().setNodeNameColumn(nodeNameColumn);

        String mode = (String) nodeSelectionModeSelecter.getSelectedItem();
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
            sourceNodeSelecter = new ListNodeSetSelecter("Source nodes", generationContextBuilder.getNetwork().getNodeList(), generationContextBuilder.getNodeNameHandler());
        } else {
            sourceNodeSelecter = new ListNodeSetSelecter(sourceNodeSelecter, generationContextBuilder.getNodeNameHandler());
        }
        nodeSetsPanel.add(sourceNodeSelecter);

        if (targetNodeSelecter == null) {
            targetNodeSelecter = new ListNodeSetSelecter("Target nodes", generationContextBuilder.getNetwork().getNodeList(), generationContextBuilder.getNodeNameHandler());
        } else {
            targetNodeSelecter = new ListNodeSetSelecter(targetNodeSelecter, generationContextBuilder.getNodeNameHandler());
        }
        nodeSetsPanel.add(targetNodeSelecter);

        if (offTargetNodeSelecter == null) {
            offTargetNodeSelecter = new ListNodeSetSelecter("Off-target nodes", generationContextBuilder.getNetwork().getNodeList(), generationContextBuilder.getNodeNameHandler());
        } else {
            offTargetNodeSelecter = new ListNodeSetSelecter(offTargetNodeSelecter, generationContextBuilder.getNodeNameHandler());
        }
        nodeSetsPanel.add(offTargetNodeSelecter);

        nodeSetsPanel.revalidate();
        nodeSetsPanel.repaint();
    }

    private void populateSetsPanelWithStringSelecters (JPanel nodeSetsPanel) {
        nodeSetsPanel.removeAll();

        if (sourceNodeSelecter == null) {
            sourceNodeSelecter = new StringNodeSetSelecter("Source nodes", generationContextBuilder.getNetwork().getNodeList(), generationContextBuilder.getNodeNameHandler());
        } else {
            sourceNodeSelecter = new StringNodeSetSelecter(sourceNodeSelecter, generationContextBuilder.getNodeNameHandler());
        }
        nodeSetsPanel.add(sourceNodeSelecter);

        if (targetNodeSelecter == null) {
            targetNodeSelecter = new StringNodeSetSelecter("Target nodes", generationContextBuilder.getNetwork().getNodeList(), generationContextBuilder.getNodeNameHandler());
        } else {
            targetNodeSelecter = new StringNodeSetSelecter(targetNodeSelecter, generationContextBuilder.getNodeNameHandler());
        }
        nodeSetsPanel.add(targetNodeSelecter);

        if (offTargetNodeSelecter == null) {
            offTargetNodeSelecter = new StringNodeSetSelecter("Off-target nodes", generationContextBuilder.getNetwork().getNodeList(), generationContextBuilder.getNodeNameHandler());
        } else {
            offTargetNodeSelecter = new StringNodeSetSelecter(offTargetNodeSelecter, generationContextBuilder.getNodeNameHandler());
        }
        nodeSetsPanel.add(offTargetNodeSelecter);

        nodeSetsPanel.revalidate();
        nodeSetsPanel.repaint();
    }

    @Override
    public void updateContextBuilder () {
        generationContextBuilder.setSourceNodes(sourceNodeSelecter.getSelectedNodes());
        generationContextBuilder.setTargetNodes(targetNodeSelecter.getSelectedNodes());
        generationContextBuilder.setOffTargetNodes(offTargetNodeSelecter.getSelectedNodes());
    }
}
