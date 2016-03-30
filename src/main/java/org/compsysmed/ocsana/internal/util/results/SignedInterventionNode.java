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

public class SignedInterventionNode {
    public final CyNode node;
    public final InterventionSign sign;
    public final String name;

    public SignedInterventionNode (CyNode node,
                                   InterventionSign sign,
                                   String name) {
        this.node = node;
        this.sign = sign;
        this.name = name;
    }

    public Long getSUID () {
        return node.getSUID();
    }

    public static enum InterventionSign {
        POSITIVE, NEGATIVE
    }
}
