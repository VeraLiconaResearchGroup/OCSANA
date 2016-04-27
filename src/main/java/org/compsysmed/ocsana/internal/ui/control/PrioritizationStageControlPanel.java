/**
 * Panel to contain OCSANA controls for prioritization stage
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
import org.cytoscape.work.TaskIterator;
import org.cytoscape.work.TaskObserver;

import org.cytoscape.work.swing.PanelTaskManager;

// OCSANA imports
import org.compsysmed.ocsana.internal.stages.generation.GenerationContext;
import org.compsysmed.ocsana.internal.stages.generation.GenerationResults;

import org.compsysmed.ocsana.internal.stages.prioritization.PrioritizationContext;
import org.compsysmed.ocsana.internal.stages.prioritization.PrioritizationContextBuilder;
import org.compsysmed.ocsana.internal.stages.prioritization.PrioritizationResults;
import org.compsysmed.ocsana.internal.stages.prioritization.PrioritizationStageRunnerTask;

import org.compsysmed.ocsana.internal.ui.results.OCSANAResultsPanel;

import org.compsysmed.ocsana.internal.ui.control.panels.*;

import org.compsysmed.ocsana.internal.util.results.CombinationOfInterventions;

/**
 * Panel to configure and run OCSANA prioritization stage
 **/
public class PrioritizationStageControlPanel
    extends JPanel
    implements ActionListener, TaskObserver {
    public static final String END_SIGN_ASSIGNMENT_SIGNAL = "Sign assignment task end";

    private final OCSANAResultsPanel resultsPanel;
    private final PanelTaskManager taskManager;

    private final List<ActionListener> listeners;

    private PrioritizationContextBuilder prioritizationContextBuilder;
    private PrioritizationResults prioritizationResults;

    private CyNetwork network;

    private GenerationContext generationContext;
    private GenerationResults generationResults;

    private final Collection<AbstractControlSubPanel> subpanels;
    private final JPanel optionsPanel;
    private TargetActivationConfigurationPanel targetActivationPanel;

    public PrioritizationStageControlPanel (CyNetwork network,
                                            OCSANAResultsPanel resultsPanel,
                                            PanelTaskManager taskManager) {
        if (network == null) {
            throw new IllegalArgumentException("Network cannot be null");
        }
        this.network = network;

        if (resultsPanel == null) {
            throw new IllegalArgumentException("Results panel cannot be null");
        }
        this.resultsPanel = resultsPanel;

        if (taskManager == null) {
            throw new IllegalArgumentException("Task manager cannot be null");
        }
        this.taskManager = taskManager;

        subpanels = new ArrayList<>();

        listeners = new ArrayList<>();

        setLayout(new BoxLayout(this, BoxLayout.Y_AXIS));

        optionsPanel = new JPanel();
        optionsPanel.setLayout(new BoxLayout(optionsPanel, BoxLayout.Y_AXIS));
        add(optionsPanel);

        JButton runPrioritizationStageButton = new JButton("Run prioritization stage computations");
        add(runPrioritizationStageButton);

        runPrioritizationStageButton.addActionListener(new ActionListener() {
                @Override
                public void actionPerformed (ActionEvent e) {
                    runPrioritizationTasks();
                }
            });

        revalidate();
        repaint();
    }

    /**
     * Populate the panel with data from previous stage
     **/
    public void populatePanel (GenerationContext generationContext,
                               GenerationResults generationResults) {
        if (generationContext == null) {
            throw new IllegalArgumentException("Generation stage context cannot be null");
        }
        this.generationContext = generationContext;

        if (generationResults == null) {
            throw new IllegalArgumentException("Generation stage results cannot be null");
        }
        this.generationResults = generationResults;

        optionsPanel.removeAll();
        subpanels.clear();

        prioritizationContextBuilder = new PrioritizationContextBuilder(network, generationContext, generationResults);

        targetActivationPanel = new TargetActivationConfigurationPanel(generationContext, prioritizationContextBuilder, taskManager);
        optionsPanel.add(targetActivationPanel);
        subpanels.add(targetActivationPanel);

        optionsPanel.revalidate();
        optionsPanel.repaint();
    }

    /**
     * Update the PrioritizationStageContext with the settings in the UI
     **/
    private void updateContextBuilder () {
        for (AbstractControlSubPanel subpanel: subpanels) {
            subpanel.updateContextBuilder();
        }
    }

    /**
     * Spawn the prioritization stage task
     **/
    private void runPrioritizationTasks () {
        updateContextBuilder();

        PrioritizationContext prioritizationContext = prioritizationContextBuilder.getContext();
        prioritizationResults = new PrioritizationResults(prioritizationContext);

        TaskIterator prioritizationTasks = new TaskIterator();
        prioritizationTasks.append(new PrioritizationStageRunnerTask(taskManager, this, generationContext, generationResults, prioritizationContext, prioritizationResults, resultsPanel));

        taskManager.execute(prioritizationTasks, this);
    }

    private void signalEndOfSignAssignmentTask () {
        sendSignal(END_SIGN_ASSIGNMENT_SIGNAL);
    }

    private void sendSignal (String signal) {
        ActionEvent signalEvent = new ActionEvent(this, ActionEvent.ACTION_PERFORMED, signal);
        for (ActionListener listener: listeners) {
            listener.actionPerformed(signalEvent);
        }
    }

    @Override
    public void taskFinished (ObservableTask task) {
        // Called after the TaskManager finishes each of its Tasks.
        // Currently, we don't do anything with this information.
    }

    @Override
    public void allFinished(FinishStatus finishStatus) {
        // Called after the TaskManager finished up a TaskIterator.
        signalEndOfSignAssignmentTask();
        resultsPanel.updateResults(prioritizationResults);
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
