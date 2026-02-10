package com.revconnect.repository;

import com.revconnect.dao.NotificationDAO;
import com.revconnect.model.Notification;
import com.revconnect.model.NotificationPreference;
import com.revconnect.util.DBConnectionUtil;
import com.revconnect.exception.DatabaseException;

import java.sql.*;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

public class NotificationRepository implements NotificationDAO {

    @Override
    public boolean saveNotification(Notification notification) {
        String sql = "INSERT INTO notifications (user_id, type, message, is_read, " +
                     "related_user_id, related_post_id, created_at) VALUES (?, ?, ?, ?, ?, ?, ?)";

        try (Connection conn = DBConnectionUtil.getConnection();
             PreparedStatement pstmt =
                     conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {

            pstmt.setInt(1, notification.getUserId());
            pstmt.setString(2, notification.getType());
            pstmt.setString(3, notification.getMessage());
            pstmt.setBoolean(4, notification.isRead());

            if (notification.getRelatedUserId() > 0) {
                pstmt.setInt(5, notification.getRelatedUserId());
            } else {
                pstmt.setNull(5, Types.INTEGER);
            }

            if (notification.getRelatedPostId() > 0) {
                pstmt.setInt(6, notification.getRelatedPostId());
            } else {
                pstmt.setNull(6, Types.INTEGER);
            }

            pstmt.setTimestamp(7, Timestamp.valueOf(LocalDateTime.now()));

            int rowsAffected = pstmt.executeUpdate();

            if (rowsAffected > 0) {
                ResultSet rs = pstmt.getGeneratedKeys();
                if (rs.next()) {
                    notification.setNotificationId(rs.getInt(1));
                    return true;
                }
            }

            return false;

        } catch (SQLException e) {
            throw new DatabaseException("Failed to save notification: " + e.getMessage());
        }
    }

    @Override
    public List<Notification> findByUserId(int userId) {
        String sql =
                "SELECT * FROM notifications WHERE user_id = ? ORDER BY created_at DESC LIMIT 50";

        List<Notification> notifications = new ArrayList<>();

        try (Connection conn = DBConnectionUtil.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {

            pstmt.setInt(1, userId);
            ResultSet rs = pstmt.executeQuery();

            while (rs.next()) {
                Notification notification = new Notification();
                notification.setNotificationId(rs.getInt("notification_id"));
                notification.setUserId(rs.getInt("user_id"));
                notification.setType(rs.getString("type"));
                notification.setMessage(rs.getString("message"));
                notification.setRead(rs.getBoolean("is_read"));

                int relatedUserId = rs.getInt("related_user_id");
                if (!rs.wasNull()) {
                    notification.setRelatedUserId(relatedUserId);
                }

                int relatedPostId = rs.getInt("related_post_id");
                if (!rs.wasNull()) {
                    notification.setRelatedPostId(relatedPostId);
                }

                notification.setCreatedAt(
                        rs.getTimestamp("created_at").toLocalDateTime());

                notifications.add(notification);
            }

            return notifications;

        } catch (SQLException e) {
            throw new DatabaseException("Failed to find notifications: " + e.getMessage());
        }
    }

    @Override
    public int countUnread(int userId) {
        String sql =
                "SELECT COUNT(*) as count FROM notifications WHERE user_id = ? AND is_read = false";

        try (Connection conn = DBConnectionUtil.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {

            pstmt.setInt(1, userId);
            ResultSet rs = pstmt.executeQuery();

            if (rs.next()) {
                return rs.getInt("count");
            }

            return 0;

        } catch (SQLException e) {
            throw new DatabaseException("Failed to count unread notifications: " + e.getMessage());
        }
    }

    @Override
    public boolean markAsRead(int notificationId) {
        String sql =
                "UPDATE notifications SET is_read = true WHERE notification_id = ?";

        try (Connection conn = DBConnectionUtil.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {

            pstmt.setInt(1, notificationId);

            int rowsAffected = pstmt.executeUpdate();
            return rowsAffected > 0;

        } catch (SQLException e) {
            throw new DatabaseException("Failed to mark notification as read: " + e.getMessage());
        }
    }

    @Override
    public boolean markAllAsRead(int userId) {
        String sql =
                "UPDATE notifications SET is_read = true WHERE user_id = ? AND is_read = false";

        try (Connection conn = DBConnectionUtil.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {

            pstmt.setInt(1, userId);

            int rowsAffected = pstmt.executeUpdate();
            return rowsAffected > 0;

        } catch (SQLException e) {
            throw new DatabaseException("Failed to mark all as read: " + e.getMessage());
        }
    }

    @Override
    public NotificationPreference getPreferences(int userId) {
        String sql =
                "SELECT * FROM notification_preferences WHERE user_id = ?";

        try (Connection conn = DBConnectionUtil.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {

            pstmt.setInt(1, userId);
            ResultSet rs = pstmt.executeQuery();

            if (rs.next()) {
                NotificationPreference pref = new NotificationPreference();
                pref.setPreferenceId(rs.getInt("preference_id"));
                pref.setUserId(rs.getInt("user_id"));
                pref.setConnectionRequests(rs.getBoolean("connection_requests"));
                pref.setNewFollowers(rs.getBoolean("new_followers"));
                pref.setLikes(rs.getBoolean("likes"));
                pref.setComments(rs.getBoolean("comments"));
                pref.setShares(rs.getBoolean("shares"));
                pref.setNewPosts(rs.getBoolean("new_posts"));
                return pref;
            }

            NotificationPreference defaultPref = new NotificationPreference();
            defaultPref.setUserId(userId);
            defaultPref.setConnectionRequests(true);
            defaultPref.setNewFollowers(true);
            defaultPref.setLikes(true);
            defaultPref.setComments(true);
            defaultPref.setShares(true);
            defaultPref.setNewPosts(true);

            return defaultPref;

        } catch (SQLException e) {
            throw new DatabaseException("Failed to get preferences: " + e.getMessage());
        }
    }

    @Override
    public boolean updatePreferences(NotificationPreference preferences) {
        String checkSql =
                "SELECT COUNT(*) as count FROM notification_preferences WHERE user_id = ?";

        String insertSql =
                "INSERT INTO notification_preferences (user_id, connection_requests, " +
                "new_followers, likes, comments, shares, new_posts) VALUES (?, ?, ?, ?, ?, ?, ?)";

        String updateSql =
                "UPDATE notification_preferences SET connection_requests = ?, " +
                "new_followers = ?, likes = ?, comments = ?, shares = ?, new_posts = ? " +
                "WHERE user_id = ?";

        try (Connection conn = DBConnectionUtil.getConnection()) {

            boolean exists = false;

            try (PreparedStatement checkPstmt =
                         conn.prepareStatement(checkSql)) {

                checkPstmt.setInt(1, preferences.getUserId());
                ResultSet rs = checkPstmt.executeQuery();

                if (rs.next()) {
                    exists = rs.getInt("count") > 0;
                }
            }

            if (exists) {
                try (PreparedStatement pstmt =
                             conn.prepareStatement(updateSql)) {

                    pstmt.setBoolean(1, preferences.isConnectionRequests());
                    pstmt.setBoolean(2, preferences.isNewFollowers());
                    pstmt.setBoolean(3, preferences.isLikes());
                    pstmt.setBoolean(4, preferences.isComments());
                    pstmt.setBoolean(5, preferences.isShares());
                    pstmt.setBoolean(6, preferences.isNewPosts());
                    pstmt.setInt(7, preferences.getUserId());

                    int rowsAffected = pstmt.executeUpdate();
                    return rowsAffected > 0;
                }

            } else {
                try (PreparedStatement pstmt =
                             conn.prepareStatement(insertSql)) {

                    pstmt.setInt(1, preferences.getUserId());
                    pstmt.setBoolean(2, preferences.isConnectionRequests());
                    pstmt.setBoolean(3, preferences.isNewFollowers());
                    pstmt.setBoolean(4, preferences.isLikes());
                    pstmt.setBoolean(5, preferences.isComments());
                    pstmt.setBoolean(6, preferences.isShares());
                    pstmt.setBoolean(7, preferences.isNewPosts());

                    int rowsAffected = pstmt.executeUpdate();
                    return rowsAffected > 0;
                }
            }

//            return false;

        } catch (SQLException e) {
            throw new DatabaseException("Failed to update preferences: " + e.getMessage());
        }
    }
}
