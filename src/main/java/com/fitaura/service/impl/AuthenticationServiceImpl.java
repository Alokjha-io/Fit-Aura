package com.fitaura.service.impl;

import com.fitaura.dao.ActivityLogDAO;
import com.fitaura.dao.UserDAO;
import com.fitaura.dao.impl.ActivityLogDAOImpl;
import com.fitaura.dao.impl.UserDAOImpl;
import com.fitaura.exception.AuthenticationException;
import com.fitaura.exception.DatabaseException;
import com.fitaura.exception.ValidationException;
import com.fitaura.model.ActivityLog;
import com.fitaura.model.User;
import com.fitaura.service.AuthenticationService;
import com.fitaura.util.AppConstants;
import com.fitaura.util.PasswordUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Optional;
import java.util.regex.Pattern;

/**
 * Implementation of AuthenticationService coordinating password hashing,
 * account status validation, database persistence, and activity logging.
 */
public class AuthenticationServiceImpl implements AuthenticationService {

    private static final Logger logger = LoggerFactory.getLogger(AuthenticationServiceImpl.class);

    private static final Pattern EMAIL_PATTERN =
            Pattern.compile("^[A-Za-z0-9+_.-]+@[A-Za-z0-9.-]+\\.[A-Za-z]{2,}$");

    private final UserDAO userDAO;
    private final ActivityLogDAO activityLogDAO;

    public AuthenticationServiceImpl() {
        this(new UserDAOImpl(), new ActivityLogDAOImpl());
    }

    public AuthenticationServiceImpl(UserDAO userDAO, ActivityLogDAO activityLogDAO) {
        this.userDAO = userDAO;
        this.activityLogDAO = activityLogDAO;
    }

    @Override
    public User register(String fullName, String email, String password, String confirmPassword,
                         String displayName, String ipAddress) throws ValidationException {
        // 1. Validate required fields
        if (fullName == null || fullName.trim().isEmpty()) {
            throw new ValidationException("Full name is required.");
        }
        if (fullName.trim().length() > 100) {
            throw new ValidationException("Full name must not exceed 100 characters.");
        }

        if (email == null || email.trim().isEmpty()) {
            throw new ValidationException("Email address is required.");
        }
        String normalizedEmail = email.trim().toLowerCase();
        if (!EMAIL_PATTERN.matcher(normalizedEmail).matches() || normalizedEmail.length() > 150) {
            throw new ValidationException("Please enter a valid email address.");
        }

        // 2. Validate password
        if (password == null || password.isEmpty()) {
            throw new ValidationException("Password is required.");
        }
        if (!PasswordUtil.isStrongPassword(password)) {
            throw new ValidationException("Password must be at least 8 characters long and contain at least one letter and one number.");
        }

        // 3. Confirm password matching
        if (confirmPassword == null || !password.equals(confirmPassword)) {
            throw new ValidationException("Passwords do not match.");
        }

        // 4. Sanitize and prepare display name
        String sanitizedDisplayName = null;
        if (displayName != null && !displayName.trim().isEmpty()) {
            sanitizedDisplayName = displayName.trim();
            if (sanitizedDisplayName.length() > 60) {
                sanitizedDisplayName = sanitizedDisplayName.substring(0, 60);
            }
        } else {
            sanitizedDisplayName = fullName.trim();
            if (sanitizedDisplayName.length() > 60) {
                sanitizedDisplayName = sanitizedDisplayName.substring(0, 60);
            }
        }

        // 5. Check duplicate email
        try {
            Optional<User> existing = userDAO.findByEmail(normalizedEmail);
            if (existing.isPresent()) {
                throw new ValidationException("An account with this email address already exists.");
            }
        } catch (DatabaseException e) {
            logger.error("Database error while checking existing email: {}", e.getMessage());
            throw new ValidationException("Unable to process registration at this time. Please try again later.");
        }

        // 6. Securely hash password
        String passwordHash = PasswordUtil.hashPassword(password);

        // 7. Construct User entity (Enforce Role.USER, AccountStatus.ACTIVE, PrivacyMode.PERSONAL)
        User newUser = new User();
        newUser.setFullName(fullName.trim());
        newUser.setEmail(normalizedEmail);
        newUser.setPasswordHash(passwordHash);
        newUser.setRole(User.Role.USER);
        newUser.setAccountStatus(User.AccountStatus.ACTIVE);
        newUser.setPrivacyMode(User.PrivacyMode.PERSONAL);
        newUser.setDisplayName(sanitizedDisplayName);

        // 8. Persist to database
        try {
            Integer userId = userDAO.create(newUser);
            newUser.setUserId(userId);

            // Log security audit
            logActivity(userId, AppConstants.ACTION_REGISTRATION, "USER", userId,
                    "User registered with email: " + normalizedEmail, ipAddress);

            logger.info("Successfully registered user id: {} with email: {}", userId, normalizedEmail);
            return newUser;
        } catch (DatabaseException e) {
            logger.error("Failed to register user in database: {}", e.getMessage());
            throw new ValidationException("Registration failed due to a server error. Please try again.");
        }
    }

