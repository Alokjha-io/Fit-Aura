package com.fitaura.controller;

import com.fitaura.dto.ChallengeDetailDTO;
import com.fitaura.dto.ChallengeSummaryDTO;
import com.fitaura.exception.ValidationException;
import com.fitaura.model.Challenge;
import com.fitaura.model.User;
import com.fitaura.service.ChallengeService;
import com.fitaura.service.impl.ChallengeServiceImpl;
import com.fitaura.util.CsrfUtil;
import com.fitaura.util.SessionUtil;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.math.BigDecimal;
import java.sql.Date;
import java.time.LocalDate;
import java.util.List;

/**
 * Controller handling Challenge & Community operations:
 * - Discovery & Filtering (/challenges)
 * - Challenge Details & Community Participants (/challenges/view)
 * - Challenge Creation (/challenges/create)
 * - Joining & Leaving Challenges (/challenges/join, /challenges/leave)
 * - Progress Sync & Contributions (/challenges/sync, /challenges/progress)
 * - User Challenge Portfolio (/challenges/my)
 * - Administrative Moderation (/challenges/admin/moderation, /challenges/moderate)
 */
@WebServlet(name = "ChallengeServlet", urlPatterns = {
        "/challenges",
        "/challenges/view",
        "/challenges/create",
        "/challenges/join",
        "/challenges/leave",
        "/challenges/sync",
        "/challenges/progress",
        "/challenges/my",
        "/challenges/admin/moderation",
        "/challenges/moderate",
        "/admin/challenges"
})
public class ChallengeServlet extends HttpServlet {

    private static final long serialVersionUID = 1L;
    private static final Logger logger = LoggerFactory.getLogger(ChallengeServlet.class);

    private final ChallengeService challengeService;

    public ChallengeServlet() {
        this(new ChallengeServiceImpl());
    }

    public ChallengeServlet(ChallengeService challengeService) {
        this.challengeService = challengeService;
    }

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        String servletPath = request.getServletPath();
        Integer userId = SessionUtil.getAuthenticatedUserId(request);
        User currentUser = SessionUtil.getAuthenticatedUser(request);

        if (userId == null || currentUser == null) {
            response.sendRedirect(request.getContextPath() + "/login");
            return;
        }

