package com.fitaura.dao.impl;

import com.fitaura.dao.SocialActivityDAO;
import com.fitaura.exception.DatabaseException;
import com.fitaura.model.SocialActivity;
import com.fitaura.util.DatabaseConnectionPool;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class SocialActivityDAOImpl implements SocialActivityDAO {

    private static final Logger logger = LoggerFactory.getLogger(SocialActivityDAOImpl.class);

    @Override
    public List<SocialActivity> getRecentFeed(int limit) {
        String sql = "SELECT sa.activity_id, sa.user_id, sa.activity_type, sa.title, sa.description, " +
                     "sa.created_at, u.display_name " +
                     "FROM social_activity sa " +
                     "JOIN users u ON sa.user_id = u.user_id " +
                     "WHERE u.privacy_mode = 'SOCIAL' AND u.account_status = 'ACTIVE' " +
                     "ORDER BY sa.created_at DESC LIMIT ?";
        List<SocialActivity> list = new ArrayList<>();
        try (Connection conn = DatabaseConnectionPool.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, limit > 0 ? limit : 20);
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    list.add(mapActivity(rs));
                }
            }
        } catch (SQLException e) {
            logger.error("Error getting social activity feed: {}", e.getMessage(), e);
            throw new DatabaseException("Database error getting social activity feed.", e);
        }
        return list;
    }

    @Override
    public Integer logActivity(SocialActivity activity) {
        String sql = "INSERT INTO social_activity (user_id, activity_type, title, description) VALUES (?, ?, ?, ?)";
        try (Connection conn = DatabaseConnectionPool.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {
            ps.setInt(1, activity.getUserId());
            ps.setString(2, activity.getActivityType().name());
            ps.setString(3, activity.getTitle());
            ps.setString(4, activity.getDescription());
            ps.executeUpdate();
            try (ResultSet keys = ps.getGeneratedKeys()) {
                if (keys.next()) {
                    activity.setActivityId(keys.getInt(1));
                    return activity.getActivityId();
                }
            }
        } catch (SQLException e) {
            logger.error("Error logging social activity: {}", e.getMessage(), e);
            throw new DatabaseException("Database error logging social activity.", e);
        }
        return null;
    }

    @Override
    public List<SocialActivity> getUserActivities(Integer userId, int limit) {
        String sql = "SELECT sa.activity_id, sa.user_id, sa.activity_type, sa.title, sa.description, " +
                     "sa.created_at, u.display_name " +
                     "FROM social_activity sa " +
                     "JOIN users u ON sa.user_id = u.user_id " +
                     "WHERE sa.user_id = ? " +
                     "ORDER BY sa.created_at DESC LIMIT ?";
        List<SocialActivity> list = new ArrayList<>();
        try (Connection conn = DatabaseConnectionPool.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, userId);
            ps.setInt(2, limit > 0 ? limit : 10);
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    list.add(mapActivity(rs));
                }
            }
        } catch (SQLException e) {
            logger.error("Error getting user activities: {}", e.getMessage(), e);
            throw new DatabaseException("Database error getting user activities.", e);
        }
        return list;
    }

    private SocialActivity mapActivity(ResultSet rs) throws SQLException {
        SocialActivity a = new SocialActivity();
        a.setActivityId(rs.getInt("activity_id"));
        a.setUserId(rs.getInt("user_id"));
        String typeStr = rs.getString("activity_type");
        if (typeStr != null) {
            try {
                a.setActivityType(SocialActivity.ActivityType.valueOf(typeStr));
            } catch (IllegalArgumentException e) {
                a.setActivityType(SocialActivity.ActivityType.STREAK_MILESTONE);
            }
        }
        a.setTitle(rs.getString("title"));
        a.setDescription(rs.getString("description"));
        a.setCreatedAt(rs.getTimestamp("created_at"));
        a.setUserDisplayName(rs.getString("display_name"));
        return a;
    }
}
