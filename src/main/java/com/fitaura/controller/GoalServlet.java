package com.fitaura.controller;

import com.fitaura.exception.DatabaseException;
import com.fitaura.exception.ResourceNotFoundException;
import com.fitaura.exception.ValidationException;
import com.fitaura.model.FitnessGoal;
import com.fitaura.service.GoalService;
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
import java.util.List;

/**
 * Controller serving the Fitness Goals module:
 * - Goals list (Active, Paused, Completed, Cancelled)
 * - Create goal form & submission
 * - View goal details
 * - Edit active goal form & submission
 * - Status transitions (Pause, Resume, Complete, Cancel)
 * - Delete goal action
 */
@WebServlet(name = "GoalServlet", urlPatterns = {"/goals", "/goals/*"})
public class GoalServlet extends HttpServlet {

    private static final long serialVersionUID = 1L;
    private static final Logger logger = LoggerFactory.getLogger(GoalServlet.class);

    private final GoalService goalService;

    public GoalServlet() {
        this(new GoalServiceImpl());
    }

    public GoalServlet(GoalService goalService) {
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

        // Initialize CSRF token
        CsrfUtil.getToken(request.getSession(true));

        String action = resolveAction(request);

        switch (action) {
            case "create":
            case "add":
                showCreateForm(request, response);
                break;
            case "view":
            case "detail":
                showGoalDetails(request, response, userId);
                break;
            case "edit":
                showEditForm(request, response, userId);
                break;
            case "list":
            default:
                showGoalsList(request, response, userId);
                break;
        }
    }

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        Integer userId = SessionUtil.getAuthenticatedUserId(request);
        if (userId == null) {
            response.sendRedirect(request.getContextPath() + "/login");
            return;
        }

        // CSRF Verification
        if (!CsrfUtil.isValidToken(request)) {
            request.setAttribute("errorMessage", "Security validation failed. Please refresh the page and try again.");
            showGoalsList(request, response, userId);
            return;
        }

        String action = resolveAction(request);
        String clientIp = getClientIp(request);

