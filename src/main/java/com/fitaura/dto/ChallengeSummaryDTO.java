package com.fitaura.dto;

import com.fitaura.model.Challenge;
import com.fitaura.model.ChallengeParticipant;

import java.io.Serializable;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.temporal.ChronoUnit;

/**
 * DTO for displaying challenges in discovery lists and user challenge cards.
 */
public class ChallengeSummaryDTO implements Serializable {

    private static final long serialVersionUID = 1L;

    private Challenge challenge;
    private String creatorDisplayName;
    private int participantCount;
    private ChallengeParticipant userParticipation;
    private int userProgressPercent;
    private boolean canJoin;
    private boolean canLeave;
    private long daysRemaining;
    private long daysUntilStart;
    private boolean isExpired;
    private boolean isUpcoming;

    public ChallengeSummaryDTO() {
    }

    public ChallengeSummaryDTO(Challenge challenge, String creatorDisplayName, int participantCount,
                               ChallengeParticipant userParticipation, LocalDate today) {
        this.challenge = challenge;
        this.creatorDisplayName = creatorDisplayName;
        this.participantCount = participantCount;
        this.userParticipation = userParticipation;

        if (challenge != null && today != null) {
            LocalDate start = challenge.getStartDate().toLocalDate();
            LocalDate end = challenge.getEndDate().toLocalDate();

            this.daysRemaining = ChronoUnit.DAYS.between(today, end);
            this.daysUntilStart = ChronoUnit.DAYS.between(today, start);
            this.isExpired = today.isAfter(end);
            this.isUpcoming = today.isBefore(start);

            boolean hasJoined = (userParticipation != null &&
                    userParticipation.getStatus() != ChallengeParticipant.ParticipantStatus.LEFT);

            this.canJoin = !hasJoined && challenge.getStatus() == Challenge.ChallengeStatus.ACTIVE && !isExpired;
            this.canLeave = hasJoined && userParticipation.getStatus() != ChallengeParticipant.ParticipantStatus.COMPLETED &&
                    challenge.getStatus() == Challenge.ChallengeStatus.ACTIVE && !isExpired;

            if (userParticipation != null && challenge.getGoalValue() != null && challenge.getGoalValue().compareTo(BigDecimal.ZERO) > 0) {
                BigDecimal progress = userParticipation.getProgressValue() != null ? userParticipation.getProgressValue() : BigDecimal.ZERO;
                double pct = progress.doubleValue() / challenge.getGoalValue().doubleValue() * 100.0;
                this.userProgressPercent = (int) Math.min(100, Math.max(0, Math.round(pct)));
            } else {
                this.userProgressPercent = 0;
            }
        }
    }

    public Challenge getChallenge() {
        return challenge;
    }

    public void setChallenge(Challenge challenge) {
        this.challenge = challenge;
    }

    public String getCreatorDisplayName() {
        return creatorDisplayName;
    }

    public void setCreatorDisplayName(String creatorDisplayName) {
        this.creatorDisplayName = creatorDisplayName;
    }

    public int getParticipantCount() {
        return participantCount;
    }

    public void setParticipantCount(int participantCount) {
        this.participantCount = participantCount;
    }

    public ChallengeParticipant getUserParticipation() {
        return userParticipation;
    }

    public void setUserParticipation(ChallengeParticipant userParticipation) {
        this.userParticipation = userParticipation;
    }

    public int getUserProgressPercent() {
        return userProgressPercent;
    }

    public void setUserProgressPercent(int userProgressPercent) {
        this.userProgressPercent = userProgressPercent;
    }

    public boolean isCanJoin() {
        return canJoin;
    }

    public void setCanJoin(boolean canJoin) {
        this.canJoin = canJoin;
    }

    public boolean isCanLeave() {
        return canLeave;
    }

    public void setCanLeave(boolean canLeave) {
        this.canLeave = canLeave;
    }

    public long getDaysRemaining() {
        return daysRemaining;
    }

    public void setDaysRemaining(long daysRemaining) {
        this.daysRemaining = daysRemaining;
    }

    public long getDaysUntilStart() {
        return daysUntilStart;
    }

    public void setDaysUntilStart(long daysUntilStart) {
        this.daysUntilStart = daysUntilStart;
    }

    public boolean isExpired() {
        return isExpired;
    }

    public void setExpired(boolean expired) {
        isExpired = expired;
    }

    public boolean isUpcoming() {
        return isUpcoming;
    }

    public void setUpcoming(boolean upcoming) {
        isUpcoming = upcoming;
    }

    public boolean isUserJoined() {
        return userParticipation != null && userParticipation.getStatus() != ChallengeParticipant.ParticipantStatus.LEFT;
    }

    public boolean isUserCompleted() {
        return userParticipation != null && userParticipation.getStatus() == ChallengeParticipant.ParticipantStatus.COMPLETED;
    }
}
