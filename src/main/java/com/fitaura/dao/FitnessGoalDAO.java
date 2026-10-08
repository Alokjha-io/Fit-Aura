package com.fitaura.dao;

import com.fitaura.model.FitnessGoal;

import java.util.List;
import java.util.Optional;

/**
 * Data Access Object interface for FitnessGoal entity.
 */
public interface FitnessGoalDAO {

    Integer create(FitnessGoal goal);

    Optional<FitnessGoal> findById(Integer goalId, Integer userId);

    Optional<FitnessGoal> findById(Integer goalId);

    List<FitnessGoal> findByUserId(Integer userId);

    List<FitnessGoal> findByUserIdAndStatus(Integer userId, FitnessGoal.GoalStatus status);

    List<FitnessGoal> findActiveByUserId(Integer userId);

    boolean update(FitnessGoal goal, Integer userId);

    boolean update(FitnessGoal goal);

    boolean updateStatus(Integer goalId, Integer userId, FitnessGoal.GoalStatus status);

    boolean updateStatus(Integer goalId, FitnessGoal.GoalStatus status);

    boolean delete(Integer goalId, Integer userId);

    boolean delete(Integer goalId);

    int countByUserId(Integer userId);

    int countActiveByUserId(Integer userId);
}
