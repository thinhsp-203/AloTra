package controller;

import service.UserService;
import service.impl.UserServiceImpl;
import utils.EmailUtil; 

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
        if (session != null && session.getAttribute("account") != null) { // Đã thêm dấu {
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
        String phone    = safe(req.getParameter("phone"));

        // 1. Validate Password match
        if (!password.equals(confirmPassword)) {
            req.setAttribute("alert", "Mật khẩu xác nhận không khớp!");
            preserveFormData(req, email, username, fullname, phone);
            req.getRequestDispatcher("views/register.jsp").forward(req, resp);
            return;
        }

        // 2. Validate Server-side logic
        String err = validate(email, username, password, phone);
        if (err != null) {
            req.setAttribute("alert", err);
            preserveFormData(req, email, username, fullname, phone);
            req.getRequestDispatcher("views/register.jsp").forward(req, resp);
            return;
        }

        // 3. Tạo mã OTP ngẫu nhiên (6 số)
        String code = String.valueOf((int) ((Math.random() * 900000) + 100000));

        // 4. Gọi Service đăng ký (kèm mã code)
        boolean success = userService.register(username, password, email, fullname, phone, code);

        if (success) {
            // 5. Gửi Email (Chạy luồng riêng)
            new Thread(() -> {
                String subject = "Kích hoạt tài khoản - AloTra";
                String body = "Chào " + fullname + ",\n\n"
                        + "Mã xác thực (OTP) của bạn là: " + code + "\n"
                        + "Vui lòng nhập mã này để kích hoạt tài khoản.\n\n"
                        + "Trân trọng, AloTra Team.";
                EmailUtil.sendEmail(email, subject, body);
            }).start();

            // 6. Chuyển hướng sang trang nhập OTP verify
            req.setAttribute("message", "Đăng ký thành công! Vui lòng kiểm tra email để lấy mã OTP.");
            req.setAttribute("email", email); 
            req.setAttribute("action", "register"); 
            
            // Forward sang trang verify.jsp 
            req.getRequestDispatcher("/views/auth/verify.jsp").forward(req, resp);

        } else {
            req.setAttribute("alert", "Tài khoản, Email hoặc SĐT đã tồn tại!");
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
        
        if (phone.isEmpty()) {
            return "Số điện thoại là bắt buộc!";
        }
        
        if (!email.matches("^[A-Za-z0-9._%+-]+@[A-Za-z0-9.-]+\\.[A-Za-z]{2,63}$")) {
            return "Email không hợp lệ!";
        }
        
        if (!username.matches("^[A-Za-z0-9._-]{4,32}$")) {
            return "Username: 4-32 ký tự (chỉ chữ/số/._-)";
        }
        
        if (password.length() < 6) {
            return "Mật khẩu tối thiểu 6 ký tự!";
        }
        
        // Sửa regex cho phép 9-11 số (phù hợp đầu số VN)
        if (!phone.matches("^[0-9]{9,11}$")) {
            return "Số điện thoại: 9-11 chữ số!";
        }
        
        return null;
    }
}