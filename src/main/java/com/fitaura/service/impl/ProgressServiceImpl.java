package com.fitaura.service.impl;

import com.fitaura.dao.FitnessGoalDAO;
import com.fitaura.dao.FitnessProfileDAO;
import com.fitaura.dao.ProgressDAO;
import com.fitaura.dao.WorkoutDAO;
import com.fitaura.dao.impl.FitnessGoalDAOImpl;
import com.fitaura.dao.impl.FitnessProfileDAOImpl;
import com.fitaura.dao.impl.ProgressDAOImpl;
import com.fitaura.dao.impl.WorkoutDAOImpl;
import com.fitaura.model.FitnessGoal;
import com.fitaura.model.FitnessProfile;
import com.fitaura.model.Workout;
import com.fitaura.service.ProgressService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.sql.Date;
import java.time.DayOfWeek;
import java.time.LocalDate;
import java.time.format.TextStyle;
import java.time.temporal.TemporalAdjusters;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Optional;

/**
 * Production implementation of ProgressService.
 * Retrieves real aggregated MySQL metrics and computes health estimates.
 */
public class ProgressServiceImpl implements ProgressService {

    private static final Logger logger = LoggerFactory.getLogger(ProgressServiceImpl.class);

    private final ProgressDAO progressDAO;
    private final FitnessGoalDAO fitnessGoalDAO;
    private final FitnessProfileDAO fitnessProfileDAO;
    private final WorkoutDAO workoutDAO;

    public ProgressServiceImpl() {
        this(new ProgressDAOImpl(), new FitnessGoalDAOImpl(), new FitnessProfileDAOImpl(), new WorkoutDAOImpl());
    }

    public ProgressServiceImpl(ProgressDAO progressDAO, FitnessGoalDAO fitnessGoalDAO,
                               FitnessProfileDAO fitnessProfileDAO, WorkoutDAO workoutDAO) {
        this.progressDAO = progressDAO;
        this.fitnessGoalDAO = fitnessGoalDAO;
        this.fitnessProfileDAO = fitnessProfileDAO;
        this.workoutDAO = workoutDAO;
    }

    @Override
    public ProgressSummary getUserProgressSummary(Integer userId) {
        ProgressSummary summary = new ProgressSummary();
        if (userId == null) {
            return summary;
        }

        LocalDate today = LocalDate.now();

        // 1. Overall Totals
        summary.setTotalWorkouts(progressDAO.countWorkoutsByUser(userId));
        summary.setTotalDurationMinutes(progressDAO.sumDurationByUser(userId));
        summary.setTotalCaloriesBurned(progressDAO.sumCaloriesByUser(userId));

        // 2. Weekly Activity (Monday to Sunday)
        LocalDate weekStart = today.with(TemporalAdjusters.previousOrSame(DayOfWeek.MONDAY));
        LocalDate weekEnd = weekStart.plusDays(6);
        Date sqlWeekStart = Date.valueOf(weekStart);
        Date sqlWeekEnd = Date.valueOf(weekEnd);

        summary.setWeekStartDate(weekStart);
        summary.setWeekEndDate(weekEnd);
        summary.setWeeklyWorkouts(progressDAO.countWorkoutsForDateRange(userId, sqlWeekStart, sqlWeekEnd));
        summary.setWeeklyDurationMinutes(progressDAO.sumDurationForDateRange(userId, sqlWeekStart, sqlWeekEnd));
        summary.setWeeklyCaloriesBurned(progressDAO.sumCaloriesForDateRange(userId, sqlWeekStart, sqlWeekEnd));
        summary.setWeeklyDayMinutes(progressDAO.getWeeklyDayMinutes(userId, sqlWeekStart, sqlWeekEnd));

        // 3. Monthly Activity (1st of month to end of month)
        LocalDate monthStart = today.withDayOfMonth(1);
        LocalDate monthEnd = monthStart.plusMonths(1).minusDays(1);
        Date sqlMonthStart = Date.valueOf(monthStart);
        Date sqlMonthEnd = Date.valueOf(monthEnd);

        summary.setMonthName(today.getMonth().getDisplayName(TextStyle.FULL, Locale.ENGLISH));
        summary.setYear(today.getYear());
        summary.setMonthlyWorkouts(progressDAO.countWorkoutsForDateRange(userId, sqlMonthStart, sqlMonthEnd));
        summary.setMonthlyDurationMinutes(progressDAO.sumDurationForDateRange(userId, sqlMonthStart, sqlMonthEnd));
        summary.setMonthlyCaloriesBurned(progressDAO.sumCaloriesForDateRange(userId, sqlMonthStart, sqlMonthEnd));

        // 4. Breakdown by Activity Type
        summary.setTypeBreakdown(progressDAO.getWorkoutTypeStats(userId));

        // 5. Active Goals
        List<FitnessGoal> activeGoals = fitnessGoalDAO.findActiveByUserId(userId);
        summary.setActiveGoals(activeGoals);

        // 6. Recent Workouts (up to 5)
        List<Workout> recent = workoutDAO.findByUserId(userId, 5, 0);
        summary.setRecentWorkouts(recent);

        // 7. Informational Health Estimates from FitnessProfile
        Optional<FitnessProfile> profileOpt = fitnessProfileDAO.findByUserId(userId);
        if (profileOpt.isPresent()) {
            FitnessProfile profile = profileOpt.get();
            Double bmi = calculateBmi(profile);
            summary.setBmi(bmi);
            summary.setBmiCategory(getBmiCategory(bmi));

            Integer bmr = calculateBmr(profile);
            summary.setBmr(bmr);

            Integer dailyCalories = calculateEstimatedDailyCalories(profile);
            summary.setDailyCalories(dailyCalories);

            boolean isComplete = profile.getHeightCm() != null && profile.getWeightKg() != null &&
                                 profile.getAge() != null && profile.getGender() != null;
            summary.setProfileComplete(isComplete);
        } else {
            summary.setProfileComplete(false);
        }

        return summary;
    }

