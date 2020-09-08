package com.amjadnas.objects;

import java.util.Objects;
import java.util.concurrent.atomic.AtomicInteger;

public class Term implements Comparable<Term> {

    private String token;
    private AtomicInteger totalFrequency;

    public Term(String token, AtomicInteger totalFrequency) {
        this.token = token;
        this.totalFrequency = totalFrequency;
    }

    public String getToken() {
        return token;
    }

    public void setToken(String token) {
        this.token = token;
    }

    public AtomicInteger getTotalFrequency() {
        return totalFrequency;
    }

    public void setTotalFrequency(AtomicInteger totalFrequency) {
        this.totalFrequency = totalFrequency;
    }
    @Override
    public int compareTo(Term o) {
        return Integer.compare(this.totalFrequency.intValue(), o.totalFrequency.intValue());
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Term term = (Term) o;
        return token.equals(term.token);
    }



    @Override
    public int hashCode() {
        return Objects.hash(token);
    }

    @Override
    public String toString() {
        return "Term{" +
                "token='" + token + '\'' +
                ", frequency=" + totalFrequency +
                '}';
    }
}
