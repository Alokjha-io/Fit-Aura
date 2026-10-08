package com.fitaura;

import com.fitaura.dao.ActivityLogDAO;
import com.fitaura.dao.ChallengeDAO;
import com.fitaura.dao.ChallengeParticipantDAO;
import com.fitaura.dao.UserDAO;
import com.fitaura.dao.WorkoutDAO;
import com.fitaura.dao.impl.ActivityLogDAOImpl;
import com.fitaura.dao.impl.ChallengeDAOImpl;
import com.fitaura.dao.impl.ChallengeParticipantDAOImpl;
import com.fitaura.dao.impl.UserDAOImpl;
import com.fitaura.dao.impl.WorkoutDAOImpl;
import com.fitaura.dto.ChallengeDetailDTO;
import com.fitaura.dto.ChallengeSummaryDTO;
import com.fitaura.dto.ParticipantSummaryDTO;
import com.fitaura.exception.ValidationException;
import com.fitaura.model.Challenge;
import com.fitaura.model.ChallengeParticipant;
import com.fitaura.model.User;
import com.fitaura.model.Workout;
import com.fitaura.service.ChallengeService;
import com.fitaura.service.impl.ChallengeServiceImpl;
import com.fitaura.util.PasswordUtil;
import org.junit.jupiter.api.*;

import java.math.BigDecimal;
import java.sql.Date;
import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;

@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
public class ChallengesAndCommunityTest {

    private static UserDAO userDAO;
    private static ChallengeDAO challengeDAO;
    private static ChallengeParticipantDAO participantDAO;
    private static WorkoutDAO workoutDAO;
    private static ActivityLogDAO activityLogDAO;
    private static ChallengeService challengeService;

    private static User testUser1;
    private static User testUser2;
    private static User adminUser;

    private static Integer userCreatedChallengeId;
    private static Integer adminActiveChallengeId;

    @BeforeAll
    public static void setUp() {
        userDAO = new UserDAOImpl();
        challengeDAO = new ChallengeDAOImpl();
        participantDAO = new ChallengeParticipantDAOImpl();
        workoutDAO = new WorkoutDAOImpl();
        activityLogDAO = new ActivityLogDAOImpl();
        challengeService = new ChallengeServiceImpl();

        // Create standard User 1 (PERSONAL privacy)
        testUser1 = new User();
        testUser1.setEmail("chal_user1_" + System.currentTimeMillis() + "@fitaura.test");
        testUser1.setPasswordHash(PasswordUtil.hashPassword("Password123!"));
        testUser1.setFullName("Alice Runner");
        testUser1.setDisplayName("AliceR");
        testUser1.setRole(User.Role.USER);
        testUser1.setAccountStatus(User.AccountStatus.ACTIVE);
        testUser1.setPrivacyMode(User.PrivacyMode.PERSONAL);
        Integer u1Id = userDAO.create(testUser1);
        testUser1.setUserId(u1Id);

        // Create standard User 2 (SOCIAL privacy)
        testUser2 = new User();
        testUser2.setEmail("chal_user2_" + System.currentTimeMillis() + "@fitaura.test");
        testUser2.setPasswordHash(PasswordUtil.hashPassword("Password123!"));
        testUser2.setFullName("Bob Cyclist");
        testUser2.setDisplayName("BobSpeed");
        testUser2.setRole(User.Role.USER);
        testUser2.setAccountStatus(User.AccountStatus.ACTIVE);
        testUser2.setPrivacyMode(User.PrivacyMode.SOCIAL);
        Integer u2Id = userDAO.create(testUser2);
        testUser2.setUserId(u2Id);

        // Create Admin User
        adminUser = new User();
        adminUser.setEmail("chal_admin_" + System.currentTimeMillis() + "@fitaura.test");
        adminUser.setPasswordHash(PasswordUtil.hashPassword("AdminPass123!"));
        adminUser.setFullName("Platform Admin");
        adminUser.setDisplayName("MasterAdmin");
        adminUser.setRole(User.Role.ADMIN);
        adminUser.setAccountStatus(User.AccountStatus.ACTIVE);
        adminUser.setPrivacyMode(User.PrivacyMode.PERSONAL);
        Integer aId = userDAO.create(adminUser);
        adminUser.setUserId(aId);
    }

