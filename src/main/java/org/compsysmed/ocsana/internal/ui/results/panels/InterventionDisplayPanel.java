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

package org.compsysmed.ocsana.internal.ui.results.panels;

// Java imports
import java.util.*;

import javax.swing.BoxLayout;
import javax.swing.JPanel;
import javax.swing.JTextPane;

// Cytoscape imports
import org.cytoscape.model.CyNode;

// OCSANA imports
import org.compsysmed.ocsana.internal.util.results.*;

/**
 * Panel presenting details of a CombinationOfInterventions
 * <p>
 * NOTE: this panel does <em>not</em> display information about any
 * SignedInterventions. Use SignedInterventionDisplayPanel for that.
 **/
public class InterventionDisplayPanel
    extends JPanel {
    private CombinationOfInterventions ci;

    /**
     * Constructor
     *
     * @param ci  the CombinationOfInterventions
     **/
    public InterventionDisplayPanel (CombinationOfInterventions ci) {
        this.ci = ci;

        JTextPane textPane = new JTextPane();
        textPane.setContentType("text/html");
        add(textPane);

        StringBuilder report = new StringBuilder();
        report.append("<html>");

        report.append("<h3>Intervention nodes:</h3>");

        report.append("<ul>");

        for (CyNode node: ci.getNodes()) {
            report.append(String.format("<li>%s</li>", ci.nodeName(node)));
        }

        report.append("</ul>");
        report.append("</html>");

        textPane.setText(report.toString());
    }
}
