package com.fitaura.dao.impl;

import com.fitaura.dao.ChallengeDAO;
import com.fitaura.exception.DatabaseException;
import com.fitaura.model.Challenge;
import com.fitaura.util.DatabaseConnectionPool;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

/**
 * JDBC implementation of ChallengeDAO.
 */
public class ChallengeDAOImpl implements ChallengeDAO {

    private static final Logger logger = LoggerFactory.getLogger(ChallengeDAOImpl.class);

    private static final String SQL_INSERT =
            "INSERT INTO challenges (created_by, name, description, goal_value, goal_unit, points_reward, start_date, end_date, status) " +
            "VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?)";

    private static final String SQL_FIND_BY_ID =
            "SELECT challenge_id, created_by, name, description, goal_value, goal_unit, points_reward, start_date, end_date, status, created_at " +
            "FROM challenges WHERE challenge_id = ?";

    private static final String SQL_FIND_BY_STATUS =
            "SELECT challenge_id, created_by, name, description, goal_value, goal_unit, points_reward, start_date, end_date, status, created_at " +
            "FROM challenges WHERE status = ? ORDER BY start_date ASC";

    private static final String SQL_FIND_ALL =
            "SELECT challenge_id, created_by, name, description, goal_value, goal_unit, points_reward, start_date, end_date, status, created_at " +
            "FROM challenges ORDER BY created_at DESC";

    private static final String SQL_FIND_BY_CREATED_BY =
            "SELECT challenge_id, created_by, name, description, goal_value, goal_unit, points_reward, start_date, end_date, status, created_at " +
            "FROM challenges WHERE created_by = ? ORDER BY created_at DESC";

    private static final String SQL_FIND_PENDING_MODERATION =
            "SELECT challenge_id, created_by, name, description, goal_value, goal_unit, points_reward, start_date, end_date, status, created_at " +
            "FROM challenges WHERE status = 'DRAFT' ORDER BY created_at ASC";

    private static final String SQL_COUNT_PARTICIPANTS =
            "SELECT COUNT(*) FROM challenge_participants WHERE challenge_id = ? AND status != 'LEFT'";

    private static final String SQL_COUNT_ACTIVE =
            "SELECT COUNT(*) FROM challenges WHERE status = 'ACTIVE'";

    private static final String SQL_COUNT_PENDING =
            "SELECT COUNT(*) FROM challenges WHERE status = 'DRAFT'";

    private static final String SQL_UPDATE =
            "UPDATE challenges SET name = ?, description = ?, goal_value = ?, goal_unit = ?, points_reward = ?, start_date = ?, end_date = ?, status = ? " +
            "WHERE challenge_id = ?";

    private static final String SQL_UPDATE_STATUS =
            "UPDATE challenges SET status = ? WHERE challenge_id = ?";

    private static final String SQL_DELETE =
            "DELETE FROM challenges WHERE challenge_id = ?";

    @Override
    public Integer create(Challenge challenge) {
        try (Connection conn = DatabaseConnectionPool.getConnection();
             PreparedStatement stmt = conn.prepareStatement(SQL_INSERT, Statement.RETURN_GENERATED_KEYS)) {
            stmt.setInt(1, challenge.getCreatedBy());
            stmt.setString(2, challenge.getName());
            stmt.setString(3, challenge.getDescription());
            stmt.setBigDecimal(4, challenge.getGoalValue());
            stmt.setString(5, challenge.getGoalUnit());
            stmt.setInt(6, challenge.getPointsReward() != null ? challenge.getPointsReward() : 0);
            stmt.setDate(7, challenge.getStartDate());
            stmt.setDate(8, challenge.getEndDate());
            stmt.setString(9, challenge.getStatus() != null ? challenge.getStatus().name() : Challenge.ChallengeStatus.DRAFT.name());

            int affected = stmt.executeUpdate();
            if (affected == 0) {
                throw new DatabaseException("Failed to insert challenge, no rows affected.");
            }
            try (ResultSet keys = stmt.getGeneratedKeys()) {
                if (keys.next()) {
                    int id = keys.getInt(1);
                    challenge.setChallengeId(id);
                    return id;
                }
            }
            throw new DatabaseException("Failed to insert challenge, no ID generated.");
        } catch (SQLException e) {
            logger.error("Error creating challenge: {}", e.getMessage());
            throw new DatabaseException("Failed to persist challenge: " + e.getMessage(), e);
        }
    }

