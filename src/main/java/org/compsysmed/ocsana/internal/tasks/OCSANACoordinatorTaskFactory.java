/**
 * Factory for main OCSANA task
 *
 * Copyright Vera-Licona Research Group (C) 2015
 *
 * This software is licensed under the Artistic License 2.0, see the
 * LICENSE file or
 * http://www.opensource.org/licenses/artistic-license-2.0.php for
 * details
 **/

package org.compsysmed.ocsana.internal.tasks;

//Java imports

// Cytoscape imports
import org.cytoscape.task.AbstractNetworkTaskFactory;
import org.cytoscape.work.TaskIterator;
import org.cytoscape.work.TaskManager;

import org.cytoscape.model.CyNetwork;

// OCSANA imports
import org.compsysmed.ocsana.internal.ui.OCSANAResultsPanel;

public class OCSANACoordinatorTaskFactory extends AbstractNetworkTaskFactory {
    protected TaskManager taskManager;
    protected OCSANAResultsPanel resultsPanel;

    public OCSANACoordinatorTaskFactory (TaskManager taskManager,
                                         OCSANAResultsPanel resultsPanel) {
        super();
        this.taskManager = taskManager;
        this.resultsPanel = resultsPanel;
    }

    public TaskIterator createTaskIterator (CyNetwork network) {
        TaskIterator tasks = new TaskIterator();
        tasks.append(new OCSANACoordinatorTask(network, taskManager, resultsPanel));
        return tasks;
    }
}
