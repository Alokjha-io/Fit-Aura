package com.fitaura.controller;

import com.fitaura.dto.LeaderboardEntryDTO;
import com.fitaura.dto.SocialHubDTO;
import com.fitaura.dto.SocialProfileDTO;
import com.fitaura.model.Competition;
import com.fitaura.model.CompetitionParticipant;
import com.fitaura.model.User;
import com.fitaura.service.SocialFitnessService;
import com.fitaura.service.UserService;
import com.fitaura.service.impl.SocialFitnessServiceImpl;
import com.fitaura.service.impl.UserServiceImpl;
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
import java.util.List;

@WebServlet(name = "SocialServlet", urlPatterns = {"/social", "/social/*"})
public class SocialServlet extends HttpServlet {

    private static final long serialVersionUID = 1L;
    private static final Logger logger = LoggerFactory.getLogger(SocialServlet.class);

    private SocialFitnessService socialService;
    private UserService userService;

    @Override
    public void init() throws ServletException {
        super.init();
        this.socialService = new SocialFitnessServiceImpl();
        this.userService = new UserServiceImpl();
    }

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        Integer currentUserId = SessionUtil.getAuthenticatedUserId(request);
        String pathInfo = request.getPathInfo();

        try {
            if (pathInfo != null && pathInfo.startsWith("/competition")) {
                handleCompetitionDetail(request, response, currentUserId);
                return;
            }

            if (pathInfo != null && pathInfo.startsWith("/profile")) {
                handleSocialProfile(request, response, currentUserId);
                return;
            }

            if (pathInfo != null && pathInfo.startsWith("/leaderboard")) {
                handleLeaderboardView(request, response, currentUserId);
                return;
            }

            // Default: Social Hub
            SocialHubDTO hub = socialService.getSocialHubData(currentUserId);
            User currentUser = userService.getUserById(currentUserId);

            request.setAttribute("hub", hub);
            request.setAttribute("currentUser", currentUser);
            request.getRequestDispatcher("/social/hub.jsp").forward(request, response);

        } catch (Exception e) {
            logger.error("Error handling social request: {}", e.getMessage(), e);
            request.setAttribute("errorMessage", e.getMessage());
            request.getRequestDispatcher("/social/hub.jsp").forward(request, response);
        }
    }

    private void handleCompetitionDetail(HttpServletRequest request, HttpServletResponse response, Integer currentUserId)
            throws ServletException, IOException {
        String idStr = request.getParameter("id");
        if (idStr == null || idStr.isEmpty()) {
            response.sendRedirect(request.getContextPath() + "/social");
            return;
        }
        int compId = Integer.parseInt(idStr);
        Competition comp = socialService.getCompetitionDetails(compId, currentUserId);
        List<CompetitionParticipant> leaderboard = socialService.getCompetitionLeaderboard(compId, 50);

        request.setAttribute("competition", comp);
        request.setAttribute("leaderboard", leaderboard);
        request.getRequestDispatcher("/social/competition.jsp").forward(request, response);
    }

    private void handleSocialProfile(HttpServletRequest request, HttpServletResponse response, Integer currentUserId)
            throws ServletException, IOException {
        String idStr = request.getParameter("id");
        if (idStr == null || idStr.isEmpty()) {
            response.sendRedirect(request.getContextPath() + "/social");
            return;
        }
        int targetUserId = Integer.parseInt(idStr);
        try {
            SocialProfileDTO profile = socialService.getSocialProfile(targetUserId, currentUserId);
            request.setAttribute("socialProfile", profile);
            request.getRequestDispatcher("/social/profile.jsp").forward(request, response);
        } catch (Exception e) {
            request.getSession().setAttribute("errorMessage", e.getMessage());
            response.sendRedirect(request.getContextPath() + "/social?error=PrivateProfile");
        }
    }

    private void handleLeaderboardView(HttpServletRequest request, HttpServletResponse response, Integer currentUserId)
            throws ServletException, IOException {
        String type = request.getParameter("period");
        List<LeaderboardEntryDTO> entries;
        String periodLabel;

        if ("weekly".equalsIgnoreCase(type)) {
            entries = socialService.getWeeklyLeaderboard(50, 0, currentUserId);
            periodLabel = "Weekly";
        } else if ("monthly".equalsIgnoreCase(type)) {
            entries = socialService.getMonthlyLeaderboard(50, 0, currentUserId);
            periodLabel = "Monthly";
        } else {
            entries = socialService.getAllTimeLeaderboard(50, 0, currentUserId);
            periodLabel = "All-Time";
        }

        request.setAttribute("leaderboard", entries);
        request.setAttribute("periodLabel", periodLabel);
        request.getRequestDispatcher("/social/leaderboard-full.jsp").forward(request, response);
    }

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        if (!CsrfUtil.isValidToken(request)) {
            response.sendError(HttpServletResponse.SC_FORBIDDEN, "Invalid or missing CSRF token.");
            return;
        }

        Integer currentUserId = SessionUtil.getAuthenticatedUserId(request);
        String clientIp = request.getRemoteAddr();
        String action = request.getParameter("action");

        try {
            if ("joinCompetition".equalsIgnoreCase(action)) {
                int compId = Integer.parseInt(request.getParameter("competitionId"));
                socialService.joinCompetition(compId, currentUserId, clientIp);
                response.sendRedirect(request.getContextPath() + "/social/competition?id=" + compId + "&success=joined");
                return;

            } else if ("leaveCompetition".equalsIgnoreCase(action)) {
                int compId = Integer.parseInt(request.getParameter("competitionId"));
                socialService.leaveCompetition(compId, currentUserId, clientIp);
                response.sendRedirect(request.getContextPath() + "/social/competition?id=" + compId + "&success=left");
                return;

            } else if ("sendConnection".equalsIgnoreCase(action)) {
                int receiverId = Integer.parseInt(request.getParameter("receiverId"));
                socialService.sendConnectionRequest(currentUserId, receiverId, clientIp);
                response.sendRedirect(request.getContextPath() + "/social/profile?id=" + receiverId + "&success=requested");
                return;

            } else if ("acceptConnection".equalsIgnoreCase(action)) {
                int connectionId = Integer.parseInt(request.getParameter("connectionId"));
                socialService.acceptConnectionRequest(connectionId, currentUserId, clientIp);
                response.sendRedirect(request.getContextPath() + "/social?success=accepted");
                return;

            } else if ("rejectConnection".equalsIgnoreCase(action)) {
                int connectionId = Integer.parseInt(request.getParameter("connectionId"));
                socialService.rejectConnectionRequest(connectionId, currentUserId, clientIp);
                response.sendRedirect(request.getContextPath() + "/social?success=rejected");
                return;

            } else if ("removeConnection".equalsIgnoreCase(action)) {
                int connectionId = Integer.parseInt(request.getParameter("connectionId"));
                socialService.removeConnection(connectionId, currentUserId, clientIp);
                response.sendRedirect(request.getContextPath() + "/social?success=removed");
                return;
            }

            response.sendRedirect(request.getContextPath() + "/social");

        } catch (Exception e) {
            logger.warn("Social action '{}' failed: {}", action, e.getMessage());
            request.getSession().setAttribute("errorMessage", e.getMessage());
            response.sendRedirect(request.getContextPath() + "/social?error=" + e.getMessage());
        }
    }
}
