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
import org.compsysmed.ocsana.internal.tasks.runner.RunnerTask;

import org.compsysmed.ocsana.internal.util.context.ContextBundle;
import org.compsysmed.ocsana.internal.util.results.ResultsBundle;

public class SignedInterventionScoringAlgorithmTaskFactory
    extends AbstractTaskFactory {
    private final RunnerTask runnerTask;
    private final ContextBundle contextBundle;
    private final ResultsBundle resultsBundle;

    public SignedInterventionScoringAlgorithmTaskFactory (RunnerTask runnerTask,
                                                          ContextBundle contextBundle,
                                                          ResultsBundle resultsBundle) {
        super();

        Objects.requireNonNull(runnerTask, "Runner task cannot be null");
        this.runnerTask = runnerTask;

        Objects.requireNonNull(contextBundle, "Context bundle cannot be null");
        this.contextBundle = contextBundle;

        Objects.requireNonNull(resultsBundle, "Results bundle cannot be null");
        this.resultsBundle = resultsBundle;
    }

    @Override
    public TaskIterator createTaskIterator () {
        TaskIterator tasks = new TaskIterator();
        tasks.append(new SignedInterventionScoringAlgorithmTask(runnerTask, contextBundle, resultsBundle));
        return tasks;
    }
}
