package com.fitaura.controller;

import com.fitaura.exception.ValidationException;
import com.fitaura.model.SystemSetting;
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
 * Controller serving administrative System Settings management.
 */
@WebServlet(name = "AdminSettingsServlet", urlPatterns = {"/admin/settings", "/admin/configuration"})
public class AdminSettingsServlet extends HttpServlet {

    private static final long serialVersionUID = 1L;
    private static final Logger logger = LoggerFactory.getLogger(AdminSettingsServlet.class);

    private final AdminService adminService;

    public AdminSettingsServlet() {
        this(new AdminServiceImpl());
    }

    public AdminSettingsServlet(AdminService adminService) {
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
            List<SystemSetting> settings = adminService.getSystemSettings();
            request.setAttribute("settings", settings);
            request.getRequestDispatcher("/admin/settings.jsp").forward(request, response);
        } catch (Exception e) {
            logger.error("Error loading system settings: {}", e.getMessage(), e);
            request.setAttribute("errorMessage", "Unable to load system settings.");
            request.getRequestDispatcher("/admin/settings.jsp").forward(request, response);
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

        if (!CsrfUtil.isValidToken(request)) {
            logger.warn("CSRF token validation failed in AdminSettingsServlet from IP {}", request.getRemoteAddr());
            response.sendRedirect(request.getContextPath() + "/admin/settings?error=" +
                    URLEncoder.encode("Security validation failed. Please try again.", StandardCharsets.UTF_8));
            return;
        }

        String key = request.getParameter("settingKey");
        String value = request.getParameter("settingValue");
        Integer adminUserId = SessionUtil.getAuthenticatedUserId(request);
        String clientIp = request.getRemoteAddr();

        try {
            adminService.updateSystemSetting(key, value, adminUserId, clientIp);
            response.sendRedirect(request.getContextPath() + "/admin/settings?success=" +
                    URLEncoder.encode("Setting '" + key + "' successfully updated to '" + value + "'.", StandardCharsets.UTF_8));

        } catch (ValidationException e) {
            response.sendRedirect(request.getContextPath() + "/admin/settings?error=" +
                    URLEncoder.encode(e.getMessage(), StandardCharsets.UTF_8));
        } catch (Exception e) {
            logger.error("Error updating system setting: {}", e.getMessage(), e);
            response.sendRedirect(request.getContextPath() + "/admin/settings?error=" +
                    URLEncoder.encode("Failed to update system setting.", StandardCharsets.UTF_8));
        }
    }
}
