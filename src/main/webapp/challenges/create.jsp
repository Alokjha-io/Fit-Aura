<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ taglib prefix="c" uri="jakarta.tags.core" %>
<!DOCTYPE html>
<html lang="en">
<head>
    <meta charset="UTF-8">
    <meta name="viewport" content="width=device-width, initial-scale=1.0">
    <title>Create Challenge – FitAura</title>
    <meta name="description" content="Create a new community fitness challenge on FitAura.">

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
        <div class="container" style="max-width: 760px;">

            <!-- Breadcrumb Navigation -->
            <nav aria-label="breadcrumb" class="mb-4">
                <ol class="breadcrumb">
                    <li class="breadcrumb-item"><a href="${pageContext.request.contextPath}/challenges" class="text-decoration-none text-success">Challenges</a></li>
                    <li class="breadcrumb-item active" aria-current="page">Create Challenge</li>
                </ol>
            </nav>

            <!-- Error Banner -->
            <c:if test="${not empty errorMessage}">
                <div class="alert alert-danger alert-dismissible fade show rounded-3 mb-4 shadow-xs" role="alert">
                    <i class="bi bi-exclamation-triangle-fill me-2"></i>
                    <c:out value="${errorMessage}"/>
                    <button type="button" class="btn-close" data-bs-dismiss="alert" aria-label="Close"></button>
                </div>
            </c:if>

            <div class="card border rounded-4 bg-white shadow-xs p-4 p-md-5">
                <div class="d-flex align-items-center gap-3 mb-4 pb-3 border-bottom">
                    <div class="icon-wrapper m-0 bg-success-subtle text-success">
                        <i class="bi bi-trophy-fill fs-4"></i>
                    </div>
                    <div>
                        <h3 class="fw-bold text-dark mb-0">Create Fitness Challenge</h3>
                        <p class="text-secondary small mb-0">Launch a motivating milestone for yourself and fellow FitAura athletes.</p>
                    </div>
                </div>

                <!-- Moderation Notice -->
                <c:choose>
                    <c:when test="${sessionScope.authenticatedRole == 'ADMIN'}">
                        <div class="alert alert-info rounded-3 small mb-4">
                            <i class="bi bi-shield-check me-1"></i> <strong>Admin Privilege:</strong> Challenges created by administrators are immediately published and marked as <strong>ACTIVE</strong>.
                        </div>
                    </c:when>
                    <c:otherwise>
                        <div class="alert alert-warning rounded-3 small mb-4">
                            <i class="bi bi-clock-history me-1"></i> <strong>Community Moderation:</strong> User-created challenges undergo review by administrators before becoming discoverable to the community.
                        </div>
                    </c:otherwise>
                </c:choose>

                <!-- Creation Form -->
                <form action="${pageContext.request.contextPath}/challenges/create" method="POST">
                    <input type="hidden" name="csrfToken" value="${sessionScope.csrfToken}"/>

                    <!-- Challenge Name -->
                    <div class="mb-3">
                        <label for="name" class="form-label fw-semibold small text-dark">
                            Challenge Name <span class="text-danger">*</span>
                        </label>
                        <input type="text" class="form-control rounded-3" id="name" name="name"
                               placeholder="e.g. October 100km Running Odyssey"
                               value="<c:out value='${challenge.name}'/>" maxlength="120" required>
                        <div class="form-text">Between 3 and 120 characters.</div>
                    </div>

                    <!-- Description -->
                    <div class="mb-3">
                        <label for="description" class="form-label fw-semibold small text-dark">
                            Description & Guidelines
                        </label>
                        <textarea class="form-control rounded-3" id="description" name="description" rows="3"
                                  placeholder="Describe the challenge rules, motivation, or target workouts..."
                                  maxlength="1000"><c:out value="${challenge.description}"/></textarea>
                    </div>

                    <!-- Goal Value & Unit -->
                    <div class="row g-3 mb-3">
                        <div class="col-12 col-md-6">
                            <label for="goalValue" class="form-label fw-semibold small text-dark">
                                Target Value <span class="text-danger">*</span>
                            </label>
                            <input type="number" step="0.01" min="0.01" class="form-control rounded-3" id="goalValue" name="goalValue"
                                   placeholder="e.g. 500" value="<c:out value='${challenge.goalValue}'/>" required>
                        </div>
                        <div class="col-12 col-md-6">
                            <label for="goalUnit" class="form-label fw-semibold small text-dark">
                                Goal Unit <span class="text-danger">*</span>
                            </label>
                            <input type="text" class="form-control rounded-3" id="goalUnit" name="goalUnit"
                                   placeholder="e.g. MINUTES, CALORIES, WORKOUTS, KM"
                                   value="<c:out value='${challenge.goalUnit != null ? challenge.goalUnit : \"MINUTES\"}'/>"
                                   maxlength="30" required>
                            <div class="d-flex flex-wrap gap-1 mt-1">
                                <span class="badge bg-light text-dark border cursor-pointer" onclick="document.getElementById('goalUnit').value='MINUTES'">MINUTES</span>
                                <span class="badge bg-light text-dark border cursor-pointer" onclick="document.getElementById('goalUnit').value='CALORIES'">CALORIES</span>
                                <span class="badge bg-light text-dark border cursor-pointer" onclick="document.getElementById('goalUnit').value='WORKOUTS'">WORKOUTS</span>
                                <span class="badge bg-light text-dark border cursor-pointer" onclick="document.getElementById('goalUnit').value='KM'">KM</span>
                            </div>
                        </div>
                    </div>

                    <!-- Timeline Dates -->
                    <div class="row g-3 mb-3">
                        <div class="col-12 col-md-6">
                            <label for="startDate" class="form-label fw-semibold small text-dark">
                                Start Date <span class="text-danger">*</span>
                            </label>
                            <input type="date" class="form-control rounded-3" id="startDate" name="startDate"
                                   value="<c:out value='${challenge.startDate != null ? challenge.startDate : defaultStartDate}'/>" required>
                        </div>
                        <div class="col-12 col-md-6">
                            <label for="endDate" class="form-label fw-semibold small text-dark">
                                End Date <span class="text-danger">*</span>
                            </label>
                            <input type="date" class="form-control rounded-3" id="endDate" name="endDate"
                                   value="<c:out value='${challenge.endDate != null ? challenge.endDate : defaultEndDate}'/>" required>
                        </div>
                    </div>

                    <!-- Points Reward -->
                    <div class="mb-4">
                        <label for="pointsReward" class="form-label fw-semibold small text-dark">
                            Points Reward
                        </label>
                        <input type="number" min="0" max="10000" class="form-control rounded-3" id="pointsReward" name="pointsReward"
                               placeholder="e.g. 50" value="<c:out value='${challenge.pointsReward != null ? challenge.pointsReward : 50}'/>">
                        <div class="form-text">Points earned upon reaching 100% completion (0 to 10,000 pts).</div>
                    </div>

                    <!-- Actions -->
                    <div class="d-flex justify-content-end gap-2 pt-3 border-top">
                        <a href="${pageContext.request.contextPath}/challenges" class="btn btn-outline-secondary px-4 py-2 fw-semibold rounded-3">
                            Cancel
                        </a>
                        <button type="submit" class="btn btn-success px-4 py-2 fw-semibold rounded-3 shadow-xs">
                            <i class="bi bi-check-lg me-1"></i> Submit Challenge
                        </button>
                    </div>
                </form>
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
