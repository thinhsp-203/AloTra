package controller.auth;

import jakarta.servlet.ServletException;
import jakarta.servlet.http.*;
import jakarta.servlet.annotation.WebServlet;
import service.AuthRecoveryService;
import service.impl.AuthRecoveryServiceImpl;

import java.io.IOException;

@WebServlet(urlPatterns = {"/auth/forgot", "/auth/reset"})
public class ForgotResetController extends HttpServlet {

    private AuthRecoveryService authRecoveryService;

    @Override
    public void init() throws ServletException {
        authRecoveryService = new AuthRecoveryServiceImpl();
    }

    // Simple rate limiting: max 5 attempts per 10 minutes per session
    private static boolean allowAttempt(HttpSession session) {
        String key = "FORGOT_ATTEMPTS";
        String winKey = "FORGOT_WINDOW";
        long now = System.currentTimeMillis();
        Long window = (Long) session.getAttribute(winKey);
        Integer count = (Integer) session.getAttribute(key);
        
        if (window == null || now - window > 10 * 60 * 1000L) { // 10 minutes
            session.setAttribute(winKey, now);
            session.setAttribute(key, 0);
            return true;
        }
        
        if (count == null) count = 0;
        if (count >= 5) return false;
        
        session.setAttribute(key, count + 1);
        return true;
    }

    @Override 
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) 
            throws ServletException, IOException {
        String path = req.getServletPath();
        
        if ("/auth/forgot".equals(path)) {
            req.getRequestDispatcher("/views/auth/forgot.jsp").forward(req, resp);
            
        } else if ("/auth/reset".equals(path)) {
            String token = req.getParameter("token");
            if (token == null || token.isBlank()) { 
                resp.sendError(400, "Thiếu token"); 
                return; 
            }
            
            boolean valid = authRecoveryService.isValidToken(token);
            if (!valid) {
                req.setAttribute("invalid", true);
            }
            
            req.setAttribute("token", token);
            req.getRequestDispatcher("/views/auth/reset.jsp").forward(req, resp);
        } else {
            resp.sendError(404);
        }
    }

    @Override 
    protected void doPost(HttpServletRequest req, HttpServletResponse resp) 
            throws ServletException, IOException {
        String path = req.getServletPath();
        
        if ("/auth/forgot".equals(path)) {
            handleForgotPassword(req, resp);
        } else if ("/auth/reset".equals(path)) {
            handleResetPassword(req, resp);
        } else {
            resp.sendError(404);
        }
    }
    
    private void handleForgotPassword(HttpServletRequest req, HttpServletResponse resp) 
            throws ServletException, IOException {
        if (!allowAttempt(req.getSession())) {
            req.setAttribute("msg", "Quá nhiều yêu cầu. Vui lòng thử lại sau 10 phút.");
            req.getRequestDispatcher("/views/auth/forgot.jsp").forward(req, resp);
            return;
        }
        
        String email = req.getParameter("email");
        if (email == null || email.trim().isEmpty()) {
            req.setAttribute("error", "Vui lòng nhập email!");
            req.getRequestDispatcher("/views/auth/forgot.jsp").forward(req, resp);
            return;
        }
        
        try {
            String baseUrl = req.getScheme() + "://" + req.getServerName() + ":" + req.getServerPort() + req.getContextPath();
            String token = authRecoveryService.createResetTokenIfEligible(email.trim(), baseUrl);

            if (token != null) {
                String resetLink = baseUrl + "/auth/reset?token=" + token;
                System.out.println("=== PASSWORD RESET EMAIL ===");
                System.out.println("To: " + email);
                System.out.println("Reset link: " + resetLink);
                System.out.println("Token expires in 1 hour");
                System.out.println("============================");
            }
        } catch (Exception e) {
            e.printStackTrace();
            req.setAttribute("error", "Có lỗi xảy ra. Vui lòng thử lại sau.");
            req.getRequestDispatcher("/views/auth/forgot.jsp").forward(req, resp);
            return;
        }
        
        // Always show generic message (don't reveal if email exists)
        req.setAttribute("msg", "Nếu email tồn tại trong hệ thống, hướng dẫn đặt lại mật khẩu đã được gửi đến email của bạn.");
        req.getRequestDispatcher("/views/auth/forgot.jsp").forward(req, resp);
    }
    
    private void handleResetPassword(HttpServletRequest req, HttpServletResponse resp) 
            throws ServletException, IOException {
        String token = req.getParameter("token");
        String password = req.getParameter("password");
        String confirm = req.getParameter("confirm");
        
        if (token == null || token.isBlank()) { 
            resp.sendError(400, "Thiếu token"); 
            return; 
        }
        
        if (password == null || confirm == null || password.isBlank() || !password.equals(confirm)) {
            req.setAttribute("token", token);
            req.setAttribute("error", "Mật khẩu không hợp lệ hoặc không khớp.");
            req.getRequestDispatcher("/views/auth/reset.jsp").forward(req, resp);
            return;
        }
        
        if (password.length() < 6) {
            req.setAttribute("token", token);
            req.setAttribute("error", "Mật khẩu phải có ít nhất 6 ký tự!");
            req.getRequestDispatcher("/views/auth/reset.jsp").forward(req, resp);
            return;
        }

        try {
            boolean ok = authRecoveryService.resetPassword(token, password);
            if (!ok) {
                req.setAttribute("token", token);
                req.setAttribute("invalid", true);
                req.getRequestDispatcher("/views/auth/reset.jsp").forward(req, resp);
                return;
            }

            HttpSession ses = req.getSession(false);
            if (ses != null) ses.invalidate();

            HttpSession newSession = req.getSession(true);
            newSession.setAttribute("success", "Đặt lại mật khẩu thành công! Vui lòng đăng nhập.");
            resp.sendRedirect(req.getContextPath() + "/login");

        } catch (Exception e) {
            e.printStackTrace();
            req.setAttribute("token", token);
            req.setAttribute("error", "Có lỗi xảy ra. Vui lòng thử lại sau.");
            req.getRequestDispatcher("/views/auth/reset.jsp").forward(req, resp);
        }
    }
}