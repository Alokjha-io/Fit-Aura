package com.fitaura.service;

import com.fitaura.exception.ResourceNotFoundException;
import com.fitaura.exception.ValidationException;
import com.fitaura.model.User;

/**
 * Service interface managing user identity, basic profile attributes,
 * and account verification.
 */
public interface UserService {

    /**
     * Retrieves the current user entity by authenticated user ID.
     *
     * @param userId Authenticated user ID from session
     * @return User entity
     * @throws ResourceNotFoundException if user does not exist
     */
    User getUserById(Integer userId) throws ResourceNotFoundException;

    /**
     * Updates basic user profile attributes (Full name, display name).
     *
     * @param userId Authenticated user ID from session
     * @param fullName Updated full name (required)
     * @param displayName Updated display name (optional)
     * @param ipAddress Client IP for audit logging
     * @return Updated User entity
     * @throws ValidationException if inputs are invalid
     * @throws ResourceNotFoundException if user does not exist
     */
    User updateBasicProfile(Integer userId, String fullName, String displayName, String ipAddress)
            throws ValidationException, ResourceNotFoundException;
}
