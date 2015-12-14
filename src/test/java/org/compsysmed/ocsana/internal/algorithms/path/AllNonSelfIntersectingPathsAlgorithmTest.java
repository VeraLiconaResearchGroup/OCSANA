/**
 * Test cases for the AllNonSelfIntersectingPathsAlgorithm class
 *
 * Copyright Vera-Licona Research Group (C) 2015
 **/

package org.compsysmed.ocsana.internal.algorithms.paths;

// JUnit imports
import org.junit.*;
import static org.junit.Assert.*;

// Java imports
import java.util.*;

// Cytoscape imports
import org.cytoscape.model.NetworkTestSupport;
import org.cytoscape.model.CyNetwork;
import org.cytoscape.model.CyNode;
import org.cytoscape.model.CyEdge;

// OCSANA imports
import org.compsysmed.ocsana.internal.algorithms.path.AllNonSelfIntersectingPathsAlgorithm;

public class AllNonSelfIntersectingPathsAlgorithmTest {
    NetworkTestSupport nts;

    CyNetwork toyNetwork;
    Set<CyNode> toyNetworkSources;
    Set<CyNode> toyNetworkTargets;

    AllNonSelfIntersectingPathsAlgorithm toyNetworkAlg;

    @Before
    public void setUp () {
        // Set up the test environment here
        // In particular, initialize any shared variables
        nts = new NetworkTestSupport();

        // Set up test network
        toyNetwork = nts.getNetwork();
        toyNetworkAlg = new AllNonSelfIntersectingPathsAlgorithm(toyNetwork);

        CyNode I1 = toyNetwork.addNode();
        CyNode I2 = toyNetwork.addNode();
        CyNode A = toyNetwork.addNode();
        CyNode B = toyNetwork.addNode();
        CyNode C = toyNetwork.addNode();
        CyNode D = toyNetwork.addNode();
        CyNode E = toyNetwork.addNode();
        CyNode F = toyNetwork.addNode();
        CyNode O1 = toyNetwork.addNode();
        CyNode O2 = toyNetwork.addNode();

        toyNetwork.addEdge(I1, A, true);
        toyNetwork.addEdge(I1, B, true);
        toyNetwork.addEdge(I2, B, true);
        toyNetwork.addEdge(I2, C, true);
        toyNetwork.addEdge(A, D, true);
        toyNetwork.addEdge(A, B, true);
        toyNetwork.addEdge(B, E, true);
        toyNetwork.addEdge(C, E, true);
        toyNetwork.addEdge(C, F, true);
        toyNetwork.addEdge(D, E, true);
        toyNetwork.addEdge(F, B, true);
        toyNetwork.addEdge(E, O1, true);
        toyNetwork.addEdge(F, O2, true);

        toyNetworkSources = new HashSet<>();
        toyNetworkSources.add(I1);
        toyNetworkSources.add(I2);

        toyNetworkTargets = new HashSet<>();
        toyNetworkTargets.add(O1);
        toyNetworkTargets.add(O2);
    }

    @After
    public void tearDown () {
        // Tear down the test environment here
        // In particular, null out any shared variables so the garbage
        // collector can trash them
        nts = null;

        toyNetwork = null;
        toyNetworkAlg = null;
        toyNetworkSources = null;
        toyNetworkTargets = null;
    }

    @Test
    public void toyNetworkShouldHaveSevenPaths () {
        Collection<List<CyEdge>> paths = toyNetworkAlg.paths(toyNetworkSources, toyNetworkTargets);
        assertEquals("Toy network should have seven paths", 7, paths.size());

        for (List<CyEdge> path: paths) {
            assertTrue("Path should start at a source", toyNetworkSources.contains(path.get(0).getSource()));
            assertTrue("Path should end at a target", toyNetworkTargets.contains(path.get(path.size() - 1).getTarget()));
        }
    }
}
