/**
 * Common implementation for dialogs in OCSANA
 *
 * Copyright Vera-Licona Research Group (C) 2016
 *
 * This software is licensed under the Artistic License 2.0, see the
 * LICENSE file or
 * http://www.opensource.org/licenses/artistic-license-2.0.php for
 * details
 **/

package org.compsysmed.ocsana.internal.ui;

// Java imports
import java.util.*;

import java.awt.BorderLayout;

import java.awt.event.ActionEvent;

import javax.swing.AbstractAction;
import javax.swing.JButton;
import javax.swing.JDialog;
import javax.swing.JFrame;
import javax.swing.JPanel;

// Cytoscape imports
import org.cytoscape.util.swing.LookAndFeelUtil;

/**
 * Wrapper class for OCSANA dialog boxes
 * <p>
 *
 * This class provides a lightweight convenience wrapper which ensures
 * a unified look-and-feel for OCSANA dialogs. This includes a
 * standardized BorderLayout and a "Close" button with hotkey support.
 **/
public class OCSANADialog
    extends JDialog {
    protected JButton closeButton;
    protected JPanel buttonPanel;

    /**
     * Constructor
     * <p>
     * NOTE: the dialog will be shown immediately on construction!
     * @param parentFrame the parent JFrame of this dialog (used for
     * positioning)
     * @param title  the title of the dialog
     **/
    public OCSANADialog (JFrame parentFrame,
                         String title) {
        super(parentFrame, title);

        setDefaultCloseOperation(DISPOSE_ON_CLOSE);

        setLocationRelativeTo(parentFrame);

        rebuild();
    }

    private void rebuild () {
        closeButton = new JButton(new AbstractAction("Close") {
                @Override
                public void actionPerformed(ActionEvent e) {
                    dispose();
                }
            });

        buttonPanel = new JPanel();
        buttonPanel.add(closeButton, BorderLayout.LINE_END);

        add(buttonPanel, BorderLayout.PAGE_END);

        LookAndFeelUtil.setDefaultOkCancelKeyStrokes(getRootPane(), closeButton.getAction(), closeButton.getAction());
        getRootPane().setDefaultButton(closeButton);

        setVisible(true);
    }

    @Override
    public void removeAll () {
        super.removeAll();
        rebuild();
    }
}
