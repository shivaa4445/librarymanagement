import java.io.IOException;
import java.sql.*;
import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

@WebServlet("/TransactionServlet")
public class TransactionServlet extends HttpServlet {
    private static final long serialVersionUID = 1L;

    // Borrow book
    protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        int userId = Integer.parseInt(request.getParameter("user_id"));
        int bookId = Integer.parseInt(request.getParameter("book_id"));
        String borrowDate = request.getParameter("borrow_date");

        try {
            Connection con = DBConnection.getConnection();
            String query = "INSERT INTO transactions (user_id, book_id, borrow_date) VALUES (?, ?, ?)";
            PreparedStatement ps = con.prepareStatement(query);
            ps.setInt(1, userId);
            ps.setInt(2, bookId);
            ps.setDate(3, Date.valueOf(borrowDate));
            ps.executeUpdate();

            // Update the number of available copies
            String updateQuery = "UPDATE books SET available_copies = available_copies - 1 WHERE book_id = ?";
            PreparedStatement updatePs = con.prepareStatement(updateQuery);
            updatePs.setInt(1, bookId);
            updatePs.executeUpdate();
            con.close();
        } catch (Exception e) {
            e.printStackTrace();
        }

        response.sendRedirect("index.html");
    }
}
