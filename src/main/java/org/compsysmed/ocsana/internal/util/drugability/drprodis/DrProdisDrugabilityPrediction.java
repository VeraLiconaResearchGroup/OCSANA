/**
 * Class representing the DR.PRODUS drugability prediction for a protein
 *
 * Copyright Vera-Licona Research Group (C) 2016
 *
 * This software is licensed under the Artistic License 2.0, see the
 * LICENSE file or
 * http://www.opensource.org/licenses/artistic-license-2.0.php for
 * details
 **/

package org.compsysmed.ocsana.internal.util.drugability.drprodis;

import java.net.URL;
import java.util.Objects;
import java.net.MalformedURLException;


/**
 * Class representing the DR.PRODUS drugability prediction for a protein
 **/
public class DrProdisDrugabilityPrediction {
    private static final String DRPRODIS_URL_BASE = "http://cssb2.biology.gatech.edu/FINDTA/doc/";

    private final String refSeqID;
    private final String drProdisCode;
    private final String magicSubDirectory;
    private final Integer novelDrugCount;
    private final Integer knownDrugCount;

    /**
     * Constructor
     *
     * @param refSeqID  the RefSeq accession ID of the protein
     * @param drProdisCode  the ID of this protein in the DR.PRODIS
     * database
     * @param magicSubDirectory  the subdirectory where this protein
     * appears on the DR.PRODIS site
     * @param novelDrugCount  the number of novel drugs predicted to
     * bind strongly to this protein
     * @param knownDrugCount  the number of known drugs predicted to
     * bind strongly to this protein
     **/
    public DrProdisDrugabilityPrediction (String refSeqID,
                                          String drProdisCode,
                                          String magicSubDirectory,
                                          Integer novelDrugCount,
                                          Integer knownDrugCount) {
    	Objects.requireNonNull(refSeqID, "RefSeq ID cannot be null");
        this.refSeqID = refSeqID;

        Objects.requireNonNull(drProdisCode, "DR.PRODIS code cannot be null");
        this.drProdisCode = drProdisCode;

        Objects.requireNonNull(magicSubDirectory, "Subdirectory cannot be null");
        this.magicSubDirectory = magicSubDirectory;

        if (novelDrugCount == null || novelDrugCount < 0) {
            throw new IllegalArgumentException("Novel drug count cannot be null or negative.");
        }
        this.novelDrugCount = novelDrugCount;

        if (knownDrugCount == null || knownDrugCount < 0) {
            throw new IllegalArgumentException("Known drug count cannot be null or negative.");
        }
        this.knownDrugCount = knownDrugCount;
    }

    /**
     * Return the RefSeq ID for this prediction
     **/
    public String getRefSeqID () {
        return refSeqID;
    }

    /**
     * Return the URL for the online DR.PRODIS entry of this protein
     *
     * NOTE: The DR.PRODIS database is subject to restrictions on
     * commercial use. Callers which present this URL to the user
     * should notify them of these restrictions. See
     * http://cssb.biology.gatech.edu/repurpose for details.
     **/
    public URL getDrProdisURL () {
        String drProdisURL = String.format("%s/%s/%s.html", DRPRODIS_URL_BASE, magicSubDirectory, drProdisCode);
        try {
            return new URL(drProdisURL);
        } catch (MalformedURLException e) {
            throw new IllegalStateException(String.format("DR.PRODIS URL %s is malformed", drProdisURL));
        }
    }

    /**
     * Return the number of novel drugs which are predicted to bind
     * strongly to this protein
     **/
    public Integer getCountOfNovelBindingDrugs () {
        return novelDrugCount;
    }

    /**
     * Return the number of known drugs which are predicted to bind
     * strongly to this protein
     **/
    public Integer getCountOfKnownBindingDrugs () {
        return knownDrugCount;
    }
 }
