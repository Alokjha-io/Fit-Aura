package com.fitaura.dao.impl;

import com.fitaura.dao.PointTransactionDAO;
import com.fitaura.dto.LeaderboardEntryDTO;
import com.fitaura.exception.DatabaseException;
import com.fitaura.model.PointTransaction;
import com.fitaura.model.User;
import com.fitaura.util.DatabaseConnectionPool;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

/**
 * JDBC implementation of PointTransactionDAO.
 */
public class PointTransactionDAOImpl implements PointTransactionDAO {

    private static final Logger logger = LoggerFactory.getLogger(PointTransactionDAOImpl.class);

    private static final String SQL_INSERT =
            "INSERT INTO point_transactions (user_id, source_type, source_id, points, description) " +
            "VALUES (?, ?, ?, ?, ?)";

    private static final String SQL_FIND_BY_USER =
            "SELECT transaction_id, user_id, source_type, source_id, points, description, created_at " +
            "FROM point_transactions WHERE user_id = ? ORDER BY created_at DESC";

    private static final String SQL_FIND_RECENT_BY_USER =
            "SELECT transaction_id, user_id, source_type, source_id, points, description, created_at " +
            "FROM point_transactions WHERE user_id = ? ORDER BY created_at DESC LIMIT ?";

    private static final String SQL_SUM_POINTS_BY_USER =
            "SELECT COALESCE(SUM(points), 0) FROM point_transactions WHERE user_id = ?";

    private static final String SQL_CHECK_SOURCE_EXISTS =
            "SELECT 1 FROM point_transactions WHERE user_id = ? AND source_type = ? AND source_id = ? LIMIT 1";

    private static final String SQL_SOCIAL_LEADERBOARD =
            "SELECT u.user_id, u.display_name, u.full_name, u.privacy_mode, " +
            "COALESCE(SUM(pt.points), 0) AS total_points, " +
            "(SELECT COUNT(*) FROM workouts w WHERE w.user_id = u.user_id) AS workout_count, " +
            "(SELECT COUNT(*) FROM user_achievements ua WHERE ua.user_id = u.user_id) AS achievement_count " +
            "FROM users u " +
            "JOIN point_transactions pt ON u.user_id = pt.user_id " +
            "WHERE u.account_status = 'ACTIVE' AND u.privacy_mode = 'SOCIAL' " +
            "GROUP BY u.user_id, u.display_name, u.full_name, u.privacy_mode " +
            "HAVING total_points > 0 " +
            "ORDER BY total_points DESC, u.user_id ASC " +
            "LIMIT ? OFFSET ?";

    private static final String SQL_COUNT_SOCIAL_LEADERBOARD_USERS =
            "SELECT COUNT(DISTINCT u.user_id) " +
            "FROM users u " +
            "JOIN point_transactions pt ON u.user_id = pt.user_id " +
            "WHERE u.account_status = 'ACTIVE' AND u.privacy_mode = 'SOCIAL'";

    private static final String SQL_USER_SOCIAL_RANK =
            "SELECT COUNT(*) + 1 AS user_rank " +
            "FROM ( " +
            "    SELECT u.user_id, COALESCE(SUM(pt.points), 0) AS points_sum " +
            "    FROM users u " +
            "    JOIN point_transactions pt ON u.user_id = pt.user_id " +
            "    WHERE u.account_status = 'ACTIVE' AND u.privacy_mode = 'SOCIAL' " +
            "    GROUP BY u.user_id " +
            ") sub " +
            "WHERE sub.points_sum > ( " +
            "    SELECT COALESCE(SUM(pt2.points), 0) " +
            "    FROM point_transactions pt2 " +
            "    WHERE pt2.user_id = ? " +
            ")";

    @Override
    public Integer create(PointTransaction tx) {
        try (Connection conn = DatabaseConnectionPool.getConnection()) {
            return create(tx, conn);
        } catch (SQLException e) {
            logger.error("Error creating point transaction: {}", e.getMessage());
            throw new DatabaseException("Failed to insert point transaction: " + e.getMessage(), e);
        }
    }

    @Override
    public Integer create(PointTransaction tx, Connection conn) {
        try (PreparedStatement stmt = conn.prepareStatement(SQL_INSERT, Statement.RETURN_GENERATED_KEYS)) {
            stmt.setInt(1, tx.getUserId());
            stmt.setString(2, tx.getSourceType().name());
            if (tx.getSourceId() != null) stmt.setInt(3, tx.getSourceId()); else stmt.setNull(3, Types.INTEGER);
            stmt.setInt(4, tx.getPoints());
            stmt.setString(5, tx.getDescription());

            int affected = stmt.executeUpdate();
            if (affected == 0) {
                throw new DatabaseException("Failed to insert point transaction.");
            }
            try (ResultSet keys = stmt.getGeneratedKeys()) {
                if (keys.next()) {
                    int id = keys.getInt(1);
                    tx.setTransactionId(id);
                    return id;
                }
            }
            throw new DatabaseException("Failed to obtain transaction ID.");
        } catch (SQLException e) {
            logger.error("Error persisting point transaction: {}", e.getMessage());
            throw new DatabaseException("Failed to persist point transaction: " + e.getMessage(), e);
        }
    }

