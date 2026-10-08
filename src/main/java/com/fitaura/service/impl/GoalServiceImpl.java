package com.fitaura.service.impl;

import com.fitaura.dao.ActivityLogDAO;
import com.fitaura.dao.FitnessGoalDAO;
import com.fitaura.dao.impl.ActivityLogDAOImpl;
import com.fitaura.dao.impl.FitnessGoalDAOImpl;
import com.fitaura.exception.DatabaseException;
import com.fitaura.exception.ResourceNotFoundException;
import com.fitaura.exception.ValidationException;
import com.fitaura.model.ActivityLog;
import com.fitaura.model.FitnessGoal;
import com.fitaura.service.GoalService;
import com.fitaura.util.AppConstants;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.math.BigDecimal;
import java.sql.Date;
import java.time.LocalDate;
import java.time.format.DateTimeParseException;
import java.util.Collections;
import java.util.List;
import java.util.Optional;

/**
 * Production implementation of GoalService.
 * Enforces server-side validation, user ownership constraints (Anti-IDOR),
 * status state machines, and audit logging.
 */
public class GoalServiceImpl implements GoalService {

    private static final Logger logger = LoggerFactory.getLogger(GoalServiceImpl.class);

    private static final int MAX_NOTES_LENGTH = 500;
    private static final int MAX_UNIT_LENGTH = 30;

    private final FitnessGoalDAO goalDAO;
    private final ActivityLogDAO activityLogDAO;

    public GoalServiceImpl() {
        this(new FitnessGoalDAOImpl(), new ActivityLogDAOImpl());
    }

    public GoalServiceImpl(FitnessGoalDAO goalDAO, ActivityLogDAO activityLogDAO) {
        this.goalDAO = goalDAO;
        this.activityLogDAO = activityLogDAO;
    }

    @Override
    public FitnessGoal createGoal(Integer userId, String goalTypeStr, BigDecimal targetValue,
                                 BigDecimal currentValue, String unit, String startDateStr,
                                 String targetDateStr, String notes, String clientIp)
            throws ValidationException {
        if (userId == null) {
            throw new ValidationException("User authentication is required to create a goal.");
        }

        FitnessGoal.GoalType goalType = validateAndParseGoalType(goalTypeStr);
        validateNumericValues(targetValue, currentValue);
        Date startDate = validateAndParseStartDate(startDateStr);
        Date targetDate = validateAndParseTargetDate(targetDateStr, startDate);
        String validUnit = validateUnit(unit);
        String validNotes = validateNotes(notes);

        FitnessGoal goal = new FitnessGoal();
        goal.setUserId(userId);
        goal.setGoalType(goalType);
        goal.setTargetValue(targetValue);
        goal.setCurrentValue(currentValue != null ? currentValue : BigDecimal.ZERO);
        goal.setUnit(validUnit);
        goal.setStartDate(startDate);
        goal.setTargetDate(targetDate);
        goal.setStatus(FitnessGoal.GoalStatus.ACTIVE);
        goal.setNotes(validNotes);

        try {
            Integer goalId = goalDAO.create(goal);
            goal.setGoalId(goalId);

            logActivity(userId, AppConstants.ACTION_GOAL_CREATED, "GOAL", goalId,
                    "Created " + goal.getGoalTypeLabel() + " goal (Target: " + targetValue + " " + (validUnit != null ? validUnit : "") + ")",
                    clientIp);

            logger.info("User {} created fitness goal {}", userId, goalId);
            return goal;

        } catch (DatabaseException e) {
            logger.error("Failed to create fitness goal for user {}: {}", userId, e.getMessage());
            throw new DatabaseException("Unable to save your goal right now. Please try again.", e);
        }
    }

    @Override
    public FitnessGoal getGoalDetails(Integer goalId, Integer userId) throws ResourceNotFoundException {
        if (goalId == null || userId == null) {
            throw new ResourceNotFoundException("Goal not found.");
        }
        Optional<FitnessGoal> goalOpt = goalDAO.findById(goalId, userId);
        return goalOpt.orElseThrow(() -> new ResourceNotFoundException("Goal not found."));
    }

    @Override
    public List<FitnessGoal> getUserGoals(Integer userId) {
        if (userId == null) {
            return Collections.emptyList();
        }
        return goalDAO.findByUserId(userId);
    }

    @Override
    public List<FitnessGoal> getActiveGoals(Integer userId) {
        if (userId == null) {
            return Collections.emptyList();
        }
        return goalDAO.findActiveByUserId(userId);
    }

