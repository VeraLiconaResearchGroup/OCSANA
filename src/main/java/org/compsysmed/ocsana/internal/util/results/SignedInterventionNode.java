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

// OCSANA imports
import org.compsysmed.ocsana.internal.util.science.InteractionSign;

public class SignedInterventionNode {
    private final CyNode node;
    private final InteractionSign sign;
    private final String name;

    public SignedInterventionNode (CyNode node,
                                   InteractionSign sign,
                                   String name) {
        if (node == null) {
            throw new IllegalArgumentException("Node cannot be null");
        }
        this.node = node;

        if (sign == null) {
            throw new IllegalArgumentException("Sign cannot be null");
        }
        this.sign = sign;

        if (name == null) {
            throw new IllegalArgumentException("Name cannot be null");
        }
        this.name = name;
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

    public Long getSUID () {
        return node.getSUID();
    }
}