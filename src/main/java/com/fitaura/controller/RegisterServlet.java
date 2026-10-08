package com.fitaura.controller;

import com.fitaura.exception.ValidationException;
import com.fitaura.model.User;
import com.fitaura.service.AuthenticationService;
import com.fitaura.service.impl.AuthenticationServiceImpl;
import com.fitaura.util.CsrfUtil;
import com.fitaura.util.SessionUtil;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

import java.io.IOException;

/**
 * Controller handling user registration page rendering and account creation.
 */
@WebServlet(name = "RegisterServlet", urlPatterns = {"/register"})
public class RegisterServlet extends HttpServlet {

    private static final long serialVersionUID = 1L;

    private final AuthenticationService authService;

    public RegisterServlet() {
        this.authService = new AuthenticationServiceImpl();
    }

    public RegisterServlet(AuthenticationService authService) {
        this.authService = authService;
    }

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        // If already authenticated, redirect to user dashboard
        if (SessionUtil.isAuthenticated(request)) {
            response.sendRedirect(request.getContextPath() + "/user/dashboard");
            return;
        }

        // Initialize CSRF token
        CsrfUtil.getToken(request.getSession(true));

        request.getRequestDispatcher("/register.jsp").forward(request, response);
    }

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        // 1. Validate CSRF token
        if (!CsrfUtil.isValidToken(request)) {
            request.setAttribute("errorMessage", "Security validation failed. Please refresh the page and try again.");
            request.getRequestDispatcher("/register.jsp").forward(request, response);
            return;
        }

        String fullName = request.getParameter("fullName");
        String email = request.getParameter("email");
        String password = request.getParameter("password");
        String confirmPassword = request.getParameter("confirmPassword");
        String displayName = request.getParameter("displayName");
        String clientIp = getClientIp(request);

        try {
            User newUser = authService.register(fullName, email, password, confirmPassword, displayName, clientIp);

            // Automatically establish authenticated session for the newly registered user
            SessionUtil.createAuthenticatedSession(request, newUser);

            // Redirect to fitness profile onboarding
            response.sendRedirect(request.getContextPath() + "/onboarding");

        } catch (ValidationException e) {
            // Preserve user inputs but NEVER preserve passwords
            request.setAttribute("fullName", fullName != null ? fullName.trim() : "");
            request.setAttribute("email", email != null ? email.trim() : "");
            request.setAttribute("displayName", displayName != null ? displayName.trim() : "");
            request.setAttribute("errorMessage", e.getMessage());

            request.getRequestDispatcher("/register.jsp").forward(request, response);
        }
    }

    private String getClientIp(HttpServletRequest request) {
        String xForwarded = request.getHeader("X-Forwarded-For");
        if (xForwarded != null && !xForwarded.isEmpty()) {
            return xForwarded.split(",")[0].trim();
        }
        return request.getRemoteAddr();
    }
}
