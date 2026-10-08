package com.fitaura.service.impl;

import com.fitaura.dao.ActivityLogDAO;
import com.fitaura.dao.AdminStatisticsDAO;
import com.fitaura.dao.ChallengeDAO;
import com.fitaura.dao.ChallengeParticipantDAO;
import com.fitaura.dao.FitnessGoalDAO;
import com.fitaura.dao.PointTransactionDAO;
import com.fitaura.dao.SystemSettingDAO;
import com.fitaura.dao.UserAchievementDAO;
import com.fitaura.dao.UserDAO;
import com.fitaura.dao.WorkoutDAO;
import com.fitaura.dao.impl.ActivityLogDAOImpl;
import com.fitaura.dao.impl.AdminStatisticsDAOImpl;
import com.fitaura.dao.impl.ChallengeDAOImpl;
import com.fitaura.dao.impl.ChallengeParticipantDAOImpl;
import com.fitaura.dao.impl.FitnessGoalDAOImpl;
import com.fitaura.dao.impl.PointTransactionDAOImpl;
import com.fitaura.dao.impl.SystemSettingDAOImpl;
import com.fitaura.dao.impl.UserAchievementDAOImpl;
import com.fitaura.dao.impl.UserDAOImpl;
import com.fitaura.dao.impl.WorkoutDAOImpl;
import com.fitaura.dto.AdminDashboardSummaryDTO;
import com.fitaura.dto.AdminStatisticsDTO;
import com.fitaura.dto.AdminUserDetailDTO;
import com.fitaura.exception.DatabaseException;
import com.fitaura.exception.ResourceNotFoundException;
import com.fitaura.exception.ValidationException;
import com.fitaura.model.ActivityLog;
import com.fitaura.model.Challenge;
import com.fitaura.model.ChallengeParticipant;
import com.fitaura.model.FitnessGoal;
import com.fitaura.model.SystemSetting;
import com.fitaura.model.User;
import com.fitaura.model.UserAchievement;
import com.fitaura.model.Workout;
import com.fitaura.service.AdminService;
import com.fitaura.util.AppConstants;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.sql.Timestamp;
import java.util.Collections;
import java.util.List;
import java.util.Optional;

/**
 * Production implementation of AdminService.
 * Enforces server-side validation, admin self-protection rules, least-privilege data sanitization,
 * and comprehensive activity audit logging.
 */
public class AdminServiceImpl implements AdminService {

    private static final Logger logger = LoggerFactory.getLogger(AdminServiceImpl.class);

    private final AdminStatisticsDAO adminStatisticsDAO;
    private final UserDAO userDAO;
    private final ActivityLogDAO activityLogDAO;
    private final SystemSettingDAO systemSettingDAO;
    private final WorkoutDAO workoutDAO;
    private final FitnessGoalDAO fitnessGoalDAO;
    private final ChallengeDAO challengeDAO;
    private final ChallengeParticipantDAO challengeParticipantDAO;
    private final PointTransactionDAO pointTransactionDAO;
    private final UserAchievementDAO userAchievementDAO;

    public AdminServiceImpl() {
        this(new AdminStatisticsDAOImpl(), new UserDAOImpl(), new ActivityLogDAOImpl(),
             new SystemSettingDAOImpl(), new WorkoutDAOImpl(), new FitnessGoalDAOImpl(),
             new ChallengeDAOImpl(), new ChallengeParticipantDAOImpl(), new PointTransactionDAOImpl(),
             new UserAchievementDAOImpl());
    }

    public AdminServiceImpl(AdminStatisticsDAO adminStatisticsDAO, UserDAO userDAO,
                            ActivityLogDAO activityLogDAO, SystemSettingDAO systemSettingDAO,
                            WorkoutDAO workoutDAO, FitnessGoalDAO fitnessGoalDAO,
                            ChallengeDAO challengeDAO, ChallengeParticipantDAO challengeParticipantDAO,
                            PointTransactionDAO pointTransactionDAO, UserAchievementDAO userAchievementDAO) {
        this.adminStatisticsDAO = adminStatisticsDAO;
        this.userDAO = userDAO;
        this.activityLogDAO = activityLogDAO;
        this.systemSettingDAO = systemSettingDAO;
        this.workoutDAO = workoutDAO;
        this.fitnessGoalDAO = fitnessGoalDAO;
        this.challengeDAO = challengeDAO;
        this.challengeParticipantDAO = challengeParticipantDAO;
        this.pointTransactionDAO = pointTransactionDAO;
        this.userAchievementDAO = userAchievementDAO;
    }

