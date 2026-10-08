package com.fitaura;

import com.fitaura.dao.ActivityLogDAO;
import com.fitaura.dao.ChallengeDAO;
import com.fitaura.dao.SystemSettingDAO;
import com.fitaura.dao.UserDAO;
import com.fitaura.dao.WorkoutDAO;
import com.fitaura.dao.impl.ActivityLogDAOImpl;
import com.fitaura.dao.impl.ChallengeDAOImpl;
import com.fitaura.dao.impl.SystemSettingDAOImpl;
import com.fitaura.dao.impl.UserDAOImpl;
import com.fitaura.dao.impl.WorkoutDAOImpl;
import com.fitaura.dto.AdminDashboardSummaryDTO;
import com.fitaura.dto.AdminStatisticsDTO;
import com.fitaura.dto.AdminUserDetailDTO;
import com.fitaura.exception.ValidationException;
import com.fitaura.model.ActivityLog;
import com.fitaura.model.Challenge;
import com.fitaura.model.SystemSetting;
import com.fitaura.model.User;
import com.fitaura.model.Workout;
import com.fitaura.service.AdminService;
import com.fitaura.service.ChallengeService;
import com.fitaura.service.impl.AdminServiceImpl;
import com.fitaura.service.impl.ChallengeServiceImpl;
import com.fitaura.util.PasswordUtil;
import org.junit.jupiter.api.*;

import java.math.BigDecimal;
import java.sql.Date;
import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;

@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
public class AdminDashboardAndManagementTest {

    private static UserDAO userDAO;
    private static WorkoutDAO workoutDAO;
    private static ChallengeDAO challengeDAO;
    private static ActivityLogDAO activityLogDAO;
    private static SystemSettingDAO systemSettingDAO;
    private static AdminService adminService;
    private static ChallengeService challengeService;

    private static User adminUser1;
    private static User adminUser2;
    private static User standardUser;
    private static Integer draftChallengeId;

    @BeforeAll
    public static void setUp() {
        userDAO = new UserDAOImpl();
        workoutDAO = new WorkoutDAOImpl();
        challengeDAO = new ChallengeDAOImpl();
        activityLogDAO = new ActivityLogDAOImpl();
        systemSettingDAO = new SystemSettingDAOImpl();
        adminService = new AdminServiceImpl();
        challengeService = new ChallengeServiceImpl();

        // 1. Primary Admin User
        adminUser1 = new User();
        adminUser1.setEmail("admin_test1_" + System.currentTimeMillis() + "@example.com");
        adminUser1.setPasswordHash(PasswordUtil.hashPassword("AdminPass123!"));
        adminUser1.setFullName("Master Admin");
        adminUser1.setDisplayName("AdminMaster");
        adminUser1.setRole(User.Role.ADMIN);
        adminUser1.setAccountStatus(User.AccountStatus.ACTIVE);
        adminUser1.setPrivacyMode(User.PrivacyMode.PERSONAL);
        Integer a1Id = userDAO.create(adminUser1);
        adminUser1.setUserId(a1Id);

        // 2. Secondary Admin User (for multi-admin safeguards)
        adminUser2 = new User();
        adminUser2.setEmail("admin_test2_" + System.currentTimeMillis() + "@example.com");
        adminUser2.setPasswordHash(PasswordUtil.hashPassword("AdminPass123!"));
        adminUser2.setFullName("Secondary Admin");
        adminUser2.setDisplayName("AdminSec");
        adminUser2.setRole(User.Role.ADMIN);
        adminUser2.setAccountStatus(User.AccountStatus.ACTIVE);
        adminUser2.setPrivacyMode(User.PrivacyMode.PERSONAL);
        Integer a2Id = userDAO.create(adminUser2);
        adminUser2.setUserId(a2Id);

        // 3. Standard User for moderation
        standardUser = new User();
        standardUser.setEmail("athlete_mod_" + System.currentTimeMillis() + "@example.com");
        standardUser.setPasswordHash(PasswordUtil.hashPassword("AthletePass123!"));
        standardUser.setFullName("Taylor Athlete");
        standardUser.setDisplayName("TaylorA");
        standardUser.setRole(User.Role.USER);
        standardUser.setAccountStatus(User.AccountStatus.ACTIVE);
        standardUser.setPrivacyMode(User.PrivacyMode.SOCIAL);
        Integer uId = userDAO.create(standardUser);
        standardUser.setUserId(uId);

        // Create a draft challenge submitted by standardUser
        Challenge draft = new Challenge();
        draft.setName("Community Cycling 50K");
        draft.setDescription("Moderate cycling challenge");
        draft.setGoalValue(new BigDecimal("50.0"));
        draft.setGoalUnit("KM");
        draft.setStartDate(Date.valueOf(LocalDate.now()));
        draft.setEndDate(Date.valueOf(LocalDate.now().plusDays(14)));
        Challenge createdDraft = challengeService.createChallenge(draft, standardUser);
        draftChallengeId = createdDraft.getChallengeId();
    }

    @Test
    @Order(1)
    public void testAdminDashboardSummaryAggregation() {
        AdminDashboardSummaryDTO summary = adminService.getDashboardSummary();
        assertNotNull(summary);
        assertTrue(summary.getTotalUsers() >= 3, "Total users should be >= 3");
        assertTrue(summary.getActiveUsers() >= 3, "Active users should be >= 3");
        assertNotNull(summary.getRecentUsers());
        assertNotNull(summary.getPendingModerationChallenges());
        assertNotNull(summary.getRecentActivityLogs());
    }

