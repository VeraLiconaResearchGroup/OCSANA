/**
 * Context for the prioritization stage of OCSANA
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

import org.compsysmed.ocsana.internal.stages.generation.GenerationContext;
import org.compsysmed.ocsana.internal.stages.generation.GenerationResults;

import org.compsysmed.ocsana.internal.util.results.CombinationOfInterventions;
import org.compsysmed.ocsana.internal.util.results.SignedIntervention;

/**
 * Context for the prioritization stage of OCSANA
 * <p>
 * This class stores the configuration required to run the scoring
 * stage.  A populated instance will be passed to a
 * ScoringStageController which handles scoring tasks.
 * <p>
 * This class is immutable by design. Instances should be constructed using
 * {@link PrioritizationContextBuilder}.
 **/
public final class PrioritizationContext {
    private final CyNetwork network;
    private final GenerationContext generationContext;
    private final GenerationResults generationResults;
    private final Collection<CyNode> targets;

    private final Set<CyNode> targetsToActivate;
    private final Set<CyNode> targetsToDeactivate;

    private final AbstractCISignAssignmentAlgorithm ciSignAlgorithm;

    public PrioritizationContext (CyNetwork network,
                                  GenerationContext generationContext,
                                  GenerationResults generationResults,
                                  Collection<CyNode> targets,
                                  Set<CyNode> targetsToActivate,
                                  Set<CyNode> targetsToDeactivate,
                                  AbstractCISignAssignmentAlgorithm ciSignAlgorithm) {
        // Sanity checks
        if (!targets.containsAll(targetsToActivate) || !targets.containsAll(targetsToDeactivate)) {
            throw new IllegalArgumentException("Targets to activate and deactivate must be in target set");
        }

        Set<CyNode> targetIntersection = new HashSet<>(targetsToActivate);
        targetIntersection.retainAll(targetsToDeactivate);
        if (!targetIntersection.isEmpty()) {
            throw new IllegalArgumentException("Targets to activate and deactivate must be disjoint");
        }

        Set<CyNode> targetUnion = new HashSet<>(targetsToActivate);
        targetUnion.addAll(targetsToDeactivate);
        if (!targetUnion.equals(targets)) {
            throw new IllegalArgumentException("Every target must be activated or deactivated");
        }

        // Assignments
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

        if (targets == null) {
            throw new IllegalArgumentException("Target set cannot be null");
        }
        this.targets = targets;

        if (targetsToActivate == null) {
            throw new IllegalArgumentException("Set of targets to activate cannot be null");
        }
        this.targetsToActivate = targetsToActivate;

        if (targetsToDeactivate == null) {
            throw new IllegalArgumentException("Set of targets to deactivate cannot be null");
        }
        this.targetsToDeactivate = targetsToDeactivate;

        if (ciSignAlgorithm == null) {
            throw new IllegalArgumentException("CI sign assignment algorithm cannot be null");
        }
        this.ciSignAlgorithm = ciSignAlgorithm;
    }

    public CyNetwork getNetwork () {
        return network;
    }

    public GenerationContext getGenerationContext () {
        return generationContext;
    }

    public GenerationResults getGenerationResults () {
        return generationResults;
    }

    public Collection<CyNode> getTargets () {
        return targets;
    }

    public Set<CyNode> getTargetsToActivate () {
        return targetsToActivate;
    }

    public Set<CyNode> getTargetsToDeactivate () {
        return targetsToDeactivate;
    }

    public AbstractCISignAssignmentAlgorithm getCISignAlgorithm () {
        return ciSignAlgorithm;
    }
}
