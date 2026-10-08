package com.fitaura.dao.impl;

import com.fitaura.dao.UserDAO;
import com.fitaura.exception.DatabaseException;
import com.fitaura.model.User;
import com.fitaura.util.DatabaseConnectionPool;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

/**
 * JDBC implementation of UserDAO.
 */
public class UserDAOImpl implements UserDAO {

    private static final Logger logger = LoggerFactory.getLogger(UserDAOImpl.class);

    private static final String SQL_INSERT =
            "INSERT INTO users (full_name, email, password_hash, role, account_status, privacy_mode, display_name) " +
            "VALUES (?, ?, ?, ?, ?, ?, ?)";

    private static final String SQL_FIND_BY_ID =
            "SELECT user_id, full_name, email, password_hash, role, account_status, privacy_mode, display_name, created_at, updated_at, last_login_at " +
            "FROM users WHERE user_id = ?";

    private static final String SQL_FIND_BY_EMAIL =
            "SELECT user_id, full_name, email, password_hash, role, account_status, privacy_mode, display_name, created_at, updated_at, last_login_at " +
            "FROM users WHERE email = ?";

    private static final String SQL_UPDATE =
            "UPDATE users SET full_name = ?, password_hash = ?, role = ?, account_status = ?, privacy_mode = ?, display_name = ? " +
            "WHERE user_id = ?";

    private static final String SQL_UPDATE_LAST_LOGIN =
            "UPDATE users SET last_login_at = CURRENT_TIMESTAMP WHERE user_id = ?";

    private static final String SQL_UPDATE_ACCOUNT_STATUS =
            "UPDATE users SET account_status = ? WHERE user_id = ?";

    private static final String SQL_UPDATE_PROFILE =
            "UPDATE users SET full_name = ?, display_name = ? WHERE user_id = ?";

    private static final String SQL_UPDATE_PRIVACY_MODE =
            "UPDATE users SET privacy_mode = ? WHERE user_id = ?";

    private static final String SQL_CHECK_EMAIL_EXISTS_OTHER_USER =
            "SELECT 1 FROM users WHERE email = ? AND user_id != ?";

    private static final String SQL_DELETE =
            "DELETE FROM users WHERE user_id = ?";

    private static final String SQL_COUNT_ALL =
            "SELECT COUNT(*) FROM users";

    private static final String SQL_FIND_ALL =
            "SELECT user_id, full_name, email, password_hash, role, account_status, privacy_mode, display_name, created_at, updated_at, last_login_at " +
            "FROM users ORDER BY user_id DESC LIMIT ? OFFSET ?";

    private static final String SQL_FIND_RECENT =
            "SELECT user_id, full_name, email, password_hash, role, account_status, privacy_mode, display_name, created_at, updated_at, last_login_at " +
            "FROM users ORDER BY created_at DESC LIMIT ?";

    @Override
    public Integer create(User user) {
        try (Connection conn = DatabaseConnectionPool.getConnection()) {
            return create(user, conn);
        } catch (SQLException e) {
            logger.error("Error creating user: {}", e.getMessage());
            throw new DatabaseException("Failed to insert user record: " + e.getMessage(), e);
        }
    }

    @Override
    public Integer create(User user, Connection conn) {
        try (PreparedStatement stmt = conn.prepareStatement(SQL_INSERT, Statement.RETURN_GENERATED_KEYS)) {
            stmt.setString(1, user.getFullName());
            stmt.setString(2, user.getEmail());
            stmt.setString(3, user.getPasswordHash());
            stmt.setString(4, user.getRole() != null ? user.getRole().name() : User.Role.USER.name());
            stmt.setString(5, user.getAccountStatus() != null ? user.getAccountStatus().name() : User.AccountStatus.ACTIVE.name());
            stmt.setString(6, user.getPrivacyMode() != null ? user.getPrivacyMode().name() : User.PrivacyMode.PERSONAL.name());
            stmt.setString(7, user.getDisplayName());

            int affectedRows = stmt.executeUpdate();
            if (affectedRows == 0) {
                throw new DatabaseException("Creating user failed, no rows affected.");
            }

            try (ResultSet generatedKeys = stmt.getGeneratedKeys()) {
                if (generatedKeys.next()) {
                    int generatedId = generatedKeys.getInt(1);
                    user.setUserId(generatedId);
                    return generatedId;
                } else {
                    throw new DatabaseException("Creating user failed, no ID obtained.");
                }
            }
        } catch (SQLException e) {
            logger.error("Error executing user insert: {}", e.getMessage());
            throw new DatabaseException("Failed to insert user into database: " + e.getMessage(), e);
        }
    }

