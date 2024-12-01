package database;

import model.Book;
import model.Library;
import model.Reader;

import java.io.*;
import java.util.ArrayList;
import java.util.UUID;

public class Database {
    private final Object LIBRARYLOCK = new Object();
    private final Object BOOKLOCK = new Object();
    private final Object READERLOCK = new Object();

    private final String LIBRARYFILE = "data/allLibraries.txt";
    private final String BOOKFILE = "data/allBooks.txt";
    private final String READERFILE = "data/allReaders.txt";

    private ArrayList<Library> libraries;
    private ArrayList<Book> books;
    private ArrayList<Reader> readers;

    public Database() {
        libraries = new ArrayList<>();
        books = new ArrayList<>();
        readers = new ArrayList<>();
        if (new File(LIBRARYFILE).exists()) {
            this.readLibraries();
        }
        if (new File(BOOKFILE).exists()) {
            this.readBooks();
        }
        if (new File(READERFILE).exists()) {
            this.readReaders();
        }
    }

    public void readLibraries() {
        libraries.clear();
        synchronized (LIBRARYLOCK) {
            try (ObjectInputStream o = new ObjectInputStream(new FileInputStream(new File(LIBRARYFILE)))) {

                Object obj = o.readObject();

                // running through allUsers and reading objects into array
                while (obj != null) {
                    Library u = (Library) obj;
                    libraries.add(u);
                    obj = o.readObject();
                }
            } catch (EOFException e) {
                return;
            } catch (FileNotFoundException e) {
                System.out.println("File not found");
            } catch (IOException e) {
                e.printStackTrace();
                System.out.println("Error initializing stream");
            } catch (ClassNotFoundException e) {
                e.printStackTrace();
            }
        }
    }

    public boolean addLibrary(Library library) {
        synchronized (LIBRARYLOCK) {
            boolean exists = false;

            for (int i = 0; i < libraries.size(); i++) {
                if (libraries.get(i).equals(library)) {
                    exists = true;
                    break;
                }
            }

            if (!exists) {
                libraries.add(library);
                return true;
            }

            return false;
        }
    }

    public boolean modifyLibrary(Library library) {
        synchronized (LIBRARYLOCK) {
            for (int i = 0; i < libraries.size(); i++) {
                if (libraries.get(i).equals(library)) {
                    libraries.set(i, library);
                    return true;
                }
            }

            return false;
        }
    }

    public boolean removeLibrary(Library library) {
        synchronized (LIBRARYLOCK) {
            for (int i = 0; i < libraries.size(); i++) {
                if (libraries.get(i).equals(library)) {
                    libraries.remove(i);
                    return true;
                }
            }

            return false;
        }
    }

    public void writeLibraries() {
        synchronized (LIBRARYLOCK) {
            try (ObjectOutputStream o = new ObjectOutputStream(new FileOutputStream(new File(LIBRARYFILE)))) {

                for (Library library : libraries) {
                    o.writeObject(library);
                }

            } catch (FileNotFoundException e) {
                System.out.println("File not found");
            } catch (IOException e) {
                System.out.println("Error initializing stream");
            }
        }
    }

    public ArrayList<Library> getLibraries() {
        synchronized (LIBRARYLOCK) {
            return libraries;
        }
    }

    public Library retrieveLibrary(UUID libraryID) {
        synchronized (LIBRARYLOCK) {
            for (Library library : libraries) {
                if (library.getLibraryId().equals(libraryID)) {
                    return library;
                }
            }
            return null;
        }
    }

    public Library retrieveLibrary(String libraryName) {
        synchronized (LIBRARYLOCK) {
            for (Library library : libraries) {
                if (library.getName().equals(libraryName)) {
                    return library;
                }
            }
            return null;
        }
    }

    public void readBooks() {
        books.clear();
        synchronized (BOOKLOCK) {
            try (ObjectInputStream o = new ObjectInputStream(new FileInputStream(new File(BOOKFILE)))) {

                Object obj = o.readObject();

                // running through allUsers and reading objects into array
                while (obj != null) {
                    Book u = (Book) obj;
                    books.add(u);
                    obj = o.readObject();
                }
            } catch (EOFException e) {
                return;
            } catch (FileNotFoundException e) {
                System.out.println("File not found");
            } catch (IOException e) {
                e.printStackTrace();
                System.out.println("Error initializing stream");
            } catch (ClassNotFoundException e) {
                e.printStackTrace();
            }
        }
    }

    public boolean addBook(Book book) {
        synchronized (BOOKLOCK) {
            boolean exists = false;

            for (int i = 0; i < books.size(); i++) {
                if (books.get(i).equals(book)) {
                    exists = true;
                    break;
                }
            }

            if (!exists) {
                books.add(book);
                return true;
            }

            return false;
        }
    }

    public boolean modifyBook(Book book) {
        synchronized (BOOKLOCK) {
            for (int i = 0; i < books.size(); i++) {
                if (books.get(i).equals(book)) {
                    books.set(i, book);
                    return true;
                }
            }

            return false;
        }
    }