    @Test
    @Order(1)
    @DisplayName("1. User Challenge Creation - Enforces DRAFT moderation status and field validations")
    public void testUserChallengeCreation() {
        Challenge c = new Challenge();
        c.setName("Spring 500-Minute Cardio Sprint");
        c.setDescription("Complete 500 minutes of cardio in 30 days.");
        c.setGoalValue(new BigDecimal("500"));
        c.setGoalUnit("MINUTES");
        c.setPointsReward(100);
        c.setStartDate(Date.valueOf(LocalDate.now()));
        c.setEndDate(Date.valueOf(LocalDate.now().plusDays(30)));

        Challenge created = challengeService.createChallenge(c, testUser1);
        assertNotNull(created.getChallengeId());
        userCreatedChallengeId = created.getChallengeId();

        // Must be DRAFT for user-created challenges
        assertEquals(Challenge.ChallengeStatus.DRAFT, created.getStatus());
        assertEquals(testUser1.getUserId(), created.getCreatedBy());

        // Validate invalid creation rules
        Challenge invalidName = new Challenge();
        invalidName.setName("Hi");
        invalidName.setGoalValue(new BigDecimal("100"));
        invalidName.setGoalUnit("MINUTES");
        invalidName.setStartDate(Date.valueOf(LocalDate.now()));
        invalidName.setEndDate(Date.valueOf(LocalDate.now().plusDays(10)));
        assertThrows(ValidationException.class, () -> challengeService.createChallenge(invalidName, testUser1));

        Challenge invalidGoal = new Challenge();
        invalidGoal.setName("Valid Name Test");
        invalidGoal.setGoalValue(new BigDecimal("-10"));
        invalidGoal.setGoalUnit("MINUTES");
        invalidGoal.setStartDate(Date.valueOf(LocalDate.now()));
        invalidGoal.setEndDate(Date.valueOf(LocalDate.now().plusDays(10)));
        assertThrows(ValidationException.class, () -> challengeService.createChallenge(invalidGoal, testUser1));

        Challenge invalidDates = new Challenge();
        invalidDates.setName("Valid Name Test");
        invalidDates.setGoalValue(new BigDecimal("100"));
        invalidDates.setGoalUnit("MINUTES");
        invalidDates.setStartDate(Date.valueOf(LocalDate.now().plusDays(10)));
        invalidDates.setEndDate(Date.valueOf(LocalDate.now()));
        assertThrows(ValidationException.class, () -> challengeService.createChallenge(invalidDates, testUser1));
    }

    @Test
    @Order(2)
    @DisplayName("2. Admin Challenge Creation - Admin can publish ACTIVE challenges directly")
    public void testAdminChallengeCreation() {
        Challenge adminChallenge = new Challenge();
        adminChallenge.setName("Official 3000 Calorie Burn Week");
        adminChallenge.setDescription("Burn 3000 kcal through any combination of logged workouts.");
        adminChallenge.setGoalValue(new BigDecimal("3000"));
        adminChallenge.setGoalUnit("CALORIES");
        adminChallenge.setPointsReward(150);
        adminChallenge.setStartDate(Date.valueOf(LocalDate.now().minusDays(2)));
        adminChallenge.setEndDate(Date.valueOf(LocalDate.now().plusDays(14)));

        Challenge created = challengeService.createChallenge(adminChallenge, adminUser);
        assertNotNull(created.getChallengeId());
        adminActiveChallengeId = created.getChallengeId();

        // Admin-created challenge is immediately ACTIVE
        assertEquals(Challenge.ChallengeStatus.ACTIVE, created.getStatus());
    }

