package com.fitaura.dao;

import com.fitaura.dto.LeaderboardEntryDTO;
import com.fitaura.model.PointTransaction;

import java.sql.Connection;
import java.util.List;

/**
 * Data Access Object interface for PointTransaction entity.
 */
public interface PointTransactionDAO {

    Integer create(PointTransaction tx);

    Integer create(PointTransaction tx, Connection conn);

    List<PointTransaction> findByUserId(Integer userId);

    List<PointTransaction> findRecentByUserId(Integer userId, int limit);

    int calculateTotalPoints(Integer userId);

    int calculateTotalPoints(Integer userId, Connection conn);

    boolean hasSourceTransaction(Integer userId, PointTransaction.SourceType sourceType, Integer sourceId);

    boolean hasSourceTransaction(Integer userId, PointTransaction.SourceType sourceType, Integer sourceId, Connection conn);

    List<LeaderboardEntryDTO> getSocialLeaderboard(int limit, int offset);

    List<LeaderboardEntryDTO> getSocialLeaderboardForPeriod(int days, int limit, int offset);

    int countSocialLeaderboardUsers();

    Integer calculateUserSocialRank(Integer userId);
}
