package network;

import model.Book;
import model.Library;

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
    private JTabbedPane landingUserTabs, landingLibraryTabs, readerMainTabs;
    private JComboBox<Library> loginLibraryDropdown, createLibraryDropdown;
    private DefaultListModel<String> libraryListModel, userListModel;
    private JList<String> libraryBookList, userBookList;

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

        readerMainTabs.add("Library Books", createLibraryBooksPanel());
        readerMainTabs.add("My Books", createUserBooksPanel());

        // Initially disable the tabs
        readerMainTabs.setEnabled(false);

        JPanel panel = new JPanel(new BorderLayout());
        panel.add(readerMainTabs, BorderLayout.CENTER);
        //panel.add(createOutputPanel(), BorderLayout.SOUTH);

        return panel;
    }

    private JPanel createLibraryBooksPanel() {
        JPanel panel = new JPanel(new BorderLayout());
        panel.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));

        ArrayList<Book> libraryBooks = client.retrieveBooksByLibrary();
        libraryListModel = new DefaultListModel<>();

        if (libraryBooks != null && !libraryBooks.isEmpty()) {
            libraryBooks.forEach(book -> libraryListModel.addElement(book.toString()));
        } else {
            libraryListModel.addElement("The library has no books");
        }

        searchLibraryBooksField = new JTextField();
        searchLibraryBooksField.setFont(new Font("Arial", Font.PLAIN, 14));
        searchLibraryBooksField.addCaretListener(e -> {
            if (libraryBooks != null && !libraryBooks.isEmpty() && !searchLibraryBooksField.getText().isEmpty()) {
                filterList(searchLibraryBooksField.getText(), libraryListModel, libraryBooks);
            }
        });

        libraryBookList = new JList<>(libraryListModel);
        libraryBookList.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);

        libraryBookList.addListSelectionListener(e -> {
            if (!e.getValueIsAdjusting()) {
                int index = libraryBookList.getSelectedIndex();
                if (index != -1 && libraryBooks != null && libraryBooks.size() > index) {
                    handleBookSelection(index, libraryBooks, true);
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
        panel.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));

        ArrayList<Book> userBooks = client.retrieveBooksByReader();
        userListModel = new DefaultListModel<>();

        if (userBooks != null && !userBooks.isEmpty()) {
            userBooks.forEach(book -> libraryListModel.addElement(book.toString()));
        } else {
            userListModel.addElement("You have no books checked out.");
        }

        searchUserBooksField = new JTextField();
        searchUserBooksField.setFont(new Font("Arial", Font.PLAIN, 14));
        searchUserBooksField.addCaretListener(e -> {
            if (userBooks != null && !userBooks.isEmpty() && !searchUserBooksField.getText().isEmpty()) {
                filterList(searchUserBooksField.getText(), userListModel, userBooks);
            }
        });

        userBookList = new JList<>(userListModel);
        userBookList.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);

        userBookList.addListSelectionListener(e -> {
            if (!e.getValueIsAdjusting()) {
                int index = userBookList.getSelectedIndex();
                if (index != -1 && userBooks != null && userBooks.size() > index) {
                    handleBookSelection(index, userBooks, false);
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

    private void handleBookSelection(int index, ArrayList<Book> books, boolean checkOut) {
        Book book = books.get(index);

        String bookDetails = "<html><strong>" + book.getTitle() + "</strong><br>";
        String action = "Return";

        if (checkOut) {
            bookDetails = "<html><strong>" + book.getTitle() + "</strong><br>"
                    + "Author: " + book.getAuthor() + "<br>"
                    + "Genre: " + book.getGenre() + "<br>"
                    + "Synopsis: " + book.getSynopsis() + "<br></html>";
            action = "Check Out";
        }

        int response = JOptionPane.showConfirmDialog(
                this,
                "Do you want to " + action + " the following book?<br>" + bookDetails,
                action + " Book",
                JOptionPane.YES_NO_OPTION,
                JOptionPane.QUESTION_MESSAGE
        );

        if (response == JOptionPane.YES_OPTION) {
            if (checkOut) {
                checkoutBook(book);
            } else {
                returnBook(book);
            }
        } else {
            JOptionPane.showMessageDialog(this, "You chose not to " + action + " the book.", "Action Cancelled", JOptionPane.INFORMATION_MESSAGE);
        }
    }

    private JPanel createLibraryPanel() {
        JPanel panel = new JPanel(new GridLayout(5, 2, 10, 10));
        panel.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));

        JLabel titleLabel = new JLabel("Book Title:");
        bookTitleField = new JTextField();

        JLabel authorLabel = new JLabel("Author:");
        bookAuthorField = new JTextField();

        JLabel genreLabel = new JLabel("Genre:");
        bookGenreField = new JTextField();

        JLabel synopsisLabel = new JLabel("Synopsis:");
        bookSynopsisField = new JTextField();

        JButton addBookButton = new JButton("Add Book to Library");
        addBookButton.addActionListener(e -> addBookToLibrary());

        JButton removeBookButton = new JButton("Remove Book from Library");
        removeBookButton.addActionListener(e -> removeBookFromLibrary());

        panel.add(titleLabel);
        panel.add(bookTitleField);
        panel.add(authorLabel);
        panel.add(bookAuthorField);
        panel.add(genreLabel);
        panel.add(bookGenreField);
        panel.add(synopsisLabel);
        panel.add(bookSynopsisField);
        panel.add(addBookButton);
        panel.add(removeBookButton);

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

//    private void enableLibraryMainTabs() {
//        libraryTabsPanel = createReaderTabsPanel();
//        mainPanel.add(readerTabsPanel, "Reader Tabs");
//        readerMainTabs.setEnabled(true);
//        cardLayout.show(mainPanel, "Reader Tabs");
//    }

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
            //enableMainTabs();
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
        } else {
            JOptionPane.showMessageDialog(this, "Book addition failed!", "Book Add", JOptionPane.ERROR_MESSAGE);
        }
    }

    private void removeBookFromLibrary() {
        String title = bookTitleField.getText();
        Book book = new Book(title, null, null, null, null);

        if (client.removeBookFromLibrary(book)) {
            JOptionPane.showMessageDialog(this, "Book removed from library successfully!", "Book Remove", JOptionPane.INFORMATION_MESSAGE);
        } else {
            JOptionPane.showMessageDialog(this, "Book removal failed!", "Book Remove", JOptionPane.ERROR_MESSAGE);
        }
    }

    private void checkoutBook(Book book) {
        if (client.checkoutBook(book)) {
            JOptionPane.showMessageDialog(this, "Book checked out successfully!", "Book Checkout", JOptionPane.INFORMATION_MESSAGE);
        } else {
            JOptionPane.showMessageDialog(this, "Book checkout failed!", "Book Checkout", JOptionPane.ERROR_MESSAGE);
        }
    }

    private void returnBook(Book book) {
        if (client.returnBook(book)) {
            JOptionPane.showMessageDialog(this, "Book returned successfully!", "Book Return", JOptionPane.INFORMATION_MESSAGE);
        } else {
            JOptionPane.showMessageDialog(this, "Book return failed!", "Book Return", JOptionPane.ERROR_MESSAGE);
        }
    }

    private void appendOutput(String message) {
        outputArea.append(message + "\n");
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
