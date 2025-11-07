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
                
            } else if (uri.endsWith("/delete") || uri.endsWith("/toggle-status")) {
                // CHUYỂN HƯỚNG NẾU DÙNG GET
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
        EntityManager em = JpaUtil.em();
        var tx = em.getTransaction();
        
        // Lấy ID trước để xử lý lỗi redirect
        String idParam = req.getParameter("id");
        Integer userId = (idParam != null && !idParam.isEmpty()) ? Integer.parseInt(idParam) : null;
            
        try {
            if (uri.endsWith("/save")) {
                
                String username = req.getParameter("username");
                String email = req.getParameter("email");
                String fullname = req.getParameter("fullname");
                String phone = req.getParameter("phone");
                String address = req.getParameter("address");
                String password = req.getParameter("password");
                String roleIdParam = req.getParameter("roleid");
                Integer roleId = (roleIdParam != null && !roleIdParam.isEmpty()) ? Integer.parseInt(roleIdParam) : 3;
                boolean isActive = "on".equals(req.getParameter("isActive"));

                // CẬP NHẬT: Validate SĐT bắt buộc
                if (phone == null || phone.trim().isEmpty()) {
                    req.getSession().setAttribute("error", "Số điện thoại là bắt buộc!");
                    if (userId == null) {
                        resp.sendRedirect(req.getContextPath() + "/admin/users/create");
                    } else {
                        resp.sendRedirect(req.getContextPath() + "/admin/users/edit?id=" + userId);
                    }
                    return;
                }
                
                tx.begin();
                
                User user;
                if (userId == null) {
                    // TẠO MỚI
                    user = new User();
                    user.setUsername(username);
                    user.setCreatedDate(LocalDateTime.now());
                    
                    // CẬP NHẬT: Kiểm tra trùng cả 3 trường
                    Long countUsername = em.createQuery("SELECT COUNT(u) FROM User u WHERE u.username = :username", Long.class)
                        .setParameter("username", username)
                        .getSingleResult();
                    Long countEmail = em.createQuery("SELECT COUNT(u) FROM User u WHERE u.email = :email", Long.class)
                        .setParameter("email", email)
                        .getSingleResult();
                    Long countPhone = em.createQuery("SELECT COUNT(u) FROM User u WHERE u.phone = :phone", Long.class)
                        .setParameter("phone", phone)
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
                    if (countPhone > 0) {
                        req.getSession().setAttribute("error", "Số điện thoại đã tồn tại!");
                        tx.rollback();
                        resp.sendRedirect(req.getContextPath() + "/admin/users/create");
                        return;
                    }
                    
                    user.setEmail(email);
                    user.setPhone(phone);
                    
                    // Hash password
                    if (password == null || password.isEmpty()) {
                        req.getSession().setAttribute("error", "Mật khẩu không được để trống!");
                        tx.rollback();
                        resp.sendRedirect(req.getContextPath() + "/admin/users/create");
                        return;
                    }
                    user.setPassword(PasswordUtil.hash(password));
                    
                } else {
                    // CẬP NHẬT
                    user = em.find(User.class, userId);
                    if (user == null) {
                        resp.sendError(HttpServletResponse.SC_NOT_FOUND, "User not found");
                        tx.rollback();
                        return;
                    }

                    // CẬP NHẬT: Kiểm tra trùng email (nếu email bị thay đổi)
                    if (!user.getEmail().equals(email)) {
                        Long countEmail = em.createQuery("SELECT COUNT(u) FROM User u WHERE u.email = :email AND u.id <> :userId", Long.class)
                            .setParameter("email", email)
                            .setParameter("userId", userId)
                            .getSingleResult();
                        if (countEmail > 0) {
                            req.getSession().setAttribute("error", "Email đã tồn tại!");
                            tx.rollback();
                            resp.sendRedirect(req.getContextPath() + "/admin/users/edit?id=" + userId);
                            return;
                        }
                    }
                    user.setEmail(email); // Cập nhật email
                    
                    // CẬP NHẬT: Kiểm tra trùng SĐT (nếu SĐT bị thay đổi)
                    if (!user.getPhone().equals(phone)) {
                         Long countPhone = em.createQuery("SELECT COUNT(u) FROM User u WHERE u.phone = :phone AND u.id <> :userId", Long.class)
                            .setParameter("phone", phone)
                            .setParameter("userId", userId)
                            .getSingleResult();
                         if (countPhone > 0) {
                            req.getSession().setAttribute("error", "Số điện thoại đã tồn tại!");
                            tx.rollback();
                            resp.sendRedirect(req.getContextPath() + "/admin/users/edit?id=" + userId);
                            return;
                         }
                    }
                    user.setPhone(phone); // Cập nhật SĐT
                    
                    // Cập nhật password nếu có
                    if (password != null && !password.isEmpty()) {
                        user.setPassword(PasswordUtil.hash(password));
                    }
                }
                
                // Cập nhật thông tin chung
                user.setFullname(fullname);
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
                
            } else if (uri.endsWith("/delete")) {
                // Xóa user
                int deleteUserId = Integer.parseInt(req.getParameter("id"));
                tx.begin();
                User user = em.find(User.class, deleteUserId);
                
                if (user != null) {
                    User currentUser = (User) req.getSession().getAttribute("currentUser");
                    if (currentUser.getId().equals(deleteUserId)) {
                        req.getSession().setAttribute("error", "Không thể xóa tài khoản của chính bạn!");
                    } else {
                        em.remove(user);
                        req.getSession().setAttribute("success", "Đã xóa người dùng thành công!");
                    }
                }
                tx.commit();

            } else if (uri.endsWith("/toggle-status")) {
                // Bật/tắt trạng thái active
                int toggleUserId = Integer.parseInt(req.getParameter("id"));
                tx.begin();
                User user = em.find(User.class, toggleUserId);
                
                if (user != null) {
                    user.setIsActive(!user.getIsActive());
                    em.merge(user);
                    req.getSession().setAttribute("success", "Đã cập nhật trạng thái người dùng!");
                }
                tx.commit();
            } else {
                 resp.sendError(HttpServletResponse.SC_NOT_FOUND);
                 return;
            }

        } catch (Exception e) {
            if (tx.isActive()) tx.rollback();
            logger.error("Error saving/deleting user", e);
            req.getSession().setAttribute("error", "Có lỗi khi xử lý: " + e.getMessage());
            
            if (uri.endsWith("/save")) {
                 if (userId == null) {
                    resp.sendRedirect(req.getContextPath() + "/admin/users/create");
                 } else {
                    resp.sendRedirect(req.getContextPath() + "/admin/users/edit?id=" + userId);
                 }
                 return;
            }
        } finally {
            if (em.isOpen()) em.close();
        }
        
        // Redirect về trang list sau mọi hành động POST
        resp.sendRedirect(req.getContextPath() + "/admin/users");
    }
}