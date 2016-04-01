/**
 * Enum of types of drug actions
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

// OCSANA imports
import org.compsysmed.ocsana.internal.util.science.*;

/**
 * Enum of types of drug actions in the DrugBank database
 **/
public enum DrugActionType {
    ACETYLATION ("acetylation", DrugActionSign.UNSIGNED),
    ACTIVATOR ("activator", DrugActionSign.POSITIVE),
    ADDUCT ("adduct", DrugActionSign.NEGATIVE),
    AGONIST ("agonist", DrugActionSign.POSITIVE),
    ALLOSTERIC_MODULATOR ("allosteric modulator", DrugActionSign.UNSIGNED),
    ANTAGONIST ("antagonist", DrugActionSign.NEGATIVE),
    ANTIBODY ("antibody", DrugActionSign.NEGATIVE),
    BLOCKER ("blocker", DrugActionSign.NEGATIVE),
    BINDER ("binder", DrugActionSign.UNSIGNED),
    BINDING ("binding", DrugActionSign.UNSIGNED),
    CHAPERONE ("chaperone", DrugActionSign.UNSIGNED),
    CHELATOR ("chelator", DrugActionSign.NEGATIVE),
    CLEAVAGE ("cleavage", DrugActionSign.NEGATIVE),
    COFACTOR ("cofactor", DrugActionSign.UNSIGNED),
    CROSS_LINKING ("cross-linking/alkylation", DrugActionSign.NEGATIVE),
    DESENSITIZE_THE_TARGET ("desensitize the target", DrugActionSign.NEGATIVE),
    INCORPORATION_INTO_AND_DESTABILIZATION ("incorporation into and destabilization", DrugActionSign.NEGATIVE),
    INDUCER ("inducer", DrugActionSign.POSITIVE),
    INHIBITOR ("inhibitor", DrugActionSign.NEGATIVE),
    COMPETITIVE_INHIBITOR ("inhibitor (competitive)", DrugActionSign.NEGATIVE),
    INHIBITORY_ALLOSTERIC_MODULATOR ("inhibitory allosteric modulator", DrugActionSign.NEGATIVE),
    INTERCALATION ("intercalation", DrugActionSign.NEGATIVE),
    INVERSE_AGONIST ("inverse agonist", DrugActionSign.NEGATIVE),
    LIGAND ("ligand", DrugActionSign.POSITIVE),
    MODULATOR ("modulator", DrugActionSign.UNSIGNED),
    MULTITARGET ("multitarget", DrugActionSign.UNSIGNED),
    NEGATIVE_MODULATOR ("negative modulator", DrugActionSign.NEGATIVE),
    NEUTRALIZER ("neutralizer", DrugActionSign.NEGATIVE),
    OTHER ("other", DrugActionSign.UNSIGNED),
    PARTIAL_AGONIST ("partial agonist", DrugActionSign.POSITIVE),
    PARTIAL_ANTAGONIST ("partial antagonist", DrugActionSign.UNSIGNED),
    POSITIVE_ALLOSTERIC_MODULATOR ("positive allosteric modulator", DrugActionSign.POSITIVE),
    POSITIVE_MODULATOR ("positive modulator", DrugActionSign.POSITIVE),
    POTENTIATOR ("potentiator", DrugActionSign.POSITIVE),
    PRODUCT_OF ("product of", DrugActionSign.UNSIGNED),
    REDUCER ("reducer", DrugActionSign.NEGATIVE),
    STIMULATOR ("stimulator", DrugActionSign.POSITIVE),
    SUPPRESSOR ("suppressor", DrugActionSign.NEGATIVE),
    UNKNOWN ("unknown", DrugActionSign.UNSIGNED);

    private static final Map<String, DrugActionType> lookupByDescription = new HashMap<>();

    static {
        for (DrugActionType type: EnumSet.allOf(DrugActionType.class)) {
            lookupByDescription.put(type.getDescription(), type);
        }
    }

    public static DrugActionType getByDescription (String description) {
        return lookupByDescription.get(description.toLowerCase());
    }

    private String description;
    private DrugActionSign sign;

    private DrugActionType (String description,
                            DrugActionSign sign) {
        this.description = description;
        this.sign = sign;
    }

    /**
     * Get the text description of this action type
     **/
    public String getDescription () {
        return description;
    }

    /**
     * Get the sign associated to this action type
     **/
    public DrugActionSign getSign () {
        return sign;
    }

}
