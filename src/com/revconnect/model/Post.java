// File: src/main/java/com/revconnect/model/Post.java

package com.revconnect.model;

import java.time.LocalDateTime;

public class Post {
    
    private int postId;
    private int userId;
    private String content;
    private String hashtags;
    private boolean isPromotional;
    private LocalDateTime scheduledTime;
    private boolean isPinned;
    private int pinOrder;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
    
    public Post() {
    }
    
    public Post(int postId, int userId, String content, String hashtags, 
                boolean isPromotional, LocalDateTime scheduledTime, boolean isPinned, 
                int pinOrder, LocalDateTime createdAt, LocalDateTime updatedAt) {
        this.postId = postId;
        this.userId = userId;
        this.content = content;
        this.hashtags = hashtags;
        this.isPromotional = isPromotional;
        this.scheduledTime = scheduledTime;
        this.isPinned = isPinned;
        this.pinOrder = pinOrder;
        this.createdAt = createdAt;
        this.updatedAt = updatedAt;
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
    
    public String getContent() {
        return content;
    }
    
    public void setContent(String content) {
        this.content = content;
    }
    
    public String getHashtags() {
        return hashtags;
    }
    
    public void setHashtags(String hashtags) {
        this.hashtags = hashtags;
    }
    
    public boolean isPromotional() {
        return isPromotional;
    }
    
    public void setPromotional(boolean promotional) {
        isPromotional = promotional;
    }
    
    public LocalDateTime getScheduledTime() {
        return scheduledTime;
    }
    
    public void setScheduledTime(LocalDateTime scheduledTime) {
        this.scheduledTime = scheduledTime;
    }
    
    public boolean isPinned() {
        return isPinned;
    }
    
    public void setPinned(boolean pinned) {
        isPinned = pinned;
    }
    
    public int getPinOrder() {
        return pinOrder;
    }
    
    public void setPinOrder(int pinOrder) {
        this.pinOrder = pinOrder;
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
        return "Post{" +
                "postId=" + postId +
                ", userId=" + userId +
                ", content='" + content + '\'' +
                ", hashtags='" + hashtags + '\'' +
                ", isPromotional=" + isPromotional +
                ", isPinned=" + isPinned +
                ", createdAt=" + createdAt +
                '}';
    }
}