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
import org.compsysmed.ocsana.internal.algorithms.scoring.CISignTestingAlgorithm.SignedIntervention;

import org.compsysmed.ocsana.internal.tasks.AbstractOCSANATask;
import org.compsysmed.ocsana.internal.tasks.OCSANAStep;

import org.compsysmed.ocsana.internal.stages.cistage.CIStageContext;
import org.compsysmed.ocsana.internal.stages.cistage.CIStageResults;

public class CIScoringTask
    extends AbstractOCSANATask {
    private static final OCSANAStep algStep = OCSANAStep.SCORE_PATHS;

    private CIStageContext ciContext;
    private Set<CyNode> CI;
    private Set<CyNode> targetsToActivate;

    private Set<CyNode> CINodesToActivate;

    private Collection<SignedIntervention> signedInterventions;

    public CIScoringTask (CIStageContext ciContext,
                          Set<CyNode> CI,
                          Set<CyNode> targetsToActivate) {
        super(ciContext.getNetwork());
        this.ciContext = ciContext;
        this.CI = CI;
        this.targetsToActivate = targetsToActivate;

        assert(ciContext.nodeSetSelecter.getTargetNodes().containsAll(targetsToActivate));
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

        CISignTestingAlgorithm testAlg = new CISignTestingAlgorithm(CI, ciContext.nodeSetSelecter.getTargetNodeSet(), signedEffectOnTargets);
        signedInterventions = testAlg.bestInterventions();
        SignedIntervention exampleIntervention = signedInterventions.stream().findFirst().get();
        System.out.println(String.format("Found %d best interventions; example: %s", signedInterventions.size(), exampleIntervention));
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
