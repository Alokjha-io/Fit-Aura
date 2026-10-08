package com.fitaura.service.impl;

import com.fitaura.dao.ActivityLogDAO;
import com.fitaura.dao.FitnessProfileDAO;
import com.fitaura.dao.impl.ActivityLogDAOImpl;
import com.fitaura.dao.impl.FitnessProfileDAOImpl;
import com.fitaura.exception.DatabaseException;
import com.fitaura.exception.ValidationException;
import com.fitaura.model.ActivityLog;
import com.fitaura.model.FitnessProfile;
import com.fitaura.service.FitnessProfileService;
import com.fitaura.util.AppConstants;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.math.BigDecimal;
import java.util.Optional;

/**
 * Implementation of FitnessProfileService validating physical measurements
 * and managing fitness profile persistence.
 */
public class FitnessProfileServiceImpl implements FitnessProfileService {

    private static final Logger logger = LoggerFactory.getLogger(FitnessProfileServiceImpl.class);

    private static final int MIN_AGE = 13;
    private static final int MAX_AGE = 120;
    private static final BigDecimal MIN_HEIGHT = new BigDecimal("50.0");
    private static final BigDecimal MAX_HEIGHT = new BigDecimal("260.0");
    private static final BigDecimal MIN_WEIGHT = new BigDecimal("20.0");
    private static final BigDecimal MAX_WEIGHT = new BigDecimal("500.0");

    private final FitnessProfileDAO fitnessProfileDAO;
    private final ActivityLogDAO activityLogDAO;

    public FitnessProfileServiceImpl() {
        this(new FitnessProfileDAOImpl(), new ActivityLogDAOImpl());
    }

    public FitnessProfileServiceImpl(FitnessProfileDAO fitnessProfileDAO, ActivityLogDAO activityLogDAO) {
        this.fitnessProfileDAO = fitnessProfileDAO;
        this.activityLogDAO = activityLogDAO;
    }

    @Override
    public Optional<FitnessProfile> getFitnessProfile(Integer userId) {
        if (userId == null) {
            return Optional.empty();
        }
        try {
            return fitnessProfileDAO.findByUserId(userId);
        } catch (DatabaseException e) {
            logger.error("Error retrieving fitness profile for user {}: {}", userId, e.getMessage());
            throw new DatabaseException("Could not retrieve fitness profile.", e);
        }
    }

    @Override
    public FitnessProfile saveOrUpdateFitnessProfile(Integer userId, Integer age, FitnessProfile.Gender gender,
                                                     BigDecimal heightCm, BigDecimal weightKg,
                                                     FitnessProfile.ActivityLevel activityLevel,
                                                     FitnessProfile.PreferredEnvironment preferredEnvironment,
                                                     String ipAddress) throws ValidationException {
        if (userId == null) {
            throw new ValidationException("User ID must not be null.");
        }

        // 1. Age Validation
        if (age != null) {
            if (age < MIN_AGE || age > MAX_AGE) {
                throw new ValidationException("Age must be between " + MIN_AGE + " and " + MAX_AGE + " years.");
            }
        }

        // 2. Height Validation
        if (heightCm != null) {
            if (heightCm.compareTo(MIN_HEIGHT) < 0 || heightCm.compareTo(MAX_HEIGHT) > 0) {
                throw new ValidationException("Height must be between " + MIN_HEIGHT + " cm and " + MAX_HEIGHT + " cm.");
            }
        }

        // 3. Weight Validation
        if (weightKg != null) {
            if (weightKg.compareTo(MIN_WEIGHT) < 0 || weightKg.compareTo(MAX_WEIGHT) > 0) {
                throw new ValidationException("Weight must be between " + MIN_WEIGHT + " kg and " + MAX_WEIGHT + " kg.");
            }
        }

        // 4. Default Enums if null
        FitnessProfile.ActivityLevel resolvedActivity =
                activityLevel != null ? activityLevel : FitnessProfile.ActivityLevel.BEGINNER;
        FitnessProfile.PreferredEnvironment resolvedEnv =
                preferredEnvironment != null ? preferredEnvironment : FitnessProfile.PreferredEnvironment.MIXED;

        // 5. Build Profile Object
        FitnessProfile profile = new FitnessProfile();
        profile.setUserId(userId);
        profile.setAge(age);
        profile.setGender(gender);
        profile.setHeightCm(heightCm);
        profile.setWeightKg(weightKg);
        profile.setActivityLevel(resolvedActivity);
        profile.setPreferredEnvironment(resolvedEnv);

        // 6. Persist
        try {
            fitnessProfileDAO.saveOrUpdate(profile);

            // Audit Log
            logActivity(userId, AppConstants.ACTION_FITNESS_PROFILE_UPDATED, "FITNESS_PROFILE", userId,
                    "Fitness profile updated: height=" + heightCm + ", weight=" + weightKg, ipAddress);

            logger.info("Saved fitness profile for user {}", userId);
            return profile;

        } catch (DatabaseException e) {
            logger.error("Database error saving fitness profile for user {}: {}", userId, e.getMessage());
            throw new DatabaseException("Failed to save fitness profile in database.", e);
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
            logger.warn("Could not write fitness profile activity log: {}", e.getMessage());
        }
    }
}
