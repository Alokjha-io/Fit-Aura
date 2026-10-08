<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ taglib prefix="c" uri="jakarta.tags.core" %>
<%@ taglib prefix="fmt" uri="jakarta.tags.fmt" %>
<!DOCTYPE html>
<html lang="en">
<head>
    <meta charset="UTF-8">
    <meta name="viewport" content="width=device-width, initial-scale=1.0">
    <title>Points & Achievements – FitAura</title>
    <meta name="description" content="Track your earned points, consecutive workout streaks, unlocked achievement badges, and recent activity rewards.">

    <!-- Bootstrap 5 CSS via CDN -->
    <link href="https://cdn.jsdelivr.net/npm/bootstrap@5.3.3/dist/css/bootstrap.min.css" rel="stylesheet" integrity="sha384-QWTKZyjpPEjISv5WaRU9OFeRpok6YctnYmDr5pNlyT2bRjXh0JMhjY6hW+ALEwIH" crossorigin="anonymous">
    <!-- Bootstrap Icons -->
    <link rel="stylesheet" href="https://cdn.jsdelivr.net/npm/bootstrap-icons@1.11.3/font/bootstrap-icons.min.css">
    <!-- FitAura Custom CSS -->
    <link rel="stylesheet" href="${pageContext.request.contextPath}/css/fitaura.css">
