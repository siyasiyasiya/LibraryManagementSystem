package network;

import database.Database;
import model.Book;
import model.Library;
import model.Reader;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.Socket;
import java.time.Clock;
import java.util.ArrayList;

public class ClientHandler implements Runnable {

    private Socket socket;
    private Database database;
    private Reader reader;
    private Library library;
    private ObjectOutputStream out;
    private ObjectInputStream in;

    public ClientHandler(Socket socket, Database database) {
        this.socket = socket;
        this.database = database;
        try {
            out = new ObjectOutputStream(socket.getOutputStream());
            in = new ObjectInputStream(socket.getInputStream());
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void loginReader() {
        try {
            String username = (String) in.readObject();
            String password = (String) in.readObject();
            //System.out.println(username);
            //System.out.println(password);
            Reader r = database.retrieveReader(username);
            if (r == null) {
                out.writeObject("FAILURE");
            } else if (r.getPassword().equals(password)) {
                this.reader = r;
                this.library = database.retrieveLibrary(r.getLibrary());
                out.writeObject("SUCCESS");
            } else {
                out.writeObject("FAILURE");
            }
            out.flush();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void loginLibrary() {
        try {
            String library = (String) in.readObject();
            String password = (String) in.readObject();
            //System.out.println(library);
            //System.out.println(password);
            Library l = database.retrieveLibrary(library);
            if (l == null) {
                out.writeObject("FAILURE");
            } else if (l.getPassword().equals(password)) {
                this.library = l;
                out.writeObject("SUCCESS");
            } else {
                out.writeObject("FAILURE");
            }
            out.flush();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void createReader() {
        try {
            String username = (String) in.readObject();
            String password = (String) in.readObject();
            Library lib = (Library) in.readObject();
            if (database.retrieveReader(username) != null) {
                out.writeObject("FAILURE");
                out.flush();
                return;
            }
            Reader r = new Reader(username, password, lib.getLibraryId());
            reader = r;
            library = lib;
            database.addReader(r);
            out.writeObject("SUCCESS");
            out.flush();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void createLibrary() {
        try {
            String name = (String) in.readObject();
            String password = (String) in.readObject();
            if (database.retrieveLibrary(name) != null) {
                out.writeObject("FAILURE");
                out.flush();
                return;
            }
            Library l = new Library(name, password);
            library = l;
            database.addLibrary(l);
            out.writeObject("SUCCESS");
            out.flush();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void addBookToLibrary() {
        try {
            String title = (String) in.readObject();
            String author = (String) in.readObject();
            String genre = (String) in.readObject();
            String synopsis = (String) in.readObject();

            Book b = database.retrieveBook(title);

            if (b != null && b.getAuthor().equals(author) && b.getLibrary().equals(library.getLibraryId())) {
                out.writeObject("FAILURE");
            } else {
                Book newB = new Book(title, author, genre, synopsis, library.getLibraryId());
                library.addBook(newB.getBookID());
                database.addBook(newB);
                out.writeObject("SUCCESS");
            }

            out.flush();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void removeBookFromLibrary() {
        try {
            Book book = (Book) in.readObject();

            if (library.getBooks().contains(book.getBookID())) {
                library.removeBook(book.getBookID());
                database.removeBook(book);
                out.writeObject("SUCCESS");
            } else {
                out.writeObject("FAILURE");
            }

            out.flush();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void retrieveLibraries() {
        try {
            ArrayList<Library> libraries = database.getLibraries();
            Clock systemClock = Clock.systemDefaultZone();
            System.out.println(systemClock.instant());
            System.out.println("Sending updated libraries to client: " + libraries);
            out.reset();
            out.writeObject(libraries);
            out.flush();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void retrieveReadersByLibrary() {
        try {
            ArrayList<Reader> readers = database.getReadersInLibrary(library);
            out.writeObject(readers);
            out.flush();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void retrieveBooksByLibrary() {
        try {
            ArrayList<Book> books = database.getBooksInLibrary(library);
            out.writeObject(books);
            out.flush();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void retrieveBooksByReader() {
        try {
            Reader tempReader;
            if (reader != null) {
                tempReader = reader;
            } else {
                 tempReader = (Reader) in.readObject();
            }

            ArrayList<Book> books = database.getBooksOfReader(tempReader);
            out.writeObject(books);
            out.flush();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void retrieveReaderOfBook() {
        try {
            Book book = (Book) in.readObject();
            Reader reader = database.retrieveReader(book.getReader());
            out.writeObject(reader);
            out.flush();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void checkoutBook() {
        try {
            Book book = (Book) in.readObject();

            if (book.isAvailable() && library.getBooks().contains(book.getBookID())) {
                reader.addBook(book.getBookID());
                book.setReader(reader.getReaderId());
                out.writeObject("SUCCESS");
            } else {
                out.writeObject("FAILURE");
            }

            out.flush();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void returnBook() {
        try {
            Book book = (Book) in.readObject();

            if (book.getReader().equals(reader.getReaderId())) {
                reader.removeBook(book.getBookID());
                book.setReader(null);
                out.writeObject("SUCCESS");
            } else {
                out.writeObject("FAILURE");
            }

            out.flush();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Override
    public void run() {
        try {
            while (true) {
                String command = (String) in.readObject();

                if (command == null) {
                    System.out.println("Client disconnected");
                    break;
                }

                switch (command) {
                    case "LOGIN_READER":
                        System.out.println("LOGIN READER REQUEST RECEIVED");
                        this.loginReader();
                        break;

                    case "LOGIN_LIBRARY":
                        System.out.println("LOGIN LIBRARY REQUEST RECEIVED");
                        this.loginLibrary();
                        break;

                    case "CREATE_READER":
                        System.out.println("CREATE READER REQUEST RECEIVED");
                        this.createReader();
                        break;

                    case "CREATE_LIBRARY":
                        System.out.println("CREATE LIBRARY REQUEST RECEIVED");
                        this.createLibrary();
                        break;

                    case "ADD_BOOK_TO_LIBRARY":
                        System.out.println("ADD BOOK TO LIBRARY REQUEST RECEIVED");
                        this.addBookToLibrary();
                        break;

                    case "REMOVE_BOOK_FROM_LIBRARY":
                        System.out.println("REMOVE BOOK FROM LIBRARY REQUEST RECEIVED");
                        this.removeBookFromLibrary();
                        break;

                    case "RETRIEVE_LIBRARIES":
                        System.out.println("RETRIEVE LIBRARIES REQUEST RECEIVED");
                        this.retrieveLibraries();
                        break;

                    case "RETRIEVE_READERS_BY_LIBRARY":
                        System.out.println("RETRIEVE READERS BY LIBRARY REQUEST RECEIVED");
                        this.retrieveReadersByLibrary();
                        break;

                    case "RETRIEVE_BOOKS_BY_LIBRARY":
                        System.out.println("RETRIEVE BOOKS BY LIBRARY REQUEST RECEIVED");
                        this.retrieveBooksByLibrary();
                        break;

                    case "RETRIEVE_BOOKS_BY_READER":
                        System.out.println("RETRIEVE BOOKS BY READER REQUEST RECEIVED");
                        this.retrieveBooksByReader();
                        break;

                    case "RETRIEVE_READER_OF_BOOK":
                        System.out.println("RETRIEVE READER OF BOOK REQUEST RECEIVED");
                        this.retrieveReaderOfBook();
                        break;

                    case "CHECKOUT_BOOK":
                        System.out.println("CHECKOUT BOOK REQUEST RECEIVED");
                        this.checkoutBook();
                        break;

                    case "RETURN_BOOK":
                        System.out.println("RETURN BOOK REQUEST RECEIVED");
                        this.returnBook();
                        break;

                    case "SAVE":
                        System.out.println("SAVE REQUEST RECEIVED");
                        database.writeBooks();
                        database.writeReader();
                        database.writeLibraries();
                        break;

                    default:
                        System.out.println("UNKNOWN COMMAND RECEIVED: " + command);
                        out.writeObject("INVALID_COMMAND");
                        out.flush();
                        break;
                }
            }
        } catch (IOException e) {
            System.out.println("Connection error: " + e.getMessage());
            e.printStackTrace();
        } catch (ClassNotFoundException e) {
            System.out.println("Class not found error: " + e.getMessage());
            e.printStackTrace();
        } finally {
            try {
                if (socket != null && !socket.isClosed()) {
                    socket.close();
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
            System.out.println("ClientHandler thread terminated");
        }
    }
}
