/**
 * Abstract base class for all MHS algorithms
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
import org.cytoscape.model.CyNetwork;

// OCSANA imports
import org.compsysmed.ocsana.internal.algorithms.AbstractOCSANAAlgorithm;

/**
 * Public abstract base class for all MHS algorithms.
 *
 * @param network  the CyNetwork to compute on
 **/

public abstract class AbstractMHSAlgorithm
    extends AbstractOCSANAAlgorithm {
    protected static final String CONFIG_GROUP = "CI search algorithm";

    public AbstractMHSAlgorithm () {};

    /**
     * Compute MHSes of a given collection of sets
     *
     * @param sets  the sets to hit
     * @return the collection of MHSes of the input sets
     **/
    abstract public List<Set<CyNode>> MHSes (Iterable<? extends Iterable<CyNode>> sets);

    /**
     * Return a name suitable for printing in a menu or status message
     **/
    abstract public String toString ();
}
