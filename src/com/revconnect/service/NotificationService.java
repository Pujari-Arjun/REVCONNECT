package com.revconnect.service;

import com.revconnect.model.Notification;
import com.revconnect.model.NotificationPreference;
import java.util.List;

public interface NotificationService {
    boolean createNotification(Notification notification);
    List<Notification> getNotifications(int userId);
    int getUnreadCount(int userId);
    boolean markAsRead(int notificationId);
    boolean markAllAsRead(int userId);
    NotificationPreference getPreferences(int userId);
    boolean updatePreferences(NotificationPreference preferences);
}