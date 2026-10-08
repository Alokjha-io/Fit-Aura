package com.fitaura.dao.impl;

import com.fitaura.dao.ChallengeParticipantDAO;
import com.fitaura.dto.ParticipantSummaryDTO;
import com.fitaura.exception.DatabaseException;
import com.fitaura.model.ChallengeParticipant;
import com.fitaura.model.User;
import com.fitaura.util.DatabaseConnectionPool;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.math.BigDecimal;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

/**
 * JDBC implementation of ChallengeParticipantDAO.
 */
public class ChallengeParticipantDAOImpl implements ChallengeParticipantDAO {

    private static final Logger logger = LoggerFactory.getLogger(ChallengeParticipantDAOImpl.class);

    private static final String SQL_INSERT =
            "INSERT INTO challenge_participants (challenge_id, user_id, progress_value, status) " +
            "VALUES (?, ?, ?, ?)";

    private static final String SQL_FIND_BY_CHALLENGE_AND_USER =
            "SELECT participation_id, challenge_id, user_id, progress_value, status, joined_at, completed_at " +
            "FROM challenge_participants WHERE challenge_id = ? AND user_id = ?";

    private static final String SQL_FIND_BY_USER_ID =
            "SELECT participation_id, challenge_id, user_id, progress_value, status, joined_at, completed_at " +
            "FROM challenge_participants WHERE user_id = ? AND status != 'LEFT' ORDER BY joined_at DESC";

    private static final String SQL_FIND_BY_CHALLENGE_ID =
            "SELECT participation_id, challenge_id, user_id, progress_value, status, joined_at, completed_at " +
            "FROM challenge_participants WHERE challenge_id = ? AND status != 'LEFT' ORDER BY progress_value DESC";

    private static final String SQL_FIND_COMMUNITY_PARTICIPANTS =
            "SELECT cp.participation_id, cp.challenge_id, cp.user_id, cp.progress_value, cp.status, cp.joined_at, cp.completed_at, " +
            "u.display_name, u.privacy_mode " +
            "FROM challenge_participants cp " +
            "JOIN users u ON cp.user_id = u.user_id " +
            "WHERE cp.challenge_id = ? AND cp.status != 'LEFT' " +
            "ORDER BY cp.progress_value DESC, cp.joined_at ASC";

    private static final String SQL_UPDATE_PROGRESS =
            "UPDATE challenge_participants SET progress_value = ?, status = ? WHERE challenge_id = ? AND user_id = ?";

    private static final String SQL_COMPLETE =
            "UPDATE challenge_participants SET status = 'COMPLETED', completed_at = CURRENT_TIMESTAMP " +
            "WHERE challenge_id = ? AND user_id = ?";

    private static final String SQL_DELETE =
            "DELETE FROM challenge_participants WHERE challenge_id = ? AND user_id = ?";

    private static final String SQL_COUNT_BY_USER =
            "SELECT COUNT(*) FROM challenge_participants WHERE user_id = ? AND status != 'LEFT'";

    private static final String SQL_COUNT_COMPLETED_BY_USER =
            "SELECT COUNT(*) FROM challenge_participants WHERE user_id = ? AND status = 'COMPLETED'";

    @Override
    public Integer create(ChallengeParticipant participant) {
        try (Connection conn = DatabaseConnectionPool.getConnection();
             PreparedStatement stmt = conn.prepareStatement(SQL_INSERT, Statement.RETURN_GENERATED_KEYS)) {
            stmt.setInt(1, participant.getChallengeId());
            stmt.setInt(2, participant.getUserId());
            stmt.setBigDecimal(3, participant.getProgressValue() != null ? participant.getProgressValue() : BigDecimal.ZERO);
            stmt.setString(4, participant.getStatus() != null ? participant.getStatus().name() : ChallengeParticipant.ParticipantStatus.JOINED.name());

            int affected = stmt.executeUpdate();
            if (affected == 0) {
                throw new DatabaseException("Failed to insert challenge participation.");
            }
            try (ResultSet keys = stmt.getGeneratedKeys()) {
                if (keys.next()) {
                    int id = keys.getInt(1);
                    participant.setParticipationId(id);
                    return id;
                }
            }
            throw new DatabaseException("Failed to obtain participation ID.");
        } catch (SQLException e) {
            logger.error("Error creating challenge participant: {}", e.getMessage());
            throw new DatabaseException("Failed to persist challenge participant: " + e.getMessage(), e);
        }
    }

