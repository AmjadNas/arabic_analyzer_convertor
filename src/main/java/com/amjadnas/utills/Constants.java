package com.amjadnas.utills;

import java.util.ArrayList;

public class Constants {
    public static final String PDF = "pdf";
    public static final String TXT = "txt";
    public static final String DOCX = "docx";
    public static final String DOC = "doc";

    public static final String SPLIT_REGEX = "\\r?\\n?\\u000B";

    public static final char ALIF='\u0627';
    public static final char ALIF_HAMZA_ABOVE='\u0623';
    public static final char ALIF_HAMZA_BELOW='\u0625';
    public static final char ALIF_MADDA='\u0622';
    public static final char TAA_MARBUTA='\u0629';
    public static final char HAA='\u0647';
    public static final char YAA='\u064A';
    public static final char DOTLESS_YAA='\u0649';
    public static final String TATWEEL="\u0640";

    public static ArrayList<String> punctuations = new ArrayList<String>();
    public static ArrayList<String> diacritics = new ArrayList<String>();

    static{
        punctuations.add(",");
        punctuations.add(":");
        punctuations.add("،");    // Araic comma
        punctuations.add("؛");    // Arabic semi-colons
        punctuations.add("؟");    // Arabic question mark
        punctuations.add("'");
        punctuations.add("!");
        punctuations.add("?");
        punctuations.add("-");
        punctuations.add("--");
        punctuations.add("(");
        punctuations.add(")");
        punctuations.add("..."); // points of ellipsis.

        diacritics.add("\u064B");  //  FATHATAN
        diacritics.add("\u064C");  //  DAMMATAN
        diacritics.add("\u064D");   //  KASRATAN
        diacritics.add("\u064E");  //  FATHA
        diacritics.add("\u064F");  //  DAMMA
        diacritics.add("\u0650");  //  KASRA
        diacritics.add("\u0651");  //  SHADDA
        diacritics.add("\u0652");  //  SUKUN


    }
}
