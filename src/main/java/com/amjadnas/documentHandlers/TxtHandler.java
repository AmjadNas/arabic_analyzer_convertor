package com.amjadnas.documentHandlers;

import com.amjadnas.utills.Constants;

import java.io.*;
import java.util.ArrayList;
import java.util.List;

class TxtHandler extends DocumentHandler {

    @Override
    public List<String> parseDocument(File file) throws IOException{

        try (BufferedReader reader = new BufferedReader(new FileReader(file))) {
            List<String> lines = new ArrayList<>();
            String str;
            while ((str = reader.readLine()) != null) {
                for (String s : str.split(Constants.SPLIT_REGEX))
                        lines.add(s);
            }
            return lines;
        }
    }
}
