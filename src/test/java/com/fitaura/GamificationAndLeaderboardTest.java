package com.fitaura;

import com.fitaura.dao.AchievementDAO;
import com.fitaura.dao.PointTransactionDAO;
import com.fitaura.dao.UserAchievementDAO;
import com.fitaura.dao.UserDAO;
import com.fitaura.dao.WorkoutDAO;
import com.fitaura.dao.impl.AchievementDAOImpl;
import com.fitaura.dao.impl.PointTransactionDAOImpl;
import com.fitaura.dao.impl.UserAchievementDAOImpl;
import com.fitaura.dao.impl.UserDAOImpl;
import com.fitaura.dao.impl.WorkoutDAOImpl;
import com.fitaura.dto.AchievementDetailDTO;
import com.fitaura.dto.GamificationDashboardDTO;
import com.fitaura.dto.LeaderboardEntryDTO;
import com.fitaura.dto.StreakInfo;
import com.fitaura.model.Achievement;
import com.fitaura.model.PointTransaction;
import com.fitaura.model.User;
import com.fitaura.model.Workout;
import com.fitaura.service.GamificationService;
import com.fitaura.service.impl.GamificationServiceImpl;
import com.fitaura.util.PasswordUtil;
import org.junit.jupiter.api.*;

import java.sql.Date;
import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;

@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
public class GamificationAndLeaderboardTest {

    private static UserDAO userDAO;
    private static WorkoutDAO workoutDAO;
    private static AchievementDAO achievementDAO;
    private static UserAchievementDAO userAchievementDAO;
    private static PointTransactionDAO pointTransactionDAO;
    private static GamificationService gamificationService;

    private static User socialAthlete1;
    private static User socialAthlete2;
    private static User privateAthlete;

    @BeforeAll
    public static void setUp() {
        userDAO = new UserDAOImpl();
        workoutDAO = new WorkoutDAOImpl();
        achievementDAO = new AchievementDAOImpl();
        userAchievementDAO = new UserAchievementDAOImpl();
        pointTransactionDAO = new PointTransactionDAOImpl();
        gamificationService = new GamificationServiceImpl();

        // 1. Social Athlete 1
        socialAthlete1 = new User();
        socialAthlete1.setEmail("gamify_social1_" + System.currentTimeMillis() + "@example.com");
        socialAthlete1.setPasswordHash(PasswordUtil.hashPassword("Password123!"));
        socialAthlete1.setFullName("Alex Champion");
        socialAthlete1.setDisplayName("AlexC");
        socialAthlete1.setPrivacyMode(User.PrivacyMode.SOCIAL);
        socialAthlete1.setAccountStatus(User.AccountStatus.ACTIVE);
        socialAthlete1.setRole(User.Role.USER);
        Integer id1 = userDAO.create(socialAthlete1);
        socialAthlete1.setUserId(id1);

        // 2. Social Athlete 2
        socialAthlete2 = new User();
        socialAthlete2.setEmail("gamify_social2_" + System.currentTimeMillis() + "@example.com");
        socialAthlete2.setPasswordHash(PasswordUtil.hashPassword("Password123!"));
        socialAthlete2.setFullName("Sam Sprint");
        socialAthlete2.setDisplayName("SammyS");
        socialAthlete2.setPrivacyMode(User.PrivacyMode.SOCIAL);
        socialAthlete2.setAccountStatus(User.AccountStatus.ACTIVE);
        socialAthlete2.setRole(User.Role.USER);
        Integer id2 = userDAO.create(socialAthlete2);
        socialAthlete2.setUserId(id2);

        // 3. Private Athlete (PERSONAL privacy)
        privateAthlete = new User();
        privateAthlete.setEmail("gamify_private_" + System.currentTimeMillis() + "@example.com");
        privateAthlete.setPasswordHash(PasswordUtil.hashPassword("Password123!"));
        privateAthlete.setFullName("Jordan Private");
        privateAthlete.setDisplayName("JordanSecret");
        privateAthlete.setPrivacyMode(User.PrivacyMode.PERSONAL);
        privateAthlete.setAccountStatus(User.AccountStatus.ACTIVE);
        privateAthlete.setRole(User.Role.USER);
        Integer id3 = userDAO.create(privateAthlete);
        privateAthlete.setUserId(id3);
    }

