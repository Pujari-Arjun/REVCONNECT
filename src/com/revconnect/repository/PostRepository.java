package com.revconnect.repository;

import com.revconnect.dao.PostDAO;
import com.revconnect.model.Post;
import com.revconnect.model.PostAnalytics;
import com.revconnect.util.DBConnectionUtil;
import com.revconnect.exception.DatabaseException;

import java.sql.*;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

public class PostRepository implements PostDAO {

    @Override
    public boolean createPost(Post post) {
        Connection conn = null;
        try {
            conn = DBConnectionUtil.getConnection();
            conn.setAutoCommit(false);

            String postSql = "INSERT INTO posts (user_id, content, hashtags, is_promotional, " +
                            "is_pinned, pin_order, created_at) VALUES (?, ?, ?, ?, ?, ?, ?)";

            try (PreparedStatement pstmt =
                         conn.prepareStatement(postSql, Statement.RETURN_GENERATED_KEYS)) {

                pstmt.setInt(1, post.getUserId());
                pstmt.setString(2, post.getContent());
                pstmt.setString(3, post.getHashtags());
                pstmt.setBoolean(4, post.isPromotional());
                pstmt.setBoolean(5, post.isPinned());
                pstmt.setInt(6, post.getPinOrder());
                pstmt.setTimestamp(7, Timestamp.valueOf(LocalDateTime.now()));

                int rowsAffected = pstmt.executeUpdate();

                if (rowsAffected > 0) {
                    ResultSet rs = pstmt.getGeneratedKeys();
                    if (rs.next()) {
                        int postId = rs.getInt(1);
                        post.setPostId(postId);

                        String analyticsSql =
                                "INSERT INTO post_analytics (post_id, total_likes, " +
                                "total_comments, total_shares, unique_viewers, last_updated) " +
                                "VALUES (?, 0, 0, 0, 0, ?)";

                        try (PreparedStatement analyticsPstmt =
                                     conn.prepareStatement(analyticsSql)) {

                            analyticsPstmt.setInt(1, postId);
                            analyticsPstmt.setTimestamp(
                                    2, Timestamp.valueOf(LocalDateTime.now()));
                            analyticsPstmt.executeUpdate();
                        }

                        conn.commit();
                        return true;
                    }
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
            throw new DatabaseException("Failed to create post: " + e.getMessage());
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
    public boolean updatePost(Post post) {
        String sql = "UPDATE posts SET content = ?, hashtags = ? WHERE post_id = ?";

        try (Connection conn = DBConnectionUtil.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {

            pstmt.setString(1, post.getContent());
            pstmt.setString(2, post.getHashtags());
            pstmt.setInt(3, post.getPostId());

            int rowsAffected = pstmt.executeUpdate();

            if (rowsAffected > 0) {
                return true;
            }

            return false;

        } catch (SQLException e) {
            throw new DatabaseException("Failed to update post: " + e.getMessage());
        }
    }

    @Override
    public boolean deletePost(int postId) {
        String sql = "DELETE FROM posts WHERE post_id = ?";

        try (Connection conn = DBConnectionUtil.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {

            pstmt.setInt(1, postId);
            int rowsAffected = pstmt.executeUpdate();

            if (rowsAffected > 0) {
                return true;
            }

            return false;

        } catch (SQLException e) {
            throw new DatabaseException("Failed to delete post: " + e.getMessage());
        }
    }

    @Override
    public Post findById(int postId) {
        String sql = "SELECT * FROM posts WHERE post_id = ?";

        try (Connection conn = DBConnectionUtil.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {

            pstmt.setInt(1, postId);
            ResultSet rs = pstmt.executeQuery();

            if (rs.next()) {
                return mapResultSetToPost(rs);
            }

            return null;

        } catch (SQLException e) {
            throw new DatabaseException("Failed to find post: " + e.getMessage());
        }
    }

    @Override
    public List<Post> findByUserId(int userId) {
        String sql =
                "SELECT * FROM posts WHERE user_id = ? ORDER BY created_at DESC";

        List<Post> posts = new ArrayList<>();

        try (Connection conn = DBConnectionUtil.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {

            pstmt.setInt(1, userId);
            ResultSet rs = pstmt.executeQuery();

            while (rs.next()) {
                posts.add(mapResultSetToPost(rs));
            }

            return posts;

        } catch (SQLException e) {
            throw new DatabaseException("Failed to find posts: " + e.getMessage());
        }
    }

    @Override
    public List<Post> getFeed(int userId) {
        String sql =
                "SELECT DISTINCT p.* FROM posts p " +
                "LEFT JOIN connection_requests cr1 ON p.user_id = cr1.sender_id " +
                "LEFT JOIN connection_requests cr2 ON p.user_id = cr2.receiver_id " +
                "LEFT JOIN follows f ON p.user_id = f.following_id " +
                "WHERE p.user_id = ? " +
                "OR (cr1.receiver_id = ? AND cr1.status = 'ACCEPTED') " +
                "OR (cr2.sender_id = ? AND cr2.status = 'ACCEPTED') " +
                "OR f.follower_id = ? " +
                "ORDER BY p.created_at DESC LIMIT 100";

        List<Post> posts = new ArrayList<>();

        try (Connection conn = DBConnectionUtil.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {

            pstmt.setInt(1, userId);
            pstmt.setInt(2, userId);
            pstmt.setInt(3, userId);
            pstmt.setInt(4, userId);

            ResultSet rs = pstmt.executeQuery();

            while (rs.next()) {
                posts.add(mapResultSetToPost(rs));
            }

            return posts;

        } catch (SQLException e) {
            throw new DatabaseException("Failed to get feed: " + e.getMessage());
        }
    }

    @Override
    public List<Post> getTrendingPosts() {
        String sql =
                "SELECT p.* FROM posts p " +
                "JOIN post_analytics pa ON p.post_id = pa.post_id " +
                "WHERE p.created_at >= DATE_SUB(NOW(), INTERVAL 7 DAY) " +
                "ORDER BY (pa.total_likes + pa.total_comments + pa.total_shares) DESC " +
                "LIMIT 20";

        List<Post> posts = new ArrayList<>();

        try (Connection conn = DBConnectionUtil.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {

            ResultSet rs = pstmt.executeQuery();

            while (rs.next()) {
                posts.add(mapResultSetToPost(rs));
            }

            return posts;

        } catch (SQLException e) {
            throw new DatabaseException("Failed to get trending posts: " + e.getMessage());
        }
    }

    @Override
    public List<Post> searchByHashtag(String hashtag) {
        String sql =
                "SELECT * FROM posts WHERE hashtags LIKE ? " +
                "ORDER BY created_at DESC LIMIT 50";

        List<Post> posts = new ArrayList<>();

        try (Connection conn = DBConnectionUtil.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {

            pstmt.setString(1, "%" + hashtag + "%");
            ResultSet rs = pstmt.executeQuery();

            while (rs.next()) {
                posts.add(mapResultSetToPost(rs));
            }

            return posts;

        } catch (SQLException e) {
            throw new DatabaseException("Failed to search by hashtag: " + e.getMessage());
        }
    }

    @Override
    public List<Post> getFilteredFeed(int userId, String postType, String userType) {
        StringBuilder sql = new StringBuilder(
            "SELECT DISTINCT p.* FROM posts p " +
            "JOIN users u ON p.user_id = u.user_id " +
            "LEFT JOIN connection_requests cr1 ON p.user_id = cr1.sender_id " +
            "LEFT JOIN connection_requests cr2 ON p.user_id = cr2.receiver_id " +
            "LEFT JOIN follows f ON p.user_id = f.following_id " +
            "WHERE (p.user_id = ? " +
            "OR (cr1.receiver_id = ? AND cr1.status = 'ACCEPTED') " +
            "OR (cr2.sender_id = ? AND cr2.status = 'ACCEPTED') " +
            "OR f.follower_id = ?) "
        );

        if (postType != null && !postType.isEmpty()) {
            if (postType.equalsIgnoreCase("PROMOTIONAL")) {
                sql.append("AND p.is_promotional = true ");
            } else if (postType.equalsIgnoreCase("REGULAR")) {
                sql.append("AND p.is_promotional = false ");
            }
        }

        if (userType != null && !userType.isEmpty()) {
            sql.append("AND u.user_type = ? ");
        }

        sql.append("ORDER BY p.created_at DESC LIMIT 100");

        List<Post> posts = new ArrayList<>();

        try (Connection conn = DBConnectionUtil.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql.toString())) {

            pstmt.setInt(1, userId);
            pstmt.setInt(2, userId);
            pstmt.setInt(3, userId);
            pstmt.setInt(4, userId);

            if (userType != null && !userType.isEmpty()) {
                pstmt.setString(5, userType);
            }

            ResultSet rs = pstmt.executeQuery();

            while (rs.next()) {
                posts.add(mapResultSetToPost(rs));
            }

            return posts;

        } catch (SQLException e) {
            throw new DatabaseException("Failed to get filtered feed: " + e.getMessage());
        }
    }

    @Override
    public boolean schedulePost(Post post) {
        boolean postCreated = createPost(post);

        if (!postCreated || post.getPostId() == 0) {
            return false;
        }

        String sql =
                "INSERT INTO scheduled_posts (post_id, scheduled_for, is_published) " +
                "VALUES (?, ?, false)";

        try (Connection conn = DBConnectionUtil.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {

            pstmt.setInt(1, post.getPostId());
            pstmt.setTimestamp(2,
                    Timestamp.valueOf(post.getScheduledTime()));

            int rowsAffected = pstmt.executeUpdate();

            if (rowsAffected > 0) {
                return true;
            }

            return false;

        } catch (SQLException e) {
            throw new DatabaseException("Failed to schedule post: " + e.getMessage());
        }
    }

    @Override
    public boolean pinPost(int postId, int userId) {
        Connection conn = null;
        try {
            conn = DBConnectionUtil.getConnection();
            conn.setAutoCommit(false);

            String getPinOrderSql =
                    "SELECT COALESCE(MAX(pin_order), 0) + 1 as next_order " +
                    "FROM posts WHERE user_id = ? AND is_pinned = true";

            int nextPinOrder = 1;

            try (PreparedStatement pstmt =
                         conn.prepareStatement(getPinOrderSql)) {

                pstmt.setInt(1, userId);
                ResultSet rs = pstmt.executeQuery();
                if (rs.next()) {
                    nextPinOrder = rs.getInt("next_order");
                }
            }

            String updateSql =
                    "UPDATE posts SET is_pinned = true, pin_order = ? " +
                    "WHERE post_id = ? AND user_id = ?";

            try (PreparedStatement pstmt =
                         conn.prepareStatement(updateSql)) {

                pstmt.setInt(1, nextPinOrder);
                pstmt.setInt(2, postId);
                pstmt.setInt(3, userId);

                int rowsAffected = pstmt.executeUpdate();

                if (rowsAffected > 0) {
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
            throw new DatabaseException("Failed to pin post: " + e.getMessage());
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
    public boolean unpinPost(int postId, int userId) {
        String sql =
                "UPDATE posts SET is_pinned = false, pin_order = 0 " +
                "WHERE post_id = ? AND user_id = ?";

        try (Connection conn = DBConnectionUtil.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {

            pstmt.setInt(1, postId);
            pstmt.setInt(2, userId);

            int rowsAffected = pstmt.executeUpdate();

            if (rowsAffected > 0) {
                return true;
            }

            return false;

        } catch (SQLException e) {
            throw new DatabaseException("Failed to unpin post: " + e.getMessage());
        }
    }

    @Override
    public PostAnalytics getAnalytics(int postId) {
        String sql =
                "SELECT * FROM post_analytics WHERE post_id = ?";

        try (Connection conn = DBConnectionUtil.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {

            pstmt.setInt(1, postId);
            ResultSet rs = pstmt.executeQuery();

            if (rs.next()) {
                PostAnalytics analytics = new PostAnalytics();
                analytics.setAnalyticsId(rs.getInt("analytics_id"));
                analytics.setPostId(rs.getInt("post_id"));
                analytics.setTotalLikes(rs.getInt("total_likes"));
                analytics.setTotalComments(rs.getInt("total_comments"));
                analytics.setTotalShares(rs.getInt("total_shares"));
                analytics.setUniqueViewers(rs.getInt("unique_viewers"));
                analytics.setLastUpdated(
                        rs.getTimestamp("last_updated").toLocalDateTime());
                return analytics;
            }

            return null;

        } catch (SQLException e) {
            throw new DatabaseException("Failed to get analytics: " + e.getMessage());
        }
    }

    private Post mapResultSetToPost(ResultSet rs) throws SQLException {
        Post post = new Post();
        post.setPostId(rs.getInt("post_id"));
        post.setUserId(rs.getInt("user_id"));
        post.setContent(rs.getString("content"));
        post.setHashtags(rs.getString("hashtags"));
        post.setPromotional(rs.getBoolean("is_promotional"));
        post.setPinned(rs.getBoolean("is_pinned"));
        post.setPinOrder(rs.getInt("pin_order"));
        post.setCreatedAt(rs.getTimestamp("created_at").toLocalDateTime());

        try {
            Timestamp updatedAt = rs.getTimestamp("updated_at");
            if (updatedAt != null) {
                post.setUpdatedAt(updatedAt.toLocalDateTime());
            }
        } catch (SQLException e) {
            // ignore
        }

        return post;
    }
}
