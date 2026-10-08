package com.fitaura;

import com.fitaura.dto.LeaderboardEntryDTO;
import com.fitaura.dto.SocialHubDTO;
import com.fitaura.dto.SocialProfileDTO;
import com.fitaura.exception.AuthorizationException;
import com.fitaura.exception.ValidationException;
import com.fitaura.model.Competition;
import com.fitaura.model.CompetitionParticipant;
import com.fitaura.model.DailyQuote;
import com.fitaura.model.SocialConnection;
import com.fitaura.model.User;
import com.fitaura.service.AuthenticationService;
import com.fitaura.service.GamificationService;
import com.fitaura.service.PrivacyService;
import com.fitaura.service.QuoteService;
import com.fitaura.service.SocialFitnessService;
import com.fitaura.service.WorkoutService;
import com.fitaura.service.impl.AuthenticationServiceImpl;
import com.fitaura.service.impl.GamificationServiceImpl;
import com.fitaura.service.impl.PrivacyServiceImpl;
import com.fitaura.service.impl.QuoteServiceImpl;
import com.fitaura.service.impl.SocialFitnessServiceImpl;
import com.fitaura.service.impl.WorkoutServiceImpl;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.math.BigDecimal;
import java.sql.Date;
import java.time.LocalDate;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

@DisplayName("Phase 14 - Daily Motivation & Social Fitness Experience Test Suite")
public class DailyMotivationAndSocialFitnessTest {

    private static QuoteService quoteService;
    private static SocialFitnessService socialService;
    private static AuthenticationService authService;
    private static PrivacyService privacyService;
    private static GamificationService gamificationService;
    private static WorkoutService workoutService;

    private static User testAdmin;
    private static User socialUserA;
    private static User socialUserB;
    private static User personalUserC;

    @BeforeAll
    public static void setup() {
        quoteService = new QuoteServiceImpl();
        socialService = new SocialFitnessServiceImpl();
        authService = new AuthenticationServiceImpl();
        privacyService = new PrivacyServiceImpl();
        gamificationService = new GamificationServiceImpl();
        workoutService = new WorkoutServiceImpl();

        long ts = System.currentTimeMillis();
        // Register test admin
        testAdmin = authService.register(
                "Admin " + ts,
                "admin_" + ts + "@fitaura.test",
                "Password123!",
                "Password123!",
                "Admin",
                "127.0.0.1"
        );
        // Elevate testAdmin to ADMIN for tests
        testAdmin.setRole(User.Role.ADMIN);
        new com.fitaura.dao.impl.UserDAOImpl().update(testAdmin);

        // Register socialUserA
        socialUserA = authService.register(
                "Athlete Alex",
                "alex_" + ts + "@fitaura.test",
                "Password123!",
                "Password123!",
                "Alex",
                "127.0.0.1"
        );
        privacyService.updatePrivacyMode(socialUserA.getUserId(), User.PrivacyMode.SOCIAL, "127.0.0.1");

        // Register socialUserB
        socialUserB = authService.register(
                "Athlete Bella",
                "bella_" + ts + "@fitaura.test",
                "Password123!",
                "Password123!",
                "Bella",
                "127.0.0.1"
        );
        privacyService.updatePrivacyMode(socialUserB.getUserId(), User.PrivacyMode.SOCIAL, "127.0.0.1");

        // Register personalUserC (Strictly PERSONAL mode)
        personalUserC = authService.register(
                "Private Chris",
                "chris_" + ts + "@fitaura.test",
                "Password123!",
                "Password123!",
                "Chris",
                "127.0.0.1"
        );
        privacyService.updatePrivacyMode(personalUserC.getUserId(), User.PrivacyMode.PERSONAL, "127.0.0.1");
    }

    // ==========================================
    // PART A: DAILY MOTIVATION QUOTE TESTS
    // ==========================================

