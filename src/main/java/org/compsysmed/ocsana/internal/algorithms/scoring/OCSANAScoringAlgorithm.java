/**
 * The OCSANA node-scoring algorithm
 *
 * Copyright Vera-Licona Research Group (C) 2015
 *
 * This software is licensed under the Artistic License 2.0, see the
 * LICENSE file or
 * http://www.opensource.org/licenses/artistic-license-2.0.php for
 * details
 **/
package org.compsysmed.ocsana.internal.algorithms.scoring;

// Java imports
import java.util.*;
import java.util.function.Predicate;

// Cytoscape imports
import org.cytoscape.work.Tunable;

import org.cytoscape.model.CyNetwork;
import org.cytoscape.model.CyEdge;
import org.cytoscape.model.CyNode;

import org.cytoscape.model.CyTable;
import org.cytoscape.model.CyRow;
import org.cytoscape.model.CyColumn;

// OCSANA imports
import org.compsysmed.ocsana.internal.algorithms.AbstractOCSANAAlgorithm;

/**
 * Implementation of the OCSANA scoring algorithm
 *
 * @param network  the network to compute on
 **/

public class OCSANAScoringAlgorithm
    extends AbstractOCSANAAlgorithm {
    public static final String CONFIG_GROUP = "Scoring algorithm";
    public static final String NAME = "OCSANA scoring";
    public static final String SHORTNAME = "OCSANA";

    private static final Double DEFAULT_SCORE = 0.0;
    private static final Integer DEFAULT_COUNT = 0;

    @Tunable(description = "Compute OCSANA scores",
             gravity = 330,
             groups = {CONFIG_GROUP})
    public Boolean computeScores = true;

    // User configuration
    @Tunable(description = "Store OCSANA score in a table column",
             gravity = 340,
             dependsOn = "computeScores=true",
             groups = {CONFIG_GROUP})
    public Boolean storeScores = false;

    @Tunable(description = "Name of column to store scores",
             gravity = 350,
             dependsOn = "storeScores=true",
             tooltip = "This column will be overwritten!",
             groups = {CONFIG_GROUP})
    public String storeScoresColumn = "ocsanaScore";

    private Boolean nodeScoringComplete = false;
    private Boolean pathScoringComplete = false;

    private CyNetwork network;

    public OCSANAScoringAlgorithm (CyNetwork network) {
        this.network = network;
    }

    /**
     * Compute the OCSANA scores for the specified paths
     *
     * @param pathsToTargets  the paths to the target nodes
     * @param pathsToOffTargets  the paths to the off-target nodes
     * @param inhibitionEdgeTester function which returns true if the given
     * edge is negative (i.e. inhibition)
     * @param Map assigning to each node its OCSANA score
     **/
    public Map<CyNode, Double> computeScores (Collection<List<CyEdge>> pathsToTargets,
                                              Collection<List<CyEdge>> pathsToOffTargets,
                                              Predicate<CyEdge> inhibitionEdgeTester) {
        if (!computeScores) {
            return null;
        }

        // Internal variables
        // Per-node subscores for target and off-target paths
        Map<CyNode, Double> effectsOnTargets = new HashMap<>();
        Map<CyNode, Double> effectsOnOffTargets = new HashMap<>();

        // Per-node counts of paths to targets and off-targets
        Map<CyNode, Integer> countPathsToTargets = new HashMap<>();
        Map<CyNode, Integer> countPathsToOffTargets = new HashMap<>();

        // Per-node sets of targets and off-targets hit
        Map<CyNode, Set<CyNode>> targetsHitDownstream = new HashMap<>();
        Map<CyNode, Set<CyNode>> offTargetsHitDownstream = new HashMap<>();

        // Global sets of targets and off-targets hit by paths
        Set<CyNode> targetsHitByAllPaths = new HashSet<>();
        Set<CyNode> offTargetsHitByAllPaths = new HashSet<>();

        // Set of all nodes in paths
        Set<CyNode> elementaryNodes = new HashSet<>();

        // Compute scores for nodes by iterating over paths
        scoreNodesInPaths(pathsToTargets, effectsOnTargets, countPathsToTargets, targetsHitDownstream, targetsHitByAllPaths, elementaryNodes, inhibitionEdgeTester);
        scoreNodesInPaths(pathsToOffTargets, effectsOnOffTargets, countPathsToOffTargets, offTargetsHitDownstream, offTargetsHitByAllPaths, elementaryNodes, (CyEdge edge) -> false);

        // Compute total scores for nodes
        Map<CyNode, Double> ocsanaScores = scoreNodes(effectsOnTargets, countPathsToTargets, targetsHitDownstream, targetsHitByAllPaths, effectsOnOffTargets, countPathsToOffTargets, offTargetsHitDownstream, offTargetsHitByAllPaths, elementaryNodes);

        if (storeScores) {
            storeScoresInColumn(ocsanaScores, storeScoresColumn);
        }

        return ocsanaScores;
    }

    /**
     * Compute scores of nodes from the given paths
     *
     * @param paths  the paths to score
     * @param scoreMap  Map to store scores for nodes (updated in-place)
     * @param pathCountMap  Map to store number of paths containing each node (updated in-place)
     * @param endpointDownstreamMap  Map to store endpoints downstream of each node (updated in-place)
     * @param allEndpointsHit  Set to store endpoints hit by these paths (updated in-place)
     * @param elementaryNodes  Set to store nodes found in these paths (updated in-place)
     * @param inhibitionEdgeTester  should return true if the given
     * edge is negative (i.e. inhibition)
     **/
    private void scoreNodesInPaths (Collection<List<CyEdge>> paths,
                                    Map<CyNode, Double> scoreMap,
                                    Map<CyNode, Integer> pathCountMap,
                                    Map<CyNode, Set<CyNode>> endpointDownstreamMap,
                                    Set<CyNode> allEndpointsHit,
                                    Set<CyNode> elementaryNodes,
                                    Predicate<CyEdge> inhibitionEdgeTester) {
        // TODO: Handle null arguments

        // Iterate over the paths
        for (List<CyEdge> path: paths) {
            // Handle cancellation
            if (isCanceled()) {
                return;
            }

            // Handle empty and null paths
            // TODO: A null path is probably an error
            if (path == null || path.size() == 0) {
                continue;
            }

            // Handle the endpoint
            CyNode endpoint = path.get(path.size() - 1).getTarget();
            allEndpointsHit.add(endpoint);
            elementaryNodes.add(endpoint);

            // Record the sign of the path
            // This will always be ±1
            Integer pathSign = 1;

            // For each path, we walk backwards from the endpoint,
            // considering the source of each edge
            for (int i = path.size() - 1; i >= 0; i--) {
                assert Math.abs(pathSign) == 1;

                CyEdge edge = path.get(i);
                if (!edge.isDirected()) {
                    throw new IllegalArgumentException("Undirected edges are not supported.");
                }

                CyNode edgeSource = edge.getSource();
                Integer subPathLength = path.size() - i;

                if (inhibitionEdgeTester.test(edge)) {
                    pathSign *= -1;
                }

                // Update the node score
                Double nodePrevScore = scoreMap.getOrDefault(edgeSource, DEFAULT_SCORE);
                Double nodeNewScoreTerm = pathSign * 1.0 / subPathLength.doubleValue();
                scoreMap.put(edgeSource, nodePrevScore + nodeNewScoreTerm);

                // Update the node path records
                Integer nodePathCount = pathCountMap.getOrDefault(edgeSource, DEFAULT_COUNT);
                pathCountMap.put(edgeSource, nodePathCount + 1);

                if (!endpointDownstreamMap.containsKey(edgeSource)) {
                    endpointDownstreamMap.put(edgeSource, new HashSet<>());
                }
                endpointDownstreamMap.get(edgeSource).add(endpoint);

                elementaryNodes.add(edgeSource);
            }
        }
    }

    /**
     * Compute the OCSANA scores of the nodes
     *
     * @param effectsOnTargets  the EFFECTS_ON_TARGETS score of each node
     * @param countPathsToTargets  the number of paths to targets from each node
     * @param targetsHitDownstream  the Set of targets downstream of each node
     * @param targetsHitByAllPaths the Set of targets hit by all paths
     * @param effectsOnOffTargets  the SIDE_EFFECTS score of each node
     * @param countPathsToOffTargets  the number of paths to offTargets from each node
     * @param offTargetsHitDownstream  the Set of off-targets downstream of each node
     * @param offTargetsHitByAllPaths the Set of off-targets hit by all paths
     * @param elementaryNodes  the Set of all nodes in the paths
     * @return Map assigning to each elementary node its OCSANA score
     **/
    private Map<CyNode, Double> scoreNodes (Map<CyNode, Double> effectsOnTargets,
                                            Map<CyNode, Integer> countPathsToTargets,
                                            Map<CyNode, Set<CyNode>> targetsHitDownstream,
                                            Set<CyNode> targetsHitByAllPaths,
                                            Map<CyNode, Double> effectsOnOffTargets,
                                            Map<CyNode, Integer> countPathsToOffTargets,
                                            Map<CyNode, Set<CyNode>> offTargetsHitDownstream,
                                            Set<CyNode> offTargetsHitByAllPaths,
                                            Set<CyNode> elementaryNodes) {
        Map<CyNode, Double> scores = new HashMap<>();

        for (CyNode node: elementaryNodes) {
            // TODO: Handle case that node is an off/target
            // EFFECT_ON_TARGETS term of OVERALL score
            Double targetSubScore;
            if (targetsHitByAllPaths.isEmpty()) {
                targetSubScore = DEFAULT_SCORE;
            } else {
                Set<CyNode> targetsHitByNode = targetsHitDownstream.getOrDefault(node, new HashSet<>());
                targetSubScore = targetsHitByNode.size() / (double) targetsHitByAllPaths.size() * Math.abs(effectsOnTargets.getOrDefault(node, DEFAULT_SCORE));
            }

            // SIDE_EFFECTS term of OVERALL score
            Double sideEffectSubScore;
            if (offTargetsHitByAllPaths.isEmpty()) {
                sideEffectSubScore = DEFAULT_SCORE;
            } else {
                Set<CyNode> offTargetsHitByNode = offTargetsHitDownstream.getOrDefault(node, new HashSet<>());
                sideEffectSubScore = offTargetsHitByNode.size() / (double) offTargetsHitByAllPaths.size() * Math.abs(effectsOnOffTargets.getOrDefault(node, DEFAULT_SCORE));
            }

            // OVERALL
            Double overallScore = targetSubScore - sideEffectSubScore;

            // OCSANA
            if (overallScore > 0d) {
                Double ocsanaScore = overallScore * countPathsToTargets.getOrDefault(node, 0);
                scores.put(node, ocsanaScore);
            } else {
                scores.put(node, 0d);
            }
        }

        return scores;
    }

    /**
     * Record the OCSANA scores in a table column
     *
     * @param ocsanaScores  the node scores
     * @param storeScoresColumn  the name of the column
     **/
    private void storeScoresInColumn(Map<CyNode, Double> ocsanaScores,
                                     String storeScoresColumn) {
        CyTable nodeTable = network.getDefaultNodeTable();

        // Delete the column if it exists
        nodeTable.deleteColumn(storeScoresColumn);

        // Create the column
        // TODO: Should we set a default value?
        nodeTable.createColumn(storeScoresColumn, Double.class, false);

        // Store the values
        for (Map.Entry<CyNode, Double> entry: ocsanaScores.entrySet()) {
            CyNode node = entry.getKey();
            Double score = entry.getValue();
            CyRow nodeRow = nodeTable.getRow(node.getSUID());
            nodeRow.set(storeScoresColumn, score);
        }
    }

    // Name methods
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
