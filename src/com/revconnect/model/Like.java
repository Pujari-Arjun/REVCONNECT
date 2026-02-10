// File: src/main/java/com/revconnect/model/Like.java

package com.revconnect.model;

import java.time.LocalDateTime;

public class Like {
    
    private int likeId;
    private int postId;
    private int userId;
    private LocalDateTime createdAt;
    
    public Like() {
    }
    
    public Like(int likeId, int postId, int userId, LocalDateTime createdAt) {
        this.likeId = likeId;
        this.postId = postId;
        this.userId = userId;
        this.createdAt = createdAt;
    }
    
    public int getLikeId() {
        return likeId;
    }
    
    public void setLikeId(int likeId) {
        this.likeId = likeId;
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
        return "Like{" +
                "likeId=" + likeId +
                ", postId=" + postId +
                ", userId=" + userId +
                ", createdAt=" + createdAt +
                '}';
    }
}