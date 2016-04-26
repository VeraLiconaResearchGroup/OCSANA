/**
 * Task to run path-finding algorithm in OCSANA
 *
 * Copyright Vera-Licona Research Group (C) 2015
 *
 * This software is licensed under the Artistic License 2.0, see the
 * LICENSE file or
 * http://www.opensource.org/licenses/artistic-license-2.0.php for
 * details
 **/

package org.compsysmed.ocsana.internal.tasks.path;

// Java imports
import java.util.*;

// Cytoscape imports
import org.cytoscape.work.TaskMonitor;

import org.cytoscape.model.CyNode;
import org.cytoscape.model.CyEdge;

// OCSANA imports
import org.compsysmed.ocsana.internal.tasks.AbstractOCSANATask;
import org.compsysmed.ocsana.internal.tasks.OCSANAStep;

import org.compsysmed.ocsana.internal.stages.generation.GenerationContext;
import org.compsysmed.ocsana.internal.stages.generation.GenerationResults;

public class PathFindingAlgorithmTask extends AbstractOCSANATask {
    private GenerationContext context;
    private GenerationResults results;
    private OCSANAStep algStep;
    private Collection<List<CyEdge>> paths;

    public PathFindingAlgorithmTask (GenerationContext context,
                                     GenerationResults results,
                                     OCSANAStep algStep) {
        super(context.getNetwork());
        this.context = context;
        this.results = results;
        this.algStep = algStep;
    }

    @Override
    public void run (TaskMonitor taskMonitor) {
        String targetType;
        Set<CyNode> sourceNodes = context.sourceNodes;
        Set<CyNode> targetsForThisRun;
        switch (algStep) {
        case FIND_PATHS_TO_TARGETS:
            targetType = "target";
            targetsForThisRun = context.targetNodes;
            break;

        case FIND_PATHS_TO_OFF_TARGETS:
            targetType = "off-target";
            targetsForThisRun = context.offTargetNodes;
            break;

        default:
            throw new IllegalStateException("Invalid algorithm step for path-finding");
        }

        if (targetsForThisRun == null || sourceNodes == null) {
            throw new IllegalStateException("Nodes not set by user.");
        }

        taskMonitor.setTitle(String.format("Paths to %ss", targetType));

        taskMonitor.setStatusMessage(String.format("Finding paths from %d source nodes to %d %s nodes (algorithm: %s).", sourceNodes.size(), targetsForThisRun.size(), targetType, context.pathFindingAlg.shortName()));

        Long preTime = System.nanoTime();
        paths = context.pathFindingAlg.paths(sourceNodes, targetsForThisRun);
        Long postTime = System.nanoTime();

        Double runTime = (postTime - preTime) / 1E9;

        switch (algStep) {
        case FIND_PATHS_TO_TARGETS:
            results.pathsToTargets = paths;
            results.pathsToTargetsExecutionSeconds = runTime;
            break;

        case FIND_PATHS_TO_OFF_TARGETS:
            results.pathsToOffTargets = paths;
            results.pathsToOffTargetsExecutionSeconds = runTime;
            break;

        default:
            throw new IllegalStateException("Invalid algorithm step for path-finding");
        }

        taskMonitor.showMessage(TaskMonitor.Level.INFO, String.format("Found %d paths in %fs.", paths.size(), runTime));
    }

    /**
     * Return the paths found by this task (or null if the task has
     * not completed)
     **/
    public Collection<List<CyEdge>> getPaths () {
        return paths;
    }

    @Override
    @SuppressWarnings("unchecked")
    public <T> T getResults (Class<? extends T> type) {
        if (type.isAssignableFrom(OCSANAStep.class)) {
            return (T) algStep;
        } else {
            return (T) getPaths();
        }
    }

    @Override
    public void cancel () {
        super.cancel();
        context.pathFindingAlg.cancel();
        results.pathFindingCanceled = true;
    }
}
