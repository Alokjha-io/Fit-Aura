package com.fitaura.controller;

import com.fitaura.exception.DatabaseException;
import com.fitaura.exception.ResourceNotFoundException;
import com.fitaura.exception.ValidationException;
import com.fitaura.model.Workout;
import com.fitaura.service.WorkoutService;
import com.fitaura.service.impl.WorkoutServiceImpl;
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
import java.time.LocalDate;
import java.util.List;

/**
 * Controller serving the Workout Tracking module:
 * - History list with sorting and filtering
 * - Add workout form and submission
 * - View workout details
 * - Edit workout form and submission
 * - Delete workout action with CSRF and ownership guards
 */
@WebServlet(name = "WorkoutServlet", urlPatterns = {"/workouts", "/workouts/*"})
public class WorkoutServlet extends HttpServlet {

    private static final long serialVersionUID = 1L;
    private static final Logger logger = LoggerFactory.getLogger(WorkoutServlet.class);

    private final WorkoutService workoutService;

    public WorkoutServlet() {
        this(new WorkoutServiceImpl());
    }

    public WorkoutServlet(WorkoutService workoutService) {
        this.workoutService = workoutService;
    }

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        Integer userId = SessionUtil.getAuthenticatedUserId(request);
        if (userId == null) {
            response.sendRedirect(request.getContextPath() + "/login");
            return;
        }

        // Initialize CSRF token in session
        CsrfUtil.getToken(request.getSession(true));

        String action = resolveAction(request);

        switch (action) {
            case "add":
                showAddForm(request, response);
                break;
            case "view":
            case "detail":
                showWorkoutDetails(request, response, userId);
                break;
            case "edit":
                showEditForm(request, response, userId);
                break;
            case "delete":
                // Redirect GET delete to history; deletions must be executed via POST
                response.sendRedirect(request.getContextPath() + "/workouts");
                break;
            case "list":
            default:
                showWorkoutHistory(request, response, userId);
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

        // CSRF Token Check for all POST actions
        if (!CsrfUtil.isValidToken(request)) {
            request.setAttribute("errorMessage", "Security validation failed. Please refresh the page and try again.");
            showWorkoutHistory(request, response, userId);
            return;
        }

        String action = resolveAction(request);
        String clientIp = getClientIp(request);

        switch (action) {
            case "add":
                handleAddWorkout(request, response, userId, clientIp);
                break;
            case "edit":
                handleEditWorkout(request, response, userId, clientIp);
                break;
            case "delete":
                handleDeleteWorkout(request, response, userId, clientIp);
                break;
            default:
                response.sendRedirect(request.getContextPath() + "/workouts");
                break;
        }
    }

    // ==========================================
    // GET Action Handlers
    // ==========================================

    private void showWorkoutHistory(HttpServletRequest request, HttpServletResponse response, Integer userId)
            throws ServletException, IOException {
        String typeFilter = request.getParameter("type");
        String intensityFilter = request.getParameter("intensity");
        String startDate = request.getParameter("startDate");
        String endDate = request.getParameter("endDate");
        String sortBy = request.getParameter("sortBy");

        int page = 1;
        int pageSize = 15;
        try {
            String pageStr = request.getParameter("page");
            if (pageStr != null && !pageStr.trim().isEmpty()) {
                page = Math.max(1, Integer.parseInt(pageStr.trim()));
            }
        } catch (NumberFormatException ignored) {
            page = 1;
        }
        int offset = (page - 1) * pageSize;

        List<Workout> workouts;
        int totalCount;

        boolean isFiltered = (typeFilter != null && !typeFilter.isEmpty()) ||
                             (intensityFilter != null && !intensityFilter.isEmpty()) ||
                             (startDate != null && !startDate.isEmpty()) ||
                             (endDate != null && !endDate.isEmpty()) ||
                             (sortBy != null && !sortBy.isEmpty() && !"date_desc".equals(sortBy));

        if (isFiltered) {
            workouts = workoutService.getFilteredWorkoutHistory(userId, typeFilter, intensityFilter,
                    startDate, endDate, sortBy, pageSize, offset);
            totalCount = workoutService.getFilteredWorkoutCount(userId, typeFilter, intensityFilter, startDate, endDate);
        } else {
            workouts = workoutService.getWorkoutHistory(userId, pageSize, offset);
            totalCount = workoutService.getWorkoutCount(userId);
        }

        int totalPages = (int) Math.ceil((double) totalCount / pageSize);
        if (totalPages == 0) totalPages = 1;

        request.setAttribute("workouts", workouts);
        request.setAttribute("totalCount", totalCount);
        request.setAttribute("currentPage", page);
        request.setAttribute("totalPages", totalPages);
        request.setAttribute("typeFilter", typeFilter);
        request.setAttribute("intensityFilter", intensityFilter);
        request.setAttribute("startDate", startDate);
        request.setAttribute("endDate", endDate);
        request.setAttribute("sortBy", sortBy != null ? sortBy : "date_desc");
        request.setAttribute("isFiltered", isFiltered);

        request.getRequestDispatcher("/workouts/history.jsp").forward(request, response);
    }

