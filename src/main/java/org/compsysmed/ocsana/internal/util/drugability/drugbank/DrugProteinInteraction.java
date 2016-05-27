/**
 * Class representing an action of a drug on a protein
 *
 * Copyright Vera-Licona Research Group (C) 2016
 *
 * This software is licensed under the Artistic License 2.0, see the
 * LICENSE file or
 * http://www.opensource.org/licenses/artistic-license-2.0.php for
 * details
 **/

package org.compsysmed.ocsana.internal.util.drugability.drugbank;

// OCSANA imports
import org.compsysmed.ocsana.internal.util.science.*;

/**
 * Class representing an action of a protein on a drug
 **/
public class DrugProteinInteraction {
    private final Drug drug;
    private final Protein protein;
    private final DrugActionType action;

    public DrugProteinInteraction (Drug drug,
                                   Protein protein,
                                   String action) {
        if (drug == null) {
            throw new IllegalArgumentException("Drug cannot be null");
        }
        this.drug = drug;

        if (protein == null) {
            throw new IllegalArgumentException("Protein cannot be null");
        }
        this.protein = protein;

        this.action = DrugActionType.getByDescription(action);
        if (this.action == null) {
            throw new IllegalArgumentException(String.format("Could not find action type for %s", action));
        }
    }

    public Drug getDrug () {
        return drug;
    }

    public Protein getProtein () {
        return protein;
    }

    public DrugActionType getDrugActionType () {
        return action;
    }
}
