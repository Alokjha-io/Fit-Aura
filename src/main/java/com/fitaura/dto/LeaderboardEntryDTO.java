package com.fitaura.dto;

import com.fitaura.model.User;

import java.io.Serializable;

/**
 * DTO representing an individual ranking row on the social leaderboard.
 */
public class LeaderboardEntryDTO implements Serializable {

    private static final long serialVersionUID = 1L;

    private int rank;
    private Integer userId;
    private String displayName;
    private User.PrivacyMode privacyMode;
    private int totalPoints;
    private int totalWorkouts;
    private int currentStreak;
    private int earnedAchievements;
    private boolean isCurrentUser;

    public LeaderboardEntryDTO() {
    }

    public LeaderboardEntryDTO(int rank, Integer userId, String displayName, User.PrivacyMode privacyMode,
                               int totalPoints, int totalWorkouts, int currentStreak,
                               int earnedAchievements, boolean isCurrentUser) {
        this.rank = rank;
        this.userId = userId;
        this.displayName = displayName;
        this.privacyMode = privacyMode;
        this.totalPoints = totalPoints;
        this.totalWorkouts = totalWorkouts;
        this.currentStreak = currentStreak;
        this.earnedAchievements = earnedAchievements;
        this.isCurrentUser = isCurrentUser;
    }

    public int getRank() {
        return rank;
    }

    public void setRank(int rank) {
        this.rank = rank;
    }

    public Integer getUserId() {
        return userId;
    }

    public void setUserId(Integer userId) {
        this.userId = userId;
    }

    public String getDisplayName() {
        if (privacyMode == User.PrivacyMode.PERSONAL && !isCurrentUser) {
            return "Private Athlete";
        }
        return (displayName != null && !displayName.trim().isEmpty()) ? displayName : "Athlete #" + userId;
    }

    public void setDisplayName(String displayName) {
        this.displayName = displayName;
    }

    public User.PrivacyMode getPrivacyMode() {
        return privacyMode;
    }

    public void setPrivacyMode(User.PrivacyMode privacyMode) {
        this.privacyMode = privacyMode;
    }

    public int getTotalPoints() {
        return totalPoints;
    }

    public void setTotalPoints(int totalPoints) {
        this.totalPoints = totalPoints;
    }

    public int getTotalWorkouts() {
        return totalWorkouts;
    }

    public void setTotalWorkouts(int totalWorkouts) {
        this.totalWorkouts = totalWorkouts;
    }

    public int getCurrentStreak() {
        return currentStreak;
    }

    public void setCurrentStreak(int currentStreak) {
        this.currentStreak = currentStreak;
    }

    public int getEarnedAchievements() {
        return earnedAchievements;
    }

    public void setEarnedAchievements(int earnedAchievements) {
        this.earnedAchievements = earnedAchievements;
    }

    public boolean isCurrentUser() {
        return isCurrentUser;
    }

    public void setCurrentUser(boolean currentUser) {
        isCurrentUser = currentUser;
    }
}