    @Test
    @Order(1)
    public void testAwardWorkoutPointsAndMilestones() {
        // Record a 45-minute HIGH intensity running workout for socialAthlete1
        Workout workout = new Workout();
        workout.setUserId(socialAthlete1.getUserId());
        workout.setWorkoutType(Workout.WorkoutType.RUNNING);
        workout.setWorkoutDate(Date.valueOf(LocalDate.now()));
        workout.setDurationMinutes(45);
        workout.setIntensity(Workout.Intensity.HIGH);
        workout.setCaloriesBurned(400);
        workout.setNotes("Morning high intensity intervals");
        Integer wId = workoutDAO.create(workout);
        workout.setWorkoutId(wId);

        // Award points
        gamificationService.awardWorkoutPoints(workout);

        int totalPoints = gamificationService.calculateTotalPoints(socialAthlete1.getUserId());
        // Base 10 + duration (45/10=4) + high intensity (5) = 19 pts + First Workout achievement (10 pts) = 29 pts
        assertTrue(totalPoints >= 19, "Total points should be at least workout points (19+)");

        List<PointTransaction> txList = gamificationService.getPointHistory(socialAthlete1.getUserId(), 10);
        assertFalse(txList.isEmpty(), "Point transactions should not be empty");

        // Test idempotency: awarding points for the exact same workout again must not duplicate
        int ptsBefore = gamificationService.calculateTotalPoints(socialAthlete1.getUserId());
        gamificationService.awardWorkoutPoints(workout);
        int ptsAfter = gamificationService.calculateTotalPoints(socialAthlete1.getUserId());
        assertEquals(ptsBefore, ptsAfter, "Awarding workout points twice must be idempotent");
    }

    @Test
    @Order(2)
    public void testConsecutiveWorkoutStreakCalculation() {
        // Create consecutive workouts for socialAthlete2 across last 3 days
        LocalDate today = LocalDate.now();

        Workout w1 = new Workout();
        w1.setUserId(socialAthlete2.getUserId());
        w1.setWorkoutType(Workout.WorkoutType.STRENGTH);
        w1.setWorkoutDate(Date.valueOf(today.minusDays(2)));
        w1.setDurationMinutes(30);
        w1.setIntensity(Workout.Intensity.MEDIUM);
        workoutDAO.create(w1);

        Workout w2 = new Workout();
        w2.setUserId(socialAthlete2.getUserId());
        w2.setWorkoutType(Workout.WorkoutType.RUNNING);
        w2.setWorkoutDate(Date.valueOf(today.minusDays(1)));
        w2.setDurationMinutes(30);
        w2.setIntensity(Workout.Intensity.MEDIUM);
        workoutDAO.create(w2);

        Workout w3 = new Workout();
        w3.setUserId(socialAthlete2.getUserId());
        w3.setWorkoutType(Workout.WorkoutType.CYCLING);
        w3.setWorkoutDate(Date.valueOf(today));
        w3.setDurationMinutes(40);
        w3.setIntensity(Workout.Intensity.HIGH);
        workoutDAO.create(w3);

        StreakInfo streakInfo = gamificationService.calculateUserStreak(socialAthlete2.getUserId());
        assertNotNull(streakInfo);
        assertEquals(3, streakInfo.getCurrentStreak(), "Current streak should be 3 days");
        assertEquals(3, streakInfo.getLongestStreak(), "Longest streak should be 3 days");
        assertTrue(streakInfo.isActiveToday(), "Active today flag should be true");
        assertFalse(streakInfo.isStreakAtRisk(), "Streak is not at risk if active today");
    }

