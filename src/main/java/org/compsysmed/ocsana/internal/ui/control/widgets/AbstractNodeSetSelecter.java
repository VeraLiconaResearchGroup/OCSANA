/**
 * Abstract base class for widgets to let users select sets of nodes
 *
 * Copyright Vera-Licona Research Group (C) 2016
 *
 * This software is licensed under the Artistic License 2.0, see the
 * LICENSE file or
 * http://www.opensource.org/licenses/artistic-license-2.0.php for
 * details
 **/

package org.compsysmed.ocsana.internal.ui.control.widgets;

// Java imports
import java.util.*;

import javax.swing.JPanel;

// Cytoscape imports
import org.cytoscape.model.CyNode;

// OCSANA imports
import org.compsysmed.ocsana.internal.util.tunables.NodeNameHandler;

abstract public class AbstractNodeSetSelecter
    extends JPanel {
    protected String label;
    protected Collection<CyNode> availableNodes;
    protected NodeNameHandler nodeNameHandler;

    /**
     * Constructor
     *
     * @param label  the label text for the selecter
     * @param availableNodes  the nodes the user should choose from
     * @param selectedNodes  the selected nodes (defaults to an empty set)
     * @param nodeNameHandler  the handler to compute node names
     **/
    public AbstractNodeSetSelecter (String label,
                                    Collection<CyNode> availableNodes,
                                    Set<CyNode> selectedNodes,
                                    NodeNameHandler nodeNameHandler) {
        super();
        this.label = label;
        this.availableNodes = availableNodes;
        this.nodeNameHandler = nodeNameHandler;

        setSelectedNodes(selectedNodes);
    }

    /**
     * Constructor
     *
     * @param label  the label text for the selecter
     * @param availableNodes  the nodes the user should choose from
     * @param nodeNameHandler  the handler to compute node names
     **/
    public AbstractNodeSetSelecter (String label,
                                    Collection<CyNode> availableNodes,
                                    NodeNameHandler nodeNameHandler) {
        this(label, availableNodes, new HashSet<>(), nodeNameHandler);
    }

    /**
     * Copy constructor
     *
     * @param other  another AbstractNodeSetSelecter
     **/
    public AbstractNodeSetSelecter (AbstractNodeSetSelecter other) {
        this(other.label, other.availableNodes, other.getSelectedNodes(), other.nodeNameHandler);
    }

    /**
     * Copy constructor with replacement NodeNameHandler
     *
     * @param other  another AbstractNodeSetSelecter
     * @param nodeNameHandler  the new NodeNameHandler
     **/
    public AbstractNodeSetSelecter (AbstractNodeSetSelecter other,
                                    NodeNameHandler nodeNameHandler) {
        this(other.label, other.availableNodes, other.getSelectedNodes(), nodeNameHandler);
    }

    /**
     * Update the selecter with a new NodeNameHandler
     *
     * @param nodeNameHandler the new NodeNameHandler
     **/
    public void updateNodeNameHandler (NodeNameHandler nodeNameHandler) {
        this.nodeNameHandler = nodeNameHandler;

        setSelectedNodes(getSelectedNodes());
    }

    /**
     * Return the selected nodes
     **/
    abstract public Set<CyNode> getSelectedNodes ();

    /**
     * Set the selected nodes
     * <p>
     * Note to implementers: you should handle updating your UI in this method
     **/
    abstract public void setSelectedNodes (Set<CyNode> selectedNodes);
}
