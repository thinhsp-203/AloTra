package controller.admin;

import config.JpaUtil;
import jakarta.persistence.EntityManager;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import model.User;
import utils.PasswordUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.time.LocalDateTime;

@WebServlet(urlPatterns = {"/admin/users/create", "/admin/users/edit", "/admin/users/save", "/admin/users/delete", "/admin/users/toggle-status"})
public class AdminUserController extends HttpServlet {
    
    private static final Logger logger = LoggerFactory.getLogger(AdminUserController.class);
    
    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) 
            throws ServletException, IOException {
        String uri = req.getRequestURI();
        EntityManager em = JpaUtil.em();
        
        try {
            if (uri.endsWith("/create")) {
                // Hiển thị form tạo user
                req.getRequestDispatcher("/views/admin/user_form.jsp").forward(req, resp);
                
            } else if (uri.endsWith("/edit")) {
                // Hiển thị form sửa user
                int userId = Integer.parseInt(req.getParameter("id"));
                User user = em.find(User.class, userId);
                
                if (user == null) {
                    resp.sendError(HttpServletResponse.SC_NOT_FOUND, "User not found");
                    return;
                }
                
                req.setAttribute("user", user);
                req.getRequestDispatcher("/views/admin/user_form.jsp").forward(req, resp);
                
            } else if (uri.endsWith("/delete")) {
                // Xóa user
                int userId = Integer.parseInt(req.getParameter("id"));
                var tx = em.getTransaction();
                
                try {
                    tx.begin();
                    User user = em.find(User.class, userId);
                    
                    if (user != null) {
                        // Không cho xóa chính mình
                        User currentUser = (User) req.getSession().getAttribute("currentUser");
                        if (currentUser.getId().equals(userId)) {
                            req.getSession().setAttribute("error", "Không thể xóa tài khoản của chính bạn!");
                        } else {
                            em.remove(user);
                            req.getSession().setAttribute("success", "Đã xóa người dùng thành công!");
                        }
                    }
                    
                    tx.commit();
                } catch (Exception e) {
                    if (tx.isActive()) tx.rollback();
                    logger.error("Error deleting user", e);
                    req.getSession().setAttribute("error", "Có lỗi khi xóa người dùng: " + e.getMessage());
                }
                
                resp.sendRedirect(req.getContextPath() + "/admin/users");
                
            } else if (uri.endsWith("/toggle-status")) {
                // Bật/tắt trạng thái active
                int userId = Integer.parseInt(req.getParameter("id"));
                var tx = em.getTransaction();
                
                try {
                    tx.begin();
                    User user = em.find(User.class, userId);
                    
                    if (user != null) {
                        user.setIsActive(!user.getIsActive());
                        em.merge(user);
                        req.getSession().setAttribute("success", "Đã cập nhật trạng thái người dùng!");
                    }
                    
                    tx.commit();
                } catch (Exception e) {
                    if (tx.isActive()) tx.rollback();
                    logger.error("Error toggling user status", e);
                    req.getSession().setAttribute("error", "Có lỗi khi cập nhật trạng thái!");
                }
                
                resp.sendRedirect(req.getContextPath() + "/admin/users");
            }
        } finally {
            em.close();
        }
    }
    
    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp) 
            throws ServletException, IOException {
        String uri = req.getRequestURI();
        
        if (!uri.endsWith("/save")) {
            resp.sendError(HttpServletResponse.SC_NOT_FOUND);
            return;
        }
        
        // Lấy thông tin từ form
        String idParam = req.getParameter("id");
        Integer userId = (idParam != null && !idParam.isEmpty()) ? Integer.parseInt(idParam) : null;
        
        String username = req.getParameter("username");
        String email = req.getParameter("email");
        String fullname = req.getParameter("fullname");
        String phone = req.getParameter("phone");
        String address = req.getParameter("address");
        String password = req.getParameter("password");
        String roleIdParam = req.getParameter("roleid");
        Integer roleId = (roleIdParam != null && !roleIdParam.isEmpty()) ? Integer.parseInt(roleIdParam) : 3;
        boolean isActive = "on".equals(req.getParameter("isActive"));
        
        EntityManager em = JpaUtil.em();
        var tx = em.getTransaction();
        
        try {
            tx.begin();
            
            User user;
            if (userId == null) {
                // Tạo mới
                user = new User();
                user.setUsername(username);
                user.setEmail(email);
                user.setCreatedDate(LocalDateTime.now());
                
                // Kiểm tra trùng username/email
                Long countUsername = em.createQuery("SELECT COUNT(u) FROM User u WHERE u.username = :username", Long.class)
                    .setParameter("username", username)
                    .getSingleResult();
                    
                Long countEmail = em.createQuery("SELECT COUNT(u) FROM User u WHERE u.email = :email", Long.class)
                    .setParameter("email", email)
                    .getSingleResult();
                
                if (countUsername > 0) {
                    req.getSession().setAttribute("error", "Username đã tồn tại!");
                    tx.rollback();
                    resp.sendRedirect(req.getContextPath() + "/admin/users/create");
                    return;
                }
                
                if (countEmail > 0) {
                    req.getSession().setAttribute("error", "Email đã tồn tại!");
                    tx.rollback();
                    resp.sendRedirect(req.getContextPath() + "/admin/users/create");
                    return;
                }
                
                // Hash password
                if (password == null || password.isEmpty()) {
                    req.getSession().setAttribute("error", "Mật khẩu không được để trống!");
                    tx.rollback();
                    resp.sendRedirect(req.getContextPath() + "/admin/users/create");
                    return;
                }
                user.setPassword(PasswordUtil.hash(password));
                
            } else {
                // Cập nhật
                user = em.find(User.class, userId);
                if (user == null) {
                    resp.sendError(HttpServletResponse.SC_NOT_FOUND, "User not found");
                    tx.rollback();
                    return;
                }
                
                // Cập nhật password nếu có
                if (password != null && !password.isEmpty()) {
                    user.setPassword(PasswordUtil.hash(password));
                }
            }
            
            // Cập nhật thông tin chung
            user.setFullname(fullname);
            user.setPhone(phone);
            user.setAddress(address);
            user.setRoleid(roleId);
            user.setIsActive(isActive);
            
            if (userId == null) {
                em.persist(user);
                req.getSession().setAttribute("success", "Đã tạo người dùng mới thành công!");
            } else {
                em.merge(user);
                req.getSession().setAttribute("success", "Đã cập nhật thông tin người dùng!");
            }
            
            tx.commit();
            resp.sendRedirect(req.getContextPath() + "/admin/users");
            
        } catch (Exception e) {
            if (tx.isActive()) tx.rollback();
            logger.error("Error saving user", e);
            req.getSession().setAttribute("error", "Có lỗi khi lưu người dùng: " + e.getMessage());
            
            if (userId == null) {
                resp.sendRedirect(req.getContextPath() + "/admin/users/create");
            } else {
                resp.sendRedirect(req.getContextPath() + "/admin/users/edit?id=" + userId);
            }
        } finally {
            em.close();
        }
    }
}