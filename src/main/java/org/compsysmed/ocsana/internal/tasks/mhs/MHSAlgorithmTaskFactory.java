/**
 * Factory for tasks to run MHS algorithms in OCSANA
 *
 * Copyright Vera-Licona Research Group (C) 2015
 * @author Andrew Gainer-Dewar, Ph.D. <andrew.gainer.dewar@gmail.com>
 *
 * This software is licensed under the Artistic License 2.0, see the
 * LICENSE file or
 * http://www.opensource.org/licenses/artistic-license-2.0.php for
 * details
 **/

package org.compsysmed.ocsana.internal.tasks.mhs;

// Java imports
import java.util.*;

// Cytoscape imports
import org.cytoscape.work.AbstractTaskFactory;
import org.cytoscape.work.TaskMonitor;
import org.cytoscape.work.TaskIterator;

import org.cytoscape.model.CyNetwork;
import org.cytoscape.model.CyEdge;
import org.cytoscape.model.CyNode;

// OCSANA imports
import org.compsysmed.ocsana.internal.algorithms.mhs.AbstractMHSAlgorithm;

import org.compsysmed.ocsana.internal.tasks.OCSANAStep;

public class MHSAlgorithmTaskFactory extends AbstractTaskFactory {
    private CyNetwork network;
    private AbstractMHSAlgorithm algorithm;
    private Iterable<List<CyEdge>> sets;

    public MHSAlgorithmTaskFactory (CyNetwork network,
                                    AbstractMHSAlgorithm algorithm,
                                    Iterable<List<CyEdge>> sets) {
        super();
        this.network = network;
        this.algorithm = algorithm;
        this.sets = sets;
    }

    public TaskIterator createTaskIterator () {
        TaskIterator tasks = new TaskIterator();
        tasks.append(new MHSAlgorithmTask(network, algorithm, sets));
        return tasks;
    }
}
