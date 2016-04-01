/**
 * Class representing a protein molecule
 *
 * Copyright Vera-Licona Research Group (C) 2016
 *
 * This software is licensed under the Artistic License 2.0, see the
 * LICENSE file or
 * http://www.opensource.org/licenses/artistic-license-2.0.php for
 * details
 **/

package org.compsysmed.ocsana.internal.util.science;

// Java imports
import java.util.*;

import java.net.URL;
import java.net.MalformedURLException;

/**
 * Class representing a protein molecule
 **/
public class Protein {
    private static final String UNIPROT_URL_BASE = "http://www.uniprot.org/uniprot/";

    private final String uniProtID;
    private final String name;
    private final Set<String> geneNames;
    private final String generalFunction;
    private final String specificFunction;

    /**
     * Constructor
     *
     * @param uniProtID  the UniProt ID of the protein
     * @param name  the human-readable name of the protein
     * @param geneNames  the ENSEMBL IDs of the genes associated to the protein
     * @param generalFunction  string description of the general
     * function of the protein (can be null, in which case an empty string is
     * stored)
     * @param specificFunction  string description of the specific
     * function of the protein (can be null, in which case an empty string is
     * stored)
     **/
    public Protein (String uniProtID,
                    String name,
                    Set<String> geneNames,
                    String generalFunction,
                    String specificFunction) {
        if (uniProtID == null) {
            throw new IllegalArgumentException("Protein UniProt ID cannot be null");
        }
        this.uniProtID = uniProtID;

        if (name == null) {
            throw new IllegalArgumentException("Protein name cannot be null");
        }
        this.name = name;

        if (geneNames == null) {
            throw new IllegalArgumentException("List of gene names cannot be null");
        }
        this.geneNames = geneNames;

        if (generalFunction == null) {
            generalFunction = "";
        }
        this.generalFunction = generalFunction;

        if (specificFunction == null) {
            specificFunction = "";
        }
        this.specificFunction = specificFunction;
    }

    /**
     * Get the UniProt ID of this protein
     **/
    public String getUniProtID () {
        return uniProtID;
    }

    /**
     * Get the name of this protein
     **/
    public String getName () {
        return name;
    }

    /**
     * Get the gene names associated to this protein
     **/
    public Set<String> getGeneNames () {
        return geneNames;
    }

    /**
     * Get a description of the general function of this protein (may
     * be empty)
     **/
    public String getGeneralFunction () {
        return generalFunction;
    }

    /**
     * Get a description of the specific function of this protein (may
     * be empty)
     **/
    public String getSpecificFunction () {
        return specificFunction;
    }

    /**
     * Return a string representation of this protein
     **/
    public String toString () {
        return String.format("%s (%s)", name, uniProtID);
    }

    /**
     * Return the URL for the online database entry of this protein
     **/
    public URL getUniProtURL () {
        String uniProtURL = UNIPROT_URL_BASE + uniProtID;
        try {
            return new URL(uniProtURL);
        } catch (MalformedURLException e) {
            throw new IllegalStateException(String.format("Protein URL %s is malformed", uniProtURL));
        }
    }
}
