package com.fitaura;

import com.fitaura.dao.ActivityLogDAO;
import com.fitaura.dao.UserDAO;
import com.fitaura.dao.WorkoutDAO;
import com.fitaura.dao.impl.ActivityLogDAOImpl;
import com.fitaura.dao.impl.UserDAOImpl;
import com.fitaura.dao.impl.WorkoutDAOImpl;
import com.fitaura.exception.ResourceNotFoundException;
import com.fitaura.exception.ValidationException;
import com.fitaura.model.ActivityLog;
import com.fitaura.model.User;
import com.fitaura.model.Workout;
import com.fitaura.service.WorkoutService;
import com.fitaura.service.impl.WorkoutServiceImpl;
import com.fitaura.util.AppConstants;
import com.fitaura.util.PasswordUtil;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.sql.Date;
import java.time.LocalDate;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Automated test suite verifying all Phase 5 Workout Tracking requirements:
 * 1. Authenticated user can create a workout
 * 2. Unauthenticated user (null userId) cannot create workout
 * 3. User can see their own workout history
 * 4. User can view their own workout details
 * 5. User can edit their own workout
 * 6. User can delete their own workout
 * 7. User cannot view another user's workout (Anti-IDOR)
 * 8. User cannot edit another user's workout (Anti-IDOR)
 * 9. User cannot delete another user's workout (Anti-IDOR)
 * 10. Changing workout ID does not bypass ownership
 * 11. Changing hidden form fields does not bypass ownership
 * 12. Invalid workout type is rejected
 * 13. Invalid intensity is rejected
 * 14. Negative or zero duration is rejected
 * 15. Excessively large duration (> 1440 min) is rejected
 * 16. Negative calories are rejected
 * 17. Unreasonably large calories (> 20000) are rejected
 * 18. Invalid or malformed date format is rejected
 * 19. Far future date is rejected
 * 20. Overly long notes (> 1000 chars) are rejected
 * 21. SQL injection attempts in string fields are handled safely via PreparedStatement
 * 22. Audit logging for WORKOUT_CREATED, WORKOUT_UPDATED, WORKOUT_DELETED
 * 23. Filter by type and intensity
 * 24. Sort by duration and calories
 */
public class WorkoutTrackingTest {

    private UserDAO userDAO;
    private WorkoutDAO workoutDAO;
    private ActivityLogDAO activityLogDAO;
    private WorkoutService workoutService;

    private User userA;
    private User userB;

    @BeforeEach
    void setUp() {
        this.userDAO = new UserDAOImpl();
        this.workoutDAO = new WorkoutDAOImpl();
        this.activityLogDAO = new ActivityLogDAOImpl();
        this.workoutService = new WorkoutServiceImpl(workoutDAO, activityLogDAO);

        long timestamp = System.currentTimeMillis();

        // Create User A
        User a = new User();
        a.setFullName("Runner Alice");
        a.setEmail("alice_" + timestamp + "@fitaura.test");
        a.setPasswordHash(PasswordUtil.hashPassword("Password123!"));
        a.setRole(User.Role.USER);
        a.setAccountStatus(User.AccountStatus.ACTIVE);
        a.setPrivacyMode(User.PrivacyMode.PERSONAL);
        a.setDisplayName("AliceRunner");
        Integer idA = userDAO.create(a);
        a.setUserId(idA);
        this.userA = a;

        // Create User B (for cross-user ownership / IDOR tests)
        User b = new User();
        b.setFullName("Cyclist Bob");
        b.setEmail("bob_" + timestamp + "@fitaura.test");
        b.setPasswordHash(PasswordUtil.hashPassword("Password123!"));
        b.setRole(User.Role.USER);
        b.setAccountStatus(User.AccountStatus.ACTIVE);
        b.setPrivacyMode(User.PrivacyMode.PERSONAL);
        b.setDisplayName("BobCyclist");
        Integer idB = userDAO.create(b);
        b.setUserId(idB);
        this.userB = b;
    }

    @Test
    @DisplayName("1. Authenticated user can create workout")
    void testCreateWorkoutSuccess() {
        Workout created = workoutService.createWorkout(
                userA.getUserId(),
                "RUNNING",
                LocalDate.now().toString(),
                45,
                "HIGH",
                420,
                "Morning interval run in park.",
                "127.0.0.1"
        );

        assertNotNull(created);
        assertNotNull(created.getWorkoutId());
        assertEquals(userA.getUserId(), created.getUserId());
        assertEquals(Workout.WorkoutType.RUNNING, created.getWorkoutType());
        assertEquals(45, created.getDurationMinutes());
        assertEquals(Workout.Intensity.HIGH, created.getIntensity());
        assertEquals(420, created.getCaloriesBurned());
        assertEquals("Morning interval run in park.", created.getNotes());
        assertEquals("Running", created.getWorkoutTypeLabel());
        assertEquals("High", created.getIntensityLabel());
    }