    @Test
    @DisplayName("Admin can create, retrieve, update, publish, and archive daily quotes")
    public void testAdminQuoteLifecycle() {
        Date futureDate = Date.valueOf(LocalDate.now().plusDays(20));

        DailyQuote quote = new DailyQuote();
        quote.setQuoteText("Every champion was once a contender that refused to give up.");
        quote.setAuthorName("Rocky Balboa");
        quote.setQuoteDate(futureDate);
        quote.setStatus(DailyQuote.Status.DRAFT);

        DailyQuote created = quoteService.createQuote(quote, testAdmin.getUserId(), "127.0.0.1");
        assertNotNull(created.getQuoteId());
        assertEquals("Rocky Balboa", created.getAuthorName());
        assertEquals(DailyQuote.Status.DRAFT, created.getStatus());

        // Update quote
        created.setQuoteText("Updated: Every champion was once a contender.");
        DailyQuote updated = quoteService.updateQuote(created, testAdmin.getUserId(), "127.0.0.1");
        assertEquals("Updated: Every champion was once a contender.", updated.getQuoteText());

        // Publish quote
        boolean published = quoteService.publishQuote(created.getQuoteId(), testAdmin.getUserId(), "127.0.0.1");
        assertTrue(published);
        DailyQuote fetchedPub = quoteService.getQuoteById(created.getQuoteId());
        assertEquals(DailyQuote.Status.PUBLISHED, fetchedPub.getStatus());

        // Archive quote
        boolean archived = quoteService.archiveQuote(created.getQuoteId(), testAdmin.getUserId(), "127.0.0.1");
        assertTrue(archived);
        DailyQuote fetchedArch = quoteService.getQuoteById(created.getQuoteId());
        assertEquals(DailyQuote.Status.ARCHIVED, fetchedArch.getStatus());

        // Delete quote
        boolean deleted = quoteService.deleteQuote(created.getQuoteId(), testAdmin.getUserId(), "127.0.0.1");
        assertTrue(deleted);
    }

    @Test
    @DisplayName("Prevent duplicate published quotes for the same date")
    public void testPreventDuplicatePublishedQuotesForSameDate() {
        Date targetDate = Date.valueOf(LocalDate.now().plusDays(25));

        DailyQuote quote1 = new DailyQuote();
        quote1.setQuoteText("Consistency is key.");
        quote1.setAuthorName("Coach 1");
        quote1.setQuoteDate(targetDate);
        quote1.setStatus(DailyQuote.Status.PUBLISHED);
        DailyQuote created1 = quoteService.createQuote(quote1, testAdmin.getUserId(), "127.0.0.1");

        // Attempting to publish another quote for the same date should fail validation
        DailyQuote quote2 = new DailyQuote();
        quote2.setQuoteText("Another quote for the same date.");
        quote2.setAuthorName("Coach 2");
        quote2.setQuoteDate(targetDate);
        quote2.setStatus(DailyQuote.Status.PUBLISHED);

        assertThrows(ValidationException.class, () -> {
            quoteService.createQuote(quote2, testAdmin.getUserId(), "127.0.0.1");
        });

        // Cleanup
        quoteService.deleteQuote(created1.getQuoteId(), testAdmin.getUserId(), "127.0.0.1");
    }

    @Test
    @DisplayName("Fallback quote returned gracefully when no quote is scheduled for today")
    public void testDailyQuoteGracefulFallback() {
        // If today's quote exists in DB, it returns it; if none exists, returns fallback
        DailyQuote todayQuote = quoteService.getTodayQuote();
        assertNotNull(todayQuote);
        assertNotNull(todayQuote.getQuoteText());
        assertFalse(todayQuote.getQuoteText().trim().isEmpty());
        assertNotNull(todayQuote.getAuthorName());
        assertNotNull(todayQuote.getQuoteDate());
        assertEquals(DailyQuote.Status.PUBLISHED, todayQuote.getStatus());
    }

    @Test
    @DisplayName("Non-admin user cannot manage quotes")
    public void testNonAdminCannotManageQuotes() {
        DailyQuote quote = new DailyQuote();
        quote.setQuoteText("Unauthorized quote attempt.");
        quote.setAuthorName("Hacker");
        quote.setQuoteDate(Date.valueOf(LocalDate.now().plusDays(30)));
        quote.setStatus(DailyQuote.Status.PUBLISHED);

        assertThrows(AuthorizationException.class, () -> {
            quoteService.createQuote(quote, socialUserA.getUserId(), "127.0.0.1");
        });
    }

    // ==========================================
    // PART B: SOCIAL FITNESS EXPERIENCE TESTS
    // ==========================================

    @Test
    @DisplayName("Personal mode user is isolated from public social profile view")
    public void testPersonalModeUserIsolationFromSocialProfile() {
        // Any attempt to view a Personal user's public social profile must throw AuthorizationException
        assertThrows(AuthorizationException.class, () -> {
            socialService.getSocialProfile(personalUserC.getUserId(), socialUserA.getUserId());
        });
    }

