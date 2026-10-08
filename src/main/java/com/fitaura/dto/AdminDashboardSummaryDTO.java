package com.fitaura.dto;

import com.fitaura.model.ActivityLog;
import com.fitaura.model.Challenge;
import com.fitaura.model.User;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

/**
 * Aggregated presentation DTO for the Admin Dashboard overview.
 */
public class AdminDashboardSummaryDTO implements Serializable {

    private static final long serialVersionUID = 1L;

    // User metrics
    private int totalUsers;
    private int activeUsers;
    private int inactiveUsers;
    private int blockedUsers;

    // Workout metrics
    private int totalWorkouts;
    private int workoutsThisWeek;
    private int workoutsThisMonth;

    // Goal metrics
    private int totalGoals;
    private int activeGoals;
    private int completedGoals;

    // Challenge metrics
    private int totalChallenges;
    private int pendingChallenges;
    private int activeChallenges;
    private int completedChallenges;
    private int totalChallengeParticipants;

    // Gamification metrics
    private int totalPointsAwarded;
    private int totalAchievementsEarned;
    private int socialLeaderboardUsers;

    // Recent events
    private List<User> recentUsers = new ArrayList<>();
    private List<Challenge> pendingModerationChallenges = new ArrayList<>();
    private List<ActivityLog> recentActivityLogs = new ArrayList<>();

    public AdminDashboardSummaryDTO() {
    }

    public int getTotalUsers() {
        return totalUsers;
    }

    public void setTotalUsers(int totalUsers) {
        this.totalUsers = totalUsers;
    }

    public int getActiveUsers() {
        return activeUsers;
    }

    public void setActiveUsers(int activeUsers) {
        this.activeUsers = activeUsers;
    }

    public int getInactiveUsers() {
        return inactiveUsers;
    }

    public void setInactiveUsers(int inactiveUsers) {
        this.inactiveUsers = inactiveUsers;
    }

    public int getBlockedUsers() {
        return blockedUsers;
    }

    public void setBlockedUsers(int blockedUsers) {
        this.blockedUsers = blockedUsers;
    }

    public int getTotalWorkouts() {
        return totalWorkouts;
    }

    public void setTotalWorkouts(int totalWorkouts) {
        this.totalWorkouts = totalWorkouts;
    }

    public int getWorkoutsThisWeek() {
        return workoutsThisWeek;
    }

    public void setWorkoutsThisWeek(int workoutsThisWeek) {
        this.workoutsThisWeek = workoutsThisWeek;
    }

    public int getWorkoutsThisMonth() {
        return workoutsThisMonth;
    }

    public void setWorkoutsThisMonth(int workoutsThisMonth) {
        this.workoutsThisMonth = workoutsThisMonth;
    }

    public int getTotalGoals() {
        return totalGoals;
    }

    public void setTotalGoals(int totalGoals) {
        this.totalGoals = totalGoals;
    }

    public int getActiveGoals() {
        return activeGoals;
    }

    public void setActiveGoals(int activeGoals) {
        this.activeGoals = activeGoals;
    }

    public int getCompletedGoals() {
        return completedGoals;
    }

    public void setCompletedGoals(int completedGoals) {
        this.completedGoals = completedGoals;
    }

    public int getTotalChallenges() {
        return totalChallenges;
    }

    public void setTotalChallenges(int totalChallenges) {
        this.totalChallenges = totalChallenges;
    }

    public int getPendingChallenges() {
        return pendingChallenges;
    }

    public void setPendingChallenges(int pendingChallenges) {
        this.pendingChallenges = pendingChallenges;
    }

    public int getActiveChallenges() {
        return activeChallenges;
    }

    public void setActiveChallenges(int activeChallenges) {
        this.activeChallenges = activeChallenges;
    }

    public int getCompletedChallenges() {
        return completedChallenges;
    }

    public void setCompletedChallenges(int completedChallenges) {
        this.completedChallenges = completedChallenges;
    }

    public int getTotalChallengeParticipants() {
        return totalChallengeParticipants;
    }

    public void setTotalChallengeParticipants(int totalChallengeParticipants) {
        this.totalChallengeParticipants = totalChallengeParticipants;
    }

    public int getTotalPointsAwarded() {
        return totalPointsAwarded;
    }

    public void setTotalPointsAwarded(int totalPointsAwarded) {
        this.totalPointsAwarded = totalPointsAwarded;
    }

    public int getTotalAchievementsEarned() {
        return totalAchievementsEarned;
    }

    public void setTotalAchievementsEarned(int totalAchievementsEarned) {
        this.totalAchievementsEarned = totalAchievementsEarned;
    }

    public int getSocialLeaderboardUsers() {
        return socialLeaderboardUsers;
    }

    public void setSocialLeaderboardUsers(int socialLeaderboardUsers) {
        this.socialLeaderboardUsers = socialLeaderboardUsers;
    }

    public List<User> getRecentUsers() {
        return recentUsers;
    }

    public void setRecentUsers(List<User> recentUsers) {
        this.recentUsers = recentUsers;
    }

    public List<Challenge> getPendingModerationChallenges() {
        return pendingModerationChallenges;
    }

    public void setPendingModerationChallenges(List<Challenge> pendingModerationChallenges) {
        this.pendingModerationChallenges = pendingModerationChallenges;
    }

    public List<ActivityLog> getRecentActivityLogs() {
        return recentActivityLogs;
    }

    public void setRecentActivityLogs(List<ActivityLog> recentActivityLogs) {
        this.recentActivityLogs = recentActivityLogs;
    }
}
