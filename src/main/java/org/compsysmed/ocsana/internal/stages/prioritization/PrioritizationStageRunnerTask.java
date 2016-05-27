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

import org.cytoscape.task.AbstractNetworkTask;
import org.cytoscape.work.FinishStatus;
import org.cytoscape.work.ObservableTask;
import org.cytoscape.work.TaskManager;
import org.cytoscape.work.TaskMonitor;
import org.cytoscape.work.TaskObserver;

import java.util.Objects;

// OCSANA imports
import org.compsysmed.ocsana.internal.stages.generation.GenerationContext;
import org.compsysmed.ocsana.internal.stages.generation.GenerationResults;

import org.compsysmed.ocsana.internal.tasks.OCSANAStep;

import org.compsysmed.ocsana.internal.tasks.drugability.SignedInterventionScoringAlgorithmTaskFactory;
import org.compsysmed.ocsana.internal.tasks.signassignment.SignAssignmentAlgorithmTaskFactory;

import org.compsysmed.ocsana.internal.ui.results.OCSANAResultsPanel;

/**
 * Runner task for the prioritization stage of OCSANA
 **/
public class PrioritizationStageRunnerTask
    extends AbstractNetworkTask
    implements TaskObserver, ObservableTask {
    private final TaskManager<?, ?> taskManager;
    private final GenerationContext generationContext;
    private final GenerationResults generationResults;
    private final PrioritizationContext prioritizationContext;
    private final PrioritizationResults prioritizationResults;
    private final TaskObserver observer;
    private final OCSANAResultsPanel resultsPanel;

    public PrioritizationStageRunnerTask (TaskManager<?, ?> taskManager,
                                          TaskObserver observer,
                                          GenerationContext generationContext,
                                          GenerationResults generationResults,
                                          PrioritizationContext prioritizationContext,
                                          PrioritizationResults prioritizationResults,
                                          OCSANAResultsPanel resultsPanel) {
        super(generationContext.getNetwork());

        Objects.requireNonNull(taskManager, "Task manager cannot be null");
        this.taskManager = taskManager;

        Objects.requireNonNull(observer, "Task observer cannot be null");
        this.observer = observer;

        Objects.requireNonNull(generationContext, "Generation context cannot be null");
        this.generationContext = generationContext;

        Objects.requireNonNull(generationResults, "Generation results cannot be null");
        this.generationResults = generationResults;

        Objects.requireNonNull(prioritizationContext, "Prioritization context cannot be null");
        this.prioritizationContext = prioritizationContext;

        Objects.requireNonNull(prioritizationResults, "Prioritization results cannot be null");
        this.prioritizationResults = prioritizationResults;

        Objects.requireNonNull(resultsPanel, "Results panel cannot be null");
        this.resultsPanel = resultsPanel;
    }

    @Override
    public void run (TaskMonitor taskMonitor) {
        // Give the task a title
        taskMonitor.setTitle("OCSANA prioritization");

        spawnSignAssignmentTask();
    }

    private void spawnSignAssignmentTask () {
        SignAssignmentAlgorithmTaskFactory ciSignTaskFactory =
            new SignAssignmentAlgorithmTaskFactory(prioritizationContext, prioritizationResults);

        taskManager.execute(ciSignTaskFactory.createTaskIterator(), this);
    }

    private void spawnSignedInterventionScoringTask () {
        SignedInterventionScoringAlgorithmTaskFactory siScoringTaskFactory =
            new SignedInterventionScoringAlgorithmTaskFactory(prioritizationContext, prioritizationResults);

        taskManager.execute(siScoringTaskFactory.createTaskIterator(), this);
    }

    private void spawnCleanupTask() {
        observer.taskFinished(this);
    }

    @Override
    @SuppressWarnings("unchecked")
    public <T> T getResults (Class<? extends T> type) {
        return (T) prioritizationResults;
    }

    @Override
    public void taskFinished(ObservableTask task) {
        if (cancelled) {
            return;
        }

        // Process the results based on the step just completed
        OCSANAStep currentStep = task.getResults(OCSANAStep.class);

        switch (currentStep) {
        case ASSIGN_CI_SIGNS:
            spawnSignedInterventionScoringTask();
            break;

        case SCORE_SIGNED_INTERVENTIONS:
            spawnCleanupTask();
            break;

        default:
            throw new IllegalStateException("Unknown OCSANA step " + currentStep);
        }
    }

    @Override
    public void allFinished(FinishStatus finishStatus) {
        if (finishStatus.getType() != FinishStatus.Type.SUCCEEDED) {
            cancel();
            spawnCleanupTask();
        }
    }
}
