package com.amjadnas.objects;

import java.util.Objects;

public class Pair<F, S> {

    private F first;
    private S second;

    public Pair(F first, S second) {
        this.first = first;
        this.second = second;
    }

    public F getFirst() {
        return first;
    }

    public void setFirst(F first) {
        this.first = first;
    }

    public S getSecond() {
        return second;
    }

    public void setSecond(S second) {
        this.second = second;
    }


    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Pair<?, ?> pair = (Pair<?, ?>) o;
        return first.equals(pair.first) &&
                second.equals(pair.second);
    }

    @Override
    public int hashCode() {
        return Objects.hash(first, second);
    }

    @Override
    public String toString() {
        return "{" + first + ", " + second + '}';
    }


    public static <F, S extends Comparable<S>> int reverseCompareSecond(Pair<F, S> pair, Pair<F, S> pair2) {
        return pair2.second.compareTo(pair.second);
    }

    public static <F, S extends Comparable<S>> int compareSecond(Pair<F, S> pair, Pair<F, S> pair2) {
        return pair.second.compareTo(pair2.second);
    }

    public static <F extends Comparable<F>, S> int compareFirst(Pair<F, S> pair, Pair<F, S> pair2) {
        return pair.first.compareTo(pair2.first);
    }

    public static <F extends Comparable<F>, S> int reverseCompareFirst(Pair<F, S> pair, Pair<F, S> pair2) {
        return pair2.first.compareTo(pair.first);
    }
}
