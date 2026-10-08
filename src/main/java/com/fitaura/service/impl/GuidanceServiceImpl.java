package com.fitaura.service.impl;

import com.fitaura.dao.FitnessGoalDAO;
import com.fitaura.dao.FitnessProfileDAO;
import com.fitaura.dao.ProgressDAO;
import com.fitaura.dao.UserDAO;
import com.fitaura.dao.WorkoutDAO;
import com.fitaura.dao.impl.FitnessGoalDAOImpl;
import com.fitaura.dao.impl.FitnessProfileDAOImpl;
import com.fitaura.dao.impl.ProgressDAOImpl;
import com.fitaura.dao.impl.UserDAOImpl;
import com.fitaura.dao.impl.WorkoutDAOImpl;
import com.fitaura.model.FitnessGoal;
import com.fitaura.model.FitnessProfile;
import com.fitaura.model.GuidanceRecommendation;
import com.fitaura.model.User;
import com.fitaura.model.UserGuidance;
import com.fitaura.model.Workout;
import com.fitaura.service.GuidanceService;
import com.fitaura.service.ProgressService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.sql.Date;
import java.time.DayOfWeek;
import java.time.LocalDate;
import java.time.temporal.ChronoUnit;
import java.time.temporal.TemporalAdjusters;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

/**
 * Production implementation of GuidanceService for Phase 7B.
 * Evaluates authenticated user fitness profiles, active goal types, target dates,
 * recent workout frequency, activity variety, and calculated metabolic baselines.
 */
public class GuidanceServiceImpl implements GuidanceService {

    private static final Logger logger = LoggerFactory.getLogger(GuidanceServiceImpl.class);

    private final UserDAO userDAO;
    private final FitnessProfileDAO fitnessProfileDAO;
    private final FitnessGoalDAO fitnessGoalDAO;
    private final WorkoutDAO workoutDAO;
    private final ProgressDAO progressDAO;
    private final ProgressService progressService;

    public GuidanceServiceImpl() {
        this(new UserDAOImpl(), new FitnessProfileDAOImpl(), new FitnessGoalDAOImpl(),
             new WorkoutDAOImpl(), new ProgressDAOImpl(), new ProgressServiceImpl());
    }

    public GuidanceServiceImpl(UserDAO userDAO, FitnessProfileDAO fitnessProfileDAO,
                               FitnessGoalDAO fitnessGoalDAO, WorkoutDAO workoutDAO,
                               ProgressDAO progressDAO, ProgressService progressService) {
        this.userDAO = userDAO;
        this.fitnessProfileDAO = fitnessProfileDAO;
        this.fitnessGoalDAO = fitnessGoalDAO;
        this.workoutDAO = workoutDAO;
        this.progressDAO = progressDAO;
        this.progressService = progressService;
    }

