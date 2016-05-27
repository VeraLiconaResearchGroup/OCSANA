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

import java.util.Objects;
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

    private GenerationContext generationContext;
    private GenerationResults generationResults;

    public OCSANAScoringTask (GenerationContext generationContext,
                              GenerationResults generationResults) {
        super(generationContext.getNetwork());
        this.generationContext = generationContext;
        this.generationResults = generationResults;
    }

    @Override
    public void run (TaskMonitor taskMonitor) {
        if (generationResults.pathFindingCanceled) {
            return;
        }

        Objects.requireNonNull(generationResults.pathsToTargets, "Paths to targets have not been computed");
        Objects.requireNonNull(generationResults.pathsToOffTargets, "Paths to off-targets have not been computed");

        Predicate<CyEdge> inhibitionEdgeTester = (CyEdge edge) -> generationContext.getEdgeProcessor().edgeIsInhibition(edge);

        taskMonitor.setTitle("OCSANA scoring");

        taskMonitor.setStatusMessage("Computing OCSANA scores.");

        Long OCSANAPreTime = System.nanoTime();
        generationContext.getOCSANAAlgorithm().computeScores(generationResults.pathsToTargets, generationResults.pathsToOffTargets, inhibitionEdgeTester);
        Long OCSANAPostTime = System.nanoTime();

        Double OCSANARunTime = (OCSANAPostTime - OCSANAPreTime) / 1E9;
        generationResults.OCSANAScoringExecutionSeconds = OCSANARunTime;
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
        generationContext.getOCSANAAlgorithm().cancel();
        generationResults.OCSANAScoringCanceled = true;
    }
}
