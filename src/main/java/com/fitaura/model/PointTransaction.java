package com.fitaura.model;

import java.io.Serializable;
import java.sql.Timestamp;

/**
 * Domain entity representing an awarded or adjusted point transaction.
 * Maps to the 'point_transactions' table (used for the leaderboard ledger).
 */
public class PointTransaction implements Serializable {

    private static final long serialVersionUID = 1L;

    public enum SourceType {
        WORKOUT, CHALLENGE, ACHIEVEMENT, BONUS, ADJUSTMENT
    }

    private Integer transactionId;
    private Integer userId;
    private SourceType sourceType;
    private Integer sourceId;
    private Integer points;
    private String description;
    private Timestamp createdAt;

    public PointTransaction() {
    }

    public PointTransaction(Integer transactionId, Integer userId, SourceType sourceType,
                            Integer sourceId, Integer points, String description) {
        this.transactionId = transactionId;
        this.userId = userId;
        this.sourceType = sourceType;
        this.sourceId = sourceId;
        this.points = points;
        this.description = description;
    }

    public Integer getTransactionId() {
        return transactionId;
    }

    public void setTransactionId(Integer transactionId) {
        this.transactionId = transactionId;
    }

    public Integer getUserId() {
        return userId;
    }

    public void setUserId(Integer userId) {
        this.userId = userId;
    }

    public SourceType getSourceType() {
        return sourceType;
    }

    public void setSourceType(SourceType sourceType) {
        this.sourceType = sourceType;
    }

    public Integer getSourceId() {
        return sourceId;
    }

    public void setSourceId(Integer sourceId) {
        this.sourceId = sourceId;
    }

    public Integer getPoints() {
        return points;
    }

    public void setPoints(Integer points) {
        this.points = points;
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

    @Override
    public String toString() {
        return "PointTransaction{" +
                "transactionId=" + transactionId +
                ", userId=" + userId +
                ", sourceType=" + sourceType +
                ", points=" + points +
                ", description='" + description + '\'' +
                '}';
    }
}
