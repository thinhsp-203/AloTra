package stnw.dao.impl;

import stnw.utils.JpaUtils;
import stnw.dao.UserDao;
import jakarta.persistence.EntityManager;
import jakarta.persistence.EntityTransaction;
import jakarta.persistence.TypedQuery;
import stnw.model.User;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

public class UserDaoImpl implements UserDao {

    @Override
    public boolean existsByEmail(String email) {
        EntityManager em = JpaUtils.em();
        try {
            Long c = em.createQuery("select count(u) from User u where u.email = :e", Long.class)
                    .setParameter("e", email)
                    .getSingleResult();
            return c != null && c > 0;
        } finally {
            em.close();
        }
    }

    @Override
    public boolean existsByUsername(String username) {
        EntityManager em = JpaUtils.em();
        try {
            Long c = em.createQuery("select count(u) from User u where u.username = :u", Long.class)
                    .setParameter("u", username)
                    .getSingleResult();
            return c != null && c > 0;
        } finally {
            em.close();
        }
    }

    @Override
    public boolean existsByPhone(String phone) {
        EntityManager em = JpaUtils.em();
        try {
            Long c = em.createQuery("select count(u) from User u where u.phone = :p", Long.class)
                    .setParameter("p", phone)
                    .getSingleResult();
            return c != null && c > 0;
        } finally {
            em.close();
        }
    }

    @Override
    public Optional<User> findByUsername(String username) {
        EntityManager em = JpaUtils.em();
        try {
            List<User> rs = em.createQuery("select u from User u where u.username = :u", User.class)
                    .setParameter("u", username)
                    .setMaxResults(1).getResultList();
            return rs.isEmpty() ? Optional.empty() : Optional.of(rs.get(0));
        } finally {
            em.close();
        }
    }

    @Override
    public Optional<User> findByUsernameOrEmail(String usernameOrEmail) {
        EntityManager em = JpaUtils.em();
        try {
            List<User> rs = em.createQuery("select u from User u where u.username = :login or u.email = :login", User.class)
                    .setParameter("login", usernameOrEmail)
                    .setMaxResults(1).getResultList();
            return rs.isEmpty() ? Optional.empty() : Optional.of(rs.get(0));
        } finally {
            em.close();
        }
    }

    @Override
    public Optional<User> findByEmail(String email) {
        EntityManager em = JpaUtils.em();
        try {
            List<User> rs = em.createQuery("select u from User u where u.email = :e", User.class)
                    .setParameter("e", email)
                    .setMaxResults(1).getResultList();
            return rs.isEmpty() ? Optional.empty() : Optional.of(rs.get(0));
        } finally {
            em.close();
        }
    }

    @Override
    public Optional<User> findByResetTokenValid(String token) {
        if (token == null || token.isBlank()) return Optional.empty();
        EntityManager em = JpaUtils.em();
        try {
            LocalDateTime now = LocalDateTime.now();
            List<User> rs = em.createQuery(
                            "select u from User u where u.resetToken = :t and u.tokenExpiry is not null and u.tokenExpiry >= :now",
                            User.class)
                    .setParameter("t", token)
                    .setParameter("now", now)
                    .setMaxResults(1)
                    .getResultList();
            return rs.isEmpty() ? Optional.empty() : Optional.of(rs.get(0));
        } finally {
            em.close();
        }
    }

    @Override
    public User findById(Integer id) {
        EntityManager em = JpaUtils.em();
        try {
            return em.find(User.class, id);
        } finally {
            em.close();
        }
    }

    @Override
    public void save(User user) {
        EntityManager em = JpaUtils.em();
        EntityTransaction trans = em.getTransaction();
        try {
            trans.begin();
            em.persist(user);
            trans.commit();
        } catch (Exception e) {
            if (trans.isActive()) {
                trans.rollback();
            }
            throw e;
        } finally {
            em.close();
        }
    }

    @Override
    public void update(User user) {
        EntityManager em = JpaUtils.em();
        EntityTransaction trans = em.getTransaction();
        try {
            trans.begin();
            em.merge(user);
            trans.commit();
        } catch (Exception e) {
            if (trans.isActive()) {
                trans.rollback();
            }
            throw e;
        } finally {
            em.close();
        }
    }

    @Override
    public int countUsers(String keyword, Integer roleId) {
        EntityManager em = JpaUtils.em();
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
        EntityManager em = JpaUtils.em();
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

    @Override
    public boolean existsByPhoneExcludingUser(String phone, Integer excludeUserId) {
        EntityManager em = JpaUtils.em();
        try {
            Long count = em.createQuery(
                    "SELECT COUNT(u) FROM User u WHERE u.phone = :phone AND u.id <> :userId", 
                    Long.class)
                    .setParameter("phone", phone)
                    .setParameter("userId", excludeUserId)
                    .getSingleResult();
            return count != null && count > 0;
        } finally {
            em.close();
        }
    }

    @Override
    public boolean existsByEmailExcludingUser(String email, Integer excludeUserId) {
        EntityManager em = JpaUtils.em();
        try {
            Long count = em.createQuery(
                    "SELECT COUNT(u) FROM User u WHERE u.email = :email AND u.id <> :userId", 
                    Long.class)
                    .setParameter("email", email)
                    .setParameter("userId", excludeUserId)
                    .getSingleResult();
            return count != null && count > 0;
        } finally {
            em.close();
        }
    }

    @Override
    public List<User> findAllActive() {
        EntityManager em = JpaUtils.em();
        try {
            TypedQuery<User> query = em.createQuery(
                    "SELECT u FROM User u WHERE u.isActive = true", User.class);
            return query.getResultList();
        } finally {
            em.close();
        }
    }

    @Override
    public void delete(Integer userId) {
        EntityManager em = JpaUtils.em();
        EntityTransaction trans = em.getTransaction();
        try {
            trans.begin();
            User user = em.find(User.class, userId);
            if (user != null) {
                em.remove(user);
            }
            trans.commit();
        } catch (Exception e) {
            if (trans.isActive()) {
                trans.rollback();
            }
            throw e;
        } finally {
            em.close();
        }
    }
    
    @Override
    public long getTotalCustomers(int roleId) {
        EntityManager em = JpaUtils.em();
        try {
            Long count = em.createQuery(
                "SELECT COUNT(u) FROM User u WHERE u.roleid = :roleId", Long.class)
                .setParameter("roleId", roleId)
                .getSingleResult();
            return count != null ? count : 0L;
        } finally {
            em.close();
        }
    }
    
    @Override
    public long getNewCustomersThisMonth(int roleId) {
        EntityManager em = JpaUtils.em();
        try {
            Long count = em.createQuery(
                "SELECT COUNT(u) FROM User u " +
                "WHERE u.roleid = :roleId " +
                "AND YEAR(u.createdDate) = YEAR(CURRENT_DATE) " +
                "AND MONTH(u.createdDate) = MONTH(CURRENT_DATE)", Long.class)
                .setParameter("roleId", roleId)
                .getSingleResult();
            return count != null ? count : 0L;
        } finally {
            em.close();
        }
    }
}

