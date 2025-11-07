package controller;

import service.UserService;
import service.impl.UserServiceImpl;

import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.*;
import java.io.IOException;

@WebServlet(urlPatterns = "/register")
public class RegisterController extends HttpServlet {
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
        
        req.setCharacterEncoding("UTF-8");
        resp.setCharacterEncoding("UTF-8");
        req.getRequestDispatcher("views/register.jsp").forward(req, resp);
    }

    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp)
            throws ServletException, IOException {
        req.setCharacterEncoding("UTF-8");
        resp.setCharacterEncoding("UTF-8");

        String email    = safe(req.getParameter("email"));
        String username = safe(req.getParameter("username"));
        String fullname = safe(req.getParameter("fullname"));
        String password = safe(req.getParameter("password"));
        String confirmPassword = safe(req.getParameter("confirmPassword"));
        String phone    = safe(req.getParameter("phone")); // SĐT giờ là bắt buộc

        // Validate password confirmation
        if (!password.equals(confirmPassword)) {
            req.setAttribute("alert", "Mật khẩu xác nhận không khớp!");
            preserveFormData(req, email, username, fullname, phone);
            req.getRequestDispatcher("views/register.jsp").forward(req, resp);
            return;
        }

        // Validate server-side
        String err = validate(email, username, password, phone); // Thêm phone vào validation
        if (err != null) {
            req.setAttribute("alert", err);
            preserveFormData(req, email, username, fullname, phone);
            req.getRequestDispatcher("views/register.jsp").forward(req, resp);
            return;
        }

        // Register user
        boolean success = userService.register(username, password, email, fullname, phone);
        if (success) {
            req.getSession().setAttribute("success", "Đăng ký thành công! Vui lòng đăng nhập.");
            resp.sendRedirect(req.getContextPath() + "/login");
        } else {
            req.setAttribute("alert", "Tài khoản/Email/SĐT đã tồn tại!");
            preserveFormData(req, email, username, fullname, phone);
            req.getRequestDispatcher("views/register.jsp").forward(req, resp);
        }
    }

    private String safe(String s) { 
        return (s == null) ? "" : s.trim(); 
    }

    private void preserveFormData(HttpServletRequest req, String email, String username, String fullname, String phone) {
        req.setAttribute("email", email);
        req.setAttribute("username", username);
        req.setAttribute("fullname", fullname);
        req.setAttribute("phone", phone);
    }

    private String validate(String email, String username, String password, String phone) {
        if (email.isEmpty() || username.isEmpty() || password.isEmpty()) {
            return "Email/Username/Password không được rỗng!";
        }
        
        // SĐT là bắt buộc
        if (phone.isEmpty()) {
            return "Số điện thoại là bắt buộc!";
        }
        
        // Email validation
        if (!email.matches("^[A-Za-z0-9._%+-]+@[A-Za-z0-9.-]+\\.[A-Za-z]{2,63}$")) {
            return "Email không hợp lệ!";
        }
        
        // Username validation (4-32 chars, alphanumeric + ._-)
        if (!username.matches("^[A-Za-z0-9._-]{4,32}$")) {
            return "Username: 4-32 ký tự (chỉ chữ/số/._-)";
        }
        
        // Password strength
        if (password.length() < 6) {
            return "Mật khẩu tối thiểu 6 ký tự!";
        }
        
        if (password.length() > 100) {
            return "Mật khẩu quá dài!";
        }
        
        // Phone validation (bây giờ là bắt buộc)
        if (!phone.matches("^[0-9]{9,11}$")) {
            return "Số điện thoại: 9-11 chữ số!";
        }
        
        return null;
    }
}