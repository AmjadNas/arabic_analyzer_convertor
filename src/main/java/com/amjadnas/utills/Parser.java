package com.amjadnas.utills;

import com.amjadnas.documentHandlers.DocumentHandler;
import com.amjadnas.documentHandlers.DocumentHandlerFactory;
import com.amjadnas.listeners.ProgressListener;
import com.opencsv.CSVReader;
import com.opencsv.CSVWriter;
import com.opencsv.exceptions.CsvException;
import org.apache.commons.io.FilenameUtils;

import java.io.*;
import java.util.*;
import java.util.concurrent.*;
import java.util.stream.Collectors;

public class Parser {

    private final Map<String, Integer> terms;
    private final HashSet<String> stopWords;
    private final HashSet<String> vocabulary;


    public Parser() {
        terms = new HashMap<>();
        stopWords = new HashSet<>();
        vocabulary = new HashSet<>();
    }


    public void loadTerms(File file) throws IOException, CsvException {
        try (CSVReader csvReader = new CSVReader(new FileReader(file))) {

            List<String[]> list = csvReader.readAll();
            list.remove(0);
            list.forEach(pair -> terms.put(pair[0], Integer.valueOf(pair[1])));

        }
    }

    public void extractVocabulary(File file, ProgressListener listener) throws IOException, CsvException {
        loadTerms(file);
        File txtFile = new File(file.getParent(), file.getName().replace(".csv", ".txt"));
        try (BufferedWriter writer = new BufferedWriter(new FileWriter(txtFile))) {
            List<String> list = terms.keySet().stream()
                    .filter(vocabulary::contains).collect(Collectors.toList());
            int size = list.size();
            int i = 0;
            for (String str : list) {
                writer.write(str);
                i++;
                int progress = (i * 100) / size;
                listener.onProgress(null, progress);
            }
        }
    }

    public void loadVocabulary(File file) throws IOException {
        loadTextIntoSet(file, vocabulary);
    }

    public void loadStopWords(File file) throws IOException {
        loadTextIntoSet(file, stopWords);
    }

    public void processAsync(File newFolder, List<File> listOfFiles, ProgressListener listener) throws InterruptedException, ExecutionException, IOException {
        int numCores = Runtime.getRuntime().availableProcessors();

        try (CSVWriter csvWriter = new CSVWriter(new FileWriter(new File(newFolder.getParentFile()
                , newFolder.getName() + ".csv")));) {

            writeToCsv(csvWriter, "Term", "Frequency");
            CompletableFuture.runAsync(createTask(newFolder, listOfFiles, listener), new ForkJoinPool(numCores))
                    .get();
            terms.remove("");
            terms.remove(" ");
            terms.forEach((key, value) -> {
                writeToCsv(csvWriter, key, value);
            });
        }
    }

    private <K, V> void writeToCsv(CSVWriter csvWriter, K key, V value) {
        String[] cols = {key.toString(), value.toString()};
        csvWriter.writeNext(cols);
    }

    private Runnable createTask(File newFolder, List<File> listOfFiles, ProgressListener listener) {
        return () -> {
            int size = listOfFiles.size();
            int i = 1;
            for (File tmp : listOfFiles) {

                int progress = (i * 100) / size;
                try {
                    processFile(newFolder, tmp);
                    i++;
                    listener.onProgress(" processed file: " + tmp.getName(), progress);
                } catch (IOException e) {
                    listener.onProgress(" failed to process file: " + tmp.getName(), progress);

                }

            }
        };
    }

    private CompletableFuture<Void> createTask(File newFolder, File[] listOfFiles, int begin, int curr) {
        return CompletableFuture.runAsync(() -> {

            for (int i = begin; i <= curr; i++) {
                File tmp = listOfFiles[i];
                try {
                    processFile(newFolder, tmp);
                } catch (IOException e) {
                    e.printStackTrace();
                }

            }
        });
    }

    private void processFile(File newFolder, File tmp) throws IOException {
        String newFileName = tmp.getName().substring(0, tmp.getName().lastIndexOf(".")).concat(".txt");

        try (BufferedWriter writer = new BufferedWriter(new FileWriter(
                new File(newFolder, newFileName)))) {

            String extension = FilenameUtils.getExtension(tmp.getName());
            DocumentHandler documentHandler = DocumentHandlerFactory.getHandler(extension);
            List<String> lines = documentHandler.parseDocument(tmp);
            for (String line : lines) {
                documentHandler.writeText(writer, terms, line, stopWords);

            }

        }
    }

    private void loadTextIntoSet(File file, HashSet<String> vocabulary) throws IOException {
        String extension = FilenameUtils.getExtension(file.getName());
        DocumentHandler documentHandler = DocumentHandlerFactory.getHandler(extension);
        vocabulary.addAll(documentHandler.normalizeLines(documentHandler.parseDocument(file)));
    }


}
