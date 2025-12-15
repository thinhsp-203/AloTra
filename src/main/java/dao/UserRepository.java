package dao;

import jakarta.persistence.EntityManager;
import model.User;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

public class UserRepository {
  private final EntityManager em;
  public UserRepository(EntityManager em){ this.em = em; }

  public boolean existsByEmail(String email){
    Long c = em.createQuery("select count(u) from User u where u.email = :e", Long.class)
               .setParameter("e", email)
               .getSingleResult();
    return c != null && c > 0;
  }
  public boolean existsByUsername(String username){
    Long c = em.createQuery("select count(u) from User u where u.username = :u", Long.class)
               .setParameter("u", username)
               .getSingleResult();
    return c != null && c > 0;
  }
  public boolean existsByPhone(String phone){
    Long c = em.createQuery("select count(u) from User u where u.phone = :p", Long.class)
               .setParameter("p", phone)
               .getSingleResult();
    return c != null && c > 0;
  }

  public Optional<User> findByUsername(String username){
    List<User> rs = em.createQuery("select u from User u where u.username = :u", User.class)
                      .setParameter("u", username)
                      .setMaxResults(1).getResultList();
    return rs.isEmpty()? Optional.empty() : Optional.of(rs.get(0));
  }

  // THÊM MỚI: Phương thức tìm kiếm cho việc đăng nhập
  public Optional<User> findByUsernameOrEmail(String usernameOrEmail){
    List<User> rs = em.createQuery("select u from User u where u.username = :login or u.email = :login", User.class)
                      .setParameter("login", usernameOrEmail)
                      .setMaxResults(1).getResultList();
    return rs.isEmpty()? Optional.empty() : Optional.of(rs.get(0));
  }

  // Tận dụng cho Forgot/Reset
  public Optional<User> findByEmail(String email){
    List<User> rs = em.createQuery("select u from User u where u.email = :e", User.class)
                      .setParameter("e", email)
                      .setMaxResults(1).getResultList();
    return rs.isEmpty()? Optional.empty() : Optional.of(rs.get(0));
  }
  public Optional<User> findByResetTokenValid(String token){
    if (token == null || token.isBlank()) return Optional.empty();
    LocalDateTime now = LocalDateTime.now();
    List<User> rs = em.createQuery(
        "select u from User u where u.resetToken = :t and u.tokenExpiry is not null and u.tokenExpiry >= :now",
        User.class)
        .setParameter("t", token)
        .setParameter("now", now)
        .setMaxResults(1)
        .getResultList();
    return rs.isEmpty()? Optional.empty() : Optional.of(rs.get(0));
  }
}