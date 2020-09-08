package com.amjadnas;

import com.amjadnas.listeners.ProgressListener;
import com.amjadnas.listeners.Taskistener;
import com.amjadnas.ui.FileReadProgressDialog;
import com.amjadnas.ui.TextProcessScreen;
import com.amjadnas.utills.Parser;
import com.opencsv.exceptions.CsvException;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;


public final class Controller {

    private static Controller instance;

    private Controller() {
    }

    public static Controller getInstance() {
        if (instance == null) {
            synchronized (Controller.class) {
                if (instance == null)
                    instance = new Controller();
            }
        }
        return instance;
    }

    public void extractVocabulary(Taskistener listener, String vocabularyDir, String termsDir) {
        new ExtractVocabulary(listener, vocabularyDir, termsDir).execute();
    }

    public void processFiles(Taskistener listener, File fc, String termsDir, String stopWordsDir, String folderPath) {
        new ProcessFiles(listener, stopWordsDir, termsDir, fc, folderPath).execute();
    }

    private void exploreDirectory(String dir, List<File> list){
        File file = new File(dir);
        if (file.listFiles() != null) {
            for (File f : file.listFiles()) {
                if (f.isDirectory())
                    exploreDirectory(f.getAbsolutePath(), list);
                else
                    list.add(f);
            }
        }
    }

    private class ProcessFiles extends SwingWorker<Void, File> implements ProgressListener {
        private final String folderPath;
        private final String stopWordsDir;
        private final String termsDir;
        private Taskistener listener;
        private final File file;
        private ProgressMonitor progressMonitor;

        public ProcessFiles(Taskistener listener, String stopWordsDir, String termsDir, File file, String folderPath) {
            this.stopWordsDir = stopWordsDir;
            this.termsDir = termsDir;
            this.listener = listener;
            this.file = file;
            this.folderPath = folderPath;
        }

        @Override
        protected void done() {
            listener = null;
            progressMonitor = null;
        }

        @Override
        public void onPreStart() {
            progressMonitor = new ProgressMonitor((JFrame) listener, "Processing Files",
                    "Task starting", 0, 100);
            //decide after 100 millis whether to show popup or not
            progressMonitor.setMillisToDecideToPopup(100);
            //after deciding if predicted time is longer than 100 show popup
            progressMonitor.setMillisToPopup(100);

        }

        @Override
        public void onProgress(String message, int progress) {
            if (progressMonitor.isCanceled())
                cancel(true);
            progressMonitor.setProgress(progress);
            progressMonitor.setNote(message);
        }

        @Override
        protected Void doInBackground() throws Exception {
            Parser parser = new Parser();
            onPreStart();
            try {
                if (!termsDir.isEmpty())
                    parser.loadTerms(new File(termsDir));
                if (!stopWordsDir.isEmpty())
                    parser.loadStopWords(new File(stopWordsDir));

                progressMonitor.setNote("Scanning and collecting files...");

                List<File> files = new ArrayList<>();
                exploreDirectory(folderPath, files);

                progressMonitor.setNote("Processing files..");
                parser.processAsync(file, files, this);
                listener.onSuccess("Extracted vocabulary successfully");

            } catch (IOException | CsvException e) {
                listener.onError(e);
                cancel(true);
            }
            return null;
        }
    }

    private class ExtractVocabulary extends SwingWorker<Void, Void> implements ProgressListener, ActionListener {
        private String vocabularyDir;
        private String termsDir;
        private Taskistener listener;
        private FileReadProgressDialog dialog;

        public ExtractVocabulary(Taskistener listener, String vocabularyDir, String termsDir) {
            this.vocabularyDir = vocabularyDir;
            this.termsDir = termsDir;
            this.listener = listener;
        }

        @Override
        protected void done() {
            dialog.setCursor(null);
            dialog.dispose();
            dialog = null;
            listener = null;
        }

        @Override
        public void onPreStart() {
            dialog = new FileReadProgressDialog((TextProcessScreen) listener, "Extracting Vocabulary");
            addPropertyChangeListener(dialog);
            dialog.setCancelListener(this);
            dialog.pack();
            dialog.setVisible(true);
            dialog.setCursor(Cursor.getPredefinedCursor(Cursor.WAIT_CURSOR));
        }

        @Override
        public void onProgress(String message, int progress) {
            setProgress(progress);
        }

        @Override
        protected Void doInBackground() throws Exception {
            Parser parser = new Parser();
            onPreStart();

            try {
                parser.loadVocabulary(new File(vocabularyDir));
                parser.extractVocabulary(new File(termsDir), this);
                listener.onSuccess("Extracted vocabulary successfully");

            } catch (IOException | CsvException e) {
                listener.onError(e);
                cancel(true);
            }
            return null;
        }

        @Override
        public void actionPerformed(ActionEvent e) {
            cancel(true);
        }
    }

}
