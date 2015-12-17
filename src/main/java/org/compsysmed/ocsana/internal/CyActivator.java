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

import org.cytoscape.application.swing.CySwingApplication;
import org.cytoscape.application.swing.CytoPanelComponent;

import static org.cytoscape.work.ServiceProperties.*;
import org.cytoscape.service.util.AbstractCyActivator;
import org.osgi.framework.BundleContext;
import org.cytoscape.task.NetworkTaskFactory;

// OCSANA imports
import org.compsysmed.ocsana.internal.tasks.OCSANACoordinatorTaskFactory;
import org.compsysmed.ocsana.internal.ui.OCSANAResultsPanel;

public class CyActivator extends AbstractCyActivator {
    public void start (BundleContext bc) throws Exception {
        // Get a TaskManager
        TaskManager<?, ?> taskManager = getService(bc, TaskManager.class);

        // Results panel registration
        CySwingApplication cySwingApplicationService = getService(bc,CySwingApplication.class);
        OCSANAResultsPanel resultsPanel =
            new OCSANAResultsPanel(cySwingApplicationService);
        registerService(bc, resultsPanel, CytoPanelComponent.class, new Properties());

        // Main OCSANA task registration
        Properties properties = new Properties();

        properties.setProperty(PREFERRED_MENU, "Apps");
        properties.setProperty(TITLE, "OCSANA");

        registerService(bc,
                        new OCSANACoordinatorTaskFactory(taskManager, resultsPanel),
                        NetworkTaskFactory.class,
                        properties);
    }

}