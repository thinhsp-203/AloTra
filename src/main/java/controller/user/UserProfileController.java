package controller.user;

import config.JpaUtil;
import jakarta.persistence.EntityManager;
import jakarta.persistence.TypedQuery;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.MultipartConfig; // THÊM IMPORT
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.Part; // THÊM IMPORT
import model.User;
import model.Orders;
import utils.Constant; // THÊM IMPORT
import utils.PasswordUtil;

import java.io.File; // THÊM IMPORT
import java.io.IOException;
import java.io.InputStream; // THÊM IMPORT
import java.nio.file.Files; // THÊM IMPORT
import java.nio.file.Paths; // THÊM IMPORT
import java.util.List;
import java.util.UUID; // THÊM IMPORT

@MultipartConfig(fileSizeThreshold = 2*1024*1024, maxFileSize = 10*1024*1024, maxRequestSize = 50*1024*1024) // THÊM ANNOTATION
@WebServlet(urlPatterns = {"/user/profile", "/user/orders"})
public class UserProfileController extends HttpServlet {
    
    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) 
            throws ServletException, IOException {
        User currentUser = (User) req.getSession().getAttribute("currentUser");
        if (currentUser == null) {
            req.getSession().setAttribute("redirectAfterLogin", req.getRequestURI());
            resp.sendRedirect(req.getContextPath() + "/login");
            return;
        }
        
        String uri = req.getRequestURI();
        EntityManager em = JpaUtil.em();
        
        try {
            if (uri.endsWith("/profile")) {
                // Refresh user data from DB
                User user = em.find(User.class, currentUser.getId());
                if (user == null) {
                    req.getSession().invalidate();
                    resp.sendRedirect(req.getContextPath() + "/login");
                    return;
                }
                req.setAttribute("user", user);
                req.getRequestDispatcher("/views/user/profile.jsp").forward(req, resp);
                
            } else if (uri.endsWith("/orders")) {
                String status = req.getParameter("status");
                String keyword = req.getParameter("keyword"); // NEW: Search functionality
                
                StringBuilder jpql = new StringBuilder(
                    "SELECT DISTINCT o FROM Orders o " +
                    "LEFT JOIN FETCH o.orderDetails od " +
                    "LEFT JOIN FETCH od.product " +
                    "WHERE o.user.id = :uid "
                );

                if (status != null && !status.isEmpty() && !status.equals("Tất cả")) {
                    jpql.append("AND o.order_status = :status ");
                }
                
                if (keyword != null && !keyword.trim().isEmpty()) {
                    jpql.append("AND (o.order_id = :orderId OR o.fullname LIKE :kw OR o.phone LIKE :kw) ");
                }
                
                jpql.append("ORDER BY o.createdDate DESC");

                TypedQuery<Orders> query = em.createQuery(jpql.toString(), Orders.class)
                                             .setParameter("uid", currentUser.getId());

                if (status != null && !status.isEmpty() && !status.equals("Tất cả")) {
                    query.setParameter("status", status);
                }
                
                if (keyword != null && !keyword.trim().isEmpty()) {
                    try {
                        query.setParameter("orderId", Integer.parseInt(keyword));
                    } catch (NumberFormatException e) {
                        query.setParameter("orderId", -1);
                    }
                    query.setParameter("kw", "%" + keyword.trim() + "%");
                }
                
                List<Orders> orders = query.getResultList();
                
                req.setAttribute("orders", orders);
                req.setAttribute("currentStatus", status);
                req.setAttribute("keyword", keyword);
                req.getRequestDispatcher("/views/user/orders.jsp").forward(req, resp);
            }
        } catch (Exception e) {
            e.printStackTrace();
            req.setAttribute("error", "Có lỗi xảy ra: " + e.getMessage());
            req.getRequestDispatcher("/views/user/profile.jsp").forward(req, resp);
        } finally {
            if (em != null && em.isOpen()) {
                em.close();
            }
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
            
            if ("cancelOrder".equals(action)) {
                int orderId = Integer.parseInt(req.getParameter("orderId"));
                Orders order = em.find(Orders.class, orderId);

                if (order != null && 
                    order.getUser().getId().equals(currentUser.getId()) && 
                    "Chờ xác nhận".equals(order.getOrder_status())) {
                    
                    order.setOrder_status("Đã hủy");
                    order.setUpdatedDate(java.time.LocalDateTime.now());
                    em.merge(order);
                    
                    req.getSession().setAttribute("orderSuccess", "Đã hủy đơn hàng #" + orderId);
                } else {
                    req.getSession().setAttribute("orderError", "Không thể hủy đơn hàng #" + orderId);
                }
                tx.commit();
                resp.sendRedirect(req.getContextPath() + "/user/orders");
                return;
            }
            
            // Refresh user from DB
            User user = em.find(User.class, currentUser.getId());
            
            if ("updateProfile".equals(action)) {
                String fullname = sanitize(req.getParameter("fullname"));
                String phone = sanitize(req.getParameter("phone"));
                String address = sanitize(req.getParameter("address"));
                
                // Validate phone (bắt buộc)
                if (phone == null || phone.isEmpty() || !phone.matches("^[0-9]{9,11}$")) {
                    req.setAttribute("error", "Số điện thoại không hợp lệ!");
                    req.setAttribute("user", user);
                    tx.rollback(); // Rollback vì có lỗi
                    req.getRequestDispatcher("/views/user/profile.jsp").forward(req, resp);
                    return;
                }
                
                // Kiểm tra SĐT trùng
                if (!phone.equals(user.getPhone())) {
                    Long countPhone = em.createQuery("SELECT COUNT(u) FROM User u WHERE u.phone = :phone AND u.id <> :userId", Long.class)
                        .setParameter("phone", phone)
                        .setParameter("userId", user.getId())
                        .getSingleResult();
                    if (countPhone > 0) {
                        req.setAttribute("error", "Số điện thoại đã tồn tại!");
                        req.setAttribute("user", user);
                        tx.rollback();
                        req.getRequestDispatcher("/views/user/profile.jsp").forward(req, resp);
                        return;
                    }
                }
                
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
                } else if (newPassword.length() > 100) {
                    req.setAttribute("error", "Mật khẩu mới quá dài!");
                } else {
                    user.setPassword(PasswordUtil.hash(newPassword));
                    em.merge(user);
                    tx.commit();
                    req.setAttribute("success", "Đổi mật khẩu thành công!");
                }
            }
            // THÊM MỚI: Logic đổi avatar
            else if ("changeAvatar".equals(action)) {
                Part filePart = req.getPart("avatar");
                if (filePart == null || filePart.getSize() == 0) {
                    req.setAttribute("error", "Vui lòng chọn một file ảnh!");
                } else {
                    String originalFileName = Paths.get(filePart.getSubmittedFileName()).getFileName().toString();
                    String extension = "";
                    int i = originalFileName.lastIndexOf('.');
                    if (i > 0) {
                        extension = originalFileName.substring(i); // .png
                    }
                    
                    // Tạo tên file duy nhất
                    String finalFileName = "user-" + user.getId() + "-" + UUID.randomUUID().toString() + extension;
                    
                    File uploadDir = new File(Constant.UPLOAD_DIRECTORY);
                    if (!uploadDir.exists()) uploadDir.mkdirs();
                    File fileToSave = new File(uploadDir, finalFileName);

                    // Xóa avatar cũ (nếu có)
                    if (user.getAvatar() != null && !user.getAvatar().isEmpty()) {
                        File oldFile = new File(uploadDir, user.getAvatar());
                        if (oldFile.exists()) {
                            oldFile.delete();
                        }
                    }

                    // Lưu file mới
                    try (InputStream input = filePart.getInputStream()) {
                        Files.copy(input, fileToSave.toPath());
                    }
                    
                    // Cập nhật DB
                    user.setAvatar(finalFileName);
                    em.merge(user);
                    tx.commit();
                    
                    // Cập nhật session
                    req.getSession().setAttribute("currentUser", user);
                    req.setAttribute("success", "Cập nhật ảnh đại diện thành công!");
                }
            }
            
            req.setAttribute("user", em.find(User.class, currentUser.getId()));
            req.getRequestDispatcher("/views/user/profile.jsp").forward(req, resp);
            
        } catch (Exception e) {
            if (tx.isActive()) tx.rollback();
            e.printStackTrace();
            req.setAttribute("error", "Có lỗi xảy ra: " + e.getMessage());
            req.setAttribute("user", em.find(User.class, currentUser.getId()));
            req.getRequestDispatcher("/views/user/profile.jsp").forward(req, resp);
        } finally {
            if (em != null && em.isOpen()) {
                em.close();
            }
        }
    }
    
    private String sanitize(String input) {
        if (input == null) return null;
        return input.trim().replaceAll("<[^>]*>", ""); // Remove HTML tags
    }
}