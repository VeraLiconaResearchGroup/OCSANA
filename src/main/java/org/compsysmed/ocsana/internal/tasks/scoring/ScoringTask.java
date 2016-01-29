/**
 * Task to run scoring algorithm in OCSANA
 *
 * Copyright Vera-Licona Research Group (C) 2015
 *
 * This software is licensed under the Artistic License 2.0, see the
 * LICENSE file or
 * http://www.opensource.org/licenses/artistic-license-2.0.php for
 * details
 **/

package org.compsysmed.ocsana.internal.tasks.scoring;

// Java imports
import java.util.*;
import java.util.function.Predicate;

// Cytoscape imports
import org.cytoscape.work.TaskMonitor;

import org.cytoscape.model.CyEdge;

// OCSANA imports
import org.compsysmed.ocsana.internal.tasks.AbstractOCSANATask;
import org.compsysmed.ocsana.internal.tasks.OCSANAStep;

import org.compsysmed.ocsana.internal.util.results.OCSANAResults;

public class ScoringTask extends AbstractOCSANATask {
    private static final OCSANAStep algStep = OCSANAStep.SCORE_PATHS;

    private OCSANAResults results;

    public ScoringTask (OCSANAResults results) {
        super(results.network);
        this.results = results;
    }

    @Override
    public void run (TaskMonitor taskMonitor) {
        if (results.pathFindingCanceled) {
            return;
        }

        if (results.pathsToTargets == null || results.pathsToOffTargets == null) {
            throw new IllegalStateException("Paths have not been computed.");
        }

        Predicate<CyEdge> inhibitionEdgeTester = (CyEdge edge) -> results.edgeProcessor.edgeIsInhibition(edge);

        taskMonitor.setTitle("OCSANA scoring");

        taskMonitor.setStatusMessage("Computing OCSANA scores.");

        Long OCSANAPreTime = System.nanoTime();
        results.ocsanaAlg.computeScores(results.pathsToTargets, results.pathsToOffTargets, inhibitionEdgeTester);
        Long OCSANAPostTime = System.nanoTime();

        Double OCSANARunTime = (OCSANAPostTime - OCSANAPreTime) / 1E9;
        results.OCSANAScoringExecutionSeconds = OCSANARunTime;
        taskMonitor.setStatusMessage(String.format("Computed OCSANA scores in %fs.", OCSANARunTime));

        if (results.drugBankAlg.computeScores) {
            taskMonitor.setStatusMessage("Computing DrugBank scores.");

            Long DBPreTime = System.nanoTime();
            results.drugBankAlg.computeScores();
            Long DBPostTime = System.nanoTime();

            Double DBRunTime = (DBPostTime - DBPreTime) / 1E9;
            results.drugBankScoringExecutionSeconds = DBRunTime;
            taskMonitor.setStatusMessage(String.format("computed DrugBank scores in %fs.", DBRunTime));
        }
    }

    @Override
    @SuppressWarnings("unchecked")
    public <T> T getResults (Class<? extends T> type) {
        if (type.isAssignableFrom(OCSANAStep.class)) {
            return (T) algStep;
        } else {
            throw new IllegalArgumentException("Invalid results type for scorer.");
        }
    }

    @Override
    public void cancel () {
        super.cancel();
        results.ocsanaAlg.cancel();
    }
}
