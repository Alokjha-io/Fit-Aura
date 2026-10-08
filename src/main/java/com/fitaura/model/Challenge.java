package com.fitaura.model;

import java.io.Serializable;
import java.math.BigDecimal;
import java.sql.Date;
import java.sql.Timestamp;
import java.time.LocalDate;
import java.time.temporal.ChronoUnit;

/**
 * Domain entity representing a fitness challenge.
 * Maps to the 'challenges' table.
 */
public class Challenge implements Serializable {

    private static final long serialVersionUID = 1L;

    public enum ChallengeStatus {
        DRAFT, ACTIVE, COMPLETED, CANCELLED
    }

    private Integer challengeId;
    private Integer createdBy;
    private String name;
    private String description;
    private BigDecimal goalValue;
    private String goalUnit;
    private Integer pointsReward = 0;
    private Date startDate;
    private Date endDate;
    private ChallengeStatus status = ChallengeStatus.DRAFT;
    private Timestamp createdAt;

    public Challenge() {
    }

    public Challenge(Integer challengeId, Integer createdBy, String name, String description,
                     BigDecimal goalValue, String goalUnit, Integer pointsReward,
                     Date startDate, Date endDate, ChallengeStatus status) {
        this.challengeId = challengeId;
        this.createdBy = createdBy;
        this.name = name;
        this.description = description;
        this.goalValue = goalValue;
        this.goalUnit = goalUnit;
        this.pointsReward = pointsReward != null ? pointsReward : 0;
        this.startDate = startDate;
        this.endDate = endDate;
        this.status = status != null ? status : ChallengeStatus.DRAFT;
    }

    public Integer getChallengeId() {
        return challengeId;
    }

    public void setChallengeId(Integer challengeId) {
        this.challengeId = challengeId;
    }

    public Integer getCreatedBy() {
        return createdBy;
    }

    public void setCreatedBy(Integer createdBy) {
        this.createdBy = createdBy;
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

    public BigDecimal getGoalValue() {
        return goalValue;
    }

    public void setGoalValue(BigDecimal goalValue) {
        this.goalValue = goalValue;
    }

    public String getGoalUnit() {
        return goalUnit;
    }

    public void setGoalUnit(String goalUnit) {
        this.goalUnit = goalUnit;
    }

    public Integer getPointsReward() {
        return pointsReward;
    }

    public void setPointsReward(Integer pointsReward) {
        this.pointsReward = pointsReward;
    }

    public Date getStartDate() {
        return startDate;
    }

    public void setStartDate(Date startDate) {
        this.startDate = startDate;
    }

    public Date getEndDate() {
        return endDate;
    }

    public void setEndDate(Date endDate) {
        this.endDate = endDate;
    }

    public ChallengeStatus getStatus() {
        return status;
    }

    public void setStatus(ChallengeStatus status) {
        this.status = status;
    }

    public Timestamp getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(Timestamp createdAt) {
        this.createdAt = createdAt;
    }

    public long getDurationDays() {
        if (startDate == null || endDate == null) return 0;
        return ChronoUnit.DAYS.between(startDate.toLocalDate(), endDate.toLocalDate());
    }

    public boolean isJoinable(LocalDate today) {
        if (status != ChallengeStatus.ACTIVE || startDate == null || endDate == null || today == null) {
            return false;
        }
        return !today.isAfter(endDate.toLocalDate());
    }

    public boolean isExpired(LocalDate today) {
        if (endDate == null || today == null) return false;
        return today.isAfter(endDate.toLocalDate());
    }

    public boolean isUpcoming(LocalDate today) {
        if (startDate == null || today == null) return false;
        return today.isBefore(startDate.toLocalDate());
    }

    public long getDaysRemaining(LocalDate today) {
        if (endDate == null || today == null) return 0;
        return ChronoUnit.DAYS.between(today, endDate.toLocalDate());
    }

    public String getStatusBadgeClass() {
        if (status == null) return "bg-secondary";
        switch (status) {
            case ACTIVE: return "bg-success";
            case DRAFT: return "bg-warning text-dark";
            case COMPLETED: return "bg-primary";
            case CANCELLED: return "bg-danger";
            default: return "bg-secondary";
        }
    }

    @Override
    public String toString() {
        return "Challenge{" +
                "challengeId=" + challengeId +
                ", name='" + name + '\'' +
                ", goalValue=" + goalValue +
                ", goalUnit='" + goalUnit + '\'' +
                ", pointsReward=" + pointsReward +
                ", status=" + status +
                '}';
    }
}
