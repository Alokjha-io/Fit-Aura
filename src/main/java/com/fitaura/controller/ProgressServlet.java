package com.fitaura.controller;

import com.fitaura.service.ProgressService;
import com.fitaura.service.impl.ProgressServiceImpl;
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
 * Controller serving the Fitness Progress Dashboard:
 * - Aggregated lifetime workout stats (count, duration, calories)
 * - Weekly activity distribution (Mon-Sun)
 * - Monthly summary
 * - Active goals tracking
 * - Informational BMI, BMR, and Daily Calorie estimates
 * - Recent activity stream
 */
@WebServlet(name = "ProgressServlet", urlPatterns = {"/progress", "/progress/*"})
public class ProgressServlet extends HttpServlet {

    private static final long serialVersionUID = 1L;
    private static final Logger logger = LoggerFactory.getLogger(ProgressServlet.class);

    private final ProgressService progressService;

    public ProgressServlet() {
        this(new ProgressServiceImpl());
    }

    public ProgressServlet(ProgressService progressService) {
        this.progressService = progressService;
    }

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        Integer userId = SessionUtil.getAuthenticatedUserId(request);
        if (userId == null) {
            response.sendRedirect(request.getContextPath() + "/login");
            return;
        }

        ProgressService.ProgressSummary summary = progressService.getUserProgressSummary(userId);
        request.setAttribute("summary", summary);

        request.getRequestDispatcher("/progress/index.jsp").forward(request, response);
    }
}
