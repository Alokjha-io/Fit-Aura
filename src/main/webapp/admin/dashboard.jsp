<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ taglib prefix="c" uri="jakarta.tags.core" %>
<%@ taglib prefix="fmt" uri="jakarta.tags.fmt" %>
<!DOCTYPE html>
<html lang="en">
<head>
    <meta charset="UTF-8">
    <meta name="viewport" content="width=device-width, initial-scale=1.0">
    <title>Admin Dashboard – FitAura</title>
    <meta name="description" content="FitAura Administrative Control & Operations Dashboard.">

    <!-- Bootstrap 5 CSS via CDN -->
    <link href="https://cdn.jsdelivr.net/npm/bootstrap@5.3.3/dist/css/bootstrap.min.css" rel="stylesheet" integrity="sha384-QWTKZyjpPEjISv5WaRU9OFeRpok6YctnYmDr5pNlyT2bRjXh0JMhjY6hW+ALEwIH" crossorigin="anonymous">
    <!-- Bootstrap Icons -->
    <link rel="stylesheet" href="https://cdn.jsdelivr.net/npm/bootstrap-icons@1.11.3/font/bootstrap-icons.min.css">
    <!-- FitAura Custom CSS -->
    <link rel="stylesheet" href="${pageContext.request.contextPath}/css/fitaura.css">
