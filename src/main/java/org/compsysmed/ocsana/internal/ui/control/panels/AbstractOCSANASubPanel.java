/**
 * Abstract base class for OCSANA control subpanels
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

import javax.swing.JLabel;
import javax.swing.JPanel;


abstract public class AbstractOCSANASubPanel
    extends JPanel {
    /**
     * Update the CIStageContext members with the settings in this subpanel
     **/
    abstract public void updateContext ();

    /**
     * Generate an appropriately-formatted JLabel for a subpanel header
     *
     * @param label  the label text for the header
     * @return a JLabel object
     **/
    protected static JLabel makeHeader (String label) {
        JLabel header = new JLabel(String.format("<html><h3>%s</h3></html>", label));
        return header;
    }
}