    @Test
    @Order(3)
    @DisplayName("3. Moderation Authorization - Users cannot approve, Admin can approve DRAFT challenge")
    public void testModerationWorkflow() {
        // User cannot moderate
        assertThrows(ValidationException.class, () ->
                challengeService.moderateChallenge(userCreatedChallengeId, "APPROVE", testUser1));

        // Admin approves user-created challenge
        boolean approved = challengeService.moderateChallenge(userCreatedChallengeId, "APPROVE", adminUser);
        assertTrue(approved);

        Optional<Challenge> updated = challengeDAO.findById(userCreatedChallengeId);
        assertTrue(updated.isPresent());
        assertEquals(Challenge.ChallengeStatus.ACTIVE, updated.get().getStatus());
    }

    @Test
    @Order(4)
    @DisplayName("4. Challenge Discovery - Retrieves active, upcoming, and filtered challenges")
    public void testChallengeDiscovery() {
        List<ChallengeSummaryDTO> active = challengeService.getDiscoverableChallenges("ACTIVE", testUser1.getUserId());
        assertNotNull(active);
        assertFalse(active.isEmpty());

        boolean foundAdminActive = active.stream()
                .anyMatch(c -> c.getChallenge().getChallengeId().equals(adminActiveChallengeId));
        assertTrue(foundAdminActive);
    }

    @Test
    @Order(5)
    @DisplayName("5. Join Challenge & Duplicate Prevention - Users can join and cannot duplicate participation")
    public void testJoinChallenge() {
        boolean joined1 = challengeService.joinChallenge(adminActiveChallengeId, testUser1.getUserId());
        assertTrue(joined1);

        boolean joined2 = challengeService.joinChallenge(adminActiveChallengeId, testUser2.getUserId());
        assertTrue(joined2);

        // Attempt duplicate join
        assertThrows(ValidationException.class, () ->
                challengeService.joinChallenge(adminActiveChallengeId, testUser1.getUserId()));

        Optional<ChallengeParticipant> partOpt = participantDAO.findByChallengeAndUser(adminActiveChallengeId, testUser1.getUserId());
        assertTrue(partOpt.isPresent());
        assertEquals(ChallengeParticipant.ParticipantStatus.JOINED, partOpt.get().getStatus());
    }

    @Test
    @Order(6)
    @DisplayName("6. Workout-Driven Progress Calculation & Clamping - Workouts sync and update progress")
    public void testWorkoutDrivenProgress() {
        // Log a workout for User 1 within the challenge timeframe: 45 min, 400 kcal
        Workout w1 = new Workout();
        w1.setUserId(testUser1.getUserId());
        w1.setWorkoutType(Workout.WorkoutType.RUNNING);
        w1.setWorkoutDate(Date.valueOf(LocalDate.now()));
        w1.setDurationMinutes(45);
        w1.setIntensity(Workout.Intensity.HIGH);
        w1.setCaloriesBurned(400);
        workoutDAO.create(w1);

        // Sync progress for Calorie challenge
        boolean synced = challengeService.syncUserProgress(adminActiveChallengeId, testUser1.getUserId());
        assertTrue(synced);

        Optional<ChallengeParticipant> partOpt = participantDAO.findByChallengeAndUser(adminActiveChallengeId, testUser1.getUserId());
        assertTrue(partOpt.isPresent());
        assertEquals(new BigDecimal("400.00"), partOpt.get().getProgressValue().setScale(2));
        assertEquals(ChallengeParticipant.ParticipantStatus.IN_PROGRESS, partOpt.get().getStatus());
    }