</head>
<body class="bg-light d-flex flex-column min-vh-100">

    <!-- Admin Navigation Header -->
    <nav class="navbar navbar-expand-lg navbar-dark bg-dark sticky-top py-2 border-bottom border-dark-subtle">
        <div class="container-fluid px-lg-5">
            <a class="navbar-brand d-flex align-items-center text-white" href="${pageContext.request.contextPath}/admin/dashboard">
                <img src="${pageContext.request.contextPath}/images/fitaura-logo.svg" alt="FitAura Logo" width="32" height="32" class="me-2">
                <span>Fit<span class="text-success">Aura</span></span>
                <span class="badge bg-danger ms-2 font-monospace">ADMIN</span>
            </a>

            <button class="navbar-toggler" type="button" data-bs-toggle="collapse" data-bs-target="#adminNavbar" aria-controls="adminNavbar" aria-expanded="false" aria-label="Toggle navigation">
                <span class="navbar-toggler-icon"></span>
            </button>

            <div class="collapse navbar-collapse" id="adminNavbar">
                <ul class="navbar-nav me-auto mb-2 mb-lg-0 ms-lg-3">
                    <li class="nav-item">
                        <a class="nav-link active fw-bold text-success" aria-current="page" href="${pageContext.request.contextPath}/admin/dashboard">
                            <i class="bi bi-speedometer2 me-1"></i> Dashboard
                        </a>
                    </li>
                    <li class="nav-item">
                        <a class="nav-link text-white-50 hover-text-white" href="${pageContext.request.contextPath}/admin/users">
                            <i class="bi bi-people me-1"></i> Users
                        </a>
                    </li>
                    <li class="nav-item">
                        <a class="nav-link text-white-50 hover-text-white" href="${pageContext.request.contextPath}/admin/challenges">
                            <i class="bi bi-trophy me-1"></i> Challenges
                            <c:if test="${summary.pendingChallenges > 0}">
                                <span class="badge bg-warning text-dark ms-1">${summary.pendingChallenges}</span>
                            </c:if>
                        </a>
                    </li>
                    <li class="nav-item">
                        <a class="nav-link text-white-50 hover-text-white" href="${pageContext.request.contextPath}/admin/quotes">
                            <i class="bi bi-quote me-1"></i> Quotes
                        </a>
                    </li>
                    <li class="nav-item">
                        <a class="nav-link text-white-50 hover-text-white" href="${pageContext.request.contextPath}/admin/social">
                            <i class="bi bi-people-fill me-1"></i> Social
                        </a>
                    </li>
                    <li class="nav-item">
                        <a class="nav-link text-white-50 hover-text-white" href="${pageContext.request.contextPath}/admin/statistics">
                            <i class="bi bi-graph-up me-1"></i> Statistics
                        </a>
                    </li>
                    <li class="nav-item">
                        <a class="nav-link text-white-50 hover-text-white" href="${pageContext.request.contextPath}/admin/activity">
                            <i class="bi bi-clock-history me-1"></i> Activity Logs
                        </a>
                    </li>
                    <li class="nav-item">
                        <a class="nav-link text-white-50 hover-text-white" href="${pageContext.request.contextPath}/admin/settings">
                            <i class="bi bi-sliders me-1"></i> Settings
                        </a>
                    </li>
                </ul>

                <div class="d-flex align-items-center gap-2">
                    <a href="${pageContext.request.contextPath}/user/dashboard" class="btn btn-outline-light btn-sm">
                        <i class="bi bi-arrow-left me-1"></i> Back to User App
                    </a>
                    <a href="${pageContext.request.contextPath}/logout" class="btn btn-danger btn-sm px-3">
                        <i class="bi bi-box-arrow-right me-1"></i> Sign Out
                    </a>
                </div>
            </div>
        </div>
    </nav>

    <!-- Main Content Container -->
    <main class="container-fluid px-lg-5 my-4 flex-grow-1">

        <!-- Alerts -->
        <c:if test="${not empty errorMessage}">
            <div class="alert alert-danger alert-dismissible fade show d-flex align-items-center shadow-sm" role="alert">
                <i class="bi bi-exclamation-triangle-fill flex-shrink-0 me-2 fs-5"></i>
                <div>${errorMessage}</div>
                <button type="button" class="btn-close" data-bs-dismiss="alert" aria-label="Close"></button>
            </div>
        </c:if>
        <c:if test="${not empty param.success}">
            <div class="alert alert-success alert-dismissible fade show d-flex align-items-center shadow-sm" role="alert">
                <i class="bi bi-check-circle-fill flex-shrink-0 me-2 fs-5"></i>
                <div><c:out value="${param.success}"/></div>
                <button type="button" class="btn-close" data-bs-dismiss="alert" aria-label="Close"></button>
            </div>
        </c:if>

        <!-- Header -->
        <div class="d-flex flex-column flex-md-row justify-content-between align-items-md-center mb-4 gap-2">
            <div>
                <h1 class="h3 fw-bold text-dark mb-1">
                    <i class="bi bi-shield-shaded text-danger me-2"></i>Administration Console
                </h1>
                <p class="text-muted mb-0">Platform overview, user moderation, challenge workflows, and audit intelligence.</p>
            </div>
            <div class="d-flex gap-2">
                <a href="${pageContext.request.contextPath}/admin/users" class="btn btn-outline-primary">
                    <i class="bi bi-person-plus me-1"></i> User Directory
                </a>
                <a href="${pageContext.request.contextPath}/admin/challenges" class="btn btn-warning text-dark fw-semibold">
                    <i class="bi bi-check2-circle me-1"></i> Moderate Challenges
                </a>
            </div>
        </div>

        <!-- 4 Metric Overview Cards -->
        <div class="row g-3 mb-4">
            <!-- Users -->
            <div class="col-sm-6 col-xl-3">
                <div class="card border-0 shadow-sm h-100 p-3" style="border-left: 4px solid #0d6efd !important;">
                    <div class="d-flex justify-content-between align-items-center">
                        <div>
                            <span class="text-muted small fw-semibold text-uppercase">Platform Users</span>
                            <h2 class="display-6 fw-bold text-dark mt-1 mb-0">${summary.totalUsers}</h2>
                        </div>
                        <div class="bg-primary-subtle text-primary rounded-circle p-3 d-flex align-items-center justify-content-center" style="width: 50px; height: 50px;">
                            <i class="bi bi-people-fill fs-4"></i>
                        </div>
                    </div>
                    <div class="mt-2 text-muted small">
                        <span class="badge bg-success-subtle text-success">${summary.activeUsers} Active</span>
                        <span class="badge bg-danger-subtle text-danger ms-1">${summary.blockedUsers} Blocked</span>
                    </div>
                </div>
            </div>

            <!-- Workouts -->
            <div class="col-sm-6 col-xl-3">
                <div class="card border-0 shadow-sm h-100 p-3" style="border-left: 4px solid #198754 !important;">
                    <div class="d-flex justify-content-between align-items-center">
                        <div>
                            <span class="text-muted small fw-semibold text-uppercase">Total Workouts</span>
                            <h2 class="display-6 fw-bold text-dark mt-1 mb-0">${summary.totalWorkouts}</h2>
                        </div>
                        <div class="bg-success-subtle text-success rounded-circle p-3 d-flex align-items-center justify-content-center" style="width: 50px; height: 50px;">
                            <i class="bi bi-activity fs-4"></i>
                        </div>
                    </div>
                    <div class="mt-2 text-muted small">
                        <span class="text-success fw-semibold">+${summary.workoutsThisWeek}</span> logged this week
                    </div>
                </div>
            </div>

            <!-- Challenges & Moderation -->
            <div class="col-sm-6 col-xl-3">
                <div class="card border-0 shadow-sm h-100 p-3" style="border-left: 4px solid #ffc107 !important;">
                    <div class="d-flex justify-content-between align-items-center">
                        <div>
                            <span class="text-muted small fw-semibold text-uppercase">Challenges</span>
                            <h2 class="display-6 fw-bold text-dark mt-1 mb-0">${summary.totalChallenges}</h2>
                        </div>
                        <div class="bg-warning-subtle text-warning-emphasis rounded-circle p-3 d-flex align-items-center justify-content-center" style="width: 50px; height: 50px;">
                            <i class="bi bi-trophy-fill fs-4"></i>
                        </div>
                    </div>
                    <div class="mt-2 text-muted small">
                        <c:choose>
                            <c:when test="${summary.pendingChallenges > 0}">
                                <span class="badge bg-warning text-dark"><i class="bi bi-exclamation-circle me-1"></i>${summary.pendingChallenges} Pending Review</span>
                            </c:when>
                            <c:otherwise>
                                <span class="badge bg-light text-muted border">All reviewed</span>
                            </c:otherwise>
                        </c:choose>
                        <span class="ms-1 text-muted">${summary.activeChallenges} active</span>
                    </div>
                </div>
            </div>

            <!-- Gamification Points -->
            <div class="col-sm-6 col-xl-3">
                <div class="card border-0 shadow-sm h-100 p-3" style="border-left: 4px solid #6f42c1 !important;">
                    <div class="d-flex justify-content-between align-items-center">
                        <div>
                            <span class="text-muted small fw-semibold text-uppercase">Points Awarded</span>
                            <h2 class="display-6 fw-bold text-dark mt-1 mb-0">${summary.totalPointsAwarded}</h2>
                        </div>
                        <div class="bg-purple-subtle text-purple rounded-circle p-3 d-flex align-items-center justify-content-center" style="width: 50px; height: 50px; background-color: #f3e8ff; color: #7e22ce;">
                            <i class="bi bi-stars fs-4"></i>
                        </div>
                    </div>
                    <div class="mt-2 text-muted small">
                        <span>${summary.totalAchievementsEarned} badges unlocked</span>
                    </div>
                </div>
            </div>
        </div>

        <!-- Management Shortcuts & Moderation Warning -->
        <c:if test="${summary.pendingChallenges > 0}">
            <div class="alert alert-warning border-warning d-flex justify-content-between align-items-center shadow-sm mb-4">
                <div class="d-flex align-items-center gap-2">
                    <i class="bi bi-hourglass-split fs-4 text-warning-emphasis"></i>
                    <div>
                        <strong>${summary.pendingChallenges} Community Challenge<c:if test="${summary.pendingChallenges > 1}">s</c:if> Awaiting Moderation</strong>
                        <div class="small text-muted">User-created challenges require administrator approval before appearing in public discovery.</div>
                    </div>
                </div>
                <a href="${pageContext.request.contextPath}/admin/challenges?status=DRAFT" class="btn btn-sm btn-dark">
                    Review Challenges
                </a>
            </div>
        </c:if>

        <div class="row g-4 mb-4">
            <!-- Left Column: Pending Challenges & Recent Users -->
            <div class="col-lg-7">
                <!-- Pending Challenges Table -->
                <div class="card border-0 shadow-sm mb-4">
                    <div class="card-header bg-white py-3 border-0 d-flex justify-content-between align-items-center">
                        <h5 class="fw-bold text-dark mb-0">
                            <i class="bi bi-trophy text-warning me-2"></i>Pending Challenge Approvals
                        </h5>
                        <a href="${pageContext.request.contextPath}/admin/challenges" class="btn btn-sm btn-outline-secondary">View All</a>
                    </div>
                    <div class="card-body p-0">
                        <c:choose>
                            <c:when test="${empty summary.pendingModerationChallenges}">
                                <div class="text-center py-4 text-muted">
                                    <i class="bi bi-check-circle fs-2 d-block mb-1 text-success"></i>
                                    <span class="small">No pending challenges require review.</span>
                                </div>
                            </c:when>
                            <c:otherwise>
                                <div class="table-responsive">
                                    <table class="table table-hover align-middle mb-0">
                                        <thead class="table-light small text-muted text-uppercase">
                                            <tr>
                                                <th class="ps-4">Challenge</th>
                                                <th>Goal</th>
                                                <th>Creator</th>
                                                <th class="pe-4 text-end">Actions</th>
                                            </tr>
                                        </thead>
                                        <tbody>
                                            <c:forEach var="c" items="${summary.pendingModerationChallenges}">
                                                <tr>
                                                    <td class="ps-4">
                                                        <div class="fw-semibold text-dark">${c.name}</div>
                                                        <span class="text-muted small">${c.formattedDuration}</span>
                                                    </td>
                                                    <td>
                                                        <span class="badge bg-light text-dark border">${c.goalValue} ${c.goalUnit}</span>
                                                    </td>
                                                    <td class="text-muted small">User #${c.createdBy}</td>
                                                    <td class="pe-4 text-end">
                                                        <div class="d-inline-flex gap-1">
                                                            <form action="${pageContext.request.contextPath}/admin/challenges/approve" method="POST" class="d-inline">
                                                                <input type="hidden" name="csrfToken" value="${sessionScope.csrfToken}">
                                                                <input type="hidden" name="challengeId" value="${c.challengeId}">
                                                                <button type="submit" class="btn btn-sm btn-success px-2 py-1" title="Approve">
                                                                    <i class="bi bi-check-lg"></i> Approve
                                                                </button>
                                                            </form>
                                                            <form action="${pageContext.request.contextPath}/admin/challenges/reject" method="POST" class="d-inline">
                                                                <input type="hidden" name="csrfToken" value="${sessionScope.csrfToken}">
                                                                <input type="hidden" name="challengeId" value="${c.challengeId}">
                                                                <button type="submit" class="btn btn-sm btn-outline-danger px-2 py-1" title="Reject" onclick="return confirm('Reject this challenge?')">
                                                                    <i class="bi bi-x-lg"></i>
                                                                </button>
                                                            </form>
                                                        </div>
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

                <!-- Recent Registrations -->
                <div class="card border-0 shadow-sm">
                    <div class="card-header bg-white py-3 border-0 d-flex justify-content-between align-items-center">
                        <h5 class="fw-bold text-dark mb-0">
                            <i class="bi bi-person-plus text-primary me-2"></i>Recent User Registrations
                        </h5>
                        <a href="${pageContext.request.contextPath}/admin/users" class="btn btn-sm btn-outline-secondary">Manage Users</a>
                    </div>
                    <div class="card-body p-0">
                        <c:choose>
                            <c:when test="${empty summary.recentUsers}">
                                <div class="text-center py-4 text-muted">
                                    <span class="small">No users registered recently.</span>
                                </div>
                            </c:when>
                            <c:otherwise>
                                <div class="table-responsive">
                                    <table class="table table-hover align-middle mb-0">
                                        <thead class="table-light small text-muted text-uppercase">
                                            <tr>
                                                <th class="ps-4">User</th>
                                                <th>Role</th>
                                                <th>Status</th>
                                                <th class="pe-4 text-end">Registered</th>
                                            </tr>
                                        </thead>
                                        <tbody>
                                            <c:forEach var="u" items="${summary.recentUsers}">
                                                <tr>
                                                    <td class="ps-4">
                                                        <a href="${pageContext.request.contextPath}/admin/users/view?id=${u.userId}" class="fw-bold text-dark text-decoration-none">
                                                            ${u.displayName}
                                                        </a>
                                                        <div class="text-muted small">${u.email}</div>
                                                    </td>
                                                    <td>
                                                        <span class="badge ${u.role == 'ADMIN' ? 'bg-danger' : 'bg-secondary'}">${u.role}</span>
                                                    </td>
                                                    <td>
                                                        <span class="badge ${u.accountStatus == 'ACTIVE' ? 'bg-success' : (u.accountStatus == 'BLOCKED' ? 'bg-danger' : 'bg-warning text-dark')}">
                                                            ${u.accountStatus}
                                                        </span>
                                                    </td>
                                                    <td class="pe-4 text-end text-muted small">
                                                        <fmt:formatDate value="${u.createdAt}" pattern="MMM dd, yyyy" />
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
            </div>

            <!-- Right Column: Recent Activity Logs & Management Shortcuts -->
            <div class="col-lg-5">
                <!-- Activity Feed -->
                <div class="card border-0 shadow-sm mb-4">
                    <div class="card-header bg-white py-3 border-0 d-flex justify-content-between align-items-center">
                        <h5 class="fw-bold text-dark mb-0">
                            <i class="bi bi-clock-history text-secondary me-2"></i>Live Audit Stream
                        </h5>
                        <a href="${pageContext.request.contextPath}/admin/activity" class="btn btn-sm btn-outline-secondary">Full Log</a>
                    </div>
                    <div class="card-body p-3">
                        <c:choose>
                            <c:when test="${empty summary.recentActivityLogs}">
                                <div class="text-center py-4 text-muted">
                                    <span class="small">No activity events recorded yet.</span>
                                </div>
                            </c:when>
                            <c:otherwise>
                                <div class="list-group list-group-flush">
                                    <c:forEach var="log" items="${summary.recentActivityLogs}">
                                        <div class="list-group-item px-0 py-2 border-light-subtle">
                                            <div class="d-flex justify-content-between align-items-start">
                                                <div>
                                                    <span class="badge bg-light text-dark border small font-monospace">${log.actionType}</span>
                                                    <c:if test="${not empty log.userId}">
                                                        <span class="text-muted small ms-1">User #${log.userId}</span>
                                                    </c:if>
                                                    <p class="mb-0 text-dark small mt-1">${log.description}</p>
                                                </div>
                                                <span class="text-muted small text-nowrap ms-2">
                                                    <fmt:formatDate value="${log.createdAt}" pattern="HH:mm" />
                                                </span>
                                            </div>
                                        </div>
                                    </c:forEach>
                                </div>
                            </c:otherwise>
                        </c:choose>
                    </div>
                </div>

                <!-- Admin Management Shortcuts -->
                <div class="card border-0 shadow-sm">
                    <div class="card-header bg-white py-3 border-0">
                        <h5 class="fw-bold text-dark mb-0">
                            <i class="bi bi-grid-fill text-primary me-2"></i>Administration Modules
                        </h5>
                    </div>
                    <div class="card-body p-3">
                        <div class="row g-2">
                            <div class="col-6">
                                <a href="${pageContext.request.contextPath}/admin/users" class="card h-100 p-3 text-decoration-none border text-center hover-shadow bg-light-subtle">
                                    <i class="bi bi-people-fill fs-3 text-primary mb-1"></i>
                                    <span class="fw-bold text-dark small">User Management</span>
                                </a>
                            </div>
                            <div class="col-6">
                                <a href="${pageContext.request.contextPath}/admin/challenges" class="card h-100 p-3 text-decoration-none border text-center hover-shadow bg-light-subtle">
                                    <i class="bi bi-trophy-fill fs-3 text-warning mb-1"></i>
                                    <span class="fw-bold text-dark small">Challenges & Moderation</span>
                                </a>
                            </div>
                            <div class="col-6">
                                <a href="${pageContext.request.contextPath}/admin/statistics" class="card h-100 p-3 text-decoration-none border text-center hover-shadow bg-light-subtle">
                                    <i class="bi bi-bar-chart-line-fill fs-3 text-success mb-1"></i>
                                    <span class="fw-bold text-dark small">System Statistics</span>
                                </a>
                            </div>
                            <div class="col-6">
                                <a href="${pageContext.request.contextPath}/admin/settings" class="card h-100 p-3 text-decoration-none border text-center hover-shadow bg-light-subtle">
                                    <i class="bi bi-sliders fs-3 text-secondary mb-1"></i>
                                    <span class="fw-bold text-dark small">System Settings</span>
                                </a>
                            </div>
                        </div>
                    </div>
                </div>
            </div>
        </div>

    </main>

    <!-- Footer -->
    <footer class="bg-white border-top py-3 mt-auto">
        <div class="container-fluid px-lg-5 d-flex flex-column flex-sm-row justify-content-between align-items-center small text-muted">
            <div>&copy; 2026 FitAura Administration Console</div>
            <div class="mt-2 mt-sm-0">
                <span class="badge bg-success-subtle text-success">Secure ADMIN Session</span>
            </div>
        </div>
    </footer>

    <!-- Bootstrap 5 JS Bundle via CDN -->
    <script src="https://cdn.jsdelivr.net/npm/bootstrap@5.3.3/dist/js/bootstrap.bundle.min.js" integrity="sha384-YvpcrYf0tY3lHB60NNkmXc5s9fDVZLESaAA55NDzOxhy9GkcIdslK1eN7N6jIeHz" crossorigin="anonymous"></script>
</body>
</html>
