package com.fitaura.model;

import java.io.Serializable;
import java.sql.Timestamp;

/**
 * Model representing a privacy-safe social connection between two SOCIAL mode users.
 */
public class SocialConnection implements Serializable {

    private static final long serialVersionUID = 1L;

    public enum Status {
        PENDING,
        ACCEPTED,
        REJECTED
    }

    private Integer connectionId;
    private Integer requesterId;
    private Integer receiverId;
    private Status status;
    private Timestamp createdAt;
    private Timestamp updatedAt;

    // Transient attributes for UI presentation
    private String otherUserDisplayName;
    private Integer otherUserId;

    public SocialConnection() {
        this.status = Status.PENDING;
    }

    public Integer getConnectionId() {
        return connectionId;
    }

    public void setConnectionId(Integer connectionId) {
        this.connectionId = connectionId;
    }

    public Integer getRequesterId() {
        return requesterId;
    }

    public void setRequesterId(Integer requesterId) {
        this.requesterId = requesterId;
    }

    public Integer getReceiverId() {
        return receiverId;
    }

    public void setReceiverId(Integer receiverId) {
        this.receiverId = receiverId;
    }

    public Status getStatus() {
        return status;
    }

    public void setStatus(Status status) {
        this.status = status;
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

    public String getOtherUserDisplayName() {
        return otherUserDisplayName;
    }

    public void setOtherUserDisplayName(String otherUserDisplayName) {
        this.otherUserDisplayName = otherUserDisplayName;
    }

    public Integer getOtherUserId() {
        return otherUserId;
    }

    public void setOtherUserId(Integer otherUserId) {
        this.otherUserId = otherUserId;
    }
}