    @Override
    public List<FitnessGoal> getUserGoalsByStatus(Integer userId, FitnessGoal.GoalStatus status) {
        if (userId == null || status == null) {
            return Collections.emptyList();
        }
        return goalDAO.findByUserIdAndStatus(userId, status);
    }

    @Override
    public FitnessGoal updateGoal(Integer goalId, Integer userId, String goalTypeStr,
                                 BigDecimal targetValue, BigDecimal currentValue, String unit,
                                 String startDateStr, String targetDateStr, String notes, String clientIp)
            throws ValidationException, ResourceNotFoundException {
        if (goalId == null || userId == null) {
            throw new ResourceNotFoundException("Goal not found.");
        }

        FitnessGoal existing = getGoalDetails(goalId, userId);

        // Disallow editing completed or cancelled goals as if they were active
        if (existing.getStatus() == FitnessGoal.GoalStatus.COMPLETED || existing.getStatus() == FitnessGoal.GoalStatus.CANCELLED) {
            throw new ValidationException("Completed or cancelled goals cannot be edited. Please create a new goal if desired.");
        }

        FitnessGoal.GoalType goalType = validateAndParseGoalType(goalTypeStr);
        validateNumericValues(targetValue, currentValue);
        Date startDate = validateAndParseStartDate(startDateStr);
        Date targetDate = validateAndParseTargetDate(targetDateStr, startDate);
        String validUnit = validateUnit(unit);
        String validNotes = validateNotes(notes);

        existing.setGoalType(goalType);
        existing.setTargetValue(targetValue);
        existing.setCurrentValue(currentValue != null ? currentValue : BigDecimal.ZERO);
        existing.setUnit(validUnit);
        existing.setStartDate(startDate);
        existing.setTargetDate(targetDate);
        existing.setNotes(validNotes);

        try {
            boolean updated = goalDAO.update(existing, userId);
            if (!updated) {
                throw new ResourceNotFoundException("Goal not found or update unauthorized.");
            }

            logActivity(userId, AppConstants.ACTION_GOAL_UPDATED, "GOAL", goalId,
                    "Updated " + existing.getGoalTypeLabel() + " goal #" + goalId, clientIp);

            logger.info("User {} updated fitness goal {}", userId, goalId);
            return existing;

        } catch (DatabaseException e) {
            logger.error("Failed to update goal {}: {}", goalId, e.getMessage());
            throw new DatabaseException("Unable to save your goal changes right now. Please try again.", e);
        }
    }

    @Override
    public void updateGoalStatus(Integer goalId, Integer userId, FitnessGoal.GoalStatus newStatus, String clientIp)
            throws ValidationException, ResourceNotFoundException {
        if (goalId == null || userId == null || newStatus == null) {
            throw new ResourceNotFoundException("Goal not found.");
        }

        FitnessGoal existing = getGoalDetails(goalId, userId);
        FitnessGoal.GoalStatus currentStatus = existing.getStatus();

        if (currentStatus == newStatus) {
            return; // No-op
        }

        // Validate state transitions
        validateStatusTransition(currentStatus, newStatus);

        try {
            boolean updated = goalDAO.updateStatus(goalId, userId, newStatus);
            if (!updated) {
                throw new ResourceNotFoundException("Goal not found or status change unauthorized.");
            }

            String actionType = newStatus == FitnessGoal.GoalStatus.CANCELLED ?
                    AppConstants.ACTION_GOAL_CANCELLED : AppConstants.ACTION_GOAL_STATUS_CHANGED;

            logActivity(userId, actionType, "GOAL", goalId,
                    "Changed goal #" + goalId + " status from " + currentStatus + " to " + newStatus, clientIp);

            logger.info("User {} transitioned goal {} from {} to {}", userId, goalId, currentStatus, newStatus);

        } catch (DatabaseException e) {
            logger.error("Failed to update goal status for goal {}: {}", goalId, e.getMessage());
            throw new DatabaseException("Unable to update goal status right now. Please try again.", e);
        }
    }

    @Override
    public void deleteGoal(Integer goalId, Integer userId, String clientIp) throws ResourceNotFoundException {
        if (goalId == null || userId == null) {
            throw new ResourceNotFoundException("Goal not found.");
        }

        FitnessGoal existing = getGoalDetails(goalId, userId);

        try {
            boolean deleted = goalDAO.delete(goalId, userId);
            if (!deleted) {
                throw new ResourceNotFoundException("Goal not found or deletion unauthorized.");
            }

            logActivity(userId, AppConstants.ACTION_GOAL_DELETED, "GOAL", goalId,
                    "Deleted " + existing.getGoalTypeLabel() + " goal #" + goalId, clientIp);

            logger.info("User {} deleted goal {}", userId, goalId);

        } catch (DatabaseException e) {
            logger.error("Failed to delete goal {}: {}", goalId, e.getMessage());
            throw new DatabaseException("Unable to delete your goal right now. Please try again.", e);
        }
    }

