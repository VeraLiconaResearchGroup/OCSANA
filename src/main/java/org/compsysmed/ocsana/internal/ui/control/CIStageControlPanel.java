/**
 * Panel to contain OCSANA controls for CI stage
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

import org.cytoscape.work.FinishStatus;
import org.cytoscape.work.ObservableTask;
import org.cytoscape.work.TaskObserver;

import org.cytoscape.work.swing.PanelTaskManager;

// OCSANA imports
import org.compsysmed.ocsana.internal.stages.cistage.CIStageContext;
import org.compsysmed.ocsana.internal.stages.cistage.CIStageResults;
import org.compsysmed.ocsana.internal.stages.cistage.CIStageRunnerTask;
import org.compsysmed.ocsana.internal.stages.cistage.CIStageRunnerTaskFactory;

import org.compsysmed.ocsana.internal.ui.results.OCSANAResultsPanel;

/**
 * Panel to configure and run OCSANA CI stage
 **/
public class CIStageControlPanel
    extends JPanel
    implements ActionListener, TaskObserver {
    public static final String START_CI_SIGNAL = "CI task start";
    public static final String END_CI_SIGNAL = "CI task end";

    private OCSANAResultsPanel resultsPanel;
    private PanelTaskManager panelTaskManager;

    private CIStageContext ciStageContext;
    private CIStageResults ciStageResults;
    private CyNetwork network;

    private List<ActionListener> listeners;

    public CIStageControlPanel (CyNetwork network,
                                OCSANAResultsPanel resultsPanel,
                                PanelTaskManager panelTaskManager) {
        this.resultsPanel = resultsPanel;
        this.panelTaskManager = panelTaskManager;

        setLayout(new BoxLayout(this, BoxLayout.PAGE_AXIS));

        ciStageContext = new CIStageContext(network);
        ciStageContext.addActionListener(this);

        listeners = new ArrayList<>();

        buildPanel();
    }

    public CIStageContext getContext () {
        return ciStageContext;
    }

    public CIStageResults getResults () {
        return ciStageResults;
    }

    /**
     * (Re)construct the panel from its components
     **/
    private void buildPanel () {
        removeAll();

        JPanel tunablePanel = panelTaskManager.getConfiguration(null, ciStageContext);
        add(tunablePanel);

        JButton runCIStageButton = new JButton("Run CI stage computations");
        add(runCIStageButton);

        runCIStageButton.addActionListener(new ActionListener() {
                @Override
                public void actionPerformed (ActionEvent e) {
                    runCITask();
                }
            });

        revalidate();
        repaint();
    }

    /**
     * Spawn the CI stage task
     **/
    private void runCITask () {
        panelTaskManager.validateAndApplyTunables(ciStageContext);
        signalStartOfCITask();
        CIStageRunnerTaskFactory runnerTaskFactory
            = new CIStageRunnerTaskFactory(panelTaskManager, this, ciStageContext, resultsPanel);
        panelTaskManager.execute(runnerTaskFactory.createTaskIterator(), this);
    }

    private void signalStartOfCITask () {
        sendSignal(START_CI_SIGNAL);
    }

    private void signalEndOfCITask () {
        sendSignal(END_CI_SIGNAL);
    }

    private void sendSignal (String signal) {
        ActionEvent signalEvent = new ActionEvent(this, ActionEvent.ACTION_PERFORMED, signal);
            for (ActionListener listener: listeners) {
                listener.actionPerformed(signalEvent);
            }
    }

    @Override
    public void taskFinished (ObservableTask task) {
        ciStageResults = task.getResults(CIStageResults.class);
        signalEndOfCITask();
    }

    @Override
    public void allFinished(FinishStatus finishStatus) {
        // Called after the TaskManager finished up a TaskIterator.
        // Currently, we don't do anything with this information.
    }

    public void addActionListener (ActionListener listener) {
        listeners.add(listener);
    }

    public void removeActionListener (ActionListener listener) {
        listeners.remove(listener);
    }

    // Helper functions to support listening for component changes
    public void actionPerformed (ActionEvent event) {
        // Synchronize any changes made to the @Tunable UI widgets
        panelTaskManager.validateAndApplyTunables(ciStageContext);

        buildPanel();
    }
}
