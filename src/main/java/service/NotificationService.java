package service;

import model.Notification;
import java.util.List;

public interface NotificationService {
    List<Notification> getUserNotifications(Integer userId);
    List<Notification> getRecentNotifications(Integer userId, int limit);
    long getUnreadCount(Integer userId);
    void markAsRead(Integer notificationId);
    void markAllAsRead(Integer userId);
    void createNotification(Integer userId, String message, String link);
}

