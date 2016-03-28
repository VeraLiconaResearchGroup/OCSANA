/**
 * Panel to contain OCSANA controls for CI stage
 *
 * Copyright Vera-Licona Research Group (C) 2015
 *
 * This software is licensed under the Artistic License 2.0, see the
 * LICENSE file or
 * http://www.opensource.org/licenses/artistic-license-2.0.php for
 * details
 **/

package org.compsysmed.ocsana.internal.ui.control;

// Java imports
import java.util.*;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.BoxLayout;
import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JCheckBox;
import javax.swing.JLabel;
import javax.swing.JPanel;

// Cytoscape imports
import org.cytoscape.model.CyNetwork;

import org.cytoscape.work.FinishStatus;
import org.cytoscape.work.ObservableTask;
import org.cytoscape.work.TaskManager;
import org.cytoscape.work.TaskObserver;

// OCSANA imports
import org.compsysmed.ocsana.internal.algorithms.mhs.*;

import org.compsysmed.ocsana.internal.stages.cistage.CIStageContext;
import org.compsysmed.ocsana.internal.stages.cistage.CIStageResults;
import org.compsysmed.ocsana.internal.stages.cistage.CIStageRunnerTask;
import org.compsysmed.ocsana.internal.stages.cistage.CIStageRunnerTaskFactory;

import org.compsysmed.ocsana.internal.ui.control.panels.*;
import org.compsysmed.ocsana.internal.ui.results.OCSANAResultsPanel;

/**
 * Panel to configure and run OCSANA CI stage
 **/
public class CIStageControlPanel
    extends JPanel
    implements ActionListener, TaskObserver {
    public static final String START_CI_SIGNAL = "CI task start";
    public static final String END_CI_SIGNAL = "CI task end";

    private OCSANAResultsPanel resultsPanel;
    private TaskManager taskManager;

    private CIStageContext ciStageContext;
    private CIStageResults ciStageResults;
    private CyNetwork network;

    private List<ActionListener> listeners;

    // UI elements
    private Collection<AbstractOCSANASubPanel> subpanels;

    private CINodeSetsPanel ciNodeSetsPanel;
    private JComboBox mhsAlgorithm;
    private JCheckBox includeEndpointsInCIs;

    /**
     * Constructor.
     *
     * @param network  the network
     * @param resultsPanel   panel to update with results
     * @param taskManager  a TaskManager to handle the CI calculation tasks
     **/
    public CIStageControlPanel (CyNetwork network,
                                OCSANAResultsPanel resultsPanel,
                                TaskManager taskManager) {
        this.network = network;
        this.resultsPanel = resultsPanel;
        this.taskManager = taskManager;

        setLayout(new BoxLayout(this, BoxLayout.PAGE_AXIS));

        ciStageContext = new CIStageContext(network);

        subpanels = new ArrayList<>();

        listeners = new ArrayList<>();

        buildPanel();
    }

    /**
     * Retrieve the underlying CIStageContext with the latest changes
     * from the UI
     *
     * @return the context
     **/
    public CIStageContext getContext () {
        updateContext();
        return ciStageContext;
    }

    /**
     * Retrieve the results of the CI stage if available
     *
     * @return the results (null if stage has not been run)
     **/
    public CIStageResults getResults () {
        return ciStageResults;
    }

    /**
     * (Re)construct the panel from its components
     **/
    private void buildPanel () {
        removeAll();

        JPanel tunablePanel = getContextPanel();
        add(tunablePanel);

        JButton runCIStageButton = new JButton("Run CI stage computations");
        add(runCIStageButton);

        runCIStageButton.addActionListener(new ActionListener() {
                @Override
                public void actionPerformed (ActionEvent e) {
                    runCITask();
                }
            });

        revalidate();
        repaint();
    }

    /**
     * Build a panel with the UI elements for a CIStageContext
     **/
    private JPanel getContextPanel () {
        JPanel panel = new JPanel();
        panel.setLayout(new BoxLayout(panel, BoxLayout.PAGE_AXIS));

        ciNodeSetsPanel = new CINodeSetsPanel(ciStageContext);
        panel.add(ciNodeSetsPanel);
        subpanels.add(ciNodeSetsPanel);

        panel.add(new JLabel("MHS algorithm"));

        List<AbstractMHSAlgorithm> mhsAlgorithms = new ArrayList<>();
        mhsAlgorithms.add(new MMCSAlgorithm());
        mhsAlgorithms.add(new RSAlgorithm());
        mhsAlgorithms.add(new BergeAlgorithm());

        mhsAlgorithm = new JComboBox(mhsAlgorithms.toArray());
        panel.add(mhsAlgorithm);

        includeEndpointsInCIs = new JCheckBox("Allow sources and targets in CIs", ciStageContext.includeEndpointsInCIs);
        panel.add(includeEndpointsInCIs);

        return panel;
    }

    /**
     * Update the CIStageContext members with the settings in the UI
     **/
    private void updateContext () {
        for (AbstractOCSANASubPanel subpanel: subpanels) {
            subpanel.updateContext();
        }

        ciStageContext.mhsAlg = (AbstractMHSAlgorithm) mhsAlgorithm.getSelectedItem();
        ciStageContext.includeEndpointsInCIs = includeEndpointsInCIs.isSelected();
    }

    /**
     * Spawn the CI stage task
     **/
    private void runCITask () {
        updateContext();

        signalStartOfCITask();

        System.out.println(String.format("Beep! include: %s", ciStageContext.includeEndpointsInCIs));
        System.out.println(String.format("Selected %d source nodes, %d target nodes, and %d off-target nodes", ciStageContext.sourceNodes.size(), ciStageContext.targetNodes.size(), ciStageContext.offTargetNodes.size()));

        CIStageRunnerTaskFactory runnerTaskFactory
            = new CIStageRunnerTaskFactory(taskManager, this, ciStageContext, resultsPanel);
        //taskManager.execute(runnerTaskFactory.createTaskIterator(), this);
    }

    private void signalStartOfCITask () {
        sendSignal(START_CI_SIGNAL);
    }

    private void signalEndOfCITask () {
        sendSignal(END_CI_SIGNAL);
    }

    private void sendSignal (String signal) {
        ActionEvent signalEvent = new ActionEvent(this, ActionEvent.ACTION_PERFORMED, signal);
            for (ActionListener listener: listeners) {
                listener.actionPerformed(signalEvent);
            }
    }

    @Override
    public void taskFinished (ObservableTask task) {
        ciStageResults = task.getResults(CIStageResults.class);
        signalEndOfCITask();
    }

    @Override
    public void allFinished(FinishStatus finishStatus) {
        // Called after the TaskManager finished up a TaskIterator.
        // Currently, we don't do anything with this information.
    }

    public void addActionListener (ActionListener listener) {
        listeners.add(listener);
    }

    public void removeActionListener (ActionListener listener) {
        listeners.remove(listener);
    }

    // Helper functions to support listening for component changes
    public void actionPerformed (ActionEvent event) {
        // Synchronize any changes made to the @Tunable UI widgets
        updateContext();

        buildPanel();
    }
}
