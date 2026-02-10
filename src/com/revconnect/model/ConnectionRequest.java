// File: src/main/java/com/revconnect/model/ConnectionRequest.java

package com.revconnect.model;

import java.time.LocalDateTime;

public class ConnectionRequest {
    
    private int requestId;
    private int senderId;
    private int receiverId;
    private String status;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
    
    public ConnectionRequest() {
    }
    
    public ConnectionRequest(int requestId, int senderId, int receiverId, 
                            String status, LocalDateTime createdAt, LocalDateTime updatedAt) {
        this.requestId = requestId;
        this.senderId = senderId;
        this.receiverId = receiverId;
        this.status = status;
        this.createdAt = createdAt;
        this.updatedAt = updatedAt;
    }
    
    public int getRequestId() {
        return requestId;
    }
    
    public void setRequestId(int requestId) {
        this.requestId = requestId;
    }
    
    public int getSenderId() {
        return senderId;
    }
    
    public void setSenderId(int senderId) {
        this.senderId = senderId;
    }
    
    public int getReceiverId() {
        return receiverId;
    }
    
    public void setReceiverId(int receiverId) {
        this.receiverId = receiverId;
    }
    
    public String getStatus() {
        return status;
    }
    
    public void setStatus(String status) {
        this.status = status;
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
        return "ConnectionRequest{" +
                "requestId=" + requestId +
                ", senderId=" + senderId +
                ", receiverId=" + receiverId +
                ", status='" + status + '\'' +
                ", createdAt=" + createdAt +
                ", updatedAt=" + updatedAt +
                '}';
    }
}