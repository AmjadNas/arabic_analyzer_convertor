package com.amjadnas;

import javax.swing.*;
import javax.swing.filechooser.FileFilter;

import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.WindowEvent;
import java.awt.event.WindowListener;
import java.io.File;
import java.io.IOException;
import java.util.Vector;
import java.util.concurrent.CompletableFuture;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import static com.amjadnas.OptionDialog.showErrorDialog;

public class MainScreen extends JFrame implements Runnable, WindowListener {
    private JButton browseButton;
    private JList<String> list1;
    private JButton processFilesButton;
    private JButton saveTokensButton;
    private JPanel mainPanel;
    private String folderPath;
    private CompletableFuture<Vector<String>> completableFuture;

    public MainScreen() {
        processFilesButton.addActionListener(this::handleProcess);
        browseButton.addActionListener(this::handleOpenFolder);
    }


    @Override
    public void run() {

        setContentPane(mainPanel);
        setBounds(new Rectangle(640, 480));
        setDefaultCloseOperation(EXIT_ON_CLOSE);
        setVisible(true);
    }

    private void handleOpenFolder(ActionEvent e) {
        JFileChooser fileChooser = new JFileChooser();
        if (folderPath != null)
            fileChooser.setCurrentDirectory(new File(folderPath));

        fileChooser.setFileSelectionMode(JFileChooser.DIRECTORIES_ONLY);
        int returnVal = fileChooser.showDialog(this, "Select Folder");
        if (returnVal == JFileChooser.APPROVE_OPTION) {
            folderPath = fileChooser.getSelectedFile().getAbsolutePath();
            completableFuture = CompletableFuture.supplyAsync(() -> Stream.of(fileChooser.getSelectedFile().listFiles()).map(File::getName).collect(Collectors.toCollection(Vector::new)));
            completableFuture.thenApplyAsync(strings -> {
                list1.setListData(strings);
                return null;
            });

        }

    }

    private void handleProcess(ActionEvent actionEvent) {
        try {
            if (folderPath == null)
                throw new IOException("No chosen directory!");

            JFileChooser fc = new JFileChooser();
            fc.setCurrentDirectory(new File(folderPath).getParentFile());
            fc.setFileSelectionMode(JFileChooser.DIRECTORIES_ONLY);
            fc.setFileFilter( new FileFilter(){

                @Override
                public boolean accept(File f) {
                    return f.isDirectory();
                }

                @Override
                public String getDescription() {
                    return "Any folder";
                }

            });

            fc.setDialogType(JFileChooser.SAVE_DIALOG);
            int returnVal = fc.showDialog(this, "Select");
            if (returnVal == JFileChooser.APPROVE_OPTION) {

                Parser parser = new Parser();
                parser.processAsync(fc.getSelectedFile(), new File(folderPath).listFiles());


            }else  {
                showErrorDialog(this, "You to choose a directory to save the files in it.");
            }
        } catch (IOException e) {
            showErrorDialog(this, e.getMessage());
        }

    }

    @Override
    public void windowOpened(WindowEvent e) {

    }

    @Override
    public void windowClosing(WindowEvent e) {
        completableFuture.cancel(true);
        completableFuture = null;
    }

    @Override
    public void windowClosed(WindowEvent e) {

    }

    @Override
    public void windowIconified(WindowEvent e) {

    }

    @Override
    public void windowDeiconified(WindowEvent e) {

    }

    @Override
    public void windowActivated(WindowEvent e) {

    }

    @Override
    public void windowDeactivated(WindowEvent e) {

    }
}
