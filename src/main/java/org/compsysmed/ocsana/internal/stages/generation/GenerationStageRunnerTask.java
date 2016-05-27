/**
 * Runner for the generation stage of OCSANA
 *
 * Copyright Vera-Licona Research Group (C) 2016
 *
 * This software is licensed under the Artistic License 2.0, see the
 * LICENSE file or
 * http://www.opensource.org/licenses/artistic-license-2.0.php for
 * details
 **/

package org.compsysmed.ocsana.internal.stages.generation;

import org.cytoscape.task.AbstractNetworkTask;
import org.cytoscape.work.FinishStatus;
import org.cytoscape.work.ObservableTask;
import org.cytoscape.work.TaskManager;
import org.cytoscape.work.TaskMonitor;
import org.cytoscape.work.TaskObserver;

// OCSANA imports
import org.compsysmed.ocsana.internal.tasks.OCSANAStep;
import org.compsysmed.ocsana.internal.tasks.path.PathFindingAlgorithmTaskFactory;
import org.compsysmed.ocsana.internal.tasks.scoring.OCSANAScoringTaskFactory;
import org.compsysmed.ocsana.internal.tasks.mhs.MHSAlgorithmTaskFactory;
import org.compsysmed.ocsana.internal.tasks.results.PresentResultsTaskFactory;

import org.compsysmed.ocsana.internal.ui.results.OCSANAResultsPanel;

/**
 * Runner task for the generation stage of OCSANA
 *
 * This task runs the OCSANA algorithm on the inputs specified in a
 * CIStageContext. In particular, it:
 * 1) Finds paths from the sources to the targets;
 * 2) Finds MHSes/CIs of those paths; and
 * 3) Scores the influence of the CI nodes on the targets.
 **/
public class GenerationStageRunnerTask
    extends AbstractNetworkTask
    implements TaskObserver, ObservableTask {
    private final TaskManager<?, ?> taskManager;
    private final GenerationContext generationContext;
    private final GenerationResults generationResults;
    private final TaskObserver observer;
    private final OCSANAResultsPanel resultsPanel;

    private Boolean hasCleanResults = false;

    public GenerationStageRunnerTask (TaskManager<?, ?> taskManager,
                                      TaskObserver observer,
                                      GenerationContext generationContext,
                                      OCSANAResultsPanel resultsPanel) {
        super(generationContext.getNetwork());

        this.taskManager = taskManager;
        this.observer = observer;
        this.generationContext = generationContext;
        this.resultsPanel = resultsPanel;

        this.generationResults = new GenerationResults();
    }

    @Override
    public void run (TaskMonitor taskMonitor) {
        // Give the task a title
        taskMonitor.setTitle("OCSANA");

        // Flag that the results are not clean
        hasCleanResults = false;

        // Start the first step of the algorithm
        spawnPathsToTargetsTask();

        // The rest of the tasks will be spawned by taskFinished().
    }

    private void spawnPathsToTargetsTask () {
        PathFindingAlgorithmTaskFactory pathsToTargetsTaskFactory =
            new PathFindingAlgorithmTaskFactory(generationContext, generationResults,
                                                OCSANAStep.FIND_PATHS_TO_TARGETS);

        taskManager.execute(pathsToTargetsTaskFactory.createTaskIterator(),
                            this);
    }

    private void spawnPathsToOffTargetsTask () {
        PathFindingAlgorithmTaskFactory pathsToOffTargetsTaskFactory =
            new PathFindingAlgorithmTaskFactory(generationContext, generationResults,
                                                OCSANAStep.FIND_PATHS_TO_OFF_TARGETS);

        taskManager.execute(pathsToOffTargetsTaskFactory.createTaskIterator(),
                            this);
    }

    private void spawnOCSANAScoringTask () {
        OCSANAScoringTaskFactory scoringTaskFactory =
            new OCSANAScoringTaskFactory(generationContext, generationResults);

        taskManager.execute(scoringTaskFactory.createTaskIterator(), this);
    }

    private void spawnMHSTask () {
        MHSAlgorithmTaskFactory mhsTaskFactory =
            new MHSAlgorithmTaskFactory(generationContext, generationResults);

        taskManager.execute(mhsTaskFactory.createTaskIterator(), this);
    }

    private void spawnPresentResultsTask () {
        PresentResultsTaskFactory presentResultsTaskFactory =
            new PresentResultsTaskFactory(generationContext, generationResults, resultsPanel);

        taskManager.execute(presentResultsTaskFactory.createTaskIterator(), this);
    }

    private void spawnCleanupTask () {
        // Flag that the results are clean
        hasCleanResults = true;

        observer.taskFinished(this);
    }

    @Override
    @SuppressWarnings("unchecked")
    public <T> T getResults (Class<? extends T> type) {
        if (hasCleanResults) {
            return (T) generationResults;
        } else {
            return null;
        }
    }

    @Override
    public void taskFinished(ObservableTask task) {
        if (cancelled) {
            return;
        }

        // Process the results based on the step just completed
        OCSANAStep currentStep = task.getResults(OCSANAStep.class);

        switch (currentStep) {
        case GET_SETS:
            break;

        case FIND_PATHS_TO_TARGETS:
            spawnPathsToOffTargetsTask();
            break;

        case FIND_PATHS_TO_OFF_TARGETS:
            spawnOCSANAScoringTask();
            break;

        case SCORE_PATHS:
            spawnMHSTask();
            break;

        case FIND_MHSES:
            spawnPresentResultsTask();
            break;

        case PRESENT_RESULTS:
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
