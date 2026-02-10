package com.revconnect.repository;

import com.revconnect.dao.ConnectionDAO;
import com.revconnect.model.ConnectionRequest;
import com.revconnect.model.User;
import com.revconnect.util.DBConnectionUtil;
import com.revconnect.exception.DatabaseException;

import java.sql.*;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

public class ConnectionRepository implements ConnectionDAO {

    @Override
    public boolean sendRequest(int senderId, int receiverId) {
        if (areConnected(senderId, receiverId)) {
            return false;
        }

        String sql = "INSERT INTO connection_requests (sender_id, receiver_id, status, created_at) " +
                     "VALUES (?, ?, 'PENDING', ?)";

        try (Connection conn = DBConnectionUtil.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {

            pstmt.setInt(1, senderId);
            pstmt.setInt(2, receiverId);
            pstmt.setTimestamp(3, Timestamp.valueOf(LocalDateTime.now()));

            int rowsAffected = pstmt.executeUpdate();

            if (rowsAffected > 0) {
                return true;
            }

            return false;

        } catch (SQLException e) {
            throw new DatabaseException("Failed to send connection request: " + e.getMessage());
        }
    }

    @Override
    public boolean acceptRequest(int requestId) {
        String sql = "UPDATE connection_requests SET status = 'ACCEPTED' " +
                     "WHERE request_id = ? AND status = 'PENDING'";

        try (Connection conn = DBConnectionUtil.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {

            pstmt.setInt(1, requestId);

            int rowsAffected = pstmt.executeUpdate();

            if (rowsAffected > 0) {
                return true;
            }

            return false;

        } catch (SQLException e) {
            throw new DatabaseException("Failed to accept connection request: " + e.getMessage());
        }
    }

    @Override
    public boolean rejectRequest(int requestId) {
        String sql = "UPDATE connection_requests SET status = 'REJECTED' " +
                     "WHERE request_id = ? AND status = 'PENDING'";

        try (Connection conn = DBConnectionUtil.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {

            pstmt.setInt(1, requestId);

            int rowsAffected = pstmt.executeUpdate();

            if (rowsAffected > 0) {
                return true;
            }

            return false;

        } catch (SQLException e) {
            throw new DatabaseException("Failed to reject connection request: " + e.getMessage());
        }
    }

    @Override
    public List<ConnectionRequest> findPendingRequests(int userId) {
        String sql = "SELECT cr.*, u.username as sender_username, p.name as sender_name " +
                     "FROM connection_requests cr " +
                     "JOIN users u ON cr.sender_id = u.user_id " +
                     "LEFT JOIN profiles p ON cr.sender_id = p.user_id " +
                     "WHERE cr.receiver_id = ? AND cr.status = 'PENDING' " +
                     "ORDER BY cr.created_at DESC";

        List<ConnectionRequest> requests = new ArrayList<>();

        try (Connection conn = DBConnectionUtil.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {

            pstmt.setInt(1, userId);
            ResultSet rs = pstmt.executeQuery();

            while (rs.next()) {
                ConnectionRequest request = new ConnectionRequest();
                request.setRequestId(rs.getInt("request_id"));
                request.setSenderId(rs.getInt("sender_id"));
                request.setReceiverId(rs.getInt("receiver_id"));
                request.setStatus(rs.getString("status"));
                request.setCreatedAt(rs.getTimestamp("created_at").toLocalDateTime());

                try {
                    Timestamp updatedAt = rs.getTimestamp("updated_at");
                    if (updatedAt != null) {
                        request.setUpdatedAt(updatedAt.toLocalDateTime());
                    }
                } catch (SQLException e) {
                    // Column doesn't exist, ignore
                }

                requests.add(request);
            }

            return requests;

        } catch (SQLException e) {
            throw new DatabaseException("Failed to find pending requests: " + e.getMessage());
        }
    }

    @Override
    public List<User> findConnections(int userId) {
        String sql = "SELECT DISTINCT u.*, p.name as profile_name " +
                     "FROM users u " +
                     "LEFT JOIN profiles p ON u.user_id = p.user_id " +
                     "JOIN connection_requests cr ON " +
                     "(cr.sender_id = u.user_id AND cr.receiver_id = ?) OR " +
                     "(cr.receiver_id = u.user_id AND cr.sender_id = ?) " +
                     "WHERE cr.status = 'ACCEPTED' " +
                     "ORDER BY p.name, u.username";

        List<User> connections = new ArrayList<>();

        try (Connection conn = DBConnectionUtil.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {

            pstmt.setInt(1, userId);
            pstmt.setInt(2, userId);
            ResultSet rs = pstmt.executeQuery();

            while (rs.next()) {
                User user = new User();
                user.setUserId(rs.getInt("user_id"));
                user.setEmail(rs.getString("email"));
                user.setUsername(rs.getString("username"));
                user.setUserType(rs.getString("user_type"));
                user.setPrivate(rs.getBoolean("is_private"));
                user.setCreatedAt(rs.getTimestamp("created_at").toLocalDateTime());
                connections.add(user);
            }

            return connections;

        } catch (SQLException e) {
            throw new DatabaseException("Failed to find connections: " + e.getMessage());
        }
    }

    @Override
    public boolean removeConnection(int userId, int connectionId) {
        String sql = "DELETE FROM connection_requests WHERE " +
                     "((sender_id = ? AND receiver_id = ?) OR (sender_id = ? AND receiver_id = ?)) " +
                     "AND status = 'ACCEPTED'";

        try (Connection conn = DBConnectionUtil.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {

            pstmt.setInt(1, userId);
            pstmt.setInt(2, connectionId);
            pstmt.setInt(3, connectionId);
            pstmt.setInt(4, userId);

            int rowsAffected = pstmt.executeUpdate();

            if (rowsAffected > 0) {
                return true;
            }

            return false;

        } catch (SQLException e) {
            throw new DatabaseException("Failed to remove connection: " + e.getMessage());
        }
    }

    @Override
    public boolean areConnected(int userId1, int userId2) {
        String sql = "SELECT COUNT(*) as count FROM connection_requests WHERE " +
                     "((sender_id = ? AND receiver_id = ?) OR (sender_id = ? AND receiver_id = ?)) " +
                     "AND status = 'ACCEPTED'";

        try (Connection conn = DBConnectionUtil.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {

            pstmt.setInt(1, userId1);
            pstmt.setInt(2, userId2);
            pstmt.setInt(3, userId2);
            pstmt.setInt(4, userId1);

            ResultSet rs = pstmt.executeQuery();

            if (rs.next()) {
                return rs.getInt("count") > 0;
            }

            return false;

        } catch (SQLException e) {
            throw new DatabaseException("Failed to check connection: " + e.getMessage());
        }
    }
}
