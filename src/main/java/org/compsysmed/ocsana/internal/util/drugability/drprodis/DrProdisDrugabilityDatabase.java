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

import org.json.JSONObject;
import org.json.JSONTokener;

// OCSANA imports
import org.compsysmed.ocsana.internal.util.science.*;

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
     * Return all RefSeq IDs with predictions
     **/
    public Collection<String> getAllScoredIsoformIDs () {
        return predictions.keySet();
    }

    /**
     * Return all predictions in the database
     **/
    public Collection<DrProdisDrugabilityPrediction> getAllPredictions () {
        return predictions.values();
    }

    /**
     * Return the predictions for a specified protein isoform
     *
     * @param isoform  the isoform
     * @return all predictions found for the isoform
     **/
    public Collection<DrProdisDrugabilityPrediction> getPredictions (Isoform isoform) {
        return isoform.getRefSeqIDs().stream().map(refSeqID -> predictions.getOrDefault(refSeqID, null)).filter(Objects::nonNull).collect(Collectors.toList());
    }
}
