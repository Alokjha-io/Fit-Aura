package com.fitaura.service;

import com.fitaura.dto.AdminDashboardSummaryDTO;
import com.fitaura.dto.AdminStatisticsDTO;
import com.fitaura.dto.AdminUserDetailDTO;
import com.fitaura.model.ActivityLog;
import com.fitaura.model.SystemSetting;
import com.fitaura.model.User;

import java.util.List;

/**
 * Service interface for administrative operations:
 * - High-level executive dashboard summaries
 * - Deep system statistics
 * - User search, filtering, inspection, and status management with self-protection
 * - Activity/audit log auditing
 * - System settings management
 */
public interface AdminService {

    /**
     * Retrieves aggregated summary metrics and recent platform events for the admin dashboard.
     */
    AdminDashboardSummaryDTO getDashboardSummary();

    /**
     * Retrieves deep system statistics across users, workouts, goals, challenges, and gamification.
     */
    AdminStatisticsDTO getSystemStatistics();

    /**
     * Queries users with server-side search, role, status, and privacy filtering with pagination.
     */
    List<User> listUsers(String search, User.Role role, User.AccountStatus status,
                         User.PrivacyMode privacy, int limit, int offset);

    /**
     * Counts total matching users for pagination calculation.
     */
    int countUsers(String search, User.Role role, User.AccountStatus status,
                   User.PrivacyMode privacy);

    /**
     * Retrieves sanitized account details and activity counts for administrative inspection.
     */
    AdminUserDetailDTO getUserDetails(Integer userId);

    /**
     * Updates a user's account status (ACTIVE, INACTIVE, BLOCKED).
     * Enforces admin self-protection and ensures the platform is never left with 0 active admins.
     */
    boolean updateUserStatus(Integer targetUserId, User.AccountStatus newStatus,
                             Integer adminUserId, String clientIp);

    /**
     * Retrieves filtered and paginated activity audit logs.
     */
    List<ActivityLog> getActivityLogs(Integer userId, String actionType, String entityType,
                                      int limit, int offset);

    /**
     * Counts filtered activity audit logs for pagination.
     */
    int countActivityLogs(Integer userId, String actionType, String entityType);

    /**
     * Retrieves all safe application-level system settings.
     */
    List<SystemSetting> getSystemSettings();

    /**
     * Updates an existing application-level setting with audit logging.
     */
    boolean updateSystemSetting(String key, String value, Integer adminUserId, String clientIp);
}
