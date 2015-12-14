/**
 * Test cases for the Hypergraph class
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

public class HypergraphTest {
    @Before
    public void setUp () {
        // Set up the test environment here
        // In particular, initialize any shared variables
    }

    @After
    public void tearDown () {
        // Tear down the test environment here
        // In particular, null out any shared variables so the garbage
        // collector can trash them
    }

    @Test
    public void defaultConstructorShouldHaveNoVertices () {
        Hypergraph example = new Hypergraph();
        assertEquals("Default Hypergraph should have no vertices", 0, example.numVerts);
    }

    @Test
    public void listConstructorShouldPreserveSize () {
        List<List<Integer>> edges = new ArrayList<>();
        edges.add(Arrays.asList(1, 2, 5));
        edges.add(Arrays.asList(2, 3, 4));
        edges.add(Arrays.asList(1, 3));

        Hypergraph example = new Hypergraph(edges);
        assertEquals("Hypergraph list constructor should preserve size", edges.size(), example.numEdges());
    }

    @Test
    public void fileConstructorShouldWork ()
        throws FileNotFoundException, IOException, NumberFormatException {
        File HER2File = new File(getClass().getResource("/mhs-data/HER2.all.dat").getFile());
        Hypergraph H = new Hypergraph(HER2File);
        assertEquals("Hypergraph from file should preserve number of vertices", 320, H.numVerts());
        assertEquals("Hypergraph from file should preserve number of edges", 69805, H.numEdges());
    }
}
