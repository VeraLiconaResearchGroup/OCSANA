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
import org.compsysmed.ocsana.internal.stages.cistage.CIStageContext;
import org.compsysmed.ocsana.internal.stages.cistage.CIStageResults;

/**
 * Panel to display OCSANA results
 **/
public class OCSANAResultsPanel
    extends JPanel
    implements CytoPanelComponent {
    private CySwingApplication cySwingApplication;
    private CytoPanel cyResultsPanel;

    CIStageContext ciContext;
    CIStageResults ciResults;

    public OCSANAResultsPanel (CySwingApplication cySwingApplication) {
        super();
        this.cySwingApplication = cySwingApplication;
        this.cyResultsPanel = cySwingApplication.getCytoPanel(getCytoPanelName());
    }

    /**
     * Update the panel with the specified CI-stage results
     *
     * @param results  the results to display
     **/
    public void updateResults (CIStageContext context,
                               CIStageResults results) {
        this.ciContext = context;
        this.ciResults = results;;

        removeAll();
        buildCIStagePanel();
        revalidate();
        repaint();

        if (cyResultsPanel.getState() == CytoPanelState.HIDE) {
            cyResultsPanel.setState(CytoPanelState.DOCK);
        }
    }

    /**
     * Build the panel for CI-stage results using the stored data
     **/
    private void buildCIStagePanel () {
        setLayout(new BorderLayout());

        JPanel resultsPanel = getCIResultsPanel();
        this.add(resultsPanel, BorderLayout.CENTER);

        JPanel operationsPanel = getCIOperationsPanel();
        this.add(operationsPanel, BorderLayout.SOUTH);

        setSize(getMinimumSize());
    }

    /**
     * Build the operations panel for CI-stage results
     *
     * This is the part of the results panel with buttons and other
     * user operations
     **/
    private JPanel getCIOperationsPanel () {
        JPanel buttonPanel = new JPanel();
        buttonPanel.setLayout(new BoxLayout(buttonPanel, BoxLayout.X_AXIS));

        JButton showReportButton = new JButton("Show report");
        buttonPanel.add(showReportButton);
        showReportButton.addActionListener(new ActionListener() {
                @Override
                public void actionPerformed (ActionEvent e) {
                    showCIResultsReport();
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
                            for (String reportLine: ciResults.getReportLines()) {
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
     * Show the CI results report in a dialog
     **/
    private void showCIResultsReport () {
        JTextArea reportTextArea = new JTextArea(40, 120);
        reportTextArea.setText(String.join("\n", ciResults.getReportLines()));
        reportTextArea.setEditable(false);
        reportTextArea.setCaretPosition(0); // Show top of file initially

        JScrollPane reportPane = new JScrollPane(reportTextArea);
        JOptionPane.showMessageDialog(this, reportPane, "OCSANA report", JOptionPane.PLAIN_MESSAGE);
    }

    /**
     * Build the CI results panel
     *
     * This is the panel that displays the results of the CI stage of
     * OCSANA operations
     **/
    private JPanel getCIResultsPanel () {
        JPanel resultsPanel = new JPanel(new BorderLayout());

        if (ciResults == null) {
            return resultsPanel;
        }

        JTabbedPane resultsTabbedPane = new JTabbedPane();
        resultsPanel.add(resultsTabbedPane, BorderLayout.CENTER);
        resultsPanel.setBorder(null);

        if (ciResults.CIs != null) {
            CIPanel ciPanel = new CIPanel(ciContext, ciResults);
            resultsTabbedPane.addTab("Optimal CIs", ciPanel);
        }

        if (ciResults.pathsToTargets != null) {
            PathsPanel targetPathsPanel = new PathsPanel(ciContext, ciResults, PathsPanel.PathType.TO_TARGETS);
            resultsTabbedPane.addTab("Paths to targets", targetPathsPanel);
        }

        if (ciResults.pathsToOffTargets != null) {
            PathsPanel targetPathsPanel = new PathsPanel(ciContext, ciResults, PathsPanel.PathType.TO_OFF_TARGETS);
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