    @Override
    public List<PointTransaction> findByUserId(Integer userId) {
        List<PointTransaction> list = new ArrayList<>();
        if (userId == null) return list;
        try (Connection conn = DatabaseConnectionPool.getConnection();
             PreparedStatement stmt = conn.prepareStatement(SQL_FIND_BY_USER)) {
            stmt.setInt(1, userId);
            try (ResultSet rs = stmt.executeQuery()) {
                while (rs.next()) {
                    list.add(mapResultSet(rs));
                }
            }
            return list;
        } catch (SQLException e) {
            logger.error("Error querying point transactions for user {}: {}", userId, e.getMessage());
            throw new DatabaseException("Failed to query point transactions: " + e.getMessage(), e);
        }
    }

    @Override
    public List<PointTransaction> findRecentByUserId(Integer userId, int limit) {
        List<PointTransaction> list = new ArrayList<>();
        if (userId == null) return list;
        int validLimit = limit > 0 ? limit : 10;
        try (Connection conn = DatabaseConnectionPool.getConnection();
             PreparedStatement stmt = conn.prepareStatement(SQL_FIND_RECENT_BY_USER)) {
            stmt.setInt(1, userId);
            stmt.setInt(2, validLimit);
            try (ResultSet rs = stmt.executeQuery()) {
                while (rs.next()) {
                    list.add(mapResultSet(rs));
                }
            }
            return list;
        } catch (SQLException e) {
            logger.error("Error querying recent point transactions for user {}: {}", userId, e.getMessage());
            throw new DatabaseException("Failed to query recent point transactions: " + e.getMessage(), e);
        }
    }

    @Override
    public int calculateTotalPoints(Integer userId) {
        if (userId == null) return 0;
        try (Connection conn = DatabaseConnectionPool.getConnection()) {
            return calculateTotalPoints(userId, conn);
        } catch (SQLException e) {
            logger.error("Error calculating total points for user {}: {}", userId, e.getMessage());
            throw new DatabaseException("Failed to calculate total points: " + e.getMessage(), e);
        }
    }

