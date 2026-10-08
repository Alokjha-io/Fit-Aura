package com.fitaura.util;

import com.fitaura.exception.DatabaseException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.sql.Connection;
import java.sql.SQLException;

/**
 * Utility helper for JDBC resource management and transaction coordination.
 * Ensures PreparedStatement, ResultSet, and Connection instances are closed safely
 * and transactions are handled atomically.
 */
public final class DBUtil {

    private static final Logger logger = LoggerFactory.getLogger(DBUtil.class);

    private DBUtil() {
        // Prevent instantiation
    }

    /**
     * Begins an atomic transaction by disabling auto-commit on a new connection.
     * The caller is responsible for committing or rolling back and finally closing.
     */
    public static Connection beginTransaction() {
        try {
            Connection conn = DatabaseConnectionPool.getConnection();
            conn.setAutoCommit(false);
            return conn;
        } catch (SQLException e) {
            logger.error("Failed to begin transaction: {}", e.getMessage());
            throw new DatabaseException("Failed to begin transaction: " + e.getMessage(), e);
        }
    }

    /**
     * Commits the current transaction on the provided connection.
     */
    public static void commitTransaction(Connection conn) {
        if (conn != null) {
            try {
                conn.commit();
            } catch (SQLException e) {
                logger.error("Failed to commit transaction: {}", e.getMessage());
                throw new DatabaseException("Failed to commit transaction: " + e.getMessage(), e);
            }
        }
    }

    /**
     * Rolls back uncommitted changes on the provided connection.
     */
    public static void rollbackTransaction(Connection conn) {
        if (conn != null) {
            try {
                conn.rollback();
            } catch (SQLException e) {
                logger.warn("Failed to rollback transaction: {}", e.getMessage());
            }
        }
    }

    /**
     * Restores auto-commit to true and safely closes/returns the connection to the pool.
     */
    public static void closeTransaction(Connection conn) {
        if (conn != null) {
            try {
                if (!conn.isClosed()) {
                    conn.setAutoCommit(true);
                }
            } catch (SQLException e) {
                logger.warn("Could not reset autoCommit on connection: {}", e.getMessage());
            } finally {
                close(conn);
            }
        }
    }

    /**
     * Safely closes one or more AutoCloseable resources without throwing checked exceptions.
     */
    public static void close(AutoCloseable... resources) {
        if (resources == null) return;
        for (AutoCloseable res : resources) {
            if (res != null) {
                try {
                    res.close();
                } catch (Exception e) {
                    logger.debug("Error while closing JDBC resource: {}", e.getMessage());
                }
            }
        }
    }
}
