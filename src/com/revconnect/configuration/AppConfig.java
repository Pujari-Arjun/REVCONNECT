package com.revconnect.configuration;

public class AppConfig {
    
    public static final String APP_NAME = "RevConnect";
    public static final String APP_VERSION = "1.0.0";
    public static final int SESSION_TIMEOUT_MINUTES = 30;
    public static final int MAX_POST_LENGTH = 5000;
    public static final int MAX_COMMENT_LENGTH = 1000;
    public static final int MAX_MESSAGE_LENGTH = 2000;
    public static final int MAX_BIO_LENGTH = 500;
    public static final int MAX_HASHTAGS_LENGTH = 500;
    public static final int MIN_PASSWORD_LENGTH = 8;
    public static final int MIN_USERNAME_LENGTH = 3;
    public static final int MAX_USERNAME_LENGTH = 20;
    
    private AppConfig() {
    }
}