    @Override
    public Optional<User> findById(Integer userId) {
        try (Connection conn = DatabaseConnectionPool.getConnection();
             PreparedStatement stmt = conn.prepareStatement(SQL_FIND_BY_ID)) {
            stmt.setInt(1, userId);
            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) {
                    return Optional.of(mapResultSetToUser(rs));
                }
            }
            return Optional.empty();
        } catch (SQLException e) {
            logger.error("Error finding user by ID {}: {}", userId, e.getMessage());
            throw new DatabaseException("Failed to retrieve user: " + e.getMessage(), e);
        }
    }

    @Override
    public Optional<User> findByEmail(String email) {
        try (Connection conn = DatabaseConnectionPool.getConnection();
             PreparedStatement stmt = conn.prepareStatement(SQL_FIND_BY_EMAIL)) {
            stmt.setString(1, email);
            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) {
                    return Optional.of(mapResultSetToUser(rs));
                }
            }
            return Optional.empty();
        } catch (SQLException e) {
            logger.error("Error finding user by email {}: {}", email, e.getMessage());
            throw new DatabaseException("Failed to retrieve user by email: " + e.getMessage(), e);
        }
    }

    @Override
    public boolean update(User user) {
        try (Connection conn = DatabaseConnectionPool.getConnection();
             PreparedStatement stmt = conn.prepareStatement(SQL_UPDATE)) {
            stmt.setString(1, user.getFullName());
            stmt.setString(2, user.getPasswordHash());
            stmt.setString(3, user.getRole().name());
            stmt.setString(4, user.getAccountStatus().name());
            stmt.setString(5, user.getPrivacyMode().name());
            stmt.setString(6, user.getDisplayName());
            stmt.setInt(7, user.getUserId());
            return stmt.executeUpdate() > 0;
        } catch (SQLException e) {
            logger.error("Error updating user {}: {}", user.getUserId(), e.getMessage());
            throw new DatabaseException("Failed to update user: " + e.getMessage(), e);
        }
    }

    @Override
    public boolean updateLastLogin(Integer userId) {
        try (Connection conn = DatabaseConnectionPool.getConnection();
             PreparedStatement stmt = conn.prepareStatement(SQL_UPDATE_LAST_LOGIN)) {
            stmt.setInt(1, userId);
            return stmt.executeUpdate() > 0;
        } catch (SQLException e) {
            logger.error("Error updating last login for user {}: {}", userId, e.getMessage());
            throw new DatabaseException("Failed to update last login timestamp: " + e.getMessage(), e);
        }
    }

    @Override
    public boolean updateAccountStatus(Integer userId, User.AccountStatus status) {
        try (Connection conn = DatabaseConnectionPool.getConnection();
             PreparedStatement stmt = conn.prepareStatement(SQL_UPDATE_ACCOUNT_STATUS)) {
            stmt.setString(1, status.name());
            stmt.setInt(2, userId);
            return stmt.executeUpdate() > 0;
        } catch (SQLException e) {
            logger.error("Error updating account status for user {}: {}", userId, e.getMessage());
            throw new DatabaseException("Failed to update account status: " + e.getMessage(), e);
        }
    }

    @Override
    public boolean updateProfile(Integer userId, String fullName, String displayName) {
        try (Connection conn = DatabaseConnectionPool.getConnection();
             PreparedStatement stmt = conn.prepareStatement(SQL_UPDATE_PROFILE)) {
            stmt.setString(1, fullName);
            stmt.setString(2, displayName);
            stmt.setInt(3, userId);
            return stmt.executeUpdate() > 0;
        } catch (SQLException e) {
            logger.error("Error updating profile for user {}: {}", userId, e.getMessage());
            throw new DatabaseException("Failed to update profile: " + e.getMessage(), e);
        }
    }

    @Override
    public boolean updatePrivacyMode(Integer userId, User.PrivacyMode privacyMode) {
        try (Connection conn = DatabaseConnectionPool.getConnection();
             PreparedStatement stmt = conn.prepareStatement(SQL_UPDATE_PRIVACY_MODE)) {
            stmt.setString(1, privacyMode.name());
            stmt.setInt(2, userId);
            return stmt.executeUpdate() > 0;
        } catch (SQLException e) {
            logger.error("Error updating privacy mode for user {}: {}", userId, e.getMessage());
            throw new DatabaseException("Failed to update user privacy mode: " + e.getMessage(), e);
        }
    }

    @Override
    public boolean emailExistsForAnotherUser(String email, Integer currentUserId) {
        try (Connection conn = DatabaseConnectionPool.getConnection();
             PreparedStatement stmt = conn.prepareStatement(SQL_CHECK_EMAIL_EXISTS_OTHER_USER)) {
            stmt.setString(1, email);
            stmt.setInt(2, currentUserId);
            try (ResultSet rs = stmt.executeQuery()) {
                return rs.next();
            }
        } catch (SQLException e) {
            logger.error("Error checking email uniqueness for {}: {}", email, e.getMessage());
            throw new DatabaseException("Failed to check email uniqueness: " + e.getMessage(), e);
        }
    }

    @Override
    public boolean delete(Integer userId) {
        try (Connection conn = DatabaseConnectionPool.getConnection();
             PreparedStatement stmt = conn.prepareStatement(SQL_DELETE)) {
            stmt.setInt(1, userId);
            return stmt.executeUpdate() > 0;
        } catch (SQLException e) {
            logger.error("Error deleting user {}: {}", userId, e.getMessage());
            throw new DatabaseException("Failed to delete user: " + e.getMessage(), e);
        }
    }

    @Override
    public int countAll() {
        try (Connection conn = DatabaseConnectionPool.getConnection();
             PreparedStatement stmt = conn.prepareStatement(SQL_COUNT_ALL);
             ResultSet rs = stmt.executeQuery()) {
            if (rs.next()) {
                return rs.getInt(1);
            }
            return 0;
        } catch (SQLException e) {
            logger.error("Error counting users: {}", e.getMessage());
            throw new DatabaseException("Failed to count users: " + e.getMessage(), e);
        }
    }

    @Override
    public List<User> findAll(int limit, int offset) {
        List<User> users = new ArrayList<>();
        try (Connection conn = DatabaseConnectionPool.getConnection();
             PreparedStatement stmt = conn.prepareStatement(SQL_FIND_ALL)) {
            stmt.setInt(1, limit);
            stmt.setInt(2, offset);
            try (ResultSet rs = stmt.executeQuery()) {
                while (rs.next()) {
                    users.add(mapResultSetToUser(rs));
                }
            }
            return users;
        } catch (SQLException e) {
            logger.error("Error fetching users: {}", e.getMessage());
            throw new DatabaseException("Failed to fetch users: " + e.getMessage(), e);
        }
    }

    @Override
    public List<User> findRecentUsers(int limit) {
        List<User> users = new ArrayList<>();
        try (Connection conn = DatabaseConnectionPool.getConnection();
             PreparedStatement stmt = conn.prepareStatement(SQL_FIND_RECENT)) {
            stmt.setInt(1, Math.max(1, limit));
            try (ResultSet rs = stmt.executeQuery()) {
                while (rs.next()) {
                    users.add(mapResultSetToUser(rs));
                }
            }
            return users;
        } catch (SQLException e) {
            logger.error("Error fetching recent users: {}", e.getMessage());
            throw new DatabaseException("Failed to fetch recent users: " + e.getMessage(), e);
        }
    }

    @Override
    public List<User> findWithFilters(String searchQuery, User.Role role, User.AccountStatus status,
                                      User.PrivacyMode privacyMode, int limit, int offset) {
        StringBuilder sql = new StringBuilder(
                "SELECT user_id, full_name, email, password_hash, role, account_status, privacy_mode, display_name, created_at, updated_at, last_login_at " +
                "FROM users WHERE 1=1 ");
        List<Object> params = new ArrayList<>();

        buildFilterClauses(sql, params, searchQuery, role, status, privacyMode);

        sql.append(" ORDER BY user_id DESC LIMIT ? OFFSET ?");
        params.add(Math.max(1, limit));
        params.add(Math.max(0, offset));

        List<User> users = new ArrayList<>();
        try (Connection conn = DatabaseConnectionPool.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql.toString())) {
            for (int i = 0; i < params.size(); i++) {
                stmt.setObject(i + 1, params.get(i));
            }
            try (ResultSet rs = stmt.executeQuery()) {
                while (rs.next()) {
                    users.add(mapResultSetToUser(rs));
                }
            }
            return users;
        } catch (SQLException e) {
            logger.error("Error searching users with filters: {}", e.getMessage());
            throw new DatabaseException("Failed to search users: " + e.getMessage(), e);
        }
    }

    @Override
    public int countWithFilters(String searchQuery, User.Role role, User.AccountStatus status,
                                User.PrivacyMode privacyMode) {
        StringBuilder sql = new StringBuilder("SELECT COUNT(*) FROM users WHERE 1=1 ");
        List<Object> params = new ArrayList<>();

        buildFilterClauses(sql, params, searchQuery, role, status, privacyMode);

        try (Connection conn = DatabaseConnectionPool.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql.toString())) {
            for (int i = 0; i < params.size(); i++) {
                stmt.setObject(i + 1, params.get(i));
            }
            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) {
                    return rs.getInt(1);
                }
            }
            return 0;
        } catch (SQLException e) {
            logger.error("Error counting users with filters: {}", e.getMessage());
            throw new DatabaseException("Failed to count filtered users: " + e.getMessage(), e);
        }
    }

    @Override
    public int countByStatus(User.AccountStatus status) {
        String sql = "SELECT COUNT(*) FROM users WHERE account_status = ?";
        try (Connection conn = DatabaseConnectionPool.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setString(1, status.name());
            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) return rs.getInt(1);
            }
            return 0;
        } catch (SQLException e) {
            logger.error("Error counting users by status {}: {}", status, e.getMessage());
            throw new DatabaseException("Failed to count users by status: " + e.getMessage(), e);
        }
    }

    @Override
    public int countByRole(User.Role role) {
        String sql = "SELECT COUNT(*) FROM users WHERE role = ?";
        try (Connection conn = DatabaseConnectionPool.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setString(1, role.name());
            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) return rs.getInt(1);
            }
            return 0;
        } catch (SQLException e) {
            logger.error("Error counting users by role {}: {}", role, e.getMessage());
            throw new DatabaseException("Failed to count users by role: " + e.getMessage(), e);
        }
    }

    @Override
    public int countByPrivacyMode(User.PrivacyMode privacyMode) {
        String sql = "SELECT COUNT(*) FROM users WHERE privacy_mode = ?";
        try (Connection conn = DatabaseConnectionPool.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setString(1, privacyMode.name());
            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) return rs.getInt(1);
            }
            return 0;
        } catch (SQLException e) {
            logger.error("Error counting users by privacy mode {}: {}", privacyMode, e.getMessage());
            throw new DatabaseException("Failed to count users by privacy mode: " + e.getMessage(), e);
        }
    }

    private void buildFilterClauses(StringBuilder sql, List<Object> params, String searchQuery,
                                    User.Role role, User.AccountStatus status, User.PrivacyMode privacyMode) {
        if (searchQuery != null && !searchQuery.trim().isEmpty()) {
            String term = "%" + searchQuery.trim() + "%";
            sql.append("AND (full_name LIKE ? OR email LIKE ? OR display_name LIKE ?) ");
            params.add(term);
            params.add(term);
            params.add(term);
        }
        if (role != null) {
            sql.append("AND role = ? ");
            params.add(role.name());
        }
        if (status != null) {
            sql.append("AND account_status = ? ");
            params.add(status.name());
        }
        if (privacyMode != null) {
            sql.append("AND privacy_mode = ? ");
            params.add(privacyMode.name());
        }
    }

    private User mapResultSetToUser(ResultSet rs) throws SQLException {
        User user = new User();
        user.setUserId(rs.getInt("user_id"));
        user.setFullName(rs.getString("full_name"));
        user.setEmail(rs.getString("email"));
        user.setPasswordHash(rs.getString("password_hash"));

        String roleStr = rs.getString("role");
        if (roleStr != null) {
            user.setRole(User.Role.valueOf(roleStr));
        }

        String statusStr = rs.getString("account_status");
        if (statusStr != null) {
            user.setAccountStatus(User.AccountStatus.valueOf(statusStr));
        }

        String privacyStr = rs.getString("privacy_mode");
        if (privacyStr != null) {
            user.setPrivacyMode(User.PrivacyMode.valueOf(privacyStr));
        }

        user.setDisplayName(rs.getString("display_name"));
        user.setCreatedAt(rs.getTimestamp("created_at"));
        user.setUpdatedAt(rs.getTimestamp("updated_at"));
        user.setLastLoginAt(rs.getTimestamp("last_login_at"));
        return user;
    }
}
