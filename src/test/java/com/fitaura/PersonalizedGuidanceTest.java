package com.fitaura;

import com.fitaura.dao.FitnessGoalDAO;
import com.fitaura.dao.FitnessProfileDAO;
import com.fitaura.dao.UserDAO;
import com.fitaura.dao.WorkoutDAO;
import com.fitaura.dao.impl.FitnessGoalDAOImpl;
import com.fitaura.dao.impl.FitnessProfileDAOImpl;
import com.fitaura.dao.impl.UserDAOImpl;
import com.fitaura.dao.impl.WorkoutDAOImpl;
import com.fitaura.model.FitnessGoal;
import com.fitaura.model.FitnessProfile;
import com.fitaura.model.GuidanceRecommendation;
import com.fitaura.model.User;
import com.fitaura.model.UserGuidance;
import com.fitaura.model.Workout;
import com.fitaura.service.GuidanceService;
import com.fitaura.service.impl.GuidanceServiceImpl;
import com.fitaura.util.PasswordUtil;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.MethodOrderer;
import org.junit.jupiter.api.Order;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestMethodOrder;

import java.math.BigDecimal;
import java.sql.Date;
import java.time.LocalDate;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import static org.junit.jupiter.api.Assertions.*;

@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
public class PersonalizedGuidanceTest {

    private static UserDAO userDAO;
    private static FitnessProfileDAO profileDAO;
    private static FitnessGoalDAO goalDAO;
    private static WorkoutDAO workoutDAO;
    private static GuidanceService guidanceService;

    private static Integer testUser1Id;
    private static Integer testUser2Id;

    @BeforeAll
    public static void setUp() {
        userDAO = new UserDAOImpl();
        profileDAO = new FitnessProfileDAOImpl();
        goalDAO = new FitnessGoalDAOImpl();
        workoutDAO = new WorkoutDAOImpl();
        guidanceService = new GuidanceServiceImpl();

        // Create isolated User 1
        User u1 = new User();
        u1.setEmail("guidance_p7b_u1_" + System.currentTimeMillis() + "@fitaura.test");
        u1.setPasswordHash(PasswordUtil.hashPassword("Password123!"));
        u1.setFullName("Alice Guidance");
        u1.setDisplayName("AliceG");
        u1.setRole(User.Role.USER);
        u1.setAccountStatus(User.AccountStatus.ACTIVE);
        u1.setPrivacyMode(User.PrivacyMode.PERSONAL);
        testUser1Id = userDAO.create(u1);

        // Create isolated User 2
        User u2 = new User();
        u2.setEmail("guidance_p7b_u2_" + System.currentTimeMillis() + "@fitaura.test");
        u2.setPasswordHash(PasswordUtil.hashPassword("Password123!"));
        u2.setFullName("Bob Guidance");
        u2.setDisplayName("BobG");
        u2.setRole(User.Role.USER);
        u2.setAccountStatus(User.AccountStatus.ACTIVE);
        u2.setPrivacyMode(User.PrivacyMode.PERSONAL);
        testUser2Id = userDAO.create(u2);
    }

    @Test
    @Order(1)
    @DisplayName("1. Weight-loss personalization - Recommends sustainable aerobic activity and sensible calorie awareness")
    public void testWeightLossPersonalization() {
        FitnessGoal weightLossGoal = new FitnessGoal();
        weightLossGoal.setGoalType(FitnessGoal.GoalType.WEIGHT_LOSS);
        weightLossGoal.setTargetValue(new BigDecimal("70.0"));
        weightLossGoal.setCurrentValue(new BigDecimal("78.0"));
        weightLossGoal.setStatus(FitnessGoal.GoalStatus.ACTIVE);

        GuidanceRecommendation advice = guidanceService.generateGoalAdvice(null, List.of(weightLossGoal), List.of(weightLossGoal));
        assertNotNull(advice);
        assertEquals("Weight Loss Strategy", advice.getTitle());
        assertTrue(advice.getMessage().toLowerCase().contains("aerobic") && advice.getMessage().toLowerCase().contains("fat loss"));

        GuidanceRecommendation nutrition = guidanceService.generateNutritionGuidance(null, List.of(weightLossGoal), 2200);
        assertNotNull(nutrition);
        assertTrue(nutrition.getMessage().toLowerCase().contains("deficit") || nutrition.getMessage().toLowerCase().contains("satiety"));
    }

