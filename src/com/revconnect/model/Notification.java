package com.revconnect.model;

import java.time.LocalDateTime;

public class Notification {
    
    private int notificationId;
    private int userId;
    private String type;  // Changed from notificationType
    private String message;
    private boolean isRead;
    private int relatedUserId;
    private int relatedPostId;
    private LocalDateTime createdAt;
    
    public Notification() {}
    
    public int getNotificationId() {
        return notificationId;
    }
    
    public void setNotificationId(int notificationId) {
        this.notificationId = notificationId;
    }
    
    public int getUserId() {
        return userId;
    }
    
    public void setUserId(int userId) {
        this.userId = userId;
    }
    
    public String getType() {
        return type;
    }
    
    public void setType(String type) {
        this.type = type;
    }
    
    public String getMessage() {
        return message;
    }
    
    public void setMessage(String message) {
        this.message = message;
    }
    
    public boolean isRead() {
        return isRead;
    }
    
    public void setRead(boolean read) {
        isRead = read;
    }
    
    public int getRelatedUserId() {
        return relatedUserId;
    }
    
    public void setRelatedUserId(int relatedUserId) {
        this.relatedUserId = relatedUserId;
    }
    
    public int getRelatedPostId() {
        return relatedPostId;
    }
    
    public void setRelatedPostId(int relatedPostId) {
        this.relatedPostId = relatedPostId;
    }
    
    public LocalDateTime getCreatedAt() {
        return createdAt;
    }
    
    public void setCreatedAt(LocalDateTime createdAt) {
        this.createdAt = createdAt;
    }
    
    @Override
    public String toString() {
        return "Notification{" +
                "notificationId=" + notificationId +
                ", userId=" + userId +
                ", type='" + type + '\'' +
                ", message='" + message + '\'' +
                ", isRead=" + isRead +
                ", relatedUserId=" + relatedUserId +
                ", relatedPostId=" + relatedPostId +
                ", createdAt=" + createdAt +
                '}';
    }
}