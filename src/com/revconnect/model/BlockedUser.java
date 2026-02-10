package com.revconnect.model;

import java.time.LocalDateTime;

public class BlockedUser {
    
    private int blockId;
    private int blockerId;
    private int blockedId;
    private LocalDateTime createdAt;
    
    public BlockedUser() {
    }
    
    public BlockedUser(int blockId, int blockerId, int blockedId, LocalDateTime createdAt) {
        this.blockId = blockId;
        this.blockerId = blockerId;
        this.blockedId = blockedId;
        this.createdAt = createdAt;
    }
    
    public int getBlockId() {
        return blockId;
    }
    
    public void setBlockId(int blockId) {
        this.blockId = blockId;
    }
    
    public int getBlockerId() {
        return blockerId;
    }
    
    public void setBlockerId(int blockerId) {
        this.blockerId = blockerId;
    }
    
    public int getBlockedId() {
        return blockedId;
    }
    
    public void setBlockedId(int blockedId) {
        this.blockedId = blockedId;
    }
    
    public LocalDateTime getCreatedAt() {
        return createdAt;
    }
    
    public void setCreatedAt(LocalDateTime createdAt) {
        this.createdAt = createdAt;
    }
    
    @Override
    public String toString() {
        return "BlockedUser{" +
                "blockId=" + blockId +
                ", blockerId=" + blockerId +
                ", blockedId=" + blockedId +
                ", createdAt=" + createdAt +
                '}';
    }
}