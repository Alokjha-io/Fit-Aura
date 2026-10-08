package com.fitaura.dao;

import com.fitaura.model.SocialConnection;

import java.util.List;
import java.util.Optional;

public interface SocialConnectionDAO {

    Optional<SocialConnection> findByUsers(Integer user1, Integer user2);

    List<SocialConnection> findConnectionsForUser(Integer userId);

    List<SocialConnection> findPendingRequestsForUser(Integer userId);

    Integer create(SocialConnection connection);

    boolean updateStatus(Integer connectionId, SocialConnection.Status status);

    boolean delete(Integer connectionId);

    int countConnections(Integer userId);

    boolean areConnected(Integer user1, Integer user2);
}
