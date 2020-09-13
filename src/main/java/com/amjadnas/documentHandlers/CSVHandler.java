package com.amjadnas.documentHandlers;


import com.opencsv.CSVReader;

import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class CSVHandler extends DocumentHandler{


    @Override
    public List<String> parseDocument(File file) throws IOException {
        List<String> lines = new ArrayList<>();

        try(CSVReader reader = new CSVReader(new FileReader(file))){
            for (String[] line : reader){
                if (line.length > 1){
                    lines.add(line[0] + " " +line[1]);
                }
            }
        }catch (Exception e){
            e.printStackTrace();
            System.out.println("dddddddddddddd");
        }

        return lines;
    }
}
