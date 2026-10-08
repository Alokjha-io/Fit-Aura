package com.fitaura.controller;

import com.fitaura.dto.FitnessContentDetailDTO;
import com.fitaura.exception.AuthorizationException;
import com.fitaura.exception.FitAuraException;
import com.fitaura.exception.ValidationException;
import com.fitaura.model.FitnessContent;
import com.fitaura.model.User;
import com.fitaura.service.FitnessContentService;
import com.fitaura.service.UserService;
import com.fitaura.service.impl.FitnessContentServiceImpl;
import com.fitaura.service.impl.UserServiceImpl;
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
 * Controller serving the Fitness Content Library and User Authoring features:
 * - Public browsing of approved educational content
 * - Category filtering and search
 * - Content creation, editing, resubmission, and deletion
 * - "My Content" status tracker
 */
@WebServlet(name = "FitnessContentServlet", urlPatterns = {"/content", "/content/*", "/fitness-content"})
public class FitnessContentServlet extends HttpServlet {

    private static final long serialVersionUID = 1L;
    private static final Logger logger = LoggerFactory.getLogger(FitnessContentServlet.class);

    private final FitnessContentService contentService;
    private final UserService userService;

    public FitnessContentServlet() {
        this(new FitnessContentServiceImpl(), new UserServiceImpl());
    }

    public FitnessContentServlet(FitnessContentService contentService, UserService userService) {
        this.contentService = contentService;
        this.userService = userService;
    }

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        String pathInfo = request.getPathInfo();

