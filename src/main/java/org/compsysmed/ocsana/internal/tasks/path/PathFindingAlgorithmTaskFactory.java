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
import org.cytoscape.work.TaskIterator;

// OCSANA imports
import org.compsysmed.ocsana.internal.tasks.OCSANAStep;

import org.compsysmed.ocsana.internal.stages.cistage.CIStageContext;
import org.compsysmed.ocsana.internal.stages.cistage.CIStageResults;

public class PathFindingAlgorithmTaskFactory extends AbstractTaskFactory {
    private CIStageContext context;
    private CIStageResults results;
    private OCSANAStep algStep;

    public PathFindingAlgorithmTaskFactory (CIStageContext context,
                                            CIStageResults results,
                                            OCSANAStep algStep) {
        super();
        this.context = context;
        this.results = results;
        this.algStep = algStep;
    }

    @Override
    public TaskIterator createTaskIterator () {
        TaskIterator tasks = new TaskIterator();
        tasks.append(new PathFindingAlgorithmTask(context, results, algStep));
        return tasks;
    }
}
