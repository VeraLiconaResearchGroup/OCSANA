/**
 * Algorithm to score nodes based on DrugBank drug records
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

// Cytoscape imports
import org.cytoscape.work.Tunable;
import org.cytoscape.work.util.ListSingleSelection;

import org.cytoscape.model.CyNetwork;
import org.cytoscape.model.CyNode;

import org.cytoscape.model.CyTable;
import org.cytoscape.model.CyRow;
import org.cytoscape.model.CyColumn;

// OCSANA imports
import org.compsysmed.ocsana.internal.algorithms.AbstractOCSANAAlgorithm;

import org.compsysmed.ocsana.internal.util.drugbank.InteractionsDatabase;

/**
 * Algorithm to score nodes based on DrugBank drug records
 *
 * @param network  the network to compute on
 **/

public class DrugBankScoringAlgorithm
    extends AbstractOCSANAAlgorithm {
    public static final String CONFIG_GROUP = "Scoring algorithm";
    public static final String NAME = "DrugBank scoring";
    public static final String SHORTNAME = "DrugBank";

    private static final Double DEFAULT_SCORE = 0.0;

    private InteractionsDatabase drugBankDB;

    // User configuration
    @Tunable(description = "Compute DrugBank drugability scores",
             gravity = 340,
             groups = {CONFIG_GROUP, NAME})
    public Boolean computeScores = false;

    @Tunable(description = "Column storing gene names",
             gravity = 342,
             dependsOn = "computeScores=true",
             tooltip = "Gene names must match HUGO/HGNC names (see drugbank.ca)",
             groups = {CONFIG_GROUP, NAME})
    public ListSingleSelection<CyColumn> geneColumnSelecter;

    @Tunable(description = "Store DrugBank score in a table column",
             gravity = 345,
             dependsOn = "computeScores=true",
             groups = {CONFIG_GROUP, NAME})
    public Boolean storeScores = false;

    @Tunable(description = "Name of column to store DrugBank scores",
             gravity = 346,
             dependsOn = "storeScores=true",
             tooltip = "This column will be overwritten!",
             groups = {CONFIG_GROUP, NAME})
    public String storeScoresColumn = "drugBankScore";

    private CyNetwork network;
    private Map<CyNode, Double> scoreCache;

    public DrugBankScoringAlgorithm (CyNetwork network) {
        this.network = network;

        List<CyColumn> stringColumns = network.getDefaultNodeTable().getColumns().stream().filter(column -> column.getType().isAssignableFrom(String.class)).collect(Collectors.toList());
        geneColumnSelecter = new ListSingleSelection<CyColumn>(stringColumns);

        drugBankDB = InteractionsDatabase.getDB();
        scoreCache = new HashMap<>();
    }

    /**
     * Compute the scores for all nodes of the network.
     **/
    public Map<CyNode, Double> computeScores () {
        if (!computeScores || isCanceled()) {
            return null;
        }

        return computeScores(network.getNodeList());
    }

    /**
     * Compute the score for each given node.
     *
     * @param nodes  the nodes
     **/
    public Map<CyNode, Double> computeScores (Collection<CyNode> nodes) {
        if (!computeScores || isCanceled()) {
            return null;
        }

        Map<CyNode, Double> result = new HashMap<>();
        for (CyNode node: nodes) {
            result.put(node, scoreNode(node));
        }

        if (storeScores) {
            storeScoresInColumn(result);
        }

        return result;
    }

    /**
     * Compute the score for the given node.
     *
     * @param node  the node
     **/
    public Double scoreNode (CyNode node) {
        if (!computeScores || isCanceled()) {
            return null;
        }

        // TODO: decide how this algorithm should work!
        if (scoreCache.containsKey(node)) {
            return scoreCache.get(node);
        }

        String geneName = network.getDefaultNodeTable().getRow(node.getSUID()).get(geneColumnSelecter.getSelectedValue().getName(), String.class);

        Double score = DEFAULT_SCORE;

        if (drugBankDB.drugNamesForGene(geneName).isEmpty()) {
            score = 1d;
        }

        scoreCache.put(node, score);
        return score;
    }

    /**
     * Compute the score for a set of nodes
     *
     * @param nodes  the nodes
     * @return  score of the node set
     **/
    public Double scoreNodeSet (Set<CyNode> nodes) {
        // TODO: write a proper aggregation function once the scoring is sorted out
        // Sum the individual node scores
        return nodes.stream().mapToDouble(node -> scoreNode(node)).sum();
    }

    /**
     * Return true if scores are available, false otherwise
     **/
    public Boolean hasScores () {
        return computeScores;
    }

    /**
     * Record the DrugBank scores in the user's chosen column
     *
     * @param scores  the node scores
     **/
    private void storeScoresInColumn(Map<CyNode, Double> scores) {
        CyTable nodeTable = network.getDefaultNodeTable();

        // Delete the column if it exists
        nodeTable.deleteColumn(storeScoresColumn);

        // Create the column
        // TODO: Should we set a default value?
        nodeTable.createColumn(storeScoresColumn, Double.class, false);

        // Store the values
        for (Map.Entry<CyNode, Double> entry: scores.entrySet()) {
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
