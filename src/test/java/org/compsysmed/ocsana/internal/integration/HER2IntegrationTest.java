/**
 * Big integration tests for OCSANA
 *
 * Copyright Vera-Licona Research Group (C) 2015

 **/

package org.compsysmed.ocsana.internal.integration;

// JUnit imports
import org.junit.*;
import static org.junit.Assert.*;

// Java imports
import java.util.*;
import java.io.*;

// Cytoscape imports
import org.cytoscape.model.NetworkTestSupport;

import org.cytoscape.model.CyNetwork;
import org.cytoscape.model.CyNode;
import org.cytoscape.model.CyEdge;

// OCSANA imports
import org.compsysmed.ocsana.internal.algorithms.path.AbstractPathFindingAlgorithm;
import org.compsysmed.ocsana.internal.algorithms.path.AllNonSelfIntersectingPathsAlgorithm;

import org.compsysmed.ocsana.internal.algorithms.scoring.OCSANAScoringAlgorithm;

import org.compsysmed.ocsana.internal.algorithms.mhs.AbstractMHSAlgorithm;
import org.compsysmed.ocsana.internal.algorithms.mhs.MMCSAlgorithm;


public class HER2IntegrationTest {
    NetworkTestSupport nts;
    CyNetwork network;
    Map<String, CyNode> nodeMap;

    Set<CyNode> sources;
    Set<CyNode> targets;

    @Before
    public void setUp ()
        throws IOException {
        // Set up the test environment here
        // In particular, initialize any shared variables
        nts = new NetworkTestSupport();
        readSIFFile(new File(getClass().getResource("/network-data/HER2.sif").getFile()));

        sources = new HashSet<>();
        List<String> sourceNames = Arrays.asList("n1064", "n1065", "n1066", "n1067", "n1069", "n1071", "n1073", "n1092", "n1093", "n1748", "n1855", "n1869", "n2270", "n2274", "n2276", "n2277", "n2679", "n2693", "n2695", "n2701", "n2704", "n2712", "n2727", "n2753", "n2758", "n2766", "n2781", "n66", "n76");
        for (String sourceName: sourceNames) {
            sources.add(getNodeFromNetwork(sourceName));
        }

        targets = new HashSet<>();
        List<String> targetNames = Arrays.asList("n1000", "n1034", "n1115", "n1119", "n1126", "n1158", "n1173", "n1177", "n1878", "n2492", "n2615", "n2652", "n839", "n940", "n983", "n996");
        for (String targetName: targetNames) {
            targets.add(getNodeFromNetwork(targetName));
        }
    }

    @After
    public void tearDown () {
        // Tear down the test environment here
        // In particular, null out any shared variables so the garbage
        // collector can trash them
        nts = null;
        network = null;
        nodeMap = null;

        sources = null;
        targets = null;
    }

    @Test
    public void HER2NetworkIsCorrectSize ()
        throws Exception {
        assertEquals("HER2 network should have correct number of nodes", 2753, network.getNodeCount());
        assertEquals("HER2 network should have correct number of edges", 3812, network.getEdgeCount());
    }

    @Test
    public void HER2NetworkOCSANAProcessing ()
        throws Exception {
        // Find paths
        AbstractPathFindingAlgorithm pathAlg = new AllNonSelfIntersectingPathsAlgorithm(network);

        Collection<List<CyEdge>> paths = pathAlg.paths(sources, targets);
        assertEquals("HER2 network should have correct number of paths", 69805, paths.size());

        // Find CIs
        List<Set<CyNode>> nodeSets = new ArrayList<>();
        for (List<CyEdge> path: paths) {
            Set<CyNode> nodes = new HashSet<>();
            for (CyEdge edge: path) {
                nodes.add(edge.getSource());
                nodes.add(edge.getTarget());
            }
            nodeSets.add(nodes);
        }

        AbstractMHSAlgorithm mhsAlg = new MMCSAlgorithm();
        List<Set<CyNode>> MHSes = mhsAlg.MHSes(nodeSets);

        assertEquals("HER2 network should have correct number of CIs", 320, MHSes.size());
    }

    private void readSIFFile (File sifFile)
        throws IOException {
        network = nts.getNetwork();
        nodeMap = new HashMap<>();

        try (BufferedReader sifFileReader
             = new BufferedReader(new FileReader(sifFile))) {
            // Process the file line-by-line
            // Each line represents an edge of the network
            for (String line = sifFileReader.readLine();
                 line != null; line = sifFileReader.readLine()) {
                // Get the head and tail vertices of the edge
                // NOTE: we do not handle the multi-target case, which
                // is supported by SIF
                String[] lineWords = line.split("\t");
                CyNode head = getNodeFromNetwork(lineWords[0]);
                CyNode tail = getNodeFromNetwork(lineWords[2]);

                // Create the edge
                CyEdge newEdge = network.addEdge(head, tail, true);
            }
        }
    }

    private CyNode getNodeFromNetwork(String nodeName) {
        if (nodeMap.containsKey(nodeName)) {
            return nodeMap.get(nodeName);
        } else {
            CyNode newNode = network.addNode();
            network.getRow(newNode).set(CyNetwork.NAME, nodeName);
            nodeMap.put(nodeName, newNode);
            return newNode;
        }
    }
}
