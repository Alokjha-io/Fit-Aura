package com.fitaura.dao.impl;

import com.fitaura.dao.FitnessProfileDAO;
import com.fitaura.exception.DatabaseException;
import com.fitaura.model.FitnessProfile;
import com.fitaura.util.DatabaseConnectionPool;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.sql.*;
import java.util.Optional;

/**
 * JDBC implementation of FitnessProfileDAO.
 */
public class FitnessProfileDAOImpl implements FitnessProfileDAO {

    private static final Logger logger = LoggerFactory.getLogger(FitnessProfileDAOImpl.class);

    private static final String SQL_INSERT =
            "INSERT INTO fitness_profiles (user_id, age, gender, height_cm, weight_kg, activity_level, preferred_environment) " +
            "VALUES (?, ?, ?, ?, ?, ?, ?)";

    private static final String SQL_FIND_BY_USER_ID =
            "SELECT profile_id, user_id, age, gender, height_cm, weight_kg, activity_level, preferred_environment " +
            "FROM fitness_profiles WHERE user_id = ?";

    private static final String SQL_FIND_BY_ID =
            "SELECT profile_id, user_id, age, gender, height_cm, weight_kg, activity_level, preferred_environment " +
            "FROM fitness_profiles WHERE profile_id = ?";

    private static final String SQL_UPDATE =
            "UPDATE fitness_profiles SET age = ?, gender = ?, height_cm = ?, weight_kg = ?, activity_level = ?, preferred_environment = ? " +
            "WHERE user_id = ?";

    private static final String SQL_DELETE =
            "DELETE FROM fitness_profiles WHERE user_id = ?";

    @Override
    public Integer create(FitnessProfile profile) {
        try (Connection conn = DatabaseConnectionPool.getConnection()) {
            return create(profile, conn);
        } catch (SQLException e) {
            logger.error("Error creating fitness profile: {}", e.getMessage());
            throw new DatabaseException("Failed to insert fitness profile: " + e.getMessage(), e);
        }
    }

    @Override
    public Integer create(FitnessProfile profile, Connection conn) {
        try (PreparedStatement stmt = conn.prepareStatement(SQL_INSERT, Statement.RETURN_GENERATED_KEYS)) {
            stmt.setInt(1, profile.getUserId());
            if (profile.getAge() != null) stmt.setInt(2, profile.getAge()); else stmt.setNull(2, Types.INTEGER);
            stmt.setString(3, profile.getGender() != null ? profile.getGender().name() : null);
            stmt.setBigDecimal(4, profile.getHeightCm());
            stmt.setBigDecimal(5, profile.getWeightKg());
            stmt.setString(6, profile.getActivityLevel() != null ? profile.getActivityLevel().name() : FitnessProfile.ActivityLevel.BEGINNER.name());
            stmt.setString(7, profile.getPreferredEnvironment() != null ? profile.getPreferredEnvironment().name() : FitnessProfile.PreferredEnvironment.MIXED.name());

            int affected = stmt.executeUpdate();
            if (affected == 0) {
                throw new DatabaseException("Creating profile failed, no rows affected.");
            }
            try (ResultSet keys = stmt.getGeneratedKeys()) {
                if (keys.next()) {
                    int id = keys.getInt(1);
                    profile.setProfileId(id);
                    return id;
                }
            }
            throw new DatabaseException("Creating profile failed, no ID obtained.");
        } catch (SQLException e) {
            logger.error("Error persisting fitness profile: {}", e.getMessage());
            throw new DatabaseException("Failed to persist fitness profile: " + e.getMessage(), e);
        }
    }

