/**
 * Interface handler for selecting nodes to activate from a list
 *
 * Copyright Vera-Licona Research Group (C) 2015
 *
 * This software is licensed under the Artistic License 2.0, see the
 * LICENSE file or
 * http://www.opensource.org/licenses/artistic-license-2.0.php for
 * details
 **/

package org.compsysmed.ocsana.internal.util.tunables;

// Java imports
import java.lang.reflect.*;
import java.lang.annotation.*;

import java.util.*;
import java.util.stream.Collectors;

// Cytoscape imports
import org.cytoscape.work.Tunable;

import org.cytoscape.work.util.ListMultipleSelection;

import org.cytoscape.model.CyColumn;
import org.cytoscape.model.CyNode;
import org.cytoscape.model.CyNetwork;
import org.cytoscape.model.CyTable;

public class ListTargetsToActivateSelecter
    extends AbstractNodeSetSelecter {
    // User options
    @Tunable(description="Targets to activate",
             tooltip="<html>Ctrl+click to select multiple nodes<br/>All other nodes will be inhibited</html>",
             gravity=141)
    public ListMultipleSelection<String> targetsToActivateSelecter;

    public ListTargetsToActivateSelecter (CyNetwork network,
                                          Collection<CyNode> targets,
                                          CyColumn nodeNameColumn) {
        super(network, targets, nodeNameColumn);

        List<String> targetNames = new ArrayList<>(getAllNodeNames());
        Collections.sort(targetNames);

        targetsToActivateSelecter = new ListMultipleSelection<>(targetNames);
    }

    public Collection<CyNode> getTargetsToActivate () {
        return targetsToActivateSelecter.getSelectedValues().stream().map(name -> getNode(name)).collect(Collectors.toSet());
    }

}
