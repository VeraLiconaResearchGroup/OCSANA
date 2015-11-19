/**
 * Helper class to handle converting Iterable<Set<CyNode>> to
 * Hypergraph and back
 *
 * Copyright Vera-Licona Research Group (C) 2015
 * @author Andrew Gainer-Dewar, Ph.D. <andrew.gainer.dewar@gmail.com>
 *
 * This software is licensed under the Artistic License 2.0, see the
 * LICENSE file or
 * http://www.opensource.org/licenses/artistic-license-2.0.php for
 * details
 **/

package org.compsysmed.ocsana.internal.algorithms.mhs;

// Java imports
import java.util.*;

// Cytoscape imports
import org.cytoscape.model.CyNode;

// OCSANA imports

/**
 * The 'MMCS' algorithm for finding minimal hitting sets
 **/

public class HypergraphOfSetsOfCyNodes extends Hypergraph {
    private Map<CyNode, Integer> mapNodeToHash;
    private Map<Integer, CyNode> mapHashToNode;

    private Integer nextKey = 0;

    /**
     * Construct a Hypergraph from a collection of Sets of CyNodes
     *
     * In particular, the underlying BitSets will be zero-packed for
     * efficiency.
     *
     * @param sets  the sets to transform into edges of the Hypergraph
     **/

    public HypergraphOfSetsOfCyNodes (Iterable<? extends Iterable<CyNode>> sets) {
        mapNodeToHash = new HashMap<>();
        mapHashToNode = new HashMap<>();

        for (Iterable<CyNode> set: sets) {
            BitSet newEdge = new BitSet();
            for (CyNode node: set) {
                newEdge.set(hashOfNode(node));
            }
            add(newEdge);
        }
        updateNumVerts();
    }

    /**
     * Convert a Hypergraph back into a collection of Sets of CyNodes
     **/
    public List<Set<CyNode>> getCyNodeSetsFromHypergraph (Hypergraph sets) {
        List<Set<CyNode>> result = new ArrayList<>();
        for (BitSet edge: sets) {
            result.add(getCyNodesFromBitSet(edge));
        }
        return result;
    }

    /**
     * Convert a BitSet back into a Set of CyNodes
     **/
    protected Set<CyNode> getCyNodesFromBitSet (BitSet edge) {
        Set<CyNode> nodes = new HashSet<>();
        for (int i = edge.nextSetBit(0); i >= 0; i = edge.nextSetBit(i+1)) {
            nodes.add(nodeOfHash(i));
        }
        return nodes;
    }

    /**
     * Return the next available key
     **/
    protected Integer nextKey () {
        return nextKey++;
    }

    protected Integer hashOfNode (CyNode node) {
        if (!mapNodeToHash.containsKey(node)) {
            Integer hash = nextKey();
            mapNodeToHash.put(node, hash);
            mapHashToNode.put(hash, node);
        }

        return mapNodeToHash.get(node);
    }

    protected CyNode nodeOfHash (Integer hash) {
        if (!mapHashToNode.containsKey(hash)) {
            throw new IllegalArgumentException("Hash " + hash + " not used!");
        }

        return mapHashToNode.get(hash);
    }
}