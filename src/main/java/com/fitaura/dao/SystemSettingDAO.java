package com.fitaura.dao;

import com.fitaura.model.SystemSetting;

import java.util.List;
import java.util.Optional;

/**
 * Data Access Object interface for SystemSetting entity.
 */
public interface SystemSettingDAO {

    Optional<SystemSetting> findByKey(String key);

    boolean updateOrInsert(String key, String value, String description, Integer updatedBy);

    List<SystemSetting> findAll();
}
