package com.fitaura.dto;

import com.fitaura.model.Achievement;
import com.fitaura.model.Competition;

import java.io.Serializable;
import java.util.List;

/**
 * Public, privacy-safe social profile for users in SOCIAL mode.
 * Strictly excludes all sensitive biometrics, weight, height, BMI, BMR, TDEE, calories, and email.
 */
public class SocialProfileDTO implements Serializable {

    private static final long serialVersionUID = 1L;

    private Integer userId;
    private String displayName;
    private int totalPoints;
    private Integer socialRank;
    private int currentStreakDays;
    private int longestStreakDays;
    private int achievementCount;
    private int connectionCount;
    private boolean isConnection;
    private boolean hasPendingConnectionRequest;
    private List<AchievementDetailDTO> showcasedAchievements;
    private List<Competition> participatingCompetitions;

    public SocialProfileDTO() {
    }

    public Integer getUserId() {
        return userId;
    }

    public void setUserId(Integer userId) {
        this.userId = userId;
    }

    public String getDisplayName() {
        return displayName;
    }

    public void setDisplayName(String displayName) {
        this.displayName = displayName;
    }

    public int getTotalPoints() {
        return totalPoints;
    }

    public void setTotalPoints(int totalPoints) {
        this.totalPoints = totalPoints;
    }

    public Integer getSocialRank() {
        return socialRank;
    }

    public void setSocialRank(Integer socialRank) {
        this.socialRank = socialRank;
    }

    public int getCurrentStreakDays() {
        return currentStreakDays;
    }

    public void setCurrentStreakDays(int currentStreakDays) {
        this.currentStreakDays = currentStreakDays;
    }

    public int getLongestStreakDays() {
        return longestStreakDays;
    }

    public void setLongestStreakDays(int longestStreakDays) {
        this.longestStreakDays = longestStreakDays;
    }

    public int getAchievementCount() {
        return achievementCount;
    }

    public void setAchievementCount(int achievementCount) {
        this.achievementCount = achievementCount;
    }

    public int getConnectionCount() {
        return connectionCount;
    }

    public void setConnectionCount(int connectionCount) {
        this.connectionCount = connectionCount;
    }

    public boolean isConnection() {
        return isConnection;
    }

    public void setConnection(boolean connection) {
        isConnection = connection;
    }

    public boolean isHasPendingConnectionRequest() {
        return hasPendingConnectionRequest;
    }

    public void setHasPendingConnectionRequest(boolean hasPendingConnectionRequest) {
        this.hasPendingConnectionRequest = hasPendingConnectionRequest;
    }

    public List<AchievementDetailDTO> getShowcasedAchievements() {
        return showcasedAchievements;
    }

    public void setShowcasedAchievements(List<AchievementDetailDTO> showcasedAchievements) {
        this.showcasedAchievements = showcasedAchievements;
    }

    public List<Competition> getParticipatingCompetitions() {
        return participatingCompetitions;
    }

    public void setParticipatingCompetitions(List<Competition> participatingCompetitions) {
        this.participatingCompetitions = participatingCompetitions;
    }
}
