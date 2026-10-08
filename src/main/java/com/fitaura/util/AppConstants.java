package com.fitaura.util;

/**
 * Global application constants for FitAura - Track. Improve. Thrive.
 * Centralizes application metadata, configuration keys, session attributes,
 * and architectural defaults.
 */
public final class AppConstants {

    // Prevent instantiation
    private AppConstants() {
        throw novelInstantiationException();
    }

    private static UnsupportedOperationException novelInstantiationException() {
        return new UnsupportedOperationException("AppConstants is a utility class and cannot be instantiated.");
    }

    // Application Metadata
    public static final String APP_NAME = "FitAura";
    public static final String APP_TAGLINE = "Track. Improve. Thrive.";
    public static final String APP_VERSION = "1.0.0";
    public static final String APP_PHASE = "Phase 12 - System Monitoring, Security Hardening & Audit";

    // Session Attribute Keys
    public static final String SESSION_USER_ID = "authenticatedUserId";
    public static final String SESSION_USER_EMAIL = "authenticatedUserEmail";
    public static final String SESSION_USER_NAME = "authenticatedUserName";
    public static final String SESSION_DISPLAY_NAME = "authenticatedDisplayName";
    public static final String SESSION_USER_ROLE = "authenticatedUserRole";
    public static final String SESSION_PRIVACY_MODE = "authenticatedPrivacyMode";

    // Security & CSRF
    public static final String CSRF_TOKEN_KEY = "csrfToken";
    public static final String CSRF_PARAM_NAME = "_csrf";

    // Privacy Modes
    public static final String PRIVACY_PERSONAL = "PERSONAL";
    public static final String PRIVACY_SOCIAL = "SOCIAL";

    // User Roles
    public static final String ROLE_USER = "USER";
    public static final String ROLE_ADMIN = "ADMIN";

    // Account Statuses
    public static final String STATUS_ACTIVE = "ACTIVE";
    public static final String STATUS_INACTIVE = "INACTIVE";
    public static final String STATUS_BLOCKED = "BLOCKED";

    // Activity Log Actions
    public static final String ACTION_LOGIN_SUCCESS = "LOGIN_SUCCESS";
    public static final String ACTION_LOGIN_FAILURE = "LOGIN_FAILURE";
    public static final String ACTION_LOGIN_BLOCKED = "LOGIN_BLOCKED";
    public static final String ACTION_LOGIN_INACTIVE = "LOGIN_INACTIVE";
    public static final String ACTION_LOGOUT = "LOGOUT";
    public static final String ACTION_REGISTRATION = "REGISTRATION";
    public static final String ACTION_PROFILE_UPDATED = "PROFILE_UPDATED";
    public static final String ACTION_FITNESS_PROFILE_UPDATED = "FITNESS_PROFILE_UPDATED";
    public static final String ACTION_PASSWORD_CHANGED = "PASSWORD_CHANGED";
    public static final String ACTION_PRIVACY_MODE_CHANGED = "PRIVACY_MODE_CHANGED";
    public static final String ACTION_PRIVACY_CHANGED = "PRIVACY_CHANGED";
    public static final String ACTION_WORKOUT_CREATED = "WORKOUT_CREATED";
    public static final String ACTION_WORKOUT_UPDATED = "WORKOUT_UPDATED";
    public static final String ACTION_WORKOUT_DELETED = "WORKOUT_DELETED";
    public static final String ACTION_GOAL_CREATED = "GOAL_CREATED";
    public static final String ACTION_GOAL_UPDATED = "GOAL_UPDATED";
    public static final String ACTION_GOAL_STATUS_CHANGED = "GOAL_STATUS_CHANGED";
    public static final String ACTION_GOAL_CANCELLED = "GOAL_CANCELLED";
    public static final String ACTION_GOAL_DELETED = "GOAL_DELETED";
    public static final String ACTION_CHALLENGE_CREATED = "CHALLENGE_CREATED";
    public static final String ACTION_CHALLENGE_JOINED = "CHALLENGE_JOINED";
    public static final String ACTION_CHALLENGE_LEFT = "CHALLENGE_LEFT";
    public static final String ACTION_CHALLENGE_COMPLETED = "CHALLENGE_COMPLETED";
    public static final String ACTION_CHALLENGE_APPROVED = "CHALLENGE_APPROVED";
    public static final String ACTION_CHALLENGE_REJECTED = "CHALLENGE_REJECTED";
    public static final String ACTION_CHALLENGE_CANCELLED = "CHALLENGE_CANCELLED";
    public static final String ACTION_CONTENT_CREATED = "CONTENT_CREATED";
    public static final String ACTION_CONTENT_UPDATED = "CONTENT_UPDATED";
    public static final String ACTION_CONTENT_APPROVED = "CONTENT_APPROVED";
    public static final String ACTION_CONTENT_REJECTED = "CONTENT_REJECTED";
    public static final String ACTION_CONTENT_DELETED = "CONTENT_DELETED";
    public static final String ACTION_USER_STATUS_CHANGED = "USER_STATUS_CHANGED";
    public static final String ACTION_POINT_ADJUSTMENT = "POINT_ADJUSTMENT";
    public static final String ACTION_SETTING_CHANGED = "SETTING_CHANGED";
    public static final String ACTION_ACHIEVEMENT_UNLOCKED = "ACHIEVEMENT_UNLOCKED";
    public static final String ACTION_UNAUTHORIZED_ACCESS_ATTEMPT = "UNAUTHORIZED_ACCESS_ATTEMPT";
    public static final String ACTION_CSRF_VALIDATION_FAILURE = "CSRF_VALIDATION_FAILURE";
    public static final String ACTION_SECURITY_AUDIT = "SECURITY_AUDIT";

    // Database Defaults
    public static final String DEFAULT_DB_NAME = "fitaura";
    public static final String DB_DRIVER = "com.mysql.cj.jdbc.Driver";

    // Default Encoding
    public static final String DEFAULT_CHARSET = "UTF-8";
}
