package com.fitaura.dao;

import com.fitaura.model.User;

import java.sql.Connection;
import java.util.List;
import java.util.Optional;

/**
 * Data Access Object interface for the User domain entity.
 */
public interface UserDAO {

    Integer create(User user);

    Integer create(User user, Connection conn);

    Optional<User> findById(Integer userId);

    Optional<User> findByEmail(String email);

    boolean update(User user);

    boolean updateLastLogin(Integer userId);

    boolean updateAccountStatus(Integer userId, User.AccountStatus status);

    boolean updateProfile(Integer userId, String fullName, String displayName);

    boolean updatePrivacyMode(Integer userId, User.PrivacyMode privacyMode);

    boolean emailExistsForAnotherUser(String email, Integer currentUserId);

    boolean delete(Integer userId);

    int countAll();

    List<User> findAll(int limit, int offset);

    List<User> findRecentUsers(int limit);

    List<User> findWithFilters(String searchQuery, User.Role role, User.AccountStatus status,
                                User.PrivacyMode privacyMode, int limit, int offset);

    int countWithFilters(String searchQuery, User.Role role, User.AccountStatus status,
                          User.PrivacyMode privacyMode);

    int countByStatus(User.AccountStatus status);

    int countByRole(User.Role role);

    int countByPrivacyMode(User.PrivacyMode privacyMode);
}