    @Override
    public UserGuidance generateUserGuidance(Integer userId) {
        UserGuidance guidance = new UserGuidance();
        if (userId == null) {
            return guidance;
        }

        guidance.setUserId(userId);

        // 1. Fetch User Info
        Optional<User> userOpt = userDAO.findById(userId);
        if (userOpt.isPresent()) {
            User user = userOpt.get();
            String name = (user.getDisplayName() != null && !user.getDisplayName().trim().isEmpty()) ?
                    user.getDisplayName() : user.getFullName();
            guidance.setUserDisplayName(name);
        }

        // 2. Fetch Profile, Goals, and Workouts
        Optional<FitnessProfile> profileOpt = fitnessProfileDAO.findByUserId(userId);
        FitnessProfile profile = profileOpt.orElse(null);

        List<FitnessGoal> activeGoals = fitnessGoalDAO.findActiveByUserId(userId);
        List<FitnessGoal> allGoals = fitnessGoalDAO.findByUserId(userId);
        List<Workout> recentWorkouts = workoutDAO.findByUserId(userId, 10, 0);
        int totalWorkouts = workoutDAO.countByUserId(userId);

        // Weekly activity calculation
        LocalDate today = LocalDate.now();
        LocalDate weekStart = today.with(TemporalAdjusters.previousOrSame(DayOfWeek.MONDAY));
        LocalDate weekEnd = weekStart.plusDays(6);
        int weeklyWorkouts = workoutDAO.countByUserIdAndDateRange(userId, Date.valueOf(weekStart), Date.valueOf(weekEnd));

        // Metabolic calculations
        Integer dailyCalories = null;
        Double bmi = null;
        String bmiCat = null;
        if (profile != null) {
            dailyCalories = progressService.calculateEstimatedDailyCalories(profile);
            bmi = progressService.calculateBmi(profile);
            bmiCat = progressService.getBmiCategory(bmi);
            if (profile.getPreferredEnvironment() != null) {
                guidance.setEnvironmentLabel(profile.getPreferredEnvironment().name());
            }
            if (bmi != null && bmiCat != null && !"Unknown".equalsIgnoreCase(bmiCat)) {
                guidance.setBmiContext(bmiCat + " (BMI " + bmi + ")");
            }
        }

        boolean profileIncomplete = (profile == null || profile.getHeightCm() == null ||
                                     profile.getWeightKg() == null || profile.getActivityLevel() == null);
        guidance.setProfileIncomplete(profileIncomplete);
        guidance.setNoWorkoutsLogged(totalWorkouts == 0);
        guidance.setNoActiveGoals(activeGoals.isEmpty());

        // 3. Generate Candidate Recommendations
        GuidanceRecommendation dailyTip = generateDailyTip(profile, activeGoals, recentWorkouts);
        GuidanceRecommendation workoutRec = generateWorkoutRecommendation(profile, activeGoals, recentWorkouts, weeklyWorkouts);
        GuidanceRecommendation goalRec = generateGoalAdvice(profile, activeGoals, allGoals);
        GuidanceRecommendation nutritionRec = generateNutritionGuidance(profile, activeGoals, dailyCalories);
        GuidanceRecommendation exerciseRec = generateExerciseSuggestion(profile, activeGoals, recentWorkouts);

        guidance.setDailyTip(dailyTip);
        guidance.setWorkoutRecommendation(workoutRec);
        guidance.setGoalAdvice(goalRec);
        guidance.setNutritionGuidance(nutritionRec);
        guidance.setExerciseSuggestion(exerciseRec);

        // 4. Build Curated & Deduplicated Recommendations
        List<GuidanceRecommendation> candidates = new ArrayList<>();

        // Add Profile completion prompt if profile is incomplete
        if (profileIncomplete) {
            GuidanceRecommendation profilePrompt = new GuidanceRecommendation(
                    GuidanceRecommendation.Category.DAILY_TIP,
                    "Complete Your Body Metrics",
                    "Adding your height, weight, and activity level enables calculated BMI, BMR, and daily calorie guidance tailored to you.",
                    GuidanceRecommendation.Priority.HIGH,
                    "Based on incomplete profile metrics",
                    "/user/profile",
                    "Update Metrics",
                    "bi-sliders"
            );
            candidates.add(profilePrompt);
        }

        // Add Goal deadline / milestone alerts if applicable
        GuidanceRecommendation deadlineRec = evaluateGoalDeadline(activeGoals, today);
        if (deadlineRec != null) {
            candidates.add(deadlineRec);
        }

        // Add Workout habit & variety evaluations
        GuidanceRecommendation habitRec = evaluateWorkoutHabits(recentWorkouts, totalWorkouts, weeklyWorkouts, today);
        if (habitRec != null) {
            candidates.add(habitRec);
        }

        // Add categorized recommendations
        candidates.add(workoutRec);
        candidates.add(goalRec);
        candidates.add(nutritionRec);
        candidates.add(exerciseRec);

        // 5. Deduplicate and select Top Primary and Supporting Recommendations
        Map<String, GuidanceRecommendation> deduplicated = new LinkedHashMap<>();
        for (GuidanceRecommendation rec : candidates) {
            if (rec != null && !deduplicated.containsKey(rec.getTitle())) {
                deduplicated.put(rec.getTitle(), rec);
            }
        }

        List<GuidanceRecommendation> distinctList = new ArrayList<>(deduplicated.values());

        // Sort by Priority: HIGH first, then MEDIUM, then LOW
        distinctList.sort((a, b) -> Integer.compare(priorityRank(a.getPriority()), priorityRank(b.getPriority())));

        if (!distinctList.isEmpty()) {
            guidance.setPrimaryRecommendation(distinctList.get(0));
            List<GuidanceRecommendation> supporting = new ArrayList<>();
            for (int i = 1; i < distinctList.size() && supporting.size() < 4; i++) {
                supporting.add(distinctList.get(i));
            }
            guidance.setSupportingRecommendations(supporting);
        } else {
            guidance.setPrimaryRecommendation(dailyTip);
        }

        logger.info("Generated advanced rule-based guidance for user {}", userId);
        return guidance;
    }

