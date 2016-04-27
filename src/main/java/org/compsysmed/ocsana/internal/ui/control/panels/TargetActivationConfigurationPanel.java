/**
 * Panel containing configuration options for target activate
 *
 * Copyright Vera-Licona Research Group (C) 2016
 *
 * This software is licensed under the Artistic License 2.0, see the
 * LICENSE file or
 * http://www.opensource.org/licenses/artistic-license-2.0.php for
 * details
 **/

package org.compsysmed.ocsana.internal.ui.control.panels;

// Java imports
import java.util.*;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

// Cytoscape imports
import org.cytoscape.model.CyColumn;
import org.cytoscape.model.CyNetwork;

import javax.swing.BoxLayout;

import org.cytoscape.work.swing.PanelTaskManager;

// OCSANA imports
import org.compsysmed.ocsana.internal.stages.generation.GenerationContext;
import org.compsysmed.ocsana.internal.stages.prioritization.PrioritizationContextBuilder;

import org.compsysmed.ocsana.internal.ui.control.widgets.*;

/**
 * Subpanel for configuring target activation
 **/
public class TargetActivationConfigurationPanel
    extends AbstractControlSubPanel {
    private GenerationContext generationContext;
    private PrioritizationContextBuilder prioritizationContextBuilder;
    private PanelTaskManager taskManager;

    // UI elements
    private AbstractNodeSetSelecter activatedTargetsSelecter;

    /**
     * Constructor
     *
     * @param generationContext  the context for the generation stage
     * @param taskManager  a PanelTaskManager to provide @Tunable panels
     **/
    public TargetActivationConfigurationPanel (GenerationContext generationContext,
                                               PrioritizationContextBuilder prioritizationContextBuilder,
                                               PanelTaskManager taskManager) {
        // Initial setup
        this.generationContext = generationContext;
        this.prioritizationContextBuilder = prioritizationContextBuilder;
        this.taskManager = taskManager;

        setLayout(new BoxLayout(this, BoxLayout.Y_AXIS));

        add(makeHeader("Configure network processing"));

        // Node set selection widgets
        activatedTargetsSelecter = new ListNodeSetSelecter("Targets to activate", generationContext.getTargetNodes(), generationContext.getNodeNameHandler());
        add(activatedTargetsSelecter);
    }

    @Override
    public void updateContextBuilder () {
        prioritizationContextBuilder.setTargetsToActivate(activatedTargetsSelecter.getSelectedNodes());
    }
}
