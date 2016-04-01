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
     **/
    public DrugabilityDataBundle getBundleByUniProtID (String uniProtID) {
        Protein protein = drugBankDB.getProteinByID(uniProtID);
        Collection<DrugProteinInteraction> interactions = drugBankDB.getInteractions(protein);

        DrugabilityDataBundle bundle = new DrugabilityDataBundle(protein, interactions);
        return bundle;
    }
}
