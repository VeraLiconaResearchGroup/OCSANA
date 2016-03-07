/**
 * Factory for tasks to run OCSANA path-scoring algorithm
 *
 * Copyright Vera-Licona Research Group (C) 2015
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
import org.cytoscape.work.TaskIterator;

import org.cytoscape.model.CyNetwork;

// OCSANA imports
import org.compsysmed.ocsana.internal.stages.cistage.CIStageContext;
import org.compsysmed.ocsana.internal.stages.cistage.CIStageResults;

public class OCSANAScoringTaskFactory extends AbstractTaskFactory {
    private CyNetwork network;

    private CIStageContext context;
    private CIStageResults results;

    public OCSANAScoringTaskFactory (CIStageContext context,
                                     CIStageResults results) {
        this.context = context;
        this.results = results;
    }

    @Override
    public TaskIterator createTaskIterator () {
        TaskIterator tasks = new TaskIterator();
        tasks.append(new OCSANAScoringTask(context, results));
        return tasks;
    }
}
