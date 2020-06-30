package com.amjadnas;

import java.util.Objects;
import java.util.concurrent.atomic.AtomicInteger;

public class Term implements Comparable<Term> {

    private String token;
    private AtomicInteger frequency;

    public Term(String token, AtomicInteger frequency) {
        this.token = token;
        this.frequency = frequency;
    }

    public String getToken() {
        return token;
    }

    public void setToken(String token) {
        this.token = token;
    }

    public AtomicInteger getFrequency() {
        return frequency;
    }

    public void setFrequency(AtomicInteger frequency) {
        this.frequency = frequency;
    }
    @Override
    public int compareTo(Term o) {
        return Integer.compare(this.frequency.intValue(), o.frequency.intValue());
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
                ", frequency=" + frequency +
                '}';
    }
}
