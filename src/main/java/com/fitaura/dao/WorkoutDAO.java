package com.fitaura.dao;

import com.fitaura.model.Workout;

import java.sql.Date;
import java.util.List;
import java.util.Optional;

/**
 * Data Access Object interface for the Workout domain entity.
 * Provides user-scoped persistence operations enforcing server-side ownership.
 */
public interface WorkoutDAO {

    /**
     * Persists a new workout entry.
     *
     * @param workout the workout entity to insert
     * @return generated workout ID
     */
    Integer create(Workout workout);

    /**
     * Finds a workout by ID strictly scoped to the authenticated user.
     *
     * @param workoutId the ID of the workout
     * @param userId    the ID of the owner
     * @return Optional containing workout if found and owned, or empty
     */
    Optional<Workout> findById(Integer workoutId, Integer userId);

    /**
     * Finds a workout by ID without ownership constraint (internal administrative access).
     *
     * @param workoutId the ID of the workout
     * @return Optional containing workout if found
     */
    Optional<Workout> findById(Integer workoutId);

    /**
     * Retrieves paginated workouts for a specific user ordered by workout_date DESC, workout_id DESC.
     *
     * @param userId the user ID
     * @param limit  page size
     * @param offset offset index
     * @return list of workouts
     */
    List<Workout> findByUserId(Integer userId, int limit, int offset);

    /**
     * Retrieves all workouts for a user with default pagination.
     *
     * @param userId the user ID
     * @return list of recent workouts
     */
    List<Workout> findByUserId(Integer userId);

    /**
     * Retrieves filtered and sorted workouts for a specific user.
     *
     * @param userId      the user ID
     * @param workoutType optional workout type filter
     * @param intensity   optional intensity filter
     * @param startDate   optional beginning workout date
     * @param endDate     optional ending workout date
     * @param sortBy      sorting order ('date_desc', 'date_asc', 'duration_desc', 'calories_desc')
     * @param limit       page limit
     * @param offset      page offset
     * @return filtered list of workouts
     */
    List<Workout> findByUserIdFiltered(Integer userId, Workout.WorkoutType workoutType,
                                       Workout.Intensity intensity, Date startDate, Date endDate,
                                       String sortBy, int limit, int offset);

    /**
     * Counts total workouts recorded by a user.
     *
     * @param userId the user ID
     * @return total workout count
     */
    int countByUserId(Integer userId);

    /**
     * Counts filtered workouts for a user.
     */
    int countByUserIdFiltered(Integer userId, Workout.WorkoutType workoutType,
                              Workout.Intensity intensity, Date startDate, Date endDate);

    /**
     * Retrieves all workouts recorded by a user within a specified date range.
     */
    List<Workout> findByUserIdAndDateRange(Integer userId, Date startDate, Date endDate);

    /**
     * Counts workouts recorded by a user within a specified date range.
     */
    int countByUserIdAndDateRange(Integer userId, Date startDate, Date endDate);

    /**
     * Updates an existing workout strictly ensuring the user owns the record.
     *
     * @param workout workout entity containing updated values
     * @param userId  authenticated owner user ID
     * @return true if updated, false if not found or unauthorized
     */
    boolean update(Workout workout, Integer userId);

    /**
     * Updates an existing workout by workout ID.
     */
    boolean update(Workout workout);

    /**
     * Deletes a workout strictly ensuring the user owns the record.
     *
     * @param workoutId the workout ID
     * @param userId    authenticated owner user ID
     * @return true if deleted, false if not found or unauthorized
     */
    boolean delete(Integer workoutId, Integer userId);

    /**
     * Deletes a workout by workout ID.
     */
    boolean delete(Integer workoutId);
}
