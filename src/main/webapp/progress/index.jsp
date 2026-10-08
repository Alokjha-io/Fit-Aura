<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ taglib prefix="c" uri="jakarta.tags.core" %>
<!DOCTYPE html>
<html lang="en">
<head>
    <meta charset="UTF-8">
    <meta name="viewport" content="width=device-width, initial-scale=1.0">
    <title>Progress & Analytics – FitAura</title>
    <meta name="description" content="Detailed overview of your fitness progress, workout history, weekly volume, and health metrics.">

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

            <button class="navbar-toggler" type="button" data-bs-toggle="collapse" data-bs-target="#progressNavbar" aria-controls="progressNavbar" aria-expanded="false" aria-label="Toggle navigation">
                <span class="navbar-toggler-icon"></span>
            </button>

            <div class="collapse navbar-collapse" id="progressNavbar">
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
                        <a class="nav-link active fw-bold text-success" aria-current="page" href="${pageContext.request.contextPath}/progress">
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
                    <a href="${pageContext.request.contextPath}/user/profile" class="text-decoration-none d-none d-sm-flex align-items-center gap-2 px-3 py-1.5 rounded-pill bg-light border">
                        <div class="rounded-circle bg-success-subtle text-success d-flex align-items-center justify-content-center" style="width: 28px; height: 28px;">
                            <i class="bi bi-person-fill small"></i>
                        </div>
                        <span class="small fw-semibold text-dark"><c:out value="${sessionScope.authenticatedDisplayName}"/></span>
                    </a>

                    <a href="${pageContext.request.contextPath}/logout" class="btn btn-outline-danger btn-sm px-3 fw-medium">
                        <i class="bi bi-box-arrow-right me-1"></i> Log Out
                    </a>
                </div>
            </div>
        </div>
    </nav>

    <!-- Main Content -->
    <main class="flex-grow-1 py-5">
        <div class="container" style="max-width: 1100px;">

            <!-- Header Title -->
            <div class="d-flex flex-column flex-sm-row justify-content-between align-items-sm-center gap-3 mb-4">
                <div>
                    <h2 class="fw-bold text-dark mb-1">Fitness Progress & Analytics</h2>
                    <p class="text-secondary small mb-0">Real-time statistics derived from your recorded workouts and profile metrics.</p>
                </div>
                <div class="d-flex gap-2">
                    <a href="${pageContext.request.contextPath}/workouts/add" class="btn btn-success px-3 py-2 fw-semibold rounded-3 shadow-xs">
                        <i class="bi bi-plus-lg me-1"></i> Log Workout
                    </a>
                    <a href="${pageContext.request.contextPath}/goals/create" class="btn btn-outline-success px-3 py-2 fw-semibold rounded-3">
                        <i class="bi bi-bullseye me-1"></i> Set Goal
                    </a>
                </div>
            </div>

            <!-- Overall Lifetime Stats Row -->
            <div class="row g-3 mb-4">
                <div class="col-12 col-md-4">
                    <div class="card border rounded-4 p-4 bg-white shadow-xs h-100">
                        <div class="d-flex align-items-center gap-3 mb-2">
                            <div class="icon-wrapper m-0 bg-success-subtle text-success">
                                <i class="bi bi-activity fs-4"></i>
                            </div>
                            <div>
                                <small class="text-secondary fw-semibold">Total Workouts</small>
                                <h3 class="fw-bold text-dark mb-0"><c:out value="${summary.totalWorkouts}"/></h3>
                            </div>
                        </div>
                        <small class="text-muted">Lifetime completed training sessions</small>
                    </div>
                </div>

                <div class="col-12 col-md-4">
                    <div class="card border rounded-4 p-4 bg-white shadow-xs h-100">
                        <div class="d-flex align-items-center gap-3 mb-2">
                            <div class="icon-wrapper m-0 bg-primary-subtle text-primary">
                                <i class="bi bi-stopwatch-fill fs-4"></i>
                            </div>
                            <div>
                                <small class="text-secondary fw-semibold">Active Exercise Time</small>
                                <h3 class="fw-bold text-dark mb-0">
                                    <c:choose>
                                        <c:when test="${summary.totalDurationMinutes >= 60}">
                                            <c:out value="${summary.totalDurationMinutes / 60}"/> hrs <c:out value="${summary.totalDurationMinutes % 60}"/> min
                                        </c:when>
                                        <c:otherwise>
                                            <c:out value="${summary.totalDurationMinutes}"/> min
                                        </c:otherwise>
                                    </c:choose>
                                </h3>
                            </div>
                        </div>
                        <small class="text-muted">Total minutes spent exercising</small>
                    </div>
                </div>

                <div class="col-12 col-md-4">
                    <div class="card border rounded-4 p-4 bg-white shadow-xs h-100">
                        <div class="d-flex align-items-center gap-3 mb-2">
                            <div class="icon-wrapper m-0 bg-warning-subtle text-warning">
                                <i class="bi bi-fire fs-4"></i>
                            </div>
                            <div>
                                <small class="text-secondary fw-semibold">Energy Burned</small>
                                <h3 class="fw-bold text-dark mb-0"><c:out value="${summary.totalCaloriesBurned}"/> <span class="fs-6 text-muted fw-normal">kcal</span></h3>
                            </div>
                        </div>
                        <small class="text-muted">Total estimated workout calories</small>
                    </div>
                </div>
            </div>

            <!-- Weekly and Monthly Summaries Row -->
            <div class="row g-4 mb-4">
                <!-- Weekly Activity Card -->
                <div class="col-12 col-lg-7">
                    <div class="card border rounded-4 p-4 bg-white shadow-xs h-100">
                        <div class="d-flex justify-content-between align-items-center pb-3 mb-3 border-bottom">
                            <div>
                                <h5 class="fw-bold text-dark mb-0">Weekly Activity</h5>
                                <small class="text-muted">
                                    <c:out value="${summary.weekStartDate}"/> to <c:out value="${summary.weekEndDate}"/>
                                </small>
                            </div>
                            <span class="badge bg-success-subtle text-success fs-6 fw-semibold px-3 py-1">
                                <c:out value="${summary.weeklyWorkouts}"/> sessions
                            </span>
                        </div>

                        <!-- Weekly Highlights -->
                        <div class="row g-2 mb-4 text-center">
                            <div class="col-6">
                                <div class="p-2.5 bg-light rounded-3">
                                    <small class="text-secondary d-block">Duration This Week</small>
                                    <span class="fs-5 fw-bold text-dark"><c:out value="${summary.weeklyDurationMinutes}"/> min</span>
                                </div>
                            </div>
                            <div class="col-6">
                                <div class="p-2.5 bg-light rounded-3">
                                    <small class="text-secondary d-block">Calories This Week</small>
                                    <span class="fs-5 fw-bold text-success"><c:out value="${summary.weeklyCaloriesBurned}"/> kcal</span>
                                </div>
                            </div>
                        </div>

                        <!-- Daily Breakdown Bar Display (Mon-Sun) -->
                        <h6 class="fw-bold text-dark small mb-3">Daily Exercise Minutes (Mon – Sun)</h6>
                        <div class="row g-2 text-center align-items-end" style="min-height: 120px;">
                            <c:set var="dayLabels" value="${['Mon', 'Tue', 'Wed', 'Thu', 'Fri', 'Sat', 'Sun']}"/>
                            <c:forEach var="dayIdx" begin="1" end="7">
                                <c:set var="dayMins" value="${summary.weeklyDayMinutes[dayIdx] != null ? summary.weeklyDayMinutes[dayIdx] : 0}"/>
                                <div class="col">
                                    <div class="d-flex flex-column align-items-center h-100 justify-content-end">
                                        <small class="text-muted fw-semibold mb-1" style="font-size: 11px;">
                                            <c:out value="${dayMins}"/>m
                                        </small>
                                        <div class="w-100 rounded-top bg-success <c:if test="${dayMins == 0}">bg-light border</c:if>"
                                             style="height: ${dayMins > 0 ? (dayMins > 90 ? 80 : (dayMins < 15 ? 20 : dayMins * 0.8)) : 8}px; max-height: 80px;">
                                        </div>
                                        <small class="text-secondary fw-semibold mt-1" style="font-size: 12px;">
                                            <c:out value="${dayLabels[dayIdx - 1]}"/>
                                        </small>
                                    </div>
                                </div>
                            </c:forEach>
                        </div>
                    </div>
                </div>

                <!-- Monthly Activity Card -->
                <div class="col-12 col-lg-5">
                    <div class="card border rounded-4 p-4 bg-white shadow-xs h-100">
                        <div class="d-flex justify-content-between align-items-center pb-3 mb-3 border-bottom">
                            <div>
                                <h5 class="fw-bold text-dark mb-0">Monthly Summary</h5>
                                <small class="text-muted"><c:out value="${summary.monthName}"/> <c:out value="${summary.year}"/></small>
                            </div>
                            <span class="badge bg-primary-subtle text-primary fs-6 fw-semibold px-3 py-1">
                                <c:out value="${summary.monthlyWorkouts}"/> workouts
                            </span>
                        </div>

                        <div class="d-flex flex-column gap-3 my-auto">
                            <div class="p-3 bg-light rounded-3 d-flex justify-content-between align-items-center">
                                <div>
                                    <small class="text-secondary d-block">Monthly Total Duration</small>
                                    <span class="fs-4 fw-bold text-dark"><c:out value="${summary.monthlyDurationMinutes}"/> <span class="fs-6 text-muted fw-normal">minutes</span></span>
                                </div>
                                <div class="icon-wrapper m-0 bg-primary-subtle text-primary" style="width: 40px; height: 40px;">
                                    <i class="bi bi-clock-history fs-5"></i>
                                </div>
                            </div>

                            <div class="p-3 bg-light rounded-3 d-flex justify-content-between align-items-center">
                                <div>
                                    <small class="text-secondary d-block">Monthly Total Calories</small>
                                    <span class="fs-4 fw-bold text-success"><c:out value="${summary.monthlyCaloriesBurned}"/> <span class="fs-6 text-muted fw-normal">kcal</span></span>
                                </div>
                                <div class="icon-wrapper m-0 bg-warning-subtle text-warning" style="width: 40px; height: 40px;">
                                    <i class="bi bi-fire fs-5"></i>
                                </div>
                            </div>
                        </div>
                    </div>
                </div>
            </div>

            <!-- Health Calculations & Body Metrics Card -->
            <div class="card border rounded-4 p-4 bg-white shadow-xs mb-4">
                <div class="d-flex flex-column flex-sm-row justify-content-between align-items-sm-center pb-3 mb-3 border-bottom gap-2">
                    <div class="d-flex align-items-center gap-3">
                        <div class="icon-wrapper m-0 bg-info-subtle text-info">
                            <i class="bi bi-heart-pulse-fill fs-4"></i>
                        </div>
                        <div>
                            <h5 class="fw-bold text-dark mb-0">Health & Metabolic Calculations</h5>
                            <small class="text-muted">Calculated server-side using your profile height, weight, age, and activity level</small>
                        </div>
                    </div>
                    <a href="${pageContext.request.contextPath}/user/profile" class="btn btn-outline-primary btn-sm px-3 fw-medium">
                        <i class="bi bi-sliders me-1"></i> Update Metrics
                    </a>
                </div>

                <c:choose>
                    <c:when test="${summary.profileComplete || (not empty summary.bmi || not empty summary.bmr)}">
                        <div class="row g-3">
                            <!-- BMI Tile -->
                            <div class="col-12 col-md-4">
                                <div class="p-3 bg-light rounded-3 text-center h-100">
                                    <small class="text-secondary d-block mb-1 fw-semibold">Body Mass Index (BMI)</small>
                                    <span class="fs-3 fw-bold text-dark">
                                        <c:out value="${summary.bmi != null ? summary.bmi : '—'}"/>
                                    </span>
                                    <c:if test="${not empty summary.bmiCategory}">
                                        <div class="mt-2">
                                            <span class="badge 
                                                <c:choose>
                                                    <c:when test="${summary.bmiCategory == 'Normal weight'}">bg-success-subtle text-success</c:when>
                                                    <c:when test="${summary.bmiCategory == 'Overweight'}">bg-warning-subtle text-warning</c:when>
                                                    <c:when test="${summary.bmiCategory == 'Obesity'}">bg-danger-subtle text-danger</c:when>
                                                    <c:otherwise>bg-secondary-subtle text-secondary</c:otherwise>
                                                </c:choose> fw-semibold px-2.5 py-1">
                                                <c:out value="${summary.bmiCategory}"/>
                                            </span>
                                        </div>
                                    </c:if>
                                    <small class="text-muted d-block mt-2" style="font-size: 11px;">Standard WHO reference range</small>
                                </div>
                            </div>

                            <!-- BMR Tile -->
                            <div class="col-12 col-md-4">
                                <div class="p-3 bg-light rounded-3 text-center h-100">
                                    <small class="text-secondary d-block mb-1 fw-semibold">Basal Metabolic Rate (BMR)</small>
                                    <span class="fs-3 fw-bold text-dark">
                                        <c:out value="${summary.bmr != null ? summary.bmr : '—'}"/>
                                        <span class="fs-6 text-muted fw-normal">kcal/day</span>
                                    </span>
                                    <div class="mt-2">
                                        <span class="badge bg-info-subtle text-info fw-semibold px-2.5 py-1">Mifflin-St Jeor</span>
                                    </div>
                                    <small class="text-muted d-block mt-2" style="font-size: 11px;">Calories burned at complete rest</small>
                                </div>
                            </div>

                            <!-- Daily Calories Tile -->
                            <div class="col-12 col-md-4">
                                <div class="p-3 bg-light rounded-3 text-center h-100">
                                    <small class="text-secondary d-block mb-1 fw-semibold">Est. Daily Maintenance (TDEE)</small>
                                    <span class="fs-3 fw-bold text-success">
                                        <c:out value="${summary.dailyCalories != null ? summary.dailyCalories : '—'}"/>
                                        <span class="fs-6 text-muted fw-normal">kcal/day</span>
                                    </span>
                                    <div class="mt-2">
                                        <span class="badge bg-success-subtle text-success fw-semibold px-2.5 py-1">Active Baseline</span>
                                    </div>
                                    <small class="text-muted d-block mt-2" style="font-size: 11px;">Estimated daily expenditure with activity</small>
                                </div>
                            </div>
                        </div>
                    </c:when>
                    <c:otherwise>
                        <div class="p-3 bg-light rounded-3 d-flex flex-column flex-sm-row justify-content-between align-items-center gap-3">
                            <div>
                                <h6 class="fw-bold text-dark mb-1">Complete your fitness metrics</h6>
                                <p class="text-secondary small mb-0">Add your height, weight, age, and gender in your profile to view calculated BMI, BMR, and daily calorie maintenance estimates.</p>
                            </div>
                            <a href="${pageContext.request.contextPath}/user/profile" class="btn btn-success btn-sm px-4 fw-semibold text-nowrap">
                                <i class="bi bi-plus-circle me-1"></i> Add Body Metrics
                            </a>
                        </div>
                    </c:otherwise>
                </c:choose>
            </div>

            <!-- Active Goals and Activity Breakdown Grid -->
            <div class="row g-4 mb-4">
                <!-- Active Goals Column -->
                <div class="col-12 col-lg-6">
                    <div class="card border rounded-4 p-4 bg-white shadow-xs h-100">
                        <div class="d-flex justify-content-between align-items-center pb-3 mb-3 border-bottom">
                            <h5 class="fw-bold text-dark mb-0">Active Fitness Goals</h5>
                            <a href="${pageContext.request.contextPath}/goals" class="small fw-semibold text-success text-decoration-none">
                                View All Goals <i class="bi bi-chevron-right"></i>
                            </a>
                        </div>

                        <c:choose>
                            <c:when test="${empty summary.activeGoals}">
                                <div class="text-center py-4">
                                    <i class="bi bi-bullseye fs-1 text-muted"></i>
                                    <p class="text-secondary small mt-2 mb-3">You have no active goals set.</p>
                                    <a href="${pageContext.request.contextPath}/goals/create" class="btn btn-outline-success btn-sm px-3 fw-medium">
                                        <i class="bi bi-plus-lg me-1"></i> Create Your First Goal
                                    </a>
                                </div>
                            </c:when>
                            <c:otherwise>
                                <div class="d-flex flex-column gap-3">
                                    <c:forEach var="g" items="${summary.activeGoals}">
                                        <div class="p-3 bg-light rounded-3">
                                            <div class="d-flex justify-content-between align-items-center mb-1">
                                                <a href="${pageContext.request.contextPath}/goals/view?id=${g.goalId}" class="fw-bold text-dark text-decoration-none hover-text-success">
                                                    <c:out value="${g.goalTypeLabel}"/>
                                                </a>
                                                <span class="small fw-bold text-success"><c:out value="${g.progressPercentage}"/>%</span>
                                            </div>
                                            <div class="d-flex justify-content-between align-items-center small text-secondary mb-2">
                                                <span>Current: <c:out value="${g.currentValue}"/> <c:out value="${g.unit}"/></span>
                                                <span>Target: <c:out value="${g.targetValue}"/> <c:out value="${g.unit}"/></span>
                                            </div>
                                            <div class="progress" style="height: 8px; border-radius: 4px;">
                                                <div class="progress-bar bg-success" role="progressbar"
                                                     style="width: ${g.progressPercentage}%;"
                                                     aria-valuenow="${g.progressPercentage}" aria-valuemin="0" aria-valuemax="100">
                                                </div>
                                            </div>
                                        </div>
                                    </c:forEach>
                                </div>
                            </c:otherwise>
                        </c:choose>
                    </div>
                </div>

                <!-- Activity Distribution by Workout Type -->
                <div class="col-12 col-lg-6">
                    <div class="card border rounded-4 p-4 bg-white shadow-xs h-100">
                        <div class="d-flex justify-content-between align-items-center pb-3 mb-3 border-bottom">
                            <h5 class="fw-bold text-dark mb-0">Activity Breakdown</h5>
                            <a href="${pageContext.request.contextPath}/workouts" class="small fw-semibold text-success text-decoration-none">
                                All Workouts <i class="bi bi-chevron-right"></i>
                            </a>
                        </div>

                        <c:choose>
                            <c:when test="${empty summary.typeBreakdown}">
                                <div class="text-center py-4">
                                    <i class="bi bi-activity fs-1 text-muted"></i>
                                    <p class="text-secondary small mt-2 mb-3">No workouts recorded yet.</p>
                                    <a href="${pageContext.request.contextPath}/workouts/add" class="btn btn-outline-success btn-sm px-3 fw-medium">
                                        <i class="bi bi-plus-lg me-1"></i> Log Your First Workout
                                    </a>
                                </div>
                            </c:when>
                            <c:otherwise>
                                <div class="table-responsive">
                                    <table class="table table-borderless align-middle mb-0">
                                        <thead class="table-light rounded small text-secondary">
                                            <tr>
                                                <th>Activity</th>
                                                <th class="text-center">Sessions</th>
                                                <th class="text-end">Duration</th>
                                                <th class="text-end">Calories</th>
                                            </tr>
                                        </thead>
                                        <tbody>
                                            <c:forEach var="entry" items="${summary.typeBreakdown}">
                                                <tr class="border-bottom">
                                                    <td class="fw-semibold text-dark">
                                                        <c:choose>
                                                            <c:when test="${entry.key == 'RUNNING'}">Running</c:when>
                                                            <c:when test="${entry.key == 'WALKING'}">Walking</c:when>
                                                            <c:when test="${entry.key == 'CYCLING'}">Cycling</c:when>
                                                            <c:when test="${entry.key == 'STRENGTH'}">Strength Training</c:when>
                                                            <c:when test="${entry.key == 'HOME_WORKOUT'}">Home Workout</c:when>
                                                            <c:otherwise><c:out value="${entry.key}"/></c:otherwise>
                                                        </c:choose>
                                                    </td>
                                                    <td class="text-center"><c:out value="${entry.value.count}"/></td>
                                                    <td class="text-end"><c:out value="${entry.value.totalDuration}"/> min</td>
                                                    <td class="text-end text-success fw-semibold"><c:out value="${entry.value.totalCalories}"/> kcal</td>
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
