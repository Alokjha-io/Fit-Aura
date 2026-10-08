package com.fitaura.model;

import java.io.Serializable;
import java.math.BigDecimal;
import java.sql.Timestamp;

/**
 * Domain entity representing a user's participation in a challenge.
 * Maps to the 'challenge_participants' table.
 */
public class ChallengeParticipant implements Serializable {

    private static final long serialVersionUID = 1L;

    public enum ParticipantStatus {
        JOINED, IN_PROGRESS, COMPLETED, LEFT
    }

    private Integer participationId;
    private Integer challengeId;
    private Integer userId;
    private BigDecimal progressValue = BigDecimal.ZERO;
    private ParticipantStatus status = ParticipantStatus.JOINED;
    private Timestamp joinedAt;
    private Timestamp completedAt;

    public ChallengeParticipant() {
    }

    public ChallengeParticipant(Integer participationId, Integer challengeId, Integer userId,
                                BigDecimal progressValue, ParticipantStatus status,
                                Timestamp joinedAt, Timestamp completedAt) {
        this.participationId = participationId;
        this.challengeId = challengeId;
        this.userId = userId;
        this.progressValue = progressValue != null ? progressValue : BigDecimal.ZERO;
        this.status = status != null ? status : ParticipantStatus.JOINED;
        this.joinedAt = joinedAt;
        this.completedAt = completedAt;
    }

    public Integer getParticipationId() {
        return participationId;
    }

    public void setParticipationId(Integer participationId) {
        this.participationId = participationId;
    }

    public Integer getChallengeId() {
        return challengeId;
    }

    public void setChallengeId(Integer challengeId) {
        this.challengeId = challengeId;
    }

    public Integer getUserId() {
        return userId;
    }

    public void setUserId(Integer userId) {
        this.userId = userId;
    }

    public BigDecimal getProgressValue() {
        return progressValue != null ? progressValue : BigDecimal.ZERO;
    }

    public void setProgressValue(BigDecimal progressValue) {
        this.progressValue = progressValue != null ? progressValue : BigDecimal.ZERO;
    }

    public ParticipantStatus getStatus() {
        return status;
    }

    public void setStatus(ParticipantStatus status) {
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

    public int getProgressPercentage(BigDecimal goalValue) {
        if (goalValue == null || goalValue.compareTo(BigDecimal.ZERO) <= 0 || progressValue == null) {
            return 0;
        }
        double pct = (progressValue.doubleValue() / goalValue.doubleValue()) * 100.0;
        return (int) Math.min(100, Math.max(0, Math.round(pct)));
    }

    public boolean isCompleted() {
        return status == ParticipantStatus.COMPLETED;
    }

    @Override
    public String toString() {
        return "ChallengeParticipant{" +
                "participationId=" + participationId +
                ", challengeId=" + challengeId +
                ", userId=" + userId +
                ", progressValue=" + progressValue +
                ", status=" + status +
                '}';
    }
}
