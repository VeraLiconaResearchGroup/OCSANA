/**
 * Test cases for the drug-gene interaction database class
 *
 * Copyright Vera-Licona Research Group (C) 2015
 **/

package org.compsysmed.ocsana.internal.util.drugbank;

// Java imports
import java.io.IOException;

// JUnit imports
import org.junit.*;
import static org.junit.Assert.*;

// OCSANA imports
import org.compsysmed.ocsana.internal.util.drugbank.InteractionsDatabase;

public class InteractionsDatabaseTest {
    @Test
    public void buildDatabaseShouldWork () {
        InteractionsDatabase db = InteractionsDatabase.getDB();
        assertEquals("Number of genes in database", 1142, db.genes.size());
    }
}
