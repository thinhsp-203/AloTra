package stnw.service.impl;

import stnw.dao.OrderDao;
import stnw.dao.UserDao;
import stnw.dao.impl.OrderDaoImpl;
import stnw.dao.impl.UserDaoImpl;
import jakarta.servlet.http.Part;
import stnw.model.Orders;
import stnw.model.User;
import stnw.service.UserProfileService;
import stnw.utils.PasswordUtils;
import stnw.utils.UploadType;
import stnw.utils.UploadUtils;

import java.util.List;

public class UserProfileServiceImpl implements UserProfileService {
    
    private final UserDao userDao = new UserDaoImpl();
    private final OrderDao orderDao = new OrderDaoImpl();
    
    @Override
    public void updateProfile(int userId, String fullname, String phone, String address) {
        // Validation
        if (phone == null || phone.isEmpty() || !phone.matches("^[0-9]{9,11}$")) {
            throw new IllegalArgumentException("Số điện thoại không hợp lệ");
        }
        
        User user = userDao.findById(userId);
        if (user == null) {
            throw new IllegalArgumentException("User không tồn tại!");
        }
        
        // Kiểm tra SĐT trùng
        if (!phone.equals(user.getPhone())) {
            if (userDao.existsByPhoneExcludingUser(phone, userId)) {
                throw new IllegalArgumentException("Số điện thoại đã tồn tại!");
            }
        }
        
        user.setFullname(fullname);
        user.setPhone(phone);
        user.setAddress(address);
        userDao.update(user);
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
        
        User user = userDao.findById(userId);
        if (user == null) {
            throw new IllegalArgumentException("User không tồn tại!");
        }
        
        // Kiểm tra mật khẩu cũ
        if (!PasswordUtils.verifyPassword(oldPassword, user.getPassword())) {
            throw new IllegalArgumentException("Mật khẩu cũ không đúng!");
        }
        
        // Cập nhật mật khẩu mới
        user.setPassword(PasswordUtils.hashPassword(newPassword));
        userDao.update(user);
    }
    
    @Override
    public void changeAvatar(int userId, Part avatarFile, jakarta.servlet.ServletContext servletContext) {
        if (avatarFile == null || avatarFile.getSize() == 0) {
            throw new IllegalArgumentException("Vui lòng chọn một file ảnh!");
        }
        
        User user = userDao.findById(userId);
        if (user == null) {
            throw new IllegalArgumentException("User không tồn tại!");
        }
        
        // Xóa avatar cũ
        if (user.getAvatar() != null && !user.getAvatar().isEmpty()) {
            UploadUtils.deleteOldImage(user.getAvatar(), servletContext);
        }
        
        // Upload file mới
        String uploadedPath = UploadUtils.save(avatarFile, UploadType.USERS, servletContext);
        if (uploadedPath == null) {
            throw new IllegalArgumentException("Không thể upload file!");
        }
        
        // Cập nhật DB - lưu relative path: "uploads/users/filename"
        user.setAvatar(uploadedPath);
        userDao.update(user);
    }
    
    @Override
    public void cancelOrder(int userId, int orderId) {
        Orders order = orderDao.findById(orderId);
        
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
        
        orderDao.update(order);
    }
    
    @Override
    public List<Orders> getUserOrders(int userId, String status, String keyword) {
        return orderDao.findByUserId(userId, status, keyword);
    }
    
    @Override
    public User getUserById(int userId) {
        return userDao.findById(userId);
    }
}
