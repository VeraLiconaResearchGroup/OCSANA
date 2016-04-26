/**
 * Factory for tasks to run CI stages in OCSANA
 *
 * Copyright Vera-Licona Research Group (C) 2016
 *
 * This software is licensed under the Artistic License 2.0, see the
 * LICENSE file or
 * http://www.opensource.org/licenses/artistic-license-2.0.php for
 * details
 **/

package org.compsysmed.ocsana.internal.stages.generation;

// Java imports
import java.util.*;

// Cytoscape imports
import org.cytoscape.work.AbstractTaskFactory;
import org.cytoscape.work.TaskIterator;
import org.cytoscape.work.TaskManager;
import org.cytoscape.work.TaskObserver;

// OCSANA imports
import org.compsysmed.ocsana.internal.stages.generation.GenerationContext;
import org.compsysmed.ocsana.internal.stages.generation.GenerationResults;

import org.compsysmed.ocsana.internal.ui.results.OCSANAResultsPanel;

public class GenerationStageRunnerTaskFactory extends AbstractTaskFactory {
    private TaskManager<?, ?> taskManager;
    private TaskObserver observer;
    private GenerationContext context;
    private OCSANAResultsPanel resultsPanel;

    public GenerationStageRunnerTaskFactory (TaskManager<?, ?> taskManager,
                                     TaskObserver observer,
                                     GenerationContext context,
                                     OCSANAResultsPanel resultsPanel) {
        super();
        this.taskManager = taskManager;
        this.observer = observer;
        this.context = context;
        this.resultsPanel = resultsPanel;
    }

    @Override
    public TaskIterator createTaskIterator () {
        TaskIterator tasks = new TaskIterator();
        tasks.append(new GenerationStageRunnerTask(taskManager, observer, context, resultsPanel));
        return tasks;
    }
}