    private int priorityRank(GuidanceRecommendation.Priority p) {
        if (p == null) return 2;
        switch (p) {
            case HIGH: return 0;
            case MEDIUM: return 1;
            case LOW: return 2;
            default: return 2;
        }
    }

    @Override
    public GuidanceRecommendation generateDailyTip(FitnessProfile profile, List<FitnessGoal> activeGoals, List<Workout> recentWorkouts) {
        if (recentWorkouts == null || recentWorkouts.isEmpty()) {
            return new GuidanceRecommendation(
                    GuidanceRecommendation.Category.DAILY_TIP,
                    "Starting with Small Wins",
                    "A short 15-minute walk or gentle stretch session can help build initial momentum and establish a positive routine.",
                    GuidanceRecommendation.Priority.LOW,
                    "No workout sessions recorded yet",
                    "/workouts/add",
                    "Log First Workout",
                    "bi-stars"
            );
        }

        if (activeGoals != null && !activeGoals.isEmpty()) {
            FitnessGoal primaryGoal = activeGoals.get(0);
            if (primaryGoal.getGoalType() == FitnessGoal.GoalType.WEIGHT_LOSS) {
                return new GuidanceRecommendation(
                        GuidanceRecommendation.Category.DAILY_TIP,
                        "Hydration & Mindful Satiety",
                        "Drinking a glass of water before meals and staying well hydrated during training supports energy levels and natural appetite cues.",
                        GuidanceRecommendation.Priority.LOW,
                        "Aligned with your active Weight Loss goal",
                        null,
                        null,
                        "bi-droplet-fill"
                );
            } else if (primaryGoal.getGoalType() == FitnessGoal.GoalType.STRENGTH ||
                       primaryGoal.getGoalType() == FitnessGoal.GoalType.MUSCLE_GAIN) {
                return new GuidanceRecommendation(
                        GuidanceRecommendation.Category.DAILY_TIP,
                        "Quality Sleep for Muscle Repair",
                        "Aiming for 7 to 9 hours of quality sleep provides the physiological recovery needed for tissue synthesis and strength gains.",
                        GuidanceRecommendation.Priority.LOW,
                        "Aligned with your Muscle & Strength focus",
                        null,
                        null,
                        "bi-moon-stars-fill"
                );
            } else if (primaryGoal.getGoalType() == FitnessGoal.GoalType.ENDURANCE) {
                return new GuidanceRecommendation(
                        GuidanceRecommendation.Category.DAILY_TIP,
                        "Pacing & Aerobic Efficiency",
                        "Keeping your steady runs or cycling sessions at an easy conversational effort helps build aerobic efficiency without excessive fatigue.",
                        GuidanceRecommendation.Priority.LOW,
                        "Aligned with your Endurance focus",
                        null,
                        null,
                        "bi-speedometer"
                );
            }
        }

        return new GuidanceRecommendation(
                GuidanceRecommendation.Category.DAILY_TIP,
                "Consistency Over Intensity",
                "Regular moderate activity performed consistently over weeks creates far greater health improvements than occasional exhaustive workouts.",
                GuidanceRecommendation.Priority.LOW,
                "General Wellness Baseline",
                null,
                null,
                "bi-heart-pulse-fill"
        );
    }

