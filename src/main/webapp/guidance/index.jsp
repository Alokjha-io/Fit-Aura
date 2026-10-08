<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ taglib prefix="c" uri="jakarta.tags.core" %>
<!DOCTYPE html>
<html lang="en">
<head>
    <meta charset="UTF-8">
    <meta name="viewport" content="width=device-width, initial-scale=1.0">
    <title>Personalized Guidance – FitAura</title>
    <meta name="description" content="Tailored daily tips, workout frequency suggestions, goal strategies, and nutrition guidance based on your fitness journey.">

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

            <button class="navbar-toggler" type="button" data-bs-toggle="collapse" data-bs-target="#guidanceNavbar" aria-controls="guidanceNavbar" aria-expanded="false" aria-label="Toggle navigation">
                <span class="navbar-toggler-icon"></span>
            </button>

            <div class="collapse navbar-collapse" id="guidanceNavbar">
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
                        <a class="nav-link active fw-bold text-success" aria-current="page" href="${pageContext.request.contextPath}/guidance">
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

    <!-- Main Content Area -->
    <main class="flex-grow-1 py-5">
        <div class="container" style="max-width: 1050px;">

            <!-- Header Title -->
            <div class="d-flex flex-column flex-sm-row justify-content-between align-items-sm-center gap-3 mb-4">
                <div>
                    <div class="d-inline-flex align-items-center gap-2 px-3 py-1 rounded-pill bg-success-subtle text-success small fw-semibold mb-2">
                        <i class="bi bi-stars"></i> Tailored Insights for <c:out value="${guidance.userDisplayName}"/>
                    </div>
                    <h2 class="fw-bold text-dark mb-1">Personalized Guidance</h2>
                    <p class="text-secondary small mb-0">Practical recommendations dynamically calibrated to your goals, recent workout volume, and environment.</p>
                </div>
                <div class="d-flex gap-2">
                    <a href="${pageContext.request.contextPath}/workouts/add" class="btn btn-success px-3 py-2 fw-semibold rounded-3 shadow-xs">
                        <i class="bi bi-plus-lg me-1"></i> Log Workout
                    </a>
                    <a href="${pageContext.request.contextPath}/goals/create" class="btn btn-outline-success px-3 py-2 fw-semibold rounded-3">
                        <i class="bi bi-bullseye me-1"></i> New Goal
                    </a>
                </div>
            </div>

            <!-- Profile Incomplete Alert Banner -->
            <c:if test="${guidance.profileIncomplete}">
                <div class="card border rounded-4 p-4 bg-white mb-4 shadow-xs border-start border-4 border-warning">
                    <div class="d-flex flex-column flex-md-row justify-content-between align-items-md-center gap-3">
                        <div class="d-flex align-items-center gap-3">
                            <div class="icon-wrapper m-0 bg-warning-subtle text-warning">
                                <i class="bi bi-sliders fs-4"></i>
                            </div>
                            <div>
                                <h6 class="fw-bold text-dark mb-1">Complete Your Fitness Metrics</h6>
                                <p class="text-secondary small mb-0">Add your body height, weight, age, and activity level in your profile to receive calibrated energy and workout guidance.</p>
                            </div>
                        </div>
                        <a href="${pageContext.request.contextPath}/user/profile" class="btn btn-warning btn-sm px-4 fw-semibold text-nowrap">
                            <i class="bi bi-pencil-square me-1"></i> Update Profile Metrics
                        </a>
                    </div>
                </div>
            </c:if>

            <!-- 1. YOUR TOP RECOMMENDATION (Hero Card) -->
            <c:if test="${not empty guidance.primaryRecommendation}">
                <div class="card border-0 rounded-4 bg-white shadow-sm p-4 p-md-5 mb-4 position-relative overflow-hidden"
                     style="border-left: 6px solid #198754 !important;">
                    <div class="row align-items-center g-4">
                        <div class="col-12 col-md-8">
                            <div class="d-flex align-items-center gap-2 mb-2">
                                <span class="badge bg-success-subtle text-success fw-bold px-3 py-1.5 text-uppercase small">
                                    <i class="bi bi-award-fill me-1"></i> Top Recommendation
                                </span>
                                <span class="badge 
                                    <c:choose>
                                        <c:when test="${guidance.primaryRecommendation.priority == 'HIGH'}">bg-danger-subtle text-danger</c:when>
                                        <c:when test="${guidance.primaryRecommendation.priority == 'MEDIUM'}">bg-primary-subtle text-primary</c:when>
                                        <c:otherwise>bg-secondary-subtle text-secondary</c:otherwise>
                                    </c:choose> fw-semibold px-2.5 py-1 small">
                                    <c:out value="${guidance.primaryRecommendation.priority}"/> Priority
                                </span>
                            </div>

                            <h3 class="fw-bold text-dark mb-2"><c:out value="${guidance.primaryRecommendation.title}"/></h3>
                            <p class="text-secondary mb-3 fs-6"><c:out value="${guidance.primaryRecommendation.message}"/></p>

                            <c:if test="${not empty guidance.primaryRecommendation.reason}">
                                <div class="d-inline-flex align-items-center gap-2 p-2 px-3 rounded-pill bg-light border small text-muted">
                                    <i class="bi bi-info-circle-fill text-success"></i>
                                    <span><strong>Why this is recommended:</strong> <c:out value="${guidance.primaryRecommendation.reason}"/></span>
                                </div>
                            </c:if>
                        </div>

                        <div class="col-12 col-md-4 text-md-end">
                            <c:if test="${not empty guidance.primaryRecommendation.actionUrl}">
                                <a href="${pageContext.request.contextPath}${guidance.primaryRecommendation.actionUrl}" class="btn btn-success px-4 py-2.5 fw-semibold rounded-3 shadow-xs">
                                    <c:out value="${guidance.primaryRecommendation.actionLabel}"/> <i class="bi bi-arrow-right ms-1"></i>
                                </a>
                            </c:if>
                        </div>
                    </div>
                </div>
            </c:if>

            <!-- Supporting Recommendations Section -->
            <c:if test="${not empty guidance.supportingRecommendations}">
                <div class="mb-4">
                    <h5 class="fw-bold text-dark mb-3"><i class="bi bi-lightbulb me-1 text-warning"></i> Key Supporting Guidance</h5>
                    <div class="row g-3">
                        <c:forEach var="rec" items="${guidance.supportingRecommendations}">
                            <div class="col-12 col-md-6">
                                <div class="card border rounded-4 p-4 bg-white shadow-xs h-100">
                                    <div class="d-flex justify-content-between align-items-center mb-2">
                                        <span class="badge bg-light text-dark border small fw-semibold">
                                            <c:out value="${rec.categoryLabel}"/>
                                        </span>
                                        <c:if test="${not empty rec.actionUrl}">
                                            <a href="${pageContext.request.contextPath}${rec.actionUrl}" class="small text-success text-decoration-none fw-semibold">
                                                <c:out value="${rec.actionLabel}"/> <i class="bi bi-chevron-right"></i>
                                            </a>
                                        </c:if>
                                    </div>
                                    <h6 class="fw-bold text-dark mb-2"><c:out value="${rec.title}"/></h6>
                                    <p class="text-secondary small mb-3 flex-grow-1"><c:out value="${rec.message}"/></p>
                                    <c:if test="${not empty rec.reason}">
                                        <small class="text-muted mt-auto pt-2 border-top d-block">
                                            <i class="bi bi-dot"></i> <c:out value="${rec.reason}"/>
                                        </small>
                                    </c:if>
                                </div>
                            </div>
                        </c:forEach>
                    </div>
                </div>
            </c:if>

            <!-- Categorized Focus Cards -->
            <div class="row g-4 mb-4">
                <!-- Workout Idea Card -->
                <div class="col-12 col-lg-6">
                    <div class="card border rounded-4 p-4 bg-white shadow-xs h-100">
                        <div class="d-flex justify-content-between align-items-center pb-3 mb-3 border-bottom">
                            <div class="d-flex align-items-center gap-2">
                                <div class="icon-wrapper m-0 bg-primary-subtle text-primary" style="width: 36px; height: 36px;">
                                    <i class="bi bi-activity"></i>
                                </div>
                                <h5 class="fw-bold text-dark mb-0">Workout Idea</h5>
                            </div>
                            <span class="badge bg-primary-subtle text-primary small fw-semibold px-2.5 py-1">Cadence</span>
                        </div>
                        <h6 class="fw-bold text-dark mb-2"><c:out value="${guidance.workoutRecommendation.title}"/></h6>
                        <p class="text-secondary small mb-3 flex-grow-1"><c:out value="${guidance.workoutRecommendation.message}"/></p>

                        <div class="p-2.5 bg-light rounded-3 d-flex align-items-center justify-content-between small text-muted mt-auto">
                            <span><i class="bi bi-graph-up me-1"></i> <c:out value="${guidance.workoutRecommendation.reason}"/></span>
                            <c:if test="${not empty guidance.workoutRecommendation.actionUrl}">
                                <a href="${pageContext.request.contextPath}${guidance.workoutRecommendation.actionUrl}" class="btn btn-sm btn-outline-primary py-0 px-2 fw-medium">
                                    <c:out value="${guidance.workoutRecommendation.actionLabel}"/>
                                </a>
                            </c:if>
                        </div>
                    </div>
                </div>

                <!-- Goal Strategy Card -->
                <div class="col-12 col-lg-6">
                    <div class="card border rounded-4 p-4 bg-white shadow-xs h-100">
                        <div class="d-flex justify-content-between align-items-center pb-3 mb-3 border-bottom">
                            <div class="d-flex align-items-center gap-2">
                                <div class="icon-wrapper m-0 bg-warning-subtle text-warning" style="width: 36px; height: 36px;">
                                    <i class="bi bi-bullseye"></i>
                                </div>
                                <h5 class="fw-bold text-dark mb-0">Goal Strategy</h5>
                            </div>
                            <span class="badge bg-warning-subtle text-warning small fw-semibold px-2.5 py-1">Strategy</span>
                        </div>
                        <h6 class="fw-bold text-dark mb-2"><c:out value="${guidance.goalAdvice.title}"/></h6>
                        <p class="text-secondary small mb-3 flex-grow-1"><c:out value="${guidance.goalAdvice.message}"/></p>

                        <div class="p-2.5 bg-light rounded-3 d-flex align-items-center justify-content-between small text-muted mt-auto">
                            <span><i class="bi bi-tag me-1"></i> <c:out value="${guidance.goalAdvice.reason}"/></span>
                            <c:if test="${not empty guidance.goalAdvice.actionUrl}">
                                <a href="${pageContext.request.contextPath}${guidance.goalAdvice.actionUrl}" class="btn btn-sm btn-outline-warning py-0 px-2 fw-medium">
                                    <c:out value="${guidance.goalAdvice.actionLabel}"/>
                                </a>
                            </c:if>
                        </div>
                    </div>
                </div>

                <!-- Nutrition Tip Card -->
                <div class="col-12 col-lg-6">
                    <div class="card border rounded-4 p-4 bg-white shadow-xs h-100">
                        <div class="d-flex justify-content-between align-items-center pb-3 mb-3 border-bottom">
                            <div class="d-flex align-items-center gap-2">
                                <div class="icon-wrapper m-0 bg-danger-subtle text-danger" style="width: 36px; height: 36px;">
                                    <i class="bi bi-apple"></i>
                                </div>
                                <h5 class="fw-bold text-dark mb-0">Nutrition Tip</h5>
                            </div>
                            <span class="badge bg-danger-subtle text-danger small fw-semibold px-2.5 py-1">Energy Balance</span>
                        </div>
                        <h6 class="fw-bold text-dark mb-2"><c:out value="${guidance.nutritionGuidance.title}"/></h6>
                        <p class="text-secondary small mb-3 flex-grow-1"><c:out value="${guidance.nutritionGuidance.message}"/></p>

                        <div class="p-2.5 bg-light rounded-3 d-flex align-items-center justify-content-between small text-muted mt-auto">
                            <span><i class="bi bi-check-circle me-1"></i> <c:out value="${guidance.nutritionGuidance.reason}"/></span>
                            <a href="${pageContext.request.contextPath}/progress" class="btn btn-sm btn-outline-secondary py-0 px-2 fw-medium">
                                View TDEE
                            </a>
                        </div>
                    </div>
                </div>

                <!-- Daily Tip Card -->
                <div class="col-12 col-lg-6">
                    <div class="card border rounded-4 p-4 bg-white shadow-xs h-100">
                        <div class="d-flex justify-content-between align-items-center pb-3 mb-3 border-bottom">
                            <div class="d-flex align-items-center gap-2">
                                <div class="icon-wrapper m-0 bg-success-subtle text-success" style="width: 36px; height: 36px;">
                                    <i class="bi bi-stars"></i>
                                </div>
                                <h5 class="fw-bold text-dark mb-0">Daily Tip</h5>
                            </div>
                            <span class="badge bg-success-subtle text-success small fw-semibold px-2.5 py-1">Mindset</span>
                        </div>
                        <h6 class="fw-bold text-dark mb-2"><c:out value="${guidance.dailyTip.title}"/></h6>
                        <p class="text-secondary small mb-3 flex-grow-1"><c:out value="${guidance.dailyTip.message}"/></p>

                        <div class="p-2.5 bg-light rounded-3 small text-muted mt-auto">
                            <i class="bi bi-lightbulb me-1"></i> <c:out value="${guidance.dailyTip.reason}"/>
                        </div>
                    </div>
                </div>
            </div>

            <!-- Environment Exercise Suggestion Banner -->
            <div class="card border rounded-4 p-4 bg-white shadow-xs mb-4">
                <div class="d-flex justify-content-between align-items-center pb-3 mb-3 border-bottom">
                    <div class="d-flex align-items-center gap-2">
                        <div class="icon-wrapper m-0 bg-info-subtle text-info" style="width: 36px; height: 36px;">
                            <i class="bi bi-lightning-charge-fill"></i>
                        </div>
                        <div>
                            <h5 class="fw-bold text-dark mb-0">Exercise & Setting Focus</h5>
                            <small class="text-muted"><c:out value="${guidance.exerciseSuggestion.reason}"/></small>
                        </div>
                    </div>
                    <c:if test="${not empty guidance.environmentLabel}">
                        <span class="badge bg-info-subtle text-info small fw-semibold px-2.5 py-1"><c:out value="${guidance.environmentLabel}"/> Setting</span>
                    </c:if>
                </div>

                <div class="row align-items-center g-3">
                    <div class="col-12 col-md-9">
                        <h6 class="fw-bold text-dark mb-1"><c:out value="${guidance.exerciseSuggestion.title}"/></h6>
                        <p class="text-secondary small mb-0"><c:out value="${guidance.exerciseSuggestion.message}"/></p>
                    </div>
                    <div class="col-12 col-md-3 text-md-end">
                        <c:if test="${not empty guidance.exerciseSuggestion.actionUrl}">
                            <a href="${pageContext.request.contextPath}${guidance.exerciseSuggestion.actionUrl}" class="btn btn-outline-success btn-sm px-3 fw-medium">
                                <i class="bi bi-plus-lg me-1"></i> <c:out value="${guidance.exerciseSuggestion.actionLabel}"/>
                            </a>
                        </c:if>
                    </div>
                </div>
            </div>

            <!-- Wellness Disclaimer -->
            <div class="p-3 bg-light rounded-3 text-center text-muted small">
                <i class="bi bi-shield-check me-1 text-success"></i>
                <strong>Wellness Disclaimer:</strong> FitAura guidance provides general lifestyle and fitness educational suggestions. Consider consulting a licensed healthcare professional before starting any rigorous new exercise or dietary regimen.
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
