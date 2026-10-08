package com.fitaura.service;

import com.fitaura.model.FitnessGoal;
import com.fitaura.model.FitnessProfile;
import com.fitaura.model.GuidanceRecommendation;
import com.fitaura.model.UserGuidance;
import com.fitaura.model.Workout;

import java.util.List;

/**
 * Service interface governing deterministic, rule-based personalized guidance generation.
 * Synthesizes profile settings, goal milestones, workout history, and progress estimates.
 */
public interface GuidanceService {

    /**
     * Generates a complete personalized guidance summary for the authenticated user.
     *
     * @param userId the authenticated user ID
     * @return UserGuidance containing category-specific recommendations
     */
    UserGuidance generateUserGuidance(Integer userId);

    /**
     * Generates a daily wellness/motivation tip based on user context.
     */
    GuidanceRecommendation generateDailyTip(FitnessProfile profile, List<FitnessGoal> activeGoals, List<Workout> recentWorkouts);

    /**
     * Generates a workout frequency and intensity recommendation based on recent activity.
     */
    GuidanceRecommendation generateWorkoutRecommendation(FitnessProfile profile, List<FitnessGoal> activeGoals,
                                                         List<Workout> recentWorkouts, int weeklyWorkouts);

    /**
     * Generates goal strategy advice based on active goal types and progress percentages.
     */
    GuidanceRecommendation generateGoalAdvice(FitnessProfile profile, List<FitnessGoal> activeGoals, List<FitnessGoal> allGoals);

    /**
     * Generates general wellness and nutrition guidance based on profile and goal alignment.
     */
    GuidanceRecommendation generateNutritionGuidance(FitnessProfile profile, List<FitnessGoal> activeGoals, Integer dailyCalories);

    /**
     * Generates specific exercise and training focus suggestions based on preferred environment and goals.
     */
    GuidanceRecommendation generateExerciseSuggestion(FitnessProfile profile, List<FitnessGoal> activeGoals, List<Workout> recentWorkouts);
}
