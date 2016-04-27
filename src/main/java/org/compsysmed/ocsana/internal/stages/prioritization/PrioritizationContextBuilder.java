/**
 * Context builder for the prioritization stage of OCSANA
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
import java.util.function.*;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

// Cytoscape imports
import org.cytoscape.model.CyColumn;
import org.cytoscape.model.CyNetwork;
import org.cytoscape.model.CyEdge;
import org.cytoscape.model.CyNode;

import org.cytoscape.work.ContainsTunables;
import org.cytoscape.work.Tunable;

import org.cytoscape.work.util.ListMultipleSelection;

// OCSANA imports
import org.compsysmed.ocsana.internal.algorithms.signassignment.AbstractCISignAssignmentAlgorithm;
import org.compsysmed.ocsana.internal.algorithms.signassignment.ExhaustiveSearchCISignAssignmentAlgorithm;

import org.compsysmed.ocsana.internal.stages.generation.GenerationContext;
import org.compsysmed.ocsana.internal.stages.generation.GenerationResults;

import org.compsysmed.ocsana.internal.util.results.CombinationOfInterventions;
import org.compsysmed.ocsana.internal.util.results.SignedIntervention;

/**
 * Context builder for the prioritization stage of OCSANA
 * <p>
 * This class allows incremental construction
 * of a {@link PrioritizationContext}.
 **/
public class PrioritizationContextBuilder {
    private final CyNetwork network;
    private final GenerationContext generationContext;
    private final GenerationResults generationResults;
    private final Collection<CyNode> targets;

    private Set<CyNode> targetsToDeactivate;
    private Set<CyNode> targetsToActivate;

    private AbstractCISignAssignmentAlgorithm ciSignAlgorithm;

    /**
     * Constructor
     *
     * @param network  the underlying network
     * @param generationContext  the context for the generation stage
     * @param generationResults  the results of the generation stage
     **/
    public PrioritizationContextBuilder (CyNetwork network,
                                         GenerationContext generationContext,
                                         GenerationResults generationResults) {
        if (network == null) {
            throw new IllegalArgumentException("network cannot be null");
        }
        this.network = network;

        if (generationContext == null) {
            throw new IllegalArgumentException("Generation stage context cannot be null");
        }
        this.generationContext = generationContext;

        if (generationResults == null) {
            throw new IllegalArgumentException("Generation stage results cannot be null");
        }
        this.generationResults = generationResults;

        targets = generationContext.getTargetNodes();

        setTargetsToActivate(new HashSet<>());

        BiFunction<CyNode, CyNode, Double> effectOnTargets = (source, target) -> generationContext.getOCSANAAlgorithm().effectOnTargetsScore(source, target);
        ciSignAlgorithm = new ExhaustiveSearchCISignAssignmentAlgorithm(effectOnTargets);
    }

    /**
     * Set the targets to be activated
     * <p>
     * NOTE: the targets to be deactivated are automatically set to be the
     * complement of this input as a subset of the target set obtained from the
     * generation stage context.
     *
     * @param targetsToActivate  the target nodes to activate
     **/
    public void setTargetsToActivate (Set<CyNode> targetsToActivate) {
        if (targetsToActivate == null) {
            throw new IllegalArgumentException("Set of targets to activate cannot be null");
        }
        this.targetsToActivate = targetsToActivate;

        targetsToDeactivate = new HashSet<>(targets);
        targetsToDeactivate.removeAll(targetsToActivate);

    }

    /**
     * Set the CI sign assignment algorithm
     *
     * @param ciSignAlgorithm  the algorithm
     **/
    public void setCISignAlgorithm (AbstractCISignAssignmentAlgorithm ciSignAlgorithm) {
        if (ciSignAlgorithm == null) {
            throw new IllegalArgumentException("CI sign assignment algorithm cannot be null");
        }

        this.ciSignAlgorithm = ciSignAlgorithm;
    }

    /**
     * Get the context as configured
     **/
    public PrioritizationContext getContext () {
        return new PrioritizationContext(network, generationContext, generationResults, targets, targetsToActivate, targetsToDeactivate, ciSignAlgorithm);
    }
}