        switch (action) {
            case "create":
            case "add":
                handleCreateGoal(request, response, userId, clientIp);
                break;
            case "edit":
                handleEditGoal(request, response, userId, clientIp);
                break;
            case "status":
                handleStatusChange(request, response, userId, clientIp);
                break;
            case "delete":
                handleDeleteGoal(request, response, userId, clientIp);
                break;
            default:
                response.sendRedirect(request.getContextPath() + "/goals");
                break;
        }
    }

    // ==========================================
    // GET Action Handlers
    // ==========================================

    private void showGoalsList(HttpServletRequest request, HttpServletResponse response, Integer userId)
            throws ServletException, IOException {
        String statusFilter = request.getParameter("status");
        List<FitnessGoal> goals;

        if (statusFilter != null && !statusFilter.trim().isEmpty()) {
            try {
                FitnessGoal.GoalStatus status = FitnessGoal.GoalStatus.valueOf(statusFilter.trim().toUpperCase());
                goals = goalService.getUserGoalsByStatus(userId, status);
                request.setAttribute("currentStatusFilter", status.name());
            } catch (IllegalArgumentException e) {
                goals = goalService.getUserGoals(userId);
            }
        } else {
            goals = goalService.getUserGoals(userId);
        }

        request.setAttribute("goals", goals);
        request.setAttribute("activeGoalCount", goalService.getActiveGoalCount(userId));
        request.setAttribute("totalGoalCount", goalService.getTotalGoalCount(userId));
        request.getRequestDispatcher("/goals/list.jsp").forward(request, response);
    }

    private void showCreateForm(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        if (request.getAttribute("startDate") == null) {
            request.setAttribute("startDate", LocalDate.now().toString());
        }
        request.getRequestDispatcher("/goals/create.jsp").forward(request, response);
    }

    private void showGoalDetails(HttpServletRequest request, HttpServletResponse response, Integer userId)
            throws ServletException, IOException {
        Integer goalId = parseIdParam(request, "id");
        if (goalId == null) {
            response.sendRedirect(request.getContextPath() + "/goals");
            return;
        }

        try {
            FitnessGoal goal = goalService.getGoalDetails(goalId, userId);
            request.setAttribute("goal", goal);
            request.getRequestDispatcher("/goals/detail.jsp").forward(request, response);
        } catch (ResourceNotFoundException e) {
            request.getSession().setAttribute("flashError", "The requested goal was not found or access was denied.");
            response.sendRedirect(request.getContextPath() + "/goals");
        }
    }

    private void showEditForm(HttpServletRequest request, HttpServletResponse response, Integer userId)
            throws ServletException, IOException {
        Integer goalId = parseIdParam(request, "id");
        if (goalId == null) {
            response.sendRedirect(request.getContextPath() + "/goals");
            return;
        }

        try {
            FitnessGoal goal = goalService.getGoalDetails(goalId, userId);
            if (goal.getStatus() == FitnessGoal.GoalStatus.COMPLETED || goal.getStatus() == FitnessGoal.GoalStatus.CANCELLED) {
                request.getSession().setAttribute("flashError", "Completed or cancelled goals cannot be edited.");
                response.sendRedirect(request.getContextPath() + "/goals/view?id=" + goalId);
                return;
            }
            request.setAttribute("goal", goal);
            request.getRequestDispatcher("/goals/edit.jsp").forward(request, response);
        } catch (ResourceNotFoundException e) {
            request.getSession().setAttribute("flashError", "The requested goal was not found or access was denied.");
            response.sendRedirect(request.getContextPath() + "/goals");
        }
    }

    // ==========================================
    // POST Action Handlers
    // ==========================================

    private void handleCreateGoal(HttpServletRequest request, HttpServletResponse response,
                                  Integer userId, String clientIp)
            throws ServletException, IOException {
        String goalType = request.getParameter("goalType");
        String targetValueStr = request.getParameter("targetValue");
        String currentValueStr = request.getParameter("currentValue");
        String unit = request.getParameter("unit");
        String startDate = request.getParameter("startDate");
        String targetDate = request.getParameter("targetDate");
        String notes = request.getParameter("notes");

        BigDecimal targetValue = parseBigDecimal(targetValueStr);
        BigDecimal currentValue = parseBigDecimal(currentValueStr);

        try {
            FitnessGoal created = goalService.createGoal(userId, goalType, targetValue, currentValue,
                    unit, startDate, targetDate, notes, clientIp);

            request.getSession().setAttribute("flashSuccess", "Fitness goal created successfully.");
            response.sendRedirect(request.getContextPath() + "/goals/view?id=" + created.getGoalId());

        } catch (ValidationException e) {
            request.setAttribute("errorMessage", e.getMessage());
            request.setAttribute("goalType", goalType);
            request.setAttribute("targetValue", targetValueStr);
            request.setAttribute("currentValue", currentValueStr);
            request.setAttribute("unit", unit);
            request.setAttribute("startDate", startDate);
            request.setAttribute("targetDate", targetDate);
            request.setAttribute("notes", notes);
            request.getRequestDispatcher("/goals/create.jsp").forward(request, response);

        } catch (DatabaseException e) {
            request.setAttribute("errorMessage", "Unable to save your goal right now. Please try again.");
            request.getRequestDispatcher("/goals/create.jsp").forward(request, response);
        }
    }

    private void handleEditGoal(HttpServletRequest request, HttpServletResponse response,
                                Integer userId, String clientIp)
            throws ServletException, IOException {
        Integer goalId = parseIdParam(request, "goalId");
        if (goalId == null) {
            goalId = parseIdParam(request, "id");
        }

        if (goalId == null) {
            response.sendRedirect(request.getContextPath() + "/goals");
            return;
        }

        String goalType = request.getParameter("goalType");
        String targetValueStr = request.getParameter("targetValue");
        String currentValueStr = request.getParameter("currentValue");
        String unit = request.getParameter("unit");
        String startDate = request.getParameter("startDate");
        String targetDate = request.getParameter("targetDate");
        String notes = request.getParameter("notes");

        BigDecimal targetValue = parseBigDecimal(targetValueStr);
        BigDecimal currentValue = parseBigDecimal(currentValueStr);

        try {
            goalService.updateGoal(goalId, userId, goalType, targetValue, currentValue,
                    unit, startDate, targetDate, notes, clientIp);

            request.getSession().setAttribute("flashSuccess", "Goal updated successfully.");
            response.sendRedirect(request.getContextPath() + "/goals/view?id=" + goalId);

        } catch (ValidationException e) {
            request.setAttribute("errorMessage", e.getMessage());
            try {
                FitnessGoal g = goalService.getGoalDetails(goalId, userId);
                request.setAttribute("goal", g);
            } catch (ResourceNotFoundException ignored) {}

            request.setAttribute("overrideType", goalType);
            request.setAttribute("overrideTarget", targetValueStr);
            request.setAttribute("overrideCurrent", currentValueStr);
            request.setAttribute("overrideUnit", unit);
            request.setAttribute("overrideStartDate", startDate);
            request.setAttribute("overrideTargetDate", targetDate);
            request.setAttribute("overrideNotes", notes);
            request.getRequestDispatcher("/goals/edit.jsp").forward(request, response);

        } catch (ResourceNotFoundException e) {
            request.getSession().setAttribute("flashError", "Goal not found or update unauthorized.");
            response.sendRedirect(request.getContextPath() + "/goals");

        } catch (DatabaseException e) {
            request.setAttribute("errorMessage", "Unable to save goal updates right now. Please try again.");
            request.getRequestDispatcher("/goals/edit.jsp").forward(request, response);
        }
    }

    private void handleStatusChange(HttpServletRequest request, HttpServletResponse response,
                                    Integer userId, String clientIp)
            throws IOException {
        Integer goalId = parseIdParam(request, "goalId");
        String statusStr = request.getParameter("status");

        if (goalId == null || statusStr == null || statusStr.trim().isEmpty()) {
            response.sendRedirect(request.getContextPath() + "/goals");
            return;
        }

        try {
            FitnessGoal.GoalStatus newStatus = FitnessGoal.GoalStatus.valueOf(statusStr.trim().toUpperCase());
            goalService.updateGoalStatus(goalId, userId, newStatus, clientIp);

            String message;
            if (newStatus == FitnessGoal.GoalStatus.COMPLETED) {
                message = "Congratulations! Goal marked as completed.";
            } else if (newStatus == FitnessGoal.GoalStatus.PAUSED) {
                message = "Goal paused.";
            } else if (newStatus == FitnessGoal.GoalStatus.CANCELLED) {
                message = "Goal cancelled.";
            } else {
                message = "Goal resumed.";
            }

            request.getSession().setAttribute("flashSuccess", message);
            response.sendRedirect(request.getContextPath() + "/goals/view?id=" + goalId);

        } catch (ValidationException e) {
            request.getSession().setAttribute("flashError", e.getMessage());
            response.sendRedirect(request.getContextPath() + "/goals/view?id=" + goalId);
        } catch (ResourceNotFoundException e) {
            request.getSession().setAttribute("flashError", "Goal not found or status change unauthorized.");
            response.sendRedirect(request.getContextPath() + "/goals");
        } catch (Exception e) {
            request.getSession().setAttribute("flashError", "Unable to update goal status right now.");
            response.sendRedirect(request.getContextPath() + "/goals");
        }
    }

    private void handleDeleteGoal(HttpServletRequest request, HttpServletResponse response,
                                  Integer userId, String clientIp)
            throws IOException {
        Integer goalId = parseIdParam(request, "goalId");
        if (goalId == null) {
            goalId = parseIdParam(request, "id");
        }

        if (goalId == null) {
            response.sendRedirect(request.getContextPath() + "/goals");
            return;
        }

        try {
            goalService.deleteGoal(goalId, userId, clientIp);
            request.getSession().setAttribute("flashSuccess", "Goal deleted successfully.");
        } catch (ResourceNotFoundException e) {
            request.getSession().setAttribute("flashError", "Goal not found or deletion unauthorized.");
        } catch (DatabaseException e) {
            request.getSession().setAttribute("flashError", "Unable to delete goal right now. Please try again.");
        }

        response.sendRedirect(request.getContextPath() + "/goals");
    }

    // ==========================================
    // Helpers
    // ==========================================

    private String resolveAction(HttpServletRequest request) {
        String pathInfo = request.getPathInfo();
        if (pathInfo != null && !pathInfo.isEmpty() && !"/".equals(pathInfo)) {
            String clean = pathInfo.startsWith("/") ? pathInfo.substring(1) : pathInfo;
            int slashIndex = clean.indexOf('/');
            if (slashIndex > 0) {
                return clean.substring(0, slashIndex).toLowerCase();
            }
            return clean.toLowerCase();
        }
        String actionParam = request.getParameter("action");
        if (actionParam != null && !actionParam.trim().isEmpty()) {
            return actionParam.trim().toLowerCase();
        }
        return "list";
    }

    private Integer parseIdParam(HttpServletRequest request, String paramName) {
        String val = request.getParameter(paramName);
        if (val != null && !val.trim().isEmpty()) {
            try {
                return Integer.parseInt(val.trim());
            } catch (NumberFormatException ignored) {}
        }
        return null;
    }

    private BigDecimal parseBigDecimal(String str) {
        if (str == null || str.trim().isEmpty()) {
            return null;
        }
        try {
            return new BigDecimal(str.trim());
        } catch (NumberFormatException ignored) {
            return new BigDecimal("-1"); // Will trigger validation error
        }
    }

    private String getClientIp(HttpServletRequest request) {
        String xf = request.getHeader("X-Forwarded-For");
        if (xf != null && !xf.isEmpty()) {
            return xf.split(",")[0].trim();
        }
        return request.getRemoteAddr() != null ? request.getRemoteAddr() : "127.0.0.1";
    }
}
