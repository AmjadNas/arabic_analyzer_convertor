package com.amjadnas;

import AlKhalil2.morphology.analyzer.AnalyzerTokens;
import AlKhalil2.morphology.result.model.Result;
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

    public Parser(){
        analyzer = new AnalyzerTokens();
        terms = new HashMap<>();
//        analyzer.clear();
    }


    public void processAsync(File newFolder, File[] listOfFiles) {

        for (File tmp: listOfFiles) {
            if (!tmp.isDirectory()) {
                String newFileName = tmp.getName().substring(0, tmp.getName().lastIndexOf(".")).concat(".txt");

                try (BufferedWriter writer = new BufferedWriter(new FileWriter(
                        new File(newFolder, newFileName)))){
                    HashMap<String, String>  words = new HashMap<>();

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


        }


//        int begin = 0;
//        int numCores = Runtime.getRuntime().availableProcessors();

//        int chunks = (int) Math.ceil( (double)listOfFiles.length / (double)numCores);
//        List<CompletableFuture<Void>> list = new ArrayList<>();



////        if (chunks == 1)
////            numCores = 1;
////            list.add(createTask(newFolder, listOfFiles, begin, listOfFiles.length-1, map));
//
//
////            for (int i = 1; i <= numCores; i++) {
////                int end = (i * chunks) - 1;
////                int dist = end - begin;
////
////                if (dist + begin > listOfFiles.length) {
////                    dist =  listOfFiles.length - begin - 1;
////                }
////
////                list.add(createTask(newFolder, listOfFiles, begin, dist+begin));
////                begin += dist + 1;
////            }
//
//
//
//        // wait for all chunks to finish
//        try {
////            CompletableFuture.allOf(list.toArray(new CompletableFuture[numCores])).get();
//            CompletableFuture.runAsync(createTask(newFolder, listOfFiles) , new ForkJoinPool(numCores))
//                             .get();
//        } catch (InterruptedException | ExecutionException e) {
//            e.printStackTrace();
//        }


        terms.remove("");
        terms.remove(" ");
//        System.out.print(terms);
    }


    private Runnable createTask(File newFolder, File[] listOfFiles) {
        return () -> {

            for (File tmp: listOfFiles) {

                String newFileName = tmp.getName().substring(0, tmp.getName().lastIndexOf(".")).concat(".txt");

                try (BufferedWriter writer = new BufferedWriter(new FileWriter(
                        new File(newFolder, newFileName)))){
                    HashMap<String, String>  words = new HashMap<>();

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
        };
    }


    private CompletableFuture<Void> createTask(File newFolder, File[] listOfFiles, int begin, int curr) {
        return CompletableFuture.runAsync(() -> {

            for (int i = begin; i <= curr; i++) {
                File tmp = listOfFiles[i];
                String newFileName = tmp.getName().substring(0, tmp.getName().lastIndexOf(".")).concat(".txt");

                try (BufferedWriter writer = new BufferedWriter(new FileWriter(
                        new File(newFolder, newFileName)))){
                    HashMap<String, String>  words = new HashMap<>();

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
        });
    }

    private void parseDocumentTXT(File file, HashMap<String, String>  map, BufferedWriter writer) {
        try(BufferedReader reader = new BufferedReader(new FileReader(file))) {
            String str;
            while ((str = reader.readLine()) != null) {
                parseText(writer, str, map, file.getName());
            }

        } catch (IOException e){
            e.printStackTrace();
        }
    }

    private void parseDocumentPDF(File tmp, HashMap<String, String>  map, BufferedWriter writer)  {
        try (PDDocument document = PDDocument.load(tmp)){
            if (!document.isEncrypted()) {
                PDFTextStripper stripper = new PDFTextStripper();
                String text = stripper.getText(document);
                for (String str : text.split("\\r?\\n")){
                    parseText(writer, str, map, tmp.getName());
                }
            }
        } catch (IOException e){
            e.printStackTrace();
        }
    }

    private void parseDocumentDoc(File tmp, HashMap<String, String>  map, BufferedWriter writer)  {
        try (FileInputStream fis = new FileInputStream(tmp)){
            HWPFDocument document = new HWPFDocument(fis);
            WordExtractor we = new WordExtractor(document);
            String[] paragraphs = we.getParagraphText();

            for (String paragraph : paragraphs) {
                for(String line : paragraph.split("\\.?\\r?\\n"))
                    parseText(writer, line, map, tmp.getName());
            }
        } catch (Exception e){
            e.printStackTrace();
        }

    }

    private void parseDocumentDocx(File tmp, HashMap<String, String>  map, BufferedWriter writer) {
        try (FileInputStream fis = new FileInputStream(tmp)){
            XWPFDocument document = new XWPFDocument(fis);
            List<XWPFParagraph> paragraphs = document.getParagraphs();
            for (XWPFParagraph paragraph : paragraphs) {
                for(String line : paragraph.getText().split(".?\\r?\\n"))
                    parseText(writer, line, map, tmp.getName());
            }
        } catch (Exception e){
            e.printStackTrace();
        }

    }

    private void parseText(BufferedWriter writer, String text, HashMap<String, String>  map, String documentName) throws IOException {

        //text = Utility.stripText(text);
        AlKhalil2.text.tokenization.Tokenization tokens = new AlKhalil2.text.tokenization.Tokenization();
//        int nbNoAnalyzedWord = 0;
//        int nbAnalyzedWord = 0;
//        int i = 0;

        for (String word : text.split("\\s")){
            tokens.setTokenizationString(word);
            List<String> tokensList = new ArrayList<>(tokens.getTokens());
//        tokensList.sort(String::compareTo);
//        AlKhalil2.util.Times.start();

            handleToken(writer, tokensList, documentName, map);
        }

        writer.write(System.lineSeparator());
//        AlKhalil2.util.Times.end();
//
//        AlKhalil2.util.Statistic.setNbTotalWords( tokens.getNbAllTokens());
//        AlKhalil2.util.Statistic.setNbWord( tokens.getNbTokens() );
//        AlKhalil2.util.Statistic.setNbAnalyzedWord( nbAnalyzedWord );
//        AlKhalil2.util.Statistic.setNbNoAnalyzedWord( nbNoAnalyzedWord );
//        AlKhalil2.util.Statistic.setTimes(AlKhalil2.util.Times.getTimes());

    }

    private void handleToken(BufferedWriter writer, List<String> tokensList, String documentName, HashMap<String, String> map) throws IOException {
        for (String token : tokensList) {
            String tmp;
            if (map.containsKey(token)) {
//                updateTerms(documentName, null, map.get(token));
                tmp = map.get(token);
            }else {
                List<Result> resultsList = analyzer.analyzerToken(token);

                if(resultsList.isEmpty()){
//                nbNoAnalyzedWord += tokens.getTokensRepeat().get(token);
                    tmp = token;

                }else{
//                nbAnalyzedWord += tokens.getTokensRepeat().get(token);
                    HashMap<String, Integer> lemmaCounter = new HashMap<>();
                    for (Result result : resultsList){

                        if (lemmaCounter.containsKey(result.getLemma()))
                            lemmaCounter.replace(result.getLemma(), lemmaCounter.get(result.getLemma()) +1);
                        else
                            lemmaCounter.put(result.getLemma(), 1);
                    }
                    Queue<Term> maxQueue = lemmaCounter.entrySet().stream()
                            .map(entry -> new Term(entry.getKey(), new AtomicInteger(entry.getValue()))).sorted()
                            .collect(Collectors.toCollection(PriorityQueue::new));
                    Term term = Optional.ofNullable(maxQueue.poll()).orElse(new Term("",new AtomicInteger(0)));
                    tmp = term.getToken();
//                updateTerms(documentName, resultsList, tmp);

                }
                map.put(token, tmp);
//                AlKhalil2.util.constants.Static.allResults.put(token, resultsList);
            }
            writer.write(tmp);
            writer.write(" ");

        }
    }

    private void updateTerms(String documentName, List<Result> resultsList, String tmp) {
        try {
            if (terms.containsKey(tmp)) {
                Pair<Term, HashSet<Result>> map = terms.get(tmp).get(documentName);
                Term t = map.getFirst();
                t.getFrequency().incrementAndGet();
            }else {
                TermDetails termDetails =  new TermDetails();
                HashSet<Result> list = new HashSet<>(resultsList);
                Pair<Term, HashSet<Result>> pair = new Pair<>(new Term(tmp, new AtomicInteger(1)), list);
                termDetails.put(documentName, pair);
                terms.put(tmp, termDetails);
            }
        }catch (NullPointerException e){
            System.out.println("NullPointerException");
            System.out.println(tmp);
            throw e;
        }

    }

}
