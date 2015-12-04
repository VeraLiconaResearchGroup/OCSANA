/**
 * Task to run path-finding algorithm in OCSANA
 *
 * Copyright Vera-Licona Research Group (C) 2015
 * @author Andrew Gainer-Dewar, Ph.D. <andrew.gainer.dewar@gmail.com>
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
import org.cytoscape.model.CyNetwork;

// OCSANA imports
import org.compsysmed.ocsana.internal.tasks.AbstractOCSANATask;
import org.compsysmed.ocsana.internal.tasks.OCSANAStep;

import org.compsysmed.ocsana.internal.tasks.results.OCSANAResults;

public class PathFindingAlgorithmTask extends AbstractOCSANATask {
    private OCSANAResults results;
    private OCSANAStep algStep;
    private Collection<List<CyEdge>> paths;

    public PathFindingAlgorithmTask (OCSANAResults results,
                                     OCSANAStep algStep) {
        super(results.network);
        this.results = results;
        this.algStep = algStep;
    }

    public void run (TaskMonitor taskMonitor) {
        String targetType;
        Set<CyNode> targetsForThisRun;
        switch (algStep) {
        case FIND_PATHS_TO_TARGETS:
            targetType = "target";
            targetsForThisRun = results.targetNodes;
            break;

        case FIND_PATHS_TO_OFF_TARGETS:
            targetType = "off-target";
            targetsForThisRun = results.offTargetNodes;
            break;

        default:
            throw new IllegalStateException("Invalid algorithm step for path-finding");
        }

        if (targetsForThisRun == null || results.sourceNodes == null) {
            throw new IllegalStateException("Nodes not set by user.");
        }

        taskMonitor.setTitle("Paths to " + targetType + "s");

        taskMonitor.setStatusMessage("Finding paths from " +
                                     results.sourceNodes.size() + " source nodes to to " +
                                     targetsForThisRun.size() + " " + targetType + " nodes.");

        paths = results.pathFindingAlg.paths(results.sourceNodes, targetsForThisRun);

        switch (algStep) {
        case FIND_PATHS_TO_TARGETS:
            results.pathsToTargets = paths;
            break;

        case FIND_PATHS_TO_OFF_TARGETS:
            results.pathsToOffTargets = paths;
            break;

        default:
            throw new IllegalStateException("Invalid algorithm step for path-finding");
        }

        taskMonitor.showMessage(TaskMonitor.Level.INFO, "Found " + paths.size() + " paths.");
    }

    public Collection<List<CyEdge>> getPaths () {
        return paths;
    }

    public <T> T getResults (Class<? extends T> type) {
        if (type.isAssignableFrom(OCSANAStep.class)) {
            return (T) algStep;
        } else {
            return (T) getPaths();
        }
    }

    public void cancel () {
        super.cancel();
        results.pathFindingAlg.cancel();
        results.pathFindingCanceled = true;
    }
}
