package com.amjadnas.documentHandlers;

import com.amjadnas.documentHandlers.DocumentHandler;
import com.amjadnas.utills.Constants;
import org.apache.pdfbox.pdmodel.PDDocument;
import org.apache.pdfbox.text.PDFTextStripper;

import java.io.File;
import java.io.IOException;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.Stream;

class PdfHandler extends DocumentHandler {

    @Override
    public List<String> parseDocument(File file) throws IOException {
        try (PDDocument document = PDDocument.load(file)) {
            if (!document.isEncrypted()) {
                PDFTextStripper stripper = new PDFTextStripper();
                String text = stripper.getText(document);
//                for (String str : text.split()) {
//                    writeText(writer, str, words, file.getName());
//                }
                return Stream.of(text.split(Constants.SPLIT_REGEX))
                        .collect(Collectors.toList());
            }else
                throw new IOException("document is encrypted!");

        }
    }
}
