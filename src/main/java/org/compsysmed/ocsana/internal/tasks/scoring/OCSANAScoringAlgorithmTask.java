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
import org.cytoscape.task.AbstractNetworkTask;
import org.cytoscape.work.TaskMonitor;

import org.cytoscape.model.CyNetwork;
import org.cytoscape.model.CyNode;
import org.cytoscape.model.CyEdge;

// OCSANA imports
import org.compsysmed.ocsana.internal.tasks.AbstractOCSANATask;
import org.compsysmed.ocsana.internal.tasks.OCSANAStep;

import org.compsysmed.ocsana.internal.tasks.results.OCSANAResults;

public class OCSANAScoringAlgorithmTask extends AbstractOCSANATask {
    private static final OCSANAStep algStep = OCSANAStep.SCORE_PATHS;

    private OCSANAResults results;

    public OCSANAScoringAlgorithmTask (OCSANAResults results) {
        super(results.network);
        this.results = results;
    }

    @Override
    public void run (TaskMonitor taskMonitor) {
        if (results.pathFindingCanceled) {
            return;
        }

        if (!results.ocsanaAlg.computeScores) {
            return;
        }

        if (results.pathsToTargets == null || results.pathsToOffTargets == null) {
            throw new IllegalStateException("Paths have not been computed.");
        }

        Predicate<CyEdge> inhibitionEdgeTester = (CyEdge edge) -> results.edgeProcessor.edgeIsInhibition(edge);

        taskMonitor.setTitle("OCSANA scoring");

        taskMonitor.setStatusMessage("Computing OCSANA scores.");

        Long preTime = System.nanoTime();
        results.ocsanaScores = results.ocsanaAlg.computeScores(results.pathsToTargets, results.pathsToOffTargets, inhibitionEdgeTester);
        Long postTime = System.nanoTime();

        Double runTime = (postTime - preTime) / 1E9;
        taskMonitor.setStatusMessage(String.format("Scored nodes in %fs.", runTime));

        results.scoringExecutionSeconds = runTime;
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
        results.scoringCanceled = true;
    }
}
