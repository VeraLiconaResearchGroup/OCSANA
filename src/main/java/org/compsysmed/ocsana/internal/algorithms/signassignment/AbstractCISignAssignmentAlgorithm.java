/**
 * Abstract base class for algorithms that assign optimal signs to CIs
 *
 * Copyright Vera-Licona Research Group (C) 2016
 *
 * This software is licensed under the Artistic License 2.0, see the
 * LICENSE file or
 * http://www.opensource.org/licenses/artistic-license-2.0.php for
 * details
 **/

package org.compsysmed.ocsana.internal.algorithms.signassignment;

// Java imports
import java.util.*;

// Cytoscape imports
import org.cytoscape.model.CyNode;

// OCSANA imports
import org.compsysmed.ocsana.internal.algorithms.AbstractOCSANAAlgorithm;

import org.compsysmed.ocsana.internal.util.results.CombinationOfInterventions;
import org.compsysmed.ocsana.internal.util.results.SignedIntervention;

/**
 * Abstract base class for algorithms which assign optimal signs to
 * combinations of interventions
 **/
public abstract class AbstractCISignAssignmentAlgorithm
    extends AbstractOCSANAAlgorithm {
    /**
     * Generate the SignedInterventions which optimize the effect of
     * some combination of interventions
     **/
    abstract public Collection<SignedIntervention> bestInterventions (CombinationOfInterventions ci,
                                                                      Collection<CyNode> targetsToActivate);
}