    @Test
    @Order(7)
    @DisplayName("7. Challenge Completion - Automatically completes when reaching target goal")
    public void testChallengeCompletion() {
        // Log a high calorie workout for User 1 to exceed 3000 kcal goal: 2700 kcal
        Workout w2 = new Workout();
        w2.setUserId(testUser1.getUserId());
        w2.setWorkoutType(Workout.WorkoutType.CYCLING);
        w2.setWorkoutDate(Date.valueOf(LocalDate.now()));
        w2.setDurationMinutes(180);
        w2.setIntensity(Workout.Intensity.HIGH);
        w2.setCaloriesBurned(2700);
        workoutDAO.create(w2);

        // Total calories = 400 + 2700 = 3100 >= 3000 target
        challengeService.syncUserProgress(adminActiveChallengeId, testUser1.getUserId());

        Optional<ChallengeParticipant> partOpt = participantDAO.findByChallengeAndUser(adminActiveChallengeId, testUser1.getUserId());
        assertTrue(partOpt.isPresent());
        // Progress should be clamped to goal value (3000)
        assertEquals(new BigDecimal("3000.00"), partOpt.get().getProgressValue().setScale(2));
        assertEquals(ChallengeParticipant.ParticipantStatus.COMPLETED, partOpt.get().getStatus());
        assertNotNull(partOpt.get().getCompletedAt());
    }

    @Test
    @Order(8)
    @DisplayName("8. Leave Challenge - Non-completed user can leave; completed user cannot abandon")
    public void testLeaveChallenge() {
        // User 1 is completed -> cannot leave
        assertThrows(ValidationException.class, () ->
                challengeService.leaveChallenge(adminActiveChallengeId, testUser1.getUserId()));

        // User 2 is not completed -> can leave
        boolean left = challengeService.leaveChallenge(adminActiveChallengeId, testUser2.getUserId());
        assertTrue(left);

        Optional<ChallengeParticipant> partOpt = participantDAO.findByChallengeAndUser(adminActiveChallengeId, testUser2.getUserId());
        assertTrue(partOpt.isEmpty() || partOpt.get().getStatus() == ChallengeParticipant.ParticipantStatus.LEFT);
    }

    @Test
    @Order(9)
    @DisplayName("9. Community Privacy Rules - Respects PERSONAL vs SOCIAL privacy modes")
    public void testCommunityPrivacyRules() {
        // Re-join user 2 and join user 1
        challengeService.joinChallenge(userCreatedChallengeId, testUser1.getUserId());
        challengeService.joinChallenge(userCreatedChallengeId, testUser2.getUserId());

        // When User 2 views challenge:
        ChallengeDetailDTO detailsForUser2 = challengeService.getChallengeDetails(userCreatedChallengeId, testUser2.getUserId());
        assertNotNull(detailsForUser2);

        List<ParticipantSummaryDTO> participants = detailsForUser2.getCommunityParticipants();
        assertFalse(participants.isEmpty());

        for (ParticipantSummaryDTO p : participants) {
            if (p.getUserId().equals(testUser1.getUserId())) {
                // User 1 has PERSONAL privacy -> displayed as "Private Participant" for User 2
                assertEquals("Private Participant", p.getDisplayName());
            } else if (p.getUserId().equals(testUser2.getUserId())) {
                // User 2 has SOCIAL privacy -> displayed with alias "BobSpeed"
                assertEquals("BobSpeed", p.getDisplayName());
            }
        }
    }

    @Test
    @Order(10)
    @DisplayName("10. User Challenge Portfolio - Retrieves user enrolled, in-progress, and created challenges")
    public void testUserChallengePortfolio() {
        List<ChallengeSummaryDTO> user1Challenges = challengeService.getUserParticipatingChallenges(testUser1.getUserId(), "COMPLETED");
        assertNotNull(user1Challenges);
        boolean hasCompleted = user1Challenges.stream()
                .anyMatch(c -> c.getChallenge().getChallengeId().equals(adminActiveChallengeId));
        assertTrue(hasCompleted);

        List<Challenge> user1Created = challengeService.getUserCreatedChallenges(testUser1.getUserId());
        assertNotNull(user1Created);
        boolean hasCreated = user1Created.stream()
                .anyMatch(c -> c.getChallengeId().equals(userCreatedChallengeId));
        assertTrue(hasCreated);
    }
}
