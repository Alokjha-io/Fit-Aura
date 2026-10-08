package com.fitaura.util;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpSession;

import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.security.SecureRandom;
import java.util.Base64;

/**
 * Utility for generating and verifying cryptographically strong, session-bound CSRF tokens.
 */
public final class CsrfUtil {

    private static final SecureRandom SECURE_RANDOM = new SecureRandom();

    private CsrfUtil() {
        // Prevent instantiation
    }

    /**
     * Retrieves or generates a session-bound CSRF token.
     *
     * @param session The active user HttpSession
     * @return 32-byte Base64-encoded token string
     */
    public static String getToken(HttpSession session) {
        if (session == null) {
            return "";
        }
        synchronized (session) {
            String token = (String) session.getAttribute(AppConstants.CSRF_TOKEN_KEY);
            if (token == null || token.isEmpty()) {
                byte[] bytes = new byte[32];
                SECURE_RANDOM.nextBytes(bytes);
                token = Base64.getUrlEncoder().withoutPadding().encodeToString(bytes);
                session.setAttribute(AppConstants.CSRF_TOKEN_KEY, token);
            }
            return token;
        }
    }

    /**
     * Validates that the submitted CSRF parameter matches the session-bound token using constant-time comparison.
     *
     * @param request The incoming HTTP request
     * @return true if valid token supplied, false otherwise
     */
    public static boolean isValidToken(HttpServletRequest request) {
        if (request == null) {
            return false;
        }
        HttpSession session = request.getSession(false);
        if (session == null) {
            return false;
        }

        String sessionToken = (String) session.getAttribute(AppConstants.CSRF_TOKEN_KEY);
        if (sessionToken == null || sessionToken.isEmpty()) {
            return false;
        }

        String requestToken = request.getParameter(AppConstants.CSRF_PARAM_NAME);
        if (requestToken == null || requestToken.isEmpty()) {
            // Also check header in case AJAX request
            requestToken = request.getHeader("X-CSRF-Token");
        }

        if (requestToken == null || requestToken.isEmpty()) {
            return false;
        }

        return MessageDigest.isEqual(
                sessionToken.getBytes(StandardCharsets.UTF_8),
                requestToken.getBytes(StandardCharsets.UTF_8)
        );
    }
}
