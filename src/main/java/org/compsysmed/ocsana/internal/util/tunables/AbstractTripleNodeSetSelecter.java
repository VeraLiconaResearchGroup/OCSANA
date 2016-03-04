/**
 * Abstract interface for triple node set selecters
 *
 * Copyright Vera-Licona Research Group (C) 2015
 *
 * This software is licensed under the Artistic License 2.0, see the
 * LICENSE file or
 * http://www.opensource.org/licenses/artistic-license-2.0.php for
 * details
 **/

package org.compsysmed.ocsana.internal.util.tunables;

// Java imports
import java.util.*;

// Cytoscape imports
import org.cytoscape.model.CyColumn;
import org.cytoscape.model.CyNode;
import org.cytoscape.model.CyNetwork;
import org.cytoscape.model.CyTable;

abstract public class AbstractTripleNodeSetSelecter
    extends AbstractNodeSetSelecter {
    public AbstractTripleNodeSetSelecter (CyNetwork network,
                                          Collection<CyNode> nodes,
                                          CyColumn nodeNameColumn) {
        super(network, nodes, nodeNameColumn);
    }

    abstract public List<CyNode> getSelectedSourceNodes ();
    abstract public List<String> getSelectedSourceNodeNames ();

    abstract public List<CyNode> getSelectedTargetNodes ();
    abstract public List<String> getSelectedTargetNodeNames ();

    abstract public List<CyNode> getSelectedOffTargetNodes ();
    abstract public List<String> getSelectedOffTargetNodeNames ();
}
