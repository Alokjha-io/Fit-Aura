<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ taglib prefix="c" uri="jakarta.tags.core" %>
<!DOCTYPE html>
<html lang="en">
<head>
    <meta charset="UTF-8">
    <meta name="viewport" content="width=device-width, initial-scale=1.0">
    <title><c:out value="${detail.challenge.name}"/> – FitAura Challenge</title>
    <meta name="description" content="View fitness challenge milestones, track your workout progress, and compete in community challenges.">

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
                        <a class="nav-link active fw-bold text-success" aria-current="page" href="${pageContext.request.contextPath}/challenges">
                            <i class="bi bi-trophy me-1"></i> Challenges
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

            <!-- Breadcrumb Navigation -->
            <nav aria-label="breadcrumb" class="mb-4">
                <ol class="breadcrumb">
                    <li class="breadcrumb-item"><a href="${pageContext.request.contextPath}/challenges" class="text-decoration-none text-success">Challenges</a></li>
                    <li class="breadcrumb-item active" aria-current="page"><c:out value="${detail.challenge.name}"/></li>
                </ol>
            </nav>

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

            <!-- Challenge Hero Card -->
            <div class="card border-0 rounded-4 bg-white shadow-sm p-4 p-md-5 mb-4 position-relative overflow-hidden"
                 style="border-left: 6px solid #198754 !important;">
                <div class="d-flex flex-column flex-md-row justify-content-between align-items-md-start gap-4">
                    <div class="flex-grow-1">
                        <div class="d-flex flex-wrap align-items-center gap-2 mb-2">
                            <span class="badge ${detail.challenge.statusBadgeClass} text-uppercase px-2.5 py-1 small fw-semibold">
                                <c:out value="${detail.challenge.status}"/>
                            </span>
                            <c:if test="${detail.challenge.pointsReward > 0}">
                                <span class="badge bg-warning-subtle text-warning-emphasis border border-warning-subtle px-2.5 py-1 small fw-bold">
                                    <i class="bi bi-star-fill me-1"></i> <c:out value="${detail.challenge.pointsReward}"/> Points Reward
                                </span>
                            </c:if>
                            <span class="badge bg-light text-secondary border px-2.5 py-1 small">
                                <i class="bi bi-person me-1"></i> Host: <c:out value="${detail.creatorDisplayName}"/>
                            </span>
                        </div>

                        <h2 class="fw-bold text-dark mb-2"><c:out value="${detail.challenge.name}"/></h2>
                        <p class="text-secondary mb-4 fs-6 leading-relaxed"><c:out value="${detail.challenge.description}"/></p>

                        <!-- Metric Highlights -->
                        <div class="row g-3">
                            <div class="col-6 col-md-3">
                                <div class="p-3 bg-light rounded-3 text-center">
                                    <small class="text-muted d-block mb-1">Target Goal</small>
                                    <strong class="text-dark fs-5"><c:out value="${detail.challenge.goalValue}"/> <c:out value="${detail.challenge.goalUnit}"/></strong>
                                </div>
                            </div>
                            <div class="col-6 col-md-3">
                                <div class="p-3 bg-light rounded-3 text-center">
                                    <small class="text-muted d-block mb-1">Timeline</small>
                                    <strong class="text-dark fs-6"><c:out value="${detail.totalDurationDays}"/> Days</strong>
                                </div>
                            </div>
                            <div class="col-6 col-md-3">
                                <div class="p-3 bg-light rounded-3 text-center">
                                    <small class="text-muted d-block mb-1">Participants</small>
                                    <strong class="text-dark fs-5"><c:out value="${detail.participantCount}"/></strong>
                                </div>
                            </div>
                            <div class="col-6 col-md-3">
                                <div class="p-3 bg-light rounded-3 text-center">
                                    <small class="text-muted d-block mb-1">Schedule</small>
                                    <strong class="text-dark fs-6">
                                        <c:choose>
                                            <c:when test="${detail.expired}"><span class="text-danger">Concluded</span></c:when>
                                            <c:when test="${detail.upcoming}"><span class="text-info"><c:out value="${detail.daysUntilStart}"/>d to start</span></c:when>
                                            <c:otherwise><span class="text-success"><c:out value="${detail.daysRemaining}"/>d left</span></c:otherwise>
                                        </c:choose>
                                    </strong>
                                </div>
                            </div>
                        </div>
                    </div>

                    <!-- Action Box -->
                    <div class="text-md-end shrink-0">
                        <c:choose>
                            <c:when test="${detail.userJoined}">
                                <span class="badge bg-success-subtle text-success px-3 py-2 fs-6 mb-2 d-inline-block">
                                    <i class="bi bi-check2-circle me-1"></i> Currently Enrolled
                                </span>
                            </c:when>
                            <c:when test="${detail.canJoin}">
                                <form action="${pageContext.request.contextPath}/challenges/join" method="POST">
                                    <input type="hidden" name="csrfToken" value="${sessionScope.csrfToken}"/>
                                    <input type="hidden" name="challengeId" value="${detail.challenge.challengeId}"/>
                                    <button type="submit" class="btn btn-success px-4 py-2.5 fw-semibold rounded-3 shadow-xs">
                                        <i class="bi bi-plus-circle me-1"></i> Join Challenge
                                    </button>
                                </form>
                            </c:when>
                            <c:otherwise>
                                <button class="btn btn-secondary px-4 py-2.5 fw-semibold rounded-3" disabled>
                                    <i class="bi bi-lock-fill me-1"></i> Not Joinable
                                </button>
                            </c:otherwise>
                        </c:choose>
                    </div>
                </div>
            </div>

            <!-- User Personal Progress Card (if enrolled) -->
            <c:if test="${detail.userJoined}">
                <div class="card border rounded-4 p-4 bg-white shadow-xs mb-4">
                    <div class="d-flex justify-content-between align-items-center mb-3">
                        <div class="d-flex align-items-center gap-2">
                            <div class="icon-wrapper m-0 bg-primary-subtle text-primary" style="width: 36px; height: 36px;">
                                <i class="bi bi-speedometer2"></i>
                            </div>
                            <h5 class="fw-bold text-dark mb-0">Your Challenge Progress</h5>
                        </div>
                        <span class="badge ${detail.userCompleted ? 'bg-success' : 'bg-primary'} px-3 py-1.5 fw-semibold">
                            <c:choose>
                                <c:when test="${detail.userCompleted}"><i class="bi bi-trophy-fill me-1"></i> Challenge Completed!</c:when>
                                <c:otherwise><i class="bi bi-hourglass-split me-1"></i> In Progress</c:otherwise>
                            </c:choose>
                        </span>
                    </div>

                    <div class="row align-items-center g-3 mb-3">
                        <div class="col-12 col-md-8">
                            <div class="d-flex justify-content-between align-items-center mb-1">
                                <span class="text-secondary small">Completion Rate</span>
                                <span class="fw-bold text-dark fs-5"><c:out value="${detail.userProgressPercent}"/>%</span>
                            </div>
                            <div class="progress" style="height: 12px;">
                                <div class="progress-bar progress-bar-striped progress-bar-animated ${detail.userCompleted ? 'bg-success' : 'bg-primary'}"
                                     role="progressbar" style="width: ${detail.userProgressPercent}%"
                                     aria-valuenow="${detail.userProgressPercent}" aria-valuemin="0" aria-valuemax="100">
                                </div>
                            </div>
                        </div>
                        <div class="col-12 col-md-4 text-md-end">
                            <div class="small text-muted mb-1">Current Progress:</div>
                            <div class="fw-bold text-dark fs-4">
                                <c:out value="${detail.userParticipation.progressValue}"/> / <c:out value="${detail.challenge.goalValue}"/> <small class="text-muted"><c:out value="${detail.challenge.goalUnit}"/></small>
                            </div>
                        </div>
                    </div>

                    <!-- Progress Controls -->
                    <div class="d-flex flex-wrap gap-2 pt-3 border-top justify-content-between align-items-center">
                        <div class="d-flex flex-wrap gap-2">
                            <!-- Sync from Workouts Form -->
                            <form action="${pageContext.request.contextPath}/challenges/sync" method="POST" class="d-inline">
                                <input type="hidden" name="csrfToken" value="${sessionScope.csrfToken}"/>
                                <input type="hidden" name="challengeId" value="${detail.challenge.challengeId}"/>
                                <button type="submit" class="btn btn-outline-success btn-sm px-3 fw-semibold">
                                    <i class="bi bi-arrow-repeat me-1"></i> Sync from Workouts
                                </button>
                            </form>

                            <c:if test="${not detail.userCompleted}">
                                <!-- Manual Contribution Button -->
                                <button type="button" class="btn btn-outline-primary btn-sm px-3 fw-semibold" data-bs-toggle="modal" data-bs-target="#logProgressModal">
                                    <i class="bi bi-plus-lg me-1"></i> Log Contribution
                                </button>
                            </c:if>
                        </div>

                        <c:if test="${detail.canLeave}">
                            <!-- Leave Challenge Form -->
                            <form action="${pageContext.request.contextPath}/challenges/leave" method="POST" class="d-inline"
                                  onsubmit="return confirm('Are you sure you want to leave this challenge? Your participation will be cancelled.');">
                                <input type="hidden" name="csrfToken" value="${sessionScope.csrfToken}"/>
                                <input type="hidden" name="challengeId" value="${detail.challenge.challengeId}"/>
                                <button type="submit" class="btn btn-outline-danger btn-sm px-3 fw-semibold">
                                    <i class="bi bi-box-arrow-left me-1"></i> Leave Challenge
                                </button>
                            </form>
                        </c:if>
                    </div>
                </div>
            </c:if>

            <!-- Community Leaderboard / Participants List -->
            <div class="card border rounded-4 p-4 bg-white shadow-xs mb-4">
                <div class="d-flex justify-content-between align-items-center pb-3 mb-3 border-bottom">
                    <div class="d-flex align-items-center gap-2">
                        <div class="icon-wrapper m-0 bg-info-subtle text-info" style="width: 36px; height: 36px;">
                            <i class="bi bi-people-fill"></i>
                        </div>
                        <div>
                            <h5 class="fw-bold text-dark mb-0">Community Participants</h5>
                            <small class="text-muted">Display names shown according to FitAura privacy preferences.</small>
                        </div>
                    </div>
                    <span class="badge bg-light text-dark border small fw-semibold px-2.5 py-1">
                        <c:out value="${detail.participantCount}"/> Enrolled
                    </span>
                </div>

                <c:choose>
                    <c:when test="${empty detail.communityParticipants}">
                        <div class="text-center py-4 text-muted small">
                            <i class="bi bi-person-dash fs-3 d-block mb-2"></i>
                            No athletes have joined this challenge yet. Be the first to join!
                        </div>
                    </c:when>
                    <c:otherwise>
                        <div class="table-responsive">
                            <table class="table table-hover align-middle mb-0">
                                <thead class="table-light small text-muted">
                                    <tr>
                                        <th style="width: 60px;">#</th>
                                        <th>Athlete</th>
                                        <th>Progress</th>
                                        <th style="width: 140px;">Status</th>
                                        <th class="text-end" style="width: 140px;">Joined</th>
                                    </tr>
                                </thead>
                                <tbody>
                                    <c:forEach var="p" items="${detail.communityParticipants}" varStatus="status">
                                        <tr class="${p.currentUser ? 'table-success-subtle fw-semibold' : ''}">
                                            <td class="text-muted small">
                                                <c:choose>
                                                    <c:when test="${status.count == 1}"><span class="badge bg-warning text-dark">🥇 1</span></c:when>
                                                    <c:when test="${status.count == 2}"><span class="badge bg-secondary">🥈 2</span></c:when>
                                                    <c:when test="${status.count == 3}"><span class="badge bg-dark">🥉 3</span></c:when>
                                                    <c:otherwise><c:out value="${status.count}"/></c:otherwise>
                                                </c:choose>
                                            </td>
                                            <td>
                                                <div class="d-flex align-items-center gap-2">
                                                    <div class="rounded-circle bg-light border d-flex align-items-center justify-content-center text-muted" style="width: 28px; height: 28px;">
                                                        <i class="bi bi-person-fill small"></i>
                                                    </div>
                                                    <span>
                                                        <c:out value="${p.displayName}"/>
                                                        <c:if test="${p.currentUser}">
                                                            <span class="badge bg-success ms-1 small">You</span>
                                                        </c:if>
                                                    </span>
                                                </div>
                                            </td>
                                            <td>
                                                <div class="d-flex align-items-center gap-2" style="max-width: 240px;">
                                                    <div class="progress flex-grow-1" style="height: 6px;">
                                                        <div class="progress-bar ${p.status == 'COMPLETED' ? 'bg-success' : 'bg-primary'}"
                                                             role="progressbar" style="width: ${p.progressPercentage}%"
                                                             aria-valuenow="${p.progressPercentage}" aria-valuemin="0" aria-valuemax="100">
                                                        </div>
                                                    </div>
                                                    <span class="small text-muted"><c:out value="${p.progressPercentage}"/>%</span>
                                                </div>
                                            </td>
                                            <td>
                                                <span class="badge ${p.status == 'COMPLETED' ? 'bg-success' : 'bg-primary-subtle text-primary'} small">
                                                    <c:out value="${p.status}"/>
                                                </span>
                                            </td>
                                            <td class="text-end small text-muted">
                                                <c:out value="${p.joinedAt}"/>
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

    <!-- Manual Progress Log Modal -->
    <div class="modal fade" id="logProgressModal" tabindex="-1" aria-labelledby="logProgressModalLabel" aria-hidden="true">
        <div class="modal-dialog modal-dialog-centered">
            <div class="modal-content rounded-4 border-0 shadow">
                <form action="${pageContext.request.contextPath}/challenges/progress" method="POST">
                    <input type="hidden" name="csrfToken" value="${sessionScope.csrfToken}"/>
                    <input type="hidden" name="challengeId" value="${detail.challenge.challengeId}"/>

                    <div class="modal-header border-bottom p-4">
                        <h5 class="modal-title fw-bold text-dark" id="logProgressModalLabel">
                            <i class="bi bi-plus-circle text-success me-2"></i> Log Challenge Contribution
                        </h5>
                        <button type="button" class="btn-close" data-bs-dismiss="modal" aria-label="Close"></button>
                    </div>

                    <div class="modal-body p-4">
                        <div class="mb-3">
                            <label class="form-label small fw-semibold text-dark">
                                Additional Contribution (<c:out value="${detail.challenge.goalUnit}"/>)
                            </label>
                            <input type="number" step="0.1" min="0.1" name="addedValue" class="form-control rounded-3"
                                   placeholder="e.g. 30" required/>
                            <div class="form-text">
                                Target goal: <c:out value="${detail.challenge.goalValue}"/> <c:out value="${detail.challenge.goalUnit}"/>.
                            </div>
                        </div>
                    </div>

                    <div class="modal-footer border-top p-3 bg-light">
                        <button type="button" class="btn btn-secondary btn-sm px-3" data-bs-dismiss="modal">Cancel</button>
                        <button type="submit" class="btn btn-success btn-sm px-4 fw-semibold">Save Contribution</button>
                    </div>
                </form>
            </div>
        </div>
    </div>

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
