package service.impl;

import config.JpaUtil;
import dao.impl.UserRepositoryImpl;
import jakarta.persistence.EntityManager;
import jakarta.persistence.EntityTransaction;
import jakarta.persistence.TypedQuery;
import model.User;
import service.UserService;
import utils.PasswordUtil;
import java.time.LocalDateTime;
import java.util.List;

public class UserServiceImpl implements UserService {

    @Override
    public boolean register(String username, String rawPassword, String email, String fullname, String phone, String code) {
        EntityManager em = JpaUtil.em();
        try {
            var repo = new UserRepositoryImpl(em);
            if (repo.existsByEmail(email) || repo.existsByUsername(username)
                || repo.existsByPhone(phone)) {
                return false;
            }
            String hash = PasswordUtil.hash(rawPassword);
            var tx = em.getTransaction();
            tx.begin();
            try {
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
                em.persist(u);
                tx.commit();
                return true;
            } catch(Exception ex){ if(tx.isActive()) tx.rollback(); throw ex; }
        } finally { em.close(); }
    }

 // --- Thêm hàm tìm kiếm theo Email (Dùng cho Quên mật khẩu & Verify) ---
    @Override
    public User findUserByEmail(String email) {
        EntityManager em = JpaUtil.em();
        try {
            String jpql = "SELECT u FROM User u WHERE u.email = :email";
            TypedQuery<User> query = em.createQuery(jpql, User.class);
            query.setParameter("email", email);
            return query.getResultStream().findFirst().orElse(null);
        } finally {
            em.close();
        }
    }

    // --- Thêm hàm Update User (Dùng để set Active=true hoặc đổi Pass) ---
    @Override
    public void updateUser(User user) {
        EntityManager em = JpaUtil.em();
        EntityTransaction trans = em.getTransaction();
        try {
            trans.begin();
            em.merge(user); // Merge dùng để update
            trans.commit();
        } catch (Exception e) {
            if (trans.isActive()) trans.rollback();
            e.printStackTrace();
        } finally {
            em.close();
        }
    }

    @Override
    public User login(String usernameOrEmail, String rawPassword) {
        EntityManager em = JpaUtil.em();
        try {
            var repo = new UserRepositoryImpl(em);
            var uopt = repo.findByUsernameOrEmail(usernameOrEmail); 
            
            if (uopt.isEmpty()) return null;
            User u = uopt.get();
            
            // Chỉ cho phép login nếu đã Active
            if (u.getIsActive() != null && !u.getIsActive()) {
                return null; // Hoặc ném Exception báo "Tài khoản chưa kích hoạt"
            }
            
            boolean ok = PasswordUtil.verify(rawPassword, u.getPassword());
            return ok ? u : null;
        } finally { 
            em.close(); 
        }
    }

    @Override
    public int countUsers(String keyword, Integer roleId) {
        EntityManager em = JpaUtil.em();
        try {
            String jpql = "SELECT COUNT(u) FROM User u WHERE 1=1";
            if (keyword != null && !keyword.isBlank()) {
                jpql += " AND (u.fullname LIKE :kw OR u.email LIKE :kw OR u.phone LIKE :kw)";
            }
            if (roleId != null) {
                jpql += " AND u.roleid = :roleId";
            }
            TypedQuery<Long> query = em.createQuery(jpql, Long.class);
            if (keyword != null && !keyword.isBlank()) {
                query.setParameter("kw", "%" + keyword + "%");
            }
            if (roleId != null) {
                query.setParameter("roleId", roleId);
            }
            return query.getSingleResult().intValue();
        } finally {
            em.close();
        }
    }

    @Override
    public List<User> searchUsers(String keyword, Integer roleId, int page, int pageSize) {
        EntityManager em = JpaUtil.em();
        try {
            String jpql = "SELECT u FROM User u WHERE 1=1";
            if (keyword != null && !keyword.isBlank()) {
                jpql += " AND (u.fullname LIKE :kw OR u.email LIKE :kw OR u.phone LIKE :kw)";
            }
            if (roleId != null) {
                jpql += " AND u.roleid = :roleId";
            }
            jpql += " ORDER BY u.createdDate DESC";

            TypedQuery<User> query = em.createQuery(jpql, User.class);
            if (keyword != null && !keyword.isBlank()) {
                query.setParameter("kw", "%" + keyword + "%");
            }
            if (roleId != null) {
                query.setParameter("roleId", roleId);
            }
            query.setFirstResult((page - 1) * pageSize);
            query.setMaxResults(pageSize);
            return query.getResultList();
        } finally {
            em.close();
        }
    }
}