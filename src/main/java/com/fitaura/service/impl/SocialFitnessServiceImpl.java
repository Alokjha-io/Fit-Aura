package com.fitaura.service.impl;

import com.fitaura.dao.*;
import com.fitaura.dao.impl.*;
import com.fitaura.dto.*;
import com.fitaura.exception.AuthorizationException;
import com.fitaura.exception.ResourceNotFoundException;
import com.fitaura.exception.ValidationException;
import com.fitaura.model.*;
import com.fitaura.service.GamificationService;
import com.fitaura.service.SocialFitnessService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.math.BigDecimal;
import java.sql.Date;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

public class SocialFitnessServiceImpl implements SocialFitnessService {

    private static final Logger logger = LoggerFactory.getLogger(SocialFitnessServiceImpl.class);

    private final CompetitionDAO competitionDAO;
    private final SocialConnectionDAO connectionDAO;
    private final SocialActivityDAO activityDAO;
    private final UserDAO userDAO;
    private final PointTransactionDAO pointTransactionDAO;
    private final GamificationService gamificationService;
    private final ActivityLogDAO activityLogDAO;

    public SocialFitnessServiceImpl() {
        this(new CompetitionDAOImpl(), new SocialConnectionDAOImpl(), new SocialActivityDAOImpl(),
             new UserDAOImpl(), new PointTransactionDAOImpl(), new GamificationServiceImpl(),
             new ActivityLogDAOImpl());
    }

    public SocialFitnessServiceImpl(CompetitionDAO competitionDAO, SocialConnectionDAO connectionDAO,
                                    SocialActivityDAO activityDAO, UserDAO userDAO,
                                    PointTransactionDAO pointTransactionDAO,
                                    GamificationService gamificationService,
                                    ActivityLogDAO activityLogDAO) {
        this.competitionDAO = competitionDAO;
        this.connectionDAO = connectionDAO;
        this.activityDAO = activityDAO;
        this.userDAO = userDAO;
        this.pointTransactionDAO = pointTransactionDAO;
        this.gamificationService = gamificationService;
        this.activityLogDAO = activityLogDAO;
    }

    @Override
    public SocialHubDTO getSocialHubData(Integer currentUserId) {
        SocialHubDTO hub = new SocialHubDTO();
        Date today = Date.valueOf(LocalDate.now());

        if (currentUserId != null) {
            Optional<User> userOpt = userDAO.findById(currentUserId);
            if (userOpt.isPresent()) {
                User user = userOpt.get();
                boolean isSocial = (user.getPrivacyMode() == User.PrivacyMode.SOCIAL);
                hub.setUserSocial(isSocial);
                hub.setUserPoints(gamificationService.calculateTotalPoints(currentUserId));
                hub.setUserRank(isSocial ? gamificationService.getUserSocialRank(currentUserId) : null);

                StreakInfo streak = gamificationService.calculateUserStreak(currentUserId);
                hub.setUserCurrentStreak(streak != null ? streak.getCurrentStreakDays() : 0);
                hub.setUserLongestStreak(streak != null ? streak.getLongestStreakDays() : 0);
                hub.setUserConnectionCount(connectionDAO.countConnections(currentUserId));
                hub.setConnections(connectionDAO.findConnectionsForUser(currentUserId));
                hub.setPendingRequests(connectionDAO.findPendingRequestsForUser(currentUserId));
            }
        }

        hub.setTopWeeklyLeaderboard(pointTransactionDAO.getSocialLeaderboardForPeriod(7, 5, 0));
        hub.setTopMonthlyLeaderboard(pointTransactionDAO.getSocialLeaderboardForPeriod(30, 5, 0));
        hub.setTopAllTimeLeaderboard(pointTransactionDAO.getSocialLeaderboard(5, 0));

        List<Competition> active = competitionDAO.findActive(today);
        if (currentUserId != null) {
            for (Competition c : active) {
                c.setUserParticipating(competitionDAO.isUserParticipating(c.getCompetitionId(), currentUserId));
                if (c.isUserParticipating()) {
                    c.setUserCurrentScore(competitionDAO.calculateUserScoreForMetric(currentUserId, c.getMetric(), c.getStartDate(), c.getEndDate()));
                    c.setUserRank(competitionDAO.getUserRankInCompetition(c.getCompetitionId(), currentUserId));
                }
            }
        }
        hub.setActiveCompetitions(active);
        hub.setUpcomingCompetitions(competitionDAO.findUpcoming(today));
        hub.setRecentActivityFeed(activityDAO.getRecentFeed(10));

        return hub;
    }

