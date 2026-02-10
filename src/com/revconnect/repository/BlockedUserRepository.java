package com.revconnect.repository;

import com.revconnect.dao.BlockedUserDAO;
import com.revconnect.model.User;
import com.revconnect.util.DBConnectionUtil;
import com.revconnect.exception.DatabaseException;

import java.sql.*;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

public class BlockedUserRepository implements BlockedUserDAO {

    @Override
    public boolean blockUser(int blockerId, int blockedId) {
        if (isBlocked(blockerId, blockedId)) {
            return false;
        }

        String sql = "INSERT INTO blocked_users (blocker_id, blocked_id, created_at) VALUES (?, ?, ?)";

        try (Connection conn = DBConnectionUtil.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {

            pstmt.setInt(1, blockerId);
            pstmt.setInt(2, blockedId);
            pstmt.setTimestamp(3, Timestamp.valueOf(LocalDateTime.now()));

            int rowsAffected = pstmt.executeUpdate();

            if (rowsAffected > 0) {
                return true;
            }

            return false;

        } catch (SQLException e) {
            throw new DatabaseException("Failed to block user: " + e.getMessage());
        }
    }

    @Override
    public boolean unblockUser(int blockerId, int blockedId) {
        String sql = "DELETE FROM blocked_users WHERE blocker_id = ? AND blocked_id = ?";

        try (Connection conn = DBConnectionUtil.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {

            pstmt.setInt(1, blockerId);
            pstmt.setInt(2, blockedId);

            int rowsAffected = pstmt.executeUpdate();

            if (rowsAffected > 0) {
                return true;
            }

            return false;

        } catch (SQLException e) {
            throw new DatabaseException("Failed to unblock user: " + e.getMessage());
        }
    }

    @Override
    public boolean isBlocked(int blockerId, int blockedId) {
        String sql = "SELECT COUNT(*) as count FROM blocked_users WHERE blocker_id = ? AND blocked_id = ?";

        try (Connection conn = DBConnectionUtil.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {

            pstmt.setInt(1, blockerId);
            pstmt.setInt(2, blockedId);
            ResultSet rs = pstmt.executeQuery();

            if (rs.next()) {
                return rs.getInt("count") > 0;
            }

            return false;

        } catch (SQLException e) {
            throw new DatabaseException("Failed to check block status: " + e.getMessage());
        }
    }

    @Override
    public List<User> findBlockedUsers(int blockerId) {
        String sql = "SELECT u.*, p.name as profile_name " +
                     "FROM users u " +
                     "LEFT JOIN profiles p ON u.user_id = p.user_id " +
                     "JOIN blocked_users bu ON u.user_id = bu.blocked_id " +
                     "WHERE bu.blocker_id = ? " +
                     "ORDER BY bu.created_at DESC";

        List<User> blockedUsers = new ArrayList<>();

        try (Connection conn = DBConnectionUtil.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {

            pstmt.setInt(1, blockerId);
            ResultSet rs = pstmt.executeQuery();

            while (rs.next()) {
                User user = new User();
                user.setUserId(rs.getInt("user_id"));
                user.setEmail(rs.getString("email"));
                user.setUsername(rs.getString("username"));
                user.setUserType(rs.getString("user_type"));
                user.setPrivate(rs.getBoolean("is_private"));
                user.setCreatedAt(rs.getTimestamp("created_at").toLocalDateTime());
                blockedUsers.add(user);
            }

            return blockedUsers;

        } catch (SQLException e) {
            throw new DatabaseException("Failed to find blocked users: " + e.getMessage());
        }
    }
}
