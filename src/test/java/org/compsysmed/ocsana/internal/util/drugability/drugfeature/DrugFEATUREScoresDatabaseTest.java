/**
 * Test cases for the DrugFEATURE database
 *
 * Copyright Vera-Licona Research Group (C) 2016
 *
 * This software is licensed under the Artistic License 2.0, see the
 * LICENSE file or
 * http://www.opensource.org/licenses/artistic-license-2.0.php for
 * details
 **/

package org.compsysmed.ocsana.internal.util.drugability.drugfeature;

// Java imports
import java.util.*;
import java.util.stream.Collectors;

// JUnit imports
import org.junit.*;
import static org.junit.Assert.*;

// OCSANA imports
import org.compsysmed.ocsana.internal.util.science.uniprot.ProteinDatabase;

import org.compsysmed.ocsana.internal.util.drugability.drugfeature.*;

public class DrugFEATUREScoresDatabaseTest {
    @Test
    public void buildDatabaseShouldWork () {
        DrugFEATUREScoresDatabase db = DrugFEATUREScoresDatabase.getDB();
        assertEquals("Number of proteins with DrugFEATURE scored ligands", 3216, db.getAllScoredProteinIDs().size());
    }

    @Test
    public void getScoredLigandsShouldWork () {
        DrugFEATUREScoresDatabase db = DrugFEATUREScoresDatabase.getDB();
        Collection<DrugFEATURELigand> ligands = db.getLigands("P04626");
        assertEquals("Number of ligands", 50, ligands.size());

        Collection<DrugFEATURELigand> drugableLigands = ligands.stream().filter(ligand -> ligand.isDrugable()).collect(Collectors.toList());
        assertEquals("Number of drugable ligands", 14, drugableLigands.size());
    }
}
