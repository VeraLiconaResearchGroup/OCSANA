/**
 * The OCSANA node-scoring algorithm
 *
 * Copyright Vera-Licona Research Group (C) 2015
 * @author Andrew Gainer-Dewar, Ph.D. <andrew.gainer.dewar@gmail.com>
 *
 * This software is licensed under the Artistic License 2.0, see the
 * LICENSE file or
 * http://www.opensource.org/licenses/artistic-license-2.0.php for
 * details
 **/
package org.compsysmed.ocsana.internal.algorithms.scoring;

// Java imports
import java.util.*;

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

    protected static final String effectsOnTargetsColumn = "_ocsana_effectsOnTargets";
    protected static final String effectsOnOffTargetsColumn = "_ocsana_effectsOnOffTargets";
    protected static final String pathsToTargetsColumn = "_ocsana_pathsToTargets";
    protected static final String pathsToOffTargetsColumn = "_ocsana_pathsToOffTargets";
    protected static final String targetsHitColumn = "_ocsana_targetsHit";
    protected static final String offTargetsHitColumn = "_ocsana_offTargetsHit";

    protected static final String ocsanaScoreColumn = "OCSANA score";

    protected CyTable nodeTable;

    protected Set<CyNode> targetsHit;
    protected Set<CyNode> offTargetsHit;

    protected Set<CyNode> elementaryNodes;

    @Tunable(description = "Delete intermediate table columns",
             gravity = 350,
             groups = {CONFIG_GROUP})
    public Boolean clearColumnsAfterRun = true;

    protected Boolean nodeScoringComplete = false;
    protected Boolean pathScoringComplete = false;

    protected CyNetwork network;

    public OCSANAScoringAlgorithm (CyNetwork network) {
        this.network = network;
        this.nodeTable = network.getDefaultNodeTable();

        targetsHit = new HashSet<>();
        offTargetsHit = new HashSet<>();
        elementaryNodes = new HashSet<>();
    }

    /**
     * Run any appropriate cleanup routines
     *
     * This method should be called at the end of an OCSANA run
     **/
    public void cleanupAfterRun () {
        if (clearColumnsAfterRun) {
            nodeTable.deleteColumn(effectsOnTargetsColumn);
            nodeTable.deleteColumn(effectsOnOffTargetsColumn);

            nodeTable.deleteColumn(pathsToTargetsColumn);
            nodeTable.deleteColumn(pathsToOffTargetsColumn);

            nodeTable.deleteColumn(targetsHitColumn);
            nodeTable.deleteColumn(offTargetsHitColumn);
        }
    }

    /**
     * Retrieve the OCSANA score for a given node
     *
     * @param node  the node to score
     **/
    public Double getScore (CyNode node) {
        if (!nodeScoringComplete) {
            throw new IllegalArgumentException("Cannot retrieve scores before running algorithm");
        }

        CyRow nodeRow = nodeTable.getRow(node.getSUID());
        Double score = nodeRow.get(ocsanaScoreColumn, Double.class);
        return score;
    }

    /**
     * Compute the OCSANA score for a set of nodes
     *
     * This is simply the sum of the scores of the nodes in the set
     *
     * @param nodes  the node set to score
     **/
    public Double getScore (Collection<CyNode> nodes) {
        // We only want to consider each node once, so we pack them into a Set
        Set<CyNode> nodeSet = new HashSet<>(nodes);

        Double totalScore = 0.0;
        for (CyNode node: nodeSet) {
            totalScore += getScore(node);
        }

        return totalScore;
    }

    /**
     * Compute scores and store them in the tables
     *
     * @param network  the network to compute on
     * @param pathsToTargets  the paths to the target nodes
     * @param pathsToOffTargets  the paths to the off-target nodes
     **/
    public void applyScores(Collection<? extends List<CyEdge>> pathsToTargets,
                            Collection<? extends List<CyEdge>> pathsToOffTargets) {
        resetScoreColumn(effectsOnTargetsColumn);
        resetScoreColumn(effectsOnOffTargetsColumn);

        resetCountColumn(pathsToTargetsColumn);
        resetCountColumn(pathsToOffTargetsColumn);

        resetSUIDListColumn(targetsHitColumn);
        resetSUIDListColumn(offTargetsHitColumn);

        precomputeScoresForPaths(pathsToTargets, effectsOnTargetsColumn,
                                 pathsToTargetsColumn,
                                 targetsHitColumn, targetsHit);

        precomputeScoresForPaths(pathsToOffTargets, effectsOnOffTargetsColumn,
                                 pathsToOffTargetsColumn,
                                 offTargetsHitColumn, offTargetsHit);

        pathScoringComplete = true;

        computeScoresForNodes();

        nodeScoringComplete = true;
    };

    protected void computeScoresForNodes () {
        if (!pathScoringComplete) {
            throw new IllegalArgumentException("Cannot score nodes before scoring paths.");
        }

        resetScoreColumn(ocsanaScoreColumn);

        for (CyNode node: elementaryNodes) {
            CyRow nodeRow = nodeTable.getRow(node.getSUID());

            Double targetEffectScore = 0.0;
            List<Long> targetsHitByNode = nodeRow.getList(targetsHitColumn, Long.class);
            Double effectsOnTargetsScore = nodeRow.get(effectsOnTargetsColumn, Double.class);

            if ((targetsHit != null) && (!targetsHit.isEmpty()) &&
                (targetsHitByNode != null) && (!targetsHitByNode.isEmpty()) &&
                (effectsOnTargetsScore != null)) {
                Double targetFraction = new Double(targetsHitByNode.size());
                targetFraction /= targetsHit.size();

                targetEffectScore = targetFraction * effectsOnTargetsScore;
            }

            Double offTargetEffectScore = 0.0;
            List<Long> offTargetsHitByNode = nodeRow.getList(offTargetsHitColumn, Long.class);
            Double effectsOnOffTargetsScore = nodeRow.get(effectsOnOffTargetsColumn, Double.class);

            if ((offTargetsHit != null) && (!offTargetsHit.isEmpty()) &&
                (offTargetsHitByNode != null) && (!offTargetsHitByNode.isEmpty()) &&
                (effectsOnOffTargetsScore != null)) {
                Double offTargetFraction = new Double(offTargetsHitByNode.size());
                offTargetFraction /= offTargetsHit.size();

                offTargetEffectScore = offTargetFraction * effectsOnOffTargetsScore;
            }

            Double totalScore = targetEffectScore - offTargetEffectScore;

            if (totalScore < 0) {
                totalScore = 0.0;
            }

            nodeRow.set(ocsanaScoreColumn, totalScore);
        }

        nodeScoringComplete = true;
    }

    /**
     * Precompute subscores on the specified collection of paths
     *
     * @param paths  the paths to score
     * @param effectColumn  table column to store EFFECT score
     * @param pathCountColumn  table column to store path counts
     * @param targetColumn  table column to store targets hit
     * @param targetsHitSet  set to store targets hit
     **/
    protected void precomputeScoresForPaths(Collection<? extends List<CyEdge>> paths,
                                            String effectColumn,
                                            String pathCountColumn,
                                            String targetColumn,
                                            Set<CyNode> targetsHitSet) {
        for (List<CyEdge> path: paths) {
            precomputeScoresForPath(path, effectColumn, pathCountColumn,
                                    targetColumn, targetsHitSet);
        }
    }

    /**
     * Precompute subscores on the specified single path
     *
     * @param path  the path to score
     * @param effectColumn  table column to store EFFECT score
     * @param pathCountColumn  table column to store path counts
     * @param targetColumn  table column to store targets hit
     * @param targetsHitSet  set to store targets hit
     **/
    protected void precomputeScoresForPath(List<CyEdge> path,
                                           String effectColumn,
                                           String pathCountColumn,
                                           String targetColumn,
                                           Set<CyNode> targetsHitSet) {
        // Walk up the path, starting from the target.

        // First, we do some one-time processing of the target.
        CyEdge edgeToTarget = path.get(path.size() - 1);
        assert edgeToTarget.isDirected();

        CyNode target = edgeToTarget.getTarget();
        incrementTableCount(target, pathCountColumn, 1);
        addToTableSUIDListIfAbsent(target, targetColumn, target.getSUID());
        targetsHitSet.add(target);

        elementaryNodes.add(target);

        // We score the source of each edge, updating the table
        // columns appropriately.

        CyNode prevNode = target;
        for (int edgeIndex = path.size() - 1; edgeIndex >= 0; edgeIndex--) {
            CyEdge edge = path.get(edgeIndex);
            assert edge.getTarget().equals(prevNode);

            CyNode currentNode = edge.getSource();
            prevNode = currentNode;
            elementaryNodes.add(currentNode);

            // EFFECT_ON_TARGETS
            // TODO: Handle signed paths
            Double pathLengthSoFar = new Double(path.size() - edgeIndex);
            incrementTableScore(currentNode, effectColumn, 1/pathLengthSoFar);

            // Path count
            incrementTableCount(currentNode, pathCountColumn, 1);

            // Record target hit
            addToTableSUIDListIfAbsent(currentNode, targetColumn, target.getSUID());
        }
    }



    /**
     * Initialize (create and clear) a column of scores
     *
     * @param columnName  the name of the column
     **/
    protected void resetScoreColumn(String columnName) {
        resetColumn(columnName, Double.class, 0.0);
    }

    /**
     * Initialize (create and clear) a column of counts
     *
     * @param columnName  the name of the column
     **/
    protected void resetCountColumn(String columnName) {
        resetColumn(columnName, Integer.class, 0);
    }

    /**
     * Initialize (create and clear) a column
     *
     * @param columnName  the name of the column
     * @param type  the data type for the column
     * @param defaultValue  the default value for the column
     **/
    private <T extends Number> void resetColumn(String columnName,
                                                Class <? extends T> type,
                                                T defaultValue) {
        // Delete the column if it exists
        try {
            nodeTable.deleteColumn(columnName);
        } catch (IllegalArgumentException e) {
            // This indicates that the column is immutable, which is very bad.
            // TODO: Figure out what to do here
            throw e;
        }

        // Create the column
        nodeTable.createColumn(columnName, type, false, defaultValue);
    }

    // Ensure that the specified column exists and is empty
    protected void resetSUIDListColumn(String columnName) {
        resetListColumn(columnName, Long.class);
    }

    /**
     * Initialize (create and clear) a list column
     *
     * @param columnName  the name of the column
     * @param type  the data type for list entries in the column
     **/
    private <T> void resetListColumn(String columnName,
                                     Class<? extends T> type) {
        // Delete the column if it exists
        try {
            nodeTable.deleteColumn(columnName);
        } catch (IllegalArgumentException e) {
            // This indicates that the column is immutable, which is very bad.
            // TODO: Figure out what to do here
            throw e;
        }

        // Create the column
        nodeTable.createListColumn(columnName, type, false, new ArrayList<>());
    }

    /**
     * Increase a count in a table
     *
     * @param node  the node to update
     * @param columnName  the column/attribute to update
     * @param incrementValue  the amount to add to the node's count
     **/
    protected void incrementTableCount(CyNode node,
                                       String columnName,
                                       Integer incrementValue) {
        CyColumn col = nodeTable.getColumn(columnName);
        Class colType = col.getType();

        if (!colType.isAssignableFrom(Integer.class)) {
            throw new IllegalArgumentException("Cannot assign " + colType + " from Integer");
        }


        CyRow nodeRow = nodeTable.getRow(node.getSUID());
        if (nodeRow.isSet(columnName)) {
            nodeRow.set(columnName, nodeRow.get(columnName, Integer.class) + incrementValue);
        } else {
            nodeRow.set(columnName, incrementValue);
        }
    }

    /**
     * Increase a score in a table
     *
     * @param node  the node to update
     * @param columnName  the column/attribute to update
     * @param incrementValue  the amount to add to the node's score
     **/
    protected void incrementTableScore(CyNode node,
                                       String columnName,
                                       Double incrementValue) {
        CyColumn col = nodeTable.getColumn(columnName);
        Class colType = col.getType();

        if (!colType.isAssignableFrom(Double.class)) {
            throw new IllegalArgumentException("Cannot assign " + colType + " from Double");
        }


        CyRow nodeRow = nodeTable.getRow(node.getSUID());
        if (nodeRow.isSet(columnName)) {
            nodeRow.set(columnName, nodeRow.get(columnName, Double.class) + incrementValue);
        } else {
            nodeRow.set(columnName, incrementValue);
        }
    }

    /**
     * Add an SUID to a list column entry if it is not already
     * there
     *
     * @param node  the node to modify
     * @param columnName  the column/attribute to modify
     * @param newSUID  the SUID to add to the list
     **/
    protected void addToTableSUIDListIfAbsent(CyNode node,
                                              String columnName,
                                              Long newSUID) {
        addToTableListIfAbsent(node, columnName, newSUID, Long.class);
    }

    /**
     * Add an element to a list column entry if it is not already
     * there
     *
     * @param node  the node to modify
     * @param columnName  the column/attribute to modify
     * @param newElement  the new element to add to the list
     * @param type  the data type of the elements of the column
     **/
    private <T> void addToTableListIfAbsent(CyNode node,
                                            String columnName,
                                            T newElement,
                                            Class <T> type) {
        CyColumn col = nodeTable.getColumn(columnName);
        Class colType = col.getListElementType();

        if (!colType.isAssignableFrom(type)) {
            throw new IllegalArgumentException("Cannot assign " + colType + " from " + type);
        }

        CyRow nodeRow = nodeTable.getRow(node.getSUID());
        List<T> nodeList = nodeRow.getList(columnName, type);

        if (nodeList == null) {
            nodeList = new ArrayList();
        }

        if (!nodeList.contains(newElement)) {
            nodeList.add(newElement);
        }
    }

    // Name methods
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
