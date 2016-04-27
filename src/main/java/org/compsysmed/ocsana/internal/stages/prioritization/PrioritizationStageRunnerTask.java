/**
 * Runner for the prioritization stage of OCSANA
 *
 * Copyright Vera-Licona Research Group (C) 2016
 *
 * This software is licensed under the Artistic License 2.0, see the
 * LICENSE file or
 * http://www.opensource.org/licenses/artistic-license-2.0.php for
 * details
 **/

package org.compsysmed.ocsana.internal.stages.prioritization;

// Cytoscape imports
import org.cytoscape.model.CyNetwork;
import org.cytoscape.model.CyEdge;
import org.cytoscape.model.CyNode;

import org.cytoscape.task.AbstractNetworkTask;
import org.cytoscape.work.FinishStatus;
import org.cytoscape.work.ObservableTask;
import org.cytoscape.work.TaskManager;
import org.cytoscape.work.TaskMonitor;
import org.cytoscape.work.TaskObserver;

// OCSANA imports
import org.compsysmed.ocsana.internal.stages.generation.GenerationContext;
import org.compsysmed.ocsana.internal.stages.generation.GenerationResults;

import org.compsysmed.ocsana.internal.tasks.OCSANAStep;

import org.compsysmed.ocsana.internal.ui.results.OCSANAResultsPanel;

/**
 * Runner task for the prioritization stage of OCSANA
 **/
public class PrioritizationStageRunnerTask
    extends AbstractNetworkTask
    implements TaskObserver {
    private final TaskManager<?, ?> taskManager;
    private final GenerationContext generationContext;
    private final GenerationResults generationResults;
    private final PrioritizationContext prioritizationContext;
    private final TaskObserver observer;
    private final OCSANAResultsPanel resultsPanel;

    public PrioritizationStageRunnerTask (TaskManager<?, ?> taskManager,
                                          TaskObserver observer,
                                          GenerationContext generationContext,
                                          GenerationResults generationResults,
                                          PrioritizationContext prioritizationContext,
                                          OCSANAResultsPanel resultsPanel) {
        super(generationContext.getNetwork());

        this.taskManager = taskManager;
        this.observer = observer;

        this.generationContext = generationContext;
        this.generationResults = generationResults;
        this.prioritizationContext = prioritizationContext;

        this.resultsPanel = resultsPanel;
    }

    @Override
    public void run (TaskMonitor taskMonitor) {
        // Give the task a title
        taskMonitor.setTitle("OCSANA prioritization");
    }

    @Override
    public void taskFinished(ObservableTask task) {
        // Make sure the task returned non-null
        if (task.getResults(Object.class) == null) {
            cancel();
            return;
        }

        // Process the results based on the step just completed
        OCSANAStep currentStep = task.getResults(OCSANAStep.class);

        switch (currentStep) {
        default:
            throw new AssertionError("Invalid OCSANA step " + currentStep);
        }
    }

    @Override
    public void allFinished(FinishStatus finishStatus) {
        // Called after the TaskManager finished up a TaskIterator.
        // Currently, we don't do anything with this information.
    }
}
