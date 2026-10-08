package com.fitaura.util;

import com.fitaura.dao.UserDAO;
import com.fitaura.dao.impl.UserDAOImpl;
import com.fitaura.model.User;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Optional;

/**
 * Secure Server-Side Administrator Bootstrap Service.
 * Bootstraps an administrator account from server-side environment variables:
 * - FITAURA_ADMIN_EMAIL
 * - FITAURA_ADMIN_PASSWORD
 * 
 * Ensures credentials are never hardcoded into source files or exposed in UI.
 */
public final class AdminBootstrapService {

    private static final Logger logger = LoggerFactory.getLogger(AdminBootstrapService.class);

    public static final String ENV_ADMIN_EMAIL = "FITAURA_ADMIN_EMAIL";
    public static final String ENV_ADMIN_PASSWORD = "FITAURA_ADMIN_PASSWORD";
    public static final String ENV_ADMIN_NAME = "FITAURA_ADMIN_NAME";

    private AdminBootstrapService() {
        // Utility
    }

    /**
     * Initializes administrator account from environment variables if configured.
     */
    public static void initializeAdminAccount() {
        initializeAdminAccount(new UserDAOImpl());
    }

    public static void initializeAdminAccount(UserDAO userDAO) {
        String adminEmail = getEnvFirst(ENV_ADMIN_EMAIL, "ADMIN_EMAIL");
        String adminPassword = getEnvFirst(ENV_ADMIN_PASSWORD, "ADMIN_PASSWORD");
        String adminName = getEnvFirst(ENV_ADMIN_NAME, "ADMIN_NAME");

        if (adminEmail == null || adminEmail.trim().isEmpty() ||
            adminPassword == null || adminPassword.trim().isEmpty()) {
            logger.info("No server administrator bootstrap environment variables detected (FITAURA_ADMIN_EMAIL / FITAURA_ADMIN_PASSWORD). Skipping admin bootstrap.");
            return;
        }

        String normalizedEmail = adminEmail.trim().toLowerCase();
        String resolvedName = (adminName != null && !adminName.trim().isEmpty()) ? adminName.trim() : "System Administrator";

        try {
            Optional<User> existing = userDAO.findByEmail(normalizedEmail);
            if (existing.isEmpty()) {
                String passwordHash = PasswordUtil.hashPassword(adminPassword);
                User admin = new User();
                admin.setFullName(resolvedName);
                admin.setDisplayName("Admin");
                admin.setEmail(normalizedEmail);
                admin.setPasswordHash(passwordHash);
                admin.setRole(User.Role.ADMIN);
                admin.setAccountStatus(User.AccountStatus.ACTIVE);
                admin.setPrivacyMode(User.PrivacyMode.PERSONAL);

                Integer id = userDAO.create(admin);
                logger.info("Securely bootstrapped server administrator account #{} for email target: {}", id, maskEmail(normalizedEmail));
            } else {
                User user = existing.get();
                if (user.getRole() != User.Role.ADMIN) {
                    user.setRole(User.Role.ADMIN);
                    userDAO.update(user);
                    logger.info("Updated existing user #{} to ADMIN role based on server environment configuration.", user.getUserId());
                }
            }
        } catch (Exception e) {
            logger.error("Failed to bootstrap administrator account: {}", e.getMessage(), e);
        }
    }

    private static String maskEmail(String email) {
        if (email == null || !email.contains("@")) return "***";
        String[] parts = email.split("@");
        String name = parts[0];
        if (name.length() <= 2) return name.charAt(0) + "*@" + parts[1];
        return name.charAt(0) + "***" + name.charAt(name.length() - 1) + "@" + parts[1];
    }

    private static String getEnvFirst(String... keys) {
        for (String key : keys) {
            String val = System.getenv(key);
            if (val != null && !val.trim().isEmpty()) {
                return val.trim();
            }
        }
        return null;
    }
}