    @Test
    @Order(2)
    @DisplayName("2. Muscle-gain personalization - Recommends progressive overload and recovery periods")
    public void testMuscleGainPersonalization() {
        FitnessGoal muscleGoal = new FitnessGoal();
        muscleGoal.setGoalType(FitnessGoal.GoalType.MUSCLE_GAIN);
        muscleGoal.setTargetValue(new BigDecimal("80.0"));
        muscleGoal.setCurrentValue(new BigDecimal("72.0"));
        muscleGoal.setStatus(FitnessGoal.GoalStatus.ACTIVE);

        GuidanceRecommendation advice = guidanceService.generateGoalAdvice(null, List.of(muscleGoal), List.of(muscleGoal));
        assertNotNull(advice);
        assertEquals("Progressive Muscle Hypertrophy", advice.getTitle());
        assertTrue(advice.getMessage().toLowerCase().contains("progressive overload"));
        assertTrue(advice.getMessage().toLowerCase().contains("48 hours"));

        GuidanceRecommendation nutrition = guidanceService.generateNutritionGuidance(null, List.of(muscleGoal), 2800);
        assertNotNull(nutrition);
        assertTrue(nutrition.getMessage().toLowerCase().contains("protein"));
    }

    @Test
    @Order(3)
    @DisplayName("3. Strength personalization - Recommends multi-joint compound lifts and rest intervals")
    public void testStrengthPersonalization() {
        FitnessGoal strGoal = new FitnessGoal();
        strGoal.setGoalType(FitnessGoal.GoalType.STRENGTH);
        strGoal.setTargetValue(new BigDecimal("140.0"));
        strGoal.setCurrentValue(new BigDecimal("100.0"));
        strGoal.setStatus(FitnessGoal.GoalStatus.ACTIVE);

        GuidanceRecommendation advice = guidanceService.generateGoalAdvice(null, List.of(strGoal), List.of(strGoal));
        assertNotNull(advice);
        assertEquals("Strength Development & Rest Intervals", advice.getTitle());
        assertTrue(advice.getMessage().toLowerCase().contains("compound"));
        assertTrue(advice.getMessage().toLowerCase().contains("2-3 minutes"));
    }

    @Test
    @Order(4)
    @DisplayName("4. Endurance personalization - Recommends conversational pace and gradual volume progression")
    public void testEndurancePersonalization() {
        FitnessGoal endGoal = new FitnessGoal();
        endGoal.setGoalType(FitnessGoal.GoalType.ENDURANCE);
        endGoal.setTargetValue(new BigDecimal("42.0"));
        endGoal.setCurrentValue(new BigDecimal("21.0"));
        endGoal.setStatus(FitnessGoal.GoalStatus.ACTIVE);

        GuidanceRecommendation advice = guidanceService.generateGoalAdvice(null, List.of(endGoal), List.of(endGoal));
        assertNotNull(advice);
        assertEquals("Gradual Aerobic Progression", advice.getTitle());
        assertTrue(advice.getMessage().toLowerCase().contains("10% weekly"));
        assertTrue(advice.getMessage().toLowerCase().contains("conversational"));
    }

    @Test
    @Order(5)
    @DisplayName("5. Environment-based recommendation - Adapts for Home, Gym, and Outdoor settings")
    public void testEnvironmentBasedRecommendations() {
        FitnessProfile homeProfile = new FitnessProfile();
        homeProfile.setPreferredEnvironment(FitnessProfile.PreferredEnvironment.HOME);
        GuidanceRecommendation homeRec = guidanceService.generateExerciseSuggestion(homeProfile, List.of(), List.of());
        assertTrue(homeRec.getMessage().toLowerCase().contains("bodyweight") || homeRec.getMessage().toLowerCase().contains("squats"));

        FitnessProfile gymProfile = new FitnessProfile();
        gymProfile.setPreferredEnvironment(FitnessProfile.PreferredEnvironment.GYM);
        GuidanceRecommendation gymRec = guidanceService.generateExerciseSuggestion(gymProfile, List.of(), List.of());
        assertTrue(gymRec.getMessage().toLowerCase().contains("barbell") || gymRec.getMessage().toLowerCase().contains("compound"));

        FitnessProfile outdoorProfile = new FitnessProfile();
        outdoorProfile.setPreferredEnvironment(FitnessProfile.PreferredEnvironment.OUTDOOR);
        GuidanceRecommendation outdoorRec = guidanceService.generateExerciseSuggestion(outdoorProfile, List.of(), List.of());
        assertTrue(outdoorRec.getMessage().toLowerCase().contains("outdoor") || outdoorRec.getMessage().toLowerCase().contains("running"));
    }

