package stnw.dao.impl;

import stnw.config.JpaUtil;
import stnw.dao.NotificationDao;
import jakarta.persistence.EntityManager;
import jakarta.persistence.TypedQuery;
import stnw.model.Notification;

import java.util.List;

public class NotificationDaoImpl implements NotificationDao {
    
    @Override
    public List<Notification> findByUserId(Integer userId) {
        EntityManager em = JpaUtil.em();
        try {
            TypedQuery<Notification> query = em.createQuery(
                "SELECT n FROM Notification n WHERE n.user.id = :userId AND (n.isDeleted = false OR n.isDeleted IS NULL) ORDER BY n.createdDate DESC", 
                Notification.class
            );
            query.setParameter("userId", userId);
            return query.getResultList();
        } finally {
            em.close();
        }
    }
    
    @Override
    public List<Notification> findRecentByUserId(Integer userId, int limit) {
        EntityManager em = JpaUtil.em();
        try {
            TypedQuery<Notification> query = em.createQuery(
                "SELECT n FROM Notification n WHERE n.user.id = :userId AND (n.isDeleted = false OR n.isDeleted IS NULL) ORDER BY n.createdDate DESC", 
                Notification.class
            );
            query.setParameter("userId", userId);
            query.setMaxResults(limit);
            return query.getResultList();
        } finally {
            em.close();
        }
    }
    
    @Override
    public long countUnreadByUserId(Integer userId) {
        EntityManager em = JpaUtil.em();
        try {
            TypedQuery<Long> query = em.createQuery(
                "SELECT COUNT(n) FROM Notification n WHERE n.user.id = :userId AND n.isRead = false AND (n.isDeleted = false OR n.isDeleted IS NULL)", 
                Long.class
            );
            query.setParameter("userId", userId);
            return query.getSingleResult();
        } finally {
            em.close();
        }
    }
    
    @Override
    public Notification findById(Integer id) {
        EntityManager em = JpaUtil.em();
        try {
            return em.find(Notification.class, id);
        } finally {
            em.close();
        }
    }
    
    @Override
    public void save(Notification notification) {
        EntityManager em = JpaUtil.em();
        try {
            em.getTransaction().begin();
            em.persist(notification);
            em.getTransaction().commit();
        } catch (Exception e) {
            if (em.getTransaction().isActive()) {
                em.getTransaction().rollback();
            }
            throw e;
        } finally {
            em.close();
        }
    }
    
    @Override
    public void update(Notification notification) {
        EntityManager em = JpaUtil.em();
        try {
            em.getTransaction().begin();
            em.merge(notification);
            em.getTransaction().commit();
        } catch (Exception e) {
            if (em.getTransaction().isActive()) {
                em.getTransaction().rollback();
            }
            throw e;
        } finally {
            em.close();
        }
    }
    
    @Override
    public void markAsRead(Integer id) {
        EntityManager em = JpaUtil.em();
        try {
            em.getTransaction().begin();
            Notification notification = em.find(Notification.class, id);
            if (notification != null) {
                notification.setIsRead(true);
                em.merge(notification);
            }
            em.getTransaction().commit();
        } catch (Exception e) {
            if (em.getTransaction().isActive()) {
                em.getTransaction().rollback();
            }
            throw e;
        } finally {
            em.close();
        }
    }
    
    @Override
    public void markAllAsRead(Integer userId) {
        EntityManager em = JpaUtil.em();
        try {
            em.getTransaction().begin();
            TypedQuery<Notification> query = em.createQuery(
                "SELECT n FROM Notification n WHERE n.user.id = :userId AND n.isRead = false AND (n.isDeleted = false OR n.isDeleted IS NULL)", 
                Notification.class
            );
            query.setParameter("userId", userId);
            List<Notification> notifications = query.getResultList();
            for (Notification n : notifications) {
                n.setIsRead(true);
                em.merge(n);
            }
            em.getTransaction().commit();
        } catch (Exception e) {
            if (em.getTransaction().isActive()) {
                em.getTransaction().rollback();
            }
            throw e;
        } finally {
            em.close();
        }
    }
    
    @Override
    public void markAsDeleted(Integer id, Integer userId) {
        EntityManager em = JpaUtil.em();
        try {
            em.getTransaction().begin();
            Notification notification = em.find(Notification.class, id);
            if (notification != null && notification.getUser().getId().equals(userId)) {
                notification.setIsDeleted(true);
                em.merge(notification);
            }
            em.getTransaction().commit();
        } catch (Exception e) {
            if (em.getTransaction().isActive()) {
                em.getTransaction().rollback();
            }
            throw e;
        } finally {
            em.close();
        }
    }
    
    @Override
    public void markAllAsDeleted(Integer userId) {
        EntityManager em = JpaUtil.em();
        try {
            em.getTransaction().begin();
            TypedQuery<Notification> query = em.createQuery(
                "SELECT n FROM Notification n WHERE n.user.id = :userId AND (n.isDeleted = false OR n.isDeleted IS NULL)", 
                Notification.class
            );
            query.setParameter("userId", userId);
            List<Notification> notifications = query.getResultList();
            for (Notification n : notifications) {
                n.setIsDeleted(true);
                em.merge(n);
            }
            em.getTransaction().commit();
        } catch (Exception e) {
            if (em.getTransaction().isActive()) {
                em.getTransaction().rollback();
            }
            throw e;
        } finally {
            em.close();
        }
    }
}