    public boolean removeBook(Book book) {
        synchronized (BOOKLOCK) {
            for (int i = 0; i < books.size(); i++) {
                if (books.get(i).equals(book)) {
                    books.remove(i);
                    return true;
                }
            }

            return false;
        }
    }

    public void writeBooks() {
        synchronized (BOOKLOCK) {
            try (ObjectOutputStream o = new ObjectOutputStream(new FileOutputStream(new File(BOOKFILE)))) {

                for (Book book : books) {
                    o.writeObject(book);
                }

            } catch (FileNotFoundException e) {
                System.out.println("File not found");
            } catch (IOException e) {
                System.out.println("Error initializing stream");
            }
        }
    }

    public ArrayList<Book> getBooks() {
        synchronized (BOOKLOCK) {
            return books;
        }
    }

    public Book retrieveBook(UUID bookID) {
        synchronized (BOOKLOCK) {
            for (Book book : books) {
                if (book.getBookID().equals(bookID)) {
                    return book;
                }
            }

            return null;
        }
    }

    public Book retrieveBook(String title) {
        synchronized (BOOKLOCK) {
            for (Book book : books) {
                if (book.getTitle().equals(title)) {
                    return book;
                }
            }

            return null;
        }
    }

    public ArrayList<Book> getBooksInLibrary(Library library) {
        synchronized (BOOKLOCK) {
            ArrayList<Book> libraryBooks = new ArrayList<>();
            for (Book book : books) {
                if (book.getLibrary().equals(library.getLibraryId())) {
                    libraryBooks.add(book);
                }
            }

            return libraryBooks;
        }
    }

    public ArrayList<Book> getBooksOfReader(Reader reader) {
        synchronized (BOOKLOCK) {
            ArrayList<Book> readerBooks = new ArrayList<>();
            for (Book book : books) {
                if (book.getReader().equals(reader.getReaderId())) {
                    readerBooks.add(book);
                }
            }

            return readerBooks;
        }
    }

    public void readReaders() {
        readers.clear();
        synchronized (READERLOCK) {
            try (ObjectInputStream o = new ObjectInputStream(new FileInputStream(new File(READERFILE)))) {

                Object obj = o.readObject();

                // running through allUsers and reading objects into array
                while (obj != null) {
                    Reader u = (Reader) obj;
                    readers.add(u);
                    obj = o.readObject();
                }
            } catch (EOFException e) {
                return;
            } catch (FileNotFoundException e) {
                System.out.println("File not found");
            } catch (IOException e) {
                e.printStackTrace();
                System.out.println("Error initializing stream");
            } catch (ClassNotFoundException e) {
                e.printStackTrace();
            }
        }
    }

    public boolean addReader(Reader reader) {
        synchronized (READERLOCK) {
            boolean exists = false;

            for (int i = 0; i < readers.size(); i++) {
                if (readers.get(i).equals(reader)) {
                    exists = true;
                    break;
                }
            }

            if (!exists) {
                readers.add(reader);
                return true;
            }

            return false;
        }
    }

    public boolean modifyReader(Reader reader) {
        synchronized (READERLOCK) {
            for (int i = 0; i < readers.size(); i++) {
                if (readers.get(i).equals(reader)) {
                    readers.set(i, reader);
                    return true;
                }
            }

            return false;
        }
    }

    public boolean removeReader(Reader reader) {
        synchronized (READERLOCK) {
            for (int i = 0; i < readers.size(); i++) {
                if (readers.get(i).equals(reader)) {
                    readers.remove(i);
                    return true;
                }
            }

            return false;
        }
    }

    public void writeReader() {
        synchronized (READERLOCK) {
            try (ObjectOutputStream o = new ObjectOutputStream(new FileOutputStream(new File(READERFILE)))) {

                for (Reader reader : readers) {
                    o.writeObject(reader);
                }

            } catch (FileNotFoundException e) {
                System.out.println("File not found");
            } catch (IOException e) {
                System.out.println("Error initializing stream");
            }
        }
    }

    public ArrayList<Reader> getReaders() {
        synchronized (READERLOCK) {
            return readers;
        }
    }

    public Reader retrieveReader(UUID readerID) {
        synchronized (READERLOCK) {
            for (Reader reader : readers) {
                if (reader.getReaderId().equals(readerID)) {
                    return reader;
                }
            }
            return null;
        }
    }

    public Reader retrieveReader(String username) {
        synchronized (READERLOCK) {
            for (Reader reader : readers) {
                if (reader.getUsername().equals(username)) {
                    return reader;
                }
            }
            return null;
        }
    }

    public ArrayList<Reader> getReadersInLibrary(Library library) {
        synchronized (READERLOCK) {
            ArrayList<Reader> libraryReaders = new ArrayList<>();

            for (Reader reader : readers) {
                if (reader.getLibrary().equals(library.getLibraryId())) {
                    libraryReaders.add(reader);
                }
            }

            return libraryReaders;
        }
    }
}
