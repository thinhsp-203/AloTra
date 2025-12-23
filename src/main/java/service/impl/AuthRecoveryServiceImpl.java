package service.impl;

import config.JpaUtil;
import dao.UserRepository;
import dao.impl.UserRepositoryImpl;
import jakarta.persistence.EntityManager;
import model.User;
import service.AuthRecoveryService;
import utils.PasswordUtil;

import java.time.LocalDateTime;
import java.util.Optional;

public class AuthRecoveryServiceImpl implements AuthRecoveryService {

    @Override
    public String createResetTokenIfEligible(String email, String resetBaseUrl) {
        EntityManager em = JpaUtil.em();
        try {
            UserRepository repo = new UserRepositoryImpl(em);
            Optional<User> uopt = repo.findByEmail(email.trim());
            if (uopt.isEmpty()) {
                return null;
            }
            User u = uopt.get();
            if (u.getIsActive() == null || !u.getIsActive()) {
                return null;
            }

            String token = PasswordUtil.newUrlToken();
            var tx = em.getTransaction();
            tx.begin();
            try {
                u.setResetToken(token);
                u.setTokenExpiry(LocalDateTime.now().plusHours(1));
                em.merge(u);
                tx.commit();
            } catch (Exception ex) {
                if (tx.isActive()) tx.rollback();
                throw ex;
            }

            // controller sẽ tự gửi email/ghi log; service chỉ trả token
            return token;
        } finally {
            em.close();
        }
    }

    @Override
    public boolean isValidToken(String token) {
        if (token == null || token.isBlank()) return false;
        EntityManager em = JpaUtil.em();
        try {
            UserRepository repo = new UserRepositoryImpl(em);
            return repo.findByResetTokenValid(token).isPresent();
        } finally {
            em.close();
        }
    }

    @Override
    public boolean resetPassword(String token, String newPassword) {
        if (token == null || token.isBlank()) return false;
        EntityManager em = JpaUtil.em();
        try {
            UserRepository repo = new UserRepositoryImpl(em);
            var uopt = repo.findByResetTokenValid(token);
            if (uopt.isEmpty()) {
                return false;
            }

            var tx = em.getTransaction();
            tx.begin();
            try {
                User u = uopt.get();
                u.setPassword(PasswordUtil.hash(newPassword));
                u.setResetToken(null);
                u.setTokenExpiry(null);
                em.merge(u);
                tx.commit();
                return true;
            } catch (Exception ex) {
                if (tx.isActive()) tx.rollback();
                throw ex;
            }
        } finally {
            em.close();
        }
    }
}

