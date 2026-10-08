package com.fitaura.dto;

import com.fitaura.model.Achievement;

import java.io.Serializable;
import java.sql.Timestamp;

/**
 * Presentation DTO for user achievements in gamification dashboards.
 */
public class AchievementDetailDTO implements Serializable {

    private static final long serialVersionUID = 1L;

    private Achievement achievement;
    private boolean earned;
    private Timestamp earnedAt;
    private int currentProgress;
    private int targetProgress;
    private int progressPercentage;

    public AchievementDetailDTO() {
    }

    public AchievementDetailDTO(Achievement achievement, boolean earned, Timestamp earnedAt,
                                int currentProgress, int targetProgress) {
        this.achievement = achievement;
        this.earned = earned;
        this.earnedAt = earnedAt;
        this.currentProgress = currentProgress;
        this.targetProgress = targetProgress;
        if (targetProgress > 0) {
            double pct = (double) currentProgress / targetProgress * 100.0;
            this.progressPercentage = (int) Math.min(100, Math.max(0, Math.round(pct)));
        } else {
            this.progressPercentage = earned ? 100 : 0;
        }
    }

    public Achievement getAchievement() {
        return achievement;
    }

    public void setAchievement(Achievement achievement) {
        this.achievement = achievement;
    }

    public boolean isEarned() {
        return earned;
    }

    public boolean isUnlocked() {
        return earned;
    }

    public void setEarned(boolean earned) {
        this.earned = earned;
    }

    public Timestamp getEarnedAt() {
        return earnedAt;
    }

    public void setEarnedAt(Timestamp earnedAt) {
        this.earnedAt = earnedAt;
    }

    public int getCurrentProgress() {
        return currentProgress;
    }

    public void setCurrentProgress(int currentProgress) {
        this.currentProgress = currentProgress;
    }

    public int getTargetProgress() {
        return targetProgress;
    }

    public void setTargetProgress(int targetProgress) {
        this.targetProgress = targetProgress;
    }

    public int getProgressPercentage() {
        return progressPercentage;
    }

    public void setProgressPercentage(int progressPercentage) {
        this.progressPercentage = progressPercentage;
    }

    public String getBootstrapIconClass() {
        if (achievement == null || achievement.getIconName() == null) {
            return "bi-award";
        }
        String icon = achievement.getIconName();
        if (icon.startsWith("bi-")) return icon;
        return "bi-" + icon;
    }
}
