package com.amjadnas.objects;

import java.util.Objects;

public class Document {
    private final long id;
    private final String name;
    private final String author;
    private final String date;

    public Document(long id, String name, String author, String date) {
        this.id = id;
        this.name = name;
        this.author = author;
        this.date = date;
    }

    public long getId() {
        return id;
    }

    public String getName() {
        return name;
    }

    public String getAuthor() {
        return author;
    }

    public String getDate() {
        return date;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Document document = (Document) o;
        return id == document.id;
    }

    @Override
    public int hashCode() {
        return Objects.hash(id);
    }
}
