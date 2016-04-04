/**
 * Test cases for the proteins database class
 *
 * Copyright Vera-Licona Research Group (C) 2016
 *
 * This software is licensed under the Artistic License 2.0, see the
 * LICENSE file or
 * http://www.opensource.org/licenses/artistic-license-2.0.php for
 * details
 **/

package org.compsysmed.ocsana.internal.util.science.uniprot;

// Java imports
import java.util.*;

// JUnit imports
import org.junit.*;
import static org.junit.Assert.*;

// OCSANA imports
import org.compsysmed.ocsana.internal.util.science.*;

public class ProteinDatabaseTest {
    @Test
    public void buildDatabaseShouldWork () {
        ProteinDatabase proteinDB = ProteinDatabase.getDB();
        assertEquals("Number of proteins in database", 70236, proteinDB.getAllProteins().size());
    }

    @Test
    public void getProteinShouldWork () {
        ProteinDatabase proteinDB = ProteinDatabase.getDB();
        Protein protein = proteinDB.getProteinByID("P48169");

        assertEquals("UniProt ID", "P48169", protein.getUniProtID());
        assertEquals("Protein name", "Gamma-aminobutyric acid receptor subunit alpha-4", protein.getName());
        assertEquals("Number of genes", 1, protein.getGeneNames().size());
    }
}
