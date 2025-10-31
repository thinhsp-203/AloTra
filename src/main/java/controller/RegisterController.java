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
        userService = new UserServiceImpl(); // JPA bên dưới
    }

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp)
            throws ServletException, IOException {
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
        String confirmPassword = safe(req.getParameter("confirmPassword")); // FIX: Thêm
        String phone    = safe(req.getParameter("phone"));

        // FIX: Validate password confirmation
        if (!password.equals(confirmPassword)) {
            req.setAttribute("alert", "Mật khẩu xác nhận không khớp!");
            req.getRequestDispatcher("views/register.jsp").forward(req, resp);
            return;
        }

        String err = validate(email, username, password, phone);
        if (err != null) {
            req.setAttribute("alert", err);
            req.getRequestDispatcher("views/register.jsp").forward(req, resp);
            return;
        }

        var ok = userService.register(username, password, email, fullname, phone);
        if (ok) {
            req.getSession().setAttribute("success", "Đăng ký thành công! Vui lòng đăng nhập.");
            resp.sendRedirect(req.getContextPath() + "/login");
        } else {
            req.setAttribute("alert", "Tài khoản/Email/SĐT đã tồn tại hoặc dữ liệu không hợp lệ!");
            req.getRequestDispatcher("views/register.jsp").forward(req, resp);
        }
    }

    private String safe(String s) { return (s == null) ? "" : s.trim(); }

    private String validate(String email, String username, String password, String phone) {
        if (email.isEmpty() || username.isEmpty() || password.isEmpty()) {
            return "Email/Username/Password không được rỗng!";
        }
        // email cơ bản
        if (!email.matches("^[A-Za-z0-9._%+-]+@[A-Za-z0-9.-]+\\.[A-Za-z]{2,63}$")) {
            return "Email không hợp lệ!";
        }
        // username 4-32 ký tự, chữ/số/._-
        if (!username.matches("^[A-Za-z0-9._-]{4,32}$")) {
            return "Username 4-32 ký tự (chữ/số/._-)";
        }
        // password >= 6 (tuỳ chỉnh policy)
        if (password.length() < 6) {
            return "Mật khẩu tối thiểu 6 ký tự!";
        }
        // phone option: cho phép rỗng hoặc 9-11 số
        if (!phone.isEmpty() && !phone.matches("^[0-9]{9,11}$")) {
            return "Số điện thoại chỉ gồm 9-11 chữ số!";
        }
        return null;
    }
}
