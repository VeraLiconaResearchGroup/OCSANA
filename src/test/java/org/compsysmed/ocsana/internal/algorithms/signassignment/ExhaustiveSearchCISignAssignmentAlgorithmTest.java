/**
 * Test cases for the ExhaustiveSearchCISignAssignmentAlgorithm class
 *
 * Copyright Vera-Licona Research Group (C) 2016
 **/

package org.compsysmed.ocsana.internal.algorithms.signassignment;

// JUnit imports
import org.junit.*;
import static org.junit.Assert.*;

// Java imports
import java.util.*;
import java.io.*;

// Cytoscape imports
import org.cytoscape.model.CyNetwork;
import org.cytoscape.model.CyNode;

// OCSANA imports
import org.compsysmed.ocsana.internal.helpers.SIFFileConverter;

import org.compsysmed.ocsana.internal.util.results.CombinationOfInterventions;
import org.compsysmed.ocsana.internal.util.results.SignedIntervention;

public class ExhaustiveSearchCISignAssignmentAlgorithmTest {
    CyNetwork toyNetwork;
    Set<CyNode> toyNetworkSources;
    Set<CyNode> toyNetworkTargets;

    @Before
    public void setUp ()
        throws IOException {
        // Set up the test environment here
        // In particular, initialize any shared variables

        // Toy network
        File toyFile = new File(getClass().getResource("/network-data/ToyNetwork.sif").getFile());
        SIFFileConverter toyConverter = new SIFFileConverter(toyFile);
        toyNetwork = toyConverter.getNetwork();

        toyNetworkSources = new HashSet<>();
        List<String> toyNetworkSourceNames = Arrays.asList("I1", "I2");
        for (String sourceName: toyNetworkSourceNames) {
            toyNetworkSources.add(toyConverter.getNode(sourceName));
        }
        assert toyNetworkSources.size() == 2;

        toyNetworkTargets = new HashSet<>();
        List<String> toyNetworkTargetNames = Arrays.asList("O1", "O2");
        for (String targetName: toyNetworkTargetNames) {
            toyNetworkTargets.add(toyConverter.getNode(targetName));
        }
        assert toyNetworkTargets.size() == 2;
    }

    @After
    public void tearDown () {
        // Tear down the test environment here
        // In particular, null out any shared variables so the garbage
        // collector can trash them
        toyNetwork = null;
        toyNetworkSources = null;
        toyNetworkTargets = null;
    }

    @Test
    public void searchShouldFindCorrectNumberOfSignAssignments () {
        ExhaustiveSearchCISignAssignmentAlgorithm algorithm = new ExhaustiveSearchCISignAssignmentAlgorithm((node1, node2) -> 1d, true);
        assertNotNull(algorithm);

        CombinationOfInterventions ci = new CombinationOfInterventions(toyNetworkSources, toyNetworkTargets, node -> "Default node name");
        Collection<SignedIntervention> signs = algorithm.bestInterventions(ci, toyNetworkTargets);

        assertEquals("Number of optimal sign assignments of CI", 1, signs.size());
        assertEquals("Number of nodes to activate", 2, signs.stream().findAny().get().getNodesToActivate().size());
    }
}
