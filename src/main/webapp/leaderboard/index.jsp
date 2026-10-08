<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ taglib prefix="c" uri="jakarta.tags.core" %>
<%@ taglib prefix="fmt" uri="jakarta.tags.fmt" %>
<!DOCTYPE html>
<html lang="en">
<head>
    <meta charset="UTF-8">
    <meta name="viewport" content="width=device-width, initial-scale=1.0">
    <title>Community Leaderboard – FitAura</title>
    <meta name="description" content="View the FitAura community points rankings, streak milestones, and celebrate community athletic achievements.">

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

            <button class="navbar-toggler" type="button" data-bs-toggle="collapse" data-bs-target="#leaderboardNavbar" aria-controls="leaderboardNavbar" aria-expanded="false" aria-label="Toggle navigation">
                <span class="navbar-toggler-icon"></span>
            </button>

            <div class="collapse navbar-collapse" id="leaderboardNavbar">
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
                        <a class="nav-link fw-medium text-dark" href="${pageContext.request.contextPath}/gamification">
                            <i class="bi bi-award me-1"></i> Points & Badges
                        </a>
                    </li>
                    <li class="nav-item">
                        <a class="nav-link active fw-bold text-success" aria-current="page" href="${pageContext.request.contextPath}/leaderboard">
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
                        <i class="bi bi-star-fill text-warning-emphasis me-1"></i> ${userTotalPoints} PTS
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

        <!-- Header Section -->
        <div class="d-flex flex-column flex-md-row justify-content-between align-items-md-center mb-4 gap-3">
            <div>
                <h1 class="h3 fw-bold text-dark mb-1">
                    <i class="bi bi-bar-chart-line-fill text-success me-2"></i>Community Leaderboard
                </h1>
                <p class="text-muted mb-0">See how you rank among fellow FitAura athletes and stay motivated together.</p>
            </div>
            <div class="d-flex gap-2">
                <a href="${pageContext.request.contextPath}/gamification" class="btn btn-outline-warning text-dark">
                    <i class="bi bi-award me-1"></i> My Badges & Points
                </a>
                <a href="${pageContext.request.contextPath}/workouts/add" class="btn btn-success">
                    <i class="bi bi-plus-lg me-1"></i> Log Workout
                </a>
            </div>
        </div>

        <!-- Current User Position Summary Card -->
        <div class="card border-0 shadow-sm mb-4 bg-white">
            <div class="card-body p-4">
                <div class="row align-items-center g-3">
                    <div class="col-sm-6 col-lg-3 text-center text-sm-start border-end-sm">
                        <span class="text-muted small text-uppercase fw-semibold">Your Community Rank</span>
                        <div class="d-flex align-items-center justify-content-center justify-content-sm-start gap-2 mt-1">
                            <c:choose>
                                <c:when test="${not empty userRank && currentUser.privacyMode == 'SOCIAL'}">
                                    <span class="display-6 fw-bold text-success">#${userRank}</span>
                                    <span class="text-muted small">of ${totalUsers} athletes</span>
                                </c:when>
                                <c:when test="${currentUser.privacyMode == 'PERSONAL'}">
                                    <span class="fs-5 fw-bold text-secondary">Private Mode</span>
                                </c:when>
                                <c:otherwise>
                                    <span class="fs-5 fw-bold text-muted">Unranked</span>
                                </c:otherwise>
                            </c:choose>
                        </div>
                    </div>

                    <div class="col-sm-6 col-lg-3 text-center text-sm-start border-end-lg">
                        <span class="text-muted small text-uppercase fw-semibold">Your Total Points</span>
                        <div class="mt-1">
                            <span class="display-6 fw-bold text-warning-emphasis">${userTotalPoints}</span>
                            <span class="text-muted small font-monospace">PTS</span>
                        </div>
                    </div>

                    <div class="col-lg-6">
                        <div class="p-3 rounded-3 bg-light border d-flex align-items-center justify-content-between flex-wrap gap-2">
                            <div>
                                <span class="fw-semibold text-dark d-block">
                                    <i class="bi bi-shield-lock text-primary me-1"></i> Privacy Mode:
                                    <span class="badge ${currentUser.privacyMode == 'SOCIAL' ? 'bg-success' : 'bg-secondary'} ms-1">
                                        ${currentUser.privacyMode}
                                    </span>
                                </span>
                                <span class="small text-muted">
                                    <c:choose>
                                        <c:when test="${currentUser.privacyMode == 'SOCIAL'}">
                                            Your display name and points are visible on the community leaderboard.
                                        </c:when>
                                        <c:otherwise>
                                            Your profile is in Personal mode. Your details are hidden from public rankings.
                                        </c:otherwise>
                                    </c:choose>
                                </span>
                            </div>
                            <a href="${pageContext.request.contextPath}/user/profile" class="btn btn-sm btn-outline-primary">
                                Change Privacy
                            </a>
                        </div>
                    </div>
                </div>
            </div>
        </div>

        <!-- Leaderboard Table Card -->
        <div class="card border-0 shadow-sm mb-4">
            <div class="card-header bg-white py-3 border-0 d-flex justify-content-between align-items-center">
                <div>
                    <h5 class="fw-bold text-dark mb-0">
                        <i class="bi bi-trophy-fill text-warning me-2"></i>Global Athlete Rankings
                    </h5>
                    <span class="text-muted small">Ranked by total earned fitness points from workouts, streaks, and challenges.</span>
                </div>
                <span class="badge bg-light text-dark border">
                    ${totalUsers} Participating Athletes
                </span>
            </div>
            <div class="card-body p-0">
                <c:choose>
                    <c:when test="${empty leaderboard}">
                        <div class="text-center py-5 text-muted">
                            <i class="bi bi-bar-chart-line fs-1 d-block mb-3 text-secondary"></i>
                            <h5 class="fw-bold text-dark">No Athletes on the Leaderboard Yet</h5>
                            <p class="mb-3 text-muted">Be the first to record workouts, earn points, and take the #1 spot!</p>
                            <a href="${pageContext.request.contextPath}/workouts/add" class="btn btn-success">Log a Workout Now</a>
                        </div>
                    </c:when>
                    <c:otherwise>
                        <div class="table-responsive">
                            <table class="table table-hover align-middle mb-0">
                                <thead class="table-light text-muted small text-uppercase">
                                    <tr>
                                        <th class="ps-4" style="width: 80px;">Rank</th>
                                        <th>Athlete</th>
                                        <th class="text-center">Workouts</th>
                                        <th class="text-center">Badges</th>
                                        <th class="pe-4 text-end">Total Points</th>
                                    </tr>
                                </thead>
                                <tbody>
                                    <c:forEach var="entry" items="${leaderboard}">
                                        <tr class="${entry.currentUser ? 'table-success border-start border-success border-4' : ''}">
                                            <td class="ps-4 fw-bold">
                                                <c:choose>
                                                    <c:when test="${entry.rank == 1}">
                                                        <span class="badge bg-warning text-dark fs-6 rounded-circle p-2 d-inline-flex align-items-center justify-content-center" style="width: 32px; height: 32px;" title="1st Place - Gold">
                                                            <i class="bi bi-trophy-fill"></i>
                                                        </span>
                                                    </c:when>
                                                    <c:when test="${entry.rank == 2}">
                                                        <span class="badge bg-secondary text-white fs-6 rounded-circle p-2 d-inline-flex align-items-center justify-content-center" style="width: 32px; height: 32px;" title="2nd Place - Silver">
                                                            <i class="bi bi-award-fill"></i>
                                                        </span>
                                                    </c:when>
                                                    <c:when test="${entry.rank == 3}">
                                                        <span class="badge bg-dark-subtle text-dark fs-6 rounded-circle p-2 d-inline-flex align-items-center justify-content-center" style="width: 32px; height: 32px;" title="3rd Place - Bronze">
                                                            <i class="bi bi-award"></i>
                                                        </span>
                                                    </c:when>
                                                    <c:otherwise>
                                                        <span class="text-muted fs-6">#${entry.rank}</span>
                                                    </c:otherwise>
                                                </c:choose>
                                            </td>
                                            <td>
                                                <div class="d-flex align-items-center gap-2">
                                                    <div class="rounded-circle bg-light border p-2 text-secondary d-flex align-items-center justify-content-center" style="width: 38px; height: 38px;">
                                                        <i class="bi bi-person-fill fs-5"></i>
                                                    </div>
                                                    <div>
                                                        <span class="fw-bold ${entry.currentUser ? 'text-success' : 'text-dark'}">
                                                            ${entry.displayName}
                                                        </span>
                                                        <c:if test="${entry.currentUser}">
                                                            <span class="badge bg-success ms-1">You</span>
                                                        </c:if>
                                                    </div>
                                                </div>
                                            </td>
                                            <td class="text-center text-muted">
                                                <span class="badge bg-light text-dark border px-2 py-1">
                                                    <i class="bi bi-activity text-primary me-1"></i> ${entry.totalWorkouts}
                                                </span>
                                            </td>
                                            <td class="text-center text-muted">
                                                <span class="badge bg-light text-dark border px-2 py-1">
                                                    <i class="bi bi-award text-warning me-1"></i> ${entry.earnedAchievements}
                                                </span>
                                            </td>
                                            <td class="pe-4 text-end">
                                                <span class="fw-bold text-success fs-5 font-monospace">${entry.totalPoints}</span>
                                                <span class="small text-muted">PTS</span>
                                            </td>
                                        </tr>
                                    </c:forEach>
                                </tbody>
                            </table>
                        </div>

                        <!-- Pagination Controls -->
                        <c:if test="${totalPages > 1}">
                            <div class="card-footer bg-white border-0 py-3 d-flex justify-content-between align-items-center">
                                <span class="text-muted small">Page ${currentPage} of ${totalPages}</span>
                                <ul class="pagination pagination-sm mb-0">
                                    <li class="page-item ${currentPage <= 1 ? 'disabled' : ''}">
                                        <a class="page-link" href="${pageContext.request.contextPath}/leaderboard?page=${currentPage - 1}">Previous</a>
                                    </li>
                                    <c:forEach var="p" begin="1" end="${totalPages}">
                                        <li class="page-item ${currentPage == p ? 'active' : ''}">
                                            <a class="page-link" href="${pageContext.request.contextPath}/leaderboard?page=${p}">${p}</a>
                                        </li>
                                    </c:forEach>
                                    <li class="page-item ${currentPage >= totalPages ? 'disabled' : ''}">
                                        <a class="page-link" href="${pageContext.request.contextPath}/leaderboard?page=${p}">Next</a>
                                    </li>
                                </ul>
                            </div>
                        </c:if>
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
                <a href="${pageContext.request.contextPath}/gamification" class="text-decoration-none text-muted">Points & Badges</a>
            </div>
        </div>
    </footer>

    <!-- Bootstrap 5 JS Bundle via CDN -->
    <script src="https://cdn.jsdelivr.net/npm/bootstrap@5.3.3/dist/js/bootstrap.bundle.min.js" integrity="sha384-YvpcrYf0tY3lHB60NNkmXc5s9fDVZLESaAA55NDzOxhy9GkcIdslK1eN7N6jIeHz" crossorigin="anonymous"></script>
</body>
</html>
