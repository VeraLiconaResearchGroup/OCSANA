/**
 * Class representing a sign assignment for a particular node
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

// Cytoscape imports
import org.cytoscape.model.CyNode;

import java.util.Objects;

// OCSANA imports
import org.compsysmed.ocsana.internal.util.science.InteractionSign;

public class SignedInterventionNode {
    private final CyNode node;
    private final InteractionSign sign;
    private final String name;
    private final String biomoleculeID;

    public SignedInterventionNode (CyNode node,
                                   InteractionSign sign,
                                   String name,
                                   String biomoleculeID) {
        Objects.requireNonNull(node, "Node cannot be null");
        this.node = node;

        Objects.requireNonNull(sign, "Sign cannot be null");
        this.sign = sign;

        Objects.requireNonNull(name, "Name cannot be null");
        this.name = name;

        Objects.requireNonNull(biomoleculeID, "Biomoledule ID cannot be null");
        this.biomoleculeID = biomoleculeID;
    }

    public CyNode getNode () {
        return node;
    }

    public InteractionSign getSign () {
        return sign;
    }

    public String getName () {
        return name;
    }

    public String getBiomoleculeID () {
        return biomoleculeID;
    }

    public Long getSUID () {
        return node.getSUID();
    }
}
