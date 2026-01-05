package stnw.service.impl;

import stnw.config.JpaUtil;
import jakarta.persistence.EntityManager;
import jakarta.persistence.TypedQuery;
import jakarta.servlet.http.Part;
import stnw.model.Orders;
import stnw.model.User;
import stnw.service.UserProfileService;
import stnw.utils.PasswordUtil;
import stnw.utils.UploadType;
import stnw.utils.UploadUtil;

import java.util.List;

public class UserProfileServiceImpl implements UserProfileService {
    
    @Override
    public void updateProfile(int userId, String fullname, String phone, String address) {
        // Validation
        if (phone == null || phone.isEmpty() || !phone.matches("^[0-9]{9,11}$")) {
            throw new IllegalArgumentException("Số điện thoại không hợp lệ");
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
            throw new IllegalArgumentException("Mật khẩu mới phải có ít nhất 6 ký tự");
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
            
            // Kiểm tra quyền hủy: chỉ được hủy khi status = CHO_XAC_NHAN
            stnw.utils.OrderStatus currentStatus = stnw.utils.OrderStatus.fromOldString(order.getOrder_status());
            if (currentStatus != stnw.utils.OrderStatus.CHO_XAC_NHAN) {
                throw new IllegalArgumentException("Bạn chỉ có thể hủy đơn hàng khi đơn đang ở trạng thái 'Chờ xác nhận'!");
            }
            
            order.setOrder_status(stnw.utils.OrderStatus.HUY_BOI_KHACH.getDisplayName());
            order.setUpdatedDate(java.time.LocalDateTime.now());
            
            // Xử lý payment status khi khách hủy đơn
            // Nếu đơn hàng có payment_method là ATM, MOMO, VNPAY và đã thanh toán thì chuyển sang Hoàn tiền
            String currentPaymentStatus = order.getPayment_status();
            String paymentMethod = order.getPayment_method();
            if ("Đã thanh toán".equals(currentPaymentStatus) && paymentMethod != null) {
                String method = paymentMethod.trim().toUpperCase();
                if ("ATM".equals(method) || "MOMO".equals(method) || "VNPAY".equals(method) || "ONLINE".equals(method)) {
                    order.setPayment_status(stnw.utils.PaymentStatus.HOAN_TIEN.getDisplayName());
                } else {
                    order.setPayment_status(stnw.utils.PaymentStatus.CHUA_THANH_TOAN.getDisplayName());
                }
            } else {
                order.setPayment_status(stnw.utils.PaymentStatus.CHUA_THANH_TOAN.getDisplayName());
            }
            
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