    @Override
    public Optional<ChallengeParticipant> findByChallengeAndUser(Integer challengeId, Integer userId) {
        if (challengeId == null || userId == null) return Optional.empty();
        try (Connection conn = DatabaseConnectionPool.getConnection();
             PreparedStatement stmt = conn.prepareStatement(SQL_FIND_BY_CHALLENGE_AND_USER)) {
            stmt.setInt(1, challengeId);
            stmt.setInt(2, userId);
            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) {
                    return Optional.of(mapResultSet(rs));
                }
            }
            return Optional.empty();
        } catch (SQLException e) {
            logger.error("Error querying challenge participant: {}", e.getMessage());
            throw new DatabaseException("Failed to query challenge participant: " + e.getMessage(), e);
        }
    }

    @Override
    public List<ChallengeParticipant> findByUserId(Integer userId) {
        List<ChallengeParticipant> list = new ArrayList<>();
        if (userId == null) return list;
        try (Connection conn = DatabaseConnectionPool.getConnection();
             PreparedStatement stmt = conn.prepareStatement(SQL_FIND_BY_USER_ID)) {
            stmt.setInt(1, userId);
            try (ResultSet rs = stmt.executeQuery()) {
                while (rs.next()) {
                    list.add(mapResultSet(rs));
                }
            }
            return list;
        } catch (SQLException e) {
            logger.error("Error querying challenge participations for user {}: {}", userId, e.getMessage());
            throw new DatabaseException("Failed to query user participations: " + e.getMessage(), e);
        }
    }

    @Override
    public List<ChallengeParticipant> findByChallengeId(Integer challengeId) {
        List<ChallengeParticipant> list = new ArrayList<>();
        if (challengeId == null) return list;
        try (Connection conn = DatabaseConnectionPool.getConnection();
             PreparedStatement stmt = conn.prepareStatement(SQL_FIND_BY_CHALLENGE_ID)) {
            stmt.setInt(1, challengeId);
            try (ResultSet rs = stmt.executeQuery()) {
                while (rs.next()) {
                    list.add(mapResultSet(rs));
                }
            }
            return list;
        } catch (SQLException e) {
            logger.error("Error querying participants for challenge {}: {}", challengeId, e.getMessage());
            throw new DatabaseException("Failed to query challenge participants: " + e.getMessage(), e);
        }
    }

    @Override
    public List<ParticipantSummaryDTO> findCommunityParticipants(Integer challengeId) {
        List<ParticipantSummaryDTO> list = new ArrayList<>();
        if (challengeId == null) return list;
        try (Connection conn = DatabaseConnectionPool.getConnection();
             PreparedStatement stmt = conn.prepareStatement(SQL_FIND_COMMUNITY_PARTICIPANTS)) {
            stmt.setInt(1, challengeId);
            try (ResultSet rs = stmt.executeQuery()) {
                while (rs.next()) {
                    ParticipantSummaryDTO dto = new ParticipantSummaryDTO();
                    dto.setUserId(rs.getInt("user_id"));
                    dto.setProgressValue(rs.getBigDecimal("progress_value"));
                    dto.setStatus(ChallengeParticipant.ParticipantStatus.valueOf(rs.getString("status")));
                    dto.setJoinedAt(rs.getTimestamp("joined_at"));
                    dto.setCompletedAt(rs.getTimestamp("completed_at"));
                    dto.setDisplayName(rs.getString("display_name"));
                    String privacyStr = rs.getString("privacy_mode");
                    dto.setPrivacyMode(privacyStr != null ? User.PrivacyMode.valueOf(privacyStr) : User.PrivacyMode.PERSONAL);
                    list.add(dto);
                }
            }
            return list;
        } catch (SQLException e) {
            logger.error("Error querying community participants for challenge {}: {}", challengeId, e.getMessage());
            throw new DatabaseException("Failed to query community participants: " + e.getMessage(), e);
        }
    }

    @Override
    public boolean updateProgress(Integer challengeId, Integer userId, BigDecimal progressValue, ChallengeParticipant.ParticipantStatus status) {
        try (Connection conn = DatabaseConnectionPool.getConnection();
             PreparedStatement stmt = conn.prepareStatement(SQL_UPDATE_PROGRESS)) {
            stmt.setBigDecimal(1, progressValue);
            stmt.setString(2, status.name());
            stmt.setInt(3, challengeId);
            stmt.setInt(4, userId);
            return stmt.executeUpdate() > 0;
        } catch (SQLException e) {
            logger.error("Error updating challenge progress: {}", e.getMessage());
            throw new DatabaseException("Failed to update challenge progress: " + e.getMessage(), e);
        }
    }

    @Override
    public boolean completeParticipation(Integer challengeId, Integer userId) {
        try (Connection conn = DatabaseConnectionPool.getConnection();
             PreparedStatement stmt = conn.prepareStatement(SQL_COMPLETE)) {
            stmt.setInt(1, challengeId);
            stmt.setInt(2, userId);
            return stmt.executeUpdate() > 0;
        } catch (SQLException e) {
            logger.error("Error completing challenge participation: {}", e.getMessage());
            throw new DatabaseException("Failed to complete participation: " + e.getMessage(), e);
        }
    }

    @Override
    public boolean delete(Integer challengeId, Integer userId) {
        try (Connection conn = DatabaseConnectionPool.getConnection();
             PreparedStatement stmt = conn.prepareStatement(SQL_DELETE)) {
            stmt.setInt(1, challengeId);
            stmt.setInt(2, userId);
            return stmt.executeUpdate() > 0;
        } catch (SQLException e) {
            logger.error("Error removing challenge participation: {}", e.getMessage());
            throw new DatabaseException("Failed to delete participation: " + e.getMessage(), e);
        }
    }

    @Override
    public int countByUserId(Integer userId) {
        if (userId == null) return 0;
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
            logger.error("Error counting challenge participations for user {}: {}", userId, e.getMessage());
            throw new DatabaseException("Failed to count user participations: " + e.getMessage(), e);
        }
    }

    @Override
    public int countCompletedByUserId(Integer userId) {
        if (userId == null) return 0;
        try (Connection conn = DatabaseConnectionPool.getConnection();
             PreparedStatement stmt = conn.prepareStatement(SQL_COUNT_COMPLETED_BY_USER)) {
            stmt.setInt(1, userId);
            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) {
                    return rs.getInt(1);
                }
            }
            return 0;
        } catch (SQLException e) {
            logger.error("Error counting completed challenges for user {}: {}", userId, e.getMessage());
            throw new DatabaseException("Failed to count completed challenges: " + e.getMessage(), e);
        }
    }

    private ChallengeParticipant mapResultSet(ResultSet rs) throws SQLException {
        ChallengeParticipant p = new ChallengeParticipant();
        p.setParticipationId(rs.getInt("participation_id"));
        p.setChallengeId(rs.getInt("challenge_id"));
        p.setUserId(rs.getInt("user_id"));
        p.setProgressValue(rs.getBigDecimal("progress_value"));
        p.setStatus(ChallengeParticipant.ParticipantStatus.valueOf(rs.getString("status")));
        p.setJoinedAt(rs.getTimestamp("joined_at"));
        p.setCompletedAt(rs.getTimestamp("completed_at"));
        return p;
    }
}
