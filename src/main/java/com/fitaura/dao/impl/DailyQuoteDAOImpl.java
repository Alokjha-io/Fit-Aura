package com.fitaura.dao.impl;

import com.fitaura.dao.DailyQuoteDAO;
import com.fitaura.exception.DatabaseException;
import com.fitaura.model.DailyQuote;
import com.fitaura.util.DatabaseConnectionPool;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

public class DailyQuoteDAOImpl implements DailyQuoteDAO {

    private static final Logger logger = LoggerFactory.getLogger(DailyQuoteDAOImpl.class);

    @Override
    public Optional<DailyQuote> findTodayQuote(Date today) {
        String sql = "SELECT quote_id, quote_text, author_name, quote_date, status, created_by, created_at, updated_at " +
                     "FROM daily_quotes WHERE quote_date = ? AND status = 'PUBLISHED' LIMIT 1";
        try (Connection conn = DatabaseConnectionPool.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setDate(1, today);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    return Optional.of(mapResultSet(rs));
                }
            }
        } catch (SQLException e) {
            logger.error("Error finding today's quote for {}: {}", today, e.getMessage(), e);
            throw new DatabaseException("Database error finding today's quote.", e);
        }
        return Optional.empty();
    }

    @Override
    public Optional<DailyQuote> findById(Integer quoteId) {
        String sql = "SELECT quote_id, quote_text, author_name, quote_date, status, created_by, created_at, updated_at " +
                     "FROM daily_quotes WHERE quote_id = ?";
        try (Connection conn = DatabaseConnectionPool.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, quoteId);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    return Optional.of(mapResultSet(rs));
                }
            }
        } catch (SQLException e) {
            logger.error("Error finding quote #{}: {}", quoteId, e.getMessage(), e);
            throw new DatabaseException("Database error finding quote.", e);
        }
        return Optional.empty();
    }

    @Override
    public List<DailyQuote> findUpcomingQuotes(Date today, int limit) {
        String sql = "SELECT quote_id, quote_text, author_name, quote_date, status, created_by, created_at, updated_at " +
                     "FROM daily_quotes WHERE quote_date > ? ORDER BY quote_date ASC LIMIT ?";
        List<DailyQuote> list = new ArrayList<>();
        try (Connection conn = DatabaseConnectionPool.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setDate(1, today);
            ps.setInt(2, limit > 0 ? limit : 20);
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    list.add(mapResultSet(rs));
                }
            }
        } catch (SQLException e) {
            logger.error("Error finding upcoming quotes: {}", e.getMessage(), e);
            throw new DatabaseException("Database error finding upcoming quotes.", e);
        }
        return list;
    }

    @Override
    public List<DailyQuote> findPastQuotes(Date today, int limit) {
        String sql = "SELECT quote_id, quote_text, author_name, quote_date, status, created_by, created_at, updated_at " +
                     "FROM daily_quotes WHERE quote_date < ? ORDER BY quote_date DESC LIMIT ?";
        List<DailyQuote> list = new ArrayList<>();
        try (Connection conn = DatabaseConnectionPool.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setDate(1, today);
            ps.setInt(2, limit > 0 ? limit : 20);
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    list.add(mapResultSet(rs));
                }
            }
        } catch (SQLException e) {
            logger.error("Error finding past quotes: {}", e.getMessage(), e);
            throw new DatabaseException("Database error finding past quotes.", e);
        }
        return list;
    }

    @Override
    public List<DailyQuote> findAll(int limit, int offset) {
        String sql = "SELECT quote_id, quote_text, author_name, quote_date, status, created_by, created_at, updated_at " +
                     "FROM daily_quotes ORDER BY quote_date DESC LIMIT ? OFFSET ?";
        List<DailyQuote> list = new ArrayList<>();
        try (Connection conn = DatabaseConnectionPool.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, limit > 0 ? limit : 50);
            ps.setInt(2, Math.max(0, offset));
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    list.add(mapResultSet(rs));
                }
            }
        } catch (SQLException e) {
            logger.error("Error finding all quotes: {}", e.getMessage(), e);
            throw new DatabaseException("Database error finding quotes.", e);
        }
        return list;
    }

    @Override
    public int countAll() {
        String sql = "SELECT COUNT(*) FROM daily_quotes";
        try (Connection conn = DatabaseConnectionPool.getConnection();
             Statement s = conn.createStatement();
             ResultSet rs = s.executeQuery(sql)) {
            if (rs.next()) {
                return rs.getInt(1);
            }
        } catch (SQLException e) {
            logger.error("Error counting quotes: {}", e.getMessage(), e);
            throw new DatabaseException("Database error counting quotes.", e);
        }
        return 0;
    }

    @Override
    public Integer create(DailyQuote quote) {
        String sql = "INSERT INTO daily_quotes (quote_text, author_name, quote_date, status, created_by) " +
                     "VALUES (?, ?, ?, ?, ?)";
        try (Connection conn = DatabaseConnectionPool.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {
            ps.setString(1, quote.getQuoteText());
            ps.setString(2, quote.getAuthorName() != null ? quote.getAuthorName() : "Anonymous");
            ps.setDate(3, quote.getQuoteDate());
            ps.setString(4, quote.getStatus() != null ? quote.getStatus().name() : DailyQuote.Status.PUBLISHED.name());
            if (quote.getCreatedBy() != null) {
                ps.setInt(5, quote.getCreatedBy());
            } else {
                ps.setNull(5, Types.INTEGER);
            }

            ps.executeUpdate();
            try (ResultSet keys = ps.getGeneratedKeys()) {
                if (keys.next()) {
                    quote.setQuoteId(keys.getInt(1));
                    return quote.getQuoteId();
                }
            }
        } catch (SQLException e) {
            logger.error("Error creating quote: {}", e.getMessage(), e);
            throw new DatabaseException("Database error creating quote.", e);
        }
        return null;
    }

    @Override
    public boolean update(DailyQuote quote) {
        String sql = "UPDATE daily_quotes SET quote_text = ?, author_name = ?, quote_date = ?, status = ? " +
                     "WHERE quote_id = ?";
        try (Connection conn = DatabaseConnectionPool.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, quote.getQuoteText());
            ps.setString(2, quote.getAuthorName() != null ? quote.getAuthorName() : "Anonymous");
            ps.setDate(3, quote.getQuoteDate());
            ps.setString(4, quote.getStatus().name());
            ps.setInt(5, quote.getQuoteId());
            return ps.executeUpdate() > 0;
        } catch (SQLException e) {
            logger.error("Error updating quote #{}: {}", quote.getQuoteId(), e.getMessage(), e);
            throw new DatabaseException("Database error updating quote.", e);
        }
    }

    @Override
    public boolean delete(Integer quoteId) {
        String sql = "DELETE FROM daily_quotes WHERE quote_id = ?";
        try (Connection conn = DatabaseConnectionPool.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, quoteId);
            return ps.executeUpdate() > 0;
        } catch (SQLException e) {
            logger.error("Error deleting quote #{}: {}", quoteId, e.getMessage(), e);
            throw new DatabaseException("Database error deleting quote.", e);
        }
    }

    @Override
    public boolean existsPublishedForDate(Date date, Integer excludeQuoteId) {
        String sql = "SELECT COUNT(*) FROM daily_quotes WHERE quote_date = ? AND status = 'PUBLISHED'" +
                     (excludeQuoteId != null ? " AND quote_id != ?" : "");
        try (Connection conn = DatabaseConnectionPool.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setDate(1, date);
            if (excludeQuoteId != null) {
                ps.setInt(2, excludeQuoteId);
            }
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    return rs.getInt(1) > 0;
                }
            }
        } catch (SQLException e) {
            logger.error("Error checking published quote for date {}: {}", date, e.getMessage(), e);
            throw new DatabaseException("Database error checking quote date.", e);
        }
        return false;
    }

    private DailyQuote mapResultSet(ResultSet rs) throws SQLException {
        DailyQuote q = new DailyQuote();
        q.setQuoteId(rs.getInt("quote_id"));
        q.setQuoteText(rs.getString("quote_text"));
        q.setAuthorName(rs.getString("author_name"));
        q.setQuoteDate(rs.getDate("quote_date"));
        String statusStr = rs.getString("status");
        if (statusStr != null) {
            try {
                q.setStatus(DailyQuote.Status.valueOf(statusStr));
            } catch (IllegalArgumentException e) {
                q.setStatus(DailyQuote.Status.PUBLISHED);
            }
        }
        int createdBy = rs.getInt("created_by");
        if (!rs.wasNull()) {
            q.setCreatedBy(createdBy);
        }
        q.setCreatedAt(rs.getTimestamp("created_at"));
        q.setUpdatedAt(rs.getTimestamp("updated_at"));
        return q;
    }
}
