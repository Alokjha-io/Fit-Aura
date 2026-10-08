package com.fitaura;

import com.fitaura.dao.FitnessGoalDAO;
import com.fitaura.dao.FitnessProfileDAO;
import com.fitaura.dao.ProgressDAO;
import com.fitaura.dao.UserDAO;
import com.fitaura.dao.WorkoutDAO;
import com.fitaura.dao.impl.FitnessGoalDAOImpl;
import com.fitaura.dao.impl.FitnessProfileDAOImpl;
import com.fitaura.dao.impl.ProgressDAOImpl;
import com.fitaura.dao.impl.UserDAOImpl;
import com.fitaura.dao.impl.WorkoutDAOImpl;
import com.fitaura.exception.ResourceNotFoundException;
import com.fitaura.exception.ValidationException;
import com.fitaura.model.FitnessGoal;
import com.fitaura.model.FitnessProfile;
import com.fitaura.model.User;
import com.fitaura.model.Workout;
import com.fitaura.service.GoalService;
import com.fitaura.service.ProgressService;
import com.fitaura.service.impl.GoalServiceImpl;
import com.fitaura.service.impl.ProgressServiceImpl;
import com.fitaura.util.DatabaseConnectionPool;
import com.fitaura.util.PasswordUtil;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.MethodOrderer;
import org.junit.jupiter.api.Order;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestMethodOrder;

import java.math.BigDecimal;
import java.sql.Connection;
import java.sql.Date;
import java.time.DayOfWeek;
import java.time.LocalDate;
import java.time.temporal.TemporalAdjusters;
import java.util.List;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.*;

@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
public class FitnessGoalsAndProgressTest {

    private static UserDAO userDAO;
    private static FitnessGoalDAO goalDAO;
    private static FitnessProfileDAO profileDAO;
    private static WorkoutDAO workoutDAO;
    private static ProgressDAO progressDAO;
    private static GoalService goalService;
    private static ProgressService progressService;

    private static Integer testUser1Id;
    private static Integer testUser2Id;

    @BeforeAll
    public static void setUp() {
        userDAO = new UserDAOImpl();
        goalDAO = new FitnessGoalDAOImpl();
        profileDAO = new FitnessProfileDAOImpl();
        workoutDAO = new WorkoutDAOImpl();
        progressDAO = new ProgressDAOImpl();
        goalService = new GoalServiceImpl();
        progressService = new ProgressServiceImpl();

        // Create isolated test user 1
        User user1 = new User();
        user1.setEmail("goal_user1_" + System.currentTimeMillis() + "@fitaura.test");
        user1.setPasswordHash(PasswordUtil.hashPassword("Password123!"));
        user1.setFullName("Goal Tester One");
        user1.setDisplayName("GoalTester1");
        user1.setRole(User.Role.USER);
        user1.setAccountStatus(User.AccountStatus.ACTIVE);
        user1.setPrivacyMode(User.PrivacyMode.PERSONAL);
        testUser1Id = userDAO.create(user1);

        // Create isolated test user 2 (for Anti-IDOR testing)
        User user2 = new User();
        user2.setEmail("goal_user2_" + System.currentTimeMillis() + "@fitaura.test");
        user2.setPasswordHash(PasswordUtil.hashPassword("Password123!"));
        user2.setFullName("Goal Tester Two");
        user2.setDisplayName("GoalTester2");
        user2.setRole(User.Role.USER);
        user2.setAccountStatus(User.AccountStatus.ACTIVE);
        user2.setPrivacyMode(User.PrivacyMode.PERSONAL);
        testUser2Id = userDAO.create(user2);
    }

    @Test
    @Order(1)
    @DisplayName("Create goal - All valid goal types succeed")
    public void testCreateGoalAllValidTypes() {
        FitnessGoal.GoalType[] types = FitnessGoal.GoalType.values();
        for (FitnessGoal.GoalType type : types) {
            FitnessGoal goal = goalService.createGoal(
                    testUser1Id,
                    type.name(),
                    new BigDecimal("75.0"),
                    new BigDecimal("82.0"),
                    "kg",
                    LocalDate.now().toString(),
                    LocalDate.now().plusMonths(3).toString(),
                    "Testing " + type.name(),
                    "127.0.0.1"
            );
            assertNotNull(goal.getGoalId());
            assertEquals(type, goal.getGoalType());
            assertEquals(FitnessGoal.GoalStatus.ACTIVE, goal.getStatus());
        }
    }

    @Test
    @Order(2)
    @DisplayName("Create goal validation - Null or invalid goal type rejected")
    public void testCreateGoalInvalidType() {
        assertThrows(ValidationException.class, () -> {
            goalService.createGoal(testUser1Id, "", new BigDecimal("70"), new BigDecimal("80"),
                    "kg", "2026-01-01", null, null, "127.0.0.1");
        });

        assertThrows(ValidationException.class, () -> {
            goalService.createGoal(testUser1Id, "INVALID_TYPE", new BigDecimal("70"), new BigDecimal("80"),
                    "kg", "2026-01-01", null, null, "127.0.0.1");
        });
    }

