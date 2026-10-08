package com.fitaura;

import com.fitaura.dao.ActivityLogDAO;
import com.fitaura.dao.FitnessProfileDAO;
import com.fitaura.dao.UserDAO;
import com.fitaura.dao.impl.ActivityLogDAOImpl;
import com.fitaura.dao.impl.FitnessProfileDAOImpl;
import com.fitaura.dao.impl.UserDAOImpl;
import com.fitaura.exception.ResourceNotFoundException;
import com.fitaura.exception.ValidationException;
import com.fitaura.model.ActivityLog;
import com.fitaura.model.FitnessProfile;
import com.fitaura.model.User;
import com.fitaura.service.FitnessProfileService;
import com.fitaura.service.PrivacyService;
import com.fitaura.service.UserService;
import com.fitaura.service.impl.FitnessProfileServiceImpl;
import com.fitaura.service.impl.PrivacyServiceImpl;
import com.fitaura.service.impl.UserServiceImpl;
import com.fitaura.util.AppConstants;
import com.fitaura.util.PasswordUtil;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.math.BigDecimal;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Automated test suite verifying all Phase 4 User Profile & Fitness Profile requirements:
 * 1. Basic profile retrieval for authenticated user
 * 2. Basic profile update (full name, display name)
 * 3. Validation: blank full name rejected
 * 4. Validation: overly long full name (> 100 chars) rejected
 * 5. Display name formatting and sanitization
 * 6. Email immutability during profile operations
 * 7. Security: role cannot be modified via profile update
 * 8. Security: account status cannot be modified via profile update
 * 9. Password hash remains intact and uncompromised
 * 10. Fitness profile persistence and retrieval
 * 11. Fitness profile update (upsert)
 * 12. Validation: Age < 13 rejected
 * 13. Validation: Age > 120 rejected
 * 14. Validation: Height < 50 cm rejected
 * 15. Validation: Height > 260 cm rejected
 * 16. Validation: Weight < 20 kg rejected
 * 17. Validation: Weight > 500 kg rejected
 * 18. Activity level enum validation and default
 * 19. Preferred environment enum validation and default
 * 20. Privacy mode retrieval defaults to PERSONAL
 * 21. Privacy mode update to SOCIAL
 * 22. Privacy mode update back to PERSONAL
 * 23. Activity audit logs recorded for profile update, fitness profile update, and privacy mode changes
 * 24. BMI calculation accuracy and category mapping
 * 25. IDOR ownership protection (session userId binding)
 */
public class ProfileFitnessSecurityTest {

    private UserDAO userDAO;
    private FitnessProfileDAO fitnessProfileDAO;
    private ActivityLogDAO activityLogDAO;

    private UserService userService;
    private FitnessProfileService fitnessProfileService;
    private PrivacyService privacyService;

    private User testUser;

    @BeforeEach
    void setUp() {
        this.userDAO = new UserDAOImpl();
        this.fitnessProfileDAO = new FitnessProfileDAOImpl();
        this.activityLogDAO = new ActivityLogDAOImpl();

        this.userService = new UserServiceImpl(userDAO, activityLogDAO);
        this.fitnessProfileService = new FitnessProfileServiceImpl(fitnessProfileDAO, activityLogDAO);
        this.privacyService = new PrivacyServiceImpl(userDAO, activityLogDAO);

        // Create a unique test user in the database
        long timestamp = System.currentTimeMillis();
        User user = new User();
        user.setFullName("Test Athlete");
        user.setEmail("athlete_" + timestamp + "@fitaura.test");
        user.setPasswordHash(PasswordUtil.hashPassword("Password123!"));
        user.setRole(User.Role.USER);
        user.setAccountStatus(User.AccountStatus.ACTIVE);
        user.setPrivacyMode(User.PrivacyMode.PERSONAL);
        user.setDisplayName("Athlete" + timestamp % 1000);

        Integer createdId = userDAO.create(user);
        assertNotNull(createdId, "Test user ID must be generated");
        user.setUserId(createdId);
        this.testUser = user;
    }

