package com.fitaura.dao.impl;

import com.fitaura.dao.SocialConnectionDAO;
import com.fitaura.exception.DatabaseException;
import com.fitaura.model.SocialConnection;
import com.fitaura.util.DatabaseConnectionPool;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

public class SocialConnectionDAOImpl implements SocialConnectionDAO {

    private static final Logger logger = LoggerFactory.getLogger(SocialConnectionDAOImpl.class);

    @Override
    public Optional<SocialConnection> findByUsers(Integer user1, Integer user2) {
        String sql = "SELECT connection_id, requester_id, receiver_id, status, created_at, updated_at " +
                     "FROM social_connections WHERE (requester_id = ? AND receiver_id = ?) " +
                     "OR (requester_id = ? AND receiver_id = ?)";
        try (Connection conn = DatabaseConnectionPool.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, user1);
            ps.setInt(2, user2);
            ps.setInt(3, user2);
            ps.setInt(4, user1);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    return Optional.of(mapConnection(rs));
                }
            }
        } catch (SQLException e) {
            logger.error("Error finding connection between {} and {}: {}", user1, user2, e.getMessage(), e);
            throw new DatabaseException("Database error finding connection.", e);
        }
        return Optional.empty();
    }

    @Override
    public List<SocialConnection> findConnectionsForUser(Integer userId) {
        String sql = "SELECT sc.connection_id, sc.requester_id, sc.receiver_id, sc.status, sc.created_at, sc.updated_at, " +
                     "CASE WHEN sc.requester_id = ? THEN u2.display_name ELSE u1.display_name END AS other_name, " +
                     "CASE WHEN sc.requester_id = ? THEN u2.user_id ELSE u1.user_id END AS other_id " +
                     "FROM social_connections sc " +
                     "JOIN users u1 ON sc.requester_id = u1.user_id " +
                     "JOIN users u2 ON sc.receiver_id = u2.user_id " +
                     "WHERE (sc.requester_id = ? OR sc.receiver_id = ?) AND sc.status = 'ACCEPTED' " +
                     "AND u1.privacy_mode = 'SOCIAL' AND u2.privacy_mode = 'SOCIAL' " +
                     "ORDER BY sc.updated_at DESC";
        List<SocialConnection> list = new ArrayList<>();
        try (Connection conn = DatabaseConnectionPool.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, userId);
            ps.setInt(2, userId);
            ps.setInt(3, userId);
            ps.setInt(4, userId);
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    SocialConnection sc = mapConnection(rs);
                    sc.setOtherUserDisplayName(rs.getString("other_name"));
                    sc.setOtherUserId(rs.getInt("other_id"));
                    list.add(sc);
                }
            }
        } catch (SQLException e) {
            logger.error("Error finding connections for user #{}: {}", userId, e.getMessage(), e);
            throw new DatabaseException("Database error finding connections.", e);
        }
        return list;
    }

    @Override
    public List<SocialConnection> findPendingRequestsForUser(Integer userId) {
        String sql = "SELECT sc.connection_id, sc.requester_id, sc.receiver_id, sc.status, sc.created_at, sc.updated_at, " +
                     "u.display_name AS other_name, u.user_id AS other_id " +
                     "FROM social_connections sc " +
                     "JOIN users u ON sc.requester_id = u.user_id " +
                     "WHERE sc.receiver_id = ? AND sc.status = 'PENDING' AND u.privacy_mode = 'SOCIAL' " +
                     "ORDER BY sc.created_at DESC";
        List<SocialConnection> list = new ArrayList<>();
        try (Connection conn = DatabaseConnectionPool.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, userId);
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    SocialConnection sc = mapConnection(rs);
                    sc.setOtherUserDisplayName(rs.getString("other_name"));
                    sc.setOtherUserId(rs.getInt("other_id"));
                    list.add(sc);
                }
            }
        } catch (SQLException e) {
            logger.error("Error finding pending requests for user #{}: {}", userId, e.getMessage(), e);
            throw new DatabaseException("Database error finding pending requests.", e);
        }
        return list;
    }

    @Override
    public Integer create(SocialConnection connection) {
        String sql = "INSERT INTO social_connections (requester_id, receiver_id, status) VALUES (?, ?, ?)";
        try (Connection conn = DatabaseConnectionPool.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {
            ps.setInt(1, connection.getRequesterId());
            ps.setInt(2, connection.getReceiverId());
            ps.setString(3, connection.getStatus() != null ? connection.getStatus().name() : SocialConnection.Status.PENDING.name());
            ps.executeUpdate();
            try (ResultSet keys = ps.getGeneratedKeys()) {
                if (keys.next()) {
                    connection.setConnectionId(keys.getInt(1));
                    return connection.getConnectionId();
                }
            }
        } catch (SQLException e) {
            logger.error("Error creating connection: {}", e.getMessage(), e);
            throw new DatabaseException("Database error creating connection.", e);
        }
        return null;
    }

    @Override
    public boolean updateStatus(Integer connectionId, SocialConnection.Status status) {
        String sql = "UPDATE social_connections SET status = ? WHERE connection_id = ?";
        try (Connection conn = DatabaseConnectionPool.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, status.name());
            ps.setInt(2, connectionId);
            return ps.executeUpdate() > 0;
        } catch (SQLException e) {
            logger.error("Error updating connection status #{}: {}", connectionId, e.getMessage(), e);
            throw new DatabaseException("Database error updating connection status.", e);
        }
    }

    @Override
    public boolean delete(Integer connectionId) {
        String sql = "DELETE FROM social_connections WHERE connection_id = ?";
        try (Connection conn = DatabaseConnectionPool.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, connectionId);
            return ps.executeUpdate() > 0;
        } catch (SQLException e) {
            logger.error("Error deleting connection #{}: {}", connectionId, e.getMessage(), e);
            throw new DatabaseException("Database error deleting connection.", e);
        }
    }

    @Override
    public int countConnections(Integer userId) {
        String sql = "SELECT COUNT(*) FROM social_connections sc " +
                     "JOIN users u1 ON sc.requester_id = u1.user_id " +
                     "JOIN users u2 ON sc.receiver_id = u2.user_id " +
                     "WHERE (sc.requester_id = ? OR sc.receiver_id = ?) AND sc.status = 'ACCEPTED' " +
                     "AND u1.privacy_mode = 'SOCIAL' AND u2.privacy_mode = 'SOCIAL'";
        try (Connection conn = DatabaseConnectionPool.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, userId);
            ps.setInt(2, userId);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    return rs.getInt(1);
                }
            }
        } catch (SQLException e) {
            logger.error("Error counting connections for user #{}: {}", userId, e.getMessage(), e);
            throw new DatabaseException("Database error counting connections.", e);
        }
        return 0;
    }

    @Override
    public boolean areConnected(Integer user1, Integer user2) {
        String sql = "SELECT COUNT(*) FROM social_connections " +
                     "WHERE ((requester_id = ? AND receiver_id = ?) OR (requester_id = ? AND receiver_id = ?)) " +
                     "AND status = 'ACCEPTED'";
        try (Connection conn = DatabaseConnectionPool.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, user1);
            ps.setInt(2, user2);
            ps.setInt(3, user2);
            ps.setInt(4, user1);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    return rs.getInt(1) > 0;
                }
            }
        } catch (SQLException e) {
            logger.error("Error checking connection between {} and {}: {}", user1, user2, e.getMessage(), e);
            throw new DatabaseException("Database error checking connection.", e);
        }
        return false;
    }

    private SocialConnection mapConnection(ResultSet rs) throws SQLException {
        SocialConnection sc = new SocialConnection();
        sc.setConnectionId(rs.getInt("connection_id"));
        sc.setRequesterId(rs.getInt("requester_id"));
        sc.setReceiverId(rs.getInt("receiver_id"));
        String statusStr = rs.getString("status");
        if (statusStr != null) {
            try {
                sc.setStatus(SocialConnection.Status.valueOf(statusStr));
            } catch (IllegalArgumentException e) {
                sc.setStatus(SocialConnection.Status.PENDING);
            }
        }
        sc.setCreatedAt(rs.getTimestamp("created_at"));
        sc.setUpdatedAt(rs.getTimestamp("updated_at"));
        return sc;
    }
}
