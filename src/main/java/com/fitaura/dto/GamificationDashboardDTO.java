package com.fitaura.dto;

import com.fitaura.model.PointTransaction;
import com.fitaura.model.User;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

/**
 * Aggregated presentation DTO for the complete Gamification Dashboard (/gamification).
 */
public class GamificationDashboardDTO implements Serializable {

    private static final long serialVersionUID = 1L;

    private int totalPoints;
    private int currentStreak;
    private int longestStreak;
    private boolean activeToday;
    private boolean streakAtRisk;
    private int earnedAchievementsCount;
    private int totalAchievementsCount;
    private int totalWorkouts;
    private int completedChallenges;
    private Integer userRank;
    private int totalSocialUsers;
    private User.PrivacyMode privacyMode;
    private List<AchievementDetailDTO> recentAchievements = new ArrayList<>();
    private List<AchievementDetailDTO> allAchievements = new ArrayList<>();
    private List<PointTransaction> recentPointTransactions = new ArrayList<>();

    public GamificationDashboardDTO() {
    }

    public int getTotalPoints() {
        return totalPoints;
    }

    public void setTotalPoints(int totalPoints) {
        this.totalPoints = totalPoints;
    }

    public int getCurrentStreak() {
        return currentStreak;
    }

    public void setCurrentStreak(int currentStreak) {
        this.currentStreak = currentStreak;
    }

    public int getLongestStreak() {
        return longestStreak;
    }

    public void setLongestStreak(int longestStreak) {
        this.longestStreak = longestStreak;
    }

    public boolean isActiveToday() {
        return activeToday;
    }

    public void setActiveToday(boolean activeToday) {
        this.activeToday = activeToday;
    }

    public boolean isStreakAtRisk() {
        return streakAtRisk;
    }

    public void setStreakAtRisk(boolean streakAtRisk) {
        this.streakAtRisk = streakAtRisk;
    }

    public int getEarnedAchievementsCount() {
        return earnedAchievementsCount;
    }

    public void setEarnedAchievementsCount(int earnedAchievementsCount) {
        this.earnedAchievementsCount = earnedAchievementsCount;
    }

    public int getTotalAchievementsCount() {
        return totalAchievementsCount;
    }

    public void setTotalAchievementsCount(int totalAchievementsCount) {
        this.totalAchievementsCount = totalAchievementsCount;
    }

    public int getTotalWorkouts() {
        return totalWorkouts;
    }

    public void setTotalWorkouts(int totalWorkouts) {
        this.totalWorkouts = totalWorkouts;
    }

    public int getCompletedChallenges() {
        return completedChallenges;
    }

    public void setCompletedChallenges(int completedChallenges) {
        this.completedChallenges = completedChallenges;
    }

    public Integer getUserRank() {
        return userRank;
    }

    public void setUserRank(Integer userRank) {
        this.userRank = userRank;
    }

    public int getTotalSocialUsers() {
        return totalSocialUsers;
    }

    public void setTotalSocialUsers(int totalSocialUsers) {
        this.totalSocialUsers = totalSocialUsers;
    }

    public User.PrivacyMode getPrivacyMode() {
        return privacyMode;
    }

    public void setPrivacyMode(User.PrivacyMode privacyMode) {
        this.privacyMode = privacyMode;
    }

    public List<AchievementDetailDTO> getRecentAchievements() {
        return recentAchievements;
    }

    public void setRecentAchievements(List<AchievementDetailDTO> recentAchievements) {
        this.recentAchievements = recentAchievements;
    }

    public List<AchievementDetailDTO> getAllAchievements() {
        return allAchievements;
    }

    public void setAllAchievements(List<AchievementDetailDTO> allAchievements) {
        this.allAchievements = allAchievements;
    }

    public List<PointTransaction> getRecentPointTransactions() {
        return recentPointTransactions;
    }

    public void setRecentPointTransactions(List<PointTransaction> recentPointTransactions) {
        this.recentPointTransactions = recentPointTransactions;
    }

    public int getAchievementProgressPercent() {
        if (totalAchievementsCount <= 0) return 0;
        double pct = (double) earnedAchievementsCount / totalAchievementsCount * 100.0;
        return (int) Math.min(100, Math.max(0, Math.round(pct)));
    }
}
