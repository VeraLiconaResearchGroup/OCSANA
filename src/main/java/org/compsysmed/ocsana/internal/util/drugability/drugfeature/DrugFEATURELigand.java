/**
 * Class representing a protein ligand and its DrugFEATURE drugability score
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
import java.util.*;

import java.net.URL;
import java.net.MalformedURLException;

/**
 * Class representing the DrugFEATURE scoring of a ligand on a protein
 **/
public class DrugFEATURELigand {
    private static final String PDB_URL_BASE = "http://www.rcsb.org/pdb/explore.do?structureId=";

    private final String uniProtID;
    private final String pdbID;
    private final Character chain;
    private final Integer index;
    private final String ligandName;
    private final Double score;

    /**
     * Constructor
     *
     * @param uniProtID  the UniProt ID of the protein
     * @param pdbID  the ID of the PDB record for this ligand
     * @param chain  the protein chain of this ligand
     * @param index  the index of the ligand on the chain
     * @param ligandName  the abbreviated name of the ligand chemical
     * @param score  the score
     **/
    public DrugFEATURELigand (String uniProtID,
                              String pdbID,
                              Character chain,
                              Integer index,
                              String ligandName,
                              Double score) {
        if (uniProtID == null) {
            throw new IllegalArgumentException("UniProt ID cannot be null");
        }
        this.uniProtID = uniProtID;

        if (pdbID == null) {
            throw new IllegalArgumentException("PDB ID cannot be null");
        }
        this.pdbID = pdbID;

        if (chain == null) {
            throw new IllegalArgumentException("Chain cannot be null");
        }
        this.chain = chain;

        if (index == null) {
            throw new IllegalArgumentException("Index cannot be null");
        }
        this.index = index;

        if (ligandName == null) {
            throw new IllegalArgumentException("Ligand name cannot be null");
        }
        this.ligandName = ligandName;

        if (score == null) {
            throw new IllegalArgumentException("Score cannot be null");
        }
        this.score = score;
    }

    /**
     * Return the UniProt ID
     **/
    public String getUniProtID () {
        return uniProtID;
    }

    /**
     * Return the PDB ID
     **/
    public String getPDBID () {
        return pdbID;
    }

    /**
     * Return the URL for the online PDB entry
     **/
    public URL getPDBURL () {
        String pdbURL = PDB_URL_BASE + getPDBID();
        try {
            return new URL(pdbURL);
        } catch (MalformedURLException e) {
            throw new IllegalStateException(String.format("PDB URL %s is malformed", pdbURL));
        }
    }

    /**
     * Return the chain
     **/
    public Character getChain () {
        return chain;
    }

    /**
     * Return the index
     **/
    public Integer getIndex () {
        return index;
    }

    /**
     * Return the ligand name
     **/
    public String getLigandName () {
        return ligandName;
    }

    /**
     * Return the score
     **/
    public Double getScore () {
        return score;
    }

    /**
     * Return whether the score qualifies this ligand as druggable
     * <p>
     * NOTE: per the original DrugFEATURE paper, this tests whether
     * the score is greater than 1.9.
     **/
    public Boolean isDrugable () {
        return (getScore() > 1.9);
    }
}
