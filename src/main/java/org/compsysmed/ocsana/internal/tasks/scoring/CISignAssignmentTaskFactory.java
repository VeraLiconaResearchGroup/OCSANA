/**
 * Factory for tasks to run CI-scoring algorithm
 *
 * Copyright Vera-Licona Research Group (C) 2016
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

// OCSANA imports
import org.compsysmed.ocsana.internal.stages.generation.GenerationContext;
import org.compsysmed.ocsana.internal.stages.prioritization.PrioritizationContext;

import org.compsysmed.ocsana.internal.util.results.CombinationOfInterventions;

public class CISignAssignmentTaskFactory
    extends AbstractTaskFactory {
    private GenerationContext ciContext;
    private PrioritizationContext scoringContext;
    private CombinationOfInterventions ci;

    public CISignAssignmentTaskFactory (GenerationContext ciContext,
                                        PrioritizationContext scoringContext,
                                        CombinationOfInterventions ci) {
        this.ciContext = ciContext;
        this.scoringContext = scoringContext;
        this.ci = ci;
    }

    @Override
    public TaskIterator createTaskIterator () {
        TaskIterator tasks = new TaskIterator();
        tasks.append(new CISignAssignmentTask(ciContext, scoringContext, ci));
        return tasks;
    }
}
