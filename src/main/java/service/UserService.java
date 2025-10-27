package service;

import model.User;

public interface UserService {
    boolean register(String username, String rawPassword, String email, String fullname, String phone);

	User login(String username, String password);
}