    @Override
    public Optional<Challenge> findById(Integer challengeId) {
        if (challengeId == null) return Optional.empty();
        try (Connection conn = DatabaseConnectionPool.getConnection();
             PreparedStatement stmt = conn.prepareStatement(SQL_FIND_BY_ID)) {
            stmt.setInt(1, challengeId);
            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) {
                    return Optional.of(mapResultSet(rs));
                }
            }
            return Optional.empty();
        } catch (SQLException e) {
            logger.error("Error finding challenge by id {}: {}", challengeId, e.getMessage());
            throw new DatabaseException("Failed to query challenge: " + e.getMessage(), e);
        }
    }

    @Override
    public List<Challenge> findByStatus(Challenge.ChallengeStatus status) {
        List<Challenge> challenges = new ArrayList<>();
        try (Connection conn = DatabaseConnectionPool.getConnection();
             PreparedStatement stmt = conn.prepareStatement(SQL_FIND_BY_STATUS)) {
            stmt.setString(1, status.name());
            try (ResultSet rs = stmt.executeQuery()) {
                while (rs.next()) {
                    challenges.add(mapResultSet(rs));
                }
            }
            return challenges;
        } catch (SQLException e) {
            logger.error("Error finding challenges by status {}: {}", status, e.getMessage());
            throw new DatabaseException("Failed to query challenges by status: " + e.getMessage(), e);
        }
    }

    @Override
    public List<Challenge> findAll() {
        List<Challenge> challenges = new ArrayList<>();
        try (Connection conn = DatabaseConnectionPool.getConnection();
             PreparedStatement stmt = conn.prepareStatement(SQL_FIND_ALL);
             ResultSet rs = stmt.executeQuery()) {
            while (rs.next()) {
                challenges.add(mapResultSet(rs));
            }
            return challenges;
        } catch (SQLException e) {
            logger.error("Error querying all challenges: {}", e.getMessage());
            throw new DatabaseException("Failed to query all challenges: " + e.getMessage(), e);
        }
    }

    @Override
    public List<Challenge> findByCreatedBy(Integer userId) {
        List<Challenge> challenges = new ArrayList<>();
        if (userId == null) return challenges;
        try (Connection conn = DatabaseConnectionPool.getConnection();
             PreparedStatement stmt = conn.prepareStatement(SQL_FIND_BY_CREATED_BY)) {
            stmt.setInt(1, userId);
            try (ResultSet rs = stmt.executeQuery()) {
                while (rs.next()) {
                    challenges.add(mapResultSet(rs));
                }
            }
            return challenges;
        } catch (SQLException e) {
            logger.error("Error finding challenges by creator {}: {}", userId, e.getMessage());
            throw new DatabaseException("Failed to query challenges by creator: " + e.getMessage(), e);
        }
    }

    @Override
    public List<Challenge> findDiscoverable(String filter, Date today) {
        List<Challenge> challenges = new ArrayList<>();
        String sql;
        boolean hasDateParam = false;

        if ("ACTIVE".equalsIgnoreCase(filter)) {
            sql = "SELECT challenge_id, created_by, name, description, goal_value, goal_unit, points_reward, start_date, end_date, status, created_at " +
                  "FROM challenges WHERE status = 'ACTIVE' AND start_date <= ? AND end_date >= ? ORDER BY end_date ASC";
            hasDateParam = true;
        } else if ("UPCOMING".equalsIgnoreCase(filter)) {
            sql = "SELECT challenge_id, created_by, name, description, goal_value, goal_unit, points_reward, start_date, end_date, status, created_at " +
                  "FROM challenges WHERE status = 'ACTIVE' AND start_date > ? ORDER BY start_date ASC";
            hasDateParam = true;
        } else if ("COMPLETED".equalsIgnoreCase(filter)) {
            sql = "SELECT challenge_id, created_by, name, description, goal_value, goal_unit, points_reward, start_date, end_date, status, created_at " +
                  "FROM challenges WHERE status = 'COMPLETED' OR (status = 'ACTIVE' AND end_date < ?) ORDER BY end_date DESC";
            hasDateParam = true;
        } else {
            // Default "ALL" active/upcoming/completed
            sql = "SELECT challenge_id, created_by, name, description, goal_value, goal_unit, points_reward, start_date, end_date, status, created_at " +
                  "FROM challenges WHERE status IN ('ACTIVE', 'COMPLETED') ORDER BY status ASC, end_date ASC";
        }

        try (Connection conn = DatabaseConnectionPool.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            if (hasDateParam) {
                if ("ACTIVE".equalsIgnoreCase(filter)) {
                    stmt.setDate(1, today);
                    stmt.setDate(2, today);
                } else {
                    stmt.setDate(1, today);
                }
            }
            try (ResultSet rs = stmt.executeQuery()) {
                while (rs.next()) {
                    challenges.add(mapResultSet(rs));
                }
            }
            return challenges;
        } catch (SQLException e) {
            logger.error("Error querying discoverable challenges (filter={}): {}", filter, e.getMessage());
            throw new DatabaseException("Failed to query discoverable challenges: " + e.getMessage(), e);
        }
    }

    @Override
    public List<Challenge> findPendingModeration() {
        List<Challenge> challenges = new ArrayList<>();
        try (Connection conn = DatabaseConnectionPool.getConnection();
             PreparedStatement stmt = conn.prepareStatement(SQL_FIND_PENDING_MODERATION);
             ResultSet rs = stmt.executeQuery()) {
            while (rs.next()) {
                challenges.add(mapResultSet(rs));
            }
            return challenges;
        } catch (SQLException e) {
            logger.error("Error querying pending moderation challenges: {}", e.getMessage());
            throw new DatabaseException("Failed to query pending moderation challenges: " + e.getMessage(), e);
        }
    }

    @Override
    public int countParticipants(Integer challengeId) {
        if (challengeId == null) return 0;
        try (Connection conn = DatabaseConnectionPool.getConnection();
             PreparedStatement stmt = conn.prepareStatement(SQL_COUNT_PARTICIPANTS)) {
            stmt.setInt(1, challengeId);
            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) {
                    return rs.getInt(1);
                }
            }
            return 0;
        } catch (SQLException e) {
            logger.error("Error counting participants for challenge {}: {}", challengeId, e.getMessage());
            throw new DatabaseException("Failed to count challenge participants: " + e.getMessage(), e);
        }
    }

    @Override
    public int countActive() {
        try (Connection conn = DatabaseConnectionPool.getConnection();
             PreparedStatement stmt = conn.prepareStatement(SQL_COUNT_ACTIVE);
             ResultSet rs = stmt.executeQuery()) {
            if (rs.next()) {
                return rs.getInt(1);
            }
            return 0;
        } catch (SQLException e) {
            logger.error("Error counting active challenges: {}", e.getMessage());
            throw new DatabaseException("Failed to count active challenges: " + e.getMessage(), e);
        }
    }

    @Override
    public int countPendingModeration() {
        try (Connection conn = DatabaseConnectionPool.getConnection();
             PreparedStatement stmt = conn.prepareStatement(SQL_COUNT_PENDING);
             ResultSet rs = stmt.executeQuery()) {
            if (rs.next()) {
                return rs.getInt(1);
            }
            return 0;
        } catch (SQLException e) {
            logger.error("Error counting pending challenges: {}", e.getMessage());
            throw new DatabaseException("Failed to count pending challenges: " + e.getMessage(), e);
        }
    }

    @Override
    public boolean update(Challenge challenge) {
        try (Connection conn = DatabaseConnectionPool.getConnection();
             PreparedStatement stmt = conn.prepareStatement(SQL_UPDATE)) {
            stmt.setString(1, challenge.getName());
            stmt.setString(2, challenge.getDescription());
            stmt.setBigDecimal(3, challenge.getGoalValue());
            stmt.setString(4, challenge.getGoalUnit());
            stmt.setInt(5, challenge.getPointsReward() != null ? challenge.getPointsReward() : 0);
            stmt.setDate(6, challenge.getStartDate());
            stmt.setDate(7, challenge.getEndDate());
            stmt.setString(8, challenge.getStatus().name());
            stmt.setInt(9, challenge.getChallengeId());

            return stmt.executeUpdate() > 0;
        } catch (SQLException e) {
            logger.error("Error updating challenge {}: {}", challenge.getChallengeId(), e.getMessage());
            throw new DatabaseException("Failed to update challenge: " + e.getMessage(), e);
        }
    }

    @Override
    public boolean updateStatus(Integer challengeId, Challenge.ChallengeStatus status) {
        try (Connection conn = DatabaseConnectionPool.getConnection();
             PreparedStatement stmt = conn.prepareStatement(SQL_UPDATE_STATUS)) {
            stmt.setString(1, status.name());
            stmt.setInt(2, challengeId);
            return stmt.executeUpdate() > 0;
        } catch (SQLException e) {
            logger.error("Error updating challenge status {}: {}", challengeId, e.getMessage());
            throw new DatabaseException("Failed to update challenge status: " + e.getMessage(), e);
        }
    }

    @Override
    public boolean delete(Integer challengeId) {
        try (Connection conn = DatabaseConnectionPool.getConnection();
             PreparedStatement stmt = conn.prepareStatement(SQL_DELETE)) {
            stmt.setInt(1, challengeId);
            return stmt.executeUpdate() > 0;
        } catch (SQLException e) {
            logger.error("Error deleting challenge {}: {}", challengeId, e.getMessage());
            throw new DatabaseException("Failed to delete challenge: " + e.getMessage(), e);
        }
    }

    private Challenge mapResultSet(ResultSet rs) throws SQLException {
        Challenge challenge = new Challenge();
        challenge.setChallengeId(rs.getInt("challenge_id"));
        challenge.setCreatedBy(rs.getInt("created_by"));
        challenge.setName(rs.getString("name"));
        challenge.setDescription(rs.getString("description"));
        challenge.setGoalValue(rs.getBigDecimal("goal_value"));
        challenge.setGoalUnit(rs.getString("goal_unit"));
        challenge.setPointsReward(rs.getInt("points_reward"));
        challenge.setStartDate(rs.getDate("start_date"));
        challenge.setEndDate(rs.getDate("end_date"));
        challenge.setStatus(Challenge.ChallengeStatus.valueOf(rs.getString("status")));
        challenge.setCreatedAt(rs.getTimestamp("created_at"));
        return challenge;
    }
}
