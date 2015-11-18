/**
 * Factory for main OCSANA task
 *
 * Copyright Vera-Licona Research Group (C) 2015
 * @author Andrew Gainer-Dewar, Ph.D. <andrew.gainer.dewar@gmail.com>
 *
 * This software is licensed under the Artistic License 2.0, see the
 * LICENSE file or
 * http://www.opensource.org/licenses/artistic-license-2.0.php for
 * details
 **/

package org.compsysmed.ocsana.internal.tasks;

import org.cytoscape.task.AbstractNetworkTaskFactory;
import org.cytoscape.work.TaskIterator;

import org.cytoscape.model.CyNetwork;

public class OCSANATaskFactory extends AbstractNetworkTaskFactory {
    public TaskIterator createTaskIterator (CyNetwork network) {
        TaskIterator tasks = new TaskIterator();
        tasks.append(new OCSANATask(network));
        return tasks;
    }
}
