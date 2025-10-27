package dao;

import java.util.List;
import model.User;

public interface UserDao {
    User get(String username);
    void insert(User user);
    boolean checkExistUsername(String username);
    boolean checkExistEmail(String email);
    boolean checkExistPhone(String phone);
    int count(String keyword, Integer roleId);
    List<User> search(String keyword, Integer roleId, int offset, int limit);
}
