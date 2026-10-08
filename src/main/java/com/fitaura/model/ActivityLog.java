package com.fitaura.model;

import java.io.Serializable;
import java.sql.Timestamp;

/**
 * Domain entity representing an audit log entry.
 * Maps to the 'activity_logs' table.
 */
public class ActivityLog implements Serializable {

    private static final long serialVersionUID = 1L;

    private Long logId;
    private Integer userId;
    private String actionType;
    private String entityType;
    private Integer entityId;
    private String description;
    private String ipAddress;
    private Timestamp createdAt;

    public ActivityLog() {
    }

    public ActivityLog(Long logId, Integer userId, String actionType, String entityType,
                       Integer entityId, String description, String ipAddress) {
        this.logId = logId;
        this.userId = userId;
        this.actionType = actionType;
        this.entityType = entityType;
        this.entityId = entityId;
        this.description = description;
        this.ipAddress = ipAddress;
    }

    public Long getLogId() {
        return logId;
    }

    public void setLogId(Long logId) {
        this.logId = logId;
    }

    public Integer getUserId() {
        return userId;
    }

    public void setUserId(Integer userId) {
        this.userId = userId;
    }

    public String getActionType() {
        return actionType;
    }

    public void setActionType(String actionType) {
        this.actionType = actionType;
    }

    public String getEntityType() {
        return entityType;
    }

    public void setEntityType(String entityType) {
        this.entityType = entityType;
    }

    public Integer getEntityId() {
        return entityId;
    }

    public void setEntityId(Integer entityId) {
        this.entityId = entityId;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getIpAddress() {
        return ipAddress;
    }

    public void setIpAddress(String ipAddress) {
        this.ipAddress = ipAddress;
    }

    public Timestamp getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(Timestamp createdAt) {
        this.createdAt = createdAt;
    }

    @Override
    public String toString() {
        return "ActivityLog{" +
                "logId=" + logId +
                ", userId=" + userId +
                ", actionType='" + actionType + '\'' +
                ", entityType='" + entityType + '\'' +
                ", entityId=" + entityId +
                '}';
    }
}
