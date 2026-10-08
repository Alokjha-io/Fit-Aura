package com.fitaura.dao;

import com.fitaura.model.UserAchievement;

import java.sql.Connection;
import java.util.List;

/**
 * Data Access Object interface for UserAchievement entity.
 */
public interface UserAchievementDAO {

    Integer create(UserAchievement userAchievement);

    Integer create(UserAchievement userAchievement, Connection conn);

    List<UserAchievement> findByUserId(Integer userId);

    boolean hasUserEarned(Integer userId, Integer achievementId);

    boolean hasUserEarned(Integer userId, Integer achievementId, Connection conn);
}
