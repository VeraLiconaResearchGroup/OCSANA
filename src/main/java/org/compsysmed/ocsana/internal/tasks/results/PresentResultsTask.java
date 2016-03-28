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

// Cytoscape imports
import org.cytoscape.work.TaskMonitor;

// OCSANA imports
import org.compsysmed.ocsana.internal.tasks.AbstractOCSANATask;
import org.compsysmed.ocsana.internal.tasks.OCSANAStep;

import org.compsysmed.ocsana.internal.ui.results.OCSANAResultsPanel;

import org.compsysmed.ocsana.internal.stages.cistage.CIStageContext;
import org.compsysmed.ocsana.internal.stages.cistage.CIStageResults;

public class PresentResultsTask extends AbstractOCSANATask {
    private static final OCSANAStep algStep = OCSANAStep.PRESENT_RESULTS;
    private CIStageContext ciContext;
    private CIStageResults ciResults;
    private OCSANAResultsPanel resultsPanel;

    public PresentResultsTask (CIStageContext ciContext,
                               CIStageResults ciResults,
                               OCSANAResultsPanel resultsPanel) {
        super(ciContext.getNetwork());
        this.ciContext = ciContext;
        this.ciResults = ciResults;
        this.resultsPanel = resultsPanel;
    }

    @Override
    public void run (TaskMonitor taskMonitor) {
        taskMonitor.setTitle("Results");

        taskMonitor.setStatusMessage("Generating results report.");

        resultsPanel.updateResults(ciContext, ciResults);
    }

    @Override
    @SuppressWarnings("unchecked")
    public <T> T getResults (Class<? extends T> type) {
        if (type.isAssignableFrom(OCSANAStep.class)) {
            return (T) algStep;
        } else {
            throw new IllegalArgumentException("Invalid results type for presenter.");
        }
    }
}
