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

import org.cytoscape.model.CyNetwork;
import org.cytoscape.model.CyNode;
import org.cytoscape.model.CyEdge;

// OCSANA imports
import org.compsysmed.ocsana.internal.tasks.AbstractOCSANATask;
import org.compsysmed.ocsana.internal.tasks.OCSANAStep;

import org.compsysmed.ocsana.internal.tasks.results.OCSANAResults;

public class MHSAlgorithmTask extends AbstractOCSANATask {
    private static final OCSANAStep algStep = OCSANAStep.FIND_MHSES;

    private OCSANAResults results;

    public MHSAlgorithmTask (OCSANAResults results) {
        super(results.network);
        this.results = results;
    }

    public void run (TaskMonitor taskMonitor) {
        taskMonitor.setTitle("Minimal CIs");

        taskMonitor.setStatusMessage("Converting paths to node sets,");
        List<Set<CyNode>> nodeSets = new ArrayList<>();
        for (List<CyEdge> path: results.pathsToTargets) {
            Set<CyNode> nodes = new HashSet<>();
            // The first node is a source and the last is a target, so we skip them
            for (int i = 1; i < path.size() - 1; i++) {
                CyEdge edge = path.get(i);
                nodes.add(edge.getSource());
                nodes.add(edge.getTarget());
            }

            if (!nodes.isEmpty()) {
                nodeSets.add(nodes);
            }
        }

        taskMonitor.setStatusMessage("Finding minimal combinations of interventions.");

        results.MHSes = results.mhsAlg.MHSes(nodeSets);

        taskMonitor.showMessage(TaskMonitor.Level.INFO, "Found " + results.MHSes.size() + " minimal CIs.");
    }

    public Collection<? extends Collection<CyNode>> getMHSes () {
        return results.MHSes;
    }

    public <T> T getResults (Class<? extends T> type) {
        if (type.isAssignableFrom(OCSANAStep.class)) {
            return (T) algStep;
        } else {
            return (T) getMHSes();
        }
    }

    public void cancel () {
        super.cancel();
        results.mhsAlg.cancel();
    }
}
