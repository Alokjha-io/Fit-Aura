package com.fitaura.service;

import com.fitaura.dto.LeaderboardEntryDTO;
import com.fitaura.dto.SocialHubDTO;
import com.fitaura.dto.SocialProfileDTO;
import com.fitaura.model.Competition;
import com.fitaura.model.CompetitionParticipant;
import com.fitaura.model.SocialActivity;
import com.fitaura.model.SocialConnection;

import java.util.List;

public interface SocialFitnessService {

    SocialHubDTO getSocialHubData(Integer currentUserId);

    List<LeaderboardEntryDTO> getWeeklyLeaderboard(int limit, int offset, Integer currentUserId);

    List<LeaderboardEntryDTO> getMonthlyLeaderboard(int limit, int offset, Integer currentUserId);

    List<LeaderboardEntryDTO> getAllTimeLeaderboard(int limit, int offset, Integer currentUserId);

    List<Competition> getActiveCompetitions(Integer currentUserId);

    List<Competition> getUpcomingCompetitions(Integer currentUserId);

    List<Competition> getCompletedCompetitions(Integer currentUserId);

    Competition getCompetitionDetails(Integer compId, Integer currentUserId);

    boolean joinCompetition(Integer compId, Integer userId, String clientIp);

    boolean leaveCompetition(Integer compId, Integer userId, String clientIp);

    List<CompetitionParticipant> getCompetitionLeaderboard(Integer compId, int limit);

    Competition createCompetition(Competition comp, Integer adminUserId, String clientIp);

    boolean updateCompetition(Competition comp, Integer adminUserId, String clientIp);

    boolean deleteCompetition(Integer compId, Integer adminUserId, String clientIp);

    SocialProfileDTO getSocialProfile(Integer targetUserId, Integer currentUserId);

    List<SocialActivity> getRecentFeed(int limit);

    void recordSocialMilestone(Integer userId, SocialActivity.ActivityType type, String title, String description);

    boolean sendConnectionRequest(Integer requesterId, Integer receiverId, String clientIp);

    boolean acceptConnectionRequest(Integer connectionId, Integer currentUserId, String clientIp);

    boolean rejectConnectionRequest(Integer connectionId, Integer currentUserId, String clientIp);

    boolean removeConnection(Integer connectionId, Integer currentUserId, String clientIp);

    List<SocialConnection> getUserConnections(Integer userId);

    List<SocialConnection> getPendingRequests(Integer userId);
}
