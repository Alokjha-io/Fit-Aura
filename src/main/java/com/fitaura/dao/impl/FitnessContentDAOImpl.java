package com.fitaura.dao.impl;

import com.fitaura.dao.FitnessContentDAO;
import com.fitaura.dto.FitnessContentDetailDTO;
import com.fitaura.exception.DatabaseException;
import com.fitaura.model.FitnessContent;
import com.fitaura.model.User;
import com.fitaura.util.DatabaseConnectionPool;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

/**
 * JDBC implementation of FitnessContentDAO.
 */
public class FitnessContentDAOImpl implements FitnessContentDAO {

    private static final Logger logger = LoggerFactory.getLogger(FitnessContentDAOImpl.class);

    private static final String SQL_INSERT =
            "INSERT INTO fitness_content (created_by, title, category, content_text, approval_status, reviewed_by, reviewed_at) " +
            "VALUES (?, ?, ?, ?, ?, ?, ?)";

    private static final String SQL_FIND_BY_ID =
            "SELECT content_id, created_by, title, category, content_text, approval_status, reviewed_by, reviewed_at, created_at, updated_at " +
            "FROM fitness_content WHERE content_id = ?";

    private static final String SQL_FIND_BY_CREATED_BY =
            "SELECT content_id, created_by, title, category, content_text, approval_status, reviewed_by, reviewed_at, created_at, updated_at " +
            "FROM fitness_content WHERE created_by = ? ORDER BY created_at DESC";

    private static final String SQL_FIND_BY_STATUS =
            "SELECT content_id, created_by, title, category, content_text, approval_status, reviewed_by, reviewed_at, created_at, updated_at " +
            "FROM fitness_content WHERE approval_status = ? ORDER BY created_at DESC";

    private static final String SQL_FIND_BY_CATEGORY_STATUS =
            "SELECT content_id, created_by, title, category, content_text, approval_status, reviewed_by, reviewed_at, created_at, updated_at " +
            "FROM fitness_content WHERE category = ? AND approval_status = ? ORDER BY created_at DESC";

    private static final String SQL_UPDATE =
            "UPDATE fitness_content SET title = ?, category = ?, content_text = ?, approval_status = ?, reviewed_by = NULL, reviewed_at = NULL " +
            "WHERE content_id = ?";

    private static final String SQL_UPDATE_APPROVAL =
            "UPDATE fitness_content SET approval_status = ?, reviewed_by = ?, reviewed_at = CURRENT_TIMESTAMP " +
            "WHERE content_id = ?";

    private static final String SQL_DELETE =
            "DELETE FROM fitness_content WHERE content_id = ?";

    private static final String SQL_COUNT_BY_STATUS =
            "SELECT COUNT(*) FROM fitness_content WHERE approval_status = ?";

    @Override
    public Integer create(FitnessContent content) {
        try (Connection conn = DatabaseConnectionPool.getConnection();
             PreparedStatement stmt = conn.prepareStatement(SQL_INSERT, Statement.RETURN_GENERATED_KEYS)) {
            stmt.setInt(1, content.getCreatedBy());
            stmt.setString(2, content.getTitle());
            stmt.setString(3, content.getCategory().name());
            stmt.setString(4, content.getContentText());
            stmt.setString(5, content.getApprovalStatus() != null ? content.getApprovalStatus().name() : FitnessContent.ApprovalStatus.PENDING.name());
            if (content.getReviewedBy() != null) stmt.setInt(6, content.getReviewedBy()); else stmt.setNull(6, Types.INTEGER);
            stmt.setTimestamp(7, content.getReviewedAt());

            int affected = stmt.executeUpdate();
            if (affected == 0) {
                throw new DatabaseException("Failed to insert fitness content.");
            }
            try (ResultSet keys = stmt.getGeneratedKeys()) {
                if (keys.next()) {
                    int id = keys.getInt(1);
                    content.setContentId(id);
                    return id;
                }
            }
            throw new DatabaseException("Failed to obtain content ID.");
        } catch (SQLException e) {
            logger.error("Error creating fitness content: {}", e.getMessage());
            throw new DatabaseException("Failed to persist fitness content: " + e.getMessage(), e);
        }
    }

