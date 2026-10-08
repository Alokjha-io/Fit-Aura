package com.fitaura.dto;

import com.fitaura.model.Competition;
import com.fitaura.model.SocialActivity;
import com.fitaura.model.SocialConnection;

import java.io.Serializable;
import java.util.List;

/**
 * Aggregated data transfer object powering the /social Hub interface.
 */
public class SocialHubDTO implements Serializable {

    private static final long serialVersionUID = 1L;

    private boolean isUserSocial;
    private int userPoints;
    private Integer userRank;
    private int userCurrentStreak;
    private int userLongestStreak;
    private int userConnectionCount;
    private List<LeaderboardEntryDTO> topWeeklyLeaderboard;
    private List<LeaderboardEntryDTO> topMonthlyLeaderboard;
    private List<LeaderboardEntryDTO> topAllTimeLeaderboard;
    private List<Competition> activeCompetitions;
    private List<Competition> upcomingCompetitions;
    private List<SocialActivity> recentActivityFeed;
    private List<SocialConnection> connections;
    private List<SocialConnection> pendingRequests;

    public SocialHubDTO() {
    }

    public boolean isUserSocial() {
        return isUserSocial;
    }

    public void setUserSocial(boolean userSocial) {
        isUserSocial = userSocial;
    }

    public int getUserPoints() {
        return userPoints;
    }

    public void setUserPoints(int userPoints) {
        this.userPoints = userPoints;
    }

    public Integer getUserRank() {
        return userRank;
    }

    public void setUserRank(Integer userRank) {
        this.userRank = userRank;
    }

    public int getUserCurrentStreak() {
        return userCurrentStreak;
    }

    public void setUserCurrentStreak(int userCurrentStreak) {
        this.userCurrentStreak = userCurrentStreak;
    }

    public int getUserLongestStreak() {
        return userLongestStreak;
    }

    public void setUserLongestStreak(int userLongestStreak) {
        this.userLongestStreak = userLongestStreak;
    }

    public int getUserConnectionCount() {
        return userConnectionCount;
    }

    public void setUserConnectionCount(int userConnectionCount) {
        this.userConnectionCount = userConnectionCount;
    }

    public List<LeaderboardEntryDTO> getTopWeeklyLeaderboard() {
        return topWeeklyLeaderboard;
    }

    public void setTopWeeklyLeaderboard(List<LeaderboardEntryDTO> topWeeklyLeaderboard) {
        this.topWeeklyLeaderboard = topWeeklyLeaderboard;
    }

    public List<LeaderboardEntryDTO> getTopMonthlyLeaderboard() {
        return topMonthlyLeaderboard;
    }

    public void setTopMonthlyLeaderboard(List<LeaderboardEntryDTO> topMonthlyLeaderboard) {
        this.topMonthlyLeaderboard = topMonthlyLeaderboard;
    }

    public List<LeaderboardEntryDTO> getTopAllTimeLeaderboard() {
        return topAllTimeLeaderboard;
    }

    public void setTopAllTimeLeaderboard(List<LeaderboardEntryDTO> topAllTimeLeaderboard) {
        this.topAllTimeLeaderboard = topAllTimeLeaderboard;
    }

    public List<Competition> getActiveCompetitions() {
        return activeCompetitions;
    }

    public void setActiveCompetitions(List<Competition> activeCompetitions) {
        this.activeCompetitions = activeCompetitions;
    }

    public List<Competition> getUpcomingCompetitions() {
        return upcomingCompetitions;
    }

    public void setUpcomingCompetitions(List<Competition> upcomingCompetitions) {
        this.upcomingCompetitions = upcomingCompetitions;
    }

    public List<SocialActivity> getRecentActivityFeed() {
        return recentActivityFeed;
    }

    public void setRecentActivityFeed(List<SocialActivity> recentActivityFeed) {
        this.recentActivityFeed = recentActivityFeed;
    }

    public List<SocialConnection> getConnections() {
        return connections;
    }

    public void setConnections(List<SocialConnection> connections) {
        this.connections = connections;
    }

    public List<SocialConnection> getPendingRequests() {
        return pendingRequests;
    }

    public void setPendingRequests(List<SocialConnection> pendingRequests) {
        this.pendingRequests = pendingRequests;
    }
}
