/**
 * Wrapper class for drugability data about a protein
 *
 * Copyright Vera-Licona Research Group (C) 2017
 *
 * This software is licensed under the Artistic License 2.0, see the
 * LICENSE file or
 * http://www.opensource.org/licenses/artistic-license-2.0.php for
 * details
 **/

package org.compsysmed.ocsana.internal.util.drugability;

// Java imports
import java.util.*;

// OCSANA imports
import org.compsysmed.ocsana.internal.util.science.*;

public class DrugabilityDataBundle {
    private Protein protein;

    public DrugabilityDataBundle (Protein protein) {
        this.protein = protein;
    }
}
