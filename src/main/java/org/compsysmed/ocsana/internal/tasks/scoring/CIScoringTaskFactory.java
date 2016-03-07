/**
 * Factory for tasks to run CI-scoring algorithm
 *
 * Copyright Vera-Licona Research Group (C) 2016
 *
 * This software is licensed under the Artistic License 2.0, see the
 * LICENSE file or
 * http://www.opensource.org/licenses/artistic-license-2.0.php for
 * details
 **/

package org.compsysmed.ocsana.internal.tasks.scoring;

// Java imports
import java.util.*;

// Cytoscape imports
import org.cytoscape.work.AbstractTaskFactory;
import org.cytoscape.work.TaskIterator;

import org.cytoscape.model.CyNode;

// OCSANA imports
import org.compsysmed.ocsana.internal.stages.cistage.CIStageContext;

public class CIScoringTaskFactory
    extends AbstractTaskFactory {
    private CIStageContext ciContext;
    private Set<CyNode> CI;
    private Set<CyNode> targetsToActivate;

    public CIScoringTaskFactory (CIStageContext ciContext,
                                 Set<CyNode> CI,
                                 Set<CyNode> targetsToActivate) {
        this.ciContext = ciContext;
        this.CI = CI;
        this.targetsToActivate = targetsToActivate;
    }

    @Override
    public TaskIterator createTaskIterator () {
        TaskIterator tasks = new TaskIterator();
        tasks.append(new CIScoringTask(ciContext, CI, targetsToActivate));
        return tasks;
    }
}
