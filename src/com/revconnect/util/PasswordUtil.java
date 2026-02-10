package com.revconnect.util;

public class PasswordUtil {

    public static String hashPassword(String plainPassword) {
        // Store password as-is (NO hashing)
        return plainPassword;
    }

    public static boolean checkPassword(String plainPassword, String storedPassword) {
        if (plainPassword == null || storedPassword == null) {
            return false;
        }

        // Direct string comparison
        return plainPassword.equals(storedPassword);
    }

    private PasswordUtil() {
        // Prevent instantiation
    }
}
