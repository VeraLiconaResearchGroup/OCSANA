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
 **/
public class AllNonSelfIntersectingPathsAlgorithm
    extends DijkstraPathAlgorithm {
    public static final String NAME = "All non-self-intersecting paths";
    public static final String SHORTNAME = "ALL";

    public AllNonSelfIntersectingPathsAlgorithm(CyNetwork network) {
        super(network);
    }

    public Collection<List<CyEdge>> paths (Set<CyNode> sources,
                                           Set<CyNode> targets) {
        assert maxPathLength >= 0;
        Collection<List<CyEdge>> results = null;

        Map<CyEdge, Integer> edgeMinDistances = edgeMinDistances(sources, targets);

        // Only run the next step if the previous succeeded
        if (edgeMinDistances != null) {
            results = computePaths(sources, targets, edgeMinDistances);
        }

        return results;
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

                if ((edgeMinDistances.containsKey(outEdge)) &&
                    ((edgeMinDistances.get(outEdge) + pathLength <= maxPathLength) || (!restrictPathLength))
                        ) {
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
                    }

                    // Note: we allow paths to pass through targets.
                    // To exclude this case, use "else if" on the
                    // previous conditional.
                    incompletePaths.add(newPath);
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
