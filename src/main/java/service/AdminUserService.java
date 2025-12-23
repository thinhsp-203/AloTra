package service;

import model.User;

/**
 * Service xử lý logic quản lý User cho Admin
 */
public interface AdminUserService {
    
    /**
     * Tạo user mới
     * @throws IllegalArgumentException nếu email/username/phone đã tồn tại hoặc dữ liệu không hợp lệ
     */
    void createUser(String username, String email, String password, 
                   String fullname, String phone, String address, 
                   Integer roleId, boolean isActive);
    
    /**
     * Cập nhật thông tin user
     * @param newPassword Mật khẩu mới (null nếu không đổi)
     * @throws IllegalArgumentException nếu email/phone trùng với user khác
     */
    void updateUser(int userId, String email, String fullname, 
                   String phone, String address, Integer roleId, 
                   boolean isActive, String newPassword);
    
    /**
     * Đổi trạng thái active/inactive
     */
    void toggleUserStatus(int userId);
    
    /**
     * Xóa mềm user (set isActive = false)
     * @throws IllegalArgumentException nếu cố xóa chính mình
     */
    void softDeleteUser(int userId, Integer currentUserId);
    
    /**
     * Lấy thông tin user theo ID
     */
    User getUserById(int userId);
}