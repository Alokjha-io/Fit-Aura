package com.fitaura.service.impl;

import com.fitaura.dao.AchievementDAO;
import com.fitaura.dao.ActivityLogDAO;
import com.fitaura.dao.ChallengeParticipantDAO;
import com.fitaura.dao.PointTransactionDAO;
import com.fitaura.dao.UserAchievementDAO;
import com.fitaura.dao.UserDAO;
import com.fitaura.dao.WorkoutDAO;
import com.fitaura.dao.impl.AchievementDAOImpl;
import com.fitaura.dao.impl.ActivityLogDAOImpl;
import com.fitaura.dao.impl.ChallengeParticipantDAOImpl;
import com.fitaura.dao.impl.PointTransactionDAOImpl;
import com.fitaura.dao.impl.UserAchievementDAOImpl;
import com.fitaura.dao.impl.UserDAOImpl;
import com.fitaura.dao.impl.WorkoutDAOImpl;
import com.fitaura.dto.AchievementDetailDTO;
import com.fitaura.dto.GamificationDashboardDTO;
import com.fitaura.dto.LeaderboardEntryDTO;
import com.fitaura.dto.StreakInfo;
import com.fitaura.exception.DatabaseException;
import com.fitaura.model.Achievement;
import com.fitaura.model.ActivityLog;
import com.fitaura.model.ChallengeParticipant;
import com.fitaura.model.PointTransaction;
import com.fitaura.model.User;
import com.fitaura.model.UserAchievement;
import com.fitaura.model.Workout;
import com.fitaura.service.GamificationService;
import com.fitaura.util.AppConstants;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.Set;
import java.util.TreeSet;
import java.util.stream.Collectors;

/**
 * Production implementation of GamificationService.
 * Handles points calculations, milestone evaluations, workout streaks, and social leaderboards.
 */
public class GamificationServiceImpl implements GamificationService {

    private static final Logger logger = LoggerFactory.getLogger(GamificationServiceImpl.class);

    private final PointTransactionDAO pointTransactionDAO;
    private final AchievementDAO achievementDAO;
    private final UserAchievementDAO userAchievementDAO;
    private final WorkoutDAO workoutDAO;
    private final ChallengeParticipantDAO challengeParticipantDAO;
    private final UserDAO userDAO;
    private final ActivityLogDAO activityLogDAO;

    public GamificationServiceImpl() {
        this(new PointTransactionDAOImpl(), new AchievementDAOImpl(), new UserAchievementDAOImpl(),
             new WorkoutDAOImpl(), new ChallengeParticipantDAOImpl(), new UserDAOImpl(),
             new ActivityLogDAOImpl());
    }

    public GamificationServiceImpl(PointTransactionDAO pointTransactionDAO,
                                   AchievementDAO achievementDAO,
                                   UserAchievementDAO userAchievementDAO,
                                   WorkoutDAO workoutDAO,
                                   ChallengeParticipantDAO challengeParticipantDAO,
                                   UserDAO userDAO,
                                   ActivityLogDAO activityLogDAO) {
        this.pointTransactionDAO = pointTransactionDAO;
        this.achievementDAO = achievementDAO;
        this.userAchievementDAO = userAchievementDAO;
        this.workoutDAO = workoutDAO;
        this.challengeParticipantDAO = challengeParticipantDAO;
        this.userDAO = userDAO;
        this.activityLogDAO = activityLogDAO;
    }

