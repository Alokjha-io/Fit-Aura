<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ taglib prefix="c" uri="jakarta.tags.core" %>
<!DOCTYPE html>
<html lang="en">
<head>
    <meta charset="UTF-8">
    <meta name="viewport" content="width=device-width, initial-scale=1.0">
    <title>Member Dashboard – FitAura</title>
    <meta name="description" content="Your authenticated FitAura member dashboard.">

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

            <button class="navbar-toggler" type="button" data-bs-toggle="collapse" data-bs-target="#userNavbar" aria-controls="userNavbar" aria-expanded="false" aria-label="Toggle navigation">
                <span class="navbar-toggler-icon"></span>
            </button>

            <div class="collapse navbar-collapse" id="userNavbar">
                <ul class="navbar-nav me-auto mb-2 mb-lg-0 ms-lg-4">
                    <li class="nav-item">
                        <a class="nav-link active fw-bold text-success" aria-current="page" href="${pageContext.request.contextPath}/user/dashboard">
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
                        <a class="nav-link fw-medium text-dark" href="${pageContext.request.contextPath}/social">
                            <i class="bi bi-people me-1"></i> Social Hub
                        </a>
                    </li>
                    <li class="nav-item">
                        <a class="nav-link fw-medium text-dark" href="${pageContext.request.contextPath}/leaderboard">
                            <i class="bi bi-bar-chart-line me-1"></i> Leaderboard
                        </a>
                    </li>
                    <li class="nav-item">
                        <a class="nav-link fw-medium text-dark" href="${pageContext.request.contextPath}/user/profile">
                            <i class="bi bi-person-gear me-1"></i> Profile & Privacy
                        </a>
                    </li>
                </ul>

                <div class="d-flex align-items-center gap-3">
                    <!-- User Identifier linking to profile -->
                    <a href="${pageContext.request.contextPath}/user/profile" class="text-decoration-none d-none d-sm-flex align-items-center gap-2 px-3 py-1.5 rounded-pill bg-light border hover-bg-slate-200">
                        <div class="rounded-circle bg-success-subtle text-success d-flex align-items-center justify-content-center" style="width: 28px; height: 28px;">
                            <i class="bi bi-person-fill small"></i>
                        </div>
                        <span class="small fw-semibold text-dark"><c:out value="${sessionScope.authenticatedDisplayName}"/></span>
                        <span class="badge bg-secondary-subtle text-secondary small"><c:out value="${currentUser.privacyMode}"/> Mode</span>
                    </a>

                    <!-- Logout Button -->
                    <a href="${pageContext.request.contextPath}/logout" class="btn btn-outline-danger btn-sm px-3 fw-medium">
                        <i class="bi bi-box-arrow-right me-1"></i> Log Out
                    </a>
                </div>
            </div>
        </div>
    </nav>

    <!-- Main Dashboard Area -->
    <main class="flex-grow-1 py-5">
        <div class="container">

            <!-- Welcome Alerts -->
            <c:if test="${param.new == 'true' || param.registered == 'true'}">
                <div class="alert alert-success alert-dismissible fade show d-flex align-items-center p-3 rounded-4 shadow-sm mb-4" role="alert">
                    <i class="bi bi-check-circle-fill fs-4 me-3 text-success"></i>
                    <div>
                        <h6 class="alert-heading fw-bold mb-1">Welcome to FitAura, <c:out value="${currentUser.fullName}"/>!</h6>
                        <p class="mb-0 small">Your account is active in <strong>Personal Mode</strong>. Your fitness data and workouts will remain completely confidential.</p>
                    </div>
                    <button type="button" class="btn-close" data-bs-dismiss="alert" aria-label="Close"></button>
                </div>
            </c:if>

            <!-- Greeting Banner -->
            <div class="bg-white border rounded-4 p-4 p-md-5 mb-4 shadow-xs">
                <div class="row align-items-center g-3">
                    <div class="col-md-8">
                        <div class="d-inline-flex align-items-center gap-2 px-3 py-1 rounded-pill bg-success-subtle text-success small fw-semibold mb-2">
                            <i class="bi bi-shield-lock-fill"></i> Secure Authenticated Session
                        </div>
                        <h2 class="fw-bold text-dark mb-1">Hello, <c:out value="${currentUser.displayName != null ? currentUser.displayName : currentUser.fullName}"/>!</h2>
                        <p class="text-secondary mb-0">Track workouts, set goals, and build consistent daily streaks.</p>
                    </div>
                    <div class="col-md-4 text-md-end">
                        <span class="badge bg-light text-dark border px-3 py-2 fs-6">
                            <i class="bi bi-person-badge me-1 text-success"></i> Account Status: <strong><c:out value="${currentUser.accountStatus}"/></strong>
                        </span>
                    </div>
                </div>
            </div>

            <!-- TODAY'S MOTIVATION (Daily Quote) -->
            <c:if test="${not empty todayQuote}">
                <div class="card border-0 rounded-4 p-4 mb-4 shadow-xs text-white" style="background: linear-gradient(135deg, #0f172a 0%, #1e293b 60%, #064e3b 100%);">
                    <div class="d-flex align-items-center justify-content-between mb-2">
                        <span class="badge bg-warning text-dark fw-bold text-uppercase px-2.5 py-1">
                            <i class="bi bi-quote me-1"></i> Today's Motivation
                        </span>
                        <small class="text-white-50"><i class="bi bi-calendar-event me-1"></i><c:out value="${todayQuote.quoteDate}"/></small>
                    </div>
                    <blockquote class="blockquote mb-0 mt-2">
                        <p class="fs-5 fw-medium mb-1 fst-italic leading-relaxed">“<c:out value="${todayQuote.quoteText}"/>”</p>
                        <footer class="blockquote-footer text-light opacity-75 small mt-1 mb-0">
                            — <cite title="Source Title" class="fw-semibold text-warning-emphasis"><c:out value="${todayQuote.authorName != null ? todayQuote.authorName : 'FitAura'}"/></cite>
                        </footer>
                    </blockquote>
                </div>
            </c:if>

            <!-- Social Mode vs Personal Mode Dashboard Integration -->
            <c:choose>
                <c:when test="${currentUser.privacyMode == 'SOCIAL'}">
                    <div class="card border-0 bg-success-subtle text-success-emphasis rounded-4 p-4 mb-4 shadow-xs">
                        <div class="d-flex flex-column flex-md-row justify-content-between align-items-md-center gap-3">
                            <div class="d-flex align-items-center gap-3">
                                <div class="rounded-circle bg-success text-white d-flex align-items-center justify-content-center" style="width: 48px; height: 48px;">
                                    <i class="bi bi-people-fill fs-4"></i>
                                </div>
                                <div>
                                    <h5 class="fw-bold mb-1 text-dark">Social Fitness Mode Active</h5>
                                    <p class="mb-0 small text-secondary">
                                        You are competing on public leaderboards! 
                                        <strong>${totalPoints} Total Points</strong> &bull; 
                                        <c:choose>
                                            <c:when test="${not empty socialRank}">Rank #${socialRank}</c:when>
                                            <c:otherwise>Rank Pending</c:otherwise>
                                        </c:choose> &bull; 
                                        <strong>${streak.currentStreakDays} Day Streak 🔥</strong>
                                    </p>
                                </div>
                            </div>
                            <div class="d-flex gap-2">
                                <a href="${pageContext.request.contextPath}/social" class="btn btn-success btn-sm px-3 fw-semibold">
                                    <i class="bi bi-people me-1"></i> Enter Social Hub
                                </a>
                                <a href="${pageContext.request.contextPath}/leaderboard" class="btn btn-outline-success btn-sm px-3 fw-semibold">
                                    <i class="bi bi-bar-chart-line me-1"></i> Leaderboard
                                </a>
                            </div>
                        </div>
                    </div>
                </c:when>
                <c:otherwise>
                    <div class="alert alert-light border rounded-4 p-3 mb-4 d-flex flex-column flex-sm-row align-items-sm-center justify-content-between gap-3 shadow-xs">
                        <div class="d-flex align-items-center gap-3">
                            <i class="bi bi-shield-check fs-3 text-success"></i>
                            <div>
                                <div class="fw-bold text-dark">Personal Mode is Active</div>
                                <div class="small text-muted">Your workouts, points, and biometrics remain 100% private. Switch to <strong>Social Mode</strong> in Profile to join weekly competitions and public rankings.</div>
                            </div>
                        </div>
                        <a href="${pageContext.request.contextPath}/user/profile" class="btn btn-outline-success btn-sm px-3 fw-medium text-nowrap">
                            Privacy Settings
                        </a>
                    </div>
                </c:otherwise>
            </c:choose>

            <!-- Feature Summary Tiles -->
            <div class="row g-4 mb-4">
                <div class="col-12 col-md-4">
                    <div class="card h-100 border rounded-4 p-4 bg-white shadow-xs">
                        <div class="d-flex align-items-center gap-3 mb-3">
                            <div class="icon-wrapper m-0 bg-success-subtle text-success">
                                <i class="bi bi-activity fs-4"></i>
                            </div>
                            <div>
                                <h6 class="fw-bold text-dark mb-0">Workout Tracking</h6>
                                <small class="text-muted">
                                    <c:choose>
                                        <c:when test="${workoutCount > 0}">
                                            <strong>${workoutCount}</strong> Session<c:if test="${workoutCount != 1}">s</c:if> Logged
                                        </c:when>
                                        <c:otherwise>Ready to log first session</c:otherwise>
                                    </c:choose>
                                </small>
                            </div>
                        </div>
                        <p class="text-secondary small mb-3">
                            Record running, walking, cycling, strength, and home workout sessions.
                        </p>
                        <div class="d-flex gap-2 mt-auto">
                            <a href="${pageContext.request.contextPath}/workouts" class="btn btn-outline-success btn-sm flex-grow-1 fw-medium">
                                <i class="bi bi-list-check me-1"></i> History
                            </a>
                            <a href="${pageContext.request.contextPath}/workouts/add" class="btn btn-success btn-sm px-3 fw-medium">
                                <i class="bi bi-plus-lg me-1"></i> Log
                            </a>
                        </div>
                    </div>
                </div>

                <div class="col-12 col-md-4">
                    <div class="card h-100 border rounded-4 p-4 bg-white shadow-xs">
                        <div class="d-flex align-items-center gap-3 mb-3">
                            <div class="icon-wrapper m-0 bg-primary-subtle text-primary">
                                <i class="bi bi-bullseye fs-4"></i>
                            </div>
                            <div>
                                <h6 class="fw-bold text-dark mb-0">Fitness Goals</h6>
                                <small class="text-muted">
                                    <c:choose>
                                        <c:when test="${activeGoalCount > 0}">
                                            <strong>${activeGoalCount}</strong> Active Goal<c:if test="${activeGoalCount != 1}">s</c:if>
                                        </c:when>
                                        <c:otherwise>No active goals</c:otherwise>
                                    </c:choose>
                                </small>
                            </div>
                        </div>
                        <p class="text-secondary small mb-3">
                            Set target weights, strength milestones, endurance, and consistency targets.
                        </p>
                        <div class="d-flex gap-2 mt-auto">
                            <a href="${pageContext.request.contextPath}/goals" class="btn btn-outline-primary btn-sm flex-grow-1 fw-medium">
                                <i class="bi bi-bullseye me-1"></i> View Goals
                            </a>
                            <a href="${pageContext.request.contextPath}/goals/create" class="btn btn-primary btn-sm px-3 fw-medium">
                                <i class="bi bi-plus-lg me-1"></i> Create
                            </a>
                        </div>
                    </div>
                </div>

                <div class="col-12 col-md-4">
                    <div class="card h-100 border rounded-4 p-4 bg-white shadow-xs">
                        <div class="d-flex align-items-center gap-3 mb-3">
                            <div class="icon-wrapper m-0 bg-info-subtle text-info">
                                <i class="bi bi-graph-up-arrow fs-4"></i>
                            </div>
                            <div>
                                <h6 class="fw-bold text-dark mb-0">Progress & Stats</h6>
                                <small class="text-muted">Weekly & Monthly Analytics</small>
                            </div>
                        </div>
                        <p class="text-secondary small mb-3">
                            View workout volume, BMI/BMR estimates, calories burned, and activity breakdown.
                        </p>
                        <div class="mt-auto">
                            <a href="${pageContext.request.contextPath}/progress" class="btn btn-outline-info text-dark btn-sm w-100 fw-medium">
                                <i class="bi bi-bar-chart-line me-1"></i> View Analytics
                            </a>
                        </div>
                    </div>
                </div>
            </div>

            <!-- Community Challenges & Personalized Guidance Row -->
            <div class="row g-4 mb-4">
                <div class="col-12 col-md-6">
                    <div class="card border rounded-4 p-4 bg-white shadow-xs h-100">
                        <div class="d-flex align-items-center gap-3 mb-3">
                            <div class="icon-wrapper m-0 bg-success-subtle text-success">
                                <i class="bi bi-compass fs-4"></i>
                            </div>
                            <div>
                                <h5 class="fw-bold text-dark mb-0">Personalized Guidance</h5>
                                <small class="text-muted">Dynamic Rule-Based Tips</small>
                            </div>
                        </div>
                        <p class="text-secondary small mb-4 flex-grow-1">
                            Actionable daily tips, workout frequency pacing, and goal strategies calibrated to your training.
                        </p>
                        <a href="${pageContext.request.contextPath}/guidance" class="btn btn-outline-success btn-sm px-4 fw-semibold align-self-start">
                            <i class="bi bi-compass me-1"></i> View Guidance
                        </a>
                    </div>
                </div>

                <div class="col-12 col-md-6">
                    <div class="card border rounded-4 p-4 bg-white shadow-xs h-100">
                        <div class="d-flex align-items-center gap-3 mb-3">
                            <div class="icon-wrapper m-0 bg-warning-subtle text-warning-emphasis">
                                <i class="bi bi-trophy-fill fs-4 text-warning"></i>
                            </div>
                            <div>
                                <h5 class="fw-bold text-dark mb-0">Community Challenges</h5>
                                <small class="text-muted">Milestones & Accountability</small>
                            </div>
                        </div>
                        <p class="text-secondary small mb-4 flex-grow-1">
                            Join community challenges, track real workout-backed progress, and earn milestone rewards.
                        </p>
                        <div class="d-flex gap-2">
                            <a href="${pageContext.request.contextPath}/challenges" class="btn btn-success btn-sm px-4 fw-semibold shadow-xs">
                                <i class="bi bi-trophy me-1"></i> Explore Challenges
                            </a>
                            <a href="${pageContext.request.contextPath}/challenges/my" class="btn btn-outline-secondary btn-sm px-3 fw-semibold">
                                My Portfolio
                            </a>
                        </div>
                    </div>
                </div>
            </div>

            <!-- Fitness Profile Metrics Section -->
            <div class="bg-white border rounded-4 p-4 shadow-xs">
                <div class="d-flex flex-column flex-sm-row justify-content-between align-items-sm-center pb-3 mb-3 border-bottom gap-2">
                    <div class="d-flex align-items-center gap-3">
                        <div class="icon-wrapper m-0 bg-success-subtle text-success">
                            <i class="bi bi-heart-pulse-fill fs-4"></i>
                        </div>
                        <div>
                            <h5 class="fw-bold text-dark mb-0">Personal Fitness Metrics</h5>
                            <small class="text-muted">Body metrics and training environment</small>
                        </div>
                    </div>
                    <a href="${pageContext.request.contextPath}/user/profile" class="btn btn-outline-success btn-sm px-3 fw-medium">
                        <i class="bi bi-gear me-1"></i> Manage Metrics
                    </a>
                </div>

                <c:choose>
                    <c:when test="${not empty fitnessProfile && (not empty fitnessProfile.heightCm || not empty fitnessProfile.weightKg || not empty fitnessProfile.age)}">
                        <div class="row g-3">
                            <div class="col-6 col-md-3">
                                <div class="p-3 bg-light rounded-3 text-center">
                                    <small class="text-secondary d-block mb-1">Height</small>
                                    <span class="fs-5 fw-bold text-dark"><c:out value="${fitnessProfile.heightCm != null ? fitnessProfile.heightCm : '—'}"/></span>
                                    <small class="text-muted ms-1">${fitnessProfile.heightCm != null ? 'cm' : ''}</small>
                                </div>
                            </div>
                            <div class="col-6 col-md-3">
                                <div class="p-3 bg-light rounded-3 text-center">
                                    <small class="text-secondary d-block mb-1">Weight</small>
                                    <span class="fs-5 fw-bold text-dark"><c:out value="${fitnessProfile.weightKg != null ? fitnessProfile.weightKg : '—'}"/></span>
                                    <small class="text-muted ms-1">${fitnessProfile.weightKg != null ? 'kg' : ''}</small>
                                </div>
                            </div>
                            <div class="col-6 col-md-3">
                                <div class="p-3 bg-light rounded-3 text-center">
                                    <small class="text-secondary d-block mb-1">Activity Level</small>
                                    <span class="badge bg-success-subtle text-success fw-semibold"><c:out value="${fitnessProfile.activityLevel}"/></span>
                                </div>
                            </div>
                            <div class="col-6 col-md-3">
                                <div class="p-3 bg-light rounded-3 text-center">
                                    <small class="text-secondary d-block mb-1">Preferred Setting</small>
                                    <span class="badge bg-secondary-subtle text-secondary fw-semibold"><c:out value="${fitnessProfile.preferredEnvironment}"/></span>
                                </div>
                            </div>
                        </div>
                    </c:when>
                    <c:otherwise>
                        <div class="p-3 bg-light rounded-3 d-flex flex-column flex-sm-row justify-content-between align-items-center gap-3">
                            <div>
                                <h6 class="fw-bold text-dark mb-1">Set up your fitness profile</h6>
                                <p class="text-secondary small mb-0">Add your height, weight, age, and activity level to complete your personal health and fitness profile.</p>
                            </div>
                            <a href="${pageContext.request.contextPath}/user/profile" class="btn btn-success btn-sm px-4 fw-semibold text-nowrap">
                                <i class="bi bi-plus-circle me-1"></i> Add Metrics
                            </a>
                        </div>
                    </c:otherwise>
                </c:choose>
            </div>

        </div>
    </main>

    <!-- Footer -->
    <footer class="bg-white border-top py-3 text-center text-muted small mt-auto">
        <div class="container d-flex flex-column flex-sm-row justify-content-between align-items-center">
            <div>&copy; 2026 FitAura. All rights reserved.</div>
            <div class="mt-2 mt-sm-0">Track. Improve. Thrive.</div>
        </div>
    </footer>

    <!-- Bootstrap 5 JavaScript Bundle -->
    <script src="https://cdn.jsdelivr.net/npm/bootstrap@5.3.3/dist/js/bootstrap.bundle.min.js" integrity="sha384-YvpcrYf0tY3lHB60NNkmXc5s9fDVZLESaAA55NDzOxhy9GkcIdslK1eN7N6jIeHz" crossorigin="anonymous"></script>
</body>
</html>
