package org.compsysmed.ocsana.internal.tasks.path;

// Java imports
import java.util.*;

// Cytoscape imports
import org.cytoscape.task.AbstractNetworkTask;
import org.cytoscape.work.TaskMonitor;
import org.cytoscape.work.Tunable;
import org.cytoscape.work.ContainsTunables;
import org.cytoscape.work.util.ListSingleSelection;

import org.cytoscape.model.CyTableUtil;
import org.cytoscape.model.CyNode;
import org.cytoscape.model.CyNetwork;

// OCSANA imports

public class PathFindingAlgorithmTask extends AbstractNetworkTask {
    @ContainsTunables
    public AbstractPathFindingAlgorithm algorithm;

    private Set<CyNode> sourceNodes;
    private Set<CyNode> targetNodes;
    private Set<CyNode> offTargetNodes;

    public PathFindingAlgorithmTask (CyNetwork network,
                                     AbstractPathFindingAlgorithm algorithm,
                                     Set<CyNode> sourceNodes,
                                     Set<CyNode> targetNodes,
                                     Set<CyNode> offTargetNodes) {
        super(network);
        this.algorithm = algorithm;
        this.sourceNodes = sourceNodes;
        this.targetNodes = targetNodes;
        this.offTargetNodes = offTargetNodes;
    }

    public void run (TaskMonitor taskMonitor) {
        taskMonitor.showMessage(TaskMonitor.Level.INFO, "Finding paths to targets");
        List<List<CyNode>> targetPaths = algorithm.paths(sourceNodes, targetNodes);

        taskMonitor.showMessage(TaskMonitor.Level.INFO, "Finding paths to off-targets");
        List<List<CyNode>> offTargetPaths = algorithm.paths(sourceNodes, offTargetNodes);

        taskMonitor.showMessage(TaskMonitor.Level.INFO, "Found " + targetPaths.size() + " paths to targets and " + offTargetPaths.size() + " to off-targets.");
    }
}
