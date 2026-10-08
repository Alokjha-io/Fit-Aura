package com.fitaura.service.impl;

import com.fitaura.dao.ActivityLogDAO;
import com.fitaura.dao.ChallengeDAO;
import com.fitaura.dao.ChallengeParticipantDAO;
import com.fitaura.dao.UserDAO;
import com.fitaura.dao.WorkoutDAO;
import com.fitaura.dao.impl.ActivityLogDAOImpl;
import com.fitaura.dao.impl.ChallengeDAOImpl;
import com.fitaura.dao.impl.ChallengeParticipantDAOImpl;
import com.fitaura.dao.impl.UserDAOImpl;
import com.fitaura.dao.impl.WorkoutDAOImpl;
import com.fitaura.dto.ChallengeDetailDTO;
import com.fitaura.dto.ChallengeSummaryDTO;
import com.fitaura.dto.ParticipantSummaryDTO;
import com.fitaura.exception.ValidationException;
import com.fitaura.model.ActivityLog;
import com.fitaura.model.Challenge;
import com.fitaura.model.ChallengeParticipant;
import com.fitaura.model.User;
import com.fitaura.model.Workout;
import com.fitaura.service.ChallengeService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.sql.Date;
import java.time.LocalDate;
import java.time.temporal.ChronoUnit;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

/**
 * Production implementation of ChallengeService.
 * Enforces business validation, moderation lifecycle, participant uniqueness,
 * workout-driven progress calculation, and privacy boundaries.
 */
public class ChallengeServiceImpl implements ChallengeService {

    private static final Logger logger = LoggerFactory.getLogger(ChallengeServiceImpl.class);

    private final ChallengeDAO challengeDAO;
    private final ChallengeParticipantDAO participantDAO;
    private final UserDAO userDAO;
    private final WorkoutDAO workoutDAO;
    private final ActivityLogDAO activityLogDAO;
    private final com.fitaura.service.GamificationService gamificationService;

    public ChallengeServiceImpl() {
        this(new ChallengeDAOImpl(), new ChallengeParticipantDAOImpl(), new UserDAOImpl(),
             new WorkoutDAOImpl(), new ActivityLogDAOImpl(), new GamificationServiceImpl());
    }

    public ChallengeServiceImpl(ChallengeDAO challengeDAO, ChallengeParticipantDAO participantDAO,
                                UserDAO userDAO, WorkoutDAO workoutDAO, ActivityLogDAO activityLogDAO) {
        this(challengeDAO, participantDAO, userDAO, workoutDAO, activityLogDAO, new GamificationServiceImpl());
    }

    public ChallengeServiceImpl(ChallengeDAO challengeDAO, ChallengeParticipantDAO participantDAO,
                                UserDAO userDAO, WorkoutDAO workoutDAO, ActivityLogDAO activityLogDAO,
                                com.fitaura.service.GamificationService gamificationService) {
        this.challengeDAO = challengeDAO;
        this.participantDAO = participantDAO;
        this.userDAO = userDAO;
        this.workoutDAO = workoutDAO;
        this.activityLogDAO = activityLogDAO;
        this.gamificationService = gamificationService;
    }