    @Override
    public Optional<FitnessContent> findById(Integer contentId) {
        try (Connection conn = DatabaseConnectionPool.getConnection();
             PreparedStatement stmt = conn.prepareStatement(SQL_FIND_BY_ID)) {
            stmt.setInt(1, contentId);
            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) {
                    return Optional.of(mapResultSet(rs));
                }
            }
            return Optional.empty();
        } catch (SQLException e) {
            logger.error("Error finding fitness content by id {}: {}", contentId, e.getMessage());
            throw new DatabaseException("Failed to query fitness content: " + e.getMessage(), e);
        }
    }

    @Override
    public List<FitnessContent> findByCreatedBy(Integer userId) {
        List<FitnessContent> list = new ArrayList<>();
        try (Connection conn = DatabaseConnectionPool.getConnection();
             PreparedStatement stmt = conn.prepareStatement(SQL_FIND_BY_CREATED_BY)) {
            stmt.setInt(1, userId);
            try (ResultSet rs = stmt.executeQuery()) {
                while (rs.next()) {
                    list.add(mapResultSet(rs));
                }
            }
            return list;
        } catch (SQLException e) {
            logger.error("Error querying content by createdBy {}: {}", userId, e.getMessage());
            throw new DatabaseException("Failed to query user content: " + e.getMessage(), e);
        }
    }

    @Override
    public List<FitnessContent> findByApprovalStatus(FitnessContent.ApprovalStatus status) {
        List<FitnessContent> list = new ArrayList<>();
        try (Connection conn = DatabaseConnectionPool.getConnection();
             PreparedStatement stmt = conn.prepareStatement(SQL_FIND_BY_STATUS)) {
            stmt.setString(1, status.name());
            try (ResultSet rs = stmt.executeQuery()) {
                while (rs.next()) {
                    list.add(mapResultSet(rs));
                }
            }
            return list;
        } catch (SQLException e) {
            logger.error("Error querying content by status {}: {}", status, e.getMessage());
            throw new DatabaseException("Failed to query content: " + e.getMessage(), e);
        }
    }

    @Override
    public List<FitnessContent> findByCategoryAndStatus(FitnessContent.Category category, FitnessContent.ApprovalStatus status) {
        List<FitnessContent> list = new ArrayList<>();
        try (Connection conn = DatabaseConnectionPool.getConnection();
             PreparedStatement stmt = conn.prepareStatement(SQL_FIND_BY_CATEGORY_STATUS)) {
            stmt.setString(1, category.name());
            stmt.setString(2, status.name());
            try (ResultSet rs = stmt.executeQuery()) {
                while (rs.next()) {
                    list.add(mapResultSet(rs));
                }
            }
            return list;
        } catch (SQLException e) {
            logger.error("Error querying content by category and status: {}", e.getMessage());
            throw new DatabaseException("Failed to query content: " + e.getMessage(), e);
        }
    }

    @Override
    public boolean update(FitnessContent content) {
        try (Connection conn = DatabaseConnectionPool.getConnection();
             PreparedStatement stmt = conn.prepareStatement(SQL_UPDATE)) {
            stmt.setString(1, content.getTitle());
            stmt.setString(2, content.getCategory().name());
            stmt.setString(3, content.getContentText());
            stmt.setString(4, content.getApprovalStatus() != null ? content.getApprovalStatus().name() : FitnessContent.ApprovalStatus.PENDING.name());
            stmt.setInt(5, content.getContentId());
            return stmt.executeUpdate() > 0;
        } catch (SQLException e) {
            logger.error("Error updating fitness content {}: {}", content.getContentId(), e.getMessage());
            throw new DatabaseException("Failed to update fitness content: " + e.getMessage(), e);
        }
    }

    @Override
    public boolean updateApproval(Integer contentId, FitnessContent.ApprovalStatus status, Integer reviewedBy) {
        try (Connection conn = DatabaseConnectionPool.getConnection();
             PreparedStatement stmt = conn.prepareStatement(SQL_UPDATE_APPROVAL)) {
            stmt.setString(1, status.name());
            if (reviewedBy != null) stmt.setInt(2, reviewedBy); else stmt.setNull(2, Types.INTEGER);
            stmt.setInt(3, contentId);
            return stmt.executeUpdate() > 0;
        } catch (SQLException e) {
            logger.error("Error updating content approval {}: {}", contentId, e.getMessage());
            throw new DatabaseException("Failed to update content approval: " + e.getMessage(), e);
        }
    }

    @Override
    public boolean delete(Integer contentId) {
        try (Connection conn = DatabaseConnectionPool.getConnection();
             PreparedStatement stmt = conn.prepareStatement(SQL_DELETE)) {
            stmt.setInt(1, contentId);
            return stmt.executeUpdate() > 0;
        } catch (SQLException e) {
            logger.error("Error deleting content {}: {}", contentId, e.getMessage());
            throw new DatabaseException("Failed to delete content: " + e.getMessage(), e);
        }
    }

    @Override
    public List<FitnessContentDetailDTO> findApprovedWithFilters(String searchQuery, FitnessContent.Category category,
                                                                 int limit, int offset, Integer currentUserId) {
        StringBuilder sql = new StringBuilder(
                "SELECT c.content_id, c.created_by, c.title, c.category, c.content_text, c.approval_status, " +
                "c.reviewed_by, c.reviewed_at, c.created_at, c.updated_at, u.display_name, u.privacy_mode " +
                "FROM fitness_content c " +
                "JOIN users u ON c.created_by = u.user_id " +
                "WHERE c.approval_status = 'APPROVED' ");

        List<Object> params = new ArrayList<>();
        if (category != null) {
            sql.append("AND c.category = ? ");
            params.add(category.name());
        }
        if (searchQuery != null && !searchQuery.trim().isEmpty()) {
            sql.append("AND (c.title LIKE ? OR c.content_text LIKE ?) ");
            String pattern = "%" + searchQuery.trim() + "%";
            params.add(pattern);
            params.add(pattern);
        }

        sql.append("ORDER BY c.created_at DESC LIMIT ? OFFSET ?");
        params.add(Math.max(1, limit));
        params.add(Math.max(0, offset));

        List<FitnessContentDetailDTO> list = new ArrayList<>();
        try (Connection conn = DatabaseConnectionPool.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql.toString())) {
            for (int i = 0; i < params.size(); i++) {
                stmt.setObject(i + 1, params.get(i));
            }
            try (ResultSet rs = stmt.executeQuery()) {
                while (rs.next()) {
                    FitnessContent c = mapResultSet(rs);
                    String displayName = rs.getString("display_name");
                    String privacy = rs.getString("privacy_mode");
                    boolean isOwner = (currentUserId != null && currentUserId.equals(c.getCreatedBy()));

                    String author = displayName;
                    if ("PERSONAL".equals(privacy) && !isOwner) {
                        author = "FitAura Athlete";
                    }

                    list.add(new FitnessContentDetailDTO(c, author, isOwner));
                }
            }
            return list;
        } catch (SQLException e) {
            logger.error("Error querying approved fitness content: {}", e.getMessage());
            throw new DatabaseException("Failed to query fitness library content: " + e.getMessage(), e);
        }
    }

    @Override
    public int countApprovedWithFilters(String searchQuery, FitnessContent.Category category) {
        StringBuilder sql = new StringBuilder(
                "SELECT COUNT(*) FROM fitness_content WHERE approval_status = 'APPROVED' ");

        List<Object> params = new ArrayList<>();
        if (category != null) {
            sql.append("AND category = ? ");
            params.add(category.name());
        }
        if (searchQuery != null && !searchQuery.trim().isEmpty()) {
            sql.append("AND (title LIKE ? OR content_text LIKE ?) ");
            String pattern = "%" + searchQuery.trim() + "%";
            params.add(pattern);
            params.add(pattern);
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
            logger.error("Error counting approved fitness content: {}", e.getMessage());
            throw new DatabaseException("Failed to count fitness library content: " + e.getMessage(), e);
        }
    }

    @Override
    public List<FitnessContentDetailDTO> findAdminWithFilters(String searchQuery, FitnessContent.Category category,
                                                              FitnessContent.ApprovalStatus status, Integer createdBy,
                                                              int limit, int offset) {
        StringBuilder sql = new StringBuilder(
                "SELECT c.content_id, c.created_by, c.title, c.category, c.content_text, c.approval_status, " +
                "c.reviewed_by, c.reviewed_at, c.created_at, c.updated_at, u.display_name, u.email " +
                "FROM fitness_content c " +
                "JOIN users u ON c.created_by = u.user_id " +
                "WHERE 1=1 ");

        List<Object> params = new ArrayList<>();
        if (status != null) {
            sql.append("AND c.approval_status = ? ");
            params.add(status.name());
        }
        if (category != null) {
            sql.append("AND c.category = ? ");
            params.add(category.name());
        }
        if (createdBy != null) {
            sql.append("AND c.created_by = ? ");
            params.add(createdBy);
        }
        if (searchQuery != null && !searchQuery.trim().isEmpty()) {
            sql.append("AND (c.title LIKE ? OR c.content_text LIKE ?) ");
            String pattern = "%" + searchQuery.trim() + "%";
            params.add(pattern);
            params.add(pattern);
        }

        sql.append("ORDER BY c.created_at DESC LIMIT ? OFFSET ?");
        params.add(Math.max(1, limit));
        params.add(Math.max(0, offset));

        List<FitnessContentDetailDTO> list = new ArrayList<>();
        try (Connection conn = DatabaseConnectionPool.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql.toString())) {
            for (int i = 0; i < params.size(); i++) {
                stmt.setObject(i + 1, params.get(i));
            }
            try (ResultSet rs = stmt.executeQuery()) {
                while (rs.next()) {
                    FitnessContent c = mapResultSet(rs);
                    String displayName = rs.getString("display_name");
                    list.add(new FitnessContentDetailDTO(c, displayName, false));
                }
            }
            return list;
        } catch (SQLException e) {
            logger.error("Error querying admin fitness content: {}", e.getMessage());
            throw new DatabaseException("Failed to query admin fitness content: " + e.getMessage(), e);
        }
    }

    @Override
    public int countAdminWithFilters(String searchQuery, FitnessContent.Category category,
                                     FitnessContent.ApprovalStatus status, Integer createdBy) {
        StringBuilder sql = new StringBuilder("SELECT COUNT(*) FROM fitness_content WHERE 1=1 ");

        List<Object> params = new ArrayList<>();
        if (status != null) {
            sql.append("AND approval_status = ? ");
            params.add(status.name());
        }
        if (category != null) {
            sql.append("AND category = ? ");
            params.add(category.name());
        }
        if (createdBy != null) {
            sql.append("AND created_by = ? ");
            params.add(createdBy);
        }
        if (searchQuery != null && !searchQuery.trim().isEmpty()) {
            sql.append("AND (title LIKE ? OR content_text LIKE ?) ");
            String pattern = "%" + searchQuery.trim() + "%";
            params.add(pattern);
            params.add(pattern);
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
            logger.error("Error counting admin fitness content: {}", e.getMessage());
            throw new DatabaseException("Failed to count admin fitness content: " + e.getMessage(), e);
        }
    }

    @Override
    public int countByApprovalStatus(FitnessContent.ApprovalStatus status) {
        try (Connection conn = DatabaseConnectionPool.getConnection();
             PreparedStatement stmt = conn.prepareStatement(SQL_COUNT_BY_STATUS)) {
            stmt.setString(1, status.name());
            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) return rs.getInt(1);
            }
            return 0;
        } catch (SQLException e) {
            logger.error("Error counting content by approval status: {}", e.getMessage());
            throw new DatabaseException("Failed to count content by status: " + e.getMessage(), e);
        }
    }

    private FitnessContent mapResultSet(ResultSet rs) throws SQLException {
        FitnessContent c = new FitnessContent();
        c.setContentId(rs.getInt("content_id"));
        c.setCreatedBy(rs.getInt("created_by"));
        c.setTitle(rs.getString("title"));
        c.setCategory(FitnessContent.Category.valueOf(rs.getString("category")));
        c.setContentText(rs.getString("content_text"));
        c.setApprovalStatus(FitnessContent.ApprovalStatus.valueOf(rs.getString("approval_status")));
        int reviewer = rs.getInt("reviewed_by");
        c.setReviewedBy(rs.wasNull() ? null : reviewer);
        c.setReviewedAt(rs.getTimestamp("reviewed_at"));
        c.setCreatedAt(rs.getTimestamp("created_at"));
        c.setUpdatedAt(rs.getTimestamp("updated_at"));
        return c;
    }
}
