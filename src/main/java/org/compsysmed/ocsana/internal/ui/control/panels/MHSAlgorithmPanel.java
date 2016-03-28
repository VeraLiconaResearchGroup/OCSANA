/**
 * Panel configuring MHS algorithm in OCSANA CI stage
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
import javax.swing.JCheckBox;
import javax.swing.JComboBox;
import javax.swing.JLabel;
import javax.swing.JPanel;

// Cytoscape imports
import org.cytoscape.work.swing.PanelTaskManager;

// OCSANA imports
import org.compsysmed.ocsana.internal.algorithms.mhs.*;

import org.compsysmed.ocsana.internal.stages.cistage.CIStageContext;

/**
 * Subpanel for user configuration of MHS algorithm
 **/
public class MHSAlgorithmPanel
    extends AbstractOCSANASubPanel
    implements ActionListener {
    private CIStageContext ciStageContext;
    private PanelTaskManager taskManager;

    // UI elements
    private JPanel algSelectionPanel;
    private JComboBox algorithmSelecter;
    private JCheckBox includeEndpointsInCIs;

    private JPanel tunablePanel;

    /**
     * Constructor
     *
     * @param ciStageContext  the context for the CI stage
     * @param taskManager  a PanelTaskManager to provide @Tunable panels
     **/
    public MHSAlgorithmPanel (CIStageContext ciStageContext,
                              PanelTaskManager taskManager) {
        // Initial setup
        this.ciStageContext = ciStageContext;
        this.taskManager = taskManager;

        setLayout(new BoxLayout(this, BoxLayout.PAGE_AXIS));

        add(makeHeader("Configure CI discovery"));

        // Algorithm selecter
        algSelectionPanel = new JPanel();
        algSelectionPanel.setLayout(new BoxLayout(algSelectionPanel, BoxLayout.LINE_AXIS));
        add(algSelectionPanel);

        algSelectionPanel.add(new JLabel("Algorithm:"));

        List<AbstractMHSAlgorithm> algorithms = new ArrayList<>();
        algorithms.add(new MMCSAlgorithm());
        algorithms.add(new RSAlgorithm());
        algorithms.add(new BergeAlgorithm());

        algorithmSelecter = new JComboBox(algorithms.toArray());
        algSelectionPanel.add(algorithmSelecter);
        algorithmSelecter.addActionListener(this);

        // CI configuration
        includeEndpointsInCIs = new JCheckBox("Allow sources and targets in CIs", ciStageContext.includeEndpointsInCIs);
        add(includeEndpointsInCIs);

        // Algorithm configuration panel
        tunablePanel = new JPanel();
        add(tunablePanel);

        updateTunablePanel();
    }

    private void updateTunablePanel () {
        tunablePanel.removeAll();

        tunablePanel.add(taskManager.getConfiguration(null, getAlgorithm()));

        tunablePanel.revalidate();
        tunablePanel.repaint();
    }

    private AbstractMHSAlgorithm getAlgorithm () {
        return (AbstractMHSAlgorithm) algorithmSelecter.getSelectedItem();
    }

    @Override
    public void actionPerformed (ActionEvent e) {
        updateTunablePanel();
    }

    @Override
    public void updateContext () {
        ciStageContext.includeEndpointsInCIs = includeEndpointsInCIs.isSelected();
        ciStageContext.mhsAlg = getAlgorithm();
    }
}
