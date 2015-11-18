/**
 * Interface handler for path finding algorithms
 *
 * Copyright Vera-Licona Research Group (C) 2015
 * @author Andrew Gainer-Dewar, Ph.D. <andrew.gainer.dewar@gmail.com>
 *
 * This software is licensed under the Artistic License 2.0, see the
 * LICENSE file or
 * http://www.opensource.org/licenses/artistic-license-2.0.php for
 * details
 **/

package org.compsysmed.ocsana.internal.tasks.path;

// Java imports
import java.util.*;

// Cytoscape imports
import org.cytoscape.work.TaskMonitor;
import org.cytoscape.work.Tunable;
import org.cytoscape.work.ContainsTunables;
import org.cytoscape.work.util.ListSingleSelection;

import org.cytoscape.model.CyNode;
import org.cytoscape.model.CyNetwork;

// OCSANA imports
import org.compsysmed.ocsana.internal.tasks.path.AbstractPathFindingAlgorithm;
import org.compsysmed.ocsana.internal.tasks.path.AllNonSelfIntersectingPathsAlgorithm;

/**
 * Interface handler for path-finding algorithms
 *
 * @param network  the network to compute on
 **/
public class PathFindingAlgorithmSelecter {
    public static final String configGroup = "2: Find pathways";
    public static final float configGravity = 2;

    @Tunable(description = "Path-finding algorithm (in order from fastest to most complete)",
             groups = {configGroup},
             gravity = configGravity)
    public ListSingleSelection<AbstractPathFindingAlgorithm> algorithmSelecter;

    private CyNetwork network;

    public PathFindingAlgorithmSelecter (CyNetwork network) {
        this.network = network;

        // Set up the algorithm selecter
        //
        // First, we build a list of all the available algorithms, in
        // order from fastest to most complete..
        List<AbstractPathFindingAlgorithm> algorithms = new ArrayList<>();
        algorithms.add(new AllNonSelfIntersectingPathsAlgorithm(network));

        // Then we populate the ListSingleSelection.
        algorithmSelecter = new ListSingleSelection<>(algorithms);
    }

    public AbstractPathFindingAlgorithm getAlgorithm () {
        return algorithmSelecter.getSelectedValue();
    }
}
