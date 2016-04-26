/**
 * Task to run HMS algorithm in OCSANA
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
import java.util.stream.Collectors;

// Cytoscape imports
import org.cytoscape.work.TaskMonitor;

import org.cytoscape.model.CyNode;
import org.cytoscape.model.CyEdge;

// OCSANA imports
import org.compsysmed.ocsana.internal.tasks.AbstractOCSANATask;
import org.compsysmed.ocsana.internal.tasks.OCSANAStep;

import org.compsysmed.ocsana.internal.stages.generation.GenerationContext;
import org.compsysmed.ocsana.internal.stages.generation.GenerationResults;

import org.compsysmed.ocsana.internal.util.results.CombinationOfInterventions;

public class MHSAlgorithmTask extends AbstractOCSANATask {
    private static final OCSANAStep algStep = OCSANAStep.FIND_MHSES;

    private GenerationContext ciContext;
    private GenerationResults ciResults;

    public MHSAlgorithmTask (GenerationContext ciContext,
                             GenerationResults ciResults) {
        super(ciContext.getNetwork());
        this.ciContext = ciContext;
        this.ciResults = ciResults;
    }

    @Override
    public void run (TaskMonitor taskMonitor) {
        if (ciResults.pathFindingCanceled) {
            return;
        }

        taskMonitor.setTitle("Minimal CIs");

        if (ciResults.pathsToTargets == null) {
            throw new IllegalStateException("Paths to targets not set.");
        }

        taskMonitor.setStatusMessage(String.format("Converting %d paths to node sets.", ciResults.pathsToTargets.size()));
        Long preConversionTime = System.nanoTime();
        List<Set<CyNode>> nodeSets = new ArrayList<>();
        Set<CyNode> sourceNodes = ciContext.sourceNodes;
        Set<CyNode> targetNodes = ciContext.targetNodes;

        for (List<CyEdge> path: ciResults.pathsToTargets) {
            Set<CyNode> nodes = new HashSet<>();

            // Scan every edge in the path, adding its nodes as
            // appropriate
            for (int i = 0; i <= path.size() - 1; i++) {
                CyEdge edge = path.get(i);

                // Since we're using a Set, we don't have to worry
                // about multiple addition, so we'll just go ahead and
                // add the source and target every time
                if (ciContext.includeEndpointsInCIs ||
                    (!sourceNodes.contains(edge.getSource()) && !targetNodes.contains(edge.getSource()))) {
                    nodes.add(edge.getSource());
                }

                if (ciContext.includeEndpointsInCIs ||
                    (!sourceNodes.contains(edge.getTarget()) && !targetNodes.contains(edge.getTarget()))) {
                    nodes.add(edge.getTarget());
                }
            }

            if (!nodes.isEmpty()) {
                nodeSets.add(nodes);
            }
        }
        Long postConversionTime = System.nanoTime();

        Double conversionTime = (postConversionTime - preConversionTime) / 1E9;
        taskMonitor.setStatusMessage(String.format("Converted paths in %f s.", conversionTime));

        taskMonitor.setStatusMessage(String.format("Finding minimal combinations of interventions (algorithm: %s).", ciContext.mhsAlg.shortName()));

        Long preMHSTime = System.nanoTime();
        Collection<Set<CyNode>> MHSes = ciContext.mhsAlg.MHSes(nodeSets);

        ciResults.CIs = MHSes.stream().map(mhs -> new CombinationOfInterventions(mhs, targetNodes, node -> ciContext.nodeNameHandler.getNodeName(node))).collect(Collectors.toList());
        Long postMHSTime = System.nanoTime();

        Double mhsTime = (postMHSTime - preMHSTime) / 1E9;
        taskMonitor.showMessage(TaskMonitor.Level.INFO, String.format("Found %d minimal CIs in %f s.", ciResults.CIs.size(), mhsTime));

        ciResults.mhsExecutionSeconds = mhsTime;
    }

    @Override
    @SuppressWarnings("unchecked")
    public <T> T getResults (Class<? extends T> type) {
        if (type.isAssignableFrom(OCSANAStep.class)) {
            return (T) algStep;
        } else {
            return (T) ciResults.CIs;
        }
    }

    @Override
    public void cancel () {
        super.cancel();
        ciContext.mhsAlg.cancel();
        ciResults.mhsFindingCanceled = true;
    }
}
