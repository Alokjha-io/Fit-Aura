package com.fitaura.service;

import com.fitaura.exception.ValidationException;
import com.fitaura.model.FitnessProfile;

import java.math.BigDecimal;
import java.util.Optional;

/**
 * Service interface managing user physical metrics, fitness profiles,
 * and environmental preferences.
 */
public interface FitnessProfileService {

    /**
     * Retrieves the fitness profile associated with a user ID.
     *
     * @param userId Authenticated user ID from session
     * @return Optional containing FitnessProfile if configured
     */
    Optional<FitnessProfile> getFitnessProfile(Integer userId);

    /**
     * Creates or updates a user's fitness profile with full server-side validation.
     *
     * @param userId Authenticated user ID
     * @param age User's age in years (13 - 120)
     * @param gender Biological gender enum
     * @param heightCm User's height in centimeters (50.0 - 260.0)
     * @param weightKg User's weight in kilograms (20.0 - 500.0)
     * @param activityLevel Activity level enum
     * @param preferredEnvironment Preferred workout environment enum
     * @param ipAddress Client IP for audit logging
     * @return Saved or updated FitnessProfile entity
     * @throws ValidationException if any physical metric is out of realistic range
     */
    FitnessProfile saveOrUpdateFitnessProfile(Integer userId, Integer age, FitnessProfile.Gender gender,
                                             BigDecimal heightCm, BigDecimal weightKg,
                                             FitnessProfile.ActivityLevel activityLevel,
                                             FitnessProfile.PreferredEnvironment preferredEnvironment,
                                             String ipAddress) throws ValidationException;
}
