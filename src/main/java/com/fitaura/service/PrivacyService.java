package com.fitaura.service;

import com.fitaura.exception.ValidationException;
import com.fitaura.model.User;

/**
 * Service interface governing FitAura's core privacy differentiator:
 * Personal Mode vs. Social Mode.
 */
public interface PrivacyService {

    /**
     * Retrieves the current privacy mode for a user.
     *
     * @param userId Authenticated user ID from session
     * @return PrivacyMode enum (PERSONAL or SOCIAL)
     */
    User.PrivacyMode getPrivacyMode(Integer userId);

    /**
     * Updates the user's privacy mode with server-side validation.
     *
     * @param userId Authenticated user ID from session
     * @param mode Target privacy mode
     * @param ipAddress Client IP for audit logging
     * @throws ValidationException if mode is invalid
     */
    void updatePrivacyMode(Integer userId, User.PrivacyMode mode, String ipAddress)
            throws ValidationException;
}
