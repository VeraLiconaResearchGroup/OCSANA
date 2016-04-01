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
import org.compsysmed.ocsana.internal.util.drugability.drugbank.*;

import org.compsysmed.ocsana.internal.util.science.*;

/**
 * Singleton factory class to build DrugabilityDataBundles
 **/
public class DrugabilityDataBundleFactory {
    private static DrugabilityDataBundleFactory factory;

    private final DrugBankInteractionsDatabase drugBankDB;

    private DrugabilityDataBundleFactory () {
        drugBankDB = DrugBankInteractionsDatabase.getDB();
    }

    /**
     * Retrieve the singleton bundle factory, constructing it if necessary
     **/
    public static synchronized DrugabilityDataBundleFactory getFactory () {
        if (factory == null) {
            factory = new DrugabilityDataBundleFactory();
        }

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
        Protein protein = drugBankDB.getProteinByID(uniProtID);
        if (protein == null) {
            return null;
        }

        Collection<DrugProteinInteraction> interactions = drugBankDB.getInteractions(protein);
        DrugabilityDataBundle bundle = new DrugabilityDataBundle(protein, interactions);
        return bundle;
    }
}
