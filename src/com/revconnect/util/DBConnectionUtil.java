package com.revconnect.util;

import com.revconnect.config.DBConfig;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

public class DBConnectionUtil {

    static {
        try {
            // Load MySQL JDBC Driver
            Class.forName("com.mysql.cj.jdbc.Driver");
        } catch (ClassNotFoundException e) {
            throw new RuntimeException("Failed to load MySQL JDBC Driver", e);
        }
    }

    public static Connection getConnection() throws SQLException {
        return DriverManager.getConnection(
                DBConfig.DB_URL,
                DBConfig.DB_USERNAME,
                DBConfig.DB_PASSWORD
        );
    }

    public static void closeDataSource() {
        // Not needed for plain JDBC (kept for compatibility)
    }

    private DBConnectionUtil() {
        // Prevent instantiation
    }
}
