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
import java.io.*;

import java.awt.Component;
import java.awt.BorderLayout;
import java.awt.Dimension;
import java.awt.Toolkit;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.BoxLayout;
import javax.swing.Icon;
import javax.swing.JButton;
import javax.swing.JFileChooser;
import javax.swing.JLabel;
import javax.swing.JList;
import javax.swing.JPanel;
import javax.swing.JTabbedPane;
import javax.swing.JTable;
import javax.swing.JTextArea;
import javax.swing.JScrollPane;
import javax.swing.JOptionPane;
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

    OCSANAResults results;
    List<String> reportLines;

    public OCSANAResultsPanel (CySwingApplication cySwingApplication) {
        super();
        this.cySwingApplication = cySwingApplication;
        this.cyResultsPanel = cySwingApplication.getCytoPanel(getCytoPanelName());
    }

    public void updateResults (OCSANAResults results) {
        this.results = results;
        reportLines = results.getReportLines();

        removeAll();
        buildPanel();
        revalidate();
        repaint();
    }

    protected void buildPanel () {
        setLayout(new BorderLayout());

        JPanel resultsPanel = getResultsPanel();
        this.add(resultsPanel, BorderLayout.CENTER);

        JPanel operationsPanel = getOperationsPanel();
        this.add(operationsPanel, BorderLayout.SOUTH);

        setSize(getMinimumSize());
    }

    protected JPanel getOperationsPanel () {
        JPanel buttonPanel = new JPanel();
        buttonPanel.setLayout(new BoxLayout(buttonPanel, BoxLayout.X_AXIS));

        JButton showReportButton = new JButton("Show report");
        buttonPanel.add(showReportButton);
        showReportButton.addActionListener(new ActionListener() {
                public void actionPerformed (ActionEvent e) {
                    showResultsReport();
                }
            });

        JButton saveReportButton = new JButton("Save report");
        buttonPanel.add(saveReportButton);

        saveReportButton.addActionListener(new ActionListener() {
                public void actionPerformed (ActionEvent event) {
                    JFileChooser fileChooser = new JFileChooser();
                    if (fileChooser.showSaveDialog(buttonPanel) == JFileChooser.APPROVE_OPTION) {
                        File outFile = fileChooser.getSelectedFile();
                        try (BufferedWriter fileWriter =
                             new BufferedWriter(new FileWriter(outFile))) {
                            for (String reportLine: reportLines) {
                                fileWriter.write(reportLine);
                                fileWriter.newLine();
                            }
                        } catch (IOException exception) {
                            String message = "Could not save to " + outFile.toString() + "\n" + exception;
                            JOptionPane.showMessageDialog(buttonPanel,
                                                          message,
                                                          "Error",
                                                          JOptionPane.ERROR_MESSAGE);
                        }
                    }
                }
            });


        getRootPane().setDefaultButton(showReportButton);

        return buttonPanel;
    }

    protected void showResultsReport () {
        JTextArea reportTextArea = new JTextArea();
        reportTextArea.setText(String.join("\n", reportLines));
        reportTextArea.setEditable(false);

        JScrollPane reportPane = new JScrollPane(reportTextArea);
        JOptionPane.showMessageDialog(this, reportPane, "OCSANA report", JOptionPane.PLAIN_MESSAGE);
    }

    protected JPanel getResultsPanel () {
        JPanel resultsPanel = new JPanel(new BorderLayout());

        if (results == null) {
            return resultsPanel;
        }

        JTabbedPane resultsTabbedPane = new JTabbedPane();
        resultsPanel.add(resultsTabbedPane, BorderLayout.CENTER);
        resultsPanel.setBorder(new TitledBorder("Results"));

        if (results.MHSes != null) {
            JPanel ciPanel = buildCIPanel();
            resultsTabbedPane.addTab("Optimal CIs", ciPanel);
        }

        if (results.pathsToTargets != null) {
            JPanel targetPathsPanel = buildPathsPanel(results.pathsToTargets, "targets");
            resultsTabbedPane.addTab("Paths to targets", targetPathsPanel);
        }

        if (results.pathsToOffTargets != null) {
            JPanel targetPathsPanel = buildPathsPanel(results.pathsToOffTargets, "off-targets");
            resultsTabbedPane.addTab("Paths to Off-targets", targetPathsPanel);
        }

        return resultsPanel;
    }

    protected JPanel buildCIPanel () {
        if (results.MHSes != null) {
            Vector<Vector<Object>> mhsRows = getMHSRows();

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

    protected Vector<Vector<Object>> getMHSRows () {
        Vector<Vector<Object>> rows = new Vector<>();
        for (Collection<CyNode> MHS: results.MHSes) {
            Vector<Object> row = new Vector<>();
            row.add(results.nodeSetString(MHS));
            row.add(MHS.size());
            row.add(results.ocsanaAlg.getScore(MHS));

            rows.add(row);
        }
        return rows;
    }

    protected JPanel buildPathsPanel (Collection<? extends List<CyEdge>> paths,
                                      String pathType) {
        if (paths != null) {
            DefaultListModel<String> pathStrings = new DefaultListModel<>();
            for (List<CyEdge> path: paths) {
                pathStrings.addElement(results.pathString(path));
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
}