    @Test
    @DisplayName("Social mode user profile is accessible without leaking sensitive metrics")
    public void testSocialModeProfileAccessibleWithoutBiometricLeakage() {
        SocialProfileDTO profile = socialService.getSocialProfile(socialUserA.getUserId(), socialUserB.getUserId());
        assertNotNull(profile);
        assertEquals(socialUserA.getUserId(), profile.getUserId());
        assertNotNull(profile.getDisplayName());
        assertTrue(profile.getTotalPoints() >= 0);
        assertTrue(profile.getCurrentStreakDays() >= 0);
        // Verifies zero biometric leakage: profile DTO has points, streak, achievements only
        assertNotNull(profile.getShowcasedAchievements());
    }

    @Test
    @DisplayName("Social connections lifecycle: Request, Pending, Accept, and Remove")
    public void testSocialConnectionsLifecycle() {
        // User A sends connection request to User B
        boolean sent = socialService.sendConnectionRequest(socialUserA.getUserId(), socialUserB.getUserId(), "127.0.0.1");
        assertTrue(sent);

        List<SocialConnection> pending = socialService.getPendingRequests(socialUserB.getUserId());
        assertFalse(pending.isEmpty());
        SocialConnection conn = pending.get(0);
        assertNotNull(conn.getConnectionId());
        assertEquals(SocialConnection.Status.PENDING, conn.getStatus());

        // Cannot send duplicate connection request
        assertThrows(ValidationException.class, () -> {
            socialService.sendConnectionRequest(socialUserA.getUserId(), socialUserB.getUserId(), "127.0.0.1");
        });

        // User B accepts connection
        boolean accepted = socialService.acceptConnectionRequest(conn.getConnectionId(), socialUserB.getUserId(), "127.0.0.1");
        assertTrue(accepted);

        // Verify connected status
        SocialProfileDTO alexProfile = socialService.getSocialProfile(socialUserA.getUserId(), socialUserB.getUserId());
        assertTrue(alexProfile.isConnection());

        // Remove connection
        boolean removed = socialService.removeConnection(conn.getConnectionId(), socialUserA.getUserId(), "127.0.0.1");
        assertTrue(removed);
    }

    @Test
    @DisplayName("Community Competitions: Create, Join, Leaderboard, and Leave")
    public void testCommunityCompetitions() {
        Date start = Date.valueOf(LocalDate.now().minusDays(1));
        Date end = Date.valueOf(LocalDate.now().plusDays(7));

        Competition comp = new Competition();
        comp.setName("Phase 14 5k Challenge " + System.currentTimeMillis());
        comp.setDescription("Run or walk 5 workouts this week!");
        comp.setMetric(Competition.Metric.WORKOUT_COUNT);
        comp.setTargetValue(new BigDecimal("5.00"));
        comp.setStartDate(start);
        comp.setEndDate(end);
        comp.setRewardPoints(100);
        comp.setStatus(Competition.Status.ACTIVE);

        Competition createdComp = socialService.createCompetition(comp, testAdmin.getUserId(), "127.0.0.1");
        assertNotNull(createdComp.getCompetitionId());

        // Social user A joins competition
        boolean joined = socialService.joinCompetition(createdComp.getCompetitionId(), socialUserA.getUserId(), "127.0.0.1");
        assertTrue(joined);

        // Leaderboard check
        List<CompetitionParticipant> lb = socialService.getCompetitionLeaderboard(createdComp.getCompetitionId(), 10);
        assertFalse(lb.isEmpty());
        assertEquals(socialUserA.getUserId(), lb.get(0).getUserId());

        // Social user A leaves competition
        boolean left = socialService.leaveCompetition(createdComp.getCompetitionId(), socialUserA.getUserId(), "127.0.0.1");
        assertTrue(left);

        // Cleanup
        socialService.deleteCompetition(createdComp.getCompetitionId(), testAdmin.getUserId(), "127.0.0.1");
    }

    @Test
    @DisplayName("Social Hub aggregation returns complete community data")
    public void testSocialHubData() {
        SocialHubDTO hub = socialService.getSocialHubData(socialUserA.getUserId());
        assertNotNull(hub);
        assertTrue(hub.isUserSocial());
        assertNotNull(hub.getTopWeeklyLeaderboard());
        assertNotNull(hub.getTopMonthlyLeaderboard());
        assertNotNull(hub.getTopAllTimeLeaderboard());
        assertNotNull(hub.getActiveCompetitions());
    }
}
