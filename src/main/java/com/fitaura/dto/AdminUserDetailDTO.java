package com.fitaura.dto;

import com.fitaura.model.User;

import java.io.Serializable;
import java.sql.Timestamp;

/**
 * Presentation DTO for admin inspection of user accounts.
 * Provides administrative insights without exposing private health data or password hashes.
 */
public class AdminUserDetailDTO implements Serializable {

    private static final long serialVersionUID = 1L;

    private User user;
    private int totalWorkouts;
    private int totalGoals;
    private int activeGoals;
    private int joinedChallenges;
    private int completedChallenges;
    private int totalPoints;
    private int earnedAchievements;
    private Timestamp lastWorkoutDate;

    public AdminUserDetailDTO() {
    }

    public User getUser() {
        return user;
    }

    public void setUser(User user) {
        this.user = user;
    }

    public int getTotalWorkouts() {
        return totalWorkouts;
    }

    public void setTotalWorkouts(int totalWorkouts) {
        this.totalWorkouts = totalWorkouts;
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

    public int getJoinedChallenges() {
        return joinedChallenges;
    }

    public void setJoinedChallenges(int joinedChallenges) {
        this.joinedChallenges = joinedChallenges;
    }

    public int getCompletedChallenges() {
        return completedChallenges;
    }

    public void setCompletedChallenges(int completedChallenges) {
        this.completedChallenges = completedChallenges;
    }

    public int getTotalPoints() {
        return totalPoints;
    }

    public void setTotalPoints(int totalPoints) {
        this.totalPoints = totalPoints;
    }

    public int getEarnedAchievements() {
        return earnedAchievements;
    }

    public void setEarnedAchievements(int earnedAchievements) {
        this.earnedAchievements = earnedAchievements;
    }

    public Timestamp getLastWorkoutDate() {
        return lastWorkoutDate;
    }

    public void setLastWorkoutDate(Timestamp lastWorkoutDate) {
        this.lastWorkoutDate = lastWorkoutDate;
    }
}
