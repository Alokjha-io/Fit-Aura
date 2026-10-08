package com.fitaura.service.impl;

import com.fitaura.dao.ActivityLogDAO;
import com.fitaura.dao.WorkoutDAO;
import com.fitaura.dao.impl.ActivityLogDAOImpl;
import com.fitaura.dao.impl.WorkoutDAOImpl;
import com.fitaura.exception.DatabaseException;
import com.fitaura.exception.ResourceNotFoundException;
import com.fitaura.exception.ValidationException;
import com.fitaura.model.ActivityLog;
import com.fitaura.model.Workout;
import com.fitaura.service.WorkoutService;
import com.fitaura.util.AppConstants;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.sql.Date;
import java.time.LocalDate;
import java.time.format.DateTimeParseException;
import java.util.Collections;
import java.util.List;
import java.util.Optional;

/**
 * Production implementation of WorkoutService.
 * Enforces business rules, server-side data validation, audit logs,
 * and strict user ownership checks preventing IDOR vulnerabilities.
 */
public class WorkoutServiceImpl implements WorkoutService {

    private static final Logger logger = LoggerFactory.getLogger(WorkoutServiceImpl.class);

    private static final int MAX_DURATION_MINUTES = 1440; // 24 hours
    private static final int MAX_CALORIES = 20000;
    private static final int MAX_NOTES_LENGTH = 1000;

    private final WorkoutDAO workoutDAO;
    private final ActivityLogDAO activityLogDAO;
    private final com.fitaura.service.GamificationService gamificationService;

    public WorkoutServiceImpl() {
        this(new WorkoutDAOImpl(), new ActivityLogDAOImpl(), new GamificationServiceImpl());
    }

    public WorkoutServiceImpl(WorkoutDAO workoutDAO, ActivityLogDAO activityLogDAO) {
        this(workoutDAO, activityLogDAO, new GamificationServiceImpl());
    }

    public WorkoutServiceImpl(WorkoutDAO workoutDAO, ActivityLogDAO activityLogDAO,
                              com.fitaura.service.GamificationService gamificationService) {
        this.workoutDAO = workoutDAO;
        this.activityLogDAO = activityLogDAO;
        this.gamificationService = gamificationService;
    }

    @Override
    public Workout createWorkout(Integer userId, String workoutTypeStr, String workoutDateStr,
                                 Integer durationMinutes, String intensityStr,
                                 Integer caloriesBurned, String notes, String clientIp)
            throws ValidationException {
        if (userId == null) {
            throw new ValidationException("User authentication is required to record a workout.");
        }

        Workout.WorkoutType type = validateAndParseWorkoutType(workoutTypeStr);
        Date workoutDate = validateAndParseDate(workoutDateStr);
        int validDuration = validateDuration(durationMinutes);
        Workout.Intensity intensity = validateAndParseIntensity(intensityStr);
        int validCalories = validateCalories(caloriesBurned);
        String validNotes = validateNotes(notes);

        Workout workout = new Workout();
        workout.setUserId(userId);
        workout.setWorkoutType(type);
        workout.setWorkoutDate(workoutDate);
        workout.setDurationMinutes(validDuration);
        workout.setIntensity(intensity);
        workout.setCaloriesBurned(validCalories);
        workout.setNotes(validNotes);

        try {
            Integer workoutId = workoutDAO.create(workout);
            workout.setWorkoutId(workoutId);

            logActivity(userId, AppConstants.ACTION_WORKOUT_CREATED, "WORKOUT", workoutId,
                    "Recorded " + type.name() + " workout (" + validDuration + " min, " + validCalories + " kcal)",
                    clientIp);

            // Award gamification points and check milestones
            try {
                if (gamificationService != null) {
                    gamificationService.awardWorkoutPoints(workout);
                }
            } catch (Exception ex) {
                logger.warn("Non-fatal: could not award gamification points for workout {}: {}", workoutId, ex.getMessage());
            }

            logger.info("User {} created workout {}", userId, workoutId);
            return workout;

        } catch (DatabaseException e) {
            logger.error("Failed to persist workout for user {}: {}", userId, e.getMessage());
            throw new DatabaseException("Unable to save your workout right now. Please try again.", e);
        }
    }

    @Override
    public Workout getWorkoutDetails(Integer workoutId, Integer userId) throws ResourceNotFoundException {
        if (workoutId == null || userId == null) {
            throw new ResourceNotFoundException("Workout not found.");
        }
        Optional<Workout> workoutOpt = workoutDAO.findById(workoutId, userId);
        return workoutOpt.orElseThrow(() -> new ResourceNotFoundException("Workout not found."));
    }

