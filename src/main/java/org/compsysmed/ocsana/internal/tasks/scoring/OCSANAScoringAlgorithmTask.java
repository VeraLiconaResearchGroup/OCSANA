/**
 * Task to run scoring algorithm in OCSANA
 *
 * Copyright Vera-Licona Research Group (C) 2015
 * @author Andrew Gainer-Dewar, Ph.D. <andrew.gainer.dewar@gmail.com>
 *
 * This software is licensed under the Artistic License 2.0, see the
 * LICENSE file or
 * http://www.opensource.org/licenses/artistic-license-2.0.php for
 * details
 **/

package org.compsysmed.ocsana.internal.tasks.scoring;

// Java imports
import java.util.*;

// Cytoscape imports
import org.cytoscape.task.AbstractNetworkTask;
import org.cytoscape.work.TaskMonitor;

import org.cytoscape.model.CyNetwork;
import org.cytoscape.model.CyNode;
import org.cytoscape.model.CyEdge;

// OCSANA imports
import org.compsysmed.ocsana.internal.algorithms.scoring.OCSANAScoringAlgorithm;

import org.compsysmed.ocsana.internal.tasks.AbstractOCSANATask;
import org.compsysmed.ocsana.internal.tasks.OCSANAStep;

public class OCSANAScoringAlgorithmTask extends AbstractOCSANATask {
    private static final OCSANAStep algStep = OCSANAStep.SCORE_PATHS;

    public OCSANAScoringAlgorithm algorithm;

    private Collection<? extends List<CyEdge>> pathsToTargets;
    private Collection<? extends List<CyEdge>> pathsToOffTargets;

    public OCSANAScoringAlgorithmTask (CyNetwork network,
                                       OCSANAScoringAlgorithm algorithm,
                                       Collection<? extends List<CyEdge>> pathsToTargets,
                                       Collection<? extends List<CyEdge>> pathsToOffTargets) {
        super(network);
        this.algorithm = algorithm;
        this.pathsToTargets = pathsToTargets;
        this.pathsToOffTargets = pathsToOffTargets;
    }

    public void run (TaskMonitor taskMonitor) {
        taskMonitor.setTitle("Scoring");

        taskMonitor.setStatusMessage("Computing scores.");

        algorithm.applyScores(pathsToTargets, pathsToOffTargets);
    }

    public <T> T getResults (Class<? extends T> type) {
        if (type.isAssignableFrom(OCSANAStep.class)) {
            return (T) algStep;
        } else {
            throw new IllegalArgumentException("Invalid results type for scorer.");
        }
    }

    public void cancel () {
        super.cancel();
        algorithm.cancel();
    }
}
