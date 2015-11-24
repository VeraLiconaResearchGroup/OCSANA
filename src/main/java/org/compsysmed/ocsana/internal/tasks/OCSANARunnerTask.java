/**
 * Runner task for OCSANA
 *
 * Copyright Vera-Licona Research Group (C) 2015
 * @author Andrew Gainer-Dewar, Ph.D. <andrew.gainer.dewar@gmail.com>
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
import org.cytoscape.work.TaskIterator;
import org.cytoscape.work.ObservableTask;
import org.cytoscape.work.FinishStatus;
import org.cytoscape.work.ContainsTunables;

import org.cytoscape.model.CyNetwork;
import org.cytoscape.model.CyEdge;
import org.cytoscape.model.CyNode;

// OCSANA imports
import org.compsysmed.ocsana.internal.algorithms.path.AbstractPathFindingAlgorithm;
import org.compsysmed.ocsana.internal.algorithms.mhs.AbstractMHSAlgorithm;
import org.compsysmed.ocsana.internal.algorithms.scoring.OCSANAScoringAlgorithm;

import org.compsysmed.ocsana.internal.tasks.path.PathFindingAlgorithmTask;
import org.compsysmed.ocsana.internal.tasks.path.PathFindingAlgorithmTaskFactory;

import org.compsysmed.ocsana.internal.tasks.scoring.OCSANAScoringAlgorithmTask;
import org.compsysmed.ocsana.internal.tasks.scoring.OCSANAScoringAlgorithmTaskFactory;

import org.compsysmed.ocsana.internal.tasks.mhs.MHSAlgorithmTask;
import org.compsysmed.ocsana.internal.tasks.mhs.MHSAlgorithmTaskFactory;

import org.compsysmed.ocsana.internal.tasks.results.PresentResultsTask;
import org.compsysmed.ocsana.internal.tasks.results.PresentResultsTaskFactory;
import org.compsysmed.ocsana.internal.tasks.results.OCSANAResults;

import org.compsysmed.ocsana.internal.ui.OCSANAResultsPanel;

/**
 * Runner task for OCSANA
 *
 * This task runs the OCSANA algorithm on the specified inputs.  It
 * also gets input from the user to set up any second-level
 * configuration of specific algorithms.
 **/

public class OCSANARunnerTask extends AbstractNetworkTask
    implements TaskObserver {
    @ContainsTunables
    public AbstractPathFindingAlgorithm pathFindingAlg;

    @ContainsTunables
    public AbstractMHSAlgorithm mhsAlg;

    public OCSANAScoringAlgorithm ocsanaAlg;

    protected Set<CyNode> sourceNodes;
    protected Set<CyNode> targetNodes;
    protected Set<CyNode> offTargetNodes;

    protected OCSANAResults results;

    protected OCSANAResultsPanel resultsPanel;

    private TaskManager taskManager;

    public OCSANARunnerTask (CyNetwork network,
                             TaskManager taskManager,
                             OCSANAResultsPanel resultsPanel,
                             AbstractPathFindingAlgorithm pathFindingAlg,
                             OCSANAScoringAlgorithm ocsanaAlg,
                             AbstractMHSAlgorithm mhsAlg,
                             Set<CyNode> sourceNodes,
                             Set<CyNode> targetNodes,
                             Set<CyNode> offTargetNodes) {
        super(network);
        this.taskManager = taskManager;
        this.resultsPanel = resultsPanel;

        this.pathFindingAlg = pathFindingAlg;
        this.ocsanaAlg = ocsanaAlg;
        this.mhsAlg = mhsAlg;

        this.sourceNodes = sourceNodes;
        this.targetNodes = targetNodes;
        this.offTargetNodes = offTargetNodes;

        results = new OCSANAResults();
        results.network = network;
        results.pathFindingAlg = pathFindingAlg;
        results.ocsanaAlg = ocsanaAlg;
        results.mhsAlg = mhsAlg;

        results.sourceNodes = sourceNodes;
        results.targetNodes = targetNodes;
        results.offTargetNodes = offTargetNodes;
    }

    public void run (TaskMonitor taskMonitor) {
        // TODO: Handle null members

        // Give the task a title
        taskMonitor.setTitle("OCSANA");

        // Start the first step of the algorithm
        spawnPathsToTargetsTask();

        // The rest of the tasks will be spawned by taskFinished().
    }

    protected void spawnPathsToTargetsTask () {
        PathFindingAlgorithmTaskFactory pathsToTargetsTaskFactory =
            new PathFindingAlgorithmTaskFactory(network,
                                                OCSANAStep.FIND_PATHS_TO_TARGETS,
                                                pathFindingAlg,
                                                sourceNodes, targetNodes);

        taskManager.execute(pathsToTargetsTaskFactory.createTaskIterator(),
                            this);
    }

    protected void spawnPathsToOffTargetsTask () {
        PathFindingAlgorithmTaskFactory pathsToOffTargetsTaskFactory =
            new PathFindingAlgorithmTaskFactory(network,
                                                OCSANAStep.FIND_PATHS_TO_OFF_TARGETS,
                                                pathFindingAlg,
                                                sourceNodes, offTargetNodes);

        taskManager.execute(pathsToOffTargetsTaskFactory.createTaskIterator(),
                            this);
    }

    protected void spawnScoringTask () {
        OCSANAScoringAlgorithmTaskFactory scoringTaskFactory =
            new OCSANAScoringAlgorithmTaskFactory(network, ocsanaAlg,
                                                  results.pathsToTargets,
                                                  results.pathsToOffTargets);

        taskManager.execute(scoringTaskFactory.createTaskIterator(), this);
    }

    protected void spawnMHSTask () {
        MHSAlgorithmTaskFactory mhsTaskFactory =
            new MHSAlgorithmTaskFactory(network,
                                        mhsAlg,
                                        results.pathsToTargets);

        taskManager.execute(mhsTaskFactory.createTaskIterator(), this);
    }

    protected void spawnPresentResultsTask () {
        PresentResultsTaskFactory presentResultsTaskFactory =
            new PresentResultsTaskFactory(network, results, resultsPanel);

        taskManager.execute(presentResultsTaskFactory.createTaskIterator(), this);
    }

    protected void spawnCleanupTask () {
        // Any post-process cleanup should happen here
        ocsanaAlg.cleanupAfterRun();
    }

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
            results.pathsToTargets = task.getResults(List.class);
            spawnPathsToOffTargetsTask();
            break;

        case FIND_PATHS_TO_OFF_TARGETS:
            results.pathsToOffTargets = task.getResults(List.class);
            spawnScoringTask();
            break;

        case SCORE_PATHS:
            spawnMHSTask();
            break;

        case FIND_MHSES:
            results.MHSes = task.getResults(List.class);
            spawnPresentResultsTask();
            break;

        case PRESENT_RESULTS:
            spawnCleanupTask();
            break;

        default:
            throw new AssertionError("Invalid OCSANA step " + currentStep);
        }
    }

    public void allFinished(FinishStatus finishStatus) {
        // Called after the TaskManager finished up a TaskIterator.
        // Currently, we don't do anything with this information.
    }
}
