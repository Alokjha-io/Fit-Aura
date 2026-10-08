<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ taglib prefix="c" uri="jakarta.tags.core" %>
<%@ taglib prefix="fmt" uri="jakarta.tags.fmt" %>
<!DOCTYPE html>
<html lang="en">
<head>
    <meta charset="UTF-8">
    <meta name="viewport" content="width=device-width, initial-scale=1.0">
    <title>System Statistics – FitAura Admin</title>

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

            <div class="collapse navbar-collapse" id="adminNavbar">
                <ul class="navbar-nav me-auto mb-2 mb-lg-0 ms-lg-3">
                    <li class="nav-item">
                        <a class="nav-link text-white-50 hover-text-white" href="${pageContext.request.contextPath}/admin/dashboard">
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
                        </a>
                    </li>
                    <li class="nav-item">
                        <a class="nav-link active fw-bold text-success" aria-current="page" href="${pageContext.request.contextPath}/admin/statistics">
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
                    <a href="${pageContext.request.contextPath}/admin/dashboard" class="btn btn-outline-light btn-sm">
                        <i class="bi bi-arrow-left me-1"></i> Admin Dashboard
                    </a>
                </div>
            </div>
        </div>
    </nav>

    <!-- Main Content Container -->
    <main class="container-fluid px-lg-5 my-4 flex-grow-1">

        <!-- Header -->
        <div class="d-flex flex-column flex-md-row justify-content-between align-items-md-center mb-4 gap-2">
            <div>
                <h1 class="h3 fw-bold text-dark mb-1">
                    <i class="bi bi-graph-up-arrow text-success me-2"></i>System-Wide Metrics & Analytics
                </h1>
                <p class="text-muted mb-0">Platform activity distributions, workout types, goal completions, and gamification volume.</p>
            </div>
            <span class="badge bg-light text-dark border px-3 py-2">
                <i class="bi bi-database-check text-success me-1"></i> Live SQL Aggregation
            </span>
        </div>

        <!-- 4 Global Overview Cards -->
        <div class="row g-3 mb-4">
            <div class="col-sm-6 col-xl-3">
                <div class="card border-0 shadow-sm p-3 bg-white h-100">
                    <span class="text-muted small fw-semibold text-uppercase">Total Athletes</span>
                    <h2 class="display-6 fw-bold text-primary mt-1 mb-0">${stats.totalUsers}</h2>
                    <div class="mt-2 small text-muted">
                        <span>${stats.socialUsers} Social / ${stats.personalUsers} Personal</span>
                    </div>
                </div>
            </div>

            <div class="col-sm-6 col-xl-3">
                <div class="card border-0 shadow-sm p-3 bg-white h-100">
                    <span class="text-muted small fw-semibold text-uppercase">Total Workouts</span>
                    <h2 class="display-6 fw-bold text-success mt-1 mb-0">${stats.totalWorkouts}</h2>
                    <div class="mt-2 small text-muted">
                        <span>${stats.workoutsThisMonth} recorded past 30 days</span>
                    </div>
                </div>
            </div>

            <div class="col-sm-6 col-xl-3">
                <div class="card border-0 shadow-sm p-3 bg-white h-100">
                    <span class="text-muted small fw-semibold text-uppercase">Completed Goals</span>
                    <h2 class="display-6 fw-bold text-dark mt-1 mb-0">${stats.completedGoals}</h2>
                    <div class="mt-2 small text-muted">
                        <span>${stats.activeGoals} Active / ${stats.totalGoals} Total</span>
                    </div>
                </div>
            </div>

            <div class="col-sm-6 col-xl-3">
                <div class="card border-0 shadow-sm p-3 bg-white h-100">
                    <span class="text-muted small fw-semibold text-uppercase">Total Gamification PTS</span>
                    <h2 class="display-6 fw-bold text-warning-emphasis mt-1 mb-0">${stats.totalPointsAwarded}</h2>
                    <div class="mt-2 small text-muted">
                        <span>${stats.totalAchievementsEarned} badges earned</span>
                    </div>
                </div>
            </div>
        </div>

        <div class="row g-4 mb-4">
            <!-- Left Column: Workout Distribution & Intensities -->
            <div class="col-lg-6">
                <!-- Workout Types -->
                <div class="card border-0 shadow-sm mb-4">
                    <div class="card-header bg-white py-3 border-0">
                        <h5 class="fw-bold text-dark mb-0">
                            <i class="bi bi-activity text-success me-2"></i>Workout Type Distribution
                        </h5>
                    </div>
                    <div class="card-body p-4 pt-0">
                        <c:choose>
                            <c:when test="${empty stats.workoutTypeDistribution}">
                                <p class="text-muted small mb-0">No workout records available.</p>
                            </c:when>
                            <c:otherwise>
                                <div class="list-group list-group-flush">
                                    <c:forEach var="entry" items="${stats.workoutTypeDistribution}">
                                        <div class="list-group-item px-0 py-2 d-flex justify-content-between align-items-center">
                                            <span class="fw-semibold text-dark">${entry.key}</span>
                                            <span class="badge bg-success-subtle text-success border border-success-subtle px-3 py-1 font-monospace">
                                                ${entry.value} sessions
                                            </span>
                                        </div>
                                    </c:forEach>
                                </div>
                            </c:otherwise>
                        </c:choose>
                    </div>
                </div>

                <!-- Workout Intensities -->
                <div class="card border-0 shadow-sm">
                    <div class="card-header bg-white py-3 border-0">
                        <h5 class="fw-bold text-dark mb-0">
                            <i class="bi bi-speedometer text-primary me-2"></i>Workout Intensity Breakdown
                        </h5>
                    </div>
                    <div class="card-body p-4 pt-0">
                        <c:choose>
                            <c:when test="${empty stats.workoutIntensityDistribution}">
                                <p class="text-muted small mb-0">No workout intensity data available.</p>
                            </c:when>
                            <c:otherwise>
                                <div class="list-group list-group-flush">
                                    <c:forEach var="entry" items="${stats.workoutIntensityDistribution}">
                                        <div class="list-group-item px-0 py-2 d-flex justify-content-between align-items-center">
                                            <span class="fw-semibold text-dark">${entry.key} Intensity</span>
                                            <span class="badge bg-primary-subtle text-primary border border-primary-subtle px-3 py-1 font-monospace">
                                                ${entry.value} workouts
                                            </span>
                                        </div>
                                    </c:forEach>
                                </div>
                            </c:otherwise>
                        </c:choose>
                    </div>
                </div>
            </div>

            <!-- Right Column: Goals & Challenges -->
            <div class="col-lg-6">
                <!-- Goals Breakdown -->
                <div class="card border-0 shadow-sm mb-4">
                    <div class="card-header bg-white py-3 border-0">
                        <h5 class="fw-bold text-dark mb-0">
                            <i class="bi bi-bullseye text-danger me-2"></i>Goal Types Breakdown
                        </h5>
                    </div>
                    <div class="card-body p-4 pt-0">
                        <c:choose>
                            <c:when test="${empty stats.goalTypeDistribution}">
                                <p class="text-muted small mb-0">No goals recorded yet.</p>
                            </c:when>
                            <c:otherwise>
                                <div class="list-group list-group-flush">
                                    <c:forEach var="entry" items="${stats.goalTypeDistribution}">
                                        <div class="list-group-item px-0 py-2 d-flex justify-content-between align-items-center">
                                            <span class="fw-semibold text-dark">${entry.key}</span>
                                            <span class="badge bg-danger-subtle text-danger border border-danger-subtle px-3 py-1 font-monospace">
                                                ${entry.value} goals
                                            </span>
                                        </div>
                                    </c:forEach>
                                </div>
                            </c:otherwise>
                        </c:choose>
                    </div>
                </div>

                <!-- Challenges Breakdown -->
                <div class="card border-0 shadow-sm">
                    <div class="card-header bg-white py-3 border-0">
                        <h5 class="fw-bold text-dark mb-0">
                            <i class="bi bi-trophy text-warning me-2"></i>Community Challenge Operations
                        </h5>
                    </div>
                    <div class="card-body p-4 pt-0">
                        <div class="row g-2">
                            <div class="col-6">
                                <div class="p-3 bg-light rounded-3 text-center">
                                    <span class="text-muted small d-block">Pending Moderation</span>
                                    <h4 class="fw-bold text-warning-emphasis mb-0 mt-1">${stats.draftChallenges}</h4>
                                </div>
                            </div>
                            <div class="col-6">
                                <div class="p-3 bg-light rounded-3 text-center">
                                    <span class="text-muted small d-block">Active Challenges</span>
                                    <h4 class="fw-bold text-success mb-0 mt-1">${stats.activeChallenges}</h4>
                                </div>
                            </div>
                            <div class="col-6">
                                <div class="p-3 bg-light rounded-3 text-center">
                                    <span class="text-muted small d-block">Total Enrollments</span>
                                    <h4 class="fw-bold text-primary mb-0 mt-1">${stats.totalEnrollments}</h4>
                                </div>
                            </div>
                            <div class="col-6">
                                <div class="p-3 bg-light rounded-3 text-center">
                                    <span class="text-muted small d-block">Goal Completions</span>
                                    <h4 class="fw-bold text-dark mb-0 mt-1">${stats.completedParticipants}</h4>
                                </div>
                            </div>
                        </div>
                    </div>
                </div>
            </div>
        </div>

    </main>

    <!-- Footer -->
    <footer class="bg-white border-top py-3 mt-auto">
        <div class="container-fluid px-lg-5 d-flex justify-content-between align-items-center small text-muted">
            <div>&copy; 2026 FitAura Administration Console</div>
            <span class="badge bg-success-subtle text-success">Secure ADMIN Session</span>
        </div>
    </footer>

    <!-- Bootstrap 5 JS Bundle via CDN -->
    <script src="https://cdn.jsdelivr.net/npm/bootstrap@5.3.3/dist/js/bootstrap.bundle.min.js" integrity="sha384-YvpcrYf0tY3lHB60NNkmXc5s9fDVZLESaAA55NDzOxhy9GkcIdslK1eN7N6jIeHz" crossorigin="anonymous"></script>
</body>
</html>
