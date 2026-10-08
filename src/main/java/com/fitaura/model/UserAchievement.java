package com.fitaura.model;

import java.io.Serializable;
import java.sql.Timestamp;

/**
 * Domain entity representing an achievement unlocked by a specific user.
 * Maps to the 'user_achievements' table.
 */
public class UserAchievement implements Serializable {

    private static final long serialVersionUID = 1L;

    private Integer userAchievementId;
    private Integer userId;
    private Integer achievementId;
    private Timestamp earnedAt;

    public UserAchievement() {
    }

    public UserAchievement(Integer userId, Integer achievementId) {
        this.userId = userId;
        this.achievementId = achievementId;
        this.earnedAt = new Timestamp(System.currentTimeMillis());
    }

    public UserAchievement(Integer userAchievementId, Integer userId, Integer achievementId, Timestamp earnedAt) {
        this.userAchievementId = userAchievementId;
        this.userId = userId;
        this.achievementId = achievementId;
        this.earnedAt = earnedAt;
    }

    public Integer getUserAchievementId() {
        return userAchievementId;
    }

    public void setUserAchievementId(Integer userAchievementId) {
        this.userAchievementId = userAchievementId;
    }

    public Integer getUserId() {
        return userId;
    }

    public void setUserId(Integer userId) {
        this.userId = userId;
    }

    public Integer getAchievementId() {
        return achievementId;
    }

    public void setAchievementId(Integer achievementId) {
        this.achievementId = achievementId;
    }

    public Timestamp getEarnedAt() {
        return earnedAt;
    }

    public void setEarnedAt(Timestamp earnedAt) {
        this.earnedAt = earnedAt;
    }

    @Override
    public String toString() {
        return "UserAchievement{" +
                "userAchievementId=" + userAchievementId +
                ", userId=" + userId +
                ", achievementId=" + achievementId +
                ", earnedAt=" + earnedAt +
                '}';
    }
}
