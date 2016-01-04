/**
 * Interface handler for MHS algorithms
 *
 * Copyright Vera-Licona Research Group (C) 2015
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
import org.compsysmed.ocsana.internal.algorithms.mhs.BergeAlgorithm;
import org.compsysmed.ocsana.internal.algorithms.mhs.MMCSAlgorithm;
import org.compsysmed.ocsana.internal.algorithms.mhs.RSAlgorithm;

/**
 * Interface handler for path-finding algorithms
 *
 * @param network  the network to compute on
 **/
public class MHSAlgorithmSelecter {
    @Tunable(description = "MHS algorithm",
             gravity = 350,
             tooltip = "MMCS is usually the fastest. Berge's algorithm is very slow and is included here only for comparison purposes.",
             groups = {"Find minimal CIs"})
    public ListSingleSelection<AbstractMHSAlgorithm> algorithmSelecter;

    @Tunable(description = "Allow sources and targets in CIs",
             gravity = 351,
             tooltip = "If true, CIs will be allowed to contain source and target nodes.",
             groups = {"Find minimal CIs"})
    public Boolean includeEndpointsInCIs = false;

    private CyNetwork network;

    public MHSAlgorithmSelecter (CyNetwork network) {
        this.network = network;

        // Set up the algorithm selecter
        //
        // First, we build a list of all the available algorithms, in
        // order from fastest to most complete..
        List<AbstractMHSAlgorithm> algorithms = new ArrayList<>();
        algorithms.add(new MMCSAlgorithm());
        algorithms.add(new RSAlgorithm());
        algorithms.add(new BergeAlgorithm());

        // Then we populate the ListSingleSelection.
        algorithmSelecter = new ListSingleSelection<>(algorithms);
    }

    public AbstractMHSAlgorithm getAlgorithm () {
        return algorithmSelecter.getSelectedValue();
    }

    public Boolean getIncludeEndpointsInCIs () {
        return includeEndpointsInCIs;
    }
}
