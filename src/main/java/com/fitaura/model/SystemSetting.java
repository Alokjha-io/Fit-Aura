package com.fitaura.model;

import java.io.Serializable;
import java.sql.Timestamp;

/**
 * Domain entity representing an administrative system setting.
 * Maps to the 'system_settings' table.
 */
public class SystemSetting implements Serializable {

    private static final long serialVersionUID = 1L;

    private Integer settingId;
    private String settingKey;
    private String settingValue;
    private String description;
    private Integer updatedBy;
    private Timestamp updatedAt;

    public SystemSetting() {
    }

    public SystemSetting(Integer settingId, String settingKey, String settingValue,
                         String description, Integer updatedBy) {
        this.settingId = settingId;
        this.settingKey = settingKey;
        this.settingValue = settingValue;
        this.description = description;
        this.updatedBy = updatedBy;
    }

    public Integer getSettingId() {
        return settingId;
    }

    public void setSettingId(Integer settingId) {
        this.settingId = settingId;
    }

    public String getSettingKey() {
        return settingKey;
    }

    public void setSettingKey(String settingKey) {
        this.settingKey = settingKey;
    }

    public String getSettingValue() {
        return settingValue;
    }

    public void setSettingValue(String settingValue) {
        this.settingValue = settingValue;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public Integer getUpdatedBy() {
        return updatedBy;
    }

    public void setUpdatedBy(Integer updatedBy) {
        this.updatedBy = updatedBy;
    }

    public Timestamp getUpdatedAt() {
        return updatedAt;
    }

    public void setUpdatedAt(Timestamp updatedAt) {
        this.updatedAt = updatedAt;
    }

    @Override
    public String toString() {
        return "SystemSetting{" +
                "settingId=" + settingId +
                ", settingKey='" + settingKey + '\'' +
                ", settingValue='" + settingValue + '\'' +
                '}';
    }
}
