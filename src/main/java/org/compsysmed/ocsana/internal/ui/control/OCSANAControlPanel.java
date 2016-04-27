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

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.BoxLayout;
import javax.swing.JButton;
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
import org.compsysmed.ocsana.internal.stages.generation.GenerationContext;
import org.compsysmed.ocsana.internal.stages.generation.GenerationResults;

import org.compsysmed.ocsana.internal.ui.results.OCSANAResultsPanel;

/**
 * Panel to configure and run OCSANA
 **/
public class OCSANAControlPanel
    extends JPanel
    implements CytoPanelComponent, SetCurrentNetworkListener, ActionListener {
    private CyApplicationManager cyApplicationManager;
    private PanelTaskManager panelTaskManager;
    private CytoPanel cyControlPanel;
    private OCSANAResultsPanel resultsPanel;

    private BasicCollapsiblePanel ciCollapsible;
    private GenerationStageControlPanel generationControlPanel;

    private BasicCollapsiblePanel prioritizationCollapsible;
    private PrioritizationStageControlPanel prioritizationControlPanel;
    Boolean prioritizationPanelLocked = true;

    public OCSANAControlPanel (CyApplicationManager cyApplicationManager,
                               CySwingApplication cySwingApplication,
                               OCSANAResultsPanel resultsPanel,
                               PanelTaskManager panelTaskManager) {
        super();
        this.resultsPanel = resultsPanel;
        this.panelTaskManager = panelTaskManager;
        this.cyControlPanel = cySwingApplication.getCytoPanel(getCytoPanelName());

        setLayout(new BoxLayout(this, BoxLayout.Y_AXIS));
        buildPanel(cyApplicationManager.getCurrentNetwork());
    }

    /**
     * (Re)build the panel in response to the selection of a network
     **/
    @Override
    public void handleEvent (SetCurrentNetworkEvent e) {
        CyNetwork network = e.getNetwork();
        resultsPanel.reset();
        buildPanel(network);
    }

    private void buildPanel (CyNetwork network) {
        if (network == null) {
            return;
        }

        removeAll();

        ciCollapsible = new BasicCollapsiblePanel("1: Find CIs");
        ciCollapsible.setCollapsed(false);
        add(ciCollapsible);

        generationControlPanel = new GenerationStageControlPanel(network, resultsPanel, panelTaskManager);
        generationControlPanel.addActionListener(this);
        ciCollapsible.add(generationControlPanel);

        prioritizationCollapsible = new BasicCollapsiblePanel("2: Score and compare CIs");
        prioritizationCollapsible.addCollapseListener(new BasicCollapsiblePanel.CollapseListener(){
                @Override
                public void collapsed () {}
                @Override
                public void expanded () {
                    if (prioritizationPanelLocked) {
                        prioritizationCollapsible.setCollapsed(true);
                    }
                }
            });
        add(prioritizationCollapsible);


        prioritizationControlPanel = new PrioritizationStageControlPanel(network, resultsPanel, panelTaskManager);
        prioritizationControlPanel.addActionListener(this);
        prioritizationCollapsible.add(prioritizationControlPanel);

        revalidate();
        repaint();
    }

    /**
     * Put the prioritization subpanel in "locked" state so user cannot access it
     **/
    private void lockPrioritizationCollapsible () {
        prioritizationPanelLocked = true;
        prioritizationCollapsible.setCollapsed(true);
    }

    /**
     * Put the prioritization subpanel in "unlocked" state so user can access it
     **/
    private void unlockPrioritizationCollapsible () {
        prioritizationPanelLocked = false;
        prioritizationCollapsible.setCollapsed(false);
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

    /**
     * Respond to an event
     **/
    @Override
    public void actionPerformed (ActionEvent event) {
        switch (event.getActionCommand()) {
        case GenerationStageControlPanel.START_CI_SIGNAL:
            lockPrioritizationCollapsible();
            break;

        case GenerationStageControlPanel.END_CI_SIGNAL:
            GenerationContext generationContext = generationControlPanel.getContext();
            GenerationResults generationResults = generationControlPanel.getResults();
            prioritizationControlPanel.populatePanel(generationContext, generationResults);
            unlockPrioritizationCollapsible();
            break;

        case PrioritizationStageControlPanel.END_SIGN_ASSIGNMENT_SIGNAL:
            // Currently, do nothing
            break;

        default:
            throw new IllegalStateException(String.format("Unknown event %s heard by OCSANA control panel", event.paramString()));
        }
    }
}
