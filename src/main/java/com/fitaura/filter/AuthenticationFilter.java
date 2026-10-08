package com.fitaura.filter;

import com.fitaura.util.SessionUtil;
import jakarta.servlet.*;
import jakarta.servlet.annotation.WebFilter;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

import java.io.IOException;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;

/**
 * AuthenticationFilter intercepts requests to protected application resources,
 * ensuring that unauthenticated users are redirected to the login endpoint.
 */
@WebFilter(filterName = "AuthenticationFilter", urlPatterns = {"/*"})
public class AuthenticationFilter implements Filter {

    @Override
    public void doFilter(ServletRequest request, ServletResponse response, FilterChain chain)
            throws IOException, ServletException {
        HttpServletRequest httpRequest = (HttpServletRequest) request;
        HttpServletResponse httpResponse = (HttpServletResponse) response;

        String requestUri = httpRequest.getRequestURI();
        String contextPath = httpRequest.getContextPath();
        String relativePath = requestUri.substring(contextPath.length());

        if (isPublicPath(relativePath)) {
            chain.doFilter(request, response);
            return;
        }

        // Check authentication state
        if (!SessionUtil.isAuthenticated(httpRequest)) {
            String redirectParam = "";
            if (!"GET".equalsIgnoreCase(httpRequest.getMethod()) || !relativePath.isEmpty()) {
                redirectParam = "?redirect=" + URLEncoder.encode(relativePath, StandardCharsets.UTF_8);
            }
            httpResponse.sendRedirect(contextPath + "/login" + redirectParam);
            return;
        }

        chain.doFilter(request, response);
    }

    private boolean isPublicPath(String path) {
        if (path == null || path.isEmpty() || "/".equals(path) || "/index.jsp".equals(path)) {
            return true;
        }

        // Authentication endpoints
        if (path.startsWith("/login") || path.startsWith("/register") || path.startsWith("/logout") ||
            path.startsWith("/welcome") || path.startsWith("/home")) {
            return true;
        }

        // Static resources
        if (path.startsWith("/css/") || path.startsWith("/js/") ||
            path.startsWith("/images/") || path.startsWith("/error/") ||
            path.startsWith("/favicon.ico")) {
            return true;
        }

        // Operations health checks
        if (path.startsWith("/api/health") || path.startsWith("/health")) {
            return true;
        }

        return false;
    }
}
