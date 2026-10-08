package com.fitaura.dto;

import java.io.Serializable;
import java.util.HashMap;
import java.util.Map;

/**
 * Aggregated presentation DTO for detailed system-wide statistics (/admin/statistics).
 */
public class AdminStatisticsDTO implements Serializable {

    private static final long serialVersionUID = 1L;

    // User breakdown
    private int totalUsers;
    private int activeUsers;
    private int inactiveUsers;
    private int blockedUsers;
    private int socialUsers;
    private int personalUsers;
    private int adminUsers;

    // Workout breakdown
    private int totalWorkouts;
    private int workoutsThisWeek;
    private int workoutsThisMonth;
    private Map<String, Integer> workoutTypeDistribution = new HashMap<>();
    private Map<String, Integer> workoutIntensityDistribution = new HashMap<>();

    // Goals breakdown
    private int totalGoals;
    private int activeGoals;
    private int completedGoals;
    private int pausedGoals;
    private int cancelledGoals;
    private Map<String, Integer> goalTypeDistribution = new HashMap<>();

    // Challenges breakdown
    private int totalChallenges;
    private int draftChallenges;
    private int activeChallenges;
    private int completedChallenges;
    private int cancelledChallenges;
    private int totalEnrollments;
    private int activeParticipants;
    private int completedParticipants;

    // Gamification totals
    private int totalPointsAwarded;
    private int totalAchievementsEarned;
    private int activeSocialLeaderboardAthletes;

    public AdminStatisticsDTO() {
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

    public int getSocialUsers() {
        return socialUsers;
    }

    public void setSocialUsers(int socialUsers) {
        this.socialUsers = socialUsers;
    }

    public int getPersonalUsers() {
        return personalUsers;
    }

    public void setPersonalUsers(int personalUsers) {
        this.personalUsers = personalUsers;
    }

    public int getAdminUsers() {
        return adminUsers;
    }

    public void setAdminUsers(int adminUsers) {
        this.adminUsers = adminUsers;
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

    public Map<String, Integer> getWorkoutTypeDistribution() {
        return workoutTypeDistribution;
    }

    public void setWorkoutTypeDistribution(Map<String, Integer> workoutTypeDistribution) {
        this.workoutTypeDistribution = workoutTypeDistribution;
    }

    public Map<String, Integer> getWorkoutIntensityDistribution() {
        return workoutIntensityDistribution;
    }

    public void setWorkoutIntensityDistribution(Map<String, Integer> workoutIntensityDistribution) {
        this.workoutIntensityDistribution = workoutIntensityDistribution;
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

    public int getPausedGoals() {
        return pausedGoals;
    }

    public void setPausedGoals(int pausedGoals) {
        this.pausedGoals = pausedGoals;
    }

    public int getCancelledGoals() {
        return cancelledGoals;
    }

    public void setCancelledGoals(int cancelledGoals) {
        this.cancelledGoals = cancelledGoals;
    }

    public Map<String, Integer> getGoalTypeDistribution() {
        return goalTypeDistribution;
    }

    public void setGoalTypeDistribution(Map<String, Integer> goalTypeDistribution) {
        this.goalTypeDistribution = goalTypeDistribution;
    }

    public int getTotalChallenges() {
        return totalChallenges;
    }

    public void setTotalChallenges(int totalChallenges) {
        this.totalChallenges = totalChallenges;
    }

    public int getDraftChallenges() {
        return draftChallenges;
    }

    public void setDraftChallenges(int draftChallenges) {
        this.draftChallenges = draftChallenges;
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

    public int getCancelledChallenges() {
        return cancelledChallenges;
    }

    public void setCancelledChallenges(int cancelledChallenges) {
        this.cancelledChallenges = cancelledChallenges;
    }

    public int getTotalEnrollments() {
        return totalEnrollments;
    }

    public void setTotalEnrollments(int totalEnrollments) {
        this.totalEnrollments = totalEnrollments;
    }

    public int getActiveParticipants() {
        return activeParticipants;
    }

    public void setActiveParticipants(int activeParticipants) {
        this.activeParticipants = activeParticipants;
    }

    public int getCompletedParticipants() {
        return completedParticipants;
    }

    public void setCompletedParticipants(int completedParticipants) {
        this.completedParticipants = completedParticipants;
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

    public int getActiveSocialLeaderboardAthletes() {
        return activeSocialLeaderboardAthletes;
    }

    public void setActiveSocialLeaderboardAthletes(int activeSocialLeaderboardAthletes) {
        this.activeSocialLeaderboardAthletes = activeSocialLeaderboardAthletes;
    }
}
