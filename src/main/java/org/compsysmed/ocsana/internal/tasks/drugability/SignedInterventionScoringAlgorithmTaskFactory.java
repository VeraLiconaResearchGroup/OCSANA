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

        if (prioritizationContext == null) {
            throw new IllegalArgumentException("Prioritization context cannot be null");
        }
        this.prioritizationContext = prioritizationContext;

        if (prioritizationResults == null) {
            throw new IllegalArgumentException("Prioritization results cannot be null");
        }
        this.prioritizationResults = prioritizationResults;
    }

    @Override
    public TaskIterator createTaskIterator () {
        TaskIterator tasks = new TaskIterator();
        tasks.append(new SignedInterventionScoringAlgorithmTask(prioritizationContext, prioritizationResults));
        return tasks;
    }
}
