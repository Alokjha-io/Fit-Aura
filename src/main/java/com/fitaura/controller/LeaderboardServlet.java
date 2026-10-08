package com.fitaura.controller;

import com.fitaura.dto.LeaderboardEntryDTO;
import com.fitaura.model.User;
import com.fitaura.service.GamificationService;
import com.fitaura.service.UserService;
import com.fitaura.service.impl.GamificationServiceImpl;
import com.fitaura.service.impl.UserServiceImpl;
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

/**
 * Controller serving the Social Leaderboard module:
 * - Community points rankings
 * - Privacy compliance (anonymizes personal profiles, respects privacy preferences)
 * - User highlight & relative position
 * - Streak & achievement badges in ranking list
 */
@WebServlet(name = "LeaderboardServlet", urlPatterns = {"/leaderboard", "/leaderboard/*"})
public class LeaderboardServlet extends HttpServlet {

    private static final long serialVersionUID = 1L;
    private static final Logger logger = LoggerFactory.getLogger(LeaderboardServlet.class);

    private final GamificationService gamificationService;
    private final UserService userService;

    public LeaderboardServlet() {
        this(new GamificationServiceImpl(), new UserServiceImpl());
    }

    public LeaderboardServlet(GamificationService gamificationService, UserService userService) {
        this.gamificationService = gamificationService;
        this.userService = userService;
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
            int page = 1;
            String pageStr = request.getParameter("page");
            if (pageStr != null && !pageStr.trim().isEmpty()) {
                try {
                    page = Math.max(1, Integer.parseInt(pageStr.trim()));
                } catch (NumberFormatException ignored) {}
            }

            int pageSize = 20;
            int offset = (page - 1) * pageSize;

            List<LeaderboardEntryDTO> entries = gamificationService.getSocialLeaderboard(pageSize, offset, userId);
            int totalUsers = gamificationService.countSocialLeaderboardUsers();
            int totalPages = (int) Math.ceil((double) totalUsers / pageSize);
            if (totalPages < 1) totalPages = 1;

            Integer userRank = gamificationService.getUserSocialRank(userId);
            int userTotalPoints = gamificationService.calculateTotalPoints(userId);

            User currentUser = userService.getUserById(userId);

            request.setAttribute("leaderboard", entries);
            request.setAttribute("currentPage", page);
            request.setAttribute("totalPages", totalPages);
            request.setAttribute("totalUsers", totalUsers);
            request.setAttribute("userRank", userRank);
            request.setAttribute("userTotalPoints", userTotalPoints);
            request.setAttribute("currentUser", currentUser);

            request.getRequestDispatcher("/leaderboard/index.jsp").forward(request, response);

        } catch (Exception e) {
            logger.error("Error loading leaderboard for user {}: {}", userId, e.getMessage(), e);
            request.setAttribute("errorMessage", "Unable to load leaderboard. Please try again later.");
            request.getRequestDispatcher("/leaderboard/index.jsp").forward(request, response);
        }
    }
}
