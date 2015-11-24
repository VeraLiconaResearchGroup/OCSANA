/**
 * Abstract base class for all scoring algorithms
 *
 * Copyright Vera-Licona Research Group (C) 2015
 * @author Andrew Gainer-Dewar, Ph.D. <andrew.gainer.dewar@gmail.com>
 *
 * This software is licensed under the Artistic License 2.0, see the
 * LICENSE file or
 * http://www.opensource.org/licenses/artistic-license-2.0.php for
 * details
 **/
package org.compsysmed.ocsana.internal.algorithms.scoring;

// Java imports
import java.util.*;

// Cytoscape imports
import org.cytoscape.model.CyNetwork;
import org.cytoscape.model.CyEdge;
import org.cytoscape.model.CyNode;

// OCSANA imports
import org.compsysmed.ocsana.internal.algorithms.AbstractOCSANAAlgorithm;

/**
 * Public abstract base class for all scoring algorithms.
 **/

public abstract class AbstractScoringAlgorithm
    extends AbstractOCSANAAlgorithm {
    protected static final String CONFIG_GROUP = "Scoring algorithm";

    protected CyNetwork network;

    public AbstractScoringAlgorithm (CyNetwork network) {
        this.network = network;
    };

    /**
     * Compute scores and store them in the tables
     *
     * @param pathsToTargets  the paths to the targets
     * @param pathsToOffTargets  the paths to the off-targets
     **/
    abstract public void applyScores(Collection<List<CyEdge>> pathsToTargets,
                                     Collection<List<CyEdge>> pathsToOffTargets);

    /**
     * Return a name suitable for printing in a menu or status message
     **/
    abstract public String toString ();
}