    @Override
    public Challenge createChallenge(Challenge challenge, User creator) {
        if (challenge == null) {
            throw new ValidationException("Challenge details cannot be null.");
        }
        if (creator == null || creator.getUserId() == null) {
            throw new ValidationException("Authenticated creator is required.");
        }

        // Validate name
        if (challenge.getName() == null || challenge.getName().trim().isEmpty()) {
            throw new ValidationException("Challenge name is required.");
        }
        String name = challenge.getName().trim();
        if (name.length() < 3 || name.length() > 120) {
            throw new ValidationException("Challenge name must be between 3 and 120 characters.");
        }
        challenge.setName(name);

        // Validate description
        if (challenge.getDescription() != null && challenge.getDescription().length() > 1000) {
            throw new ValidationException("Description cannot exceed 1000 characters.");
        }

        // Validate goal value
        if (challenge.getGoalValue() == null || challenge.getGoalValue().compareTo(BigDecimal.ZERO) <= 0) {
            throw new ValidationException("Goal value must be a positive number.");
        }
        if (challenge.getGoalValue().compareTo(new BigDecimal("1000000")) > 0) {
            throw new ValidationException("Goal value exceeds the allowed maximum limit.");
        }

        // Validate goal unit
        if (challenge.getGoalUnit() == null || challenge.getGoalUnit().trim().isEmpty()) {
            throw new ValidationException("Goal unit is required (e.g., MINUTES, CALORIES, WORKOUTS, KM).");
        }
        String unit = challenge.getGoalUnit().trim().toUpperCase();
        if (unit.length() > 30) {
            throw new ValidationException("Goal unit cannot exceed 30 characters.");
        }
        challenge.setGoalUnit(unit);

        // Validate points reward
        if (challenge.getPointsReward() == null || challenge.getPointsReward() < 0) {
            challenge.setPointsReward(0);
        } else if (challenge.getPointsReward() > 10000) {
            throw new ValidationException("Points reward cannot exceed 10,000 points.");
        }

        // Validate dates
        if (challenge.getStartDate() == null) {
            throw new ValidationException("Start date is required.");
        }
        if (challenge.getEndDate() == null) {
            throw new ValidationException("End date is required.");
        }
        if (challenge.getEndDate().before(challenge.getStartDate())) {
            throw new ValidationException("End date cannot be before the start date.");
        }

        long durationDays = ChronoUnit.DAYS.between(
                challenge.getStartDate().toLocalDate(),
                challenge.getEndDate().toLocalDate()
        );
        if (durationDays > 365) {
            throw new ValidationException("Challenge duration cannot exceed 365 days (1 year).");
        }

        // Authoritative creator assignment
        challenge.setCreatedBy(creator.getUserId());

        // Moderation state enforcement:
        // Users -> DRAFT (Pending Admin Review)
        // Admin -> ACTIVE or requested status
        if (creator.getRole() == User.Role.ADMIN) {
            if (challenge.getStatus() == null || challenge.getStatus() == Challenge.ChallengeStatus.DRAFT) {
                challenge.setStatus(Challenge.ChallengeStatus.ACTIVE);
            }
        } else {
            challenge.setStatus(Challenge.ChallengeStatus.DRAFT);
        }

        Integer id = challengeDAO.create(challenge);
        challenge.setChallengeId(id);

        logActivity(creator.getUserId(), "CHALLENGE_CREATED", id,
                "Challenge '" + challenge.getName() + "' created in status " + challenge.getStatus());

        logger.info("Challenge {} created by user {} with status {}", id, creator.getUserId(), challenge.getStatus());
        return challenge;
    }

    @Override
    public Optional<Challenge> getChallengeById(Integer challengeId) {
        if (challengeId == null) return Optional.empty();
        return challengeDAO.findById(challengeId);
    }

    @Override
    public ChallengeDetailDTO getChallengeDetails(Integer challengeId, Integer requestingUserId) {
        if (challengeId == null) return null;

        Optional<Challenge> challengeOpt = challengeDAO.findById(challengeId);
        if (challengeOpt.isEmpty()) {
            return null;
        }

        Challenge challenge = challengeOpt.get();
        LocalDate today = LocalDate.now();

        // Creator metadata
        String creatorName = "Community Admin";
        User.Role creatorRole = User.Role.ADMIN;
        Optional<User> creatorOpt = userDAO.findById(challenge.getCreatedBy());
        if (creatorOpt.isPresent()) {
            User c = creatorOpt.get();
            creatorRole = c.getRole();
            if (c.getPrivacyMode() == User.PrivacyMode.SOCIAL || c.getRole() == User.Role.ADMIN ||
                    (requestingUserId != null && requestingUserId.equals(c.getUserId()))) {
                creatorName = (c.getDisplayName() != null && !c.getDisplayName().trim().isEmpty()) ?
                        c.getDisplayName() : c.getFullName();
            } else {
                creatorName = "Community Member";
            }
        }

        int participantCount = challengeDAO.countParticipants(challengeId);
        ChallengeParticipant userPart = null;
        if (requestingUserId != null) {
            userPart = participantDAO.findByChallengeAndUser(challengeId, requestingUserId).orElse(null);
        }

        ChallengeDetailDTO dto = new ChallengeDetailDTO(challenge, creatorName, creatorRole,
                participantCount, userPart, today, requestingUserId);

        // Fetch community participants respecting privacy
        List<ParticipantSummaryDTO> participants = participantDAO.findCommunityParticipants(challengeId);
        for (ParticipantSummaryDTO p : participants) {
            if (requestingUserId != null && requestingUserId.equals(p.getUserId())) {
                p.setCurrentUser(true);
            }
            if (challenge.getGoalValue() != null && challenge.getGoalValue().compareTo(BigDecimal.ZERO) > 0) {
                BigDecimal val = p.getProgressValue() != null ? p.getProgressValue() : BigDecimal.ZERO;
                double pct = (val.doubleValue() / challenge.getGoalValue().doubleValue()) * 100.0;
                p.setProgressPercentage((int) Math.min(100, Math.max(0, Math.round(pct))));
            }
        }
        dto.setCommunityParticipants(participants);

        return dto;
    }

