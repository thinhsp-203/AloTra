package service.impl;

import config.JpaUtil;
import jakarta.persistence.EntityManager;
import jakarta.persistence.TypedQuery;
import jakarta.servlet.http.Part;
import model.Orders;
import model.User;
import service.UserProfileService;
import utils.PasswordUtil;
import utils.UploadType;
import utils.UploadUtil;

import java.util.List;

public class UserProfileServiceImpl implements UserProfileService {
    
    @Override
    public void updateProfile(int userId, String fullname, String phone, String address) {
        // Validation
        if (phone == null || phone.isEmpty() || !phone.matches("^[0-9]{9,11}$")) {
            throw new IllegalArgumentException("Số điện thoại không hợp lệ!");
        }
        
        EntityManager em = JpaUtil.em();
        try {
            em.getTransaction().begin();
            
            User user = em.find(User.class, userId);
            if (user == null) {
                throw new IllegalArgumentException("User không tồn tại!");
            }
            
            // Kiểm tra SĐT trùng
            if (!phone.equals(user.getPhone())) {
                Long countPhone = em.createQuery(
                    "SELECT COUNT(u) FROM User u WHERE u.phone = :phone AND u.id <> :userId", 
                    Long.class)
                    .setParameter("phone", phone)
                    .setParameter("userId", userId)
                    .getSingleResult();
                
                if (countPhone > 0) {
                    throw new IllegalArgumentException("Số điện thoại đã tồn tại!");
                }
            }
            
            user.setFullname(fullname);
            user.setPhone(phone);
            user.setAddress(address);
            em.merge(user);
            
            em.getTransaction().commit();
            
        } catch (IllegalArgumentException e) {
            if (em.getTransaction().isActive()) {
                em.getTransaction().rollback();
            }
            throw e;
        } catch (Exception e) {
            if (em.getTransaction().isActive()) {
                em.getTransaction().rollback();
            }
            throw new RuntimeException("Lỗi khi cập nhật profile: " + e.getMessage(), e);
        } finally {
            em.close();
        }
    }
    
    @Override
    public void changePassword(int userId, String oldPassword, String newPassword, 
                              String confirmPassword) {
        // Validation
        if (!newPassword.equals(confirmPassword)) {
            throw new IllegalArgumentException("Mật khẩu xác nhận không khớp!");
        }
        
        if (newPassword.length() < 6) {
            throw new IllegalArgumentException("Mật khẩu mới phải có ít nhất 6 ký tự!");
        }
        
        if (newPassword.length() > 100) {
            throw new IllegalArgumentException("Mật khẩu mới quá dài!");
        }
        
        EntityManager em = JpaUtil.em();
        try {
            em.getTransaction().begin();
            
            User user = em.find(User.class, userId);
            if (user == null) {
                throw new IllegalArgumentException("User không tồn tại!");
            }
            
            // Kiểm tra mật khẩu cũ
            if (!PasswordUtil.verify(oldPassword, user.getPassword())) {
                throw new IllegalArgumentException("Mật khẩu cũ không đúng!");
            }
            
            // Cập nhật mật khẩu mới
            user.setPassword(PasswordUtil.hash(newPassword));
            em.merge(user);
            
            em.getTransaction().commit();
            
        } catch (IllegalArgumentException e) {
            if (em.getTransaction().isActive()) {
                em.getTransaction().rollback();
            }
            throw e;
        } catch (Exception e) {
            if (em.getTransaction().isActive()) {
                em.getTransaction().rollback();
            }
            throw new RuntimeException("Lỗi khi đổi mật khẩu: " + e.getMessage(), e);
        } finally {
            em.close();
        }
    }
    
    @Override
    public void changeAvatar(int userId, Part avatarFile, jakarta.servlet.ServletContext servletContext) {
        if (avatarFile == null || avatarFile.getSize() == 0) {
            throw new IllegalArgumentException("Vui lòng chọn một file ảnh!");
        }
        
        EntityManager em = JpaUtil.em();
        try {
            em.getTransaction().begin();
            
            User user = em.find(User.class, userId);
            if (user == null) {
                throw new IllegalArgumentException("User không tồn tại!");
            }
            
            // Xóa avatar cũ
            if (user.getAvatar() != null && !user.getAvatar().isEmpty()) {
                UploadUtil.deleteOldImage(user.getAvatar(), servletContext);
            }
            
            // Upload file mới
            String uploadedPath = UploadUtil.save(avatarFile, UploadType.USERS, servletContext);
            if (uploadedPath == null) {
                throw new IllegalArgumentException("Không thể upload file!");
            }
            
            // Cập nhật DB - lưu relative path: "uploads/users/filename"
            user.setAvatar(uploadedPath);
            em.merge(user);
            
            em.getTransaction().commit();
            
        } catch (IllegalArgumentException e) {
            if (em.getTransaction().isActive()) {
                em.getTransaction().rollback();
            }
            throw e;
        } catch (Exception e) {
            if (em.getTransaction().isActive()) {
                em.getTransaction().rollback();
            }
            throw new RuntimeException("Lỗi khi upload avatar: " + e.getMessage(), e);
        } finally {
            em.close();
        }
    }
    
    @Override
    public void cancelOrder(int userId, int orderId) {
        EntityManager em = JpaUtil.em();
        try {
            em.getTransaction().begin();
            
            Orders order = em.find(Orders.class, orderId);
            
            if (order == null) {
                throw new IllegalArgumentException("Đơn hàng không tồn tại!");
            }
            
            if (!order.getUser().getId().equals(userId)) {
                throw new IllegalArgumentException("Đơn hàng không thuộc về bạn!");
            }
            
            if (!"Chờ xác nhận".equals(order.getOrder_status())) {
                throw new IllegalArgumentException("Không thể hủy đơn hàng này!");
            }
            
            order.setOrder_status("Hủy Đơn");
            order.setUpdatedDate(java.time.LocalDateTime.now());
            em.merge(order);
            
            em.getTransaction().commit();
            
        } catch (IllegalArgumentException e) {
            if (em.getTransaction().isActive()) {
                em.getTransaction().rollback();
            }
            throw e;
        } catch (Exception e) {
            if (em.getTransaction().isActive()) {
                em.getTransaction().rollback();
            }
            throw new RuntimeException("Lỗi khi hủy đơn hàng: " + e.getMessage(), e);
        } finally {
            em.close();
        }
    }
    
    @Override
    public List<Orders> getUserOrders(int userId, String status, String keyword) {
        EntityManager em = JpaUtil.em();
        try {
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
                .setParameter("uid", userId);

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
            
            return query.getResultList();
            
        } finally {
            em.close();
        }
    }
    
    @Override
    public User getUserById(int userId) {
        EntityManager em = JpaUtil.em();
        try {
            return em.find(User.class, userId);
        } finally {
            em.close();
        }
    }
}