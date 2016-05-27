/**
 * Task to run SI scoring algorithm in OCSANA
 *
 * Copyright Vera-Licona Research Group (C) 2016
 *
 * This software is licensed under the Artistic License 2.0, see the
 * LICENSE file or
 * http://www.opensource.org/licenses/artistic-license-2.0.php for
 * details
 **/

package org.compsysmed.ocsana.internal.tasks.drugability;

// Cytoscape imports
import org.cytoscape.work.TaskMonitor;

// OCSANA imports
import org.compsysmed.ocsana.internal.tasks.AbstractOCSANATask;
import org.compsysmed.ocsana.internal.tasks.OCSANAStep;

import java.util.Objects;

import org.compsysmed.ocsana.internal.stages.prioritization.PrioritizationContext;
import org.compsysmed.ocsana.internal.stages.prioritization.PrioritizationResults;

import org.compsysmed.ocsana.internal.util.results.CombinationOfInterventions;
import org.compsysmed.ocsana.internal.util.results.SignedIntervention;

public class SignedInterventionScoringAlgorithmTask
    extends AbstractOCSANATask {
    private static final OCSANAStep algStep = OCSANAStep.SCORE_SIGNED_INTERVENTIONS;

    private final PrioritizationContext prioritizationContext;
    private final PrioritizationResults prioritizationResults;

    public SignedInterventionScoringAlgorithmTask (PrioritizationContext prioritizationContext,
                                                   PrioritizationResults prioritizationResults) {
        super(prioritizationContext.getGenerationContext().getNetwork());

        Objects.requireNonNull(prioritizationContext, "Prioritization context cannot be null");
        this.prioritizationContext = prioritizationContext;

        Objects.requireNonNull(prioritizationResults, "Prioritization results cannot be null");
        this.prioritizationResults = prioritizationResults;
    }

    @Override
    public void run (TaskMonitor taskMonitor) {
        taskMonitor.setTitle("Scoring signed interventions");

        Long preTime = System.nanoTime();
        for (CombinationOfInterventions ci: prioritizationContext.getGenerationResults().CIs) {
            if (cancelled) {
                break;
            }

            for (SignedIntervention si: prioritizationResults.getOptimalInterventionSignings(ci)) {
                if (cancelled) {
                    break;
                }

                Double siScore = prioritizationContext.getSIScoringAlgorithm().computePriorityScore(si);
                prioritizationResults.setSignedInterventionScore(si, siScore);
            }
        }
        Long postTime = System.nanoTime();

        Double signingTime = (postTime - preTime) / 1E9;

        taskMonitor.setStatusMessage(String.format("Scored signed interventions in %f s.", signingTime));
    }

    @Override
    @SuppressWarnings("unchecked")
    public <T> T getResults (Class<? extends T> type) {
        if (type.isAssignableFrom(OCSANAStep.class)) {
            return (T) algStep;
        } else {
            return (T) prioritizationResults;
        }
    }

    @Override
    public void cancel () {
        super.cancel();
        prioritizationContext.getCISignAlgorithm().cancel();
    }
}
