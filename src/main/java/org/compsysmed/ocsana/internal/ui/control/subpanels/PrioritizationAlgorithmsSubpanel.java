/**
 * Subpanel configuring CI prioritization algorithms
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
import java.util.*;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.JComboBox;
import javax.swing.JLabel;
import javax.swing.JPanel;

// Cytoscape imports
import org.cytoscape.work.swing.PanelTaskManager;

// OCSANA imports
import org.compsysmed.ocsana.internal.algorithms.drugability.*;
import org.compsysmed.ocsana.internal.algorithms.signassignment.*;

import org.compsysmed.ocsana.internal.ui.control.OCSANAControlPanel;

import org.compsysmed.ocsana.internal.util.context.ContextBundleBuilder;

/**
 * Subpanel for user configuration of CI prioritization algorithms
 **/
public class PrioritizationAlgorithmsSubpanel
    extends AbstractControlSubpanel
    implements ActionListener {
    private ContextBundleBuilder contextBundleBuilder;
    private PanelTaskManager taskManager;

    // UI elements
    private JPanel signAssignmentAlgorithmPanel;
    private JComboBox<AbstractCISignAssignmentAlgorithm> signAssignmentAlgorithmSelecter;
    private JPanel signAssignmentTunablesPanel;

    private JPanel signedInterventionScoringAlgorithmPanel;
    private JComboBox<AbstractSignedInterventionScoringAlgorithm> signedInterventionScoringAlgorithmSelecter;
    private JPanel signedInterventionScoringTunablesPanel;

    /**
     * Constructor
     *
     * @param controlPanel  the parent control panel
     * @param contextBundleBuilder  the context bundle builder
     * @param taskManager  a PanelTaskManager to provide @Tunable panels
     **/
    public PrioritizationAlgorithmsSubpanel (OCSANAControlPanel controlPanel,
                                             ContextBundleBuilder contextBundleBuilder,
                                             PanelTaskManager taskManager) {
        super(controlPanel);

        // Initial setup
        this.contextBundleBuilder = contextBundleBuilder;
        this.taskManager = taskManager;

        setStandardLayout(this);

        add(makeHeader("Configure prioritization algorithms"));

        // Sign assignment algorithms
        signAssignmentAlgorithmPanel = new JPanel();
        setStandardLayout(signAssignmentAlgorithmPanel);
        add(signAssignmentAlgorithmPanel);

        signAssignmentAlgorithmPanel.add(new JLabel("Sign assignent algorithm:"));

        List<AbstractCISignAssignmentAlgorithm> signAssignmentAlgorithms = new ArrayList<>();
        ExhaustiveSearchCISignAssignmentAlgorithm exhaustiveAlgorithm = new ExhaustiveSearchCISignAssignmentAlgorithm();
        contextBundleBuilder.getOCSANAAlgorithm().addListener(exhaustiveAlgorithm);
        signAssignmentAlgorithms.add(exhaustiveAlgorithm);

        signAssignmentAlgorithmSelecter = new JComboBox<>(signAssignmentAlgorithms.toArray(new AbstractCISignAssignmentAlgorithm[signAssignmentAlgorithms.size()]));
        signAssignmentAlgorithmSelecter.addActionListener(this);
        signAssignmentAlgorithmPanel.add(signAssignmentAlgorithmSelecter);
        add(signAssignmentAlgorithmPanel);

        signAssignmentTunablesPanel = new JPanel();
        setStandardLayout(signAssignmentTunablesPanel);
        add(signAssignmentTunablesPanel);
        updateSignAssignmentTunablesPanel();

        // Signed intervention scoring algorithms
        signedInterventionScoringAlgorithmPanel = new JPanel();
        setStandardLayout(signedInterventionScoringAlgorithmPanel);
        add(signedInterventionScoringAlgorithmPanel);

        signedInterventionScoringAlgorithmPanel.add(new JLabel("Sign assignent algorithm:"));

        List<AbstractSignedInterventionScoringAlgorithm> signedInterventionScoringAlgorithms = new ArrayList<>();
        signedInterventionScoringAlgorithms.add(new SimpleSignedInterventionScoringAlgorithm());

        signedInterventionScoringAlgorithmSelecter = new JComboBox<>(signedInterventionScoringAlgorithms.toArray(new AbstractSignedInterventionScoringAlgorithm[signedInterventionScoringAlgorithms.size()]));
        signedInterventionScoringAlgorithmSelecter.addActionListener(this);
        signedInterventionScoringAlgorithmPanel.add(signedInterventionScoringAlgorithmSelecter);
        add(signedInterventionScoringAlgorithmPanel);

        signedInterventionScoringTunablesPanel = new JPanel();
        setStandardLayout(signedInterventionScoringTunablesPanel);
        add(signedInterventionScoringTunablesPanel);
        updateSignedInterventionScoringTunablesPanel();
    }

    private void updateSignAssignmentTunablesPanel () {
        signAssignmentTunablesPanel.removeAll();

        JPanel content = taskManager.getConfiguration(null, getSignAssignmentAlgorithm());
        if (content != null) {
            signAssignmentTunablesPanel.add(content);
        }

        signAssignmentTunablesPanel.revalidate();
        signAssignmentTunablesPanel.repaint();
    }

    private void updateSignedInterventionScoringTunablesPanel () {
        signedInterventionScoringTunablesPanel.removeAll();

        JPanel content = taskManager.getConfiguration(null, getSignedInterventionScoringAlgorithm());
        if (content != null) {
            signedInterventionScoringTunablesPanel.add(content);
        }

        signedInterventionScoringTunablesPanel.revalidate();
        signedInterventionScoringTunablesPanel.repaint();
    }

    private AbstractCISignAssignmentAlgorithm getSignAssignmentAlgorithm () {
        return (AbstractCISignAssignmentAlgorithm) signAssignmentAlgorithmSelecter.getSelectedItem();
    }

    private AbstractSignedInterventionScoringAlgorithm getSignedInterventionScoringAlgorithm () {
        return (AbstractSignedInterventionScoringAlgorithm) signedInterventionScoringAlgorithmSelecter.getSelectedItem();
    }

    @Override
    public void actionPerformed (ActionEvent e) {
        if (e.getSource().equals(signAssignmentAlgorithmSelecter)) {
            updateSignAssignmentTunablesPanel();
        } else if (e.getSource().equals(signedInterventionScoringAlgorithmSelecter)) {
            updateSignedInterventionScoringTunablesPanel();
        } else {
            // Do nothing
        }
    }

    @Override
    public void updateContextBuilder () {
        contextBundleBuilder.setCISignAssignmentAlgorithm(getSignAssignmentAlgorithm());
        contextBundleBuilder.setSIScoringAlgorithm(getSignedInterventionScoringAlgorithm());
    }
}
