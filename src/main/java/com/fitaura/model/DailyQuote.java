package com.fitaura.model;

import java.io.Serializable;
import java.sql.Date;
import java.sql.Timestamp;

/**
 * Model representing an admin-managed daily motivational quote.
 */
public class DailyQuote implements Serializable {

    private static final long serialVersionUID = 1L;

    public enum Status {
        DRAFT,
        PUBLISHED,
        ARCHIVED
    }

    private Integer quoteId;
    private String quoteText;
    private String authorName;
    private Date quoteDate;
    private Status status;
    private Integer createdBy;
    private Timestamp createdAt;
    private Timestamp updatedAt;

    public DailyQuote() {
        this.status = Status.PUBLISHED;
    }

    public DailyQuote(Integer quoteId, String quoteText, String authorName, Date quoteDate, Status status) {
        this.quoteId = quoteId;
        this.quoteText = quoteText;
        this.authorName = authorName;
        this.quoteDate = quoteDate;
        this.status = status;
    }

    public Integer getQuoteId() {
        return quoteId;
    }

    public void setQuoteId(Integer quoteId) {
        this.quoteId = quoteId;
    }

    public String getQuoteText() {
        return quoteText;
    }

    public void setQuoteText(String quoteText) {
        this.quoteText = quoteText;
    }

    public String getAuthorName() {
        return authorName;
    }

    public void setAuthorName(String authorName) {
        this.authorName = authorName;
    }

    public Date getQuoteDate() {
        return quoteDate;
    }

    public void setQuoteDate(Date quoteDate) {
        this.quoteDate = quoteDate;
    }

    public Status getStatus() {
        return status;
    }

    public void setStatus(Status status) {
        this.status = status;
    }

    public Integer getCreatedBy() {
        return createdBy;
    }

    public void setCreatedBy(Integer createdBy) {
        this.createdBy = createdBy;
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
        return "DailyQuote{" +
                "quoteId=" + quoteId +
                ", quoteText='" + (quoteText != null && quoteText.length() > 30 ? quoteText.substring(0, 30) + "..." : quoteText) + '\'' +
                ", authorName='" + authorName + '\'' +
                ", quoteDate=" + quoteDate +
                ", status=" + status +
                '}';
    }
}
