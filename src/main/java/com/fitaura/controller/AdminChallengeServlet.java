package com.fitaura.controller;

import com.fitaura.dao.ChallengeDAO;
import com.fitaura.dao.impl.ChallengeDAOImpl;
import com.fitaura.exception.FitAuraException;
import com.fitaura.model.Challenge;
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
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.util.List;

/**
 * Controller serving administrative Challenge Moderation & Management:
 * - Review pending user-created challenges
 * - Approve, reject, and cancel challenges
 * - CSRF verification and audit logging
 */
@WebServlet(name = "AdminChallengeServlet", urlPatterns = {"/admin/challenges", "/admin/challenges/*"})
public class AdminChallengeServlet extends HttpServlet {

    private static final long serialVersionUID = 1L;
    private static final Logger logger = LoggerFactory.getLogger(AdminChallengeServlet.class);

    private final ChallengeService challengeService;
    private final ChallengeDAO challengeDAO;

    public AdminChallengeServlet() {
        this(new ChallengeServiceImpl(), new ChallengeDAOImpl());
    }

    public AdminChallengeServlet(ChallengeService challengeService, ChallengeDAO challengeDAO) {
        this.challengeService = challengeService;
        this.challengeDAO = challengeDAO;
    }

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        if (!SessionUtil.isAuthenticated(request) || !SessionUtil.isAdmin(request)) {
            response.setStatus(HttpServletResponse.SC_FORBIDDEN);
            request.getRequestDispatcher("/error/unauthorized.jsp").forward(request, response);
            return;
        }

        try {
            String statusFilter = request.getParameter("status");
            List<Challenge> challenges;

            if (statusFilter != null && !statusFilter.trim().isEmpty() && !"ALL".equalsIgnoreCase(statusFilter)) {
                try {
                    Challenge.ChallengeStatus status = Challenge.ChallengeStatus.valueOf(statusFilter.trim().toUpperCase());
                    challenges = challengeDAO.findByStatus(status);
                } catch (IllegalArgumentException e) {
                    challenges = challengeDAO.findAll();
                }
            } else {
                challenges = challengeDAO.findAll();
            }

            int pendingCount = challengeDAO.countPendingModeration();
            int activeCount = challengeDAO.countActive();

            request.setAttribute("challenges", challenges);
            request.setAttribute("selectedStatus", statusFilter != null ? statusFilter : "ALL");
            request.setAttribute("pendingCount", pendingCount);
            request.setAttribute("activeCount", activeCount);

            request.getRequestDispatcher("/admin/challenges.jsp").forward(request, response);

        } catch (Exception e) {
            logger.error("Error loading admin challenges: {}", e.getMessage(), e);
            request.setAttribute("errorMessage", "Unable to load challenges for moderation.");
            request.getRequestDispatcher("/admin/challenges.jsp").forward(request, response);
        }
    }

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        if (!SessionUtil.isAuthenticated(request) || !SessionUtil.isAdmin(request)) {
            response.setStatus(HttpServletResponse.SC_FORBIDDEN);
            request.getRequestDispatcher("/error/unauthorized.jsp").forward(request, response);
            return;
        }

        if (!CsrfUtil.isValidToken(request)) {
            logger.warn("CSRF token validation failed in AdminChallengeServlet from IP {}", request.getRemoteAddr());
            response.sendRedirect(request.getContextPath() + "/admin/challenges?error=" +
                    URLEncoder.encode("Security validation failed. Please try again.", StandardCharsets.UTF_8));
            return;
        }

        String pathInfo = request.getPathInfo();
        String challengeIdStr = request.getParameter("challengeId");
        Integer adminUserId = SessionUtil.getAuthenticatedUserId(request);

        if (challengeIdStr == null || challengeIdStr.trim().isEmpty()) {
            response.sendRedirect(request.getContextPath() + "/admin/challenges?error=" +
                    URLEncoder.encode("Missing challenge ID.", StandardCharsets.UTF_8));
            return;
        }

        try {
            int challengeId = Integer.parseInt(challengeIdStr.trim());

            if ("/approve".equals(pathInfo)) {
                challengeService.moderateChallenge(challengeId, "APPROVE", adminUserId);
                response.sendRedirect(request.getContextPath() + "/admin/challenges?success=" +
                        URLEncoder.encode("Challenge #" + challengeId + " approved and activated successfully.", StandardCharsets.UTF_8));
            } else if ("/reject".equals(pathInfo)) {
                challengeService.moderateChallenge(challengeId, "REJECT", adminUserId);
                response.sendRedirect(request.getContextPath() + "/admin/challenges?success=" +
                        URLEncoder.encode("Challenge #" + challengeId + " has been rejected.", StandardCharsets.UTF_8));
            } else if ("/cancel".equals(pathInfo)) {
                challengeService.moderateChallenge(challengeId, "CANCEL", adminUserId);
                response.sendRedirect(request.getContextPath() + "/admin/challenges?success=" +
                        URLEncoder.encode("Challenge #" + challengeId + " has been cancelled.", StandardCharsets.UTF_8));
            } else {
                response.sendRedirect(request.getContextPath() + "/admin/challenges");
            }

        } catch (NumberFormatException e) {
            response.sendRedirect(request.getContextPath() + "/admin/challenges?error=" +
                    URLEncoder.encode("Invalid challenge identifier.", StandardCharsets.UTF_8));
        } catch (FitAuraException e) {
            response.sendRedirect(request.getContextPath() + "/admin/challenges?error=" +
                    URLEncoder.encode(e.getMessage(), StandardCharsets.UTF_8));
        } catch (Exception e) {
            logger.error("Error moderating challenge: {}", e.getMessage(), e);
            response.sendRedirect(request.getContextPath() + "/admin/challenges?error=" +
                    URLEncoder.encode("Failed to complete challenge moderation.", StandardCharsets.UTF_8));
        }
    }
}
