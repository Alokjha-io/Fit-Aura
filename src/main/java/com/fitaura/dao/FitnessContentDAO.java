package com.fitaura.dao;

import com.fitaura.dto.FitnessContentDetailDTO;
import com.fitaura.model.FitnessContent;

import java.util.List;
import java.util.Optional;

/**
 * Data Access Object interface for FitnessContent entity.
 */
public interface FitnessContentDAO {

    Integer create(FitnessContent content);

    Optional<FitnessContent> findById(Integer contentId);

    List<FitnessContent> findByCreatedBy(Integer userId);

    List<FitnessContent> findByApprovalStatus(FitnessContent.ApprovalStatus status);

    List<FitnessContent> findByCategoryAndStatus(FitnessContent.Category category, FitnessContent.ApprovalStatus status);

    boolean update(FitnessContent content);

    boolean updateApproval(Integer contentId, FitnessContent.ApprovalStatus status, Integer reviewedBy);

    boolean delete(Integer contentId);

    List<FitnessContentDetailDTO> findApprovedWithFilters(String searchQuery, FitnessContent.Category category,
                                                          int limit, int offset, Integer currentUserId);

    int countApprovedWithFilters(String searchQuery, FitnessContent.Category category);

    List<FitnessContentDetailDTO> findAdminWithFilters(String searchQuery, FitnessContent.Category category,
                                                       FitnessContent.ApprovalStatus status, Integer createdBy,
                                                       int limit, int offset);

    int countAdminWithFilters(String searchQuery, FitnessContent.Category category,
                              FitnessContent.ApprovalStatus status, Integer createdBy);

    int countByApprovalStatus(FitnessContent.ApprovalStatus status);
}
