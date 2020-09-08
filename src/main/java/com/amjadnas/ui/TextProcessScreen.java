package com.amjadnas.ui;

import com.amjadnas.Controller;
import com.amjadnas.listeners.Taskistener;

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

import static com.amjadnas.ui.OptionDialog.showErrorDialog;
import static com.amjadnas.ui.OptionDialog.showSuccessDialog;

public class TextProcessScreen extends JFrame implements Runnable, WindowListener, Taskistener {
    private JButton browseButton;
    private JList<String> list1;
    private JButton processFilesButton;
    private JButton saveTokensButton;
    private JPanel mainPanel;
    private JButton browseTerms;
    private JButton browseVocabulary;
    private JTextField vocabularyPathTextField;
    private JTextField termsPathTextField;
    private JTextField stopWordsPathTextField;
    private JButton browseStopwords;
    private String folderPath;
    private CompletableFuture<Vector<String>> completableFuture;
    private Controller controller;

    public TextProcessScreen() {
        processFilesButton.addActionListener(this::handleProcess);
        browseButton.addActionListener(this::handleOpenFolder);
        browseStopwords.addActionListener(this::handleBrowseStopWords);
        browseTerms.addActionListener(this::handleBrowseTerms);
        browseVocabulary.addActionListener(this::handleBrowseVocabulary);
        saveTokensButton.addActionListener(this::extractVocabulary);
    }

    @Override
    public void run() {
        controller = Controller.getInstance();
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

                controller.processFiles(this, fc.getSelectedFile(), termsPathTextField.getText(), stopWordsPathTextField.getText(), folderPath);

            }else  {
                showErrorDialog(this, "You have to choose a directory to save the files in it.");
            }
        } catch (IOException  e) {
            showErrorDialog(this, e.getMessage());
        }

    }

    private void handleBrowseTerms(ActionEvent actionEvent) {
        chooseFileForTextField(termsPathTextField);
    }

    private void handleBrowseStopWords(ActionEvent actionEvent) {
        chooseFileForTextField(stopWordsPathTextField);
    }

    private void handleBrowseVocabulary(ActionEvent actionEvent) {
        chooseFileForTextField(vocabularyPathTextField);
    }

    private void extractVocabulary(ActionEvent actionEvent) {
        if (!vocabularyPathTextField.getText().isEmpty()){
            controller.extractVocabulary(this,vocabularyPathTextField.getText(), termsPathTextField.getText());
        }else
            showErrorDialog(this, "A controlled vocabulary must be loaded!");
    }

    private void chooseFileForTextField(JTextField textField) {
        JFileChooser fileChooser = new JFileChooser();
        fileChooser.setFileSelectionMode(JFileChooser.FILES_ONLY);

        int returnVal = fileChooser.showDialog(this, "Select File");
        if (returnVal == JFileChooser.APPROVE_OPTION) {
            String path = fileChooser.getSelectedFile().getAbsolutePath();
            textField.setText(path);
        }
    }

    @Override
    public void onSuccess(String message) {
        showSuccessDialog(this, message);
    }

    @Override
    public void onError(Exception e) {
        showErrorDialog(this, e.getMessage());
    }

    @Override
    public void windowClosing(WindowEvent e) {
        completableFuture.cancel(true);
        completableFuture = null;
    }

    @Override
    public void windowOpened(WindowEvent e) {

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
