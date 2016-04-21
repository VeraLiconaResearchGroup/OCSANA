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

// Java imports
import java.util.*;
import java.util.stream.Collectors;

import java.net.URL;
import java.net.MalformedURLException;

// OCSANA imports
import org.compsysmed.ocsana.internal.util.science.*;


/**
 * Class representing the DR.PRODUS drugability prediction for a protein
 **/
public class DrProdisDrugabilityPrediction {
    private static final String DRPRODIS_URL_BASE = "http://cssb2.biology.gatech.edu/FINDTA/doc/";

    private final Protein protein;
    private final String drProdisCode;
    private final String magicSubDirectory;
    private final Integer drugCount;

    /**
     * Constructor
     *
     * @param protein  the protein
     * @param drProdisCode  the ID of this protein in the DR.PRODIS
     * database
     * @param magicSubDirectory  the subdirectory where this protein
     * appears on the DR.PRODIS site
     * @param drugCount  the number of drugs predicted to bind strongly
     * to this protein
     **/
    public DrProdisDrugabilityPrediction (Protein protein,
                                          String drProdisCode,
                                          String magicSubDirectory,
                                          Integer drugCount) {
        if (protein == null) {
            throw new IllegalArgumentException("Protein cannot be null");
        }
        this.protein = protein;

        if (drProdisCode == null) {
            throw new IllegalArgumentException("DR.PRODIS code cannot be null");
        }
        this.drProdisCode = drProdisCode;

        if (magicSubDirectory == null) {
            throw new IllegalArgumentException("Subdirectory cannot be null");
        }
        this.magicSubDirectory = magicSubDirectory;

        if (drugCount == null || drugCount < 0) {
            throw new IllegalArgumentException("Drug count cannot be null or negative.");
        }
        this.drugCount = drugCount;
    }

    /**
     * Return the protein for this prediction
     **/
    public Protein getProtein () {
        return protein;
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
     * Return the number of drugs which are predicted to bind strongly
     * to this protein
     **/
    public Integer getCountOfBindingDrugs () {
        return drugCount;
    }
 }