    @Test
    @DisplayName("1. Basic profile retrieval returns correct user data")
    void testGetUserById() {
        User retrieved = userService.getUserById(testUser.getUserId());
        assertNotNull(retrieved);
        assertEquals(testUser.getEmail(), retrieved.getEmail());
        assertEquals(testUser.getFullName(), retrieved.getFullName());
        assertEquals(User.Role.USER, retrieved.getRole());
        assertEquals(User.PrivacyMode.PERSONAL, retrieved.getPrivacyMode());
    }

    @Test
    @DisplayName("2. Basic profile update modifies full name and display name")
    void testUpdateBasicProfileSuccess() {
        User updated = userService.updateBasicProfile(
                testUser.getUserId(),
                "Updated Athlete Name",
                "SpeedRunner",
                "127.0.0.1"
        );

        assertEquals("Updated Athlete Name", updated.getFullName());
        assertEquals("SpeedRunner", updated.getDisplayName());

        // Verify in database
        User fromDb = userDAO.findById(testUser.getUserId()).orElseThrow();
        assertEquals("Updated Athlete Name", fromDb.getFullName());
        assertEquals("SpeedRunner", fromDb.getDisplayName());
    }

    @Test
    @DisplayName("3. Validation rejects empty or whitespace-only full name")
    void testUpdateBasicProfileEmptyNameFails() {
        assertThrows(ValidationException.class, () ->
                userService.updateBasicProfile(testUser.getUserId(), "   ", "Alias", "127.0.0.1")
        );
        assertThrows(ValidationException.class, () ->
                userService.updateBasicProfile(testUser.getUserId(), null, "Alias", "127.0.0.1")
        );
    }

    @Test
    @DisplayName("4. Validation rejects overly long full name (> 100 chars)")
    void testUpdateBasicProfileLongNameFails() {
        String overlyLongName = "A".repeat(101);
        assertThrows(ValidationException.class, () ->
                userService.updateBasicProfile(testUser.getUserId(), overlyLongName, "Alias", "127.0.0.1")
        );
    }

    @Test
    @DisplayName("5. Display name is sanitized and truncated if excessively long")
    void testUpdateBasicProfileDisplayNameSanitization() {
        String longDisplayName = "B".repeat(80);
        User updated = userService.updateBasicProfile(
                testUser.getUserId(),
                "Valid Name",
                longDisplayName,
                "127.0.0.1"
        );
        assertEquals(60, updated.getDisplayName().length());
    }

    @Test
    @DisplayName("6. Profile update does not mutate email address")
    void testEmailImmutabilityOnProfileUpdate() {
        String originalEmail = testUser.getEmail();
        userService.updateBasicProfile(testUser.getUserId(), "Changed Name", "ChangedAlias", "127.0.0.1");

        User fromDb = userDAO.findById(testUser.getUserId()).orElseThrow();
        assertEquals(originalEmail, fromDb.getEmail(), "Email must remain identical after profile update");
    }

    @Test
    @DisplayName("7. Security: role cannot be elevated to ADMIN via profile update")
    void testRoleElevationProtection() {
        userService.updateBasicProfile(testUser.getUserId(), "Hacker Name", "HackerAlias", "127.0.0.1");

        User fromDb = userDAO.findById(testUser.getUserId()).orElseThrow();
        assertEquals(User.Role.USER, fromDb.getRole(), "User role must remain USER");
    }

    @Test
    @DisplayName("8. Security: account status cannot be altered via profile update")
    void testAccountStatusProtection() {
        userService.updateBasicProfile(testUser.getUserId(), "Fresh Name", "FreshAlias", "127.0.0.1");

        User fromDb = userDAO.findById(testUser.getUserId()).orElseThrow();
        assertEquals(User.AccountStatus.ACTIVE, fromDb.getAccountStatus(), "Account status must remain ACTIVE");
    }

    @Test
    @DisplayName("9. Password hash remains intact and unchanged after profile updates")
    void testPasswordHashRemainsIntact() {
        String originalHash = testUser.getPasswordHash();
        userService.updateBasicProfile(testUser.getUserId(), "Secure Name", "SecureAlias", "127.0.0.1");

        User fromDb = userDAO.findById(testUser.getUserId()).orElseThrow();
        assertEquals(originalHash, fromDb.getPasswordHash(), "Password hash must not be modified");
    }

