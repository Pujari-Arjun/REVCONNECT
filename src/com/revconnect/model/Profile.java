// File: src/main/java/com/revconnect/model/Profile.java

package com.revconnect.model;

import java.time.LocalDateTime;

public class Profile {
    
    private int profileId;
    private int userId;
    private String name;
    private String bio;
    private String profilePicPath;
    private String location;
    private String website;
    private String category;
    private String businessAddress;
    private String contactInfo;
    private String businessHours;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
    
    public Profile() {
    }
    
    public Profile(int profileId, int userId, String name, String bio, 
                   String profilePicPath, String location, String website, 
                   String category, String businessAddress, String contactInfo, 
                   String businessHours, LocalDateTime createdAt, LocalDateTime updatedAt) {
        this.profileId = profileId;
        this.userId = userId;
        this.name = name;
        this.bio = bio;
        this.profilePicPath = profilePicPath;
        this.location = location;
        this.website = website;
        this.category = category;
        this.businessAddress = businessAddress;
        this.contactInfo = contactInfo;
        this.businessHours = businessHours;
        this.createdAt = createdAt;
        this.updatedAt = updatedAt;
    }
    
    public int getProfileId() {
        return profileId;
    }
    
    public void setProfileId(int profileId) {
        this.profileId = profileId;
    }
    
    public int getUserId() {
        return userId;
    }
    
    public void setUserId(int userId) {
        this.userId = userId;
    }
    
    public String getName() {
        return name;
    }
    
    public void setName(String name) {
        this.name = name;
    }
    
    public String getBio() {
        return bio;
    }
    
    public void setBio(String bio) {
        this.bio = bio;
    }
    
    public String getProfilePicPath() {
        return profilePicPath;
    }
    
    public void setProfilePicPath(String profilePicPath) {
        this.profilePicPath = profilePicPath;
    }
    
    public String getLocation() {
        return location;
    }
    
    public void setLocation(String location) {
        this.location = location;
    }
    
    public String getWebsite() {
        return website;
    }
    
    public void setWebsite(String website) {
        this.website = website;
    }
    
    public String getCategory() {
        return category;
    }
    
    public void setCategory(String category) {
        this.category = category;
    }
    
    public String getBusinessAddress() {
        return businessAddress;
    }
    
    public void setBusinessAddress(String businessAddress) {
        this.businessAddress = businessAddress;
    }
    
    public String getContactInfo() {
        return contactInfo;
    }
    
    public void setContactInfo(String contactInfo) {
        this.contactInfo = contactInfo;
    }
    
    public String getBusinessHours() {
        return businessHours;
    }
    
    public void setBusinessHours(String businessHours) {
        this.businessHours = businessHours;
    }
    
    public LocalDateTime getCreatedAt() {
        return createdAt;
    }
    
    public void setCreatedAt(LocalDateTime createdAt) {
        this.createdAt = createdAt;
    }
    
    public LocalDateTime getUpdatedAt() {
        return updatedAt;
    }
    
    public void setUpdatedAt(LocalDateTime updatedAt) {
        this.updatedAt = updatedAt;
    }
    
    @Override
    public String toString() {
        return "Profile{" +
                "profileId=" + profileId +
                ", userId=" + userId +
                ", name='" + name + '\'' +
                ", location='" + location + '\'' +
                ", category='" + category + '\'' +
                ", createdAt=" + createdAt +
                '}';
    }
}