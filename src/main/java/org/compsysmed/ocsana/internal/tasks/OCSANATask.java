/**
 * Main task for OCSANA
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
import org.cytoscape.work.TaskMonitor;
import org.cytoscape.work.ContainsTunables;

import org.cytoscape.model.CyNetwork;
import org.cytoscape.model.CyNode;

// OCSANA imports
import org.compsysmed.ocsana.internal.tasks.nodeselection.NodeSetSelecter;

import org.compsysmed.ocsana.internal.tasks.path.AbstractPathFindingAlgorithm;
import org.compsysmed.ocsana.internal.tasks.path.PathFindingAlgorithmSelecter;
import org.compsysmed.ocsana.internal.tasks.path.PathFindingAlgorithmTask;

public class OCSANATask extends AbstractNetworkTask {
    @ContainsTunables
    public NodeSetSelecter nodeSelecter;

    @ContainsTunables
    public PathFindingAlgorithmSelecter pathFinder;

    public OCSANATask (CyNetwork network) {
        super(network);
        nodeSelecter = new NodeSetSelecter(network);
        pathFinder = new PathFindingAlgorithmSelecter(network);
    }

    public void run (TaskMonitor taskMonitor) {
        // Give the task a title.
        taskMonitor.setTitle("OCSANA task");

        // Make sure we have a network
        if (this.network == null) {
            taskMonitor.showMessage(TaskMonitor.Level.INFO, "No network found.");
            return;
        }

        // Fetch configuration
        Set<CyNode> sourceNodes = new HashSet<>(nodeSelecter.getSourceNodes());
        Set<CyNode> targetNodes = new HashSet<>(nodeSelecter.getTargetNodes());
        Set<CyNode> offTargetNodes = new HashSet<>(nodeSelecter.getOffTargetNodes());

        taskMonitor.showMessage(TaskMonitor.Level.INFO, sourceNodes.size() + " sources, " + targetNodes.size() + " targets, " + offTargetNodes.size() + " off-targets.");

        // Set up the algorithm environment
        AbstractPathFindingAlgorithm pathAlgorithm = pathFinder.getAlgorithm();

        // Run the OCSANA algorithm
        // 0: Turn undirected edges into pairs of directed edges
        // TODO: Implement

        // 1: Get nodes (handled above)

        // 2: Find paths
        PathFindingAlgorithmTask pathFinder =
            new PathFindingAlgorithmTask(network,
                                         pathAlgorithm,
                                         sourceNodes,
                                         targetNodes,
                                         offTargetNodes);
        insertTasksAfterCurrentTask(pathFinder);

        // 3: Score nodes
        // TODO: Implement

        // 4: Find MHSes of target paths
        // TODO: Implement

        // 5: Show results
        // TODO: Implement
    }
}
