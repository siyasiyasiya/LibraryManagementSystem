package model;

import java.io.Serial;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.UUID;

public class Book implements Serializable {

    @Serial
    private static final long serialVersionUID = 123456789L;

    private final UUID bookID;
    private final String title;
    private final String author;
    private final String genre;
    private final String synopsis;

    private UUID reader;
    private UUID library;

    public Book(String title, String author, String genre, String synopsis, UUID library) {
        this.bookID = UUID.randomUUID();
        this.title = title;
        this.author = author;
        this.genre = genre;
        this.synopsis = synopsis;
        this.reader = null;
        this.library = library;
    }

    public UUID getBookID() { return bookID; }

    public String getTitle() { return title; }

    public String getAuthor() { return author; }

    public String getGenre() { return genre; }

    public String getSynopsis() { return synopsis; }

    public UUID getReader() { return reader; }

    public UUID getLibrary() { return library; }

    public void setReader(UUID reader) { this.reader = reader; }

    public void setLibrary(UUID library) { this.library = library; }

    public boolean isAvailable() { return this.reader == null; }

    @Override
    public boolean equals(Object o) {
        if (!(o instanceof Book)) {
            return false;
        }
        Book l = (Book) o;
        return this.bookID.equals(l.bookID);
    }

    @Override
    public String toString() {
        return this.title + " by " + this.author;
    }
}