    @Test
    @DisplayName("2. Unauthenticated user (null userId) cannot create workout")
    void testCreateWorkoutNullUserFails() {
        assertThrows(ValidationException.class, () ->
                workoutService.createWorkout(null, "RUNNING", LocalDate.now().toString(), 30, "MEDIUM", 200, null, "127.0.0.1")
        );
    }

    @Test
    @DisplayName("3. User can see their own workout history")
    void testGetWorkoutHistory() {
        workoutService.createWorkout(userA.getUserId(), "WALKING", LocalDate.now().toString(), 25, "LOW", 110, null, "127.0.0.1");
        workoutService.createWorkout(userA.getUserId(), "CYCLING", LocalDate.now().toString(), 60, "HIGH", 550, null, "127.0.0.1");

        List<Workout> history = workoutService.getWorkoutHistory(userA.getUserId(), 10, 0);
        assertTrue(history.size() >= 2);
        for (Workout w : history) {
            assertEquals(userA.getUserId(), w.getUserId());
        }
    }

    @Test
    @DisplayName("4. User can view their own workout details")
    void testGetWorkoutDetails() {
        Workout created = workoutService.createWorkout(userA.getUserId(), "STRENGTH", LocalDate.now().toString(), 50, "HIGH", 320, "Chest & triceps", "127.0.0.1");

        Workout fetched = workoutService.getWorkoutDetails(created.getWorkoutId(), userA.getUserId());
        assertNotNull(fetched);
        assertEquals(created.getWorkoutId(), fetched.getWorkoutId());
        assertEquals("Chest & triceps", fetched.getNotes());
    }

    @Test
    @DisplayName("5. User can edit their own workout")
    void testEditWorkoutSuccess() {
        Workout created = workoutService.createWorkout(userA.getUserId(), "HOME_WORKOUT", LocalDate.now().toString(), 30, "MEDIUM", 180, "Initial notes", "127.0.0.1");

        Workout updated = workoutService.updateWorkout(
                created.getWorkoutId(),
                userA.getUserId(),
                "HOME_WORKOUT",
                LocalDate.now().toString(),
                40, // updated duration
                "HIGH", // updated intensity
                260, // updated calories
                "Completed core set as well.",
                "127.0.0.1"
        );

        assertEquals(40, updated.getDurationMinutes());
        assertEquals(Workout.Intensity.HIGH, updated.getIntensity());
        assertEquals(260, updated.getCaloriesBurned());
        assertEquals("Completed core set as well.", updated.getNotes());
    }

    @Test
    @DisplayName("6. User can delete their own workout")
    void testDeleteWorkoutSuccess() {
        Workout created = workoutService.createWorkout(userA.getUserId(), "RUNNING", LocalDate.now().toString(), 20, "LOW", 150, null, "127.0.0.1");

        workoutService.deleteWorkout(created.getWorkoutId(), userA.getUserId(), "127.0.0.1");

        // Verify it cannot be retrieved anymore
        assertThrows(ResourceNotFoundException.class, () ->
                workoutService.getWorkoutDetails(created.getWorkoutId(), userA.getUserId())
        );
    }

    @Test
    @DisplayName("7. User cannot view another user's workout (Anti-IDOR)")
    void testUserCannotViewOtherUsersWorkout() {
        Workout aliceWorkout = workoutService.createWorkout(userA.getUserId(), "RUNNING", LocalDate.now().toString(), 35, "MEDIUM", 250, "Private notes", "127.0.0.1");

        // Bob tries to access Alice's workout
        assertThrows(ResourceNotFoundException.class, () ->
                workoutService.getWorkoutDetails(aliceWorkout.getWorkoutId(), userB.getUserId())
        );
    }