    @Override
    public List<ChallengeSummaryDTO> getDiscoverableChallenges(String filter, Integer userId) {
        LocalDate today = LocalDate.now();
        Date sqlToday = Date.valueOf(today);

        List<Challenge> challenges = challengeDAO.findDiscoverable(filter, sqlToday);
        List<ChallengeSummaryDTO> result = new ArrayList<>();

        for (Challenge c : challenges) {
            String creatorName = "Community Member";
            Optional<User> creatorOpt = userDAO.findById(c.getCreatedBy());
            if (creatorOpt.isPresent()) {
                User creator = creatorOpt.get();
                if (creator.getPrivacyMode() == User.PrivacyMode.SOCIAL || creator.getRole() == User.Role.ADMIN ||
                        (userId != null && userId.equals(creator.getUserId()))) {
                    creatorName = (creator.getDisplayName() != null && !creator.getDisplayName().trim().isEmpty()) ?
                            creator.getDisplayName() : creator.getFullName();
                }
            }

            int count = challengeDAO.countParticipants(c.getChallengeId());
            ChallengeParticipant userPart = null;
            if (userId != null) {
                userPart = participantDAO.findByChallengeAndUser(c.getChallengeId(), userId).orElse(null);
            }

            result.add(new ChallengeSummaryDTO(c, creatorName, count, userPart, today));
        }

        return result;
    }

    @Override
    public List<ChallengeSummaryDTO> getUserParticipatingChallenges(Integer userId, String filter) {
        List<ChallengeSummaryDTO> result = new ArrayList<>();
        if (userId == null) return result;

        LocalDate today = LocalDate.now();
        List<ChallengeParticipant> participations = participantDAO.findByUserId(userId);

        for (ChallengeParticipant cp : participations) {
            Optional<Challenge> cOpt = challengeDAO.findById(cp.getChallengeId());
            if (cOpt.isPresent()) {
                Challenge c = cOpt.get();

                // Apply filter if specified: "IN_PROGRESS" vs "COMPLETED"
                if ("COMPLETED".equalsIgnoreCase(filter) && cp.getStatus() != ChallengeParticipant.ParticipantStatus.COMPLETED) {
                    continue;
                }
                if ("IN_PROGRESS".equalsIgnoreCase(filter) && cp.getStatus() == ChallengeParticipant.ParticipantStatus.COMPLETED) {
                    continue;
                }

                String creatorName = "Community Member";
                Optional<User> creatorOpt = userDAO.findById(c.getCreatedBy());
                if (creatorOpt.isPresent()) {
                    User creator = creatorOpt.get();
                    if (creator.getPrivacyMode() == User.PrivacyMode.SOCIAL || creator.getRole() == User.Role.ADMIN ||
                            userId.equals(creator.getUserId())) {
                        creatorName = (creator.getDisplayName() != null && !creator.getDisplayName().trim().isEmpty()) ?
                                creator.getDisplayName() : creator.getFullName();
                    }
                }

                int count = challengeDAO.countParticipants(c.getChallengeId());
                result.add(new ChallengeSummaryDTO(c, creatorName, count, cp, today));
            }
        }

        return result;
    }

    @Override
    public List<Challenge> getUserCreatedChallenges(Integer userId) {
        if (userId == null) return new ArrayList<>();
        return challengeDAO.findByCreatedBy(userId);
    }

