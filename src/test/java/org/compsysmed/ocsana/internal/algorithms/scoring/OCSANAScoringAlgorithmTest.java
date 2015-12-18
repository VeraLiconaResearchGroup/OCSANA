/**
 * Test cases for the OCSANAScoringAlgorithm class
 *
 * Copyright Vera-Licona Research Group (C) 2015
 **/

package org.compsysmed.ocsana.internal.algorithms.scoring;

// JUnit imports
import org.junit.*;
import static org.junit.Assert.*;

// Java imports
import java.util.*;
import java.io.*;

// Cytoscape imports
import org.cytoscape.model.CyNetwork;
import org.cytoscape.model.CyEdge;
import org.cytoscape.model.CyNode;

// OCSANA imports
import org.compsysmed.ocsana.internal.algorithms.scoring.OCSANAScoringAlgorithm;

import org.compsysmed.ocsana.internal.helpers.SIFFileConverter;

public class OCSANAScoringAlgorithmTest {
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
    public void shouldScoreToyNetworkCorrectly ()
        throws IOException {
        // Toy network setup
        File toyFile = new File(getClass().getResource("/network-data/ToyNetwork.sif").getFile());
        SIFFileConverter toyConverter = new SIFFileConverter(toyFile);
        CyNetwork toyNetwork = toyConverter.getNetwork();

        // Scoring algorithm setup
        OCSANAScoringAlgorithm scoringAlg = new OCSANAScoringAlgorithm(toyNetwork);

        // Get nodes
        CyNode I1 = toyConverter.getNode("I1");
        CyNode I2 = toyConverter.getNode("I2");
        CyNode A = toyConverter.getNode("A");
        CyNode B = toyConverter.getNode("B");
        CyNode C = toyConverter.getNode("C");
        CyNode D = toyConverter.getNode("D");
        CyNode E = toyConverter.getNode("E");
        CyNode F = toyConverter.getNode("F");
        CyNode O1 = toyConverter.getNode("O1");
        CyNode O2 = toyConverter.getNode("O2");

        // Define paths
        Collection<List<CyEdge>> pathsToTargets = new ArrayList<>();
        pathsToTargets.add(toyConverter.getPath(I1, A, D, E, O1));
        pathsToTargets.add(toyConverter.getPath(I1, B, E, O1));
        pathsToTargets.add(toyConverter.getPath(I1, A, B, E, O1));
        pathsToTargets.add(toyConverter.getPath(I2, C, F, O2));
        pathsToTargets.add(toyConverter.getPath(I2, C, F, B, E, O1));
        pathsToTargets.add(toyConverter.getPath(I2, C, E, O1));
        pathsToTargets.add(toyConverter.getPath(I2, B, E, O1));

        Collection<List<CyEdge>> pathsToOffTargets = Arrays.asList(toyConverter.getPath(I1, A, D));

        // Compute scores
        Map<CyNode, Double> scores = scoringAlg.computeScores(pathsToTargets, pathsToOffTargets);

        // Tests
        //assertEquals("Toy network score: A", -2.0d, scores.get(A), 0.0d);
        assertEquals("Toy network score: B", 4.0d, scores.get(B), 0.0d);
        assertEquals("Toy network score: C", 3.75d, scores.get(C), 0.0d);
        //assertEquals("Toy network score: D", -1.0d, scores.get(D), 0.0d);
        assertEquals("Toy network score: E", 18.0d, scores.get(E), 0.0d);
        assertEquals("Toy network score: F", 2.66d, scores.get(F), 0.01d);
    }
}
