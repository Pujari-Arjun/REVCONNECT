// File: src/main/java/com/revconnect/model/SecurityQuestion.java

package com.revconnect.model;

import java.time.LocalDateTime;

public class SecurityQuestion {
    
    private int questionId;
    private String questionText;
    private LocalDateTime createdAt;
    
    public SecurityQuestion() {
    }
    
    public SecurityQuestion(int questionId, String questionText, LocalDateTime createdAt) {
        this.questionId = questionId;
        this.questionText = questionText;
        this.createdAt = createdAt;
    }
    
    public int getQuestionId() {
        return questionId;
    }
    
    public void setQuestionId(int questionId) {
        this.questionId = questionId;
    }
    
    public String getQuestionText() {
        return questionText;
    }
    
    public void setQuestionText(String questionText) {
        this.questionText = questionText;
    }
    
    public LocalDateTime getCreatedAt() {
        return createdAt;
    }
    
    public void setCreatedAt(LocalDateTime createdAt) {
        this.createdAt = createdAt;
    }
    
    @Override
    public String toString() {
        return "SecurityQuestion{" +
                "questionId=" + questionId +
                ", questionText='" + questionText + '\'' +
                ", createdAt=" + createdAt +
                '}';
    }
}