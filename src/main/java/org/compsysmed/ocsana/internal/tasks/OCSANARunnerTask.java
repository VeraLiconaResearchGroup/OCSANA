/**
 * Runner task for OCSANA
 *
 * Copyright Vera-Licona Research Group (C) 2015
 *
 * This software is licensed under the Artistic License 2.0, see the
 * LICENSE file or
 * http://www.opensource.org/licenses/artistic-license-2.0.php for
 * details
 **/

package org.compsysmed.ocsana.internal.tasks;

// Java imports
import java.util.*;

// Cytoscape imports
import org.cytoscape.task.AbstractNetworkTask;

import org.cytoscape.work.Task;
import org.cytoscape.work.TaskMonitor;
import org.cytoscape.work.TaskManager;
import org.cytoscape.work.TaskObserver;
import org.cytoscape.work.ObservableTask;
import org.cytoscape.work.FinishStatus;
import org.cytoscape.work.ContainsTunables;
import org.cytoscape.work.ProvidesTitle;

import org.cytoscape.model.CyNetwork;
import org.cytoscape.model.CyEdge;
import org.cytoscape.model.CyNode;

// OCSANA imports
import org.compsysmed.ocsana.internal.tasks.nodeselection.NodeSetSelecter;
import org.compsysmed.ocsana.internal.tasks.edgeprocessing.EdgeProcessor;
import org.compsysmed.ocsana.internal.algorithms.path.AbstractPathFindingAlgorithm;
import org.compsysmed.ocsana.internal.algorithms.mhs.AbstractMHSAlgorithm;

import org.compsysmed.ocsana.internal.algorithms.scoring.OCSANAScoringAlgorithm;
import org.compsysmed.ocsana.internal.algorithms.scoring.DrugBankScoringAlgorithm;

import org.compsysmed.ocsana.internal.tasks.path.PathFindingAlgorithmTaskFactory;
import org.compsysmed.ocsana.internal.tasks.scoring.ScoringTaskFactory;
import org.compsysmed.ocsana.internal.tasks.mhs.MHSAlgorithmTaskFactory;
import org.compsysmed.ocsana.internal.tasks.results.PresentResultsTaskFactory;
import org.compsysmed.ocsana.internal.util.results.OCSANAResults;

import org.compsysmed.ocsana.internal.ui.results.OCSANAResultsPanel;

/**
 * Runner task for OCSANA
 *
 * This task runs the OCSANA algorithm on the specified inputs.  It
 * also gets input from the user to set up any second-level
 * configuration of specific algorithms.
 **/

public class OCSANARunnerTask extends AbstractNetworkTask
    implements TaskObserver {
    // User-configurable options

    // In general, the Runner should only contain configuration
    // options that are dependent on the choices made in the
    // Coordinator. For example, if the user chose an algorithm in the
    // Coordinator, they should configure it here.
    @ContainsTunables
    public AbstractPathFindingAlgorithm pathFindingAlg;

    @ContainsTunables
    public AbstractMHSAlgorithm mhsAlg;

    // End user configuration

    private OCSANAScoringAlgorithm ocsanaAlg;

    private OCSANAResults results;

    private OCSANAResultsPanel resultsPanel;

    private TaskManager<?, ?> taskManager;

    public OCSANARunnerTask (CyNetwork network,
                             TaskManager<?, ?> taskManager,
                             OCSANAResultsPanel resultsPanel,
                             EdgeProcessor edgeProcessor,
                             NodeSetSelecter nodeSetSelecter,
                             AbstractPathFindingAlgorithm pathFindingAlg,
                             OCSANAScoringAlgorithm ocsanaAlg,
                             DrugBankScoringAlgorithm drugBankAlg,
                             AbstractMHSAlgorithm mhsAlg,
                             Boolean includeEndpointsInCIs) {
        super(network);
        this.taskManager = taskManager;
        this.resultsPanel = resultsPanel;

        this.pathFindingAlg = pathFindingAlg;
        this.ocsanaAlg = ocsanaAlg;
        this.mhsAlg = mhsAlg;

        results = new OCSANAResults();
        results.network = network;
        results.edgeProcessor = edgeProcessor;
        results.nodeSetSelecter = nodeSetSelecter;
        results.pathFindingAlg = pathFindingAlg;
        results.ocsanaAlg = ocsanaAlg;
        results.drugBankAlg = drugBankAlg;
        results.mhsAlg = mhsAlg;

        results.includeEndpointsInCIs = includeEndpointsInCIs;
    }

    @ProvidesTitle
    public String getTitle() {
        return "OCSANA parameters II";
    }

    @Override
    public void run (TaskMonitor taskMonitor) {
        // TODO: Handle null members

        // Give the task a title
        taskMonitor.setTitle("OCSANA");

        // Start the first step of the algorithm
        spawnPathsToTargetsTask();

        // The rest of the tasks will be spawned by taskFinished().
    }

    private void spawnPathsToTargetsTask () {
        PathFindingAlgorithmTaskFactory pathsToTargetsTaskFactory =
            new PathFindingAlgorithmTaskFactory(results,
                                                OCSANAStep.FIND_PATHS_TO_TARGETS);

        taskManager.execute(pathsToTargetsTaskFactory.createTaskIterator(),
                            this);
    }

    private void spawnPathsToOffTargetsTask () {
        PathFindingAlgorithmTaskFactory pathsToOffTargetsTaskFactory =
            new PathFindingAlgorithmTaskFactory(results,
                                                OCSANAStep.FIND_PATHS_TO_OFF_TARGETS);

        taskManager.execute(pathsToOffTargetsTaskFactory.createTaskIterator(),
                            this);
    }

    private void spawnScoringTask () {
        ScoringTaskFactory scoringTaskFactory =
            new ScoringTaskFactory(results);

        taskManager.execute(scoringTaskFactory.createTaskIterator(), this);
    }

    private void spawnMHSTask () {
        MHSAlgorithmTaskFactory mhsTaskFactory =
            new MHSAlgorithmTaskFactory(results);

        taskManager.execute(mhsTaskFactory.createTaskIterator(), this);
    }

    private void spawnPresentResultsTask () {
        PresentResultsTaskFactory presentResultsTaskFactory =
            new PresentResultsTaskFactory(results, resultsPanel);

        taskManager.execute(presentResultsTaskFactory.createTaskIterator(), this);
    }

    private void spawnCleanupTask () {
        // Any post-process cleanup should happen here
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
            spawnPresentResultsTask();
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