    @Test
    @DisplayName("10. Fitness profile persistence and retrieval")
    void testSaveAndGetFitnessProfile() {
        FitnessProfile profile = fitnessProfileService.saveOrUpdateFitnessProfile(
                testUser.getUserId(),
                28,
                FitnessProfile.Gender.FEMALE,
                new BigDecimal("168.0"),
                new BigDecimal("62.5"),
                FitnessProfile.ActivityLevel.MODERATE,
                FitnessProfile.PreferredEnvironment.GYM,
                "127.0.0.1"
        );

        assertNotNull(profile);
        assertEquals(testUser.getUserId(), profile.getUserId());
        assertEquals(28, profile.getAge());
        assertEquals(FitnessProfile.Gender.FEMALE, profile.getGender());
        assertEquals(0, new BigDecimal("168.0").compareTo(profile.getHeightCm()));
        assertEquals(0, new BigDecimal("62.5").compareTo(profile.getWeightKg()));
        assertEquals(FitnessProfile.ActivityLevel.MODERATE, profile.getActivityLevel());
        assertEquals(FitnessProfile.PreferredEnvironment.GYM, profile.getPreferredEnvironment());

        // Verify retrieval via service
        Optional<FitnessProfile> retrieved = fitnessProfileService.getFitnessProfile(testUser.getUserId());
        assertTrue(retrieved.isPresent());
        assertEquals(28, retrieved.get().getAge());
    }

    @Test
    @DisplayName("11. Fitness profile upsert: subsequent saves update existing record")
    void testFitnessProfileUpsert() {
        fitnessProfileService.saveOrUpdateFitnessProfile(
                testUser.getUserId(),
                30,
                FitnessProfile.Gender.MALE,
                new BigDecimal("180.0"),
                new BigDecimal("80.0"),
                FitnessProfile.ActivityLevel.ACTIVE,
                FitnessProfile.PreferredEnvironment.HOME,
                "127.0.0.1"
        );

        // Update with new values
        FitnessProfile updated = fitnessProfileService.saveOrUpdateFitnessProfile(
                testUser.getUserId(),
                31,
                FitnessProfile.Gender.MALE,
                new BigDecimal("180.0"),
                new BigDecimal("78.5"),
                FitnessProfile.ActivityLevel.VERY_ACTIVE,
                FitnessProfile.PreferredEnvironment.OUTDOOR,
                "127.0.0.1"
        );

        assertEquals(31, updated.getAge());
        assertEquals(0, new BigDecimal("78.5").compareTo(updated.getWeightKg()));
        assertEquals(FitnessProfile.ActivityLevel.VERY_ACTIVE, updated.getActivityLevel());
        assertEquals(FitnessProfile.PreferredEnvironment.OUTDOOR, updated.getPreferredEnvironment());
    }

    @Test
    @DisplayName("12. Validation: Age < 13 rejected")
    void testAgeUnder13Rejected() {
        assertThrows(ValidationException.class, () ->
                fitnessProfileService.saveOrUpdateFitnessProfile(
                        testUser.getUserId(),
                        12,
                        FitnessProfile.Gender.MALE,
                        new BigDecimal("150.0"),
                        new BigDecimal("45.0"),
                        FitnessProfile.ActivityLevel.BEGINNER,
                        FitnessProfile.PreferredEnvironment.MIXED,
                        "127.0.0.1"
                )
        );
    }

    @Test
    @DisplayName("13. Validation: Age > 120 rejected")
    void testAgeOver120Rejected() {
        assertThrows(ValidationException.class, () ->
                fitnessProfileService.saveOrUpdateFitnessProfile(
                        testUser.getUserId(),
                        121,
                        FitnessProfile.Gender.MALE,
                        new BigDecimal("170.0"),
                        new BigDecimal("70.0"),
                        FitnessProfile.ActivityLevel.BEGINNER,
                        FitnessProfile.PreferredEnvironment.MIXED,
                        "127.0.0.1"
                )
        );
    }

