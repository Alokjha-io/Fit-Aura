package com.fitaura.controller;

import com.fitaura.dao.FitnessGoalDAO;
import com.fitaura.dao.FitnessProfileDAO;
import com.fitaura.dao.UserDAO;
import com.fitaura.dao.WorkoutDAO;
import com.fitaura.dao.impl.FitnessGoalDAOImpl;
import com.fitaura.dao.impl.FitnessProfileDAOImpl;
import com.fitaura.dao.impl.UserDAOImpl;
import com.fitaura.dao.impl.WorkoutDAOImpl;
import com.fitaura.dto.StreakInfo;
import com.fitaura.model.DailyQuote;
import com.fitaura.model.FitnessProfile;
import com.fitaura.model.User;
import com.fitaura.service.GamificationService;
import com.fitaura.service.QuoteService;
import com.fitaura.service.SocialFitnessService;
import com.fitaura.service.impl.GamificationServiceImpl;
import com.fitaura.service.impl.QuoteServiceImpl;
import com.fitaura.service.impl.SocialFitnessServiceImpl;
import com.fitaura.util.SessionUtil;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

import java.io.IOException;
import java.util.Optional;

/**
 * Controller serving the protected user home and landing area.
 * Validates session identity and provides safe user metadata, today's motivation,
 * and social stats to the JSP view.
 */
@WebServlet(name = "UserDashboardServlet", urlPatterns = {"/user/dashboard", "/user/home"})
public class UserDashboardServlet extends HttpServlet {

    private static final long serialVersionUID = 1L;

    private final UserDAO userDAO;
    private final FitnessProfileDAO fitnessProfileDAO;
    private final WorkoutDAO workoutDAO;
    private final FitnessGoalDAO fitnessGoalDAO;
    private final QuoteService quoteService;
    private final GamificationService gamificationService;
    private final SocialFitnessService socialService;

    public UserDashboardServlet() {
        this(new UserDAOImpl(), new FitnessProfileDAOImpl(), new WorkoutDAOImpl(),
             new FitnessGoalDAOImpl(), new QuoteServiceImpl(), new GamificationServiceImpl(),
             new SocialFitnessServiceImpl());
    }

    public UserDashboardServlet(UserDAO userDAO, FitnessProfileDAO fitnessProfileDAO,
                                WorkoutDAO workoutDAO, FitnessGoalDAO fitnessGoalDAO,
                                QuoteService quoteService, GamificationService gamificationService,
                                SocialFitnessService socialService) {
        this.userDAO = userDAO;
        this.fitnessProfileDAO = fitnessProfileDAO;
        this.workoutDAO = workoutDAO;
        this.fitnessGoalDAO = fitnessGoalDAO;
        this.quoteService = quoteService;
        this.gamificationService = gamificationService;
        this.socialService = socialService;
    }

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        Integer userId = SessionUtil.getAuthenticatedUserId(request);
        if (userId == null) {
            response.sendRedirect(request.getContextPath() + "/login");
            return;
        }

        Optional<User> userOpt = userDAO.findById(userId);
        if (userOpt.isEmpty()) {
            SessionUtil.invalidateSession(request);
            response.sendRedirect(request.getContextPath() + "/login");
            return;
        }

        User user = userOpt.get();
        request.setAttribute("currentUser", user);

        Optional<FitnessProfile> profileOpt = fitnessProfileDAO.findByUserId(userId);
        request.setAttribute("fitnessProfile", profileOpt.orElse(null));

        int workoutCount = workoutDAO.countByUserId(userId);
        request.setAttribute("workoutCount", workoutCount);

        int activeGoalCount = fitnessGoalDAO.countActiveByUserId(userId);
        request.setAttribute("activeGoalCount", activeGoalCount);

        // Daily Motivation Quote
        DailyQuote todayQuote = quoteService.getTodayQuote();
        request.setAttribute("todayQuote", todayQuote);

        // Gamification & Streak stats
        int totalPoints = gamificationService.calculateTotalPoints(userId);
        request.setAttribute("totalPoints", totalPoints);
        StreakInfo streak = gamificationService.calculateUserStreak(userId);
        request.setAttribute("streak", streak);

        // Social-specific data if user has enabled SOCIAL mode
        if (user.getPrivacyMode() == User.PrivacyMode.SOCIAL) {
            Integer rank = gamificationService.getUserSocialRank(userId);
            request.setAttribute("socialRank", rank);
            request.setAttribute("activeCompetitions", socialService.getActiveCompetitions(userId));
        }

        request.getRequestDispatcher("/user/dashboard.jsp").forward(request, response);
    }
}
