package com.revconnect.repository;

import com.revconnect.dao.CommentDAO;
import com.revconnect.model.Comment;
import com.revconnect.util.DBConnectionUtil;
import com.revconnect.exception.DatabaseException;

import java.sql.*;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

public class CommentRepository implements CommentDAO {

    @Override
    public boolean addComment(int postId, int userId, String text) {
        Connection conn = null;
        try {
            conn = DBConnectionUtil.getConnection();
            conn.setAutoCommit(false);

            String insertSql = "INSERT INTO comments (post_id, user_id, comment_text, created_at) " +
                               "VALUES (?, ?, ?, ?)";

            try (PreparedStatement pstmt = conn.prepareStatement(insertSql, Statement.RETURN_GENERATED_KEYS)) {
                pstmt.setInt(1, postId);
                pstmt.setInt(2, userId);
                pstmt.setString(3, text);
                pstmt.setTimestamp(4, Timestamp.valueOf(LocalDateTime.now()));

                int rowsAffected = pstmt.executeUpdate();

                if (rowsAffected > 0) {
                    String updateAnalyticsSql =
                            "UPDATE post_analytics SET total_comments = total_comments + 1, " +
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
            throw new DatabaseException("Failed to add comment: " + e.getMessage());

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
    public List<Comment> findByPostId(int postId) {
        String sql =
                "SELECT c.*, u.username, p.name as profile_name " +
                "FROM comments c " +
                "JOIN users u ON c.user_id = u.user_id " +
                "LEFT JOIN profiles p ON c.user_id = p.user_id " +
                "WHERE c.post_id = ? " +
                "ORDER BY c.created_at ASC";

        List<Comment> comments = new ArrayList<>();

        try (Connection conn = DBConnectionUtil.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {

            pstmt.setInt(1, postId);
            ResultSet rs = pstmt.executeQuery();

            while (rs.next()) {
                Comment comment = new Comment();
                comment.setCommentId(rs.getInt("comment_id"));
                comment.setPostId(rs.getInt("post_id"));
                comment.setUserId(rs.getInt("user_id"));
                comment.setCommentText(rs.getString("comment_text"));
                comment.setCreatedAt(
                        rs.getTimestamp("created_at").toLocalDateTime());
                comments.add(comment);
            }

            return comments;

        } catch (SQLException e) {
            throw new DatabaseException("Failed to find comments: " + e.getMessage());
        }
    }

    @Override
    public boolean deleteComment(int commentId, int userId) {
        Connection conn = null;
        try {
            conn = DBConnectionUtil.getConnection();
            conn.setAutoCommit(false);

            String getPostIdSql =
                    "SELECT post_id FROM comments WHERE comment_id = ? AND user_id = ?";
            int postId = 0;

            try (PreparedStatement pstmt = conn.prepareStatement(getPostIdSql)) {
                pstmt.setInt(1, commentId);
                pstmt.setInt(2, userId);
                ResultSet rs = pstmt.executeQuery();

                if (rs.next()) {
                    postId = rs.getInt("post_id");
                } else {
                    conn.rollback();
                    return false;
                }
            }

            String deleteSql =
                    "DELETE FROM comments WHERE comment_id = ? AND user_id = ?";

            try (PreparedStatement pstmt = conn.prepareStatement(deleteSql)) {
                pstmt.setInt(1, commentId);
                pstmt.setInt(2, userId);

                int rowsAffected = pstmt.executeUpdate();

                if (rowsAffected > 0) {
                    String updateAnalyticsSql =
                            "UPDATE post_analytics SET total_comments = total_comments - 1, " +
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
            throw new DatabaseException("Failed to delete comment: " + e.getMessage());

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
}