    @Override
    public List<Workout> getWorkoutHistory(Integer userId, int limit, int offset) {
        if (userId == null) {
            return Collections.emptyList();
        }
        int validLimit = limit > 0 ? Math.min(limit, 100) : 20;
        int validOffset = Math.max(0, offset);
        return workoutDAO.findByUserId(userId, validLimit, validOffset);
    }

    @Override
    public List<Workout> getFilteredWorkoutHistory(Integer userId, String workoutTypeStr,
                                                   String intensityStr, String startDateStr,
                                                   String endDateStr, String sortBy,
                                                   int limit, int offset) {
        if (userId == null) {
            return Collections.emptyList();
        }

        Workout.WorkoutType type = parseNullableWorkoutType(workoutTypeStr);
        Workout.Intensity intensity = parseNullableIntensity(intensityStr);
        Date startDate = parseNullableDate(startDateStr);
        Date endDate = parseNullableDate(endDateStr);

        int validLimit = limit > 0 ? Math.min(limit, 100) : 20;
        int validOffset = Math.max(0, offset);

        return workoutDAO.findByUserIdFiltered(userId, type, intensity, startDate, endDate, sortBy, validLimit, validOffset);
    }

    @Override
    public int getWorkoutCount(Integer userId) {
        if (userId == null) {
            return 0;
        }
        return workoutDAO.countByUserId(userId);
    }

    @Override
    public int getFilteredWorkoutCount(Integer userId, String workoutTypeStr, String intensityStr,
                                       String startDateStr, String endDateStr) {
        if (userId == null) {
            return 0;
        }
        Workout.WorkoutType type = parseNullableWorkoutType(workoutTypeStr);
        Workout.Intensity intensity = parseNullableIntensity(intensityStr);
        Date startDate = parseNullableDate(startDateStr);
        Date endDate = parseNullableDate(endDateStr);

        return workoutDAO.countByUserIdFiltered(userId, type, intensity, startDate, endDate);
    }

    @Override
    public Workout updateWorkout(Integer workoutId, Integer userId, String workoutTypeStr,
                                 String workoutDateStr, Integer durationMinutes, String intensityStr,
                                 Integer caloriesBurned, String notes, String clientIp)
            throws ValidationException, ResourceNotFoundException {
        if (workoutId == null || userId == null) {
            throw new ResourceNotFoundException("Workout not found.");
        }

        // Verify ownership and existence
        Workout existing = getWorkoutDetails(workoutId, userId);

        Workout.WorkoutType type = validateAndParseWorkoutType(workoutTypeStr);
        Date workoutDate = validateAndParseDate(workoutDateStr);
        int validDuration = validateDuration(durationMinutes);
        Workout.Intensity intensity = validateAndParseIntensity(intensityStr);
        int validCalories = validateCalories(caloriesBurned);
        String validNotes = validateNotes(notes);

        existing.setWorkoutType(type);
        existing.setWorkoutDate(workoutDate);
        existing.setDurationMinutes(validDuration);
        existing.setIntensity(intensity);
        existing.setCaloriesBurned(validCalories);
        existing.setNotes(validNotes);

        try {
            boolean updated = workoutDAO.update(existing, userId);
            if (!updated) {
                throw new ResourceNotFoundException("Workout not found or update unauthorized.");
            }

            logActivity(userId, AppConstants.ACTION_WORKOUT_UPDATED, "WORKOUT", workoutId,
                    "Updated workout #" + workoutId + " (" + type.name() + ")", clientIp);

            logger.info("User {} updated workout {}", userId, workoutId);
            return existing;

        } catch (DatabaseException e) {
            logger.error("Failed to update workout {}: {}", workoutId, e.getMessage());
            throw new DatabaseException("Unable to save your workout right now. Please try again.", e);
        }
    }

    @Override
    public void deleteWorkout(Integer workoutId, Integer userId, String clientIp)
            throws ResourceNotFoundException {
        if (workoutId == null || userId == null) {
            throw new ResourceNotFoundException("Workout not found.");
        }

        // Verify existence & ownership first
        Workout existing = getWorkoutDetails(workoutId, userId);

        try {
            boolean deleted = workoutDAO.delete(workoutId, userId);
            if (!deleted) {
                throw new ResourceNotFoundException("Workout not found or deletion unauthorized.");
            }

            logActivity(userId, AppConstants.ACTION_WORKOUT_DELETED, "WORKOUT", workoutId,
                    "Deleted " + existing.getWorkoutType().name() + " workout #" + workoutId, clientIp);

            logger.info("User {} deleted workout {}", userId, workoutId);

        } catch (DatabaseException e) {
            logger.error("Failed to delete workout {}: {}", workoutId, e.getMessage());
            throw new DatabaseException("Unable to delete your workout right now. Please try again.", e);
        }
    }

