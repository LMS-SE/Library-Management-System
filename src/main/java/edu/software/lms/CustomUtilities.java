package edu.software.lms;

import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.Base64;
import java.util.logging.Logger;

/**
 * A collection of static utility methods used across the library system.
 * <p>
 * The class provides:
 * <ul>
 *     <li>Password strength validation</li>
 *     <li>Password hashing using SHA-256 with username-based salting</li>
 * </ul>
 *
 * <p>The class is non-instantiable and only exposes static methods.</p>
 */
public class CustomUtilities {

    /**
     * Private constructor to prevent instantiation.
     */
    private CustomUtilities() {}

    /**
     * Determines whether a given password is considered strong.
     * <p>
     * A strong password must contain at least:
     * <ul>
     *     <li>One digit</li>
     *     <li>One lowercase letter</li>
     *     <li>One uppercase letter</li>
     *     <li>One symbol (non-alphanumeric character)</li>
     * </ul>
     *
     * @param pwd the password to evaluate
     * @return true if the password meets strength requirements, false otherwise
     */
    public static boolean isStrongPassword(String pwd) {
        boolean hasDigit = pwd.matches(".*\\d.*");
        boolean hasLower = pwd.matches(".*[a-z].*");
        boolean hasUpper = pwd.matches(".*[A-Z].*");
        boolean hasSymbol = pwd.matches(".*[^a-zA-Z0-9].*");
        return hasDigit && hasLower && hasUpper && hasSymbol;
    }

    /**
     * Generates a salted SHA-256 hash of a password.
     * The username is used as part of the salt.
     *
     * <p>Hashing steps:
     * <ol>
     *     <li>Concatenate password + username</li>
     *     <li>Apply SHA-256 hashing</li>
     *     <li>Encode result using Base64</li>
     * </ol>
     *
     * @param password the raw password
     * @param username username used as salt
     * @return Base64-encoded hashed password, or null if hashing fails
     */
    public static String hashPassword(String password, String username) {
        try {
            String saltedPassword = password + username;
            MessageDigest md = MessageDigest.getInstance("SHA-256");
            byte[] hashedBytes = md.digest(saltedPassword.getBytes(StandardCharsets.UTF_8));
            return Base64.getEncoder().encodeToString(hashedBytes);
        } catch (NoSuchAlgorithmException e) {
            Logger.getLogger(CustomUtilities.class.getName())
                    .severe("SHA-256 algorithm not found: " + e);
            return null;
        }
    }

    /**
     * Custom exception for password hashing failures.
     * <p>This is currently unused but may be useful if hashing
     * is reworked to support exception handling.</p>
     */
    public static class HashingException extends Exception {

        /**
         * Constructs a new hashing exception.
         *
         * @param message explanation of the error
         * @param cause underlying exception
         */
        public HashingException(String message, Throwable cause) {
            super(message, cause);
        }
    }
}
