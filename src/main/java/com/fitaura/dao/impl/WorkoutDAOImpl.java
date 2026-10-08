package com.fitaura.dao.impl;

import com.fitaura.dao.WorkoutDAO;
import com.fitaura.exception.DatabaseException;
import com.fitaura.model.Workout;
import com.fitaura.util.DatabaseConnectionPool;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

/**
 * Robust JDBC implementation of WorkoutDAO.
 * Enforces ownership filters at the SQL level (WHERE workout_id = ? AND user_id = ?).
 */
public class WorkoutDAOImpl implements WorkoutDAO {

    private static final Logger logger = LoggerFactory.getLogger(WorkoutDAOImpl.class);

    private static final String SQL_INSERT =
            "INSERT INTO workouts (user_id, workout_type, workout_date, duration_minutes, intensity, calories_burned, notes) " +
            "VALUES (?, ?, ?, ?, ?, ?, ?)";

    private static final String SQL_FIND_BY_ID_AND_USER =
            "SELECT workout_id, user_id, workout_type, workout_date, duration_minutes, intensity, calories_burned, notes, created_at, updated_at " +
            "FROM workouts WHERE workout_id = ? AND user_id = ?";

    private static final String SQL_FIND_BY_ID =
            "SELECT workout_id, user_id, workout_type, workout_date, duration_minutes, intensity, calories_burned, notes, created_at, updated_at " +
            "FROM workouts WHERE workout_id = ?";

    private static final String SQL_FIND_BY_USER_ID =
            "SELECT workout_id, user_id, workout_type, workout_date, duration_minutes, intensity, calories_burned, notes, created_at, updated_at " +
            "FROM workouts WHERE user_id = ? ORDER BY workout_date DESC, workout_id DESC LIMIT ? OFFSET ?";

    private static final String SQL_COUNT_BY_USER_ID =
            "SELECT COUNT(*) FROM workouts WHERE user_id = ?";

    private static final String SQL_UPDATE_WITH_USER =
            "UPDATE workouts SET workout_type = ?, workout_date = ?, duration_minutes = ?, intensity = ?, calories_burned = ?, notes = ? " +
            "WHERE workout_id = ? AND user_id = ?";

    private static final String SQL_UPDATE =
            "UPDATE workouts SET workout_type = ?, workout_date = ?, duration_minutes = ?, intensity = ?, calories_burned = ?, notes = ? " +
            "WHERE workout_id = ?";

    private static final String SQL_DELETE_WITH_USER =
            "DELETE FROM workouts WHERE workout_id = ? AND user_id = ?";

    private static final String SQL_DELETE =
            "DELETE FROM workouts WHERE workout_id = ?";

    @Override
    public Integer create(Workout workout) {
        try (Connection conn = DatabaseConnectionPool.getConnection();
             PreparedStatement stmt = conn.prepareStatement(SQL_INSERT, Statement.RETURN_GENERATED_KEYS)) {
            stmt.setInt(1, workout.getUserId());
            stmt.setString(2, workout.getWorkoutType().name());
            stmt.setDate(3, workout.getWorkoutDate());
            stmt.setInt(4, workout.getDurationMinutes());
            stmt.setString(5, workout.getIntensity().name());
            stmt.setInt(6, workout.getCaloriesBurned() != null ? workout.getCaloriesBurned() : 0);
            stmt.setString(7, workout.getNotes());

            int affected = stmt.executeUpdate();
            if (affected == 0) {
                throw new DatabaseException("Failed to insert workout, no rows affected.");
            }
            try (ResultSet keys = stmt.getGeneratedKeys()) {
                if (keys.next()) {
                    int id = keys.getInt(1);
                    workout.setWorkoutId(id);
                    return id;
                }
            }
            throw new DatabaseException("Failed to insert workout, no ID generated.");
        } catch (SQLException e) {
            logger.error("Error creating workout: {}", e.getMessage());
            throw new DatabaseException("Failed to persist workout: " + e.getMessage(), e);
        }
    }

