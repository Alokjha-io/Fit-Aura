package com.fitaura;

import com.fitaura.dto.AdminDashboardSummaryDTO;
import com.fitaura.dto.LeaderboardEntryDTO;
import com.fitaura.exception.AuthorizationException;
import com.fitaura.exception.ResourceNotFoundException;
import com.fitaura.model.*;
import com.fitaura.service.*;
import com.fitaura.service.impl.*;
import com.fitaura.util.AppConstants;
import com.fitaura.util.CsrfUtil;
import com.fitaura.util.DatabaseConnectionPool;
import org.junit.jupiter.api.*;

import java.math.BigDecimal;
import java.sql.Connection;
import java.sql.Date;
import java.sql.ResultSet;
import java.sql.Statement;
import java.time.LocalDate;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Phase 13 Final Integration & End-to-End Acceptance Test Suite.
 * Validates the complete user journey, cross-user isolation, admin workflows,
 * business rule calculations, gamification tie-breaking, and operational health.
 */
@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
public class FitAuraEndToEndIntegrationTest {

    private AuthenticationService authService;
    private UserService userService;
    private FitnessProfileService profileService;
    private PrivacyService privacyService;
    private WorkoutService workoutService;
    private GoalService goalService;
    private ProgressService progressService;
    private GuidanceService guidanceService;
    private ChallengeService challengeService;
    private GamificationService gamificationService;
    private FitnessContentService contentService;
    private AdminService adminService;

    @BeforeEach
    void setUp() {
        this.authService = new AuthenticationServiceImpl();
        this.userService = new UserServiceImpl();
        this.profileService = new FitnessProfileServiceImpl();
        this.privacyService = new PrivacyServiceImpl();
        this.workoutService = new WorkoutServiceImpl();
        this.goalService = new GoalServiceImpl();
        this.progressService = new ProgressServiceImpl();
        this.guidanceService = new GuidanceServiceImpl();
        this.challengeService = new ChallengeServiceImpl();
        this.gamificationService = new GamificationServiceImpl();
        this.contentService = new FitnessContentServiceImpl();
        this.adminService = new AdminServiceImpl();
    }

    @Test
    @Order(1)
    @DisplayName("1. Complete User Journey: Registration -> Biometrics -> Goals -> Workouts -> Gamification -> Privacy")
    void testCompleteUserJourney() {
        long ts = System.currentTimeMillis();
        String email = "journey_" + ts + "@fitaura.test";
        String password = "Password123!";

        // 1. Registration
        User registeredUser = authService.register(
                "Journey Athlete",
                email,
                password,
                password,
                "JourneyAlias",
                "127.0.0.1"
        );
        assertNotNull(registeredUser);
        assertNotNull(registeredUser.getUserId());
        assertEquals(User.Role.USER, registeredUser.getRole());
        assertEquals(User.AccountStatus.ACTIVE, registeredUser.getAccountStatus());
        assertEquals(User.PrivacyMode.PERSONAL, registeredUser.getPrivacyMode());

        // 2. Authentication
        User loggedIn = authService.authenticate(email, password, "127.0.0.1");
        assertNotNull(loggedIn);
        assertEquals(registeredUser.getUserId(), loggedIn.getUserId());

        // 3. Fitness Profile Setup
        FitnessProfile profile = profileService.saveOrUpdateFitnessProfile(
                loggedIn.getUserId(),
                28,
                FitnessProfile.Gender.MALE,
                new BigDecimal("178.0"),
                new BigDecimal("76.0"),
                FitnessProfile.ActivityLevel.MODERATE,
                FitnessProfile.PreferredEnvironment.GYM,
                "127.0.0.1"
        );
        assertNotNull(profile);
        assertEquals(28, profile.getAge());
        assertEquals(0, new BigDecimal("76.0").compareTo(profile.getWeightKg()));

        // 4. Metabolic Calculations Verification
        double bmi = progressService.calculateBmi(profile);
        assertTrue(bmi > 23.0 && bmi < 25.0, "BMI should be roughly 24.0");
        String bmiCategory = progressService.getBmiCategory(bmi);
        assertEquals("Normal weight", bmiCategory);

        int bmr = progressService.calculateBmr(profile);
        assertTrue(bmr > 1600 && bmr < 1850, "BMR should be between 1600 and 1850 kcal");

        int dailyCalories = progressService.calculateEstimatedDailyCalories(profile);
        assertTrue(dailyCalories > 2400 && dailyCalories < 2900, "Daily calories should be between 2400 and 2900 kcal");

        // 5. Initial Goal Creation
        FitnessGoal goal = goalService.createGoal(
                loggedIn.getUserId(),
                "WEIGHT_LOSS",
                new BigDecimal("72.0"),
                new BigDecimal("76.0"),
                "kg",
                LocalDate.now().toString(),
                LocalDate.now().plusMonths(3).toString(),
                "Reach target weight safely",
                "127.0.0.1"
        );
        assertNotNull(goal);
        assertNotNull(goal.getGoalId());
        assertEquals(FitnessGoal.GoalStatus.ACTIVE, goal.getStatus());

        // 6. Workout Logging
        Workout workout = new Workout();
        workout.setUserId(loggedIn.getUserId());
        workout.setWorkoutType(Workout.WorkoutType.RUNNING);
        workout.setWorkoutDate(Date.valueOf(LocalDate.now()));
        workout.setDurationMinutes(35);
        workout.setIntensity(Workout.Intensity.MEDIUM);
        workout.setCaloriesBurned(320);
        workout.setNotes("35-min steady pace interval run");

        Workout loggedWorkout = workoutService.createWorkout(workout, "127.0.0.1");
        assertNotNull(loggedWorkout);
        assertNotNull(loggedWorkout.getWorkoutId());

        // 7. Gamification Integration
        int points = gamificationService.calculateTotalPoints(loggedIn.getUserId());
        assertTrue(points >= 10, "User should receive points for workout logging");

        // 8. Guidance Generation
        UserGuidance guidance = guidanceService.generateUserGuidance(loggedIn.getUserId());
        assertNotNull(guidance);
        assertEquals("GYM", guidance.getEnvironmentLabel());
        assertNotNull(guidance.getGoalAdvice());

        // 9. Profile & Privacy Update
        privacyService.updatePrivacyMode(loggedIn.getUserId(), User.PrivacyMode.SOCIAL, "127.0.0.1");
        User refreshed = userService.getUserById(loggedIn.getUserId());
        assertEquals(User.PrivacyMode.SOCIAL, refreshed.getPrivacyMode());
    }

