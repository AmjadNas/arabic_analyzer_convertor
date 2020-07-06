package com.amjadnas;

import AlKhalil2.morphology.result.model.Result;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.concurrent.ConcurrentHashMap;
import java.util.stream.Collectors;

public class TermDetails extends HashMap<String, Pair<Term, HashSet<Result>>> {

    @Override
    public String toString() {
        return entrySet().stream().map(e -> "docuemnt "  + e.getKey() + " value "+ e.getValue()+"\n").collect(Collectors.joining());
    }
}
