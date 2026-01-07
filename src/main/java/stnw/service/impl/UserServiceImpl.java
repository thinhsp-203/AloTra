package stnw.service.impl;

import stnw.dao.UserDao;
import stnw.dao.impl.UserDaoImpl;
import stnw.model.User;
import stnw.service.UserService;
import stnw.utils.PasswordUtils;
import java.time.LocalDateTime;
import java.util.List;

public class UserServiceImpl implements UserService {

    private final UserDao userDao = new UserDaoImpl();

    @Override
    public boolean register(String username, String rawPassword, String email, String fullname, String phone, String code) {
        // Kiểm tra email và username (bắt buộc)
        if (userDao.existsByEmail(email) || userDao.existsByUsername(username)) {
            return false;
        }
        // Chỉ kiểm tra phone nếu phone không null và không rỗng
        if (phone != null && !phone.trim().isEmpty() && userDao.existsByPhone(phone)) {
            return false;
        }
        
        String hash = PasswordUtils.hashPassword(rawPassword);
        User u = new User();
        u.setEmail(email);
        u.setUsername(username);
        u.setFullname(fullname);
        u.setPassword(hash);
        u.setPhone(phone); 
        u.setRoleid(3);
        u.setIsActive(false); 
        u.setCode(code);      
        u.setCreatedDate(LocalDateTime.now());
        userDao.save(u);
        return true;
    }

    @Override
    public User findUserByEmail(String email) {
        return userDao.findByEmail(email).orElse(null);
    }
    
    @Override
    public User getUserById(Integer userId) {
        return userDao.findById(userId);
    }

    @Override
    public void updateUser(User user) {
        userDao.update(user);
    }

    @Override
    public User login(String usernameOrEmail, String rawPassword) {
        var uopt = userDao.findByUsernameOrEmail(usernameOrEmail); 
        
        if (uopt.isEmpty()) return null;
        User u = uopt.get();
        
        // Chỉ cho phép login nếu đã Active
        if (u.getIsActive() != null && !u.getIsActive()) {
            return null; // Hoặc ném Exception báo "Tài khoản chưa kích hoạt"
        }
        
        boolean ok = PasswordUtils.verifyPassword(rawPassword, u.getPassword());
        return ok ? u : null;
    }

    @Override
    public int countUsers(String keyword, Integer roleId) {
        return userDao.countUsers(keyword, roleId);
    }

    @Override
    public List<User> searchUsers(String keyword, Integer roleId, int page, int pageSize) {
        return userDao.searchUsers(keyword, roleId, page, pageSize);
    }
}
