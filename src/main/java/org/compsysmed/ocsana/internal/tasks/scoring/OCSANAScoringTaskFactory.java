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

// Cytoscape imports
import org.cytoscape.work.AbstractTaskFactory;
import org.cytoscape.work.TaskIterator;

import org.cytoscape.model.CyNetwork;

// OCSANA imports
import org.compsysmed.ocsana.internal.stages.generation.GenerationContext;
import org.compsysmed.ocsana.internal.stages.generation.GenerationResults;

public class OCSANAScoringTaskFactory extends AbstractTaskFactory {
    private GenerationContext context;
    private GenerationResults results;

    public OCSANAScoringTaskFactory (GenerationContext context,
                                     GenerationResults results) {
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
