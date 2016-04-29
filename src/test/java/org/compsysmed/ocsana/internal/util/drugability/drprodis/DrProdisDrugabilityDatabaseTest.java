/**
 * Test cases for the DR.PRODIS drugability database
 *
 * Copyright Vera-Licona Research Group (C) 2016
 *
 * This software is licensed under the Artistic License 2.0, see the
 * LICENSE file or
 * http://www.opensource.org/licenses/artistic-license-2.0.php for
 * details
 **/

package org.compsysmed.ocsana.internal.util.drugability.drprodis;

// Java imports
import java.util.*;

// JUnit imports
import org.junit.*;
import static org.junit.Assert.*;

// OCSANA imports
import org.compsysmed.ocsana.internal.util.drugability.drprodis.*;

import org.compsysmed.ocsana.internal.util.science.*;

public class DrProdisDrugabilityDatabaseTest {
    @Test
    public void buildDatabaseShouldWork () {
        DrProdisDrugabilityDatabase db = DrProdisDrugabilityDatabase.getDB();
        assertEquals("Number of predictions in database", 32584, db.getAllPredictions().size());
    }

    @Test
    public void retrievePredictionShouldWork () {
        DrProdisDrugabilityDatabase db = DrProdisDrugabilityDatabase.getDB();
        DrProdisDrugabilityPrediction prediction = db.getPrediction("NP_001005862");
        assertEquals("Number of novel predicted binders of erbB-2 isoform b", (Integer) 14, prediction.getCountOfNovelBindingDrugs());
        assertEquals("Number of known predicted binders of erbB-2 isoform b", (Integer) 15, prediction.getCountOfKnownBindingDrugs());
    }
}
