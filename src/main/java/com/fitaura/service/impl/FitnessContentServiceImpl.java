package com.fitaura.service.impl;

import com.fitaura.dao.ActivityLogDAO;
import com.fitaura.dao.FitnessContentDAO;
import com.fitaura.dao.UserDAO;
import com.fitaura.dao.impl.ActivityLogDAOImpl;
import com.fitaura.dao.impl.FitnessContentDAOImpl;
import com.fitaura.dao.impl.UserDAOImpl;
import com.fitaura.dto.FitnessContentDetailDTO;
import com.fitaura.exception.AuthorizationException;
import com.fitaura.exception.DatabaseException;
import com.fitaura.exception.ResourceNotFoundException;
import com.fitaura.exception.ValidationException;
import com.fitaura.model.ActivityLog;
import com.fitaura.model.FitnessContent;
import com.fitaura.model.User;
import com.fitaura.service.FitnessContentService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Optional;

/**
 * Production implementation of FitnessContentService.
 * Enforces business rules, validation, lifecycle states (PENDING/APPROVED/REJECTED),
 * strict ownership, and comprehensive audit logging.
 */
public class FitnessContentServiceImpl implements FitnessContentService {

    private static final Logger logger = LoggerFactory.getLogger(FitnessContentServiceImpl.class);

    private final FitnessContentDAO fitnessContentDAO;
    private final UserDAO userDAO;
    private final ActivityLogDAO activityLogDAO;

    public FitnessContentServiceImpl() {
        this(new FitnessContentDAOImpl(), new UserDAOImpl(), new ActivityLogDAOImpl());
    }

    public FitnessContentServiceImpl(FitnessContentDAO fitnessContentDAO, UserDAO userDAO, ActivityLogDAO activityLogDAO) {
        this.fitnessContentDAO = fitnessContentDAO;
        this.userDAO = userDAO;
        this.activityLogDAO = activityLogDAO;
    }

    @Override
    public FitnessContent createContent(FitnessContent content, User author, String clientIp) {
        if (content == null) {
            throw new ValidationException("Content payload cannot be null.");
        }
        if (author == null || author.getUserId() == null) {
            throw new ValidationException("Authenticated author is required.");
        }

        validateContentFields(content.getTitle(), content.getCategory(), content.getContentText());

        content.setCreatedBy(author.getUserId());
        content.setTitle(content.getTitle().trim());
        content.setContentText(content.getContentText().trim());

        // Admin-authored content is approved automatically; User content enters PENDING review
        if (author.getRole() == User.Role.ADMIN) {
            content.setApprovalStatus(FitnessContent.ApprovalStatus.APPROVED);
            content.setReviewedBy(author.getUserId());
            content.setReviewedAt(new Timestamp(System.currentTimeMillis()));
        } else {
            content.setApprovalStatus(FitnessContent.ApprovalStatus.PENDING);
            content.setReviewedBy(null);
            content.setReviewedAt(null);
        }

        try {
            Integer contentId = fitnessContentDAO.create(content);
            content.setContentId(contentId);

            // Audit log
            logActivity(author.getUserId(), "CONTENT_CREATED", contentId,
                    "Created " + content.getCategory().name() + " content: '" + content.getTitle() + "' (status: " + content.getApprovalStatus() + ")",
                    clientIp);

            logger.info("User {} created fitness content {} with status {}", author.getUserId(), contentId, content.getApprovalStatus());
            return content;

        } catch (DatabaseException e) {
            logger.error("Error creating fitness content: {}", e.getMessage(), e);
            throw new DatabaseException("Failed to publish content. Please try again.", e);
        }
    }

