package com.amjadnas.objects;

import AlKhalil2.morphology.result.model.Result;
import com.amjadnas.objects.Pair;
import com.amjadnas.objects.Term;

import java.util.HashMap;
import java.util.HashSet;
import java.util.stream.Collectors;

public class TermDetails extends HashMap<String, Pair<Term, HashSet<Result>>> {

    @Override
    public String toString() {
        return entrySet().stream().map(e -> "docuemnt "  + e.getKey() + " value "+ e.getValue()+"\n").collect(Collectors.joining());
    }
}
