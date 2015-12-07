/**
 * Test cases for the RSAlgorithm class
 *
 * Copyright Vera-Licona Research Group (C) 2015
 * @author Andrew Gainer-Dewar, Ph.D. <andrew.gainer.dewar@gmail.com>
 **/

package org.compsysmed.ocsana.internal.algorithms.mhs;

// JUnit imports
import org.junit.*;
import static org.junit.Assert.*;

// Java imports
import java.util.*;

// OCSANA imports
import org.compsysmed.ocsana.internal.algorithms.mhs.Hypergraph;
import org.compsysmed.ocsana.internal.algorithms.mhs.RSAlgorithm;

public class RSAlgorithmTest {
    RSAlgorithm alg;
    Hypergraph smallHypergraph;

    @Before
    public void setUp () {
        // Set up the test environment here
        // In particular, initialize any shared variables
        alg = new RSAlgorithm();

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
        alg = null;
        smallHypergraph = null;
    }

    @Test
    public void smallHypergraphTransversalShouldWork () {
        Hypergraph smallHypergraphTransversal = alg.transversalHypergraph(smallHypergraph);
        assertEquals("Small hypergraph transversal should have 5 edges", 5, smallHypergraphTransversal.numEdges());

        for (BitSet transversal: smallHypergraphTransversal) {
            assertTrue("Small hypergraph should be transversed by each element of its transversal", smallHypergraph.isTransversedBy(transversal));
        }
    }
}
