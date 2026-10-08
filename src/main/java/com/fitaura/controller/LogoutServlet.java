package com.fitaura.controller;

import com.fitaura.service.AuthenticationService;
import com.fitaura.service.impl.AuthenticationServiceImpl;
import com.fitaura.util.SessionUtil;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

import java.io.IOException;

/**
 * Controller handling user logout and session invalidation.
 */
@WebServlet(name = "LogoutServlet", urlPatterns = {"/logout"})
public class LogoutServlet extends HttpServlet {

    private static final long serialVersionUID = 1L;

    private final AuthenticationService authService;

    public LogoutServlet() {
        this.authService = new AuthenticationServiceImpl();
    }

    public LogoutServlet(AuthenticationService authService) {
        this.authService = authService;
    }

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        processLogout(request, response);
    }

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        processLogout(request, response);
    }

    private void processLogout(HttpServletRequest request, HttpServletResponse response)
            throws IOException {
        Integer userId = SessionUtil.getAuthenticatedUserId(request);
        String clientIp = request.getRemoteAddr();

        // Record security audit event
        authService.recordLogout(userId, clientIp);

        // Invalidate HttpSession
        SessionUtil.invalidateSession(request);

        // Redirect to login page with logged_out banner flag
        response.sendRedirect(request.getContextPath() + "/login?logged_out=true");
    }
}
