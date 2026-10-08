package com.fitaura.model;

import java.io.Serializable;
import java.sql.Date;
import java.sql.Timestamp;

/**
 * Domain entity representing an activity or workout session.
 * Maps to the 'workouts' table.
 */
public class Workout implements Serializable {

    private static final long serialVersionUID = 1L;

    public enum WorkoutType {
        RUNNING, WALKING, CYCLING, STRENGTH, HOME_WORKOUT
    }

    public enum Intensity {
        LOW, MEDIUM, HIGH
    }

    private Integer workoutId;
    private Integer userId;
    private WorkoutType workoutType;
    private Date workoutDate;
    private Integer durationMinutes;
    private Intensity intensity;
    private Integer caloriesBurned = 0;
    private String notes;
    private Timestamp createdAt;
    private Timestamp updatedAt;

    public Workout() {
    }

    public Workout(Integer workoutId, Integer userId, WorkoutType workoutType,
                   Date workoutDate, Integer durationMinutes, Intensity intensity,
                   Integer caloriesBurned, String notes) {
        this.workoutId = workoutId;
        this.userId = userId;
        this.workoutType = workoutType;
        this.workoutDate = workoutDate;
        this.durationMinutes = durationMinutes;
        this.intensity = intensity;
        this.caloriesBurned = caloriesBurned != null ? caloriesBurned : 0;
        this.notes = notes;
    }

    public Integer getWorkoutId() {
        return workoutId;
    }

    public void setWorkoutId(Integer workoutId) {
        this.workoutId = workoutId;
    }

    public Integer getUserId() {
        return userId;
    }

    public void setUserId(Integer userId) {
        this.userId = userId;
    }

    public WorkoutType getWorkoutType() {
        return workoutType;
    }

    public void setWorkoutType(WorkoutType workoutType) {
        this.workoutType = workoutType;
    }

    public Date getWorkoutDate() {
        return workoutDate;
    }

    public void setWorkoutDate(Date workoutDate) {
        this.workoutDate = workoutDate;
    }

    public Integer getDurationMinutes() {
        return durationMinutes;
    }

    public void setDurationMinutes(Integer durationMinutes) {
        this.durationMinutes = durationMinutes;
    }

    public Intensity getIntensity() {
        return intensity;
    }

    public void setIntensity(Intensity intensity) {
        this.intensity = intensity;
    }

    public Integer getCaloriesBurned() {
        return caloriesBurned;
    }

    public void setCaloriesBurned(Integer caloriesBurned) {
        this.caloriesBurned = caloriesBurned;
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

    public Timestamp getUpdatedAt() {
        return updatedAt;
    }

    public void setUpdatedAt(Timestamp updatedAt) {
        this.updatedAt = updatedAt;
    }

    public String getWorkoutTypeLabel() {
        if (workoutType == WorkoutType.RUNNING) {
            return "Running";
        } else if (workoutType == WorkoutType.WALKING) {
            return "Walking";
        } else if (workoutType == WorkoutType.CYCLING) {
            return "Cycling";
        } else if (workoutType == WorkoutType.STRENGTH) {
            return "Strength Training";
        } else if (workoutType == WorkoutType.HOME_WORKOUT) {
            return "Home Workout";
        }
        return workoutType != null ? workoutType.name() : "";
    }

    public String getIntensityLabel() {
        if (intensity == Intensity.LOW) {
            return "Low";
        } else if (intensity == Intensity.MEDIUM) {
            return "Medium";
        } else if (intensity == Intensity.HIGH) {
            return "High";
        }
        return intensity != null ? intensity.name() : "";
    }

    @Override
    public String toString() {
        return "Workout{" +
                "workoutId=" + workoutId +
                ", userId=" + userId +
                ", workoutType=" + workoutType +
                ", workoutDate=" + workoutDate +
                ", durationMinutes=" + durationMinutes +
                ", intensity=" + intensity +
                ", caloriesBurned=" + caloriesBurned +
                '}';
    }
}
