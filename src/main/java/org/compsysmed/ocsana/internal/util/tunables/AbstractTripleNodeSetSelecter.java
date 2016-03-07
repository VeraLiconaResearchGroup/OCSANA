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

    public AbstractTripleNodeSetSelecter (CyNetwork network,
                                          Collection<CyNode> nodes,
                                          CyColumn nodeNameColumn,
                                          Collection<CyNode> selectedSourceNodes,
                                          Collection<CyNode> selectedTargetNodes,
                                          Collection<CyNode> selectedOffTargetNodes) {
        this(network, nodes, nodeNameColumn);

        setSelectedSourceNodes(selectedSourceNodes);
        setSelectedTargetNodes(selectedTargetNodes);
        setSelectedOffTargetNodes(selectedOffTargetNodes);
    }

    public AbstractTripleNodeSetSelecter (AbstractTripleNodeSetSelecter other) {
        this(other.network, other.nodes, other.nodeNameColumn, other.getSelectedSourceNodes(), other.getSelectedTargetNodes(), other.getSelectedOffTargetNodes());
    }

    abstract public Collection<CyNode> getSelectedSourceNodes ();
    abstract public Collection<String> getSelectedSourceNodeNames ();
    abstract protected void setSelectedSourceNodes (Collection<CyNode> selectedSourceNodes);

    abstract public Collection<CyNode> getSelectedTargetNodes ();
    abstract public Collection<String> getSelectedTargetNodeNames ();
    abstract protected void setSelectedTargetNodes (Collection<CyNode> selectedTargetNodes);

    abstract public Collection<CyNode> getSelectedOffTargetNodes ();
    abstract public Collection<String> getSelectedOffTargetNodeNames ();
    abstract protected void setSelectedOffTargetNodes (Collection<CyNode> selectedOffTargetNodes);
}