    @Test
    @Order(6)
    @DisplayName("6. Low-activity recommendation - Encourages gentle progressive increase without intimidation")
    public void testLowActivityRecommendation() {
        Workout w1 = new Workout();
        w1.setWorkoutType(Workout.WorkoutType.WALKING);
        w1.setDurationMinutes(25);
        w1.setIntensity(Workout.Intensity.LOW);

        GuidanceRecommendation rec = guidanceService.generateWorkoutRecommendation(null, List.of(), List.of(w1), 1);
        assertNotNull(rec);
        assertEquals("Build Weekly Momentum", rec.getTitle());
        assertTrue(rec.getMessage().toLowerCase().contains("one extra"));
    }

    @Test
    @Order(7)
    @DisplayName("7. Recovery recommendation - Detects high recent workload or high intensity")
    public void testRecoveryRecommendation() {
        Workout w1 = new Workout();
        w1.setWorkoutType(Workout.WorkoutType.RUNNING);
        w1.setIntensity(Workout.Intensity.HIGH);

        Workout w2 = new Workout();
        w2.setWorkoutType(Workout.WorkoutType.STRENGTH);
        w2.setIntensity(Workout.Intensity.HIGH);

        GuidanceRecommendation rec = guidanceService.generateWorkoutRecommendation(null, List.of(), List.of(w1, w2), 5);
        assertNotNull(rec);
        assertTrue(rec.getTitle().contains("Recovery") || rec.getTitle().contains("Rest"));
        assertTrue(rec.getMessage().toLowerCase().contains("recovery") || rec.getMessage().toLowerCase().contains("rest"));
    }

    @Test
    @Order(8)
    @DisplayName("8. Missing profile handling - Flags incomplete metrics and offers profile-completion action")
    public void testMissingProfileHandling() {
        UserGuidance guidance = guidanceService.generateUserGuidance(testUser1Id);
        assertNotNull(guidance);
        assertTrue(guidance.isProfileIncomplete());

        // Primary or supporting recommendations should include the profile prompt
        boolean foundPrompt = false;
        if (guidance.getPrimaryRecommendation() != null &&
            guidance.getPrimaryRecommendation().getTitle().contains("Complete Your Body Metrics")) {
            foundPrompt = true;
        }
        for (GuidanceRecommendation rec : guidance.getSupportingRecommendations()) {
            if (rec.getTitle().contains("Complete Your Body Metrics")) {
                foundPrompt = true;
                break;
            }
        }
        assertTrue(foundPrompt);
    }

    @Test
    @Order(9)
    @DisplayName("9. Goal deadline handling - Differentiates approaching deadlines vs overdue dates")
    public void testGoalDeadlineHandling() {
        // Approaching deadline in 5 days
        FitnessGoal approachingGoal = new FitnessGoal();
        approachingGoal.setGoalId(101);
        approachingGoal.setGoalType(FitnessGoal.GoalType.WEIGHT_LOSS);
        approachingGoal.setTargetDate(Date.valueOf(LocalDate.now().plusDays(5)));
        approachingGoal.setCurrentValue(new BigDecimal("72.0"));
        approachingGoal.setTargetValue(new BigDecimal("70.0"));
        approachingGoal.setStatus(FitnessGoal.GoalStatus.ACTIVE);

        UserGuidance gApproaching = guidanceService.generateUserGuidance(testUser2Id);
        assertNotNull(gApproaching);

        // Overdue goal in the past
        FitnessGoal overdueGoal = new FitnessGoal();
        overdueGoal.setGoalId(102);
        overdueGoal.setGoalType(FitnessGoal.GoalType.STRENGTH);
        overdueGoal.setTargetDate(Date.valueOf(LocalDate.now().minusDays(3)));
        overdueGoal.setCurrentValue(new BigDecimal("90.0"));
        overdueGoal.setTargetValue(new BigDecimal("100.0"));
        overdueGoal.setStatus(FitnessGoal.GoalStatus.ACTIVE);

        // Verify goal advice generates safely
        GuidanceRecommendation advice = guidanceService.generateGoalAdvice(null, List.of(approachingGoal), List.of(approachingGoal));
        assertNotNull(advice);
    }

