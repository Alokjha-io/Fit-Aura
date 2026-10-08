package com.fitaura;

import com.fitaura.dao.FitnessGoalDAO;
import com.fitaura.dao.FitnessProfileDAO;
import com.fitaura.dao.UserDAO;
import com.fitaura.dao.WorkoutDAO;
import com.fitaura.dao.impl.FitnessGoalDAOImpl;
import com.fitaura.dao.impl.FitnessProfileDAOImpl;
import com.fitaura.dao.impl.UserDAOImpl;
import com.fitaura.dao.impl.WorkoutDAOImpl;
import com.fitaura.exception.AuthenticationException;
import com.fitaura.exception.ResourceNotFoundException;
import com.fitaura.model.FitnessGoal;
import com.fitaura.model.FitnessProfile;
import com.fitaura.model.User;
import com.fitaura.model.Workout;
import com.fitaura.service.AuthenticationService;
import com.fitaura.service.FitnessProfileService;
import com.fitaura.service.GoalService;
import com.fitaura.service.WorkoutService;
import com.fitaura.service.impl.AuthenticationServiceImpl;
import com.fitaura.service.impl.FitnessProfileServiceImpl;
import com.fitaura.service.impl.GoalServiceImpl;
import com.fitaura.service.impl.WorkoutServiceImpl;
import org.junit.jupiter.api.*;

import java.math.BigDecimal;
import java.sql.Date;
import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Test Suite verifying complete User Isolation and Post-Registration Onboarding:
 * 1. User A (Alice) and User B (Bob) have distinct isolated profiles, workouts, and goals.
 * 2. Cross-user access is blocked at the service and DAO levels.
 * 3. Onboarding independently populates physical metrics and initial goals.
 * 4. Password and authentication checks reject unknown and invalid credentials.
 */
@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
public class UserIsolationAndOnboardingTest {

    private static UserDAO userDAO;
    private static FitnessProfileDAO profileDAO;
    private static WorkoutDAO workoutDAO;
    private static FitnessGoalDAO goalDAO;

    private static AuthenticationService authService;
    private static FitnessProfileService profileService;
    private static WorkoutService workoutService;
    private static GoalService goalService;

    private static User userA;
    private static User userB;
    private static FitnessGoal goalA;
    private static Workout workoutA;

    @BeforeAll
    public static void setUp() {
        userDAO = new UserDAOImpl();
        profileDAO = new FitnessProfileDAOImpl();
        workoutDAO = new WorkoutDAOImpl();
        goalDAO = new FitnessGoalDAOImpl();

        authService = new AuthenticationServiceImpl();
        profileService = new FitnessProfileServiceImpl();
        workoutService = new WorkoutServiceImpl();
        goalService = new GoalServiceImpl();

        // 1. Register User A
        String emailA = "alice_iso_" + System.currentTimeMillis() + "@example.com";
        userA = authService.register("Alice Athlete", emailA, "Password123!", "Password123!", "AliceA", "127.0.0.1");

        // 2. Register User B
        String emailB = "bob_iso_" + System.currentTimeMillis() + "@example.com";
        userB = authService.register("Bob Builder", emailB, "Password456!", "Password456!", "BobB", "127.0.0.1");
    }

    @AfterAll
    public static void tearDown() {
        if (userA != null && userA.getUserId() != null) userDAO.delete(userA.getUserId());
        if (userB != null && userB.getUserId() != null) userDAO.delete(userB.getUserId());
    }

    @Test
    @Order(1)
    @DisplayName("1. User IDs are unique and distinct")
    public void testDistinctUserIdentities() {
        assertNotNull(userA.getUserId());
        assertNotNull(userB.getUserId());
        assertNotEquals(userA.getUserId(), userB.getUserId(), "User A and User B must have unique IDs");
    }

    @Test
    @Order(2)
    @DisplayName("2. Authentication rejects invalid credentials and non-existent users")
    public void testAuthenticationRejections() {
        // Non-existent email
        assertThrows(AuthenticationException.class, () ->
                authService.authenticate("nonexistent_" + System.currentTimeMillis() + "@test.com", "Password123!", "127.0.0.1"));

        // Existing email + wrong password
        assertThrows(AuthenticationException.class, () ->
                authService.authenticate(userA.getEmail(), "WrongPassword999!", "127.0.0.1"));

        // Empty credentials
        assertThrows(AuthenticationException.class, () ->
                authService.authenticate("", "", "127.0.0.1"));

        // Correct credentials succeeds
        User authed = authService.authenticate(userA.getEmail(), "Password123!", "127.0.0.1");
        assertEquals(userA.getUserId(), authed.getUserId());
    }

