/**
 * Abstract base class for all path-finding algorithms
 *
 * Copyright Vera-Licona Research Group (C) 2015
 * @author Andrew Gainer-Dewar, Ph.D. <andrew.gainer.dewar@gmail.com>
 *
 * This software is licensed under the Artistic License 2.0, see the
 * LICENSE file or
 * http://www.opensource.org/licenses/artistic-license-2.0.php for
 * details
 **/

package org.compsysmed.ocsana.internal.tasks.path;

// Java imports
import java.util.*;

// Cytoscape imports
import org.cytoscape.model.CyNode;
import org.cytoscape.model.CyNetwork;

// OCSANA imports

/**
 * Public abstract base class for all path-finding algorithms.
 *
 * @param network  the CyNetwork to compute on
 **/

public abstract class AbstractPathFindingAlgorithm {
    protected CyNetwork network;

    public AbstractPathFindingAlgorithm (CyNetwork network) {
        this.network = network;
    }

    /**
     * Compute paths from a set of source nodes to a set of target nodes.
     *
     * @param sources  the source nodes
     * @param targets  the target nodes
     * @return a List of paths, each of which is given as a List of
     * CyNodes, in order from source to target
     **/
    abstract public List<List<CyNode>> paths (Set<CyNode> sources,
                                              Set<CyNode> targets);

    /**
     * Return a name suitable for printing in a menu or status message
     **/
    abstract public String toString ();

    /**
     * Return a long, explanatory name
     **/
    abstract public String fullName ();

    /**
     * Return a short name
     **/
    abstract public String shortName ();
}
