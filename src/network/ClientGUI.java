package network;

import model.Book;
import model.Library;
import model.Reader;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.IOException;
import java.net.Socket;
import java.util.ArrayList;

public class ClientGUI extends JFrame implements Runnable {

    private Client client;
    private JTextField loginUsernameField, createUsernameField, libraryNameField, bookTitleField, bookAuthorField, bookGenreField, bookSynopsisField, searchLibraryBooksField, searchUserBooksField;
    private JPasswordField loginPasswordField, createPasswordField;
    private JTextArea outputArea;
    private JPanel mainPanel, landingPanel, landingUserPanel, landingLibraryPanel, readerTabsPanel, libraryTabsPanel;
    private CardLayout cardLayout;
    private JTabbedPane landingUserTabs, landingLibraryTabs, readerMainTabs, libraryMainTabs;
    private JComboBox<Library> loginLibraryDropdown, createLibraryDropdown;
    private DefaultListModel<String> libraryListModel, userListModel;
    private JList<String> libraryBookList, userBookList;
    private ArrayList<Book> userBooks, libraryBooks;

    public ClientGUI(String host, int port) {
        try {
            client = new Client(new Socket(host, port));
        } catch (IOException e) {
            showError("Unable to connect to server: " + e.getMessage());
            return;
        }

        setTitle("Library Management System");
        setSize(600, 500);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);

        cardLayout = new CardLayout();
        mainPanel = new JPanel(cardLayout);

        landingPanel = createLandingPanel();

        //readerTabsPanel = createReaderTabsPanel();
        //libraryTabsPanel = createLibraryTabsPanel();

        mainPanel.add(landingPanel, "Landing Page");
        //mainPanel.add(readerTabsPanel, "Reader Tabs");
        //mainPanel.add(libraryTabsPanel, "Library Tabs");