    private void showAddForm(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        if (request.getAttribute("workoutDate") == null) {
            request.setAttribute("workoutDate", LocalDate.now().toString());
        }
        request.getRequestDispatcher("/workouts/add.jsp").forward(request, response);
    }

    private void showWorkoutDetails(HttpServletRequest request, HttpServletResponse response, Integer userId)
            throws ServletException, IOException {
        Integer workoutId = parseIdParam(request, "id");
        if (workoutId == null) {
            response.sendRedirect(request.getContextPath() + "/workouts");
            return;
        }

        try {
            Workout workout = workoutService.getWorkoutDetails(workoutId, userId);
            request.setAttribute("workout", workout);
            request.getRequestDispatcher("/workouts/detail.jsp").forward(request, response);
        } catch (ResourceNotFoundException e) {
            request.getSession().setAttribute("flashError", "The requested workout was not found or access was denied.");
            response.sendRedirect(request.getContextPath() + "/workouts");
        }
    }

    private void showEditForm(HttpServletRequest request, HttpServletResponse response, Integer userId)
            throws ServletException, IOException {
        Integer workoutId = parseIdParam(request, "id");
        if (workoutId == null) {
            response.sendRedirect(request.getContextPath() + "/workouts");
            return;
        }

        try {
            Workout workout = workoutService.getWorkoutDetails(workoutId, userId);
            request.setAttribute("workout", workout);
            request.getRequestDispatcher("/workouts/edit.jsp").forward(request, response);
        } catch (ResourceNotFoundException e) {
            request.getSession().setAttribute("flashError", "The requested workout was not found or access was denied.");
            response.sendRedirect(request.getContextPath() + "/workouts");
        }
    }

    // ==========================================
    // POST Action Handlers
    // ==========================================

    private void handleAddWorkout(HttpServletRequest request, HttpServletResponse response,
                                  Integer userId, String clientIp)
            throws ServletException, IOException {
        String workoutType = request.getParameter("workoutType");
        String workoutDate = request.getParameter("workoutDate");
        String durationStr = request.getParameter("durationMinutes");
        String intensity = request.getParameter("intensity");
        String caloriesStr = request.getParameter("caloriesBurned");
        String notes = request.getParameter("notes");

        Integer duration = null;
        try {
            if (durationStr != null && !durationStr.trim().isEmpty()) {
                duration = Integer.parseInt(durationStr.trim());
            }
        } catch (NumberFormatException ignored) {}

        Integer calories = 0;
        try {
            if (caloriesStr != null && !caloriesStr.trim().isEmpty()) {
                calories = Integer.parseInt(caloriesStr.trim());
            }
        } catch (NumberFormatException ignored) {
            calories = -1; // Trigger validation error
        }

        try {
            Workout created = workoutService.createWorkout(userId, workoutType, workoutDate,
                    duration, intensity, calories, notes, clientIp);

            request.getSession().setAttribute("flashSuccess", "Workout added successfully.");
            response.sendRedirect(request.getContextPath() + "/workouts/view?id=" + created.getWorkoutId());

        } catch (ValidationException e) {
            request.setAttribute("errorMessage", e.getMessage());
            request.setAttribute("workoutType", workoutType);
            request.setAttribute("workoutDate", workoutDate);
            request.setAttribute("durationMinutes", durationStr);
            request.setAttribute("intensity", intensity);
            request.setAttribute("caloriesBurned", caloriesStr);
            request.setAttribute("notes", notes);
            request.getRequestDispatcher("/workouts/add.jsp").forward(request, response);

        } catch (DatabaseException e) {
            request.setAttribute("errorMessage", "Unable to save your workout right now. Please try again.");
            request.getRequestDispatcher("/workouts/add.jsp").forward(request, response);
        }
    }

