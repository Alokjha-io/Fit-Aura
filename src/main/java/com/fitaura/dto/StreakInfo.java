package com.fitaura.dto;

import java.io.Serializable;
import java.time.LocalDate;

/**
 * Encapsulates workout streak metrics for an authenticated user.
 */
public class StreakInfo implements Serializable {

    private static final long serialVersionUID = 1L;

    private int currentStreak;
    private int longestStreak;
    private LocalDate lastActiveDate;
    private boolean activeToday;
    private boolean streakAtRisk; // True if active yesterday but not yet worked out today

    public StreakInfo() {
    }

    public StreakInfo(int currentStreak, int longestStreak, LocalDate lastActiveDate, boolean activeToday, boolean streakAtRisk) {
        this.currentStreak = currentStreak;
        this.longestStreak = longestStreak;
        this.lastActiveDate = lastActiveDate;
        this.activeToday = activeToday;
        this.streakAtRisk = streakAtRisk;
    }

    public int getCurrentStreak() {
        return currentStreak;
    }

    public int getCurrentStreakDays() {
        return currentStreak;
    }

    public void setCurrentStreak(int currentStreak) {
        this.currentStreak = currentStreak;
    }

    public int getLongestStreak() {
        return longestStreak;
    }

    public int getLongestStreakDays() {
        return longestStreak;
    }

    public void setLongestStreak(int longestStreak) {
        this.longestStreak = longestStreak;
    }

    public LocalDate getLastActiveDate() {
        return lastActiveDate;
    }

    public void setLastActiveDate(LocalDate lastActiveDate) {
        this.lastActiveDate = lastActiveDate;
    }

    public boolean isActiveToday() {
        return activeToday;
    }

    public void setActiveToday(boolean activeToday) {
        this.activeToday = activeToday;
    }

    public boolean isStreakAtRisk() {
        return streakAtRisk;
    }

    public void setStreakAtRisk(boolean streakAtRisk) {
        this.streakAtRisk = streakAtRisk;
    }
}
