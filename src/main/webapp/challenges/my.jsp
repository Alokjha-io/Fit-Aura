<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ taglib prefix="c" uri="jakarta.tags.core" %>
<!DOCTYPE html>
<html lang="en">
<head>
    <meta charset="UTF-8">
    <meta name="viewport" content="width=device-width, initial-scale=1.0">
    <title>My Challenges – FitAura</title>
    <meta name="description" content="View your enrolled, completed, and created fitness challenges on FitAura.">

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
        <div class="container" style="max-width: 1050px;">

            <!-- Header & Actions -->
            <div class="d-flex flex-column flex-md-row justify-content-between align-items-md-center gap-3 mb-4">
                <div>
                    <div class="d-inline-flex align-items-center gap-2 px-3 py-1 rounded-pill bg-success-subtle text-success small fw-semibold mb-2">
                        <i class="bi bi-folder-check"></i> Personal Challenge Portfolio
                    </div>
                    <h2 class="fw-bold text-dark mb-1">My Challenges</h2>
                    <p class="text-secondary small mb-0">Track all challenges you have joined, completed milestones, and challenges you created.</p>
                </div>
                <div class="d-flex gap-2">
                    <a href="${pageContext.request.contextPath}/challenges" class="btn btn-outline-success px-3 py-2 fw-semibold rounded-3">
                        <i class="bi bi-compass me-1"></i> Discover Challenges
                    </a>
                    <a href="${pageContext.request.contextPath}/challenges/create" class="btn btn-success px-3 py-2 fw-semibold rounded-3 shadow-xs">
                        <i class="bi bi-plus-lg me-1"></i> New Challenge
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

            <!-- 1. Active / In-Progress Section -->
            <div class="card border rounded-4 p-4 bg-white shadow-xs mb-4">
                <div class="d-flex justify-content-between align-items-center pb-3 mb-3 border-bottom">
                    <div class="d-flex align-items-center gap-2">
                        <div class="icon-wrapper m-0 bg-primary-subtle text-primary" style="width: 36px; height: 36px;">
                            <i class="bi bi-hourglass-split"></i>
                        </div>
                        <h5 class="fw-bold text-dark mb-0">In-Progress Challenges</h5>
                    </div>
                    <span class="badge bg-primary-subtle text-primary small fw-semibold px-2.5 py-1">
                        <c:out value="${inProgressChallenges.size()}"/> Active
                    </span>
                </div>

                <c:choose>
                    <c:when test="${empty inProgressChallenges}">
                        <div class="text-center py-4 text-muted small">
                            <i class="bi bi-trophy fs-3 d-block mb-2 text-muted"></i>
                            You are not currently participating in any active challenges.
                            <div class="mt-2">
                                <a href="${pageContext.request.contextPath}/challenges" class="btn btn-sm btn-outline-success">Browse Active Challenges</a>
                            </div>
                        </div>
                    </c:when>
                    <c:otherwise>
                        <div class="row g-3">
                            <c:forEach var="item" items="${inProgressChallenges}">
                                <div class="col-12 col-md-6">
                                    <div class="card border rounded-4 p-3.5 bg-light h-100 d-flex flex-column">
                                        <div class="d-flex justify-content-between align-items-start mb-2">
                                            <h6 class="fw-bold text-dark mb-1">
                                                <a href="${pageContext.request.contextPath}/challenges/view?id=${item.challenge.challengeId}" class="text-dark text-decoration-none">
                                                    <c:out value="${item.challenge.name}"/>
                                                </a>
                                            </h6>
                                            <span class="badge bg-primary small"><c:out value="${item.userProgressPercent}"/>%</span>
                                        </div>

                                        <div class="progress mb-2" style="height: 8px;">
                                            <div class="progress-bar progress-bar-striped progress-bar-animated bg-primary"
                                                 role="progressbar" style="width: ${item.userProgressPercent}%"
                                                 aria-valuenow="${item.userProgressPercent}" aria-valuemin="0" aria-valuemax="100">
                                            </div>
                                        </div>

                                        <div class="d-flex justify-content-between align-items-center small text-muted mb-3">
                                            <span>
                                                Progress: <strong><c:out value="${item.userParticipation.progressValue}"/> / <c:out value="${item.challenge.goalValue}"/> <c:out value="${item.challenge.goalUnit}"/></strong>
                                            </span>
                                            <span><c:out value="${item.daysRemaining}"/> days left</span>
                                        </div>

                                        <div class="mt-auto pt-2 border-top d-flex gap-2 justify-content-between align-items-center">
                                            <form action="${pageContext.request.contextPath}/challenges/sync" method="POST" class="d-inline">
                                                <input type="hidden" name="csrfToken" value="${sessionScope.csrfToken}"/>
                                                <input type="hidden" name="challengeId" value="${item.challenge.challengeId}"/>
                                                <button type="submit" class="btn btn-sm btn-outline-success py-1 px-2.5 small fw-semibold">
                                                    <i class="bi bi-arrow-repeat"></i> Sync Workouts
                                                </button>
                                            </form>
                                            <a href="${pageContext.request.contextPath}/challenges/view?id=${item.challenge.challengeId}" class="btn btn-sm btn-success py-1 px-3 small fw-semibold">
                                                View <i class="bi bi-arrow-right ms-1"></i>
                                            </a>
                                        </div>
                                    </div>
                                </div>
                            </c:forEach>
                        </div>
                    </c:otherwise>
                </c:choose>
            </div>

            <!-- 2. Completed Milestones Section -->
            <div class="card border rounded-4 p-4 bg-white shadow-xs mb-4">
                <div class="d-flex justify-content-between align-items-center pb-3 mb-3 border-bottom">
                    <div class="d-flex align-items-center gap-2">
                        <div class="icon-wrapper m-0 bg-success-subtle text-success" style="width: 36px; height: 36px;">
                            <i class="bi bi-trophy-fill"></i>
                        </div>
                        <h5 class="fw-bold text-dark mb-0">Completed Challenges</h5>
                    </div>
                    <span class="badge bg-success-subtle text-success small fw-semibold px-2.5 py-1">
                        <c:out value="${completedChallenges.size()}"/> Completed
                    </span>
                </div>

                <c:choose>
                    <c:when test="${empty completedChallenges}">
                        <div class="text-center py-4 text-muted small">
                            <i class="bi bi-award fs-3 d-block mb-2 text-muted"></i>
                            No challenges completed yet. Finish active challenges to unlock milestone achievements!
                        </div>
                    </c:when>
                    <c:otherwise>
                        <div class="table-responsive">
                            <table class="table table-hover align-middle mb-0">
                                <thead class="table-light small text-muted">
                                    <tr>
                                        <th>Challenge Name</th>
                                        <th>Target Goal</th>
                                        <th>Points Earned</th>
                                        <th>Completed Date</th>
                                        <th class="text-end">Action</th>
                                    </tr>
                                </thead>
                                <tbody>
                                    <c:forEach var="item" items="${completedChallenges}">
                                        <tr>
                                            <td>
                                                <a href="${pageContext.request.contextPath}/challenges/view?id=${item.challenge.challengeId}" class="fw-bold text-dark text-decoration-none">
                                                    <c:out value="${item.challenge.name}"/>
                                                </a>
                                            </td>
                                            <td><c:out value="${item.challenge.goalValue}"/> <c:out value="${item.challenge.goalUnit}"/></td>
                                            <td>
                                                <span class="badge bg-warning-subtle text-warning-emphasis border border-warning-subtle">
                                                    +<c:out value="${item.challenge.pointsReward}"/> pts
                                                </span>
                                            </td>
                                            <td class="small text-muted"><c:out value="${item.userParticipation.completedAt}"/></td>
                                            <td class="text-end">
                                                <a href="${pageContext.request.contextPath}/challenges/view?id=${item.challenge.challengeId}" class="btn btn-sm btn-outline-secondary py-1 px-3 small">
                                                    Details
                                                </a>
                                            </td>
                                        </tr>
                                    </c:forEach>
                                </tbody>
                            </table>
                        </div>
                    </c:otherwise>
                </c:choose>
            </div>

            <!-- 3. Created Challenges Section -->
            <div class="card border rounded-4 p-4 bg-white shadow-xs">
                <div class="d-flex justify-content-between align-items-center pb-3 mb-3 border-bottom">
                    <div class="d-flex align-items-center gap-2">
                        <div class="icon-wrapper m-0 bg-info-subtle text-info" style="width: 36px; height: 36px;">
                            <i class="bi bi-pencil-square"></i>
                        </div>
                        <h5 class="fw-bold text-dark mb-0">Challenges Created by You</h5>
                    </div>
                    <a href="${pageContext.request.contextPath}/challenges/create" class="btn btn-sm btn-outline-success">
                        <i class="bi bi-plus-lg me-1"></i> Create Another
                    </a>
                </div>

                <c:choose>
                    <c:when test="${empty createdChallenges}">
                        <div class="text-center py-4 text-muted small">
                            <i class="bi bi-folder-plus fs-3 d-block mb-2 text-muted"></i>
                            You haven't created any custom challenges yet.
                        </div>
                    </c:when>
                    <c:otherwise>
                        <div class="table-responsive">
                            <table class="table table-hover align-middle mb-0">
                                <thead class="table-light small text-muted">
                                    <tr>
                                        <th>Name</th>
                                        <th>Target</th>
                                        <th>Timeline</th>
                                        <th>Moderation Status</th>
                                        <th class="text-end">Action</th>
                                    </tr>
                                </thead>
                                <tbody>
                                    <c:forEach var="c" items="${createdChallenges}">
                                        <tr>
                                            <td>
                                                <strong><c:out value="${c.name}"/></strong>
                                            </td>
                                            <td><c:out value="${c.goalValue}"/> <c:out value="${c.goalUnit}"/></td>
                                            <td class="small text-muted"><c:out value="${c.startDate}"/> to <c:out value="${c.endDate}"/></td>
                                            <td>
                                                <span class="badge ${c.statusBadgeClass} text-uppercase small">
                                                    <c:choose>
                                                        <c:when test="${c.status == 'DRAFT'}">Pending Review</c:when>
                                                        <c:otherwise><c:out value="${c.status}"/></c:otherwise>
                                                    </c:choose>
                                                </span>
                                            </td>
                                            <td class="text-end">
                                                <a href="${pageContext.request.contextPath}/challenges/view?id=${c.challengeId}" class="btn btn-sm btn-outline-secondary py-1 px-3 small">
                                                    View
                                                </a>
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
