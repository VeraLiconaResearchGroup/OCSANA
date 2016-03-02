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

package org.compsysmed.ocsana.internal.stages.cistage;

// Java imports
import java.util.*;
import java.util.stream.Collectors;

// Cytoscape imports
import org.cytoscape.model.CyNode;
import org.cytoscape.model.CyEdge;

// OCSANA imports


public class CIStageResults {
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
    public Collection<Set<CyNode>> MHSes;
    public Double mhsExecutionSeconds;
    public Boolean mhsFindingCanceled = false;

    public List<String> getReportLines () {
        return new ArrayList<String>();
    }
}
