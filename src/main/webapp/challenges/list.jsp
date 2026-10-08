<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ taglib prefix="c" uri="jakarta.tags.core" %>
<!DOCTYPE html>
<html lang="en">
<head>
    <meta charset="UTF-8">
    <meta name="viewport" content="width=device-width, initial-scale=1.0">
    <title>Fitness Challenges – FitAura</title>
    <meta name="description" content="Discover, join, and track community fitness challenges on FitAura.">

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

            <button class="navbar-toggler" type="button" data-bs-toggle="collapse" data-bs-target="#challengesNavbar" aria-controls="challengesNavbar" aria-expanded="false" aria-label="Toggle navigation">
                <span class="navbar-toggler-icon"></span>
            </button>

            <div class="collapse navbar-collapse" id="challengesNavbar">
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
                        <a class="nav-link active fw-bold text-success" aria-current="page" href="${pageContext.request.contextPath}/challenges">
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
        <div class="container" style="max-width: 1100px;">

            <!-- Header & Action Buttons -->
            <div class="d-flex flex-column flex-md-row justify-content-between align-items-md-center gap-3 mb-4">
                <div>
                    <div class="d-inline-flex align-items-center gap-2 px-3 py-1 rounded-pill bg-success-subtle text-success small fw-semibold mb-2">
                        <i class="bi bi-people-fill"></i> Community & Milestones
                    </div>
                    <h2 class="fw-bold text-dark mb-1">Community Challenges</h2>
                    <p class="text-secondary small mb-0">Join time-bound fitness challenges, stay accountable with the community, and earn milestone rewards.</p>
                </div>
                <div class="d-flex gap-2">
                    <a href="${pageContext.request.contextPath}/challenges/my" class="btn btn-outline-secondary px-3 py-2 fw-semibold rounded-3">
                        <i class="bi bi-folder-check me-1"></i> My Challenges
                    </a>
                    <a href="${pageContext.request.contextPath}/challenges/create" class="btn btn-success px-3 py-2 fw-semibold rounded-3 shadow-xs">
                        <i class="bi bi-plus-lg me-1"></i> Create Challenge
                    </a>
                </div>
            </div>

            <!-- Flash Feedback Messages -->
            <c:if test="${not empty sessionScope.feedbackMessage}">
                <div class="alert alert-success alert-dismissible fade show rounded-3 mb-4 shadow-xs" role="alert">
                    <i class="bi bi-check-circle-fill me-2"></i>
                    <c:out value="${sessionScope.feedbackMessage}"/>
                    <button type="button" class="btn-close" data-bs-dismiss="alert" aria-label="Close"></button>
                </div>
                <c:remove var="feedbackMessage" scope="session"/>
            </c:if>

            <c:if test="${not empty sessionScope.errorMessage}">
                <div class="alert alert-danger alert-dismissible fade show rounded-3 mb-4 shadow-xs" role="alert">
                    <i class="bi bi-exclamation-triangle-fill me-2"></i>
                    <c:out value="${sessionScope.errorMessage}"/>
                    <button type="button" class="btn-close" data-bs-dismiss="alert" aria-label="Close"></button>
                </div>
                <c:remove var="errorMessage" scope="session"/>
            </c:if>

            <!-- Filter Tabs -->
            <div class="card border rounded-4 p-2 bg-white shadow-xs mb-4">
                <ul class="nav nav-pills nav-fill">
                    <li class="nav-item">
                        <a class="nav-link fw-semibold ${currentFilter == 'ACTIVE' ? 'active bg-success' : 'text-dark'}"
                           href="${pageContext.request.contextPath}/challenges?filter=ACTIVE">
                            <i class="bi bi-play-circle me-1"></i> Active Challenges
                        </a>
                    </li>
                    <li class="nav-item">
                        <a class="nav-link fw-semibold ${currentFilter == 'UPCOMING' ? 'active bg-success' : 'text-dark'}"
                           href="${pageContext.request.contextPath}/challenges?filter=UPCOMING">
                            <i class="bi bi-calendar-event me-1"></i> Upcoming
                        </a>
                    </li>
                    <li class="nav-item">
                        <a class="nav-link fw-semibold ${currentFilter == 'COMPLETED' ? 'active bg-success' : 'text-dark'}"
                           href="${pageContext.request.contextPath}/challenges?filter=COMPLETED">
                            <i class="bi bi-check2-all me-1"></i> Completed / Past
                        </a>
                    </li>
                    <li class="nav-item">
                        <a class="nav-link fw-semibold ${currentFilter == 'ALL' ? 'active bg-success' : 'text-dark'}"
                           href="${pageContext.request.contextPath}/challenges?filter=ALL">
                            <i class="bi bi-grid me-1"></i> All Challenges
                        </a>
                    </li>
                </ul>
            </div>

            <!-- Challenges Grid -->
            <c:choose>
                <c:when test="${empty challenges}">
                    <div class="card border rounded-4 p-5 text-center bg-white shadow-xs">
                        <div class="icon-wrapper bg-light text-muted mx-auto mb-3" style="width: 54px; height: 54px;">
                            <i class="bi bi-trophy fs-3"></i>
                        </div>
                        <h5 class="fw-bold text-dark mb-1">No Challenges Found</h5>
                        <p class="text-secondary small mb-4">There are currently no challenges in this category. Be the first to start one!</p>
                        <div>
                            <a href="${pageContext.request.contextPath}/challenges/create" class="btn btn-success px-4 py-2 fw-semibold rounded-3">
                                <i class="bi bi-plus-lg me-1"></i> Create Challenge
                            </a>
                        </div>
                    </div>
                </c:when>
                <c:otherwise>
                    <div class="row g-4">
                        <c:forEach var="item" items="${challenges}">
                            <div class="col-12 col-md-6 col-lg-4">
                                <div class="card border rounded-4 h-100 bg-white shadow-xs hover-shadow transition-all d-flex flex-column">
                                    <div class="p-4 flex-grow-1">
                                        <!-- Header: Status & Points -->
                                        <div class="d-flex justify-content-between align-items-center mb-3">
                                            <span class="badge ${item.challenge.statusBadgeClass} text-uppercase px-2.5 py-1 small fw-semibold">
                                                <c:out value="${item.challenge.status}"/>
                                            </span>
                                            <c:if test="${item.challenge.pointsReward > 0}">
                                                <span class="badge bg-warning-subtle text-warning-emphasis border border-warning-subtle px-2.5 py-1 small fw-bold">
                                                    <i class="bi bi-star-fill me-1"></i> <c:out value="${item.challenge.pointsReward}"/> pts
                                                </span>
                                            </c:if>
                                        </div>

                                        <!-- Challenge Title & Description -->
                                        <h5 class="fw-bold text-dark mb-2">
                                            <a href="${pageContext.request.contextPath}/challenges/view?id=${item.challenge.challengeId}" class="text-dark text-decoration-none">
                                                <c:out value="${item.challenge.name}"/>
                                            </a>
                                        </h5>
                                        <p class="text-secondary small mb-3 text-truncate-2" style="min-height: 2.5rem;">
                                            <c:out value="${item.challenge.description}"/>
                                        </p>

                                        <!-- Target & Metrics -->
                                        <div class="p-3 bg-light rounded-3 mb-3">
                                            <div class="d-flex justify-content-between align-items-center mb-1">
                                                <span class="small text-muted">Target Goal:</span>
                                                <span class="fw-bold text-dark">
                                                    <c:out value="${item.challenge.goalValue}"/> <c:out value="${item.challenge.goalUnit}"/>
                                                </span>
                                            </div>
                                            <div class="d-flex justify-content-between align-items-center small text-muted">
                                                <span>Timeline:</span>
                                                <span><c:out value="${item.challenge.startDate}"/> to <c:out value="${item.challenge.endDate}"/></span>
                                            </div>
                                        </div>

                                        <!-- User Participation Progress (if joined) -->
                                        <c:if test="${item.userJoined}">
                                            <div class="mb-3">
                                                <div class="d-flex justify-content-between align-items-center small mb-1">
                                                    <span class="fw-semibold text-success">
                                                        <c:choose>
                                                            <c:when test="${item.userCompleted}">
                                                                <i class="bi bi-check-circle-fill"></i> Completed!
                                                            </c:when>
                                                            <c:otherwise>
                                                                <i class="bi bi-person-check-fill"></i> Your Progress
                                                            </c:otherwise>
                                                        </c:choose>
                                                    </span>
                                                    <span class="fw-bold text-dark"><c:out value="${item.userProgressPercent}"/>%</span>
                                                </div>
                                                <div class="progress" style="height: 6px;">
                                                    <div class="progress-bar ${item.userCompleted ? 'bg-success' : 'bg-primary'}"
                                                         role="progressbar" style="width: ${item.userProgressPercent}%"
                                                         aria-valuenow="${item.userProgressPercent}" aria-valuemin="0" aria-valuemax="100">
                                                    </div>
                                                </div>
                                            </div>
                                        </c:if>

                                        <!-- Meta Footer -->
                                        <div class="d-flex justify-content-between align-items-center small text-muted pt-2 border-top">
                                            <span>
                                                <i class="bi bi-people me-1"></i> <c:out value="${item.participantCount}"/> joined
                                            </span>
                                            <span>
                                                <c:choose>
                                                    <c:when test="${item.expired}">
                                                        <span class="text-danger fw-medium">Concluded</span>
                                                    </c:when>
                                                    <c:when test="${item.upcoming}">
                                                        <span class="text-info fw-medium">Starts in <c:out value="${item.daysUntilStart}"/>d</span>
                                                    </c:when>
                                                    <c:otherwise>
                                                        <span class="text-success fw-medium"><c:out value="${item.daysRemaining}"/>d left</span>
                                                    </c:otherwise>
                                                </c:choose>
                                            </span>
                                        </div>
                                    </div>

                                    <!-- Card Footer Action -->
                                    <div class="card-footer bg-white border-top p-3 text-end">
                                        <a href="${pageContext.request.contextPath}/challenges/view?id=${item.challenge.challengeId}" class="btn btn-sm ${item.userJoined ? 'btn-outline-success' : 'btn-success'} w-100 fw-semibold">
                                            <c:choose>
                                                <c:when test="${item.userJoined}">
                                                    <i class="bi bi-eye me-1"></i> View Progress
                                                </c:when>
                                                <c:when test="${item.canJoin}">
                                                    <i class="bi bi-plus-circle me-1"></i> Join Challenge
                                                </c:when>
                                                <c:otherwise>
                                                    <i class="bi bi-info-circle me-1"></i> View Details
                                                </c:otherwise>
                                            </c:choose>
                                        </a>
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
