/**
 * Context for the CI stage of OCSANA
 *
 * Copyright Vera-Licona Research Group (C) 2016
 *
 * This software is licensed under the Artistic License 2.0, see the
 * LICENSE file or
 * http://www.opensource.org/licenses/artistic-license-2.0.php for
 * details
 **/

package org.compsysmed.ocsana.internal.stages.cistage;

// Java imports
import java.util.*;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

// Cytoscape imports
import org.cytoscape.model.CyNetwork;

import org.cytoscape.work.ContainsTunables;
import org.cytoscape.work.Tunable;

import org.cytoscape.work.util.ListSelection;
import org.cytoscape.work.util.ListSingleSelection;
import org.cytoscape.work.util.ListChangeListener;

// OCSANA imports
import org.compsysmed.ocsana.internal.tasks.nodeselection.NodeSetSelecter;
import org.compsysmed.ocsana.internal.tasks.edgeprocessing.EdgeProcessor;

import org.compsysmed.ocsana.internal.algorithms.path.AbstractPathFindingAlgorithm;
import org.compsysmed.ocsana.internal.algorithms.path.AllNonSelfIntersectingPathsAlgorithm;
import org.compsysmed.ocsana.internal.algorithms.path.ShortestPathsAlgorithm;

import org.compsysmed.ocsana.internal.algorithms.mhs.AbstractMHSAlgorithm;
import org.compsysmed.ocsana.internal.algorithms.mhs.BergeAlgorithm;
import org.compsysmed.ocsana.internal.algorithms.mhs.MMCSAlgorithm;
import org.compsysmed.ocsana.internal.algorithms.mhs.RSAlgorithm;

import org.compsysmed.ocsana.internal.algorithms.scoring.OCSANAScoringAlgorithm;

/**
 * Context for the CI stage of OCSANA
 *
 * This class stores the configuration required to run the CI stage. A
 * populated instance will be passed to a CIStageRunner at the
 * beginning of a run.
 **/
public class CIStageContext
    implements ListChangeListener {

    // User options as Tunables
    // Node and edge selection
    @ContainsTunables
    public NodeSetSelecter nodeSetSelecter;

    @ContainsTunables
    public EdgeProcessor edgeProcessor;

    // Path search options
    @Tunable(description = "Path-finding algorithm",
             gravity = 210,
             groups = {"Find pathways"})
    public ListSingleSelection<AbstractPathFindingAlgorithm> pathFindingAlgSelecter;

    @ContainsTunables
    public AbstractPathFindingAlgorithm pathFindingAlg;

    // MHS search options
    @Tunable(description = "MHS algorithm",
             gravity = 310,
             tooltip = "MMCS is usually the fastest. Berge's algorithm is very slow and is included here only for comparison purposes.",
             groups = {"Find minimal CIs"})
    public ListSingleSelection<AbstractMHSAlgorithm> mhsAlgSelecter;

    @Tunable(description = "Allow sources and targets in CIs",
             gravity = 311,
             tooltip = "If true, CIs will be allowed to contain source and target nodes.",
             groups = {"Find minimal CIs"})
    public Boolean includeEndpointsInCIs = false;

    @ContainsTunables
    public AbstractMHSAlgorithm mhsAlg;

    // Scoring options
    @ContainsTunables
    public OCSANAScoringAlgorithm ocsanaAlg;

    // Internal data
    private CyNetwork network;
    private List<ActionListener> listeners = new ArrayList<>();

    public CIStageContext (CyNetwork network) {
        this.network = network;

        if (network == null) {
            return;
        }

        // Node and edge selection
        nodeSetSelecter = new NodeSetSelecter(network);
        edgeProcessor = new EdgeProcessor(network);

        // Path search options
        List<AbstractPathFindingAlgorithm> pathFindingAlgs = new ArrayList<>();
        pathFindingAlgs.add(new AllNonSelfIntersectingPathsAlgorithm(network));
        pathFindingAlgs.add(new ShortestPathsAlgorithm(network));

        pathFindingAlgSelecter = new ListSingleSelection<>(pathFindingAlgs);
        pathFindingAlgSelecter.addListener(this);

        pathFindingAlg = pathFindingAlgSelecter.getSelectedValue();

        // MHS search options
        List<AbstractMHSAlgorithm> mhsAlgs = new ArrayList<>();
        mhsAlgs.add(new MMCSAlgorithm());
        mhsAlgs.add(new RSAlgorithm());
        mhsAlgs.add(new BergeAlgorithm());

        mhsAlgSelecter = new ListSingleSelection<>(mhsAlgs);
        mhsAlgSelecter.addListener(this);

        mhsAlg = mhsAlgSelecter.getSelectedValue();

        // OCSANA scoring options
        ocsanaAlg = new OCSANAScoringAlgorithm(network);
    }

    // Bits and pieces to support listeners
    public void addActionListener (ActionListener listener) {
        listeners.add(listener);
    }

    public void removeActionListener (ActionListener listener) {
        listeners.remove(listener);
    }

    private void notifyListenersToRepaint () {
        ActionEvent repaintEvent = new ActionEvent(this, ActionEvent.ACTION_PERFORMED, "CI repaint");
        for (ActionListener listener: listeners) {
            listener.actionPerformed(repaintEvent);
        }
    }

    @Override
    public void listChanged (ListSelection listSelecter) {
        throw new IllegalStateException("The list selecters should not change.");
    }

    @Override
    public void selectionChanged (ListSelection listSelecter) {
        if (listSelecter.equals(mhsAlgSelecter)) {
            if (!mhsAlg.equals(mhsAlgSelecter.getSelectedValue())) {
                mhsAlg = mhsAlgSelecter.getSelectedValue();
                notifyListenersToRepaint();
            }
        } else if (listSelecter.equals(pathFindingAlgSelecter)) {
            if (!pathFindingAlg.equals(pathFindingAlgSelecter.getSelectedValue())) {
                pathFindingAlg = pathFindingAlgSelecter.getSelectedValue();
                notifyListenersToRepaint();
            }
        } else {
            throw new IllegalStateException("Unknown list selecter");
        }
    }
}
