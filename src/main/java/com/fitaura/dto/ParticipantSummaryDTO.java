package com.fitaura.dto;

import com.fitaura.model.ChallengeParticipant;
import com.fitaura.model.User;

import java.io.Serializable;
import java.math.BigDecimal;
import java.sql.Timestamp;

/**
 * Privacy-aware DTO for participant representation in community challenge listings.
 */
public class ParticipantSummaryDTO implements Serializable {

    private static final long serialVersionUID = 1L;

    private Integer userId;
    private String displayName;
    private User.PrivacyMode privacyMode;
    private BigDecimal progressValue;
    private int progressPercentage;
    private ChallengeParticipant.ParticipantStatus status;
    private Timestamp joinedAt;
    private Timestamp completedAt;
    private boolean isCurrentUser;

    public ParticipantSummaryDTO() {
    }

    public Integer getUserId() {
        return userId;
    }

    public void setUserId(Integer userId) {
        this.userId = userId;
    }

    public String getDisplayName() {
        if (privacyMode == User.PrivacyMode.PERSONAL && !isCurrentUser) {
            return "Private Participant";
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

    public BigDecimal getProgressValue() {
        return progressValue;
    }

    public void setProgressValue(BigDecimal progressValue) {
        this.progressValue = progressValue;
    }

    public int getProgressPercentage() {
        return progressPercentage;
    }

    public void setProgressPercentage(int progressPercentage) {
        this.progressPercentage = Math.max(0, Math.min(100, progressPercentage));
    }

    public ChallengeParticipant.ParticipantStatus getStatus() {
        return status;
    }

    public void setStatus(ChallengeParticipant.ParticipantStatus status) {
        this.status = status;
    }

    public Timestamp getJoinedAt() {
        return joinedAt;
    }

    public void setJoinedAt(Timestamp joinedAt) {
        this.joinedAt = joinedAt;
    }

    public Timestamp getCompletedAt() {
        return completedAt;
    }

    public void setCompletedAt(Timestamp completedAt) {
        this.completedAt = completedAt;
    }

    public boolean isCurrentUser() {
        return isCurrentUser;
    }

    public void setCurrentUser(boolean isCurrentUser) {
        this.isCurrentUser = isCurrentUser;
    }
}
