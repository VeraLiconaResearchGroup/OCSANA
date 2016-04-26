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
import org.compsysmed.ocsana.internal.stages.generation.GenerationContext;

import org.compsysmed.ocsana.internal.ui.control.widgets.*;

/**
 * Subpanel for user configuration of network parameters in CI stage
 **/
public class CINetworkConfigurationPanel
    extends AbstractControlSubPanel
    implements ActionListener {
    private GenerationContext ciStageContext;
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
     * @param ciStageContext  the context for the CI stage
     * @param taskManager  a PanelTaskManager to provide @Tunable panels
     **/
    public CINetworkConfigurationPanel (GenerationContext ciStageContext,
                                        PanelTaskManager taskManager) {
        // Initial setup
        this.ciStageContext = ciStageContext;
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
        CyColumn[] nodeNameColumns = ciStageContext.getNetwork().getDefaultNodeTable().getColumns().stream().toArray(CyColumn[]::new);
        nodeNameColumnSelecter = new JComboBox<>(nodeNameColumns);
        nodeNameColumnSelecter.addActionListener(this);
        columnPanel.add(nodeNameColumnSelecter);

        // Node set selection widgets
        nodeSetsPanel = new JPanel();
        nodeSetsPanel.setLayout(new BoxLayout(nodeSetsPanel, BoxLayout.Y_AXIS));

        add(nodeSetsPanel);

        populateSetsPanelWithListSelecters(nodeSetsPanel);

        // Edge processor
        add(taskManager.getConfiguration(null, ciStageContext.edgeProcessor));
    }

    @Override
    public void actionPerformed (ActionEvent e) {
        CyColumn nodeNameColumn = (CyColumn) nodeNameColumnSelecter.getSelectedItem();
        ciStageContext.nodeNameHandler.setNodeNameColumn(nodeNameColumn);

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
            sourceNodeSelecter = new ListNodeSetSelecter("Source nodes", ciStageContext.getNetwork().getNodeList(), ciStageContext.nodeNameHandler);
        } else {
            sourceNodeSelecter = new ListNodeSetSelecter(sourceNodeSelecter, ciStageContext.nodeNameHandler);
        }
        nodeSetsPanel.add(sourceNodeSelecter);

        if (targetNodeSelecter == null) {
            targetNodeSelecter = new ListNodeSetSelecter("Target nodes", ciStageContext.getNetwork().getNodeList(), ciStageContext.nodeNameHandler);
        } else {
            targetNodeSelecter = new ListNodeSetSelecter(targetNodeSelecter, ciStageContext.nodeNameHandler);
        }
        nodeSetsPanel.add(targetNodeSelecter);

        if (offTargetNodeSelecter == null) {
            offTargetNodeSelecter = new ListNodeSetSelecter("Off-target nodes", ciStageContext.getNetwork().getNodeList(), ciStageContext.nodeNameHandler);
        } else {
            offTargetNodeSelecter = new ListNodeSetSelecter(offTargetNodeSelecter, ciStageContext.nodeNameHandler);
        }
        nodeSetsPanel.add(offTargetNodeSelecter);

        nodeSetsPanel.revalidate();
        nodeSetsPanel.repaint();
    }

    private void populateSetsPanelWithStringSelecters (JPanel nodeSetsPanel) {
        nodeSetsPanel.removeAll();

        if (sourceNodeSelecter == null) {
            sourceNodeSelecter = new StringNodeSetSelecter("Source nodes", ciStageContext.getNetwork().getNodeList(), ciStageContext.nodeNameHandler);
        } else {
            sourceNodeSelecter = new StringNodeSetSelecter(sourceNodeSelecter, ciStageContext.nodeNameHandler);
        }
        nodeSetsPanel.add(sourceNodeSelecter);

        if (targetNodeSelecter == null) {
            targetNodeSelecter = new StringNodeSetSelecter("Target nodes", ciStageContext.getNetwork().getNodeList(), ciStageContext.nodeNameHandler);
        } else {
            targetNodeSelecter = new StringNodeSetSelecter(targetNodeSelecter, ciStageContext.nodeNameHandler);
        }
        nodeSetsPanel.add(targetNodeSelecter);

        if (offTargetNodeSelecter == null) {
            offTargetNodeSelecter = new StringNodeSetSelecter("Off-target nodes", ciStageContext.getNetwork().getNodeList(), ciStageContext.nodeNameHandler);
        } else {
            offTargetNodeSelecter = new StringNodeSetSelecter(offTargetNodeSelecter, ciStageContext.nodeNameHandler);
        }
        nodeSetsPanel.add(offTargetNodeSelecter);

        nodeSetsPanel.revalidate();
        nodeSetsPanel.repaint();
    }

    @Override
    public void updateContext () {
        ciStageContext.sourceNodes = sourceNodeSelecter.getSelectedNodes();
        ciStageContext.targetNodes = targetNodeSelecter.getSelectedNodes();
        ciStageContext.offTargetNodes = offTargetNodeSelecter.getSelectedNodes();
    }
}
