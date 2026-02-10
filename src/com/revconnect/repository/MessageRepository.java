package com.revconnect.repository;

import com.revconnect.dao.MessageDAO;
import com.revconnect.model.Message;
import com.revconnect.util.DBConnectionUtil;
import com.revconnect.exception.DatabaseException;

import java.sql.*;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

public class MessageRepository implements MessageDAO {

    @Override
    public boolean sendMessage(Message message) {
        String sql = "INSERT INTO messages (sender_id, receiver_id, message_text, is_read, created_at) " +
                     "VALUES (?, ?, ?, false, ?)";

        try (Connection conn = DBConnectionUtil.getConnection();
             PreparedStatement pstmt =
                     conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {

            pstmt.setInt(1, message.getSenderId());
            pstmt.setInt(2, message.getReceiverId());
            pstmt.setString(3, message.getMessageText());
            pstmt.setTimestamp(4, Timestamp.valueOf(LocalDateTime.now()));

            int rowsAffected = pstmt.executeUpdate();

            if (rowsAffected > 0) {
                ResultSet rs = pstmt.getGeneratedKeys();
                if (rs.next()) {
                    message.setMessageId(rs.getInt(1));
                    return true;
                }
            }

            return false;

        } catch (SQLException e) {
            throw new DatabaseException("Failed to send message: " + e.getMessage());
        }
    }

    @Override
    public List<Message> getConversation(int userId, int otherUserId) {
        String sql = "SELECT * FROM messages WHERE " +
                     "(sender_id = ? AND receiver_id = ?) OR " +
                     "(sender_id = ? AND receiver_id = ?) " +
                     "ORDER BY created_at ASC";

        List<Message> messages = new ArrayList<>();

        try (Connection conn = DBConnectionUtil.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {

            pstmt.setInt(1, userId);
            pstmt.setInt(2, otherUserId);
            pstmt.setInt(3, otherUserId);
            pstmt.setInt(4, userId);

            ResultSet rs = pstmt.executeQuery();

            while (rs.next()) {
                Message message = new Message();
                message.setMessageId(rs.getInt("message_id"));
                message.setSenderId(rs.getInt("sender_id"));
                message.setReceiverId(rs.getInt("receiver_id"));
                message.setMessageText(rs.getString("message_text"));
                message.setRead(rs.getBoolean("is_read"));
                message.setCreatedAt(
                        rs.getTimestamp("created_at").toLocalDateTime());
                messages.add(message);
            }

            return messages;

        } catch (SQLException e) {
            throw new DatabaseException("Failed to get conversation: " + e.getMessage());
        }
    }

    @Override
    public boolean markAsRead(int userId, int otherUserId) {
        String sql = "UPDATE messages SET is_read = true " +
                     "WHERE sender_id = ? AND receiver_id = ? AND is_read = false";

        try (Connection conn = DBConnectionUtil.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {

            pstmt.setInt(1, otherUserId);
            pstmt.setInt(2, userId);

            int rowsAffected = pstmt.executeUpdate();
            return rowsAffected > 0;

        } catch (SQLException e) {
            throw new DatabaseException("Failed to mark messages as read: " + e.getMessage());
        }
    }

    @Override
    public boolean deleteConversation(int userId, int otherUserId) {
        String sql = "DELETE FROM messages WHERE " +
                     "(sender_id = ? AND receiver_id = ?) OR " +
                     "(sender_id = ? AND receiver_id = ?)";

        try (Connection conn = DBConnectionUtil.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {

            pstmt.setInt(1, userId);
            pstmt.setInt(2, otherUserId);
            pstmt.setInt(3, otherUserId);
            pstmt.setInt(4, userId);

            int rowsAffected = pstmt.executeUpdate();
            return rowsAffected > 0;

        } catch (SQLException e) {
            throw new DatabaseException("Failed to delete conversation: " + e.getMessage());
        }
    }

    @Override
    public int getUnreadCount(int userId) {
        String sql =
                "SELECT COUNT(*) as count FROM messages " +
                "WHERE receiver_id = ? AND is_read = false";

        try (Connection conn = DBConnectionUtil.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {

            pstmt.setInt(1, userId);
            ResultSet rs = pstmt.executeQuery();

            if (rs.next()) {
                return rs.getInt("count");
            }

            return 0;

        } catch (SQLException e) {
            throw new DatabaseException("Failed to get unread count: " + e.getMessage());
        }
    }
}