    @Override
    public GuidanceRecommendation generateWorkoutRecommendation(FitnessProfile profile, List<FitnessGoal> activeGoals,
                                                                 List<Workout> recentWorkouts, int weeklyWorkouts) {
        if (recentWorkouts == null || recentWorkouts.isEmpty()) {
            return new GuidanceRecommendation(
                    GuidanceRecommendation.Category.WORKOUT,
                    "Establish a Foundation Routine",
                    "Consider scheduling 2 to 3 manageable sessions this week (20-30 minutes each), such as brisk walking, light cycling, or bodyweight exercises.",
                    GuidanceRecommendation.Priority.HIGH,
                    "Starting a new training routine",
                    "/workouts/add",
                    "Plan Workout",
                    "bi-calendar-plus"
            );
        }

        // Check for high-intensity fatigue
        long highIntensityCount = recentWorkouts.stream()
                .limit(3)
                .filter(w -> w.getIntensity() == Workout.Intensity.HIGH)
                .count();

        if (highIntensityCount >= 2 && weeklyWorkouts >= 4) {
            return new GuidanceRecommendation(
                    GuidanceRecommendation.Category.WORKOUT,
                    "Incorporate Active Recovery",
                    "You have completed multiple high-intensity sessions recently. Consider a dedicated active recovery day (light walking or mobility) to prevent cumulative fatigue.",
                    GuidanceRecommendation.Priority.HIGH,
                    "High recent intensity and volume (" + weeklyWorkouts + " sessions this week)",
                    null,
                    null,
                    "bi-shield-check"
            );
        }

        if (weeklyWorkouts >= 5) {
            return new GuidanceRecommendation(
                    GuidanceRecommendation.Category.WORKOUT,
                    "Optimal Rest & Adaptation",
                    "You have maintained great workout frequency this week (" + weeklyWorkouts + " sessions). Ensure at least 1-2 days of adequate rest for optimal muscular adaptation.",
                    GuidanceRecommendation.Priority.MEDIUM,
                    "High weekly frequency (" + weeklyWorkouts + " sessions)",
                    null,
                    null,
                    "bi-shield-check"
            );
        } else if (weeklyWorkouts <= 1) {
            return new GuidanceRecommendation(
                    GuidanceRecommendation.Category.WORKOUT,
                    "Build Weekly Momentum",
                    "Consider adding one extra 25-35 minute workout this week to help build progressive momentum toward your weekly health goals.",
                    GuidanceRecommendation.Priority.MEDIUM,
                    "Your activity is on the lower side this week (" + weeklyWorkouts + " session)",
                    "/workouts/add",
                    "Log a Session",
                    "bi-plus-circle"
            );
        } else {
            return new GuidanceRecommendation(
                    GuidanceRecommendation.Category.WORKOUT,
                    "Maintain Steady Training Cadence",
                    "You are maintaining a balanced weekly cadence (" + weeklyWorkouts + " sessions). Continue combining aerobic endurance with functional strength.",
                    GuidanceRecommendation.Priority.LOW,
                    "Steady training cadence this week",
                    "/workouts",
                    "View History",
                    "bi-activity"
            );
        }
    }