    private void handleEditWorkout(HttpServletRequest request, HttpServletResponse response,
                                   Integer userId, String clientIp)
            throws ServletException, IOException {
        Integer workoutId = parseIdParam(request, "workoutId");
        if (workoutId == null) {
            workoutId = parseIdParam(request, "id");
        }

        if (workoutId == null) {
            response.sendRedirect(request.getContextPath() + "/workouts");
            return;
        }

        String workoutType = request.getParameter("workoutType");
        String workoutDate = request.getParameter("workoutDate");
        String durationStr = request.getParameter("durationMinutes");
        String intensity = request.getParameter("intensity");
        String caloriesStr = request.getParameter("caloriesBurned");
        String notes = request.getParameter("notes");

        Integer duration = null;
        try {
            if (durationStr != null && !durationStr.trim().isEmpty()) {
                duration = Integer.parseInt(durationStr.trim());
            }
        } catch (NumberFormatException ignored) {}

        Integer calories = 0;
        try {
            if (caloriesStr != null && !caloriesStr.trim().isEmpty()) {
                calories = Integer.parseInt(caloriesStr.trim());
            }
        } catch (NumberFormatException ignored) {
            calories = -1; // Trigger validation error
        }

        try {
            workoutService.updateWorkout(workoutId, userId, workoutType, workoutDate,
                    duration, intensity, calories, notes, clientIp);

            request.getSession().setAttribute("flashSuccess", "Workout updated successfully.");
            response.sendRedirect(request.getContextPath() + "/workouts/view?id=" + workoutId);

        } catch (ValidationException e) {
            request.setAttribute("errorMessage", e.getMessage());
            // Reload original workout to preserve view
            try {
                Workout w = workoutService.getWorkoutDetails(workoutId, userId);
                w.setNotes(notes);
                request.setAttribute("workout", w);
            } catch (ResourceNotFoundException ignored) {}

            request.setAttribute("overrideType", workoutType);
            request.setAttribute("overrideDate", workoutDate);
            request.setAttribute("overrideDuration", durationStr);
            request.setAttribute("overrideIntensity", intensity);
            request.setAttribute("overrideCalories", caloriesStr);
            request.setAttribute("overrideNotes", notes);
            request.getRequestDispatcher("/workouts/edit.jsp").forward(request, response);

        } catch (ResourceNotFoundException e) {
            request.getSession().setAttribute("flashError", "Workout not found or update unauthorized.");
            response.sendRedirect(request.getContextPath() + "/workouts");

        } catch (DatabaseException e) {
            request.setAttribute("errorMessage", "Unable to save your workout right now. Please try again.");
            request.getRequestDispatcher("/workouts/edit.jsp").forward(request, response);
        }
    }

    private void handleDeleteWorkout(HttpServletRequest request, HttpServletResponse response,
                                     Integer userId, String clientIp)
            throws IOException {
        Integer workoutId = parseIdParam(request, "workoutId");
        if (workoutId == null) {
            workoutId = parseIdParam(request, "id");
        }

        if (workoutId == null) {
            response.sendRedirect(request.getContextPath() + "/workouts");
            return;
        }

        try {
            workoutService.deleteWorkout(workoutId, userId, clientIp);
            request.getSession().setAttribute("flashSuccess", "Workout deleted successfully.");
        } catch (ResourceNotFoundException e) {
            request.getSession().setAttribute("flashError", "Workout not found or deletion unauthorized.");
        } catch (DatabaseException e) {
            request.getSession().setAttribute("flashError", "Unable to delete your workout right now. Please try again.");
        }

        response.sendRedirect(request.getContextPath() + "/workouts");
    }

    // ==========================================
    // Routing & Request Parameter Helpers
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

    private String getClientIp(HttpServletRequest request) {
        String xf = request.getHeader("X-Forwarded-For");
        if (xf != null && !xf.isEmpty()) {
            return xf.split(",")[0].trim();
        }
        return request.getRemoteAddr() != null ? request.getRemoteAddr() : "127.0.0.1";
    }
}
