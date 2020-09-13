package com.amjadnas.documentHandlers;

import com.amjadnas.utills.Constants;

import java.io.IOException;

public final class DocumentHandlerFactory {

    private DocumentHandlerFactory() {
    }

    public static DocumentHandler getHandler(String extension) throws IOException {
        switch (extension) {
            case Constants.TXT:
                return new TxtHandler();
            case Constants.PDF:
                return new PdfHandler();
            case Constants.DOCX:
                return new DocxHandler();
            case Constants.DOC:
                return new DocHandler();
            case Constants.JSON:
                return new JSONHandler();
            case Constants.CSV:
                return new CSVHandler();
            default:
                throw new IOException("File extension is not supported");
        }
    }

}
