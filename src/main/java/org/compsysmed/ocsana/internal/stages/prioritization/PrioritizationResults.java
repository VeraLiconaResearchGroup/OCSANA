/**
 * Results container for the prioritization stage of OCSANA
 *
 * Copyright Vera-Licona Research Group (C) 2016
 *
 * This software is licensed under the Artistic License 2.0, see the
 * LICENSE file or
 * http://www.opensource.org/licenses/artistic-license-2.0.php for
 * details
 **/

package org.compsysmed.ocsana.internal.stages.prioritization;

// Java imports
import java.util.*;
import java.util.stream.Collectors;

// Cytoscape imports
import org.cytoscape.model.CyNode;
import org.cytoscape.model.CyEdge;

// OCSANA imports
import org.compsysmed.ocsana.internal.util.results.CombinationOfInterventions;
import org.compsysmed.ocsana.internal.util.results.SignedIntervention;

public class PrioritizationResults {
    private final PrioritizationContext prioritizationContext;

    private Map<CombinationOfInterventions, Collection<SignedIntervention>> optimalInterventionSignings = new HashMap<>();

    public PrioritizationResults (PrioritizationContext prioritizationContext) {
        if (prioritizationContext == null) {
            throw new IllegalArgumentException("Prioritization stage context cannot be null");
        }
        this.prioritizationContext = prioritizationContext;
    }

    /**
     * Set the optimal signings for all CIs at once
     * <p>
     * NOTE: the input is not validated or sanitized in any way. Use with
     * caution!
     *
     * @param optimalInterventionSignings  map assigning to each CI its
     * collection of optimal sign assignments
     **/
    public void setOptimalInterventionSignings (Map<CombinationOfInterventions, Collection<SignedIntervention>> optimalInterventionSignings) {
        this.optimalInterventionSignings = optimalInterventionSignings;
    }

    /**
     * Set the optimal signings for one CI
     *
     * @param ci  the CI
     * @param optimalSignings  the optimal sign assignments for the CI
     **/
    public void setOptimalInterventionSignings (CombinationOfInterventions ci,
                                                Collection<SignedIntervention> optimalSignings) {
        if (!optimalSignings.stream().allMatch(si -> si.hasUnderlyingCI(ci))) {
            throw new IllegalArgumentException("Sign assignments do not match given CI");
        }
        optimalInterventionSignings.put(ci, optimalSignings);
    }

    /**
     * Get the optimal signings for all CIs at once
     **/
    public Map<CombinationOfInterventions, Collection<SignedIntervention>> getOptimalInterventionSignings () {
        return optimalInterventionSignings;
    }

    /**
     * Get the optimal signings for a specified CombinationOfInterventions
     *
     * @param ci  the CI
     **/
    public Collection<SignedIntervention> getOptimalInterventionSignings (CombinationOfInterventions ci) {
        return optimalInterventionSignings.get(ci);
    }
}
