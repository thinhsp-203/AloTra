package stnw.dao;

import stnw.model.User;

import java.util.List;
import java.util.Optional;

public interface UserDao {
    boolean existsByEmail(String email);
    boolean existsByUsername(String username);
    boolean existsByPhone(String phone);
    Optional<User> findByUsername(String username);
    Optional<User> findByUsernameOrEmail(String usernameOrEmail);
    Optional<User> findByEmail(String email);
    Optional<User> findByResetTokenValid(String token);
    User findById(Integer id);
    void save(User user);
    void update(User user);
    int countUsers(String keyword, Integer roleId);
    List<User> searchUsers(String keyword, Integer roleId, int page, int pageSize);
    boolean existsByPhoneExcludingUser(String phone, Integer excludeUserId);
    boolean existsByEmailExcludingUser(String email, Integer excludeUserId);
    List<User> findAllActive();
    void delete(Integer userId);
    long getTotalCustomers(int roleId);
    long getNewCustomersThisMonth(int roleId);
}

