package com.fitaura.util;

import com.fitaura.model.User;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpSession;

/**
 * Utility for managing user authentication state within HttpSession.
 * Provides protection against session fixation and strictly limits stored session attributes.
 */
public final class SessionUtil {

    private SessionUtil() {
        // Prevent instantiation
    }

    /**
     * Establishes an authenticated session for a validated user.
     * Rotates session ID to prevent session fixation vulnerabilities.
     *
     * @param request HTTP request
     * @param user Authenticated user entity
     * @return New or refreshed HttpSession
     */
    public static HttpSession createAuthenticatedSession(HttpServletRequest request, User user) {
        if (request == null || user == null) {
            throw new IllegalArgumentException("Request and User must not be null.");
        }

        // Session fixation protection: rotate session
        HttpSession existingSession = request.getSession(false);
        if (existingSession != null) {
            try {
                // Servlet 3.1+ method to change session id while preserving attributes
                request.changeSessionId();
            } catch (IllegalStateException e) {
                // If changeSessionId is not supported, invalidate and create new
                existingSession.invalidate();
            }
        }

        HttpSession session = request.getSession(true);

        // Store minimal identity metadata only
        session.setAttribute(AppConstants.SESSION_USER_ID, user.getUserId());
        session.setAttribute(AppConstants.SESSION_USER_EMAIL, user.getEmail());
        session.setAttribute(AppConstants.SESSION_USER_NAME, user.getFullName());
        session.setAttribute(AppConstants.SESSION_DISPLAY_NAME,
                user.getDisplayName() != null && !user.getDisplayName().isEmpty()
                        ? user.getDisplayName()
                        : user.getFullName());
        session.setAttribute(AppConstants.SESSION_USER_ROLE, user.getRole().name());
        session.setAttribute(AppConstants.SESSION_PRIVACY_MODE, user.getPrivacyMode().name());

        // Refresh/re-bind CSRF token for the new session
        CsrfUtil.getToken(session);

        return session;
    }

    /**
     * Checks if a request has an active, authenticated session.
     */
    public static boolean isAuthenticated(HttpServletRequest request) {
        if (request == null) return false;
        HttpSession session = request.getSession(false);
        return session != null && session.getAttribute(AppConstants.SESSION_USER_ID) != null;
    }

    /**
     * Retrieves the authenticated user's ID from session.
     * Never trusts IDs passed via query parameters or form fields.
     */
    public static Integer getAuthenticatedUserId(HttpServletRequest request) {
        if (request == null) return null;
        HttpSession session = request.getSession(false);
        if (session == null) return null;
        Object userIdObj = session.getAttribute(AppConstants.SESSION_USER_ID);
        if (userIdObj instanceof Integer) {
            return (Integer) userIdObj;
        }
        return null;
    }

    /**
     * Retrieves the authenticated user's role from session.
     */
    public static String getAuthenticatedUserRole(HttpServletRequest request) {
        if (request == null) return null;
        HttpSession session = request.getSession(false);
        if (session == null) return null;
        return (String) session.getAttribute(AppConstants.SESSION_USER_ROLE);
    }

    /**
     * Retrieves a safe User identity entity representing the authenticated user.
     * Contains only verified session-bound metadata (no passwords or hashes).
     */
    public static User getAuthenticatedUser(HttpServletRequest request) {
        if (!isAuthenticated(request)) {
            return null;
        }
        HttpSession session = request.getSession(false);
        if (session == null) {
            return null;
        }

        User user = new User();
        user.setUserId(getAuthenticatedUserId(request));
        user.setEmail((String) session.getAttribute(AppConstants.SESSION_USER_EMAIL));
        user.setFullName((String) session.getAttribute(AppConstants.SESSION_USER_NAME));
        user.setDisplayName((String) session.getAttribute(AppConstants.SESSION_DISPLAY_NAME));
        user.setAccountStatus(User.AccountStatus.ACTIVE);

        String roleStr = getAuthenticatedUserRole(request);
        if (roleStr != null) {
            try {
                user.setRole(User.Role.valueOf(roleStr));
            } catch (Exception ignored) {
                user.setRole(User.Role.USER);
            }
        }

        String privStr = (String) session.getAttribute(AppConstants.SESSION_PRIVACY_MODE);
        if (privStr != null) {
            try {
                user.setPrivacyMode(User.PrivacyMode.valueOf(privStr));
            } catch (Exception ignored) {
                user.setPrivacyMode(User.PrivacyMode.PERSONAL);
            }
        }

        return user;
    }

    /**
     * Checks if the authenticated user has ADMIN privileges.
     */
    public static boolean isAdmin(HttpServletRequest request) {
        return AppConstants.ROLE_ADMIN.equalsIgnoreCase(getAuthenticatedUserRole(request));
    }

    /**
     * Safely invalidates the session upon logout.
     */
    public static void invalidateSession(HttpServletRequest request) {
        if (request == null) return;
        HttpSession session = request.getSession(false);
        if (session != null) {
            session.invalidate();
        }
    }
}
