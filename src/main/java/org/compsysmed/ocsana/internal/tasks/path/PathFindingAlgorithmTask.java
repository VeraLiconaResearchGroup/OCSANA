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
import org.cytoscape.model.CyNetwork;

// OCSANA imports
import org.compsysmed.ocsana.internal.algorithms.path.AbstractPathFindingAlgorithm;

import org.compsysmed.ocsana.internal.tasks.AbstractOCSANATask;
import org.compsysmed.ocsana.internal.tasks.OCSANAStep;

public class PathFindingAlgorithmTask extends AbstractOCSANATask {
    private OCSANAStep algStep;
    public AbstractPathFindingAlgorithm algorithm;

    private Set<CyNode> sourceNodes;
    private Set<CyNode> targetNodes;

    private List<List<CyNode>> paths;

    public PathFindingAlgorithmTask (CyNetwork network,
                                     OCSANAStep algStep,
                                     AbstractPathFindingAlgorithm algorithm,
                                     Set<CyNode> sourceNodes,
                                     Set<CyNode> targetNodes) {
        super(network);
        this.algStep = algStep;
        this.algorithm = algorithm;
        this.sourceNodes = sourceNodes;
        this.targetNodes = targetNodes;
    }

    public void run (TaskMonitor taskMonitor) {
        String targetType;
        switch (algStep) {
        case FIND_PATHS_TO_TARGETS:
            targetType = "target";
            break;

        case FIND_PATHS_TO_OFF_TARGETS:
            targetType = "off-target";
            break;

        default:
            throw new IllegalArgumentException("Invalid algorithm step for path-finding");
        }

        taskMonitor.setTitle("Paths to " + targetType + "s");

        taskMonitor.showMessage(TaskMonitor.Level.INFO, "Finding paths from " +
                                sourceNodes.size() + " source nodes to to " +
                                targetNodes.size() + " " + targetType + " nodes.");

        paths = algorithm.paths(sourceNodes, targetNodes);

        taskMonitor.showMessage(TaskMonitor.Level.INFO, "Found " + paths.size() + " paths.");
    }

    public List<List<CyNode>> getPaths () {
        return paths;
    }

    public <T> T getResults (Class<? extends T> type) {
        if (type.isAssignableFrom(OCSANAStep.class)) {
            return (T) algStep;
        } else {
            return (T) getPaths();
        }
    }

    public OCSANAStep getOCSANAStep () {
        return algStep;
    }

    public void cancel () {
        super.cancel();
        algorithm.cancel();
    }
}
