package controller.user;

import config.JpaUtil;
import jakarta.persistence.EntityManager;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import model.User;
import utils.PasswordUtil;

import java.io.IOException;

@WebServlet(urlPatterns = {"/user/profile", "/user/orders"})
public class UserProfileController extends HttpServlet {
    
    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) 
            throws ServletException, IOException {
        User currentUser = (User) req.getSession().getAttribute("currentUser");
        if (currentUser == null) {
            resp.sendRedirect(req.getContextPath() + "/login");
            return;
        }
        
        String uri = req.getRequestURI();
        EntityManager em = JpaUtil.em();
        
        try {
            if (uri.endsWith("/profile")) {
                // Refresh user data from DB
                User user = em.find(User.class, currentUser.getId());
                req.setAttribute("user", user);
                req.getRequestDispatcher("/views/user/profile.jsp").forward(req, resp);
                
            } else if (uri.endsWith("/orders")) {
                // Danh sách đơn hàng của user
                var orders = em.createQuery(
                    "SELECT o FROM Orders o WHERE o.user.id = :uid ORDER BY o.createdDate DESC",
                    model.Orders.class)
                    .setParameter("uid", currentUser.getId())
                    .getResultList();
                
                req.setAttribute("orders", orders);
                req.getRequestDispatcher("/views/user/orders.jsp").forward(req, resp);
            }
        } finally {
            em.close();
        }
    }
    
    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp) 
            throws ServletException, IOException {
        User currentUser = (User) req.getSession().getAttribute("currentUser");
        if (currentUser == null) {
            resp.sendRedirect(req.getContextPath() + "/login");
            return;
        }
        
        String action = req.getParameter("action");
        EntityManager em = JpaUtil.em();
        var tx = em.getTransaction();
        
        try {
            tx.begin();
            User user = em.find(User.class, currentUser.getId());
            
            if ("updateProfile".equals(action)) {
                String fullname = req.getParameter("fullname");
                String phone = req.getParameter("phone");
                String address = req.getParameter("address");
                
                user.setFullname(fullname);
                user.setPhone(phone);
                user.setAddress(address);
                
                em.merge(user);
                tx.commit();
                
                // Update session
                req.getSession().setAttribute("currentUser", user);
                req.setAttribute("success", "Cập nhật thông tin thành công!");
                
            } else if ("changePassword".equals(action)) {
                String oldPassword = req.getParameter("oldPassword");
                String newPassword = req.getParameter("newPassword");
                String confirmPassword = req.getParameter("confirmPassword");
                
                if (!PasswordUtil.verify(oldPassword, user.getPassword())) {
                    req.setAttribute("error", "Mật khẩu cũ không đúng!");
                } else if (!newPassword.equals(confirmPassword)) {
                    req.setAttribute("error", "Mật khẩu xác nhận không khớp!");
                } else if (newPassword.length() < 6) {
                    req.setAttribute("error", "Mật khẩu mới phải có ít nhất 6 ký tự!");
                } else {
                    user.setPassword(PasswordUtil.hash(newPassword));
                    em.merge(user);
                    tx.commit();
                    req.setAttribute("success", "Đổi mật khẩu thành công!");
                }
            }
            
            // Reload user data
            User updatedUser = em.find(User.class, currentUser.getId());
            req.setAttribute("user", updatedUser);
            req.getRequestDispatcher("/views/user/profile.jsp").forward(req, resp);
            
        } catch (Exception e) {
            if (tx.isActive()) tx.rollback();
            e.printStackTrace();
            req.setAttribute("error", "Có lỗi xảy ra: " + e.getMessage());
            req.getRequestDispatcher("/views/user/profile.jsp").forward(req, resp);
        } finally {
            em.close();
        }
    }
}