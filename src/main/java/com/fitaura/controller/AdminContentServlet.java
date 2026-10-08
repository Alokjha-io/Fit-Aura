package com.fitaura.controller;

import com.fitaura.dto.FitnessContentDetailDTO;
import com.fitaura.exception.FitAuraException;
import com.fitaura.model.FitnessContent;
import com.fitaura.service.FitnessContentService;
import com.fitaura.service.impl.FitnessContentServiceImpl;
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
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.util.List;

/**
 * Controller serving administrative Fitness Content Moderation & Management:
 * - Review pending content submissions
 * - Approve or reject educational articles and guides
 * - Delete inappropriate content
 * - Filter across approval statuses and categories
 */
@WebServlet(name = "AdminContentServlet", urlPatterns = {"/admin/content", "/admin/content/*"})
public class AdminContentServlet extends HttpServlet {

    private static final long serialVersionUID = 1L;
    private static final Logger logger = LoggerFactory.getLogger(AdminContentServlet.class);

    private final FitnessContentService contentService;

    public AdminContentServlet() {
        this(new FitnessContentServiceImpl());
    }

    public AdminContentServlet(FitnessContentService contentService) {
        this.contentService = contentService;
    }

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        if (!SessionUtil.isAuthenticated(request) || !SessionUtil.isAdmin(request)) {
            response.setStatus(HttpServletResponse.SC_FORBIDDEN);
            request.getRequestDispatcher("/error/unauthorized.jsp").forward(request, response);
            return;
        }