    @Override
    public Double calculateBmi(FitnessProfile profile) {
        if (profile == null || profile.getHeightCm() == null || profile.getWeightKg() == null) {
            return null;
        }
        double heightCm = profile.getHeightCm().doubleValue();
        double weightKg = profile.getWeightKg().doubleValue();

        if (heightCm <= 0 || weightKg <= 0) {
            return null;
        }

        double heightM = heightCm / 100.0;
        double bmi = weightKg / (heightM * heightM);

        // Round to 1 decimal place
        return BigDecimal.valueOf(bmi).setScale(1, RoundingMode.HALF_UP).doubleValue();
    }

    @Override
    public String getBmiCategory(Double bmi) {
        if (bmi == null) {
            return "Unknown";
        }
        if (bmi < 18.5) {
            return "Underweight";
        } else if (bmi < 25.0) {
            return "Normal weight";
        } else if (bmi < 30.0) {
            return "Overweight";
        } else {
            return "Obesity";
        }
    }

    @Override
    public Integer calculateBmr(FitnessProfile profile) {
        if (profile == null || profile.getHeightCm() == null || profile.getWeightKg() == null ||
            profile.getAge() == null || profile.getAge() <= 0) {
            return null;
        }

        double heightCm = profile.getHeightCm().doubleValue();
        double weightKg = profile.getWeightKg().doubleValue();
        int age = profile.getAge();

        if (heightCm <= 0 || weightKg <= 0) {
            return null;
        }

        FitnessProfile.Gender gender = profile.getGender();

        // Mifflin-St Jeor Equation:
        // Male: 10 * weight(kg) + 6.25 * height(cm) - 5 * age + 5
        // Female: 10 * weight(kg) + 6.25 * height(cm) - 5 * age - 161
        // Other / Unspecified: average offset (-78)
        double base = (10.0 * weightKg) + (6.25 * heightCm) - (5.0 * age);

        double bmr;
        if (gender == FitnessProfile.Gender.MALE) {
            bmr = base + 5.0;
        } else if (gender == FitnessProfile.Gender.FEMALE) {
            bmr = base - 161.0;
        } else {
            bmr = base - 78.0;
        }

        return (int) Math.round(Math.max(500, bmr));
    }

    @Override
    public Integer calculateEstimatedDailyCalories(FitnessProfile profile) {
        Integer bmr = calculateBmr(profile);
        if (bmr == null) {
            return null;
        }

        FitnessProfile.ActivityLevel level = profile != null ? profile.getActivityLevel() : null;
        double multiplier;

        if (level == FitnessProfile.ActivityLevel.LIGHT) {
            multiplier = 1.375;
        } else if (level == FitnessProfile.ActivityLevel.MODERATE) {
            multiplier = 1.55;
        } else if (level == FitnessProfile.ActivityLevel.ACTIVE) {
            multiplier = 1.725;
        } else if (level == FitnessProfile.ActivityLevel.VERY_ACTIVE) {
            multiplier = 1.9;
        } else {
            // BEGINNER / Sedentary baseline
            multiplier = 1.2;
        }

        return (int) Math.round(bmr * multiplier);
    }
}
