package com.fitaura.service;

import com.fitaura.dto.AchievementDetailDTO;
import com.fitaura.dto.GamificationDashboardDTO;
import com.fitaura.dto.LeaderboardEntryDTO;
import com.fitaura.dto.StreakInfo;
import com.fitaura.model.PointTransaction;
import com.fitaura.model.Workout;

import java.util.List;

/**
 * Service interface for Gamification, Points, Streaks, Achievements, and Social Leaderboard.
 */
public interface GamificationService {

    /**
     * Retrieves the complete dashboard data for the authenticated user.
     */
    GamificationDashboardDTO getGamificationDashboard(Integer userId);

    /**
     * Calculates the user's current workout streak and longest streak in days.
     */
    StreakInfo calculateUserStreak(Integer userId);

    /**
     * Calculates total accumulated points for the user.
     */
    int calculateTotalPoints(Integer userId);

    /**
     * Fetches recent point transactions for user audit.
     */
    List<PointTransaction> getPointHistory(Integer userId, int limit);

    /**
     * Fetches all active achievements with current user completion status & progress.
     */
    List<AchievementDetailDTO> getUserAchievements(Integer userId);

    /**
     * Fetches ranked social leaderboard users respecting privacy boundaries.
     */
    List<LeaderboardEntryDTO> getSocialLeaderboard(int limit, int offset, Integer currentUserId);

    /**
     * Returns total number of active users in the social leaderboard.
     */
    int countSocialLeaderboardUsers();

    /**
     * Returns the user's current 1-based rank on the social leaderboard, or null if unranked.
     */
    Integer getUserSocialRank(Integer userId);

    /**
     * Awards points for a newly recorded workout (idempotent) and triggers milestone evaluations.
     */
    void awardWorkoutPoints(Workout workout);

    /**
     * Awards points for completing a fitness challenge (idempotent) and evaluates challenge milestones.
     */
    void awardChallengeCompletionPoints(Integer userId, Integer challengeId, String challengeName);

    /**
     * Awards an achievement by name, persists record, creates points transaction, and logs activity.
     * Returns true if newly unlocked, false if already earned or not found.
     */
    boolean awardAchievement(Integer userId, String achievementName);

    /**
     * Checks all milestone criteria against user's actual workout and challenge data.
     */
    List<AchievementDetailDTO> checkAndAwardMilestones(Integer userId);
}
