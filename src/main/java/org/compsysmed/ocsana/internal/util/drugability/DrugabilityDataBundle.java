/**
 * Wrapper class for drugability data about a protein
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
import java.util.stream.Collectors;

// OCSANA imports
import org.compsysmed.ocsana.internal.util.drugability.drprodis.DrProdisDrugabilityPrediction;

import org.compsysmed.ocsana.internal.util.drugability.drugbank.*;

import org.compsysmed.ocsana.internal.util.drugability.drugfeature.DrugFEATURELigand;

import org.compsysmed.ocsana.internal.util.science.*;

/**
 * Class containing data about the drugability of a protein or isoform
 **/
public class DrugabilityDataBundle {
    private final Protein protein;
    private final Isoform isoform;
    private final Collection<DrProdisDrugabilityPrediction> drProdisPredictions;
    private final Collection<DrugProteinInteraction> interactions;
    private final Collection<DrugFEATURELigand> ligands;

    /**
     * Constructor
     *
     * @param protein  the protein this bundle represents
     * @param drProdisPredictions  the DR.PRODIS predictions for this
     * protein
     * @param interactions  the known drug-protein interactions for this protein
     * @param ligands  the scored ligands of this protein in the PDB
     **/
    public DrugabilityDataBundle (Protein protein,
                                  Collection<DrProdisDrugabilityPrediction> drProdisPredictions,
                                  Collection<DrugProteinInteraction> interactions,
                                  Collection<DrugFEATURELigand> ligands) {
        Objects.requireNonNull(protein, "Protein cannot be null.");
        this.protein = protein;
        this.isoform = null;

        Objects.requireNonNull(drProdisPredictions, "DR.PRODIS predictions collection cannot be null");
        this.drProdisPredictions = drProdisPredictions;

        // TODO: check whether interactions are for correct protein
        Objects.requireNonNull(interactions, "Interactions set cannot be null.");
        this.interactions = interactions;

        // TODO: check whether ligands are for correct protein
        Objects.requireNonNull(ligands, "Ligands collection cannot be null.");
        this.ligands = ligands;
    }

    /**
     * Constructor
     *
     * @param isoform  the isoform this bundle represents
     * @param drProdisPredictions  the DR.PRODIS predictions for this
     * protein
     * @param interactions  the known drug-protein interactions for this protein
     * @param ligands  the scored ligands of this protein in the PDB
     **/
    public DrugabilityDataBundle (Isoform isoform,
                                  Collection<DrProdisDrugabilityPrediction> drProdisPredictions,
                                  Collection<DrugProteinInteraction> interactions,
                                  Collection<DrugFEATURELigand> ligands) {
        Objects.requireNonNull(isoform, "Isoform cannot be null.");
        this.isoform = isoform;
        this.protein = isoform.getProtein();

        Objects.requireNonNull(drProdisPredictions, "DR.PRODIS predictions collection cannot be null");
        this.drProdisPredictions = drProdisPredictions;

        // TODO: check whether interactions are for correct protein
        Objects.requireNonNull(interactions, "Interactions collection cannot be null.");
        this.interactions = interactions;

        // TODO: check whether ligands are for correct protein
        Objects.requireNonNull(ligands, "Ligands collection cannot be null.");
        this.ligands = ligands;
    }

    /**
     * Return the protein
     **/
    public Protein getProtein () {
        return protein;
    }

    /**
     * Return the isoform, or null if this bundle is only for a protein
     **/
    public Isoform getIsoform () {
        return isoform;
    }

    /**
     * Return the DR.PRODIS predictions for the protein
     **/
    public Collection<DrProdisDrugabilityPrediction> getDrProdisPredictions () {
        return drProdisPredictions;
    }

    /**
     * Get all the known drug-protein interactions
     **/
    public Collection<DrugProteinInteraction> getAllInteractions () {
        return interactions;
    }

    /**
     * Get all the positive known drug-protein interactions
     * <p>
     * (NOTE: convenience wrapper for {@link #getAllInteractionsOfSign})
     **/
    public Collection<DrugProteinInteraction> getAllPositiveInteractions () {
        return getAllInteractionsOfSign(InteractionSign.POSITIVE);
    }

    /**
     * Get all the negative known drug-protein interactions
     * <p>
     * (NOTE: convenience wrapper for {@link #getAllInteractionsOfSign})
     **/
    public Collection<DrugProteinInteraction> getAllNegativeInteractions () {
        return getAllInteractionsOfSign(InteractionSign.NEGATIVE);
    }

    /**
     * Get all known drug-protein interactions with a specified sign
     *
     * @param sign  the sign
     **/
    public Collection<DrugProteinInteraction> getAllInteractionsOfSign (InteractionSign sign) {
        return interactions.stream().filter(interaction -> interaction.getDrugActionType().getSign() == sign).collect(Collectors.toSet());
    }

    /**
     * Get all known drug-protein interactions with sign other than
     * the one specifiedd
     *
     * @param sign  the sign
     **/
    public Collection<DrugProteinInteraction> getAllInteractionsNotOfSign (InteractionSign sign) {
        return interactions.stream().filter(interaction -> interaction.getDrugActionType().getSign() != sign).collect(Collectors.toSet());
    }

    /**
     * Return the ligands
     **/
    public Collection<DrugFEATURELigand> getAllLigands () {
        return ligands;
    }

    /**
     * Return drugable ligands
     **/
    public Collection<DrugFEATURELigand> getDrugableLigands () {
        return getAllLigands().stream().filter(ligand -> ligand.isDrugable()).collect(Collectors.toList());
    }

    /**
     * Test whether any ligand is drugable
     **/
    public Boolean hasDrugableLigand () {
        return getAllLigands().stream().anyMatch(ligand -> ligand.isDrugable());
    }
}
