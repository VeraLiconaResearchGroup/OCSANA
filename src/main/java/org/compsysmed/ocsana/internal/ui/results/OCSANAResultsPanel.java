/**
 * Panel to contain OCSANA results report
 *
 * Copyright Vera-Licona Research Group (C) 2015
 *
 * This software is licensed under the Artistic License 2.0, see the
 * LICENSE file or
 * http://www.opensource.org/licenses/artistic-license-2.0.php for
 * details
 **/

package org.compsysmed.ocsana.internal.ui.results;

// Java imports
import java.util.*;
import java.io.*;
import java.nio.charset.StandardCharsets;

import java.awt.Component;
import java.awt.BorderLayout;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.BoxLayout;
import javax.swing.Icon;
import javax.swing.JButton;
import javax.swing.JFileChooser;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTabbedPane;
import javax.swing.JTextArea;

// Cytoscape imports
import org.cytoscape.application.swing.CySwingApplication;
import org.cytoscape.application.swing.CytoPanel;
import org.cytoscape.application.swing.CytoPanelComponent;
import org.cytoscape.application.swing.CytoPanelName;
import org.cytoscape.application.swing.CytoPanelState;

// OCSANA imports
import org.compsysmed.ocsana.internal.tasks.results.OCSANAResults;

/**
 * Panel to display OCSANA results
 **/
public class OCSANAResultsPanel
    extends JPanel
    implements CytoPanelComponent {
    private CySwingApplication cySwingApplication;
    private CytoPanel cyResultsPanel;

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

        if (cyResultsPanel.getState() == CytoPanelState.HIDE) {
            cyResultsPanel.setState(CytoPanelState.DOCK);
        }
    }

    /**
     * Build the panel using the stored data
     **/
    private void buildPanel () {
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
    private JPanel getOperationsPanel () {
        JPanel buttonPanel = new JPanel();
        buttonPanel.setLayout(new BoxLayout(buttonPanel, BoxLayout.X_AXIS));

        JButton showReportButton = new JButton("Show report");
        buttonPanel.add(showReportButton);
        showReportButton.addActionListener(new ActionListener() {
                @Override
                public void actionPerformed (ActionEvent e) {
                    showResultsReport();
                }
            });

        JButton saveReportButton = new JButton("Save report");
        buttonPanel.add(saveReportButton);

        saveReportButton.addActionListener(new ActionListener() {
                @Override
                public void actionPerformed (ActionEvent event) {
                    JFileChooser fileChooser = new JFileChooser();
                    if (fileChooser.showSaveDialog(buttonPanel) == JFileChooser.APPROVE_OPTION) {
                        File outFile = fileChooser.getSelectedFile();
                        try (BufferedWriter fileWriter =
                             new BufferedWriter(new OutputStreamWriter(new FileOutputStream(outFile), StandardCharsets.UTF_8))) {
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
    private void showResultsReport () {
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
    private JPanel getResultsPanel () {
        JPanel resultsPanel = new JPanel(new BorderLayout());

        if (results == null) {
            return resultsPanel;
        }

        JTabbedPane resultsTabbedPane = new JTabbedPane();
        resultsPanel.add(resultsTabbedPane, BorderLayout.CENTER);
        resultsPanel.setBorder(null);

        if (results.MHSes != null) {
            CIPanel ciPanel = new CIPanel(results);
            resultsTabbedPane.addTab("Optimal CIs", ciPanel);
        }

        if (results.pathsToTargets != null) {
            PathsPanel targetPathsPanel = new PathsPanel(results, PathsPanel.PathType.TO_TARGETS);
            resultsTabbedPane.addTab("Paths to targets", targetPathsPanel);
        }

        if (results.pathsToOffTargets != null) {
            PathsPanel targetPathsPanel = new PathsPanel(results, PathsPanel.PathType.TO_OFF_TARGETS);
            resultsTabbedPane.addTab("Paths to Off-targets", targetPathsPanel);
        }

        return resultsPanel;
    }

    // Helper functions to get information about the panel
    /**
     * Get the results panel component
     */
    @Override
    public Component getComponent() {
        return this;
    }

    /**
     * Get the results panel name
     */
    @Override
    public CytoPanelName getCytoPanelName () {
        return CytoPanelName.EAST;
    }

    /**
     * Get the results panel title
     */
    @Override
    public String getTitle() {
        return "OCSANA";
    }

    /**
     * Get the results panel icon
     */
    @Override
    public Icon getIcon() {
        return null;
    }
}
