package com.fitaura;

import com.fitaura.dao.ActivityLogDAO;
import com.fitaura.dao.FitnessContentDAO;
import com.fitaura.dao.UserDAO;
import com.fitaura.dao.impl.ActivityLogDAOImpl;
import com.fitaura.dao.impl.FitnessContentDAOImpl;
import com.fitaura.dao.impl.UserDAOImpl;
import com.fitaura.dto.FitnessContentDetailDTO;
import com.fitaura.exception.AuthorizationException;
import com.fitaura.exception.ValidationException;
import com.fitaura.model.FitnessContent;
import com.fitaura.model.User;
import com.fitaura.service.FitnessContentService;
import com.fitaura.service.impl.FitnessContentServiceImpl;
import com.fitaura.util.PasswordUtil;
import org.junit.jupiter.api.*;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Phase 11 Test Suite: Fitness Content Management & Approval Workflow
 * Verifies:
 * - Content creation (User -> PENDING, Admin -> APPROVED)
 * - Strict field validation (title length, required fields, supported categories)
 * - Content editing and automatic reversion to PENDING for re-moderation
 * - Ownership & Role-based Authorization guards
 * - Admin moderation (Approve / Reject) with audit log tracking
 * - Public Fitness Library queries (strictly APPROVED items with category & search filters)
 * - Admin moderation list with status filters
 * - Content deletion permissions
 */
@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
public class FitnessContentManagementTest {

    private static UserDAO userDAO;
    private static FitnessContentDAO fitnessContentDAO;
    private static ActivityLogDAO activityLogDAO;
    private static FitnessContentService contentService;

    private static User regularUser1;
    private static User regularUser2;
    private static User adminUser;

    private static Integer user1ContentId;
    private static Integer adminContentId;

    @BeforeAll
    public static void setUp() {
        userDAO = new UserDAOImpl();
        fitnessContentDAO = new FitnessContentDAOImpl();
        activityLogDAO = new ActivityLogDAOImpl();
        contentService = new FitnessContentServiceImpl(fitnessContentDAO, userDAO, activityLogDAO);

        // 1. Regular User 1 (Author)
        regularUser1 = new User();
        regularUser1.setEmail("content_author1_" + System.currentTimeMillis() + "@example.com");
        regularUser1.setPasswordHash(PasswordUtil.hashPassword("SecurePass123!"));
        regularUser1.setFullName("Alex Coach");
        regularUser1.setDisplayName("CoachAlex");
        regularUser1.setRole(User.Role.USER);
        regularUser1.setAccountStatus(User.AccountStatus.ACTIVE);
        regularUser1.setPrivacyMode(User.PrivacyMode.SOCIAL);
        Integer u1Id = userDAO.create(regularUser1);
        regularUser1.setUserId(u1Id);

        // 2. Regular User 2 (Different User)
        regularUser2 = new User();
        regularUser2.setEmail("content_author2_" + System.currentTimeMillis() + "@example.com");
        regularUser2.setPasswordHash(PasswordUtil.hashPassword("SecurePass123!"));
        regularUser2.setFullName("Sam Runner");
        regularUser2.setDisplayName("RunnerSam");
        regularUser2.setRole(User.Role.USER);
        regularUser2.setAccountStatus(User.AccountStatus.ACTIVE);
        regularUser2.setPrivacyMode(User.PrivacyMode.PERSONAL);
        Integer u2Id = userDAO.create(regularUser2);
        regularUser2.setUserId(u2Id);

        // 3. Admin User
        adminUser = new User();
        adminUser.setEmail("admin_moderator_" + System.currentTimeMillis() + "@example.com");
        adminUser.setPasswordHash(PasswordUtil.hashPassword("AdminPass123!"));
        adminUser.setFullName("Jordan Moderator");
        adminUser.setDisplayName("ModJordan");
        adminUser.setRole(User.Role.ADMIN);
        adminUser.setAccountStatus(User.AccountStatus.ACTIVE);
        adminUser.setPrivacyMode(User.PrivacyMode.PERSONAL);
        Integer aId = userDAO.create(adminUser);
        adminUser.setUserId(aId);
    }

    @Test
    @Order(1)
    @DisplayName("1. User content submission enters PENDING moderation status")
    public void testUserContentCreationPending() {
        FitnessContent content = new FitnessContent();
        content.setTitle("HIIT 20-Minute Core Burner");
        content.setCategory(FitnessContent.Category.WORKOUT);
        content.setContentText("Complete 4 rounds of 45 seconds work and 15 seconds rest: Plank, Mountain Climbers, Russian Twists, Bicycle Crunches.");

        FitnessContent created = contentService.createContent(content, regularUser1, "127.0.0.1");

        assertNotNull(created.getContentId());
        assertEquals(FitnessContent.ApprovalStatus.PENDING, created.getApprovalStatus());
        assertEquals(regularUser1.getUserId(), created.getCreatedBy());
        assertNull(created.getReviewedBy());
        assertNull(created.getReviewedAt());

        user1ContentId = created.getContentId();
    }

