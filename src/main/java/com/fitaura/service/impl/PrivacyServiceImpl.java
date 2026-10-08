package com.fitaura.service.impl;

import com.fitaura.dao.ActivityLogDAO;
import com.fitaura.dao.UserDAO;
import com.fitaura.dao.impl.ActivityLogDAOImpl;
import com.fitaura.dao.impl.UserDAOImpl;
import com.fitaura.exception.DatabaseException;
import com.fitaura.exception.ValidationException;
import com.fitaura.model.ActivityLog;
import com.fitaura.model.User;
import com.fitaura.service.PrivacyService;
import com.fitaura.util.AppConstants;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Optional;

/**
 * Implementation of PrivacyService enforcing server-side privacy boundaries.
 */
public class PrivacyServiceImpl implements PrivacyService {

    private static final Logger logger = LoggerFactory.getLogger(PrivacyServiceImpl.class);

    private final UserDAO userDAO;
    private final ActivityLogDAO activityLogDAO;

    public PrivacyServiceImpl() {
        this(new UserDAOImpl(), new ActivityLogDAOImpl());
    }

    public PrivacyServiceImpl(UserDAO userDAO, ActivityLogDAO activityLogDAO) {
        this.userDAO = userDAO;
        this.activityLogDAO = activityLogDAO;
    }

    @Override
    public User.PrivacyMode getPrivacyMode(Integer userId) {
        if (userId == null) {
            return User.PrivacyMode.PERSONAL;
        }
        try {
            Optional<User> userOpt = userDAO.findById(userId);
            return userOpt.map(User::getPrivacyMode).orElse(User.PrivacyMode.PERSONAL);
        } catch (DatabaseException e) {
            logger.error("Error retrieving privacy mode for user {}: {}", userId, e.getMessage());
            return User.PrivacyMode.PERSONAL;
        }
    }

    @Override
    public void updatePrivacyMode(Integer userId, User.PrivacyMode mode, String ipAddress) throws ValidationException {
        if (userId == null) {
            throw new ValidationException("User ID must not be null.");
        }
        if (mode == null) {
            throw new ValidationException("Please select a valid privacy mode (Personal or Social).");
        }

        try {
            boolean updated = userDAO.updatePrivacyMode(userId, mode);
            if (!updated) {
                throw new DatabaseException("Failed to update privacy mode.");
            }

            // Audit Log
            logActivity(userId, AppConstants.ACTION_PRIVACY_MODE_CHANGED, "USER", userId,
                    "Privacy mode changed to " + mode.name(), ipAddress);

            logger.info("Updated privacy mode to {} for user {}", mode, userId);

        } catch (DatabaseException e) {
            logger.error("Database error updating privacy mode for user {}: {}", userId, e.getMessage());
            throw new DatabaseException("Failed to update privacy mode in database.", e);
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
            logger.warn("Could not write privacy activity log: {}", e.getMessage());
        }
    }
}
