package com.fitaura.dto;

import com.fitaura.model.FitnessContent;

import java.io.Serializable;
import java.sql.Timestamp;

/**
 * Presentation DTO for Fitness Content items.
 * Enforces privacy boundaries by exposing only safe author display names.
 */
public class FitnessContentDetailDTO implements Serializable {

    private static final long serialVersionUID = 1L;

    private FitnessContent content;
    private String authorDisplayName;
    private boolean isOwner;
    private int readingTimeMinutes;

    public FitnessContentDetailDTO() {
    }

    public FitnessContentDetailDTO(FitnessContent content, String authorDisplayName, boolean isOwner) {
        this.content = content;
        this.authorDisplayName = (authorDisplayName != null && !authorDisplayName.trim().isEmpty())
                ? authorDisplayName : "FitAura Coach";
        this.isOwner = isOwner;
        if (content != null && content.getContentText() != null) {
            int words = content.getContentText().trim().split("\\s+").length;
            this.readingTimeMinutes = Math.max(1, (int) Math.ceil(words / 180.0));
        } else {
            this.readingTimeMinutes = 1;
        }
    }

    public FitnessContent getContent() {
        return content;
    }

    public void setContent(FitnessContent content) {
        this.content = content;
    }

    public String getAuthorDisplayName() {
        return authorDisplayName;
    }

    public void setAuthorDisplayName(String authorDisplayName) {
        this.authorDisplayName = authorDisplayName;
    }

    public boolean isOwner() {
        return isOwner;
    }

    public void setOwner(boolean owner) {
        isOwner = owner;
    }

    public int getReadingTimeMinutes() {
        return readingTimeMinutes;
    }

    public void setReadingTimeMinutes(int readingTimeMinutes) {
        this.readingTimeMinutes = readingTimeMinutes;
    }

    public Integer getContentId() {
        return content != null ? content.getContentId() : null;
    }

    public String getTitle() {
        return content != null ? content.getTitle() : null;
    }

    public FitnessContent.Category getCategory() {
        return content != null ? content.getCategory() : null;
    }

    public FitnessContent.ApprovalStatus getApprovalStatus() {
        return content != null ? content.getApprovalStatus() : null;
    }

    public Integer getReviewedBy() {
        return content != null ? content.getReviewedBy() : null;
    }

    public Timestamp getReviewedAt() {
        return content != null ? content.getReviewedAt() : null;
    }

    public String getCategoryDisplayName() {
        if (content == null || content.getCategory() == null) return "General";
        switch (content.getCategory()) {
            case WORKOUT: return "Workout Routine";
            case EXERCISE: return "Exercise Technique";
            case NUTRITION: return "Nutrition & Diet";
            case FITNESS_TIP: return "Fitness Tip";
            case GUIDE: return "Comprehensive Guide";
            default: return content.getCategory().name();
        }
    }

    public String getCategoryBadgeClass() {
        if (content == null || content.getCategory() == null) return "bg-secondary";
        switch (content.getCategory()) {
            case WORKOUT: return "bg-primary";
            case EXERCISE: return "bg-info text-dark";
            case NUTRITION: return "bg-success";
            case FITNESS_TIP: return "bg-warning text-dark";
            case GUIDE: return "bg-purple text-white";
            default: return "bg-secondary";
        }
    }

    public String getCategoryIconClass() {
        if (content == null || content.getCategory() == null) return "bi-file-text";
        switch (content.getCategory()) {
            case WORKOUT: return "bi-activity";
            case EXERCISE: return "bi-lightning-charge";
            case NUTRITION: return "bi-apple";
            case FITNESS_TIP: return "bi-lightbulb";
            case GUIDE: return "bi-book";
            default: return "bi-file-text";
        }
    }

    public String getSnippet(int maxChars) {
        if (content == null || content.getContentText() == null) return "";
        String text = content.getContentText().trim();
        if (text.length() <= maxChars) return text;
        return text.substring(0, maxChars) + "...";
    }
}
