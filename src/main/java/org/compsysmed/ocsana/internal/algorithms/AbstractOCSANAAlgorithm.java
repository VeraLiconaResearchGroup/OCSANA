/**
 * Abstract base class for all OCSANA algorithms
 *
 * Copyright Vera-Licona Research Group (C) 2015
 * @author Andrew Gainer-Dewar, Ph.D. <andrew.gainer.dewar@gmail.com>
 *
 * This software is licensed under the Artistic License 2.0, see the
 * LICENSE file or
 * http://www.opensource.org/licenses/artistic-license-2.0.php for
 * details
 **/
package org.compsysmed.ocsana.internal.algorithms;

// Java imports
import java.util.*;

import java.util.concurrent.atomic.AtomicBoolean;

// Cytoscape imports

// OCSANA imports

/**
 * Abstract base class for all OCSANA algorithms
 **/

public abstract class AbstractOCSANAAlgorithm {
    // Keep track of whether the user has canceled the algorithm
    private AtomicBoolean canceled = new AtomicBoolean(false);

    /**
     * Abort execution of the algorithm.
     **/
    public void cancel () {
        canceled.set(true);
    };

    /**
     * Indicate whether the algorithm has been canceled by the user
     **/
    public boolean isCanceled() {
        return canceled.get();
    }
}
