package com.fitaura.dao;

import com.fitaura.model.SocialActivity;

import java.util.List;

public interface SocialActivityDAO {

    List<SocialActivity> getRecentFeed(int limit);

    Integer logActivity(SocialActivity activity);

    List<SocialActivity> getUserActivities(Integer userId, int limit);
}
