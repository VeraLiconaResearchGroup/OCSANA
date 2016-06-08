/**
 * Dialog to contain details about a signed CI
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
import java.util.stream.Collectors;

import javax.swing.BoxLayout;
import javax.swing.JPanel;
import javax.swing.JTextPane;

import javax.swing.event.HyperlinkEvent;
import javax.swing.event.HyperlinkListener;

// Templating engine imports
import com.mitchellbosecke.pebble.PebbleEngine;
import com.mitchellbosecke.pebble.error.PebbleException;
import com.mitchellbosecke.pebble.template.PebbleTemplate;

// OCSANA imports
import org.compsysmed.ocsana.internal.ui.results.InterventionDetailsDialog;

import org.compsysmed.ocsana.internal.util.results.*;

/**
 * Panel presenting details of a Signed Intervention
 * <p>
 * NOTE: this panel does <em>not</em> display information about any
 * the underlying CombinationOfInterventions. Use InterventionReportSubpanel for
 * that.
 **/
public class SignedInterventionReportSubpanel
    extends JPanel {
    private InterventionDetailsDialog parentDialog;
    private SignedIntervention signedIntervention;

    private JTextPane textPane;
    private PebbleTemplate compiledTemplate;

    /**
     * Constructor
     *
     * @param parentDialog  the parent dialog of this panel
     **/
    public SignedInterventionReportSubpanel (InterventionDetailsDialog parentDialog) {
        this.parentDialog = parentDialog;
        this.signedIntervention = signedIntervention;

        // Set up dialog
        setLayout(new BoxLayout(this, BoxLayout.Y_AXIS));

        textPane = new JTextPane();
        textPane.setContentType("text/html");
        textPane.setEditable(false);
        add(textPane);

        // Compile template
        PebbleEngine engine = new PebbleEngine.Builder().build();

        try {
            compiledTemplate = engine.getTemplate("templates/SignedInterventionReport.html");
        } catch (PebbleException e) {
            throw new IllegalStateException("Could not load signed intervention report template. Please report the following error to the plugin author: " + e.getMessage());
        }
    }

    public void updateIntervention (SignedIntervention signedIntervention) {
        // Set up data
        Map<String, Object> data = new HashMap<>();
        Map<Long, SignedInterventionNode> nodeBySUID = new HashMap<>();

        List<SignedInterventionNode> nodesToActivate = new ArrayList<>();
        List<SignedInterventionNode> nodesToInhibit = new ArrayList<>();

        for (SignedInterventionNode node: signedIntervention.getSignedInterventionNodes()) {
            switch (node.getSign()) {
            case POSITIVE:
                nodesToActivate.add(node);
                break;

            case NEGATIVE:
                nodesToInhibit.add(node);
                break;

            default:
                throw new IllegalStateException("Unknown intervention sign " + node.getSign());
            }

            nodeBySUID.put(node.getSUID(), node);
        }

        data.put("nodesToActivate", nodesToActivate);
        data.put("nodesToInhibit", nodesToInhibit);

        List<ScoredTargetNode> targets = signedIntervention.getTargetNodes().stream().map(target -> new ScoredTargetNode(target, signedIntervention.effectOnTarget(target), signedIntervention.nodeName(target))).collect(Collectors.toList());
        data.put("targets", targets);

        // Build template

        Writer writer = new StringWriter();

        try {
            compiledTemplate.evaluate(writer, data);
        } catch (PebbleException|IOException e) {
            throw new IllegalStateException("Could not write signed intervention report. Please report the following error to the plugin author: " + e.getMessage());
        }

        textPane.setText(writer.toString());

        // Listen to hyperlink clicks
        textPane.addHyperlinkListener(new HyperlinkListener() {
                @Override
                public void hyperlinkUpdate (HyperlinkEvent e) {
                    if (HyperlinkEvent.EventType.ACTIVATED.equals(e.getEventType())) {
                        Map<String, String> queryMap = getQueryMap(e.getURL().getQuery());
                        Long suid = Long.valueOf(queryMap.get("suid"));
                        SignedInterventionNode node = nodeBySUID.get(suid);
                        parentDialog.processNodeClick(node);
                    }
                }
            });
    }

    // Derived from http://stackoverflow.com/a/11733697
    private static Map<String, String> getQueryMap(String query)
        {
            String[] params = query.split("&");
            Map<String, String> map = new HashMap<String, String>();
            for (String param : params)
            {
                String name = param.split("=")[0];
                String value = param.split("=")[1];
                map.put(name, value);
            }
            return map;
        }
}
