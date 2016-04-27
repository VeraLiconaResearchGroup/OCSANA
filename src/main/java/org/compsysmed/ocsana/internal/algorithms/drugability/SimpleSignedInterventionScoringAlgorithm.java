/**
 * Simple algorithm to score a signed intervention based on drugability
 *
 * Copyright Vera-Licona Research Group (C) 2016
 *
 * This software is licensed under the Artistic License 2.0, see the
 * LICENSE file or
 * http://www.opensource.org/licenses/artistic-license-2.0.php for
 * details
 **/

package org.compsysmed.ocsana.internal.algorithms.drugability;

// Java imports
import java.util.*;

// Cytoscape imports
import org.cytoscape.model.CyNode;

// OCSANA imports
import org.compsysmed.ocsana.internal.util.drugability.DrugabilityDataBundle;
import org.compsysmed.ocsana.internal.util.drugability.DrugabilityDataBundleFactory;

import org.compsysmed.ocsana.internal.util.drugability.drugbank.DrugProteinInteraction;

import org.compsysmed.ocsana.internal.util.drugability.drprodis.DrProdisDrugabilityPrediction;

import org.compsysmed.ocsana.internal.util.results.SignedIntervention;
import org.compsysmed.ocsana.internal.util.results.SignedInterventionNode;

/**
 * Simple algorithm to score a signed intervention based on drugability
 **/
public abstract class SimpleSignedInterventionScoringAlgorithm
    extends AbstractSignedInterventionScoringAlgorithm {
    private DrugabilityDataBundleFactory drugabilityDataBundleFactory = DrugabilityDataBundleFactory.getFactory();

    @Override
    public Double computePriorityScore (SignedIntervention signedIntervention) {
        Collection<SignedInterventionNode> nodes = signedIntervention.getSignedInterventionNodes();
        return nodes.stream().mapToDouble(node -> computePriorityScore(node)).sum() / nodes.size();
    }

    /**
     * Score a single node of the intervention
     **/
    private Double computePriorityScore (SignedInterventionNode node) {
        // This algorithm is completely ad-hoc and should not be
        // trusted to achieve anything.
        // TODO: Write a real algorithm
        DrugabilityDataBundle drugabilityBundle = drugabilityDataBundleFactory.getBundle(node);

        Collection<DrugProteinInteraction> knownSignedInteractions = drugabilityBundle.getAllInteractionsOfSign(node.getSign());

        if (!knownSignedInteractions.isEmpty()) {
            return 10d;
        }

        if (drugabilityBundle.getDrProdisPrediction().getCountOfBindingDrugs() > 1) {
            return 4d;
        }

        if (drugabilityBundle.hasDrugableLigand()) {
            return 4d;
        }

        return 0d;
    }

    @Override
    public String fullName () {
        return "Simple signed intervention scoring algorithm";
    }

    public String shortName () {
        return "SIMPLE";
    }

    @Override
    public String toString () {
        return shortName();
    }

    @Override
    public String description () {
        StringBuilder result = new StringBuilder(fullName());

        return result.toString();
    }
}
