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

import org.compsysmed.ocsana.internal.util.context.ContextBundle;
import org.compsysmed.ocsana.internal.util.results.ResultsBundle;
import org.compsysmed.ocsana.internal.util.results.CombinationOfInterventions;
import org.compsysmed.ocsana.internal.util.results.SignedIntervention;

public class SignedInterventionScoringAlgorithmTask
    extends AbstractOCSANATask {
    private static final OCSANAStep algStep = OCSANAStep.SCORE_SIGNED_INTERVENTIONS;

    private final ContextBundle contextBundle;
    private final ResultsBundle resultsBundle;

    public SignedInterventionScoringAlgorithmTask (ContextBundle contextBundle,
                                                   ResultsBundle resultsBundle) {
        super(contextBundle.getNetwork());

        Objects.requireNonNull(contextBundle, "Context bundle cannot be null");
        this.contextBundle = contextBundle;

        Objects.requireNonNull(resultsBundle, "Results bundle cannot be null");
        this.resultsBundle = resultsBundle;
    }

    @Override
    public void run (TaskMonitor taskMonitor) {
        taskMonitor.setTitle("Scoring signed interventions");

        Long preTime = System.nanoTime();
        for (CombinationOfInterventions ci: resultsBundle.getCIs()) {
            if (cancelled) {
                break;
            }

            for (SignedIntervention si: resultsBundle.getOptimalInterventionSignings(ci)) {
                if (cancelled) {
                    break;
                }

                Double siScore = contextBundle.getSIScoringAlgorithm().computePriorityScore(si);
                resultsBundle.setSignedInterventionScore(si, siScore);
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
            return (T) resultsBundle;
        }
    }

    @Override
    public void cancel () {
        super.cancel();
        contextBundle.getCISignAlgorithm().cancel();
    }
}