    @Override
    public GuidanceRecommendation generateGoalAdvice(FitnessProfile profile, List<FitnessGoal> activeGoals, List<FitnessGoal> allGoals) {
        if (activeGoals == null || activeGoals.isEmpty()) {
            return new GuidanceRecommendation(
                    GuidanceRecommendation.Category.GOAL_ADVICE,
                    "Set a Measurable Milestone",
                    "Defining a clear, achievable target—such as a target weight, weekly running distance, or strength milestone—helps maintain daily focus.",
                    GuidanceRecommendation.Priority.HIGH,
                    "No active goals currently set",
                    "/goals/create",
                    "Create Goal",
                    "bi-bullseye"
            );
        }

        FitnessGoal goal = activeGoals.get(0);

        switch (goal.getGoalType()) {
            case WEIGHT_LOSS:
                return new GuidanceRecommendation(
                        GuidanceRecommendation.Category.GOAL_ADVICE,
                        "Weight Loss Strategy",
                        "Pairing moderate aerobic activity with progressive resistance training helps preserve lean muscle mass while fostering steady fat loss.",
                        GuidanceRecommendation.Priority.MEDIUM,
                        "Based on your active Weight Loss goal",
                        "/goals/view?id=" + goal.getGoalId(),
                        "Check Goal",
                        "bi-graph-down-arrow"
                );
            case WEIGHT_GAIN:
            case MUSCLE_GAIN:
                return new GuidanceRecommendation(
                        GuidanceRecommendation.Category.GOAL_ADVICE,
                        "Progressive Muscle Hypertrophy",
                        "Focus on progressive overload by gradually increasing resistance, volume, or repetitions every 1 to 2 weeks, while allowing 48 hours recovery between muscle groups.",
                        GuidanceRecommendation.Priority.MEDIUM,
                        "Based on your active Muscle Gain goal",
                        "/goals/view?id=" + goal.getGoalId(),
                        "Check Goal",
                        "bi-bounding-box"
                );
            case STRENGTH:
                return new GuidanceRecommendation(
                        GuidanceRecommendation.Category.GOAL_ADVICE,
                        "Strength Development & Rest Intervals",
                        "Prioritize multi-joint compound lifts, rest 2-3 minutes between challenging sets, and maintain strict technique over heavier weight.",
                        GuidanceRecommendation.Priority.MEDIUM,
                        "Based on your active Strength goal",
                        "/goals/view?id=" + goal.getGoalId(),
                        "Check Goal",
                        "bi-hammer"
                );
            case ENDURANCE:
                return new GuidanceRecommendation(
                        GuidanceRecommendation.Category.GOAL_ADVICE,
                        "Gradual Aerobic Progression",
                        "A steady conversational pace for aerobic sessions, accompanied by no more than a 10% weekly increase in duration, protects joints while expanding stamina.",
                        GuidanceRecommendation.Priority.MEDIUM,
                        "Based on your active Endurance goal",
                        "/goals/view?id=" + goal.getGoalId(),
                        "Check Goal",
                        "bi-speedometer2"
                );
            case FLEXIBILITY:
                return new GuidanceRecommendation(
                        GuidanceRecommendation.Category.GOAL_ADVICE,
                        "Mobility & Joint Health",
                        "Gentle daily stretching and dynamic mobility drills (10-15 minutes) help reduce muscular tension, improve posture, and support joint longevity.",
                        GuidanceRecommendation.Priority.MEDIUM,
                        "Based on your active Flexibility goal",
                        "/goals/view?id=" + goal.getGoalId(),
                        "Check Goal",
                        "bi-flower1"
                );
            case GENERAL_FITNESS:
            default:
                return new GuidanceRecommendation(
                        GuidanceRecommendation.Category.GOAL_ADVICE,
                        "Balanced Fitness Routine",
                        "Balance your training evenly between cardiovascular endurance, functional resistance, and joint mobility for overall wellness.",
                        GuidanceRecommendation.Priority.LOW,
                        "Based on your General Fitness goal",
                        "/goals/view?id=" + goal.getGoalId(),
                        "Check Goal",
                        "bi-check2-circle"
                );
        }
    }

