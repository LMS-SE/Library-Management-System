package edu.software.lms;

import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.Base64;

public final class CustomUtilities {

    private CustomUtilities() {}

    public static boolean isStrongPassword(String pwd) {
        boolean hasDigit = pwd.matches(".*\\d.*");
        boolean hasLower = pwd.matches(".*[a-z].*");
        boolean hasUpper = pwd.matches(".*[A-Z].*");
        boolean hasSymbol = pwd.matches(".*[^a-zA-Z0-9].*");
        return hasDigit && hasLower && hasUpper && hasSymbol;
    }

    public static String hashPassword(String password, String username) {
        try {
            String saltedPassword = password + username;
            MessageDigest md = MessageDigest.getInstance("SHA-256");
            byte[] hashedBytes = md.digest(saltedPassword.getBytes(StandardCharsets.UTF_8));
            return Base64.getEncoder().encodeToString(hashedBytes);
        } catch (NoSuchAlgorithmException e) {
            throw new RuntimeException("SHA-256 algorithm not found!", e);
        }
    }
}
