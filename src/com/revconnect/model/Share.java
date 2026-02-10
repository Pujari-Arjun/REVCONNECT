// File: src/main/java/com/revconnect/model/Share.java

package com.revconnect.model;

import java.time.LocalDateTime;

public class Share {
    
    private int shareId;
    private int postId;
    private int userId;
    private LocalDateTime createdAt;
    
    public Share() {
    }
    
    public Share(int shareId, int postId, int userId, LocalDateTime createdAt) {
        this.shareId = shareId;
        this.postId = postId;
        this.userId = userId;
        this.createdAt = createdAt;
    }
    
    public int getShareId() {
        return shareId;
    }
    
    public void setShareId(int shareId) {
        this.shareId = shareId;
    }
    
    public int getPostId() {
        return postId;
    }
    
    public void setPostId(int postId) {
        this.postId = postId;
    }
    
    public int getUserId() {
        return userId;
    }
    
    public void setUserId(int userId) {
        this.userId = userId;
    }
    
    public LocalDateTime getCreatedAt() {
        return createdAt;
    }
    
    public void setCreatedAt(LocalDateTime createdAt) {
        this.createdAt = createdAt;
    }
    
    @Override
    public String toString() {
        return "Share{" +
                "shareId=" + shareId +
                ", postId=" + postId +
                ", userId=" + userId +
                ", createdAt=" + createdAt +
                '}';
    }
}