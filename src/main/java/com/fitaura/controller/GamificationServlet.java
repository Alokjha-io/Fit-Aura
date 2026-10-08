package com.fitaura.controller;

import com.fitaura.dto.GamificationDashboardDTO;
import com.fitaura.service.GamificationService;
import com.fitaura.service.impl.GamificationServiceImpl;
import com.fitaura.util.SessionUtil;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;

/**
 * Controller serving the Gamification module:
 * - Points summary and transaction history
 * - Consecutive workout streaks & streak status
 * - Unlocked achievements & in-progress milestones
 * - Quick preview of social leaderboard rank
 */
@WebServlet(name = "GamificationServlet", urlPatterns = {"/gamification", "/gamification/*", "/points", "/achievements"})
public class GamificationServlet extends HttpServlet {

    private static final long serialVersionUID = 1L;
    private static final Logger logger = LoggerFactory.getLogger(GamificationServlet.class);

    private final GamificationService gamificationService;

    public GamificationServlet() {
        this(new GamificationServiceImpl());
    }

    public GamificationServlet(GamificationService gamificationService) {
        this.gamificationService = gamificationService;
    }

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        Integer userId = SessionUtil.getAuthenticatedUserId(request);
        if (userId == null) {
            response.sendRedirect(request.getContextPath() + "/login");
            return;
        }

        try {
            // Trigger milestone evaluation to ensure any newly reached goals/streaks are rewarded
            gamificationService.checkAndAwardMilestones(userId);

            GamificationDashboardDTO dashboard = gamificationService.getGamificationDashboard(userId);
            request.setAttribute("dashboard", dashboard);

            request.getRequestDispatcher("/gamification/index.jsp").forward(request, response);

        } catch (Exception e) {
            logger.error("Error loading gamification dashboard for user {}: {}", userId, e.getMessage(), e);
            request.setAttribute("errorMessage", "Unable to load gamification metrics. Please try again later.");
            request.setAttribute("dashboard", new GamificationDashboardDTO());
            request.getRequestDispatcher("/gamification/index.jsp").forward(request, response);
        }
    }
}
