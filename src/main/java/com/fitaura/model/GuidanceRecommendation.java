package com.fitaura.model;

import java.io.Serializable;

/**
 * Domain entity representing a personalized rule-based recommendation item.
 */
public class GuidanceRecommendation implements Serializable {

    private static final long serialVersionUID = 1L;

    public enum Category {
        WORKOUT, EXERCISE, NUTRITION, DAILY_TIP, GOAL_ADVICE
    }

    public enum Priority {
        HIGH, MEDIUM, LOW
    }

    private Category category;
    private String title;
    private String message;
    private Priority priority = Priority.MEDIUM;
    private String reason;
    private String actionUrl;
    private String actionLabel;
    private String iconClass;

    public GuidanceRecommendation() {
    }

    public GuidanceRecommendation(Category category, String title, String message, Priority priority) {
        this.category = category;
        this.title = title;
        this.message = message;
        this.priority = priority;
    }

    public GuidanceRecommendation(Category category, String title, String message, Priority priority,
                                  String reason, String actionUrl, String actionLabel, String iconClass) {
        this.category = category;
        this.title = title;
        this.message = message;
        this.priority = priority;
        this.reason = reason;
        this.actionUrl = actionUrl;
        this.actionLabel = actionLabel;
        this.iconClass = iconClass;
    }

    public Category getCategory() {
        return category;
    }

    public void setCategory(Category category) {
        this.category = category;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public Priority getPriority() {
        return priority;
    }

    public void setPriority(Priority priority) {
        this.priority = priority;
    }

    public String getReason() {
        return reason;
    }

    public void setReason(String reason) {
        this.reason = reason;
    }

    public String getActionUrl() {
        return actionUrl;
    }

    public void setActionUrl(String actionUrl) {
        this.actionUrl = actionUrl;
    }

    public String getActionLabel() {
        return actionLabel;
    }

    public void setActionLabel(String actionLabel) {
        this.actionLabel = actionLabel;
    }

    public String getIconClass() {
        return iconClass;
    }

    public void setIconClass(String iconClass) {
        this.iconClass = iconClass;
    }

    public String getCategoryLabel() {
        if (category == null) return "";
        switch (category) {
            case WORKOUT: return "Workout Plan";
            case EXERCISE: return "Exercise Focus";
            case NUTRITION: return "Nutrition Tip";
            case DAILY_TIP: return "Daily Motivation";
            case GOAL_ADVICE: return "Goal Strategy";
            default: return category.name();
        }
    }
}
