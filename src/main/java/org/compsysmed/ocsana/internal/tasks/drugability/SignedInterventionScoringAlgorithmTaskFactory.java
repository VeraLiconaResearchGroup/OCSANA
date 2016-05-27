/**
 * Factory for tasks to run Si scoring algorithms in OCSANA
 *
 * Copyright Vera-Licona Research Group (C) 2016
 *
 * This software is licensed under the Artistic License 2.0, see the
 * LICENSE file or
 * http://www.opensource.org/licenses/artistic-license-2.0.php for
 * details
 **/

package org.compsysmed.ocsana.internal.tasks.drugability;

// Java imports

// Cytoscape imports
import org.cytoscape.work.AbstractTaskFactory;
import org.cytoscape.work.TaskIterator;

import java.util.Objects;

// OCSANA imports
import org.compsysmed.ocsana.internal.stages.prioritization.PrioritizationContext;
import org.compsysmed.ocsana.internal.stages.prioritization.PrioritizationResults;

public class SignedInterventionScoringAlgorithmTaskFactory
    extends AbstractTaskFactory {
    private final PrioritizationContext prioritizationContext;
    private final PrioritizationResults prioritizationResults;

    public SignedInterventionScoringAlgorithmTaskFactory (PrioritizationContext prioritizationContext,
                                                          PrioritizationResults prioritizationResults) {
        super();

        Objects.requireNonNull(prioritizationContext, "Prioritization context cannot be null");
        this.prioritizationContext = prioritizationContext;

        Objects.requireNonNull(prioritizationResults, "Prioritization results cannot be null");
        this.prioritizationResults = prioritizationResults;
    }

    @Override
    public TaskIterator createTaskIterator () {
        TaskIterator tasks = new TaskIterator();
        tasks.append(new SignedInterventionScoringAlgorithmTask(prioritizationContext, prioritizationResults));
        return tasks;
    }
}
