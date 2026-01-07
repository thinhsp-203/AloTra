package stnw.controller.auth;

import stnw.service.UserService;
import stnw.service.impl.UserServiceImpl;
import stnw.utils.EmailUtils;
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
        HttpSession session = req.getSession(false);
        if (session != null && session.getAttribute("account") != null) {
            resp.sendRedirect(req.getContextPath() + "/home");
            return;
        }
        req.setCharacterEncoding("UTF-8");
        resp.setCharacterEncoding("UTF-8");
        req.getRequestDispatcher("/views/auth/register.jsp").forward(req, resp);
    }

    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp)
            throws ServletException, IOException {
        req.setCharacterEncoding("UTF-8");
        resp.setCharacterEncoding("UTF-8");

        try {
            String email    = safe(req.getParameter("email"));
            String username = safe(req.getParameter("username"));
            String fullname = safe(req.getParameter("fullname"));
            String password = safe(req.getParameter("password"));
            String confirm  = safe(req.getParameter("confirmPassword"));
            String phone    = safe(req.getParameter("phone"));

            // Validation cơ bản
            if (!password.equals(confirm)) {
                req.setAttribute("alert", "Mật khẩu không khớp!");
                req.getRequestDispatcher("/views/auth/register.jsp").forward(req, resp);
                return;
            }

            // Tạo mã OTP
            String code = String.valueOf((int) ((Math.random() * 900000) + 100000));

           boolean success = userService.register(username, password, email, fullname, phone, code);

            if (success) {
                // Gửi Email trong luồng riêng để không làm chậm web
                new Thread(() -> {
                    try {
                        String subject = "Xác thực tài khoản AloTra";
                        String body = "Mã OTP của bạn là: " + code;
                        EmailUtils.sendEmail(email, subject, body);
                    } catch (Exception e) {
                        e.printStackTrace(); 
                    }
                }).start();

                // Chuyển hướng sang trang nhập OTP
                req.setAttribute("email", email);
                req.setAttribute("action", "register"); 
                req.setAttribute("message", "Đã gửi mã OTP vào email: " + email);
                req.getRequestDispatcher("/views/auth/verify.jsp").forward(req, resp);
            } else {
                req.setAttribute("alert", "Đăng ký thất bại! Username hoặc Email đã tồn tại.");
                req.getRequestDispatcher("/views/auth/register.jsp").forward(req, resp);
            }

        } catch (Exception e) {
            e.printStackTrace();
            req.setAttribute("alert", "Lỗi hệ thống: " + e.getMessage());
            req.getRequestDispatcher("/views/auth/register.jsp").forward(req, resp);
        }
    }

    private String safe(String s) {
        return (s == null) ? "" : s.trim();
    }
}
