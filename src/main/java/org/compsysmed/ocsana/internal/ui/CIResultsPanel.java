/**
 * Panel to display CI results in OCSANA
 *
 * Copyright Vera-Licona Research Group (C) 2015
 * @author Andrew Gainer-Dewar, Ph.D. <andrew.gainer.dewar@gmail.com>
 *
 * This software is licensed under the Artistic License 2.0, see the
 * LICENSE file or
 * http://www.opensource.org/licenses/artistic-license-2.0.php for
 * details
 **/

package org.compsysmed.ocsana.internal.ui;

// Java imports
import java.util.*;

import java.awt.Component;
import java.awt.BorderLayout;

import javax.swing.Icon;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JTabbedPane;
import javax.swing.JTable;
import javax.swing.JScrollPane;

import javax.swing.border.TitledBorder;

// Cytoscape imports
import org.cytoscape.application.swing.CySwingApplication;
import org.cytoscape.application.swing.CytoPanel;
import org.cytoscape.application.swing.CytoPanelComponent;
import org.cytoscape.application.swing.CytoPanelName;

import org.cytoscape.model.CyNetwork;
import org.cytoscape.model.CyNode;
import org.cytoscape.model.CyEdge;

// OCSANA imports
import org.compsysmed.ocsana.internal.tasks.results.OCSANAResults;

public class CIResultsPanel
    extends JPanel {
    protected CytoPanel cyResultsPanel;

    /**
     * Construct a new blank results panel
     **/
    public CIResultsPanel () {
        // Called when plugin is loaded (i.e. at new network load)
        super();
    }

    /**
     * Construct a results panel for some results
     *
     * @param results  the results
     **/
    public CIResultsPanel (OCSANAResults results) {
        super(new BorderLayout());

        JTabbedPane resultsTabbedPanel = new JTabbedPane();
        this.add(resultsTabbedPanel, BorderLayout.CENTER);
        this.setBorder(new TitledBorder("Results"));

        // TODO: Fit in window
        if (results.MHSes != null) {
            Vector<Vector<Object>> mhsRows = getMHSRows(results);

            JTable mhsTable = new JTable(mhsRows, mhsCols);
            mhsTable.setAutoCreateRowSorter(true);
            mhsTable.setAutoResizeMode(JTable.AUTO_RESIZE_ALL_COLUMNS);

            JScrollPane mhsScrollPane = new JScrollPane(mhsTable);

            JPanel mhsPanel = new JPanel(new BorderLayout());
            String mhsText = "Found " + results.MHSes.size() + " optimal CIs.";
            mhsPanel.add(new JLabel(mhsText), BorderLayout.PAGE_START);
            mhsPanel.add(mhsScrollPane, BorderLayout.CENTER);

            mhsPanel.revalidate();

            resultsTabbedPanel.addTab("Optimal CIs", mhsPanel);
        }

        // TODO: Paths to targets
        // TODO: Paths to off-targets
        // TODO: Report file generation

        revalidate();
    }

    //
    protected Vector<Vector<Object>> getMHSRows (OCSANAResults results) {
        Vector<Vector<Object>> rows = new Vector<>();
        for (Collection<CyNode> MHS: results.MHSes) {
            Vector<Object> row = new Vector<>();
            row.add(nodeSetString(MHS, results));
            row.add(MHS.size());
            row.add(results.ocsanaAlg.getScore(MHS));

            rows.add(row);
        }
        return rows;
    }

    protected static final Vector<String> mhsCols =
        new Vector<>(Arrays.asList(new String[] {"CI", "Size", "Score"}));


    /**
     * Get the results panel component
     */
    public Component getComponent() {
        return this;
    }

    /**
     * Get the results panel name
     */
    public CytoPanelName getCytoPanelName () {
        return CytoPanelName.EAST;
    }

    /**
     * Get the results panel title
     */
    public String getTitle() {
        return "OCSANA Results";
    }

    /**
     * Get the results panel icon
     */
    public Icon getIcon() {
        return null;
    }

    // Helper functions for generating report sections
    private String report (OCSANAResults results) {
        List<String> reportLines = reportLines(results);
        String report = String.join("\n", reportLines);
        return report;
    }

    private List<String> reportLines (OCSANAResults results) {
        List<String> lines = new ArrayList<> ();

        lines.addAll(inputReportLines(results));
        lines.add(SECTION_SEPARATOR);

        lines.addAll(pathReportLines(results));
        lines.add(SECTION_SEPARATOR);

        lines.addAll(CIReportLines(results));

        return lines;
    }

    // Generate report lines describing the user's inputs
    private List<String> inputReportLines (OCSANAResults results) {
        List<String> lines = new ArrayList<> ();

        String headerLine = "User arguments";
        lines.add(headerLine);

        String sourceLine = "Source nodes: " + nodeSetString(results.sourceNodes, results);
        lines.add(sourceLine);

        String targetLine = "Target nodes: " + nodeSetString(results.targetNodes, results);
        lines.add(targetLine);

        String offTargetLine = "Off-target nodes: " + nodeSetString(results.offTargetNodes, results);
        lines.add(offTargetLine);

        return lines;
    }

    // Generate report lines describing the paths
    private List<String> pathReportLines(OCSANAResults results) {
        List<String> lines = new ArrayList<> ();

        String algLine = "Path-finding algorithm: " + results.pathFindingAlg.fullName();
        lines.add(algLine);

        lines.add("Paths to targets:");
        for (List<CyEdge> path: results.pathsToTargets) {
            lines.add(edgeSetString(path, results));
        }
        lines.add(MINOR_SEPARATOR);

        lines.add("Paths to off-targets:");
        for (List<CyEdge> path: results.pathsToOffTargets) {
            lines.add(edgeSetString(path, results));
        }

        return lines;
    }

    // Generate report lines describing the CIs
    private List<String> CIReportLines(OCSANAResults results) {
        List<String> lines = new ArrayList<> ();

        String algLine = "MHS algorithm: " + results.mhsAlg.fullName();
        lines.add(algLine);

        lines.add("Optimal CIs:");
        for (Collection<CyNode> MHS: results.MHSes) {
            lines.add(nodeSetString(MHS, results));
        }

        return lines;
    }

    // Helper functions for generating strings from structures
    private String nodeName(CyNetwork network,
                            CyNode node) {
        return network.getRow(node).get(CyNetwork.NAME, String.class);
    }

    private String nodeSetString(Collection<CyNode> nodes,
                                 OCSANAResults results) {
        if (nodes == null) {
            return new String();
        }

        List<String> strings = new ArrayList<>();
         for (CyNode node: nodes) {
            strings.add(nodeName(results.network, node));
         }

        return "[" + String.join(", ", strings) + "]";
    }

    private String edgeSetString(List<CyEdge> edges,
                                 OCSANAResults results) {
        if (edges == null) {
            return new String();
        }

        String result = new String("[");

        // Handle first node
        try {
            CyNode firstNode = edges.iterator().next().getSource();
            result += nodeName(results.network, firstNode);
        } catch (NoSuchElementException e) {
            return "[]";
        }

        // Each other node is a target
        for (CyEdge edge: edges) {
            // TODO: Handle activation and inhibition symbols
            result += " -> ";
            result += nodeName(results.network, edge.getTarget());
        }

        result += "]";
        return result;
    }

    private static final String MINOR_SEPARATOR = "\n";
    private static final String SECTION_SEPARATOR = "\n******\n";
}