    @Override
    public GamificationDashboardDTO getGamificationDashboard(Integer userId) {
        if (userId == null) {
            return new GamificationDashboardDTO();
        }

        GamificationDashboardDTO dto = new GamificationDashboardDTO();

        // 1. Total points
        int totalPoints = pointTransactionDAO.calculateTotalPoints(userId);
        dto.setTotalPoints(totalPoints);

        // 2. Streaks
        StreakInfo streakInfo = calculateUserStreak(userId);
        dto.setCurrentStreak(streakInfo.getCurrentStreak());
        dto.setLongestStreak(streakInfo.getLongestStreak());
        dto.setActiveToday(streakInfo.isActiveToday());
        dto.setStreakAtRisk(streakInfo.isStreakAtRisk());

        // 3. User & Privacy Mode
        Optional<User> userOpt = userDAO.findById(userId);
        if (userOpt.isPresent()) {
            dto.setPrivacyMode(userOpt.get().getPrivacyMode());
        }

        // 4. Workout count & Completed challenges
        int workoutCount = workoutDAO.countByUserId(userId);
        dto.setTotalWorkouts(workoutCount);

        List<ChallengeParticipant> userChallenges = challengeParticipantDAO.findByUserId(userId);
        long completedCount = userChallenges.stream()
                .filter(cp -> cp.getStatus() == ChallengeParticipant.ParticipantStatus.COMPLETED)
                .count();
        dto.setCompletedChallenges((int) completedCount);

        // 5. Leaderboard Rank & Social Pool
        Integer userRank = pointTransactionDAO.calculateUserSocialRank(userId);
        dto.setUserRank(userRank);
        int totalSocialUsers = pointTransactionDAO.countSocialLeaderboardUsers();
        dto.setTotalSocialUsers(totalSocialUsers);

        // 6. Achievements
        List<AchievementDetailDTO> achievements = getUserAchievements(userId);
        dto.setAllAchievements(achievements);
        dto.setTotalAchievementsCount(achievements.size());

        long earnedCount = achievements.stream().filter(AchievementDetailDTO::isEarned).count();
        dto.setEarnedAchievementsCount((int) earnedCount);

        List<AchievementDetailDTO> recentEarned = achievements.stream()
                .filter(AchievementDetailDTO::isEarned)
                .sorted((a, b) -> {
                    if (a.getEarnedAt() == null && b.getEarnedAt() == null) return 0;
                    if (a.getEarnedAt() == null) return 1;
                    if (b.getEarnedAt() == null) return -1;
                    return b.getEarnedAt().compareTo(a.getEarnedAt());
                })
                .limit(4)
                .collect(Collectors.toList());
        dto.setRecentAchievements(recentEarned);

        // 7. Recent Transactions
        List<PointTransaction> recentTx = pointTransactionDAO.findRecentByUserId(userId, 10);
        dto.setRecentPointTransactions(recentTx);

        return dto;
    }

    @Override
    public StreakInfo calculateUserStreak(Integer userId) {
        if (userId == null) {
            return new StreakInfo(0, 0, null, false, false);
        }

        List<Workout> workouts = workoutDAO.findByUserId(userId, 500, 0);
        if (workouts == null || workouts.isEmpty()) {
            return new StreakInfo(0, 0, null, false, false);
        }

        // Collect distinct local dates in descending and set order
        TreeSet<LocalDate> distinctDates = new TreeSet<>(Collections.reverseOrder());
        for (Workout w : workouts) {
            if (w.getWorkoutDate() != null) {
                distinctDates.add(w.getWorkoutDate().toLocalDate());
            }
        }

        if (distinctDates.isEmpty()) {
            return new StreakInfo(0, 0, null, false, false);
        }

        LocalDate today = LocalDate.now();
        LocalDate yesterday = today.minusDays(1);
        LocalDate lastActiveDate = distinctDates.first();

        boolean activeToday = distinctDates.contains(today);
        boolean activeYesterday = distinctDates.contains(yesterday);
        boolean streakAtRisk = !activeToday && activeYesterday;

        // Calculate current streak
        int currentStreak = 0;
        if (activeToday || activeYesterday) {
            LocalDate checkDate = activeToday ? today : yesterday;
            while (distinctDates.contains(checkDate)) {
                currentStreak++;
                checkDate = checkDate.minusDays(1);
            }
        }

        // Calculate longest streak
        List<LocalDate> sortedAscDates = distinctDates.stream()
                .sorted()
                .collect(Collectors.toList());

        int longestStreak = 0;
        int tempStreak = 0;
        LocalDate prevDate = null;

        for (LocalDate date : sortedAscDates) {
            if (prevDate == null) {
                tempStreak = 1;
            } else if (date.equals(prevDate.plusDays(1))) {
                tempStreak++;
            } else {
                tempStreak = 1;
            }
            if (tempStreak > longestStreak) {
                longestStreak = tempStreak;
            }
            prevDate = date;
        }

        return new StreakInfo(currentStreak, longestStreak, lastActiveDate, activeToday, streakAtRisk);
    }

    @Override
    public int calculateTotalPoints(Integer userId) {
        if (userId == null) return 0;
        return pointTransactionDAO.calculateTotalPoints(userId);
    }

    @Override
    public List<PointTransaction> getPointHistory(Integer userId, int limit) {
        if (userId == null) return Collections.emptyList();
        int validLimit = limit > 0 ? Math.min(limit, 100) : 20;
        return pointTransactionDAO.findRecentByUserId(userId, validLimit);
    }

