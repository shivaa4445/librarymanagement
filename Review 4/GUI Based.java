import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.awt.event.*;
import java.sql.*;
import java.util.regex.*;

public class LibraryManagementSystem {

    private static final String DATABASE_URL = "jdbc:sqlite:library.db";

    private JFrame frame;
    private JTable table;
    private JTextField txtBookID, txtTitle, txtAuthor, txtYear, txtSearch;
    private DefaultTableModel model;

    public LibraryManagementSystem() {
        frame = new JFrame("Library Management System");
        frame.setSize(900, 600);
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.setLayout(new BorderLayout());

        // Table for displaying books
        model = new DefaultTableModel(new String[]{"Book ID", "Title", "Author", "Year"}, 0);
        table = new JTable(model);
        JScrollPane tableScrollPane = new JScrollPane(table);
        frame.add(tableScrollPane, BorderLayout.CENTER);

        // Input Panel
        JPanel inputPanel = new JPanel(new GridLayout(5, 2, 10, 10));
        inputPanel.setBorder(BorderFactory.createTitledBorder("Book Details"));

        inputPanel.add(new JLabel("Book ID:"));
        txtBookID = new JTextField();
        inputPanel.add(txtBookID);

        inputPanel.add(new JLabel("Title:"));
        txtTitle = new JTextField();
        inputPanel.add(txtTitle);

        inputPanel.add(new JLabel("Author:"));
        txtAuthor = new JTextField();
        inputPanel.add(txtAuthor);

        inputPanel.add(new JLabel("Year:"));
        txtYear = new JTextField();
        inputPanel.add(txtYear);

        frame.add(inputPanel, BorderLayout.NORTH);

        // Buttons Panel
        JPanel buttonPanel = new JPanel(new GridLayout(1, 5, 10, 10));
        JButton btnAdd = new JButton("Add");
        JButton btnUpdate = new JButton("Update");
        JButton btnDelete = new JButton("Delete");
        JButton btnLoad = new JButton("Load Books");
        JButton btnSearch = new JButton("Search");

        buttonPanel.add(btnAdd);
        buttonPanel.add(btnUpdate);
        buttonPanel.add(btnDelete);
        buttonPanel.add(btnLoad);
        buttonPanel.add(btnSearch);
        frame.add(buttonPanel, BorderLayout.SOUTH);

        // Search Panel
        JPanel searchPanel = new JPanel(new FlowLayout());
        searchPanel.setBorder(BorderFactory.createTitledBorder("Search Books"));
        txtSearch = new JTextField(20);
        searchPanel.add(new JLabel("Search:"));
        searchPanel.add(txtSearch);
        frame.add(searchPanel, BorderLayout.EAST);

        // Event Listeners
        btnAdd.addActionListener(e -> addBook());
        btnUpdate.addActionListener(e -> updateBook());
        btnDelete.addActionListener(e -> deleteBook());
        btnLoad.addActionListener(e -> loadBooks());
        btnSearch.addActionListener(e -> searchBooks());

        initializeDatabase();
        loadBooks();
        frame.setVisible(true);
    }

    private void initializeDatabase() {
        try (Connection conn = DriverManager.getConnection(DATABASE_URL);
             Statement stmt = conn.createStatement()) {
            String createTableSQL = "CREATE TABLE IF NOT EXISTS books (" +
                    "book_id TEXT PRIMARY KEY, " +
                    "title TEXT NOT NULL, " +
                    "author TEXT NOT NULL, " +
                    "year TEXT NOT NULL)";
            stmt.execute(createTableSQL);
        } catch (SQLException e) {
            showErrorDialog("Database Initialization Error: " + e.getMessage());
        }
    }

    private void addBook() {
        String bookID = txtBookID.getText().trim();
        String title = txtTitle.getText().trim();
        String author = txtAuthor.getText().trim();
        String year = txtYear.getText().trim();

        if (!validateInputs(bookID, title, author, year)) return;

        try (Connection conn = DriverManager.getConnection(DATABASE_URL);
             PreparedStatement stmt = conn.prepareStatement("INSERT INTO books (book_id, title, author, year) VALUES (?, ?, ?, ?)");) {
            stmt.setString(1, bookID);
            stmt.setString(2, title);
            stmt.setString(3, author);
            stmt.setString(4, year);
            stmt.executeUpdate();
            showInfoDialog("Book added successfully!");
            loadBooks();
        } catch (SQLException e) {
            showErrorDialog("Error Adding Book: " + e.getMessage());
        }
    }

