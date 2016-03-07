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
import org.compsysmed.ocsana.internal.stages.cistage.CIStageContext;
import org.compsysmed.ocsana.internal.stages.cistage.CIStageResults;

import org.compsysmed.ocsana.internal.util.tunables.ListTargetsToActivateSelecter;

/**
 * Context for the scoring stage of OCSANA
 *
 * This class stores the configuration required to run the scoring
 * stage.  A populated instance will be passed to a
 * ScoringStageController which handles scoring tasks.
 **/
public class ScoringStageContext {
    // User options as Tunables
    @ContainsTunables
    public ListTargetsToActivateSelecter targetsSelecter;

    // Internal data
    private CyNetwork network;
    private CIStageContext ciContext;
    private CIStageResults ciResults;

    public ScoringStageContext (CyNetwork network,
                                CIStageContext ciContext,
                                CIStageResults ciResults) {
        this.network = network;
        this.ciContext = ciContext;
        this.ciResults = ciResults;

        Collection<CyNode> targets = ciContext.nodeSetSelecter.getTargetNodes();
        CyColumn nodeNameColumn = ciContext.getNodeNameColumn();
        targetsSelecter = new ListTargetsToActivateSelecter(network, targets, nodeNameColumn);
    }

    public Set<CyNode> targetsToActivate () {
        return new HashSet<>(targetsSelecter.getTargetsToActivate());
    }

    public Set<CyNode> targetsToDeActivate () {
        Set<CyNode> result = targetsToActivate();
        result.removeAll(targetsToActivate());
        return result;
    }
}
