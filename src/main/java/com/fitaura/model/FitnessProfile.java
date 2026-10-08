package com.fitaura.model;

import java.io.Serializable;
import java.math.BigDecimal;

/**
 * Domain entity representing a user's fitness profile and physical metrics.
 * Maps to the 'fitness_profiles' table (1-to-1 relationship with User).
 */
public class FitnessProfile implements Serializable {

    private static final long serialVersionUID = 1L;

    public enum Gender {
        MALE, FEMALE, OTHER, PREFER_NOT_TO_SAY
    }

    public enum ActivityLevel {
        BEGINNER, LIGHT, MODERATE, ACTIVE, VERY_ACTIVE
    }

    public enum PreferredEnvironment {
        GYM, HOME, OUTDOOR, MIXED
    }

    private Integer profileId;
    private Integer userId;
    private Integer age;
    private Gender gender;
    private BigDecimal heightCm;
    private BigDecimal weightKg;
    private ActivityLevel activityLevel = ActivityLevel.BEGINNER;
    private PreferredEnvironment preferredEnvironment = PreferredEnvironment.MIXED;

    public FitnessProfile() {
    }

    public FitnessProfile(Integer profileId, Integer userId, Integer age, Gender gender,
                          BigDecimal heightCm, BigDecimal weightKg,
                          ActivityLevel activityLevel, PreferredEnvironment preferredEnvironment) {
        this.profileId = profileId;
        this.userId = userId;
        this.age = age;
        this.gender = gender;
        this.heightCm = heightCm;
        this.weightKg = weightKg;
        this.activityLevel = activityLevel != null ? activityLevel : ActivityLevel.BEGINNER;
        this.preferredEnvironment = preferredEnvironment != null ? preferredEnvironment : PreferredEnvironment.MIXED;
    }

    public Integer getProfileId() {
        return profileId;
    }

    public void setProfileId(Integer profileId) {
        this.profileId = profileId;
    }

    public Integer getUserId() {
        return userId;
    }

    public void setUserId(Integer userId) {
        this.userId = userId;
    }

    public Integer getAge() {
        return age;
    }

    public void setAge(Integer age) {
        this.age = age;
    }

    public Gender getGender() {
        return gender;
    }

    public void setGender(Gender gender) {
        this.gender = gender;
    }

    public BigDecimal getHeightCm() {
        return heightCm;
    }

    public void setHeightCm(BigDecimal heightCm) {
        this.heightCm = heightCm;
    }

    public BigDecimal getWeightKg() {
        return weightKg;
    }

    public void setWeightKg(BigDecimal weightKg) {
        this.weightKg = weightKg;
    }

    public ActivityLevel getActivityLevel() {
        return activityLevel;
    }

    public void setActivityLevel(ActivityLevel activityLevel) {
        this.activityLevel = activityLevel;
    }

    public PreferredEnvironment getPreferredEnvironment() {
        return preferredEnvironment;
    }

    public void setPreferredEnvironment(PreferredEnvironment preferredEnvironment) {
        this.preferredEnvironment = preferredEnvironment;
    }

    /**
     * Calculates Body Mass Index (BMI) = weight (kg) / [height (m)]^2
     * Returns null if height or weight is not configured.
     */
    public BigDecimal calculateBmi() {
        if (heightCm == null || weightKg == null 
                || heightCm.compareTo(BigDecimal.ZERO) <= 0 
                || weightKg.compareTo(BigDecimal.ZERO) <= 0) {
            return null;
        }
        try {
            BigDecimal heightMeters = heightCm.divide(new BigDecimal("100"), 4, java.math.RoundingMode.HALF_UP);
            BigDecimal heightSquared = heightMeters.multiply(heightMeters);
            return weightKg.divide(heightSquared, 1, java.math.RoundingMode.HALF_UP);
        } catch (ArithmeticException e) {
            return null;
        }
    }

    /**
     * Determines standard clinical BMI category.
     */
    public String getBmiCategory() {
        BigDecimal bmi = calculateBmi();
        if (bmi == null) {
            return "Not Set";
        }
        double val = bmi.doubleValue();
        if (val < 18.5) {
            return "Underweight";
        } else if (val < 25.0) {
            return "Normal weight";
        } else if (val < 30.0) {
            return "Overweight";
        } else {
            return "Obese";
        }
    }

    @Override
    public String toString() {
        return "FitnessProfile{" +
                "profileId=" + profileId +
                ", userId=" + userId +
                ", age=" + age +
                ", gender=" + gender +
                ", heightCm=" + heightCm +
                ", weightKg=" + weightKg +
                ", activityLevel=" + activityLevel +
                ", preferredEnvironment=" + preferredEnvironment +
                '}';
    }
}
