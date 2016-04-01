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
    ACETYLATION ("acetylation", InteractionSign.UNSIGNED),
    ACTIVATOR ("activator", InteractionSign.POSITIVE),
    ADDUCT ("adduct", InteractionSign.NEGATIVE),
    AGONIST ("agonist", InteractionSign.POSITIVE),
    ALLOSTERIC_MODULATOR ("allosteric modulator", InteractionSign.UNSIGNED),
    ANTAGONIST ("antagonist", InteractionSign.NEGATIVE),
    ANTIBODY ("antibody", InteractionSign.NEGATIVE),
    BLOCKER ("blocker", InteractionSign.NEGATIVE),
    BINDER ("binder", InteractionSign.UNSIGNED),
    BINDING ("binding", InteractionSign.UNSIGNED),
    CHAPERONE ("chaperone", InteractionSign.UNSIGNED),
    CHELATOR ("chelator", InteractionSign.NEGATIVE),
    CLEAVAGE ("cleavage", InteractionSign.NEGATIVE),
    COFACTOR ("cofactor", InteractionSign.UNSIGNED),
    CROSS_LINKING ("cross-linking/alkylation", InteractionSign.NEGATIVE),
    DESENSITIZE_THE_TARGET ("desensitize the target", InteractionSign.NEGATIVE),
    INCORPORATION_INTO_AND_DESTABILIZATION ("incorporation into and destabilization", InteractionSign.NEGATIVE),
    INDUCER ("inducer", InteractionSign.POSITIVE),
    INHIBITOR ("inhibitor", InteractionSign.NEGATIVE),
    COMPETITIVE_INHIBITOR ("inhibitor (competitive)", InteractionSign.NEGATIVE),
    INHIBITORY_ALLOSTERIC_MODULATOR ("inhibitory allosteric modulator", InteractionSign.NEGATIVE),
    INTERCALATION ("intercalation", InteractionSign.NEGATIVE),
    INVERSE_AGONIST ("inverse agonist", InteractionSign.NEGATIVE),
    LIGAND ("ligand", InteractionSign.POSITIVE),
    MODULATOR ("modulator", InteractionSign.UNSIGNED),
    MULTITARGET ("multitarget", InteractionSign.UNSIGNED),
    NEGATIVE_MODULATOR ("negative modulator", InteractionSign.NEGATIVE),
    NEUTRALIZER ("neutralizer", InteractionSign.NEGATIVE),
    OTHER ("other", InteractionSign.UNSIGNED),
    PARTIAL_AGONIST ("partial agonist", InteractionSign.POSITIVE),
    PARTIAL_ANTAGONIST ("partial antagonist", InteractionSign.UNSIGNED),
    POSITIVE_ALLOSTERIC_MODULATOR ("positive allosteric modulator", InteractionSign.POSITIVE),
    POSITIVE_MODULATOR ("positive modulator", InteractionSign.POSITIVE),
    POTENTIATOR ("potentiator", InteractionSign.POSITIVE),
    PRODUCT_OF ("product of", InteractionSign.UNSIGNED),
    REDUCER ("reducer", InteractionSign.NEGATIVE),
    STIMULATOR ("stimulator", InteractionSign.POSITIVE),
    SUPPRESSOR ("suppressor", InteractionSign.NEGATIVE),
    UNKNOWN ("unknown", InteractionSign.UNSIGNED);

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
    private InteractionSign sign;

    private DrugActionType (String description,
                            InteractionSign sign) {
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
    public InteractionSign getSign () {
        return sign;
    }

}
