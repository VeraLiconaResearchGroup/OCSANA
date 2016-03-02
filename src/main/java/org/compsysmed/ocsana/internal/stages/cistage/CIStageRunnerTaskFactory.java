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

package org.compsysmed.ocsana.internal.stages.cistage;

// Java imports
import java.util.*;

// Cytoscape imports
import org.cytoscape.work.AbstractTaskFactory;
import org.cytoscape.work.TaskIterator;
import org.cytoscape.work.TaskManager;
import org.cytoscape.work.TaskObserver;

// OCSANA imports
import org.compsysmed.ocsana.internal.stages.cistage.CIStageContext;
import org.compsysmed.ocsana.internal.stages.cistage.CIStageResults;

public class CIStageRunnerTaskFactory extends AbstractTaskFactory {
    private TaskManager<?, ?> taskManager;
    private TaskObserver observer;
    private CIStageContext context;

    public CIStageRunnerTaskFactory (TaskManager<?, ?> taskManager,
                                     TaskObserver observer,
                                     CIStageContext context) {
        super();
        this.taskManager = taskManager;
        this.observer = observer;
        this.context = context;
    }

    @Override
    public TaskIterator createTaskIterator () {
        TaskIterator tasks = new TaskIterator();
        tasks.append(new CIStageRunnerTask(taskManager, observer, context));
        return tasks;
    }
}
