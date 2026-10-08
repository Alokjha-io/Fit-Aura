package com.fitaura.dao;

import com.fitaura.model.ActivityLog;

import java.util.List;

/**
 * Data Access Object interface for ActivityLog entity.
 */
public interface ActivityLogDAO {

    Long create(ActivityLog log);

    List<ActivityLog> findByUserId(Integer userId, int limit);

    List<ActivityLog> findRecent(int limit);

    List<ActivityLog> findWithFilters(Integer userId, String actionType, String entityType, int limit, int offset);

    int countWithFilters(Integer userId, String actionType, String entityType);
}
