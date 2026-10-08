package com.fitaura.model;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

/**
 * Aggregated personalized guidance bundle for an authenticated user.
 * Carries top curated recommendation, supporting advice, category slots,
 * and contextual evaluation metadata.
 */
public class UserGuidance implements Serializable {

    private static final long serialVersionUID = 1L;

    private Integer userId;
    private String userDisplayName;

    // Top Curated Primary Recommendation
    private GuidanceRecommendation primaryRecommendation;

    // Supporting Curated Recommendations (Deduplicated)
    private List<GuidanceRecommendation> supportingRecommendations = new ArrayList<>();

    // Direct Categorized Recommendations
    private GuidanceRecommendation dailyTip;
    private GuidanceRecommendation workoutRecommendation;
    private GuidanceRecommendation goalAdvice;
    private GuidanceRecommendation nutritionGuidance;
    private GuidanceRecommendation exerciseSuggestion;

    private List<GuidanceRecommendation> additionalRecommendations = new ArrayList<>();

    // Context metadata
    private boolean profileIncomplete;
    private boolean noWorkoutsLogged;
    private boolean noActiveGoals;
    private String contextSummary;
    private String environmentLabel;
    private String bmiContext;

    public UserGuidance() {
    }

    public Integer getUserId() {
        return userId;
    }

    public void setUserId(Integer userId) {
        this.userId = userId;
    }

    public String getUserDisplayName() {
        return userDisplayName;
    }

    public void setUserDisplayName(String userDisplayName) {
        this.userDisplayName = userDisplayName;
    }

    public GuidanceRecommendation getPrimaryRecommendation() {
        return primaryRecommendation;
    }

    public void setPrimaryRecommendation(GuidanceRecommendation primaryRecommendation) {
        this.primaryRecommendation = primaryRecommendation;
    }

    public List<GuidanceRecommendation> getSupportingRecommendations() {
        return supportingRecommendations;
    }

    public void setSupportingRecommendations(List<GuidanceRecommendation> supportingRecommendations) {
        this.supportingRecommendations = supportingRecommendations;
    }

    public void addSupportingRecommendation(GuidanceRecommendation rec) {
        if (rec != null) {
            this.supportingRecommendations.add(rec);
        }
    }

    public GuidanceRecommendation getDailyTip() {
        return dailyTip;
    }

    public void setDailyTip(GuidanceRecommendation dailyTip) {
        this.dailyTip = dailyTip;
    }

    public GuidanceRecommendation getWorkoutRecommendation() {
        return workoutRecommendation;
    }

    public void setWorkoutRecommendation(GuidanceRecommendation workoutRecommendation) {
        this.workoutRecommendation = workoutRecommendation;
    }

    public GuidanceRecommendation getGoalAdvice() {
        return goalAdvice;
    }

    public void setGoalAdvice(GuidanceRecommendation goalAdvice) {
        this.goalAdvice = goalAdvice;
    }

    public GuidanceRecommendation getNutritionGuidance() {
        return nutritionGuidance;
    }

    public void setNutritionGuidance(GuidanceRecommendation nutritionGuidance) {
        this.nutritionGuidance = nutritionGuidance;
    }

    public GuidanceRecommendation getExerciseSuggestion() {
        return exerciseSuggestion;
    }

    public void setExerciseSuggestion(GuidanceRecommendation exerciseSuggestion) {
        this.exerciseSuggestion = exerciseSuggestion;
    }

    public List<GuidanceRecommendation> getAdditionalRecommendations() {
        return additionalRecommendations;
    }

    public void setAdditionalRecommendations(List<GuidanceRecommendation> additionalRecommendations) {
        this.additionalRecommendations = additionalRecommendations;
    }

    public void addAdditionalRecommendation(GuidanceRecommendation rec) {
        if (rec != null) {
            this.additionalRecommendations.add(rec);
        }
    }

    public boolean isProfileIncomplete() {
        return profileIncomplete;
    }

    public void setProfileIncomplete(boolean profileIncomplete) {
        this.profileIncomplete = profileIncomplete;
    }

    public boolean isNoWorkoutsLogged() {
        return noWorkoutsLogged;
    }

    public void setNoWorkoutsLogged(boolean noWorkoutsLogged) {
        this.noWorkoutsLogged = noWorkoutsLogged;
    }

    public boolean isNoActiveGoals() {
        return noActiveGoals;
    }

    public void setNoActiveGoals(boolean noActiveGoals) {
        this.noActiveGoals = noActiveGoals;
    }

    public String getContextSummary() {
        return contextSummary;
    }

    public void setContextSummary(String contextSummary) {
        this.contextSummary = contextSummary;
    }

    public String getEnvironmentLabel() {
        return environmentLabel;
    }

    public void setEnvironmentLabel(String environmentLabel) {
        this.environmentLabel = environmentLabel;
    }

    public String getBmiContext() {
        return bmiContext;
    }

    public void setBmiContext(String bmiContext) {
        this.bmiContext = bmiContext;
    }
}