    @Override
    public List<LeaderboardEntryDTO> getWeeklyLeaderboard(int limit, int offset, Integer currentUserId) {
        return pointTransactionDAO.getSocialLeaderboardForPeriod(7, limit, offset);
    }

    @Override
    public List<LeaderboardEntryDTO> getMonthlyLeaderboard(int limit, int offset, Integer currentUserId) {
        return pointTransactionDAO.getSocialLeaderboardForPeriod(30, limit, offset);
    }

    @Override
    public List<LeaderboardEntryDTO> getAllTimeLeaderboard(int limit, int offset, Integer currentUserId) {
        return pointTransactionDAO.getSocialLeaderboard(limit, offset);
    }

    @Override
    public List<Competition> getActiveCompetitions(Integer currentUserId) {
        Date today = Date.valueOf(LocalDate.now());
        List<Competition> list = competitionDAO.findActive(today);
        if (currentUserId != null) {
            for (Competition c : list) {
                c.setUserParticipating(competitionDAO.isUserParticipating(c.getCompetitionId(), currentUserId));
            }
        }
        return list;
    }

    @Override
    public List<Competition> getUpcomingCompetitions(Integer currentUserId) {
        Date today = Date.valueOf(LocalDate.now());
        return competitionDAO.findUpcoming(today);
    }

    @Override
    public List<Competition> getCompletedCompetitions(Integer currentUserId) {
        Date today = Date.valueOf(LocalDate.now());
        return competitionDAO.findCompleted(today);
    }

    @Override
    public Competition getCompetitionDetails(Integer compId, Integer currentUserId) {
        if (compId == null) {
            throw new ValidationException("Competition ID is required.");
        }
        Competition comp = competitionDAO.findById(compId)
                .orElseThrow(() -> new ResourceNotFoundException("Competition #" + compId + " not found."));

        if (currentUserId != null) {
            comp.setUserParticipating(competitionDAO.isUserParticipating(compId, currentUserId));
            if (comp.isUserParticipating()) {
                comp.setUserCurrentScore(competitionDAO.calculateUserScoreForMetric(currentUserId, comp.getMetric(), comp.getStartDate(), comp.getEndDate()));
                comp.setUserRank(competitionDAO.getUserRankInCompetition(compId, currentUserId));
            }
        }
        return comp;
    }

    @Override
    public boolean joinCompetition(Integer compId, Integer userId, String clientIp) {
        if (compId == null || userId == null) {
            throw new ValidationException("Competition ID and user ID are required.");
        }

        User user = userDAO.findById(userId)
                .orElseThrow(() -> new ResourceNotFoundException("User not found."));

        // STRICT PRIVACY RULE: Only SOCIAL mode users can join public competitions
        if (user.getPrivacyMode() != User.PrivacyMode.SOCIAL) {
            throw new AuthorizationException("You must enable Social Mode in your Profile to participate in community competitions.");
        }

        Competition comp = competitionDAO.findById(compId)
                .orElseThrow(() -> new ResourceNotFoundException("Competition #" + compId + " not found."));

        if (comp.getStatus() == Competition.Status.COMPLETED || comp.getStatus() == Competition.Status.CANCELLED) {
            throw new ValidationException("This competition is no longer accepting participants.");
        }

        if (competitionDAO.isUserParticipating(compId, userId)) {
            throw new ValidationException("You are already participating in this competition.");
        }

        boolean joined = competitionDAO.joinCompetition(compId, userId);
        if (joined) {
            // Update initial score based on real activity in date window
            BigDecimal initialScore = competitionDAO.calculateUserScoreForMetric(userId, comp.getMetric(), comp.getStartDate(), comp.getEndDate());
            competitionDAO.updateParticipantScore(compId, userId, initialScore);

            logActivity(userId, "COMPETITION_JOINED", compId, "Joined competition '" + comp.getName() + "'", clientIp);
            activityDAO.logActivity(new SocialActivity(userId, SocialActivity.ActivityType.COMPETITION_JOINED,
                    user.getDisplayName() + " joined competition", comp.getName()));
            logger.info("User {} joined competition {}", userId, compId);
        }
        return joined;
    }

