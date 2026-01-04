package service.impl;

import config.JpaUtil;
import dao.NotificationDao;
import dao.impl.NotificationDaoImpl;
import jakarta.persistence.EntityManager;
import model.Notification;
import model.User;
import service.NotificationService;

import java.util.List;

public class NotificationServiceImpl implements NotificationService {
    
    private final NotificationDao notificationDao = new NotificationDaoImpl();
    
    @Override
    public List<Notification> getUserNotifications(Integer userId) {
        return notificationDao.findByUserId(userId);
    }
    
    @Override
    public List<Notification> getRecentNotifications(Integer userId, int limit) {
        return notificationDao.findRecentByUserId(userId, limit);
    }
    
    @Override
    public long getUnreadCount(Integer userId) {
        return notificationDao.countUnreadByUserId(userId);
    }
    
    @Override
    public void markAsRead(Integer notificationId) {
        notificationDao.markAsRead(notificationId);
    }
    
    @Override
    public void markAllAsRead(Integer userId) {
        notificationDao.markAllAsRead(userId);
    }
    
    @Override
    public void createNotification(Integer userId, String message, String link) {
        EntityManager em = JpaUtil.em();
        try {
            User user = em.find(User.class, userId);
            if (user != null) {
                Notification notification = new Notification();
                notification.setUser(user);
                notification.setMessage(message);
                notification.setLink(link);
                notification.setIsRead(false);
                notification.setIsDeleted(false);
                notification.setCreatedDate(java.time.LocalDateTime.now());
                notificationDao.save(notification);
            }
        } finally {
            em.close();
        }
    }
    
    @Override
    public void deleteNotification(Integer notificationId, Integer userId) {
        notificationDao.markAsDeleted(notificationId, userId);
    }
    
    @Override
    public void deleteAllNotifications(Integer userId) {
        notificationDao.markAllAsDeleted(userId);
    }
}

