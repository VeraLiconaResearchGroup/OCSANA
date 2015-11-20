/**
 * Algorithm which finds all non-self-intersecting paths
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

/**
 * Use depth-first search to generate all non-self-intersecting
 * directed paths up to a specified length
 *
 * @param network  the CyNetwork to compute on
 * @param maxPathLength  the maximum number of nodes allowed in a path
 * (including source and target)
 **/
public class AllNonSelfIntersectingPathsAlgorithm extends AbstractPathFindingAlgorithm {
    public static final String NAME = "All non-self-intersecting paths";
    public static final String SHORTNAME = "ALL";

    @Tunable(description = "Find paths with up to this many nodes:",
             groups = {AbstractPathFindingAlgorithm.CONFIG_GROUP + ": " + SHORTNAME},
             gravity = 210)
    public Integer maxPathLength = 20;

    public AllNonSelfIntersectingPathsAlgorithm(CyNetwork network) {
        super(network);
    }

    public List<List<CyEdge>> paths (Set<CyNode> sources,
                                     Set<CyNode> targets) {
        assert maxPathLength >= 0;
        List<List<CyEdge>> results = null;

        Map<CyEdge, Integer> edgeMinDistances = computeEdgeMinDistances(sources, targets);

        // Only run the next step if the previous succeeded
        if (edgeMinDistances != null) {
            results = computePaths(sources, targets, edgeMinDistances);
        }

        return results;
    }

    /**
     * Pre-compute the minimum length of a path to the targets from
     * each edge
     *
     * @param sources  the source nodes
     * @param targets  the target nodes
     *
     * @return a Map which assigns to some CyEdges a non-negative
     * integer representing the minimum number of in a path starting
     * from the given edge and leading to a target node, as long as
     * that number is not greater than maxPathLength
     **/
    protected Map<CyEdge, Integer> computeEdgeMinDistances (Set<CyNode> sources,
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

                assert nodeToProcess == outEdge.getTarget();
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

        assert nodesToProcess.size() == 0;
        return edgeMinDistances;
    }

    /**
     * Use the pre-computed minimal distances to find all paths from
     * one set of nodes to another
     *
     * @param sources  the source nodes
     * @param targets  the target nodes
     * @param edgeMinDistances  the minimum number of edges in a path
     * to a target beginning with the given edge
     * @return a List of paths, each given as a List of CyEdges in
     * order from a source to a target
     **/
    protected List<List<CyEdge>> computePaths (Set<CyNode> sources,
                                               Set<CyNode> targets,
                                               Map<CyEdge, Integer> edgeMinDistances) {
        // This time, we'll iterate down through the network, starting
        // at the sources and walking forwards along edges. As we
        // walk, we'll extend the incomplete paths we find along any
        // edges whose minimum distance to a target is small enough to
        // keep the total path length no larger than maxPathLength.
        List<List<CyEdge>> completePaths = new ArrayList<>();
        Queue<List<CyEdge>> incompletePaths = new LinkedList<>();

        // Invalid state: abort!
        if (maxPathLength <= 0) {
            return null;
        }

        // Bootstrap the queue with the edges coming out of the sources
        for (CyNode sourceNode: sources) {
            for (CyEdge outEdge: network.getAdjacentEdgeIterable(sourceNode, CyEdge.Type.OUTGOING)) {
                // Handle cancellation
                if (isCanceled()) {
                    return null;
                }

                if (!outEdge.isDirected()) {
                    throw new IllegalArgumentException("Undirected edges are not supported.");
                }

                assert outEdge.getSource() == sourceNode;

                if (edgeMinDistances.containsKey(outEdge)) {
                    List<CyEdge> newPath = new ArrayList<>();
                    newPath.add(outEdge);

                    incompletePaths.add(newPath);
                }
            }
        }

        // Work through the queue of incomplete paths
        for (List<CyEdge> incompletePath; (incompletePath = incompletePaths.poll()) != null;) {
            // Number of edges in path
            Integer pathLength = incompletePath.size();

            // Consider all edges coming out of the leaf of the path
            CyEdge leafEdge = incompletePath.get(incompletePath.size() - 1);

            if (!leafEdge.isDirected()) {
                throw new IllegalArgumentException("Undirected edges are not supported.");
            }

            CyNode leafNode = leafEdge.getTarget();
            for (CyEdge outEdge: network.getAdjacentEdgeIterable(leafNode, CyEdge.Type.OUTGOING)) {
                // Handle cancellation
                if (isCanceled()) {
                    return null;
                }

                if (!outEdge.isDirected()) {
                    throw new IllegalArgumentException("Undirected edges are not supported.");
                }

                if ((edgeMinDistances.containsKey(outEdge)) && (edgeMinDistances.get(outEdge) + pathLength <= maxPathLength)) {
                    // Make sure this doesn't create a self-intersecting path
                    boolean pathIsSelfIntersecting = false;
                    for (CyEdge pathEdge: incompletePath) {
                        if (pathEdge.getSource().equals(outEdge.getTarget()) || (pathEdge.getTarget().equals(outEdge.getTarget()))) {
                            pathIsSelfIntersecting = true;
                            break;
                        }
                    }

                    if (pathIsSelfIntersecting) {
                        continue;
                    }

                    // Otherwise, create the new path
                    List<CyEdge> newPath = new ArrayList<>(incompletePath);
                    newPath.add(outEdge);

                    if (targets.contains(outEdge.getTarget())) {
                        completePaths.add(newPath);
                    } else {
                        incompletePaths.add(newPath);
                    }
                }
            }
        }

        assert incompletePaths.size() == 0;
        return completePaths;
    }

    public String fullName () {
        return this.NAME;
    }

    public String shortName () {
        return this.SHORTNAME;
    }

    public String toString () {
        return this.shortName();
    }
}