        switch (servletPath) {
            case "/challenges/view":
                handleViewChallenge(request, response, userId);
                break;
            case "/challenges/create":
                handleCreatePage(request, response);
                break;
            case "/challenges/my":
                handleMyChallenges(request, response, userId);
                break;
            case "/challenges/admin/moderation":
            case "/admin/challenges":
                handleAdminModerationPage(request, response, currentUser);
                break;
            case "/challenges":
            default:
                handleListChallenges(request, response, userId);
                break;
        }
    }

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        Integer userId = SessionUtil.getAuthenticatedUserId(request);
        User currentUser = SessionUtil.getAuthenticatedUser(request);

        if (userId == null || currentUser == null) {
            response.sendRedirect(request.getContextPath() + "/login");
            return;
        }

        // Validate CSRF token for all state-changing actions
        if (!CsrfUtil.isValidToken(request)) {
            logger.warn("CSRF token validation failed for user {} on {}", userId, request.getServletPath());
            response.sendError(HttpServletResponse.SC_FORBIDDEN, "Security validation failed. Please refresh and try again.");
            return;
        }

        String servletPath = request.getServletPath();

        switch (servletPath) {
            case "/challenges/create":
                handleCreateSubmit(request, response, currentUser);
                break;
            case "/challenges/join":
                handleJoinSubmit(request, response, userId);
                break;
            case "/challenges/leave":
                handleLeaveSubmit(request, response, userId);
                break;
            case "/challenges/sync":
                handleSyncSubmit(request, response, userId);
                break;
            case "/challenges/progress":
                handleProgressSubmit(request, response, userId);
                break;
            case "/challenges/moderate":
                handleModerateSubmit(request, response, currentUser);
                break;
            default:
                response.sendRedirect(request.getContextPath() + "/challenges");
                break;
        }
    }

    private void handleListChallenges(HttpServletRequest request, HttpServletResponse response, Integer userId)
            throws ServletException, IOException {
        String filter = request.getParameter("filter");
        if (filter == null || filter.trim().isEmpty()) {
            filter = "ACTIVE";
        }

        List<ChallengeSummaryDTO> challenges = challengeService.getDiscoverableChallenges(filter, userId);
        request.setAttribute("challenges", challenges);
        request.setAttribute("currentFilter", filter);

        request.getRequestDispatcher("/challenges/list.jsp").forward(request, response);
    }

    private void handleViewChallenge(HttpServletRequest request, HttpServletResponse response, Integer userId)
            throws ServletException, IOException {
        String idParam = request.getParameter("id");
        if (idParam == null || idParam.trim().isEmpty()) {
            response.sendRedirect(request.getContextPath() + "/challenges");
            return;
        }

        try {
            int challengeId = Integer.parseInt(idParam);
            ChallengeDetailDTO detail = challengeService.getChallengeDetails(challengeId, userId);

            if (detail == null) {
                request.setAttribute("errorMessage", "The requested challenge was not found.");
                request.getRequestDispatcher("/error/404.jsp").forward(request, response);
                return;
            }

            request.setAttribute("detail", detail);
            request.getRequestDispatcher("/challenges/view.jsp").forward(request, response);
        } catch (NumberFormatException e) {
            response.sendRedirect(request.getContextPath() + "/challenges");
        }
    }

    private void handleCreatePage(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        // Provide default start/end dates
        LocalDate today = LocalDate.now();
        request.setAttribute("defaultStartDate", today.toString());
        request.setAttribute("defaultEndDate", today.plusDays(30).toString());
        request.getRequestDispatcher("/challenges/create.jsp").forward(request, response);
    }

    private void handleCreateSubmit(HttpServletRequest request, HttpServletResponse response, User currentUser)
            throws ServletException, IOException {
        String name = request.getParameter("name");
        String description = request.getParameter("description");
        String goalValueStr = request.getParameter("goalValue");
        String goalUnit = request.getParameter("goalUnit");
        String pointsRewardStr = request.getParameter("pointsReward");
        String startDateStr = request.getParameter("startDate");
        String endDateStr = request.getParameter("endDate");

        Challenge challenge = new Challenge();
        try {
            challenge.setName(name);
            challenge.setDescription(description);

            if (goalValueStr != null && !goalValueStr.trim().isEmpty()) {
                challenge.setGoalValue(new BigDecimal(goalValueStr.trim()));
            }
            challenge.setGoalUnit(goalUnit);

            if (pointsRewardStr != null && !pointsRewardStr.trim().isEmpty()) {
                challenge.setPointsReward(Integer.parseInt(pointsRewardStr.trim()));
            }

            if (startDateStr != null && !startDateStr.trim().isEmpty()) {
                challenge.setStartDate(Date.valueOf(startDateStr.trim()));
            }
            if (endDateStr != null && !endDateStr.trim().isEmpty()) {
                challenge.setEndDate(Date.valueOf(endDateStr.trim()));
            }

            Challenge created = challengeService.createChallenge(challenge, currentUser);

            if (currentUser.getRole() == User.Role.ADMIN) {
                request.getSession().setAttribute("feedbackMessage", "Challenge '" + created.getName() + "' published successfully!");
                response.sendRedirect(request.getContextPath() + "/challenges/view?id=" + created.getChallengeId());
            } else {
                request.getSession().setAttribute("feedbackMessage",
                        "Challenge '" + created.getName() + "' submitted for moderation. It will become discoverable once approved by an administrator.");
                response.sendRedirect(request.getContextPath() + "/challenges/my");
            }
        } catch (ValidationException e) {
            request.setAttribute("errorMessage", e.getMessage());
            request.setAttribute("challenge", challenge);
            request.setAttribute("defaultStartDate", startDateStr);
            request.setAttribute("defaultEndDate", endDateStr);
            request.getRequestDispatcher("/challenges/create.jsp").forward(request, response);
        } catch (Exception e) {
            logger.error("Error creating challenge: {}", e.getMessage());
            request.setAttribute("errorMessage", "An error occurred while creating the challenge. Please verify your inputs.");
            request.setAttribute("challenge", challenge);
            request.getRequestDispatcher("/challenges/create.jsp").forward(request, response);
        }
    }

    private void handleJoinSubmit(HttpServletRequest request, HttpServletResponse response, Integer userId)
            throws ServletException, IOException {
        String idParam = request.getParameter("challengeId");
        if (idParam == null) {
            response.sendRedirect(request.getContextPath() + "/challenges");
            return;
        }

        try {
            int challengeId = Integer.parseInt(idParam);
            challengeService.joinChallenge(challengeId, userId);
            request.getSession().setAttribute("feedbackMessage", "You have successfully joined the challenge!");
            response.sendRedirect(request.getContextPath() + "/challenges/view?id=" + challengeId);
        } catch (ValidationException e) {
            request.getSession().setAttribute("errorMessage", e.getMessage());
            response.sendRedirect(request.getContextPath() + "/challenges/view?id=" + idParam);
        } catch (Exception e) {
            logger.error("Error joining challenge {}: {}", idParam, e.getMessage());
            request.getSession().setAttribute("errorMessage", "Unable to join challenge. Please try again.");
            response.sendRedirect(request.getContextPath() + "/challenges");
        }
    }

    private void handleLeaveSubmit(HttpServletRequest request, HttpServletResponse response, Integer userId)
            throws ServletException, IOException {
        String idParam = request.getParameter("challengeId");
        if (idParam == null) {
            response.sendRedirect(request.getContextPath() + "/challenges");
            return;
        }

        try {
            int challengeId = Integer.parseInt(idParam);
            challengeService.leaveChallenge(challengeId, userId);
            request.getSession().setAttribute("feedbackMessage", "You have left the challenge.");
            response.sendRedirect(request.getContextPath() + "/challenges/view?id=" + challengeId);
        } catch (ValidationException e) {
            request.getSession().setAttribute("errorMessage", e.getMessage());
            response.sendRedirect(request.getContextPath() + "/challenges/view?id=" + idParam);
        } catch (Exception e) {
            logger.error("Error leaving challenge {}: {}", idParam, e.getMessage());
            request.getSession().setAttribute("errorMessage", "Unable to leave challenge. Please try again.");
            response.sendRedirect(request.getContextPath() + "/challenges");
        }
    }

    private void handleSyncSubmit(HttpServletRequest request, HttpServletResponse response, Integer userId)
            throws ServletException, IOException {
        String idParam = request.getParameter("challengeId");
        if (idParam == null) {
            response.sendRedirect(request.getContextPath() + "/challenges");
            return;
        }

        try {
            int challengeId = Integer.parseInt(idParam);
            challengeService.syncUserProgress(challengeId, userId);
            request.getSession().setAttribute("feedbackMessage", "Your challenge progress has been synchronized with your logged workouts.");
            response.sendRedirect(request.getContextPath() + "/challenges/view?id=" + challengeId);
        } catch (Exception e) {
            logger.error("Error syncing challenge progress {}: {}", idParam, e.getMessage());
            request.getSession().setAttribute("errorMessage", "Unable to sync progress right now.");
            response.sendRedirect(request.getContextPath() + "/challenges/view?id=" + idParam);
        }
    }

    private void handleProgressSubmit(HttpServletRequest request, HttpServletResponse response, Integer userId)
            throws ServletException, IOException {
        String idParam = request.getParameter("challengeId");
        String valueParam = request.getParameter("addedValue");

        if (idParam == null || valueParam == null) {
            response.sendRedirect(request.getContextPath() + "/challenges");
            return;
        }

        try {
            int challengeId = Integer.parseInt(idParam);
            BigDecimal addedValue = new BigDecimal(valueParam.trim());
            challengeService.logManualProgress(challengeId, userId, addedValue);
            request.getSession().setAttribute("feedbackMessage", "Progress updated successfully!");
            response.sendRedirect(request.getContextPath() + "/challenges/view?id=" + challengeId);
        } catch (ValidationException e) {
            request.getSession().setAttribute("errorMessage", e.getMessage());
            response.sendRedirect(request.getContextPath() + "/challenges/view?id=" + idParam);
        } catch (Exception e) {
            logger.error("Error updating manual challenge progress: {}", e.getMessage());
            request.getSession().setAttribute("errorMessage", "Invalid progress value provided.");
            response.sendRedirect(request.getContextPath() + "/challenges/view?id=" + idParam);
        }
    }

    private void handleMyChallenges(HttpServletRequest request, HttpServletResponse response, Integer userId)
            throws ServletException, IOException {
        List<ChallengeSummaryDTO> inProgress = challengeService.getUserParticipatingChallenges(userId, "IN_PROGRESS");
        List<ChallengeSummaryDTO> completed = challengeService.getUserParticipatingChallenges(userId, "COMPLETED");
        List<Challenge> created = challengeService.getUserCreatedChallenges(userId);

        request.setAttribute("inProgressChallenges", inProgress);
        request.setAttribute("completedChallenges", completed);
        request.setAttribute("createdChallenges", created);

        request.getRequestDispatcher("/challenges/my.jsp").forward(request, response);
    }

    private void handleAdminModerationPage(HttpServletRequest request, HttpServletResponse response, User currentUser)
            throws ServletException, IOException {
        if (currentUser.getRole() != User.Role.ADMIN) {
            response.setStatus(HttpServletResponse.SC_FORBIDDEN);
            request.getRequestDispatcher("/error/unauthorized.jsp").forward(request, response);
            return;
        }

        List<Challenge> pending = challengeService.getPendingModerationChallenges(currentUser);
        request.setAttribute("pendingChallenges", pending);
        request.getRequestDispatcher("/admin/challenges.jsp").forward(request, response);
    }

    private void handleModerateSubmit(HttpServletRequest request, HttpServletResponse response, User currentUser)
            throws ServletException, IOException {
        if (currentUser.getRole() != User.Role.ADMIN) {
            response.setStatus(HttpServletResponse.SC_FORBIDDEN);
            request.getRequestDispatcher("/error/unauthorized.jsp").forward(request, response);
            return;
        }

        String idParam = request.getParameter("challengeId");
        String action = request.getParameter("action"); // APPROVE, REJECT, CANCEL

        if (idParam == null || action == null) {
            response.sendRedirect(request.getContextPath() + "/admin/challenges");
            return;
        }

        try {
            int challengeId = Integer.parseInt(idParam);
            challengeService.moderateChallenge(challengeId, action, currentUser);
            request.getSession().setAttribute("feedbackMessage", "Challenge ID #" + challengeId + " successfully updated (" + action + ").");
        } catch (ValidationException e) {
            request.getSession().setAttribute("errorMessage", e.getMessage());
        } catch (Exception e) {
            logger.error("Error moderating challenge: {}", e.getMessage());
            request.getSession().setAttribute("errorMessage", "Failed to update challenge moderation status.");
        }

        response.sendRedirect(request.getContextPath() + "/admin/challenges");
    }
}