    @Override
    public Optional<Workout> findById(Integer workoutId, Integer userId) {
        if (workoutId == null || userId == null) {
            return Optional.empty();
        }
        try (Connection conn = DatabaseConnectionPool.getConnection();
             PreparedStatement stmt = conn.prepareStatement(SQL_FIND_BY_ID_AND_USER)) {
            stmt.setInt(1, workoutId);
            stmt.setInt(2, userId);
            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) {
                    return Optional.of(mapResultSet(rs));
                }
            }
            return Optional.empty();
        } catch (SQLException e) {
            logger.error("Error finding workout by id {} and user {}: {}", workoutId, userId, e.getMessage());
            throw new DatabaseException("Failed to query workout: " + e.getMessage(), e);
        }
    }

    @Override
    public Optional<Workout> findById(Integer workoutId) {
        if (workoutId == null) {
            return Optional.empty();
        }
        try (Connection conn = DatabaseConnectionPool.getConnection();
             PreparedStatement stmt = conn.prepareStatement(SQL_FIND_BY_ID)) {
            stmt.setInt(1, workoutId);
            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) {
                    return Optional.of(mapResultSet(rs));
                }
            }
            return Optional.empty();
        } catch (SQLException e) {
            logger.error("Error finding workout by id {}: {}", workoutId, e.getMessage());
            throw new DatabaseException("Failed to query workout: " + e.getMessage(), e);
        }
    }

    @Override
    public List<Workout> findByUserId(Integer userId, int limit, int offset) {
        List<Workout> workouts = new ArrayList<>();
        if (userId == null) {
            return workouts;
        }
        try (Connection conn = DatabaseConnectionPool.getConnection();
             PreparedStatement stmt = conn.prepareStatement(SQL_FIND_BY_USER_ID)) {
            stmt.setInt(1, userId);
            stmt.setInt(2, Math.max(1, limit));
            stmt.setInt(3, Math.max(0, offset));
            try (ResultSet rs = stmt.executeQuery()) {
                while (rs.next()) {
                    workouts.add(mapResultSet(rs));
                }
            }
            return workouts;
        } catch (SQLException e) {
            logger.error("Error finding workouts for user {}: {}", userId, e.getMessage());
            throw new DatabaseException("Failed to query user workouts: " + e.getMessage(), e);
        }
    }

    @Override
    public List<Workout> findByUserId(Integer userId) {
        return findByUserId(userId, 100, 0);
    }

    @Override
    public List<Workout> findByUserIdFiltered(Integer userId, Workout.WorkoutType workoutType,
                                             Workout.Intensity intensity, Date startDate, Date endDate,
                                             String sortBy, int limit, int offset) {
        List<Workout> workouts = new ArrayList<>();
        if (userId == null) {
            return workouts;
        }

        StringBuilder sql = new StringBuilder(
                "SELECT workout_id, user_id, workout_type, workout_date, duration_minutes, intensity, calories_burned, notes, created_at, updated_at " +
                "FROM workouts WHERE user_id = ? ");
        List<Object> params = new ArrayList<>();
        params.add(userId);

        if (workoutType != null) {
            sql.append("AND workout_type = ? ");
            params.add(workoutType.name());
        }
        if (intensity != null) {
            sql.append("AND intensity = ? ");
            params.add(intensity.name());
        }
        if (startDate != null) {
            sql.append("AND workout_date >= ? ");
            params.add(startDate);
        }
        if (endDate != null) {
            sql.append("AND workout_date <= ? ");
            params.add(endDate);
        }

        // Sorting
        if ("date_asc".equalsIgnoreCase(sortBy)) {
            sql.append("ORDER BY workout_date ASC, workout_id ASC ");
        } else if ("duration_desc".equalsIgnoreCase(sortBy)) {
            sql.append("ORDER BY duration_minutes DESC, workout_date DESC ");
        } else if ("calories_desc".equalsIgnoreCase(sortBy)) {
            sql.append("ORDER BY calories_burned DESC, workout_date DESC ");
        } else {
            sql.append("ORDER BY workout_date DESC, workout_id DESC ");
        }

        sql.append("LIMIT ? OFFSET ?");
        params.add(Math.max(1, limit));
        params.add(Math.max(0, offset));

        try (Connection conn = DatabaseConnectionPool.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql.toString())) {
            for (int i = 0; i < params.size(); i++) {
                Object p = params.get(i);
                if (p instanceof Integer) {
                    stmt.setInt(i + 1, (Integer) p);
                } else if (p instanceof Date) {
                    stmt.setDate(i + 1, (Date) p);
                } else {
                    stmt.setString(i + 1, p.toString());
                }
            }

            try (ResultSet rs = stmt.executeQuery()) {
                while (rs.next()) {
                    workouts.add(mapResultSet(rs));
                }
            }
            return workouts;
        } catch (SQLException e) {
            logger.error("Error querying filtered workouts for user {}: {}", userId, e.getMessage());
            throw new DatabaseException("Failed to query filtered workouts: " + e.getMessage(), e);
        }
    }

    @Override
    public int countByUserId(Integer userId) {
        if (userId == null) {
            return 0;
        }
        try (Connection conn = DatabaseConnectionPool.getConnection();
             PreparedStatement stmt = conn.prepareStatement(SQL_COUNT_BY_USER_ID)) {
            stmt.setInt(1, userId);
            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) {
                    return rs.getInt(1);
                }
            }
            return 0;
        } catch (SQLException e) {
            logger.error("Error counting workouts for user {}: {}", userId, e.getMessage());
            throw new DatabaseException("Failed to count workouts: " + e.getMessage(), e);
        }
    }

    @Override
    public int countByUserIdFiltered(Integer userId, Workout.WorkoutType workoutType,
                                    Workout.Intensity intensity, Date startDate, Date endDate) {
        if (userId == null) {
            return 0;
        }
        StringBuilder sql = new StringBuilder("SELECT COUNT(*) FROM workouts WHERE user_id = ? ");
        List<Object> params = new ArrayList<>();
        params.add(userId);

        if (workoutType != null) {
            sql.append("AND workout_type = ? ");
            params.add(workoutType.name());
        }
        if (intensity != null) {
            sql.append("AND intensity = ? ");
            params.add(intensity.name());
        }
        if (startDate != null) {
            sql.append("AND workout_date >= ? ");
            params.add(startDate);
        }
        if (endDate != null) {
            sql.append("AND workout_date <= ? ");
            params.add(endDate);
        }

        try (Connection conn = DatabaseConnectionPool.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql.toString())) {
            for (int i = 0; i < params.size(); i++) {
                Object p = params.get(i);
                if (p instanceof Integer) {
                    stmt.setInt(i + 1, (Integer) p);
                } else if (p instanceof Date) {
                    stmt.setDate(i + 1, (Date) p);
                } else {
                    stmt.setString(i + 1, p.toString());
                }
            }

            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) {
                    return rs.getInt(1);
                }
            }
            return 0;
        } catch (SQLException e) {
            logger.error("Error counting filtered workouts for user {}: {}", userId, e.getMessage());
            throw new DatabaseException("Failed to count filtered workouts: " + e.getMessage(), e);
        }
    }

    @Override
    public List<Workout> findByUserIdAndDateRange(Integer userId, Date startDate, Date endDate) {
        return findByUserIdFiltered(userId, null, null, startDate, endDate, "date_desc", 1000, 0);
    }

    @Override
    public int countByUserIdAndDateRange(Integer userId, Date startDate, Date endDate) {
        return countByUserIdFiltered(userId, null, null, startDate, endDate);
    }

    @Override
    public boolean update(Workout workout, Integer userId) {
        if (workout == null || workout.getWorkoutId() == null || userId == null) {
            return false;
        }
        try (Connection conn = DatabaseConnectionPool.getConnection();
             PreparedStatement stmt = conn.prepareStatement(SQL_UPDATE_WITH_USER)) {
            stmt.setString(1, workout.getWorkoutType().name());
            stmt.setDate(2, workout.getWorkoutDate());
            stmt.setInt(3, workout.getDurationMinutes());
            stmt.setString(4, workout.getIntensity().name());
            stmt.setInt(5, workout.getCaloriesBurned() != null ? workout.getCaloriesBurned() : 0);
            stmt.setString(6, workout.getNotes());
            stmt.setInt(7, workout.getWorkoutId());
            stmt.setInt(8, userId);

            return stmt.executeUpdate() > 0;
        } catch (SQLException e) {
            logger.error("Error updating workout {} for user {}: {}", workout.getWorkoutId(), userId, e.getMessage());
            throw new DatabaseException("Failed to update workout: " + e.getMessage(), e);
        }
    }

    @Override
    public boolean update(Workout workout) {
        if (workout == null || workout.getWorkoutId() == null) {
            return false;
        }
        try (Connection conn = DatabaseConnectionPool.getConnection();
             PreparedStatement stmt = conn.prepareStatement(SQL_UPDATE)) {
            stmt.setString(1, workout.getWorkoutType().name());
            stmt.setDate(2, workout.getWorkoutDate());
            stmt.setInt(3, workout.getDurationMinutes());
            stmt.setString(4, workout.getIntensity().name());
            stmt.setInt(5, workout.getCaloriesBurned() != null ? workout.getCaloriesBurned() : 0);
            stmt.setString(6, workout.getNotes());
            stmt.setInt(7, workout.getWorkoutId());

            return stmt.executeUpdate() > 0;
        } catch (SQLException e) {
            logger.error("Error updating workout {}: {}", workout.getWorkoutId(), e.getMessage());
            throw new DatabaseException("Failed to update workout: " + e.getMessage(), e);
        }
    }

    @Override
    public boolean delete(Integer workoutId, Integer userId) {
        if (workoutId == null || userId == null) {
            return false;
        }
        try (Connection conn = DatabaseConnectionPool.getConnection();
             PreparedStatement stmt = conn.prepareStatement(SQL_DELETE_WITH_USER)) {
            stmt.setInt(1, workoutId);
            stmt.setInt(2, userId);

            return stmt.executeUpdate() > 0;
        } catch (SQLException e) {
            logger.error("Error deleting workout {} for user {}: {}", workoutId, userId, e.getMessage());
            throw new DatabaseException("Failed to delete workout: " + e.getMessage(), e);
        }
    }

    @Override
    public boolean delete(Integer workoutId) {
        if (workoutId == null) {
            return false;
        }
        try (Connection conn = DatabaseConnectionPool.getConnection();
             PreparedStatement stmt = conn.prepareStatement(SQL_DELETE)) {
            stmt.setInt(1, workoutId);

            return stmt.executeUpdate() > 0;
        } catch (SQLException e) {
            logger.error("Error deleting workout {}: {}", workoutId, e.getMessage());
            throw new DatabaseException("Failed to delete workout: " + e.getMessage(), e);
        }
    }

    private Workout mapResultSet(ResultSet rs) throws SQLException {
        Workout workout = new Workout();
        workout.setWorkoutId(rs.getInt("workout_id"));
        workout.setUserId(rs.getInt("user_id"));
        workout.setWorkoutType(Workout.WorkoutType.valueOf(rs.getString("workout_type")));
        workout.setWorkoutDate(rs.getDate("workout_date"));
        workout.setDurationMinutes(rs.getInt("duration_minutes"));
        workout.setIntensity(Workout.Intensity.valueOf(rs.getString("intensity")));
        workout.setCaloriesBurned(rs.getInt("calories_burned"));
        workout.setNotes(rs.getString("notes"));
        workout.setCreatedAt(rs.getTimestamp("created_at"));
        workout.setUpdatedAt(rs.getTimestamp("updated_at"));
        return workout;
    }
}