    @Override
    public boolean leaveCompetition(Integer compId, Integer userId, String clientIp) {
        if (compId == null || userId == null) {
            throw new ValidationException("Competition ID and user ID are required.");
        }

        if (!competitionDAO.isUserParticipating(compId, userId)) {
            throw new ValidationException("You are not registered in this competition.");
        }

        boolean left = competitionDAO.leaveCompetition(compId, userId);
        if (left) {
            logActivity(userId, "COMPETITION_LEFT", compId, "Left competition #" + compId, clientIp);
            logger.info("User {} left competition {}", userId, compId);
        }
        return left;
    }

    @Override
    public List<CompetitionParticipant> getCompetitionLeaderboard(Integer compId, int limit) {
        if (compId == null) {
            throw new ValidationException("Competition ID is required.");
        }
        Competition comp = competitionDAO.findById(compId)
                .orElseThrow(() -> new ResourceNotFoundException("Competition #" + compId + " not found."));

        // Refresh scores from real activity before returning
        List<CompetitionParticipant> participants = competitionDAO.getParticipants(compId, limit > 0 ? limit : 50);
        for (CompetitionParticipant cp : participants) {
            BigDecimal realScore = competitionDAO.calculateUserScoreForMetric(cp.getUserId(), comp.getMetric(), comp.getStartDate(), comp.getEndDate());
            if (cp.getCurrentScore().compareTo(realScore) != 0) {
                competitionDAO.updateParticipantScore(compId, cp.getUserId(), realScore);
                cp.setCurrentScore(realScore);
            }
        }
        // Re-fetch sorted with updated ranks
        return competitionDAO.getParticipants(compId, limit > 0 ? limit : 50);
    }

    @Override
    public Competition createCompetition(Competition comp, Integer adminUserId, String clientIp) {
        validateAdmin(adminUserId);
        if (comp == null || comp.getName() == null || comp.getName().trim().isEmpty()) {
            throw new ValidationException("Competition name is required.");
        }
        if (comp.getStartDate() == null || comp.getEndDate() == null) {
            throw new ValidationException("Competition start and end dates are required.");
        }
        if (comp.getStartDate().after(comp.getEndDate())) {
            throw new ValidationException("Start date cannot be after end date.");
        }

        comp.setCreatedBy(adminUserId);
        Integer id = competitionDAO.create(comp);
        comp.setCompetitionId(id);

        logActivity(adminUserId, "COMPETITION_CREATED", id, "Created competition '" + comp.getName() + "'", clientIp);
        logger.info("Admin {} created competition #{}", adminUserId, id);
        return comp;
    }

    @Override
    public boolean updateCompetition(Competition comp, Integer adminUserId, String clientIp) {
        validateAdmin(adminUserId);
        if (comp == null || comp.getCompetitionId() == null) {
            throw new ValidationException("Competition ID is required for update.");
        }
        boolean updated = competitionDAO.update(comp);
        if (updated) {
            logActivity(adminUserId, "COMPETITION_UPDATED", comp.getCompetitionId(),
                    "Updated competition #" + comp.getCompetitionId(), clientIp);
        }
        return updated;
    }

