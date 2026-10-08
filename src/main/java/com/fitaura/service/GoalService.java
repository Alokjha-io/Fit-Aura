package com.fitaura.service;

import com.fitaura.exception.ResourceNotFoundException;
import com.fitaura.exception.ValidationException;
import com.fitaura.model.FitnessGoal;

import java.math.BigDecimal;
import java.util.List;

/**
 * Service interface governing fitness goals creation, validation, retrieval,
 * status transitions, deletion, and ownership verification.
 */
public interface GoalService {

    /**
     * Creates a new fitness goal for an authenticated user.
     *
     * @param userId         authenticated user ID (from session)
     * @param goalTypeStr    raw goal type string
     * @param targetValue    target numerical value (e.g. 68.0 kg)
     * @param currentValue   current starting numerical value (e.g. 75.0 kg)
     * @param unit           measurement unit (e.g. 'kg', 'sessions', 'minutes')
     * @param startDateStr   start date string (YYYY-MM-DD)
     * @param targetDateStr  target date string (YYYY-MM-DD, optional)
     * @param notes          optional notes
     * @param clientIp       client IP for security audit
     * @return created FitnessGoal with generated ID
     * @throws ValidationException if validation rules fail
     */
    FitnessGoal createGoal(Integer userId, String goalTypeStr, BigDecimal targetValue,
                           BigDecimal currentValue, String unit, String startDateStr,
                           String targetDateStr, String notes, String clientIp)
            throws ValidationException;

    /**
     * Retrieves goal details ensuring user ownership.
     *
     * @param goalId the goal ID
     * @param userId the requesting authenticated user ID
     * @return the FitnessGoal entity
     * @throws ResourceNotFoundException if not found or unauthorized
     */
    FitnessGoal getGoalDetails(Integer goalId, Integer userId)
            throws ResourceNotFoundException;

    /**
     * Retrieves all goals for a user.
     */
    List<FitnessGoal> getUserGoals(Integer userId);

    /**
     * Retrieves only active goals for a user.
     */
    List<FitnessGoal> getActiveGoals(Integer userId);

    /**
     * Retrieves goals by status for a user.
     */
    List<FitnessGoal> getUserGoalsByStatus(Integer userId, FitnessGoal.GoalStatus status);

    /**
     * Updates an existing active goal ensuring user ownership.
     *
     * @param goalId        the goal ID
     * @param userId        authenticated user ID
     * @param goalTypeStr   updated goal type
     * @param targetValue   updated target value
     * @param currentValue  updated current value
     * @param unit          updated unit
     * @param startDateStr  updated start date
     * @param targetDateStr updated target date
     * @param notes         updated notes
     * @param clientIp      client IP for security audit
     * @return updated FitnessGoal
     * @throws ValidationException       if validation fails
     * @throws ResourceNotFoundException if not found or unauthorized
     */
    FitnessGoal updateGoal(Integer goalId, Integer userId, String goalTypeStr,
                           BigDecimal targetValue, BigDecimal currentValue, String unit,
                           String startDateStr, String targetDateStr, String notes, String clientIp)
            throws ValidationException, ResourceNotFoundException;

    /**
     * Transitions the status of a goal (e.g., ACTIVE -> PAUSED, PAUSED -> ACTIVE, ACTIVE -> COMPLETED, ACTIVE -> CANCELLED).
     *
     * @param goalId    the goal ID
     * @param userId    authenticated user ID
     * @param newStatus desired status
     * @param clientIp  client IP for security audit
     * @throws ValidationException       if transition is illegal
     * @throws ResourceNotFoundException if not found or unauthorized
     */
    void updateGoalStatus(Integer goalId, Integer userId, FitnessGoal.GoalStatus newStatus, String clientIp)
            throws ValidationException, ResourceNotFoundException;

    /**
     * Deletes a goal permanently.
     *
     * @param goalId   the goal ID
     * @param userId   authenticated user ID
     * @param clientIp client IP for audit
     * @throws ResourceNotFoundException if not found or unauthorized
     */
    void deleteGoal(Integer goalId, Integer userId, String clientIp)
            throws ResourceNotFoundException;

    /**
     * Counts active goals for user.
     */
    int getActiveGoalCount(Integer userId);

    /**
     * Counts total goals for user.
     */
    int getTotalGoalCount(Integer userId);
}
