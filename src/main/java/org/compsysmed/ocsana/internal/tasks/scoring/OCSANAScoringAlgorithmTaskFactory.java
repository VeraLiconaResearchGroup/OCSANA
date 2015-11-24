/**
 * Factory for tasks to run scoring algorithms in OCSANA
 *
 * Copyright Vera-Licona Research Group (C) 2015
 * @author Andrew Gainer-Dewar, Ph.D. <andrew.gainer.dewar@gmail.com>
 *
 * This software is licensed under the Artistic License 2.0, see the
 * LICENSE file or
 * http://www.opensource.org/licenses/artistic-license-2.0.php for
 * details
 **/

package org.compsysmed.ocsana.internal.tasks.scoring;

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
import org.compsysmed.ocsana.internal.algorithms.scoring.OCSANAScoringAlgorithm;

import org.compsysmed.ocsana.internal.tasks.OCSANAStep;

public class OCSANAScoringAlgorithmTaskFactory extends AbstractTaskFactory {
    private CyNetwork network;

    // User inputs
    private OCSANAScoringAlgorithm algorithm;
    private Collection<List<CyEdge>> pathsToTargets;
    private Collection<List<CyEdge>> pathsToOffTargets;

    public OCSANAScoringAlgorithmTaskFactory (CyNetwork network,
                                              OCSANAScoringAlgorithm algorithm,
                                              Collection<List<CyEdge>> pathsToTargets,
                                              Collection<List<CyEdge>> pathsToOffTargets) {
        this.network = network;
        this.algorithm = algorithm;
        this.pathsToTargets = pathsToTargets;
        this.pathsToOffTargets = pathsToOffTargets;
    }

    public TaskIterator createTaskIterator () {
        TaskIterator tasks = new TaskIterator();
        tasks.append(new OCSANAScoringAlgorithmTask(network, algorithm, pathsToTargets, pathsToOffTargets));
        return tasks;
    }
}
