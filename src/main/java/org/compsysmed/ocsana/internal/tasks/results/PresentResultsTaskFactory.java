/**
 * Factory for tasks to present results to user in OCSANA
 *
 * Copyright Vera-Licona Research Group (C) 2015
 *
 * This software is licensed under the Artistic License 2.0, see the
 * LICENSE file or
 * http://www.opensource.org/licenses/artistic-license-2.0.php for
 * details
 **/

package org.compsysmed.ocsana.internal.tasks.results;

// Java imports
import java.util.*;

// Cytoscape imports
import org.cytoscape.work.AbstractTaskFactory;
import org.cytoscape.work.TaskIterator;

// OCSANA imports
import org.compsysmed.ocsana.internal.ui.results.OCSANAResultsPanel;

import org.compsysmed.ocsana.internal.util.results.OCSANAResults;

public class PresentResultsTaskFactory extends AbstractTaskFactory {
    private OCSANAResults results;
    private OCSANAResultsPanel resultsPanel;

    public PresentResultsTaskFactory (OCSANAResults results,
                                      OCSANAResultsPanel resultsPanel) {
        this.results = results;
        this.resultsPanel = resultsPanel;
    }

    @Override
    public TaskIterator createTaskIterator () {
        TaskIterator tasks = new TaskIterator();
        tasks.append(new PresentResultsTask(results, resultsPanel));
        return tasks;
    }
}
