package com.fitaura.dao.impl;

import com.fitaura.dao.FitnessGoalDAO;
import com.fitaura.exception.DatabaseException;
import com.fitaura.model.FitnessGoal;
import com.fitaura.util.DatabaseConnectionPool;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

/**
 * JDBC implementation of FitnessGoalDAO.
 */
public class FitnessGoalDAOImpl implements FitnessGoalDAO {

    private static final Logger logger = LoggerFactory.getLogger(FitnessGoalDAOImpl.class);

    private static final String SQL_INSERT =
            "INSERT INTO fitness_goals (user_id, goal_type, target_value, current_value, unit, start_date, target_date, status, notes) " +
            "VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?)";

    private static final String SQL_FIND_BY_ID_AND_USER =
            "SELECT goal_id, user_id, goal_type, target_value, current_value, unit, start_date, target_date, status, notes, created_at " +
            "FROM fitness_goals WHERE goal_id = ? AND user_id = ?";

    private static final String SQL_FIND_BY_ID =
            "SELECT goal_id, user_id, goal_type, target_value, current_value, unit, start_date, target_date, status, notes, created_at " +
            "FROM fitness_goals WHERE goal_id = ?";

    private static final String SQL_FIND_BY_USER_ID =
            "SELECT goal_id, user_id, goal_type, target_value, current_value, unit, start_date, target_date, status, notes, created_at " +
            "FROM fitness_goals WHERE user_id = ? ORDER BY CASE status WHEN 'ACTIVE' THEN 1 WHEN 'PAUSED' THEN 2 WHEN 'COMPLETED' THEN 3 ELSE 4 END, created_at DESC";

    private static final String SQL_FIND_BY_USER_AND_STATUS =
            "SELECT goal_id, user_id, goal_type, target_value, current_value, unit, start_date, target_date, status, notes, created_at " +
            "FROM fitness_goals WHERE user_id = ? AND status = ? ORDER BY created_at DESC";

    private static final String SQL_FIND_ACTIVE_BY_USER =
            "SELECT goal_id, user_id, goal_type, target_value, current_value, unit, start_date, target_date, status, notes, created_at " +
            "FROM fitness_goals WHERE user_id = ? AND status = 'ACTIVE' ORDER BY created_at DESC";

    private static final String SQL_UPDATE_WITH_USER =
            "UPDATE fitness_goals SET goal_type = ?, target_value = ?, current_value = ?, unit = ?, start_date = ?, target_date = ?, status = ?, notes = ? " +
            "WHERE goal_id = ? AND user_id = ?";

    private static final String SQL_UPDATE =
            "UPDATE fitness_goals SET goal_type = ?, target_value = ?, current_value = ?, unit = ?, start_date = ?, target_date = ?, status = ?, notes = ? " +
            "WHERE goal_id = ?";

    private static final String SQL_UPDATE_STATUS_WITH_USER =
            "UPDATE fitness_goals SET status = ? WHERE goal_id = ? AND user_id = ?";

    private static final String SQL_UPDATE_STATUS =
            "UPDATE fitness_goals SET status = ? WHERE goal_id = ?";

    private static final String SQL_DELETE_WITH_USER =
            "DELETE FROM fitness_goals WHERE goal_id = ? AND user_id = ?";

    private static final String SQL_DELETE =
            "DELETE FROM fitness_goals WHERE goal_id = ?";

    private static final String SQL_COUNT_BY_USER =
            "SELECT COUNT(*) FROM fitness_goals WHERE user_id = ?";

    private static final String SQL_COUNT_ACTIVE_BY_USER =
            "SELECT COUNT(*) FROM fitness_goals WHERE user_id = ? AND status = 'ACTIVE'";