    @Test
    @Order(3)
    @DisplayName("3. Onboarding creates distinct fitness profiles for each user")
    public void testIndependentFitnessProfiles() {
        // Alice onboards: Female, 26 yrs, 165 cm, 58 kg, MODERATE, GYM
        FitnessProfile profileA = profileService.saveOrUpdateFitnessProfile(
                userA.getUserId(), 26, FitnessProfile.Gender.FEMALE,
                new BigDecimal("165.0"), new BigDecimal("58.0"),
                FitnessProfile.ActivityLevel.MODERATE, FitnessProfile.PreferredEnvironment.GYM, "127.0.0.1"
        );

        // Bob onboards: Male, 32 yrs, 182 cm, 84 kg, ACTIVE, HOME
        FitnessProfile profileB = profileService.saveOrUpdateFitnessProfile(
                userB.getUserId(), 32, FitnessProfile.Gender.MALE,
                new BigDecimal("182.0"), new BigDecimal("84.0"),
                FitnessProfile.ActivityLevel.ACTIVE, FitnessProfile.PreferredEnvironment.HOME, "127.0.0.1"
        );

        // Verify Alice's profile
        Optional<FitnessProfile> fetchedA = profileService.getFitnessProfile(userA.getUserId());
        assertTrue(fetchedA.isPresent());
        assertEquals(26, fetchedA.get().getAge());
        assertEquals(0, new BigDecimal("58.0").compareTo(fetchedA.get().getWeightKg()));

        // Verify Bob's profile
        Optional<FitnessProfile> fetchedB = profileService.getFitnessProfile(userB.getUserId());
        assertTrue(fetchedB.isPresent());
        assertEquals(32, fetchedB.get().getAge());
        assertEquals(0, new BigDecimal("84.0").compareTo(fetchedB.get().getWeightKg()));

        // Verify they are completely isolated
        assertNotEquals(fetchedA.get().getProfileId(), fetchedB.get().getProfileId());
    }

    @Test
    @Order(4)
    @DisplayName("4. Workout Isolation: User A's workouts are not visible to User B")
    public void testWorkoutIsolation() {
        // Alice logs a running workout
        Workout w = new Workout();
        w.setUserId(userA.getUserId());
        w.setWorkoutType(Workout.WorkoutType.RUNNING);
        w.setWorkoutDate(Date.valueOf(LocalDate.now()));
        w.setDurationMinutes(45);
        w.setIntensity(Workout.Intensity.HIGH);
        w.setCaloriesBurned(400);
        w.setNotes("Alice's morning 5k interval run");

        workoutA = workoutService.createWorkout(w, "127.0.0.1");
        assertNotNull(workoutA.getWorkoutId());

        // Fetch workouts for Alice
        List<Workout> aliceWorkouts = workoutService.getWorkoutsByUserId(userA.getUserId());
        assertTrue(aliceWorkouts.stream().anyMatch(item -> item.getWorkoutId().equals(workoutA.getWorkoutId())));

        // Fetch workouts for Bob
        List<Workout> bobWorkouts = workoutService.getWorkoutsByUserId(userB.getUserId());
        assertFalse(bobWorkouts.stream().anyMatch(item -> item.getWorkoutId().equals(workoutA.getWorkoutId())),
                "Bob MUST NOT see Alice's workouts");
        assertEquals(0, bobWorkouts.size());
    }

    @Test
    @Order(5)
    @DisplayName("5. Goal Isolation & Ownership Enforcement: User B cannot access or modify User A's goal")
    public void testGoalIsolationAndOwnership() {
        // Alice creates a goal
        goalA = goalService.createGoal(
                userA.getUserId(), "WEIGHT_LOSS", new BigDecimal("55.0"),
                new BigDecimal("58.0"), "kg", LocalDate.now().toString(),
                LocalDate.now().plusMonths(2).toString(), "Alice target", "127.0.0.1"
        );

        // Alice can access her own goal
        FitnessGoal retrieved = goalService.getGoalDetails(goalA.getGoalId(), userA.getUserId());
        assertNotNull(retrieved);
        assertEquals(goalA.getGoalId(), retrieved.getGoalId());

        // Bob attempting to view Alice's goal is rejected
        assertThrows(ResourceNotFoundException.class, () ->
                goalService.getGoalDetails(goalA.getGoalId(), userB.getUserId()),
                "Bob accessing Alice's goal must throw ResourceNotFoundException");

        // Bob attempting to update Alice's goal is rejected
        assertThrows(ResourceNotFoundException.class, () ->
                goalService.updateGoal(goalA.getGoalId(), userB.getUserId(), "WEIGHT_LOSS",
                        new BigDecimal("99.0"), new BigDecimal("58.0"), "kg",
                        LocalDate.now().toString(), null, "Hacked", "127.0.0.1"),
                "Bob updating Alice's goal must be blocked");
    }
}
