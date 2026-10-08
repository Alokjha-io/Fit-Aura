package com.fitaura.controller;

import com.fitaura.dto.AdminStatisticsDTO;
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
 * Controller serving detailed administrative system statistics and platform health metrics.
 */
@WebServlet(name = "AdminStatisticsServlet", urlPatterns = {"/admin/statistics", "/admin/stats"})
public class AdminStatisticsServlet extends HttpServlet {

    private static final long serialVersionUID = 1L;
    private static final Logger logger = LoggerFactory.getLogger(AdminStatisticsServlet.class);

    private final AdminService adminService;

    public AdminStatisticsServlet() {
        this(new AdminServiceImpl());
    }

    public AdminStatisticsServlet(AdminService adminService) {
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
            AdminStatisticsDTO stats = adminService.getSystemStatistics();
            request.setAttribute("stats", stats);
            request.getRequestDispatcher("/admin/statistics.jsp").forward(request, response);
        } catch (Exception e) {
            logger.error("Error retrieving admin system statistics: {}", e.getMessage(), e);
            request.setAttribute("errorMessage", "Unable to aggregate detailed platform statistics.");
            request.setAttribute("stats", new AdminStatisticsDTO());
            request.getRequestDispatcher("/admin/statistics.jsp").forward(request, response);
        }
    }
}
