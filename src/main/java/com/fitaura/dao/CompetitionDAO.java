package com.fitaura.dao;

import com.fitaura.model.Competition;
import com.fitaura.model.CompetitionParticipant;

import java.math.BigDecimal;
import java.sql.Date;
import java.util.List;
import java.util.Optional;

public interface CompetitionDAO {

    Optional<Competition> findById(Integer id);

    List<Competition> findActive(Date today);

    List<Competition> findUpcoming(Date today);

    List<Competition> findCompleted(Date today);

    List<Competition> findAll(int limit, int offset);

    int countAll();

    Integer create(Competition comp);

    boolean update(Competition comp);

    boolean delete(Integer id);

    boolean isUserParticipating(Integer compId, Integer userId);

    boolean joinCompetition(Integer compId, Integer userId);

    boolean leaveCompetition(Integer compId, Integer userId);

    List<CompetitionParticipant> getParticipants(Integer compId, int limit);

    int countParticipants(Integer compId);

    void updateParticipantScore(Integer compId, Integer userId, BigDecimal score);

    BigDecimal calculateUserScoreForMetric(Integer userId, Competition.Metric metric, Date startDate, Date endDate);

    Integer getUserRankInCompetition(Integer compId, Integer userId);
}