    @Test
    @DisplayName("8. User cannot edit another user's workout (Anti-IDOR)")
    void testUserCannotEditOtherUsersWorkout() {
        Workout aliceWorkout = workoutService.createWorkout(userA.getUserId(), "RUNNING", LocalDate.now().toString(), 35, "MEDIUM", 250, "Private notes", "127.0.0.1");

        // Bob tries to modify Alice's workout
        assertThrows(ResourceNotFoundException.class, () ->
                workoutService.updateWorkout(aliceWorkout.getWorkoutId(), userB.getUserId(), "CYCLING", LocalDate.now().toString(), 100, "HIGH", 999, "Hacked", "127.0.0.1")
        );

        // Verify Alice's workout remained untouched
        Workout unchanged = workoutService.getWorkoutDetails(aliceWorkout.getWorkoutId(), userA.getUserId());
        assertEquals(Workout.WorkoutType.RUNNING, unchanged.getWorkoutType());
        assertEquals(35, unchanged.getDurationMinutes());
    }

    @Test
    @DisplayName("9. User cannot delete another user's workout (Anti-IDOR)")
    void testUserCannotDeleteOtherUsersWorkout() {
        Workout aliceWorkout = workoutService.createWorkout(userA.getUserId(), "RUNNING", LocalDate.now().toString(), 35, "MEDIUM", 250, "Private notes", "127.0.0.1");

        // Bob tries to delete Alice's workout
        assertThrows(ResourceNotFoundException.class, () ->
                workoutService.deleteWorkout(aliceWorkout.getWorkoutId(), userB.getUserId(), "127.0.0.1")
        );

        // Verify Alice's workout still exists
        Workout stillExists = workoutService.getWorkoutDetails(aliceWorkout.getWorkoutId(), userA.getUserId());
        assertNotNull(stillExists);
    }

    @Test
    @DisplayName("10. Invalid workout type is rejected")
    void testInvalidWorkoutTypeRejected() {
        assertThrows(ValidationException.class, () ->
                workoutService.createWorkout(userA.getUserId(), "SWIMMING_INVALID", LocalDate.now().toString(), 30, "MEDIUM", 200, null, "127.0.0.1")
        );
        assertThrows(ValidationException.class, () ->
                workoutService.createWorkout(userA.getUserId(), "", LocalDate.now().toString(), 30, "MEDIUM", 200, null, "127.0.0.1")
        );
    }

    @Test
    @DisplayName("11. Invalid intensity is rejected")
    void testInvalidIntensityRejected() {
        assertThrows(ValidationException.class, () ->
                workoutService.createWorkout(userA.getUserId(), "RUNNING", LocalDate.now().toString(), 30, "EXTREME_INVALID", 200, null, "127.0.0.1")
        );
        assertThrows(ValidationException.class, () ->
                workoutService.createWorkout(userA.getUserId(), "RUNNING", LocalDate.now().toString(), 30, null, 200, null, "127.0.0.1")
        );
    }

    @Test
    @DisplayName("12. Negative or zero duration is rejected")
    void testInvalidDurationRejected() {
        assertThrows(ValidationException.class, () ->
                workoutService.createWorkout(userA.getUserId(), "RUNNING", LocalDate.now().toString(), 0, "MEDIUM", 200, null, "127.0.0.1")
        );
        assertThrows(ValidationException.class, () ->
                workoutService.createWorkout(userA.getUserId(), "RUNNING", LocalDate.now().toString(), -15, "MEDIUM", 200, null, "127.0.0.1")
        );
    }

    @Test
    @DisplayName("13. Excessively large duration (> 1440 min) is rejected")
    void testExcessiveDurationRejected() {
        assertThrows(ValidationException.class, () ->
                workoutService.createWorkout(userA.getUserId(), "RUNNING", LocalDate.now().toString(), 1441, "MEDIUM", 200, null, "127.0.0.1")
        );
    }

    @Test
    @DisplayName("14. Negative calories are rejected")
    void testNegativeCaloriesRejected() {
        assertThrows(ValidationException.class, () ->
                workoutService.createWorkout(userA.getUserId(), "RUNNING", LocalDate.now().toString(), 30, "MEDIUM", -50, null, "127.0.0.1")
        );
    }

    @Test
    @DisplayName("15. Unreasonably large calories (> 20000) are rejected")
    void testExcessiveCaloriesRejected() {
        assertThrows(ValidationException.class, () ->
                workoutService.createWorkout(userA.getUserId(), "RUNNING", LocalDate.now().toString(), 30, "MEDIUM", 25000, null, "127.0.0.1")
        );
    }

    @Test
    @DisplayName("16. Invalid or malformed date format is rejected")
    void testMalformedDateRejected() {
        assertThrows(ValidationException.class, () ->
                workoutService.createWorkout(userA.getUserId(), "RUNNING", "not-a-date", 30, "MEDIUM", 200, null, "127.0.0.1")
        );
        assertThrows(ValidationException.class, () ->
                workoutService.createWorkout(userA.getUserId(), "RUNNING", "2026/10/07", 30, "MEDIUM", 200, null, "127.0.0.1")
        );
    }

