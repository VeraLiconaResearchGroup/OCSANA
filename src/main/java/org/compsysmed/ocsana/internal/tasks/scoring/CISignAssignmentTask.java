/**
 * Task to run CI-scoring algorithm
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
import java.util.function.*;

// Cytoscape imports
import org.cytoscape.work.TaskMonitor;

import org.cytoscape.model.CyNode;

// OCSANA imports
import org.compsysmed.ocsana.internal.tasks.AbstractOCSANATask;
import org.compsysmed.ocsana.internal.tasks.OCSANAStep;

import org.compsysmed.ocsana.internal.stages.generation.GenerationContext;

import org.compsysmed.ocsana.internal.stages.prioritization.PrioritizationContext;
import org.compsysmed.ocsana.internal.stages.prioritization.PrioritizationResults;

import org.compsysmed.ocsana.internal.util.results.CombinationOfInterventions;
import org.compsysmed.ocsana.internal.util.results.SignedIntervention;


public class CISignAssignmentTask
    extends AbstractOCSANATask {
    private static final OCSANAStep algStep = OCSANAStep.SCORE_PATHS;

    private final GenerationContext generationContext;
    private final PrioritizationContext prioritizationContext;
    private final PrioritizationResults prioritizationResults;

    private final CombinationOfInterventions ci;

    private Collection<SignedIntervention> signedInterventions;

    public CISignAssignmentTask (GenerationContext generationContext,
                                 PrioritizationContext prioritizationContext,
                                 PrioritizationResults prioritizationResults,
                                 CombinationOfInterventions ci) {
        super(generationContext.getNetwork());

        if (generationContext == null) {
            throw new IllegalArgumentException("Generation stage context cannot be null");
        }
        this.generationContext = generationContext;

        if (prioritizationContext == null) {
            throw new IllegalArgumentException("Prioritization stage context cannot be null");
        }
        this.prioritizationContext = prioritizationContext;

        if (prioritizationResults == null) {
            throw new IllegalArgumentException("Prioritization stage results cannot be null");
        }
        this.prioritizationResults = prioritizationResults;

        if (ci == null) {
            throw new IllegalArgumentException("CI cannot be null");
        }
        this.ci = ci;
    }

    @Override
    public void run (TaskMonitor taskMonitor) {
        signedInterventions = prioritizationContext.getCISignAlgorithm().bestInterventions(ci, prioritizationContext.getTargetsToActivate());
        prioritizationResults.setOptimalInterventionSignings(ci, signedInterventions);
    }

    @Override
    @SuppressWarnings("unchecked")
    public <T> T getResults (Class<? extends T> type) {
        if (type.isAssignableFrom(OCSANAStep.class)) {
            return (T) algStep;
        } else if (type.isAssignableFrom(Collection.class)) {
            return (T) signedInterventions;
        } else {
            throw new IllegalArgumentException("Invalid results type for scorer.");
        }
    }

    @Override
    public void cancel () {
        super.cancel();
    }
}
