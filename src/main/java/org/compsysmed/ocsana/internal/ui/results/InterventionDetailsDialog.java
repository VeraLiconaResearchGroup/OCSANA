/**
 * Dialog to contain details about a CI
 *
 * Copyright Vera-Licona Research Group (C) 2016
 *
 * This software is licensed under the Artistic License 2.0, see the
 * LICENSE file or
 * http://www.opensource.org/licenses/artistic-license-2.0.php for
 * details
 **/

package org.compsysmed.ocsana.internal.ui.results;

// Java imports
import java.util.*;

import java.awt.BorderLayout;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.DefaultComboBoxModel;
import javax.swing.JComboBox;
import javax.swing.JDialog;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JScrollPane;

// Cytoscape imports

// OCSANA improts
import org.compsysmed.ocsana.internal.ui.OCSANADialog;

import org.compsysmed.ocsana.internal.util.results.CombinationOfInterventions;
import org.compsysmed.ocsana.internal.util.results.SignedIntervention;

/**
 * Dialog presenting details of a given CombinationOfInterventions
 **/
public class InterventionDetailsDialog
    extends OCSANADialog {
    private CombinationOfInterventions ci;

    /**
     * Constructor
     * <p>
     * NOTE: the dialog will be shown immediately on construction!
     *
     * @param parentFrame the parent JFrame of this dialog (used for
     * positioning)
     * @param ci  the CombinationOfInterventions
    **/
    public InterventionDetailsDialog (JFrame parentFrame,
                                      CombinationOfInterventions ci) {
        super(parentFrame, "Intervention details report");
        this.ci = ci;

        InterventionDisplayPanel displayPanel = new InterventionDisplayPanel();
        add(displayPanel, BorderLayout.CENTER);

        if (ci.getOptimalSignings().size() >= 1) {
            InterventionSelectionPanel selectionPanel = new InterventionSelectionPanel(ci, displayPanel);
            add(selectionPanel, BorderLayout.LINE_END);
        } else {
            displayPanel.showIntervention(ci.getOptimalSignings().stream().findFirst().get());
        }

        pack();
    }

    private static class InterventionSelectionPanel
        extends JPanel {
        private InterventionDisplayPanel displayPanel;

        public InterventionSelectionPanel (CombinationOfInterventions ci,
                                           InterventionDisplayPanel displayPanel) {
            this.displayPanel = displayPanel;

            DefaultComboBoxModel<SignedIntervention> signedInterventionListModel = new DefaultComboBoxModel<>();
            for (SignedIntervention si: ci.getOptimalSignings()) {
                signedInterventionListModel.addElement(si);
            }

            JComboBox<SignedIntervention> signedInterventionSelecter = new JComboBox<>(signedInterventionListModel);
            signedInterventionSelecter.addActionListener(new ActionListener(){
                    @Override
                    public void actionPerformed (ActionEvent event) {
                        JComboBox source = (JComboBox) event.getSource();
                        SignedIntervention intervention = (SignedIntervention) source.getSelectedItem();
                        displayIntervention(intervention);
                    }
                });
            add(signedInterventionSelecter);

            displayIntervention((SignedIntervention) signedInterventionSelecter.getSelectedItem());

            revalidate();
            repaint();
        }

        private void displayIntervention (SignedIntervention intervention) {
            displayPanel.showIntervention(intervention);
        }
    }

    private static class InterventionDisplayPanel
        extends JPanel {
        public void showIntervention (SignedIntervention intervention) {
            removeAll();

            JLabel testLabel = new JLabel(intervention.getCI().interventionNodesString());
            add(testLabel);

            JLabel activationLabel = new JLabel("Activated nodes: " + intervention.getCI().nodeSetString(intervention.getInterventionNodesToActivate()));
            add(activationLabel);

            revalidate();
            repaint();
        }
    }
}
