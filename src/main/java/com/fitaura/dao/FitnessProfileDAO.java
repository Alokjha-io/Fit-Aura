package com.fitaura.dao;

import com.fitaura.model.FitnessProfile;

import java.sql.Connection;
import java.util.Optional;

/**
 * Data Access Object interface for FitnessProfile entity.
 */
public interface FitnessProfileDAO {

    Integer create(FitnessProfile profile);

    Integer create(FitnessProfile profile, Connection conn);

    Optional<FitnessProfile> findByUserId(Integer userId);

    Optional<FitnessProfile> findById(Integer profileId);

    boolean update(FitnessProfile profile);

    boolean saveOrUpdate(FitnessProfile profile);

    default boolean save(FitnessProfile profile) {
        return saveOrUpdate(profile);
    }

    boolean deleteByUserId(Integer userId);
}
