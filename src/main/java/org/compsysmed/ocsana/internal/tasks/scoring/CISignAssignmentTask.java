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
import org.compsysmed.ocsana.internal.algorithms.scoring.CISignTestingAlgorithm;

import org.compsysmed.ocsana.internal.tasks.AbstractOCSANATask;
import org.compsysmed.ocsana.internal.tasks.OCSANAStep;

import org.compsysmed.ocsana.internal.stages.cistage.CIStageContext;
import org.compsysmed.ocsana.internal.stages.cistage.CIStageResults;

import org.compsysmed.ocsana.internal.util.results.CombinationOfInterventions;
import org.compsysmed.ocsana.internal.util.results.SignedIntervention;


public class CISignAssignmentTask
    extends AbstractOCSANATask {
    private static final OCSANAStep algStep = OCSANAStep.SCORE_PATHS;

    private CIStageContext ciContext;
    private CombinationOfInterventions ci;
    private Set<CyNode> CINodesToActivate;

    private Set<CyNode> targetsToActivate;


    private Collection<SignedIntervention> signedInterventions;

    public CISignAssignmentTask (CIStageContext ciContext,
                                 CombinationOfInterventions ci,
                                 Set<CyNode> targetsToActivate) {
        super(ciContext.getNetwork());

        if (!ciContext.nodeSetSelecter.getTargetNodes().containsAll(targetsToActivate)) {
            throw new IllegalArgumentException("Specified target nodes are not targets");
        }

        this.ciContext = ciContext;
        this.ci = ci;
        this.targetsToActivate = targetsToActivate;
    }

    @Override
    public void run (TaskMonitor taskMonitor) {
        BiFunction<CyNode, CyNode, Double> signedEffectOnTargets = (source, target) -> {
            Double effectValue = ciContext.ocsanaAlg.effectOnTargetsScore(source, target);
            if (!targetsToActivate.contains(target)) {
                effectValue = -effectValue;
            }
            return effectValue;
        };

        CISignTestingAlgorithm signingAlg = new CISignTestingAlgorithm(ci, ciContext.nodeSetSelecter.getTargetNodeSet(), signedEffectOnTargets);
        signedInterventions = signingAlg.bestInterventions();
        ci.setOptimalSignings(signedInterventions);
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
