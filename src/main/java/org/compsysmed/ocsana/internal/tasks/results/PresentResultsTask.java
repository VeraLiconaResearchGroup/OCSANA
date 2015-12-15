/**
 * Task to present results to user in OCSANA
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
import org.cytoscape.work.TaskMonitor;
import org.cytoscape.model.CyNetwork;
import org.cytoscape.model.CyEdge;
import org.cytoscape.model.CyNode;

// OCSANA imports
import org.compsysmed.ocsana.internal.tasks.AbstractOCSANATask;
import org.compsysmed.ocsana.internal.tasks.OCSANAStep;

import org.compsysmed.ocsana.internal.ui.OCSANAResultsPanel;

public class PresentResultsTask extends AbstractOCSANATask {
    private static final OCSANAStep algStep = OCSANAStep.PRESENT_RESULTS;
    private OCSANAResults results;
    private OCSANAResultsPanel resultsPanel;

    public PresentResultsTask (OCSANAResults results,
                               OCSANAResultsPanel resultsPanel) {
        super(results.network);
        this.results = results;
        this.resultsPanel = resultsPanel;
    }

    @Override
    public void run (TaskMonitor taskMonitor) {
        taskMonitor.setTitle("Results");

        taskMonitor.setStatusMessage("Generating results report.");

        resultsPanel.updateResults(results);
    }

    @Override
    public <T> T getResults (Class<? extends T> type) {
        if (type.isAssignableFrom(OCSANAStep.class)) {
            return (T) algStep;
        } else {
            throw new IllegalArgumentException("Invalid results type for presenter.");
        }
    }
}
