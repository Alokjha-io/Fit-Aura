package com.fitaura.controller;

import com.fitaura.model.ActivityLog;
import com.fitaura.service.AdminService;
import com.fitaura.service.impl.AdminServiceImpl;
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
 * Controller serving the administrative Activity & Audit Log viewer.
 */
@WebServlet(name = "AdminActivityServlet", urlPatterns = {"/admin/activity", "/admin/logs", "/admin/audit"})
public class AdminActivityServlet extends HttpServlet {

    private static final long serialVersionUID = 1L;
    private static final Logger logger = LoggerFactory.getLogger(AdminActivityServlet.class);

    private final AdminService adminService;

    public AdminActivityServlet() {
        this(new AdminServiceImpl());
    }

    public AdminActivityServlet(AdminService adminService) {
        this.adminService = adminService;
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
            String actionType = request.getParameter("action");
            String entityType = request.getParameter("entity");
            String userIdStr = request.getParameter("userId");
            String pageStr = request.getParameter("page");

            Integer filterUserId = null;
            if (userIdStr != null && !userIdStr.trim().isEmpty()) {
                try {
                    filterUserId = Integer.parseInt(userIdStr.trim());
                } catch (NumberFormatException ignored) {}
            }

            int page = 1;
            if (pageStr != null && !pageStr.trim().isEmpty()) {
                try {
                    page = Math.max(1, Integer.parseInt(pageStr.trim()));
                } catch (NumberFormatException ignored) {}
            }

            int pageSize = 25;
            int offset = (page - 1) * pageSize;

            List<ActivityLog> logs = adminService.getActivityLogs(filterUserId, actionType, entityType, pageSize, offset);
            int totalLogs = adminService.countActivityLogs(filterUserId, actionType, entityType);
            int totalPages = (int) Math.ceil((double) totalLogs / pageSize);
            if (totalPages < 1) totalPages = 1;

            request.setAttribute("logs", logs);
            request.setAttribute("totalLogs", totalLogs);
            request.setAttribute("currentPage", page);
            request.setAttribute("totalPages", totalPages);
            request.setAttribute("selectedAction", actionType);
            request.setAttribute("selectedEntity", entityType);
            request.setAttribute("selectedUserId", userIdStr);

            request.getRequestDispatcher("/admin/activity.jsp").forward(request, response);

        } catch (Exception e) {
            logger.error("Error retrieving activity logs: {}", e.getMessage(), e);
            request.setAttribute("errorMessage", "Unable to load activity logs.");
            request.getRequestDispatcher("/admin/activity.jsp").forward(request, response);
        }
    }
}
