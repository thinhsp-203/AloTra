package service;

import jakarta.servlet.http.Part;
import model.Orders;
import model.User;

import java.util.List;

/**
 * Service xử lý thông tin cá nhân người dùng
 */
public interface UserProfileService {
    
    /**
     * Cập nhật thông tin profile
     * @throws IllegalArgumentException nếu phone trùng với user khác
     */
    void updateProfile(int userId, String fullname, String phone, String address);
    
    /**
     * Đổi mật khẩu
     * @throws IllegalArgumentException nếu mật khẩu cũ sai hoặc mật khẩu mới không hợp lệ
     */
    void changePassword(int userId, String oldPassword, String newPassword, String confirmPassword);
    
    /**
     * Đổi avatar
     * @throws IllegalArgumentException nếu file không hợp lệ
     */
    void changeAvatar(int userId, Part avatarFile);
    
    /**
     * Hủy đơn hàng (chỉ được hủy nếu đơn đang "Chờ xác nhận")
     * @throws IllegalArgumentException nếu không thể hủy
     */
    void cancelOrder(int userId, int orderId);
    
    /**
     * Lấy danh sách đơn hàng của user
     */
    List<Orders> getUserOrders(int userId, String status, String keyword);
    
    /**
     * Lấy thông tin user (refresh từ DB)
     */
    User getUserById(int userId);
}