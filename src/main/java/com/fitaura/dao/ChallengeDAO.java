package com.fitaura.dao;

import com.fitaura.model.Challenge;

import java.sql.Date;
import java.util.List;
import java.util.Optional;

/**
 * Data Access Object interface for Challenge entity.
 */
public interface ChallengeDAO {

    Integer create(Challenge challenge);

    Optional<Challenge> findById(Integer challengeId);

    List<Challenge> findByStatus(Challenge.ChallengeStatus status);

    List<Challenge> findAll();

    List<Challenge> findByCreatedBy(Integer userId);

    List<Challenge> findDiscoverable(String filter, Date today);

    List<Challenge> findPendingModeration();

    int countParticipants(Integer challengeId);

    int countActive();

    int countPendingModeration();

    boolean update(Challenge challenge);

    boolean updateStatus(Integer challengeId, Challenge.ChallengeStatus status);

    boolean delete(Integer challengeId);
}
