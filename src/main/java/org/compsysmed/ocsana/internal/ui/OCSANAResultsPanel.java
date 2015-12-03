/**
 * Panel to contain OCSANA results report
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
import java.awt.Dimension;
import java.awt.Toolkit;

import javax.swing.Icon;
import javax.swing.JLabel;
import javax.swing.JList;
import javax.swing.JPanel;
import javax.swing.JTabbedPane;
import javax.swing.JTable;
import javax.swing.JScrollPane;
import javax.swing.border.TitledBorder;
import javax.swing.AbstractListModel;
import javax.swing.DefaultListModel;

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

/**
 * Panel to display OCSANA results
 *
 **/
public class OCSANAResultsPanel
    extends JPanel
    implements CytoPanelComponent {
    protected CySwingApplication cySwingApplication;
    protected CytoPanel cyResultsPanel;

    public OCSANAResultsPanel (CySwingApplication cySwingApplication) {
        super();
        this.cySwingApplication = cySwingApplication;
        this.cyResultsPanel = cySwingApplication.getCytoPanel(getCytoPanelName());
    }

    protected void buildPanel (OCSANAResults results) {
        setLayout(new BorderLayout());

        JPanel resultsPanel = getResultsPanel(results);
        if (resultsPanel != null) {
            this.add(resultsPanel, BorderLayout.CENTER);
        }

        setSize(getMinimumSize());
    }

    protected JPanel getResultsPanel (OCSANAResults results) {
        JPanel resultsPanel = new JPanel(new BorderLayout());

        JTabbedPane resultsTabbedPane = new JTabbedPane();
        resultsPanel.add(resultsTabbedPane, BorderLayout.CENTER);
        resultsPanel.setBorder(new TitledBorder("Results"));

        if (results.MHSes != null) {
            JPanel ciPanel = buildCIPanel(results);
            resultsTabbedPane.addTab("Optimal CIs", ciPanel);
        }

        if (results.pathsToTargets != null) {
            JPanel targetPathsPanel = buildPathsToTargetsPanel(results);
            resultsTabbedPane.addTab("Paths to targets", targetPathsPanel);
        }

        if (results.pathsToOffTargets != null) {
            JPanel targetPathsPanel = buildPathsToOffTargetsPanel(results);
            resultsTabbedPane.addTab("Paths to Off-targets", targetPathsPanel);
        }

        return resultsPanel;
    }

    protected JPanel buildCIPanel (OCSANAResults results) {
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

            return mhsPanel;
        } else {
            return null;
        }
    }

    protected static final Vector<String> mhsCols =
        new Vector<>(Arrays.asList(new String[] {"CI", "Size", "Score"}));

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

    protected JPanel buildPathsToTargetsPanel (OCSANAResults results) {
        return buildPathsPanel(results.pathsToTargets, "targets", results);
    }

    protected JPanel buildPathsToOffTargetsPanel (OCSANAResults results) {
        return buildPathsPanel(results.pathsToOffTargets, "off-targets", results);
    }

    protected JPanel buildPathsPanel (Collection<? extends List<CyEdge>> paths,
                                      String pathType,
                                      OCSANAResults results) {
        if (paths != null) {
            DefaultListModel<String> pathStrings = new DefaultListModel<>();
            for (List<CyEdge> path: paths) {
                pathStrings.addElement(edgeSetString(path, results));
            }

            JList pathList = new JList(pathStrings);
            JScrollPane pathScrollPane = new JScrollPane(pathList);

            JPanel pathPanel = new JPanel(new BorderLayout());
            String panelText = "Found " + paths.size() + " paths to " + pathType;
            pathPanel.add(new JLabel(panelText), BorderLayout.PAGE_START);
            pathPanel.add(pathScrollPane, BorderLayout.CENTER);

            return pathPanel;
        } else {
            return null;
        }
    }


    // Helper functions to get names and strings for various substructures
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
        return "OCSANA";
    }

    /**
     * Get the results panel icon
     */
    public Icon getIcon() {
        return null;
    }

    public void updateResults (OCSANAResults results) {
        removeAll();
        buildPanel(results);
        revalidate();
        repaint();
    }
}
