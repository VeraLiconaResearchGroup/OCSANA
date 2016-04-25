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

// OCSANA imports
import org.compsysmed.ocsana.internal.util.drugability.drprodis.*;
import org.compsysmed.ocsana.internal.util.drugability.drugbank.*;
import org.compsysmed.ocsana.internal.util.drugability.drugfeature.*;

import org.compsysmed.ocsana.internal.util.science.*;
import org.compsysmed.ocsana.internal.util.science.uniprot.ProteinDatabase;

/**
 * Singleton factory class to build DrugabilityDataBundles
 **/
public class DrugabilityDataBundleFactory {
    private static final DrugabilityDataBundleFactory factory = new DrugabilityDataBundleFactory();

    private final ProteinDatabase proteinDB = ProteinDatabase.getDB();
    private final DrProdisDrugabilityDatabase drProdisDB = DrProdisDrugabilityDatabase.getDB();
    private final DrugBankInteractionsDatabase drugBankDB = DrugBankInteractionsDatabase.getDB();
    private final DrugFEATUREScoresDatabase drugFeatureDB = DrugFEATUREScoresDatabase.getDB();

    private DrugabilityDataBundleFactory () {
        // Nothing to do…
    }

    /**
     * Retrieve the singleton bundle factory
     **/
    public static DrugabilityDataBundleFactory getFactory () {
        return factory;
    }

    /**
     * Build the DrugabilityDataBundle for a protein, specified by its
     * UniProt ID.
     *
     * @param uniProtID  the UniProt ID of the protein.
     * @return a bundle of all the drugability data available for the
     * protein, if found, or null if not
     **/
    public DrugabilityDataBundle getBundleByUniProtID (String uniProtID) {
        Protein protein = proteinDB.getProteinByID(uniProtID);
        if (protein == null) {
            return null;
        }

        DrProdisDrugabilityPrediction drProdisPrediction = drProdisDB.getPrediction(protein);
        Collection<DrugProteinInteraction> interactions = drugBankDB.getInteractions(protein);
        Collection<DrugFEATURELigand> ligands = drugFeatureDB.getLigands(protein);
        DrugabilityDataBundle bundle = new DrugabilityDataBundle(protein, drProdisPrediction, interactions, ligands);
        return bundle;
    }
}
