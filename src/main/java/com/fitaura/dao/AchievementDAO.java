package com.fitaura.dao;

import com.fitaura.model.Achievement;

import java.util.List;
import java.util.Optional;

/**
 * Data Access Object interface for Achievement entity.
 */
public interface AchievementDAO {

    Integer create(Achievement achievement);

    Optional<Achievement> findById(Integer achievementId);

    Optional<Achievement> findByName(String name);

    List<Achievement> findAllActive();

    boolean update(Achievement achievement);
}
