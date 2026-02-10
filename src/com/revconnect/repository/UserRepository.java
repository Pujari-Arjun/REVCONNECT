package com.revconnect.repository;

import com.revconnect.dao.UserDAO;
import com.revconnect.model.User;
import com.revconnect.util.DBConnectionUtil;
import com.revconnect.util.PasswordUtil;
import com.revconnect.util.ValidationUtil;
import com.revconnect.exception.DatabaseException;

import java.sql.*;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

public class UserRepository implements UserDAO {

    @Override
    public boolean registerUser(User user) {
        if (!ValidationUtil.isValidEmail(user.getEmail())) {
            throw new DatabaseException("Invalid email format");
        }
        if (!ValidationUtil.isValidUsername(user.getUsername())) {
            throw new DatabaseException("Invalid username format");
        }

        String sql =
                "INSERT INTO users (email, username, password, password_hint, " +
                "user_type, is_private, created_at) VALUES (?, ?, ?, ?, ?, ?, ?)";

        try (Connection conn = DBConnectionUtil.getConnection();
             PreparedStatement pstmt =
                     conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {

            pstmt.setString(1, user.getEmail().toLowerCase());
            pstmt.setString(2, user.getUsername());
            pstmt.setString(3, user.getPassword());
            pstmt.setString(4, user.getPasswordHint());
            pstmt.setString(5, user.getUserType());
            pstmt.setBoolean(6, user.isPrivate());
            pstmt.setTimestamp(7,
                    Timestamp.valueOf(LocalDateTime.now()));

            int rowsAffected = pstmt.executeUpdate();

            if (rowsAffected > 0) {
                ResultSet rs = pstmt.getGeneratedKeys();
                if (rs.next()) {
                    user.setUserId(rs.getInt(1));
                    return true;
                }
            }

            return false;

        } catch (SQLException e) {
            throw new DatabaseException(
                    "Failed to register user: " + e.getMessage());
        }
    }

    @Override
    public User findByEmail(String email) {
        String sql =
                "SELECT * FROM users WHERE email = ?";

        try (Connection conn = DBConnectionUtil.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {

            pstmt.setString(1, email.toLowerCase());
            ResultSet rs = pstmt.executeQuery();

            if (rs.next()) {
                return mapResultSetToUser(rs);
            }

            return null;

        } catch (SQLException e) {
            throw new DatabaseException(
                    "Failed to find user by email: " + e.getMessage());
        }
    }

    @Override
    public User findByUsername(String username) {
        String sql =
                "SELECT * FROM users WHERE username = ?";

        try (Connection conn = DBConnectionUtil.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {

            pstmt.setString(1, username);
            ResultSet rs = pstmt.executeQuery();

            if (rs.next()) {
                return mapResultSetToUser(rs);
            }

            return null;

        } catch (SQLException e) {
            throw new DatabaseException(
                    "Failed to find user by username: " + e.getMessage());
        }
    }

    @Override
    public User findById(int userId) {
        String sql =
                "SELECT * FROM users WHERE user_id = ?";

        try (Connection conn = DBConnectionUtil.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {

            pstmt.setInt(1, userId);
            ResultSet rs = pstmt.executeQuery();

            if (rs.next()) {
                return mapResultSetToUser(rs);
            }

            return null;

        } catch (SQLException e) {
            throw new DatabaseException(
                    "Failed to find user by ID: " + e.getMessage());
        }
    }

    @Override
    public boolean updatePassword(int userId, String newPasswordHash) {
        String sql =
                "UPDATE users SET password = ? WHERE user_id = ?";

        try (Connection conn = DBConnectionUtil.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {

            pstmt.setString(1, newPasswordHash);
            pstmt.setInt(2, userId);

            int rowsAffected = pstmt.executeUpdate();

            if (rowsAffected > 0) {
                return true;
            }

            return false;

        } catch (SQLException e) {
            throw new DatabaseException(
                    "Failed to update password: " + e.getMessage());
        }
    }

    @Override
    public List<User> searchUsers(String keyword) {
        String sql =
                "SELECT u.* FROM users u " +
                "LEFT JOIN profiles p ON u.user_id = p.user_id " +
                "WHERE u.username LIKE ? OR p.name LIKE ? " +
                "LIMIT 50";

        List<User> users = new ArrayList<>();

        try (Connection conn = DBConnectionUtil.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {

            String searchPattern = "%" + keyword + "%";
            pstmt.setString(1, searchPattern);
            pstmt.setString(2, searchPattern);

            ResultSet rs = pstmt.executeQuery();

            while (rs.next()) {
                users.add(mapResultSetToUser(rs));
            }

            return users;

        } catch (SQLException e) {
            throw new DatabaseException(
                    "Failed to search users: " + e.getMessage());
        }
    }

    @Override
    public boolean updatePrivacy(int userId, boolean isPrivate) {
        String sql =
                "UPDATE users SET is_private = ? WHERE user_id = ?";

        try (Connection conn = DBConnectionUtil.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {

            pstmt.setBoolean(1, isPrivate);
            pstmt.setInt(2, userId);

            int rowsAffected = pstmt.executeUpdate();

            if (rowsAffected > 0) {
                return true;
            }

            return false;

        } catch (SQLException e) {
            throw new DatabaseException(
                    "Failed to update privacy: " + e.getMessage());
        }
    }

    private User mapResultSetToUser(ResultSet rs) throws SQLException {
        User user = new User();
        user.setUserId(rs.getInt("user_id"));
        user.setEmail(rs.getString("email"));
        user.setUsername(rs.getString("username"));
        user.setPassword(rs.getString("password"));
        user.setPasswordHint(rs.getString("password_hint"));
        user.setUserType(rs.getString("user_type"));
        user.setPrivate(rs.getBoolean("is_private"));
        user.setCreatedAt(
                rs.getTimestamp("created_at").toLocalDateTime());

        try {
            Timestamp updatedAt = rs.getTimestamp("updated_at");
            if (updatedAt != null) {
                user.setUpdatedAt(updatedAt.toLocalDateTime());
            }
        } catch (SQLException e) {
            // ignore
        }

        return user;
    }
}
