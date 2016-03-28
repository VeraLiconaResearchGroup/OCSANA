/**
 * Panel containing node set selecters for CI stage
 *
 * Copyright Vera-Licona Research Group (C) 2015
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

// OCSANA imports
import org.compsysmed.ocsana.internal.stages.cistage.CIStageContext;

import org.compsysmed.ocsana.internal.ui.control.widgets.*;

/**
 * Subpanel for user selection of node sets (sources, targets, and off-targets)
 **/
public class CINodeSetsPanel
    extends AbstractOCSANASubPanel
    implements ActionListener {
    private CIStageContext ciStageContext;

    // UI elements
    private static final String listMode = "List";
    private static final String stringMode = "String";

    private JPanel modePanel;
    private JComboBox nodeSelectionModeSelecter;

    private JPanel columnPanel;
    private JComboBox nodeNameColumnSelecter;

    private JPanel nodeSetsPanel;

    private AbstractNodeSetSelecter sourceNodeSelecter;
    private AbstractNodeSetSelecter targetNodeSelecter;
    private AbstractNodeSetSelecter offTargetNodeSelecter;

    /**
     * Constructor
     *
     * @param ciStageContext  the context for the CI stage
     **/
    public CINodeSetsPanel (CIStageContext ciStageContext) {
        // Initial setup
        this.ciStageContext = ciStageContext;
        setLayout(new BoxLayout(this, BoxLayout.PAGE_AXIS));

        // Selection mode selection widgets
        add(makeHeader("Select node sets"));

        modePanel = new JPanel();
        modePanel.setLayout(new BoxLayout(modePanel, BoxLayout.LINE_AXIS));
        add(modePanel);

        modePanel.add(new JLabel("Selection mode"));

        String[] modes = {listMode, stringMode};
        nodeSelectionModeSelecter = new JComboBox(modes);
        nodeSelectionModeSelecter.addActionListener(this);
        modePanel.add(nodeSelectionModeSelecter);

        columnPanel = new JPanel();
        columnPanel.setLayout(new BoxLayout(columnPanel, BoxLayout.LINE_AXIS));
        add(columnPanel);

        columnPanel.add(new JLabel("Node name column"));
        CyColumn[] nodeNameColumns = ciStageContext.getNetwork().getDefaultNodeTable().getColumns().stream().toArray(CyColumn[]::new);
        nodeNameColumnSelecter = new JComboBox(nodeNameColumns);
        nodeNameColumnSelecter.addActionListener(this);
        columnPanel.add(nodeNameColumnSelecter);

        // Node set selection widgets
        nodeSetsPanel = new JPanel();
        nodeSetsPanel.setLayout(new BoxLayout(nodeSetsPanel, BoxLayout.PAGE_AXIS));
        add(nodeSetsPanel);

        populateSetsPanelWithListSelecters(nodeSetsPanel);
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
