import java.io.IOException;
import java.sql.*;
import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

@WebServlet("/BookServlet")
public class BookServlet extends HttpServlet {
    private static final long serialVersionUID = 1L;

    // Add a book to the library
    protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        String title = request.getParameter("title");
        String author = request.getParameter("author");
        String genre = request.getParameter("genre");
        int availableCopies = Integer.parseInt(request.getParameter("available_copies"));

        try {
            Connection con = DBConnection.getConnection();
            String query = "INSERT INTO books (title, author, genre, available_copies) VALUES (?, ?, ?, ?)";
            PreparedStatement ps = con.prepareStatement(query);
            ps.setString(1, title);
            ps.setString(2, author);
            ps.setString(3, genre);
            ps.setInt(4, availableCopies);
            ps.executeUpdate();
            con.close();
        } catch (Exception e) {
            e.printStackTrace();
        }

        response.sendRedirect("index.html");
    }

    // List all books in the library
    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        try {
            Connection con = DBConnection.getConnection();
            Statement stmt = con.createStatement();
            ResultSet rs = stmt.executeQuery("SELECT * FROM books");
            StringBuilder booksList = new StringBuilder();

            while (rs.next()) {
                booksList.append("<p>").append(rs.getString("title"))
                         .append(" by ").append(rs.getString("author"))
                         .append(" - Genre: ").append(rs.getString("genre"))
                         .append(" - Available Copies: ").append(rs.getInt("available_copies"))
                         .append("</p>");
            }

            con.close();
            response.setContentType("text/html");
            response.getWriter().write(booksList.toString());
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
