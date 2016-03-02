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

import org.compsysmed.ocsana.internal.stages.cistage.CIStageContext;
import org.compsysmed.ocsana.internal.stages.cistage.CIStageResults;

public class PresentResultsTaskFactory extends AbstractTaskFactory {
    private CIStageContext ciContext;
    private CIStageResults ciResults;

    private OCSANAResultsPanel resultsPanel;

    public PresentResultsTaskFactory (CIStageContext ciContext,
                                      CIStageResults ciResults,
                                      OCSANAResultsPanel resultsPanel) {
        this.ciContext = ciContext;
        this.ciResults = ciResults;
        this.resultsPanel = resultsPanel;
    }

    @Override
    public TaskIterator createTaskIterator () {
        TaskIterator tasks = new TaskIterator();
        tasks.append(new PresentResultsTask(ciContext, ciResults, resultsPanel));
        return tasks;
    }
}
