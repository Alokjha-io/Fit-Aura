package com.fitaura.service;

import com.fitaura.dto.ChallengeDetailDTO;
import com.fitaura.dto.ChallengeSummaryDTO;
import com.fitaura.model.Challenge;
import com.fitaura.model.User;

import java.math.BigDecimal;
import java.util.List;
import java.util.Optional;

/**
 * Service interface for managing challenge lifecycle, participation, moderation,
 * progress tracking, and community privacy rules.
 */
public interface ChallengeService {

    Challenge createChallenge(Challenge challenge, User creator);

    Optional<Challenge> getChallengeById(Integer challengeId);

    ChallengeDetailDTO getChallengeDetails(Integer challengeId, Integer requestingUserId);

    List<ChallengeSummaryDTO> getDiscoverableChallenges(String filter, Integer userId);

    List<ChallengeSummaryDTO> getUserParticipatingChallenges(Integer userId, String filter);

    List<Challenge> getUserCreatedChallenges(Integer userId);

    List<Challenge> getPendingModerationChallenges(User adminUser);

    boolean moderateChallenge(Integer challengeId, String action, User adminUser);

    boolean moderateChallenge(Integer challengeId, String action, Integer adminUserId);

    boolean joinChallenge(Integer challengeId, Integer userId);

    boolean leaveChallenge(Integer challengeId, Integer userId);

    boolean syncUserProgress(Integer challengeId, Integer userId);

    boolean logManualProgress(Integer challengeId, Integer userId, BigDecimal addedValue);
}
