// File: src/main/java/com/revconnect/model/UserSecurityAnswer.java

package com.revconnect.model;

import java.time.LocalDateTime;

public class UserSecurityAnswer {
    
    private int answerId;
    private int userId;
    private int questionId;
    private String answerHash;
    private LocalDateTime createdAt;
    
    public UserSecurityAnswer() {
    }
    
    public UserSecurityAnswer(int answerId, int userId, int questionId, 
                             String answerHash, LocalDateTime createdAt) {
        this.answerId = answerId;
        this.userId = userId;
        this.questionId = questionId;
        this.answerHash = answerHash;
        this.createdAt = createdAt;
    }
    
    public int getAnswerId() {
        return answerId;
    }
    
    public void setAnswerId(int answerId) {
        this.answerId = answerId;
    }
    
    public int getUserId() {
        return userId;
    }
    
    public void setUserId(int userId) {
        this.userId = userId;
    }
    
    public int getQuestionId() {
        return questionId;
    }
    
    public void setQuestionId(int questionId) {
        this.questionId = questionId;
    }
    
    public String getAnswerHash() {
        return answerHash;
    }
    
    public void setAnswerHash(String answerHash) {
        this.answerHash = answerHash;
    }
    
    public LocalDateTime getCreatedAt() {
        return createdAt;
    }
    
    public void setCreatedAt(LocalDateTime createdAt) {
        this.createdAt = createdAt;
    }
    
    @Override
    public String toString() {
        return "UserSecurityAnswer{" +
                "answerId=" + answerId +
                ", userId=" + userId +
                ", questionId=" + questionId +
                ", createdAt=" + createdAt +
                '}';
    }
}