    @Override
    public List<AchievementDetailDTO> getUserAchievements(Integer userId) {
        List<Achievement> allAchievements = achievementDAO.findAllActive();
        if (allAchievements.isEmpty()) {
            return Collections.emptyList();
        }

        List<UserAchievement> earnedList = (userId != null)
                ? userAchievementDAO.findByUserId(userId)
                : Collections.emptyList();

        Map<Integer, UserAchievement> earnedMap = earnedList.stream()
                .collect(Collectors.toMap(UserAchievement::getAchievementId, ua -> ua, (k1, k2) -> k1));

        // Get user metrics for progress calculations
        int workoutCount = (userId != null) ? workoutDAO.countByUserId(userId) : 0;
        StreakInfo streak = (userId != null) ? calculateUserStreak(userId) : new StreakInfo();
        int challengeCount = 0;
        if (userId != null) {
            List<ChallengeParticipant> cps = challengeParticipantDAO.findByUserId(userId);
            challengeCount = (int) cps.stream()
                    .filter(c -> c.getStatus() == ChallengeParticipant.ParticipantStatus.COMPLETED)
                    .count();
        }

        List<AchievementDetailDTO> result = new ArrayList<>();
        for (Achievement a : allAchievements) {
            UserAchievement earned = earnedMap.get(a.getAchievementId());
            boolean isEarned = (earned != null);

            int currentProgress = 0;
            int targetProgress = 1;

            String name = a.getName() != null ? a.getName().toLowerCase() : "";
            if (name.contains("first workout")) {
                targetProgress = 1;
                currentProgress = Math.min(1, workoutCount);
            } else if (name.contains("7 day streak") || name.contains("7-day streak")) {
                targetProgress = 7;
                currentProgress = Math.min(7, Math.max(streak.getCurrentStreak(), streak.getLongestStreak()));
            } else if (name.contains("30 day streak") || name.contains("30-day streak")) {
                targetProgress = 30;
                currentProgress = Math.min(30, Math.max(streak.getCurrentStreak(), streak.getLongestStreak()));
            } else if (name.contains("challenge completed") || name.contains("challenge complete")) {
                targetProgress = 1;
                currentProgress = Math.min(1, challengeCount);
            } else if (name.contains("5 workout") || name.contains("dedicated")) {
                targetProgress = 5;
                currentProgress = Math.min(5, workoutCount);
            } else if (name.contains("10 workout") || name.contains("club")) {
                targetProgress = 10;
                currentProgress = Math.min(10, workoutCount);
            } else {
                targetProgress = 1;
                currentProgress = isEarned ? 1 : 0;
            }

            result.add(new AchievementDetailDTO(
                    a,
                    isEarned,
                    isEarned ? earned.getEarnedAt() : null,
                    currentProgress,
                    targetProgress
            ));
        }

        return result;
    }

    @Override
    public List<LeaderboardEntryDTO> getSocialLeaderboard(int limit, int offset, Integer currentUserId) {
        int validLimit = limit > 0 ? Math.min(limit, 100) : 50;
        int validOffset = Math.max(0, offset);

        List<LeaderboardEntryDTO> leaderboard = pointTransactionDAO.getSocialLeaderboard(validLimit, validOffset);

        // Assign ranks and isCurrentUser flag
        for (int i = 0; i < leaderboard.size(); i++) {
            LeaderboardEntryDTO entry = leaderboard.get(i);
            entry.setRank(validOffset + i + 1);
            if (currentUserId != null && currentUserId.equals(entry.getUserId())) {
                entry.setCurrentUser(true);
            }
        }

        return leaderboard;
    }

    @Override
    public int countSocialLeaderboardUsers() {
        return pointTransactionDAO.countSocialLeaderboardUsers();
    }

    @Override
    public Integer getUserSocialRank(Integer userId) {
        if (userId == null) return null;
        return pointTransactionDAO.calculateUserSocialRank(userId);
    }

    @Override
    public void awardWorkoutPoints(Workout workout) {
        if (workout == null || workout.getUserId() == null || workout.getWorkoutId() == null) {
            return;
        }

        Integer userId = workout.getUserId();
        Integer workoutId = workout.getWorkoutId();

        // Idempotency check: has points already been recorded for this workout?
        boolean alreadyAwarded = pointTransactionDAO.hasSourceTransaction(
                userId, PointTransaction.SourceType.WORKOUT, workoutId);

        if (alreadyAwarded) {
            logger.debug("Workout {} already awarded points for user {}", workoutId, userId);
            return;
        }

        // Base points: 10
        int points = 10;

        // Duration bonus: +1 point per 10 minutes
        if (workout.getDurationMinutes() != null && workout.getDurationMinutes() > 0) {
            points += (workout.getDurationMinutes() / 10);
        }

        // Intensity bonus: HIGH +5, MEDIUM +2
        if (workout.getIntensity() == Workout.Intensity.HIGH) {
            points += 5;
        } else if (workout.getIntensity() == Workout.Intensity.MEDIUM) {
            points += 2;
        }

        PointTransaction tx = new PointTransaction();
        tx.setUserId(userId);
        tx.setSourceType(PointTransaction.SourceType.WORKOUT);
        tx.setSourceId(workoutId);
        tx.setPoints(points);
        tx.setDescription("Recorded " + workout.getWorkoutType().name() + " workout (" +
                           workout.getDurationMinutes() + " min, +" + points + " pts)");

        try {
            pointTransactionDAO.create(tx);
            logger.info("Awarded {} points to user {} for workout {}", points, userId, workoutId);

            // Trigger milestone evaluation
            checkAndAwardMilestones(userId);

        } catch (DatabaseException e) {
            logger.error("Failed to award workout points to user {}: {}", userId, e.getMessage());
        }
    }

