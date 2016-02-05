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
import java.util.stream.Collectors;

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
    private static final String CONFIG_GROUP = "Process edges";

    // User configuration
    @Tunable(description = "Process edge activation and inhibition",
             tooltip = "If enabled, consider whether each edge represents an activation or an inhibition, using information from an edge attribute in the table",
             gravity = 190,
             groups = {CONFIG_GROUP})
    public Boolean processEdgeSigns = false;

    @Tunable(description = "Column storing edge interactions",
             gravity = 191,
             groups = {CONFIG_GROUP},
             dependsOn = "processEdgeSigns=true")
    public ListSingleSelection<CyColumn> getEdgeSignColumnSelecter () {
        return edgeSignColumnSelecter;
    };

    public void setEdgeSignColumnSelecter (ListSingleSelection<CyColumn> edgeSignColumnSelecter) {
        this.edgeSignColumnSelecter = edgeSignColumnSelecter;
        populateWithEdgeSignColumn();
    }

    private ListSingleSelection<CyColumn> edgeSignColumnSelecter;

    @Tunable(description = "Column value representing inhibition",
             tooltip = "All other values will be interpreted as activation",
             gravity = 192,
             groups = {CONFIG_GROUP},
             dependsOn = "processEdgeSigns=true",
             listenForChange = "EdgeSignColumnSelecter")
    public ListMultipleSelection<Object> edgeInhibitionValueSelecter;

    // End user configuration

    private CyNetwork network;
    public EdgeProcessor (CyNetwork network) {
        this.network = network;

        CyTable edgeTable = network.getDefaultEdgeTable();
        edgeSignColumnSelecter = new ListSingleSelection<>(new ArrayList<>(edgeTable.getColumns()));
        populateWithEdgeSignColumn();
    }

    private CyColumn selectedColumn;
    private void populateWithEdgeSignColumn () {
        CyColumn selectedColumn = edgeSignColumnSelecter.getSelectedValue();
        if (selectedColumn.equals(this.selectedColumn)) {
            return;
        }
        this.selectedColumn = selectedColumn;

        Set<Object> columnValues = selectedColumn.getValues(selectedColumn.getType()).stream().collect(Collectors.toSet());

        edgeInhibitionValueSelecter = new ListMultipleSelection<Object>(new ArrayList<>(columnValues));
    }

    /**
     * Test whether this edge is inhibitory.
     *
     * NOTE: if processEdgeSigns is false, this always returns false
     *
     * @param edge  the edge
     **/
    public Boolean edgeIsInhibition (CyEdge edge) {
        if (!processEdgeSigns) {
            return false;
        }

        assert network.containsEdge(edge);

        CyRow edgeRow = network.getDefaultEdgeTable().getRow(edge.getSUID());
        CyColumn signColumn = edgeSignColumnSelecter.getSelectedValue();
        Object edgeValue = edgeRow.get(signColumn.getName(), signColumn.getType());

        List<Object> inhibitionValues = edgeInhibitionValueSelecter.getSelectedValues();

        return inhibitionValues.contains(edgeValue);
    }

    /**
     * Return a descriptive string suitable for printing in a report
     **/
    public String description () {
        StringBuilder result = new StringBuilder("Edge effect processor (");

        if (processEdgeSigns) {
            result.append(String.format("edge column: %s, inhibition values: %s", edgeSignColumnSelecter.getSelectedValue().toString(), edgeInhibitionValueSelecter.getSelectedValues().toString()));
        } else {
            result.append("no processing, all edges activation");
        }

        result.append(")");
        return result.toString();
    }
}
