package com.fitaura.model;

import java.io.Serializable;
import java.sql.Timestamp;

/**
 * Model representing a privacy-safe community milestone event in the social feed.
 */
public class SocialActivity implements Serializable {

    private static final long serialVersionUID = 1L;

    public enum ActivityType {
        STREAK_MILESTONE,
        ACHIEVEMENT_UNLOCKED,
        CHALLENGE_COMPLETED,
        COMPETITION_JOINED,
        COMPETITION_WON
    }

    private Integer activityId;
    private Integer userId;
    private ActivityType activityType;
    private String title;
    private String description;
    private Timestamp createdAt;

    // Transient attributes
    private String userDisplayName;

    public SocialActivity() {
    }

    public SocialActivity(Integer userId, ActivityType activityType, String title, String description) {
        this.userId = userId;
        this.activityType = activityType;
        this.title = title;
        this.description = description;
    }

    public Integer getActivityId() {
        return activityId;
    }

    public void setActivityId(Integer activityId) {
        this.activityId = activityId;
    }

    public Integer getUserId() {
        return userId;
    }

    public void setUserId(Integer userId) {
        this.userId = userId;
    }

    public ActivityType getActivityType() {
        return activityType;
    }

    public void setActivityType(ActivityType activityType) {
        this.activityType = activityType;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public Timestamp getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(Timestamp createdAt) {
        this.createdAt = createdAt;
    }

    public String getUserDisplayName() {
        return userDisplayName;
    }

    public void setUserDisplayName(String userDisplayName) {
        this.userDisplayName = userDisplayName;
    }
}
