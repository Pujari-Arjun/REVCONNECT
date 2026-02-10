// File: src/main/java/com/revconnect/model/Follow.java

package com.revconnect.model;

import java.time.LocalDateTime;

public class Follow {
    
    private int followId;
    private int followerId;
    private int followingId;
    private LocalDateTime createdAt;
    
    public Follow() {
    }
    
    public Follow(int followId, int followerId, int followingId, LocalDateTime createdAt) {
        this.followId = followId;
        this.followerId = followerId;
        this.followingId = followingId;
        this.createdAt = createdAt;
    }
    
    public int getFollowId() {
        return followId;
    }
    
    public void setFollowId(int followId) {
        this.followId = followId;
    }
    
    public int getFollowerId() {
        return followerId;
    }
    
    public void setFollowerId(int followerId) {
        this.followerId = followerId;
    }
    
    public int getFollowingId() {
        return followingId;
    }
    
    public void setFollowingId(int followingId) {
        this.followingId = followingId;
    }
    
    public LocalDateTime getCreatedAt() {
        return createdAt;
    }
    
    public void setCreatedAt(LocalDateTime createdAt) {
        this.createdAt = createdAt;
    }
    
    @Override
    public String toString() {
        return "Follow{" +
                "followId=" + followId +
                ", followerId=" + followerId +
                ", followingId=" + followingId +
                ", createdAt=" + createdAt +
                '}';
    }
}