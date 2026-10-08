package com.fitaura.dao;

import com.fitaura.dto.ParticipantSummaryDTO;
import com.fitaura.model.ChallengeParticipant;

import java.math.BigDecimal;
import java.util.List;
import java.util.Optional;

/**
 * Data Access Object interface for ChallengeParticipant entity.
 */
public interface ChallengeParticipantDAO {

    Integer create(ChallengeParticipant participant);

    Optional<ChallengeParticipant> findByChallengeAndUser(Integer challengeId, Integer userId);

    List<ChallengeParticipant> findByUserId(Integer userId);

    List<ChallengeParticipant> findByChallengeId(Integer challengeId);

    List<ParticipantSummaryDTO> findCommunityParticipants(Integer challengeId);

    boolean updateProgress(Integer challengeId, Integer userId, BigDecimal progressValue, ChallengeParticipant.ParticipantStatus status);

    boolean completeParticipation(Integer challengeId, Integer userId);

    boolean delete(Integer challengeId, Integer userId);

    int countByUserId(Integer userId);

    int countCompletedByUserId(Integer userId);
}
