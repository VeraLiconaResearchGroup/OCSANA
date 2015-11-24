/**
 * Panel to display OCSANA results
 *
 * Copyright Vera-Licona Research Group (C) 2015
 * @author Andrew Gainer-Dewar, Ph.D. <andrew.gainer.dewar@gmail.com>
 *
 * This software is licensed under the Artistic License 2.0, see the
 * LICENSE file or
 * http://www.opensource.org/licenses/artistic-license-2.0.php for
 * details
 **/

package org.compsysmed.ocsana.internal.ui;

// Java imports
import java.util.*;

import java.awt.Component;
import java.awt.BorderLayout;
import java.awt.Dimension;
import java.awt.Toolkit;

import javax.swing.Icon;
import javax.swing.JPanel;

// Cytoscape imports
import org.cytoscape.application.swing.CySwingApplication;
import org.cytoscape.application.swing.CytoPanel;
import org.cytoscape.application.swing.CytoPanelComponent;
import org.cytoscape.application.swing.CytoPanelName;

// OCSANA imports
import org.compsysmed.ocsana.internal.tasks.results.OCSANAResults;

public class OCSANAResultsPanel
    extends JPanel
    implements CytoPanelComponent {
    protected CySwingApplication cySwingApplication;
    protected CytoPanel cyResultsPanel;

    protected CIResultsPanel ciPanel;

    public OCSANAResultsPanel (CySwingApplication cySwingApplication) {
        super();
        this.cySwingApplication = cySwingApplication;
        this.cyResultsPanel = cySwingApplication.getCytoPanel(getCytoPanelName());

        int width = (int)(0.2 * Toolkit.getDefaultToolkit().getScreenSize().getWidth());
        this.setPreferredSize(new Dimension(width, 0));

        ciPanel = new CIResultsPanel();
        this.add(ciPanel, BorderLayout.CENTER);

        revalidate();
    }

    /**
     * Get the results panel component
     */
    public Component getComponent() {
        return this;
    }

    /**
     * Get the results panel name
     */
    public CytoPanelName getCytoPanelName () {
        return CytoPanelName.EAST;
    }

    /**
     * Get the results panel title
     */
    public String getTitle() {
        return "OCSANA";
    }

    /**
     * Get the results panel icon
     */
    public Icon getIcon() {
        return null;
    }

    public void updateResults (OCSANAResults results) {
        removeAll();
        ciPanel = new CIResultsPanel(results);
        this.add(ciPanel, BorderLayout.CENTER);
    }
}
