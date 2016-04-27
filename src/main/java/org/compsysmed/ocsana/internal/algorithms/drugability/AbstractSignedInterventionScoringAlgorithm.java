/**
 * Abstract base class for algorithms that assign priority scores to
 * signed CIs
 *
 * Copyright Vera-Licona Research Group (C) 2016
 *
 * This software is licensed under the Artistic License 2.0, see the
 * LICENSE file or
 * http://www.opensource.org/licenses/artistic-license-2.0.php for
 * details
 **/

package org.compsysmed.ocsana.internal.algorithms.drugability;

// Java imports
import java.util.*;

// Cytoscape imports
import org.cytoscape.model.CyNode;

// OCSANA imports
import org.compsysmed.ocsana.internal.algorithms.AbstractOCSANAAlgorithm;

import org.compsysmed.ocsana.internal.util.results.SignedIntervention;

/**
 * Abstract base class for algorithms which assign priority scores to
 * signed CIs
 **/
public abstract class AbstractSignedInterventionScoringAlgorithm
    extends AbstractOCSANAAlgorithm {
    /**
     * Compute the priority score for a given intervention.
     * <p>
     * NOTE: These scores should be in <em>increasing</em> order of priority;
     * that is, higher-priority interventions in a given network should receive
     * higher scores.
     * <p>
     * NOTE: Scores computed by different algorithms or for different networks
     * should not be compared. There is no requirement of consistency except
     * for a single algorithm applied to a single network.
     **/
    abstract public Double computePriorityScore (SignedIntervention signedIntervention);
}