        if (pathInfo == null || "/".equals(pathInfo) || "".equals(pathInfo)) {
            handleBrowseLibrary(request, response);
        } else if ("/view".equals(pathInfo)) {
            handleViewContent(request, response);
        } else if ("/create".equals(pathInfo)) {
            handleShowCreate(request, response);
        } else if ("/my".equals(pathInfo)) {
            handleMyContent(request, response);
        } else if ("/edit".equals(pathInfo)) {
            handleShowEdit(request, response);
        } else {
            response.sendRedirect(request.getContextPath() + "/content");
        }
    }

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        Integer userId = SessionUtil.getAuthenticatedUserId(request);
        if (userId == null) {
            response.sendRedirect(request.getContextPath() + "/login");
            return;
        }

        if (!CsrfUtil.isValidToken(request)) {
            logger.warn("CSRF token validation failed in FitnessContentServlet from IP {}", request.getRemoteAddr());
            response.sendRedirect(request.getContextPath() + "/content?error=" +
                    URLEncoder.encode("Security validation failed. Please try again.", StandardCharsets.UTF_8));
            return;
        }

        String pathInfo = request.getPathInfo();
        if ("/create".equals(pathInfo)) {
            handleProcessCreate(request, response, userId);
        } else if ("/edit".equals(pathInfo)) {
            handleProcessEdit(request, response, userId);
        } else if ("/delete".equals(pathInfo)) {
            handleProcessDelete(request, response, userId);
        } else {
            response.sendRedirect(request.getContextPath() + "/content");
        }
    }

    private void handleBrowseLibrary(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        try {
            String search = request.getParameter("search");
            String catStr = request.getParameter("category");
            String pageStr = request.getParameter("page");

            FitnessContent.Category category = null;
            if (catStr != null && !catStr.trim().isEmpty()) {
                try {
                    category = FitnessContent.Category.valueOf(catStr.trim().toUpperCase());
                } catch (IllegalArgumentException ignored) {}
            }

            int page = 1;
            if (pageStr != null && !pageStr.trim().isEmpty()) {
                try {
                    page = Math.max(1, Integer.parseInt(pageStr.trim()));
                } catch (NumberFormatException ignored) {}
            }

            int pageSize = 12;
            int offset = (page - 1) * pageSize;

            Integer currentUserId = SessionUtil.getAuthenticatedUserId(request);
            List<FitnessContentDetailDTO> libraryItems = contentService.getPublishedLibrary(search, category, pageSize, offset, currentUserId);
            int totalItems = contentService.countPublishedLibrary(search, category);
            int totalPages = (int) Math.ceil((double) totalItems / pageSize);
            if (totalPages < 1) totalPages = 1;

            request.setAttribute("items", libraryItems);
            request.setAttribute("totalItems", totalItems);
            request.setAttribute("currentPage", page);
            request.setAttribute("totalPages", totalPages);
            request.setAttribute("searchQuery", search);
            request.setAttribute("selectedCategory", catStr);

            request.getRequestDispatcher("/content/library.jsp").forward(request, response);

        } catch (Exception e) {
            logger.error("Error loading fitness library: {}", e.getMessage(), e);
            request.setAttribute("errorMessage", "Unable to load fitness library.");
            request.getRequestDispatcher("/content/library.jsp").forward(request, response);
        }
    }

    private void handleViewContent(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        String idStr = request.getParameter("id");
        if (idStr == null || idStr.trim().isEmpty()) {
            response.sendRedirect(request.getContextPath() + "/content");
            return;
        }

        try {
            int contentId = Integer.parseInt(idStr.trim());
            Integer currentUserId = SessionUtil.getAuthenticatedUserId(request);
            boolean isAdmin = SessionUtil.isAdmin(request);

            FitnessContentDetailDTO item = contentService.getContentDetails(contentId, currentUserId, isAdmin);
            request.setAttribute("item", item);
            request.getRequestDispatcher("/content/view.jsp").forward(request, response);

        } catch (NumberFormatException e) {
            response.sendRedirect(request.getContextPath() + "/content");
        } catch (AuthorizationException e) {
            request.setAttribute("errorMessage", e.getMessage());
            request.getRequestDispatcher("/content/library.jsp").forward(request, response);
        } catch (FitAuraException e) {
            request.setAttribute("errorMessage", e.getMessage());
            request.getRequestDispatcher("/content/library.jsp").forward(request, response);
        } catch (Exception e) {
            logger.error("Error viewing fitness content: {}", e.getMessage(), e);
            response.sendRedirect(request.getContextPath() + "/content");
        }
    }

    private void handleShowCreate(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        if (!SessionUtil.isAuthenticated(request)) {
            response.sendRedirect(request.getContextPath() + "/login");
            return;
        }
        request.getRequestDispatcher("/content/create.jsp").forward(request, response);
    }

    private void handleMyContent(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        Integer userId = SessionUtil.getAuthenticatedUserId(request);
        if (userId == null) {
            response.sendRedirect(request.getContextPath() + "/login");
            return;
        }

        try {
            List<FitnessContentDetailDTO> myItems = contentService.getUserContentList(userId);
            request.setAttribute("myItems", myItems);
            request.getRequestDispatcher("/content/my.jsp").forward(request, response);
        } catch (Exception e) {
            logger.error("Error loading user authored content: {}", e.getMessage(), e);
            request.setAttribute("errorMessage", "Unable to load your content submissions.");
            request.getRequestDispatcher("/content/my.jsp").forward(request, response);
        }
    }

    private void handleShowEdit(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        Integer userId = SessionUtil.getAuthenticatedUserId(request);
        if (userId == null) {
            response.sendRedirect(request.getContextPath() + "/login");
            return;
        }

        String idStr = request.getParameter("id");
        if (idStr == null || idStr.trim().isEmpty()) {
            response.sendRedirect(request.getContextPath() + "/content/my");
            return;
        }

        try {
            int contentId = Integer.parseInt(idStr.trim());
            boolean isAdmin = SessionUtil.isAdmin(request);
            FitnessContentDetailDTO item = contentService.getContentDetails(contentId, userId, isAdmin);

            if (!item.isOwner() && !isAdmin) {
                response.sendRedirect(request.getContextPath() + "/content/my?error=" +
                        URLEncoder.encode("You do not have permission to edit this article.", StandardCharsets.UTF_8));
                return;
            }

            request.setAttribute("item", item);
            request.getRequestDispatcher("/content/edit.jsp").forward(request, response);

        } catch (Exception e) {
            logger.error("Error preparing content edit form: {}", e.getMessage(), e);
            response.sendRedirect(request.getContextPath() + "/content/my");
        }
    }

    private void handleProcessCreate(HttpServletRequest request, HttpServletResponse response, Integer userId)
            throws ServletException, IOException {
        String title = request.getParameter("title");
        String catStr = request.getParameter("category");
        String contentText = request.getParameter("contentText");
        String clientIp = request.getRemoteAddr();

        try {
            FitnessContent.Category category = FitnessContent.Category.valueOf(catStr != null ? catStr.trim().toUpperCase() : "");
            User author = userService.getUserById(userId);

            FitnessContent content = new FitnessContent();
            content.setTitle(title);
            content.setCategory(category);
            content.setContentText(contentText);

            FitnessContent created = contentService.createContent(content, author, clientIp);

            String message = (created.getApprovalStatus() == FitnessContent.ApprovalStatus.APPROVED)
                    ? "Content successfully published!"
                    : "Your content has been submitted for review.";

            response.sendRedirect(request.getContextPath() + "/content/my?success=" +
                    URLEncoder.encode(message, StandardCharsets.UTF_8));

        } catch (IllegalArgumentException e) {
            request.setAttribute("errorMessage", "Please select a valid content category.");
            request.setAttribute("enteredTitle", title);
            request.setAttribute("enteredContent", contentText);
            request.getRequestDispatcher("/content/create.jsp").forward(request, response);
        } catch (ValidationException e) {
            request.setAttribute("errorMessage", e.getMessage());
            request.setAttribute("enteredTitle", title);
            request.setAttribute("enteredCategory", catStr);
            request.setAttribute("enteredContent", contentText);
            request.getRequestDispatcher("/content/create.jsp").forward(request, response);
        } catch (Exception e) {
            logger.error("Error creating content: {}", e.getMessage(), e);
            request.setAttribute("errorMessage", "Failed to submit content. Please check your inputs.");
            request.getRequestDispatcher("/content/create.jsp").forward(request, response);
        }
    }

    private void handleProcessEdit(HttpServletRequest request, HttpServletResponse response, Integer userId)
            throws ServletException, IOException {
        String idStr = request.getParameter("contentId");
        String title = request.getParameter("title");
        String catStr = request.getParameter("category");
        String contentText = request.getParameter("contentText");
        String clientIp = request.getRemoteAddr();

        if (idStr == null || idStr.trim().isEmpty()) {
            response.sendRedirect(request.getContextPath() + "/content/my");
            return;
        }

        try {
            int contentId = Integer.parseInt(idStr.trim());
            FitnessContent.Category category = FitnessContent.Category.valueOf(catStr != null ? catStr.trim().toUpperCase() : "");
            User currentUser = userService.getUserById(userId);

            contentService.updateContent(contentId, title, category, contentText, currentUser, clientIp);

            response.sendRedirect(request.getContextPath() + "/content/my?success=" +
                    URLEncoder.encode("Article updated successfully and resubmitted for review.", StandardCharsets.UTF_8));

        } catch (Exception e) {
            logger.error("Error updating content: {}", e.getMessage(), e);
            response.sendRedirect(request.getContextPath() + "/content/my?error=" +
                    URLEncoder.encode(e.getMessage(), StandardCharsets.UTF_8));
        }
    }

    private void handleProcessDelete(HttpServletRequest request, HttpServletResponse response, Integer userId)
            throws IOException {
        String idStr = request.getParameter("contentId");
        String clientIp = request.getRemoteAddr();

        if (idStr == null || idStr.trim().isEmpty()) {
            response.sendRedirect(request.getContextPath() + "/content/my");
            return;
        }

        try {
            int contentId = Integer.parseInt(idStr.trim());
            User currentUser = userService.getUserById(userId);
            contentService.deleteContent(contentId, currentUser, clientIp);

            response.sendRedirect(request.getContextPath() + "/content/my?success=" +
                    URLEncoder.encode("Content successfully deleted.", StandardCharsets.UTF_8));

        } catch (Exception e) {
            logger.error("Error deleting content: {}", e.getMessage(), e);
            response.sendRedirect(request.getContextPath() + "/content/my?error=" +
                    URLEncoder.encode(e.getMessage(), StandardCharsets.UTF_8));
        }
    }
}
