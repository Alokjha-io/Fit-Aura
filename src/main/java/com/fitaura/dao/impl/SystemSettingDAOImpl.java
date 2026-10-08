package com.fitaura.dao.impl;

import com.fitaura.dao.SystemSettingDAO;
import com.fitaura.exception.DatabaseException;
import com.fitaura.model.SystemSetting;
import com.fitaura.util.DatabaseConnectionPool;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

/**
 * JDBC implementation of SystemSettingDAO.
 */
public class SystemSettingDAOImpl implements SystemSettingDAO {

    private static final Logger logger = LoggerFactory.getLogger(SystemSettingDAOImpl.class);

    private static final String SQL_FIND_BY_KEY =
            "SELECT setting_id, setting_key, setting_value, description, updated_by, updated_at " +
            "FROM system_settings WHERE setting_key = ?";

    private static final String SQL_UPSERT =
            "INSERT INTO system_settings (setting_key, setting_value, description, updated_by) " +
            "VALUES (?, ?, ?, ?) " +
            "ON DUPLICATE KEY UPDATE setting_value = VALUES(setting_value), description = VALUES(description), updated_by = VALUES(updated_by)";

    private static final String SQL_FIND_ALL =
            "SELECT setting_id, setting_key, setting_value, description, updated_by, updated_at " +
            "FROM system_settings ORDER BY setting_key ASC";

    @Override
    public Optional<SystemSetting> findByKey(String key) {
        try (Connection conn = DatabaseConnectionPool.getConnection();
             PreparedStatement stmt = conn.prepareStatement(SQL_FIND_BY_KEY)) {
            stmt.setString(1, key);
            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) {
                    return Optional.of(mapResultSet(rs));
                }
            }
            return Optional.empty();
        } catch (SQLException e) {
            logger.error("Error querying system setting by key {}: {}", key, e.getMessage());
            throw new DatabaseException("Failed to query system setting: " + e.getMessage(), e);
        }
    }

    @Override
    public boolean updateOrInsert(String key, String value, String description, Integer updatedBy) {
        try (Connection conn = DatabaseConnectionPool.getConnection();
             PreparedStatement stmt = conn.prepareStatement(SQL_UPSERT)) {
            stmt.setString(1, key);
            stmt.setString(2, value);
            stmt.setString(3, description);
            if (updatedBy != null) stmt.setInt(4, updatedBy); else stmt.setNull(4, Types.INTEGER);

            return stmt.executeUpdate() > 0;
        } catch (SQLException e) {
            logger.error("Error saving system setting {}: {}", key, e.getMessage());
            throw new DatabaseException("Failed to save system setting: " + e.getMessage(), e);
        }
    }

    @Override
    public List<SystemSetting> findAll() {
        List<SystemSetting> list = new ArrayList<>();
        try (Connection conn = DatabaseConnectionPool.getConnection();
             PreparedStatement stmt = conn.prepareStatement(SQL_FIND_ALL);
             ResultSet rs = stmt.executeQuery()) {
            while (rs.next()) {
                list.add(mapResultSet(rs));
            }
            return list;
        } catch (SQLException e) {
            logger.error("Error querying all system settings: {}", e.getMessage());
            throw new DatabaseException("Failed to query system settings: " + e.getMessage(), e);
        }
    }

    private SystemSetting mapResultSet(ResultSet rs) throws SQLException {
        SystemSetting s = new SystemSetting();
        s.setSettingId(rs.getInt("setting_id"));
        s.setSettingKey(rs.getString("setting_key"));
        s.setSettingValue(rs.getString("setting_value"));
        s.setDescription(rs.getString("description"));
        int updatedBy = rs.getInt("updated_by");
        s.setUpdatedBy(rs.wasNull() ? null : updatedBy);
        s.setUpdatedAt(rs.getTimestamp("updated_at"));
        return s;
    }
}
