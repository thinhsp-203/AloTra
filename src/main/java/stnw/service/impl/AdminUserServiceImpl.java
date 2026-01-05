package stnw.service.impl;

import stnw.config.JpaUtil;
import jakarta.persistence.EntityManager;
import stnw.model.User;
import stnw.service.AdminUserService;
import stnw.utils.PasswordUtil;

import java.time.LocalDateTime;

public class AdminUserServiceImpl implements AdminUserService {
    
    @Override
    public void createUser(String username, String email, String password, 
                          String fullname, String phone, String address, 
                          Integer roleId, boolean isActive) {
        // 1. VALIDATION
        validateUserData(null, username, email, phone, password, true);
        
        EntityManager em = JpaUtil.em();
        try {
            em.getTransaction().begin();
            
            // 2. KIỂM TRA TRÙNG LẶP
            checkDuplicates(em, null, username, email, phone);
            
            // 3. TẠO USER MỚI
            User user = new User();
            user.setUsername(username);
            user.setEmail(email);
            user.setPassword(PasswordUtil.hash(password));
            user.setFullname(fullname);
            user.setPhone(phone);
            user.setAddress(address);
            user.setRoleid(roleId != null ? roleId : 3); // Default: Customer
            user.setIsActive(isActive);
            user.setCreatedDate(LocalDateTime.now());
            
            em.persist(user);
            em.getTransaction().commit();
            
        } catch (Exception e) {
            if (em.getTransaction().isActive()) {
                em.getTransaction().rollback();
            }
            throw new RuntimeException("Lỗi khi tạo user: " + e.getMessage(), e);
        } finally {
            em.close();
        }
    }
    
    @Override
    public void updateUser(int userId, String email, String fullname, 
                          String phone, String address, Integer roleId, 
                          boolean isActive, String newPassword) {
        // 1. VALIDATION
        validateUserData(userId, null, email, phone, newPassword, false);
        
        EntityManager em = JpaUtil.em();
        try {
            em.getTransaction().begin();
            
            User user = em.find(User.class, userId);
            if (user == null) {
                throw new IllegalArgumentException("User không tồn tại!");
            }
            
            // 2. KIỂM TRA TRÙNG (chỉ nếu thay đổi)
            if (!user.getEmail().equals(email) || !user.getPhone().equals(phone)) {
                checkDuplicates(em, userId, null, email, phone);
            }
            
            // 3. CẬP NHẬT
            user.setEmail(email);
            user.setFullname(fullname);
            user.setPhone(phone);
            user.setAddress(address);
            // Không cho phép thay đổi role - chỉ cập nhật nếu roleId != null (để backward compatibility)
            // Nhưng trong thực tế, roleId sẽ luôn là null khi update từ form
            if (roleId != null) {
                // Chỉ cho phép set role khi tạo mới, không cho phép thay đổi khi update
                // Giữ nguyên role hiện tại
            }
            user.setIsActive(isActive);
            
            if (newPassword != null && !newPassword.isEmpty()) {
                user.setPassword(PasswordUtil.hash(newPassword));
            }
            
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
            throw new RuntimeException("Lỗi khi cập nhật user: " + e.getMessage(), e);
        } finally {
            em.close();
        }
    }
    
    @Override
    public void toggleUserStatus(int userId, Integer currentUserId) {
        EntityManager em = JpaUtil.em();
        try {
            em.getTransaction().begin();
            
            User user = em.find(User.class, userId);
            if (user == null) {
                throw new IllegalArgumentException("User không tồn tại!");
            }
            
            // Kiểm tra không cho inactive chính mình
            if (currentUserId != null && currentUserId.equals(userId)) {
                throw new IllegalArgumentException("Không thể thay đổi trạng thái của chính mình!");
            }
            
            // Kiểm tra không cho inactive admin khác (roleid = 1)
            if (user.getRoleid() != null && user.getRoleid() == stnw.utils.Roles.ADMIN) {
                throw new IllegalArgumentException("Không thể thay đổi trạng thái của quản trị viên!");
            }
            
            boolean currentStatus = user.getIsActive() != null ? user.getIsActive() : false;
            user.setIsActive(!currentStatus);
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
            throw new RuntimeException("Lỗi khi đổi trạng thái: " + e.getMessage(), e);
        } finally {
            em.close();
        }
    }
    
    @Override
    public void softDeleteUser(int userId, Integer currentUserId) {
        if (currentUserId != null && currentUserId.equals(userId)) {
            throw new IllegalArgumentException("Không thể xóa tài khoản đang đăng nhập!");
        }
        
        EntityManager em = JpaUtil.em();
        try {
            em.getTransaction().begin();
            
            User user = em.find(User.class, userId);
            if (user == null) {
                throw new IllegalArgumentException("User không tồn tại!");
            }
            
            // Kiểm tra không cho xóa admin (roleid = 1)
            if (user.getRoleid() != null && user.getRoleid() == stnw.utils.Roles.ADMIN) {
                throw new IllegalArgumentException("Không thể xóa quản trị viên!");
            }
            
            user.setIsActive(false);
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
            throw new RuntimeException("Lỗi khi xóa user: " + e.getMessage(), e);
        } finally {
            em.close();
        }
    }
    
