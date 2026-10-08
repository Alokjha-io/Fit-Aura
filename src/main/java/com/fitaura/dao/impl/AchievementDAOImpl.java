package com.fitaura.dao.impl;

import com.fitaura.dao.AchievementDAO;
import com.fitaura.exception.DatabaseException;
import com.fitaura.model.Achievement;
import com.fitaura.util.DatabaseConnectionPool;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

/**
 * JDBC implementation of AchievementDAO.
 */
public class AchievementDAOImpl implements AchievementDAO {

    private static final Logger logger = LoggerFactory.getLogger(AchievementDAOImpl.class);

    private static final String SQL_INSERT =
            "INSERT INTO achievements (name, description, points, icon_name, is_active) " +
            "VALUES (?, ?, ?, ?, ?)";

    private static final String SQL_FIND_BY_ID =
            "SELECT achievement_id, name, description, points, icon_name, is_active " +
            "FROM achievements WHERE achievement_id = ?";

    private static final String SQL_FIND_BY_NAME =
            "SELECT achievement_id, name, description, points, icon_name, is_active " +
            "FROM achievements WHERE name = ?";

    private static final String SQL_FIND_ALL_ACTIVE =
            "SELECT achievement_id, name, description, points, icon_name, is_active " +
            "FROM achievements WHERE is_active = TRUE ORDER BY points ASC";

    private static final String SQL_UPDATE =
            "UPDATE achievements SET name = ?, description = ?, points = ?, icon_name = ?, is_active = ? " +
            "WHERE achievement_id = ?";

    @Override
    public Integer create(Achievement achievement) {
        try (Connection conn = DatabaseConnectionPool.getConnection();
             PreparedStatement stmt = conn.prepareStatement(SQL_INSERT, Statement.RETURN_GENERATED_KEYS)) {
            stmt.setString(1, achievement.getName());
            stmt.setString(2, achievement.getDescription());
            stmt.setInt(3, achievement.getPoints() != null ? achievement.getPoints() : 0);
            stmt.setString(4, achievement.getIconName());
            stmt.setBoolean(5, achievement.getIsActive() != null ? achievement.getIsActive() : true);

            int affected = stmt.executeUpdate();
            if (affected == 0) {
                throw new DatabaseException("Failed to insert achievement.");
            }
            try (ResultSet keys = stmt.getGeneratedKeys()) {
                if (keys.next()) {
                    int id = keys.getInt(1);
                    achievement.setAchievementId(id);
                    return id;
                }
            }
            throw new DatabaseException("Failed to obtain achievement ID.");
        } catch (SQLException e) {
            logger.error("Error creating achievement: {}", e.getMessage());
            throw new DatabaseException("Failed to persist achievement: " + e.getMessage(), e);
        }
    }

    @Override
    public Optional<Achievement> findById(Integer achievementId) {
        try (Connection conn = DatabaseConnectionPool.getConnection();
             PreparedStatement stmt = conn.prepareStatement(SQL_FIND_BY_ID)) {
            stmt.setInt(1, achievementId);
            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) {
                    return Optional.of(mapResultSet(rs));
                }
            }
            return Optional.empty();
        } catch (SQLException e) {
            logger.error("Error finding achievement by id {}: {}", achievementId, e.getMessage());
            throw new DatabaseException("Failed to query achievement: " + e.getMessage(), e);
        }
    }

    @Override
    public Optional<Achievement> findByName(String name) {
        try (Connection conn = DatabaseConnectionPool.getConnection();
             PreparedStatement stmt = conn.prepareStatement(SQL_FIND_BY_NAME)) {
            stmt.setString(1, name);
            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) {
                    return Optional.of(mapResultSet(rs));
                }
            }
            return Optional.empty();
        } catch (SQLException e) {
            logger.error("Error finding achievement by name {}: {}", name, e.getMessage());
            throw new DatabaseException("Failed to query achievement: " + e.getMessage(), e);
        }
    }

    @Override
    public List<Achievement> findAllActive() {
        List<Achievement> list = new ArrayList<>();
        try (Connection conn = DatabaseConnectionPool.getConnection();
             PreparedStatement stmt = conn.prepareStatement(SQL_FIND_ALL_ACTIVE);
             ResultSet rs = stmt.executeQuery()) {
            while (rs.next()) {
                list.add(mapResultSet(rs));
            }
            return list;
        } catch (SQLException e) {
            logger.error("Error querying active achievements: {}", e.getMessage());
            throw new DatabaseException("Failed to query achievements: " + e.getMessage(), e);
        }
    }

    @Override
    public boolean update(Achievement achievement) {
        try (Connection conn = DatabaseConnectionPool.getConnection();
             PreparedStatement stmt = conn.prepareStatement(SQL_UPDATE)) {
            stmt.setString(1, achievement.getName());
            stmt.setString(2, achievement.getDescription());
            stmt.setInt(3, achievement.getPoints() != null ? achievement.getPoints() : 0);
            stmt.setString(4, achievement.getIconName());
            stmt.setBoolean(5, achievement.getIsActive() != null ? achievement.getIsActive() : true);
            stmt.setInt(6, achievement.getAchievementId());
            return stmt.executeUpdate() > 0;
        } catch (SQLException e) {
            logger.error("Error updating achievement {}: {}", achievement.getAchievementId(), e.getMessage());
            throw new DatabaseException("Failed to update achievement: " + e.getMessage(), e);
        }
    }

    private Achievement mapResultSet(ResultSet rs) throws SQLException {
        Achievement a = new Achievement();
        a.setAchievementId(rs.getInt("achievement_id"));
        a.setName(rs.getString("name"));
        a.setDescription(rs.getString("description"));
        a.setPoints(rs.getInt("points"));
        a.setIconName(rs.getString("icon_name"));
        a.setIsActive(rs.getBoolean("is_active"));
        return a;
    }
}
