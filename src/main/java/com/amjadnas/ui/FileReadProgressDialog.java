package com.amjadnas.ui;

import javax.swing.*;
import java.awt.event.*;
import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;

public class FileReadProgressDialog extends JDialog implements PropertyChangeListener {
    private JPanel contentPane;
    private JButton buttonCancel;
    private JProgressBar progressBar1;

    public FileReadProgressDialog(JFrame parent, String title) {
        super(parent);
        setTitle(title);
        setContentPane(contentPane);
//        setModal(true);
        getRootPane().setDefaultButton(buttonCancel);

        // call onCancel() when cross is clicked
//        setDefaultCloseOperation(DO_NOTHING_ON_CLOSE);
//        addWindowListener(new WindowAdapter() {
//            public void windowClosing(WindowEvent e) {
//                onCancel(null);
//            }
//        });

        // call onCancel() on ESCAPE
//        contentPane.registerKeyboardAction(this::onCancel, KeyStroke.getKeyStroke(KeyEvent.VK_ESCAPE, 0), JComponent.WHEN_ANCESTOR_OF_FOCUSED_COMPONENT);
    }

    private void onCancel(ActionEvent e) {
        buttonCancel.getActionListeners()[0].actionPerformed(e);
    }


    void updateProgressBar(int progress){
        progressBar1.setValue(progress);
    }

    @Override
    public void propertyChange(PropertyChangeEvent evt) {
        if ("progress".equals(evt.getPropertyName()) ) {
            int progress = (Integer) evt.getNewValue();
            updateProgressBar(progress);
        }
    }

    public void setCancelListener(ActionListener listener) {
        buttonCancel.addActionListener(listener);

    }
}
