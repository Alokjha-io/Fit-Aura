package com.fitaura.dao.impl;

import com.fitaura.dao.AdminStatisticsDAO;
import com.fitaura.dto.AdminDashboardSummaryDTO;
import com.fitaura.dto.AdminStatisticsDTO;
import com.fitaura.exception.DatabaseException;
import com.fitaura.util.DatabaseConnectionPool;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.HashMap;
import java.util.Map;

/**
 * JDBC implementation of AdminStatisticsDAO.
 * Performs fast, aggregated SQL queries for administration monitoring.
 */
public class AdminStatisticsDAOImpl implements AdminStatisticsDAO {

    private static final Logger logger = LoggerFactory.getLogger(AdminStatisticsDAOImpl.class);

    @Override
    public AdminDashboardSummaryDTO getDashboardSummary() {
        AdminDashboardSummaryDTO dto = new AdminDashboardSummaryDTO();

        try (Connection conn = DatabaseConnectionPool.getConnection()) {

            // 1. User metrics
            String sqlUsers = "SELECT COUNT(*) AS total, " +
                    "COALESCE(SUM(CASE WHEN account_status = 'ACTIVE' THEN 1 ELSE 0 END), 0) AS active_cnt, " +
                    "COALESCE(SUM(CASE WHEN account_status = 'INACTIVE' THEN 1 ELSE 0 END), 0) AS inactive_cnt, " +
                    "COALESCE(SUM(CASE WHEN account_status = 'BLOCKED' THEN 1 ELSE 0 END), 0) AS blocked_cnt " +
                    "FROM users";
            try (PreparedStatement stmt = conn.prepareStatement(sqlUsers);
                 ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) {
                    dto.setTotalUsers(rs.getInt("total"));
                    dto.setActiveUsers(rs.getInt("active_cnt"));
                    dto.setInactiveUsers(rs.getInt("inactive_cnt"));
                    dto.setBlockedUsers(rs.getInt("blocked_cnt"));
                }
            }

            // 2. Workout metrics
            String sqlWorkouts = "SELECT COUNT(*) AS total, " +
                    "COALESCE(SUM(CASE WHEN workout_date >= DATE_SUB(CURDATE(), INTERVAL 7 DAY) THEN 1 ELSE 0 END), 0) AS week_cnt, " +
                    "COALESCE(SUM(CASE WHEN workout_date >= DATE_SUB(CURDATE(), INTERVAL 30 DAY) THEN 1 ELSE 0 END), 0) AS month_cnt " +
                    "FROM workouts";
            try (PreparedStatement stmt = conn.prepareStatement(sqlWorkouts);
                 ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) {
                    dto.setTotalWorkouts(rs.getInt("total"));
                    dto.setWorkoutsThisWeek(rs.getInt("week_cnt"));
                    dto.setWorkoutsThisMonth(rs.getInt("month_cnt"));
                }
            }