    @Override
    public int calculateTotalPoints(Integer userId, Connection conn) {
        if (userId == null) return 0;
        try (PreparedStatement stmt = conn.prepareStatement(SQL_SUM_POINTS_BY_USER)) {
            stmt.setInt(1, userId);
            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) {
                    return rs.getInt(1);
                }
            }
            return 0;
        } catch (SQLException e) {
            logger.error("Error calculating total points: {}", e.getMessage());
            throw new DatabaseException("Failed to sum points: " + e.getMessage(), e);
        }
    }

    @Override
    public boolean hasSourceTransaction(Integer userId, PointTransaction.SourceType sourceType, Integer sourceId) {
        if (userId == null || sourceType == null || sourceId == null) return false;
        try (Connection conn = DatabaseConnectionPool.getConnection()) {
            return hasSourceTransaction(userId, sourceType, sourceId, conn);
        } catch (SQLException e) {
            logger.error("Error checking source transaction: {}", e.getMessage());
            throw new DatabaseException("Failed to verify source transaction: " + e.getMessage(), e);
        }
    }

    @Override
    public boolean hasSourceTransaction(Integer userId, PointTransaction.SourceType sourceType, Integer sourceId, Connection conn) {
        if (userId == null || sourceType == null || sourceId == null) return false;
        try (PreparedStatement stmt = conn.prepareStatement(SQL_CHECK_SOURCE_EXISTS)) {
            stmt.setInt(1, userId);
            stmt.setString(2, sourceType.name());
            stmt.setInt(3, sourceId);
            try (ResultSet rs = stmt.executeQuery()) {
                return rs.next();
            }
        } catch (SQLException e) {
            logger.error("Error checking source transaction: {}", e.getMessage());
            throw new DatabaseException("Failed to check source transaction: " + e.getMessage(), e);
        }
    }

    @Override
    public List<LeaderboardEntryDTO> getSocialLeaderboard(int limit, int offset) {
        return getSocialLeaderboardForPeriod(0, limit, offset);
    }

    @Override
    public List<LeaderboardEntryDTO> getSocialLeaderboardForPeriod(int days, int limit, int offset) {
        List<LeaderboardEntryDTO> list = new ArrayList<>();
        int validLimit = limit > 0 ? Math.min(limit, 100) : 25;
        int validOffset = Math.max(0, offset);

        String sql;
        if (days > 0) {
            sql = "SELECT u.user_id, u.display_name, u.full_name, u.privacy_mode, " +
                  "COALESCE(SUM(pt.points), 0) AS total_points, " +
                  "(SELECT COUNT(*) FROM workouts w WHERE w.user_id = u.user_id AND w.workout_date >= DATE_SUB(CURRENT_DATE, INTERVAL ? DAY)) AS workout_count, " +
                  "(SELECT COUNT(*) FROM user_achievements ua WHERE ua.user_id = u.user_id) AS achievement_count " +
                  "FROM users u " +
                  "JOIN point_transactions pt ON u.user_id = pt.user_id " +
                  "WHERE u.account_status = 'ACTIVE' AND u.privacy_mode = 'SOCIAL' " +
                  "AND pt.created_at >= DATE_SUB(NOW(), INTERVAL ? DAY) " +
                  "GROUP BY u.user_id, u.display_name, u.full_name, u.privacy_mode " +
                  "HAVING total_points > 0 " +
                  "ORDER BY total_points DESC, u.user_id ASC " +
                  "LIMIT ? OFFSET ?";
        } else {
            sql = SQL_SOCIAL_LEADERBOARD;
        }

        try (Connection conn = DatabaseConnectionPool.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            if (days > 0) {
                stmt.setInt(1, days);
                stmt.setInt(2, days);
                stmt.setInt(3, validLimit);
                stmt.setInt(4, validOffset);
            } else {
                stmt.setInt(1, validLimit);
                stmt.setInt(2, validOffset);
            }

            try (ResultSet rs = stmt.executeQuery()) {
                int currentPoints = -1;
                int currentRank = 0;
                int position = validOffset;

                while (rs.next()) {
                    position++;
                    int pts = rs.getInt("total_points");
                    if (pts != currentPoints) {
                        currentRank = position;
                        currentPoints = pts;
                    }

                    LeaderboardEntryDTO entry = new LeaderboardEntryDTO();
                    entry.setRank(currentRank);
                    entry.setUserId(rs.getInt("user_id"));
                    entry.setDisplayName(rs.getString("display_name"));
                    entry.setPrivacyMode(User.PrivacyMode.valueOf(rs.getString("privacy_mode")));
                    entry.setTotalPoints(pts);
                    entry.setTotalWorkouts(rs.getInt("workout_count"));
                    entry.setEarnedAchievements(rs.getInt("achievement_count"));
                    list.add(entry);
                }
            }
            return list;
        } catch (SQLException e) {
            logger.error("Error querying social leaderboard for period: {}", e.getMessage());
            throw new DatabaseException("Failed to query social leaderboard: " + e.getMessage(), e);
        }
    }

    @Override
    public int countSocialLeaderboardUsers() {
        try (Connection conn = DatabaseConnectionPool.getConnection();
             PreparedStatement stmt = conn.prepareStatement(SQL_COUNT_SOCIAL_LEADERBOARD_USERS);
             ResultSet rs = stmt.executeQuery()) {
            if (rs.next()) {
                return rs.getInt(1);
            }
            return 0;
        } catch (SQLException e) {
            logger.error("Error counting social leaderboard users: {}", e.getMessage());
            throw new DatabaseException("Failed to count leaderboard users: " + e.getMessage(), e);
        }
    }

    @Override
    public Integer calculateUserSocialRank(Integer userId) {
        if (userId == null) return null;
        try (Connection conn = DatabaseConnectionPool.getConnection();
             PreparedStatement stmt = conn.prepareStatement(SQL_USER_SOCIAL_RANK)) {
            stmt.setInt(1, userId);
            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) {
                    return rs.getInt("user_rank");
                }
            }
            return null;
        } catch (SQLException e) {
            logger.error("Error calculating user social rank {}: {}", userId, e.getMessage());
            throw new DatabaseException("Failed to calculate user social rank: " + e.getMessage(), e);
        }
    }

    private PointTransaction mapResultSet(ResultSet rs) throws SQLException {
        PointTransaction tx = new PointTransaction();
        tx.setTransactionId(rs.getInt("transaction_id"));
        tx.setUserId(rs.getInt("user_id"));
        tx.setSourceType(PointTransaction.SourceType.valueOf(rs.getString("source_type")));
        int srcId = rs.getInt("source_id");
        tx.setSourceId(rs.wasNull() ? null : srcId);
        tx.setPoints(rs.getInt("points"));
        tx.setDescription(rs.getString("description"));
        tx.setCreatedAt(rs.getTimestamp("created_at"));
        return tx;
    }
}
