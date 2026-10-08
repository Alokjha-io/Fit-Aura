package com.fitaura.model;

import java.io.Serializable;
import java.sql.Timestamp;

/**
 * Domain entity representing educational and guided fitness content.
 * Maps to the 'fitness_content' table.
 */
public class FitnessContent implements Serializable {

    private static final long serialVersionUID = 1L;

    public enum Category {
        WORKOUT, EXERCISE, NUTRITION, FITNESS_TIP, GUIDE
    }

    public enum ApprovalStatus {
        PENDING, APPROVED, REJECTED
    }

    private Integer contentId;
    private Integer createdBy;
    private String title;
    private Category category;
    private String contentText;
    private ApprovalStatus approvalStatus = ApprovalStatus.PENDING;
    private Integer reviewedBy;
    private Timestamp reviewedAt;
    private Timestamp createdAt;
    private Timestamp updatedAt;

    public FitnessContent() {
    }

    public FitnessContent(Integer contentId, Integer createdBy, String title,
                          Category category, String contentText,
                          ApprovalStatus approvalStatus, Integer reviewedBy,
                          Timestamp reviewedAt) {
        this.contentId = contentId;
        this.createdBy = createdBy;
        this.title = title;
        this.category = category;
        this.contentText = contentText;
        this.approvalStatus = approvalStatus != null ? approvalStatus : ApprovalStatus.PENDING;
        this.reviewedBy = reviewedBy;
        this.reviewedAt = reviewedAt;
    }

    public Integer getContentId() {
        return contentId;
    }

    public void setContentId(Integer contentId) {
        this.contentId = contentId;
    }

    public Integer getCreatedBy() {
        return createdBy;
    }

    public void setCreatedBy(Integer createdBy) {
        this.createdBy = createdBy;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public Category getCategory() {
        return category;
    }

    public void setCategory(Category category) {
        this.category = category;
    }

    public String getContentText() {
        return contentText;
    }

    public void setContentText(String contentText) {
        this.contentText = contentText;
    }

    public ApprovalStatus getApprovalStatus() {
        return approvalStatus;
    }

    public void setApprovalStatus(ApprovalStatus approvalStatus) {
        this.approvalStatus = approvalStatus;
    }

    public Integer getReviewedBy() {
        return reviewedBy;
    }

    public void setReviewedBy(Integer reviewedBy) {
        this.reviewedBy = reviewedBy;
    }

    public Timestamp getReviewedAt() {
        return reviewedAt;
    }

    public void setReviewedAt(Timestamp reviewedAt) {
        this.reviewedAt = reviewedAt;
    }

    public Timestamp getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(Timestamp createdAt) {
        this.createdAt = createdAt;
    }

    public Timestamp getUpdatedAt() {
        return updatedAt;
    }

    public void setUpdatedAt(Timestamp updatedAt) {
        this.updatedAt = updatedAt;
    }

    @Override
    public String toString() {
        return "FitnessContent{" +
                "contentId=" + contentId +
                ", title='" + title + '\'' +
                ", category=" + category +
                ", approvalStatus=" + approvalStatus +
                '}';
    }
}
