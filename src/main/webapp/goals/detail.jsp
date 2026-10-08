<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ taglib prefix="c" uri="jakarta.tags.core" %>
<!DOCTYPE html>
<html lang="en">
<head>
    <meta charset="UTF-8">
    <meta name="viewport" content="width=device-width, initial-scale=1.0">
    <title>Goal Details – FitAura</title>
    <meta name="description" content="View and manage details of your fitness goal.">

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
                        <a class="nav-link fw-medium text-dark" href="${pageContext.request.contextPath}/challenges">
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

    <!-- Main Content -->
    <main class="flex-grow-1 py-5">
        <div class="container" style="max-width: 800px;">

            <!-- Breadcrumb Navigation -->
            <nav aria-label="breadcrumb" class="mb-3">
                <ol class="breadcrumb small">
                    <li class="breadcrumb-item"><a href="${pageContext.request.contextPath}/goals" class="text-success text-decoration-none">Goals</a></li>
                    <li class="breadcrumb-item active" aria-current="page"><c:out value="${goal.goalTypeLabel}"/></li>
                </ol>
            </nav>

            <!-- Alerts -->
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

            <!-- Main Goal Card -->
            <div class="card border rounded-4 bg-white p-4 p-md-5 shadow-xs mb-4">
                <div class="d-flex flex-column flex-sm-row justify-content-between align-items-sm-center pb-4 mb-4 border-bottom gap-3">
                    <div>
                        <div class="d-flex align-items-center gap-2 mb-1">
                            <h2 class="fw-bold text-dark mb-0"><c:out value="${goal.goalTypeLabel}"/></h2>
                            <span class="badge 
                                <c:choose>
                                    <c:when test="${goal.status == 'ACTIVE'}">bg-success-subtle text-success</c:when>
                                    <c:when test="${goal.status == 'PAUSED'}">bg-warning-subtle text-warning</c:when>
                                    <c:when test="${goal.status == 'COMPLETED'}">bg-primary-subtle text-primary</c:when>
                                    <c:otherwise>bg-secondary-subtle text-secondary</c:otherwise>
                                </c:choose> fs-6 fw-semibold px-3 py-1">
                                <c:out value="${goal.statusLabel}"/>
                            </span>
                        </div>
                        <p class="text-secondary small mb-0">Goal #<c:out value="${goal.goalId}"/> • Created on <c:out value="${goal.createdAt}"/></p>
                    </div>

                    <!-- Action Buttons -->
                    <div class="d-flex flex-wrap gap-2">
                        <c:if test="${goal.status == 'ACTIVE'}">
                            <a href="${pageContext.request.contextPath}/goals/edit?id=${goal.goalId}" class="btn btn-outline-primary btn-sm px-3 fw-medium">
                                <i class="bi bi-pencil me-1"></i> Edit
                            </a>
                            <form action="${pageContext.request.contextPath}/goals/status" method="post" class="d-inline">
                                <input type="hidden" name="_csrf" value="${sessionScope.csrfToken}">
                                <input type="hidden" name="goalId" value="${goal.goalId}">
                                <input type="hidden" name="status" value="PAUSED">
                                <button type="submit" class="btn btn-outline-warning btn-sm px-3 fw-medium">
                                    <i class="bi bi-pause-fill me-1"></i> Pause
                                </button>
                            </form>
                            <form action="${pageContext.request.contextPath}/goals/status" method="post" class="d-inline">
                                <input type="hidden" name="_csrf" value="${sessionScope.csrfToken}">
                                <input type="hidden" name="goalId" value="${goal.goalId}">
                                <input type="hidden" name="status" value="COMPLETED">
                                <button type="submit" class="btn btn-success btn-sm px-3 fw-semibold shadow-xs">
                                    <i class="bi bi-check-lg me-1"></i> Complete
                                </button>
                            </form>
                        </c:if>

                        <c:if test="${goal.status == 'PAUSED'}">
                            <form action="${pageContext.request.contextPath}/goals/status" method="post" class="d-inline">
                                <input type="hidden" name="_csrf" value="${sessionScope.csrfToken}">
                                <input type="hidden" name="goalId" value="${goal.goalId}">
                                <input type="hidden" name="status" value="ACTIVE">
                                <button type="submit" class="btn btn-success btn-sm px-3 fw-semibold shadow-xs">
                                    <i class="bi bi-play-fill me-1"></i> Resume Goal
                                </button>
                            </form>
                        </c:if>
                    </div>
                </div>

                <!-- Progress Section -->
                <div class="bg-light rounded-4 p-4 mb-4">
                    <div class="d-flex justify-content-between align-items-center mb-2">
                        <span class="fw-bold text-dark">Overall Milestone Progress</span>
                        <span class="fs-4 fw-bold text-success"><c:out value="${goal.progressPercentage}"/>%</span>
                    </div>
                    <div class="progress" style="height: 14px; border-radius: 8px;">
                        <div class="progress-bar 
                            <c:choose>
                                <c:when test="${goal.status == 'COMPLETED'}">bg-primary</c:when>
                                <c:when test="${goal.status == 'PAUSED'}">bg-warning</c:when>
                                <c:when test="${goal.status == 'CANCELLED'}">bg-secondary</c:when>
                                <c:otherwise>bg-success</c:otherwise>
                            </c:choose>"
                             role="progressbar"
                             style="width: ${goal.progressPercentage}%;"
                             aria-valuenow="${goal.progressPercentage}"
                             aria-valuemin="0"
                             aria-valuemax="100">
                        </div>
                    </div>
                </div>

                <!-- Metric Numbers Grid -->
                <div class="row g-3 mb-4">
                    <div class="col-6 col-md-4">
                        <div class="card border rounded-3 p-3 text-center h-100 bg-white shadow-xs">
                            <small class="text-secondary d-block mb-1 fw-medium">Current Value</small>
                            <span class="fs-4 fw-bold text-dark"><c:out value="${goal.currentValue}"/></span>
                            <small class="text-muted"><c:out value="${goal.unit}"/></small>
                        </div>
                    </div>

                    <div class="col-6 col-md-4">
                        <div class="card border rounded-3 p-3 text-center h-100 bg-white shadow-xs">
                            <small class="text-secondary d-block mb-1 fw-medium">Target Value</small>
                            <span class="fs-4 fw-bold text-success"><c:out value="${goal.targetValue}"/></span>
                            <small class="text-muted"><c:out value="${goal.unit}"/></small>
                        </div>
                    </div>

                    <div class="col-12 col-md-4">
                        <div class="card border rounded-3 p-3 text-center h-100 bg-white shadow-xs">
                            <small class="text-secondary d-block mb-1 fw-medium">Start Date</small>
                            <span class="fs-5 fw-bold text-dark"><c:out value="${goal.startDate}"/></span>
                            <c:if test="${not empty goal.targetDate}">
                                <small class="text-muted d-block mt-1">Target: <c:out value="${goal.targetDate}"/></small>
                            </c:if>
                        </div>
                    </div>
                </div>

                <!-- Notes Section -->
                <c:if test="${not empty goal.notes}">
                    <div class="mb-4">
                        <h6 class="fw-bold text-dark mb-2">Notes & Strategy</h6>
                        <div class="p-3 bg-light rounded-3 text-secondary" style="white-space: pre-wrap;"><c:out value="${goal.notes}"/></div>
                    </div>
                </c:if>

                <!-- Footer & Dangerous Actions (Cancel / Delete) -->
                <div class="d-flex flex-wrap align-items-center justify-content-between pt-3 border-top gap-3">
                    <a href="${pageContext.request.contextPath}/goals" class="btn btn-outline-secondary px-3 py-1.5 fw-medium">
                        <i class="bi bi-arrow-left me-1"></i> Back to Goals
                    </a>

                    <div class="d-flex gap-2">
                        <c:if test="${goal.status == 'ACTIVE' || goal.status == 'PAUSED'}">
                            <button type="button" class="btn btn-outline-warning btn-sm px-3 fw-medium" data-bs-toggle="modal" data-bs-target="#cancelGoalModal">
                                <i class="bi bi-slash-circle me-1"></i> Cancel Goal
                            </button>
                        </c:if>

                        <button type="button" class="btn btn-outline-danger btn-sm px-3 fw-medium" data-bs-toggle="modal" data-bs-target="#deleteGoalModal">
                            <i class="bi bi-trash3 me-1"></i> Delete Goal
                        </button>
                    </div>
                </div>
            </div>

        </div>
    </main>

    <!-- Cancel Modal -->
    <c:if test="${goal.status == 'ACTIVE' || goal.status == 'PAUSED'}">
        <div class="modal fade" id="cancelGoalModal" tabindex="-1" aria-labelledby="cancelGoalModalLabel" aria-hidden="true">
            <div class="modal-dialog modal-dialog-centered">
                <div class="modal-content rounded-4 border-0 shadow">
                    <div class="modal-header border-bottom-0 pb-0">
                        <h5 class="modal-title fw-bold text-dark" id="cancelGoalModalLabel">Cancel Fitness Goal</h5>
                        <button type="button" class="btn-close" data-bs-dismiss="modal" aria-label="Close"></button>
                    </div>
                    <div class="modal-body py-3">
                        <p class="text-secondary mb-0">Are you sure you want to cancel this goal? Cancelled goals can no longer be edited or resumed.</p>
                    </div>
                    <div class="modal-footer border-top-0 pt-0">
                        <button type="button" class="btn btn-light px-3 fw-medium" data-bs-dismiss="modal">Keep Goal</button>
                        <form action="${pageContext.request.contextPath}/goals/status" method="post">
                            <input type="hidden" name="_csrf" value="${sessionScope.csrfToken}">
                            <input type="hidden" name="goalId" value="${goal.goalId}">
                            <input type="hidden" name="status" value="CANCELLED">
                            <button type="submit" class="btn btn-warning px-3 fw-semibold">Confirm Cancel</button>
                        </form>
                    </div>
                </div>
            </div>
        </div>
    </c:if>

    <!-- Delete Modal -->
    <div class="modal fade" id="deleteGoalModal" tabindex="-1" aria-labelledby="deleteGoalModalLabel" aria-hidden="true">
        <div class="modal-dialog modal-dialog-centered">
            <div class="modal-content rounded-4 border-0 shadow">
                <div class="modal-header border-bottom-0 pb-0">
                    <h5 class="modal-title fw-bold text-danger" id="deleteGoalModalLabel">Delete Goal Permanently</h5>
                    <button type="button" class="btn-close" data-bs-dismiss="modal" aria-label="Close"></button>
                </div>
                <div class="modal-body py-3">
                    <p class="text-secondary mb-0">Are you sure you want to delete this fitness goal? This action is permanent and cannot be undone.</p>
                </div>
                <div class="modal-footer border-top-0 pt-0">
                    <button type="button" class="btn btn-light px-3 fw-medium" data-bs-dismiss="modal">Cancel</button>
                    <form action="${pageContext.request.contextPath}/goals/delete" method="post">
                        <input type="hidden" name="_csrf" value="${sessionScope.csrfToken}">
                        <input type="hidden" name="goalId" value="${goal.goalId}">
                        <button type="submit" class="btn btn-danger px-4 fw-semibold">Delete Permanently</button>
                    </form>
                </div>
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
