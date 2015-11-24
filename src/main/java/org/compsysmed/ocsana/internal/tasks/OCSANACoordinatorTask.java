/**
 * Coordinator task for OCSANA
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
import org.cytoscape.work.TaskManager;
import org.cytoscape.work.TaskMonitor;
import org.cytoscape.work.TaskIterator;
import org.cytoscape.work.ContainsTunables;

import org.cytoscape.model.CyNetwork;
import org.cytoscape.model.CyNode;

// OCSANA imports
import org.compsysmed.ocsana.internal.ui.OCSANAResultsPanel;

import org.compsysmed.ocsana.internal.algorithms.path.AbstractPathFindingAlgorithm;
import org.compsysmed.ocsana.internal.algorithms.scoring.OCSANAScoringAlgorithm;
import org.compsysmed.ocsana.internal.algorithms.mhs.AbstractMHSAlgorithm;

import org.compsysmed.ocsana.internal.tasks.OCSANARunnerTask;
import org.compsysmed.ocsana.internal.tasks.nodeselection.NodeSetSelecter;
import org.compsysmed.ocsana.internal.tasks.path.PathFindingAlgorithmSelecter;
import org.compsysmed.ocsana.internal.tasks.mhs.MHSAlgorithmSelecter;

/**
 * Coordinator task for OCSANA
 *
 * This task gets input from the user to set up the top-level
 * configuration of an OCSANA run: which vertices are in the sets and
 * which algorithms should be used for the computations.
 *
 * It will launch an OCSANARunnerTask which configures and runs the
 * algorithms.
 **/

public class OCSANACoordinatorTask extends AbstractNetworkTask {
    protected TaskManager taskManager;

    @ContainsTunables
    public NodeSetSelecter nodeSelecter;

    @ContainsTunables
    public PathFindingAlgorithmSelecter pathAlgSelecter;

    @ContainsTunables
    public OCSANAScoringAlgorithm ocsanaAlgorithm;

    @ContainsTunables
    public MHSAlgorithmSelecter mhsAlgSelecter;

    protected OCSANAResultsPanel resultsPanel;

    public OCSANACoordinatorTask (CyNetwork network,
                                  TaskManager taskManager,
                                  OCSANAResultsPanel resultsPanel) {
        super(network);

        this.taskManager = taskManager;
        this.resultsPanel = resultsPanel;

        nodeSelecter = new NodeSetSelecter(network);
        pathAlgSelecter = new PathFindingAlgorithmSelecter(network);
        mhsAlgSelecter = new MHSAlgorithmSelecter(network);
        ocsanaAlgorithm = new OCSANAScoringAlgorithm(network);
    }

    public void run (TaskMonitor taskMonitor) {
        // TODO: Handle null members
        // Give the task a title.
        taskMonitor.setTitle("OCSANA setup");

        // Fetch configuration
        AbstractPathFindingAlgorithm pathAlgorithm = pathAlgSelecter.getAlgorithm();
        AbstractMHSAlgorithm mhsAlgorithm = mhsAlgSelecter.getAlgorithm();

        Set<CyNode> sourceNodes = new HashSet<>(nodeSelecter.getSourceNodes());
        Set<CyNode> targetNodes = new HashSet<>(nodeSelecter.getTargetNodes());
        Set<CyNode> offTargetNodes = new HashSet<>(nodeSelecter.getOffTargetNodes());

        OCSANARunnerTask runnerTask = new OCSANARunnerTask(network,
                                                           taskManager,
                                                           resultsPanel,
                                                           pathAlgorithm,
                                                           ocsanaAlgorithm,
                                                           mhsAlgorithm,
                                                           sourceNodes,
                                                           targetNodes,
                                                           offTargetNodes);

        insertTasksAfterCurrentTask(runnerTask);
    }
}
