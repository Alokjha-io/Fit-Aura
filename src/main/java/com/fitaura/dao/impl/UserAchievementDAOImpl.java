package com.fitaura.dao.impl;

import com.fitaura.dao.UserAchievementDAO;
import com.fitaura.exception.DatabaseException;
import com.fitaura.model.UserAchievement;
import com.fitaura.util.DatabaseConnectionPool;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

/**
 * JDBC implementation of UserAchievementDAO.
 */
public class UserAchievementDAOImpl implements UserAchievementDAO {

    private static final Logger logger = LoggerFactory.getLogger(UserAchievementDAOImpl.class);

    private static final String SQL_INSERT =
            "INSERT INTO user_achievements (user_id, achievement_id) VALUES (?, ?)";

    private static final String SQL_FIND_BY_USER =
            "SELECT user_achievement_id, user_id, achievement_id, earned_at " +
            "FROM user_achievements WHERE user_id = ? ORDER BY earned_at DESC";

    private static final String SQL_CHECK_EXISTS =
            "SELECT 1 FROM user_achievements WHERE user_id = ? AND achievement_id = ?";

    @Override
    public Integer create(UserAchievement userAchievement) {
        try (Connection conn = DatabaseConnectionPool.getConnection()) {
            return create(userAchievement, conn);
        } catch (SQLException e) {
            logger.error("Error creating user achievement: {}", e.getMessage());
            throw new DatabaseException("Failed to insert user achievement: " + e.getMessage(), e);
        }
    }

    @Override
    public Integer create(UserAchievement userAchievement, Connection conn) {
        try (PreparedStatement stmt = conn.prepareStatement(SQL_INSERT, Statement.RETURN_GENERATED_KEYS)) {
            stmt.setInt(1, userAchievement.getUserId());
            stmt.setInt(2, userAchievement.getAchievementId());

            int affected = stmt.executeUpdate();
            if (affected == 0) {
                throw new DatabaseException("Failed to insert user achievement.");
            }
            try (ResultSet keys = stmt.getGeneratedKeys()) {
                if (keys.next()) {
                    int id = keys.getInt(1);
                    userAchievement.setUserAchievementId(id);
                    return id;
                }
            }
            throw new DatabaseException("Failed to obtain user achievement ID.");
        } catch (SQLException e) {
            logger.error("Error persisting user achievement: {}", e.getMessage());
            throw new DatabaseException("Failed to persist user achievement: " + e.getMessage(), e);
        }
    }

    @Override
    public List<UserAchievement> findByUserId(Integer userId) {
        List<UserAchievement> list = new ArrayList<>();
        try (Connection conn = DatabaseConnectionPool.getConnection();
             PreparedStatement stmt = conn.prepareStatement(SQL_FIND_BY_USER)) {
            stmt.setInt(1, userId);
            try (ResultSet rs = stmt.executeQuery()) {
                while (rs.next()) {
                    UserAchievement ua = new UserAchievement();
                    ua.setUserAchievementId(rs.getInt("user_achievement_id"));
                    ua.setUserId(rs.getInt("user_id"));
                    ua.setAchievementId(rs.getInt("achievement_id"));
                    ua.setEarnedAt(rs.getTimestamp("earned_at"));
                    list.add(ua);
                }
            }
            return list;
        } catch (SQLException e) {
            logger.error("Error querying user achievements for user {}: {}", userId, e.getMessage());
            throw new DatabaseException("Failed to query user achievements: " + e.getMessage(), e);
        }
    }

    @Override
    public boolean hasUserEarned(Integer userId, Integer achievementId) {
        try (Connection conn = DatabaseConnectionPool.getConnection()) {
            return hasUserEarned(userId, achievementId, conn);
        } catch (SQLException e) {
            logger.error("Error checking user achievement: {}", e.getMessage());
            throw new DatabaseException("Failed to check user achievement: " + e.getMessage(), e);
        }
    }

    @Override
    public boolean hasUserEarned(Integer userId, Integer achievementId, Connection conn) {
        try (PreparedStatement stmt = conn.prepareStatement(SQL_CHECK_EXISTS)) {
            stmt.setInt(1, userId);
            stmt.setInt(2, achievementId);
            try (ResultSet rs = stmt.executeQuery()) {
                return rs.next();
            }
        } catch (SQLException e) {
            logger.error("Error checking user achievement: {}", e.getMessage());
            throw new DatabaseException("Failed to check user achievement: " + e.getMessage(), e);
        }
    }
}