    @Override
    public void hardDeleteUser(int userId, Integer currentUserId) {
        if (currentUserId != null && currentUserId.equals(userId)) {
            throw new IllegalArgumentException("Không thể xóa tài khoản đang đăng nhập!");
        }
        
        EntityManager em = JpaUtil.em();
        try {
            em.getTransaction().begin();
            
            User user = em.find(User.class, userId);
            if (user == null) {
                throw new IllegalArgumentException("User không tồn tại!");
            }
            
            // Kiểm tra không cho xóa admin (roleid = 1)
            if (user.getRoleid() != null && user.getRoleid() == stnw.utils.Roles.ADMIN) {
                throw new IllegalArgumentException("Không thể xóa vĩnh viễn quản trị viên!");
            }
            
            // Xóa các bản ghi liên quan trước khi xóa user
            // 1. Xóa PointTransaction
            em.createQuery("DELETE FROM PointTransaction pt WHERE pt.user.id = :userId")
              .setParameter("userId", userId)
              .executeUpdate();
            
            // 2. Xóa Notification
            em.createQuery("DELETE FROM Notification n WHERE n.user.id = :userId")
              .setParameter("userId", userId)
              .executeUpdate();
            
            // 3. Xóa WishlistItem
            em.createQuery("DELETE FROM WishlistItem w WHERE w.user.id = :userId")
              .setParameter("userId", userId)
              .executeUpdate();
            
            // 4. Xóa Review
            em.createQuery("DELETE FROM Review r WHERE r.user.id = :userId")
              .setParameter("userId", userId)
              .executeUpdate();
            
            // 5. Xóa OrderDetail trước (vì có foreign key với Orders)
            em.createQuery("DELETE FROM OrderDetail od WHERE od.order.user.id = :userId")
              .setParameter("userId", userId)
              .executeUpdate();
            
            // 6. Xóa Orders
            em.createQuery("DELETE FROM Orders o WHERE o.user.id = :userId")
              .setParameter("userId", userId)
              .executeUpdate();
            
            // 7. Cuối cùng xóa User
            em.remove(user);
            
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
            throw new RuntimeException("Lỗi khi xóa vĩnh viễn user: " + e.getMessage(), e);
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
    
    // ==================== PRIVATE HELPER METHODS ====================
    
    /**
     * Validate dữ liệu đầu vào
     */
    private void validateUserData(Integer userId, String username, String email, 
                                  String phone, String password, boolean isCreate) {
        if (isCreate && (username == null || username.isBlank())) {
            throw new IllegalArgumentException("Username không được rỗng!");
        }
        
        if (email == null || email.isBlank()) {
            throw new IllegalArgumentException("Email không được rỗng!");
        }
        
        if (!email.matches("^[A-Za-z0-9._%+-]+@[A-Za-z0-9.-]+\\.[A-Za-z]{2,63}$")) {
            throw new IllegalArgumentException("Email không hợp lệ!");
        }
        
        if (phone == null || phone.isBlank()) {
            throw new IllegalArgumentException("Số điện thoại là bắt buộc!");
        }
        
        if (!phone.matches("^[0-9]{9,11}$")) {
            throw new IllegalArgumentException("Số điện thoại: 9-11 chữ số");
        }
        
        if (isCreate) {
            if (password == null || password.isEmpty()) {
                throw new IllegalArgumentException("Mật khẩu không được để trống!");
            }
            if (password.length() < 6) {
                throw new IllegalArgumentException("Mật khẩu tối thiểu 6 ký tự");
            }
        }
    }
    
    /**
     * Kiểm tra trùng lặp username/email/phone
     */
    private void checkDuplicates(EntityManager em, Integer userId, 
                                 String username, String email, String phone) {
        // Kiểm tra username (chỉ khi tạo mới)
        if (username != null) {
            Long countUsername = em.createQuery(
                "SELECT COUNT(u) FROM User u WHERE u.username = :username", Long.class)
                .setParameter("username", username)
                .getSingleResult();
            
            if (countUsername > 0) {
                throw new IllegalArgumentException("Username đã tồn tại!");
            }
        }
        
        // Kiểm tra email
        String emailQuery = "SELECT COUNT(u) FROM User u WHERE u.email = :email";
        if (userId != null) {
            emailQuery += " AND u.id <> :userId";
        }
        
        var emailQueryObj = em.createQuery(emailQuery, Long.class)
            .setParameter("email", email);
        
        if (userId != null) {
            emailQueryObj.setParameter("userId", userId);
        }
        
        if (emailQueryObj.getSingleResult() > 0) {
            throw new IllegalArgumentException("Email đã tồn tại!");
        }
        
        // Kiểm tra phone
        String phoneQuery = "SELECT COUNT(u) FROM User u WHERE u.phone = :phone";
        if (userId != null) {
            phoneQuery += " AND u.id <> :userId";
        }
        
        var phoneQueryObj = em.createQuery(phoneQuery, Long.class)
            .setParameter("phone", phone);
        
        if (userId != null) {
            phoneQueryObj.setParameter("userId", userId);
        }
        
        if (phoneQueryObj.getSingleResult() > 0) {
            throw new IllegalArgumentException("Số điện thoại đã tồn tại!");
        }
    }
}
