package com.fitaura.model;

import java.io.Serializable;

/**
 * Domain entity representing a predefined achievement in the gamification system.
 * Maps to the 'achievements' table.
 */
public class Achievement implements Serializable {

    private static final long serialVersionUID = 1L;

    private Integer achievementId;
    private String name;
    private String description;
    private Integer points = 0;
    private String iconName;
    private Boolean isActive = true;

    public Achievement() {
    }

    public Achievement(Integer achievementId, String name, String description,
                       Integer points, String iconName, Boolean isActive) {
        this.achievementId = achievementId;
        this.name = name;
        this.description = description;
        this.points = points != null ? points : 0;
        this.iconName = iconName;
        this.isActive = isActive != null ? isActive : true;
    }

    public Integer getAchievementId() {
        return achievementId;
    }

    public void setAchievementId(Integer achievementId) {
        this.achievementId = achievementId;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public Integer getPoints() {
        return points;
    }

    public void setPoints(Integer points) {
        this.points = points;
    }

    public String getIconName() {
        return iconName;
    }

    public void setIconName(String iconName) {
        this.iconName = iconName;
    }

    public Boolean getIsActive() {
        return isActive;
    }

    public void setIsActive(Boolean isActive) {
        this.isActive = isActive;
    }

    @Override
    public String toString() {
        return "Achievement{" +
                "achievementId=" + achievementId +
                ", name='" + name + '\'' +
                ", points=" + points +
                ", iconName='" + iconName + '\'' +
                ", isActive=" + isActive +
                '}';
    }
}
