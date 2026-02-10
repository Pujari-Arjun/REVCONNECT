package com.revconnect.util;

public class ValidationUtil {

    public static boolean isValidEmail(String email) {
        if (email == null) return false;
        email = email.trim();
        String emailRegex = "^[A-Za-z0-9._-]+@[A-Za-z0-9-]+\\.[A-Za-z]{2,}$";
        return email.matches(emailRegex);
    }

    public static boolean isValidUsername(String username) {
        if (username == null) return false;
        username = username.trim();
        return username.matches("^[A-Za-z0-9_]{3,20}$");
    }

    public static boolean isValidPassword(String password) {
        if (password == null) return false;
        password = password.trim();
        return password.matches("^(?=.*[A-Za-z])(?=.*\\d)[A-Za-z\\d@$!%*?&]{8,}$");
    }

    public static String sanitizeInput(String input) {
        if (input == null) return null;
        return input.replaceAll("<", "&lt;")
                    .replaceAll(">", "&gt;")
                    .replaceAll("\"", "&quot;")
                    .replaceAll("'", "&#x27;");
    }

    public static boolean containsSQLInjection(String input) {
        if (input == null) return false;
        String lowerInput = input.toLowerCase();
        return lowerInput.contains("select ") ||
               lowerInput.contains("insert ") ||
               lowerInput.contains("update ") ||
               lowerInput.contains("delete ") ||
               lowerInput.contains("drop ") ||
               lowerInput.contains("--") ||
               lowerInput.contains(";") ||
               lowerInput.contains("' or '1'='1");
    }

    public static boolean isValidPostContent(String content) {
        return content != null && !content.trim().isEmpty();
    }

    public static boolean isValidComment(String comment) {
        return comment != null && !comment.trim().isEmpty();
    }

    public static boolean isValidMessage(String message) {
        return message != null && !message.trim().isEmpty();
    }

    public static boolean isValidBio(String bio) {
        return bio == null || bio.length() <= 250;
    }

    private ValidationUtil() {
    }
}
