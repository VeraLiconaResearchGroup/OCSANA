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
        if (results.pathFindingCanceled) {
            return;
        }

        taskMonitor.setTitle("Minimal CIs");

        if (results.pathsToTargets == null) {
            throw new IllegalStateException("Paths to targets not set.");
        }

        taskMonitor.setStatusMessage("Converting paths to node sets,");
        Long preConversionTime = System.nanoTime();
        List<Set<CyNode>> nodeSets = new ArrayList<>();
        for (List<CyEdge> path: results.pathsToTargets) {
            Set<CyNode> nodes = new HashSet<>();
            // We are only interested in nodes which are not sources
            // or targets. Thus, we ignore the first and last nodes in
            // the path, then check each remaining node for membership
            // in those sets.
            for (int i = 1; i <= path.size() - 1; i++) {
                CyEdge edge = path.get(i);
                CyNode node = edge.getSource();
                if (!results.sourceNodes.contains(node) && !results.targetNodes.contains(node)) {
                    nodes.add(node);
                }
            }

            if (!nodes.isEmpty()) {
                nodeSets.add(nodes);
            }
        }
        Long postConversionTime = System.nanoTime();

        Double conversionTime = (postConversionTime - preConversionTime) / 1E9;
        taskMonitor.setStatusMessage("Converted paths in " + conversionTime + "s.");

        taskMonitor.setStatusMessage("Finding minimal combinations of interventions in " + conversionTime + "s.");

        Long preMHSTime = System.nanoTime();
        results.MHSes = results.mhsAlg.MHSes(nodeSets);
        Long postMHSTime = System.nanoTime();

        Double mhsTime = (postMHSTime - preMHSTime) / 1E9;
        taskMonitor.showMessage(TaskMonitor.Level.INFO, "Found " + results.MHSes.size() + " minimal CIs in " + mhsTime + "s.");

        results.mhsExecutionSeconds = mhsTime;
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
        results.mhsFindingCanceled = true;
    }
}