    @Override
    public Integer create(FitnessGoal goal) {
        try (Connection conn = DatabaseConnectionPool.getConnection();
             PreparedStatement stmt = conn.prepareStatement(SQL_INSERT, Statement.RETURN_GENERATED_KEYS)) {
            stmt.setInt(1, goal.getUserId());
            stmt.setString(2, goal.getGoalType().name());
            stmt.setBigDecimal(3, goal.getTargetValue());
            stmt.setBigDecimal(4, goal.getCurrentValue());
            stmt.setString(5, goal.getUnit());
            stmt.setDate(6, goal.getStartDate());
            stmt.setDate(7, goal.getTargetDate());
            stmt.setString(8, goal.getStatus() != null ? goal.getStatus().name() : FitnessGoal.GoalStatus.ACTIVE.name());
            stmt.setString(9, goal.getNotes());

            int affected = stmt.executeUpdate();
            if (affected == 0) {
                throw new DatabaseException("Failed to insert goal, no rows affected.");
            }
            try (ResultSet keys = stmt.getGeneratedKeys()) {
                if (keys.next()) {
                    int id = keys.getInt(1);
                    goal.setGoalId(id);
                    return id;
                }
            }
            throw new DatabaseException("Failed to insert goal, no ID generated.");
        } catch (SQLException e) {
            logger.error("Error creating goal: {}", e.getMessage());
            throw new DatabaseException("Failed to persist fitness goal: " + e.getMessage(), e);
        }
    }

