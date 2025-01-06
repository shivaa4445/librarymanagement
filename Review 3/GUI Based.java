import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.awt.event.*;
import java.sql.*;

public class LibraryManagementGUI {

    // SQLite database connection URL
    private static final String DATABASE_URL = "jdbc:sqlite:library.db";

    // GUI Components
    private JFrame frame;
    private JTable table;
    private JTextField txtBookID, txtTitle, txtAuthor, txtYear;

    public LibraryManagementGUI() {
        // Initialize GUI
        frame = new JFrame("Library Management System");
        frame.setSize(800, 600);
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.setLayout(new BorderLayout());

        // Table for displaying book data
        table = new JTable();
        DefaultTableModel model = new DefaultTableModel(new String[]{"Book ID", "Title", "Author", "Year"}, 0);
        table.setModel(model);
        JScrollPane tableScrollPane = new JScrollPane(table);
        frame.add(tableScrollPane, BorderLayout.CENTER);

        // Panel for input fields
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

        // Panel for buttons
        JPanel buttonPanel = new JPanel(new GridLayout(1, 4, 10, 10));
        JButton btnAdd = new JButton("Add");
        JButton btnUpdate = new JButton("Update");
        JButton btnDelete = new JButton("Delete");
        JButton btnLoad = new JButton("Load Books");

        buttonPanel.add(btnAdd);
        buttonPanel.add(btnUpdate);
        buttonPanel.add(btnDelete);
        buttonPanel.add(btnLoad);

        frame.add(buttonPanel, BorderLayout.SOUTH);

        // Event handling for buttons
        btnAdd.addActionListener(e -> addBook());
        btnUpdate.addActionListener(e -> updateBook());
        btnDelete.addActionListener(e -> deleteBook());
        btnLoad.addActionListener(e -> loadBooks());

        // Initialize the database and load data
        initializeDatabase();
        loadBooks();

        frame.setVisible(true);
    }

    private void initializeDatabase() {
        try (Connection conn = DriverManager.getConnection(DATABASE_URL);
             Statement stmt = conn.createStatement()) {
            String sql = "CREATE TABLE IF NOT EXISTS books (" +
                    "book_id TEXT PRIMARY KEY," +
                    "title TEXT NOT NULL," +
                    "author TEXT NOT NULL," +
                    "year TEXT NOT NULL" +
                    ")";
            stmt.execute(sql);
        } catch (SQLException ex) {
            JOptionPane.showMessageDialog(frame, "Database Initialization Error: " + ex.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
        }
    }

    private void addBook() {
        String bookID = txtBookID.getText().trim();
        String title = txtTitle.getText().trim();
        String author = txtAuthor.getText().trim();
        String year = txtYear.getText().trim();

        if (bookID.isEmpty() || title.isEmpty() || author.isEmpty() || year.isEmpty()) {
            JOptionPane.showMessageDialog(frame, "All fields are required!", "Validation Error", JOptionPane.ERROR_MESSAGE);
            return;
        }

        try (Connection conn = DriverManager.getConnection(DATABASE_URL);
             PreparedStatement stmt = conn.prepareStatement("INSERT INTO books (book_id, title, author, year) VALUES (?, ?, ?, ?)");) {
            stmt.setString(1, bookID);
            stmt.setString(2, title);
            stmt.setString(3, author);
            stmt.setString(4, year);
            stmt.executeUpdate();
            JOptionPane.showMessageDialog(frame, "Book added successfully!", "Success", JOptionPane.INFORMATION_MESSAGE);
            loadBooks();
        } catch (SQLException ex) {
            JOptionPane.showMessageDialog(frame, "Error Adding Book: " + ex.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
        }
    }

    private void updateBook() {
        String bookID = txtBookID.getText().trim();
        String title = txtTitle.getText().trim();
        String author = txtAuthor.getText().trim();
        String year = txtYear.getText().trim();

        if (bookID.isEmpty() || title.isEmpty() || author.isEmpty() || year.isEmpty()) {
            JOptionPane.showMessageDialog(frame, "All fields are required!", "Validation Error", JOptionPane.ERROR_MESSAGE);
            return;
        }

        try (Connection conn = DriverManager.getConnection(DATABASE_URL);
             PreparedStatement stmt = conn.prepareStatement("UPDATE books SET title = ?, author = ?, year = ? WHERE book_id = ?");) {
            stmt.setString(1, title);
            stmt.setString(2, author);
            stmt.setString(3, year);
            stmt.setString(4, bookID);
            stmt.executeUpdate();
            JOptionPane.showMessageDialog(frame, "Book updated successfully!", "Success", JOptionPane.INFORMATION_MESSAGE);
            loadBooks();
        } catch (SQLException ex) {
            JOptionPane.showMessageDialog(frame, "Error Updating Book: " + ex.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
        }
    }

    private void deleteBook() {
        int selectedRow = table.getSelectedRow();
        if (selectedRow == -1) {
            JOptionPane.showMessageDialog(frame, "Please select a book to delete!", "Validation Error", JOptionPane.ERROR_MESSAGE);
            return;
        }

        String bookID = table.getValueAt(selectedRow, 0).toString();

        try (Connection conn = DriverManager.getConnection(DATABASE_URL);
             PreparedStatement stmt = conn.prepareStatement("DELETE FROM books WHERE book_id = ?");) {
            stmt.setString(1, bookID);
            stmt.executeUpdate();
            JOptionPane.showMessageDialog(frame, "Book deleted successfully!", "Success", JOptionPane.INFORMATION_MESSAGE);
            loadBooks();
        } catch (SQLException ex) {
            JOptionPane.showMessageDialog(frame, "Error Deleting Book: " + ex.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
        }
    }

    private void loadBooks() {
        DefaultTableModel model = (DefaultTableModel) table.getModel();
        model.setRowCount(0);

        try (Connection conn = DriverManager.getConnection(DATABASE_URL);
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery("SELECT * FROM books")) {
            while (rs.next()) {
                model.addRow(new Object[]{rs.getString("book_id"), rs.getString("title"), rs.getString("author"), rs.getString("year")});
            }
        } catch (SQLException ex) {
            JOptionPane.showMessageDialog(frame, "Error Loading Books: " + ex.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
        }
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(LibraryManagementGUI::new);
    }
}
