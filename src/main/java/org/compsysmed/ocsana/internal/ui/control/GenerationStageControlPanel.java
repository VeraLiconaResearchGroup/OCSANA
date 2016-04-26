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
import javax.swing.JLabel;
import javax.swing.JPanel;

// Cytoscape imports
import org.cytoscape.model.CyNetwork;

import org.cytoscape.work.FinishStatus;
import org.cytoscape.work.ObservableTask;
import org.cytoscape.work.TaskObserver;

import org.cytoscape.work.swing.PanelTaskManager;

// OCSANA imports
import org.compsysmed.ocsana.internal.algorithms.mhs.*;

import org.compsysmed.ocsana.internal.stages.generation.GenerationContext;
import org.compsysmed.ocsana.internal.stages.generation.GenerationResults;
import org.compsysmed.ocsana.internal.stages.generation.GenerationStageRunnerTask;
import org.compsysmed.ocsana.internal.stages.generation.GenerationStageRunnerTaskFactory;

import org.compsysmed.ocsana.internal.ui.control.panels.*;
import org.compsysmed.ocsana.internal.ui.results.OCSANAResultsPanel;

/**
 * Panel to configure and run OCSANA CI stage
 **/
public class GenerationStageControlPanel
    extends JPanel
    implements ActionListener, TaskObserver {
    public static final String START_CI_SIGNAL = "CI task start";
    public static final String END_CI_SIGNAL = "CI task end";

    private OCSANAResultsPanel resultsPanel;
    private PanelTaskManager taskManager;

    private GenerationContext ciStageContext;
    private GenerationResults ciStageResults;
    private CyNetwork network;

    private List<ActionListener> listeners;

    // UI elements
    private Collection<AbstractControlSubPanel> subpanels;

    private CINetworkConfigurationPanel networkConfigPanel;
    private PathFindingAlgorithmPanel pathFindingAlgorithmPanel;
    private MHSAlgorithmPanel mhsAlgorithmPanel;

    /**
     * Constructor.
     *
     * @param network  the network
     * @param resultsPanel   panel to update with results
     * @param taskManager  a TaskManager to handle the CI calculation tasks
     **/
    public GenerationStageControlPanel (CyNetwork network,
                                OCSANAResultsPanel resultsPanel,
                                PanelTaskManager taskManager) {
        this.network = network;
        this.resultsPanel = resultsPanel;
        this.taskManager = taskManager;

        setLayout(new BoxLayout(this, BoxLayout.Y_AXIS));

        ciStageContext = new GenerationContext(network);

        subpanels = new ArrayList<>();

        listeners = new ArrayList<>();

        buildPanel();
    }

    /**
     * Retrieve the underlying CIStageContext with the latest changes
     * from the UI
     *
     * @return the context
     **/
    public GenerationContext getContext () {
        updateContext();
        return ciStageContext;
    }

    /**
     * Retrieve the results of the CI stage if available
     *
     * @return the results (null if stage has not been run)
     **/
    public GenerationResults getResults () {
        return ciStageResults;
    }

    /**
     * (Re)construct the panel from its components
     **/
    private void buildPanel () {
        removeAll();

        JPanel tunablePanel = getContextPanel();
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
     * Build a panel with the UI elements for a CIStageContext
     **/
    private JPanel getContextPanel () {
        JPanel panel = new JPanel();
        panel.setLayout(new BoxLayout(panel, BoxLayout.Y_AXIS));

        networkConfigPanel = new CINetworkConfigurationPanel(ciStageContext, taskManager);
        panel.add(networkConfigPanel);
        subpanels.add(networkConfigPanel);

        pathFindingAlgorithmPanel = new PathFindingAlgorithmPanel(ciStageContext, taskManager);
        panel.add(pathFindingAlgorithmPanel);
        subpanels.add(pathFindingAlgorithmPanel);

        mhsAlgorithmPanel = new MHSAlgorithmPanel(ciStageContext, taskManager);
        panel.add(mhsAlgorithmPanel);
        subpanels.add(mhsAlgorithmPanel);

        return panel;
    }

    /**
     * Update the CIStageContext members with the settings in the UI
     **/
    private void updateContext () {
        for (AbstractControlSubPanel subpanel: subpanels) {
            subpanel.updateContext();
        }
    }

    /**
     * Spawn the CI stage task
     **/
    private void runCITask () {
        updateContext();

        signalStartOfCITask();

        GenerationStageRunnerTaskFactory runnerTaskFactory
            = new GenerationStageRunnerTaskFactory(taskManager, this, ciStageContext, resultsPanel);
        taskManager.execute(runnerTaskFactory.createTaskIterator(), this);
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
        ciStageResults = task.getResults(GenerationResults.class);
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
        updateContext();

        buildPanel();
    }
}
