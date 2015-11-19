/**
 * Task to run HMS algorithm in OCSANA
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
import org.cytoscape.task.AbstractNetworkTask;
import org.cytoscape.work.TaskMonitor;
import org.cytoscape.work.ContainsTunables;

import org.cytoscape.model.CyNode;
import org.cytoscape.model.CyNetwork;

// OCSANA imports
import org.compsysmed.ocsana.internal.algorithms.mhs.AbstractMHSAlgorithm;

public class MHSAlgorithmTask extends AbstractNetworkTask {
    public AbstractMHSAlgorithm algorithm;

    private Iterable<? extends Iterable<CyNode>> sets;

    private List<Set<CyNode>> MHSes;

    public MHSAlgorithmTask (CyNetwork network,
                             AbstractMHSAlgorithm algorithm,
                             Iterable<? extends Iterable<CyNode>> sets) {
        super(network);
        this.algorithm = algorithm;
        this.sets = sets;
    }

    public void run (TaskMonitor taskMonitor) {
        taskMonitor.setTitle("Minimal CIs");

        taskMonitor.showMessage(TaskMonitor.Level.INFO, "Finding minimal combinations of interventions.");

        MHSes = algorithm.MHSes(sets);

        taskMonitor.showMessage(TaskMonitor.Level.INFO, "Found " + MHSes.size() + " minimal CIs.");
    }

    public List<Set<CyNode>> getMHSes () {
        return MHSes;
    }


    public <T> T getResults (Class<? extends T> type) {
        return (T) getMHSes();
    }
}
