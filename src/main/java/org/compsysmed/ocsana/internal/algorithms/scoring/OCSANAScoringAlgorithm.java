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
import java.util.stream.Collectors;
import java.util.function.Predicate;

// Cytoscape imports
import org.cytoscape.work.Tunable;

import org.cytoscape.model.CyNetwork;
import org.cytoscape.model.CyEdge;
import org.cytoscape.model.CyNode;

import org.cytoscape.model.CyTable;
import org.cytoscape.model.CyRow;

// OCSANA imports
import org.compsysmed.ocsana.internal.algorithms.AbstractOCSANAAlgorithm;

/**
 * Implementation of the OCSANA scoring algorithm
 *
 * @param network  the network to compute on
 **/

public class OCSANAScoringAlgorithm
    extends AbstractOCSANAAlgorithm {
    public static final String NAME = "OCSANA scoring";
    public static final String SHORTNAME = "OCSANA";

    // User configuration
    @Tunable(description = "Create score column",
             gravity = 335,
             groups = {NAME})
             public Boolean storeScores = false;

    @Tunable(description = "Name of column",
             gravity = 336,
             dependsOn = "storeScores=true",
             tooltip = "This column will be overwritten!",
             groups = {NAME})
             public String storeScoresColumn = "ocsanaScore";

    // Internal data
    private CyNetwork network;

    // Per-node subscores for each target and off-target
    Map<CyNode, Map<CyNode, Double>> effectsOnTargets;
    Map<CyNode, Map<CyNode, Double>> effectsOnOffTargets;

    // Per-node collections of subpaths to targets and off-targets
    Map<CyNode, Collection<List<CyEdge>>> nodeSubPathsToTargets;
    Map<CyNode, Collection<List<CyEdge>>> nodeSubPathsToOffTargets;

    // Per-node sets of targets and off-targets hit
    Map<CyNode, Set<CyNode>> targetsHitDownstream;
    Map<CyNode, Set<CyNode>> offTargetsHitDownstream;

    // Global sets of targets and off-targets hit by paths
    Set<CyNode> targetsHitByAllPaths;
    Set<CyNode> offTargetsHitByAllPaths;

    // Set of all nodes in paths
    Set<CyNode> elementaryNodes;

    // Subscores
    private Map<CyNode, Double> nodeTotalScores;
    private Map<CyNode, Double> nodeTargetScores;
    private Map<CyNode, Double> nodeOffTargetScores;

    private Boolean scoresComputed = false;

    public OCSANAScoringAlgorithm (CyNetwork network) {
        this.network = network;
    }

    private void initializeInternalVariables () {
        nodeTotalScores = new HashMap<>();
        nodeTargetScores = new HashMap<>();
        nodeOffTargetScores = new HashMap<>();

        effectsOnTargets = new HashMap<>();
        effectsOnOffTargets = new HashMap<>();

        nodeSubPathsToTargets = new HashMap<>();
        nodeSubPathsToOffTargets = new HashMap<>();

        targetsHitDownstream = new HashMap<>();
        offTargetsHitDownstream = new HashMap<>();

        targetsHitByAllPaths = new HashSet<>();
        offTargetsHitByAllPaths = new HashSet<>();

        elementaryNodes = new HashSet<>();
    }

    /**
     * Compute the OCSANA scores for the specified paths and cache the
     * results
     *
     * @param pathsToTargets  the paths to the target nodes
     * @param pathsToOffTargets  the paths to the off-target nodes
     * @param inhibitionEdgeTester function which returns true if the given
     * edge is negative (i.e. inhibition)
     **/
    public void computeScores (Collection<List<CyEdge>> pathsToTargets,
                               Collection<List<CyEdge>> pathsToOffTargets,
                               Predicate<CyEdge> inhibitionEdgeTester) {
        // Internal variables
        scoresComputed = false;
        initializeInternalVariables();

        // Compute scores for nodes by iterating over paths
        scoreNodesInPaths(pathsToTargets, effectsOnTargets, nodeSubPathsToTargets, targetsHitDownstream, targetsHitByAllPaths, elementaryNodes, inhibitionEdgeTester);
        scoreNodesInPaths(pathsToOffTargets, effectsOnOffTargets, nodeSubPathsToOffTargets, offTargetsHitDownstream, offTargetsHitByAllPaths, elementaryNodes, (CyEdge edge) -> false);

        // Compute total scores for nodes
        scoreNodes();

        if (storeScores) {
            storeScoresInColumn();
        }

        scoresComputed = true;
    }

    /**
     * Compute scores of nodes from the given paths
     *
     * @param paths  the paths to score
     * @param scoreMap  Map to store scores for nodes (updated in-place)
     * @param subPathsMap Map to store subpaths from path nodes to
     * endpoints (NOTE: these are given as subList views of the
     * original paths, so modifications may have unexpected effects)
     * @param endpointDownstreamMap  Map to store endpoints downstream of each node (updated in-place)
     * @param allEndpointsHit  Set to store endpoints hit by these paths (updated in-place)
     * @param elementaryNodes  Set to store nodes found in these paths (updated in-place)
     * @param inhibitionEdgeTester  should return true if the given
     * edge is negative (i.e. inhibition)
     **/
    private void scoreNodesInPaths (Collection<List<CyEdge>> paths,
                                    Map<CyNode, Map<CyNode, Double>> scoreMap,
                                    Map<CyNode, Collection<List<CyEdge>>> subPathsMap,
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
            if (path == null || path.isEmpty()) {
                continue;
            }

            // Handle the endpoints
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
                if (!scoreMap.containsKey(edgeSource)) {
                    scoreMap.put(edgeSource, new HashMap<>());
                }
                Map<CyNode, Double> nodeScoreMap = scoreMap.get(edgeSource);
                Double nodePrevScore = nodeScoreMap.getOrDefault(endpoint, 0d);
                Double nodeNewScoreTerm = pathSign * 1.0 / subPathLength.doubleValue();
                nodeScoreMap.put(endpoint, nodePrevScore + nodeNewScoreTerm);

                // Update the node path records
                if (!subPathsMap.containsKey(edgeSource)) {
                    subPathsMap.put(edgeSource, new ArrayList<>());
                }
                Collection<List<CyEdge>> nodeSubPaths = subPathsMap.get(edgeSource);
                List<CyEdge> subPath = path.subList(i, path.size());
                nodeSubPaths.add(subPath);
                subPathsMap.put(edgeSource, nodeSubPaths);

                if (!endpointDownstreamMap.containsKey(edgeSource)) {
                    endpointDownstreamMap.put(edgeSource, new HashSet<>());
                }
                endpointDownstreamMap.get(edgeSource).add(endpoint);

                elementaryNodes.add(edgeSource);
            }
        }
    }

    /**
     * Retrieve the EFFECT_ON_TARGETS score of a node
     *
     * @param node  the node
     *
     * @return the EFFECT_ON_TARGETS score of that node
     **/
    public Double effectOnTargetsScore (CyNode node) {
        if (!effectsOnTargets.containsKey(node)) {
            return 0d;
        } else {
            return effectsOnTargets.get(node).values().stream().reduce(0d, Double::sum);
        }
    }

    /**
     * Retrieve the EFFECT_ON_TARGETS score of a set of nodes
     *
     * @param nodes  the nodes
     *
     * @return the EFFECT_ON_TARGETS score of that node set (obtained
     * by summing over all the nodes)
     **/
    public Double effectOnTargetsScore (Collection<CyNode> nodes) {
        return nodes.stream().mapToDouble(node -> effectOnTargetsScore(node)).sum();
    }

    /**
     * Retrieve the EFFECT_ON_TARGETS score of a node on a target
     *
     * @param node  the node
     * @param target  the target
     *
     * @return the EFFECT_ON_TARGETS score of that node on that target
     * (or 0 if the node does not effect the target)
     **/
    public Double effectOnTargetsScore (CyNode node,
                                        CyNode target) {
        if (!effectsOnTargets.containsKey(node) || !effectsOnTargets.get(node).containsKey(target)) {
            return 0d;
        } else {
            return effectsOnTargets.get(node).get(target);
        }
    }

    /**
     * Retrieve the SIDE_EFFECTS score of a node
     *
     * @param node  the node
     *
     * @return the SIDE_EFFECTS score of that node
     **/
    public Double effectOnOffTargetsScore (CyNode node) {
        if (!effectsOnOffTargets.containsKey(node)) {
            return 0d;
        } else {
            return effectsOnOffTargets.get(node).values().stream().reduce(0d, Double::sum);
        }
    }

    /**
     * Retrieve the SIDE_EFFECTS score of a set of nodes
     *
     * @param nodes  the nodes
     *
     * @return the SIDE_EFFECTS score of that node set (obtained
     * by summing over all the nodes)
     **/
    public Double effectOnOffTargetsScore (Collection<CyNode> nodes) {
        return nodes.stream().mapToDouble(node -> effectOnOffTargetsScore(node)).sum();
    }

    /**
     * Retrieve the SIDE_EFFECTS score of a node on a target
     *
     * @param node  the node
     * @param target  the target
     *
     * @return the SIDE_EFFECTS score of that node on that target
     * (or 0 if the node does not effect the target)
     **/
    public Double effectOnOffTargetsScore (CyNode node,
                                           CyNode target) {
        if (!effectsOnOffTargets.containsKey(node) || !effectsOnOffTargets.get(node).containsKey(target)) {
            return 0d;
        } else {
            return effectsOnOffTargets.get(node).get(target);
        }
    }

    /**
     * Retrieve the set of elementary nodes
     **/
    public Set<CyNode> getElementaryNodes () {
        return elementaryNodes;
    }

    /**
     * Retrieve the subpaths from a node to all targets
     *
     * @param node  the node
     *
     * @return all subpaths starting from the node and ending at the targets
     **/
    public Collection<List<CyEdge>> nodeSubPathsToTargets (CyNode node) {
        return nodeSubPathsToTargets.getOrDefault(node, new HashSet<>());
    }

    /**
     * Retrieve the subpaths from a node to a selected target
     *
     * @param node  the node
     * @param target  the target
     *
     * @return all subpaths starting from the node and ending at the target
     **/
    public Collection<List<CyEdge>> nodeSubPathsToTarget (CyNode node,
                                                          CyNode target) {
        return nodeSubPathsToTargets(node).stream().filter(path -> !path.isEmpty()).filter(path -> path.get(path.size() - 1).getTarget().equals(target)).collect(Collectors.toList());
    }

    /**
     * Retrieve the subpaths from a node to all off-targets
     *
     * @param node  the node
     *
     * @return all subpaths starting from the node and ending at the off-targets
     **/
    public Collection<List<CyEdge>> nodeSubPathsToOffTargets (CyNode node) {
        return nodeSubPathsToOffTargets.getOrDefault(node, new HashSet<>());
    }

    /**
     * Retrieve the subpaths from a node to a selected off-target
     *
     * @param node  the node
     * @param offTarget  the off-target
     *
     * @return all subpaths starting from the node and ending at the off-target
     **/
    public Collection<List<CyEdge>> nodeSubPathsToOffTarget (CyNode node,
                                                             CyNode offTarget) {
        return nodeSubPathsToOffTargets(node).stream().filter(path -> path.get(path.size() - 1).getTarget().equals(offTarget)).collect(Collectors.toList());
    }

    /**
     * Compute the OCSANA scores of all elementary nodes
     **/
    private void scoreNodes () {
        for (CyNode node: elementaryNodes) {
            // TODO: Handle case that node is an off/target
            // EFFECT_ON_TARGETS term of OVERALL score
            Double targetSubScore;
            if (targetsHitByAllPaths.isEmpty()) {
                targetSubScore = 0d;
            } else {
                Set<CyNode> targetsHitByNode = targetsHitDownstream.getOrDefault(node, new HashSet<>());
                targetSubScore = targetsHitByNode.size() / (double) targetsHitByAllPaths.size() * Math.abs(effectOnTargetsScore(node));
            }

            // SIDE_EFFECTS term of OVERALL score
            Double sideEffectSubScore;
            if (offTargetsHitByAllPaths.isEmpty()) {
                sideEffectSubScore = 0d;
            } else {
                Set<CyNode> offTargetsHitByNode = offTargetsHitDownstream.getOrDefault(node, new HashSet<>());
                sideEffectSubScore = offTargetsHitByNode.size() / (double) offTargetsHitByAllPaths.size() * Math.abs(effectOnOffTargetsScore(node));
            }

            // OVERALL
            Double overallScore = targetSubScore - sideEffectSubScore;

            // OCSANA
            if (overallScore > 0d) {
                Integer setScore = nodeSubPathsToTargets.get(node).size();
                Double ocsanaScore = overallScore * setScore;
                nodeTotalScores.put(node, ocsanaScore);
            } else {
                nodeTotalScores.put(node, 0d);
            }
        }
    }

    /**
     * Compute the score of a single node
     *
     * @param node  the node
     * @return the node's score, or null if the node has not been scored
     **/
    public Double scoreNode (CyNode node) {
        if (isCanceled()) {
            return null;
        }

        // Will return null
        return nodeTotalScores.get(node);
    }

    /**
     * Compute the score for a set of nodes
     *
     * @param nodes  the nodes
     * @return  score of the node set
     **/
    public Double scoreNodeSet (Set<CyNode> nodes) {
        // Sum the individual node scores
        return nodes.stream().mapToDouble(node -> scoreNode(node)).sum();
    }

    /**
     * Return true if scores are available, false otherwise
     **/
    public Boolean hasScores () {
        return scoresComputed;
    }

    /**
     * Record the OCSANA scores in a table column
     **/
    private void storeScoresInColumn() {
        CyTable nodeTable = network.getDefaultNodeTable();

        // Delete the column if it exists
        nodeTable.deleteColumn(storeScoresColumn);

        // Create the column
        // TODO: Should we set a default value?
        nodeTable.createColumn(storeScoresColumn, Double.class, false);

        // Store the values
        for (Map.Entry<CyNode, Double> entry: nodeTotalScores.entrySet()) {
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

    @Override
    public String description () {
        return fullName();
    }
}
