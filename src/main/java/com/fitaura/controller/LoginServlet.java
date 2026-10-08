package com.fitaura.controller;

import com.fitaura.exception.AuthenticationException;
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
 * Controller handling user login page rendering and authentication verification.
 */
@WebServlet(name = "LoginServlet", urlPatterns = {"/login"})
public class LoginServlet extends HttpServlet {

    private static final long serialVersionUID = 1L;

    private final AuthenticationService authService;

    public LoginServlet() {
        this.authService = new AuthenticationServiceImpl();
    }

    public LoginServlet(AuthenticationService authService) {
        this.authService = authService;
    }

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        // If already authenticated, redirect to appropriate home
        if (SessionUtil.isAuthenticated(request)) {
            if (SessionUtil.isAdmin(request)) {
                response.sendRedirect(request.getContextPath() + "/admin/dashboard");
            } else {
                response.sendRedirect(request.getContextPath() + "/user/dashboard");
            }
            return;
        }

        // Initialize CSRF token
        CsrfUtil.getToken(request.getSession(true));

        request.getRequestDispatcher("/login.jsp").forward(request, response);
    }

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        // 1. Validate CSRF token
        if (!CsrfUtil.isValidToken(request)) {
            request.setAttribute("errorMessage", "Security validation failed. Please refresh the page and try again.");
            request.getRequestDispatcher("/login.jsp").forward(request, response);
            return;
        }

        String email = request.getParameter("email");
        String password = request.getParameter("password");
        String redirectTarget = request.getParameter("redirect");
        String clientIp = getClientIp(request);

        try {
            User authenticatedUser = authService.authenticate(email, password, clientIp);

            // Establish secure session with session fixation protection
            SessionUtil.createAuthenticatedSession(request, authenticatedUser);

            // Determine safe redirect URL
            if (redirectTarget != null && isValidInternalRedirect(redirectTarget, request.getContextPath())) {
                response.sendRedirect(request.getContextPath() + redirectTarget);
                return;
            }

            if (authenticatedUser.getRole() == User.Role.ADMIN) {
                response.sendRedirect(request.getContextPath() + "/admin/dashboard");
            } else {
                response.sendRedirect(request.getContextPath() + "/user/dashboard");
            }

        } catch (AuthenticationException e) {
            // Keep email in form but NEVER preserve the password
            request.setAttribute("email", email != null ? email.trim() : "");
            request.setAttribute("errorMessage", e.getMessage());
            if (redirectTarget != null) {
                request.setAttribute("redirect", redirectTarget);
            }
            request.getRequestDispatcher("/login.jsp").forward(request, response);
        }
    }

    private String getClientIp(HttpServletRequest request) {
        String xForwarded = request.getHeader("X-Forwarded-For");
        if (xForwarded != null && !xForwarded.isEmpty()) {
            return xForwarded.split(",")[0].trim();
        }
        return request.getRemoteAddr();
    }

    private boolean isValidInternalRedirect(String target, String contextPath) {
        if (target == null || target.isEmpty()) {
            return false;
        }
        // Must start with '/' and not contain protocol or double slashes (prevent open redirect attacks)
        return target.startsWith("/") && !target.startsWith("//") && !target.contains("\\");
    }
}
