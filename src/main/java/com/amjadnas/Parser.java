package com.amjadnas;

import AlKhalil2.morphology.analyzer.AnalyzerTokens;
import AlKhalil2.morphology.result.model.Result;
import com.opencsv.CSVWriter;
import org.apache.pdfbox.pdmodel.PDDocument;
import org.apache.pdfbox.text.PDFTextStripper;
import org.apache.poi.hwpf.HWPFDocument;
import org.apache.poi.hwpf.extractor.WordExtractor;
import org.apache.poi.xwpf.usermodel.XWPFDocument;
import org.apache.poi.xwpf.usermodel.XWPFParagraph;

import java.io.*;
import java.util.*;
import java.util.concurrent.*;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.stream.Collectors;

public class Parser {

    private final Map<String, TermDetails> terms;
    private final AnalyzerTokens analyzer;

    public Parser() {
        analyzer = new AnalyzerTokens();
        terms = new HashMap<>();
    }

    public void processAsync(File newFolder, File[] listOfFiles) {
        int numCores = Runtime.getRuntime().availableProcessors();

        try (CSVWriter csvWriter = new CSVWriter(new FileWriter(new File(newFolder.getParentFile()
                , newFolder.getName() + ".csv")));) {
            String[] columns = {"Normalized Term", "Documents", "Details..."};
            csvWriter.writeNext(columns);
            CompletableFuture.runAsync(createTask(newFolder, listOfFiles), new ForkJoinPool(numCores))
                    .get();
            terms.remove("");
            terms.remove(" ");
            terms.forEach((key, value) -> {
                writeToCsv(csvWriter, key, value);
            });
        } catch (InterruptedException | ExecutionException | IOException e) {
            e.printStackTrace();
        }
    }

    private void writeToCsv(CSVWriter csvWriter, String key, TermDetails value) {
        List<String> strings = new ArrayList<>();
        strings.add(key);
        value.forEach((key2, value2) -> {
            strings.add(key2);
            strings.add(value2.getSecond().toString());
        });
        csvWriter.writeNext(strings.toArray(new String[0]));
    }

    private Runnable createTask(File newFolder, File[] listOfFiles) {
        return () -> {

            for (File tmp : listOfFiles) {

                readFile(newFolder, tmp);

            }
        };
    }

    private CompletableFuture<Void> createTask(File newFolder, File[] listOfFiles, int begin, int curr) {
        return CompletableFuture.runAsync(() -> {

            for (int i = begin; i <= curr; i++) {
                File tmp = listOfFiles[i];
                readFile(newFolder, tmp);

            }
        });
    }

