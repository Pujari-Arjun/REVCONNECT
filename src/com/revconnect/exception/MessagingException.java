package com.revconnect.exception;

public class MessagingException extends RuntimeException {
    
    private static final long serialVersionUID = 1L;
    
    public MessagingException(String message) {
        super(message);
    }
    
    public MessagingException(String message, Throwable cause) {
        super(message, cause);
    }
}