package service.impl;

import config.JpaUtil;
import dao.jpa.UserRepository;
import jakarta.persistence.EntityManager;
import model.User;
import service.UserService;
import utils.PasswordUtil;

import java.time.LocalDateTime;

public class UserServiceImpl implements UserService {

    @Override
    public boolean register(String username, String rawPassword, String email, String fullname, String phone) {
        EntityManager em = JpaUtil.em();
        try {
            var repo = new UserRepository(em);
            if (repo.existsByEmail(email) || repo.existsByUsername(username)
                || (phone != null && !phone.isBlank() && repo.existsByPhone(phone))) {
                return false;
            }
            String hash = PasswordUtil.hash(rawPassword);
            var tx = em.getTransaction(); tx.begin();
            try {
                User u = new User();
                u.setEmail(email);
                u.setUsername(username);
                u.setFullname(fullname);
                u.setPassword(hash);
                u.setPhone((phone == null || phone.isBlank()) ? null : phone);
                u.setRoleid(3);
                u.setIsActive(true);
                u.setCreatedDate(LocalDateTime.now());
                em.persist(u);
                tx.commit();
                return true;
            } catch(Exception ex){ if(tx.isActive()) tx.rollback(); throw ex; }
        } finally { em.close(); }
    }

    @Override
    public User login(String username, String rawPassword) {
        EntityManager em = JpaUtil.em();
        try {
            var repo = new UserRepository(em);
            var uopt = repo.findByUsername(username);
            if (uopt.isEmpty()) return null;
            User u = uopt.get();
            if (u.getIsActive()!=null && !u.getIsActive()) return null; // khoá tài khoản
            boolean ok = PasswordUtil.verify(rawPassword, u.getPassword());
            return ok ? u : null;
        } finally { em.close(); }
    }
}
