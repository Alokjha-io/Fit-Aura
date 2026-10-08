package com.fitaura.service.impl;

import com.fitaura.dao.ActivityLogDAO;
import com.fitaura.dao.UserDAO;
import com.fitaura.dao.impl.ActivityLogDAOImpl;
import com.fitaura.dao.impl.UserDAOImpl;
import com.fitaura.exception.DatabaseException;
import com.fitaura.exception.ResourceNotFoundException;
import com.fitaura.exception.ValidationException;
import com.fitaura.model.ActivityLog;
import com.fitaura.model.User;
import com.fitaura.service.UserService;
import com.fitaura.util.AppConstants;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Optional;

/**
 * Implementation of UserService managing basic user profile modifications
 * and security audit logs.
 */
public class UserServiceImpl implements UserService {

    private static final Logger logger = LoggerFactory.getLogger(UserServiceImpl.class);

    private final UserDAO userDAO;
    private final ActivityLogDAO activityLogDAO;

    public UserServiceImpl() {
        this(new UserDAOImpl(), new ActivityLogDAOImpl());
    }

    public UserServiceImpl(UserDAO userDAO, ActivityLogDAO activityLogDAO) {
        this.userDAO = userDAO;
        this.activityLogDAO = activityLogDAO;
    }

    @Override
    public User getUserById(Integer userId) throws ResourceNotFoundException {
        if (userId == null) {
            throw new ResourceNotFoundException("User ID cannot be null.");
        }
        Optional<User> userOpt;
        try {
            userOpt = userDAO.findById(userId);
        } catch (DatabaseException e) {
            logger.error("Database error retrieving user {}: {}", userId, e.getMessage());
            throw new DatabaseException("Could not retrieve user information.", e);
        }
        return userOpt.orElseThrow(() -> new ResourceNotFoundException("User with ID " + userId + " was not found."));
    }

    @Override
    public User updateBasicProfile(Integer userId, String fullName, String displayName, String ipAddress)
            throws ValidationException, ResourceNotFoundException {
        // Validate user existence
        User existingUser = getUserById(userId);

        // Validate Full Name
        if (fullName == null || fullName.trim().isEmpty()) {
            throw new ValidationException("Full name is required.");
        }
        String sanitizedFullName = fullName.trim();
        if (sanitizedFullName.length() > 100) {
            throw new ValidationException("Full name must not exceed 100 characters.");
        }

        // Validate Display Name
        String sanitizedDisplayName = sanitizedFullName;
        if (displayName != null && !displayName.trim().isEmpty()) {
            sanitizedDisplayName = displayName.trim();
            if (sanitizedDisplayName.length() > 60) {
                sanitizedDisplayName = sanitizedDisplayName.substring(0, 60);
            }
        }

        try {
            boolean updated = userDAO.updateProfile(userId, sanitizedFullName, sanitizedDisplayName);
            if (!updated) {
                throw new DatabaseException("Unable to update profile record.");
            }

            // Update in-memory entity
            existingUser.setFullName(sanitizedFullName);
            existingUser.setDisplayName(sanitizedDisplayName);

            // Audit log
            logActivity(userId, AppConstants.ACTION_PROFILE_UPDATED, "USER", userId,
                    "Basic profile updated: " + sanitizedFullName, ipAddress);

            logger.info("Updated basic profile for user {}", userId);
            return existingUser;

        } catch (DatabaseException e) {
            logger.error("Error updating profile for user {}: {}", userId, e.getMessage());
            throw new DatabaseException("Failed to update user profile in database.", e);
        }
    }

    private void logActivity(Integer userId, String action, String entityType,
                             Integer entityId, String description, String ipAddress) {
        try {
            ActivityLog log = new ActivityLog();
            log.setUserId(userId);
            log.setActionType(action);
            log.setEntityType(entityType);
            log.setEntityId(entityId);
            log.setDescription(description);
            log.setIpAddress(ipAddress);
            activityLogDAO.create(log);
        } catch (Exception e) {
            logger.warn("Could not write profile activity log: {}", e.getMessage());
        }
    }
}
