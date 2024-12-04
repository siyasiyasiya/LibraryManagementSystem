package network;

import model.Book;
import model.Library;
import network.Client;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.IOException;
import java.net.Socket;
import java.util.ArrayList;

public class ClientGUI extends JFrame implements Runnable {

    private Client client;
    private JTextField loginUsernameField, createUsernameField, libraryNameField, bookTitleField, bookAuthorField, bookGenreField, bookSynopsisField;
    private JPasswordField loginPasswordField, createPasswordField;
    private JTextArea outputArea;
    private JPanel mainPanel, landingPanel, landingUserPanel, landingLibraryPanel, tabsPanel;
    private CardLayout cardLayout;
    private JTabbedPane landingUserTabs, landingLibraryTabs, mainTabs;
    private JComboBox<Library> loginLibraryDropdown, createLibraryDropdown;

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

        tabsPanel = createTabsPanel();

        mainPanel.add(landingPanel, "Landing Page");
        mainPanel.add(tabsPanel, "Tabs");

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

    private JPanel createTabsPanel() {
        mainTabs = new JTabbedPane();

        mainTabs.add("Reader Operations", createReaderPanel());
        mainTabs.add("Library Operations", createLibraryPanel());

        // Initially disable the tabs
        mainTabs.setEnabled(false);

        JPanel panel = new JPanel(new BorderLayout());
        panel.add(mainTabs, BorderLayout.CENTER);
        panel.add(createOutputPanel(), BorderLayout.SOUTH);

        return panel;
    }

    private JPanel createReaderPanel() {
        JPanel panel = new JPanel(new GridLayout(3, 1, 10, 10));
        panel.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));

        JButton viewBooksButton = new JButton("View Books in Library");
        viewBooksButton.addActionListener(e -> retrieveBooksByLibrary());

        JButton checkoutBookButton = new JButton("Checkout Book");
        checkoutBookButton.addActionListener(e -> checkoutBook());

        JButton returnBookButton = new JButton("Return Book");
        returnBookButton.addActionListener(e -> returnBook());

        panel.add(viewBooksButton);
        panel.add(checkoutBookButton);
        panel.add(returnBookButton);

        return panel;
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

    private void enableMainTabs() {
        mainTabs.setEnabled(true);
        cardLayout.show(mainPanel, "Tabs");
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
            appendOutput("Reader login successful");
            enableMainTabs();
        } else if (client.loginLibrary(username, password)) {
            appendOutput("Library login successful");
            enableMainTabs();
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

        //must rerun to see new changes
        if (!isReader && client.createLibrary(username, password)) {
            JOptionPane.showMessageDialog(this, "Library Account Created Successfully!", "Account Creation", JOptionPane.INFORMATION_MESSAGE);

            loginLibraryDropdown.removeAllItems();
            loginLibraryDropdown.addItem(null);
            System.out.println("Adding libraries to dropdown...");
            ArrayList<Library> libraries = retrieveLibraries();
            System.out.println(libraries.size());
            for (Library lib : libraries) {
                System.out.println("Adding library: " + lib);
                loginLibraryDropdown.addItem(lib);
            }
            loginLibraryDropdown.setSelectedIndex(0);

        } else if (client.createReader(username, password, library)) {
            JOptionPane.showMessageDialog(this, "Reader Account Created Successfully!", "Account Creation", JOptionPane.INFORMATION_MESSAGE);
        } else {
            showError("Account Creation Failed");
        }
    }

    private void updateLibraryDropdowns() {
        ArrayList<Library> libraries = retrieveLibraries();

        loginLibraryDropdown.removeAllItems();
        loginLibraryDropdown.addItem(null);
        for (Library lib : libraries) {
            loginLibraryDropdown.addItem(lib);
        }
        loginLibraryDropdown.setSelectedIndex(0);

        createLibraryDropdown.removeAllItems();
        createLibraryDropdown.addItem(null);
        for (Library lib : libraries) {
            createLibraryDropdown.addItem(lib);
        }
        createLibraryDropdown.setSelectedIndex(0);

        loginLibraryDropdown.revalidate();
        loginLibraryDropdown.repaint();
        createLibraryDropdown.revalidate();
        createLibraryDropdown.repaint();
    }

    private ArrayList<Library> retrieveLibraries() {
        return client.retrieveLibraries();
    }

    private void retrieveBooksByLibrary() {
        ArrayList<Book> books = client.retrieveBooksByLibrary();
        if (books != null) {
            books.forEach(book -> appendOutput(book.getTitle() + " by " + book.getAuthor()));
        } else {
            appendOutput("Failed to retrieve books from library");
        }
    }

    private void addBookToLibrary() {
        String title = bookTitleField.getText();
        String author = bookAuthorField.getText();
        String genre = bookGenreField.getText();
        String synopsis = bookSynopsisField.getText();

        if (client.addBookToLibrary(title, author, genre, synopsis)) {
            appendOutput("Book added to library: " + title);
        } else {
            appendOutput("Failed to add book to library");
        }
    }

    private void removeBookFromLibrary() {
        String title = bookTitleField.getText();
        Book book = new Book(title, null, null, null, null);

        if (client.removeBookFromLibrary(book)) {
            appendOutput("Book removed from library: " + title);
        } else {
            appendOutput("Failed to remove book from library");
        }
    }

    private void checkoutBook() {
        String title = bookTitleField.getText();
        Book book = new Book(title, null, null, null, null);

        if (client.checkoutBook(book)) {
            appendOutput("Book checked out: " + title);
        } else {
            appendOutput("Failed to checkout book");
        }
    }

    private void returnBook() {
        String title = bookTitleField.getText();
        Book book = new Book(title, null, null, null, null);

        if (client.returnBook(book)) {
            appendOutput("Book returned: " + title);
        } else {
            appendOutput("Failed to return book");
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