    @Test
    @Order(3)
    @DisplayName("Create goal validation - Non-positive target value rejected")
    public void testCreateGoalInvalidTargetValue() {
        assertThrows(ValidationException.class, () -> {
            goalService.createGoal(testUser1Id, "WEIGHT_LOSS", new BigDecimal("0"), new BigDecimal("80"),
                    "kg", "2026-01-01", null, null, "127.0.0.1");
        });

        assertThrows(ValidationException.class, () -> {
            goalService.createGoal(testUser1Id, "WEIGHT_LOSS", new BigDecimal("-10"), new BigDecimal("80"),
                    "kg", "2026-01-01", null, null, "127.0.0.1");
        });
    }

    @Test
    @Order(4)
    @DisplayName("Create goal validation - Target date before start date rejected")
    public void testCreateGoalInvalidDates() {
        assertThrows(ValidationException.class, () -> {
            goalService.createGoal(testUser1Id, "WEIGHT_LOSS", new BigDecimal("70"), new BigDecimal("80"),
                    "kg", "2026-06-01", "2026-05-01", null, "127.0.0.1");
        });
    }

    @Test
    @Order(5)
    @DisplayName("Anti-IDOR Security - User 2 cannot access or modify User 1's goal")
    public void testGoalAntiIDORProtection() {
        FitnessGoal u1Goal = goalService.createGoal(
                testUser1Id,
                "WEIGHT_LOSS",
                new BigDecimal("68.0"),
                new BigDecimal("75.0"),
                "kg",
                LocalDate.now().toString(),
                null,
                "Private goal",
                "127.0.0.1"
        );

        // User 2 cannot read details
        assertThrows(ResourceNotFoundException.class, () -> {
            goalService.getGoalDetails(u1Goal.getGoalId(), testUser2Id);
        });

        // User 2 cannot update
        assertThrows(ResourceNotFoundException.class, () -> {
            goalService.updateGoal(u1Goal.getGoalId(), testUser2Id, "WEIGHT_LOSS",
                    new BigDecimal("65.0"), new BigDecimal("75.0"), "kg",
                    LocalDate.now().toString(), null, "Hacked", "127.0.0.1");
        });

        // User 2 cannot pause/resume
        assertThrows(ResourceNotFoundException.class, () -> {
            goalService.updateGoalStatus(u1Goal.getGoalId(), testUser2Id, FitnessGoal.GoalStatus.PAUSED, "127.0.0.1");
        });

        // User 2 cannot delete
        assertThrows(ResourceNotFoundException.class, () -> {
            goalService.deleteGoal(u1Goal.getGoalId(), testUser2Id, "127.0.0.1");
        });

        // Ensure goal remains intact
        FitnessGoal verified = goalService.getGoalDetails(u1Goal.getGoalId(), testUser1Id);
        assertEquals(FitnessGoal.GoalStatus.ACTIVE, verified.getStatus());
        assertEquals("Private goal", verified.getNotes());
    }

    @Test
    @Order(6)
    @DisplayName("Status Lifecycle State Machine - Pause, Resume, Complete, Cancel")
    public void testGoalStatusLifecycle() {
        FitnessGoal goal = goalService.createGoal(
                testUser1Id,
                "STRENGTH",
                new BigDecimal("100.0"),
                new BigDecimal("80.0"),
                "kg",
                LocalDate.now().toString(),
                null,
                "Bench press 100kg",
                "127.0.0.1"
        );

        // ACTIVE -> PAUSED
        goalService.updateGoalStatus(goal.getGoalId(), testUser1Id, FitnessGoal.GoalStatus.PAUSED, "127.0.0.1");
        FitnessGoal paused = goalService.getGoalDetails(goal.getGoalId(), testUser1Id);
        assertEquals(FitnessGoal.GoalStatus.PAUSED, paused.getStatus());

        // PAUSED -> Cannot complete directly without resuming
        assertThrows(ValidationException.class, () -> {
            goalService.updateGoalStatus(goal.getGoalId(), testUser1Id, FitnessGoal.GoalStatus.COMPLETED, "127.0.0.1");
        });

        // PAUSED -> ACTIVE (Resume)
        goalService.updateGoalStatus(goal.getGoalId(), testUser1Id, FitnessGoal.GoalStatus.ACTIVE, "127.0.0.1");
        FitnessGoal resumed = goalService.getGoalDetails(goal.getGoalId(), testUser1Id);
        assertEquals(FitnessGoal.GoalStatus.ACTIVE, resumed.getStatus());

        // ACTIVE -> COMPLETED
        goalService.updateGoalStatus(goal.getGoalId(), testUser1Id, FitnessGoal.GoalStatus.COMPLETED, "127.0.0.1");
        FitnessGoal completed = goalService.getGoalDetails(goal.getGoalId(), testUser1Id);
        assertEquals(FitnessGoal.GoalStatus.COMPLETED, completed.getStatus());

        // COMPLETED -> Cannot change status anymore
        assertThrows(ValidationException.class, () -> {
            goalService.updateGoalStatus(goal.getGoalId(), testUser1Id, FitnessGoal.GoalStatus.ACTIVE, "127.0.0.1");
        });
    }

