/**
 * Factory to build DrugabilityDataBundles
 *
 * Copyright Vera-Licona Research Group (C) 2016
 *
 * This software is licensed under the Artistic License 2.0, see the
 * LICENSE file or
 * http://www.opensource.org/licenses/artistic-license-2.0.php for
 * details
 **/

package org.compsysmed.ocsana.internal.util.drugability;

// Java imports
import java.util.*;
import java.util.stream.Collectors;

// OCSANA imports
import org.compsysmed.ocsana.internal.util.drugability.drprodis.*;
import org.compsysmed.ocsana.internal.util.drugability.drugbank.*;
import org.compsysmed.ocsana.internal.util.drugability.drugfeature.*;

import org.compsysmed.ocsana.internal.util.results.SignedInterventionNode;

import org.compsysmed.ocsana.internal.util.science.*;
import org.compsysmed.ocsana.internal.util.science.uniprot.ProteinDatabase;

/**
 * Factory class to build DrugabilityDataBundles
 **/
public class DrugabilityDataBundleFactory {
    private final ProteinDatabase proteinDB;
    private final DrProdisDrugabilityDatabase drProdisDB;
    private final DrugBankInteractionsDatabase drugBankDB;
    private final DrugFEATUREScoresDatabase drugFeatureDB;

    /**
     * Constructor
     **/
    public DrugabilityDataBundleFactory () {
        proteinDB = ProteinDatabase.getDB();
        drProdisDB = DrProdisDrugabilityDatabase.getDB();
        drugBankDB = DrugBankInteractionsDatabase.getDB();
        drugFeatureDB = DrugFEATUREScoresDatabase.getDB();
    }

    /**
     * Build the DrugabilityDataBundle for a protein or isoform
     *
     * @param id an ID for the protein or isoform (see {@link
     * ProteinDatabase#getProtein} and {@link
     * ProteinDatabase#getIsoform})
     * @return a bundle of all the drugability data available for the
     * protein or isoform, if found, or null if not
     **/
    public DrugabilityDataBundle getBundle (String id) {
        if (proteinDB.isKnownIsoform(id)) {
            Isoform isoform = proteinDB.getIsoform(id);
            Protein protein = isoform.getProtein();

            Collection<DrProdisDrugabilityPrediction> drProdisPredictions = drProdisDB.getPredictions(isoform);
            Collection<DrugProteinInteraction> interactions = drugBankDB.getInteractions(protein);
            Collection<DrugFEATURELigand> ligands = drugFeatureDB.getLigands(protein);

            DrugabilityDataBundle bundle = new DrugabilityDataBundle(protein, drProdisPredictions, interactions, ligands);
            return bundle;
        } else if (proteinDB.isKnownProtein(id)) {
            Protein protein = proteinDB.getProtein(id);

            Collection<DrProdisDrugabilityPrediction> drProdisPredictions = protein.getIsoforms().stream().flatMap(isoform -> drProdisDB.getPredictions(isoform).stream()).collect(Collectors.toList());
            Collection<DrugProteinInteraction> interactions = drugBankDB.getInteractions(protein);
            Collection<DrugFEATURELigand> ligands = drugFeatureDB.getLigands(protein);

            DrugabilityDataBundle bundle = new DrugabilityDataBundle(protein, drProdisPredictions, interactions, ligands);
            return bundle;
        } else {
            return null;
        }

    }

    /**
     * Build the DrugabilityDataBundle for a given SignedInterventionNode
     *
     * @param node  the node
     * @return a bundle of all the drugability data available for the
     * protein, if found, or null if not
     **/
    public DrugabilityDataBundle getBundle (SignedInterventionNode node) {
        // TODO: allow user to configure how name is processed
        String proteinID = node.getBiomoleculeID();

        return getBundle(proteinID);
    }
}
