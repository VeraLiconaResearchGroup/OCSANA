/**
 * Helper class for DrugFEATURE drugability prediction database
 *
 * Copyright Vera-Licona Research Group (C) 2016
 *
 * This software is licensed under the Artistic License 2.0, see the
 * LICENSE file or
 * http://www.opensource.org/licenses/artistic-license-2.0.php for
 * details
 **/

package org.compsysmed.ocsana.internal.util.drugability.drugfeature;

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
 * Singleton class representing the DrugFEATURE prediction database
 **/
public class DrugFEATUREScoresDatabase {
    private static final String DRUGFEATURE_PATH = "/drugfeature/scores.json";
    private static final DrugFEATUREScoresDatabase internalDB = new DrugFEATUREScoresDatabase();

    private final ProteinDatabase proteinDB = ProteinDatabase.getDB();
    private Map<String, Collection<DrugFEATURELigand>> ligandsOfProtein = new HashMap<>();

    private DrugFEATUREScoresDatabase () {
        JSONObject drugFeatureJSON;
        try (InputStream jsonFileStream = getClass().getResourceAsStream(DRUGFEATURE_PATH)) {
            drugFeatureJSON = new JSONObject(new JSONTokener(jsonFileStream));
        } catch (IOException e) {
            throw new IllegalStateException("Could not find or read DrugFEATURE JSON file");
        }

        // Process protein records
        Iterator<String> proteinKeys = drugFeatureJSON.keys();
        while (proteinKeys.hasNext()) {
            String uniProtID = proteinKeys.next();
            JSONArray ligandsJSON = drugFeatureJSON.getJSONArray(uniProtID);

            Collection<DrugFEATURELigand> ligands = new ArrayList<>();

            for (int i = 0; i < ligandsJSON.length(); i++) {
                JSONObject ligandJSON = ligandsJSON.getJSONObject(i);

                String chainString = ligandJSON.getString("chain");
                if (chainString.length() != 1) {
                    throw new IllegalStateException(String.format("Chain string %s is not one character long", chainString));
                }
                Character chain = chainString.charAt(0);

                Integer index = ligandJSON.getInt("index");
                String ligandName = ligandJSON.getString("ligand");
                String pdbID = ligandJSON.getString("pdb");
                Double score = ligandJSON.getDouble("score");

                DrugFEATURELigand ligand = new DrugFEATURELigand(uniProtID, pdbID, chain, index, ligandName, score);
                ligands.add(ligand);
            }

            ligandsOfProtein.put(uniProtID, ligands);
        }
    }

    /**
     * Retrieve the singleton database instance
     **/
    public static DrugFEATUREScoresDatabase getDB () {
        return internalDB;
    }

    /**
     * Retrieve all UniProt IDs with known scores
     **/
    public Collection<String> getAllScoredProteinIDs () {
        return ligandsOfProtein.keySet();
    }

    /**
     * Retrieve all proteins with known scores
     **/
    public Collection<Protein> getAllScoredProteins () {
        return getAllScoredProteinIDs().stream().map(proteinID -> proteinDB.getProtein(proteinID)).collect(Collectors.toList());
    }

    /**
     * Return the ligands for a given protein
     *
     * @param uniProtID  the UniProt ID of the protein
     * @return the ligands for the specified protein
     **/
    public Collection<DrugFEATURELigand> getLigands (String uniProtID) {
        return ligandsOfProtein.getOrDefault(uniProtID, new HashSet<>());
    }

    /**
     * Return the ligands for a given protein
     *
     * @param protein  the protein
     * @return the ligands for the specified protein
     **/
    public Collection<DrugFEATURELigand> getLigands (Protein protein) {
        return getLigands(protein.getUniProtID());
    }

    /**
     * Return the drugable ligands for a given protein
     *
     * @param uniProtID  the UniProt ID of the protein
     * @return the drugable ligands for the specified protein
     **/
    public Collection<DrugFEATURELigand> getDrugableLigands (String uniProtID) {
        return ligandsOfProtein.getOrDefault(uniProtID, new HashSet<>()).stream().filter(ligand -> ligand.isDrugable()).collect(Collectors.toList());
    }

    /**
     * Return the drugable ligands for a given protein
     *
     * @param protein  the protein
     * @return the drugable ligands for the specified protein
     **/
    public Collection<DrugFEATURELigand> getDrugableLigands (Protein protein) {
        return getDrugableLigands(protein.getUniProtID());
    }
}
