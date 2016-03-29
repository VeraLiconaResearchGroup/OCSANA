/**
 * Context for the scoring stage of OCSANA
 *
 * Copyright Vera-Licona Research Group (C) 2016
 *
 * This software is licensed under the Artistic License 2.0, see the
 * LICENSE file or
 * http://www.opensource.org/licenses/artistic-license-2.0.php for
 * details
 **/

package org.compsysmed.ocsana.internal.stages.scorestage;

// Java imports
import java.util.*;
import java.util.function.*;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

// Cytoscape imports
import org.cytoscape.model.CyColumn;
import org.cytoscape.model.CyNetwork;
import org.cytoscape.model.CyEdge;
import org.cytoscape.model.CyNode;

import org.cytoscape.work.ContainsTunables;
import org.cytoscape.work.Tunable;

import org.cytoscape.work.util.ListMultipleSelection;

// OCSANA imports
import org.compsysmed.ocsana.internal.algorithms.scoring.AbstractCISignAssignmentAlgorithm;
import org.compsysmed.ocsana.internal.algorithms.scoring.ExhaustiveSearchCISignAssignmentAlgorithm;

import org.compsysmed.ocsana.internal.stages.cistage.CIStageContext;
import org.compsysmed.ocsana.internal.stages.cistage.CIStageResults;

/**
 * Context for the scoring stage of OCSANA
 *
 * This class stores the configuration required to run the scoring
 * stage.  A populated instance will be passed to a
 * ScoringStageController which handles scoring tasks.
 **/
public class ScoringStageContext {
    public Set<CyNode> targetsToDeactivate;
    public Set<CyNode> targetsToActivate;

    // Internal data
    private CyNetwork network;
    private CIStageContext ciContext;
    private CIStageResults ciResults;
    private Collection<CyNode> targets;

    public AbstractCISignAssignmentAlgorithm ciSignAlgorithm;

    public ScoringStageContext (CyNetwork network,
                                CIStageContext ciContext,
                                CIStageResults ciResults) {
        this.network = network;
        this.ciContext = ciContext;
        this.ciResults = ciResults;

        targets = ciContext.targetNodes;
        targetsToActivate = new HashSet<>(targets);
        updateTargetsToDeactivate();

        BiFunction<CyNode, CyNode, Double> effectOnTargets = (source, target) -> ciContext.ocsanaAlg.effectOnTargetsScore(source, target);
        ciSignAlgorithm = new ExhaustiveSearchCISignAssignmentAlgorithm(effectOnTargets);
    }

    public void setTargetsToActivate (Set<CyNode> targetsToActivate) {
        this.targetsToActivate = targetsToActivate;
        updateTargetsToDeactivate();
    }

    private void updateTargetsToDeactivate () {
        targetsToDeactivate = new HashSet<>(targets);
        targetsToDeactivate.removeAll(targetsToActivate);
    }
}
