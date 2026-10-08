<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ taglib prefix="c" uri="jakarta.tags.core" %>
<!DOCTYPE html>
<html lang="en">
<head>
    <meta charset="UTF-8">
    <meta name="viewport" content="width=device-width, initial-scale=1.0">
    <title>Workout Details – FitAura</title>
    <meta name="description" content="View comprehensive details of your recorded workout session.">

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

            <button class="navbar-toggler" type="button" data-bs-toggle="collapse" data-bs-target="#workoutNavbar" aria-controls="workoutNavbar" aria-expanded="false" aria-label="Toggle navigation">
                <span class="navbar-toggler-icon"></span>
            </button>

            <div class="collapse navbar-collapse" id="workoutNavbar">
                <ul class="navbar-nav me-auto mb-2 mb-lg-0 ms-lg-4">
                    <li class="nav-item">
                        <a class="nav-link fw-medium text-dark" href="${pageContext.request.contextPath}/user/dashboard">
                            <i class="bi bi-house-door me-1"></i> Dashboard
                        </a>
                    </li>
                    <li class="nav-item">
                        <a class="nav-link active fw-bold text-success" aria-current="page" href="${pageContext.request.contextPath}/workouts">
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

    <!-- Main Content Area -->
    <main class="flex-grow-1 py-5">
        <div class="container" style="max-width: 800px;">

            <!-- Breadcrumb Navigation -->
            <nav aria-label="breadcrumb" class="mb-3">
                <ol class="breadcrumb small">
                    <li class="breadcrumb-item"><a href="${pageContext.request.contextPath}/workouts" class="text-success text-decoration-none">Workouts</a></li>
                    <li class="breadcrumb-item active" aria-current="page">Session Details</li>
                </ol>
            </nav>

            <!-- Feedback Alerts (Session Flash Messages) -->
            <c:if test="${not empty sessionScope.flashSuccess}">
                <div class="alert alert-success alert-dismissible fade show d-flex align-items-center p-3 rounded-4 shadow-sm mb-4" role="alert">
                    <i class="bi bi-check-circle-fill fs-5 me-2 text-success"></i>
                    <div><c:out value="${sessionScope.flashSuccess}"/></div>
                    <button type="button" class="btn-close" data-bs-dismiss="alert" aria-label="Close"></button>
                </div>
                <c:remove var="flashSuccess" scope="session"/>
            </c:if>

            <!-- Workout Details Card -->
            <div class="card border rounded-4 p-4 p-md-5 bg-white shadow-xs mb-4">
                <!-- Activity Header -->
                <div class="d-flex flex-column flex-sm-row justify-content-between align-items-sm-center pb-4 mb-4 border-bottom gap-3">
                    <div class="d-flex align-items-center gap-3">
                        <div class="icon-wrapper m-0 
                            <c:choose>
                                <c:when test="${workout.workoutType == 'RUNNING'}">bg-primary-subtle text-primary</c:when>
                                <c:when test="${workout.workoutType == 'CYCLING'}">bg-info-subtle text-info</c:when>
                                <c:when test="${workout.workoutType == 'STRENGTH'}">bg-danger-subtle text-danger</c:when>
                                <c:when test="${workout.workoutType == 'HOME_WORKOUT'}">bg-purple-subtle text-dark</c:when>
                                <c:otherwise>bg-success-subtle text-success</c:otherwise>
                            </c:choose>">
                            <c:choose>
                                <c:when test="${workout.workoutType == 'RUNNING'}"><i class="bi bi-person-walking fs-3"></i></c:when>
                                <c:when test="${workout.workoutType == 'CYCLING'}"><i class="bi bi-bicycle fs-3"></i></c:when>
                                <c:when test="${workout.workoutType == 'STRENGTH'}"><i class="bi bi-trophy fs-3"></i></c:when>
                                <c:when test="${workout.workoutType == 'HOME_WORKOUT'}"><i class="bi bi-house-heart fs-3"></i></c:when>
                                <c:otherwise><i class="bi bi-activity fs-3"></i></c:otherwise>
                            </c:choose>
                        </div>
                        <div>
                            <h3 class="fw-bold text-dark mb-0"><c:out value="${workout.workoutTypeLabel}"/></h3>
                            <span class="text-secondary small">
                                <i class="bi bi-calendar-check me-1"></i>Performed on <c:out value="${workout.workoutDate}"/>
                            </span>
                        </div>
                    </div>

                    <div>
                        <span class="badge 
                            <c:choose>
                                <c:when test="${workout.intensity == 'HIGH'}">bg-danger-subtle text-danger</c:when>
                                <c:when test="${workout.intensity == 'MEDIUM'}">bg-warning-subtle text-warning</c:when>
                                <c:otherwise>bg-success-subtle text-success</c:otherwise>
                            </c:choose> fs-6 px-3 py-2 fw-semibold">
                            <c:out value="${workout.intensityLabel}"/> Intensity
                        </span>
                    </div>
                </div>

                <!-- Primary Metric Badges -->
                <div class="row g-3 mb-4">
                    <div class="col-6 col-sm-4">
                        <div class="p-3 bg-light rounded-3 text-center">
                            <small class="text-secondary d-block mb-1">Duration</small>
                            <span class="fs-4 fw-bold text-dark"><c:out value="${workout.durationMinutes}"/></span>
                            <small class="text-muted ms-1">minutes</small>
                        </div>
                    </div>
                    <div class="col-6 col-sm-4">
                        <div class="p-3 bg-light rounded-3 text-center">
                            <small class="text-secondary d-block mb-1">Calories Burned</small>
                            <span class="fs-4 fw-bold text-dark">
                                <c:choose>
                                    <c:when test="${workout.caloriesBurned > 0}">
                                        <c:out value="${workout.caloriesBurned}"/>
                                    </c:when>
                                    <c:otherwise>—</c:otherwise>
                                </c:choose>
                            </span>
                            <small class="text-muted ms-1">${workout.caloriesBurned > 0 ? 'kcal' : ''}</small>
                        </div>
                    </div>
                    <div class="col-12 col-sm-4">
                        <div class="p-3 bg-light rounded-3 text-center">
                            <small class="text-secondary d-block mb-1">Effort Level</small>
                            <span class="fs-5 fw-bold text-dark"><c:out value="${workout.intensityLabel}"/></span>
                        </div>
                    </div>
                </div>

                <!-- Notes Section -->
                <div class="mb-4">
                    <h6 class="fw-bold text-dark mb-2">Session Notes</h6>
                    <div class="p-3 bg-light rounded-3">
                        <c:choose>
                            <c:when test="${not empty workout.notes}">
                                <p class="mb-0 text-dark small text-break"><c:out value="${workout.notes}"/></p>
                            </c:when>
                            <c:otherwise>
                                <span class="text-muted small fst-italic">No additional notes were recorded for this session.</span>
                            </c:otherwise>
                        </c:choose>
                    </div>
                </div>

                <!-- Audit Timestamps -->
                <div class="row g-2 pt-3 border-top small text-secondary">
                    <div class="col-sm-6">
                        <span>Logged:</span>
                        <strong class="text-dark ms-1"><c:out value="${workout.createdAt}"/></strong>
                    </div>
                    <c:if test="${not empty workout.updatedAt}">
                        <div class="col-sm-6 text-sm-end">
                            <span>Last Updated:</span>
                            <strong class="text-dark ms-1"><c:out value="${workout.updatedAt}"/></strong>
                        </div>
                    </c:if>
                </div>
            </div>

            <!-- Action Toolbar -->
            <div class="d-flex flex-column flex-sm-row justify-content-between align-items-center gap-3">
                <a href="${pageContext.request.contextPath}/workouts" class="btn btn-outline-secondary px-4 fw-medium order-2 order-sm-1">
                    <i class="bi bi-arrow-left me-1"></i> Back to History
                </a>
                <div class="d-flex gap-2 order-1 order-sm-2 w-100 w-sm-auto justify-content-end">
                    <a href="${pageContext.request.contextPath}/workouts/edit?id=${workout.workoutId}"
                       class="btn btn-outline-primary px-3 fw-medium">
                        <i class="bi bi-pencil me-1"></i> Edit Workout
                    </a>
                    <button type="button" class="btn btn-outline-danger px-3 fw-medium"
                            data-bs-toggle="modal" data-bs-target="#deleteDetailModal">
                        <i class="bi bi-trash me-1"></i> Delete
                    </button>
                </div>
            </div>

        </div>
    </main>

    <!-- Delete Modal -->
    <div class="modal fade" id="deleteDetailModal" tabindex="-1" aria-labelledby="deleteDetailModalLabel" aria-hidden="true">
        <div class="modal-dialog modal-dialog-centered">
            <div class="modal-content border-0 rounded-4 shadow">
                <div class="modal-header border-0 pb-0">
                    <h5 class="modal-title fw-bold text-dark" id="deleteDetailModalLabel">Confirm Deletion</h5>
                    <button type="button" class="btn-close" data-bs-dismiss="modal" aria-label="Close"></button>
                </div>
                <div class="modal-body py-3">
                    <p class="text-secondary mb-2">Are you sure you want to permanently delete this workout?</p>
                    <div class="p-3 bg-light rounded-3">
                        <strong class="text-dark"><c:out value="${workout.workoutTypeLabel}"/> on <c:out value="${workout.workoutDate}"/></strong>
                    </div>
                    <small class="text-muted d-block mt-2">This action cannot be undone.</small>
                </div>
                <div class="modal-footer border-0 pt-0">
                    <button type="button" class="btn btn-outline-secondary px-3" data-bs-dismiss="modal">Cancel</button>
                    <form action="${pageContext.request.contextPath}/workouts/delete" method="post" class="d-inline">
                        <input type="hidden" name="_csrf" value="${sessionScope.csrfToken}">
                        <input type="hidden" name="workoutId" value="${workout.workoutId}">
                        <button type="submit" class="btn btn-danger px-4 fw-semibold">
                            <i class="bi bi-trash me-1"></i> Delete Workout
                        </button>
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
