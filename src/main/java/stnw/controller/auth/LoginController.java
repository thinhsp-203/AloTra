package stnw.controller.auth;

import stnw.model.User;
import stnw.service.UserService;
import stnw.service.impl.UserServiceImpl;

import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.*;
import java.io.IOException;

@WebServlet(urlPatterns = "/login", asyncSupported = false)
public class LoginController extends HttpServlet {
    private static final long serialVersionUID = 1L;
    private UserService userService;

    @Override 
    public void init() throws ServletException {
        userService = new UserServiceImpl();
    }

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp)
            throws ServletException, IOException {
        // Nếu đã login, redirect về home
        HttpSession session = req.getSession(false);
        if (session != null && session.getAttribute("currentUser") != null) {
            resp.sendRedirect(req.getContextPath() + "/home");
            return;
        }
        
        // Xử lý alert từ query parameter
        String alertParam = req.getParameter("alert");
        if (alertParam != null && !alertParam.isEmpty()) {
            req.setAttribute("alert", alertParam);
        }
        
        // Lưu redirect URL vào session nếu có
        String redirectUrl = req.getParameter("redirect");
        if (redirectUrl != null && !redirectUrl.isEmpty()) {
            if (session == null) {
                session = req.getSession(true);
            }
            session.setAttribute("redirectAfterLogin", redirectUrl);
        }
        
        req.getRequestDispatcher("/views/auth/login.jsp").forward(req, resp);
    }

    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp)
            throws ServletException, IOException {
        String username = safe(req.getParameter("username"));
        String password = safe(req.getParameter("password"));
        String rememberMe = req.getParameter("rememberMe"); // NEW

        User user = userService.login(username, password);

        if (user != null) {
            // Kiểm tra tài khoản có bị vô hiệu hóa
            if (user.getIsActive() == null || !user.getIsActive()) {
                req.setAttribute("alert", "Tài khoản đã bị vô hiệu hóa. Vui lòng liên hệ quản trị viên.");
                req.getRequestDispatcher("/views/auth/login.jsp").forward(req, resp);
                return;
            }

            // Invalidate old session to prevent session fixation
            HttpSession oldSession = req.getSession(false);
            if (oldSession != null) {
                oldSession.invalidate();
            }
            
            HttpSession session = req.getSession(true);
            session.setAttribute("currentUser", user);
            session.setMaxInactiveInterval(30 * 60); // 30 minutes

            // Remember me functionality
            if ("on".equals(rememberMe)) {
                Cookie usernameCookie = new Cookie("remembered_username", username);
                usernameCookie.setMaxAge(7 * 24 * 60 * 60); // 7 days
                usernameCookie.setPath(req.getContextPath());
                usernameCookie.setHttpOnly(true);
                resp.addCookie(usernameCookie);
            }

            // Redirect handling
            String redirectUrl = (String) session.getAttribute("redirectAfterLogin");
            if (redirectUrl != null && !redirectUrl.isEmpty()) {
                session.removeAttribute("redirectAfterLogin");
                resp.sendRedirect(redirectUrl);
            } else {
                // Redirect based on role
                if (user.getRoleid() == 1 || user.getRoleid() == 2) {
                    resp.sendRedirect(req.getContextPath() + "/admin/dashboard");
                } else {
                    resp.sendRedirect(req.getContextPath() + "/home");
                }
            }
        } else {
            req.setAttribute("alert", "Sai tài khoản hoặc mật khẩu");
            req.setAttribute("username", username); // Preserve username
            req.getRequestDispatcher("/views/auth/login.jsp").forward(req, resp);
        }
    }

    private String safe(String s) { 
        return s == null ? "" : s.trim(); 
    }
}

