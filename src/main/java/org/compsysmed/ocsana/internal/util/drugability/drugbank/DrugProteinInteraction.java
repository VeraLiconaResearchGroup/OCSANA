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

// Java imports
import java.util.*;
import java.util.stream.Collectors;

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
        this.drug = drug;
        this.protein = protein;
        this.action = DrugActionType.getByDescription(action);
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
