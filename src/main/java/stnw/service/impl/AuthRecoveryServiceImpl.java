package stnw.service.impl;

import stnw.dao.UserDao;
import stnw.dao.impl.UserDaoImpl;
import stnw.model.User;
import stnw.service.AuthRecoveryService;
import stnw.utils.PasswordUtils;
import stnw.utils.TokenUtils;

import java.time.LocalDateTime;
import java.util.Optional;

public class AuthRecoveryServiceImpl implements AuthRecoveryService {

    private final UserDao userDao = new UserDaoImpl();

    @Override
    public String createResetTokenIfEligible(String email, String resetBaseUrl) {
        Optional<User> uopt = userDao.findByEmail(email.trim());
        if (uopt.isEmpty()) {
            return null;
        }
        User u = uopt.get();
        if (u.getIsActive() == null || !u.getIsActive()) {
            return null;
        }

        String token = TokenUtils.generateUrlToken();
        u.setResetToken(token);
        u.setTokenExpiry(LocalDateTime.now().plusHours(1));
        userDao.update(u);

        // controller sẽ tự gửi email/ghi log; service chỉ trả token
        return token;
    }

    @Override
    public boolean isValidToken(String token) {
        if (token == null || token.isBlank()) return false;
        return userDao.findByResetTokenValid(token).isPresent();
    }

    @Override
    public boolean resetPassword(String token, String newPassword) {
        if (token == null || token.isBlank()) return false;
        
        var uopt = userDao.findByResetTokenValid(token);
        if (uopt.isEmpty()) {
            return false;
        }

        User u = uopt.get();
        u.setPassword(PasswordUtils.hashPassword(newPassword));
        u.setResetToken(null);
        u.setTokenExpiry(null);
        userDao.update(u);
        return true;
    }
}
