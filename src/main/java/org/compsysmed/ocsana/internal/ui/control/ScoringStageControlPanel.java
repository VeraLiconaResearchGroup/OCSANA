/**
 * Panel to contain OCSANA controls for scoring stage
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

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.BoxLayout;
import javax.swing.JButton;
import javax.swing.JPanel;

// Cytoscape imports
import org.cytoscape.model.CyNetwork;
import org.cytoscape.model.CyNode;

import org.cytoscape.work.FinishStatus;
import org.cytoscape.work.ObservableTask;
import org.cytoscape.work.TaskObserver;

import org.cytoscape.work.swing.PanelTaskManager;

// OCSANA imports
import org.compsysmed.ocsana.internal.stages.cistage.CIStageContext;
import org.compsysmed.ocsana.internal.stages.cistage.CIStageResults;

import org.compsysmed.ocsana.internal.stages.scorestage.ScoringStageContext;

import org.compsysmed.ocsana.internal.tasks.scoring.CIScoringTaskFactory;

import org.compsysmed.ocsana.internal.ui.results.OCSANAResultsPanel;

/**
 * Panel to configure and run OCSANA scoring stage
 **/
public class ScoringStageControlPanel
    extends JPanel
    implements ActionListener, TaskObserver {
    private OCSANAResultsPanel resultsPanel;
    private PanelTaskManager panelTaskManager;

    private List<ActionListener> listeners;

    private ScoringStageContext scoringContext;

    private CyNetwork network;

    private CIStageContext ciContext;
    private CIStageResults ciResults;

    private JPanel tunablePanel;

    public ScoringStageControlPanel (CyNetwork network,
                                     OCSANAResultsPanel resultsPanel,
                                     PanelTaskManager panelTaskManager) {
        this.network = network;
        this.resultsPanel = resultsPanel;
        this.panelTaskManager = panelTaskManager;

        listeners = new ArrayList<>();

        setLayout(new BoxLayout(this, BoxLayout.PAGE_AXIS));

        tunablePanel = new JPanel();
        add(tunablePanel);

        JButton runScoringStageButton = new JButton("Run scoring stage computations");
        add(runScoringStageButton);

        runScoringStageButton.addActionListener(new ActionListener() {
                @Override
                public void actionPerformed (ActionEvent e) {
                    runScoringTask();
                }
            });

        revalidate();
        repaint();
    }

    /**
     * Populate the panel with data from previous stage
     **/
    public void populatePanel (CIStageContext ciContext,
                               CIStageResults ciResults) {
        this.ciContext = ciContext;
        this.ciResults = ciResults;

        tunablePanel.removeAll();

        scoringContext = new ScoringStageContext(network, ciContext, ciResults);
        JPanel setupPanel = panelTaskManager.getConfiguration(null, scoringContext);
        if (setupPanel == null) {
            throw new IllegalStateException("WTF");
        }
        tunablePanel.add(setupPanel);

        tunablePanel.revalidate();
        tunablePanel.repaint();
    }

    /**
     * Spawn the scoring stage task
     **/
    private void runScoringTask () {
        panelTaskManager.validateAndApplyTunables(scoringContext);

        for (Set<CyNode> MHS: ciResults.MHSes) {
            CIScoringTaskFactory scorerTaskFactory
                = new CIScoringTaskFactory(ciContext, MHS, scoringContext.targetsToActivate());
            panelTaskManager.execute(scorerTaskFactory.createTaskIterator());
        }
    }

    @Override
    public void taskFinished (ObservableTask task) {
    }

    @Override
    public void allFinished(FinishStatus finishStatus) {
        // Called after the TaskManager finished up a TaskIterator.
        // Currently, we don't do anything with this information.
    }

    // Helper functions to support listening for component changes
    public void actionPerformed (ActionEvent event) {
        // No listeners defined yet
    }

    // Helper functions to signal changes to listeners
    public void addActionListener (ActionListener listener) {
        listeners.add(listener);
    }

    public void removeActionListener (ActionListener listener) {
        listeners.remove(listener);
    }
}