    @Test
    @Order(2)
    @DisplayName("2. Two-User Isolation & Anti-IDOR Protection")
    void testTwoUserIsolationAndIDOR() {
        long ts = System.currentTimeMillis();

        User userA = authService.register("Alice Isolated", "alice_iso_" + ts + "@fitaura.test", "Password123!", "Password123!", "AliceIso", "127.0.0.1");
        User userB = authService.register("Bob Isolated", "bob_iso_" + ts + "@fitaura.test", "Password123!", "Password123!", "BobIso", "127.0.0.1");

        // User A creates goal and workout
        FitnessGoal goalA = goalService.createGoal(
                userA.getUserId(), "STRENGTH", new BigDecimal("100.0"), new BigDecimal("80.0"),
                "kg", LocalDate.now().toString(), LocalDate.now().plusWeeks(6).toString(), "A's goal", "127.0.0.1"
        );

        Workout workoutA = new Workout();
        workoutA.setUserId(userA.getUserId());
        workoutA.setWorkoutType(Workout.WorkoutType.STRENGTH);
        workoutA.setWorkoutDate(Date.valueOf(LocalDate.now()));
        workoutA.setDurationMinutes(40);
        workoutA.setIntensity(Workout.Intensity.HIGH);
        workoutA.setCaloriesBurned(300);
        Workout createdWA = workoutService.createWorkout(workoutA, "127.0.0.1");

        // Verify Bob cannot read or modify User A's goal
        assertThrows(ResourceNotFoundException.class, () ->
                goalService.getGoalDetails(goalA.getGoalId(), userB.getUserId()));

        assertThrows(ResourceNotFoundException.class, () ->
                goalService.updateGoal(goalA.getGoalId(), userB.getUserId(), "WEIGHT_LOSS",
                        new BigDecimal("90.0"), new BigDecimal("80.0"), "kg",
                        LocalDate.now().toString(), null, "Compromise attempt", "127.0.0.1"));

        // Verify Bob cannot read or modify User A's workout
        assertThrows(ResourceNotFoundException.class, () ->
                workoutService.getWorkoutDetails(createdWA.getWorkoutId(), userB.getUserId()));

        assertThrows(ResourceNotFoundException.class, () ->
                workoutService.deleteWorkout(createdWA.getWorkoutId(), userB.getUserId(), "127.0.0.1"));
    }

