<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ taglib prefix="c" uri="jakarta.tags.core" %>
<!DOCTYPE html>
<html lang="en">
<head>
    <meta charset="UTF-8">
    <meta name="viewport" content="width=device-width, initial-scale=1.0">
    <title>Fitness Goals – FitAura</title>
    <meta name="description" content="Set, track, and manage your personal fitness milestones on FitAura.">

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

            <button class="navbar-toggler" type="button" data-bs-toggle="collapse" data-bs-target="#goalsNavbar" aria-controls="goalsNavbar" aria-expanded="false" aria-label="Toggle navigation">
                <span class="navbar-toggler-icon"></span>
            </button>

            <div class="collapse navbar-collapse" id="goalsNavbar">
                <ul class="navbar-nav me-auto mb-2 mb-lg-0 ms-lg-4">
                    <li class="nav-item">
                        <a class="nav-link fw-medium" href="${pageContext.request.contextPath}/user/dashboard">
                            <i class="bi bi-house-door me-1"></i> Dashboard
                        </a>
                    </li>
                    <li class="nav-item">
                        <a class="nav-link fw-medium text-dark" href="${pageContext.request.contextPath}/workouts">
                            <i class="bi bi-activity me-1"></i> Workouts
                        </a>
                    </li>
                    <li class="nav-item">
                        <a class="nav-link active fw-bold text-success" aria-current="page" href="${pageContext.request.contextPath}/goals">
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
        <div class="container" style="max-width: 1000px;">

            <!-- Header & Action Row -->
            <div class="d-flex flex-column flex-sm-row justify-content-between align-items-sm-center gap-3 mb-4">
                <div>
                    <h2 class="fw-bold text-dark mb-1">Fitness Goals</h2>
                    <p class="text-secondary small mb-0">
                        <c:choose>
                            <c:when test="${activeGoalCount > 0}">
                                You have <strong>${activeGoalCount}</strong> active goal<c:if test="${activeGoalCount != 1}">s</c:if> in progress.
                            </c:when>
                            <c:otherwise>
                                Set personal fitness milestones to guide your training.
                            </c:otherwise>
                        </c:choose>
                    </p>
                </div>
                <a href="${pageContext.request.contextPath}/goals/create" class="btn btn-success px-4 py-2 fw-semibold rounded-3 shadow-xs">
                    <i class="bi bi-plus-lg me-1"></i> Create Goal
                </a>
            </div>

            <!-- Feedback Alerts -->
            <c:if test="${not empty sessionScope.flashSuccess}">
                <div class="alert alert-success alert-dismissible fade show d-flex align-items-center p-3 rounded-4 shadow-sm mb-4" role="alert">
                    <i class="bi bi-check-circle-fill fs-5 me-2 text-success"></i>
                    <div><c:out value="${sessionScope.flashSuccess}"/></div>
                    <button type="button" class="btn-close" data-bs-dismiss="alert" aria-label="Close"></button>
                </div>
                <c:remove var="flashSuccess" scope="session"/>
            </c:if>

            <c:if test="${not empty sessionScope.flashError}">
                <div class="alert alert-danger alert-dismissible fade show d-flex align-items-center p-3 rounded-4 shadow-sm mb-4" role="alert">
                    <i class="bi bi-exclamation-triangle-fill fs-5 me-2 text-danger"></i>
                    <div><c:out value="${sessionScope.flashError}"/></div>
                    <button type="button" class="btn-close" data-bs-dismiss="alert" aria-label="Close"></button>
                </div>
                <c:remove var="flashError" scope="session"/>
            </c:if>

            <c:if test="${not empty errorMessage}">
                <div class="alert alert-danger alert-dismissible fade show d-flex align-items-center p-3 rounded-4 shadow-sm mb-4" role="alert">
                    <i class="bi bi-exclamation-triangle-fill fs-5 me-2 text-danger"></i>
                    <div><c:out value="${errorMessage}"/></div>
                    <button type="button" class="btn-close" data-bs-dismiss="alert" aria-label="Close"></button>
                </div>
            </c:if>

            <!-- Status Filter Tabs -->
            <div class="card border rounded-4 p-2 bg-white mb-4 shadow-xs">
                <ul class="nav nav-pills nav-fill small">
                    <li class="nav-item">
                        <a class="nav-link <c:if test="${empty currentStatusFilter}">active fw-semibold</c:if>"
                           href="${pageContext.request.contextPath}/goals">
                            All Goals (${totalGoalCount})
                        </a>
                    </li>
                    <li class="nav-item">
                        <a class="nav-link <c:if test="${currentStatusFilter == 'ACTIVE'}">active fw-semibold</c:if>"
                           href="${pageContext.request.contextPath}/goals?status=ACTIVE">
                            Active
                        </a>
                    </li>
                    <li class="nav-item">
                        <a class="nav-link <c:if test="${currentStatusFilter == 'PAUSED'}">active fw-semibold</c:if>"
                           href="${pageContext.request.contextPath}/goals?status=PAUSED">
                            Paused
                        </a>
                    </li>
                    <li class="nav-item">
                        <a class="nav-link <c:if test="${currentStatusFilter == 'COMPLETED'}">active fw-semibold</c:if>"
                           href="${pageContext.request.contextPath}/goals?status=COMPLETED">
                            Completed
                        </a>
                    </li>
                    <li class="nav-item">
                        <a class="nav-link <c:if test="${currentStatusFilter == 'CANCELLED'}">active fw-semibold</c:if>"
                           href="${pageContext.request.contextPath}/goals?status=CANCELLED">
                            Cancelled
                        </a>
                    </li>
                </ul>
            </div>

            <!-- Goals List or Empty State -->
            <c:choose>
                <c:when test="${empty goals}">
                    <!-- Empty State -->
                    <div class="card border rounded-4 p-5 bg-white text-center shadow-xs">
                        <div class="icon-wrapper mx-auto mb-3 bg-success-subtle text-success" style="width: 64px; height: 64px;">
                            <i class="bi bi-bullseye fs-2"></i>
                        </div>
                        <h4 class="fw-bold text-dark mb-2">
                            <c:choose>
                                <c:when test="${not empty currentStatusFilter}">No ${currentStatusFilter.toLowerCase()} goals</c:when>
                                <c:otherwise>No goals yet</c:otherwise>
                            </c:choose>
                        </h4>
                        <p class="text-secondary small mb-4">
                            <c:choose>
                                <c:when test="${not empty currentStatusFilter}">
                                    You do not have any goals matching the selected status.
                                </c:when>
                                <c:otherwise>
                                    Set your first fitness goal to start tracking your progress.
                                </c:otherwise>
                            </c:choose>
                        </p>
                        <div>
                            <a href="${pageContext.request.contextPath}/goals/create" class="btn btn-success px-4 py-2 fw-semibold rounded-3 shadow-xs">
                                <i class="bi bi-plus-lg me-1"></i> Create Goal
                            </a>
                        </div>
                    </div>
                </c:when>
                <c:otherwise>
                    <!-- Goals Cards Grid -->
                    <div class="row g-3 mb-4">
                        <c:forEach var="g" items="${goals}">
                            <div class="col-12">
                                <div class="card border rounded-4 p-4 bg-white shadow-xs hover-shadow transition-all">
                                    <div class="row align-items-center g-3">
                                        <!-- Header & Goal Type -->
                                        <div class="col-12 col-md-5">
                                            <div class="d-flex align-items-center gap-2 mb-1">
                                                <h5 class="fw-bold text-dark mb-0"><c:out value="${g.goalTypeLabel}"/></h5>
                                                <span class="badge 
                                                    <c:choose>
                                                        <c:when test="${g.status == 'ACTIVE'}">bg-success-subtle text-success</c:when>
                                                        <c:when test="${g.status == 'PAUSED'}">bg-warning-subtle text-warning</c:when>
                                                        <c:when test="${g.status == 'COMPLETED'}">bg-primary-subtle text-primary</c:when>
                                                        <c:otherwise>bg-secondary-subtle text-secondary</c:otherwise>
                                                    </c:choose> small fw-semibold">
                                                    <c:out value="${g.statusLabel}"/>
                                                </span>
                                            </div>
                                            <div class="small text-secondary">
                                                <span>Current: <strong><c:out value="${g.currentValue}"/> <c:out value="${g.unit}"/></strong></span>
                                                <span class="mx-1">•</span>
                                                <span>Target: <strong><c:out value="${g.targetValue}"/> <c:out value="${g.unit}"/></strong></span>
                                            </div>
                                            <c:if test="${not empty g.targetDate}">
                                                <small class="text-muted d-block mt-1">
                                                    <i class="bi bi-calendar-event me-1"></i>Target Date: <c:out value="${g.targetDate}"/>
                                                </small>
                                            </c:if>
                                        </div>

                                        <!-- Middle: Progress Bar -->
                                        <div class="col-12 col-md-4">
                                            <div class="d-flex justify-content-between align-items-center small mb-1">
                                                <span class="text-secondary fw-semibold">Progress</span>
                                                <strong class="text-dark"><c:out value="${g.progressPercentage}"/>%</strong>
                                            </div>
                                            <div class="progress" style="height: 10px; border-radius: 6px;">
                                                <div class="progress-bar 
                                                    <c:choose>
                                                        <c:when test="${g.status == 'COMPLETED'}">bg-primary</c:when>
                                                        <c:when test="${g.status == 'PAUSED'}">bg-warning</c:when>
                                                        <c:when test="${g.status == 'CANCELLED'}">bg-secondary</c:when>
                                                        <c:otherwise>bg-success</c:otherwise>
                                                    </c:choose>"
                                                     role="progressbar"
                                                     style="width: ${g.progressPercentage}%;"
                                                     aria-valuenow="${g.progressPercentage}"
                                                     aria-valuemin="0"
                                                     aria-valuemax="100">
                                                </div>
                                            </div>
                                        </div>

                                        <!-- Right: Actions -->
                                        <div class="col-12 col-md-3 text-md-end d-flex flex-wrap justify-content-md-end align-items-center gap-1.5">
                                            <a href="${pageContext.request.contextPath}/goals/view?id=${g.goalId}"
                                               class="btn btn-outline-secondary btn-sm px-2.5 py-1" title="View Details">
                                                <i class="bi bi-eye"></i> <span class="d-none d-lg-inline">View</span>
                                            </a>

                                            <c:if test="${g.status == 'ACTIVE'}">
                                                <a href="${pageContext.request.contextPath}/goals/edit?id=${g.goalId}"
                                                   class="btn btn-outline-primary btn-sm px-2.5 py-1" title="Edit Goal">
                                                    <i class="bi bi-pencil"></i>
                                                </a>
                                                <form action="${pageContext.request.contextPath}/goals/status" method="post" class="d-inline">
                                                    <input type="hidden" name="_csrf" value="${sessionScope.csrfToken}">
                                                    <input type="hidden" name="goalId" value="${g.goalId}">
                                                    <input type="hidden" name="status" value="PAUSED">
                                                    <button type="submit" class="btn btn-outline-warning btn-sm px-2 py-1" title="Pause Goal">
                                                        <i class="bi bi-pause-fill"></i>
                                                    </button>
                                                </form>
                                                <form action="${pageContext.request.contextPath}/goals/status" method="post" class="d-inline">
                                                    <input type="hidden" name="_csrf" value="${sessionScope.csrfToken}">
                                                    <input type="hidden" name="goalId" value="${g.goalId}">
                                                    <input type="hidden" name="status" value="COMPLETED">
                                                    <button type="submit" class="btn btn-outline-success btn-sm px-2 py-1" title="Mark as Completed">
                                                        <i class="bi bi-check-lg"></i>
                                                    </button>
                                                </form>
                                            </c:if>

                                            <c:if test="${g.status == 'PAUSED'}">
                                                <form action="${pageContext.request.contextPath}/goals/status" method="post" class="d-inline">
                                                    <input type="hidden" name="_csrf" value="${sessionScope.csrfToken}">
                                                    <input type="hidden" name="goalId" value="${g.goalId}">
                                                    <input type="hidden" name="status" value="ACTIVE">
                                                    <button type="submit" class="btn btn-outline-success btn-sm px-2.5 py-1" title="Resume Goal">
                                                        <i class="bi bi-play-fill me-1"></i>Resume
                                                    </button>
                                                </form>
                                            </c:if>
                                        </div>
                                    </div>
                                </div>
                            </div>
                        </c:forEach>
                    </div>
                </c:otherwise>
            </c:choose>

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