    @Override
    public GuidanceRecommendation generateNutritionGuidance(FitnessProfile profile, List<FitnessGoal> activeGoals, Integer dailyCalories) {
        String calInfo = dailyCalories != null ?
                " Your estimated daily energy expenditure is approx. " + dailyCalories + " kcal/day." : "";

        if (activeGoals != null && !activeGoals.isEmpty()) {
            FitnessGoal.GoalType type = activeGoals.get(0).getGoalType();
            if (type == FitnessGoal.GoalType.WEIGHT_LOSS) {
                return new GuidanceRecommendation(
                        GuidanceRecommendation.Category.NUTRITION,
                        "Mindful Energy Intake",
                        "Focus on nutrient-dense whole foods, lean proteins, vegetables, and fiber to promote satiety while maintaining a modest caloric deficit." + calInfo,
                        GuidanceRecommendation.Priority.MEDIUM,
                        "Aligned with your Weight Loss goal",
                        null,
                        null,
                        "bi-cup-hot-fill"
                );
            } else if (type == FitnessGoal.GoalType.MUSCLE_GAIN || type == FitnessGoal.GoalType.WEIGHT_GAIN || type == FitnessGoal.GoalType.STRENGTH) {
                return new GuidanceRecommendation(
                        GuidanceRecommendation.Category.NUTRITION,
                        "Adequate Protein & Sustained Energy",
                        "Ensure a regular intake of high-quality protein distributed across meals to support muscle synthesis and post-workout repair." + calInfo,
                        GuidanceRecommendation.Priority.MEDIUM,
                        "Aligned with your Strength & Muscle building goal",
                        null,
                        null,
                        "bi-egg-fried"
                );
            }
        }

        return new GuidanceRecommendation(
                GuidanceRecommendation.Category.NUTRITION,
                "Balanced Daily Nutrition",
                "Incorporate whole grains, healthy fats, colorful vegetables, and balanced hydration to fuel your daily physical activities." + calInfo,
                GuidanceRecommendation.Priority.LOW,
                "General Wellness Baseline",
                null,
                null,
                "bi-apple"
        );
    }

    @Override
    public GuidanceRecommendation generateExerciseSuggestion(FitnessProfile profile, List<FitnessGoal> activeGoals, List<Workout> recentWorkouts) {
        FitnessProfile.PreferredEnvironment env = profile != null ? profile.getPreferredEnvironment() : null;

        if (env == FitnessProfile.PreferredEnvironment.HOME) {
            return new GuidanceRecommendation(
                    GuidanceRecommendation.Category.EXERCISE,
                    "Home Bodyweight & Mobility Drills",
                    "Consider bodyweight squats, push-up variations, glute bridges, and plank holds for an effective full-body workout without bulky equipment.",
                    GuidanceRecommendation.Priority.LOW,
                    "Based on your preferred Home workout setting",
                    "/workouts/add",
                    "Log Home Session",
                    "bi-house-heart"
            );
        } else if (env == FitnessProfile.PreferredEnvironment.OUTDOOR) {
            return new GuidanceRecommendation(
                    GuidanceRecommendation.Category.EXERCISE,
                    "Outdoor Interval & Tempo Sessions",
                    "Consider combining steady-state running or brisk hill walking with outdoor tempo strides to elevate cardiovascular fitness in the fresh air.",
                    GuidanceRecommendation.Priority.LOW,
                    "Based on your preferred Outdoor workout setting",
                    "/workouts/add",
                    "Log Outdoor Session",
                    "bi-tree-fill"
            );
        } else if (env == FitnessProfile.PreferredEnvironment.GYM) {
            return new GuidanceRecommendation(
                    GuidanceRecommendation.Category.EXERCISE,
                    "Gym Compound Movements",
                    "Focus on major compound movements like barbell/dumbbell presses, rows, and leg presses, keeping warm-up sets light to ensure safe form.",
                    GuidanceRecommendation.Priority.LOW,
                    "Based on your preferred Gym workout setting",
                    "/workouts/add",
                    "Log Gym Session",
                    "bi-building"
            );
        } else {
            return new GuidanceRecommendation(
                    GuidanceRecommendation.Category.EXERCISE,
                    "Functional Movement Circuit",
                    "Combine bodyweight movements with moderate cardio intervals (e.g. brisk walking or cycling) for a versatile full-body routine.",
                    GuidanceRecommendation.Priority.LOW,
                    "Based on your Mixed workout setting",
                    "/workouts/add",
                    "Log Workout",
                    "bi-lightning-charge-fill"
            );
        }
    }

    // ==========================================
    // Advanced Evaluation Helpers for Phase 7B
    // ==========================================

