/**
 * Panel configuring path-finding algorithm in OCSANA CI stage
 *
 * Copyright Vera-Licona Research Group (C) 2016
 *
 * This software is licensed under the Artistic License 2.0, see the
 * LICENSE file or
 * http://www.opensource.org/licenses/artistic-license-2.0.php for
 * details
 **/

package org.compsysmed.ocsana.internal.ui.control.panels;

// Java imports
import java.util.*;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.BoxLayout;
import javax.swing.JComboBox;
import javax.swing.JLabel;
import javax.swing.JPanel;

// Cytoscape imports
import org.cytoscape.work.swing.PanelTaskManager;

// OCSANA imports
import org.compsysmed.ocsana.internal.algorithms.path.*;

import org.compsysmed.ocsana.internal.stages.generation.GenerationContextBuilder;

/**
 * Subpanel for user configuration of path-finding algorithm
 **/
public class PathFindingAlgorithmPanel
    extends AbstractControlSubPanel
    implements ActionListener {
    private GenerationContextBuilder generationContextBuilder;
    private PanelTaskManager taskManager;

    // UI elements
    private JPanel algSelectionPanel;
    private JComboBox<AbstractPathFindingAlgorithm> algorithmSelecter;

    private JPanel tunablePanel;

    /**
     * Constructor
     *
     * @param generationContextBuilder  the context for the CI stage
     * @param taskManager  a PanelTaskManager to provide @Tunable panels
     **/
    public PathFindingAlgorithmPanel (GenerationContextBuilder generationContextBuilder,
                                      PanelTaskManager taskManager) {
        // Initial setup
        this.generationContextBuilder = generationContextBuilder;
        this.taskManager = taskManager;

        setLayout(new BoxLayout(this, BoxLayout.Y_AXIS));

        add(makeHeader("Configure path-finding"));

        // Algorithm selecter
        algSelectionPanel = new JPanel();
        add(algSelectionPanel);

        algSelectionPanel.add(new JLabel("Algorithm:"));

        List<AbstractPathFindingAlgorithm> algorithms = new ArrayList<>();
        algorithms.add(new AllNonSelfIntersectingPathsAlgorithm(generationContextBuilder.getNetwork()));
        algorithms.add(new ShortestPathsAlgorithm(generationContextBuilder.getNetwork()));

        algorithmSelecter = new JComboBox<>(algorithms.toArray(new AbstractPathFindingAlgorithm[algorithms.size()]));
        algSelectionPanel.add(algorithmSelecter);
        algorithmSelecter.addActionListener(this);

        // Algorithm configuration panel
        tunablePanel = new JPanel();
        add(tunablePanel);

        updateTunablePanel();
    }

    private void updateTunablePanel () {
        tunablePanel.removeAll();

        tunablePanel.add(taskManager.getConfiguration(null, getAlgorithm()));

        tunablePanel.revalidate();
        tunablePanel.repaint();
    }

    private AbstractPathFindingAlgorithm getAlgorithm () {
        return (AbstractPathFindingAlgorithm) algorithmSelecter.getSelectedItem();
    }

    @Override
    public void actionPerformed (ActionEvent e) {
        updateTunablePanel();
    }

    @Override
    public void updateContextBuilder () {
        generationContextBuilder.setPathFindingAlgorithm(getAlgorithm());
    }
}
