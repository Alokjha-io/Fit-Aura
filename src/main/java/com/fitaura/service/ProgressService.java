package com.fitaura.service;

import com.fitaura.dao.ProgressDAO;
import com.fitaura.model.FitnessGoal;
import com.fitaura.model.FitnessProfile;
import com.fitaura.model.Workout;

import java.io.Serializable;
import java.time.LocalDate;
import java.util.List;
import java.util.Map;

/**
 * Service governing comprehensive fitness progress calculations,
 * workout aggregations, weekly and monthly activity trends, and health estimates.
 */
public interface ProgressService {

    /**
     * Data Transfer Object aggregating all authenticated user progress metrics.
     */
    public static class ProgressSummary implements Serializable {
        private static final long serialVersionUID = 1L;

        // Overall Totals
        private int totalWorkouts;
        private int totalDurationMinutes;
        private int totalCaloriesBurned;

        // Weekly Activity (Current Calendar Week: Monday to Sunday)
        private int weeklyWorkouts;
        private int weeklyDurationMinutes;
        private int weeklyCaloriesBurned;
        private LocalDate weekStartDate;
        private LocalDate weekEndDate;
        private Map<Integer, Integer> weeklyDayMinutes; // 1=Mon .. 7=Sun

        // Monthly Activity (Current Calendar Month)
        private int monthlyWorkouts;
        private int monthlyDurationMinutes;
        private int monthlyCaloriesBurned;
        private String monthName;
        private int year;

        // Breakdown & Lists
        private Map<String, ProgressDAO.WorkoutTypeStat> typeBreakdown;
        private List<FitnessGoal> activeGoals;
        private List<Workout> recentWorkouts;

        // Informational Health Calculations (from FitnessProfile)
        private Double bmi;
        private String bmiCategory;
        private Integer bmr;
        private Integer dailyCalories;
        private boolean profileComplete;

        public int getTotalWorkouts() { return totalWorkouts; }
        public void setTotalWorkouts(int totalWorkouts) { this.totalWorkouts = totalWorkouts; }

        public int getTotalDurationMinutes() { return totalDurationMinutes; }
        public void setTotalDurationMinutes(int totalDurationMinutes) { this.totalDurationMinutes = totalDurationMinutes; }

        public int getTotalCaloriesBurned() { return totalCaloriesBurned; }
        public void setTotalCaloriesBurned(int totalCaloriesBurned) { this.totalCaloriesBurned = totalCaloriesBurned; }

        public int getWeeklyWorkouts() { return weeklyWorkouts; }
        public void setWeeklyWorkouts(int weeklyWorkouts) { this.weeklyWorkouts = weeklyWorkouts; }

        public int getWeeklyDurationMinutes() { return weeklyDurationMinutes; }
        public void setWeeklyDurationMinutes(int weeklyDurationMinutes) { this.weeklyDurationMinutes = weeklyDurationMinutes; }

        public int getWeeklyCaloriesBurned() { return weeklyCaloriesBurned; }
        public void setWeeklyCaloriesBurned(int weeklyCaloriesBurned) { this.weeklyCaloriesBurned = weeklyCaloriesBurned; }

        public LocalDate getWeekStartDate() { return weekStartDate; }
        public void setWeekStartDate(LocalDate weekStartDate) { this.weekStartDate = weekStartDate; }

        public LocalDate getWeekEndDate() { return weekEndDate; }
        public void setWeekEndDate(LocalDate weekEndDate) { this.weekEndDate = weekEndDate; }

        public Map<Integer, Integer> getWeeklyDayMinutes() { return weeklyDayMinutes; }
        public void setWeeklyDayMinutes(Map<Integer, Integer> weeklyDayMinutes) { this.weeklyDayMinutes = weeklyDayMinutes; }

        public int getMonthlyWorkouts() { return monthlyWorkouts; }
        public void setMonthlyWorkouts(int monthlyWorkouts) { this.monthlyWorkouts = monthlyWorkouts; }

        public int getMonthlyDurationMinutes() { return monthlyDurationMinutes; }
        public void setMonthlyDurationMinutes(int monthlyDurationMinutes) { this.monthlyDurationMinutes = monthlyDurationMinutes; }

        public int getMonthlyCaloriesBurned() { return monthlyCaloriesBurned; }
        public void setMonthlyCaloriesBurned(int monthlyCaloriesBurned) { this.monthlyCaloriesBurned = monthlyCaloriesBurned; }

        public String getMonthName() { return monthName; }
        public void setMonthName(String monthName) { this.monthName = monthName; }

        public int getYear() { return year; }
        public void setYear(int year) { this.year = year; }

        public Map<String, ProgressDAO.WorkoutTypeStat> getTypeBreakdown() { return typeBreakdown; }
        public void setTypeBreakdown(Map<String, ProgressDAO.WorkoutTypeStat> typeBreakdown) { this.typeBreakdown = typeBreakdown; }

        public List<FitnessGoal> getActiveGoals() { return activeGoals; }
        public void setActiveGoals(List<FitnessGoal> activeGoals) { this.activeGoals = activeGoals; }

        public List<Workout> getRecentWorkouts() { return recentWorkouts; }
        public void setRecentWorkouts(List<Workout> recentWorkouts) { this.recentWorkouts = recentWorkouts; }

        public Double getBmi() { return bmi; }
        public void setBmi(Double bmi) { this.bmi = bmi; }

        public String getBmiCategory() { return bmiCategory; }
        public void setBmiCategory(String bmiCategory) { this.bmiCategory = bmiCategory; }

        public Integer getBmr() { return bmr; }
        public void setBmr(Integer bmr) { this.bmr = bmr; }

        public Integer getDailyCalories() { return dailyCalories; }
        public void setDailyCalories(Integer dailyCalories) { this.dailyCalories = dailyCalories; }

        public boolean isProfileComplete() { return profileComplete; }
        public void setProfileComplete(boolean profileComplete) { this.profileComplete = profileComplete; }
    }

    /**
     * Builds and retrieves the full progress summary for the user.
     */
    ProgressSummary getUserProgressSummary(Integer userId);

    /**
     * Calculates Body Mass Index (BMI = weight_kg / height_m^2).
     * Returns null if height or weight is missing.
     */
    Double calculateBmi(FitnessProfile profile);

    /**
     * Returns WHO BMI classification category.
     */
    String getBmiCategory(Double bmi);

    /**
     * Calculates Basal Metabolic Rate (BMR) using Mifflin-St Jeor formula.
     * Returns null if required profile attributes are missing.
     */
    Integer calculateBmr(FitnessProfile profile);

    /**
     * Calculates estimated Total Daily Energy Expenditure (TDEE) based on BMR and activity level.
     */
    Integer calculateEstimatedDailyCalories(FitnessProfile profile);
}
