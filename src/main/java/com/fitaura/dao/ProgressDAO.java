package com.fitaura.dao;

import java.sql.Date;
import java.util.Map;

/**
 * Data Access Object for aggregated workout metrics and activity summaries.
 * Uses SQL aggregations (COUNT, SUM, GROUP BY) over the MySQL 'workouts' table.
 */
public interface ProgressDAO {

    public static class WorkoutTypeStat {
        private final String type;
        private final int count;
        private final int totalDuration;
        private final int totalCalories;

        public WorkoutTypeStat(String type, int count, int totalDuration, int totalCalories) {
            this.type = type;
            this.count = count;
            this.totalDuration = totalDuration;
            this.totalCalories = totalCalories;
        }

        public String getType() {
            return type;
        }

        public int getCount() {
            return count;
        }

        public int getTotalDuration() {
            return totalDuration;
        }

        public int getTotalCalories() {
            return totalCalories;
        }
    }

    int countWorkoutsByUser(Integer userId);

    int sumDurationByUser(Integer userId);

    int sumCaloriesByUser(Integer userId);

    int countWorkoutsForDateRange(Integer userId, Date startDate, Date endDate);

    int sumDurationForDateRange(Integer userId, Date startDate, Date endDate);

    int sumCaloriesForDateRange(Integer userId, Date startDate, Date endDate);

    /**
     * Retrieves day-by-day active exercise minutes for a 7-day period.
     * Key is Day of Week index (1 = Monday, ..., 7 = Sunday).
     */
    Map<Integer, Integer> getWeeklyDayMinutes(Integer userId, Date startDate, Date endDate);

    /**
     * Retrieves distribution and metrics broken down by activity type.
     */
    Map<String, WorkoutTypeStat> getWorkoutTypeStats(Integer userId);
}
