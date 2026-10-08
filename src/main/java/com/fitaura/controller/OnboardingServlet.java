package com.fitaura.controller;

import com.fitaura.exception.ValidationException;
import com.fitaura.model.FitnessGoal;
import com.fitaura.model.FitnessProfile;
import com.fitaura.service.FitnessProfileService;
import com.fitaura.service.GoalService;
import com.fitaura.service.impl.FitnessProfileServiceImpl;
import com.fitaura.service.impl.GoalServiceImpl;
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
import java.time.LocalDate;
import java.util.Optional;

/**
 * Controller managing post-registration Fitness Profile and Initial Goal Onboarding.
 * Collects physical measurements, activity level, environment preference, and target goals.
 */
@WebServlet(name = "OnboardingServlet", urlPatterns = {"/onboarding"})
public class OnboardingServlet extends HttpServlet {

    private static final long serialVersionUID = 1L;
    private static final Logger logger = LoggerFactory.getLogger(OnboardingServlet.class);

    private final FitnessProfileService profileService;
    private final GoalService goalService;

    public OnboardingServlet() {
        this(new FitnessProfileServiceImpl(), new GoalServiceImpl());
    }

    public OnboardingServlet(FitnessProfileService profileService, GoalService goalService) {
        this.profileService = profileService;
        this.goalService = goalService;
    }

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        Integer userId = SessionUtil.getAuthenticatedUserId(request);
        if (userId == null) {
            response.sendRedirect(request.getContextPath() + "/login");
            return;
        }

        // Check if user already completed onboarding
        Optional<FitnessProfile> existing = profileService.getFitnessProfile(userId);
        request.setAttribute("existingProfile", existing.orElse(null));

        request.getRequestDispatcher("/onboarding.jsp").forward(request, response);
    }

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        Integer userId = SessionUtil.getAuthenticatedUserId(request);
        if (userId == null) {
            response.sendRedirect(request.getContextPath() + "/login");
            return;
        }

        if (!CsrfUtil.isValidToken(request)) {
            request.setAttribute("errorMessage", "Security validation failed. Please try again.");
            request.getRequestDispatcher("/onboarding.jsp").forward(request, response);
            return;
        }

        String action = request.getParameter("action");
        if ("skip".equalsIgnoreCase(action)) {
            response.sendRedirect(request.getContextPath() + "/user/dashboard");
            return;
        }

        String ageStr = request.getParameter("age");
        String genderStr = request.getParameter("gender");
        String heightStr = request.getParameter("heightCm");
        String weightStr = request.getParameter("weightKg");
        String activityStr = request.getParameter("activityLevel");
        String environmentStr = request.getParameter("preferredEnvironment");
        String initialGoalType = request.getParameter("goalType");
        String targetValueStr = request.getParameter("targetValue");
        String targetDateStr = request.getParameter("targetDate");
        String clientIp = request.getRemoteAddr();

        try {
            Integer age = (ageStr != null && !ageStr.trim().isEmpty()) ? Integer.parseInt(ageStr.trim()) : null;
            FitnessProfile.Gender gender = (genderStr != null && !genderStr.trim().isEmpty())
                    ? FitnessProfile.Gender.valueOf(genderStr.trim().toUpperCase()) : null;
            BigDecimal height = (heightStr != null && !heightStr.trim().isEmpty())
                    ? new BigDecimal(heightStr.trim()) : null;
            BigDecimal weight = (weightStr != null && !weightStr.trim().isEmpty())
                    ? new BigDecimal(weightStr.trim()) : null;
            FitnessProfile.ActivityLevel activity = (activityStr != null && !activityStr.trim().isEmpty())
                    ? FitnessProfile.ActivityLevel.valueOf(activityStr.trim().toUpperCase()) : FitnessProfile.ActivityLevel.MODERATE;
            FitnessProfile.PreferredEnvironment env = (environmentStr != null && !environmentStr.trim().isEmpty())
                    ? FitnessProfile.PreferredEnvironment.valueOf(environmentStr.trim().toUpperCase()) : FitnessProfile.PreferredEnvironment.MIXED;

            // 1. Save Fitness Profile
            profileService.saveOrUpdateFitnessProfile(userId, age, gender, height, weight, activity, env, clientIp);

            // 2. Save Initial Goal if provided
            if (initialGoalType != null && !initialGoalType.trim().isEmpty() && targetValueStr != null && !targetValueStr.trim().isEmpty()) {
                try {
                    BigDecimal targetValue = new BigDecimal(targetValueStr.trim());
                    BigDecimal startVal = weight != null ? weight : BigDecimal.ZERO;
                    String unit = initialGoalType.contains("WEIGHT") ? "kg" : "sessions/wk";
                    String startDate = LocalDate.now().toString();

                    goalService.createGoal(userId, initialGoalType, targetValue, startVal, unit, startDate, targetDateStr,
                            "Initial goal created during account onboarding.", clientIp);
                } catch (Exception e) {
                    logger.warn("Could not save initial goal during onboarding: {}", e.getMessage());
                }
            }

            response.sendRedirect(request.getContextPath() + "/user/dashboard?onboarded=true");

        } catch (ValidationException e) {
            request.setAttribute("errorMessage", e.getMessage());
            request.setAttribute("age", ageStr);
            request.setAttribute("gender", genderStr);
            request.setAttribute("heightCm", heightStr);
            request.setAttribute("weightKg", weightStr);
            request.setAttribute("activityLevel", activityStr);
            request.setAttribute("preferredEnvironment", environmentStr);
            request.getRequestDispatcher("/onboarding.jsp").forward(request, response);
        } catch (Exception e) {
            logger.error("Error during fitness onboarding for user {}: {}", userId, e.getMessage(), e);
            request.setAttribute("errorMessage", "Failed to save fitness onboarding profile: " + e.getMessage());
            request.getRequestDispatcher("/onboarding.jsp").forward(request, response);
        }
    }
}
