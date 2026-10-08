package com.fitaura.util;

import org.mindrot.jbcrypt.BCrypt;

/**
 * Utility for secure password hashing and verification using the BCrypt algorithm.
 * Strictly avoids storing, logging, or exposing plaintext passwords.
 */
public final class PasswordUtil {

    private static final int BCRYPT_WORK_FACTOR = 12;

    private PasswordUtil() {
        // Prevent instantiation
    }

    /**
     * Hashes a plaintext password using BCrypt with a secure salt and work factor 12.
     *
     * @param plainPassword Plaintext password to hash
     * @return Resulting BCrypt password hash
     * @throws IllegalArgumentException if plainPassword is null or empty
     */
    public static String hashPassword(String plainPassword) {
        if (plainPassword == null || plainPassword.isEmpty()) {
            throw new IllegalArgumentException("Password cannot be null or empty.");
        }
        return BCrypt.hashpw(plainPassword, BCrypt.gensalt(BCRYPT_WORK_FACTOR));
    }

    /**
     * Verifies a candidate plaintext password against an existing BCrypt hash.
     *
     * @param plainPassword Plaintext password to test
     * @param hashedPassword Stored BCrypt password hash
     * @return true if credentials match, false otherwise
     */
    public static boolean verifyPassword(String plainPassword, String hashedPassword) {
        if (plainPassword == null || hashedPassword == null || hashedPassword.isEmpty()) {
            return false;
        }
        try {
            return BCrypt.checkpw(plainPassword, hashedPassword);
        } catch (IllegalArgumentException e) {
            // Invalid hash format
            return false;
        }
    }

    /**
     * Enforces password complexity rules:
     * - Minimum 8 characters
     * - At least one digit (0-9)
     * - At least one letter (a-z or A-Z)
     *
     * @param password Candidate password
     * @return true if password meets strength criteria
     */
    public static boolean isStrongPassword(String password) {
        if (password == null || password.length() < 8) {
            return false;
        }
        boolean hasDigit = false;
        boolean hasLetter = false;

        for (char c : password.toCharArray()) {
            if (Character.isDigit(c)) {
                hasDigit = true;
            } else if (Character.isLetter(c)) {
                hasLetter = true;
            }
            if (hasDigit && hasLetter) {
                return true;
            }
        }
        return false;
    }
}