    private void readFile(File newFolder, File tmp) {
        String newFileName = tmp.getName().substring(0, tmp.getName().lastIndexOf(".")).concat(".txt");

        try (BufferedWriter writer = new BufferedWriter(new FileWriter(
                new File(newFolder, newFileName)))) {
            HashMap<String, String> words = new HashMap<>();

            if (tmp.getName().endsWith(".txt"))
                parseDocumentTXT(tmp, words, writer);
            else if (tmp.getName().endsWith(".docx"))
                parseDocumentDocx(tmp, words, writer);
            else if (tmp.getName().endsWith(".pdf"))
                parseDocumentPDF(tmp, words, writer);
            else if (tmp.getName().endsWith(".doc"))
                parseDocumentDoc(tmp, words, writer);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private void parseDocumentTXT(File file, HashMap<String, String> map, BufferedWriter writer) {
        try (BufferedReader reader = new BufferedReader(new FileReader(file))) {
            String str;
            while ((str = reader.readLine()) != null) {
                parseText(writer, str, map, file.getName());
            }

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void parseDocumentPDF(File tmp, HashMap<String, String> map, BufferedWriter writer) {
        try (PDDocument document = PDDocument.load(tmp)) {
            if (!document.isEncrypted()) {
                PDFTextStripper stripper = new PDFTextStripper();
                String text = stripper.getText(document);
                for (String str : text.split("[،|.|\\r|\\n |,]")) {
                    parseText(writer, str, map, tmp.getName());
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void parseDocumentDoc(File tmp, HashMap<String, String> map, BufferedWriter writer) {
        try (FileInputStream fis = new FileInputStream(tmp)) {
            HWPFDocument document = new HWPFDocument(fis);
            WordExtractor we = new WordExtractor(document);
            String[] paragraphs = we.getParagraphText();

            for (String paragraph : paragraphs) {
                for (String line : paragraph.split("[،|.|\\r|\\n |,]"))
                    parseText(writer, line, map, tmp.getName());
            }
        } catch (Exception e) {
            e.printStackTrace();
        }

    }

    private void parseDocumentDocx(File tmp, HashMap<String, String> map, BufferedWriter writer) {
        try (FileInputStream fis = new FileInputStream(tmp)) {
            XWPFDocument document = new XWPFDocument(fis);
            List<XWPFParagraph> paragraphs = document.getParagraphs();
            for (XWPFParagraph paragraph : paragraphs) {

                for (String line : paragraph.getText().split("[،|.|\\r|\\n |,]"))
                    parseText(writer, line, map, tmp.getName());
            }
        } catch (Exception e) {
            e.printStackTrace();
        }

    }

    private void parseText(BufferedWriter writer, String text, HashMap<String, String> map, String documentName) throws IOException {

        AlKhalil2.text.tokenization.Tokenization tokens = new AlKhalil2.text.tokenization.Tokenization();

        for (String word : text.split("\\s")) {
            tokens.setTokenizationString(word);
            List<String> tokensList = new ArrayList<>(tokens.getTokens());

            handleToken(writer, tokensList, documentName, map);
        }

        writer.write("\n");


    }

    private void handleToken(BufferedWriter writer, List<String> tokensList, String documentName, HashMap<String, String> map) throws IOException {
        for (String token : tokensList) {
            String tmp;
            if (map.containsKey(token)) {
//                updateTerms(documentName, null, map.get(token));
                tmp = map.get(token);
            } else {
                List<Result> resultsList = analyzer.analyzerToken(token);

                if (resultsList.isEmpty()) {
                    tmp = token;

                } else {
                    tmp = processToken(documentName, resultsList);

                }
                map.put(token, tmp);
            }
            writer.write(tmp);
            writer.write(" ");

        }
    }

    private String processToken(String documentName, List<Result> resultsList) {
        String tmp;
        HashMap<String, Integer> lemmaCounter = new HashMap<>();
        for (Result result : resultsList) {

            if (lemmaCounter.containsKey(result.getLemma()))
                lemmaCounter.replace(result.getLemma(), lemmaCounter.get(result.getLemma()) + 1);
            else
                lemmaCounter.put(result.getLemma(), 1);
        }
        Queue<Term> maxQueue = lemmaCounter.entrySet().stream()
                .map(entry -> new Term(entry.getKey(), new AtomicInteger(entry.getValue()))).sorted()
                .collect(Collectors.toCollection(PriorityQueue::new));
        Term term = Optional.ofNullable(maxQueue.poll()).orElse(new Term("", new AtomicInteger(0)));
        tmp = term.getToken();
//        updateTerms(documentName, resultsList, tmp);
        return tmp;
    }

    private void updateTerms(String documentName, List<Result> resultsList, String tmp) {

        if (terms.containsKey(tmp)) {


            if (terms.get(tmp).containsKey(documentName)) {
                Pair<Term, HashSet<Result>> pair = terms.get(tmp).get(documentName);
                Term t = pair.getFirst();
                t.getFrequency().incrementAndGet();
            } else {
                HashSet<Result> list = new HashSet<>(resultsList);
                Pair<Term, HashSet<Result>> pair = new Pair<>(new Term(tmp, new AtomicInteger(1)), list);
                terms.get(tmp).put(documentName, pair);
            }
        } else {
            TermDetails termDetails = new TermDetails();
            HashSet<Result> list = new HashSet<>(resultsList);
            Pair<Term, HashSet<Result>> pair = new Pair<>(new Term(tmp, new AtomicInteger(1)), list);
            termDetails.put(documentName, pair);
            terms.put(tmp, termDetails);
        }

    }

}
