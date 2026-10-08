<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ taglib prefix="c" uri="jakarta.tags.core" %>
<!DOCTYPE html>
<html lang="en">
<head>
    <meta charset="UTF-8">
    <meta name="viewport" content="width=device-width, initial-scale=1.0">
    <title>Social Fitness Hub – FitAura</title>
    <!-- Bootstrap 5 CSS via CDN -->
    <link href="https://cdn.jsdelivr.net/npm/bootstrap@5.3.3/dist/css/bootstrap.min.css" rel="stylesheet">
    <link rel="stylesheet" href="https://cdn.jsdelivr.net/npm/bootstrap-icons@1.11.3/font/bootstrap-icons.min.css">
    <link rel="stylesheet" href="${pageContext.request.contextPath}/css/fitaura.css">
</head>
<body class="bg-light d-flex flex-column min-vh-100">

    <!-- Header Navigation -->
    <nav class="navbar navbar-expand-lg navbar-light bg-white border-bottom sticky-top py-3">
        <div class="container">
            <a class="navbar-brand brand-badge d-flex align-items-center" href="${pageContext.request.contextPath}/user/dashboard">
                <img src="${pageContext.request.contextPath}/images/fitaura-logo.svg" alt="FitAura Logo" width="36" height="36" class="me-2">
                <span>Fit<span class="text-success">Aura</span></span>
            </a>
            <div class="collapse navbar-collapse" id="userNavbar">
                <ul class="navbar-nav me-auto mb-2 mb-lg-0 ms-lg-4">
                    <li class="nav-item"><a class="nav-link fw-medium text-dark" href="${pageContext.request.contextPath}/user/dashboard"><i class="bi bi-house-door me-1"></i> Dashboard</a></li>
                    <li class="nav-item"><a class="nav-link fw-medium text-dark" href="${pageContext.request.contextPath}/workouts"><i class="bi bi-activity me-1"></i> Workouts</a></li>
                    <li class="nav-item"><a class="nav-link active fw-bold text-success" href="${pageContext.request.contextPath}/social"><i class="bi bi-people me-1"></i> Social Hub</a></li>
                    <li class="nav-item"><a class="nav-link fw-medium text-dark" href="${pageContext.request.contextPath}/challenges"><i class="bi bi-trophy me-1"></i> Challenges</a></li>
                    <li class="nav-item"><a class="nav-link fw-medium text-dark" href="${pageContext.request.contextPath}/leaderboard"><i class="bi bi-bar-chart-line me-1"></i> Leaderboard</a></li>
                    <li class="nav-item"><a class="nav-link fw-medium text-dark" href="${pageContext.request.contextPath}/user/profile"><i class="bi bi-person-gear me-1"></i> Profile & Privacy</a></li>
                </ul>
                <div class="d-flex align-items-center gap-2">
                    <a href="${pageContext.request.contextPath}/user/profile" class="btn btn-outline-secondary btn-sm px-3">
                        <c:out value="${currentUser.displayName != null ? currentUser.displayName : currentUser.fullName}"/>
                    </a>
                    <a href="${pageContext.request.contextPath}/logout" class="btn btn-outline-danger btn-sm px-3">Log Out</a>
                </div>
            </div>
        </div>
    </nav>

    <main class="container py-4 flex-grow-1">

        <c:if test="${not empty param.success}">
            <div class="alert alert-success alert-dismissible fade show rounded-4 shadow-xs" role="alert">
                <i class="bi bi-check-circle-fill me-2"></i> Action processed successfully!
                <button type="button" class="btn-close" data-bs-dismiss="alert"></button>
            </div>
        </c:if>
        <c:if test="${not empty param.error}">
            <div class="alert alert-danger alert-dismissible fade show rounded-4 shadow-xs" role="alert">
                <i class="bi bi-exclamation-triangle-fill me-2"></i> <c:out value="${param.error}"/>
                <button type="button" class="btn-close" data-bs-dismiss="alert"></button>
            </div>
        </c:if>

        <!-- Social Privacy Mode Status Banner -->
        <c:choose>
            <c:when test="${!hub.userSocial}">
                <div class="card border-0 rounded-4 p-4 mb-4 shadow-xs bg-dark text-white">
                    <div class="d-flex flex-column flex-md-row justify-content-between align-items-md-center gap-3">
                        <div class="d-flex align-items-center gap-3">
                            <div class="rounded-circle bg-warning text-dark d-flex align-items-center justify-content-center" style="width: 52px; height: 52px;">
                                <i class="bi bi-shield-lock-fill fs-3"></i>
                            </div>
                            <div>
                                <h5 class="fw-bold mb-1">Personal Mode is Active</h5>
                                <p class="mb-0 small text-light opacity-75">
                                    Your workouts and statistics are private. Switch to <strong>Social Mode</strong> to compete in weekly competitions, climb public leaderboards, and showcase earned achievements.
                                </p>
                            </div>
                        </div>
                        <a href="${pageContext.request.contextPath}/user/profile" class="btn btn-warning btn-sm px-4 fw-bold text-dark text-nowrap">
                            Enable Social Mode
                        </a>
                    </div>
                </div>
            </c:when>
            <c:otherwise>
                <!-- Social User Profile Ribbon -->
                <div class="card border-0 rounded-4 p-4 mb-4 shadow-xs text-white" style="background: linear-gradient(135deg, #064e3b 0%, #065f46 60%, #0f172a 100%);">
                    <div class="row align-items-center g-3">
                        <div class="col-md-7">
                            <div class="d-inline-flex align-items-center gap-2 px-3 py-1 rounded-pill bg-success-subtle text-success small fw-bold mb-2">
                                <i class="bi bi-award-fill"></i> Community Athlete Profile
                            </div>
                            <h3 class="fw-bold mb-1"><c:out value="${currentUser.displayName != null ? currentUser.displayName : currentUser.fullName}"/></h3>
                            <p class="small text-light opacity-75 mb-0">Active in Social Fitness Mode. Competing with real workout consistency.</p>
                        </div>
                        <div class="col-md-5">
                            <div class="row g-2 text-center">
                                <div class="col-4">
                                    <div class="p-2 bg-white bg-opacity-10 rounded-3">
                                        <div class="small text-white-50">Points</div>
                                        <div class="fs-5 fw-bold text-warning">${hub.userPoints}</div>
                                    </div>
                                </div>
                                <div class="col-4">
                                    <div class="p-2 bg-white bg-opacity-10 rounded-3">
                                        <div class="small text-white-50">Rank</div>
                                        <div class="fs-5 fw-bold text-white">#${hub.userRank != null ? hub.userRank : '—'}</div>
                                    </div>
                                </div>
                                <div class="col-4">
                                    <div class="p-2 bg-white bg-opacity-10 rounded-3">
                                        <div class="small text-white-50">Streak</div>
                                        <div class="fs-5 fw-bold text-danger">${hub.userCurrentStreak}d 🔥</div>
                                    </div>
                                </div>
                            </div>
                        </div>
                    </div>
                </div>
            </c:otherwise>
        </c:choose>

        <!-- Main Content Grid -->
        <div class="row g-4">

            <!-- Left Column: Competitions & Activity -->
            <div class="col-lg-8">

                <!-- Active Competitions -->
                <div class="card border rounded-4 shadow-xs bg-white mb-4">
                    <div class="card-header bg-transparent py-3 border-bottom d-flex justify-content-between align-items-center">
                        <h5 class="fw-bold text-dark mb-0"><i class="bi bi-trophy text-warning me-2"></i>Weekly Competitions</h5>
                        <span class="badge bg-warning-subtle text-warning-emphasis">${hub.activeCompetitions.size()} Active</span>
                    </div>
                    <div class="p-4">
                        <c:choose>
                            <c:when test="${not empty hub.activeCompetitions}">
                                <div class="row g-3">
                                    <c:forEach var="c" items="${hub.activeCompetitions}">
                                        <div class="col-12">
                                            <div class="p-3 border rounded-3 bg-light d-flex flex-column flex-md-row justify-content-between align-items-md-center gap-3">
                                                <div>
                                                    <div class="d-flex align-items-center gap-2 mb-1">
                                                        <h6 class="fw-bold text-dark mb-0"><c:out value="${c.name}"/></h6>
                                                        <span class="badge bg-primary-subtle text-primary small"><c:out value="${c.metricLabel}"/></span>
                                                    </div>
                                                    <p class="small text-secondary mb-1"><c:out value="${c.description}"/></p>
                                                    <small class="text-muted"><i class="bi bi-calendar3 me-1"></i><c:out value="${c.startDate}"/> to <c:out value="${c.endDate}"/> &bull; <strong>+${c.rewardPoints} pts reward</strong></small>
                                                </div>
                                                <div class="d-flex align-items-center gap-2 text-nowrap">
                                                    <a href="${pageContext.request.contextPath}/social/competition?id=${c.competitionId}" class="btn btn-outline-success btn-sm px-3 fw-medium">
                                                        Leaderboard
                                                    </a>
                                                    <c:if test="${hub.userSocial}">
                                                        <c:choose>
                                                            <c:when test="${c.userParticipating}">
                                                                <span class="badge bg-success-subtle text-success py-2 px-3 fw-semibold">
                                                                    <i class="bi bi-check2-circle me-1"></i> Enrolled
                                                                </span>
                                                            </c:when>
                                                            <c:otherwise>
                                                                <form action="${pageContext.request.contextPath}/social" method="POST" class="d-inline">
                                                                    <input type="hidden" name="csrfToken" value="${sessionScope.csrfToken}"/>
                                                                    <input type="hidden" name="action" value="joinCompetition"/>
                                                                    <input type="hidden" name="competitionId" value="${c.competitionId}"/>
                                                                    <button type="submit" class="btn btn-success btn-sm px-3 fw-semibold">
                                                                        Join Competition
                                                                    </button>
                                                                </form>
                                                            </c:otherwise>
                                                        </c:choose>
                                                    </c:if>
                                                </div>
                                            </div>
                                        </div>
                                    </c:forEach>
                                </div>
                            </c:when>
                            <c:otherwise>
                                <div class="text-center py-4 text-muted small">No active competitions this week. Check back soon for the next event!</div>
                            </c:otherwise>
                        </c:choose>
                    </div>
                </div>

                <!-- Leaderboard Tabs (Weekly / Monthly / All-Time) -->
                <div class="card border rounded-4 shadow-xs bg-white mb-4">
                    <div class="card-header bg-transparent py-3 border-bottom d-flex justify-content-between align-items-center">
                        <h5 class="fw-bold text-dark mb-0"><i class="bi bi-bar-chart-line text-success me-2"></i>Community Leaderboards</h5>
                        <a href="${pageContext.request.contextPath}/leaderboard" class="small text-success fw-semibold text-decoration-none">Full Rankings &rarr;</a>
                    </div>
                    <div class="p-3">
                        <ul class="nav nav-pills mb-3" id="leaderboardPills" role="tablist">
                            <li class="nav-item" role="presentation">
                                <button class="nav-link active btn-sm" id="weekly-tab" data-bs-toggle="pill" data-bs-target="#weekly" type="button" role="tab">Weekly (Last 7 Days)</button>
                            </li>
                            <li class="nav-item" role="presentation">
                                <button class="nav-link btn-sm" id="monthly-tab" data-bs-toggle="pill" data-bs-target="#monthly" type="button" role="tab">Monthly (Last 30 Days)</button>
                            </li>
                            <li class="nav-item" role="presentation">
                                <button class="nav-link btn-sm" id="alltime-tab" data-bs-toggle="pill" data-bs-target="#alltime" type="button" role="tab">All-Time</button>
                            </li>
                        </ul>
                        <div class="tab-content" id="leaderboardTabContent">
                            <!-- Weekly Tab -->
                            <div class="tab-pane fade show active" id="weekly" role="tabpanel">
                                <div class="table-responsive">
                                    <table class="table align-middle table-hover mb-0">
                                        <thead class="table-light small">
                                            <tr>
                                                <th style="width: 60px;">Rank</th>
                                                <th>Athlete</th>
                                                <th>Recent Workouts</th>
                                                <th class="text-end">Weekly Points</th>
                                            </tr>
                                        </thead>
                                        <tbody>
                                            <c:forEach var="entry" items="${hub.topWeeklyLeaderboard}">
                                                <tr>
                                                    <td>
                                                        <span class="badge ${entry.rank == 1 ? 'bg-warning text-dark' : (entry.rank == 2 ? 'bg-secondary text-white' : (entry.rank == 3 ? 'bg-warning-subtle text-dark' : 'bg-light text-dark border'))}">
                                                            #${entry.rank}
                                                        </span>
                                                    </td>
                                                    <td class="fw-semibold">
                                                        <a href="${pageContext.request.contextPath}/social/profile?id=${entry.userId}" class="text-dark text-decoration-none">
                                                            <c:out value="${entry.displayName}"/>
                                                        </a>
                                                    </td>
                                                    <td class="small text-secondary">${entry.totalWorkouts} sessions</td>
                                                    <td class="text-end fw-bold text-success">+${entry.totalPoints} pts</td>
                                                </tr>
                                            </c:forEach>
                                        </tbody>
                                    </table>
                                </div>
                            </div>
                            <!-- Monthly Tab -->
                            <div class="tab-pane fade" id="monthly" role="tabpanel">
                                <div class="table-responsive">
                                    <table class="table align-middle table-hover mb-0">
                                        <thead class="table-light small">
                                            <tr>
                                                <th style="width: 60px;">Rank</th>
                                                <th>Athlete</th>
                                                <th>Monthly Workouts</th>
                                                <th class="text-end">Monthly Points</th>
                                            </tr>
                                        </thead>
                                        <tbody>
                                            <c:forEach var="entry" items="${hub.topMonthlyLeaderboard}">
                                                <tr>
                                                    <td><span class="badge bg-light text-dark border">#${entry.rank}</span></td>
                                                    <td class="fw-semibold">
                                                        <a href="${pageContext.request.contextPath}/social/profile?id=${entry.userId}" class="text-dark text-decoration-none">
                                                            <c:out value="${entry.displayName}"/>
                                                        </a>
                                                    </td>
                                                    <td class="small text-secondary">${entry.totalWorkouts} sessions</td>
                                                    <td class="text-end fw-bold text-success">+${entry.totalPoints} pts</td>
                                                </tr>
                                            </c:forEach>
                                        </tbody>
                                    </table>
                                </div>
                            </div>
                            <!-- All-Time Tab -->
                            <div class="tab-pane fade" id="alltime" role="tabpanel">
                                <div class="table-responsive">
                                    <table class="table align-middle table-hover mb-0">
                                        <thead class="table-light small">
                                            <tr>
                                                <th style="width: 60px;">Rank</th>
                                                <th>Athlete</th>
                                                <th>Badges</th>
                                                <th class="text-end">All-Time Points</th>
                                            </tr>
                                        </thead>
                                        <tbody>
                                            <c:forEach var="entry" items="${hub.topAllTimeLeaderboard}">
                                                <tr>
                                                    <td><span class="badge bg-light text-dark border">#${entry.rank}</span></td>
                                                    <td class="fw-semibold">
                                                        <a href="${pageContext.request.contextPath}/social/profile?id=${entry.userId}" class="text-dark text-decoration-none">
                                                            <c:out value="${entry.displayName}"/>
                                                        </a>
                                                    </td>
                                                    <td class="small text-secondary">${entry.earnedAchievements} 🏆</td>
                                                    <td class="text-end fw-bold text-success">${entry.totalPoints} pts</td>
                                                </tr>
                                            </c:forEach>
                                        </tbody>
                                    </table>
                                </div>
                            </div>
                        </div>
                    </div>
                </div>

            </div>

            <!-- Right Column: Connections & Recent Feed -->
            <div class="col-lg-4">

                <!-- Connections Box (if in SOCIAL mode) -->
                <c:if test="${hub.userSocial}">
                    <div class="card border rounded-4 shadow-xs bg-white mb-4">
                        <div class="card-header bg-transparent py-3 border-bottom d-flex justify-content-between align-items-center">
                            <h6 class="fw-bold text-dark mb-0"><i class="bi bi-people-fill text-primary me-2"></i>My Fitness Network</h6>
                            <span class="badge bg-primary-subtle text-primary">${hub.userConnectionCount}</span>
                        </div>
                        <div class="p-3">
                            <c:if test="${not empty hub.pendingRequests}">
                                <div class="mb-3 p-2 bg-warning-subtle rounded-3">
                                    <small class="fw-bold text-dark d-block mb-1">Pending Requests:</small>
                                    <c:forEach var="req" items="${hub.pendingRequests}">
                                        <div class="d-flex justify-content-between align-items-center mb-1 small">
                                            <span><c:out value="${req.otherUserDisplayName}"/></span>
                                            <div>
                                                <form action="${pageContext.request.contextPath}/social" method="POST" class="d-inline">
                                                    <input type="hidden" name="csrfToken" value="${sessionScope.csrfToken}"/>
                                                    <input type="hidden" name="action" value="acceptConnection"/>
                                                    <input type="hidden" name="connectionId" value="${req.connectionId}"/>
                                                    <button type="submit" class="btn btn-success btn-xs px-2 py-0">Accept</button>
                                                </form>
                                                <form action="${pageContext.request.contextPath}/social" method="POST" class="d-inline ms-1">
                                                    <input type="hidden" name="csrfToken" value="${sessionScope.csrfToken}"/>
                                                    <input type="hidden" name="action" value="rejectConnection"/>
                                                    <input type="hidden" name="connectionId" value="${req.connectionId}"/>
                                                    <button type="submit" class="btn btn-outline-secondary btn-xs px-2 py-0">Decline</button>
                                                </form>
                                            </div>
                                        </div>
                                    </c:forEach>
                                </div>
                            </c:if>

                            <c:choose>
                                <c:when test="${not empty hub.connections}">
                                    <ul class="list-unstyled mb-0 small">
                                        <c:forEach var="conn" items="${hub.connections}">
                                            <li class="d-flex justify-content-between align-items-center py-1.5 border-bottom">
                                                <a href="${pageContext.request.contextPath}/social/profile?id=${conn.otherUserId}" class="text-dark fw-medium text-decoration-none">
                                                    <i class="bi bi-person-circle text-secondary me-1"></i> <c:out value="${conn.otherUserDisplayName}"/>
                                                </a>
                                                <a href="${pageContext.request.contextPath}/social/profile?id=${conn.otherUserId}" class="btn btn-outline-light text-secondary btn-xs">View</a>
                                            </li>
                                        </c:forEach>
                                    </ul>
                                </c:when>
                                <c:otherwise>
                                    <div class="text-muted small text-center py-2">Click on athletes on the leaderboard to connect.</div>
                                </c:otherwise>
                            </c:choose>
                        </div>
                    </div>
                </c:if>

                <!-- Community Milestone Feed -->
                <div class="card border rounded-4 shadow-xs bg-white">
                    <div class="card-header bg-transparent py-3 border-bottom">
                        <h6 class="fw-bold text-dark mb-0"><i class="bi bi-lightning-charge-fill text-warning me-2"></i>Community Highlights</h6>
                    </div>
                    <div class="p-3">
                        <c:choose>
                            <c:when test="${not empty hub.recentActivityFeed}">
                                <ul class="list-unstyled mb-0 small">
                                    <c:forEach var="act" items="${hub.recentActivityFeed}">
                                        <li class="mb-3 pb-2 border-bottom">
                                            <div class="fw-semibold text-dark mb-0.5">
                                                <i class="bi bi-award text-success me-1"></i><c:out value="${act.title}"/>
                                            </div>
                                            <div class="text-secondary small"><c:out value="${act.description}"/></div>
                                        </li>
                                    </c:forEach>
                                </ul>
                            </c:when>
                            <c:otherwise>
                                <div class="text-muted small text-center py-3">Community milestone highlights appear here as users achieve new streaks.</div>
                            </c:otherwise>
                        </c:choose>
                    </div>
                </div>

            </div>

        </div>

    </main>

    <!-- Bootstrap 5 JS -->
    <script src="https://cdn.jsdelivr.net/npm/bootstrap@5.3.3/dist/js/bootstrap.bundle.min.js"></script>
</body>
</html>
