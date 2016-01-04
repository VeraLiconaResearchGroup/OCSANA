/**
 * Test cases for the BergeAlgorithm class
 *
 * Copyright Vera-Licona Research Group (C) 2015
 **/

package org.compsysmed.ocsana.internal.algorithms.mhs;

// JUnit imports
import org.junit.*;
import static org.junit.Assert.*;

// Java imports
import java.util.*;
import java.io.*;

// OCSANA imports
import org.compsysmed.ocsana.internal.algorithms.mhs.Hypergraph;
import org.compsysmed.ocsana.internal.algorithms.mhs.BergeAlgorithm;

public class BergeAlgorithmTest {
    BergeAlgorithm algFull;
    BergeAlgorithm algCutoff;
    Hypergraph smallHypergraph;

    @Before
    public void setUp () {
        // Set up the test environment here
        // In particular, initialize any shared variables
        algFull = new BergeAlgorithm();
        algFull.useMaxCardinality = false;

        algCutoff = new BergeAlgorithm();
        algCutoff.useMaxCardinality = true;
        algCutoff.maxCardinalityBInt.setValue(6);

        List<List<Integer>> smallHypergraphEdges = new ArrayList<>();
        smallHypergraphEdges.add(Arrays.asList(1, 2, 5));
        smallHypergraphEdges.add(Arrays.asList(2, 3, 4));
        smallHypergraphEdges.add(Arrays.asList(1, 3));
        smallHypergraph = new Hypergraph(smallHypergraphEdges);
    }

    @After
    public void tearDown () {
        // Tear down the test environment here
        // In particular, null out any shared variables so the garbage
        // collector can trash them
        algFull = null;
        algCutoff = null;

        smallHypergraph = null;
    }

    @Test
    public void smallHypergraphTransversalShouldWork () {
        Hypergraph T = algFull.transversalHypergraph(smallHypergraph);
        assertEquals("Transversal count", 5, T.numEdges());

        for (BitSet transversal: T) {
            assertTrue("Transversal condition", smallHypergraph.isTransversedBy(transversal));
        }
    }

    @Ignore("Berge's algorithm is too slow for this input")
    public void hugeHypergraphCutoffTransversalShouldWork ()
        throws FileNotFoundException, IOException, NumberFormatException {
        File HER2File = new File(getClass().getResource("/mhs-data/HER2.all.dat").getFile());
        Hypergraph H = new Hypergraph(HER2File);

        Hypergraph T = algCutoff.transversalHypergraph(H);

        assertEquals("Transversal count", 320, T.numEdges());

        for (BitSet transversal: T) {
            assertTrue("Transversal condition", H.isTransversedBy(transversal));
        }
    }
}
