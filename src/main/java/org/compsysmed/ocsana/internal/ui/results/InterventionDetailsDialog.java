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

// OCSANA improts
import org.compsysmed.ocsana.internal.ui.OCSANADialog;

import org.compsysmed.ocsana.internal.ui.results.panels.*;

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

        // Set up page skeleton
        setLayout(new BoxLayout(getContentPane(), BoxLayout.Y_AXIS));

        JPanel contentPanel = new JPanel();
        contentPanel.setLayout(new BoxLayout(contentPanel, BoxLayout.X_AXIS));
        add(contentPanel);
        add(getButtonPanel());

        // Add subpanels
        InterventionDisplayPanel ciPanel = new InterventionDisplayPanel(ci);
        contentPanel.add(ciPanel);

        if (ci.getOptimalSignings() != null) {
            SignedIntervention signedIntervention = ci.getOptimalSignings().stream().findFirst().get();
            SignedInterventionDisplayPanel signedInterventionPanel = new SignedInterventionDisplayPanel(signedIntervention);
            contentPanel.add(signedInterventionPanel);
        }

        // Format for presentation
        pack();
    }
}