    @Override
    public Optional<FitnessProfile> findByUserId(Integer userId) {
        try (Connection conn = DatabaseConnectionPool.getConnection();
             PreparedStatement stmt = conn.prepareStatement(SQL_FIND_BY_USER_ID)) {
            stmt.setInt(1, userId);
            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) {
                    return Optional.of(mapResultSet(rs));
                }
            }
            return Optional.empty();
        } catch (SQLException e) {
            logger.error("Error finding fitness profile by user id: {}", e.getMessage());
            throw new DatabaseException("Failed to query fitness profile: " + e.getMessage(), e);
        }
    }

    @Override
    public Optional<FitnessProfile> findById(Integer profileId) {
        try (Connection conn = DatabaseConnectionPool.getConnection();
             PreparedStatement stmt = conn.prepareStatement(SQL_FIND_BY_ID)) {
            stmt.setInt(1, profileId);
            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) {
                    return Optional.of(mapResultSet(rs));
                }
            }
            return Optional.empty();
        } catch (SQLException e) {
            logger.error("Error finding fitness profile by profile id: {}", e.getMessage());
            throw new DatabaseException("Failed to query fitness profile: " + e.getMessage(), e);
        }
    }

    @Override
    public boolean update(FitnessProfile profile) {
        try (Connection conn = DatabaseConnectionPool.getConnection();
             PreparedStatement stmt = conn.prepareStatement(SQL_UPDATE)) {
            if (profile.getAge() != null) stmt.setInt(1, profile.getAge()); else stmt.setNull(1, Types.INTEGER);
            stmt.setString(2, profile.getGender() != null ? profile.getGender().name() : null);
            stmt.setBigDecimal(3, profile.getHeightCm());
            stmt.setBigDecimal(4, profile.getWeightKg());
            stmt.setString(5, profile.getActivityLevel() != null ? profile.getActivityLevel().name() : FitnessProfile.ActivityLevel.BEGINNER.name());
            stmt.setString(6, profile.getPreferredEnvironment() != null ? profile.getPreferredEnvironment().name() : FitnessProfile.PreferredEnvironment.MIXED.name());
            stmt.setInt(7, profile.getUserId());

            return stmt.executeUpdate() > 0;
        } catch (SQLException e) {
            logger.error("Error updating fitness profile: {}", e.getMessage());
            throw new DatabaseException("Failed to update fitness profile: " + e.getMessage(), e);
        }
    }

    @Override
    public boolean saveOrUpdate(FitnessProfile profile) {
        if (profile == null || profile.getUserId() == null) {
            throw new IllegalArgumentException("FitnessProfile and userId must not be null.");
        }
        Optional<FitnessProfile> existing = findByUserId(profile.getUserId());
        if (existing.isPresent()) {
            return update(profile);
        } else {
            create(profile);
            return true;
        }
    }

    @Override
    public boolean deleteByUserId(Integer userId) {
        try (Connection conn = DatabaseConnectionPool.getConnection();
             PreparedStatement stmt = conn.prepareStatement(SQL_DELETE)) {
            stmt.setInt(1, userId);
            return stmt.executeUpdate() > 0;
        } catch (SQLException e) {
            logger.error("Error deleting fitness profile: {}", e.getMessage());
            throw new DatabaseException("Failed to delete fitness profile: " + e.getMessage(), e);
        }
    }

    private FitnessProfile mapResultSet(ResultSet rs) throws SQLException {
        FitnessProfile profile = new FitnessProfile();
        profile.setProfileId(rs.getInt("profile_id"));
        profile.setUserId(rs.getInt("user_id"));
        int age = rs.getInt("age");
        profile.setAge(rs.wasNull() ? null : age);

        String genderStr = rs.getString("gender");
        if (genderStr != null) {
            profile.setGender(FitnessProfile.Gender.valueOf(genderStr));
        }

        profile.setHeightCm(rs.getBigDecimal("height_cm"));
        profile.setWeightKg(rs.getBigDecimal("weight_kg"));

        String activityStr = rs.getString("activity_level");
        if (activityStr != null) {
            profile.setActivityLevel(FitnessProfile.ActivityLevel.valueOf(activityStr));
        }

        String envStr = rs.getString("preferred_environment");
        if (envStr != null) {
            profile.setPreferredEnvironment(FitnessProfile.PreferredEnvironment.valueOf(envStr));
        }

        return profile;
    }
}
