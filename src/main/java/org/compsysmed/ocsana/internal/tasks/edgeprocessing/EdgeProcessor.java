/**
 * Interface handler for processing edges in OCSANA
 *
 * Copyright Vera-Licona Research Group (C) 2015
 *
 * This software is licensed under the Artistic License 2.0, see the
 * LICENSE file or
 * http://www.opensource.org/licenses/artistic-license-2.0.php for
 * details
 **/

package org.compsysmed.ocsana.internal.tasks.edgeprocessing;

// Java imports
import java.util.*;

// Cytoscape imports
import org.cytoscape.work.Tunable;
import org.cytoscape.work.util.ListSingleSelection;
import org.cytoscape.work.util.ListMultipleSelection;

import org.cytoscape.model.CyColumn;
import org.cytoscape.model.CyEdge;
import org.cytoscape.model.CyNetwork;
import org.cytoscape.model.CyRow;
import org.cytoscape.model.CyTable;

// OCSANA imports

/**
 * Interface handler for processing edges in OCSANA
 *
 * @param network  the network to compute on
 **/

public class EdgeProcessor {
    private static final String configGroup = "Process edges";

    // User configuration
    @Tunable(description = "Process edge activation and inhibition",
             tooltip = "If enabled, consider whether each edge represents an activation or an inhibition, using information from an edge attribute in the table",
             gravity = 190,
             groups = {configGroup})
    public Boolean processEdgeSigns = false;

    @Tunable(description = "Column storing edge interactions",
             gravity = 191,
             groups = {configGroup},
             dependsOn = "processEdgeSigns=true")
    public ListSingleSelection<CyColumn> edgeSignColumnSelecter;

    @Tunable(description = "Column value representing inhibition",
             tooltip = "All other values will be interpreted as activation",
             gravity = 192,
             groups = {configGroup},
             dependsOn = "processEdgeSigns=true",
             listenForChange = "edgeSignColumnSelecter")
    public ListSingleSelection<Object> getEdgeInhibitionValue () {
        CyColumn selectedColumn = edgeSignColumnSelecter.getSelectedValue();
        List<Object> columnValuesList = selectedColumn.getValues(selectedColumn.getType());
        Set<Object> columnValues = new HashSet<>(columnValuesList);

        edgeInhibitionValueSelecter = new ListSingleSelection<Object>(new ArrayList<>(columnValues));
        return edgeInhibitionValueSelecter;
    };

    public void setEdgeInhibitionValue (ListSingleSelection<Object> edgeInhibitionValueSelecter) {
        this.edgeInhibitionValueSelecter = edgeInhibitionValueSelecter;
    }

    private ListSingleSelection<Object> edgeInhibitionValueSelecter;

    // End user configuration

    private CyNetwork network;
    public EdgeProcessor (CyNetwork network) {
        this.network = network;

        CyTable edgeTable = network.getDefaultEdgeTable();
        edgeSignColumnSelecter = new ListSingleSelection<>(new ArrayList<>(edgeTable.getColumns()));
    }

    public Boolean edgeIsInhibition (CyEdge edge) {
        if (!processEdgeSigns) {
            return false;
        }

        assert network.containsEdge(edge);

        CyRow edgeRow = network.getDefaultEdgeTable().getRow(edge.getSUID());
        CyColumn signColumn = edgeSignColumnSelecter.getSelectedValue();
        Object edgeValue = edgeRow.get(signColumn.getName(), signColumn.getType());

        Object inhibitionValue = edgeInhibitionValueSelecter.getSelectedValue();

        return edgeValue.equals(inhibitionValue);
    }
}
