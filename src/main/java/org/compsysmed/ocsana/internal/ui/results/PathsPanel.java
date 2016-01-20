/**
 * Panel to contain OCSANA paths report
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
import java.awt.BorderLayout;

import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTextArea;

// Cytoscape imports
import org.cytoscape.model.CyNode;
import org.cytoscape.model.CyEdge;

// OCSANA imports
import org.compsysmed.ocsana.internal.tasks.results.OCSANAResults;

public class PathsPanel
    extends JPanel {
    private static final Vector<String> mhsCols =
        new Vector<>(Arrays.asList(new String[] {"CI", "Size", "Score"}));

    public PathsPanel (OCSANAResults results,
                       PathType pathType) {
        Collection<List<CyEdge>> paths;
        Double pathFindingTime;

        switch (pathType) {
        case TO_TARGETS:
            paths = results.pathsToTargets;
            pathFindingTime = results.pathsToTargetsExecutionSeconds;
            break;

        case TO_OFF_TARGETS:
            paths = results.pathsToOffTargets;
            pathFindingTime = results.pathsToOffTargetsExecutionSeconds;
            break;

        default:
            throw new IllegalStateException("Undefined path type");
        }

        if (paths != null) {
            List<String> pathLines = new ArrayList<>();
            for (List<CyEdge> path: paths) {
                pathLines.add(results.pathString(path));
            }

            // Sort alphabetically
            Collections.sort(pathLines);

            // Create panel
            JTextArea pathTextArea = new JTextArea(String.join("\n", pathLines));

            JScrollPane pathScrollPane = new JScrollPane(pathTextArea);

            setLayout(new BorderLayout());
            String panelText = String.format("Found %d paths to %s in %fs.", paths.size(), pathType, pathFindingTime);
            add(new JLabel(panelText), BorderLayout.PAGE_START);
            add(pathScrollPane, BorderLayout.CENTER);
        }
    }

    public enum PathType {
        TO_TARGETS("targets"),
        TO_OFF_TARGETS("off-targets");

        private final String pluralName;

        private PathType(String pluralName) {
            this.pluralName = pluralName;
        }

        public String toString() {
            return pluralName;
        }
    }
}