    private void updateBook() {
        int selectedRow = table.getSelectedRow();
        if (selectedRow == -1) {
            showErrorDialog("Please select a book to update!");
            return;
        }

        String bookID = table.getValueAt(selectedRow, 0).toString();
        String title = txtTitle.getText().trim();
        String author = txtAuthor.getText().trim();
        String year = txtYear.getText().trim();

        if (!validateInputs(bookID, title, author, year)) return;

        try (Connection conn = DriverManager.getConnection(DATABASE_URL);
             PreparedStatement stmt = conn.prepareStatement("UPDATE books SET title = ?, author = ?, year = ? WHERE book_id = ?");) {
            stmt.setString(1, title);
            stmt.setString(2, author);
            stmt.setString(3, year);
            stmt.setString(4, bookID);
            stmt.executeUpdate();
            showInfoDialog("Book updated successfully!");
            loadBooks();
        } catch (SQLException e) {
            showErrorDialog("Error Updating Book: " + e.getMessage());
        }
    }

    private void deleteBook() {
        int selectedRow = table.getSelectedRow();
        if (selectedRow == -1) {
            showErrorDialog("Please select a book to delete!");
            return;
        }

        String bookID = table.getValueAt(selectedRow, 0).toString();

        try (Connection conn = DriverManager.getConnection(DATABASE_URL);
             PreparedStatement stmt = conn.prepareStatement("DELETE FROM books WHERE book_id = ?");) {
            stmt.setString(1, bookID);
            stmt.executeUpdate();
            showInfoDialog("Book deleted successfully!");
            loadBooks();
        } catch (SQLException e) {
            showErrorDialog("Error Deleting Book: " + e.getMessage());
        }
    }

    private void loadBooks() {
        model.setRowCount(0);

        try (Connection conn = DriverManager.getConnection(DATABASE_URL);
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery("SELECT * FROM books")) {
            while (rs.next()) {
                model.addRow(new Object[]{rs.getString("book_id"), rs.getString("title"), rs.getString("author"), rs.getString("year")});
            }
        } catch (SQLException e) {
            showErrorDialog("Error Loading Books: " + e.getMessage());
        }
    }

    private void searchBooks() {
        String searchQuery = txtSearch.getText().trim().toLowerCase();
        model.setRowCount(0);

        try (Connection conn = DriverManager.getConnection(DATABASE_URL);
             PreparedStatement stmt = conn.prepareStatement("SELECT * FROM books WHERE LOWER(title) LIKE ? OR LOWER(author) LIKE ?")) {
            stmt.setString(1, "%" + searchQuery + "%");
            stmt.setString(2, "%" + searchQuery + "%");
            ResultSet rs = stmt.executeQuery();
            while (rs.next()) {
                model.addRow(new Object[]{rs.getString("book_id"), rs.getString("title"), rs.getString("author"), rs.getString("year")});
            }
        } catch (SQLException e) {
            showErrorDialog("Error Searching Books: " + e.getMessage());
        }
    }

    private boolean validateInputs(String bookID, String title, String author, String year) {
        if (bookID.isEmpty() || title.isEmpty() || author.isEmpty() || year.isEmpty()) {
            showErrorDialog("All fields are required!");
            return false;
        }
        if (!year.matches("\\d{4}")) {
            showErrorDialog("Year must be a 4-digit number!");
            return false;
        }
        return true;
    }

    private void showErrorDialog(String message) {
        JOptionPane.showMessageDialog(frame, message, "Error", JOptionPane.ERROR_MESSAGE);
    }

    private void showInfoDialog(String message) {
        JOptionPane.showMessageDialog(frame, message, "Info", JOptionPane.INFORMATION_MESSAGE);
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(LibraryManagementSystem::new);
    }
}