            // 3. Goal metrics
            String sqlGoals = "SELECT COUNT(*) AS total, " +
                    "COALESCE(SUM(CASE WHEN status = 'ACTIVE' THEN 1 ELSE 0 END), 0) AS active_cnt, " +
                    "COALESCE(SUM(CASE WHEN status = 'COMPLETED' THEN 1 ELSE 0 END), 0) AS completed_cnt " +
                    "FROM fitness_goals";
            try (PreparedStatement stmt = conn.prepareStatement(sqlGoals);
                 ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) {
                    dto.setTotalGoals(rs.getInt("total"));
                    dto.setActiveGoals(rs.getInt("active_cnt"));
                    dto.setCompletedGoals(rs.getInt("completed_cnt"));
                }
            }

            // 4. Challenge metrics
            String sqlChallenges = "SELECT COUNT(*) AS total, " +
                    "COALESCE(SUM(CASE WHEN status = 'DRAFT' THEN 1 ELSE 0 END), 0) AS pending_cnt, " +
                    "COALESCE(SUM(CASE WHEN status = 'ACTIVE' THEN 1 ELSE 0 END), 0) AS active_cnt, " +
                    "COALESCE(SUM(CASE WHEN status = 'COMPLETED' THEN 1 ELSE 0 END), 0) AS completed_cnt " +
                    "FROM challenges";
            try (PreparedStatement stmt = conn.prepareStatement(sqlChallenges);
                 ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) {
                    dto.setTotalChallenges(rs.getInt("total"));
                    dto.setPendingChallenges(rs.getInt("pending_cnt"));
                    dto.setActiveChallenges(rs.getInt("active_cnt"));
                    dto.setCompletedChallenges(rs.getInt("completed_cnt"));
                }
            }

            // 5. Challenge participants
            String sqlParticipants = "SELECT COUNT(*) AS total FROM challenge_participants";
            try (PreparedStatement stmt = conn.prepareStatement(sqlParticipants);
                 ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) {
                    dto.setTotalChallengeParticipants(rs.getInt("total"));
                }
            }

            // 6. Gamification metrics
            String sqlPoints = "SELECT COALESCE(SUM(points), 0) AS total_pts FROM point_transactions";
            try (PreparedStatement stmt = conn.prepareStatement(sqlPoints);
                 ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) {
                    dto.setTotalPointsAwarded(rs.getInt("total_pts"));
                }
            }

            String sqlAchievements = "SELECT COUNT(*) AS total FROM user_achievements";
            try (PreparedStatement stmt = conn.prepareStatement(sqlAchievements);
                 ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) {
                    dto.setTotalAchievementsEarned(rs.getInt("total"));
                }
            }

            String sqlSocial = "SELECT COUNT(DISTINCT user_id) AS total FROM users WHERE privacy_mode = 'SOCIAL' AND account_status = 'ACTIVE'";
            try (PreparedStatement stmt = conn.prepareStatement(sqlSocial);
                 ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) {
                    dto.setSocialLeaderboardUsers(rs.getInt("total"));
                }
            }

            return dto;

        } catch (SQLException e) {
            logger.error("Error aggregating admin dashboard summary: {}", e.getMessage());
            throw new DatabaseException("Failed to query dashboard summary statistics: " + e.getMessage(), e);
        }
    }

    @Override
    public AdminStatisticsDTO getSystemStatistics() {
        AdminStatisticsDTO dto = new AdminStatisticsDTO();

        try (Connection conn = DatabaseConnectionPool.getConnection()) {

            // 1. Users breakdown
            String sqlUsers = "SELECT COUNT(*) AS total, " +
                    "COALESCE(SUM(CASE WHEN account_status = 'ACTIVE' THEN 1 ELSE 0 END), 0) AS active_cnt, " +
                    "COALESCE(SUM(CASE WHEN account_status = 'INACTIVE' THEN 1 ELSE 0 END), 0) AS inactive_cnt, " +
                    "COALESCE(SUM(CASE WHEN account_status = 'BLOCKED' THEN 1 ELSE 0 END), 0) AS blocked_cnt, " +
                    "COALESCE(SUM(CASE WHEN privacy_mode = 'SOCIAL' THEN 1 ELSE 0 END), 0) AS social_cnt, " +
                    "COALESCE(SUM(CASE WHEN privacy_mode = 'PERSONAL' THEN 1 ELSE 0 END), 0) AS personal_cnt, " +
                    "COALESCE(SUM(CASE WHEN role = 'ADMIN' THEN 1 ELSE 0 END), 0) AS admin_cnt " +
                    "FROM users";
            try (PreparedStatement stmt = conn.prepareStatement(sqlUsers);
                 ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) {
                    dto.setTotalUsers(rs.getInt("total"));
                    dto.setActiveUsers(rs.getInt("active_cnt"));
                    dto.setInactiveUsers(rs.getInt("inactive_cnt"));
                    dto.setBlockedUsers(rs.getInt("blocked_cnt"));
                    dto.setSocialUsers(rs.getInt("social_cnt"));
                    dto.setPersonalUsers(rs.getInt("personal_cnt"));
                    dto.setAdminUsers(rs.getInt("admin_cnt"));
                }
            }

            // 2. Workouts
            String sqlWorkouts = "SELECT COUNT(*) AS total, " +
                    "COALESCE(SUM(CASE WHEN workout_date >= DATE_SUB(CURDATE(), INTERVAL 7 DAY) THEN 1 ELSE 0 END), 0) AS week_cnt, " +
                    "COALESCE(SUM(CASE WHEN workout_date >= DATE_SUB(CURDATE(), INTERVAL 30 DAY) THEN 1 ELSE 0 END), 0) AS month_cnt " +
                    "FROM workouts";
            try (PreparedStatement stmt = conn.prepareStatement(sqlWorkouts);
                 ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) {
                    dto.setTotalWorkouts(rs.getInt("total"));
                    dto.setWorkoutsThisWeek(rs.getInt("week_cnt"));
                    dto.setWorkoutsThisMonth(rs.getInt("month_cnt"));
                }
            }

            // Workout Type distribution
            Map<String, Integer> typeMap = new HashMap<>();
            String sqlType = "SELECT workout_type, COUNT(*) AS cnt FROM workouts GROUP BY workout_type";
            try (PreparedStatement stmt = conn.prepareStatement(sqlType);
                 ResultSet rs = stmt.executeQuery()) {
                while (rs.next()) {
                    typeMap.put(rs.getString("workout_type"), rs.getInt("cnt"));
                }
            }
            dto.setWorkoutTypeDistribution(typeMap);

            // Workout Intensity distribution
            Map<String, Integer> intensityMap = new HashMap<>();
            String sqlIntensity = "SELECT intensity, COUNT(*) AS cnt FROM workouts GROUP BY intensity";
            try (PreparedStatement stmt = conn.prepareStatement(sqlIntensity);
                 ResultSet rs = stmt.executeQuery()) {
                while (rs.next()) {
                    intensityMap.put(rs.getString("intensity"), rs.getInt("cnt"));
                }
            }
            dto.setWorkoutIntensityDistribution(intensityMap);

            // 3. Goals
            String sqlGoals = "SELECT COUNT(*) AS total, " +
                    "COALESCE(SUM(CASE WHEN status = 'ACTIVE' THEN 1 ELSE 0 END), 0) AS active_cnt, " +
                    "COALESCE(SUM(CASE WHEN status = 'COMPLETED' THEN 1 ELSE 0 END), 0) AS completed_cnt, " +
                    "COALESCE(SUM(CASE WHEN status = 'PAUSED' THEN 1 ELSE 0 END), 0) AS paused_cnt, " +
                    "COALESCE(SUM(CASE WHEN status = 'CANCELLED' THEN 1 ELSE 0 END), 0) AS cancelled_cnt " +
                    "FROM fitness_goals";
            try (PreparedStatement stmt = conn.prepareStatement(sqlGoals);
                 ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) {
                    dto.setTotalGoals(rs.getInt("total"));
                    dto.setActiveGoals(rs.getInt("active_cnt"));
                    dto.setCompletedGoals(rs.getInt("completed_cnt"));
                    dto.setPausedGoals(rs.getInt("paused_cnt"));
                    dto.setCancelledGoals(rs.getInt("cancelled_cnt"));
                }
            }

            Map<String, Integer> goalTypeMap = new HashMap<>();
            String sqlGoalType = "SELECT goal_type, COUNT(*) AS cnt FROM fitness_goals GROUP BY goal_type";
            try (PreparedStatement stmt = conn.prepareStatement(sqlGoalType);
                 ResultSet rs = stmt.executeQuery()) {
                while (rs.next()) {
                    goalTypeMap.put(rs.getString("goal_type"), rs.getInt("cnt"));
                }
            }
            dto.setGoalTypeDistribution(goalTypeMap);

            // 4. Challenges
            String sqlChallenges = "SELECT COUNT(*) AS total, " +
                    "COALESCE(SUM(CASE WHEN status = 'DRAFT' THEN 1 ELSE 0 END), 0) AS draft_cnt, " +
                    "COALESCE(SUM(CASE WHEN status = 'ACTIVE' THEN 1 ELSE 0 END), 0) AS active_cnt, " +
                    "COALESCE(SUM(CASE WHEN status = 'COMPLETED' THEN 1 ELSE 0 END), 0) AS completed_cnt, " +
                    "COALESCE(SUM(CASE WHEN status = 'CANCELLED' THEN 1 ELSE 0 END), 0) AS cancelled_cnt " +
                    "FROM challenges";
            try (PreparedStatement stmt = conn.prepareStatement(sqlChallenges);
                 ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) {
                    dto.setTotalChallenges(rs.getInt("total"));
                    dto.setDraftChallenges(rs.getInt("draft_cnt"));
                    dto.setActiveChallenges(rs.getInt("active_cnt"));
                    dto.setCompletedChallenges(rs.getInt("completed_cnt"));
                    dto.setCancelledChallenges(rs.getInt("cancelled_cnt"));
                }
            }

            String sqlParts = "SELECT COUNT(*) AS total, " +
                    "COALESCE(SUM(CASE WHEN status = 'JOINED' OR status = 'IN_PROGRESS' THEN 1 ELSE 0 END), 0) AS active_cnt, " +
                    "COALESCE(SUM(CASE WHEN status = 'COMPLETED' THEN 1 ELSE 0 END), 0) AS comp_cnt " +
                    "FROM challenge_participants";
            try (PreparedStatement stmt = conn.prepareStatement(sqlParts);
                 ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) {
                    dto.setTotalEnrollments(rs.getInt("total"));
                    dto.setActiveParticipants(rs.getInt("active_cnt"));
                    dto.setCompletedParticipants(rs.getInt("comp_cnt"));
                }
            }

            // 5. Gamification
            String sqlPoints = "SELECT COALESCE(SUM(points), 0) AS total_pts FROM point_transactions";
            try (PreparedStatement stmt = conn.prepareStatement(sqlPoints);
                 ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) {
                    dto.setTotalPointsAwarded(rs.getInt("total_pts"));
                }
            }

            String sqlAch = "SELECT COUNT(*) AS total FROM user_achievements";
            try (PreparedStatement stmt = conn.prepareStatement(sqlAch);
                 ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) {
                    dto.setTotalAchievementsEarned(rs.getInt("total"));
                }
            }

            String sqlSoc = "SELECT COUNT(DISTINCT user_id) AS total FROM users WHERE privacy_mode = 'SOCIAL' AND account_status = 'ACTIVE'";
            try (PreparedStatement stmt = conn.prepareStatement(sqlSoc);
                 ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) {
                    dto.setActiveSocialLeaderboardAthletes(rs.getInt("total"));
                }
            }

            return dto;

        } catch (SQLException e) {
            logger.error("Error aggregating admin system statistics: {}", e.getMessage());
            throw new DatabaseException("Failed to query detailed system statistics: " + e.getMessage(), e);
        }
    }
}
