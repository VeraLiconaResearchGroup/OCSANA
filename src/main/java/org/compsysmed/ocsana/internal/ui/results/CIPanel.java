/**
 * Panel to contain OCSANA CIs report
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
import javax.swing.JTable;
import javax.swing.RowSorter;

import javax.swing.table.DefaultTableModel;
import javax.swing.table.TableModel;
import javax.swing.table.TableRowSorter;

// Cytoscape imports
import org.cytoscape.model.CyNode;
import org.cytoscape.model.CyEdge;

// OCSANA imports
import org.compsysmed.ocsana.internal.tasks.results.OCSANAResults;

public class CIPanel
    extends JPanel {
    private static final Vector<String> mhsCols =
        new Vector<>(Arrays.asList(new String[] {"CI", "Size", "Score"}));

    public CIPanel (OCSANAResults results) {
        if (results.MHSes != null) {
            Vector<Vector<Object>> mhsRows = getMHSRows(results);

            TableModel mhsModel = new DefaultTableModel(mhsRows, mhsCols) {
                    @Override
                    public Class<?> getColumnClass(int column) {
                        try {
                            return getValueAt(0, column).getClass();
                        } catch (ArrayIndexOutOfBoundsException exception) {
                            return Object.class;
                        }
                    }

                    // Disable cell editing
                    @Override
                    public boolean isCellEditable(int row, int column) {
                        return false;
                    }
                };

            JTable mhsTable = new JTable(mhsModel);

            // Sort the rows
            RowSorter<TableModel> mhsSorter = new TableRowSorter<TableModel>(mhsModel);

            if (results.ocsanaScores != null) {
                // If we have OCSANA scores, sort in decreasing order with respect to them
                mhsSorter.toggleSortOrder(2);
                mhsSorter.toggleSortOrder(2);
            } else {
                // Otherwise, sort in increasing order of CI size
                mhsSorter.toggleSortOrder(1);
            }

            mhsTable.setRowSorter(mhsSorter);

            mhsTable.setAutoResizeMode(JTable.AUTO_RESIZE_ALL_COLUMNS);

            JScrollPane mhsScrollPane = new JScrollPane(mhsTable);

            setLayout(new BorderLayout());
            String mhsText = "<html>" + "Found " + results.MHSes.size() + " optimal CIs in " + results.mhsExecutionSeconds + " s." + "<br />" + "Scored them in " + results.scoringExecutionSeconds + " s." + "</html>";
            add(new JLabel(mhsText), BorderLayout.PAGE_START);
            add(mhsScrollPane, BorderLayout.CENTER);
        }
    }

    /**
     * Get the rows of the CI results table
     **/
    private static Vector<Vector<Object>> getMHSRows (OCSANAResults results) {
        Vector<Vector<Object>> rows = new Vector<>();
        for (Collection<CyNode> MHS: results.MHSes) {
            Vector<Object> row = new Vector<>();
            row.add(results.nodeSetString(MHS));
            row.add(MHS.size());

            Double mhsScore = 0.0;
            if (results.ocsanaScores != null) {
                for (CyNode node: MHS) {
                    mhsScore += results.ocsanaScores.getOrDefault(node, 0.0);
                }
            }
            row.add(mhsScore);

            rows.add(row);
        }
        return rows;
    }
}
