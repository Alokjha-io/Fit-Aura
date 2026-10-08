package com.fitaura.controller;

import com.fitaura.model.UserGuidance;
import com.fitaura.service.GuidanceService;
import com.fitaura.service.impl.GuidanceServiceImpl;
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
 * Controller serving the Personalized Guidance module:
 * - Daily motivational tips
 * - Workout frequency and workload advice
 * - Goal-oriented strategy suggestions
 * - Nutrition and energy balance guidance
 * - Environment-aware exercise recommendations
 */
@WebServlet(name = "GuidanceServlet", urlPatterns = {"/guidance", "/guidance/*"})
public class GuidanceServlet extends HttpServlet {

    private static final long serialVersionUID = 1L;
    private static final Logger logger = LoggerFactory.getLogger(GuidanceServlet.class);

    private final GuidanceService guidanceService;

    public GuidanceServlet() {
        this(new GuidanceServiceImpl());
    }

    public GuidanceServlet(GuidanceService guidanceService) {
        this.guidanceService = guidanceService;
    }

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        Integer userId = SessionUtil.getAuthenticatedUserId(request);
        if (userId == null) {
            response.sendRedirect(request.getContextPath() + "/login");
            return;
        }

        try {
            UserGuidance guidance = guidanceService.generateUserGuidance(userId);
            request.setAttribute("guidance", guidance);
            request.getRequestDispatcher("/guidance/index.jsp").forward(request, response);
        } catch (Exception e) {
            logger.error("Error generating guidance for user {}: {}", userId, e.getMessage());
            UserGuidance fallback = new UserGuidance();
            fallback.setUserId(userId);
            request.setAttribute("guidance", fallback);
            request.setAttribute("errorMessage", "Unable to refresh all guidance metrics right now. General recommendations are shown.");
            request.getRequestDispatcher("/guidance/index.jsp").forward(request, response);
        }
    }
}
