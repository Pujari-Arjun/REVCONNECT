package com.revconnect.dao;

import com.revconnect.model.Notification;
import com.revconnect.model.NotificationPreference;
import java.util.List;

public interface NotificationDAO {
    boolean saveNotification(Notification notification);
    List<Notification> findByUserId(int userId);
    int countUnread(int userId);
    boolean markAsRead(int notificationId);
    boolean markAllAsRead(int userId);
    NotificationPreference getPreferences(int userId);
    boolean updatePreferences(NotificationPreference preferences);
}