/**
 * Test cases for the drug-gene interaction database class
 *
 * Copyright Vera-Licona Research Group (C) 2015
 **/

package org.compsysmed.ocsana.internal.util.drugbank;

// Java imports
import java.util.*;
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
        assertEquals("Number of genes in database", 3550, db.numGenes());
        assertTrue("ERBB2 has antagonist Lapatanib", db.drugInteractionGroupsForGene("erbb2").get("antagonist").contains("Lapatinib"));
        assertEquals("P04626 is ERBB2", "ERBB2", db.convertUniProtIDToGeneName("P04626"));
    }

    @Test
    public void emptyDrugNamesForBadGene () {
        InteractionsDatabase db = InteractionsDatabase.getDB();
        Set<String> bogusGeneNames = db.drugNamesForGene("BogusGene");
        assertNotNull("Set of gene names", bogusGeneNames);
        assertTrue("Set of gene names should be empty", bogusGeneNames.isEmpty());
    }
}
