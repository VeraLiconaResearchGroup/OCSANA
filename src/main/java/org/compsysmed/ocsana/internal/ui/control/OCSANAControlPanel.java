/**
 * Panel to contain OCSANA controls
 *
 * Copyright Vera-Licona Research Group (C) 2015
 *
 * This software is licensed under the Artistic License 2.0, see the
 * LICENSE file or
 * http://www.opensource.org/licenses/artistic-license-2.0.php for
 * details
 **/

package org.compsysmed.ocsana.internal.ui.control;

// Java imports
import java.util.*;

import java.awt.Component;

import javax.swing.BoxLayout;
import javax.swing.JPanel;
import javax.swing.Icon;

// Cytoscape imports
import org.cytoscape.model.CyNetwork;

import org.cytoscape.work.swing.PanelTaskManager;

import org.cytoscape.application.CyApplicationManager;
import org.cytoscape.application.events.SetCurrentNetworkEvent;
import org.cytoscape.application.events.SetCurrentNetworkListener;

import org.cytoscape.application.swing.CySwingApplication;
import org.cytoscape.application.swing.CytoPanel;
import org.cytoscape.application.swing.CytoPanelComponent;
import org.cytoscape.application.swing.CytoPanelName;

import org.cytoscape.util.swing.BasicCollapsiblePanel;

// OCSANA imports
import org.compsysmed.ocsana.internal.ui.results.OCSANAResultsPanel;

/**
 * Panel to configure and run OCSANA
 **/
public class OCSANAControlPanel
    extends JPanel
    implements CytoPanelComponent, SetCurrentNetworkListener {
    private CyApplicationManager cyApplicationManager;
    private PanelTaskManager panelTaskManager;
    private CytoPanel cyControlPanel;
    private OCSANAResultsPanel resultsPanel;

    private CIStageControlPanel ciControlPanel;

    public OCSANAControlPanel (CyApplicationManager cyApplicationManager,
                               CySwingApplication cySwingApplication,
                               OCSANAResultsPanel resultsPanel,
                               PanelTaskManager panelTaskManager) {
        super();
        this.resultsPanel = resultsPanel;
        this.panelTaskManager = panelTaskManager;
        this.cyControlPanel = cySwingApplication.getCytoPanel(getCytoPanelName());

        setLayout(new BoxLayout(this, BoxLayout.PAGE_AXIS));
        buildPanel(cyApplicationManager.getCurrentNetwork());
    }

    /**
     * (Re)build the panel in response to the selection of a network
     **/
    @Override
    public void handleEvent (SetCurrentNetworkEvent e) {
        CyNetwork network = e.getNetwork();
        buildPanel(network);
    }

    private void buildPanel (CyNetwork network) {
        removeAll();

        BasicCollapsiblePanel ciCollapsible = new BasicCollapsiblePanel("1: Find CIs");
        ciCollapsible.setCollapsed(false);
        add(ciCollapsible);

        ciControlPanel = new CIStageControlPanel(network, resultsPanel, panelTaskManager);
        ciCollapsible.add(ciControlPanel);

        revalidate();
        repaint();
    }

    // Helper functions to get information about the panel
    /**
     * Get the results panel component
     */
    @Override
    public Component getComponent() {
        return this;
    }

    /**
     * Get the results panel name
     */
    @Override
    public CytoPanelName getCytoPanelName () {
        return CytoPanelName.WEST;
    }

    /**
     * Get the results panel title
     */
    @Override
    public String getTitle() {
        return "OCSANA";
    }

    /**
     * Get the results panel icon
     */
    @Override
    public Icon getIcon() {
        return null;
    }
}
