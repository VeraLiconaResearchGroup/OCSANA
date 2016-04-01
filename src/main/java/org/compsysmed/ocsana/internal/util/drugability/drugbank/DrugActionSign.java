/**
 * Enum of signs of drug actions
 *
 * Copyright Vera-Licona Research Group (C) 2015
 *
 * This software is licensed under the Artistic License 2.0, see the
 * LICENSE file or
 * http://www.opensource.org/licenses/artistic-license-2.0.php for
 * details
 **/

package org.compsysmed.ocsana.internal.util.drugability.drugbank;

public enum DrugActionSign {
    POSITIVE ("positive", '+'),
    NEGATIVE ("negative", '-'),
    UNSIGNED ("unsigned", '±');

    private final String description;
    private final Character symbol;

    private DrugActionSign (String description,
                            Character symbol) {
        this.description = description;
        this.symbol = symbol;
    }

    public String getDescription () {
        return description;
    }

    public Character getSymbol () {
        return symbol;
    }
}
