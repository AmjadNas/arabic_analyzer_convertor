package com.amjadnas.ui;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionListener;

public class OptionDialog {

    public static void showErrorDialog(Component parent, String message) {
        JOptionPane.showConfirmDialog(parent, message, "Error", JOptionPane.DEFAULT_OPTION, JOptionPane.ERROR_MESSAGE);
    }

    public static void showYesNoDialog(Component parent, String message, String title, ActionListener yesListener, ActionListener noListener) {
        int option = JOptionPane.showConfirmDialog(parent, message, title, JOptionPane.YES_NO_CANCEL_OPTION, JOptionPane.QUESTION_MESSAGE);
        if (option == JOptionPane.YES_OPTION)
            yesListener.actionPerformed(null);
    }

    public static void showSuccessDialog(Component parent, String message) {
        JOptionPane.showConfirmDialog(parent, message, "Success", JOptionPane.DEFAULT_OPTION, JOptionPane.PLAIN_MESSAGE);
    }
}