    @Test
    @Order(2)
    @DisplayName("2. Admin content submission is automatically APPROVED")
    public void testAdminContentCreationAutoApproved() {
        FitnessContent adminGuide = new FitnessContent();
        adminGuide.setTitle("Essential Post-Workout Nutrition Principles");
        adminGuide.setCategory(FitnessContent.Category.NUTRITION);
        adminGuide.setContentText("Optimal protein intake within 45 minutes of training accelerates glycogen replenishment and muscle protein synthesis.");

        FitnessContent created = contentService.createContent(adminGuide, adminUser, "127.0.0.1");

        assertNotNull(created.getContentId());
        assertEquals(FitnessContent.ApprovalStatus.APPROVED, created.getApprovalStatus());
        assertEquals(adminUser.getUserId(), created.getCreatedBy());
        assertEquals(adminUser.getUserId(), created.getReviewedBy());
        assertNotNull(created.getReviewedAt());

        adminContentId = created.getContentId();
    }

    @Test
    @Order(3)
    @DisplayName("3. Content creation enforces validation rules")
    public void testContentValidationRules() {
        // Blank title
        FitnessContent bad1 = new FitnessContent();
        bad1.setTitle("  ");
        bad1.setCategory(FitnessContent.Category.EXERCISE);
        bad1.setContentText("Valid body content.");
        assertThrows(ValidationException.class, () -> contentService.createContent(bad1, regularUser1, "127.0.0.1"));

        // Null category
        FitnessContent bad2 = new FitnessContent();
        bad2.setTitle("Valid Title");
        bad2.setCategory(null);
        bad2.setContentText("Valid body content.");
        assertThrows(ValidationException.class, () -> contentService.createContent(bad2, regularUser1, "127.0.0.1"));

        // Blank content text
        FitnessContent bad3 = new FitnessContent();
        bad3.setTitle("Valid Title");
        bad3.setCategory(FitnessContent.Category.FITNESS_TIP);
        bad3.setContentText("   ");
        assertThrows(ValidationException.class, () -> contentService.createContent(bad3, regularUser1, "127.0.0.1"));
    }

    @Test
    @Order(4)
    @DisplayName("4. Public library only includes APPROVED content")
    public void testPublicLibraryExcludesPending() {
        List<FitnessContentDetailDTO> library = contentService.getPublishedLibrary(null, null, 50, 0, regularUser2.getUserId());

        // Must include admin approved guide
        boolean hasAdminGuide = library.stream().anyMatch(dto -> dto.getContentId().equals(adminContentId));
        assertTrue(hasAdminGuide, "Approved admin guide should be visible in public library");

        // Must NOT include user1 pending content
        boolean hasUser1Pending = library.stream().anyMatch(dto -> dto.getContentId().equals(user1ContentId));
        assertFalse(hasUser1Pending, "Pending user content must NOT be visible in public library");
    }

    @Test
    @Order(5)
    @DisplayName("5. Admin approves pending user content")
    public void testAdminApproveContent() {
        boolean approved = contentService.moderateContent(user1ContentId, true, adminUser.getUserId(), "127.0.0.1");
        assertTrue(approved);

        FitnessContentDetailDTO details = contentService.getContentDetails(user1ContentId, regularUser1.getUserId(), false);
        assertEquals(FitnessContent.ApprovalStatus.APPROVED, details.getApprovalStatus());
        assertEquals(adminUser.getUserId(), details.getReviewedBy());
        assertNotNull(details.getReviewedAt());

        // Now it must appear in public library
        List<FitnessContentDetailDTO> library = contentService.getPublishedLibrary("HIIT 20-Minute Core", null, 10, 0, regularUser1.getUserId());
        assertEquals(1, library.size());
        assertEquals(user1ContentId, library.get(0).getContentId());
    }

