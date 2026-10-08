package com.fitaura.controller;

import com.fitaura.exception.DatabaseException;
import com.fitaura.exception.ValidationException;
import com.fitaura.model.FitnessProfile;
import com.fitaura.model.User;
import com.fitaura.service.FitnessProfileService;
import com.fitaura.service.PrivacyService;
import com.fitaura.service.UserService;
import com.fitaura.service.impl.FitnessProfileServiceImpl;
import com.fitaura.service.impl.PrivacyServiceImpl;
import com.fitaura.service.impl.UserServiceImpl;
import com.fitaura.util.AppConstants;
import com.fitaura.util.CsrfUtil;
import com.fitaura.util.SessionUtil;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;

import java.io.IOException;
import java.math.BigDecimal;
import java.util.Optional;

/**
 * Controller serving the authenticated user profile page,
 * supporting basic profile edits, physical metric updates, and privacy mode toggling.
 */
@WebServlet(name = "ProfileServlet", urlPatterns = {"/user/profile"})
public class ProfileServlet extends HttpServlet {

    private static final long serialVersionUID = 1L;

    private final UserService userService;
    private final FitnessProfileService fitnessProfileService;
    private final PrivacyService privacyService;

    public ProfileServlet() {
        this(new UserServiceImpl(), new FitnessProfileServiceImpl(), new PrivacyServiceImpl());
    }

    public ProfileServlet(UserService userService,
                          FitnessProfileService fitnessProfileService,
                          PrivacyService privacyService) {
        this.userService = userService;
        this.fitnessProfileService = fitnessProfileService;
        this.privacyService = privacyService;
    }

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        Integer userId = SessionUtil.getAuthenticatedUserId(request);
        if (userId == null) {
            response.sendRedirect(request.getContextPath() + "/login");
            return;
        }

        populateProfileAttributes(request, userId);
        CsrfUtil.getToken(request.getSession(true));

        request.getRequestDispatcher("/user/profile.jsp").forward(request, response);
    }

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        Integer userId = SessionUtil.getAuthenticatedUserId(request);
        if (userId == null) {
            response.sendRedirect(request.getContextPath() + "/login");
            return;
        }

        // 1. Verify CSRF Token
        if (!CsrfUtil.isValidToken(request)) {
            request.setAttribute("errorMessage", "Security validation failed. Please refresh the page and try again.");
            populateProfileAttributes(request, userId);
            request.getRequestDispatcher("/user/profile.jsp").forward(request, response);
            return;
        }

        String action = request.getParameter("action");
        String clientIp = getClientIp(request);
        HttpSession session = request.getSession(false);

        try {
            if ("update_basic".equals(action)) {
                String fullName = request.getParameter("fullName");
                String displayName = request.getParameter("displayName");

                User updated = userService.updateBasicProfile(userId, fullName, displayName, clientIp);

                if (session != null) {
                    session.setAttribute(AppConstants.SESSION_USER_NAME, updated.getFullName());
                    session.setAttribute(AppConstants.SESSION_DISPLAY_NAME, updated.getDisplayName());
                }
                request.setAttribute("successMessage", "Profile updated successfully.");

            } else if ("update_fitness".equals(action)) {
                String ageStr = request.getParameter("age");
                String genderStr = request.getParameter("gender");
                String heightStr = request.getParameter("heightCm");
                String weightStr = request.getParameter("weightKg");
                String activityStr = request.getParameter("activityLevel");
                String envStr = request.getParameter("preferredEnvironment");

                Integer age = parseNullableInteger(ageStr, "Age");
                BigDecimal heightCm = parseNullableBigDecimal(heightStr, "Height");
                BigDecimal weightKg = parseNullableBigDecimal(weightStr, "Weight");

                FitnessProfile.Gender gender = parseNullableEnum(FitnessProfile.Gender.class, genderStr);
                FitnessProfile.ActivityLevel activity = parseNullableEnum(FitnessProfile.ActivityLevel.class, activityStr);
                FitnessProfile.PreferredEnvironment env = parseNullableEnum(FitnessProfile.PreferredEnvironment.class, envStr);

                fitnessProfileService.saveOrUpdateFitnessProfile(userId, age, gender, heightCm, weightKg, activity, env, clientIp);
                request.setAttribute("successMessage", "Fitness profile updated successfully.");

            } else if ("update_privacy".equals(action)) {
                String privacyStr = request.getParameter("privacyMode");
                User.PrivacyMode mode = parseNullableEnum(User.PrivacyMode.class, privacyStr);
                if (mode == null) {
                    throw new ValidationException("Please select a valid privacy mode.");
                }

                privacyService.updatePrivacyMode(userId, mode, clientIp);
                if (session != null) {
                    session.setAttribute(AppConstants.SESSION_PRIVACY_MODE, mode.name());
                }
                request.setAttribute("successMessage", "Privacy settings updated successfully.");
            }

        } catch (ValidationException e) {
            request.setAttribute("errorMessage", e.getMessage());
        } catch (DatabaseException e) {
            request.setAttribute("errorMessage", "Unable to update your profile right now. Please try again.");
        }

        populateProfileAttributes(request, userId);
        request.getRequestDispatcher("/user/profile.jsp").forward(request, response);
    }

    private void populateProfileAttributes(HttpServletRequest request, Integer userId) {
        User user = userService.getUserById(userId);
        Optional<FitnessProfile> profileOpt = fitnessProfileService.getFitnessProfile(userId);

        request.setAttribute("user", user);
        request.setAttribute("fitnessProfile", profileOpt.orElse(new FitnessProfile()));
        request.setAttribute("privacyMode", user.getPrivacyMode());
    }

    private String getClientIp(HttpServletRequest request) {
        String xForwarded = request.getHeader("X-Forwarded-For");
        if (xForwarded != null && !xForwarded.isEmpty()) {
            return xForwarded.split(",")[0].trim();
        }
        return request.getRemoteAddr();
    }

    private Integer parseNullableInteger(String value, String fieldName) throws ValidationException {
        if (value == null || value.trim().isEmpty()) {
            return null;
        }
        try {
            return Integer.parseInt(value.trim());
        } catch (NumberFormatException e) {
            throw new ValidationException(fieldName + " must be a valid whole number.");
        }
    }

    private BigDecimal parseNullableBigDecimal(String value, String fieldName) throws ValidationException {
        if (value == null || value.trim().isEmpty()) {
            return null;
        }
        try {
            return new BigDecimal(value.trim());
        } catch (NumberFormatException e) {
            throw new ValidationException(fieldName + " must be a valid numeric measurement.");
        }
    }

    private <E extends Enum<E>> E parseNullableEnum(Class<E> enumClass, String value) {
        if (value == null || value.trim().isEmpty()) {
            return null;
        }
        try {
            return Enum.valueOf(enumClass, value.trim().toUpperCase());
        } catch (IllegalArgumentException e) {
            return null;
        }
    }
}
