package controller.auth;

import java.io.IOException;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;
import model.User;
import service.UserService;
import service.impl.UserServiceImpl;

@WebServlet(urlPatterns = { "/verify-otp" })
public class VerifyController extends HttpServlet {

    UserService userService = new UserServiceImpl();

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        // Hiển thị trang nhập mã OTP
        req.getRequestDispatcher("/views/auth/verify.jsp").forward(req, resp);
    }

    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        req.setCharacterEncoding("UTF-8");
        resp.setCharacterEncoding("UTF-8");

        String email = req.getParameter("email");
        String inputCode = req.getParameter("otp"); // Mã người dùng nhập
        String action = req.getParameter("action"); // "register" hoặc "forgot"

        // 1. Kiểm tra User tồn tại
        User user = userService.findUserByEmail(email);
        
        if (user != null) {
            // 2. So sánh mã OTP (Lấy mã đã lưu trong DB ra so với mã nhập vào)
            if (user.getCode() != null && user.getCode().equals(inputCode)) {
                
                // --- TRƯỜNG HỢP 1: KÍCH HOẠT TÀI KHOẢN ---
                if ("register".equals(action)) {
                	user.setIsActive(true);
                    user.setCode(null); // Xóa OTP sau khi dùng
                    userService.updateUser(user); // Lưu lại
                    
                    req.setAttribute("alert", "Kích hoạt thành công! Vui lòng đăng nhập.");
                    req.getRequestDispatcher("/views/login.jsp").forward(req, resp);
                } 
                
                // --- TRƯỜNG HỢP 2: QUÊN MẬT KHẨU ---
                else if ("forgot".equals(action)) {
                    // Chuyển sang trang đặt lại mật khẩu mới
                    // Lưu email vào session để trang đổi mật khẩu biết đang đổi cho ai
                    HttpSession session = req.getSession();
                    session.setAttribute("emailReset", email);
                    
                    resp.sendRedirect(req.getContextPath() + "/views/auth/new-password.jsp");
                }

            } else {
                // Mã OTP sai
                req.setAttribute("error", "Mã xác thực không đúng!");
                req.setAttribute("email", email); // Giữ lại email để không phải nhập lại
                req.setAttribute("action", action);
                req.getRequestDispatcher("/views/auth/verify.jsp").forward(req, resp);
            }
        } else {
            req.setAttribute("error", "Email không tồn tại!");
            req.getRequestDispatcher("/views/auth/verify.jsp").forward(req, resp);
        }
    }
}