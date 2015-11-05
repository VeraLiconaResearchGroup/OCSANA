package org.cytoscape.myapp.internal;

import java.awt.event.ActionEvent;
import org.cytoscape.application.swing.AbstractCyAction;
import org.cytoscape.application.CyApplicationManager;
import org.cytoscape.model.CyNode;
import org.cytoscape.model.CyEdge;
import org.cytoscape.model.CyNetwork;
import org.cytoscape.view.model.CyNetworkView;
import org.cytoscape.view.presentation.property.BasicVisualLexicon;

/**
 * Creates a new menu item under Apps menu section.
 * http://opentutorials.cgl.ucsf.edu/index.php/Tutorial:Create_a_Bundle_App_Using_IDE
 * http://opentutorials.cgl.ucsf.edu/index.php/Tutorial:Creating_a_Simple_Cytoscape_3_App
 * C:\Users\raha\misagh\my-cyaction-app\target
 * C:\Users\raha\CytoscapeConfiguration\3\apps\installed
 */
public class MenuAction extends AbstractCyAction {

    private final CyApplicationManager applicationManager;

    public MenuAction(final CyApplicationManager applicationManager, final String menuTitle) {
        super(menuTitle, applicationManager, null, null);
        this.applicationManager = applicationManager;
        setPreferredMenu("Apps");
    }

    public void actionPerformed(ActionEvent e) {

        final CyNetworkView currentNetworkView = applicationManager.getCurrentNetworkView();
        if (currentNetworkView == null) {
            return;
        }

        // View is always associated with its model.
        final CyNetwork network = currentNetworkView.getModel();
        Graph graph = new Graph(network);

      

        NewJFrame inputNewJFrame = new NewJFrame(graph);
        inputNewJFrame.setVisible(true);

        for (CyNode node : network.getNodeList()) {

            if (network.getNeighborList(node, CyEdge.Type.ANY).isEmpty()) {
                currentNetworkView.getNodeView(node).setVisualProperty(BasicVisualLexicon.NODE_VISIBLE, true);
            }
        }
        currentNetworkView.updateView();

    }

}