    @Override
    public List<Challenge> getPendingModerationChallenges(User adminUser) {
        if (adminUser == null || adminUser.getRole() != User.Role.ADMIN) {
            throw new ValidationException("Access denied: Administrative privileges required.");
        }
        return challengeDAO.findPendingModeration();
    }

    @Override
    public boolean moderateChallenge(Integer challengeId, String action, User adminUser) {
        if (adminUser == null || adminUser.getRole() != User.Role.ADMIN) {
            throw new ValidationException("Access denied: Administrative privileges required.");
        }
        if (challengeId == null || action == null) {
            throw new ValidationException("Invalid challenge ID or moderation action.");
        }

        Optional<Challenge> challengeOpt = challengeDAO.findById(challengeId);
        if (challengeOpt.isEmpty()) {
            throw new ValidationException("Challenge not found.");
        }

        Challenge challenge = challengeOpt.get();
        Challenge.ChallengeStatus targetStatus;
        String logAction;

        if ("APPROVE".equalsIgnoreCase(action)) {
            targetStatus = Challenge.ChallengeStatus.ACTIVE;
            logAction = "CHALLENGE_APPROVED";
        } else if ("REJECT".equalsIgnoreCase(action)) {
            targetStatus = Challenge.ChallengeStatus.CANCELLED;
            logAction = "CHALLENGE_REJECTED";
        } else if ("CANCEL".equalsIgnoreCase(action)) {
            targetStatus = Challenge.ChallengeStatus.CANCELLED;
            logAction = "CHALLENGE_CANCELLED";
        } else {
            throw new ValidationException("Unknown moderation action: " + action);
        }

        boolean updated = challengeDAO.updateStatus(challengeId, targetStatus);
        if (updated) {
            logActivity(adminUser.getUserId(), logAction, challengeId,
                    "Challenge '" + challenge.getName() + "' moderated to " + targetStatus + " by Admin");
            logger.info("Admin {} moderated challenge {} to {}", adminUser.getUserId(), challengeId, targetStatus);
        }
        return updated;
    }

    @Override
    public boolean moderateChallenge(Integer challengeId, String action, Integer adminUserId) {
        if (adminUserId == null) {
            throw new ValidationException("Admin user ID is required.");
        }
        User admin = userDAO.findById(adminUserId)
                .orElseThrow(() -> new ValidationException("Admin user not found."));
        return moderateChallenge(challengeId, action, admin);
    }

    @Override
    public boolean joinChallenge(Integer challengeId, Integer userId) {
        if (challengeId == null || userId == null) {
            throw new ValidationException("Challenge ID and User ID are required.");
        }

        Optional<Challenge> challengeOpt = challengeDAO.findById(challengeId);
        if (challengeOpt.isEmpty()) {
            throw new ValidationException("Challenge not found.");
        }

        Challenge challenge = challengeOpt.get();
        LocalDate today = LocalDate.now();

        if (challenge.getStatus() != Challenge.ChallengeStatus.ACTIVE) {
            throw new ValidationException("This challenge is not currently active.");
        }
        if (challenge.getEndDate() != null && today.isAfter(challenge.getEndDate().toLocalDate())) {
            throw new ValidationException("This challenge has already concluded.");
        }

        // Duplicate participation check
        Optional<ChallengeParticipant> existingOpt = participantDAO.findByChallengeAndUser(challengeId, userId);
        if (existingOpt.isPresent()) {
            ChallengeParticipant existing = existingOpt.get();
            if (existing.getStatus() != ChallengeParticipant.ParticipantStatus.LEFT) {
                throw new ValidationException("You are already participating in this challenge.");
            } else {
                // Re-join previously left challenge
                participantDAO.updateProgress(challengeId, userId, BigDecimal.ZERO, ChallengeParticipant.ParticipantStatus.JOINED);
                syncUserProgress(challengeId, userId);
                logActivity(userId, "CHALLENGE_JOINED", challengeId, "Re-joined challenge '" + challenge.getName() + "'");
                return true;
            }
        }

        ChallengeParticipant participant = new ChallengeParticipant();
        participant.setChallengeId(challengeId);
        participant.setUserId(userId);
        participant.setProgressValue(BigDecimal.ZERO);
        participant.setStatus(ChallengeParticipant.ParticipantStatus.JOINED);

        participantDAO.create(participant);

        // Initial progress synchronization from eligible workouts
        syncUserProgress(challengeId, userId);

        logActivity(userId, "CHALLENGE_JOINED", challengeId, "Joined challenge '" + challenge.getName() + "'");
        logger.info("User {} joined challenge {}", userId, challengeId);
        return true;
    }

