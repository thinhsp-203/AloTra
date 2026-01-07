package stnw.service.impl;

import stnw.dao.NotificationDao;
import stnw.dao.UserDao;
import stnw.dao.impl.NotificationDaoImpl;
import stnw.dao.impl.UserDaoImpl;
import stnw.model.Notification;
import stnw.model.User;
import stnw.service.NotificationService;

import java.util.List;

public class NotificationServiceImpl implements NotificationService {
    
    private final NotificationDao notificationDao = new NotificationDaoImpl();
    private final UserDao userDao = new UserDaoImpl();
    
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
        User user = userDao.findById(userId);
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

