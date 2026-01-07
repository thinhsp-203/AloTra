package stnw.service;

import stnw.model.User;
import java.util.List;

public interface UserService {
    boolean register(String username, String rawPassword, String email, String fullname, String phone, String code);
    
    User login(String usernameOrEmail, String rawPassword);
    User findUserByEmail(String email);
    User getUserById(Integer userId);
    void updateUser(User user);
    int countUsers(String keyword, Integer roleId);
    List<User> searchUsers(String keyword, Integer roleId, int page, int pageSize);
}