    @Override
    public boolean leaveChallenge(Integer challengeId, Integer userId) {
        if (challengeId == null || userId == null) {
            throw new ValidationException("Challenge ID and User ID are required.");
        }

        Optional<ChallengeParticipant> partOpt = participantDAO.findByChallengeAndUser(challengeId, userId);
        if (partOpt.isEmpty()) {
            throw new ValidationException("You are not currently enrolled in this challenge.");
        }

        ChallengeParticipant participant = partOpt.get();
        if (participant.getStatus() == ChallengeParticipant.ParticipantStatus.COMPLETED) {
            throw new ValidationException("Completed challenges cannot be abandoned.");
        }

        boolean deleted = participantDAO.delete(challengeId, userId);
        if (deleted) {
            logActivity(userId, "CHALLENGE_LEFT", challengeId, "Left challenge ID " + challengeId);
            logger.info("User {} left challenge {}", userId, challengeId);
        }
        return deleted;
    }

    @Override
    public boolean syncUserProgress(Integer challengeId, Integer userId) {
        if (challengeId == null || userId == null) return false;

        Optional<Challenge> challengeOpt = challengeDAO.findById(challengeId);
        Optional<ChallengeParticipant> partOpt = participantDAO.findByChallengeAndUser(challengeId, userId);

        if (challengeOpt.isEmpty() || partOpt.isEmpty()) return false;

        Challenge challenge = challengeOpt.get();
        ChallengeParticipant participant = partOpt.get();

        if (participant.getStatus() == ChallengeParticipant.ParticipantStatus.COMPLETED) {
            return true; // Already completed
        }

        // Fetch user workouts within the challenge timeframe
        Date startDate = challenge.getStartDate();
        Date endDate = challenge.getEndDate();
        List<Workout> workouts = workoutDAO.findByUserIdAndDateRange(userId, startDate, endDate);

        BigDecimal calculatedProgress = calculateProgressFromWorkouts(workouts, challenge.getGoalUnit());

        // Clamp between 0 and goalValue
        BigDecimal goalValue = challenge.getGoalValue() != null ? challenge.getGoalValue() : BigDecimal.ZERO;
        if (calculatedProgress.compareTo(goalValue) > 0) {
            calculatedProgress = goalValue;
        }
        if (calculatedProgress.compareTo(BigDecimal.ZERO) < 0) {
            calculatedProgress = BigDecimal.ZERO;
        }

        ChallengeParticipant.ParticipantStatus newStatus = ChallengeParticipant.ParticipantStatus.IN_PROGRESS;
        boolean reachedGoal = goalValue.compareTo(BigDecimal.ZERO) > 0 && calculatedProgress.compareTo(goalValue) >= 0;

        if (reachedGoal) {
            newStatus = ChallengeParticipant.ParticipantStatus.COMPLETED;
            participantDAO.updateProgress(challengeId, userId, calculatedProgress, newStatus);
            participantDAO.completeParticipation(challengeId, userId);
            logActivity(userId, "CHALLENGE_COMPLETED", challengeId,
                    "Completed challenge '" + challenge.getName() + "' reaching goal of " + goalValue + " " + challenge.getGoalUnit());

            try {
                if (gamificationService != null) {
                    gamificationService.awardChallengeCompletionPoints(userId, challengeId, challenge.getName());
                }
            } catch (Exception ex) {
                logger.warn("Non-fatal: failed to award challenge completion points for user {}: {}", userId, ex.getMessage());
            }

            logger.info("User {} completed challenge {}", userId, challengeId);
        } else {
            if (calculatedProgress.compareTo(BigDecimal.ZERO) == 0) {
                newStatus = ChallengeParticipant.ParticipantStatus.JOINED;
            }
            participantDAO.updateProgress(challengeId, userId, calculatedProgress, newStatus);
        }

        return true;
    }

