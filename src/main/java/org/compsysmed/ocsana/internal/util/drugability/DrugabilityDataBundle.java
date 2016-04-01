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
import org.compsysmed.ocsana.internal.util.drugability.drugbank.*;

import org.compsysmed.ocsana.internal.util.science.*;

public class DrugabilityDataBundle {
    private final Protein protein;
    private final Collection<DrugProteinInteraction> interactions;

    /**
     * Constructor
     *
     * @param protein  the protein this bundle represents
     * @param interactions  the known drug-protein interactions for this protein
     **/
    public DrugabilityDataBundle (Protein protein,
                                  Collection<DrugProteinInteraction> interactions) {
        if (protein == null) {
            throw new IllegalArgumentException("Protein cannot be null.");
        }
        this.protein = protein;

        if (interactions == null) {
            throw new IllegalArgumentException("Interactions set cannot be null.");
        }
        this.interactions = interactions;
    }

    /**
     * Return the protein
     **/
    public Protein getProtein () {
        return protein;
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
}
