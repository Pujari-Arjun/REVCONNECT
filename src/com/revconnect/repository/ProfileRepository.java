package com.revconnect.repository;

import com.revconnect.dao.ProfileDAO;
import com.revconnect.model.Profile;
import com.revconnect.util.DBConnectionUtil;
import com.revconnect.exception.DatabaseException;

import java.sql.*;
import java.time.LocalDateTime;

public class ProfileRepository implements ProfileDAO {

    @Override
    public boolean createProfile(Profile profile) {
        String sql = "INSERT INTO profiles (user_id, name, bio, profile_pic_path, location, " +
                     "website, category, business_address, contact_info, business_hours, created_at) " +
                     "VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?)";

        try (Connection conn = DBConnectionUtil.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {

            pstmt.setInt(1, profile.getUserId());
            pstmt.setString(2, profile.getName());
            pstmt.setString(3, profile.getBio());
            pstmt.setString(4, profile.getProfilePicPath());
            pstmt.setString(5, profile.getLocation());
            pstmt.setString(6, profile.getWebsite());
            pstmt.setString(7, profile.getCategory());
            pstmt.setString(8, profile.getBusinessAddress());
            pstmt.setString(9, profile.getContactInfo());
            pstmt.setString(10, profile.getBusinessHours());
            pstmt.setTimestamp(11, Timestamp.valueOf(LocalDateTime.now()));

            int rowsAffected = pstmt.executeUpdate();

            if (rowsAffected > 0) {
                ResultSet rs = pstmt.getGeneratedKeys();
                if (rs.next()) {
                    profile.setProfileId(rs.getInt(1));
                    return true;
                }
            }

            return false;

        } catch (SQLException e) {
            throw new DatabaseException("Failed to create profile: " + e.getMessage());
        }
    }

    @Override
    public Profile findByUserId(int userId) {
        String sql = "SELECT * FROM profiles WHERE user_id = ?";

        try (Connection conn = DBConnectionUtil.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {

            pstmt.setInt(1, userId);
            ResultSet rs = pstmt.executeQuery();

            if (rs.next()) {
                return mapResultSetToProfile(rs);
            }

            return null;

        } catch (SQLException e) {
            throw new DatabaseException("Failed to find profile: " + e.getMessage());
        }
    }

    @Override
    public boolean updateProfile(Profile profile) {
        String sql = "UPDATE profiles SET name = ?, bio = ?, profile_pic_path = ?, " +
                     "location = ?, website = ?, category = ?, business_address = ?, " +
                     "contact_info = ?, business_hours = ? " +
                     "WHERE profile_id = ?";

        try (Connection conn = DBConnectionUtil.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {

            pstmt.setString(1, profile.getName());
            pstmt.setString(2, profile.getBio());
            pstmt.setString(3, profile.getProfilePicPath());
            pstmt.setString(4, profile.getLocation());
            pstmt.setString(5, profile.getWebsite());
            pstmt.setString(6, profile.getCategory());
            pstmt.setString(7, profile.getBusinessAddress());
            pstmt.setString(8, profile.getContactInfo());
            pstmt.setString(9, profile.getBusinessHours());
            pstmt.setInt(10, profile.getProfileId());

            int rowsAffected = pstmt.executeUpdate();

            if (rowsAffected > 0) {
                return true;
            }

            return false;

        } catch (SQLException e) {
            throw new DatabaseException("Failed to update profile: " + e.getMessage());
        }
    }

    private Profile mapResultSetToProfile(ResultSet rs) throws SQLException {
        Profile profile = new Profile();
        profile.setProfileId(rs.getInt("profile_id"));
        profile.setUserId(rs.getInt("user_id"));
        profile.setName(rs.getString("name"));
        profile.setBio(rs.getString("bio"));
        profile.setProfilePicPath(rs.getString("profile_pic_path"));
        profile.setLocation(rs.getString("location"));
        profile.setWebsite(rs.getString("website"));
        profile.setCategory(rs.getString("category"));
        profile.setBusinessAddress(rs.getString("business_address"));
        profile.setContactInfo(rs.getString("contact_info"));
        profile.setBusinessHours(rs.getString("business_hours"));
        profile.setCreatedAt(rs.getTimestamp("created_at").toLocalDateTime());

        // Handle updated_at column if it exists
        try {
            Timestamp updatedAt = rs.getTimestamp("updated_at");
            if (updatedAt != null) {
                profile.setUpdatedAt(updatedAt.toLocalDateTime());
            }
        } catch (SQLException e) {
            // Column doesn't exist in schema, ignore
        }

        return profile;
    }
}