    @Test
    @Order(7)
    @DisplayName("Goal Progress Percentage Calculation")
    public void testGoalProgressPercentage() {
        FitnessGoal g1 = new FitnessGoal();
        g1.setGoalType(FitnessGoal.GoalType.WEIGHT_LOSS);
        g1.setTargetValue(new BigDecimal("70.0"));
        g1.setCurrentValue(new BigDecimal("70.0")); // Reached target
        assertEquals(100, g1.getProgressPercentage());

        FitnessGoal g2 = new FitnessGoal();
        g2.setGoalType(FitnessGoal.GoalType.WEIGHT_LOSS);
        g2.setTargetValue(new BigDecimal("70.0"));
        g2.setCurrentValue(new BigDecimal("68.0")); // Exceeded target weight loss
        assertEquals(100, g2.getProgressPercentage());

        FitnessGoal g3 = new FitnessGoal();
        g3.setGoalType(FitnessGoal.GoalType.STRENGTH);
        g3.setTargetValue(new BigDecimal("100.0"));
        g3.setCurrentValue(new BigDecimal("50.0")); // 50%
        assertEquals(50, g3.getProgressPercentage());

        FitnessGoal g4 = new FitnessGoal();
        g4.setStatus(FitnessGoal.GoalStatus.COMPLETED);
        assertEquals(100, g4.getProgressPercentage());
    }

    @Test
    @Order(8)
    @DisplayName("Health Calculations - BMI, BMR, and Daily Maintenance Calories")
    public void testHealthCalculations() {
        FitnessProfile profile = new FitnessProfile();
        profile.setHeightCm(new BigDecimal("178.0"));
        profile.setWeightKg(new BigDecimal("74.0"));
        profile.setAge(28);
        profile.setGender(FitnessProfile.Gender.MALE);
        profile.setActivityLevel(FitnessProfile.ActivityLevel.MODERATE);

        // BMI = 74 / (1.78^2) = 23.35 -> 23.4
        Double bmi = progressService.calculateBmi(profile);
        assertNotNull(bmi);
        assertEquals(23.4, bmi, 0.1);
        assertEquals("Normal weight", progressService.getBmiCategory(bmi));

        // BMR (Mifflin-St Jeor): 10*74 + 6.25*178 - 5*28 + 5 = 740 + 1112.5 - 140 + 5 = 1717.5 -> 1718
        Integer bmr = progressService.calculateBmr(profile);
        assertNotNull(bmr);
        assertTrue(bmr >= 1710 && bmr <= 1725);

        // Daily Calories = BMR * 1.55 (Moderate) = ~2660
        Integer calories = progressService.calculateEstimatedDailyCalories(profile);
        assertNotNull(calories);
        assertTrue(calories >= 2600 && calories <= 2700);
    }

    @Test
    @Order(9)
    @DisplayName("Progress Summary - Aggregates real workouts and weekly activity")
    public void testProgressSummaryAggregation() {
        // Create workouts for User 1
        LocalDate today = LocalDate.now();
        LocalDate weekStart = today.with(TemporalAdjusters.previousOrSame(DayOfWeek.MONDAY));

        Workout w1 = new Workout();
        w1.setUserId(testUser1Id);
        w1.setWorkoutType(Workout.WorkoutType.RUNNING);
        w1.setWorkoutDate(Date.valueOf(weekStart));
        w1.setDurationMinutes(40);
        w1.setIntensity(Workout.Intensity.HIGH);
        w1.setCaloriesBurned(400);
        workoutDAO.create(w1);

        Workout w2 = new Workout();
        w2.setUserId(testUser1Id);
        w2.setWorkoutType(Workout.WorkoutType.STRENGTH);
        w2.setWorkoutDate(Date.valueOf(weekStart.plusDays(1)));
        w2.setDurationMinutes(50);
        w2.setIntensity(Workout.Intensity.MEDIUM);
        w2.setCaloriesBurned(350);
        workoutDAO.create(w2);

        // Get progress summary
        ProgressService.ProgressSummary summary = progressService.getUserProgressSummary(testUser1Id);
        assertNotNull(summary);
        assertTrue(summary.getTotalWorkouts() >= 2);
        assertTrue(summary.getTotalDurationMinutes() >= 90);
        assertTrue(summary.getTotalCaloriesBurned() >= 750);
        assertTrue(summary.getWeeklyWorkouts() >= 2);

        Map<String, ProgressDAO.WorkoutTypeStat> breakdown = summary.getTypeBreakdown();
        assertNotNull(breakdown);
        assertTrue(breakdown.containsKey("RUNNING"));
        assertTrue(breakdown.containsKey("STRENGTH"));
    }
}