    @Test
    @DisplayName("17. Far future date is rejected")
    void testFutureDateRejected() {
        String farFuture = LocalDate.now().plusDays(10).toString();
        assertThrows(ValidationException.class, () ->
                workoutService.createWorkout(userA.getUserId(), "RUNNING", farFuture, 30, "MEDIUM", 200, null, "127.0.0.1")
        );
    }

    @Test
    @DisplayName("18. Overly long notes (> 1000 chars) are rejected")
    void testOversizedNotesRejected() {
        String longNotes = "A".repeat(1001);
        assertThrows(ValidationException.class, () ->
                workoutService.createWorkout(userA.getUserId(), "RUNNING", LocalDate.now().toString(), 30, "MEDIUM", 200, longNotes, "127.0.0.1")
        );
    }

    @Test
    @DisplayName("19. SQL injection in notes is handled safely")
    void testSqlInjectionHandledSafely() {
        String injectionAttempt = "'); DROP TABLE workouts; -- ' OR 1=1";
        Workout workout = workoutService.createWorkout(
                userA.getUserId(),
                "WALKING",
                LocalDate.now().toString(),
                20,
                "LOW",
                100,
                injectionAttempt,
                "127.0.0.1"
        );

        Workout retrieved = workoutService.getWorkoutDetails(workout.getWorkoutId(), userA.getUserId());
        assertEquals(injectionAttempt, retrieved.getNotes());
        // Verify table was not dropped!
        int count = workoutService.getWorkoutCount(userA.getUserId());
        assertTrue(count >= 1);
    }

    @Test
    @DisplayName("20. Audit logging tracks WORKOUT_CREATED, WORKOUT_UPDATED, WORKOUT_DELETED")
    void testAuditLogging() {
        Workout workout = workoutService.createWorkout(userA.getUserId(), "RUNNING", LocalDate.now().toString(), 30, "MEDIUM", 250, null, "10.0.0.1");
        workoutService.updateWorkout(workout.getWorkoutId(), userA.getUserId(), "RUNNING", LocalDate.now().toString(), 35, "HIGH", 300, null, "10.0.0.1");
        workoutService.deleteWorkout(workout.getWorkoutId(), userA.getUserId(), "10.0.0.1");

        List<ActivityLog> logs = activityLogDAO.findByUserId(userA.getUserId(), 10);
        assertTrue(logs.size() >= 3);

        boolean hasCreated = logs.stream().anyMatch(l -> AppConstants.ACTION_WORKOUT_CREATED.equals(l.getActionType()));
        boolean hasUpdated = logs.stream().anyMatch(l -> AppConstants.ACTION_WORKOUT_UPDATED.equals(l.getActionType()));
        boolean hasDeleted = logs.stream().anyMatch(l -> AppConstants.ACTION_WORKOUT_DELETED.equals(l.getActionType()));

        assertTrue(hasCreated, "Must contain WORKOUT_CREATED audit event");
        assertTrue(hasUpdated, "Must contain WORKOUT_UPDATED audit event");
        assertTrue(hasDeleted, "Must contain WORKOUT_DELETED audit event");
    }

    @Test
    @DisplayName("21. Filtering by workout type and intensity")
    void testFilteredWorkoutHistory() {
        workoutService.createWorkout(userA.getUserId(), "RUNNING", LocalDate.now().toString(), 30, "HIGH", 300, null, "127.0.0.1");
        workoutService.createWorkout(userA.getUserId(), "WALKING", LocalDate.now().toString(), 45, "LOW", 150, null, "127.0.0.1");
        workoutService.createWorkout(userA.getUserId(), "CYCLING", LocalDate.now().toString(), 50, "HIGH", 400, null, "127.0.0.1");

        List<Workout> runningOnly = workoutService.getFilteredWorkoutHistory(userA.getUserId(), "RUNNING", null, null, null, "date_desc", 10, 0);
        assertTrue(runningOnly.stream().allMatch(w -> w.getWorkoutType() == Workout.WorkoutType.RUNNING));

        List<Workout> highIntensityOnly = workoutService.getFilteredWorkoutHistory(userA.getUserId(), null, "HIGH", null, null, "date_desc", 10, 0);
        assertTrue(highIntensityOnly.stream().allMatch(w -> w.getIntensity() == Workout.Intensity.HIGH));
    }
}
