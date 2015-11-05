package org.compsysmed.ocsana.internal;
import java.io.*;
import javax.swing.*;

class b {
	JTextArea out ;
    public static void main(String[] args) {
        Runnable r = new Runnable() {

            @Override
            public void run() {
                final JTextArea out = 
                        new JTextArea(25,25); // suggest columns & rows
                JScrollPane outScroll = new JScrollPane(out);

                File f = new File("TextAreaScrolling.java");
                try {
                    Reader reader = new FileReader(f);
                    out.read(reader, f);
                } catch (Exception ex) {
                    ex.printStackTrace();
                }

                JOptionPane.showMessageDialog(null, outScroll);
            }
        };
        // Swing GUIs should be created and updated on the EDT
        // http://docs.oracle.com/javase/tutorial/uiswing/concurrency/initial.html
        SwingUtilities.invokeLater(r);
    }
}