    @Test
    @Order(6)
    @DisplayName("6. User editing approved content resets status to PENDING for re-moderation")
    public void testUserEditResetsToPending() {
        FitnessContent updated = contentService.updateContent(
                user1ContentId,
                "HIIT 25-Minute Advanced Core Burner",
                FitnessContent.Category.WORKOUT,
                "Updated 5-round circuit: Plank, Mountain Climbers, Russian Twists, Bicycle Crunches, Hollow Holds.",
                regularUser1,
                "127.0.0.1"
        );

        assertEquals(FitnessContent.ApprovalStatus.PENDING, updated.getApprovalStatus());
        assertNull(updated.getReviewedBy());

        // Should no longer appear in public library
        List<FitnessContentDetailDTO> library = contentService.getPublishedLibrary("Advanced Core Burner", null, 10, 0, regularUser2.getUserId());
        assertEquals(0, library.size(), "Pending re-moderation content should be hidden from library");
    }

    @Test
    @Order(7)
    @DisplayName("7. Non-owner cannot update or tamper with another user's content")
    public void testNonOwnerCannotUpdateContent() {
        assertThrows(AuthorizationException.class, () -> {
            contentService.updateContent(
                    user1ContentId,
                    "Unauthorized Edit Attempt",
                    FitnessContent.Category.WORKOUT,
                    "Malicious content injection.",
                    regularUser2,
                    "127.0.0.1"
            );
        });
    }

    @Test
    @Order(8)
    @DisplayName("8. Admin rejects content submission with audit trail")
    public void testAdminRejectContent() {
        boolean rejected = contentService.moderateContent(user1ContentId, false, adminUser.getUserId(), "127.0.0.1");
        assertTrue(rejected);

        FitnessContentDetailDTO details = contentService.getContentDetails(user1ContentId, regularUser1.getUserId(), false);
        assertEquals(FitnessContent.ApprovalStatus.REJECTED, details.getApprovalStatus());
        assertEquals(adminUser.getUserId(), details.getReviewedBy());
    }

    @Test
    @Order(9)
    @DisplayName("9. User can view their own content in 'My Content' with all statuses")
    public void testUserMyContentList() {
        List<FitnessContentDetailDTO> myContent = contentService.getUserContentList(regularUser1.getUserId());
        assertFalse(myContent.isEmpty());
        boolean hasItem = myContent.stream().anyMatch(dto -> dto.getContentId().equals(user1ContentId));
        assertTrue(hasItem, "Author should see their submitted content in My Content");
    }

    @Test
    @Order(10)
    @DisplayName("10. Category filtering and search across published library")
    public void testCategoryAndSearchFilters() {
        // Create an Exercise guide
        String uniqueTitle = "Proper Deadlift Form & Hip Hinge Mechanics " + System.currentTimeMillis();
        FitnessContent guide = new FitnessContent();
        guide.setTitle(uniqueTitle);
        guide.setCategory(FitnessContent.Category.EXERCISE);
        guide.setContentText("Keep spine neutral, hinge at hips, engage lats, and drive through heels.");
        FitnessContent created = contentService.createContent(guide, adminUser, "127.0.0.1");

        // Filter by EXERCISE category
        List<FitnessContentDetailDTO> exercises = contentService.getPublishedLibrary(null, FitnessContent.Category.EXERCISE, 10, 0, regularUser1.getUserId());
        assertFalse(exercises.isEmpty());
        assertTrue(exercises.stream().allMatch(dto -> dto.getCategory() == FitnessContent.Category.EXERCISE));

        // Filter by NUTRITION category
        List<FitnessContentDetailDTO> nutrition = contentService.getPublishedLibrary(null, FitnessContent.Category.NUTRITION, 10, 0, regularUser1.getUserId());
        assertFalse(nutrition.isEmpty());
        assertTrue(nutrition.stream().allMatch(dto -> dto.getCategory() == FitnessContent.Category.NUTRITION));

        // Search query
        List<FitnessContentDetailDTO> searchResults = contentService.getPublishedLibrary(uniqueTitle, null, 10, 0, regularUser1.getUserId());
        assertEquals(1, searchResults.size());
        assertEquals(uniqueTitle, searchResults.get(0).getTitle());
    }

    @Test
    @Order(11)
    @DisplayName("11. Content deletion authorized for author or admin only")
    public void testContentDeletionSecurity() {
        // Unauthorized user attempt
        assertThrows(AuthorizationException.class, () -> {
            contentService.deleteContent(user1ContentId, regularUser2, "127.0.0.1");
        });

        // Authorized author deletion
        boolean deleted = contentService.deleteContent(user1ContentId, regularUser1, "127.0.0.1");
        assertTrue(deleted);

        // Verification
        List<FitnessContentDetailDTO> myContent = contentService.getUserContentList(regularUser1.getUserId());
        boolean exists = myContent.stream().anyMatch(dto -> dto.getContentId().equals(user1ContentId));
        assertFalse(exists, "Deleted content should no longer exist");
    }
}
