/**
 * Container to hold results of an OCSANA run
 *
 * Copyright Vera-Licona Research Group (C) 2015
 *
 * This software is licensed under the Artistic License 2.0, see the
 * LICENSE file or
 * http://www.opensource.org/licenses/artistic-license-2.0.php for
 * details
 **/

package org.compsysmed.ocsana.internal.stages.generation;

// Java imports
import java.util.*;
import java.util.stream.*;

// Cytoscape imports
import org.cytoscape.model.CyNode;
import org.cytoscape.model.CyEdge;

// OCSANA imports
import org.compsysmed.ocsana.internal.util.results.CombinationOfInterventions;

public class GenerationResults {
    // Paths data
    public Collection<List<CyEdge>> pathsToTargets;
    public Collection<List<CyEdge>> pathsToOffTargets;

    public Double pathsToTargetsExecutionSeconds;
    public Double pathsToOffTargetsExecutionSeconds;

    public Boolean pathFindingCanceled = false;

    // Scoring data
    public Double OCSANAScoringExecutionSeconds;
    public Boolean OCSANAScoringCanceled = false;

    // MHS data
    public Collection<CombinationOfInterventions> CIs;
    public Double mhsExecutionSeconds;
    public Boolean mhsFindingCanceled = false;

    public Collection<List<CyEdge>> getPathsToTargets () {
        return pathsToTargets;
    }

    public Collection<List<CyEdge>> getPathsToOffTargets () {
        return pathsToOffTargets;
    }

    public Double getPathsToTargetsExecutionSeconds () {
        return pathsToTargetsExecutionSeconds;
    }

    public Double getPathsToOffTargetsExecutionSeconds () {
        return pathsToOffTargetsExecutionSeconds;
    }

    public Boolean pathFindingWasCanceled () {
        return pathFindingCanceled;
    }

    public Double getOCSANAScoringExecutionSeconds () {
        return OCSANAScoringExecutionSeconds;
    }

    public Boolean OCSANAScoringWasCanceled () {
        return OCSANAScoringCanceled;
    }

    public Collection<CombinationOfInterventions> getCIs () {
        return CIs;
    }

    public Double getMHSExecutionSeconds () {
        return mhsExecutionSeconds;
    }

    public Boolean MHSFindingWasCanceled () {
        return mhsFindingCanceled;
    }

    public Set<CyNode> getElementaryNodes () {
        return pathsToTargets.stream().flatMap(path -> path.stream()).flatMap(edge -> Stream.of(edge.getSource(), edge.getTarget())).collect(Collectors.toSet());
    }
}
