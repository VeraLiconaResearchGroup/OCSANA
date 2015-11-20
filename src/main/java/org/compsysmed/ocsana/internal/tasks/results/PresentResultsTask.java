/**
 * Task to present results to user in OCSANA
 *
 * Copyright Vera-Licona Research Group (C) 2015
 * @author Andrew Gainer-Dewar, Ph.D. <andrew.gainer.dewar@gmail.com>
 *
 * This software is licensed under the Artistic License 2.0, see the
 * LICENSE file or
 * http://www.opensource.org/licenses/artistic-license-2.0.php for
 * details
 **/

package org.compsysmed.ocsana.internal.tasks.results;

// Java imports
import java.util.*;

// Cytoscape imports
import org.cytoscape.work.TaskMonitor;

import org.cytoscape.model.CyNode;
import org.cytoscape.model.CyEdge;
import org.cytoscape.model.CyIdentifiable;
import org.cytoscape.model.CyNetwork;

// OCSANA imports
import org.compsysmed.ocsana.internal.algorithms.path.AbstractPathFindingAlgorithm;
import org.compsysmed.ocsana.internal.algorithms.mhs.AbstractMHSAlgorithm;

import org.compsysmed.ocsana.internal.tasks.AbstractOCSANATask;
import org.compsysmed.ocsana.internal.tasks.OCSANAStep;

public class PresentResultsTask extends AbstractOCSANATask {
    private static final OCSANAStep algStep = OCSANAStep.PRESENT_RESULTS;

// User inputs
    protected Set<CyNode> sourceNodes;
    protected Set<CyNode> targetNodes;
    protected Set<CyNode> offTargetNodes;

// Paths data
    protected AbstractPathFindingAlgorithm pathAlg;
    protected Iterable<? extends Iterable<CyEdge>> pathsToTargets;
    protected Iterable<? extends Iterable<CyEdge>> pathsToOffTargets;

// MHS data
    protected AbstractMHSAlgorithm mhsAlg;
    protected Iterable<? extends Iterable<CyNode>> MHSes;

    public PresentResultsTask (CyNetwork network,
                               Set<CyNode> sourceNodes,
                               Set<CyNode> targetNodes,
                               Set<CyNode> offTargetNodes,
                               AbstractPathFindingAlgorithm pathAlg,
                               Iterable<? extends Iterable<CyEdge>> pathsToTargets,
                               Iterable<? extends Iterable<CyEdge>> pathsToOffTargets,
                               AbstractMHSAlgorithm mhsAlg,
                               Iterable<? extends Iterable<CyNode>> MHSes) {
        super(network);
        this.sourceNodes = sourceNodes;
        this.targetNodes = targetNodes;
        this.offTargetNodes = offTargetNodes;
        this.pathAlg = pathAlg;
        this.pathsToTargets = pathsToTargets;
        this.pathsToOffTargets = pathsToOffTargets;
        this.mhsAlg = mhsAlg;
        this.MHSes = MHSes;
    }

    public void run (TaskMonitor taskMonitor) {
        taskMonitor.setTitle("OCSANA results");

        // User inputs
        taskMonitor.showMessage(TaskMonitor.Level.INFO, "Source nodes: " + printIterableInline(sourceNodes));
        taskMonitor.showMessage(TaskMonitor.Level.INFO, "Target nodes: " + printIterableInline(targetNodes));
        taskMonitor.showMessage(TaskMonitor.Level.INFO, "Off-Target nodes: " + printIterableInline(offTargetNodes));

        // Paths
        taskMonitor.showMessage(TaskMonitor.Level.INFO, "Paths to off-targets: " + printNestedIterable(pathsToOffTargets));
        taskMonitor.showMessage(TaskMonitor.Level.INFO, "Paths to targets: " + printNestedIterable(pathsToTargets));

        // CIs
        taskMonitor.showMessage(TaskMonitor.Level.INFO, "Minimal CIs: " + printNestedIterable(MHSes));
    }

    public <T> T getResults (Class<? extends T> type) {
        if (type.isAssignableFrom(OCSANAStep.class)) {
            return (T) algStep;
        } else {
            throw new IllegalArgumentException("Invalid results type for presenter.");
        }
    }

    // Helper functions for common printing tasks
    private String printCyIdentifiable(CyIdentifiable cyThing) {
        return network.getRow(cyThing).get(CyNetwork.NAME, String.class);
    }

    private <T extends CyIdentifiable, I extends Iterable<T>> String printIterableInline(I toPrint) {
        List<String> strings = new ArrayList<>();
        for (T elt: toPrint) {
            strings.add(printCyIdentifiable(elt));
        }

        return "[" + String.join(", ", strings) + "]";
    }

    private <T extends CyIdentifiable, I extends Iterable<T>> String printNestedIterable (Iterable<I> toPrint) {
        String result = new String();
        for (I innerIterable: toPrint) {
            result += printIterableInline(innerIterable) + "\n";
        }
        return result;
    }
}
