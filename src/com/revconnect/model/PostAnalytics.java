// File: src/main/java/com/revconnect/model/PostAnalytics.java

package com.revconnect.model;

import java.time.LocalDateTime;

public class PostAnalytics {
    
    private int analyticsId;
    private int postId;
    private int totalLikes;
    private int totalComments;
    private int totalShares;
    private int uniqueViewers;
    private LocalDateTime lastUpdated;
    
    public PostAnalytics() {
    }
    
    public PostAnalytics(int analyticsId, int postId, int totalLikes, int totalComments, 
                        int totalShares, int uniqueViewers, LocalDateTime lastUpdated) {
        this.analyticsId = analyticsId;
        this.postId = postId;
        this.totalLikes = totalLikes;
        this.totalComments = totalComments;
        this.totalShares = totalShares;
        this.uniqueViewers = uniqueViewers;
        this.lastUpdated = lastUpdated;
    }
    
    public int getAnalyticsId() {
        return analyticsId;
    }
    
    public void setAnalyticsId(int analyticsId) {
        this.analyticsId = analyticsId;
    }
    
    public int getPostId() {
        return postId;
    }
    
    public void setPostId(int postId) {
        this.postId = postId;
    }
    
    public int getTotalLikes() {
        return totalLikes;
    }
    
    public void setTotalLikes(int totalLikes) {
        this.totalLikes = totalLikes;
    }
    
    public int getTotalComments() {
        return totalComments;
    }
    
    public void setTotalComments(int totalComments) {
        this.totalComments = totalComments;
    }
    
    public int getTotalShares() {
        return totalShares;
    }
    
    public void setTotalShares(int totalShares) {
        this.totalShares = totalShares;
    }
    
    public int getUniqueViewers() {
        return uniqueViewers;
    }
    
    public void setUniqueViewers(int uniqueViewers) {
        this.uniqueViewers = uniqueViewers;
    }
    
    public LocalDateTime getLastUpdated() {
        return lastUpdated;
    }
    
    public void setLastUpdated(LocalDateTime lastUpdated) {
        this.lastUpdated = lastUpdated;
    }
    
    @Override
    public String toString() {
        return "PostAnalytics{" +
                "analyticsId=" + analyticsId +
                ", postId=" + postId +
                ", totalLikes=" + totalLikes +
                ", totalComments=" + totalComments +
                ", totalShares=" + totalShares +
                ", uniqueViewers=" + uniqueViewers +
                ", lastUpdated=" + lastUpdated +
                '}';
    }
}