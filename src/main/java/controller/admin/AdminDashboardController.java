package controller.admin;

import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;
import java.io.IOException;
import model.User;

@WebServlet(urlPatterns = "/admin")
public class AdminDashboardController extends HttpServlet {
    private static final long serialVersionUID = 1L;

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp)
            throws ServletException, IOException {
        HttpSession session = req.getSession(false);
        User currentUser = session != null ? (User) session.getAttribute("currentUser") : null;
        
        // Allow both Admin (roleId=1) and Manager (roleId=2), consistent with AdminAuthorizationFilter
        if (currentUser == null || (currentUser.getRoleid() != 1 && currentUser.getRoleid() != 2)) {
            resp.sendRedirect(req.getContextPath() + "/login");
            return;
        }
        
        // Redirect to a valid admin page
        resp.sendRedirect(req.getContextPath() + "/admin/dashboard");
    }
}