    @Override
    public FitnessContent updateContent(Integer contentId, String title, FitnessContent.Category category,
                                        String contentText, User currentUser, String clientIp) {
        if (contentId == null || currentUser == null || currentUser.getUserId() == null) {
            throw new ValidationException("Content ID and authenticated user are required.");
        }

        validateContentFields(title, category, contentText);

        Optional<FitnessContent> existingOpt = fitnessContentDAO.findById(contentId);
        if (existingOpt.isEmpty()) {
            throw new ResourceNotFoundException("Fitness content #" + contentId + " not found.");
        }

        FitnessContent existing = existingOpt.get();

        // Enforce ownership: only creator or ADMIN can edit
        boolean isAuthor = existing.getCreatedBy().equals(currentUser.getUserId());
        boolean isAdmin = (currentUser.getRole() == User.Role.ADMIN);

        if (!isAuthor && !isAdmin) {
            throw new AuthorizationException("You are not authorized to modify this content.");
        }

        existing.setTitle(title.trim());
        existing.setCategory(category);
        existing.setContentText(contentText.trim());

        // Reset to PENDING if author edited an already approved or rejected article
        if (isAuthor && !isAdmin) {
            existing.setApprovalStatus(FitnessContent.ApprovalStatus.PENDING);
            existing.setReviewedBy(null);
            existing.setReviewedAt(null);
        }

        try {
            fitnessContentDAO.update(existing);

            logActivity(currentUser.getUserId(), "CONTENT_UPDATED", contentId,
                    "Updated fitness content '" + existing.getTitle() + "' (status: " + existing.getApprovalStatus() + ")",
                    clientIp);

            logger.info("User {} updated fitness content {}", currentUser.getUserId(), contentId);
            return existing;

        } catch (DatabaseException e) {
            logger.error("Error updating fitness content {}: {}", contentId, e.getMessage(), e);
            throw new DatabaseException("Failed to update content.", e);
        }
    }

    @Override
    public boolean deleteContent(Integer contentId, User currentUser, String clientIp) {
        if (contentId == null || currentUser == null || currentUser.getUserId() == null) {
            throw new ValidationException("Content ID and authenticated user are required.");
        }

        Optional<FitnessContent> existingOpt = fitnessContentDAO.findById(contentId);
        if (existingOpt.isEmpty()) {
            throw new ResourceNotFoundException("Fitness content #" + contentId + " not found.");
        }

        FitnessContent existing = existingOpt.get();

        // Ownership enforcement
        boolean isAuthor = existing.getCreatedBy().equals(currentUser.getUserId());
        boolean isAdmin = (currentUser.getRole() == User.Role.ADMIN);

        if (!isAuthor && !isAdmin) {
            throw new AuthorizationException("You do not have permission to delete this content.");
        }

        boolean deleted = fitnessContentDAO.delete(contentId);
        if (deleted) {
            logActivity(currentUser.getUserId(), "CONTENT_DELETED", contentId,
                    "Deleted fitness content '" + existing.getTitle() + "'", clientIp);
            logger.info("User {} deleted fitness content {}", currentUser.getUserId(), contentId);
        }

        return deleted;
    }

    @Override
    public FitnessContentDetailDTO getContentDetails(Integer contentId, Integer currentUserId, boolean isAdmin) {
        if (contentId == null) {
            throw new ValidationException("Content ID is required.");
        }

        Optional<FitnessContent> contentOpt = fitnessContentDAO.findById(contentId);
        if (contentOpt.isEmpty()) {
            throw new ResourceNotFoundException("Fitness content #" + contentId + " not found.");
        }

        FitnessContent content = contentOpt.get();
        boolean isOwner = (currentUserId != null && currentUserId.equals(content.getCreatedBy()));

        // Visibility rule: non-approved content is only viewable by its author or an admin
        if (content.getApprovalStatus() != FitnessContent.ApprovalStatus.APPROVED && !isOwner && !isAdmin) {
            throw new AuthorizationException("This content is currently under moderation and not publicly available.");
        }

        String authorName = "FitAura Coach";
        Optional<User> authorOpt = userDAO.findById(content.getCreatedBy());
        if (authorOpt.isPresent()) {
            User author = authorOpt.get();
            if (author.getPrivacyMode() == User.PrivacyMode.SOCIAL || isOwner || isAdmin) {
                authorName = author.getDisplayName();
            } else {
                authorName = "FitAura Athlete";
            }
        }

        return new FitnessContentDetailDTO(content, authorName, isOwner);
    }

    @Override
    public List<FitnessContentDetailDTO> getPublishedLibrary(String searchQuery, FitnessContent.Category category,
                                                            int limit, int offset, Integer currentUserId) {
        int validLimit = limit > 0 ? Math.min(limit, 50) : 12;
        int validOffset = Math.max(0, offset);
        return fitnessContentDAO.findApprovedWithFilters(searchQuery, category, validLimit, validOffset, currentUserId);
    }

    @Override
    public int countPublishedLibrary(String searchQuery, FitnessContent.Category category) {
        return fitnessContentDAO.countApprovedWithFilters(searchQuery, category);
    }

