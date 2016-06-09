/**
 * Dialog to contain drugability details for a particular node intervention
 *
 * Copyright Vera-Licona Research Group (C) 2016
 *
 * This software is licensed under the Artistic License 2.0, see the
 * LICENSE file or
 * http://www.opensource.org/licenses/artistic-license-2.0.php for
 * details
 **/

package org.compsysmed.ocsana.internal.ui.results.subpanels;

// Java imports
import java.io.*;

import java.util.*;
import java.awt.Desktop;
import java.net.URISyntaxException;
import java.io.IOException;

import javax.swing.BoxLayout;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JTextPane;

import javax.swing.event.HyperlinkEvent;
import javax.swing.event.HyperlinkListener;

// Templating engine imports
import com.mitchellbosecke.pebble.PebbleEngine;
import com.mitchellbosecke.pebble.error.PebbleException;
import com.mitchellbosecke.pebble.template.PebbleTemplate;

import org.compsysmed.ocsana.internal.util.drugability.*;
import org.compsysmed.ocsana.internal.util.results.SignedInterventionNode;
import org.compsysmed.ocsana.internal.util.science.*;

/**
 * Panel presenting drugability details for a particular node intervention
 * <p>
 * NOTE: this panel does <em>not</em> display information about any
 * the underlying CombinationOfInterventions. Use InterventionDisplayPanel for
 * that.
 **/
public class DrugabilityReportSubpanel
    extends JPanel {
    private JTextPane textPane;
    private PebbleTemplate compiledTemplate;

    /**
     * Constructor
     **/
    public DrugabilityReportSubpanel () {
        // Set up dialog
        setLayout(new BoxLayout(this, BoxLayout.Y_AXIS));

        textPane = new JTextPane();
        textPane.setContentType("text/html");
        textPane.setEditable(false);
        textPane.addHyperlinkListener(new HyperlinkListener() {
                public void hyperlinkUpdate(HyperlinkEvent e) {
                    if(e.getEventType() == HyperlinkEvent.EventType.ACTIVATED) {
                        if(Desktop.isDesktopSupported()) {
                            try {
                                Desktop.getDesktop().browse(e.getURL().toURI());
                            } catch (URISyntaxException|IOException ex) {
                                throw new IllegalStateException(String.format("Handling HyperlinkEvent %s resulted in an error: %s", e, ex));
                            } catch (UnsupportedOperationException ex) {
                                // This platform doesn't support
                                // launching the browser. Oh well.
                                String errorMessage = String.format("<html>This platform does not support launching a browser from Java.<br />The URL you clicked was <a href='%1$s'>%1$s</a></html>.", e.getURL().toString());
                                JOptionPane.showMessageDialog(null, errorMessage, "Browser not supported", JOptionPane.ERROR_MESSAGE);
                            }
                        }
                    }
                }
            });
        add(textPane);

        // Compile template
        PebbleEngine engine = new PebbleEngine.Builder().strictVariables(true).build();
        try {
            compiledTemplate = engine.getTemplate("templates/DrugabilityReport.html");
        } catch (PebbleException e) {
            throw new IllegalStateException("Could not load drugability report template. Please report the following error to the plugin author: " + e.getMessage());
        }
    }

    /**
     * Display the report for a particular protein with a particular
     * intervention sign
     *
     * @param signedNode  the SignedInterventionNode for the intervention
     * @param bundle  the DrugabilityDataBundle for the protein
     **/
    public void showReport (SignedInterventionNode signedNode,
                            DrugabilityDataBundle bundle) {
        // Set up data
        Map<String, Object> data = new HashMap<>();

        data.put("node", signedNode);
        data.put("bundle", bundle);

        Writer writer = new StringWriter();
        try {
            compiledTemplate.evaluate(writer, data);
        } catch (PebbleException|IOException e) {
            throw new IllegalStateException("Could not write drugability report. Please report the following error to the plugin author: " + e.getMessage());
        }

        textPane.setText(writer.toString());
    }

}
