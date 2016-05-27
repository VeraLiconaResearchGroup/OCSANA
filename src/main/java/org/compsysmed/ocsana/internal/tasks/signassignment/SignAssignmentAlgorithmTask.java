/**
 * Task to run CI sign assignment algorithm in OCSANA
 *
 * Copyright Vera-Licona Research Group (C) 2016
 *
 * This software is licensed under the Artistic License 2.0, see the
 * LICENSE file or
 * http://www.opensource.org/licenses/artistic-license-2.0.php for
 * details
 **/

package org.compsysmed.ocsana.internal.tasks.signassignment;

// Java imports
import java.util.*;

// Cytoscape imports
import org.cytoscape.work.TaskMonitor;

// OCSANA imports
import org.compsysmed.ocsana.internal.tasks.AbstractOCSANATask;
import org.compsysmed.ocsana.internal.tasks.OCSANAStep;

import org.compsysmed.ocsana.internal.stages.prioritization.PrioritizationContext;
import org.compsysmed.ocsana.internal.stages.prioritization.PrioritizationResults;

import org.compsysmed.ocsana.internal.util.results.CombinationOfInterventions;
import org.compsysmed.ocsana.internal.util.results.SignedIntervention;

public class SignAssignmentAlgorithmTask
    extends AbstractOCSANATask {
    private static final OCSANAStep algStep = OCSANAStep.ASSIGN_CI_SIGNS;

    private final PrioritizationContext prioritizationContext;
    private final PrioritizationResults prioritizationResults;

    public SignAssignmentAlgorithmTask (PrioritizationContext prioritizationContext,
                                        PrioritizationResults prioritizationResults) {
        super(prioritizationContext.getGenerationContext().getNetwork());

        Objects.requireNonNull(prioritizationContext, "Prioritization context cannot be null");
        this.prioritizationContext = prioritizationContext;

        Objects.requireNonNull(prioritizationResults, "Prioritization results cannot be null");
        this.prioritizationResults = prioritizationResults;
    }

    @Override
    public void run (TaskMonitor taskMonitor) {
        taskMonitor.setTitle("Computing sign assignments for CIs");

        Long preTime = System.nanoTime();

        for (CombinationOfInterventions ci: prioritizationContext.getGenerationResults().CIs) {
            if (cancelled) {
                break;
            }

            Collection<SignedIntervention> optimalSignings = prioritizationContext.getCISignAlgorithm().bestInterventions(ci, prioritizationContext.getTargetsToActivate());
            prioritizationResults.setOptimalInterventionSignings(ci, optimalSignings);
        }
        Long postTime = System.nanoTime();

        Double signingTime = (postTime - preTime) / 1E9;

        taskMonitor.setStatusMessage(String.format("Found optimal sign assignments in %f s.", signingTime));
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