    @Override
    public List<FitnessContentDetailDTO> getUserContentList(Integer userId) {
        if (userId == null) {
            return Collections.emptyList();
        }

        List<FitnessContent> list = fitnessContentDAO.findByCreatedBy(userId);
        Optional<User> userOpt = userDAO.findById(userId);
        String displayName = userOpt.map(User::getDisplayName).orElse("You");

        List<FitnessContentDetailDTO> result = new ArrayList<>();
        for (FitnessContent c : list) {
            result.add(new FitnessContentDetailDTO(c, displayName, true));
        }
        return result;
    }

    @Override
    public boolean moderateContent(Integer contentId, boolean approve, Integer adminUserId, String clientIp) {
        if (contentId == null || adminUserId == null) {
            throw new ValidationException("Content ID and admin user ID are required.");
        }

        Optional<User> adminOpt = userDAO.findById(adminUserId);
        if (adminOpt.isEmpty() || adminOpt.get().getRole() != User.Role.ADMIN) {
            throw new AuthorizationException("Only administrators can moderate community content.");
        }

        Optional<FitnessContent> existingOpt = fitnessContentDAO.findById(contentId);
        if (existingOpt.isEmpty()) {
            throw new ResourceNotFoundException("Fitness content #" + contentId + " not found.");
        }

        FitnessContent content = existingOpt.get();
        FitnessContent.ApprovalStatus newStatus = approve ? FitnessContent.ApprovalStatus.APPROVED : FitnessContent.ApprovalStatus.REJECTED;

        boolean updated = fitnessContentDAO.updateApproval(contentId, newStatus, adminUserId);
        if (updated) {
            String action = approve ? "CONTENT_APPROVED" : "CONTENT_REJECTED";
            logActivity(adminUserId, action, contentId,
                    "Admin " + (approve ? "approved" : "rejected") + " content: '" + content.getTitle() + "'", clientIp);
            logger.info("Admin {} moderated content {} to {}", adminUserId, contentId, newStatus);
        }

        return updated;
    }

    @Override
    public List<FitnessContentDetailDTO> getAdminContentList(String searchQuery, FitnessContent.Category category,
                                                            FitnessContent.ApprovalStatus status, Integer createdBy,
                                                            int limit, int offset) {
        int validLimit = limit > 0 ? Math.min(limit, 100) : 20;
        int validOffset = Math.max(0, offset);
        return fitnessContentDAO.findAdminWithFilters(searchQuery, category, status, createdBy, validLimit, validOffset);
    }

    @Override
    public int countAdminContent(String searchQuery, FitnessContent.Category category,
                                 FitnessContent.ApprovalStatus status, Integer createdBy) {
        return fitnessContentDAO.countAdminWithFilters(searchQuery, category, status, createdBy);
    }

    @Override
    public int countPendingModeration() {
        return fitnessContentDAO.countByApprovalStatus(FitnessContent.ApprovalStatus.PENDING);
    }

    private void validateContentFields(String title, FitnessContent.Category category, String contentText) {
        if (title == null || title.trim().isEmpty()) {
            throw new ValidationException("Content title is required.");
        }
        String cleanTitle = title.trim();
        if (cleanTitle.length() < 3 || cleanTitle.length() > 200) {
            throw new ValidationException("Content title must be between 3 and 200 characters.");
        }

        if (category == null) {
            throw new ValidationException("Please select a valid content category.");
        }

        if (contentText == null || contentText.trim().isEmpty()) {
            throw new ValidationException("Content text cannot be empty.");
        }
        String cleanText = contentText.trim();
        if (cleanText.length() < 10) {
            throw new ValidationException("Content text is too brief (minimum 10 characters).");
        }
        if (cleanText.length() > 20000) {
            throw new ValidationException("Content text exceeds the 20,000 character limit.");
        }
    }

    private void logActivity(Integer userId, String actionType, Integer entityId, String desc, String ip) {
        try {
            ActivityLog log = new ActivityLog();
            log.setUserId(userId);
            log.setActionType(actionType);
            log.setEntityType("FITNESS_CONTENT");
            log.setEntityId(entityId);
            log.setDescription(desc);
            log.setIpAddress(ip);
            activityLogDAO.create(log);
        } catch (Exception ex) {
            logger.warn("Non-fatal: failed to write activity log for {}: {}", actionType, ex.getMessage());
        }
    }
}
