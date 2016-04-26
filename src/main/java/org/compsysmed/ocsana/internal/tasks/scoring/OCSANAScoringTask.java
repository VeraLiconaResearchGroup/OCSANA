/**
 * Task to run OCSANA path-scoring algorithm
 *
 * Copyright Vera-Licona Research Group (C) 2015
 *
 * This software is licensed under the Artistic License 2.0, see the
 * LICENSE file or
 * http://www.opensource.org/licenses/artistic-license-2.0.php for
 * details
 **/

package org.compsysmed.ocsana.internal.tasks.scoring;

import java.util.function.Predicate;

// Cytoscape imports
import org.cytoscape.work.TaskMonitor;

import org.cytoscape.model.CyEdge;

// OCSANA imports
import org.compsysmed.ocsana.internal.tasks.AbstractOCSANATask;
import org.compsysmed.ocsana.internal.tasks.OCSANAStep;

import org.compsysmed.ocsana.internal.stages.generation.GenerationContext;
import org.compsysmed.ocsana.internal.stages.generation.GenerationResults;

public class OCSANAScoringTask extends AbstractOCSANATask {
    private static final OCSANAStep algStep = OCSANAStep.SCORE_PATHS;

    private GenerationContext context;
    private GenerationResults results;

    public OCSANAScoringTask (GenerationContext context,
                              GenerationResults results) {
        super(context.getNetwork());
        this.context = context;
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

        Predicate<CyEdge> inhibitionEdgeTester = (CyEdge edge) -> context.edgeProcessor.edgeIsInhibition(edge);

        taskMonitor.setTitle("OCSANA scoring");

        taskMonitor.setStatusMessage("Computing OCSANA scores.");

        Long OCSANAPreTime = System.nanoTime();
        context.ocsanaAlg.computeScores(results.pathsToTargets, results.pathsToOffTargets, inhibitionEdgeTester);
        Long OCSANAPostTime = System.nanoTime();

        Double OCSANARunTime = (OCSANAPostTime - OCSANAPreTime) / 1E9;
        results.OCSANAScoringExecutionSeconds = OCSANARunTime;
        taskMonitor.setStatusMessage(String.format("Computed OCSANA scores in %fs.", OCSANARunTime));
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
        context.ocsanaAlg.cancel();
        results.OCSANAScoringCanceled = true;
    }
}
