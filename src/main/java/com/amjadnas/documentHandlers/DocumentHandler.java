package com.amjadnas.documentHandlers;

import com.amjadnas.utills.AraNormalizer;
import com.amjadnas.utills.DiacriticsRemover;
import com.amjadnas.utills.PunctuationsRemover;

import java.io.BufferedWriter;
import java.io.File;
import java.io.IOException;
import java.util.*;
import java.util.stream.Collectors;

public abstract class DocumentHandler {
    private final AlKhalil2.text.tokenization.Tokenization tokennizer;
    private final DiacriticsRemover diacriticsRemover;
    private final AraNormalizer araNormalizer;
    private final PunctuationsRemover punctuationsRemover;

    DocumentHandler() {
        tokennizer = new AlKhalil2.text.tokenization.Tokenization();
        diacriticsRemover = new DiacriticsRemover();
        araNormalizer = new AraNormalizer();
        punctuationsRemover = new PunctuationsRemover();
    }

    public abstract List<String> parseDocument(File file) throws IOException;

    public List<String> normalizeLines(List<String> lines){
        return lines.stream()
                .map(this::normalizeLine)
                .collect(Collectors.toList());
    }

    public void writeText(BufferedWriter writer, Map<String,Integer> terms, String line, Set<String> stopWords) throws IOException {
        line = normalizeLine(line);

        for (String word : line.split("\\s")) {
            tokennizer.setTokenizationString(word);
            List<String> tokensList = new ArrayList<>(tokennizer.getTokens());
            handleToken(writer, tokensList, stopWords);
            updateTerms(terms, word, stopWords);
        }

        writer.write("\n");
    }

    private String normalizeLine(String line){
        return punctuationsRemover.removePunctuations(diacriticsRemover.removeDiacritics(araNormalizer.normalize(line)));
    }

    private void handleToken(BufferedWriter writer, List<String> tokensList, Set<String> stopWords) throws IOException {
        for (String token : tokensList) {
            if (!stopWords.contains(token)){
                writer.write(token);
                writer.write(" ");
            }
        }
    }

    private void updateTerms(Map<String,Integer> terms, String term, Set<String> stopWords) {
        if (!stopWords.contains(term) && !term.contains("_")) {
            if (terms.containsKey(term)) {
                int count = terms.get(term) + 1;
                terms.replace(term, count);
            }else{
                terms.put(term, 1);
            }
        }
    }
//    private String processToken(String documentName, List<Result> resultsList) {
//        String tmp;
//        HashMap<String, Integer> lemmaCounter = new HashMap<>();
//        for (Result result : resultsList) {
//
//            if (lemmaCounter.containsKey(result.getLemma()))
//                lemmaCounter.replace(result.getLemma(), lemmaCounter.get(result.getLemma()) + 1);
//            else
//                lemmaCounter.put(result.getLemma(), 1);
//        }
//        Queue<Term> maxQueue = lemmaCounter.entrySet().stream()
//                .map(entry -> new Term(entry.getKey(), new AtomicInteger(entry.getValue()))).sorted()
//                .collect(Collectors.toCollection(PriorityQueue::new));
//        Term term = Optional.ofNullable(maxQueue.poll()).orElse(new Term("", new AtomicInteger(0)));
//        tmp = term.getToken();
////        updateTerms(documentName, resultsList, tmp);
//        return tmp;
//    }


}



