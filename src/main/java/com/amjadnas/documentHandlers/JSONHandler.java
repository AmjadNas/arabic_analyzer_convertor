package com.amjadnas.documentHandlers;


import org.json.JSONArray;
import org.json.JSONObject;

import java.io.*;
import java.util.ArrayList;
import java.util.List;

public class JSONHandler extends DocumentHandler{
    @Override
    public List<String> parseDocument(File file) throws IOException {
        List<String> lines = new ArrayList<>();
        StringBuilder stringBuilder = new StringBuilder();
        try (BufferedReader reader = new BufferedReader(new FileReader(file))) {
            String str;
            while ((str = reader.readLine()) != null) {
                stringBuilder.append(str);
            }
        }
        JSONArray jsonArray = new JSONArray(stringBuilder.toString());
        for (int i = 0; i <= jsonArray.length(); i++){
            JSONObject jsonObject = jsonArray.getJSONObject(i);
            JSONArray contentArray =jsonObject.getJSONArray("context");
            for (int j = 0; j <= jsonArray.length(); j++){
                JSONObject poem = contentArray.getJSONObject(j);
                String line = poem.getString("ajuz") + " " +poem.getString("sadr");
                lines.add(line);
            }

        }
        return lines;
    }
}
