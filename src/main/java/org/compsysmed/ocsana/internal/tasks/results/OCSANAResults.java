/**
 * Container to hold results of an OCSANA run
 *
 * Copyright Vera-Licona Research Group (C) 2015
 * @author Andrew Gainer-Dewar, Ph.D. <andrew.gainer.dewar@gmail.com>
 *
 * This software is licensed under the Artistic License 2.0, see the
 * LICENSE file or
 * http://www.opensource.org/licenses/artistic-license-2.0.php for
 * details
 **/

package org.compsysmed.ocsana.internal.tasks.results;

// Java imports
import java.util.*;

// Cytoscape imports
import org.cytoscape.model.CyNode;
import org.cytoscape.model.CyEdge;
import org.cytoscape.model.CyIdentifiable;
import org.cytoscape.model.CyNetwork;

// OCSANA imports
import org.compsysmed.ocsana.internal.algorithms.path.AbstractPathFindingAlgorithm;
import org.compsysmed.ocsana.internal.algorithms.mhs.AbstractMHSAlgorithm;
import org.compsysmed.ocsana.internal.algorithms.scoring.OCSANAScoringAlgorithm;

public class OCSANAResults {
    // User inputs
    public CyNetwork network;
    public Set<CyNode> sourceNodes;
    public Set<CyNode> targetNodes;
    public Set<CyNode> offTargetNodes;

    // Paths data
    public AbstractPathFindingAlgorithm pathFindingAlg;
    public Collection<? extends List<CyEdge>> pathsToTargets;
    public Collection<? extends List<CyEdge>> pathsToOffTargets;

    // Scoring data
    public OCSANAScoringAlgorithm ocsanaAlg;

    // MHS data
    public AbstractMHSAlgorithm mhsAlg;
    public Collection<? extends Collection<CyNode>> MHSes;

    public OCSANAResults () {};
}
