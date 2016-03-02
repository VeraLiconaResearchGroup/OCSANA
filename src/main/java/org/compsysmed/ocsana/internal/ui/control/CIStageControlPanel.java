/**
 * Panel to contain OCSANA controls for CI stage
 *
 * Copyright Vera-Licona Research Group (C) 2015
 *
 * This software is licensed under the Artistic License 2.0, see the
 * LICENSE file or
 * http://www.opensource.org/licenses/artistic-license-2.0.php for
 * details
 **/

package org.compsysmed.ocsana.internal.ui.control;

// Java imports
import java.util.*;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.BoxLayout;
import javax.swing.JPanel;

// Cytoscape imports
import org.cytoscape.model.CyNetwork;

import org.cytoscape.work.swing.PanelTaskManager;

// OCSANA imports
import org.compsysmed.ocsana.internal.stages.cistage.CIStageContext;

/**
 * Panel to configure and run OCSANA CI stage
 **/
public class CIStageControlPanel
    extends JPanel
    implements ActionListener {
    private PanelTaskManager panelTaskManager;
    private CIStageContext ciStageContext;
    private CyNetwork network;

    public CIStageControlPanel (CyNetwork network,
                                PanelTaskManager panelTaskManager) {
        this.panelTaskManager = panelTaskManager;

        setLayout(new BoxLayout(this, BoxLayout.PAGE_AXIS));

        ciStageContext = new CIStageContext(network);
        ciStageContext.addActionListener(this);

        buildPanel();
    }

    public void buildPanel () {
        removeAll();

        JPanel tunablePanel = panelTaskManager.getConfiguration(null, ciStageContext);
        add(tunablePanel);

        setSize(getMinimumSize());

        revalidate();
        repaint();
    }

    // Helper functions to support listening for component changes
    public void actionPerformed (ActionEvent event) {
        // Synchronize any changes made to the @Tunable UI widgets
        panelTaskManager.validateAndApplyTunables(ciStageContext);

        // Copy the context so PanelTaskManager.getConfiguration()
        // will have a cache miss and rebuild the JPanel.
        // This is a workaround for a bug.
        //ciStageContext = new CIStageContext(ciStageContext);

        buildPanel();
    }
}
