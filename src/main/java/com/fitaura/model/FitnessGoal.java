package com.fitaura.model;

import java.io.Serializable;
import java.math.BigDecimal;
import java.sql.Date;
import java.sql.Timestamp;

/**
 * Domain entity representing a targeted fitness goal.
 * Maps to the 'fitness_goals' table.
 */
public class FitnessGoal implements Serializable {

    private static final long serialVersionUID = 1L;

    public enum GoalType {
        WEIGHT_LOSS, WEIGHT_GAIN, MUSCLE_GAIN, STRENGTH, ENDURANCE, GENERAL_FITNESS, FLEXIBILITY
    }

    public enum GoalStatus {
        ACTIVE, COMPLETED, PAUSED, CANCELLED
    }

    private Integer goalId;
    private Integer userId;
    private GoalType goalType;
    private BigDecimal targetValue;
    private BigDecimal currentValue;
    private String unit;
    private Date startDate;
    private Date targetDate;
    private GoalStatus status = GoalStatus.ACTIVE;
    private String notes;
    private Timestamp createdAt;

    public FitnessGoal() {
    }

    public FitnessGoal(Integer goalId, Integer userId, GoalType goalType,
                       BigDecimal targetValue, BigDecimal currentValue, String unit,
                       Date startDate, Date targetDate, GoalStatus status, String notes) {
        this.goalId = goalId;
        this.userId = userId;
        this.goalType = goalType;
        this.targetValue = targetValue;
        this.currentValue = currentValue;
        this.unit = unit;
        this.startDate = startDate;
        this.targetDate = targetDate;
        this.status = status != null ? status : GoalStatus.ACTIVE;
        this.notes = notes;
    }

    public Integer getGoalId() {
        return goalId;
    }

    public void setGoalId(Integer goalId) {
        this.goalId = goalId;
    }

    public Integer getUserId() {
        return userId;
    }

    public void setUserId(Integer userId) {
        this.userId = userId;
    }

    public GoalType getGoalType() {
        return goalType;
    }

    public void setGoalType(GoalType goalType) {
        this.goalType = goalType;
    }

    public BigDecimal getTargetValue() {
        return targetValue;
    }

    public void setTargetValue(BigDecimal targetValue) {
        this.targetValue = targetValue;
    }

    public BigDecimal getCurrentValue() {
        return currentValue;
    }

    public void setCurrentValue(BigDecimal currentValue) {
        this.currentValue = currentValue;
    }

    public String getUnit() {
        return unit;
    }

    public void setUnit(String unit) {
        this.unit = unit;
    }

    public Date getStartDate() {
        return startDate;
    }

    public void setStartDate(Date startDate) {
        this.startDate = startDate;
    }

    public Date getTargetDate() {
        return targetDate;
    }

    public void setTargetDate(Date targetDate) {
        this.targetDate = targetDate;
    }

    public GoalStatus getStatus() {
        return status;
    }

    public void setStatus(GoalStatus status) {
        this.status = status;
    }

    public String getNotes() {
        return notes;
    }

    public void setNotes(String notes) {
        this.notes = notes;
    }

    public Timestamp getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(Timestamp createdAt) {
        this.createdAt = createdAt;
    }

    public String getGoalTypeLabel() {
        if (goalType == GoalType.WEIGHT_LOSS) {
            return "Weight Loss";
        } else if (goalType == GoalType.WEIGHT_GAIN) {
            return "Weight Gain";
        } else if (goalType == GoalType.MUSCLE_GAIN) {
            return "Muscle Gain";
        } else if (goalType == GoalType.STRENGTH) {
            return "Strength";
        } else if (goalType == GoalType.ENDURANCE) {
            return "Endurance";
        } else if (goalType == GoalType.GENERAL_FITNESS) {
            return "General Fitness";
        } else if (goalType == GoalType.FLEXIBILITY) {
            return "Flexibility";
        }
        return goalType != null ? goalType.name() : "";
    }

    public String getStatusLabel() {
        if (status == GoalStatus.ACTIVE) {
            return "Active";
        } else if (status == GoalStatus.COMPLETED) {
            return "Completed";
        } else if (status == GoalStatus.PAUSED) {
            return "Paused";
        } else if (status == GoalStatus.CANCELLED) {
            return "Cancelled";
        }
        return status != null ? status.name() : "";
    }

    /**
     * Calculates user-facing progress percentage clamped between 0 and 100.
     */
    public int getProgressPercentage() {
        if (status == GoalStatus.COMPLETED) {
            return 100;
        }
        if (targetValue == null || currentValue == null) {
            return 0;
        }

        double target = targetValue.doubleValue();
        double current = currentValue.doubleValue();

        if (target <= 0) {
            return 0;
        }

        if (goalType == GoalType.WEIGHT_LOSS) {
            if (current <= target) {
                return 100;
            }
            if (current > 0) {
                int pct = (int) Math.round((target / current) * 100.0);
                return Math.min(100, Math.max(0, pct));
            }
            return 0;
        } else {
            if (current >= target) {
                return 100;
            }
            int pct = (int) Math.round((current / target) * 100.0);
            return Math.min(100, Math.max(0, pct));
        }
    }

    @Override
    public String toString() {
        return "FitnessGoal{" +
                "goalId=" + goalId +
                ", userId=" + userId +
                ", goalType=" + goalType +
                ", targetValue=" + targetValue +
                ", currentValue=" + currentValue +
                ", unit='" + unit + '\'' +
                ", status=" + status +
                '}';
    }
}
