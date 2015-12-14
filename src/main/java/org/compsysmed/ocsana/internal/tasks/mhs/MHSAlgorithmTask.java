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

        taskMonitor.setStatusMessage("Converting " + results.pathsToTargets.size() + " paths to node sets,");
        Long preConversionTime = System.nanoTime();
        List<Set<CyNode>> nodeSets = new ArrayList<>();
        for (List<CyEdge> path: results.pathsToTargets) {
            Set<CyNode> nodes = new HashSet<>();

            // Scan every edge in the path, adding its nodes as
            // appropriate
            for (int i = 0; i <= path.size() - 1; i++) {
                CyEdge edge = path.get(i);

                // Since we're using a Set, we don't have to worry
                // about multiple addition, so we'll just go ahead and
                // add the source and target every time
                addNodeWithEndpointChecks(edge.getSource(), nodes);
                addNodeWithEndpointChecks(edge.getTarget(), nodes);
            }

            if (!nodes.isEmpty()) {
                nodeSets.add(nodes);
            }
        }
        Long postConversionTime = System.nanoTime();

        Double conversionTime = (postConversionTime - preConversionTime) / 1E9;
        taskMonitor.setStatusMessage("Converted paths in " + conversionTime + "s.");

        taskMonitor.setStatusMessage("Finding minimal combinations of interventions.");

        Long preMHSTime = System.nanoTime();
        results.MHSes = results.mhsAlg.MHSes(nodeSets);
        Long postMHSTime = System.nanoTime();

        Double mhsTime = (postMHSTime - preMHSTime) / 1E9;
        taskMonitor.showMessage(TaskMonitor.Level.INFO, "Found " + results.MHSes.size() + " minimal CIs in " + mhsTime + "s.");

        results.mhsExecutionSeconds = mhsTime;
    }

    /**
     * Add a node to a set if it satisfies the endpoint conditions
     *
     * In particular, if the node is a source or target, only add it
     * if results.includeEndpointsinCIs is true
     *
     * @param nodeToAdd  the node to add
     * @param nodeSet  the set of nodes to (maybe) add to
     **/
    private void addNodeWithEndpointChecks (CyNode nodeToAdd,
                                            Set<CyNode> nodeSet) {
        if (results.includeEndpointsInCIs ||
            (!results.sourceNodes.contains(nodeToAdd) && !results.targetNodes.contains(nodeToAdd))) {
            nodeSet.add(nodeToAdd);
        }
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
