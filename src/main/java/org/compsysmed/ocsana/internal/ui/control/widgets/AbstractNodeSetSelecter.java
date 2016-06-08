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
    protected final String label;
    protected NodeNameHandler nodeNameHandler;
    private Set<CyNode> availableNodes;

    /**
     * Constructor
     *
     * @param label  the label text for the selecter
     * @param availableNodes  the nodes the user should choose from
     * @param selectedNodes  the selected nodes (defaults to an empty set)
     * @param nodeNameHandler  the handler to compute node names
     **/
    public AbstractNodeSetSelecter (String label,
                                    Set<CyNode> availableNodes,
                                    Set<CyNode> selectedNodes,
                                    NodeNameHandler nodeNameHandler) {
        super();

        Objects.requireNonNull(label, "Label cannot be null");
        this.label = label;

        Objects.requireNonNull(nodeNameHandler, "Node name handler cannot be null");
        this.nodeNameHandler = nodeNameHandler;

        Objects.requireNonNull(availableNodes, "Set of available nodes cannot be null");
        setAvailableNodes(availableNodes);

        Objects.requireNonNull(selectedNodes, "Set of selected nodes cannot be null");
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
                                    Set<CyNode> availableNodes,
                                    NodeNameHandler nodeNameHandler) {
        this(label, availableNodes, new HashSet<>(), nodeNameHandler);
    }

    /**
     * Copy constructor
     *
     * @param other  another AbstractNodeSetSelecter
     **/
    public AbstractNodeSetSelecter (AbstractNodeSetSelecter other) {
        this(other.label, other.getAvailableNodes(), other.getSelectedNodes(), other.nodeNameHandler);
    }

    /**
     * Copy constructor with replacement NodeNameHandler
     *
     * @param other  another AbstractNodeSetSelecter
     * @param nodeNameHandler  the new NodeNameHandler
     **/
    public AbstractNodeSetSelecter (AbstractNodeSetSelecter other,
                                    NodeNameHandler nodeNameHandler) {
        this(other.label, other.getAvailableNodes(), other.getSelectedNodes(), nodeNameHandler);
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
     * Return the underlying node set
     **/
    public Set<CyNode> getAvailableNodes () {
        return availableNodes;
    }

    /**
     * Update the underlying node set
     * @param availableNodes  the new set of nodes
     **/
    public void setAvailableNodes (Set<CyNode> availableNodes) {
        Objects.requireNonNull(availableNodes, "Set of available nodes must not be empty");
        this.availableNodes = availableNodes;
        handleAvailableNodesUpdate();
    };

    /**
     * Respond to an update in the underlying node set
     * <p>
     * Note to implementers: you should handle updating your UI in this method
     **/
    abstract protected void handleAvailableNodesUpdate ();

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