    @Override
    public AdminDashboardSummaryDTO getDashboardSummary() {
        AdminDashboardSummaryDTO summary = adminStatisticsDAO.getDashboardSummary();

        // Populate recent items for rapid admin review
        summary.setRecentUsers(userDAO.findRecentUsers(5));
        summary.setPendingModerationChallenges(challengeDAO.findPendingModeration());
        summary.setRecentActivityLogs(activityLogDAO.findRecent(8));

        return summary;
    }

    @Override
    public AdminStatisticsDTO getSystemStatistics() {
        return adminStatisticsDAO.getSystemStatistics();
    }

    @Override
    public List<User> listUsers(String search, User.Role role, User.AccountStatus status,
                                User.PrivacyMode privacy, int limit, int offset) {
        int validLimit = limit > 0 ? Math.min(limit, 100) : 20;
        int validOffset = Math.max(0, offset);
        return userDAO.findWithFilters(search, role, status, privacy, validLimit, validOffset);
    }

    @Override
    public int countUsers(String search, User.Role role, User.AccountStatus status,
                          User.PrivacyMode privacy) {
        return userDAO.countWithFilters(search, role, status, privacy);
    }

    @Override
    public AdminUserDetailDTO getUserDetails(Integer userId) {
        if (userId == null) {
            throw new ValidationException("User ID is required.");
        }

        Optional<User> userOpt = userDAO.findById(userId);
        if (userOpt.isEmpty()) {
            throw new ResourceNotFoundException("User #" + userId + " not found.");
        }

        User user = userOpt.get();
        AdminUserDetailDTO dto = new AdminUserDetailDTO();
        dto.setUser(user);

        // Aggregate safe counts
        dto.setTotalWorkouts(workoutDAO.countByUserId(userId));
        dto.setTotalGoals(fitnessGoalDAO.countByUserId(userId));
        dto.setActiveGoals(fitnessGoalDAO.countActiveByUserId(userId));

        List<ChallengeParticipant> cps = challengeParticipantDAO.findByUserId(userId);
        dto.setJoinedChallenges(cps.size());
        long completedChallenges = cps.stream()
                .filter(c -> c.getStatus() == ChallengeParticipant.ParticipantStatus.COMPLETED)
                .count();
        dto.setCompletedChallenges((int) completedChallenges);

        dto.setTotalPoints(pointTransactionDAO.calculateTotalPoints(userId));
        List<UserAchievement> uas = userAchievementDAO.findByUserId(userId);
        dto.setEarnedAchievements(uas.size());

        List<Workout> recentWorkouts = workoutDAO.findByUserId(userId, 1, 0);
        if (!recentWorkouts.isEmpty() && recentWorkouts.get(0).getCreatedAt() != null) {
            dto.setLastWorkoutDate(recentWorkouts.get(0).getCreatedAt());
        }

        return dto;
    }

