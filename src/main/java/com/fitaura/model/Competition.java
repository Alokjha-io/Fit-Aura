package com.fitaura.model;

import java.io.Serializable;
import java.math.BigDecimal;
import java.sql.Date;
import java.sql.Timestamp;

/**
 * Model representing a privacy-safe community fitness competition.
 */
public class Competition implements Serializable {

    private static final long serialVersionUID = 1L;

    public enum Metric {
        WORKOUT_COUNT,
        TOTAL_MINUTES,
        CALORIES_BURNED,
        STREAK_DAYS
    }

    public enum Status {
        UPCOMING,
        ACTIVE,
        COMPLETED,
        CANCELLED
    }

    private Integer competitionId;
    private String name;
    private String description;
    private Metric metric;
    private BigDecimal targetValue;
    private Date startDate;
    private Date endDate;
    private Integer rewardPoints;
    private Status status;
    private Integer createdBy;
    private Timestamp createdAt;
    private Timestamp updatedAt;

    // Transient attributes for UI presentation
    private int participantCount;
    private boolean isUserParticipating;
    private BigDecimal userCurrentScore;
    private Integer userRank;

    public Competition() {
        this.metric = Metric.WORKOUT_COUNT;
        this.status = Status.ACTIVE;
        this.rewardPoints = 50;
    }

    public Integer getCompetitionId() {
        return competitionId;
    }

    public void setCompetitionId(Integer competitionId) {
        this.competitionId = competitionId;
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

    public Metric getMetric() {
        return metric;
    }

    public void setMetric(Metric metric) {
        this.metric = metric;
    }

    public BigDecimal getTargetValue() {
        return targetValue;
    }

    public void setTargetValue(BigDecimal targetValue) {
        this.targetValue = targetValue;
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

    public Integer getRewardPoints() {
        return rewardPoints;
    }

    public void setRewardPoints(Integer rewardPoints) {
        this.rewardPoints = rewardPoints;
    }

    public Status getStatus() {
        return status;
    }

    public void setStatus(Status status) {
        this.status = status;
    }

    public Integer getCreatedBy() {
        return createdBy;
    }

    public void setCreatedBy(Integer createdBy) {
        this.createdBy = createdBy;
    }

    public Timestamp getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(Timestamp createdAt) {
        this.createdAt = createdAt;
    }

    public Timestamp getUpdatedAt() {
        return updatedAt;
    }

    public void setUpdatedAt(Timestamp updatedAt) {
        this.updatedAt = updatedAt;
    }

    public int getParticipantCount() {
        return participantCount;
    }

    public void setParticipantCount(int participantCount) {
        this.participantCount = participantCount;
    }

    public boolean isUserParticipating() {
        return isUserParticipating;
    }

    public void setUserParticipating(boolean userParticipating) {
        isUserParticipating = userParticipating;
    }

    public BigDecimal getUserCurrentScore() {
        return userCurrentScore;
    }

    public void setUserCurrentScore(BigDecimal userCurrentScore) {
        this.userCurrentScore = userCurrentScore;
    }

    public Integer getUserRank() {
        return userRank;
    }

    public void setUserRank(Integer userRank) {
        this.userRank = userRank;
    }

    public String getMetricLabel() {
        if (metric == null) return "Workouts";
        return switch (metric) {
            case WORKOUT_COUNT -> "Workouts Completed";
            case TOTAL_MINUTES -> "Total Active Minutes";
            case CALORIES_BURNED -> "Calories Burned (kcal)";
            case STREAK_DAYS -> "Consecutive Active Days";
        };
    }
}