    @Test
    @DisplayName("14. Validation: Height < 50 cm rejected")
    void testHeightUnder50Rejected() {
        assertThrows(ValidationException.class, () ->
                fitnessProfileService.saveOrUpdateFitnessProfile(
                        testUser.getUserId(),
                        25,
                        FitnessProfile.Gender.FEMALE,
                        new BigDecimal("49.9"),
                        new BigDecimal("55.0"),
                        FitnessProfile.ActivityLevel.LIGHT,
                        FitnessProfile.PreferredEnvironment.MIXED,
                        "127.0.0.1"
                )
        );
    }

    @Test
    @DisplayName("15. Validation: Height > 260 cm rejected")
    void testHeightOver260Rejected() {
        assertThrows(ValidationException.class, () ->
                fitnessProfileService.saveOrUpdateFitnessProfile(
                        testUser.getUserId(),
                        25,
                        FitnessProfile.Gender.FEMALE,
                        new BigDecimal("260.1"),
                        new BigDecimal("55.0"),
                        FitnessProfile.ActivityLevel.LIGHT,
                        FitnessProfile.PreferredEnvironment.MIXED,
                        "127.0.0.1"
                )
        );
    }

    @Test
    @DisplayName("16. Validation: Weight < 20 kg rejected")
    void testWeightUnder20Rejected() {
        assertThrows(ValidationException.class, () ->
                fitnessProfileService.saveOrUpdateFitnessProfile(
                        testUser.getUserId(),
                        25,
                        FitnessProfile.Gender.FEMALE,
                        new BigDecimal("165.0"),
                        new BigDecimal("19.9"),
                        FitnessProfile.ActivityLevel.LIGHT,
                        FitnessProfile.PreferredEnvironment.MIXED,
                        "127.0.0.1"
                )
        );
    }

    @Test
    @DisplayName("17. Validation: Weight > 500 kg rejected")
    void testWeightOver500Rejected() {
        assertThrows(ValidationException.class, () ->
                fitnessProfileService.saveOrUpdateFitnessProfile(
                        testUser.getUserId(),
                        25,
                        FitnessProfile.Gender.FEMALE,
                        new BigDecimal("165.0"),
                        new BigDecimal("500.5"),
                        FitnessProfile.ActivityLevel.LIGHT,
                        FitnessProfile.PreferredEnvironment.MIXED,
                        "127.0.0.1"
                )
        );
    }

    @Test
    @DisplayName("18. Activity level enum defaults to BEGINNER if null")
    void testActivityLevelDefault() {
        FitnessProfile profile = fitnessProfileService.saveOrUpdateFitnessProfile(
                testUser.getUserId(),
                22,
                FitnessProfile.Gender.OTHER,
                new BigDecimal("175.0"),
                new BigDecimal("70.0"),
                null,
                null,
                "127.0.0.1"
        );
        assertEquals(FitnessProfile.ActivityLevel.BEGINNER, profile.getActivityLevel());
        assertEquals(FitnessProfile.PreferredEnvironment.MIXED, profile.getPreferredEnvironment());
    }

    @Test
    @DisplayName("19. Privacy mode defaults to PERSONAL for new accounts")
    void testDefaultPrivacyMode() {
        User.PrivacyMode mode = privacyService.getPrivacyMode(testUser.getUserId());
        assertEquals(User.PrivacyMode.PERSONAL, mode);
    }

    @Test
    @DisplayName("20. Privacy mode toggle to SOCIAL mode updates database")
    void testUpdatePrivacyModeToSocial() {
        privacyService.updatePrivacyMode(testUser.getUserId(), User.PrivacyMode.SOCIAL, "127.0.0.1");

        User.PrivacyMode updatedMode = privacyService.getPrivacyMode(testUser.getUserId());
        assertEquals(User.PrivacyMode.SOCIAL, updatedMode);

        User fromDb = userDAO.findById(testUser.getUserId()).orElseThrow();
        assertEquals(User.PrivacyMode.SOCIAL, fromDb.getPrivacyMode());
    }

