package com.amjadnas.documentHandlers;

import com.amjadnas.utills.Constants;
import org.apache.poi.xwpf.usermodel.XWPFDocument;
import org.apache.poi.xwpf.usermodel.XWPFParagraph;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

class DocxHandler extends DocumentHandler {

    @Override
    public List<String> parseDocument(File file) throws IOException {

        try (FileInputStream fis = new FileInputStream(file)) {
            XWPFDocument document = new XWPFDocument(fis);
            List<XWPFParagraph> paragraphs = document.getParagraphs();
            List<String> lines = new ArrayList<>();
            for (XWPFParagraph paragraph : paragraphs) {

                lines.addAll(Arrays.asList(paragraph.getText().split(Constants.SPLIT_REGEX)));
            }

            return lines;
        }
    }
}
