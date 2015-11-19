/**
 * Factory for tasks to run path-finding algorithms in OCSANA
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
import org.cytoscape.work.AbstractTaskFactory;
import org.cytoscape.work.TaskMonitor;
import org.cytoscape.work.TaskIterator;

import org.cytoscape.model.CyNode;
import org.cytoscape.model.CyNetwork;

// OCSANA imports
import org.compsysmed.ocsana.internal.algorithms.path.AbstractPathFindingAlgorithm;

import org.compsysmed.ocsana.internal.tasks.OCSANAStep;

public class PathFindingAlgorithmTaskFactory extends AbstractTaskFactory {
    private CyNetwork network;
    private OCSANAStep algStep;
    private AbstractPathFindingAlgorithm algorithm;
    private Set<CyNode> sourceNodes;
    private Set<CyNode> targetNodes;

    public PathFindingAlgorithmTaskFactory (CyNetwork network,
                                            OCSANAStep algStep,
                                            AbstractPathFindingAlgorithm algorithm,
                                            Set<CyNode> sourceNodes,
                                            Set<CyNode> targetNodes) {
        super();
        this.network = network;
        this.algStep = algStep;
        this.algorithm = algorithm;
        this.sourceNodes = sourceNodes;
        this.targetNodes = targetNodes;
    }

    public TaskIterator createTaskIterator () {
        TaskIterator tasks = new TaskIterator();
        tasks.append(new PathFindingAlgorithmTask(network, algStep, algorithm,
                                                  sourceNodes, targetNodes));
        return tasks;
    }
}
