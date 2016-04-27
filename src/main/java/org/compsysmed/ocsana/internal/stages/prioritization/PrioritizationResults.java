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

    private final Map<CombinationOfInterventions, Collection<SignedIntervention>> optimalInterventionSignings = new HashMap<>();
    private final Map<SignedIntervention, Double> signedInterventionScores = new HashMap<>();

    public PrioritizationResults (PrioritizationContext prioritizationContext) {
        if (prioritizationContext == null) {
            throw new IllegalArgumentException("Prioritization stage context cannot be null");
        }
        this.prioritizationContext = prioritizationContext;
    }

    /**
     * Set the optimal signings for one CI
     *
     * @param ci  the CI
     * @param optimalSignings  the optimal sign assignments for the CI
     **/
    public void setOptimalInterventionSignings (CombinationOfInterventions ci,
                                                Collection<SignedIntervention> optimalSignings) {
        if (ci == null) {
            throw new IllegalArgumentException("CI cannot be null");
        }

        if (optimalSignings == null) {
            throw new IllegalArgumentException("Optimal signings collection cannot be null");
        }

        if (!optimalSignings.stream().allMatch(si -> si.hasUnderlyingCI(ci))) {
            throw new IllegalArgumentException("Sign assignments do not match given CI");
        }
        optimalInterventionSignings.put(ci, optimalSignings);
    }

    /**
     * Drop all records of optimal intervention signings
     **/
    public void resetOptimalInterventionSignings () {
        optimalInterventionSignings.clear();
    }

    /**
     * Get the optimal signings for a specified CombinationOfInterventions
     *
     * @param ci  the CI
     * @return the optimal SignedIntervention assignments for the CI,
     * if assigned, or an empty collection if not.
     **/
    public Collection<SignedIntervention> getOptimalInterventionSignings (CombinationOfInterventions ci) {
        if (ci == null) {
            throw new IllegalArgumentException("CI cannot be null");
        }

        return optimalInterventionSignings.getOrDefault(ci, Collections.emptyList());
    }

    /**
     * Set the score for a particular signed intervention
     *
     * @param si  the signed intervention
     * @param score  the score
     **/
    public void setSignedInterventionScore (SignedIntervention si,
                                            Double score) {
        if (si == null) {
            throw new IllegalArgumentException("Signed intervention cannot be null");
        }

        if (score == null) {
            throw new IllegalArgumentException("Score cannot be null");
        }

        signedInterventionScores.put(si, score);
    }

    /**
     * Drop all records of signed intervention scores
     **/
    public void resetSignedInterventionScores () {
        signedInterventionScores.clear();
    }

    /**
     * Get the score for a specified signed intervention
     *
     * @param si  the signed intervention
     * @return the score of the si, if assigned, or null if not
     **/
    public Double getSignedInterventionScore (SignedIntervention si) {
        return signedInterventionScores.getOrDefault(si, null);
    }

}
