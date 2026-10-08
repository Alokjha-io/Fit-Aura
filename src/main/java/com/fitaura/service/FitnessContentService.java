package com.fitaura.service;

import com.fitaura.dto.FitnessContentDetailDTO;
import com.fitaura.model.FitnessContent;
import com.fitaura.model.User;

import java.util.List;

/**
 * Service interface for Fitness Content Management & Approval Workflow:
 * - Content creation, editing, resubmission, and deletion with ownership enforcement
 * - Public Fitness Library browsing of approved items
 * - Admin moderation queue (approval/rejection)
 * - Safe activity audit logging
 */
public interface FitnessContentService {

    /**
     * Creates new fitness content authored by the authenticated user.
     * Enforces server-side validation and sets initial status to PENDING for users.
     */
    FitnessContent createContent(FitnessContent content, User author, String clientIp);

    /**
     * Updates an existing fitness content item strictly ensuring ownership.
     * Resets status to PENDING for moderation re-review.
     */
    FitnessContent updateContent(Integer contentId, String title, FitnessContent.Category category,
                                 String contentText, User currentUser, String clientIp);

    /**
     * Deletes a fitness content item (allowed by author or administrator).
     */
    boolean deleteContent(Integer contentId, User currentUser, String clientIp);

    /**
     * Retrieves content details enforcing privacy and visibility boundaries.
     */
    FitnessContentDetailDTO getContentDetails(Integer contentId, Integer currentUserId, boolean isAdmin);

    /**
     * Retrieves paginated public fitness library items (strictly APPROVED status).
     */
    List<FitnessContentDetailDTO> getPublishedLibrary(String searchQuery, FitnessContent.Category category,
                                                      int limit, int offset, Integer currentUserId);

    /**
     * Counts approved public fitness library items.
     */
    int countPublishedLibrary(String searchQuery, FitnessContent.Category category);

    /**
     * Retrieves all content authored by a specific user (showing PENDING, APPROVED, REJECTED).
     */
    List<FitnessContentDetailDTO> getUserContentList(Integer userId);

    /**
     * Admin moderation action: approves or rejects a content submission.
     */
    boolean moderateContent(Integer contentId, boolean approve, Integer adminUserId, String clientIp);

    /**
     * Admin content management list with filters across approval statuses.
     */
    List<FitnessContentDetailDTO> getAdminContentList(String searchQuery, FitnessContent.Category category,
                                                      FitnessContent.ApprovalStatus status, Integer createdBy,
                                                      int limit, int offset);

    /**
     * Counts filtered admin content records.
     */
    int countAdminContent(String searchQuery, FitnessContent.Category category,
                          FitnessContent.ApprovalStatus status, Integer createdBy);

    /**
     * Counts total pending content items awaiting admin review.
     */
    int countPendingModeration();
}
