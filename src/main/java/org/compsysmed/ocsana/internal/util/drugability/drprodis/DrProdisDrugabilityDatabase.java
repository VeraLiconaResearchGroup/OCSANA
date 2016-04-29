/**
 * DR.PRODIS drugability predictions database
 *
 * Copyright Vera-Licona Research Group (C) 2016
 *
 * This software is licensed under the Artistic License 2.0, see the
 * LICENSE file or
 * http://www.opensource.org/licenses/artistic-license-2.0.php for
 * details
 **/

package org.compsysmed.ocsana.internal.util.drugability.drprodis;

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
 * Singleton class representing the DR.PRODIS database
 **/
public class DrProdisDrugabilityDatabase {
    private static final String DRPRODIS_PATH = "/drprodis/drprodis.stripped.json";
    private static final DrProdisDrugabilityDatabase internalDB = new DrProdisDrugabilityDatabase();

    private final Map<String, DrProdisDrugabilityPrediction> predictions = new HashMap<>();

    private DrProdisDrugabilityDatabase () {
        JSONObject drProdisJSON;
        try (InputStream jsonFileStream = getClass().getResourceAsStream(DRPRODIS_PATH)) {
            drProdisJSON = new JSONObject(new JSONTokener(jsonFileStream));
        }
        catch (IOException e) {
            throw new IllegalStateException("Could not find or read DR.PRODIS JSON file");
        }

        // Process prediction records
        Iterator<String> refSeqIDs = drProdisJSON.keys();
        while (refSeqIDs.hasNext()) {
            String refSeqID = refSeqIDs.next();

            JSONObject predictionJSON = drProdisJSON.getJSONObject(refSeqID);

            String drProdisCode = predictionJSON.getString("drprodisCode");
            String magicSubDirectory = predictionJSON.getString("magicSubdir");
            Integer novelDrugCount = predictionJSON.getInt("strongBindingPredictions");
            Integer knownDrugCount = predictionJSON.getInt("knownBindersMatched");

            DrProdisDrugabilityPrediction prediction = new DrProdisDrugabilityPrediction(refSeqID, drProdisCode, magicSubDirectory, novelDrugCount, knownDrugCount);

            predictions.put(refSeqID, prediction);
        }
    }

    /**
     * Retrieve the singleton database instance
     **/
    public static DrProdisDrugabilityDatabase getDB () {
        return internalDB;
    }

    /**
     * Return all UniProt IDs with predictions
     **/
    public Collection<String> getAllScoredProteinIDs () {
        return predictions.keySet();
    }

    /**
     * Return all predictions in the database
     **/
    public Collection<DrProdisDrugabilityPrediction> getAllPredictions () {
        return predictions.values();
    }

    /**
     * Return the prediction for a specified protein
     *
     * @param refSeqID  the RefSeq accession ID of the protein
     * @return the prediction for the protein, if found, or null if not
     **/
    public DrProdisDrugabilityPrediction getPrediction (String refSeqID) {
        refSeqID = refSeqID.split("\\.")[0];
        return predictions.getOrDefault(refSeqID, null);
    }

    /**
     * Return the predictions for a given protein
     *
     * @param protein  the protein
     * @return all predictions found for the given protein
     **/
    public Collection<DrProdisDrugabilityPrediction> getPredictions (Protein protein) {
        return protein.getRefSeqIDs().stream().map(this::getPrediction).filter(Objects::nonNull).collect(Collectors.toList());
    }

}
