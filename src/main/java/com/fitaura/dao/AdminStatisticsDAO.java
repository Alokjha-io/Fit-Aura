package com.fitaura.dao;

import com.fitaura.dto.AdminDashboardSummaryDTO;
import com.fitaura.dto.AdminStatisticsDTO;

/**
 * Data Access Object interface for system-wide administrative aggregations.
 */
public interface AdminStatisticsDAO {

    /**
     * Aggregates high-level system metrics for the administrative dashboard overview.
     */
    AdminDashboardSummaryDTO getDashboardSummary();

    /**
     * Aggregates comprehensive system statistics across users, workouts, goals, challenges, and points.
     */
    AdminStatisticsDTO getSystemStatistics();
}
