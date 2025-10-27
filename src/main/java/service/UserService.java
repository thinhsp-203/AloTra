package service;

import model.User;
import java.util.List;

public interface UserService {
    boolean register(String username, String rawPassword, String email, String fullname, String phone);
    User login(String username, String password);
    int countUsers(String keyword, Integer roleId);
    List<User> searchUsers(String keyword, Integer roleId, int page, int pageSize);
}