    @Test
    @Order(3)
    public void testAwardChallengeCompletionPoints() {
        gamificationService.awardChallengeCompletionPoints(
                socialAthlete1.getUserId(),
                999,
                "Summer 100K Steps"
        );

        List<PointTransaction> txList = gamificationService.getPointHistory(socialAthlete1.getUserId(), 10);
        boolean foundChallengeTx = txList.stream()
                .anyMatch(tx -> tx.getSourceType() == PointTransaction.SourceType.CHALLENGE && tx.getPoints() == 100);

        assertTrue(foundChallengeTx, "Should record challenge completion point transaction of 100 pts");
    }

    @Test
    @Order(4)
    public void testAchievementsRetrievalAndProgress() {
        List<AchievementDetailDTO> achievements = gamificationService.getUserAchievements(socialAthlete1.getUserId());
        assertNotNull(achievements);
        assertFalse(achievements.isEmpty(), "Active achievements list should not be empty");

        Optional<AchievementDetailDTO> firstWorkoutAch = achievements.stream()
                .filter(a -> a.getAchievement().getName().equalsIgnoreCase("First Workout"))
                .findFirst();

        assertTrue(firstWorkoutAch.isPresent(), "First Workout achievement should exist");
        assertTrue(firstWorkoutAch.get().isEarned(), "First Workout achievement should be unlocked for user with workouts");
        assertEquals(100, firstWorkoutAch.get().getProgressPercentage());
    }

    @Test
    @Order(5)
    public void testSocialLeaderboardRankingsAndPrivacy() {
        // Award points to socialAthlete2 so they appear on leaderboard
        Workout w = new Workout();
        w.setUserId(socialAthlete2.getUserId());
        w.setWorkoutType(Workout.WorkoutType.RUNNING);
        w.setWorkoutDate(Date.valueOf(LocalDate.now()));
        w.setDurationMinutes(60);
        w.setIntensity(Workout.Intensity.HIGH);
        Integer wid = workoutDAO.create(w);
        w.setWorkoutId(wid);
        gamificationService.awardWorkoutPoints(w);

        List<LeaderboardEntryDTO> leaderboard = gamificationService.getSocialLeaderboard(10, 0, socialAthlete1.getUserId());
        assertNotNull(leaderboard);
        assertFalse(leaderboard.isEmpty(), "Social leaderboard should return entries");

        // Verify rank ordering
        int prevRank = 0;
        int prevPoints = Integer.MAX_VALUE;
        for (LeaderboardEntryDTO entry : leaderboard) {
            assertTrue(entry.getRank() > prevRank, "Ranks must be strictly ascending");
            assertTrue(entry.getTotalPoints() <= prevPoints, "Points must be descending or equal");
            prevRank = entry.getRank();
            prevPoints = entry.getTotalPoints();
        }

        // Verify privacy isolation: Private user should NOT appear in SOCIAL leaderboard query
        boolean privateUserInLeaderboard = leaderboard.stream()
                .anyMatch(e -> e.getUserId().equals(privateAthlete.getUserId()));
        assertFalse(privateUserInLeaderboard, "Users with PERSONAL privacy mode should not appear in public social leaderboard");
    }

    @Test
    @Order(6)
    public void testGamificationDashboardAggregation() {
        GamificationDashboardDTO dashboard = gamificationService.getGamificationDashboard(socialAthlete1.getUserId());
        assertNotNull(dashboard);
        assertTrue(dashboard.getTotalPoints() > 0, "Dashboard total points should be > 0");
        assertTrue(dashboard.getTotalWorkouts() >= 1, "Dashboard total workouts should be >= 1");
        assertNotNull(dashboard.getAllAchievements());
        assertNotNull(dashboard.getRecentPointTransactions());
        assertEquals(User.PrivacyMode.SOCIAL, dashboard.getPrivacyMode());
    }
}
