/**
 * Test cases for the drug-gene interaction database class
 *
 * Copyright Vera-Licona Research Group (C) 2015
 *
 * This software is licensed under the Artistic License 2.0, see the
 * LICENSE file or
 * http://www.opensource.org/licenses/artistic-license-2.0.php for
 * details
 **/

package org.compsysmed.ocsana.internal.util.drugability.drugbank;

// Java imports
import java.util.*;

// JUnit imports
import org.junit.*;
import static org.junit.Assert.*;

// OCSANA imports
import org.compsysmed.ocsana.internal.util.drugability.drugbank.DrugBankInteractionsDatabase;

import org.compsysmed.ocsana.internal.util.science.*;

public class DrugBankInteractionsDatabaseTest {
    @Test
    public void buildDatabaseShouldWork () {
        DrugBankInteractionsDatabase db = DrugBankInteractionsDatabase.getDB();
        assertEquals("Number of drugs in database", 8203, db.getAllDrugs().size());
    }

    @Test
    public void retrieveDrugShouldWork () {
        DrugBankInteractionsDatabase db = DrugBankInteractionsDatabase.getDB();
        Drug barbital = db.getDrugByID("DB01483");
        assertEquals("Name of drug", "Barbital", barbital.getName());
        assertEquals("Number of FDA categories", 1, barbital.getFDACategories().size());
    }
}