    @Override
    public int getActiveGoalCount(Integer userId) {
        if (userId == null) {
            return 0;
        }
        return goalDAO.countActiveByUserId(userId);
    }

    @Override
    public int getTotalGoalCount(Integer userId) {
        if (userId == null) {
            return 0;
        }
        return goalDAO.countByUserId(userId);
    }

    // ==========================================
    // Server-Side Validation Helpers
    // ==========================================

    private FitnessGoal.GoalType validateAndParseGoalType(String str) {
        if (str == null || str.trim().isEmpty()) {
            throw new ValidationException("Goal type is required.");
        }
        try {
            return FitnessGoal.GoalType.valueOf(str.trim().toUpperCase());
        } catch (IllegalArgumentException e) {
            throw new ValidationException("Please select a valid goal type (Weight Loss, Weight Gain, Muscle Gain, Strength, Endurance, General Fitness, or Flexibility).");
        }
    }

    private void validateNumericValues(BigDecimal targetValue, BigDecimal currentValue) {
        if (targetValue == null) {
            throw new ValidationException("Target value is required.");
        }
        if (targetValue.compareTo(BigDecimal.ZERO) <= 0) {
            throw new ValidationException("Target value must be a positive number.");
        }
        if (targetValue.compareTo(new BigDecimal("100000")) > 0) {
            throw new ValidationException("Target value cannot exceed 100,000.");
        }
        if (currentValue != null) {
            if (currentValue.compareTo(BigDecimal.ZERO) < 0) {
                throw new ValidationException("Current value cannot be negative.");
            }
            if (currentValue.compareTo(new BigDecimal("100000")) > 0) {
                throw new ValidationException("Current value cannot exceed 100,000.");
            }
        }
    }

    private Date validateAndParseStartDate(String str) {
        if (str == null || str.trim().isEmpty()) {
            throw new ValidationException("Start date is required.");
        }
        try {
            LocalDate date = LocalDate.parse(str.trim());
            if (date.isBefore(LocalDate.of(2000, 1, 1))) {
                throw new ValidationException("Start date cannot be earlier than the year 2000.");
            }
            return Date.valueOf(date);
        } catch (DateTimeParseException e) {
            throw new ValidationException("Please enter a valid start date in YYYY-MM-DD format.");
        }
    }

    private Date validateAndParseTargetDate(String str, Date startDate) {
        if (str == null || str.trim().isEmpty()) {
            return null;
        }
        try {
            LocalDate target = LocalDate.parse(str.trim());
            if (startDate != null && target.isBefore(startDate.toLocalDate())) {
                throw new ValidationException("Target completion date cannot be before the start date.");
            }
            return Date.valueOf(target);
        } catch (DateTimeParseException e) {
            throw new ValidationException("Please enter a valid target date in YYYY-MM-DD format.");
        }
    }

    private String validateUnit(String unit) {
        if (unit == null || unit.trim().isEmpty()) {
            return "units";
        }
        String trimmed = unit.trim();
        if (trimmed.length() > MAX_UNIT_LENGTH) {
            throw new ValidationException("Unit description cannot exceed 30 characters.");
        }
        return trimmed;
    }

    private String validateNotes(String notes) {
        if (notes == null || notes.trim().isEmpty()) {
            return null;
        }
        String trimmed = notes.trim();
        if (trimmed.length() > MAX_NOTES_LENGTH) {
            throw new ValidationException("Notes cannot exceed 500 characters.");
        }
        return trimmed;
    }

    private void validateStatusTransition(FitnessGoal.GoalStatus current, FitnessGoal.GoalStatus target) {
        // State machine rules:
        // ACTIVE -> PAUSED, COMPLETED, CANCELLED
        // PAUSED -> ACTIVE, CANCELLED
        // COMPLETED -> CANNOT BE CHANGED (Historical integrity)
        // CANCELLED -> CANNOT BE CHANGED (Historical integrity)
        if (current == FitnessGoal.GoalStatus.COMPLETED) {
            throw new ValidationException("Completed goals cannot change status.");
        }
        if (current == FitnessGoal.GoalStatus.CANCELLED) {
            throw new ValidationException("Cancelled goals cannot change status.");
        }
        if (current == FitnessGoal.GoalStatus.PAUSED && target == FitnessGoal.GoalStatus.COMPLETED) {
            throw new ValidationException("Please resume the goal before marking it as completed.");
        }
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
            logger.warn("Could not record goal activity log: {}", e.getMessage());
        }
    }
}
