package model;

import java.io.Serial;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.UUID;

public class Library implements Serializable {

    @Serial
    private static final long serialVersionUID = 987654321L;

    private final UUID libraryId;
    private final String name;
    private final String password;
    private ArrayList<UUID> books;
    private ArrayList<UUID> readers;

    public Library(String name, String password) {
        this.libraryId = UUID.randomUUID();
        this.name = name;
        this.password = password;
        this.books = new ArrayList<>();
        this.readers = new ArrayList<>();
    }

    public UUID getLibraryId() { return libraryId; }

    public String getName() { return name; }

    public String getPassword() { return password; }

    public ArrayList<UUID> getBooks() { return books; }

    public ArrayList<UUID> getReaders() { return readers; }

    public void setBooks(ArrayList<UUID> books) { this.books = books; }

    public void addBook(UUID bookId) { this.books.add(bookId); }

    public void removeBook(UUID bookId) { this.books.remove(bookId); }

    public void setReaders(ArrayList<UUID> readers) { this.readers = readers; }

    public void addReader(UUID readerId) { this.readers.add(readerId); }

    public void removeReader(UUID readerId) { this.readers.remove(readerId); }

    @Override
    public boolean equals(Object o) {
        if (!(o instanceof Library)) {
            return false;
        }
        Library l = (Library) o;
        return this.libraryId.equals(l.libraryId);
    }

    @Override
    public String toString() {
        return this.name;
    }
}
