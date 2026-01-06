package stnw.service.impl;

import stnw.dao.NotificationDao;
import stnw.dao.OrderDao;
import stnw.dao.PointTransactionDao;
import stnw.dao.ReviewDao;
import stnw.dao.UserDao;
import stnw.dao.WishlistDao;
import stnw.dao.impl.NotificationDaoImpl;
import stnw.dao.impl.OrderDaoImpl;
import stnw.dao.impl.PointTransactionDaoImpl;
import stnw.dao.impl.ReviewDaoImpl;
import stnw.dao.impl.UserDaoImpl;
import stnw.dao.impl.WishlistDaoImpl;
import stnw.model.User;
import stnw.service.AdminUserService;
import stnw.utils.PasswordUtils;

import java.time.LocalDateTime;

public class AdminUserServiceImpl implements AdminUserService {
    
    private final UserDao userDao = new UserDaoImpl();
    private final PointTransactionDao pointTransactionDao = new PointTransactionDaoImpl();
    private final NotificationDao notificationDao = new NotificationDaoImpl();
    private final WishlistDao wishlistDao = new WishlistDaoImpl();
    private final ReviewDao reviewDao = new ReviewDaoImpl();
    private final OrderDao orderDao = new OrderDaoImpl();
    
    @Override
    public void createUser(String username, String email, String password, 
                          String fullname, String phone, String address, 
                          Integer roleId, boolean isActive) {
        // 1. VALIDATION
        validateUserData(null, username, email, phone, password, true);
        
        // 2. KIỂM TRA TRÙNG LẶP
        checkDuplicates(null, username, email, phone);
        
        // 3. TẠO USER MỚI
        User user = new User();
        user.setUsername(username);
        user.setEmail(email);
        user.setPassword(PasswordUtils.hashPassword(password));
        user.setFullname(fullname);
        user.setPhone(phone);
        user.setAddress(address);
        user.setRoleid(roleId != null ? roleId : 3); // Default: Customer
        user.setIsActive(isActive);
        user.setCreatedDate(LocalDateTime.now());
        
        userDao.save(user);
    }
    
    @Override
    public void updateUser(int userId, String email, String fullname, 
                          String phone, String address, Integer roleId, 
                          boolean isActive, String newPassword) {
        // 1. VALIDATION
        validateUserData(userId, null, email, phone, newPassword, false);
        
        User user = userDao.findById(userId);
        if (user == null) {
            throw new IllegalArgumentException("User không tồn tại!");
        }
        
        // Kiểm tra không cho phép chỉnh sửa admin
        if (user.getRoleid() != null && user.getRoleid() == stnw.utils.Roles.ADMIN) {
            throw new IllegalArgumentException("Không thể chỉnh sửa thông tin quản trị viên!");
        }
        
        // 2. KIỂM TRA TRÙNG (chỉ nếu thay đổi)
        if (!user.getEmail().equals(email) || !user.getPhone().equals(phone)) {
            checkDuplicates(userId, null, email, phone);
        }
        
        // 3. CẬP NHẬT
        user.setEmail(email);
        user.setFullname(fullname);
        user.setPhone(phone);
        user.setAddress(address);
        user.setIsActive(isActive);
        
        // Cập nhật role nếu được cung cấp
        if (roleId != null && user.getRoleid() != null) {
            // Không cho phép ADMIN đổi role (giữ nguyên ADMIN)
            if (user.getRoleid() == stnw.utils.Roles.ADMIN) {
                // Không làm gì, giữ nguyên ADMIN
            } else {
                // CUSTOMER và STAFF có thể đổi role
                // CUSTOMER có thể đổi sang STAFF hoặc ADMIN
                // STAFF có thể đổi sang ADMIN
                if (roleId == stnw.utils.Roles.CUSTOMER || 
                    roleId == stnw.utils.Roles.STAFF || 
                    roleId == stnw.utils.Roles.ADMIN) {
                    user.setRoleid(roleId);
                }
            }
        }
        
        if (newPassword != null && !newPassword.isEmpty()) {
            user.setPassword(PasswordUtils.hashPassword(newPassword));
        }
        
        userDao.update(user);
    }
    
    @Override
    public void toggleUserStatus(int userId, Integer currentUserId) {
        User user = userDao.findById(userId);
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
        userDao.update(user);
    }
    
    @Override
    public void softDeleteUser(int userId, Integer currentUserId) {
        if (currentUserId != null && currentUserId.equals(userId)) {
            throw new IllegalArgumentException("Không thể xóa tài khoản đang đăng nhập!");
        }
        
        User user = userDao.findById(userId);
        if (user == null) {
            throw new IllegalArgumentException("User không tồn tại!");
        }
        
        // Kiểm tra không cho xóa admin (roleid = 1)
        if (user.getRoleid() != null && user.getRoleid() == stnw.utils.Roles.ADMIN) {
            throw new IllegalArgumentException("Không thể xóa quản trị viên!");
        }
        
        user.setIsActive(false);
        userDao.update(user);
    }
    
    @Override
    public void hardDeleteUser(int userId, Integer currentUserId) {
        if (currentUserId != null && currentUserId.equals(userId)) {
            throw new IllegalArgumentException("Không thể xóa tài khoản đang đăng nhập!");
        }
        
        User user = userDao.findById(userId);
        if (user == null) {
            throw new IllegalArgumentException("User không tồn tại!");
        }
        
        // Kiểm tra không cho xóa admin (roleid = 1)
        if (user.getRoleid() != null && user.getRoleid() == stnw.utils.Roles.ADMIN) {
            throw new IllegalArgumentException("Không thể xóa vĩnh viễn quản trị viên!");
        }
        
        // Xóa các bản ghi liên quan trước khi xóa user
        // 1. Xóa PointTransaction
        pointTransactionDao.deleteByUserId(userId);
        
        // 2. Xóa Notification
        notificationDao.deleteByUserId(userId);
        
        // 3. Xóa WishlistItem
        wishlistDao.deleteByUserId(userId);
        
        // 4. Xóa Review
        reviewDao.deleteByUserId(userId);
        
        // 5. Xóa Orders và OrderDetail (OrderDao tự xử lý thứ tự)
        orderDao.deleteByUserId(userId);
        
        // 6. Cuối cùng xóa User
        userDao.delete(userId);
    }
    
    @Override
    public User getUserById(int userId) {
        return userDao.findById(userId);
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
    private void checkDuplicates(Integer userId, 
                                 String username, String email, String phone) {
        // Kiểm tra username (chỉ khi tạo mới)
        if (username != null && userDao.existsByUsername(username)) {
            throw new IllegalArgumentException("Username đã tồn tại!");
        }
        
        // Kiểm tra email
        if (userId != null) {
            if (userDao.existsByEmailExcludingUser(email, userId)) {
                throw new IllegalArgumentException("Email đã tồn tại!");
            }
        } else {
            if (userDao.existsByEmail(email)) {
                throw new IllegalArgumentException("Email đã tồn tại!");
            }
        }
        
        // Kiểm tra phone
        if (userId != null) {
            if (userDao.existsByPhoneExcludingUser(phone, userId)) {
                throw new IllegalArgumentException("Số điện thoại đã tồn tại!");
            }
        } else {
            if (userDao.existsByPhone(phone)) {
                throw new IllegalArgumentException("Số điện thoại đã tồn tại!");
            }
        }
    }
}
