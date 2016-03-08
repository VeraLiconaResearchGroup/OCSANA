/**
 * Class representing a combination of interventions in a signaling network
 *
 * Copyright Vera-Licona Research Group (C) 2016
 *
 * This software is licensed under the Artistic License 2.0, see the
 * LICENSE file or
 * http://www.opensource.org/licenses/artistic-license-2.0.php for
 * details
 **/

package org.compsysmed.ocsana.internal.util.results;

// Java imports
import java.util.*;

// Cytoscape imports
import org.cytoscape.model.CyNode;

/**
 * Class representing a combination of interventions in a signaling
 * network
 **/
public class CombinationOfInterventions {
    private final Set<CyNode> ciNodes;
    private final Set<CyNode> targetNodes;

    private Double classicalOCSANAScore;

    /**
     * Constructor
     *
     * @param ciNodes  the nodes in this CI
     * @param targetNodes  the target nodes that this CI dominates
     **/
    public CombinationOfInterventions (Set<CyNode> ciNodes,
                                       Set<CyNode> targetNodes) {
        this.ciNodes = ciNodes;
        this.targetNodes = targetNodes;
    }

    /**
     * Get the nodes in this CI
     **/
    public Set<CyNode> getNodes () {
        return ciNodes;
    }

    /**
     * Get the size of this CI
     **/
    public Integer size () {
        return ciNodes.size();
    }

    /**
     * Set the classical OCSANA score of this CI
     **/
    public void setClassicalOCSANAScore (Double classicalOCSANAScore) {
        this.classicalOCSANAScore = classicalOCSANAScore;
    }

    /**
     * Get the classical OCSANA score of this CI
     **/
    public Double getClassicalOCSANAScore () {
        return classicalOCSANAScore;
    }
}
