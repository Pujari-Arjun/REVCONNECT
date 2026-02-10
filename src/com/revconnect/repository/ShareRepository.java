package com.revconnect.repository;

import com.revconnect.dao.ShareDAO;
import com.revconnect.model.Share;
import com.revconnect.util.DBConnectionUtil;
import com.revconnect.exception.DatabaseException;

import java.sql.*;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

public class ShareRepository implements ShareDAO {

    @Override
    public boolean sharePost(int postId, int userId) {
        Connection conn = null;
        try {
            conn = DBConnectionUtil.getConnection();
            conn.setAutoCommit(false);

            String insertSql =
                    "INSERT INTO shares (post_id, user_id, created_at) VALUES (?, ?, ?)";

            try (PreparedStatement pstmt =
                         conn.prepareStatement(insertSql)) {

                pstmt.setInt(1, postId);
                pstmt.setInt(2, userId);
                pstmt.setTimestamp(
                        3, Timestamp.valueOf(LocalDateTime.now()));

                int rowsAffected = pstmt.executeUpdate();

                if (rowsAffected > 0) {
                    String updateAnalyticsSql =
                            "UPDATE post_analytics SET total_shares = total_shares + 1, " +
                            "last_updated = ? WHERE post_id = ?";

                    try (PreparedStatement analyticsPstmt =
                                 conn.prepareStatement(updateAnalyticsSql)) {

                        analyticsPstmt.setTimestamp(
                                1, Timestamp.valueOf(LocalDateTime.now()));
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
                    // ignore
                }
            }
            throw new DatabaseException("Failed to share post: " + e.getMessage());
        } finally {
            if (conn != null) {
                try {
                    conn.setAutoCommit(true);
                    conn.close();
                } catch (SQLException e) {
                    // ignore
                }
            }
        }
    }

    @Override
    public int getShareCount(int postId) {
        String sql =
                "SELECT COUNT(*) as share_count FROM shares WHERE post_id = ?";

        try (Connection conn = DBConnectionUtil.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {

            pstmt.setInt(1, postId);
            ResultSet rs = pstmt.executeQuery();

            if (rs.next()) {
                return rs.getInt("share_count");
            }

            return 0;

        } catch (SQLException e) {
            throw new DatabaseException("Failed to get share count: " + e.getMessage());
        }
    }

    @Override
    public List<Share> findByPostId(int postId) {
        String sql =
                "SELECT s.*, u.username, p.name as profile_name " +
                "FROM shares s " +
                "JOIN users u ON s.user_id = u.user_id " +
                "LEFT JOIN profiles p ON s.user_id = p.user_id " +
                "WHERE s.post_id = ? " +
                "ORDER BY s.created_at DESC";

        List<Share> shares = new ArrayList<>();

        try (Connection conn = DBConnectionUtil.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {

            pstmt.setInt(1, postId);
            ResultSet rs = pstmt.executeQuery();

            while (rs.next()) {
                Share share = new Share();
                share.setShareId(rs.getInt("share_id"));
                share.setPostId(rs.getInt("post_id"));
                share.setUserId(rs.getInt("user_id"));
                share.setCreatedAt(
                        rs.getTimestamp("created_at").toLocalDateTime());
                shares.add(share);
            }

            return shares;

        } catch (SQLException e) {
            throw new DatabaseException("Failed to find shares: " + e.getMessage());
        }
    }
}
