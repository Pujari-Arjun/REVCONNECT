package com.revconnect.serviceimplementation;

import com.revconnect.service.NotificationService;
import com.revconnect.model.Notification;
import com.revconnect.model.NotificationPreference;
import com.revconnect.repository.NotificationRepository;

import java.util.List;

public class NotificationServiceImpl implements NotificationService {

    private final NotificationRepository notificationRepository;

    public NotificationServiceImpl() {
        this.notificationRepository = new NotificationRepository();
    }

    @Override
    public boolean createNotification(Notification notification) {

        NotificationPreference preferences =
                notificationRepository.getPreferences(notification.getUserId());

        boolean shouldNotify =
                shouldCreateNotification(notification.getType(), preferences);

        if (!shouldNotify) {
            return false;
        }

        return notificationRepository.saveNotification(notification);
    }

    @Override
    public List<Notification> getNotifications(int userId) {
        return notificationRepository.findByUserId(userId);
    }

    @Override
    public int getUnreadCount(int userId) {
        return notificationRepository.countUnread(userId);
    }

    @Override
    public boolean markAsRead(int notificationId) {
        return notificationRepository.markAsRead(notificationId);
    }

    @Override
    public boolean markAllAsRead(int userId) {
        return notificationRepository.markAllAsRead(userId);
    }

    @Override
    public NotificationPreference getPreferences(int userId) {
        return notificationRepository.getPreferences(userId);
    }

    @Override
    public boolean updatePreferences(NotificationPreference preferences) {
        return notificationRepository.updatePreferences(preferences);
    }

    private boolean shouldCreateNotification(
            String notificationType,
            NotificationPreference preferences) {

        switch (notificationType) {
            case "CONNECTION_REQUEST":
                return preferences.isConnectionRequests();
            case "NEW_FOLLOWER":
                return preferences.isNewFollowers();
            case "LIKE":
                return preferences.isLikes();
            case "COMMENT":
                return preferences.isComments();
            case "SHARE":
                return preferences.isShares();
            case "NEW_POST":
                return preferences.isNewPosts();
            default:
                return true;
        }
    }
}
