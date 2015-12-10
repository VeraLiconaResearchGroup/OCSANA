/**
 * Base class for path-finding algorithms using Dikjstra annotation
 *
 * Copyright Vera-Licona Research Group (C) 2015
 * @author Andrew Gainer-Dewar, Ph.D. <andrew.gainer.dewar@gmail.com>
 *
 * This software is licensed under the Artistic License 2.0, see the
 * LICENSE file or
 * http://www.opensource.org/licenses/artistic-license-2.0.php for
 * details
 **/

package org.compsysmed.ocsana.internal.algorithms.path;

// Java imports
import java.util.*;

// Cytoscape imports
import org.cytoscape.work.Tunable;

import org.cytoscape.model.CyNetwork;
import org.cytoscape.model.CyEdge;
import org.cytoscape.model.CyNode;

// OCSANA imports
import org.compsysmed.ocsana.internal.algorithms.path.AbstractPathFindingAlgorithm;

public abstract class DijkstraPathAlgorithm
    extends AbstractPathFindingAlgorithm {
    @Tunable(description = "Use finite search radius",
             groups = {AbstractPathFindingAlgorithm.CONFIG_GROUP},
             gravity=210)
             public Boolean restrictPathLength = true;

    @Tunable(description = "Find paths with up to this many nodes:",
             groups = {AbstractPathFindingAlgorithm.CONFIG_GROUP},
             gravity = 211,
             dependsOn = "restrictPathLength=true")
             public Integer maxPathLength = 20;

    public DijkstraPathAlgorithm (CyNetwork network) {
        super(network);
    }

    /**
     * Use the pre-computed minimal distances to find the paths
     *
     * @param sources  the source nodes
     * @param targets  the target nodes
     * @param edgeMinDistances  the minimum number of edges in a path
     * to a target beginning with the given edge
     * @return a List of paths, each given as a List of CyEdges in
     * order from a source to a target
     **/
    abstract protected List<List<CyEdge>> computePaths (Set<CyNode> sources,
                                                        Set<CyNode> targets,
                                                        Map<CyEdge, Integer> edgeMinDistances);

    /**
     * Compute the minimum length of a path to the targets from
     * each edge
     *
     * @param sources  the source nodes
     * @param targets  the target nodes
     *
     * @return a Map which assigns to some CyEdges a non-negative
     * integer representing the minimum number of edges in a path
     * starting from the given edge and leading to a target node, as
     * long as that number is not greater than maxPathLength
     **/
    protected Map<CyEdge, Integer> edgeMinDistances (Set<CyNode> sources,
                                                     Set<CyNode> targets) {
        // We'll iterate up through the network, starting at the
        // targets and walking backwards along edges. Each time we
        // find an edge which has not been marked with a distance, we
        // mark it with one more than the distance which got us
        // there. Each time we find an edge which *has* been marked
        // with a distance, we overwrite it if appropriate.
        Map<CyEdge, Integer> edgeMinDistances = new HashMap<>();
        Map<CyNode, Integer> nodeMinDistances = new HashMap<>();
        Queue<CyNode> nodesToProcess = new LinkedList<>();

        // Bootstrap with the target nodes
        for (CyNode targetNode: targets) {
            nodeMinDistances.put(targetNode, 0);
            nodesToProcess.add(targetNode);
        }

        // Work through the node queue
        for (CyNode nodeToProcess; (nodeToProcess = nodesToProcess.poll()) != null;) {
            // Handle cancellation
            if (isCanceled()) {
                return null;
            }

            assert nodeMinDistances.containsKey(nodeToProcess);
            // Look at all the edges connected to this node
            for (CyEdge outEdge: network.getAdjacentEdgeIterable(nodeToProcess, CyEdge.Type.INCOMING)) {
                if (!outEdge.isDirected()) {
                    throw new IllegalArgumentException("Undirected edges are not supported.");
                }

                assert nodeToProcess.equals(outEdge.getTarget());
                CyNode nextNode = outEdge.getSource();

                // Mark this edge if needed
                Integer newEdgeDist = nodeMinDistances.get(nodeToProcess) + 1;
                if ((!edgeMinDistances.containsKey(outEdge))
                    || (edgeMinDistances.get(outEdge) > newEdgeDist)) {
                    edgeMinDistances.put(outEdge, newEdgeDist);
                }

                // Mark the other node if needed
                if ((!nodeMinDistances.containsKey(nextNode))
                    || (nodeMinDistances.get(nextNode) > newEdgeDist)) {
                    nodeMinDistances.put(nextNode, newEdgeDist);
                    nodesToProcess.add(nextNode);
                }
            }
        }

        assert nodesToProcess.isEmpty();
        return edgeMinDistances;
    }
}
