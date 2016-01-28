/**
 * Factory for tasks to run MHS algorithms in OCSANA
 *
 * Copyright Vera-Licona Research Group (C) 2015
 *
 * This software is licensed under the Artistic License 2.0, see the
 * LICENSE file or
 * http://www.opensource.org/licenses/artistic-license-2.0.php for
 * details
 **/

package org.compsysmed.ocsana.internal.tasks.mhs;

// Java imports
import java.util.*;

// Cytoscape imports
import org.cytoscape.work.AbstractTaskFactory;
import org.cytoscape.work.TaskIterator;

// OCSANA imports
import org.compsysmed.ocsana.internal.tasks.results.OCSANAResults;

public class MHSAlgorithmTaskFactory extends AbstractTaskFactory {
    private OCSANAResults results;

    public MHSAlgorithmTaskFactory (OCSANAResults results) {
        super();
        this.results = results;
    }

    @Override
    public TaskIterator createTaskIterator () {
        TaskIterator tasks = new TaskIterator();
        tasks.append(new MHSAlgorithmTask(results));
        return tasks;
    }
}