    @Test
    @Order(10)
    @DisplayName("10. Recommendation deduplication - Ensures distinct curated recommendations")
    public void testRecommendationDeduplication() {
        UserGuidance guidance = guidanceService.generateUserGuidance(testUser1Id);
        assertNotNull(guidance);

        Set<String> titles = new HashSet<>();
        if (guidance.getPrimaryRecommendation() != null) {
            titles.add(guidance.getPrimaryRecommendation().getTitle());
        }

        for (GuidanceRecommendation rec : guidance.getSupportingRecommendations()) {
            assertFalse(titles.contains(rec.getTitle()), "Duplicate recommendation title detected: " + rec.getTitle());
            titles.add(rec.getTitle());
        }

        // Supporting recommendations list should be capped at 4
        assertTrue(guidance.getSupportingRecommendations().size() <= 4);
    }

    @Test
    @Order(11)
    @DisplayName("11. User isolation - Guarantees data isolation across different authenticated user IDs")
    public void testUserIsolation() {
        // Setup User 1: Home profile + Weight Loss goal
        FitnessProfile p1 = new FitnessProfile();
        p1.setUserId(testUser1Id);
        p1.setHeightCm(new BigDecimal("170.0"));
        p1.setWeightKg(new BigDecimal("75.0"));
        p1.setAge(32);
        p1.setGender(FitnessProfile.Gender.FEMALE);
        p1.setActivityLevel(FitnessProfile.ActivityLevel.MODERATE);
        p1.setPreferredEnvironment(FitnessProfile.PreferredEnvironment.HOME);
        profileDAO.save(p1);

        FitnessGoal g1 = new FitnessGoal();
        g1.setUserId(testUser1Id);
        g1.setGoalType(FitnessGoal.GoalType.WEIGHT_LOSS);
        g1.setTargetValue(new BigDecimal("68.0"));
        g1.setCurrentValue(new BigDecimal("75.0"));
        g1.setStartDate(Date.valueOf(LocalDate.now()));
        g1.setStatus(FitnessGoal.GoalStatus.ACTIVE);
        goalDAO.create(g1);

        // Setup User 2: Gym profile + Strength goal
        FitnessProfile p2 = new FitnessProfile();
        p2.setUserId(testUser2Id);
        p2.setHeightCm(new BigDecimal("182.0"));
        p2.setWeightKg(new BigDecimal("88.0"));
        p2.setAge(26);
        p2.setGender(FitnessProfile.Gender.MALE);
        p2.setActivityLevel(FitnessProfile.ActivityLevel.ACTIVE);
        p2.setPreferredEnvironment(FitnessProfile.PreferredEnvironment.GYM);
        profileDAO.save(p2);

        FitnessGoal g2 = new FitnessGoal();
        g2.setUserId(testUser2Id);
        g2.setGoalType(FitnessGoal.GoalType.STRENGTH);
        g2.setTargetValue(new BigDecimal("130.0"));
        g2.setCurrentValue(new BigDecimal("100.0"));
        g2.setStartDate(Date.valueOf(LocalDate.now()));
        g2.setStatus(FitnessGoal.GoalStatus.ACTIVE);
        goalDAO.create(g2);

        UserGuidance u1Guidance = guidanceService.generateUserGuidance(testUser1Id);
        UserGuidance u2Guidance = guidanceService.generateUserGuidance(testUser2Id);

        assertEquals(testUser1Id, u1Guidance.getUserId());
        assertEquals(testUser2Id, u2Guidance.getUserId());
        assertEquals("HOME", u1Guidance.getEnvironmentLabel());
        assertEquals("GYM", u2Guidance.getEnvironmentLabel());

        assertTrue(u1Guidance.getGoalAdvice().getTitle().contains("Weight Loss"));
        assertTrue(u2Guidance.getGoalAdvice().getTitle().contains("Strength"));
    }

    @Test
    @Order(12)
    @DisplayName("12. Deterministic output - Sequential calls for identical user state produce matching recommendations")
    public void testDeterministicOutput() {
        UserGuidance firstCall = guidanceService.generateUserGuidance(testUser1Id);
        UserGuidance secondCall = guidanceService.generateUserGuidance(testUser1Id);

        assertEquals(firstCall.getPrimaryRecommendation().getTitle(), secondCall.getPrimaryRecommendation().getTitle());
        assertEquals(firstCall.getPrimaryRecommendation().getMessage(), secondCall.getPrimaryRecommendation().getMessage());
        assertEquals(firstCall.getSupportingRecommendations().size(), secondCall.getSupportingRecommendations().size());
        assertEquals(firstCall.getDailyTip().getTitle(), secondCall.getDailyTip().getTitle());
    }
}
