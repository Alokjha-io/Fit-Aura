package com.fitaura.dao.impl;

import com.fitaura.dao.CompetitionDAO;
import com.fitaura.exception.DatabaseException;
import com.fitaura.model.Competition;
import com.fitaura.model.CompetitionParticipant;
import com.fitaura.util.DatabaseConnectionPool;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.math.BigDecimal;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

public class CompetitionDAOImpl implements CompetitionDAO {

    private static final Logger logger = LoggerFactory.getLogger(CompetitionDAOImpl.class);

    @Override
    public Optional<Competition> findById(Integer id) {
        String sql = "SELECT c.competition_id, c.name, c.description, c.metric, c.target_value, " +
                     "c.start_date, c.end_date, c.reward_points, c.status, c.created_by, c.created_at, c.updated_at, " +
                     "(SELECT COUNT(*) FROM competition_participants cp WHERE cp.competition_id = c.competition_id) AS p_count " +
                     "FROM competitions c WHERE c.competition_id = ?";
        try (Connection conn = DatabaseConnectionPool.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, id);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    return Optional.of(mapCompetition(rs));
                }
            }
        } catch (SQLException e) {
            logger.error("Error finding competition #{}: {}", id, e.getMessage(), e);
            throw new DatabaseException("Database error finding competition.", e);
        }
        return Optional.empty();
    }

    @Override
    public List<Competition> findActive(Date today) {
        String sql = "SELECT c.competition_id, c.name, c.description, c.metric, c.target_value, " +
                     "c.start_date, c.end_date, c.reward_points, c.status, c.created_by, c.created_at, c.updated_at, " +
                     "(SELECT COUNT(*) FROM competition_participants cp WHERE cp.competition_id = c.competition_id) AS p_count " +
                     "FROM competitions c WHERE c.status = 'ACTIVE' AND c.start_date <= ? AND c.end_date >= ? " +
                     "ORDER BY c.end_date ASC";
        List<Competition> list = new ArrayList<>();
        try (Connection conn = DatabaseConnectionPool.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setDate(1, today);
            ps.setDate(2, today);
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    list.add(mapCompetition(rs));
                }
            }
        } catch (SQLException e) {
            logger.error("Error finding active competitions: {}", e.getMessage(), e);
            throw new DatabaseException("Database error finding active competitions.", e);
        }
        return list;
    }

    @Override
    public List<Competition> findUpcoming(Date today) {
        String sql = "SELECT c.competition_id, c.name, c.description, c.metric, c.target_value, " +
                     "c.start_date, c.end_date, c.reward_points, c.status, c.created_by, c.created_at, c.updated_at, " +
                     "(SELECT COUNT(*) FROM competition_participants cp WHERE cp.competition_id = c.competition_id) AS p_count " +
                     "FROM competitions c WHERE c.status = 'UPCOMING' OR (c.status = 'ACTIVE' AND c.start_date > ?) " +
                     "ORDER BY c.start_date ASC";
        List<Competition> list = new ArrayList<>();
        try (Connection conn = DatabaseConnectionPool.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setDate(1, today);
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    list.add(mapCompetition(rs));
                }
            }
        } catch (SQLException e) {
            logger.error("Error finding upcoming competitions: {}", e.getMessage(), e);
            throw new DatabaseException("Database error finding upcoming competitions.", e);
        }
        return list;
    }

    @Override
    public List<Competition> findCompleted(Date today) {
        String sql = "SELECT c.competition_id, c.name, c.description, c.metric, c.target_value, " +
                     "c.start_date, c.end_date, c.reward_points, c.status, c.created_by, c.created_at, c.updated_at, " +
                     "(SELECT COUNT(*) FROM competition_participants cp WHERE cp.competition_id = c.competition_id) AS p_count " +
                     "FROM competitions c WHERE c.status = 'COMPLETED' OR (c.status = 'ACTIVE' AND c.end_date < ?) " +
                     "ORDER BY c.end_date DESC LIMIT 20";
        List<Competition> list = new ArrayList<>();
        try (Connection conn = DatabaseConnectionPool.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setDate(1, today);
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    list.add(mapCompetition(rs));
                }
            }
        } catch (SQLException e) {
            logger.error("Error finding completed competitions: {}", e.getMessage(), e);
            throw new DatabaseException("Database error finding completed competitions.", e);
        }
        return list;
    }

    @Override
    public List<Competition> findAll(int limit, int offset) {
        String sql = "SELECT c.competition_id, c.name, c.description, c.metric, c.target_value, " +
                     "c.start_date, c.end_date, c.reward_points, c.status, c.created_by, c.created_at, c.updated_at, " +
                     "(SELECT COUNT(*) FROM competition_participants cp WHERE cp.competition_id = c.competition_id) AS p_count " +
                     "FROM competitions c ORDER BY c.created_at DESC LIMIT ? OFFSET ?";
        List<Competition> list = new ArrayList<>();
        try (Connection conn = DatabaseConnectionPool.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, limit > 0 ? limit : 50);
            ps.setInt(2, Math.max(0, offset));
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    list.add(mapCompetition(rs));
                }
            }
        } catch (SQLException e) {
            logger.error("Error listing all competitions: {}", e.getMessage(), e);
            throw new DatabaseException("Database error listing competitions.", e);
        }
        return list;
    }

    @Override
    public int countAll() {
        String sql = "SELECT COUNT(*) FROM competitions";
        try (Connection conn = DatabaseConnectionPool.getConnection();
             Statement s = conn.createStatement();
             ResultSet rs = s.executeQuery(sql)) {
            if (rs.next()) {
                return rs.getInt(1);
            }
        } catch (SQLException e) {
            logger.error("Error counting competitions: {}", e.getMessage(), e);
            throw new DatabaseException("Database error counting competitions.", e);
        }
        return 0;
    }

    @Override
    public Integer create(Competition comp) {
        String sql = "INSERT INTO competitions (name, description, metric, target_value, start_date, end_date, " +
                     "reward_points, status, created_by) VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?)";
        try (Connection conn = DatabaseConnectionPool.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {
            ps.setString(1, comp.getName());
            ps.setString(2, comp.getDescription());
            ps.setString(3, comp.getMetric() != null ? comp.getMetric().name() : Competition.Metric.WORKOUT_COUNT.name());
            ps.setBigDecimal(4, comp.getTargetValue());
            ps.setDate(5, comp.getStartDate());
            ps.setDate(6, comp.getEndDate());
            ps.setInt(7, comp.getRewardPoints() != null ? comp.getRewardPoints() : 50);
            ps.setString(8, comp.getStatus() != null ? comp.getStatus().name() : Competition.Status.ACTIVE.name());
            if (comp.getCreatedBy() != null) {
                ps.setInt(9, comp.getCreatedBy());
            } else {
                ps.setNull(9, Types.INTEGER);
            }

            ps.executeUpdate();
            try (ResultSet keys = ps.getGeneratedKeys()) {
                if (keys.next()) {
                    comp.setCompetitionId(keys.getInt(1));
                    return comp.getCompetitionId();
                }
            }
        } catch (SQLException e) {
            logger.error("Error creating competition: {}", e.getMessage(), e);
            throw new DatabaseException("Database error creating competition.", e);
        }
        return null;
    }

    @Override
    public boolean update(Competition comp) {
        String sql = "UPDATE competitions SET name = ?, description = ?, metric = ?, target_value = ?, " +
                     "start_date = ?, end_date = ?, reward_points = ?, status = ? WHERE competition_id = ?";
        try (Connection conn = DatabaseConnectionPool.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, comp.getName());
            ps.setString(2, comp.getDescription());
            ps.setString(3, comp.getMetric().name());
            ps.setBigDecimal(4, comp.getTargetValue());
            ps.setDate(5, comp.getStartDate());
            ps.setDate(6, comp.getEndDate());
            ps.setInt(7, comp.getRewardPoints());
            ps.setString(8, comp.getStatus().name());
            ps.setInt(9, comp.getCompetitionId());
            return ps.executeUpdate() > 0;
        } catch (SQLException e) {
            logger.error("Error updating competition #{}: {}", comp.getCompetitionId(), e.getMessage(), e);
            throw new DatabaseException("Database error updating competition.", e);
        }
    }

    @Override
    public boolean delete(Integer id) {
        String sql = "DELETE FROM competitions WHERE competition_id = ?";
        try (Connection conn = DatabaseConnectionPool.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, id);
            return ps.executeUpdate() > 0;
        } catch (SQLException e) {
            logger.error("Error deleting competition #{}: {}", id, e.getMessage(), e);
            throw new DatabaseException("Database error deleting competition.", e);
        }
    }

    @Override
    public boolean isUserParticipating(Integer compId, Integer userId) {
        String sql = "SELECT COUNT(*) FROM competition_participants WHERE competition_id = ? AND user_id = ?";
        try (Connection conn = DatabaseConnectionPool.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, compId);
            ps.setInt(2, userId);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    return rs.getInt(1) > 0;
                }
            }
        } catch (SQLException e) {
            logger.error("Error checking participation: {}", e.getMessage(), e);
            throw new DatabaseException("Database error checking participation.", e);
        }
        return false;
    }

    @Override
    public boolean joinCompetition(Integer compId, Integer userId) {
        String sql = "INSERT INTO competition_participants (competition_id, user_id, current_score) VALUES (?, ?, 0.00)";
        try (Connection conn = DatabaseConnectionPool.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, compId);
            ps.setInt(2, userId);
            return ps.executeUpdate() > 0;
        } catch (SQLException e) {
            logger.error("Error joining competition #{}: {}", compId, e.getMessage(), e);
            throw new DatabaseException("Database error joining competition.", e);
        }
    }

    @Override
    public boolean leaveCompetition(Integer compId, Integer userId) {
        String sql = "DELETE FROM competition_participants WHERE competition_id = ? AND user_id = ?";
        try (Connection conn = DatabaseConnectionPool.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, compId);
            ps.setInt(2, userId);
            return ps.executeUpdate() > 0;
        } catch (SQLException e) {
            logger.error("Error leaving competition #{}: {}", compId, e.getMessage(), e);
            throw new DatabaseException("Database error leaving competition.", e);
        }
    }

    @Override
    public List<CompetitionParticipant> getParticipants(Integer compId, int limit) {
        String sql = "SELECT cp.participant_id, cp.competition_id, cp.user_id, cp.joined_at, cp.current_score, " +
                     "cp.completed, cp.completed_at, u.display_name " +
                     "FROM competition_participants cp " +
                     "JOIN users u ON cp.user_id = u.user_id " +
                     "WHERE cp.competition_id = ? AND u.privacy_mode = 'SOCIAL' " +
                     "ORDER BY cp.current_score DESC, cp.joined_at ASC LIMIT ?";
        List<CompetitionParticipant> list = new ArrayList<>();
        try (Connection conn = DatabaseConnectionPool.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, compId);
            ps.setInt(2, limit > 0 ? limit : 50);
            try (ResultSet rs = ps.executeQuery()) {
                int currentRank = 1;
                BigDecimal previousScore = null;
                int position = 0;

                while (rs.next()) {
                    position++;
                    CompetitionParticipant p = new CompetitionParticipant();
                    p.setParticipantId(rs.getInt("participant_id"));
                    p.setCompetitionId(rs.getInt("competition_id"));
                    p.setUserId(rs.getInt("user_id"));
                    p.setJoinedAt(rs.getTimestamp("joined_at"));
                    p.setCurrentScore(rs.getBigDecimal("current_score"));
                    p.setCompleted(rs.getBoolean("completed"));
                    p.setCompletedAt(rs.getTimestamp("completed_at"));
                    p.setDisplayName(rs.getString("display_name"));

                    // Sports ranking: equal score receives equal rank
                    if (previousScore != null && p.getCurrentScore().compareTo(previousScore) == 0) {
                        p.setRank(currentRank);
                    } else {
                        currentRank = position;
                        p.setRank(currentRank);
                        previousScore = p.getCurrentScore();
                    }

                    list.add(p);
                }
            }
        } catch (SQLException e) {
            logger.error("Error getting competition participants: {}", e.getMessage(), e);
            throw new DatabaseException("Database error getting competition participants.", e);
        }
        return list;
    }

    @Override
    public int countParticipants(Integer compId) {
        String sql = "SELECT COUNT(*) FROM competition_participants cp " +
                     "JOIN users u ON cp.user_id = u.user_id " +
                     "WHERE cp.competition_id = ? AND u.privacy_mode = 'SOCIAL'";
        try (Connection conn = DatabaseConnectionPool.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, compId);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    return rs.getInt(1);
                }
            }
        } catch (SQLException e) {
            logger.error("Error counting competition participants: {}", e.getMessage(), e);
            throw new DatabaseException("Database error counting participants.", e);
        }
        return 0;
    }

    @Override
    public void updateParticipantScore(Integer compId, Integer userId, BigDecimal score) {
        String sql = "UPDATE competition_participants SET current_score = ? WHERE competition_id = ? AND user_id = ?";
        try (Connection conn = DatabaseConnectionPool.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setBigDecimal(1, score);
            ps.setInt(2, compId);
            ps.setInt(3, userId);
            ps.executeUpdate();
        } catch (SQLException e) {
            logger.error("Error updating participant score: {}", e.getMessage(), e);
            throw new DatabaseException("Database error updating score.", e);
        }
    }

    @Override
    public BigDecimal calculateUserScoreForMetric(Integer userId, Competition.Metric metric, Date startDate, Date endDate) {
        String sql = switch (metric) {
            case WORKOUT_COUNT -> "SELECT COUNT(*) FROM workouts WHERE user_id = ? AND workout_date BETWEEN ? AND ?";
            case TOTAL_MINUTES -> "SELECT COALESCE(SUM(duration_minutes), 0) FROM workouts WHERE user_id = ? AND workout_date BETWEEN ? AND ?";
            case CALORIES_BURNED -> "SELECT COALESCE(SUM(calories_burned), 0) FROM workouts WHERE user_id = ? AND workout_date BETWEEN ? AND ?";
            case STREAK_DAYS -> "SELECT COUNT(DISTINCT workout_date) FROM workouts WHERE user_id = ? AND workout_date BETWEEN ? AND ?";
        };

        try (Connection conn = DatabaseConnectionPool.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, userId);
            ps.setDate(2, startDate);
            ps.setDate(3, endDate);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    return rs.getBigDecimal(1);
                }
            }
        } catch (SQLException e) {
            logger.error("Error calculating metric score for user #{}: {}", userId, e.getMessage(), e);
            throw new DatabaseException("Database error calculating score.", e);
        }
        return BigDecimal.ZERO;
    }

    @Override
    public Integer getUserRankInCompetition(Integer compId, Integer userId) {
        List<CompetitionParticipant> list = getParticipants(compId, 500);
        for (CompetitionParticipant cp : list) {
            if (cp.getUserId().equals(userId)) {
                return cp.getRank();
            }
        }
        return null;
    }

    private Competition mapCompetition(ResultSet rs) throws SQLException {
        Competition c = new Competition();
        c.setCompetitionId(rs.getInt("competition_id"));
        c.setName(rs.getString("name"));
        c.setDescription(rs.getString("description"));
        String metricStr = rs.getString("metric");
        if (metricStr != null) {
            try {
                c.setMetric(Competition.Metric.valueOf(metricStr));
            } catch (IllegalArgumentException e) {
                c.setMetric(Competition.Metric.WORKOUT_COUNT);
            }
        }
        c.setTargetValue(rs.getBigDecimal("target_value"));
        c.setStartDate(rs.getDate("start_date"));
        c.setEndDate(rs.getDate("end_date"));
        c.setRewardPoints(rs.getInt("reward_points"));
        String statusStr = rs.getString("status");
        if (statusStr != null) {
            try {
                c.setStatus(Competition.Status.valueOf(statusStr));
            } catch (IllegalArgumentException e) {
                c.setStatus(Competition.Status.ACTIVE);
            }
        }
        int createdBy = rs.getInt("created_by");
        if (!rs.wasNull()) {
            c.setCreatedBy(createdBy);
        }
        c.setCreatedAt(rs.getTimestamp("created_at"));
        c.setUpdatedAt(rs.getTimestamp("updated_at"));
        c.setParticipantCount(rs.getInt("p_count"));
        return c;
    }
}