    private GuidanceRecommendation evaluateGoalDeadline(List<FitnessGoal> activeGoals, LocalDate today) {
        if (activeGoals == null || activeGoals.isEmpty()) {
            return null;
        }

        for (FitnessGoal goal : activeGoals) {
            if (goal.getTargetDate() != null) {
                LocalDate target = goal.getTargetDate().toLocalDate();
                long daysRemaining = ChronoUnit.DAYS.between(today, target);

                if (daysRemaining < 0) {
                    return new GuidanceRecommendation(
                            GuidanceRecommendation.Category.GOAL_ADVICE,
                            "Review Target Date for " + goal.getGoalTypeLabel(),
                            "The target completion date for this goal has passed. Consider updating the target date to establish a fresh, motivating timeline.",
                            GuidanceRecommendation.Priority.HIGH,
                            "Past scheduled target date (" + goal.getTargetDate() + ")",
                            "/goals/edit?id=" + goal.getGoalId(),
                            "Update Goal Date",
                            "bi-calendar-x"
                    );
                } else if (daysRemaining <= 14) {
                    return new GuidanceRecommendation(
                            GuidanceRecommendation.Category.GOAL_ADVICE,
                            "Upcoming Goal Deadline",
                            "Your " + goal.getGoalTypeLabel() + " target date is in " + daysRemaining + " day" + (daysRemaining == 1 ? "" : "s") + ". Maintain steady daily consistency to finish strong.",
                            GuidanceRecommendation.Priority.HIGH,
                            "Approaching milestone date (" + daysRemaining + " days left)",
                            "/goals/view?id=" + goal.getGoalId(),
                            "View Goal",
                            "bi-alarm"
                    );
                }
            }
        }
        return null;
    }

    private GuidanceRecommendation evaluateWorkoutHabits(List<Workout> recentWorkouts, int totalWorkouts, int weeklyWorkouts, LocalDate today) {
        if (recentWorkouts == null || recentWorkouts.isEmpty()) {
            return null;
        }

        Workout lastWorkout = recentWorkouts.get(0);
        if (lastWorkout.getWorkoutDate() != null) {
            LocalDate lastDate = lastWorkout.getWorkoutDate().toLocalDate();
            long daysSinceLast = ChronoUnit.DAYS.between(lastDate, today);

            // Long inactivity after previous history
            if (daysSinceLast >= 10 && totalWorkouts >= 3) {
                return new GuidanceRecommendation(
                        GuidanceRecommendation.Category.WORKOUT,
                        "Ease Back into Training",
                        "It has been " + daysSinceLast + " days since your last recorded session. Start with a light 15-20 minute workout at 50-60% intensity to ease your muscles back into rhythm.",
                        GuidanceRecommendation.Priority.HIGH,
                        "Inactivity gap of " + daysSinceLast + " days",
                        "/workouts/add",
                        "Log Gentle Session",
                        "bi-arrow-clockwise"
                );
            }
        }

        // Check for repetitive workout types
        if (recentWorkouts.size() >= 3) {
            Workout.WorkoutType type1 = recentWorkouts.get(0).getWorkoutType();
            Workout.WorkoutType type2 = recentWorkouts.get(1).getWorkoutType();
            Workout.WorkoutType type3 = recentWorkouts.get(2).getWorkoutType();

            if (type1 == type2 && type2 == type3) {
                String alternative = (type1 == Workout.WorkoutType.RUNNING || type1 == Workout.WorkoutType.CYCLING) ?
                        "strength or mobility session" : "light aerobic cardio or walking session";

                return new GuidanceRecommendation(
                        GuidanceRecommendation.Category.WORKOUT,
                        "Incorporate Cross-Training Variety",
                        "You have completed three consecutive " + type1.name().toLowerCase() + " sessions. Adding a " + alternative + " can help balance muscle development and prevent repetitive strain.",
                        GuidanceRecommendation.Priority.MEDIUM,
                        "Three consecutive " + type1.name().toLowerCase() + " workouts",
                        "/workouts/add",
                        "Try Varied Workout",
                        "bi-shuffle"
                );
            }
        }

        return null;
    }
}
