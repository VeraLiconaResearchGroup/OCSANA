/**
 * Interface handler for MHS algorithms
 *
 * Copyright Vera-Licona Research Group (C) 2015
 * @author Andrew Gainer-Dewar, Ph.D. <andrew.gainer.dewar@gmail.com>
 *
 * This software is licensed under the Artistic License 2.0, see the
 * LICENSE file or
 * http://www.opensource.org/licenses/artistic-license-2.0.php for
 * details
 **/

package org.compsysmed.ocsana.internal.tasks.mhs;

// Java imports
import java.util.*;

// Cytoscape imports
import org.cytoscape.work.TaskMonitor;
import org.cytoscape.work.Tunable;
import org.cytoscape.work.util.ListSingleSelection;

import org.cytoscape.model.CyNode;
import org.cytoscape.model.CyNetwork;

// OCSANA imports
import org.compsysmed.ocsana.internal.algorithms.mhs.AbstractMHSAlgorithm;
import org.compsysmed.ocsana.internal.algorithms.mhs.MMCSAlgorithm;

/**
 * Interface handler for path-finding algorithms
 *
 * @param network  the network to compute on
 **/
public class MHSAlgorithmSelecter {
    public static final String configGroup = "4: Find minimal CIs";
    public static final float configGravity = 4;

    @Tunable(description = "MHS algorithm",
             groups = {configGroup},
             gravity = configGravity)
    public ListSingleSelection<AbstractMHSAlgorithm> algorithmSelecter;

    private CyNetwork network;

    public MHSAlgorithmSelecter (CyNetwork network) {
        this.network = network;

        // Set up the algorithm selecter
        //
        // First, we build a list of all the available algorithms, in
        // order from fastest to most complete..
        List<AbstractMHSAlgorithm> algorithms = new ArrayList<>();
        algorithms.add(new MMCSAlgorithm());

        // Then we populate the ListSingleSelection.
        algorithmSelecter = new ListSingleSelection<>(algorithms);
    }

    public AbstractMHSAlgorithm getAlgorithm () {
        return algorithmSelecter.getSelectedValue();
    }
}