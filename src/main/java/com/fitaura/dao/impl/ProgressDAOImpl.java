package com.fitaura.dao.impl;

import com.fitaura.dao.ProgressDAO;
import com.fitaura.exception.DatabaseException;
import com.fitaura.util.DatabaseConnectionPool;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.sql.Connection;
import java.sql.Date;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.Map;

/**
 * JDBC implementation of ProgressDAO.
 * Performs parameterized SQL aggregations directly on MySQL.
 */
public class ProgressDAOImpl implements ProgressDAO {

    private static final Logger logger = LoggerFactory.getLogger(ProgressDAOImpl.class);

    private static final String SQL_COUNT_ALL =
            "SELECT COUNT(*) FROM workouts WHERE user_id = ?";

    private static final String SQL_SUM_DURATION_ALL =
            "SELECT COALESCE(SUM(duration_minutes), 0) FROM workouts WHERE user_id = ?";

    private static final String SQL_SUM_CALORIES_ALL =
            "SELECT COALESCE(SUM(calories_burned), 0) FROM workouts WHERE user_id = ?";

    private static final String SQL_COUNT_RANGE =
            "SELECT COUNT(*) FROM workouts WHERE user_id = ? AND workout_date >= ? AND workout_date <= ?";

    private static final String SQL_SUM_DURATION_RANGE =
            "SELECT COALESCE(SUM(duration_minutes), 0) FROM workouts WHERE user_id = ? AND workout_date >= ? AND workout_date <= ?";

    private static final String SQL_SUM_CALORIES_RANGE =
            "SELECT COALESCE(SUM(calories_burned), 0) FROM workouts WHERE user_id = ? AND workout_date >= ? AND workout_date <= ?";

    // MySQL WEEKDAY(workout_date) returns 0 = Monday, 1 = Tuesday, ..., 6 = Sunday
    private static final String SQL_WEEKLY_DAY_MINUTES =
            "SELECT WEEKDAY(workout_date) AS day_idx, COALESCE(SUM(duration_minutes), 0) AS total_dur " +
            "FROM workouts WHERE user_id = ? AND workout_date >= ? AND workout_date <= ? " +
            "GROUP BY WEEKDAY(workout_date)";

    private static final String SQL_TYPE_STATS =
            "SELECT workout_type, COUNT(*) AS cnt, COALESCE(SUM(duration_minutes), 0) AS total_dur, COALESCE(SUM(calories_burned), 0) AS total_cal " +
            "FROM workouts WHERE user_id = ? GROUP BY workout_type";

    @Override
    public int countWorkoutsByUser(Integer userId) {
        if (userId == null) return 0;
        return executeSingleIntQuery(SQL_COUNT_ALL, userId);
    }

    @Override
    public int sumDurationByUser(Integer userId) {
        if (userId == null) return 0;
        return executeSingleIntQuery(SQL_SUM_DURATION_ALL, userId);
    }

    @Override
    public int sumCaloriesByUser(Integer userId) {
        if (userId == null) return 0;
        return executeSingleIntQuery(SQL_SUM_CALORIES_ALL, userId);
    }

    @Override
    public int countWorkoutsForDateRange(Integer userId, Date startDate, Date endDate) {
        if (userId == null || startDate == null || endDate == null) return 0;
        return executeRangeIntQuery(SQL_COUNT_RANGE, userId, startDate, endDate);
    }

    @Override
    public int sumDurationForDateRange(Integer userId, Date startDate, Date endDate) {
        if (userId == null || startDate == null || endDate == null) return 0;
        return executeRangeIntQuery(SQL_SUM_DURATION_RANGE, userId, startDate, endDate);
    }

    @Override
    public int sumCaloriesForDateRange(Integer userId, Date startDate, Date endDate) {
        if (userId == null || startDate == null || endDate == null) return 0;
        return executeRangeIntQuery(SQL_SUM_CALORIES_RANGE, userId, startDate, endDate);
    }

    @Override
    public Map<Integer, Integer> getWeeklyDayMinutes(Integer userId, Date startDate, Date endDate) {
        Map<Integer, Integer> dayMap = new LinkedHashMap<>();
        // Initialize Monday (1) through Sunday (7) with 0
        for (int i = 1; i <= 7; i++) {
            dayMap.put(i, 0);
        }
        if (userId == null || startDate == null || endDate == null) {
            return dayMap;
        }

        try (Connection conn = DatabaseConnectionPool.getConnection();
             PreparedStatement stmt = conn.prepareStatement(SQL_WEEKLY_DAY_MINUTES)) {
            stmt.setInt(1, userId);
            stmt.setDate(2, startDate);
            stmt.setDate(3, endDate);

            try (ResultSet rs = stmt.executeQuery()) {
                while (rs.next()) {
                    int mysqlWeekday = rs.getInt("day_idx"); // 0=Mon..6=Sun
                    int dayNumber = mysqlWeekday + 1; // 1=Mon..7=Sun
                    int minutes = rs.getInt("total_dur");
                    dayMap.put(dayNumber, minutes);
                }
            }
            return dayMap;
        } catch (SQLException e) {
            logger.error("Error querying weekly day minutes for user {}: {}", userId, e.getMessage());
            throw new DatabaseException("Failed to query weekly day activity: " + e.getMessage(), e);
        }
    }

    @Override
    public Map<String, WorkoutTypeStat> getWorkoutTypeStats(Integer userId) {
        Map<String, WorkoutTypeStat> stats = new HashMap<>();
        if (userId == null) {
            return stats;
        }

        try (Connection conn = DatabaseConnectionPool.getConnection();
             PreparedStatement stmt = conn.prepareStatement(SQL_TYPE_STATS)) {
            stmt.setInt(1, userId);

            try (ResultSet rs = stmt.executeQuery()) {
                while (rs.next()) {
                    String type = rs.getString("workout_type");
                    int count = rs.getInt("cnt");
                    int duration = rs.getInt("total_dur");
                    int calories = rs.getInt("total_cal");
                    stats.put(type, new WorkoutTypeStat(type, count, duration, calories));
                }
            }
            return stats;
        } catch (SQLException e) {
            logger.error("Error querying workout type statistics for user {}: {}", userId, e.getMessage());
            throw new DatabaseException("Failed to query workout type distribution: " + e.getMessage(), e);
        }
    }

    private int executeSingleIntQuery(String sql, int userId) {
        try (Connection conn = DatabaseConnectionPool.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setInt(1, userId);
            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) {
                    return rs.getInt(1);
                }
            }
            return 0;
        } catch (SQLException e) {
            logger.error("Error executing progress aggregation query: {}", e.getMessage());
            throw new DatabaseException("Failed to calculate progress statistics: " + e.getMessage(), e);
        }
    }

    private int executeRangeIntQuery(String sql, int userId, Date start, Date end) {
        try (Connection conn = DatabaseConnectionPool.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setInt(1, userId);
            stmt.setDate(2, start);
            stmt.setDate(3, end);
            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) {
                    return rs.getInt(1);
                }
            }
            return 0;
        } catch (SQLException e) {
            logger.error("Error executing range progress aggregation query: {}", e.getMessage());
            throw new DatabaseException("Failed to calculate range statistics: " + e.getMessage(), e);
        }
    }
}