    @Override
    public boolean logManualProgress(Integer challengeId, Integer userId, BigDecimal addedValue) {
        if (challengeId == null || userId == null) {
            throw new ValidationException("Challenge ID and User ID are required.");
        }
        if (addedValue == null || addedValue.compareTo(BigDecimal.ZERO) <= 0) {
            throw new ValidationException("Progress contribution must be greater than zero.");
        }

        Optional<Challenge> challengeOpt = challengeDAO.findById(challengeId);
        Optional<ChallengeParticipant> partOpt = participantDAO.findByChallengeAndUser(challengeId, userId);

        if (challengeOpt.isEmpty() || partOpt.isEmpty()) {
            throw new ValidationException("Participation record not found.");
        }

        Challenge challenge = challengeOpt.get();
        ChallengeParticipant participant = partOpt.get();

        if (participant.getStatus() == ChallengeParticipant.ParticipantStatus.COMPLETED) {
            throw new ValidationException("Challenge is already completed.");
        }

        BigDecimal current = participant.getProgressValue() != null ? participant.getProgressValue() : BigDecimal.ZERO;
        BigDecimal updated = current.add(addedValue);
        BigDecimal goal = challenge.getGoalValue() != null ? challenge.getGoalValue() : BigDecimal.ZERO;

        if (goal.compareTo(BigDecimal.ZERO) > 0 && updated.compareTo(goal) >= 0) {
            updated = goal;
            participantDAO.updateProgress(challengeId, userId, updated, ChallengeParticipant.ParticipantStatus.COMPLETED);
            participantDAO.completeParticipation(challengeId, userId);
            logActivity(userId, "CHALLENGE_COMPLETED", challengeId,
                    "Completed challenge '" + challenge.getName() + "' reaching goal of " + goal + " " + challenge.getGoalUnit());

            try {
                if (gamificationService != null) {
                    gamificationService.awardChallengeCompletionPoints(userId, challengeId, challenge.getName());
                }
            } catch (Exception ex) {
                logger.warn("Non-fatal: failed to award challenge completion points for user {}: {}", userId, ex.getMessage());
            }
        } else {
            participantDAO.updateProgress(challengeId, userId, updated, ChallengeParticipant.ParticipantStatus.IN_PROGRESS);
        }

        return true;
    }

    private BigDecimal calculateProgressFromWorkouts(List<Workout> workouts, String goalUnit) {
        if (workouts == null || workouts.isEmpty() || goalUnit == null) {
            return BigDecimal.ZERO;
        }

        String unit = goalUnit.toUpperCase();
        BigDecimal total = BigDecimal.ZERO;

        if (unit.contains("MIN") || unit.contains("HOUR") || unit.contains("DURATION")) {
            long totalMinutes = 0;
            for (Workout w : workouts) {
                totalMinutes += w.getDurationMinutes();
            }
            if (unit.contains("HOUR")) {
                total = BigDecimal.valueOf(totalMinutes).divide(BigDecimal.valueOf(60), 2, RoundingMode.HALF_UP);
            } else {
                total = BigDecimal.valueOf(totalMinutes);
            }
        } else if (unit.contains("CAL") || unit.contains("KCAL") || unit.contains("ENERGY")) {
            long totalCalories = 0;
            for (Workout w : workouts) {
                totalCalories += w.getCaloriesBurned() != null ? w.getCaloriesBurned() : 0;
            }
            total = BigDecimal.valueOf(totalCalories);
        } else if (unit.contains("WORKOUT") || unit.contains("SESSION") || unit.contains("COUNT") || unit.contains("TIMES")) {
            total = BigDecimal.valueOf(workouts.size());
        } else {
            // General activity sum: duration minutes default
            long totalMinutes = 0;
            for (Workout w : workouts) {
                totalMinutes += w.getDurationMinutes();
            }
            total = BigDecimal.valueOf(totalMinutes);
        }

        return total;
    }

    private void logActivity(Integer userId, String actionType, Integer entityId, String description) {
        try {
            ActivityLog log = new ActivityLog(null, userId, actionType, "CHALLENGE", entityId, description, "127.0.0.1");
            activityLogDAO.create(log);
        } catch (Exception e) {
            logger.warn("Failed to write activity log: {}", e.getMessage());
        }
    }
}
