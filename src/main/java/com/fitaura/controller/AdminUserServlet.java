package com.fitaura.controller;

import com.fitaura.dto.AdminUserDetailDTO;
import com.fitaura.exception.FitAuraException;
import com.fitaura.exception.ValidationException;
import com.fitaura.model.User;
import com.fitaura.service.AdminService;
import com.fitaura.service.impl.AdminServiceImpl;
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
 * Controller serving administrative User Management:
 * - User listing, search, filtering, and pagination
 * - User detail inspection
 * - Account status updates (Activate, Deactivate, Block, Unblock)
 * - Self-protection and CSRF enforcement
 */
@WebServlet(name = "AdminUserServlet", urlPatterns = {"/admin/users", "/admin/users/*"})
public class AdminUserServlet extends HttpServlet {

    private static final long serialVersionUID = 1L;
    private static final Logger logger = LoggerFactory.getLogger(AdminUserServlet.class);

    private final AdminService adminService;

    public AdminUserServlet() {
        this(new AdminServiceImpl());
    }

    public AdminUserServlet(AdminService adminService) {
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

        String pathInfo = request.getPathInfo();
        if (pathInfo == null || "/".equals(pathInfo) || "".equals(pathInfo)) {
            handleListUsers(request, response);
        } else if ("/view".equals(pathInfo)) {
            handleViewUser(request, response);
        } else {
            response.sendRedirect(request.getContextPath() + "/admin/users");
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

        // Verify CSRF token
        if (!CsrfUtil.isValidToken(request)) {
            logger.warn("CSRF token validation failed in AdminUserServlet from IP {}", request.getRemoteAddr());
            response.sendRedirect(request.getContextPath() + "/admin/users?error=" +
                    URLEncoder.encode("Security validation failed. Please try again.", StandardCharsets.UTF_8));
            return;
        }

        String pathInfo = request.getPathInfo();
        if ("/status".equals(pathInfo)) {
            handleUpdateStatus(request, response);
        } else {
            response.sendRedirect(request.getContextPath() + "/admin/users");
        }
    }

    private void handleListUsers(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        try {
            String search = request.getParameter("search");
            String roleStr = request.getParameter("role");
            String statusStr = request.getParameter("status");
            String privacyStr = request.getParameter("privacy");
            String pageStr = request.getParameter("page");

            User.Role role = parseEnum(User.Role.class, roleStr);
            User.AccountStatus status = parseEnum(User.AccountStatus.class, statusStr);
            User.PrivacyMode privacy = parseEnum(User.PrivacyMode.class, privacyStr);

            int page = 1;
            if (pageStr != null && !pageStr.trim().isEmpty()) {
                try {
                    page = Math.max(1, Integer.parseInt(pageStr.trim()));
                } catch (NumberFormatException ignored) {}
            }

            int pageSize = 20;
            int offset = (page - 1) * pageSize;

            List<User> users = adminService.listUsers(search, role, status, privacy, pageSize, offset);
            int totalMatching = adminService.countUsers(search, role, status, privacy);
            int totalPages = (int) Math.ceil((double) totalMatching / pageSize);
            if (totalPages < 1) totalPages = 1;

            request.setAttribute("users", users);
            request.setAttribute("totalUsers", totalMatching);
            request.setAttribute("currentPage", page);
            request.setAttribute("totalPages", totalPages);
            request.setAttribute("searchQuery", search);
            request.setAttribute("selectedRole", roleStr);
            request.setAttribute("selectedStatus", statusStr);
            request.setAttribute("selectedPrivacy", privacyStr);

            request.getRequestDispatcher("/admin/users.jsp").forward(request, response);

        } catch (Exception e) {
            logger.error("Error listing users in admin portal: {}", e.getMessage(), e);
            request.setAttribute("errorMessage", "Failed to retrieve user directory.");
            request.getRequestDispatcher("/admin/users.jsp").forward(request, response);
        }
    }

    private void handleViewUser(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        String idStr = request.getParameter("id");
        if (idStr == null || idStr.trim().isEmpty()) {
            response.sendRedirect(request.getContextPath() + "/admin/users");
            return;
        }

        try {
            int userId = Integer.parseInt(idStr.trim());
            AdminUserDetailDTO userDetail = adminService.getUserDetails(userId);
            request.setAttribute("userDetail", userDetail);
            request.getRequestDispatcher("/admin/user-detail.jsp").forward(request, response);

        } catch (NumberFormatException e) {
            response.sendRedirect(request.getContextPath() + "/admin/users?error=" +
                    URLEncoder.encode("Invalid user identifier provided.", StandardCharsets.UTF_8));
        } catch (FitAuraException e) {
            response.sendRedirect(request.getContextPath() + "/admin/users?error=" +
                    URLEncoder.encode(e.getMessage(), StandardCharsets.UTF_8));
        } catch (Exception e) {
            logger.error("Error viewing user detail: {}", e.getMessage(), e);
            response.sendRedirect(request.getContextPath() + "/admin/users?error=" +
                    URLEncoder.encode("Unable to load user details.", StandardCharsets.UTF_8));
        }
    }

    private void handleUpdateStatus(HttpServletRequest request, HttpServletResponse response)
            throws IOException {
        String targetIdStr = request.getParameter("userId");
        String newStatusStr = request.getParameter("status");
        Integer adminUserId = SessionUtil.getAuthenticatedUserId(request);
        String clientIp = request.getRemoteAddr();

        if (targetIdStr == null || newStatusStr == null) {
            response.sendRedirect(request.getContextPath() + "/admin/users?error=" +
                    URLEncoder.encode("Missing required parameters for status change.", StandardCharsets.UTF_8));
            return;
        }

        try {
            int targetUserId = Integer.parseInt(targetIdStr.trim());
            User.AccountStatus newStatus = User.AccountStatus.valueOf(newStatusStr.trim().toUpperCase());

            adminService.updateUserStatus(targetUserId, newStatus, adminUserId, clientIp);

            String returnUrl = request.getParameter("returnUrl");
            String redirectTarget = (returnUrl != null && !returnUrl.trim().isEmpty())
                    ? returnUrl
                    : request.getContextPath() + "/admin/users";

            String glue = redirectTarget.contains("?") ? "&" : "?";
            response.sendRedirect(redirectTarget + glue + "success=" +
                    URLEncoder.encode("User account status successfully updated to " + newStatus, StandardCharsets.UTF_8));

        } catch (IllegalArgumentException e) {
            response.sendRedirect(request.getContextPath() + "/admin/users?error=" +
                    URLEncoder.encode("Invalid status value provided.", StandardCharsets.UTF_8));
        } catch (ValidationException e) {
            response.sendRedirect(request.getContextPath() + "/admin/users?error=" +
                    URLEncoder.encode(e.getMessage(), StandardCharsets.UTF_8));
        } catch (Exception e) {
            logger.error("Error updating account status: {}", e.getMessage(), e);
            response.sendRedirect(request.getContextPath() + "/admin/users?error=" +
                    URLEncoder.encode("Failed to update user status due to a system error.", StandardCharsets.UTF_8));
        }
    }

    private <T extends Enum<T>> T parseEnum(Class<T> enumClass, String val) {
        if (val == null || val.trim().isEmpty()) return null;
        try {
            return Enum.valueOf(enumClass, val.trim().toUpperCase());
        } catch (IllegalArgumentException e) {
            return null;
        }
    }
}
