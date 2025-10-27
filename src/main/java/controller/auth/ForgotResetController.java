package controller.auth;

import config.JpaUtil;
import dao.jpa.UserRepository;
import jakarta.persistence.EntityManager;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.*;
import jakarta.servlet.annotation.WebServlet;
import model.User;
import util.PasswordUtil;

import java.io.IOException;
import java.time.LocalDateTime;

/**
 * /auth/forgot (GET|POST)
 * /auth/reset  (GET|POST)   ?token=...
 */
public class ForgotResetController extends HttpServlet {

  // Rate-limit đơn giản: tối đa 5 lần / 10 phút theo session
  private static boolean allowAttempt(HttpSession ses) {
    String key = "FORGOT_ATTEMPTS";
    String winKey = "FORGOT_WINDOW";
    long now = System.currentTimeMillis();
    Long win = (Long) ses.getAttribute(winKey);
    Integer cnt = (Integer) ses.getAttribute(key);
    if (win == null || now - win > 10*60*1000L) { // 10 phút
      ses.setAttribute(winKey, now);
      ses.setAttribute(key, 0);
      return true;
    }
    if (cnt == null) cnt = 0;
    if (cnt >= 5) return false;
    ses.setAttribute(key, cnt + 1);
    return true;
  }

  @Override protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
    String path = req.getPathInfo(); // "/forgot" | "/reset"
    if ("/forgot".equals(path)) {
      req.getRequestDispatcher("/views/auth/forgot.jsp").forward(req, resp);
    } else if ("/reset".equals(path)) {
      String token = req.getParameter("token");
      if (token == null || token.isBlank()) { resp.sendError(400, "Thiếu token"); return; }
      // Kiểm tra hợp lệ để show form reset
      try (EntityManager em = JpaUtil.em()) {
        var uopt = new UserRepository(em).findByResetTokenValid(token);
        if (uopt.isEmpty()) { req.setAttribute("invalid", true); }
      }
      req.setAttribute("token", token);
      req.getRequestDispatcher("/views/auth/reset.jsp").forward(req, resp);
    } else {
      resp.sendError(404);
    }
  }

  @Override protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
    String path = req.getPathInfo();
    if ("/forgot".equals(path)) {
      if (!allowAttempt(req.getSession())) {
        // quá nhiều lượt
        req.setAttribute("msg", "Nếu email tồn tại, hướng dẫn đặt lại đã được gửi.");
        req.getRequestDispatcher("/views/auth/forgot.jsp").forward(req, resp);
        return;
      }
      String email = req.getParameter("email");
      try (EntityManager em = JpaUtil.em()) {
        var repo = new UserRepository(em);
        var uopt = repo.findByEmail(email);
        if (uopt.isPresent()) {
          var tx = em.getTransaction(); tx.begin();
          try {
            User u = uopt.get();
            String token = PasswordUtil.newUrlToken();
            u.setResetToken(token);
            u.setTokenExpiry(LocalDateTime.now().plusMinutes(60));
            em.merge(u);
            tx.commit();

            // Mock: “Gửi email” — log ra console server
            String resetLink = req.getRequestURL().toString().replace("/forgot","/reset") + "?token=" + token;
            System.out.println("[FORGOT] Send reset link to "+email+": "+resetLink);
          } catch(Exception ex){ if(tx.isActive()) tx.rollback(); throw ex; }
        }
      }
      // Luôn trả lời chung chung (tránh lộ thông tin tài khoản)
      req.setAttribute("msg", "Nếu email tồn tại, hướng dẫn đặt lại đã được gửi.");
      req.getRequestDispatcher("/views/auth/forgot.jsp").forward(req, resp);

    } else if ("/reset".equals(path)) {
      String token = req.getParameter("token");
      String p1 = req.getParameter("password");
      String p2 = req.getParameter("confirm");
      if (token == null || token.isBlank()) { resp.sendError(400, "Thiếu token"); return; }
      if (p1 == null || p2 == null || p1.isBlank() || !p1.equals(p2)) {
        req.setAttribute("token", token);
        req.setAttribute("error", "Mật khẩu không hợp lệ hoặc không khớp.");
        req.getRequestDispatcher("/views/auth/reset.jsp").forward(req, resp);
        return;
      }

      try (EntityManager em = JpaUtil.em()) {
        var repo = new UserRepository(em);
        var uopt = repo.findByResetTokenValid(token);
        if (uopt.isEmpty()) {
          req.setAttribute("token", token);
          req.setAttribute("invalid", true);
          req.getRequestDispatcher("/views/auth/reset.jsp").forward(req, resp);
          return;
        }
        var tx = em.getTransaction(); tx.begin();
        try {
          User u = uopt.get();
          u.setPassword(util.PasswordUtil.hash(p1));
          u.setResetToken(null);
          u.setTokenExpiry(null);
          em.merge(u);
          tx.commit();
        } catch(Exception ex){ if(tx.isActive()) tx.rollback(); throw ex; }

        // Invalidate session hiện tại để tránh session fixation
        HttpSession ses = req.getSession(false);
        if (ses != null) ses.invalidate();

        // Chuyển về login với thông báo
        req.setAttribute("resetOK", true);
        req.getRequestDispatcher("/views/auth/login.jsp").forward(req, resp);
      }

    } else {
      resp.sendError(404);
    }
  }
}
