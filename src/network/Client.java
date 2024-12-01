package network;

import model.Book;
import model.Library;
import model.Reader;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.Socket;
import java.util.ArrayList;

public class Client {

    private String username;
    private String password;
    private Socket socket;
    private ObjectOutputStream out;
    private ObjectInputStream in;
    private static final String[] invalidWords = new String[] { "LOGIN_READER", "LOGIN_LIBRARY", "SUCCESS", "REQUEST_THREADS", "CREATE_USER",
            "SEARCH_USER", "SEND_FRIEND_REQUEST", "REMOVE_FRIEND", "BLOCK_USER", "UNBLOCK_USER", "SEND_MESSAGE",
            "DELETE_MESSAGE", "SAVE" };

    public Client(Socket socket) {
        try {
            out = new ObjectOutputStream(socket.getOutputStream());
            in = new ObjectInputStream(socket.getInputStream());
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public boolean loginReader(String username, String password) {
        try {
            out.writeObject("LOGIN_READER");
            out.writeObject(username);
            out.writeObject(password);
            out.flush();
            String response = (String) in.readObject();
            if (response.equals("SUCCESS")) {
                this.username = username;
                this.password = password;
                return true;
            }
            return false;
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
    }

    public boolean loginLibrary(String library, String password) {
        try {
            out.writeObject("LOGIN_LIBRARY");
            out.writeObject(library);
            out.writeObject(password);
            out.flush();
            String response = (String) in.readObject();
            if (response.equals("SUCCESS")) {
                this.username = library;
                this.password = password;
                return true;
            }
            return false;
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
    }

    public void endStream() {
        try {
            out.writeObject("SAVE");
            out.writeObject(null);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public boolean createReader(String username, String password, Library library) {
        try {
            out.writeObject("CREATE_READER");
            out.writeObject(username);
            out.writeObject(password);
            out.writeObject(library);
            out.flush();
            String response = (String) in.readObject();
            return response.equals("SUCCESS");
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
    }

    public boolean createLibrary(String name, String password) {
        try {
            out.writeObject("CREATE_LIBRARY");
            out.writeObject(name);
            out.writeObject(password);
            out.flush();
            String response = (String) in.readObject();
            return response.equals("SUCCESS");
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
    }

    public boolean addBookToLibrary(String title, String author, String genre, String synopsis) {
        try {
            out.writeObject("ADD_BOOK_TO_LIBRARY");
            out.writeObject(title);
            out.writeObject(author);
            out.writeObject(genre);
            out.writeObject(synopsis);
            out.flush();
            String response = (String) in.readObject();
            return response.equals("SUCCESS");
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
    }

    public boolean removeBookFromLibrary(Book book) {
        try {
            out.writeObject("REMOVE_BOOK_FROM_LIBRARY");
            out.writeObject(book);
            out.flush();
            String response = (String) in.readObject();
            return response.equals("SUCCESS");
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
    }

    public ArrayList<Library> retrieveLibraries() {
        try {
            out.writeObject("RETRIEVE_LIBRARIES");
            ArrayList<Library> libraries = (ArrayList<Library>) in.readObject();
            return libraries;
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }

    public ArrayList<Reader> retrieveReadersByLibrary() {
        try {
            out.writeObject("RETRIEVE_READERS_BY_LIBRARY");
            ArrayList<Reader> readers = (ArrayList<Reader>) in.readObject();
            return readers;
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }

    public ArrayList<Book> retrieveBooksByLibrary() {
        try {
            out.writeObject("RETRIEVE_BOOKS_BY_LIBRARY");
            ArrayList<Book> books = (ArrayList<Book>) in.readObject();
            return books;
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }

    public ArrayList<Book> retrieveBooksByReader(Reader reader) {
        try {
            out.writeObject("RETRIEVE_BOOKS_BY_READER");
            out.writeObject(reader);
            ArrayList<Book> books = (ArrayList<Book>) in.readObject();
            return books;
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }

    public ArrayList<Book> retrieveBooksByReader() {
        try {
            out.writeObject("RETRIEVE_BOOKS_BY_READER");
            ArrayList<Book> books = (ArrayList<Book>) in.readObject();
            return books;
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }

    public Reader retrieveReaderOfBook(Book book) {
        try {
            out.writeObject("RETRIEVE_READER_OF_BOOK");
            out.writeObject(book);
            Reader reader = (Reader) in.readObject();
            return reader;
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }

    public boolean checkoutBook(Book book) {
        try {
            out.writeObject("CHECKOUT_BOOK");
            out.writeObject(book);
            out.flush();
            String response = (String) in.readObject();
            return response.equals("SUCCESS");
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
    }

    public boolean returnBook(Book book) {
        try {
            out.writeObject("RETURN_BOOK");
            out.writeObject(book);
            out.flush();
            String response = (String) in.readObject();
            return response.equals("SUCCESS");
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
    }
}
