package com.fitaura.model;

import java.io.Serializable;
import java.sql.Timestamp;

/**
 * Domain entity representing an application user in the FitAura system.
 * Maps to the 'users' table.
 */
public class User implements Serializable {

    private static final long serialVersionUID = 1L;

    public enum Role {
        USER, ADMIN
    }

    public enum AccountStatus {
        ACTIVE, INACTIVE, BLOCKED
    }

    public enum PrivacyMode {
        PERSONAL, SOCIAL
    }

    private Integer userId;
    private String fullName;
    private String email;
    private String passwordHash;
    private Role role = Role.USER;
    private AccountStatus accountStatus = AccountStatus.ACTIVE;
    private PrivacyMode privacyMode = PrivacyMode.PERSONAL;
    private String displayName;
    private Timestamp createdAt;
    private Timestamp updatedAt;
    private Timestamp lastLoginAt;

    public User() {
    }

    public User(Integer userId, String fullName, String email, String passwordHash,
                Role role, AccountStatus accountStatus, PrivacyMode privacyMode,
                String displayName) {
        this.userId = userId;
        this.fullName = fullName;
        this.email = email;
        this.passwordHash = passwordHash;
        this.role = role != null ? role : Role.USER;
        this.accountStatus = accountStatus != null ? accountStatus : AccountStatus.ACTIVE;
        this.privacyMode = privacyMode != null ? privacyMode : PrivacyMode.PERSONAL;
        this.displayName = displayName;
    }

    public Integer getUserId() {
        return userId;
    }

    public void setUserId(Integer userId) {
        this.userId = userId;
    }

    public String getFullName() {
        return fullName;
    }

    public void setFullName(String fullName) {
        this.fullName = fullName;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getPasswordHash() {
        return passwordHash;
    }

    public void setPasswordHash(String passwordHash) {
        this.passwordHash = passwordHash;
    }

    public Role getRole() {
        return role;
    }

    public void setRole(Role role) {
        this.role = role;
    }

    public AccountStatus getAccountStatus() {
        return accountStatus;
    }

    public void setAccountStatus(AccountStatus accountStatus) {
        this.accountStatus = accountStatus;
    }

    public PrivacyMode getPrivacyMode() {
        return privacyMode;
    }

    public void setPrivacyMode(PrivacyMode privacyMode) {
        this.privacyMode = privacyMode;
    }

    public String getDisplayName() {
        return displayName;
    }

    public void setDisplayName(String displayName) {
        this.displayName = displayName;
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

    public Timestamp getLastLoginAt() {
        return lastLoginAt;
    }

    public void setLastLoginAt(Timestamp lastLoginAt) {
        this.lastLoginAt = lastLoginAt;
    }

    @Override
    public String toString() {
        return "User{" +
                "userId=" + userId +
                ", fullName='" + fullName + '\'' +
                ", email='" + email + '\'' +
                ", role=" + role +
                ", accountStatus=" + accountStatus +
                ", privacyMode=" + privacyMode +
                ", displayName='" + displayName + '\'' +
                '}';
    }
}
