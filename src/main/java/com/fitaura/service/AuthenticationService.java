package com.fitaura.service;

import com.fitaura.exception.AuthenticationException;
import com.fitaura.exception.ValidationException;
import com.fitaura.model.User;

/**
 * Service interface encapsulating user registration, authentication,
 * and security event logging workflows.
 */
public interface AuthenticationService {

    /**
     * Registers a new user account with default role USER, status ACTIVE, and privacy PERSONAL.
     *
     * @param fullName User's legal or preferred full name
     * @param email User's email address
     * @param password Plaintext password to validate and hash
     * @param confirmPassword Password confirmation to ensure exact match
     * @param displayName Optional alias display name for social mode
     * @param ipAddress Client IP address for security audit logging
     * @return Newly created User entity
     * @throws ValidationException if inputs are invalid or email already exists
     */
    User register(String fullName, String email, String password, String confirmPassword,
                  String displayName, String ipAddress) throws ValidationException;

    /**
     * Authenticates an existing user credentials and validates account status.
     *
     * @param email User's login email
     * @param password Plaintext candidate password
     * @param ipAddress Client IP address for security audit logging
     * @return Authenticated User entity with updated last_login_at
     * @throws AuthenticationException if credentials fail or account is inactive/blocked
     */
    User authenticate(String email, String password, String ipAddress) throws AuthenticationException;

    /**
     * Records a logout event in the security audit trail.
     *
     * @param userId Authenticated user ID
     * @param ipAddress Client IP address
     */
    void recordLogout(Integer userId, String ipAddress);
}
