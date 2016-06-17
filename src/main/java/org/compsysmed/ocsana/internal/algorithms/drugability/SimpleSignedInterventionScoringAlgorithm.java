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

// OCSANA imports
import org.compsysmed.ocsana.internal.util.drugability.DrugabilityDataBundle;
import org.compsysmed.ocsana.internal.util.drugability.DrugabilityDataBundleFactory;

import org.compsysmed.ocsana.internal.util.drugability.drugbank.DrugProteinInteraction;

import org.compsysmed.ocsana.internal.util.results.SignedIntervention;
import org.compsysmed.ocsana.internal.util.results.SignedInterventionNode;

/**
 * Simple algorithm to score a signed intervention based on drugability
 **/
public class SimpleSignedInterventionScoringAlgorithm
    extends AbstractSignedInterventionScoringAlgorithm {
    private DrugabilityDataBundleFactory drugabilityDataBundleFactory;

    @Override
    public Double computePriorityScore (SignedIntervention signedIntervention) {
        drugabilityDataBundleFactory = new DrugabilityDataBundleFactory();
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

        if (drugabilityBundle == null) {
            // Case that the ID is not in the database
            return 0d;
        }

        if (!drugabilityBundle.getAllInteractionsOfSign(node.getSign()).isEmpty()) {
            return 10d;
        }

        if (!drugabilityBundle.getAllInteractionsNotOfSign(node.getSign()).isEmpty()) {
            return 4d;
        }

        if (drugabilityBundle.getDrProdisPredictions().stream().anyMatch(prediction -> prediction.getCountOfNovelBindingDrugs() > 1)) {
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
    public String description () {
        StringBuilder result = new StringBuilder(fullName());

        return result.toString();
    }
}
