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

import javax.swing.AbstractListModel;
import javax.swing.BoxLayout;
import javax.swing.DefaultListModel;
import javax.swing.Icon;
import javax.swing.JButton;
import javax.swing.JFileChooser;
import javax.swing.JLabel;
import javax.swing.JList;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTabbedPane;
import javax.swing.JTable;
import javax.swing.JTextArea;
import javax.swing.RowSorter;
import javax.swing.border.TitledBorder;
import javax.swing.table.DefaultTableModel;
import javax.swing.table.TableModel;
import javax.swing.table.TableRowSorter;

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
 **/
public class OCSANAResultsPanel
    extends JPanel
    implements CytoPanelComponent {
    protected CySwingApplication cySwingApplication;
    protected CytoPanel cyResultsPanel;

    OCSANAResults results;

    public OCSANAResultsPanel (CySwingApplication cySwingApplication) {
        super();
        this.cySwingApplication = cySwingApplication;
        this.cyResultsPanel = cySwingApplication.getCytoPanel(getCytoPanelName());
    }

    /**
     * Update the panel with the specified results
     *
     * @param results  the results to display
     **/
    public void updateResults (OCSANAResults results) {
        this.results = results;

        removeAll();
        buildPanel();
        revalidate();
        repaint();
    }

    /**
     * Build the panel using the stored data
     **/
    protected void buildPanel () {
        setLayout(new BorderLayout());

        JPanel resultsPanel = getResultsPanel();
        this.add(resultsPanel, BorderLayout.CENTER);

        JPanel operationsPanel = getOperationsPanel();
        this.add(operationsPanel, BorderLayout.SOUTH);

        setSize(getMinimumSize());
    }

    /**
     * Build the operations panel
     *
     * This is the part of the results panel with buttons and other
     * user operations
     **/
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
                            for (String reportLine: results.getReportLines()) {
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

    /**
     * Show the results report in a dialog
     **/
    protected void showResultsReport () {
        JTextArea reportTextArea = new JTextArea(40, 120);
        reportTextArea.setText(String.join("\n", results.getReportLines()));
        reportTextArea.setEditable(false);
        reportTextArea.setCaretPosition(0); // Show top of file initially

        JScrollPane reportPane = new JScrollPane(reportTextArea);
        JOptionPane.showMessageDialog(this, reportPane, "OCSANA report", JOptionPane.PLAIN_MESSAGE);
    }

    /**
     * Build the results panel
     *
     * This is the panel that displays the results of the OCSANA
     * operations
     **/
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
            JPanel targetPathsPanel = buildPathsPanel(results.pathsToTargets, "targets", results.pathsToTargetsExecutionSeconds);
            resultsTabbedPane.addTab("Paths to targets", targetPathsPanel);
        }

        if (results.pathsToOffTargets != null) {
            JPanel targetPathsPanel = buildPathsPanel(results.pathsToOffTargets, "off-targets", results.pathsToOffTargetsExecutionSeconds);
            resultsTabbedPane.addTab("Paths to Off-targets", targetPathsPanel);
        }

        return resultsPanel;
    }


    /**
     * Build the CI results panel
     **/
    protected JPanel buildCIPanel () {
        if (results.MHSes != null) {
            Vector<Vector<Object>> mhsRows = getMHSRows();

            TableModel mhsModel = new DefaultTableModel(mhsRows, mhsCols) {
                    public Class getColumnClass(int column) {
                        Class returnValue;
                        if ((column >= 0) && (column < getColumnCount())) {
                            returnValue = getValueAt(0, column).getClass();
                        } else {
                            returnValue = Object.class;
                        }
                        return returnValue;
                    }
                };

            JTable mhsTable = new JTable(mhsModel);

            RowSorter<TableModel> mhsSorter = new TableRowSorter<TableModel>(mhsModel);
            mhsSorter.toggleSortOrder(2);
            mhsSorter.toggleSortOrder(2);
            mhsTable.setRowSorter(mhsSorter);

            mhsTable.setAutoResizeMode(JTable.AUTO_RESIZE_ALL_COLUMNS);

            JScrollPane mhsScrollPane = new JScrollPane(mhsTable);

            JPanel mhsPanel = new JPanel(new BorderLayout());
            String mhsText = "<html>" + "Found " + results.MHSes.size() + " optimal CIs in " + results.mhsExecutionSeconds + " s." + "<br />" + "Scored them in " + results.scoringExecutionSeconds + " s." + "</html>";
            mhsPanel.add(new JLabel(mhsText), BorderLayout.PAGE_START);
            mhsPanel.add(mhsScrollPane, BorderLayout.CENTER);

            return mhsPanel;
        } else {
            return null;
        }
    }

    // Column names for the CI results table
    protected static final Vector<String> mhsCols =
        new Vector<>(Arrays.asList(new String[] {"CI", "Size", "Score"}));

    /**
     * Get the rows of the CI results table
     **/
    protected Vector<Vector<Object>> getMHSRows () {
        Vector<Vector<Object>> rows = new Vector<>();
        for (Collection<CyNode> MHS: results.MHSes) {
            Vector<Object> row = new Vector<>();
            row.add(results.nodeSetString(MHS));
            row.add(MHS.size());

            Double mhsScore = 0.0;
            for (CyNode node: MHS) {
                mhsScore += results.ocsanaScores.getOrDefault(node, 0.0);
            }
            row.add(mhsScore);

            rows.add(row);
        }
        return rows;
    }

    /**
     * Build a panel displaying the given paths
     *
     * @param paths  the paths to display
     * @param pathType  a string to follow "Found n paths to "
     * @param runTime  the running time of the path-finding process in seconds
     **/
    protected JPanel buildPathsPanel (Collection<? extends List<CyEdge>> paths,
                                      String pathType,
                                      Double runTime) {
        if (paths != null) {
            Vector<Vector<String>> pathRows = new Vector<>();
            for (List<CyEdge> path: paths) {
                Vector<String> pathRow = new Vector<String>();
                pathRow.add(results.pathString(path));
                pathRows.add(pathRow);
            }

            Vector<String> pathCols = new Vector<>();
            pathCols.add("Path");

            JTable pathTable = new JTable(pathRows, pathCols);
            pathTable.setAutoCreateRowSorter(true);
            pathTable.setAutoResizeMode(JTable.AUTO_RESIZE_ALL_COLUMNS);

            // Sort alphabetically
            pathTable.getRowSorter().toggleSortOrder(0);

            JScrollPane pathScrollPane = new JScrollPane(pathTable);

            JPanel pathPanel = new JPanel(new BorderLayout());
            String panelText = "Found " + paths.size() + " paths to " + pathType + " in " + runTime + " s.";
            pathPanel.add(new JLabel(panelText), BorderLayout.PAGE_START);
            pathPanel.add(pathScrollPane, BorderLayout.CENTER);

            return pathPanel;
        } else {
            return null;
        }
    }

    // Helper functions to get information about the panel
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
