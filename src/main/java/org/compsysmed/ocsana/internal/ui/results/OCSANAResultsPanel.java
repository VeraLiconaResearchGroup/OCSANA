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
import org.compsysmed.ocsana.internal.stages.generation.GenerationContext;
import org.compsysmed.ocsana.internal.stages.generation.GenerationResults;

import org.compsysmed.ocsana.internal.stages.prioritization.PrioritizationContext;

/**
 * Panel to display OCSANA results
 **/
public class OCSANAResultsPanel
    extends JPanel
    implements CytoPanelComponent {
    private final CySwingApplication cySwingApplication;
    private final CytoPanel cyResultsPanel;

    GenerationContext ciContext;
    GenerationResults ciResults;

    PrioritizationContext scoreContext;

    JPanel resultsPanel;
    JPanel operationsPanel;

    /**
     * Constructor
     * <p>
     * Produces a blank panel. To populate, use the updateResults methods.
     *
     * @param cySwingApplication  the CySwingApplication of this
     * Cytoscape instance
     **/
    public OCSANAResultsPanel (CySwingApplication cySwingApplication) {
        super();
        this.cySwingApplication = cySwingApplication;
        this.cyResultsPanel = cySwingApplication.getCytoPanel(getCytoPanelName());

        setLayout(new BorderLayout());
    }

    /**
     * Update the panel with the specified CI-stage results
     *
     * @param ciContext  the CI stage context
     * @param ciResults  the CI stage results to display
     **/
    public void updateResults (GenerationContext ciContext,
                               GenerationResults ciResults) {
        System.out.println("Boop!");
        this.ciContext = ciContext;
        this.ciResults = ciResults;

        this.scoreContext = null;

        rebuildPanels();
    }

    public void updateResults (PrioritizationContext scoreContext) {
        System.out.println("Beep!");
        this.scoreContext = scoreContext;

        rebuildPanels();
    }

    private void rebuildPanels () {
        resultsPanel = getResultsPanel();
        operationsPanel = getOperationsPanel();
        reset();
    }


    /**
     * Reset the panel to display the
     **/
    public void reset () {
        removeAll();

        add(resultsPanel, BorderLayout.CENTER);
        add(operationsPanel, BorderLayout.SOUTH);

        setSize(getMinimumSize());

        revalidate();
        repaint();

        if (cyResultsPanel.getState() == CytoPanelState.HIDE) {
            cyResultsPanel.setState(CytoPanelState.DOCK);
        }
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
    private void showResultsReport () {
        JTextArea reportTextArea = new JTextArea(40, 120);
        reportTextArea.setText(String.join("\n", ciResults.getReportLines()));
        reportTextArea.setEditable(false);
        reportTextArea.setCaretPosition(0); // Show top of file initially

        JScrollPane reportPane = new JScrollPane(reportTextArea);
        JOptionPane.showMessageDialog(this, reportPane, "OCSANA report", JOptionPane.PLAIN_MESSAGE);
    }

    /**
     * Build the results panel
     *
     * This is the panel that displays the results of whatever OCSANA
     * operations have completed
     **/
    private JPanel getResultsPanel () {
        JPanel resultsPanel = new JPanel(new BorderLayout());

        if (ciResults == null) {
            return resultsPanel;
        }

        JTabbedPane resultsTabbedPane = new JTabbedPane();
        resultsPanel.add(resultsTabbedPane, BorderLayout.CENTER);
        resultsPanel.setBorder(null);

        if (ciResults.CIs != null) {
            UnscoredCIListPanel ciPanel = new UnscoredCIListPanel(ciContext, ciResults, cySwingApplication.getJFrame());
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
