package com.fitaura;

import com.fitaura.dao.AchievementDAO;
import com.fitaura.dao.SystemSettingDAO;
import com.fitaura.dao.UserDAO;
import com.fitaura.dao.impl.AchievementDAOImpl;
import com.fitaura.dao.impl.SystemSettingDAOImpl;
import com.fitaura.dao.impl.UserDAOImpl;
import com.fitaura.model.Achievement;
import com.fitaura.model.SystemSetting;
import com.fitaura.model.User;
import com.fitaura.util.DBUtil;
import com.fitaura.util.DatabaseConnectionPool;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.Statement;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Backend tests verifying Phase 2 database & persistence requirements:
 * - Driver availability
 * - HikariCP pool health and connection acquisition
 * - Lightweight 'SELECT 1' query execution
 * - Safe return of connections to pool
 * - Atomic transaction commit and rollback support
 * - Seed data accessibility via DAO layer (Achievements, System Settings)
 */
public class DatabaseConnectionTest {

    @Test
    @DisplayName("Verify MySQL JDBC Driver can be loaded by ClassLoader")
    void testDriverLoading() {
        assertDoesNotThrow(() -> {
            Class.forName("com.mysql.cj.jdbc.Driver");
        }, "MySQL Connector/J driver class must be loadable");
    }

    @Test
    @DisplayName("Verify HikariCP Connection Pool and 'SELECT 1' Probe")
    void testConnectionPoolHealth() {
        boolean healthy = DatabaseConnectionPool.isHealthy();
        assertTrue(healthy, "HikariCP connection pool must report healthy connection to MySQL database 'fitaura'");

        // Checkout and verify returning connection to pool
        try (Connection conn = DatabaseConnectionPool.getConnection();
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery("SELECT 1 AS probe")) {
            assertTrue(rs.next(), "ResultSet from probe query must return a row");
            assertEquals(1, rs.getInt("probe"), "SELECT 1 must return 1");
            assertFalse(conn.isClosed(), "Connection must remain open until try-with-resources closes it");
        } catch (Exception e) {
            fail("Database probe failed with exception: " + e.getMessage());
        }
    }

    @Test
    @DisplayName("Verify Atomic Transactions (Commit and Rollback) via DBUtil")
    void testTransactionSupport() {
        Connection txConn = null;
        try {
            txConn = DBUtil.beginTransaction();
            assertNotNull(txConn, "Transaction connection must not be null");
            assertFalse(txConn.getAutoCommit(), "autoCommit must be disabled during active transaction");

            // Execute test transactional operation
            User testUser = new User();
            testUser.setFullName("Tx Test User");
            testUser.setEmail("txtest_" + System.currentTimeMillis() + "@fitaura.test");
            testUser.setPasswordHash("$2a$12$e0MYzXy4YjFkZ4Gz5K0qOe...");
            testUser.setRole(User.Role.USER);
            testUser.setAccountStatus(User.AccountStatus.ACTIVE);
            testUser.setPrivacyMode(User.PrivacyMode.PERSONAL);
            testUser.setDisplayName("TxTester");

            UserDAO userDAO = new UserDAOImpl();
            Integer generatedId = userDAO.create(testUser, txConn);
            assertNotNull(generatedId, "Generated ID must be returned inside transaction");

            // Explicit rollback to ensure test isolation
            DBUtil.rollbackTransaction(txConn);

            // Verify rolled-back user does NOT exist in subsequent query
            Optional<User> found = userDAO.findById(generatedId);
            assertFalse(found.isPresent(), "Rolled back user must not persist in database");

        } catch (Exception e) {
            if (txConn != null) {
                DBUtil.rollbackTransaction(txConn);
            }
            fail("Transaction test failed with exception: " + e.getMessage());
        } finally {
            DBUtil.closeTransaction(txConn);
        }
    }

    @Test
    @DisplayName("Verify Seed Data accessibility via AchievementDAO")
    void testAchievementDAOSeedData() {
        AchievementDAO achievementDAO = new AchievementDAOImpl();
        List<Achievement> achievements = achievementDAO.findAllActive();
        assertNotNull(achievements, "Active achievements list must not be null");
        assertTrue(achievements.size() >= 3, "At least 3 seed achievements must be present in database");

        Optional<Achievement> firstWorkout = achievementDAO.findByName("First Workout");
        assertTrue(firstWorkout.isPresent(), "'First Workout' achievement must exist");
        assertEquals(10, firstWorkout.get().getPoints());

        Optional<Achievement> streak7 = achievementDAO.findByName("7 Day Streak");
        assertTrue(streak7.isPresent(), "'7 Day Streak' achievement must exist");
        assertEquals(50, streak7.get().getPoints());

        Optional<Achievement> challengeCompleted = achievementDAO.findByName("Challenge Completed");
        assertTrue(challengeCompleted.isPresent(), "'Challenge Completed' achievement must exist");
        assertEquals(100, challengeCompleted.get().getPoints());
    }

    @Test
    @DisplayName("Verify System Settings accessibility via SystemSettingDAO")
    void testSystemSettingDAO() {
        SystemSettingDAO settingDAO = new SystemSettingDAOImpl();
        Optional<SystemSetting> regSetting = settingDAO.findByKey("registration_enabled");
        assertTrue(regSetting.isPresent(), "'registration_enabled' setting must exist");
        assertEquals("true", regSetting.get().getSettingValue());

        Optional<SystemSetting> challengesSetting = settingDAO.findByKey("challenges_enabled");
        assertTrue(challengesSetting.isPresent(), "'challenges_enabled' setting must exist");
        assertEquals("true", challengesSetting.get().getSettingValue());
    }
}
