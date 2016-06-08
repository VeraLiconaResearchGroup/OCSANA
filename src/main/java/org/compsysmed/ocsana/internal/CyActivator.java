/**
 * Outer wrapper class for OCSANA Cytoscape plugin
 *
 * Copyright Vera-Licona Research Group (C) 2015
 *
 * This software is licensed under the Artistic License 2.0, see the
 * LICENSE file or
 * http://www.opensource.org/licenses/artistic-license-2.0.php for
 * details
 **/

package org.compsysmed.ocsana.internal;

// Java imports
import java.util.Properties;

// Cytoscape imports
import org.cytoscape.work.TaskManager;

import org.cytoscape.work.swing.PanelTaskManager;

import org.cytoscape.application.CyApplicationManager;
import org.cytoscape.application.events.SetCurrentNetworkListener;

import org.cytoscape.application.swing.CySwingApplication;
import org.cytoscape.application.swing.CytoPanel;
import org.cytoscape.application.swing.CytoPanelComponent;
import org.cytoscape.application.swing.CytoPanelName;

import org.cytoscape.service.util.AbstractCyActivator;
import org.osgi.framework.BundleContext;
// OCSANA imports
import org.compsysmed.ocsana.internal.ui.control.OCSANAControlPanel;
import org.compsysmed.ocsana.internal.ui.results.OCSANAResultsPanel;

public class CyActivator extends AbstractCyActivator {
    @Override
    public void start (BundleContext bc) throws Exception {
        // Get Cytoscape internal utilities
        TaskManager<?, ?> taskManager = getService(bc, TaskManager.class);

        CyApplicationManager cyApplicationManager = getService(bc, CyApplicationManager.class);

        CySwingApplication cySwingApplication = getService(bc, CySwingApplication.class);
        CytoPanel cyControlPanel = cySwingApplication.getCytoPanel(CytoPanelName.WEST);

        PanelTaskManager panelTaskManager = getService(bc, PanelTaskManager.class);

        // Results panel registration
        OCSANAResultsPanel resultsPanel =
            new OCSANAResultsPanel(cySwingApplication);
        registerService(bc, resultsPanel, CytoPanelComponent.class, new Properties());

        // Control panel registration
        OCSANAControlPanel controlPanel =
            new OCSANAControlPanel(cyApplicationManager, cyControlPanel, resultsPanel, panelTaskManager);
        registerService(bc, controlPanel, CytoPanelComponent.class, new Properties());
        registerService(bc, controlPanel, SetCurrentNetworkListener.class, new Properties());
    }
}
