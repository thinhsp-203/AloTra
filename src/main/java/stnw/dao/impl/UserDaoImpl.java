package stnw.dao.impl;

import stnw.config.JpaUtil;
import stnw.dao.UserDao;
import jakarta.persistence.EntityManager;
import stnw.model.User;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

public class UserDaoImpl implements UserDao {
    private final EntityManager em;

    public UserDaoImpl(EntityManager em) {
        this.em = em;
    }

    private EntityManager em() {
        return em != null ? em : JpaUtil.em();
    }

    @Override
    public boolean existsByEmail(String email) {
        EntityManager manager = em();
        try {
            Long c = manager.createQuery("select count(u) from User u where u.email = :e", Long.class)
                    .setParameter("e", email)
                    .getSingleResult();
            return c != null && c > 0;
        } finally {
            if (manager != em) manager.close();
        }
    }

    @Override
    public boolean existsByUsername(String username) {
        EntityManager manager = em();
        try {
            Long c = manager.createQuery("select count(u) from User u where u.username = :u", Long.class)
                    .setParameter("u", username)
                    .getSingleResult();
            return c != null && c > 0;
        } finally {
            if (manager != em) manager.close();
        }
    }

    @Override
    public boolean existsByPhone(String phone) {
        EntityManager manager = em();
        try {
            Long c = manager.createQuery("select count(u) from User u where u.phone = :p", Long.class)
                    .setParameter("p", phone)
                    .getSingleResult();
            return c != null && c > 0;
        } finally {
            if (manager != em) manager.close();
        }
    }

    @Override
    public Optional<User> findByUsername(String username) {
        EntityManager manager = em();
        try {
            List<User> rs = manager.createQuery("select u from User u where u.username = :u", User.class)
                    .setParameter("u", username)
                    .setMaxResults(1).getResultList();
            return rs.isEmpty() ? Optional.empty() : Optional.of(rs.get(0));
        } finally {
            if (manager != em) manager.close();
        }
    }

    @Override
    public Optional<User> findByUsernameOrEmail(String usernameOrEmail) {
        EntityManager manager = em();
        try {
            List<User> rs = manager.createQuery("select u from User u where u.username = :login or u.email = :login", User.class)
                    .setParameter("login", usernameOrEmail)
                    .setMaxResults(1).getResultList();
            return rs.isEmpty() ? Optional.empty() : Optional.of(rs.get(0));
        } finally {
            if (manager != em) manager.close();
        }
    }

    @Override
    public Optional<User> findByEmail(String email) {
        EntityManager manager = em();
        try {
            List<User> rs = manager.createQuery("select u from User u where u.email = :e", User.class)
                    .setParameter("e", email)
                    .setMaxResults(1).getResultList();
            return rs.isEmpty() ? Optional.empty() : Optional.of(rs.get(0));
        } finally {
            if (manager != em) manager.close();
        }
    }

    @Override
    public Optional<User> findByResetTokenValid(String token) {
        if (token == null || token.isBlank()) return Optional.empty();
        EntityManager manager = em();
        try {
            LocalDateTime now = LocalDateTime.now();
            List<User> rs = manager.createQuery(
                            "select u from User u where u.resetToken = :t and u.tokenExpiry is not null and u.tokenExpiry >= :now",
                            User.class)
                    .setParameter("t", token)
                    .setParameter("now", now)
                    .setMaxResults(1)
                    .getResultList();
            return rs.isEmpty() ? Optional.empty() : Optional.of(rs.get(0));
        } finally {
            if (manager != em) manager.close();
        }
    }
}

