package com.fitaura.controller;

import com.fitaura.model.Competition;
import com.fitaura.model.User;
import com.fitaura.service.AdminService;
import com.fitaura.service.SocialFitnessService;
import com.fitaura.service.impl.AdminServiceImpl;
import com.fitaura.service.impl.SocialFitnessServiceImpl;
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
import java.sql.Date;
import java.util.List;

@WebServlet(name = "AdminSocialServlet", urlPatterns = {"/admin/social", "/admin/social/*", "/admin/competitions", "/admin/competitions/*"})
public class AdminSocialServlet extends HttpServlet {

    private static final long serialVersionUID = 1L;
    private static final Logger logger = LoggerFactory.getLogger(AdminSocialServlet.class);

    private SocialFitnessService socialService;
    private AdminService adminService;

    @Override
    public void init() throws ServletException {
        super.init();
        this.socialService = new SocialFitnessServiceImpl();
        this.adminService = new AdminServiceImpl();
    }

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        String pathInfo = request.getPathInfo();
        Integer adminUserId = SessionUtil.getAuthenticatedUserId(request);

        try {
            if (pathInfo != null && pathInfo.startsWith("/edit")) {
                handleEdit(request, response, adminUserId);
                return;
            }

            List<Competition> active = socialService.getActiveCompetitions(adminUserId);
            List<Competition> upcoming = socialService.getUpcomingCompetitions(adminUserId);
            List<Competition> completed = socialService.getCompletedCompetitions(adminUserId);

            int socialUserCount = adminService.countUsers(null, null, null, User.PrivacyMode.SOCIAL);

            request.setAttribute("activeCompetitions", active);
            request.setAttribute("upcomingCompetitions", upcoming);
            request.setAttribute("completedCompetitions", completed);
            request.setAttribute("socialUserCount", socialUserCount);

            request.getRequestDispatcher("/admin/social.jsp").forward(request, response);
        } catch (Exception e) {
            logger.error("Error in admin social servlet: {}", e.getMessage(), e);
            request.setAttribute("errorMessage", "Failed to load social management: " + e.getMessage());
            request.getRequestDispatcher("/admin/social.jsp").forward(request, response);
        }
    }

    private void handleEdit(HttpServletRequest request, HttpServletResponse response, Integer adminUserId)
            throws ServletException, IOException {
        String idStr = request.getParameter("id");
        if (idStr == null || idStr.isEmpty()) {
            response.sendRedirect(request.getContextPath() + "/admin/social");
            return;
        }
        int compId = Integer.parseInt(idStr);
        Competition comp = socialService.getCompetitionDetails(compId, adminUserId);
        request.setAttribute("editCompetition", comp);
        request.getRequestDispatcher("/admin/competition-edit.jsp").forward(request, response);
    }

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        if (!CsrfUtil.isValidToken(request)) {
            response.sendError(HttpServletResponse.SC_FORBIDDEN, "Invalid or missing CSRF token.");
            return;
        }

        Integer adminUserId = SessionUtil.getAuthenticatedUserId(request);
        String clientIp = request.getRemoteAddr();
        String action = request.getParameter("action");

        try {
            if ("create".equalsIgnoreCase(action)) {
                String name = request.getParameter("name");
                String description = request.getParameter("description");
                String metricStr = request.getParameter("metric");
                String targetStr = request.getParameter("targetValue");
                String startStr = request.getParameter("startDate");
                String endStr = request.getParameter("endDate");
                String pointsStr = request.getParameter("rewardPoints");
                String statusStr = request.getParameter("status");

                Competition comp = new Competition();
                comp.setName(name != null ? name.trim() : "");
                comp.setDescription(description != null ? description.trim() : "");
                comp.setMetric(metricStr != null ? Competition.Metric.valueOf(metricStr) : Competition.Metric.WORKOUT_COUNT);
                if (targetStr != null && !targetStr.trim().isEmpty()) {
                    comp.setTargetValue(new BigDecimal(targetStr.trim()));
                }
                comp.setStartDate(Date.valueOf(startStr));
                comp.setEndDate(Date.valueOf(endStr));
                comp.setRewardPoints(pointsStr != null && !pointsStr.trim().isEmpty() ? Integer.parseInt(pointsStr.trim()) : 50);
                comp.setStatus(statusStr != null ? Competition.Status.valueOf(statusStr) : Competition.Status.ACTIVE);

                socialService.createCompetition(comp, adminUserId, clientIp);
                response.sendRedirect(request.getContextPath() + "/admin/social?success=created");
                return;

            } else if ("update".equalsIgnoreCase(action)) {
                int compId = Integer.parseInt(request.getParameter("competitionId"));
                String name = request.getParameter("name");
                String description = request.getParameter("description");
                String metricStr = request.getParameter("metric");
                String targetStr = request.getParameter("targetValue");
                String startStr = request.getParameter("startDate");
                String endStr = request.getParameter("endDate");
                String pointsStr = request.getParameter("rewardPoints");
                String statusStr = request.getParameter("status");

                Competition comp = new Competition();
                comp.setCompetitionId(compId);
                comp.setName(name != null ? name.trim() : "");
                comp.setDescription(description != null ? description.trim() : "");
                comp.setMetric(metricStr != null ? Competition.Metric.valueOf(metricStr) : Competition.Metric.WORKOUT_COUNT);
                if (targetStr != null && !targetStr.trim().isEmpty()) {
                    comp.setTargetValue(new BigDecimal(targetStr.trim()));
                }
                comp.setStartDate(Date.valueOf(startStr));
                comp.setEndDate(Date.valueOf(endStr));
                comp.setRewardPoints(pointsStr != null && !pointsStr.trim().isEmpty() ? Integer.parseInt(pointsStr.trim()) : 50);
                comp.setStatus(statusStr != null ? Competition.Status.valueOf(statusStr) : Competition.Status.ACTIVE);

                socialService.updateCompetition(comp, adminUserId, clientIp);
                response.sendRedirect(request.getContextPath() + "/admin/social?success=updated");
                return;

            } else if ("delete".equalsIgnoreCase(action)) {
                int compId = Integer.parseInt(request.getParameter("competitionId"));
                socialService.deleteCompetition(compId, adminUserId, clientIp);
                response.sendRedirect(request.getContextPath() + "/admin/social?success=deleted");
                return;
            }

            response.sendRedirect(request.getContextPath() + "/admin/social");

        } catch (Exception e) {
            logger.warn("Admin social action '{}' failed: {}", action, e.getMessage());
            request.getSession().setAttribute("errorMessage", e.getMessage());
            response.sendRedirect(request.getContextPath() + "/admin/social?error=" + e.getMessage());
        }
    }
}
