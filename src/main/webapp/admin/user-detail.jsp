<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ taglib prefix="c" uri="jakarta.tags.core" %>
<%@ taglib prefix="fmt" uri="jakarta.tags.fmt" %>
<!DOCTYPE html>
<html lang="en">
<head>
    <meta charset="UTF-8">
    <meta name="viewport" content="width=device-width, initial-scale=1.0">
    <title>Athlete Profile #${userDetail.user.userId} – FitAura Admin</title>

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
                        <a class="nav-link active fw-bold text-success" aria-current="page" href="${pageContext.request.contextPath}/admin/users">
                            <i class="bi bi-people me-1"></i> Users
                        </a>
                    </li>
                    <li class="nav-item">
                        <a class="nav-link text-white-50 hover-text-white" href="${pageContext.request.contextPath}/admin/challenges">
                            <i class="bi bi-trophy me-1"></i> Challenges
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
                    <a href="${pageContext.request.contextPath}/admin/users" class="btn btn-outline-light btn-sm">
                        <i class="bi bi-arrow-left me-1"></i> Back to Users
                    </a>
                </div>
            </div>
        </div>
    </nav>

    <!-- Main Content Container -->
    <main class="container my-4 flex-grow-1">

        <!-- Breadcrumb -->
        <nav aria-label="breadcrumb" class="mb-3">
            <ol class="breadcrumb small">
                <li class="breadcrumb-item"><a href="${pageContext.request.contextPath}/admin/dashboard">Dashboard</a></li>
                <li class="breadcrumb-item"><a href="${pageContext.request.contextPath}/admin/users">Users</a></li>
                <li class="breadcrumb-item active" aria-current="page">User #${userDetail.user.userId}</li>
            </ol>
        </nav>

        <!-- User Header Card -->
        <div class="card border-0 shadow-sm mb-4">
            <div class="card-body p-4">
                <div class="d-flex flex-column flex-md-row justify-content-between align-items-md-center gap-3">
                    <div class="d-flex align-items-center gap-3">
                        <div class="rounded-circle bg-primary-subtle text-primary p-3 d-flex align-items-center justify-content-center" style="width: 64px; height: 64px;">
                            <i class="bi bi-person-fill fs-2"></i>
                        </div>
                        <div>
                            <div class="d-flex align-items-center gap-2">
                                <h3 class="fw-bold mb-0 text-dark"><c:out value="${userDetail.user.displayName}"/></h3>
                                <span class="badge ${userDetail.user.role == 'ADMIN' ? 'bg-danger' : 'bg-secondary'}">${userDetail.user.role}</span>
                                <span class="badge ${userDetail.user.accountStatus == 'ACTIVE' ? 'bg-success' : (userDetail.user.accountStatus == 'BLOCKED' ? 'bg-danger' : 'bg-warning text-dark')}">
                                    ${userDetail.user.accountStatus}
                                </span>
                            </div>
                            <div class="text-muted small mt-1">
                                <span><i class="bi bi-envelope me-1"></i><c:out value="${userDetail.user.email}"/></span>
                                <span class="ms-3"><i class="bi bi-calendar me-1"></i>Registered: <fmt:formatDate value="${userDetail.user.createdAt}" pattern="MMM dd, yyyy" /></span>
                                <span class="ms-3"><i class="bi bi-shield-lock me-1"></i>Privacy: <strong>${userDetail.user.privacyMode}</strong></span>
                            </div>
                        </div>
                    </div>

                    <!-- Moderate Status Actions -->
                    <div class="d-flex gap-2 align-items-center">
                        <c:choose>
                            <c:when test="${userDetail.user.accountStatus == 'ACTIVE'}">
                                <form action="${pageContext.request.contextPath}/admin/users/status" method="POST" class="d-inline" onsubmit="return confirm('Deactivate account for ${userDetail.user.displayName}?');">
                                    <input type="hidden" name="csrfToken" value="${sessionScope.csrfToken}">
                                    <input type="hidden" name="userId" value="${userDetail.user.userId}">
                                    <input type="hidden" name="status" value="INACTIVE">
                                    <input type="hidden" name="returnUrl" value="${pageContext.request.contextPath}/admin/users/view?id=${userDetail.user.userId}">
                                    <button type="submit" class="btn btn-outline-warning">
                                        <i class="bi bi-pause-circle me-1"></i> Deactivate
                                    </button>
                                </form>
                                <form action="${pageContext.request.contextPath}/admin/users/status" method="POST" class="d-inline" onsubmit="return confirm('Block account for ${userDetail.user.displayName}?');">
                                    <input type="hidden" name="csrfToken" value="${sessionScope.csrfToken}">
                                    <input type="hidden" name="userId" value="${userDetail.user.userId}">
                                    <input type="hidden" name="status" value="BLOCKED">
                                    <input type="hidden" name="returnUrl" value="${pageContext.request.contextPath}/admin/users/view?id=${userDetail.user.userId}">
                                    <button type="submit" class="btn btn-outline-danger">
                                        <i class="bi bi-slash-circle me-1"></i> Block Account
                                    </button>
                                </form>
                            </c:when>
                            <c:otherwise>
                                <form action="${pageContext.request.contextPath}/admin/users/status" method="POST" class="d-inline" onsubmit="return confirm('Activate account for ${userDetail.user.displayName}?');">
                                    <input type="hidden" name="csrfToken" value="${sessionScope.csrfToken}">
                                    <input type="hidden" name="userId" value="${userDetail.user.userId}">
                                    <input type="hidden" name="status" value="ACTIVE">
                                    <input type="hidden" name="returnUrl" value="${pageContext.request.contextPath}/admin/users/view?id=${userDetail.user.userId}">
                                    <button type="submit" class="btn btn-success">
                                        <i class="bi bi-check-circle me-1"></i> Activate Account
                                    </button>
                                </form>
                            </c:otherwise>
                        </c:choose>
                    </div>
                </div>
            </div>
        </div>

        <!-- 4 Summary Stats Cards -->
        <div class="row g-3 mb-4">
            <div class="col-sm-6 col-md-3">
                <div class="card border-0 shadow-sm p-3 text-center bg-white h-100">
                    <span class="text-muted small fw-semibold text-uppercase">Recorded Workouts</span>
                    <h3 class="fw-bold text-dark mt-1 mb-0">${userDetail.totalWorkouts}</h3>
                    <span class="text-muted small mt-1">
                        <c:choose>
                            <c:when test="${not empty userDetail.lastWorkoutDate}">
                                Last: <fmt:formatDate value="${userDetail.lastWorkoutDate}" pattern="MMM dd" />
                            </c:when>
                            <c:otherwise>No workouts yet</c:otherwise>
                        </c:choose>
                    </span>
                </div>
            </div>

            <div class="col-sm-6 col-md-3">
                <div class="card border-0 shadow-sm p-3 text-center bg-white h-100">
                    <span class="text-muted small fw-semibold text-uppercase">Fitness Goals</span>
                    <h3 class="fw-bold text-dark mt-1 mb-0">${userDetail.totalGoals}</h3>
                    <span class="text-muted small mt-1">${userDetail.activeGoals} Active</span>
                </div>
            </div>

            <div class="col-sm-6 col-md-3">
                <div class="card border-0 shadow-sm p-3 text-center bg-white h-100">
                    <span class="text-muted small fw-semibold text-uppercase">Challenges</span>
                    <h3 class="fw-bold text-dark mt-1 mb-0">${userDetail.joinedChallenges}</h3>
                    <span class="text-muted small mt-1">${userDetail.completedChallenges} Completed</span>
                </div>
            </div>

            <div class="col-sm-6 col-md-3">
                <div class="card border-0 shadow-sm p-3 text-center bg-white h-100">
                    <span class="text-muted small fw-semibold text-uppercase">Points & Badges</span>
                    <h3 class="fw-bold text-warning-emphasis mt-1 mb-0">${userDetail.totalPoints} <span class="fs-6 text-muted">PTS</span></h3>
                    <span class="text-muted small mt-1">${userDetail.earnedAchievements} Badges</span>
                </div>
            </div>
        </div>

        <!-- Account Metadata & Privacy Details -->
        <div class="card border-0 shadow-sm mb-4">
            <div class="card-header bg-white py-3 border-0">
                <h5 class="fw-bold text-dark mb-0">
                    <i class="bi bi-info-circle text-primary me-2"></i>Account Metadata & Safety Boundaries
                </h5>
            </div>
            <div class="card-body p-4 pt-0">
                <div class="row g-3">
                    <div class="col-md-6">
                        <div class="p-3 bg-light rounded-3">
                            <span class="text-muted small d-block">Full Legal Name</span>
                            <span class="fw-semibold text-dark"><c:out value="${userDetail.user.fullName}"/></span>
                        </div>
                    </div>
                    <div class="col-md-6">
                        <div class="p-3 bg-light rounded-3">
                            <span class="text-muted small d-block">Last Login</span>
                            <span class="fw-semibold text-dark">
                                <c:choose>
                                    <c:when test="${not empty userDetail.user.lastLoginAt}">
                                        <fmt:formatDate value="${userDetail.user.lastLoginAt}" pattern="MMM dd, yyyy HH:mm" />
                                    </c:when>
                                    <c:otherwise>Never logged in</c:otherwise>
                                </c:choose>
                            </span>
                        </div>
                    </div>
                    <div class="col-12">
                        <div class="p-3 bg-light rounded-3 border-start border-4 border-info">
                            <div class="d-flex align-items-center gap-2">
                                <i class="bi bi-shield-check text-info fs-4"></i>
                                <div>
                                    <strong class="text-dark">Privacy & Healthcare Data Boundary</strong>
                                    <p class="mb-0 small text-muted">
                                        Personal health metrics, biometrics, calorie intake logs, and private journal notes are strictly isolated and not exposed on administrative screens under the principle of least privilege.
                                    </p>
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
        <div class="container d-flex justify-content-between align-items-center small text-muted">
            <div>&copy; 2026 FitAura Administration Console</div>
            <span class="badge bg-success-subtle text-success">Secure ADMIN Session</span>
        </div>
    </footer>

    <!-- Bootstrap 5 JS Bundle via CDN -->
    <script src="https://cdn.jsdelivr.net/npm/bootstrap@5.3.3/dist/js/bootstrap.bundle.min.js" integrity="sha384-YvpcrYf0tY3lHB60NNkmXc5s9fDVZLESaAA55NDzOxhy9GkcIdslK1eN7N6jIeHz" crossorigin="anonymous"></script>
</body>
</html>
