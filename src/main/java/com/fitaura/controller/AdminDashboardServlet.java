package com.fitaura.controller;

import com.fitaura.dto.AdminDashboardSummaryDTO;
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

/**
 * Controller serving the administrative dashboard overview.
 * Requires authenticated session with ADMIN role.
 */
@WebServlet(name = "AdminDashboardServlet", urlPatterns = {"/admin", "/admin/dashboard", "/admin/home"})
public class AdminDashboardServlet extends HttpServlet {

    private static final long serialVersionUID = 1L;
    private static final Logger logger = LoggerFactory.getLogger(AdminDashboardServlet.class);

    private final AdminService adminService;

    public AdminDashboardServlet() {
        this(new AdminServiceImpl());
    }

    public AdminDashboardServlet(AdminService adminService) {
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
            AdminDashboardSummaryDTO summary = adminService.getDashboardSummary();
            request.setAttribute("summary", summary);
            request.getRequestDispatcher("/admin/dashboard.jsp").forward(request, response);
        } catch (Exception e) {
            logger.error("Error loading admin dashboard overview: {}", e.getMessage(), e);
            request.setAttribute("errorMessage", "Unable to load complete dashboard overview metrics.");
            request.setAttribute("summary", new AdminDashboardSummaryDTO());
            request.getRequestDispatcher("/admin/dashboard.jsp").forward(request, response);
        }
    }
}
