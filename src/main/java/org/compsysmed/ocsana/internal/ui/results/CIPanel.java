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
import java.awt.Point;

import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;

import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.RowSorter;

import javax.swing.table.AbstractTableModel;
import javax.swing.table.TableModel;
import javax.swing.table.TableRowSorter;

// Cytoscape imports
import org.cytoscape.model.CyNode;

// OCSANA imports
import org.compsysmed.ocsana.internal.stages.cistage.CIStageContext;
import org.compsysmed.ocsana.internal.stages.cistage.CIStageResults;

import org.compsysmed.ocsana.internal.util.results.CombinationOfInterventions;

public class CIPanel
    extends JPanel {
    private JFrame cytoscapeFrame;
    private CIStageContext ciContext;
    private CIStageResults ciResults;

    public CIPanel (CIStageContext ciContext,
                    CIStageResults ciResults,
                    JFrame cytoscapeFrame) {
        this.ciContext = ciContext;
        this.ciResults = ciResults;
        this.cytoscapeFrame = cytoscapeFrame;

        if (ciResults.CIs != null) {
            MHSTable mhsTable = new MHSTable();

            JScrollPane mhsScrollPane = new JScrollPane(mhsTable);

            setLayout(new BorderLayout());
            String mhsText = String.format("<html>Found %d optimal CIs in %f s.<br />Double-click a CI for a detailed intervention report.</html>", ciResults.CIs.size(), ciResults.mhsExecutionSeconds);
            add(new JLabel(mhsText), BorderLayout.PAGE_START);
            add(mhsScrollPane, BorderLayout.CENTER);
        }
    }

    private class MHSTable extends JTable {
        List<CombinationOfInterventions> CIs;

        public MHSTable () {
            this.CIs = new ArrayList<>(ciResults.CIs);

            MHSTableModel mhsModel = new MHSTableModel(ciContext, CIs);
            setModel(mhsModel);

            // Sort the rows
            RowSorter<TableModel> mhsSorter = new TableRowSorter<TableModel>(mhsModel);

            // If the CIs have intervention data, sort on that
            if (ciResults.CIs.stream().findFirst().get().maximumNumberOfCorrectEffects() != null) {
                mhsSorter.toggleSortOrder(2);
                mhsSorter.toggleSortOrder(2);
            } else {
                // Otherwise, sort on CI size
                mhsSorter.toggleSortOrder(1);
            }

            setRowSorter(mhsSorter);

            MouseListener mouseListener = new MouseAdapter() {
                    public void mousePressed(MouseEvent me) {
                        Point p = me.getPoint();
                        int row = rowAtPoint(p);
                        if (me.getClickCount() == 2 && row != -1) {
                            handleUserDoubleClick(row);
                        }
                    }};

            addMouseListener(mouseListener);

            setAutoResizeMode(JTable.AUTO_RESIZE_ALL_COLUMNS);
        }

        public void handleUserDoubleClick (Integer row) {
            CombinationOfInterventions ci = CIs.get(row);
            InterventionDetailsDialog detailsDialog = new InterventionDetailsDialog(cytoscapeFrame, ciContext.getNetwork(), CIs.get(row));
        }
    }

    private static class MHSTableModel extends AbstractTableModel {
        private CIStageContext ciContext;
        private List<CombinationOfInterventions> CIs;

        public MHSTableModel (CIStageContext ciContext,
                              List<CombinationOfInterventions> CIs) {
            this.ciContext = ciContext;
            this.CIs = CIs;
        }
        String[] colNames = {"CI", "Size", "Successful targets"};

        @Override
        public String getColumnName (int col) {
            return colNames[col];
        }

        @Override
        public int getRowCount () {
            return CIs.size();
        }

        @Override
        public int getColumnCount () {
            return 3;
        }

        @Override
        public Object getValueAt (int row, int col) {
            CombinationOfInterventions ci = CIs.get(row);
            switch (col) {
            case 0:
                return ci.interventionNodesString();

            case 1:
                return ci.size();

            case 2:
                return ci.maximumNumberOfCorrectEffects();

            default:
                throw new IllegalArgumentException(String.format("Table does not have %d columns", col));
            }
        }

        @Override
        public Class<?> getColumnClass(int column) {
            try {
                return getValueAt(0, column).getClass();
            } catch (IndexOutOfBoundsException|NullPointerException exception) {
                return Object.class;
            }
        }

        // Disable cell editing
        @Override
        public boolean isCellEditable(int row, int column) {
            return false;
        }
    }
}
