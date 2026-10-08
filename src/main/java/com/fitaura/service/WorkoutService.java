package com.fitaura.service;

import com.fitaura.exception.ResourceNotFoundException;
import com.fitaura.exception.ValidationException;
import com.fitaura.model.Workout;

import java.sql.Date;
import java.util.List;

/**
 * Service interface governing workout logging, validation, retrieval,
 * updates, deletion, and ownership verification.
 */
public interface WorkoutService {

    /**
     * Creates and records a new workout for an authenticated user.
     *
     * @param userId          the owner user ID (derived from session)
     * @param workoutTypeStr  raw workout type name
     * @param workoutDateStr  raw date string (YYYY-MM-DD)
     * @param durationMinutes raw duration in minutes
     * @param intensityStr    raw intensity level ('LOW', 'MEDIUM', 'HIGH')
     * @param caloriesBurned  calories burned (optional, defaults to 0)
     * @param notes           optional notes
     * @param clientIp        client IP for security audit
     * @return the created Workout entity with generated ID
     * @throws ValidationException if any input fails domain constraints
     */
    Workout createWorkout(Integer userId, String workoutTypeStr, String workoutDateStr,
                          Integer durationMinutes, String intensityStr,
                          Integer caloriesBurned, String notes, String clientIp)
            throws ValidationException;

    /**
     * Retrieves workout details ensuring the requesting user owns the workout.
     *
     * @param workoutId the workout ID
     * @param userId    the requesting authenticated user ID
     * @return the Workout entity
     * @throws ResourceNotFoundException if workout does not exist or belongs to another user
     */
    Workout getWorkoutDetails(Integer workoutId, Integer userId)
            throws ResourceNotFoundException;

    /**
     * Retrieves paginated workouts for a user.
     *
     * @param userId the user ID
     * @param limit  page size
     * @param offset page offset
     * @return list of workouts
     */
    List<Workout> getWorkoutHistory(Integer userId, int limit, int offset);

    /**
     * Retrieves filtered and sorted workouts for a user.
     *
     * @param userId         the user ID
     * @param workoutTypeStr optional workout type filter string
     * @param intensityStr   optional intensity filter string
     * @param startDateStr   optional start date string
     * @param endDateStr     optional end date string
     * @param sortBy         sort identifier
     * @param limit          page limit
     * @param offset         page offset
     * @return filtered workouts
     */
    List<Workout> getFilteredWorkoutHistory(Integer userId, String workoutTypeStr,
                                            String intensityStr, String startDateStr,
                                            String endDateStr, String sortBy,
                                            int limit, int offset);

    /**
     * Counts total workouts for a user.
     */
    int getWorkoutCount(Integer userId);

    /**
     * Counts filtered workouts for a user.
     */
    int getFilteredWorkoutCount(Integer userId, String workoutTypeStr,
                                String intensityStr, String startDateStr, String endDateStr);

    /**
     * Updates an existing workout ensuring strict ownership verification.
     *
     * @param workoutId       the workout ID to update
     * @param userId          the authenticated user ID
     * @param workoutTypeStr  updated workout type
     * @param workoutDateStr  updated workout date
     * @param durationMinutes updated duration
     * @param intensityStr    updated intensity
     * @param caloriesBurned  updated calories burned
     * @param notes           updated notes
     * @param clientIp        client IP for security audit
     * @return the updated Workout entity
     * @throws ValidationException       if validation fails
     * @throws ResourceNotFoundException if workout does not exist or does not belong to user
     */
    Workout updateWorkout(Integer workoutId, Integer userId, String workoutTypeStr,
                          String workoutDateStr, Integer durationMinutes, String intensityStr,
                          Integer caloriesBurned, String notes, String clientIp)
            throws ValidationException, ResourceNotFoundException;

    /**
     * Deletes a workout ensuring strict ownership verification.
     *
     * @param workoutId the workout ID
     * @param userId    the authenticated user ID
     * @param clientIp  client IP for security audit
     * @throws ResourceNotFoundException if workout does not exist or does not belong to user
     */
    void deleteWorkout(Integer workoutId, Integer userId, String clientIp)
            throws ResourceNotFoundException;

    default List<Workout> getWorkoutsByUserId(Integer userId) {
        return getWorkoutHistory(userId, 100, 0);
    }

    default Workout createWorkout(Workout w, String clientIp) {
        return createWorkout(
                w.getUserId(),
                w.getWorkoutType() != null ? w.getWorkoutType().name() : "RUNNING",
                w.getWorkoutDate() != null ? w.getWorkoutDate().toString() : java.time.LocalDate.now().toString(),
                w.getDurationMinutes(),
                w.getIntensity() != null ? w.getIntensity().name() : "MEDIUM",
                w.getCaloriesBurned(),
                w.getNotes(),
                clientIp
        );
    }
}
