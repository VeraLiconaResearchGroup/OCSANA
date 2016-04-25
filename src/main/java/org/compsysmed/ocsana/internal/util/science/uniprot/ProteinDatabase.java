/**
 * Proteins database (from UniProt)
 *
 * Copyright Vera-Licona Research Group (C) 2016
 *
 * This software is licensed under the Artistic License 2.0, see the
 * LICENSE file or
 * http://www.opensource.org/licenses/artistic-license-2.0.php for
 * details
 **/

package org.compsysmed.ocsana.internal.util.science.uniprot;

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

/**
 * Singleton class representing the UniProt database
 **/
public class ProteinDatabase {
    private static final String UNIPROT_PATH = "/uniprot/proteins.json";
    private static final ProteinDatabase internalDB = new ProteinDatabase();

    private final Map<String, Protein> proteinByUniProtID = new HashMap<>();
    private final Map<String, String> primaryIDBySecondaryID = new HashMap<>();

    private ProteinDatabase () {
        JSONObject proteinsJSON;
        try (InputStream jsonFileStream = getClass().getResourceAsStream(UNIPROT_PATH)) {
            proteinsJSON = new JSONObject(new JSONTokener(jsonFileStream));
        }
        catch (IOException e) {
            throw new IllegalStateException("Could not find or read UniProt JSON file");
        }

        // Process protein records
        Iterator<String> uniProtKeys = proteinsJSON.keys();
        while (uniProtKeys.hasNext()) {
            String uniProtID = uniProtKeys.next();
            JSONObject proteinData = proteinsJSON.getJSONObject(uniProtID);

            Collection<String> uniProtIDs = new HashSet<>();
            JSONArray uniProtIDsJSON = proteinData.getJSONArray("upids");
            for (int i = 0; i < uniProtIDsJSON.length(); i++) {
                String secondaryID = uniProtIDsJSON.getString(i);
                uniProtIDs.add(secondaryID);
                primaryIDBySecondaryID.put(secondaryID, uniProtID);
            }

            Collection<String> geneNames = new HashSet<>();
            JSONArray geneNamesJSON = proteinData.getJSONArray("geneNames");
            for (int i = 0; i < geneNamesJSON.length(); i++) {
                String geneName = geneNamesJSON.getString(i);
                geneNames.add(geneName);
                primaryIDBySecondaryID.put(geneName, uniProtID);
            }

            String name = proteinData.getString("name");
            String function = proteinData.getString("function");

            Protein protein = new Protein(uniProtID, uniProtIDs, name, geneNames, function);
            proteinByUniProtID.put(uniProtID, protein);
        }
    }

    /**
     * Retrieve the singleton database instance
     **/
    public static ProteinDatabase getDB () {
        return internalDB;
    }

    /**
     * Return all proteins in the database
     **/
    public Collection<Protein> getAllProteins () {
        return proteinByUniProtID.values();
    }

    /**
     * Get the protein with a particular UniProt or Ensembl ID
     *
     * @param proteinID  the ID
     * @return the protein, if found, or null if not
     **/
    public Protein getProteinByID (String proteinID) {
        String cleanedID = proteinID.trim().toUpperCase();

        if (primaryIDBySecondaryID.containsKey(cleanedID)) {
            String primaryID = primaryIDBySecondaryID.get(cleanedID);
            Protein protein = proteinByUniProtID.get(primaryID);

            assert (protein.getGeneNames().contains(primaryID) || protein.getAllUniProtIDs().contains(primaryID));

            return protein;
        }

        if (proteinByUniProtID.containsKey(cleanedID)) {
            Protein protein = proteinByUniProtID.get(cleanedID);
            return protein;
        }

        return null;
    }
}