        String pathInfo = request.getPathInfo();
        if (pathInfo == null || "/".equals(pathInfo) || "".equals(pathInfo)) {
            handleListContent(request, response);
        } else if ("/view".equals(pathInfo)) {
            handleViewContent(request, response);
        } else {
            response.sendRedirect(request.getContextPath() + "/admin/content");
        }
    }

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        if (!SessionUtil.isAuthenticated(request) || !SessionUtil.isAdmin(request)) {
            response.setStatus(HttpServletResponse.SC_FORBIDDEN);
            request.getRequestDispatcher("/error/unauthorized.jsp").forward(request, response);
            return;
        }

        if (!CsrfUtil.isValidToken(request)) {
            logger.warn("CSRF token validation failed in AdminContentServlet from IP {}", request.getRemoteAddr());
            response.sendRedirect(request.getContextPath() + "/admin/content?error=" +
                    URLEncoder.encode("Security validation failed. Please try again.", StandardCharsets.UTF_8));
            return;
        }

        String pathInfo = request.getPathInfo();
        String contentIdStr = request.getParameter("contentId");
        Integer adminUserId = SessionUtil.getAuthenticatedUserId(request);
        String clientIp = request.getRemoteAddr();

        if (contentIdStr == null || contentIdStr.trim().isEmpty()) {
            response.sendRedirect(request.getContextPath() + "/admin/content?error=" +
                    URLEncoder.encode("Missing content identifier.", StandardCharsets.UTF_8));
            return;
        }

        try {
            int contentId = Integer.parseInt(contentIdStr.trim());

            if ("/approve".equals(pathInfo)) {
                contentService.moderateContent(contentId, true, adminUserId, clientIp);
                response.sendRedirect(request.getContextPath() + "/admin/content?success=" +
                        URLEncoder.encode("Content item #" + contentId + " approved and published to the Fitness Library.", StandardCharsets.UTF_8));
            } else if ("/reject".equals(pathInfo)) {
                contentService.moderateContent(contentId, false, adminUserId, clientIp);
                response.sendRedirect(request.getContextPath() + "/admin/content?success=" +
                        URLEncoder.encode("Content item #" + contentId + " has been rejected.", StandardCharsets.UTF_8));
            } else if ("/delete".equals(pathInfo)) {
                com.fitaura.model.User admin = new com.fitaura.model.User();
                admin.setUserId(adminUserId);
                admin.setRole(com.fitaura.model.User.Role.ADMIN);
                contentService.deleteContent(contentId, admin, clientIp);
                response.sendRedirect(request.getContextPath() + "/admin/content?success=" +
                        URLEncoder.encode("Content item #" + contentId + " successfully removed.", StandardCharsets.UTF_8));
            } else {
                response.sendRedirect(request.getContextPath() + "/admin/content");
            }

        } catch (NumberFormatException e) {
            response.sendRedirect(request.getContextPath() + "/admin/content?error=" +
                    URLEncoder.encode("Invalid content ID.", StandardCharsets.UTF_8));
        } catch (FitAuraException e) {
            response.sendRedirect(request.getContextPath() + "/admin/content?error=" +
                    URLEncoder.encode(e.getMessage(), StandardCharsets.UTF_8));
        } catch (Exception e) {
            logger.error("Error moderating content: {}", e.getMessage(), e);
            response.sendRedirect(request.getContextPath() + "/admin/content?error=" +
                    URLEncoder.encode("Failed to complete moderation action.", StandardCharsets.UTF_8));
        }
    }

    private void handleListContent(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        try {
            String search = request.getParameter("search");
            String catStr = request.getParameter("category");
            String statusStr = request.getParameter("status");
            String pageStr = request.getParameter("page");

            FitnessContent.Category category = null;
            if (catStr != null && !catStr.trim().isEmpty()) {
                try {
                    category = FitnessContent.Category.valueOf(catStr.trim().toUpperCase());
                } catch (IllegalArgumentException ignored) {}
            }

            FitnessContent.ApprovalStatus status = null;
            if (statusStr != null && !statusStr.trim().isEmpty() && !"ALL".equalsIgnoreCase(statusStr)) {
                try {
                    status = FitnessContent.ApprovalStatus.valueOf(statusStr.trim().toUpperCase());
                } catch (IllegalArgumentException ignored) {}
            }

            int page = 1;
            if (pageStr != null && !pageStr.trim().isEmpty()) {
                try {
                    page = Math.max(1, Integer.parseInt(pageStr.trim()));
                } catch (NumberFormatException ignored) {}
            }

            int pageSize = 20;
            int offset = (page - 1) * pageSize;

            List<FitnessContentDetailDTO> items = contentService.getAdminContentList(search, category, status, null, pageSize, offset);
            int totalMatching = contentService.countAdminContent(search, category, status, null);
            int totalPages = (int) Math.ceil((double) totalMatching / pageSize);
            if (totalPages < 1) totalPages = 1;

            int pendingCount = contentService.countPendingModeration();

            request.setAttribute("items", items);
            request.setAttribute("totalItems", totalMatching);
            request.setAttribute("currentPage", page);
            request.setAttribute("totalPages", totalPages);
            request.setAttribute("searchQuery", search);
            request.setAttribute("selectedCategory", catStr);
            request.setAttribute("selectedStatus", statusStr != null ? statusStr : "ALL");
            request.setAttribute("pendingCount", pendingCount);

            request.getRequestDispatcher("/admin/content.jsp").forward(request, response);

        } catch (Exception e) {
            logger.error("Error loading admin content queue: {}", e.getMessage(), e);
            request.setAttribute("errorMessage", "Unable to load content moderation directory.");
            request.getRequestDispatcher("/admin/content.jsp").forward(request, response);
        }
    }

    private void handleViewContent(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        String idStr = request.getParameter("id");
        if (idStr == null || idStr.trim().isEmpty()) {
            response.sendRedirect(request.getContextPath() + "/admin/content");
            return;
        }

        try {
            int contentId = Integer.parseInt(idStr.trim());
            Integer adminUserId = SessionUtil.getAuthenticatedUserId(request);
            FitnessContentDetailDTO item = contentService.getContentDetails(contentId, adminUserId, true);

            request.setAttribute("item", item);
            request.getRequestDispatcher("/admin/content-view.jsp").forward(request, response);

        } catch (Exception e) {
            logger.error("Error viewing content in admin panel: {}", e.getMessage(), e);
            response.sendRedirect(request.getContextPath() + "/admin/content");
        }
    }
}
