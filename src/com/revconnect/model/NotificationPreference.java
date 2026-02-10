// File: src/main/java/com/revconnect/model/NotificationPreference.java

package com.revconnect.model;

import java.time.LocalDateTime;

public class NotificationPreference {
    
    private int preferenceId;
    private int userId;
    private boolean connectionRequests;
    private boolean newFollowers;
    private boolean likes;
    private boolean comments;
    private boolean shares;
    private boolean newPosts;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
    
    public NotificationPreference() {
    }
    
    public NotificationPreference(int preferenceId, int userId, boolean connectionRequests, 
                                 boolean newFollowers, boolean likes, boolean comments, 
                                 boolean shares, boolean newPosts, LocalDateTime createdAt, 
                                 LocalDateTime updatedAt) {
        this.preferenceId = preferenceId;
        this.userId = userId;
        this.connectionRequests = connectionRequests;
        this.newFollowers = newFollowers;
        this.likes = likes;
        this.comments = comments;
        this.shares = shares;
        this.newPosts = newPosts;
        this.createdAt = createdAt;
        this.updatedAt = updatedAt;
    }
    
    public int getPreferenceId() {
        return preferenceId;
    }
    
    public void setPreferenceId(int preferenceId) {
        this.preferenceId = preferenceId;
    }
    
    public int getUserId() {
        return userId;
    }
    
    public void setUserId(int userId) {
        this.userId = userId;
    }
    
    public boolean isConnectionRequests() {
        return connectionRequests;
    }
    
    public void setConnectionRequests(boolean connectionRequests) {
        this.connectionRequests = connectionRequests;
    }
    
    public boolean isNewFollowers() {
        return newFollowers;
    }
    
    public void setNewFollowers(boolean newFollowers) {
        this.newFollowers = newFollowers;
    }
    
    public boolean isLikes() {
        return likes;
    }
    
    public void setLikes(boolean likes) {
        this.likes = likes;
    }
    
    public boolean isComments() {
        return comments;
    }
    
    public void setComments(boolean comments) {
        this.comments = comments;
    }
    
    public boolean isShares() {
        return shares;
    }
    
    public void setShares(boolean shares) {
        this.shares = shares;
    }
    
    public boolean isNewPosts() {
        return newPosts;
    }
    
    public void setNewPosts(boolean newPosts) {
        this.newPosts = newPosts;
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
        return "NotificationPreference{" +
                "preferenceId=" + preferenceId +
                ", userId=" + userId +
                ", connectionRequests=" + connectionRequests +
                ", newFollowers=" + newFollowers +
                ", likes=" + likes +
                ", comments=" + comments +
                ", shares=" + shares +
                ", newPosts=" + newPosts +
                '}';
    }
}