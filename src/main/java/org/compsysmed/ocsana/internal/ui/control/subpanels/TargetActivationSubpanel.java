/**
 * Subpanel configuring targets to activate
 *
 * Copyright Vera-Licona Research Group (C) 2016
 *
 * This software is licensed under the Artistic License 2.0, see the
 * LICENSE file or
 * http://www.opensource.org/licenses/artistic-license-2.0.php for
 * details
 **/

package org.compsysmed.ocsana.internal.ui.control.subpanels;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.BoxLayout;
import javax.swing.JButton;

import org.cytoscape.work.swing.PanelTaskManager;

// OCSANA imports
import org.compsysmed.ocsana.internal.util.context.ContextBundleBuilder;

import org.compsysmed.ocsana.internal.ui.control.OCSANAControlPanel;

import org.compsysmed.ocsana.internal.ui.control.widgets.*;

/**
 * Subpanel for configuring target activation
 **/
public class TargetActivationSubpanel
    extends AbstractControlSubpanel {
    private ContextBundleBuilder contextBundleBuilder;
    private PanelTaskManager taskManager;

    // UI elements
    private AbstractNodeSetSelecter activatedTargetsSelecter;

    /**
     * Constructor
     *
     * @param contextBundleBuilder  the context builder
     * @param taskManager  a PanelTaskManager to provide @Tunable panels
     **/
    public TargetActivationSubpanel (OCSANAControlPanel controlPanel,
                                     ContextBundleBuilder contextBundleBuilder,
                                     PanelTaskManager taskManager) {
        super(controlPanel);

        // Initial setup
        this.contextBundleBuilder = contextBundleBuilder;
        this.taskManager = taskManager;

        setLayout(new BoxLayout(this, BoxLayout.Y_AXIS));

        add(makeHeader("Configure network processing"));

        // Node set selection widgets
        activatedTargetsSelecter = new ListNodeSetSelecter("Targets to activate", contextBundleBuilder.getTargetNodes(), contextBundleBuilder.getNodeHandler());
        add(activatedTargetsSelecter);

        // Target list refresh button
        JButton refreshButton = new JButton("Refresh targets list");
        add(refreshButton);
        refreshButton.addActionListener(new ActionListener() {
                @Override
                public void actionPerformed (ActionEvent e) {
                    requestContextBundleBuilderUpdate();
                    activatedTargetsSelecter.setAvailableNodes(contextBundleBuilder.getTargetNodes());
                }
            });
    }

    @Override
    public void updateContextBuilder () {
        contextBundleBuilder.setTargetsToActivate(activatedTargetsSelecter.getSelectedNodes());
    }
}
