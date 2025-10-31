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

        User user = userService.login(username, password);

        if (user != null) {
            // FIX: Kiểm tra tài khoản có bị vô hiệu hóa không
            if (user.getIsActive() == null || !user.getIsActive()) {
                req.setAttribute("alert", "Tài khoản đã bị vô hiệu hóa. Vui lòng liên hệ quản trị viên.");
                req.getRequestDispatcher("views/login.jsp").forward(req, resp);
                return;
            }
            
            HttpSession old = req.getSession(false);
            if (old != null) old.invalidate();
            HttpSession session = req.getSession(true);
            session.setAttribute("currentUser", user);

            String redirectUrl = (String) session.getAttribute("redirectAfterLogin");
            if (redirectUrl != null && !redirectUrl.isEmpty()) {
                session.removeAttribute("redirectAfterLogin");
                resp.sendRedirect(redirectUrl);
            } else {
                resp.sendRedirect(req.getContextPath() + "/home");
            }
        } else {
            req.setAttribute("alert", "Sai tài khoản hoặc mật khẩu");
            req.getRequestDispatcher("views/login.jsp").forward(req, resp);
        }
    }

    private String safe(String s){ return s==null? "": s.trim(); }
}