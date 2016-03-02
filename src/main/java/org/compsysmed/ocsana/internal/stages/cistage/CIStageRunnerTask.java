/**
 * Runner for the CI stage of OCSANA
 *
 * Copyright Vera-Licona Research Group (C) 2016
 *
 * This software is licensed under the Artistic License 2.0, see the
 * LICENSE file or
 * http://www.opensource.org/licenses/artistic-license-2.0.php for
 * details
 **/

package org.compsysmed.ocsana.internal.stages.cistage;

// Java imports
import java.util.*;

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
import org.compsysmed.ocsana.internal.tasks.OCSANAStep;
import org.compsysmed.ocsana.internal.tasks.path.PathFindingAlgorithmTaskFactory;
import org.compsysmed.ocsana.internal.tasks.scoring.ScoringTaskFactory;
import org.compsysmed.ocsana.internal.tasks.mhs.MHSAlgorithmTaskFactory;

/**
 * Runner task for the CI stage of OCSANA
 *
 * This task runs the OCSANA algorithm on the inputs specified in a
 * CIStageContext. In particular, it:
 * 1) Finds paths from the sources to the targets;
 * 2) Finds MHSes/CIs of those paths; and
 * 3) Scores the influence of the CI nodes on the targets.
 **/
public class CIStageRunnerTask
    extends AbstractNetworkTask
    implements TaskObserver, ObservableTask {
    private TaskManager<?, ?> taskManager;
    private CIStageContext context;
    private CIStageResults results;
    private TaskObserver observer;

    private Boolean hasCleanResults = false;

    public CIStageRunnerTask (TaskManager<?, ?> taskManager,
                              TaskObserver observer,
                              CIStageContext context) {
        super(context.getNetwork());
        this.taskManager = taskManager;
        this.observer = observer;
        this.context = context;
        this.results = new CIStageResults();
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
            new PathFindingAlgorithmTaskFactory(context, results,
                                                OCSANAStep.FIND_PATHS_TO_TARGETS);

        taskManager.execute(pathsToTargetsTaskFactory.createTaskIterator(),
                            this);
    }

    private void spawnPathsToOffTargetsTask () {
        PathFindingAlgorithmTaskFactory pathsToOffTargetsTaskFactory =
            new PathFindingAlgorithmTaskFactory(context, results,
                                                OCSANAStep.FIND_PATHS_TO_OFF_TARGETS);

        taskManager.execute(pathsToOffTargetsTaskFactory.createTaskIterator(),
                            this);
    }

    private void spawnScoringTask () {
        ScoringTaskFactory scoringTaskFactory =
            new ScoringTaskFactory(context, results);

        taskManager.execute(scoringTaskFactory.createTaskIterator(), this);
    }

    private void spawnMHSTask () {
        MHSAlgorithmTaskFactory mhsTaskFactory =
            new MHSAlgorithmTaskFactory(context, results);

        taskManager.execute(mhsTaskFactory.createTaskIterator(), this);
    }

    private void spawnPresentResultsTask () {
        /*
        PresentResultsTaskFactory presentResultsTaskFactory =
            new PresentResultsTaskFactory(context, results, resultsPanel);

        taskManager.execute(presentResultsTaskFactory.createTaskIterator(), this);
        */
    }

    private void spawnCleanupTask () {
        // Flag that the results are clean
        hasCleanResults = true;
        observer.taskFinished(this);
    }

    @SuppressWarnings("unchecked")
    public <T> T getResults (Class<? extends T> type) {
        if (hasCleanResults) {
            return (T) results;
        } else {
            return null;
        }
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
        case GET_SETS:
            break;

        case FIND_PATHS_TO_TARGETS:
            spawnPathsToOffTargetsTask();
            break;

        case FIND_PATHS_TO_OFF_TARGETS:
            spawnScoringTask();
            break;

        case SCORE_PATHS:
            spawnMHSTask();
            break;

        case FIND_MHSES:
            spawnCleanupTask();
            //spawnPresentResultsTask();
            break;

        case PRESENT_RESULTS:
            spawnCleanupTask();
            break;

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