    @Override
    public void awardChallengeCompletionPoints(Integer userId, Integer challengeId, String challengeName) {
        if (userId == null || challengeId == null) {
            return;
        }

        boolean alreadyAwarded = pointTransactionDAO.hasSourceTransaction(
                userId, PointTransaction.SourceType.CHALLENGE, challengeId);

        if (alreadyAwarded) {
            logger.debug("Challenge {} completion already awarded points for user {}", challengeId, userId);
            return;
        }

        int points = 100;
        PointTransaction tx = new PointTransaction();
        tx.setUserId(userId);
        tx.setSourceType(PointTransaction.SourceType.CHALLENGE);
        tx.setSourceId(challengeId);
        tx.setPoints(points);
        tx.setDescription("Completed Challenge: " + (challengeName != null ? challengeName : "Challenge #" + challengeId));

        try {
            pointTransactionDAO.create(tx);
            logger.info("Awarded {} points to user {} for completing challenge {}", points, userId, challengeId);

            // Trigger milestone check
            checkAndAwardMilestones(userId);

        } catch (DatabaseException e) {
            logger.error("Failed to award challenge completion points to user {}: {}", userId, e.getMessage());
        }
    }

    @Override
    public boolean awardAchievement(Integer userId, String achievementName) {
        if (userId == null || achievementName == null || achievementName.trim().isEmpty()) {
            return false;
        }

        Optional<Achievement> achOpt = achievementDAO.findByName(achievementName.trim());
        if (!achOpt.isPresent()) {
            logger.warn("Achievement '{}' not found in database", achievementName);
            return false;
        }

        Achievement achievement = achOpt.get();
        Integer achievementId = achievement.getAchievementId();

        // Check if already earned
        if (userAchievementDAO.hasUserEarned(userId, achievementId)) {
            return false;
        }

        try {
            UserAchievement ua = new UserAchievement(userId, achievementId);
            userAchievementDAO.create(ua);

            // Award achievement points
            int pts = (achievement.getPoints() != null) ? achievement.getPoints() : 0;
            if (pts > 0) {
                PointTransaction pt = new PointTransaction();
                pt.setUserId(userId);
                pt.setSourceType(PointTransaction.SourceType.ACHIEVEMENT);
                pt.setSourceId(achievementId);
                pt.setPoints(pts);
                pt.setDescription("Unlocked Achievement: " + achievement.getName() + " (+" + pts + " pts)");
                pointTransactionDAO.create(pt);
            }

            // Log activity
            try {
                ActivityLog log = new ActivityLog();
                log.setUserId(userId);
                log.setActionType(AppConstants.ACTION_ACHIEVEMENT_UNLOCKED);
                log.setEntityType("ACHIEVEMENT");
                log.setEntityId(achievementId);
                log.setDescription("Unlocked achievement: " + achievement.getName());
                activityLogDAO.create(log);
            } catch (Exception ex) {
                logger.warn("Failed to write activity log for achievement: {}", ex.getMessage());
            }

            logger.info("User {} unlocked achievement '{}' (+{} pts)", userId, achievement.getName(), pts);
            return true;

        } catch (DatabaseException e) {
            logger.error("Error awarding achievement '{}' to user {}: {}", achievementName, userId, e.getMessage());
            return false;
        }
    }

    @Override
    public List<AchievementDetailDTO> checkAndAwardMilestones(Integer userId) {
        if (userId == null) {
            return Collections.emptyList();
        }

        int workoutCount = workoutDAO.countByUserId(userId);
        StreakInfo streakInfo = calculateUserStreak(userId);

        List<ChallengeParticipant> cps = challengeParticipantDAO.findByUserId(userId);
        long completedChallenges = cps.stream()
                .filter(cp -> cp.getStatus() == ChallengeParticipant.ParticipantStatus.COMPLETED)
                .count();

        // 1. First Workout
        if (workoutCount >= 1) {
            awardAchievement(userId, "First Workout");
        }

        // 2. 7 Day Streak
        if (streakInfo.getCurrentStreak() >= 7 || streakInfo.getLongestStreak() >= 7) {
            awardAchievement(userId, "7 Day Streak");
        }

        // 3. Challenge Completed
        if (completedChallenges >= 1) {
            awardAchievement(userId, "Challenge Completed");
        }

        return getUserAchievements(userId);
    }
}
