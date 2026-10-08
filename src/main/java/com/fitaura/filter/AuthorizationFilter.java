package com.fitaura.filter;

import com.fitaura.util.SessionUtil;
import jakarta.servlet.*;
import jakarta.servlet.annotation.WebFilter;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;

/**
 * AuthorizationFilter enforces role-based access control (RBAC).
 * Specifically restricts administrative resources under '/admin/*' exclusively to users with the ADMIN role.
 */
@WebFilter(filterName = "AuthorizationFilter", urlPatterns = {"/admin/*"})
public class AuthorizationFilter implements Filter {

    private static final Logger logger = LoggerFactory.getLogger(AuthorizationFilter.class);

    @Override
    public void doFilter(ServletRequest request, ServletResponse response, FilterChain chain)
            throws IOException, ServletException {
        HttpServletRequest httpRequest = (HttpServletRequest) request;
        HttpServletResponse httpResponse = (HttpServletResponse) response;

        if (!SessionUtil.isAuthenticated(httpRequest)) {
            // Unauthenticated requests are directed to login
            httpResponse.sendRedirect(httpRequest.getContextPath() + "/login");
            return;
        }

        if (!SessionUtil.isAdmin(httpRequest)) {
            // User is authenticated but lacks required ADMIN privileges
            logger.warn("Security alert: Unauthorized access attempt to {} by user {} (role: {}) from IP {}",
                    httpRequest.getRequestURI(),
                    SessionUtil.getAuthenticatedUserId(httpRequest),
                    SessionUtil.getAuthenticatedUserRole(httpRequest),
                    httpRequest.getRemoteAddr());
            httpResponse.setStatus(HttpServletResponse.SC_FORBIDDEN);
            httpRequest.getRequestDispatcher("/error/unauthorized.jsp").forward(request, response);
            return;
        }

        chain.doFilter(request, response);
    }
}
