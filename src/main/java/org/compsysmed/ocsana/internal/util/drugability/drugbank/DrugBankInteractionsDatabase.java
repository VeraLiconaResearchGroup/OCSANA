/**
 * Drug-gene interactions database (from DrugBank)
 *
 * Copyright Vera-Licona Research Group (C) 2016
 *
 * This software is licensed under the Artistic License 2.0, see the
 * LICENSE file or
 * http://www.opensource.org/licenses/artistic-license-2.0.php for
 * details
 **/

package org.compsysmed.ocsana.internal.util.drugability.drugbank;

// Java imports
import java.io.*;
import java.util.*;
import java.util.stream.Collectors;

// JSON imports
import org.json.JSONArray;
import org.json.JSONObject;
import org.json.JSONString;
import org.json.JSONTokener;

// OCSANA imports
import org.compsysmed.ocsana.internal.util.science.*;
import org.compsysmed.ocsana.internal.util.science.uniprot.ProteinDatabase;

/**
 * Singleton class representing the DrugBank database
 **/
public class DrugBankInteractionsDatabase {
    private static final String DRUGBANK_PATH = "/drugbank/drugs.json";
    private static DrugBankInteractionsDatabase internalDB;
    private static ProteinDatabase proteinDB = ProteinDatabase.getDB();

    private Map<String, Drug> drugsByID = new HashMap<>();
    private Map<Drug, Collection<DrugProteinInteraction>> drugActions = new HashMap<>();
    private Map<Protein, Collection<DrugProteinInteraction>> proteinActions = new HashMap<>();

    private DrugBankInteractionsDatabase () {
        JSONObject drugBankJSON;
        try (InputStream jsonFileStream = getClass().getResourceAsStream(DRUGBANK_PATH)) {
            drugBankJSON = new JSONObject(new JSONTokener(jsonFileStream));
        }
        catch (IOException e) {
            throw new IllegalStateException("Could not find or read DrugBank JSON file");
        }

        // Process drug records
        Iterator<String> drugKeys = drugBankJSON.keys();
        while (drugKeys.hasNext()) {
            String drugBankPrimaryID = drugKeys.next();
            JSONObject drugInformationJSON = drugBankJSON.getJSONObject(drugBankPrimaryID);

            String drugName = drugInformationJSON.getString("name");

            Set<String> drugBankIDs = new HashSet<>();
            JSONArray dbidJSON = drugInformationJSON.getJSONArray("dbids");
            for (int i = 0; i < dbidJSON.length(); i++) {
                drugBankIDs.add(dbidJSON.getString(i));
            }

            Set<FDACategory> categories = new HashSet<>();
            JSONArray categoriesJSON = drugInformationJSON.getJSONArray("groups");
            for (int i = 0; i < categoriesJSON.length(); i++) {
                categories.add(FDACategory.getByDescription(categoriesJSON.getString(i)));
            }

            Drug drug = new Drug(drugName, drugBankPrimaryID, drugBankIDs, categories);
            drugsByID.put(drugBankPrimaryID, drug);

            JSONArray interactionsJSON = drugInformationJSON.getJSONArray("targets");
            for (int i = 0; i < interactionsJSON.length(); i++) {
                JSONObject interactionJSON = interactionsJSON.getJSONObject(i);
                String actionDescription = interactionJSON.getString("action");
                String drugID = interactionJSON.getString("drug");
                String proteinID = interactionJSON.getString("target");

                assert (drugID.equals(drugBankPrimaryID));

                Protein protein = proteinDB.getProteinByID(proteinID);
                if (protein == null) {
                    // Not a human protein, so we move on.
                    continue;
                }

                DrugProteinInteraction interaction = new DrugProteinInteraction(drug, protein, actionDescription);

                if (!drugActions.containsKey(drug)) {
                    drugActions.put(drug, new HashSet<>());
                }
                drugActions.get(drug).add(interaction);

                if (!proteinActions.containsKey(protein)) {
                    proteinActions.put(protein, new HashSet<>());
                }
                proteinActions.get(protein).add(interaction);
            }
        }
    }

    /**
     * Retrieve the singleton database instance, constructing it from
     * disk if necessary
     **/
    public static synchronized DrugBankInteractionsDatabase getDB () {
        if (internalDB == null) {
            internalDB = new DrugBankInteractionsDatabase();
        }

        return internalDB;
    }

    /**
     * Return all the drugs in the database
     **/
    public Collection<Drug> getAllDrugs () {
        return drugsByID.values();
    }

    /**
     * Get the drug with a particular DrugBank ID
     *
     * @param drugBankID  the ID
     * @return the drug, if found, or null if not
     **/
    public Drug getDrugByID (String drugBankID) {
        return drugsByID.getOrDefault(drugBankID, null);
    }

    /**
     * Return all interactions for a given drug
     *
     * @param drug  the drug
     **/
    public Collection<DrugProteinInteraction> getInteractions (Drug drug) {
        return drugActions.getOrDefault(drug, new HashSet<>());
    }

    /**
     * Return all interactions for a given protein
     *
     * @param protein  the protein
     **/
    public Collection<DrugProteinInteraction> getInteractions (Protein protein) {
        return proteinActions.getOrDefault(protein, new HashSet<>());
    }
}