    @Override
    public boolean deleteCompetition(Integer compId, Integer adminUserId, String clientIp) {
        validateAdmin(adminUserId);
        boolean deleted = competitionDAO.delete(compId);
        if (deleted) {
            logActivity(adminUserId, "COMPETITION_DELETED", compId, "Deleted competition #" + compId, clientIp);
        }
        return deleted;
    }

    @Override
    public SocialProfileDTO getSocialProfile(Integer targetUserId, Integer currentUserId) {
        if (targetUserId == null) {
            throw new ValidationException("Target user ID is required.");
        }

        User target = userDAO.findById(targetUserId)
                .orElseThrow(() -> new ResourceNotFoundException("User #" + targetUserId + " not found."));

        // STRICT PRIVACY BOUNDARY: Personal users cannot have public social profiles
        if (target.getPrivacyMode() != User.PrivacyMode.SOCIAL && !targetUserId.equals(currentUserId)) {
            throw new AuthorizationException("This user's profile is set to Personal mode and is not visible to the public.");
        }

        SocialProfileDTO dto = new SocialProfileDTO();
        dto.setUserId(target.getUserId());
        dto.setDisplayName(target.getDisplayName() != null ? target.getDisplayName() : "Athlete");
        dto.setTotalPoints(gamificationService.calculateTotalPoints(targetUserId));
        dto.setSocialRank(gamificationService.getUserSocialRank(targetUserId));

        StreakInfo streak = gamificationService.calculateUserStreak(targetUserId);
        dto.setCurrentStreakDays(streak != null ? streak.getCurrentStreakDays() : 0);
        dto.setLongestStreakDays(streak != null ? streak.getLongestStreakDays() : 0);

        List<AchievementDetailDTO> achievements = gamificationService.getUserAchievements(targetUserId);
        List<AchievementDetailDTO> unlockedOnly = achievements.stream().filter(AchievementDetailDTO::isUnlocked).toList();
        dto.setAchievementCount(unlockedOnly.size());
        dto.setShowcasedAchievements(unlockedOnly);

        dto.setConnectionCount(connectionDAO.countConnections(targetUserId));

        if (currentUserId != null && !currentUserId.equals(targetUserId)) {
            dto.setConnection(connectionDAO.areConnected(currentUserId, targetUserId));
            Optional<SocialConnection> connOpt = connectionDAO.findByUsers(currentUserId, targetUserId);
            dto.setHasPendingConnectionRequest(connOpt.isPresent() && connOpt.get().getStatus() == SocialConnection.Status.PENDING);
        }

        return dto;
    }

    @Override
    public List<SocialActivity> getRecentFeed(int limit) {
        return activityDAO.getRecentFeed(limit > 0 ? limit : 20);
    }

    @Override
    public void recordSocialMilestone(Integer userId, SocialActivity.ActivityType type, String title, String description) {
        if (userId == null) return;
        Optional<User> userOpt = userDAO.findById(userId);
        if (userOpt.isPresent() && userOpt.get().getPrivacyMode() == User.PrivacyMode.SOCIAL) {
            activityDAO.logActivity(new SocialActivity(userId, type, title, description));
        }
    }

    @Override
    public boolean sendConnectionRequest(Integer requesterId, Integer receiverId, String clientIp) {
        if (requesterId == null || receiverId == null) {
            throw new ValidationException("Requester and receiver are required.");
        }
        if (requesterId.equals(receiverId)) {
            throw new ValidationException("You cannot connect with yourself.");
        }

        User requester = userDAO.findById(requesterId)
                .orElseThrow(() -> new ResourceNotFoundException("User not found."));
        User receiver = userDAO.findById(receiverId)
                .orElseThrow(() -> new ResourceNotFoundException("Receiver user not found."));

        if (requester.getPrivacyMode() != User.PrivacyMode.SOCIAL || receiver.getPrivacyMode() != User.PrivacyMode.SOCIAL) {
            throw new AuthorizationException("Both users must have Social Mode enabled to connect.");
        }

        Optional<SocialConnection> existing = connectionDAO.findByUsers(requesterId, receiverId);
        if (existing.isPresent()) {
            throw new ValidationException("A connection request already exists between these users.");
        }

        SocialConnection conn = new SocialConnection();
        conn.setRequesterId(requesterId);
        conn.setReceiverId(receiverId);
        conn.setStatus(SocialConnection.Status.PENDING);

        Integer id = connectionDAO.create(conn);
        logActivity(requesterId, "SOCIAL_CONNECTION_REQUESTED", id,
                "Requested connection with " + receiver.getDisplayName(), clientIp);
        return id != null;
    }