    @Override
    public User authenticate(String email, String password, String ipAddress) throws AuthenticationException {
        // 1. Basic validation
        if (email == null || email.trim().isEmpty() || password == null || password.isEmpty()) {
            logActivity(null, AppConstants.ACTION_LOGIN_FAILURE, "USER", null,
                    "Login attempt with empty credentials", ipAddress);
            throw new AuthenticationException("Invalid email or password.");
        }

        String normalizedEmail = email.trim().toLowerCase();

        // 2. Lookup user
        Optional<User> userOpt;
        try {
            userOpt = userDAO.findByEmail(normalizedEmail);
        } catch (DatabaseException e) {
            logger.error("Database error during login query: {}", e.getMessage());
            throw new AuthenticationException("Authentication service temporarily unavailable.");
        }

        // 3. Verify user presence and password
        if (userOpt.isEmpty() || !PasswordUtil.verifyPassword(password, userOpt.get().getPasswordHash())) {
            logActivity(userOpt.map(User::getUserId).orElse(null),
                    AppConstants.ACTION_LOGIN_FAILURE, "USER",
                    userOpt.map(User::getUserId).orElse(null),
                    "Failed login attempt for email: " + normalizedEmail, ipAddress);
            throw new AuthenticationException("Invalid email or password.");
        }

        User user = userOpt.get();

        // 4. Verify account status
        if (user.getAccountStatus() == User.AccountStatus.BLOCKED) {
            logActivity(user.getUserId(), AppConstants.ACTION_LOGIN_BLOCKED, "USER", user.getUserId(),
                    "Blocked user login attempt", ipAddress);
            throw new AuthenticationException("Your account is currently unavailable. Please contact support.");
        }

        if (user.getAccountStatus() == User.AccountStatus.INACTIVE) {
            logActivity(user.getUserId(), AppConstants.ACTION_LOGIN_INACTIVE, "USER", user.getUserId(),
                    "Inactive user login attempt", ipAddress);
            throw new AuthenticationException("Your account is deactivated. Please contact support.");
        }

        // 5. Update last login timestamp
        try {
            userDAO.updateLastLogin(user.getUserId());
        } catch (DatabaseException e) {
            logger.warn("Failed to update last_login_at for user {}: {}", user.getUserId(), e.getMessage());
        }

        // 6. Log successful authentication
        logActivity(user.getUserId(), AppConstants.ACTION_LOGIN_SUCCESS, "USER", user.getUserId(),
                "User successfully logged in", ipAddress);

        logger.info("User {} ({}) successfully authenticated from IP: {}",
                user.getUserId(), user.getEmail(), ipAddress);

        return user;
    }

    @Override
    public void recordLogout(Integer userId, String ipAddress) {
        if (userId != null) {
            logActivity(userId, AppConstants.ACTION_LOGOUT, "USER", userId,
                    "User logged out", ipAddress);
            logger.info("User {} logged out from IP: {}", userId, ipAddress);
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
            // Never fail authentication merely because non-critical audit log insert failed
            logger.warn("Could not record security activity log: {}", e.getMessage());
        }
    }
}