    @Test
    @Order(2)
    public void testUserSearchAndFiltering() {
        // Search by email prefix
        List<User> found = adminService.listUsers("athlete_mod_", null, null, null, 10, 0);
        assertFalse(found.isEmpty(), "Should find user by email pattern");
        assertEquals(standardUser.getUserId(), found.get(0).getUserId());

        // Filter by role = ADMIN
        List<User> admins = adminService.listUsers(null, User.Role.ADMIN, null, null, 10, 0);
        assertTrue(admins.size() >= 2, "Should find at least 2 admin users");

        // Filter by status = ACTIVE
        List<User> activeUsers = adminService.listUsers(null, null, User.AccountStatus.ACTIVE, null, 10, 0);
        assertFalse(activeUsers.isEmpty(), "Should find active users");
    }

    @Test
    @Order(3)
    public void testAdminUserDetailsInspection() {
        AdminUserDetailDTO details = adminService.getUserDetails(standardUser.getUserId());
        assertNotNull(details);
        assertEquals(standardUser.getUserId(), details.getUser().getUserId());
        assertEquals("TaylorA", details.getUser().getDisplayName());
    }

    @Test
    @Order(4)
    public void testUserAccountStatusManagement() {
        // Deactivate standard user
        boolean deactivated = adminService.updateUserStatus(
                standardUser.getUserId(),
                User.AccountStatus.INACTIVE,
                adminUser1.getUserId(),
                "127.0.0.1"
        );
        assertTrue(deactivated);
        Optional<User> u1 = userDAO.findById(standardUser.getUserId());
        assertTrue(u1.isPresent());
        assertEquals(User.AccountStatus.INACTIVE, u1.get().getAccountStatus());

        // Block standard user
        boolean blocked = adminService.updateUserStatus(
                standardUser.getUserId(),
                User.AccountStatus.BLOCKED,
                adminUser1.getUserId(),
                "127.0.0.1"
        );
        assertTrue(blocked);
        Optional<User> u2 = userDAO.findById(standardUser.getUserId());
        assertTrue(u2.isPresent());
        assertEquals(User.AccountStatus.BLOCKED, u2.get().getAccountStatus());

        // Re-activate standard user
        boolean reactivated = adminService.updateUserStatus(
                standardUser.getUserId(),
                User.AccountStatus.ACTIVE,
                adminUser1.getUserId(),
                "127.0.0.1"
        );
        assertTrue(reactivated);
        Optional<User> u3 = userDAO.findById(standardUser.getUserId());
        assertTrue(u3.isPresent());
        assertEquals(User.AccountStatus.ACTIVE, u3.get().getAccountStatus());
    }

    @Test
    @Order(5)
    public void testAdminSelfProtectionRule() {
        // Admin1 attempts to deactivate or block their own account
        assertThrows(ValidationException.class, () -> {
            adminService.updateUserStatus(
                    adminUser1.getUserId(),
                    User.AccountStatus.INACTIVE,
                    adminUser1.getUserId(),
                    "127.0.0.1"
            );
        }, "Admin should not be permitted to deactivate own account");

        assertThrows(ValidationException.class, () -> {
            adminService.updateUserStatus(
                    adminUser1.getUserId(),
                    User.AccountStatus.BLOCKED,
                    adminUser1.getUserId(),
                    "127.0.0.1"
            );
        }, "Admin should not be permitted to block own account");
    }

    @Test
    @Order(6)
    public void testChallengeModerationApprove() {
        assertNotNull(draftChallengeId);

        // Approve draft challenge
        boolean approved = challengeService.moderateChallenge(draftChallengeId, "APPROVE", adminUser1.getUserId());
        assertTrue(approved);

        Optional<Challenge> cOpt = challengeDAO.findById(draftChallengeId);
        assertTrue(cOpt.isPresent());
        assertEquals(Challenge.ChallengeStatus.ACTIVE, cOpt.get().getStatus());
    }

    @Test
    @Order(7)
    public void testSystemStatisticsAggregation() {
        AdminStatisticsDTO stats = adminService.getSystemStatistics();
        assertNotNull(stats);
        assertTrue(stats.getTotalUsers() >= 3);
        assertTrue(stats.getAdminUsers() >= 2);
        assertNotNull(stats.getWorkoutTypeDistribution());
        assertNotNull(stats.getGoalTypeDistribution());
    }

    @Test
    @Order(8)
    public void testActivityLogsRetrievalAndFiltering() {
        List<ActivityLog> logs = adminService.getActivityLogs(null, null, null, 20, 0);
        assertNotNull(logs);
        assertFalse(logs.isEmpty(), "Activity logs should contain entries");

        // Verify status change audit log was recorded
        List<ActivityLog> statusLogs = adminService.getActivityLogs(null, "USER_STATUS_CHANGED", null, 20, 0);
        assertFalse(statusLogs.isEmpty(), "Should find USER_STATUS_CHANGED logs");
    }

    @Test
    @Order(9)
    public void testSystemSettingsManagement() {
        List<SystemSetting> settings = adminService.getSystemSettings();
        assertNotNull(settings);

        boolean updated = adminService.updateSystemSetting(
                "challenges_enabled",
                "true",
                adminUser1.getUserId(),
                "127.0.0.1"
        );
        assertTrue(updated);

        // Disallow dangerous keys
        assertThrows(ValidationException.class, () -> {
            adminService.updateSystemSetting(
                    "database_password",
                    "secret",
                    adminUser1.getUserId(),
                    "127.0.0.1"
            );
        });
    }
}
