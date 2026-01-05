package stnw.dao;

import stnw.model.User;

import java.util.Optional;

public interface UserRepository {
    boolean existsByEmail(String email);
    boolean existsByUsername(String username);
    boolean existsByPhone(String phone);
    Optional<User> findByUsername(String username);
    Optional<User> findByUsernameOrEmail(String usernameOrEmail);
    Optional<User> findByEmail(String email);
    Optional<User> findByResetTokenValid(String token);
}