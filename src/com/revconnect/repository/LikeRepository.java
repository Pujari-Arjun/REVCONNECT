package com.revconnect.repository;

import com.revconnect.dao.LikeDAO;
import com.revconnect.util.DBConnectionUtil;
import com.revconnect.exception.DatabaseException;

import java.sql.*;
import java.time.LocalDateTime;

public class LikeRepository implements LikeDAO {

    @Override
    public boolean addLike(int postId, int userId) {
        Connection conn = null;
        try {
            conn = DBConnectionUtil.getConnection();
            conn.setAutoCommit(false);

            if (hasUserLiked(postId, userId)) {
                conn.rollback();
                return false;
            }

            String insertSql = "INSERT INTO likes (post_id, user_id, created_at) VALUES (?, ?, ?)";

            try (PreparedStatement pstmt = conn.prepareStatement(insertSql)) {
                pstmt.setInt(1, postId);
                pstmt.setInt(2, userId);
                pstmt.setTimestamp(3, Timestamp.valueOf(LocalDateTime.now()));

                int rowsAffected = pstmt.executeUpdate();

                if (rowsAffected > 0) {
                    String updateAnalyticsSql =
                            "UPDATE post_analytics SET total_likes = total_likes + 1, " +
                            "last_updated = ? WHERE post_id = ?";

                    try (PreparedStatement analyticsPstmt =
                                 conn.prepareStatement(updateAnalyticsSql)) {
                        analyticsPstmt.setTimestamp(1, Timestamp.valueOf(LocalDateTime.now()));
                        analyticsPstmt.setInt(2, postId);
                        analyticsPstmt.executeUpdate();
                    }

                    conn.commit();
                    return true;
                }
            }

            conn.rollback();
            return false;

        } catch (SQLException e) {
            if (conn != null) {
                try {
                    conn.rollback();
                } catch (SQLException ex) {
                    // rollback failed
                }
            }
            throw new DatabaseException("Failed to add like: " + e.getMessage());

        } finally {
            if (conn != null) {
                try {
                    conn.setAutoCommit(true);
                    conn.close();
                } catch (SQLException e) {
                    // close failed
                }
            }
        }
    }

    @Override
    public boolean removeLike(int postId, int userId) {
        Connection conn = null;
        try {
            conn = DBConnectionUtil.getConnection();
            conn.setAutoCommit(false);

            String deleteSql = "DELETE FROM likes WHERE post_id = ? AND user_id = ?";

            try (PreparedStatement pstmt = conn.prepareStatement(deleteSql)) {
                pstmt.setInt(1, postId);
                pstmt.setInt(2, userId);

                int rowsAffected = pstmt.executeUpdate();

                if (rowsAffected > 0) {
                    String updateAnalyticsSql =
                            "UPDATE post_analytics SET total_likes = total_likes - 1, " +
                            "last_updated = ? WHERE post_id = ?";

                    try (PreparedStatement analyticsPstmt =
                                 conn.prepareStatement(updateAnalyticsSql)) {
                        analyticsPstmt.setTimestamp(1, Timestamp.valueOf(LocalDateTime.now()));
                        analyticsPstmt.setInt(2, postId);
                        analyticsPstmt.executeUpdate();
                    }

                    conn.commit();
                    return true;
                }
            }

            conn.rollback();
            return false;

        } catch (SQLException e) {
            if (conn != null) {
                try {
                    conn.rollback();
                } catch (SQLException ex) {
                    // rollback failed
                }
            }
            throw new DatabaseException("Failed to remove like: " + e.getMessage());

        } finally {
            if (conn != null) {
                try {
                    conn.setAutoCommit(true);
                    conn.close();
                } catch (SQLException e) {
                    // close failed
                }
            }
        }
    }

    @Override
    public int getLikeCount(int postId) {
        String sql = "SELECT COUNT(*) as like_count FROM likes WHERE post_id = ?";

        try (Connection conn = DBConnectionUtil.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {

            pstmt.setInt(1, postId);
            ResultSet rs = pstmt.executeQuery();

            if (rs.next()) {
                return rs.getInt("like_count");
            }

            return 0;

        } catch (SQLException e) {
            throw new DatabaseException("Failed to get like count: " + e.getMessage());
        }
    }

    @Override
    public boolean hasUserLiked(int postId, int userId) {
        String sql = "SELECT COUNT(*) as count FROM likes WHERE post_id = ? AND user_id = ?";

        try (Connection conn = DBConnectionUtil.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {

            pstmt.setInt(1, postId);
            pstmt.setInt(2, userId);
            ResultSet rs = pstmt.executeQuery();

            if (rs.next()) {
                return rs.getInt("count") > 0;
            }

            return false;

        } catch (SQLException e) {
            throw new DatabaseException("Failed to check like status: " + e.getMessage());
        }
    }
}
