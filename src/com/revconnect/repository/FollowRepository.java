package com.revconnect.repository;

import com.revconnect.dao.FollowDAO;
import com.revconnect.model.User;
import com.revconnect.util.DBConnectionUtil;
import com.revconnect.exception.DatabaseException;

import java.sql.*;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

public class FollowRepository implements FollowDAO {

    @Override
    public boolean followUser(int followerId, int followingId) {
        try {
            if (isFollowing(followerId, followingId)) {
                return false;
            }
        } catch (DatabaseException e) {
            throw e;
        }

        String sql = "INSERT INTO follows (follower_id, following_id, created_at) VALUES (?, ?, ?)";

        try (Connection conn = DBConnectionUtil.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {

            pstmt.setInt(1, followerId);
            pstmt.setInt(2, followingId);
            pstmt.setTimestamp(3, Timestamp.valueOf(LocalDateTime.now()));

            int rowsAffected = pstmt.executeUpdate();

            if (rowsAffected > 0) {
                return true;
            }

            return false;

        } catch (SQLException e) {
            throw new DatabaseException("Failed to follow user: " + e.getMessage());
        }
    }

    @Override
    public boolean unfollowUser(int followerId, int followingId) {
        String sql = "DELETE FROM follows WHERE follower_id = ? AND following_id = ?";

        try (Connection conn = DBConnectionUtil.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {

            pstmt.setInt(1, followerId);
            pstmt.setInt(2, followingId);

            int rowsAffected = pstmt.executeUpdate();

            if (rowsAffected > 0) {
                return true;
            }

            return false;

        } catch (SQLException e) {
            throw new DatabaseException("Failed to unfollow user: " + e.getMessage());
        }
    }

    @Override
    public List<User> findFollowers(int userId) {
        String sql = "SELECT u.*, p.name as profile_name " +
                     "FROM users u " +
                     "LEFT JOIN profiles p ON u.user_id = p.user_id " +
                     "JOIN follows f ON u.user_id = f.follower_id " +
                     "WHERE f.following_id = ? " +
                     "ORDER BY f.created_at DESC";

        List<User> followers = new ArrayList<>();

        try (Connection conn = DBConnectionUtil.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {

            pstmt.setInt(1, userId);
            ResultSet rs = pstmt.executeQuery();

            while (rs.next()) {
                User user = new User();
                user.setUserId(rs.getInt("user_id"));
                user.setEmail(rs.getString("email"));
                user.setUsername(rs.getString("username"));
                user.setUserType(rs.getString("user_type"));
                user.setPrivate(rs.getBoolean("is_private"));
                user.setCreatedAt(rs.getTimestamp("created_at").toLocalDateTime());
                followers.add(user);
            }

            return followers;

        } catch (SQLException e) {
            throw new DatabaseException("Failed to find followers: " + e.getMessage());
        }
    }

    @Override
    public List<User> findFollowing(int userId) {
        String sql = "SELECT u.*, p.name as profile_name " +
                     "FROM users u " +
                     "LEFT JOIN profiles p ON u.user_id = p.user_id " +
                     "JOIN follows f ON u.user_id = f.following_id " +
                     "WHERE f.follower_id = ? " +
                     "ORDER BY f.created_at DESC";

        List<User> following = new ArrayList<>();

        try (Connection conn = DBConnectionUtil.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {

            pstmt.setInt(1, userId);
            ResultSet rs = pstmt.executeQuery();

            while (rs.next()) {
                User user = new User();
                user.setUserId(rs.getInt("user_id"));
                user.setEmail(rs.getString("email"));
                user.setUsername(rs.getString("username"));
                user.setUserType(rs.getString("user_type"));
                user.setPrivate(rs.getBoolean("is_private"));
                user.setCreatedAt(rs.getTimestamp("created_at").toLocalDateTime());
                following.add(user);
            }

            return following;

        } catch (SQLException e) {
            throw new DatabaseException("Failed to find following: " + e.getMessage());
        }
    }

    @Override
    public boolean isFollowing(int followerId, int followingId) {
        String sql = "SELECT COUNT(*) as count FROM follows WHERE follower_id = ? AND following_id = ?";

        try (Connection conn = DBConnectionUtil.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {

            pstmt.setInt(1, followerId);
            pstmt.setInt(2, followingId);
            ResultSet rs = pstmt.executeQuery();

            if (rs.next()) {
                return rs.getInt("count") > 0;
            }

            return false;

        } catch (SQLException e) {
            throw new DatabaseException("Failed to check follow status: " + e.getMessage());
        }
    }
}
