/**
 * Interface handler for node selection
 *
 * Copyright Vera-Licona Research Group (C) 2015
 * @author Andrew Gainer-Dewar, Ph.D. <andrew.gainer.dewar@gmail.com>
 *
 * This software is licensed under the Artistic License 2.0, see the
 * LICENSE file or
 * http://www.opensource.org/licenses/artistic-license-2.0.php for
 * details
 **/

package org.compsysmed.ocsana.internal.tasks.nodeselection;

// Java imports
import java.util.*;

// Cytoscape imports
import org.cytoscape.work.Tunable;
import org.cytoscape.work.util.ListMultipleSelection;

import org.cytoscape.model.CyNode;
import org.cytoscape.model.CyNetwork;

// OCSANA imports

/**
 * Interface handler for node selection
 *
 * @param network  the network to compute on
 **/

public class NodeSetSelecter {
    public static final String configGroup = "1: Select nodes";
    public static final float configGravity = 1;

    @Tunable(description = "Source nodes",
             groups = {configGroup},
             gravity = configGravity)
    public ListMultipleSelection<CyNode> sourceNodes;

    @Tunable(description = "Target nodes",
             groups = {configGroup},
             gravity = configGravity)
    public ListMultipleSelection<CyNode> targetNodes;

    @Tunable(description = "Off-target nodes",
             groups = {configGroup},
             gravity = configGravity)
    public ListMultipleSelection<CyNode> offTargetNodes;

    private CyNetwork network;

    public NodeSetSelecter (CyNetwork network) {
        this.network = network;

        List<CyNode> nodes = network.getNodeList();
        sourceNodes = new ListMultipleSelection<>(nodes);
        targetNodes = new ListMultipleSelection<>(nodes);
        offTargetNodes = new ListMultipleSelection<>(nodes);
    }

    public List<CyNode> getSourceNodes () {
        return sourceNodes.getSelectedValues();
    }

    public List<CyNode> getTargetNodes () {
        return targetNodes.getSelectedValues();
    }

    public List<CyNode> getOffTargetNodes () {
        return offTargetNodes.getSelectedValues();
    }
}
