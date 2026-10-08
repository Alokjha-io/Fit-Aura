package com.fitaura.controller;

import com.fitaura.util.AppConstants;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

import java.io.IOException;

/**
 * HomeController manages navigation to the FitAura welcome and landing page.
 * Sets foundational attributes such as application name, version, and architecture phase.
 */
@WebServlet(name = "HomeController", urlPatterns = {"/home", "/welcome", "/landing"})
public class HomeController extends HttpServlet {

    private static final long serialVersionUID = 1L;

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        // Expose foundational application metadata to JSP
        request.setAttribute("appName", AppConstants.APP_NAME);
        request.setAttribute("appTagline", AppConstants.APP_TAGLINE);
        request.setAttribute("appVersion", AppConstants.APP_VERSION);
        request.setAttribute("appPhase", AppConstants.APP_PHASE);

        // Forward to the welcome view
        request.getRequestDispatcher("/index.jsp").forward(request, response);
    }
}