    @Override
    public boolean updateUserStatus(Integer targetUserId, User.AccountStatus newStatus,
                                    Integer adminUserId, String clientIp) {
        if (targetUserId == null) {
            throw new ValidationException("Target user ID is required.");
        }
        if (newStatus == null) {
            throw new ValidationException("Target account status is required.");
        }
        if (adminUserId == null) {
            throw new ValidationException("Authenticated admin ID is required.");
        }

        Optional<User> targetUserOpt = userDAO.findById(targetUserId);
        if (targetUserOpt.isEmpty()) {
            throw new ResourceNotFoundException("Target user #" + targetUserId + " does not exist.");
        }
        User targetUser = targetUserOpt.get();

        // 1. Admin Self-Protection: Cannot deactivate or block own account
        if (targetUserId.equals(adminUserId) &&
                (newStatus == User.AccountStatus.INACTIVE || newStatus == User.AccountStatus.BLOCKED)) {
            throw new ValidationException("You cannot deactivate or block your own administrator account.");
        }

        // 2. Platform Safeguard: Prevent removing the last active administrator
        if (targetUser.getRole() == User.Role.ADMIN &&
                (newStatus == User.AccountStatus.INACTIVE || newStatus == User.AccountStatus.BLOCKED)) {
            int adminCount = userDAO.countByRole(User.Role.ADMIN);
            int activeAdminCount = userDAO.countByStatus(User.AccountStatus.ACTIVE);
            if (adminCount <= 1 || activeAdminCount <= 1) {
                throw new ValidationException("Action denied: Cannot deactivate or block the only active administrator.");
            }
        }

        User.AccountStatus oldStatus = targetUser.getAccountStatus();
        if (oldStatus == newStatus) {
            return true; // No change necessary
        }

        boolean updated = userDAO.updateAccountStatus(targetUserId, newStatus);
        if (updated) {
            // Write audit log
            try {
                ActivityLog log = new ActivityLog();
                log.setUserId(adminUserId);
                log.setActionType("USER_STATUS_CHANGED");
                log.setEntityType("USER");
                log.setEntityId(targetUserId);
                log.setDescription("Admin changed account status of user '" + targetUser.getEmail() +
                                   "' (#" + targetUserId + ") from " + oldStatus + " to " + newStatus);
                log.setIpAddress(clientIp);
                activityLogDAO.create(log);
            } catch (Exception ex) {
                logger.warn("Failed to write audit log for user status update: {}", ex.getMessage());
            }

            logger.info("Admin {} changed user {} status from {} to {}", adminUserId, targetUserId, oldStatus, newStatus);
        }

        return updated;
    }

    @Override
    public List<ActivityLog> getActivityLogs(Integer userId, String actionType, String entityType,
                                            int limit, int offset) {
        int validLimit = limit > 0 ? Math.min(limit, 100) : 25;
        int validOffset = Math.max(0, offset);
        return activityLogDAO.findWithFilters(userId, actionType, entityType, validLimit, validOffset);
    }

    @Override
    public int countActivityLogs(Integer userId, String actionType, String entityType) {
        return activityLogDAO.countWithFilters(userId, actionType, entityType);
    }

    @Override
    public List<SystemSetting> getSystemSettings() {
        return systemSettingDAO.findAll();
    }

    @Override
    public boolean updateSystemSetting(String key, String value, Integer adminUserId, String clientIp) {
        if (key == null || key.trim().isEmpty()) {
            throw new ValidationException("Setting key is required.");
        }
        if (value == null) {
            throw new ValidationException("Setting value cannot be null.");
        }

        String sanitizedKey = key.trim().toLowerCase();

        // Ensure key is an allowed safe application setting
        boolean isAllowedKey = sanitizedKey.equals("registration_enabled") ||
                               sanitizedKey.equals("challenges_enabled") ||
                               sanitizedKey.equals("content_approval_required") ||
                               sanitizedKey.equals("app_name") ||
                               sanitizedKey.equals("default_page_size") ||
                               sanitizedKey.equals("maintenance_mode");

        if (!isAllowedKey) {
            throw new ValidationException("Invalid setting key '" + key + "'. Only safe application settings can be configured.");
        }

        Optional<SystemSetting> existingOpt = systemSettingDAO.findByKey(sanitizedKey);
        String desc = existingOpt.map(SystemSetting::getDescription).orElse("Application setting: " + sanitizedKey);

        boolean saved = systemSettingDAO.updateOrInsert(sanitizedKey, value.trim(), desc, adminUserId);
        if (saved) {
            // Write audit log
            try {
                ActivityLog log = new ActivityLog();
                log.setUserId(adminUserId);
                log.setActionType("ADMIN_SETTING_CHANGED");
                log.setEntityType("SETTING");
                log.setDescription("Admin updated setting '" + sanitizedKey + "' to '" + value.trim() + "'");
                log.setIpAddress(clientIp);
                activityLogDAO.create(log);
            } catch (Exception ex) {
                logger.warn("Failed to write audit log for setting update: {}", ex.getMessage());
            }

            logger.info("Admin {} updated setting {} to {}", adminUserId, sanitizedKey, value);
        }

        return saved;
    }
}
