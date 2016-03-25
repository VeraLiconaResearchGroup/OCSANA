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

import org.compsysmed.ocsana.internal.stages.cistage.CIStageContext;
import org.compsysmed.ocsana.internal.stages.cistage.CIStageResults;

import org.compsysmed.ocsana.internal.stages.scorestage.ScoringStageContext;

import org.compsysmed.ocsana.internal.util.results.CombinationOfInterventions;
import org.compsysmed.ocsana.internal.util.results.SignedIntervention;


public class CISignAssignmentTask
    extends AbstractOCSANATask {
    private static final OCSANAStep algStep = OCSANAStep.SCORE_PATHS;

    private CIStageContext ciContext;
    private ScoringStageContext scoringContext;

    private CombinationOfInterventions ci;
    private Set<CyNode> CINodesToActivate;

    private Collection<SignedIntervention> signedInterventions;

    public CISignAssignmentTask (CIStageContext ciContext,
                                 ScoringStageContext scoringContext,
                                 CombinationOfInterventions ci) {
        super(ciContext.getNetwork());

        this.ciContext = ciContext;
        this.scoringContext = scoringContext;

        this.ci = ci;
    }

    @Override
    public void run (TaskMonitor taskMonitor) {
        signedInterventions = scoringContext.ciSignAlgorithm.bestInterventions(ci, scoringContext.targetsToActivate);
        ci.setOptimalSignings(signedInterventions);
        ci.setTargetsToActivate(scoringContext.targetsToActivate);
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
