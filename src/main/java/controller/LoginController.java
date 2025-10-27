package controller;

import model.User;
import service.UserService;
import service.impl.UserServiceImpl;

import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.*;
import java.io.IOException;

@WebServlet(urlPatterns = "/login")
public class LoginController extends HttpServlet {
    private static final long serialVersionUID = 1L;

    private UserService userService;

    @Override public void init() throws ServletException {
        userService = new UserServiceImpl();
    }

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp)
            throws ServletException, IOException {
        req.getRequestDispatcher("views/login.jsp").forward(req, resp);
    }

    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp)
            throws ServletException, IOException {
        String username = safe(req.getParameter("username"));
        String password = safe(req.getParameter("password"));

        // Service tự xử lý: tìm user + BCrypt.checkpw + isActive
        User user = userService.login(username, password);

        if (user != null) {
            // Rotate session để chống session fixation
            HttpSession old = req.getSession(false);
            if (old != null) old.invalidate();
            HttpSession session = req.getSession(true);
            session.setAttribute("currentUser", user);
            // tuỳ chọn: session.setMaxInactiveInterval(30*60); // 30 phút
            resp.sendRedirect(req.getContextPath() + "/home");
        } else {
            req.setAttribute("alert", "Sai tài khoản hoặc mật khẩu");
            req.getRequestDispatcher("views/login.jsp").forward(req, resp);
        }
    }

    private String safe(String s){ return s==null? "": s.trim(); }
}
