package com.fitaura.dao.impl;

import com.fitaura.dao.ActivityLogDAO;
import com.fitaura.exception.DatabaseException;
import com.fitaura.model.ActivityLog;
import com.fitaura.util.DatabaseConnectionPool;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

/**
 * JDBC implementation of ActivityLogDAO.
 */
public class ActivityLogDAOImpl implements ActivityLogDAO {

    private static final Logger logger = LoggerFactory.getLogger(ActivityLogDAOImpl.class);

    private static final String SQL_INSERT =
            "INSERT INTO activity_logs (user_id, action_type, entity_type, entity_id, description, ip_address) " +
            "VALUES (?, ?, ?, ?, ?, ?)";

    private static final String SQL_FIND_BY_USER =
            "SELECT log_id, user_id, action_type, entity_type, entity_id, description, ip_address, created_at " +
            "FROM activity_logs WHERE user_id = ? ORDER BY created_at DESC LIMIT ?";

    private static final String SQL_FIND_RECENT =
            "SELECT log_id, user_id, action_type, entity_type, entity_id, description, ip_address, created_at " +
            "FROM activity_logs ORDER BY created_at DESC LIMIT ?";

    @Override
    public Long create(ActivityLog log) {
        try (Connection conn = DatabaseConnectionPool.getConnection();
             PreparedStatement stmt = conn.prepareStatement(SQL_INSERT, Statement.RETURN_GENERATED_KEYS)) {
            if (log.getUserId() != null) stmt.setInt(1, log.getUserId()); else stmt.setNull(1, Types.INTEGER);
            stmt.setString(2, log.getActionType());
            stmt.setString(3, log.getEntityType());
            if (log.getEntityId() != null) stmt.setInt(4, log.getEntityId()); else stmt.setNull(4, Types.INTEGER);
            stmt.setString(5, log.getDescription());
            stmt.setString(6, log.getIpAddress());

            int affected = stmt.executeUpdate();
            if (affected == 0) {
                throw new DatabaseException("Failed to insert activity log.");
            }
            try (ResultSet keys = stmt.getGeneratedKeys()) {
                if (keys.next()) {
                    long id = keys.getLong(1);
                    log.setLogId(id);
                    return id;
                }
            }
            throw new DatabaseException("Failed to obtain log ID.");
        } catch (SQLException e) {
            logger.error("Error creating activity log: {}", e.getMessage());
            throw new DatabaseException("Failed to persist activity log: " + e.getMessage(), e);
        }
    }

    @Override
    public List<ActivityLog> findByUserId(Integer userId, int limit) {
        List<ActivityLog> list = new ArrayList<>();
        try (Connection conn = DatabaseConnectionPool.getConnection();
             PreparedStatement stmt = conn.prepareStatement(SQL_FIND_BY_USER)) {
            stmt.setInt(1, userId);
            stmt.setInt(2, limit);
            try (ResultSet rs = stmt.executeQuery()) {
                while (rs.next()) {
                    list.add(mapResultSet(rs));
                }
            }
            return list;
        } catch (SQLException e) {
            logger.error("Error querying activity logs for user {}: {}", userId, e.getMessage());
            throw new DatabaseException("Failed to query activity logs: " + e.getMessage(), e);
        }
    }

    @Override
    public List<ActivityLog> findRecent(int limit) {
        List<ActivityLog> list = new ArrayList<>();
        try (Connection conn = DatabaseConnectionPool.getConnection();
             PreparedStatement stmt = conn.prepareStatement(SQL_FIND_RECENT)) {
            stmt.setInt(1, limit);
            try (ResultSet rs = stmt.executeQuery()) {
                while (rs.next()) {
                    list.add(mapResultSet(rs));
                }
            }
            return list;
        } catch (SQLException e) {
            logger.error("Error querying recent activity logs: {}", e.getMessage());
            throw new DatabaseException("Failed to query recent activity logs: " + e.getMessage(), e);
        }
    }

    @Override
    public List<ActivityLog> findWithFilters(Integer userId, String actionType, String entityType, int limit, int offset) {
        StringBuilder sql = new StringBuilder(
                "SELECT log_id, user_id, action_type, entity_type, entity_id, description, ip_address, created_at " +
                "FROM activity_logs WHERE 1=1 ");
        List<Object> params = new ArrayList<>();

        if (userId != null) {
            sql.append("AND user_id = ? ");
            params.add(userId);
        }
        if (actionType != null && !actionType.trim().isEmpty()) {
            sql.append("AND action_type = ? ");
            params.add(actionType.trim());
        }
        if (entityType != null && !entityType.trim().isEmpty()) {
            sql.append("AND entity_type = ? ");
            params.add(entityType.trim());
        }

        sql.append("ORDER BY created_at DESC LIMIT ? OFFSET ?");
        params.add(Math.max(1, limit));
        params.add(Math.max(0, offset));

        List<ActivityLog> list = new ArrayList<>();
        try (Connection conn = DatabaseConnectionPool.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql.toString())) {
            for (int i = 0; i < params.size(); i++) {
                stmt.setObject(i + 1, params.get(i));
            }
            try (ResultSet rs = stmt.executeQuery()) {
                while (rs.next()) {
                    list.add(mapResultSet(rs));
                }
            }
            return list;
        } catch (SQLException e) {
            logger.error("Error querying filtered activity logs: {}", e.getMessage());
            throw new DatabaseException("Failed to query activity logs: " + e.getMessage(), e);
        }
    }

    @Override
    public int countWithFilters(Integer userId, String actionType, String entityType) {
        StringBuilder sql = new StringBuilder("SELECT COUNT(*) FROM activity_logs WHERE 1=1 ");
        List<Object> params = new ArrayList<>();

        if (userId != null) {
            sql.append("AND user_id = ? ");
            params.add(userId);
        }
        if (actionType != null && !actionType.trim().isEmpty()) {
            sql.append("AND action_type = ? ");
            params.add(actionType.trim());
        }
        if (entityType != null && !entityType.trim().isEmpty()) {
            sql.append("AND entity_type = ? ");
            params.add(entityType.trim());
        }

        try (Connection conn = DatabaseConnectionPool.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql.toString())) {
            for (int i = 0; i < params.size(); i++) {
                stmt.setObject(i + 1, params.get(i));
            }
            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) return rs.getInt(1);
            }
            return 0;
        } catch (SQLException e) {
            logger.error("Error counting filtered activity logs: {}", e.getMessage());
            throw new DatabaseException("Failed to count activity logs: " + e.getMessage(), e);
        }
    }

    private ActivityLog mapResultSet(ResultSet rs) throws SQLException {
        ActivityLog l = new ActivityLog();
        l.setLogId(rs.getLong("log_id"));
        int uid = rs.getInt("user_id");
        l.setUserId(rs.wasNull() ? null : uid);
        l.setActionType(rs.getString("action_type"));
        l.setEntityType(rs.getString("entity_type"));
        int eid = rs.getInt("entity_id");
        l.setEntityId(rs.wasNull() ? null : eid);
        l.setDescription(rs.getString("description"));
        l.setIpAddress(rs.getString("ip_address"));
        l.setCreatedAt(rs.getTimestamp("created_at"));
        return l;
    }
}