        add(mainPanel);
    }

    private JPanel createLandingPanel() {
        // Use GridLayout with more spacing for aesthetics
        JPanel panel = new JPanel(new BorderLayout());
        panel.setBorder(BorderFactory.createEmptyBorder(50, 50, 50, 50));

        // Create the label
        JLabel whoLabel = new JLabel("I am a ...", SwingConstants.CENTER);
        whoLabel.setFont(new Font("Arial", Font.BOLD, 16));

        JPanel buttonPanel = new JPanel(new GridLayout(2, 1, 10, 10));
        buttonPanel.setBorder(BorderFactory.createEmptyBorder(20, 50, 50, 50));

        JButton readerButton = new JButton("Reader");
        readerButton.setFont(new Font("Arial", Font.PLAIN, 20));
        readerButton.setPreferredSize(new Dimension(100, 40));
        readerButton.setFocusPainted(false);
        readerButton.addActionListener(e -> enableLoginUserTabs());

        JButton libraryButton = new JButton("Library");
        libraryButton.setFont(new Font("Arial", Font.PLAIN, 20));
        libraryButton.setPreferredSize(new Dimension(100, 40));
        libraryButton.setFocusPainted(false);
        libraryButton.addActionListener(e -> enableLoginLibraryTabs());

        buttonPanel.add(readerButton);
        buttonPanel.add(libraryButton);

        panel.add(whoLabel, BorderLayout.NORTH);
        panel.add(buttonPanel, BorderLayout.CENTER);

        return panel;
    }

    private JPanel createLandingUserPanel() {
        landingUserTabs = new JTabbedPane();

        landingUserTabs.add("Login", createLoginPanel(true));
        landingUserTabs.add("Sign Up", createSignUpPanel(true));

        landingUserTabs.setEnabled(false);

        JPanel panel = new JPanel(new BorderLayout());
        panel.add(landingUserTabs, BorderLayout.CENTER);

        return panel;
    }

    private JPanel createLandingLibraryPanel() {
        landingLibraryTabs = new JTabbedPane();

        landingLibraryTabs.add("Login", createLoginPanel(false));
        landingLibraryTabs.add("Sign Up", createSignUpPanel(false));

        landingLibraryTabs.setEnabled(false);

        JPanel panel = new JPanel(new BorderLayout());
        panel.add(landingLibraryTabs, BorderLayout.CENTER);

        return panel;
    }

    private JPanel createLoginPanel(Boolean isReader) {
        JPanel panel = new JPanel(new GridLayout(3, 1, 0, 20));
        panel.setBorder(BorderFactory.createEmptyBorder(60, 60, 20, 60));

        JPanel usernamePanel = new JPanel(new BorderLayout());
        usernamePanel.setBorder(BorderFactory.createEmptyBorder(5, 5, 30, 5));

        JLabel usernameLabel;

        if (isReader) {
            usernameLabel = new JLabel("Username:");
            loginUsernameField = new JTextField();
            loginUsernameField.setFont(new Font("Arial", Font.PLAIN, 14));
            loginUsernameField.setPreferredSize(new Dimension(100, 20));
            usernamePanel.add(loginUsernameField, BorderLayout.CENTER);
        } else {
            usernameLabel = new JLabel("Library:");
            loginLibraryDropdown = new JComboBox<>(retrieveLibraries().toArray(new Library[0]));
            loginLibraryDropdown.setFont(new Font("Arial", Font.PLAIN, 14));
            loginLibraryDropdown.setPreferredSize(new Dimension(100, 20));

            loginLibraryDropdown.insertItemAt(null, 0);
            loginLibraryDropdown.setSelectedIndex(0);

            usernamePanel.add(loginLibraryDropdown, BorderLayout.CENTER);
        }

        usernameLabel.setFont(new Font("Arial", Font.PLAIN, 14));
        usernamePanel.add(usernameLabel, BorderLayout.NORTH);

        JPanel passwordPanel = new JPanel(new BorderLayout());
        passwordPanel.setBorder(BorderFactory.createEmptyBorder(5, 5, 30, 5));

        JLabel passwordLabel = new JLabel("Password:");
        passwordLabel.setFont(new Font("Arial", Font.PLAIN, 14));
        loginPasswordField = new JPasswordField();
        loginPasswordField.setFont(new Font("Arial", Font.PLAIN, 14));
        loginPasswordField.setPreferredSize(new Dimension(100, 20));
        loginPasswordField.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                String text = new String(loginPasswordField.getPassword());
                System.out.println("Text entered: " + text);
            }
        });
        passwordPanel.add(passwordLabel, BorderLayout.NORTH);
        passwordPanel.add(loginPasswordField, BorderLayout.CENTER);

        JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.CENTER));
        JButton loginButton = new JButton("Login");
        loginButton.setFont(new Font("Arial", Font.BOLD, 14));
        loginButton.setPreferredSize(new Dimension(150, 50));
        loginButton.addActionListener(e -> handleLogin(isReader));
        buttonPanel.add(loginButton);

        panel.add(usernamePanel);
        panel.add(passwordPanel);
        panel.add(buttonPanel);

        return panel;
    }


    private JPanel createSignUpPanel(Boolean isReader) {
        JPanel panel = new JPanel(new GridLayout(4, 1, 0, 20));
        panel.setBorder(BorderFactory.createEmptyBorder(30, 60, 10, 60));

        JPanel usernamePanel = new JPanel(new BorderLayout());
        usernamePanel.setBorder(BorderFactory.createEmptyBorder(5, 5, 10, 5));

        JLabel usernameLabel;

        if (isReader) {
            usernameLabel = new JLabel("Username:");
        } else {
            usernameLabel = new JLabel("Library Name:");
        }

        usernameLabel.setFont(new Font("Arial", Font.PLAIN, 15));
        createUsernameField = new JTextField();
        createUsernameField.setFont(new Font("Arial", Font.PLAIN, 15));
        createUsernameField.setPreferredSize(new Dimension(100, 20));

        usernamePanel.add(usernameLabel, BorderLayout.NORTH);
        usernamePanel.add(createUsernameField, BorderLayout.CENTER);

        JPanel passwordPanel = new JPanel(new BorderLayout());
        passwordPanel.setBorder(BorderFactory.createEmptyBorder(5, 5, 10, 5));

        JLabel passwordLabel = new JLabel("Password:");
        passwordLabel.setFont(new Font("Arial", Font.PLAIN, 15));
        createPasswordField = new JPasswordField();
        createPasswordField.setFont(new Font("Arial", Font.PLAIN, 15));
        createPasswordField.setPreferredSize(new Dimension(100, 20));

        passwordPanel.add(passwordLabel, BorderLayout.NORTH);
        passwordPanel.add(createPasswordField, BorderLayout.CENTER);

        JPanel dropdownPanel = new JPanel(new BorderLayout());
        dropdownPanel.setBorder(BorderFactory.createEmptyBorder(5, 5, 10, 5));

        JLabel dropdownLabel = new JLabel("Library:");
        dropdownLabel.setFont(new Font("Arial", Font.PLAIN, 15));
        createLibraryDropdown = new JComboBox<>(retrieveLibraries().toArray(new Library[0]));
        createLibraryDropdown.setFont(new Font("Arial", Font.PLAIN, 15));
        createLibraryDropdown.setPreferredSize(new Dimension(100, 20));
        createLibraryDropdown.setBorder(BorderFactory.createEmptyBorder(15, 0, 10, 0));

        createLibraryDropdown.insertItemAt(null, 0);
        createLibraryDropdown.setSelectedIndex(0);

        dropdownPanel.add(dropdownLabel, BorderLayout.NORTH);
        dropdownPanel.add(createLibraryDropdown, BorderLayout.CENTER);

        JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.CENTER));
        JButton signUpBotton = new JButton("Sign Up");
        signUpBotton.setFont(new Font("Arial", Font.BOLD, 15));
        signUpBotton.setPreferredSize(new Dimension(150, 50));
        signUpBotton.addActionListener(e -> handleCreateAccount(isReader));
        buttonPanel.add(signUpBotton);

        panel.add(usernamePanel);
        panel.add(passwordPanel);
        if (isReader) {
            panel.add(dropdownPanel);
        }
        panel.add(buttonPanel);

        return panel;
    }

    private JPanel createReaderTabsPanel() {
        readerMainTabs = new JTabbedPane();

        readerMainTabs.add("Library Books", createLibraryBooksPanel(true));
        readerMainTabs.add("My Books", createUserBooksPanel());

        // Initially disable the tabs
        readerMainTabs.setEnabled(false);

        JPanel panel = new JPanel(new BorderLayout());
        panel.add(readerMainTabs, BorderLayout.CENTER);
        //panel.add(createOutputPanel(), BorderLayout.SOUTH);

        return panel;
    }

    private JPanel createLibraryTabsPanel() {
        libraryMainTabs = new JTabbedPane();

        libraryMainTabs.add("Library Books", createLibraryBooksPanel(false));
        //libraryMainTabs.add("Library Readers", createUserBooksPanel());
        libraryMainTabs.add("Add Book", createAddBookPanel());

        libraryMainTabs.setEnabled(false);

        JPanel panel = new JPanel(new BorderLayout());
        panel.add(libraryMainTabs, BorderLayout.CENTER);

        return panel;
    }

    private JPanel createLibraryBooksPanel(boolean isReader) {
        JPanel panel = new JPanel(new BorderLayout());
        panel.setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));

        int which;
        if (isReader) {
            which = 0;
        } else {
            which = 2;
        }

        libraryBooks = client.retrieveBooksByLibrary();
        libraryListModel = new DefaultListModel<>();

        if (libraryBooks != null && !libraryBooks.isEmpty()) {
            libraryBooks.forEach(book -> libraryListModel.addElement(book.toString()));
        } else {
            libraryListModel.addElement("The library has no books");
        }

        searchLibraryBooksField = new JTextField();

        searchLibraryBooksField.setFont(new Font("Arial", Font.PLAIN, 14));
        searchLibraryBooksField.setPreferredSize(new Dimension(200, 30));
        searchLibraryBooksField.setForeground(Color.BLACK);
        searchLibraryBooksField.setBackground(new Color(245, 245, 245));  // Light gray background
        searchLibraryBooksField.setBorder(BorderFactory.createLineBorder(Color.GRAY, 1));
        searchLibraryBooksField.setMargin(new Insets(5, 20, 5, 20));

        searchLibraryBooksField.addCaretListener(e -> {
            if (libraryBooks != null && !libraryBooks.isEmpty() && !searchLibraryBooksField.getText().isEmpty()) {
                filterList(searchLibraryBooksField.getText(), libraryListModel, libraryBooks);
            }
        });

        libraryBookList = new JList<>(libraryListModel);
        libraryBookList.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);

        libraryBookList.setFont(new Font("Arial", Font.PLAIN, 14));
        libraryBookList.setBackground(Color.WHITE);
        libraryBookList.setForeground(Color.DARK_GRAY);
        libraryBookList.setSelectionBackground(new Color(30, 144, 255));
        libraryBookList.setSelectionForeground(Color.WHITE);

        libraryBookList.addListSelectionListener(e -> {
            if (!e.getValueIsAdjusting()) {
                int index = libraryBookList.getSelectedIndex();
                if (index != -1 && libraryBooks != null && libraryBooks.size() > index) {
                    handleBookSelection(index, libraryBooks, which);
                }
            }
        });

        JScrollPane scrollPane = new JScrollPane(libraryBookList);
        scrollPane.setPreferredSize(new Dimension(300, 200));

        panel.add(searchLibraryBooksField, BorderLayout.NORTH);
        panel.add(scrollPane, BorderLayout.CENTER);

        return panel;
    }

    private JPanel createUserBooksPanel() {
        JPanel panel = new JPanel(new BorderLayout());
        panel.setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));

        userBooks = client.retrieveBooksByReader();
        userListModel = new DefaultListModel<>();

        if (userBooks != null && !userBooks.isEmpty()) {
            userBooks.forEach(book -> libraryListModel.addElement(book.toString()));
        } else {
            userListModel.addElement("You have no books checked out.");
        }

        searchUserBooksField = new JTextField();

        searchUserBooksField.setFont(new Font("Arial", Font.PLAIN, 14));
        searchUserBooksField.setPreferredSize(new Dimension(200, 30));
        searchUserBooksField.setForeground(Color.BLACK);
        searchUserBooksField.setBackground(new Color(245, 245, 245));  // Light gray background
        searchUserBooksField.setBorder(BorderFactory.createLineBorder(Color.GRAY, 1));
        searchUserBooksField.setMargin(new Insets(5, 10, 5, 10));

        searchUserBooksField.addCaretListener(e -> {
            if (userBooks != null && !userBooks.isEmpty() && !searchUserBooksField.getText().isEmpty()) {
                filterList(searchUserBooksField.getText(), userListModel, userBooks);
            }
        });

        userBookList = new JList<>(userListModel);
        userBookList.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);

        userBookList.setFont(new Font("Arial", Font.PLAIN, 14));
        userBookList.setBackground(Color.WHITE);
        userBookList.setForeground(Color.DARK_GRAY);
        userBookList.setSelectionBackground(new Color(30, 144, 255));
        userBookList.setSelectionForeground(Color.WHITE);

        userBookList.addListSelectionListener(e -> {
            if (!e.getValueIsAdjusting()) {
                int index = userBookList.getSelectedIndex();
                if (index != -1 && userBooks != null && userBooks.size() > index) {
                    handleBookSelection(index, userBooks, 1);
                }
            }
        });

        JScrollPane scrollPane = new JScrollPane(userBookList);
        scrollPane.setPreferredSize(new Dimension(300, 200));

        panel.add(searchUserBooksField, BorderLayout.NORTH);
        panel.add(scrollPane, BorderLayout.CENTER);

        return panel;
    }

    private void filterList(String query, DefaultListModel<String> listModel, ArrayList<Book> books) {
        listModel.clear();

        if (query.isEmpty()) {
            books.forEach(book -> listModel.addElement(book.toString()));
        } else {
            for (Book book : books) {
                if (book.toString().toLowerCase().contains(query.toLowerCase())) {
                    listModel.addElement(book.toString());
                }
            }
        }

        if (listModel.isEmpty()) {
            listModel.addElement("No books found matching the search criteria.");
        }
    }

    private void handleBookSelection(int index, ArrayList<Book> books, int which) {
        Book book = books.get(index);

        String bookDetails = "<html><strong>" + book.getTitle() + "</strong><br>";
        String action = "Remove";

        if (which == 0) {
            bookDetails = "<html><strong>" + book.getTitle() + "</strong><br>"
                    + "Author: " + book.getAuthor() + "<br>"
                    + "Genre: " + book.getGenre() + "<br>"
                    + "Synopsis: " + book.getSynopsis() + "<br></html>";
            action = "Check Out";
        } else if (which == 1) {
            action = "Return";
        }

        bookDetails += "</html>";

        if (!book.isAvailable()) {
            if (which == 0) {
                JOptionPane.showMessageDialog(this, "Book is currently checked out by another user.", "Book Unavailable", JOptionPane.INFORMATION_MESSAGE);
            } else if (which == 2) {
                Reader reader = client.retrieveReaderOfBook(book);
                JOptionPane.showMessageDialog(this, "Book is currently checked out by " + reader.getUsername() + ".", "Book Unavailable", JOptionPane.INFORMATION_MESSAGE);
            }
        } else {
            int response = JOptionPane.showConfirmDialog(
                    this,
                    "<html>Do you want to " + action.toLowerCase() + " the following book?<br>" + bookDetails,
                    action + " Book",
                    JOptionPane.YES_NO_OPTION,
                    JOptionPane.QUESTION_MESSAGE
            );

            if (response == JOptionPane.YES_OPTION) {
                if (which == 0) {
                    checkoutBook(book);
                } else if (which == 1) {
                    returnBook(book);
                } else {
                    removeBookFromLibrary(book);
                }
            } else {
                JOptionPane.showMessageDialog(this, "You chose not to " + action.toLowerCase() + " the book.", "Action Cancelled", JOptionPane.INFORMATION_MESSAGE);
            }

            if (which == 1) {
                userBookList.clearSelection();
            } else {
                libraryBookList.clearSelection();
            }
        }
    }

    private JPanel createAddBookPanel() {
        JPanel panel = new JPanel(new BorderLayout());

        JPanel fieldPanel = new JPanel(new GridLayout(4, 2, 10, 10));
        fieldPanel.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));

        JLabel titleLabel = new JLabel("Book Title:");
        titleLabel.setFont(new Font("Arial", Font.BOLD, 14));
        bookTitleField = new JTextField();
        bookTitleField.setFont(new Font("Arial", Font.PLAIN, 14));

        JLabel authorLabel = new JLabel("Author:");
        authorLabel.setFont(new Font("Arial", Font.BOLD, 14));
        bookAuthorField = new JTextField();
        bookAuthorField.setFont(new Font("Arial", Font.PLAIN, 14));

        JLabel genreLabel = new JLabel("Genre:");
        genreLabel.setFont(new Font("Arial", Font.BOLD, 14));
        bookGenreField = new JTextField();
        bookGenreField.setFont(new Font("Arial", Font.PLAIN, 14));

        JLabel synopsisLabel = new JLabel("Synopsis:");
        synopsisLabel.setFont(new Font("Arial", Font.BOLD, 14));
        bookSynopsisField = new JTextField();
        bookSynopsisField.setFont(new Font("Arial", Font.PLAIN, 14));

        JPanel buttonPanel = new JPanel(new BorderLayout());
        JButton addBookButton = new JButton("Add Book to Library");
        addBookButton.setFont(new Font("Arial", Font.BOLD, 14));
        addBookButton.setFocusPainted(false);
        addBookButton.addActionListener(e -> addBookToLibrary());
        buttonPanel.add(addBookButton);

        fieldPanel.add(titleLabel);
        fieldPanel.add(bookTitleField);
        fieldPanel.add(authorLabel);
        fieldPanel.add(bookAuthorField);
        fieldPanel.add(genreLabel);
        fieldPanel.add(bookGenreField);
        fieldPanel.add(synopsisLabel);
        fieldPanel.add(bookSynopsisField);

        panel.add(fieldPanel, BorderLayout.CENTER);
        panel.add(buttonPanel, BorderLayout.SOUTH);

        return panel;
    }

    private JPanel createOutputPanel() {
        JPanel panel = new JPanel(new BorderLayout());
        panel.setBorder(BorderFactory.createTitledBorder("Output"));

        outputArea = new JTextArea();
        outputArea.setEditable(false);

        JScrollPane scrollPane = new JScrollPane(outputArea);
        panel.add(scrollPane, BorderLayout.CENTER);

        return panel;
    }

    private void enableLoginUserTabs() {
        landingUserPanel = createLandingUserPanel();
        mainPanel.add(landingUserPanel, "Landing User Page");
        landingUserTabs.setEnabled(true);
        cardLayout.show(mainPanel, "Landing User Page");
    }

    private void enableLoginLibraryTabs() {
        landingLibraryPanel = createLandingLibraryPanel();
        mainPanel.add(landingLibraryPanel, "Landing Library Page");
        landingLibraryTabs.setEnabled(true);
        cardLayout.show(mainPanel, "Landing Library Page");
    }

    private void enableReaderMainTabs() {
        readerTabsPanel = createReaderTabsPanel();
        mainPanel.add(readerTabsPanel, "Reader Tabs");
        readerMainTabs.setEnabled(true);
        cardLayout.show(mainPanel, "Reader Tabs");
    }

    private void enableLibraryMainTabs() {
        libraryTabsPanel = createLibraryTabsPanel();
        mainPanel.add(libraryTabsPanel, "Library Tabs");
        libraryMainTabs.setEnabled(true);
        cardLayout.show(mainPanel, "Library Tabs");
    }

    private void handleLogin(Boolean isReader) {
        String username;
        if (isReader) {
            username = loginUsernameField.getText().trim();
            if (username.isEmpty()) {
                System.out.println("username: " + loginUsernameField.getText());
                showError("Please enter a username");
                return;
            }
        } else {
            Object obj = loginLibraryDropdown.getSelectedItem();
            if (obj == null) {
                showError("Please select a library");
                return;
            }
            username = obj.toString();
        }

        String password = new String(loginPasswordField.getPassword()).trim();

        if (password.isEmpty()) {
            System.out.println("password: " + password);
            showError("Please enter a password");
            return;
        }

        if (isReader && client.loginReader(username, password)) {
            JOptionPane.showMessageDialog(this, "Reader Login Successful", "Login", JOptionPane.INFORMATION_MESSAGE);
            enableReaderMainTabs();
        } else if (client.loginLibrary(username, password)) {
            JOptionPane.showMessageDialog(this, "Library Login Successful", "Login", JOptionPane.INFORMATION_MESSAGE);
            enableLibraryMainTabs();
        } else {
            showError("Either Library Name/Username or Password is Incorrect");
        }
    }

    private void handleCreateAccount(Boolean isReader){
        String username = createUsernameField.getText();
        if (username.isEmpty()) {
            showError("Please enter a username");
            return;
        }

        String password = new String(createPasswordField.getPassword());
        if (password.isEmpty()) {
            showError("Please enter a password");
            return;
        }

        Library library = null;
        if (isReader) {
            String libraryName = createLibraryDropdown.getSelectedItem().toString();
            if (libraryName == null) {
                showError("Please select a library");
                return;
            }

            for (Library l : retrieveLibraries()) {
                if (l.getName().equals(libraryName)) {
                    library = l;
                    break;
                }
            }
        }

        if (!isReader && client.createLibrary(username, password)) {
            JOptionPane.showMessageDialog(this, "Library Account Created Successfully!", "Account Creation", JOptionPane.INFORMATION_MESSAGE);

            updateLibraryDropdowns();

        } else if (isReader && client.createReader(username, password, library)) {

            JOptionPane.showMessageDialog(this, "Reader Account Created Successfully!", "Account Creation", JOptionPane.INFORMATION_MESSAGE);

        } else {
            showError("Account Creation Failed");
        }
    }

    private void updateLibraryDropdowns() {
        System.out.println("looking to update libraries");
        ArrayList<Library> libraries = this.retrieveLibraries();

        loginLibraryDropdown.removeAllItems();
        loginLibraryDropdown.addItem(null);
        loginLibraryDropdown.setSelectedIndex(0);

        createLibraryDropdown.removeAllItems();
        createLibraryDropdown.addItem(null);
        createLibraryDropdown.setSelectedIndex(0);

        for (Library lib : libraries) {
            loginLibraryDropdown.addItem(lib);
            createLibraryDropdown.addItem(lib);
        }

        loginLibraryDropdown.revalidate();
        loginLibraryDropdown.repaint();
        createLibraryDropdown.revalidate();
        createLibraryDropdown.repaint();
    }

    private void updateLibraryBookList() {
        libraryBooks = client.retrieveBooksByLibrary();
        libraryListModel.clear();

        if (libraryBooks != null && !libraryBooks.isEmpty()) {
            libraryBooks.forEach(book -> libraryListModel.addElement(book.toString()));
        } else {
            libraryListModel.addElement("The library has no books");
        }

        libraryBookList.revalidate();
        libraryBookList.repaint();
    }

    private void updateUserBookList() {
        userBooks = client.retrieveBooksByReader();
        userListModel.clear();

        if (userBooks != null && !userBooks.isEmpty()) {
            userBooks.forEach(book -> libraryListModel.addElement(book.toString()));
        } else {
            userListModel.addElement("You have no books checked out.");
        }

        userBookList.revalidate();
        userBookList.repaint();
    }

    private ArrayList<Library> retrieveLibraries() {
        return client.retrieveLibraries();
    }

    private void addBookToLibrary() {
        String title = bookTitleField.getText();
        String author = bookAuthorField.getText();
        String genre = bookGenreField.getText();
        String synopsis = bookSynopsisField.getText();

        if (client.addBookToLibrary(title, author, genre, synopsis)) {
            JOptionPane.showMessageDialog(this, "Book added to library successfully!", "Book Add", JOptionPane.INFORMATION_MESSAGE);
            updateLibraryBookList();
        } else {
            JOptionPane.showMessageDialog(this, "Book addition failed!", "Book Add", JOptionPane.ERROR_MESSAGE);
        }
    }

    private void removeBookFromLibrary(Book book) {
        if (client.removeBookFromLibrary(book)) {
            JOptionPane.showMessageDialog(this, "Book removed from library successfully!", "Book Remove", JOptionPane.INFORMATION_MESSAGE);
            libraryListModel.removeElement(book.toString());
            libraryBookList.revalidate();
            libraryBookList.repaint();
        } else {
            JOptionPane.showMessageDialog(this, "Book removal failed!", "Book Remove", JOptionPane.ERROR_MESSAGE);
        }
    }

    private void checkoutBook(Book book) {
        if (client.checkoutBook(book)) {
            JOptionPane.showMessageDialog(this, "Book checked out successfully!", "Book Checkout", JOptionPane.INFORMATION_MESSAGE);
            updateUserBookList();
        } else {
            JOptionPane.showMessageDialog(this, "Book checkout failed!", "Book Checkout", JOptionPane.ERROR_MESSAGE);
        }
    }

    private void returnBook(Book book) {
        if (client.returnBook(book)) {
            JOptionPane.showMessageDialog(this, "Book returned successfully!", "Book Return", JOptionPane.INFORMATION_MESSAGE);
            userListModel.removeElement(book.toString());
            userBookList.revalidate();
            userBookList.repaint();
        } else {
            JOptionPane.showMessageDialog(this, "Book return failed!", "Book Return", JOptionPane.ERROR_MESSAGE);
        }
    }

    private void showError(String message) {
        JOptionPane.showMessageDialog(this, message, "Error", JOptionPane.ERROR_MESSAGE);
    }

    @Override
    public void run() {
        SwingUtilities.invokeLater(() -> this.setVisible(true));
    }

    public static void main(String[] args) {
        Thread guiThread = new Thread(new ClientGUI("localhost", 8000));
        guiThread.start();
    }
}