    @Override
    public boolean acceptConnectionRequest(Integer connectionId, Integer currentUserId, String clientIp) {
        if (connectionId == null || currentUserId == null) {
            throw new ValidationException("Connection ID and user ID are required.");
        }

        Optional<SocialConnection> connOpt = connectionDAO.findPendingRequestsForUser(currentUserId).stream()
                .filter(c -> c.getConnectionId().equals(connectionId))
                .findFirst();

        if (connOpt.isEmpty()) {
            throw new AuthorizationException("Pending connection request not found or unauthorized.");
        }

        boolean updated = connectionDAO.updateStatus(connectionId, SocialConnection.Status.ACCEPTED);
        if (updated) {
            logActivity(currentUserId, "SOCIAL_CONNECTION_ACCEPTED", connectionId,
                    "Accepted connection request #" + connectionId, clientIp);
        }
        return updated;
    }

    @Override
    public boolean rejectConnectionRequest(Integer connectionId, Integer currentUserId, String clientIp) {
        if (connectionId == null || currentUserId == null) {
            throw new ValidationException("Connection ID and user ID are required.");
        }

        Optional<SocialConnection> connOpt = connectionDAO.findPendingRequestsForUser(currentUserId).stream()
                .filter(c -> c.getConnectionId().equals(connectionId))
                .findFirst();

        if (connOpt.isEmpty()) {
            throw new AuthorizationException("Pending connection request not found or unauthorized.");
        }

        return connectionDAO.updateStatus(connectionId, SocialConnection.Status.REJECTED);
    }

    @Override
    public boolean removeConnection(Integer connectionId, Integer currentUserId, String clientIp) {
        if (connectionId == null || currentUserId == null) {
            throw new ValidationException("Connection ID and user ID are required.");
        }

        List<SocialConnection> connections = connectionDAO.findConnectionsForUser(currentUserId);
        boolean isParty = connections.stream().anyMatch(c -> c.getConnectionId().equals(connectionId));
        if (!isParty) {
            throw new AuthorizationException("Connection not found or unauthorized.");
        }

        boolean deleted = connectionDAO.delete(connectionId);
        if (deleted) {
            logActivity(currentUserId, "SOCIAL_CONNECTION_REMOVED", connectionId,
                    "Removed connection #" + connectionId, clientIp);
        }
        return deleted;
    }

    @Override
    public List<SocialConnection> getUserConnections(Integer userId) {
        return connectionDAO.findConnectionsForUser(userId);
    }

    @Override
    public List<SocialConnection> getPendingRequests(Integer userId) {
        return connectionDAO.findPendingRequestsForUser(userId);
    }

    private void validateAdmin(Integer userId) {
        if (userId == null) {
            throw new AuthorizationException("Authentication required.");
        }
        Optional<User> opt = userDAO.findById(userId);
        if (opt.isEmpty() || opt.get().getRole() != User.Role.ADMIN) {
            throw new AuthorizationException("Only administrators can perform this action.");
        }
    }

    private void logActivity(Integer userId, String actionType, Integer entityId, String description, String ip) {
        try {
            ActivityLog log = new ActivityLog();
            log.setUserId(userId);
            log.setActionType(actionType);
            log.setEntityType("SOCIAL");
            log.setEntityId(entityId);
            log.setDescription(description);
            log.setIpAddress(ip);
            activityLogDAO.create(log);
        } catch (Exception e) {
            logger.warn("Failed to log activity {}: {}", actionType, e.getMessage());
        }
    }
}
