/**
 * Algorithm which finds only shortest paths
 *
 * Copyright Vera-Licona Research Group (C) 2015
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
import org.cytoscape.work.ContainsTunables;

import org.cytoscape.model.CyNetwork;
import org.cytoscape.model.CyEdge;
import org.cytoscape.model.CyNode;

// OCSANA imports


/**
 * Use depth-first search to generate the shortest directed paths
 *
 * @param network  the CyNetwork to compute on
 **/
public class ShortestPathsAlgorithm
    extends AbstractPathFindingAlgorithm {
    public static final String NAME = "Shortest paths";
    public static final String SHORTNAME = "SHORT";

    @ContainsTunables
    public DijkstraPathDecoratorAlgorithm dijkstra;

    public ShortestPathsAlgorithm (CyNetwork network) {
        super(network);

        dijkstra = new DijkstraPathDecoratorAlgorithm(network);
        dijkstra.restrictPathLength = false;
    }

    @Override
    public Collection<List<CyEdge>> paths (Set<CyNode> sources,
                                           Set<CyNode> targets) {
        Collection<List<CyEdge>> shortestPaths = new ArrayList<>();

        for (CyNode source: sources) {
            for (CyNode target: targets) {
                Collection<List<CyEdge>> newPaths = computeShortestPaths(source, target);
                // TODO: handle null?
                if (newPaths != null) {
                    shortestPaths.addAll(newPaths);
                }
            }
        }

        return shortestPaths;
    }

    /**
     * Find all shortest paths from one source to one target node, as
     * long as they satisfy the constraints on the underlying Dijkstra
     * decorator algorithm
     *
     * @param source  the source node
     * @param target  the target node
     * @return a list of all shortest paths from the source to the
     * target, each given as a List of CyEdges in order from source to
     * target, or null if the operation was canceled
     **/
    private Collection<List<CyEdge>> computeShortestPaths (CyNode source,
                                                           CyNode target) {
        Set<CyNode> sourceSet = Collections.singleton(source);
        Set<CyNode> targetSet = Collections.singleton(target);

        // Decorate graph with minimal distances to target
        Map<CyEdge, Integer> edgeMinDistances = dijkstra.edgeMinDistances(targetSet);

        // Find shortest path length
        Integer shortestPathLength = null;
        for (CyEdge outEdge: network.getAdjacentEdgeIterable(source, CyEdge.Type.OUTGOING)) {
            // Handle cancellation
            if (isCanceled()) {
                return null;
            }

            if (!outEdge.isDirected()) {
                throw new IllegalArgumentException(UNDIRECTED_ERROR_MESSAGE);
            }

            assert outEdge.getSource() == source;

            if (edgeMinDistances.containsKey(outEdge)) {
                Integer shortestPathLengthThroughEdge = edgeMinDistances.get(outEdge);
                if ((shortestPathLength == null) || (shortestPathLengthThroughEdge < shortestPathLength)) {
                    shortestPathLength = shortestPathLengthThroughEdge;
                }
            }
        }

        if (shortestPathLength == null) {
            // No path was found within the length bounds or the
            // source had no out edges. Either way, we give back an
            // empty list.
            return new ArrayList<>();
        }

        // If we make it this far, there is at least one shortest
        // path, so let's find them!
        List<List<CyEdge>> completePaths = new ArrayList<>();
        Queue<List<CyEdge>> incompletePaths = new LinkedList<>();

        // Bootstrap the queue with edges out of the source
        for (CyEdge outEdge: network.getAdjacentEdgeIterable(source, CyEdge.Type.OUTGOING)) {
            // Handle cancellation
            if (isCanceled()) {
                return null;
            }

            if (!outEdge.isDirected()) {
                throw new IllegalArgumentException(UNDIRECTED_ERROR_MESSAGE);
            }

            assert outEdge.getSource() == source;

            if (edgeMinDistances.containsKey(outEdge)) {
                Integer shortestPathLengthThroughEdge = edgeMinDistances.get(outEdge);
                assert shortestPathLengthThroughEdge >= shortestPathLength;

                if (shortestPathLengthThroughEdge.equals(shortestPathLength)) {
                    List<CyEdge> newPath = new ArrayList<>();
                    newPath.add(outEdge);

                    if (outEdge.getTarget() == target) {
                        assert shortestPathLengthThroughEdge == 1;
                        completePaths.add(newPath);
                    } else {
                        incompletePaths.add(newPath);
                    }
                }
            }
        }

        // Work through the queue, extending paths along edges that
        // preserve shortestness
        for (List<CyEdge> incompletePath; (incompletePath = incompletePaths.poll()) != null;) {
            // Number of edges in path
            Integer pathLength = incompletePath.size();
            assert pathLength > 0;

            // Consider all edges coming out of the leaf of the path
            CyEdge leafEdge = incompletePath.get(pathLength - 1);

            if (!leafEdge.isDirected()) {
                throw new IllegalArgumentException(UNDIRECTED_ERROR_MESSAGE);
            }

            CyNode leafNode = leafEdge.getTarget();
            for (CyEdge outEdge: network.getAdjacentEdgeIterable(leafNode, CyEdge.Type.OUTGOING)) {
                // Handle cancellation
                if (isCanceled()) {
                    return null;
                }

                if (!outEdge.isDirected()) {
                    throw new IllegalArgumentException(UNDIRECTED_ERROR_MESSAGE);
                }

                Integer shortestPathLengthRemaining = shortestPathLength - pathLength;

                if (edgeMinDistances.containsKey(outEdge)) {
                    Integer shortestPathLengthThroughEdge = edgeMinDistances.get(outEdge);
                    assert shortestPathLengthThroughEdge >= shortestPathLengthRemaining;

                    if (shortestPathLengthThroughEdge.equals(shortestPathLengthRemaining)) {
                        List<CyEdge> newPath = new ArrayList<>(incompletePath);
                        newPath.add(outEdge);

                        if (outEdge.getTarget() == target) {
                            assert shortestPathLengthThroughEdge == 1;
                            completePaths.add(newPath);
                        } else {
                            incompletePaths.add(newPath);
                        }
                    }
                }
            }
        }

        assert incompletePaths.isEmpty();
        return completePaths;
    }

    @Override
    public void cancel () {
        super.cancel();
        dijkstra.cancel();
    }

    @Override
    public String fullName () {
        return NAME;
    }

    @Override
    public String shortName () {
        return SHORTNAME;
    }

    @Override
    public String toString () {
        return this.shortName();
    }
}
