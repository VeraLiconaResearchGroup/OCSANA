/**
 * Factory for tasks to run path-finding algorithms in OCSANA
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
import org.cytoscape.work.AbstractTaskFactory;
import org.cytoscape.work.TaskMonitor;
import org.cytoscape.work.TaskIterator;

import org.cytoscape.model.CyNode;
import org.cytoscape.model.CyNetwork;

// OCSANA imports
import org.compsysmed.ocsana.internal.tasks.OCSANAStep;

import org.compsysmed.ocsana.internal.tasks.results.OCSANAResults;

public class PathFindingAlgorithmTaskFactory extends AbstractTaskFactory {
    private OCSANAResults results;
    private OCSANAStep algStep;

    public PathFindingAlgorithmTaskFactory (OCSANAResults results,
                                            OCSANAStep algStep) {
        super();
        this.results = results;
        this.algStep = algStep;
    }

    @Override
    public TaskIterator createTaskIterator () {
        TaskIterator tasks = new TaskIterator();
        tasks.append(new PathFindingAlgorithmTask(results, algStep));
        return tasks;
    }
}