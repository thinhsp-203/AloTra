package dao;

import model.Notification;
import java.util.List;

public interface NotificationDao {
    List<Notification> findByUserId(Integer userId);
    List<Notification> findRecentByUserId(Integer userId, int limit);
    long countUnreadByUserId(Integer userId);
    Notification findById(Integer id);
    void save(Notification notification);
    void update(Notification notification);
    void markAsRead(Integer id);
    void markAllAsRead(Integer userId);
    void markAsDeleted(Integer id, Integer userId);
    void markAllAsDeleted(Integer userId);
}

