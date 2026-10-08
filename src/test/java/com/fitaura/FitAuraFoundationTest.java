package com.fitaura;

import com.fitaura.util.AppConstants;
import com.fitaura.exception.FitAuraException;
import com.fitaura.exception.DatabaseException;
import com.fitaura.exception.ValidationException;
import com.fitaura.exception.AuthenticationException;
import com.fitaura.exception.AuthorizationException;
import com.fitaura.exception.ResourceNotFoundException;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Foundation tests verifying Phase 1 requirements:
 * - Java 17+ runtime
 * - Global Application Constants
 * - Layered exception hierarchy
 */
public class FitAuraFoundationTest {

    @Test
    @DisplayName("Verify Java Runtime is version 17 or higher")
    void testJavaVersion() {
        String version = System.getProperty("java.version");
        assertNotNull(version, "Java version string must not be null");
        int majorVersion = Runtime.version().feature();
        assertTrue(majorVersion >= 17, "Application requires Java 17+, detected: " + majorVersion);
    }

    @Test
    @DisplayName("Verify Application Constants are properly defined")
    void testAppConstants() {
        assertEquals("FitAura", AppConstants.APP_NAME);
        assertEquals("Track. Improve. Thrive.", AppConstants.APP_TAGLINE);
        assertEquals("1.0.0", AppConstants.APP_VERSION);
        assertEquals("PERSONAL", AppConstants.PRIVACY_PERSONAL);
        assertEquals("SOCIAL", AppConstants.PRIVACY_SOCIAL);
        assertEquals("USER", AppConstants.ROLE_USER);
        assertEquals("ADMIN", AppConstants.ROLE_ADMIN);
    }

    @Test
    @DisplayName("Verify FitAura Exception hierarchy")
    void testExceptionHierarchy() {
        FitAuraException dbEx = new DatabaseException("DB error");
        assertTrue(dbEx instanceof RuntimeException);
        assertEquals("DB error", dbEx.getMessage());

        FitAuraException valEx = new ValidationException("Validation failed");
        assertEquals("Validation failed", valEx.getMessage());

        FitAuraException authEx = new AuthenticationException("Bad credentials");
        assertEquals("Bad credentials", authEx.getMessage());

        FitAuraException authzEx = new AuthorizationException("Forbidden");
        assertEquals("Forbidden", authzEx.getMessage());

        FitAuraException notFoundEx = new ResourceNotFoundException("Not found");
        assertEquals("Not found", notFoundEx.getMessage());
    }
}