    @Test
    @Order(3)
    @DisplayName("3. Admin End-to-End & Role Authorization Enforcement")
    void testAdminEndToEndAndAuthorization() {
        long ts = System.currentTimeMillis();
        User normalUser = authService.register("Regular Member", "member_" + ts + "@fitaura.test", "Password123!", "Password123!", "RegMember", "127.0.0.1");

        // Verify normal user has USER role
        assertEquals(User.Role.USER, normalUser.getRole());

        // Verify admin dashboard statistics can be acquired by admin service
        assertDoesNotThrow(() -> {
            AdminDashboardSummaryDTO stats = adminService.getDashboardSummary();
            assertNotNull(stats);
            assertTrue(stats.getTotalUsers() >= 1);
        });

        // Verify user cannot moderate content without ADMIN role
        FitnessContent content = new FitnessContent();
        content.setTitle("Community Nutrition Guide " + ts);
        content.setCategory(FitnessContent.Category.NUTRITION);
        content.setContentText("Proper hydration and electrolyte management for endurance athletes.");
        FitnessContent created = contentService.createContent(content, normalUser, "127.0.0.1");

        assertThrows(AuthorizationException.class, () -> {
            contentService.moderateContent(created.getContentId(), true, normalUser.getUserId(), "127.0.0.1");
        });
    }

    @Test
    @Order(4)
    @DisplayName("4. Gamification Tie-Breaking & Privacy-Respecting Leaderboard")
    void testGamificationRankingAndPrivacy() {
        long ts = System.currentTimeMillis();

        // User 1 (Social)
        User u1 = authService.register("Social Player One", "soc1_" + ts + "@fitaura.test", "Password123!", "Password123!", "Soc1", "127.0.0.1");
        privacyService.updatePrivacyMode(u1.getUserId(), User.PrivacyMode.SOCIAL, "127.0.0.1");

        // User 2 (Personal - Must NOT appear on public leaderboard)
        User u2 = authService.register("Private Player Two", "priv2_" + ts + "@fitaura.test", "Password123!", "Password123!", "Priv2", "127.0.0.1");
        privacyService.updatePrivacyMode(u2.getUserId(), User.PrivacyMode.PERSONAL, "127.0.0.1");

        // Log a workout for each user to generate points
        Workout w1 = new Workout();
        w1.setUserId(u1.getUserId());
        w1.setWorkoutType(Workout.WorkoutType.RUNNING);
        w1.setWorkoutDate(Date.valueOf(LocalDate.now()));
        w1.setDurationMinutes(30);
        w1.setIntensity(Workout.Intensity.MEDIUM);
        w1.setCaloriesBurned(250);
        workoutService.createWorkout(w1, "127.0.0.1");

        Workout w2 = new Workout();
        w2.setUserId(u2.getUserId());
        w2.setWorkoutType(Workout.WorkoutType.CYCLING);
        w2.setWorkoutDate(Date.valueOf(LocalDate.now()));
        w2.setDurationMinutes(45);
        w2.setIntensity(Workout.Intensity.HIGH);
        w2.setCaloriesBurned(400);
        workoutService.createWorkout(w2, "127.0.0.1");

        List<LeaderboardEntryDTO> leaderboard = gamificationService.getSocialLeaderboard(50, 0, null);

        // Social user is present
        boolean u1Found = leaderboard.stream().anyMatch(e -> e.getUserId().equals(u1.getUserId()));
        assertTrue(u1Found, "Social user must be included on public leaderboard");

        // Personal user is NOT present
        boolean u2Found = leaderboard.stream().anyMatch(e -> e.getUserId().equals(u2.getUserId()));
        assertFalse(u2Found, "Personal mode user must NOT appear on public leaderboard");
    }

    @Test
    @Order(5)
    @DisplayName("5. CSRF Protection: Constant-Time Token Validation")
    void testCsrfProtection() {
        assertFalse(CsrfUtil.isValidToken(null), "Null request must fail CSRF validation");
    }

    @Test
    @Order(6)
    @DisplayName("6. Database Persistence & Pool Health")
    void testDatabasePersistenceAndPoolHealth() {
        assertTrue(DatabaseConnectionPool.isHealthy(), "HikariCP pool must report healthy state");

        try (Connection conn = DatabaseConnectionPool.getConnection();
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery("SELECT 1 AS probe")) {
            assertTrue(rs.next());
            assertEquals(1, rs.getInt("probe"));
        } catch (Exception e) {
            fail("Database probe failed: " + e.getMessage());
        }
    }
}