</head>
<body class="bg-light d-flex flex-column min-vh-100">

    <!-- Authenticated Header Navigation -->
    <nav class="navbar navbar-expand-lg navbar-light bg-white border-bottom sticky-top py-3">
        <div class="container">
            <a class="navbar-brand brand-badge d-flex align-items-center" href="${pageContext.request.contextPath}/user/dashboard">
                <img src="${pageContext.request.contextPath}/images/fitaura-logo.svg" alt="FitAura Logo" width="36" height="36" class="me-2">
                <span>Fit<span class="text-success">Aura</span></span>
            </a>

            <button class="navbar-toggler" type="button" data-bs-toggle="collapse" data-bs-target="#gamificationNavbar" aria-controls="gamificationNavbar" aria-expanded="false" aria-label="Toggle navigation">
                <span class="navbar-toggler-icon"></span>
            </button>

            <div class="collapse navbar-collapse" id="gamificationNavbar">
                <ul class="navbar-nav me-auto mb-2 mb-lg-0 ms-lg-4">
                    <li class="nav-item">
                        <a class="nav-link fw-medium text-dark" href="${pageContext.request.contextPath}/user/dashboard">
                            <i class="bi bi-house-door me-1"></i> Dashboard
                        </a>
                    </li>
                    <li class="nav-item">
                        <a class="nav-link fw-medium text-dark" href="${pageContext.request.contextPath}/workouts">
                            <i class="bi bi-activity me-1"></i> Workouts
                        </a>
                    </li>
                    <li class="nav-item">
                        <a class="nav-link fw-medium text-dark" href="${pageContext.request.contextPath}/goals">
                            <i class="bi bi-bullseye me-1"></i> Goals
                        </a>
                    </li>
                    <li class="nav-item">
                        <a class="nav-link fw-medium text-dark" href="${pageContext.request.contextPath}/progress">
                            <i class="bi bi-graph-up-arrow me-1"></i> Progress
                        </a>
                    </li>
                    <li class="nav-item">
                        <a class="nav-link fw-medium text-dark" href="${pageContext.request.contextPath}/guidance">
                            <i class="bi bi-compass me-1"></i> Guidance
                        </a>
                    </li>
                    <li class="nav-item">
                        <a class="nav-link fw-medium text-dark" href="${pageContext.request.contextPath}/content">
                            <i class="bi bi-book me-1"></i> Library
                        </a>
                    </li>
                    <li class="nav-item">
                        <a class="nav-link fw-medium text-dark" href="${pageContext.request.contextPath}/challenges">
                            <i class="bi bi-trophy me-1"></i> Challenges
                        </a>
                    </li>
                    <li class="nav-item">
                        <a class="nav-link active fw-bold text-success" aria-current="page" href="${pageContext.request.contextPath}/gamification">
                            <i class="bi bi-award me-1"></i> Points & Badges
                        </a>
                    </li>
                    <li class="nav-item">
                        <a class="nav-link fw-medium text-dark" href="${pageContext.request.contextPath}/leaderboard">
                            <i class="bi bi-bar-chart-line me-1"></i> Leaderboard
                        </a>
                    </li>
                    <li class="nav-item">
                        <a class="nav-link fw-medium text-dark" href="${pageContext.request.contextPath}/user/profile">
                            <i class="bi bi-person-gear me-1"></i> Profile
                        </a>
                    </li>
                </ul>

                <div class="d-flex align-items-center gap-3">
                    <span class="badge bg-warning text-dark px-3 py-2 rounded-pill font-monospace fw-bold">
                        <i class="bi bi-star-fill text-warning-emphasis me-1"></i> ${dashboard.totalPoints} PTS
                    </span>
                    <a href="${pageContext.request.contextPath}/logout" class="btn btn-outline-danger btn-sm px-3">
                        <i class="bi bi-box-arrow-right me-1"></i> Sign Out
                    </a>
                </div>
            </div>
        </div>
    </nav>

    <!-- Main Content Container -->
    <main class="container my-4 flex-grow-1">

        <!-- Error/Success Alerts -->
        <c:if test="${not empty errorMessage}">
            <div class="alert alert-danger alert-dismissible fade show d-flex align-items-center shadow-sm" role="alert">
                <i class="bi bi-exclamation-triangle-fill flex-shrink-0 me-2 fs-5"></i>
                <div>${errorMessage}</div>
                <button type="button" class="btn-close" data-bs-dismiss="alert" aria-label="Close"></button>
            </div>
        </c:if>

        <c:if test="${not empty successMessage}">
            <div class="alert alert-success alert-dismissible fade show d-flex align-items-center shadow-sm" role="alert">
                <i class="bi bi-check-circle-fill flex-shrink-0 me-2 fs-5"></i>
                <div>${successMessage}</div>
                <button type="button" class="btn-close" data-bs-dismiss="alert" aria-label="Close"></button>
            </div>
        </c:if>

        <!-- Header Section -->
        <div class="d-flex flex-column flex-md-row justify-content-between align-items-md-center mb-4 gap-3">
            <div>
                <h1 class="h3 fw-bold text-dark mb-1">
                    <i class="bi bi-award-fill text-warning me-2"></i>Gamification & Badges
                </h1>
                <p class="text-muted mb-0">Earn points with workouts, maintain daily streaks, and unlock achievement milestones.</p>
            </div>
            <div class="d-flex gap-2">
                <a href="${pageContext.request.contextPath}/leaderboard" class="btn btn-outline-success">
                    <i class="bi bi-bar-chart-line-fill me-1"></i> View Leaderboard
                </a>
                <a href="${pageContext.request.contextPath}/workouts/add" class="btn btn-success">
                    <i class="bi bi-plus-lg me-1"></i> Log Workout
                </a>
            </div>
        </div>

        <!-- 4 KPI Metrics Banner -->
        <div class="row g-3 mb-4">
            <!-- Total Points -->
            <div class="col-sm-6 col-lg-3">
                <div class="card border-0 shadow-sm h-100 bg-gradient text-dark p-3" style="background-color: #fff9e6; border-left: 4px solid #ffc107 !important;">
                    <div class="d-flex justify-content-between align-items-center">
                        <div>
                            <span class="text-muted small fw-semibold text-uppercase">Total Points</span>
                            <h2 class="display-6 fw-bold text-dark mt-1 mb-0">${dashboard.totalPoints}</h2>
                        </div>
                        <div class="bg-warning text-dark rounded-circle p-3 d-flex align-items-center justify-content-center" style="width: 52px; height: 52px;">
                            <i class="bi bi-star-fill fs-4"></i>
                        </div>
                    </div>
                    <div class="mt-2 text-muted small">
                        <i class="bi bi-graph-up text-success me-1"></i> Earned across all activities
                    </div>
                </div>
            </div>

            <!-- Current Streak -->
            <div class="col-sm-6 col-lg-3">
                <div class="card border-0 shadow-sm h-100 bg-gradient text-dark p-3" style="background-color: #fdf2e9; border-left: 4px solid #fd7e14 !important;">
                    <div class="d-flex justify-content-between align-items-center">
                        <div>
                            <span class="text-muted small fw-semibold text-uppercase">Current Streak</span>
                            <h2 class="display-6 fw-bold text-dark mt-1 mb-0">${dashboard.currentStreak} <span class="fs-6 fw-normal text-muted">days</span></h2>
                        </div>
                        <div class="bg-warning-subtle text-danger rounded-circle p-3 d-flex align-items-center justify-content-center" style="width: 52px; height: 52px;">
                            <i class="bi bi-fire fs-3 text-danger"></i>
                        </div>
                    </div>
                    <div class="mt-2 small">
                        <c:choose>
                            <c:when test="${dashboard.activeToday}">
                                <span class="badge bg-success"><i class="bi bi-check-circle me-1"></i> Active Today</span>
                            </c:when>
                            <c:when test="${dashboard.streakAtRisk}">
                                <span class="badge bg-danger"><i class="bi bi-exclamation-triangle me-1"></i> At Risk – Log today!</span>
                            </c:when>
                            <c:otherwise>
                                <span class="text-muted">Best: ${dashboard.longestStreak} days</span>
                            </c:otherwise>
                        </c:choose>
                    </div>
                </div>
            </div>

            <!-- Badges Unlocked -->
            <div class="col-sm-6 col-lg-3">
                <div class="card border-0 shadow-sm h-100 bg-gradient text-dark p-3" style="background-color: #f0fdf4; border-left: 4px solid #198754 !important;">
                    <div class="d-flex justify-content-between align-items-center">
                        <div>
                            <span class="text-muted small fw-semibold text-uppercase">Badges Unlocked</span>
                            <h2 class="display-6 fw-bold text-dark mt-1 mb-0">${dashboard.earnedAchievementsCount} <span class="fs-6 fw-normal text-muted">/ ${dashboard.totalAchievementsCount}</span></h2>
                        </div>
                        <div class="bg-success text-white rounded-circle p-3 d-flex align-items-center justify-content-center" style="width: 52px; height: 52px;">
                            <i class="bi bi-trophy-fill fs-4"></i>
                        </div>
                    </div>
                    <div class="mt-2 text-muted small">
                        <div class="progress" style="height: 6px;">
                            <div class="progress-bar bg-success" role="progressbar" style="width: ${dashboard.achievementProgressPercent}%;" aria-valuenow="${dashboard.achievementProgressPercent}" aria-valuemin="0" aria-valuemax="100"></div>
                        </div>
                    </div>
                </div>
            </div>

            <!-- Social Rank -->
            <div class="col-sm-6 col-lg-3">
                <div class="card border-0 shadow-sm h-100 bg-gradient text-dark p-3" style="background-color: #f0f9ff; border-left: 4px solid #0dcaf0 !important;">
                    <div class="d-flex justify-content-between align-items-center">
                        <div>
                            <span class="text-muted small fw-semibold text-uppercase">Community Rank</span>
                            <h2 class="display-6 fw-bold text-dark mt-1 mb-0">
                                <c:choose>
                                    <c:when test="${not empty dashboard.userRank}">
                                        #${dashboard.userRank}
                                    </c:when>
                                    <c:otherwise>
                                        <span class="fs-5 text-muted">Unranked</span>
                                    </c:otherwise>
                                </c:choose>
                            </h2>
                        </div>
                        <div class="bg-info text-white rounded-circle p-3 d-flex align-items-center justify-content-center" style="width: 52px; height: 52px;">
                            <i class="bi bi-people-fill fs-4"></i>
                        </div>
                    </div>
                    <div class="mt-2 text-muted small">
                        <c:choose>
                            <c:when test="${dashboard.privacyMode == 'PERSONAL'}">
                                <span class="badge bg-secondary">Personal Mode</span>
                            </c:when>
                            <c:otherwise>
                                <span>Among ${dashboard.totalSocialUsers} active athletes</span>
                            </c:otherwise>
                        </c:choose>
                    </div>
                </div>
            </div>
        </div>

        <!-- Streak Status Banner -->
        <div class="card border-0 shadow-sm mb-4">
            <div class="card-body p-4">
                <div class="row align-items-center g-3">
                    <div class="col-auto">
                        <div class="bg-warning-subtle text-warning-emphasis p-3 rounded-4 d-flex align-items-center justify-content-center">
                            <i class="bi bi-fire fs-1 text-danger"></i>
                        </div>
                    </div>
                    <div class="col">
                        <h4 class="fw-bold mb-1">
                            <c:choose>
                                <c:when test="${dashboard.currentStreak > 0}">
                                    ${dashboard.currentStreak}-Day Workout Streak! 🔥
                                </c:when>
                                <c:otherwise>
                                    Start Your Daily Streak Today!
                                </c:otherwise>
                            </c:choose>
                        </h4>
                        <p class="text-muted mb-0">
                            <c:choose>
                                <c:when test="${dashboard.activeToday}">
                                    Awesome work! You completed a workout today and kept your streak blazing strong.
                                </c:when>
                                <c:when test="${dashboard.streakAtRisk}">
                                    You worked out yesterday! Log a session today to keep your ${dashboard.currentStreak}-day streak alive before midnight.
                                </c:when>
                                <c:otherwise>
                                    Record a workout today to initiate your active streak and begin earning streak milestone awards.
                                </c:otherwise>
                            </c:choose>
                        </p>
                    </div>
                    <div class="col-md-auto text-md-end">
                        <div class="d-inline-flex gap-2 align-items-center bg-light p-2 px-3 rounded-pill border">
                            <span class="text-muted small">All-time record:</span>
                            <span class="fw-bold text-dark">${dashboard.longestStreak} consecutive days</span>
                        </div>
                    </div>
                </div>
            </div>
        </div>

        <!-- Achievements Section -->
        <div class="card border-0 shadow-sm mb-4">
            <div class="card-header bg-white py-3 border-0 d-flex justify-content-between align-items-center">
                <div>
                    <h5 class="fw-bold text-dark mb-0">
                        <i class="bi bi-trophy text-success me-2"></i>Achievement Badges
                    </h5>
                    <span class="text-muted small">Unlock milestones by logging workouts, hitting streaks, and conquering challenges.</span>
                </div>
                <span class="badge bg-light text-dark border px-3 py-2">
                    ${dashboard.earnedAchievementsCount} / ${dashboard.totalAchievementsCount} Unlocked
                </span>
            </div>
            <div class="card-body p-4 pt-0">
                <c:choose>
                    <c:when test="${empty dashboard.allAchievements}">
                        <div class="text-center py-5 text-muted">
                            <i class="bi bi-award fs-1 d-block mb-3 text-secondary"></i>
                            <p class="mb-0">No achievements configured yet.</p>
                        </div>
                    </c:when>
                    <c:otherwise>
                        <div class="row g-3">
                            <c:forEach var="item" items="${dashboard.allAchievements}">
                                <div class="col-md-6 col-lg-4">
                                    <div class="card h-100 border ${item.earned ? 'border-success-subtle bg-white' : 'border-light-subtle bg-light text-muted'} shadow-sm">
                                        <div class="card-body p-3 d-flex flex-column">
                                            <div class="d-flex align-items-start gap-3 mb-2">
                                                <div class="rounded-circle p-3 d-flex align-items-center justify-content-center ${item.earned ? 'bg-success text-white shadow-sm' : 'bg-secondary-subtle text-secondary'}" style="width: 48px; height: 48px;">
                                                    <i class="bi ${item.bootstrapIconClass} fs-4"></i>
                                                </div>
                                                <div class="flex-grow-1">
                                                    <div class="d-flex justify-content-between align-items-center">
                                                        <h6 class="fw-bold mb-0 ${item.earned ? 'text-dark' : 'text-secondary'}">${item.achievement.name}</h6>
                                                        <span class="badge ${item.earned ? 'bg-warning text-dark' : 'bg-secondary-subtle text-muted'}">
                                                            +${item.achievement.points} pts
                                                        </span>
                                                    </div>
                                                    <p class="small text-muted mb-0 mt-1">${item.achievement.description}</p>
                                                </div>
                                            </div>

                                            <div class="mt-auto pt-2">
                                                <c:choose>
                                                    <c:when test="${item.earned}">
                                                        <div class="d-flex justify-content-between align-items-center text-success small fw-semibold">
                                                            <span><i class="bi bi-check-circle-fill me-1"></i> Unlocked</span>
                                                            <span class="text-muted fw-normal">
                                                                <c:if test="${not empty item.earnedAt}">
                                                                    <fmt:formatDate value="${item.earnedAt}" pattern="MMM dd, yyyy" />
                                                                </c:if>
                                                            </span>
                                                        </div>
                                                    </c:when>
                                                    <c:otherwise>
                                                        <div class="d-flex justify-content-between text-muted small mb-1">
                                                            <span>Progress</span>
                                                            <span>${item.currentProgress} / ${item.targetProgress}</span>
                                                        </div>
                                                        <div class="progress" style="height: 6px;">
                                                            <div class="progress-bar bg-secondary" role="progressbar" style="width: ${item.progressPercentage}%;" aria-valuenow="${item.progressPercentage}" aria-valuemin="0" aria-valuemax="100"></div>
                                                        </div>
                                                    </c:otherwise>
                                                </c:choose>
                                            </div>
                                        </div>
                                    </div>
                                </div>
                            </c:forEach>
                        </div>
                    </c:otherwise>
                </c:choose>
            </div>
        </div>

        <!-- Points Transaction History Section -->
        <div class="card border-0 shadow-sm mb-4">
            <div class="card-header bg-white py-3 border-0 d-flex justify-content-between align-items-center">
                <div>
                    <h5 class="fw-bold text-dark mb-0">
                        <i class="bi bi-clock-history text-primary me-2"></i>Points Transaction History
                    </h5>
                    <span class="text-muted small">Transparent audit log of points awarded for your fitness activities and milestones.</span>
                </div>
                <span class="badge bg-light text-dark border">
                    Latest Transactions
                </span>
            </div>
            <div class="card-body p-0">
                <c:choose>
                    <c:when test="${empty dashboard.recentPointTransactions}">
                        <div class="text-center py-5 text-muted">
                            <i class="bi bi-receipt fs-1 d-block mb-2 text-secondary"></i>
                            <p class="mb-2">No point transactions recorded yet.</p>
                            <a href="${pageContext.request.contextPath}/workouts/add" class="btn btn-sm btn-success">Record a Workout to Earn Points</a>
                        </div>
                    </c:when>
                    <c:otherwise>
                        <div class="table-responsive">
                            <table class="table table-hover align-middle mb-0">
                                <thead class="table-light text-muted small text-uppercase">
                                    <tr>
                                        <th class="ps-4">Source</th>
                                        <th>Description</th>
                                        <th>Points</th>
                                        <th class="pe-4 text-end">Date & Time</th>
                                    </tr>
                                </thead>
                                <tbody>
                                    <c:forEach var="tx" items="${dashboard.recentPointTransactions}">
                                        <tr>
                                            <td class="ps-4">
                                                <c:choose>
                                                    <c:when test="${tx.sourceType == 'WORKOUT'}">
                                                        <span class="badge bg-primary-subtle text-primary border border-primary-subtle px-2 py-1">
                                                            <i class="bi bi-activity me-1"></i> Workout
                                                        </span>
                                                    </c:when>
                                                    <c:when test="${tx.sourceType == 'CHALLENGE'}">
                                                        <span class="badge bg-success-subtle text-success border border-success-subtle px-2 py-1">
                                                            <i class="bi bi-trophy me-1"></i> Challenge
                                                        </span>
                                                    </c:when>
                                                    <c:when test="${tx.sourceType == 'ACHIEVEMENT'}">
                                                        <span class="badge bg-warning-subtle text-warning-emphasis border border-warning-subtle px-2 py-1">
                                                            <i class="bi bi-award me-1"></i> Badge
                                                        </span>
                                                    </c:when>
                                                    <c:otherwise>
                                                        <span class="badge bg-secondary-subtle text-secondary px-2 py-1">
                                                            ${tx.sourceType}
                                                        </span>
                                                    </c:otherwise>
                                                </c:choose>
                                            </td>
                                            <td class="fw-medium text-dark">${tx.description}</td>
                                            <td>
                                                <span class="fw-bold text-success font-monospace">+${tx.points} PTS</span>
                                            </td>
                                            <td class="pe-4 text-end text-muted small">
                                                <fmt:formatDate value="${tx.createdAt}" pattern="MMM dd, yyyy HH:mm" />
                                            </td>
                                        </tr>
                                    </c:forEach>
                                </tbody>
                            </table>
                        </div>
                    </c:otherwise>
                </c:choose>
            </div>
        </div>

    </main>

    <!-- Footer -->
    <footer class="bg-white border-top py-3 mt-auto">
        <div class="container d-flex flex-column flex-sm-row justify-content-between align-items-center small text-muted">
            <div>&copy; 2026 FitAura. All rights reserved.</div>
            <div class="mt-2 mt-sm-0">
                <a href="${pageContext.request.contextPath}/user/profile" class="text-decoration-none text-muted me-3">Privacy Settings</a>
                <a href="${pageContext.request.contextPath}/leaderboard" class="text-decoration-none text-muted">Leaderboard</a>
            </div>
        </div>
    </footer>

    <!-- Bootstrap 5 JS Bundle via CDN -->
    <script src="https://cdn.jsdelivr.net/npm/bootstrap@5.3.3/dist/js/bootstrap.bundle.min.js" integrity="sha384-YvpcrYf0tY3lHB60NNkmXc5s9fDVZLESaAA55NDzOxhy9GkcIdslK1eN7N6jIeHz" crossorigin="anonymous"></script>
</body>
</html>