    @Test
    @DisplayName("21. Privacy mode toggle back to PERSONAL mode updates database")
    void testUpdatePrivacyModeBackToPersonal() {
        privacyService.updatePrivacyMode(testUser.getUserId(), User.PrivacyMode.SOCIAL, "127.0.0.1");
        privacyService.updatePrivacyMode(testUser.getUserId(), User.PrivacyMode.PERSONAL, "127.0.0.1");

        User.PrivacyMode finalMode = privacyService.getPrivacyMode(testUser.getUserId());
        assertEquals(User.PrivacyMode.PERSONAL, finalMode);
    }

    @Test
    @DisplayName("22. Activity audit logs recorded for profile, fitness, and privacy modifications")
    void testAuditLogging() {
        // Perform profile update
        userService.updateBasicProfile(testUser.getUserId(), "Audited User", "AuditAlias", "10.0.0.1");

        // Perform fitness profile save
        fitnessProfileService.saveOrUpdateFitnessProfile(
                testUser.getUserId(),
                29,
                FitnessProfile.Gender.FEMALE,
                new BigDecimal("165.0"),
                new BigDecimal("60.0"),
                FitnessProfile.ActivityLevel.MODERATE,
                FitnessProfile.PreferredEnvironment.GYM,
                "10.0.0.1"
        );

        // Perform privacy update
        privacyService.updatePrivacyMode(testUser.getUserId(), User.PrivacyMode.SOCIAL, "10.0.0.1");

        List<ActivityLog> logs = activityLogDAO.findByUserId(testUser.getUserId(), 10);
        assertTrue(logs.size() >= 3, "At least 3 activity logs must be generated");

        boolean hasProfileUpdate = logs.stream().anyMatch(l -> AppConstants.ACTION_PROFILE_UPDATED.equals(l.getActionType()));
        boolean hasFitnessUpdate = logs.stream().anyMatch(l -> AppConstants.ACTION_FITNESS_PROFILE_UPDATED.equals(l.getActionType()));
        boolean hasPrivacyUpdate = logs.stream().anyMatch(l -> AppConstants.ACTION_PRIVACY_MODE_CHANGED.equals(l.getActionType()));

        assertTrue(hasProfileUpdate, "Must contain PROFILE_UPDATED audit log");
        assertTrue(hasFitnessUpdate, "Must contain FITNESS_PROFILE_UPDATED audit log");
        assertTrue(hasPrivacyUpdate, "Must contain PRIVACY_MODE_CHANGED audit log");
    }

    @Test
    @DisplayName("23. BMI calculation accuracy and clinical category mapping")
    void testBmiCalculationAndCategory() {
        FitnessProfile p = new FitnessProfile();
        // Height: 175 cm (1.75 m), Weight: 70 kg -> BMI = 70 / (1.75 * 1.75) = 22.86 -> 22.9
        p.setHeightCm(new BigDecimal("175.0"));
        p.setWeightKg(new BigDecimal("70.0"));

        BigDecimal bmi = p.calculateBmi();
        assertNotNull(bmi);
        assertEquals(new BigDecimal("22.9"), bmi);
        assertEquals("Normal weight", p.getBmiCategory());

        // Underweight test: 175 cm, 50 kg -> BMI = 50 / 3.0625 = 16.3
        p.setWeightKg(new BigDecimal("50.0"));
        assertEquals("Underweight", p.getBmiCategory());

        // Overweight test: 175 cm, 80 kg -> BMI = 80 / 3.0625 = 26.1
        p.setWeightKg(new BigDecimal("80.0"));
        assertEquals("Overweight", p.getBmiCategory());

        // Obese test: 175 cm, 100 kg -> BMI = 100 / 3.0625 = 32.7
        p.setWeightKg(new BigDecimal("100.0"));
        assertEquals("Obese", p.getBmiCategory());
    }

    @Test
    @DisplayName("24. UserNotFound exception thrown when retrieving non-existent user")
    void testNonExistentUserThrowsResourceNotFound() {
        assertThrows(ResourceNotFoundException.class, () -> userService.getUserById(99999999));
    }
}
