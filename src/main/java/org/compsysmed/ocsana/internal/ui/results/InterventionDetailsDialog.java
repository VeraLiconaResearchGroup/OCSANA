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

import javax.swing.BoxLayout;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;

// Cytoscape imports
import org.cytoscape.model.CyNetwork;
import org.cytoscape.model.CyNode;

// OCSANA improts
import org.compsysmed.ocsana.internal.ui.OCSANADialog;

import org.compsysmed.ocsana.internal.ui.results.panels.*;

import org.compsysmed.ocsana.internal.util.results.*;

import org.compsysmed.ocsana.internal.util.drugability.*;

import org.compsysmed.ocsana.internal.util.science.*;

/**
 * Dialog presenting details of a given CombinationOfInterventions
 **/
public class InterventionDetailsDialog
    extends OCSANADialog {
    private CombinationOfInterventions ci;
    private CyNetwork network;
    private DrugabilityDataBundleFactory drugabilityDataBundleFactory;

    // UI elements
    private JPanel contentPanel;
    private SignedInterventionReportPanel signedInterventionPanel;
    private DrugabilityReportPanel drugabilityPanel;

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
                                      CyNetwork network,
                                      CombinationOfInterventions ci) {
        super(parentFrame, "Intervention details report");
        this.network = network;
        this.ci = ci;

        drugabilityDataBundleFactory = DrugabilityDataBundleFactory.getFactory();


        // Set up page skeleton
        setLayout(new BoxLayout(getContentPane(), BoxLayout.Y_AXIS));

        contentPanel = new JPanel();
        add(contentPanel);
        add(getButtonPanel());

        signedInterventionPanel = new SignedInterventionReportPanel(this);
        contentPanel.add(signedInterventionPanel);

        if (ci.getOptimalSignings() != null && !ci.getOptimalSignings().isEmpty()) {
            SignedIntervention signedIntervention = ci.getOptimalSignings().stream().findFirst().get();
            signedInterventionPanel.updateIntervention(signedIntervention);
        }

        // Format for presentation
        pack();
        setLocationRelativeTo(getOwner());
    }

    /**
     * Respond to a node click event in the signed intervention dialog
     * panel
     **/
    public void processNodeClick (SignedInterventionNode signedNode) {
        if (drugabilityPanel == null) {
            drugabilityPanel = new DrugabilityReportPanel();
            contentPanel.add(drugabilityPanel);
        }

        DrugabilityDataBundle bundle = drugabilityDataBundleFactory.getBundle(signedNode);
        drugabilityPanel.showReport(signedNode.getName(), bundle, signedNode.getSign());

        pack();
        setLocationRelativeTo(getOwner());
    }
}
