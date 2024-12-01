package model;

import java.io.Serial;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.UUID;

public class Reader implements Serializable {

    @Serial
    private static final long serialVersionUID = 135792468L;

    private final UUID readerId;
    private String username;
    private String password;
    private ArrayList<UUID> books;
    private UUID library;

    public Reader(String username, String password, UUID library) {
        this.readerId = UUID.randomUUID();
        this.username = username;
        this.password = password;
        this.books = new ArrayList<>();
        this.library = library;
    }

    public UUID getReaderId() { return readerId; }

    public String getUsername() { return username; }

    public String getPassword() { return password; }

    public ArrayList<UUID> getBooks() { return books; }

    public UUID getLibrary() { return library; }

    public void setBooks(ArrayList<UUID> books) { this.books = books; }

    public void addBook(UUID id) { this.books.add(id); }

    public void removeBook(UUID id) { this.books.remove(id); }

    public void setLibrary(UUID library) { this.library = library; }

    @Override
    public boolean equals(Object o) {
        if (!(o instanceof Reader)) {
            return false;
        }
        Reader l = (Reader) o;
        return this.readerId.equals(l.readerId);
    }
}
