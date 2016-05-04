/**
 * Test cases for the drugability data bundle class
 *
 * Copyright Vera-Licona Research Group (C) 2016
 *
 * This software is licensed under the Artistic License 2.0, see the
 * LICENSE file or
 * http://www.opensource.org/licenses/artistic-license-2.0.php for
 * details
 **/

package org.compsysmed.ocsana.internal.util.drugability;

// Java imports
import java.util.*;

// JUnit imports
import org.junit.*;
import static org.junit.Assert.*;

// OCSANA imports
import org.compsysmed.ocsana.internal.util.science.*;
import org.compsysmed.ocsana.internal.util.drugability.*;
import org.compsysmed.ocsana.internal.util.drugability.drugbank.*;

public class DrugabilityDataBundleTest {
    @Test
    public void getProteinShouldWork () {
        DrugabilityDataBundleFactory factory = new DrugabilityDataBundleFactory();
        DrugabilityDataBundle bundle = factory.getBundle("P48169");
        Protein protein = bundle.getProtein();

        assertEquals("UniProt ID", "P48169", protein.getUniProtID());
        assertEquals("Protein name", "Gamma-aminobutyric acid receptor subunit alpha-4", protein.getName());
        assertEquals("Number of genes", 1, protein.getGeneNames().size());
    }

    @Test
    public void getInteractionsShouldWork () {
        DrugabilityDataBundleFactory factory = new DrugabilityDataBundleFactory();
        DrugabilityDataBundle bundle = factory.getBundle("P48169");
        Protein protein = bundle.getProtein();

        assertEquals("Number of known interactions", 63, bundle.getAllInteractions().size());
        assertEquals("Number of positive known interactions", 61, bundle.getAllInteractionsOfSign(InteractionSign.POSITIVE).size());
        assertEquals("Number of non-positive known interactions", 2, bundle.getAllInteractionsNotOfSign(InteractionSign.POSITIVE).size());
    }
}
