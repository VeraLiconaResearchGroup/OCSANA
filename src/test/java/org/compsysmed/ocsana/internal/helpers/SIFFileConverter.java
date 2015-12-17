/**
 * Class to read a network from an SIF file
 *
 * Copyright Vera-Licona Research Group (C) 2015
 **/

package org.compsysmed.ocsana.internal.helpers;

// Java imports
import java.util.*;
import java.io.*;

// Cytoscape imports
import org.cytoscape.model.NetworkTestSupport;

import org.cytoscape.model.CyNetwork;
import org.cytoscape.model.CyNode;
import org.cytoscape.model.CyEdge;

/**
 * Build a CyNetwork from an SIF file
 *
 * @param sifFile  the SIF file
 **/
public class SIFFileConverter {
    NetworkTestSupport nts;
    CyNetwork network;
    Map<String, CyNode> nodeMap;

    public SIFFileConverter (File sifFile)
        throws IOException {
        nts = new NetworkTestSupport();
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
                CyNode head = getNode(lineWords[0]);
                CyNode tail = getNode(lineWords[2]);

                // Create the edge
                CyEdge newEdge = network.addEdge(head, tail, true);
            }
        }
    }

    /**
     * Retrieve the network
     **/
    public CyNetwork getNetwork() {
        return network;
    }

    /**
     * Get the node with the specified name
     *
     * @param nodeName  the node name
     **/
    public CyNode getNode(String nodeName) {
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