    @Override
    public Optional<FitnessGoal> findById(Integer goalId, Integer userId) {
        if (goalId == null || userId == null) {
            return Optional.empty();
        }
        try (Connection conn = DatabaseConnectionPool.getConnection();
             PreparedStatement stmt = conn.prepareStatement(SQL_FIND_BY_ID_AND_USER)) {
            stmt.setInt(1, goalId);
            stmt.setInt(2, userId);
            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) {
                    return Optional.of(mapResultSet(rs));
                }
            }
            return Optional.empty();
        } catch (SQLException e) {
            logger.error("Error querying goal by id {} and user {}: {}", goalId, userId, e.getMessage());
            throw new DatabaseException("Failed to query fitness goal: " + e.getMessage(), e);
        }
    }

    @Override
    public Optional<FitnessGoal> findById(Integer goalId) {
        if (goalId == null) {
            return Optional.empty();
        }
        try (Connection conn = DatabaseConnectionPool.getConnection();
             PreparedStatement stmt = conn.prepareStatement(SQL_FIND_BY_ID)) {
            stmt.setInt(1, goalId);
            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) {
                    return Optional.of(mapResultSet(rs));
                }
            }
            return Optional.empty();
        } catch (SQLException e) {
            logger.error("Error querying goal by id {}: {}", goalId, e.getMessage());
            throw new DatabaseException("Failed to query fitness goal: " + e.getMessage(), e);
        }
    }

    @Override
    public List<FitnessGoal> findByUserId(Integer userId) {
        List<FitnessGoal> goals = new ArrayList<>();
        if (userId == null) {
            return goals;
        }
        try (Connection conn = DatabaseConnectionPool.getConnection();
             PreparedStatement stmt = conn.prepareStatement(SQL_FIND_BY_USER_ID)) {
            stmt.setInt(1, userId);
            try (ResultSet rs = stmt.executeQuery()) {
                while (rs.next()) {
                    goals.add(mapResultSet(rs));
                }
            }
            return goals;
        } catch (SQLException e) {
            logger.error("Error querying goals for user {}: {}", userId, e.getMessage());
            throw new DatabaseException("Failed to query user goals: " + e.getMessage(), e);
        }
    }

    @Override
    public List<FitnessGoal> findByUserIdAndStatus(Integer userId, FitnessGoal.GoalStatus status) {
        List<FitnessGoal> goals = new ArrayList<>();
        if (userId == null || status == null) {
            return goals;
        }
        try (Connection conn = DatabaseConnectionPool.getConnection();
             PreparedStatement stmt = conn.prepareStatement(SQL_FIND_BY_USER_AND_STATUS)) {
            stmt.setInt(1, userId);
            stmt.setString(2, status.name());
            try (ResultSet rs = stmt.executeQuery()) {
                while (rs.next()) {
                    goals.add(mapResultSet(rs));
                }
            }
            return goals;
        } catch (SQLException e) {
            logger.error("Error querying goals for user {} and status {}: {}", userId, status, e.getMessage());
            throw new DatabaseException("Failed to query goals: " + e.getMessage(), e);
        }
    }

    @Override
    public List<FitnessGoal> findActiveByUserId(Integer userId) {
        List<FitnessGoal> goals = new ArrayList<>();
        if (userId == null) {
            return goals;
        }
        try (Connection conn = DatabaseConnectionPool.getConnection();
             PreparedStatement stmt = conn.prepareStatement(SQL_FIND_ACTIVE_BY_USER)) {
            stmt.setInt(1, userId);
            try (ResultSet rs = stmt.executeQuery()) {
                while (rs.next()) {
                    goals.add(mapResultSet(rs));
                }
            }
            return goals;
        } catch (SQLException e) {
            logger.error("Error querying active goals for user {}: {}", userId, e.getMessage());
            throw new DatabaseException("Failed to query active goals: " + e.getMessage(), e);
        }
    }

    @Override
    public boolean update(FitnessGoal goal, Integer userId) {
        if (goal == null || goal.getGoalId() == null || userId == null) {
            return false;
        }
        try (Connection conn = DatabaseConnectionPool.getConnection();
             PreparedStatement stmt = conn.prepareStatement(SQL_UPDATE_WITH_USER)) {
            stmt.setString(1, goal.getGoalType().name());
            stmt.setBigDecimal(2, goal.getTargetValue());
            stmt.setBigDecimal(3, goal.getCurrentValue());
            stmt.setString(4, goal.getUnit());
            stmt.setDate(5, goal.getStartDate());
            stmt.setDate(6, goal.getTargetDate());
            stmt.setString(7, goal.getStatus().name());
            stmt.setString(8, goal.getNotes());
            stmt.setInt(9, goal.getGoalId());
            stmt.setInt(10, userId);

            return stmt.executeUpdate() > 0;
        } catch (SQLException e) {
            logger.error("Error updating goal {} for user {}: {}", goal.getGoalId(), userId, e.getMessage());
            throw new DatabaseException("Failed to update goal: " + e.getMessage(), e);
        }
    }

    @Override
    public boolean update(FitnessGoal goal) {
        if (goal == null || goal.getGoalId() == null) {
            return false;
        }
        try (Connection conn = DatabaseConnectionPool.getConnection();
             PreparedStatement stmt = conn.prepareStatement(SQL_UPDATE)) {
            stmt.setString(1, goal.getGoalType().name());
            stmt.setBigDecimal(2, goal.getTargetValue());
            stmt.setBigDecimal(3, goal.getCurrentValue());
            stmt.setString(4, goal.getUnit());
            stmt.setDate(5, goal.getStartDate());
            stmt.setDate(6, goal.getTargetDate());
            stmt.setString(7, goal.getStatus().name());
            stmt.setString(8, goal.getNotes());
            stmt.setInt(9, goal.getGoalId());

            return stmt.executeUpdate() > 0;
        } catch (SQLException e) {
            logger.error("Error updating goal {}: {}", goal.getGoalId(), e.getMessage());
            throw new DatabaseException("Failed to update goal: " + e.getMessage(), e);
        }
    }

    @Override
    public boolean updateStatus(Integer goalId, Integer userId, FitnessGoal.GoalStatus status) {
        if (goalId == null || userId == null || status == null) {
            return false;
        }
        try (Connection conn = DatabaseConnectionPool.getConnection();
             PreparedStatement stmt = conn.prepareStatement(SQL_UPDATE_STATUS_WITH_USER)) {
            stmt.setString(1, status.name());
            stmt.setInt(2, goalId);
            stmt.setInt(3, userId);
            return stmt.executeUpdate() > 0;
        } catch (SQLException e) {
            logger.error("Error updating goal status for goal {} and user {}: {}", goalId, userId, e.getMessage());
            throw new DatabaseException("Failed to update goal status: " + e.getMessage(), e);
        }
    }

    @Override
    public boolean updateStatus(Integer goalId, FitnessGoal.GoalStatus status) {
        if (goalId == null || status == null) {
            return false;
        }
        try (Connection conn = DatabaseConnectionPool.getConnection();
             PreparedStatement stmt = conn.prepareStatement(SQL_UPDATE_STATUS)) {
            stmt.setString(1, status.name());
            stmt.setInt(2, goalId);
            return stmt.executeUpdate() > 0;
        } catch (SQLException e) {
            logger.error("Error updating goal status {}: {}", goalId, e.getMessage());
            throw new DatabaseException("Failed to update goal status: " + e.getMessage(), e);
        }
    }

    @Override
    public boolean delete(Integer goalId, Integer userId) {
        if (goalId == null || userId == null) {
            return false;
        }
        try (Connection conn = DatabaseConnectionPool.getConnection();
             PreparedStatement stmt = conn.prepareStatement(SQL_DELETE_WITH_USER)) {
            stmt.setInt(1, goalId);
            stmt.setInt(2, userId);
            return stmt.executeUpdate() > 0;
        } catch (SQLException e) {
            logger.error("Error deleting goal {} for user {}: {}", goalId, userId, e.getMessage());
            throw new DatabaseException("Failed to delete goal: " + e.getMessage(), e);
        }
    }

    @Override
    public boolean delete(Integer goalId) {
        if (goalId == null) {
            return false;
        }
        try (Connection conn = DatabaseConnectionPool.getConnection();
             PreparedStatement stmt = conn.prepareStatement(SQL_DELETE)) {
            stmt.setInt(1, goalId);
            return stmt.executeUpdate() > 0;
        } catch (SQLException e) {
            logger.error("Error deleting goal {}: {}", goalId, e.getMessage());
            throw new DatabaseException("Failed to delete goal: " + e.getMessage(), e);
        }
    }

    @Override
    public int countByUserId(Integer userId) {
        if (userId == null) {
            return 0;
        }
        try (Connection conn = DatabaseConnectionPool.getConnection();
             PreparedStatement stmt = conn.prepareStatement(SQL_COUNT_BY_USER)) {
            stmt.setInt(1, userId);
            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) {
                    return rs.getInt(1);
                }
            }
            return 0;
        } catch (SQLException e) {
            logger.error("Error counting goals for user {}: {}", userId, e.getMessage());
            throw new DatabaseException("Failed to count goals: " + e.getMessage(), e);
        }
    }

    @Override
    public int countActiveByUserId(Integer userId) {
        if (userId == null) {
            return 0;
        }
        try (Connection conn = DatabaseConnectionPool.getConnection();
             PreparedStatement stmt = conn.prepareStatement(SQL_COUNT_ACTIVE_BY_USER)) {
            stmt.setInt(1, userId);
            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) {
                    return rs.getInt(1);
                }
            }
            return 0;
        } catch (SQLException e) {
            logger.error("Error counting active goals for user {}: {}", userId, e.getMessage());
            throw new DatabaseException("Failed to count active goals: " + e.getMessage(), e);
        }
    }

    private FitnessGoal mapResultSet(ResultSet rs) throws SQLException {
        FitnessGoal goal = new FitnessGoal();
        goal.setGoalId(rs.getInt("goal_id"));
        goal.setUserId(rs.getInt("user_id"));
        goal.setGoalType(FitnessGoal.GoalType.valueOf(rs.getString("goal_type")));
        goal.setTargetValue(rs.getBigDecimal("target_value"));
        goal.setCurrentValue(rs.getBigDecimal("current_value"));
        goal.setUnit(rs.getString("unit"));
        goal.setStartDate(rs.getDate("start_date"));
        goal.setTargetDate(rs.getDate("target_date"));
        goal.setStatus(FitnessGoal.GoalStatus.valueOf(rs.getString("status")));
        goal.setNotes(rs.getString("notes"));
        goal.setCreatedAt(rs.getTimestamp("created_at"));
        return goal;
    }
}