    // ==========================================
    // Server-Side Field Validation Helpers
    // ==========================================

    private Workout.WorkoutType validateAndParseWorkoutType(String str) {
        if (str == null || str.trim().isEmpty()) {
            throw new ValidationException("Workout type is required.");
        }
        try {
            return Workout.WorkoutType.valueOf(str.trim().toUpperCase());
        } catch (IllegalArgumentException e) {
            throw new ValidationException("Please select a valid workout type (Running, Walking, Cycling, Strength Training, or Home Workout).");
        }
    }

    private Workout.WorkoutType parseNullableWorkoutType(String str) {
        if (str == null || str.trim().isEmpty()) {
            return null;
        }
        try {
            return Workout.WorkoutType.valueOf(str.trim().toUpperCase());
        } catch (IllegalArgumentException e) {
            return null;
        }
    }

    private Date validateAndParseDate(String str) {
        if (str == null || str.trim().isEmpty()) {
            throw new ValidationException("Workout date is required.");
        }
        try {
            LocalDate localDate = LocalDate.parse(str.trim());
            // Disallow dates more than 1 day in the future
            if (localDate.isAfter(LocalDate.now().plusDays(1))) {
                throw new ValidationException("Workout date cannot be set in the future.");
            }
            // Disallow dates older than year 2000
            if (localDate.isBefore(LocalDate.of(2000, 1, 1))) {
                throw new ValidationException("Workout date cannot be earlier than the year 2000.");
            }
            return Date.valueOf(localDate);
        } catch (DateTimeParseException e) {
            throw new ValidationException("Please enter a valid date in YYYY-MM-DD format.");
        }
    }

    private Date parseNullableDate(String str) {
        if (str == null || str.trim().isEmpty()) {
            return null;
        }
        try {
            return Date.valueOf(LocalDate.parse(str.trim()));
        } catch (Exception e) {
            return null;
        }
    }

    private int validateDuration(Integer duration) {
        if (duration == null) {
            throw new ValidationException("Workout duration is required.");
        }
        if (duration <= 0) {
            throw new ValidationException("Duration must be a positive number of minutes.");
        }
        if (duration > MAX_DURATION_MINUTES) {
            throw new ValidationException("Duration cannot exceed 1440 minutes (24 hours).");
        }
        return duration;
    }

    private Workout.Intensity validateAndParseIntensity(String str) {
        if (str == null || str.trim().isEmpty()) {
            throw new ValidationException("Intensity level is required.");
        }
        try {
            return Workout.Intensity.valueOf(str.trim().toUpperCase());
        } catch (IllegalArgumentException e) {
            throw new ValidationException("Please select a valid intensity level (Low, Medium, or High).");
        }
    }

    private Workout.Intensity parseNullableIntensity(String str) {
        if (str == null || str.trim().isEmpty()) {
            return null;
        }
        try {
            return Workout.Intensity.valueOf(str.trim().toUpperCase());
        } catch (IllegalArgumentException e) {
            return null;
        }
    }

    private int validateCalories(Integer calories) {
        if (calories == null) {
            return 0;
        }
        if (calories < 0) {
            throw new ValidationException("Calories burned cannot be negative.");
        }
        if (calories > MAX_CALORIES) {
            throw new ValidationException("Calories burned cannot exceed 20,000 kcal.");
        }
        return calories;
    }

    private String validateNotes(String notes) {
        if (notes == null || notes.trim().isEmpty()) {
            return null;
        }
        String trimmed = notes.trim();
        if (trimmed.length() > MAX_NOTES_LENGTH) {
            throw new ValidationException("Notes cannot exceed 1000 characters.");
        }
        return trimmed;
    }

    private void logActivity(Integer userId, String action, String entityType,
                             Integer entityId, String description, String ipAddress) {
        try {
            ActivityLog log = new ActivityLog();
            log.setUserId(userId);
            log.setActionType(action);
            log.setEntityType(entityType);
            log.setEntityId(entityId);
            log.setDescription(description);
            log.setIpAddress(ipAddress);
            activityLogDAO.create(log);
        } catch (Exception e) {
            logger.warn("Could not record workout activity log: {}", e.getMessage());
        }
    }
}
