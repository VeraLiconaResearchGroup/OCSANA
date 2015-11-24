/**
 * Factory for tasks to present results to user in OCSANA
 *
 * Copyright Vera-Licona Research Group (C) 2015
 * @author Andrew Gainer-Dewar, Ph.D. <andrew.gainer.dewar@gmail.com>
 *
 * This software is licensed under the Artistic License 2.0, see the
 * LICENSE file or
 * http://www.opensource.org/licenses/artistic-license-2.0.php for
 * details
 **/

package org.compsysmed.ocsana.internal.tasks.results;

// Java imports
import java.util.*;

// Cytoscape imports
import org.cytoscape.work.AbstractTaskFactory;
import org.cytoscape.work.TaskMonitor;
import org.cytoscape.work.TaskIterator;

import org.cytoscape.model.CyNetwork;
import org.cytoscape.model.CyNode;
import org.cytoscape.model.CyEdge;

// OCSANA imports
import org.compsysmed.ocsana.internal.algorithms.path.AbstractPathFindingAlgorithm;
import org.compsysmed.ocsana.internal.algorithms.mhs.AbstractMHSAlgorithm;
import org.compsysmed.ocsana.internal.algorithms.scoring.OCSANAScoringAlgorithm;

import org.compsysmed.ocsana.internal.tasks.OCSANAStep;

public class PresentResultsTaskFactory extends AbstractTaskFactory {
    private CyNetwork network;

    // User inputs
    protected Set<CyNode> sourceNodes;
    protected Set<CyNode> targetNodes;
    protected Set<CyNode> offTargetNodes;

    // Paths data
    protected AbstractPathFindingAlgorithm pathAlg;
    protected Iterable<? extends Iterable<CyEdge>> pathsToTargets;
    protected Iterable<? extends Iterable<CyEdge>> pathsToOffTargets;

    // Scoring data
    protected OCSANAScoringAlgorithm ocsanaAlg;

    // MHS data
    protected AbstractMHSAlgorithm mhsAlg;
    protected Iterable<? extends Iterable<CyNode>> MHSes;

    public PresentResultsTaskFactory (CyNetwork network,
                                      Set<CyNode> sourceNodes,
                                      Set<CyNode> targetNodes,
                                      Set<CyNode> offTargetNodes,
                                      AbstractPathFindingAlgorithm pathAlg,
                                      Iterable<? extends Iterable<CyEdge>> pathsToTargets,
                                      Iterable<? extends Iterable<CyEdge>> pathsToOffTargets,
                                      OCSANAScoringAlgorithm ocsanaAlg,
                                      AbstractMHSAlgorithm mhsAlg,
                                      Iterable<? extends Iterable<CyNode>> MHSes) {
        this.network = network;
        this.sourceNodes = sourceNodes;
        this.targetNodes = targetNodes;
        this.offTargetNodes = offTargetNodes;
        this.pathAlg = pathAlg;
        this.pathsToTargets = pathsToTargets;
        this.pathsToOffTargets = pathsToOffTargets;
        this.ocsanaAlg = ocsanaAlg;
        this.mhsAlg = mhsAlg;
        this.MHSes = MHSes;
    }

    public TaskIterator createTaskIterator () {
        TaskIterator tasks = new TaskIterator();
        tasks.append(new PresentResultsTask(network, sourceNodes,
                                            targetNodes, offTargetNodes,
                                            pathAlg,
                                            pathsToTargets, pathsToOffTargets,
                                            ocsanaAlg,
                                            mhsAlg, MHSes));
        return tasks;
    }
}
