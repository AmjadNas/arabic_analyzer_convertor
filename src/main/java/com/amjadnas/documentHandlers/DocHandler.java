package com.amjadnas.documentHandlers;

import com.amjadnas.utills.Constants;
import org.apache.poi.hwpf.HWPFDocument;
import org.apache.poi.hwpf.extractor.WordExtractor;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

class DocHandler extends DocumentHandler {

    @Override
    public List<String> parseDocument(File file) throws IOException {
        try (FileInputStream fis = new FileInputStream(file)) {
            HWPFDocument document = new HWPFDocument(fis);
            WordExtractor we = new WordExtractor(document);
            String[] paragraphs = we.getParagraphText();

            List<String> lines = new ArrayList<>();

            for (String paragraph : paragraphs) {
                lines.addAll(Arrays.asList(paragraph.split(Constants.SPLIT_REGEX)));
            }
            return lines;
        }
    }
}
