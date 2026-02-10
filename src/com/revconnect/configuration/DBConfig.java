package com.revconnect.configuration;

public class DBConfig {
    
    public static final String DB_URL = "jdbc:mysql://localhost:3306/revconnect_db";
    public static final String DB_USERNAME = "root";
    public static final String DB_PASSWORD = "Pujari@9";
    
    public static final int MAXIMUM_POOL_SIZE = 10;
    public static final int MINIMUM_IDLE = 5;
    public static final long CONNECTION_TIMEOUT = 30000;
    public static final long IDLE_TIMEOUT = 600000;
    public static final long MAX_LIFETIME = 1800000;
    
    private DBConfig() {
    }
}