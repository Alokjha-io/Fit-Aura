<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ taglib prefix="c" uri="jakarta.tags.core" %>
<!DOCTYPE html>
<html lang="en">
<head>
    <meta charset="UTF-8">
    <meta name="viewport" content="width=device-width, initial-scale=1.0">
    <title>Edit Workout – FitAura</title>
    <meta name="description" content="Update details and metrics for your recorded workout session.">

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
        <div class="container" style="max-width: 760px;">

            <!-- Breadcrumb Navigation -->
            <nav aria-label="breadcrumb" class="mb-3">
                <ol class="breadcrumb small">
                    <li class="breadcrumb-item"><a href="${pageContext.request.contextPath}/workouts" class="text-success text-decoration-none">Workouts</a></li>
                    <li class="breadcrumb-item"><a href="${pageContext.request.contextPath}/workouts/view?id=${workout.workoutId}" class="text-success text-decoration-none">Session Details</a></li>
                    <li class="breadcrumb-item active" aria-current="page">Edit</li>
                </ol>
            </nav>

            <!-- Card Header -->
            <div class="mb-4">
                <h2 class="fw-bold text-dark mb-1">Edit Workout</h2>
                <p class="text-secondary small mb-0">Modify information for your recorded exercise session.</p>
            </div>

            <!-- Error Alerts -->
            <c:if test="${not empty errorMessage}">
                <div class="alert alert-danger alert-dismissible fade show d-flex align-items-center p-3 rounded-4 shadow-sm mb-4" role="alert">
                    <i class="bi bi-exclamation-triangle-fill fs-5 me-2 text-danger"></i>
                    <div><c:out value="${errorMessage}"/></div>
                    <button type="button" class="btn-close" data-bs-dismiss="alert" aria-label="Close"></button>
                </div>
            </c:if>

            <!-- Form Card -->
            <div class="card border rounded-4 p-4 p-md-5 bg-white shadow-xs">
                <form action="${pageContext.request.contextPath}/workouts/edit" method="post" autocomplete="off">
                    <input type="hidden" name="_csrf" value="${sessionScope.csrfToken}">
                    <input type="hidden" name="workoutId" value="${workout.workoutId}">

                    <!-- Choose values (override if validation error occurred, otherwise existing model) -->
                    <c:set var="valType" value="${not empty overrideType ? overrideType : workout.workoutType}" />
                    <c:set var="valDate" value="${not empty overrideDate ? overrideDate : workout.workoutDate}" />
                    <c:set var="valDuration" value="${not empty overrideDuration ? overrideDuration : workout.durationMinutes}" />
                    <c:set var="valIntensity" value="${not empty overrideIntensity ? overrideIntensity : workout.intensity}" />
                    <c:set var="valCalories" value="${not empty overrideCalories ? overrideCalories : workout.caloriesBurned}" />
                    <c:set var="valNotes" value="${not empty overrideNotes ? overrideNotes : workout.notes}" />

                    <div class="row g-4">
                        <!-- Workout Type -->
                        <div class="col-md-6">
                            <label for="workoutType" class="form-label small fw-semibold text-secondary">
                                Activity Type <span class="text-danger">*</span>
                            </label>
                            <select class="form-select" id="workoutType" name="workoutType" required autofocus>
                                <option value="RUNNING" <c:if test="${valType == 'RUNNING'}">selected</c:if>>Running</option>
                                <option value="WALKING" <c:if test="${valType == 'WALKING'}">selected</c:if>>Walking</option>
                                <option value="CYCLING" <c:if test="${valType == 'CYCLING'}">selected</c:if>>Cycling</option>
                                <option value="STRENGTH" <c:if test="${valType == 'STRENGTH'}">selected</c:if>>Strength Training</option>
                                <option value="HOME_WORKOUT" <c:if test="${valType == 'HOME_WORKOUT'}">selected</c:if>>Home Workout</option>
                            </select>
                        </div>

                        <!-- Workout Date -->
                        <div class="col-md-6">
                            <label for="workoutDate" class="form-label small fw-semibold text-secondary">
                                Date Performed <span class="text-danger">*</span>
                            </label>
                            <input type="date" class="form-control" id="workoutDate" name="workoutDate"
                                   value="<c:out value="${valDate}"/>" required>
                        </div>

                        <!-- Duration (Minutes) -->
                        <div class="col-md-6">
                            <label for="durationMinutes" class="form-label small fw-semibold text-secondary">
                                Duration (Minutes) <span class="text-danger">*</span>
                            </label>
                            <div class="input-group">
                                <input type="number" class="form-control" id="durationMinutes" name="durationMinutes"
                                       value="<c:out value="${valDuration}"/>" min="1" max="1440" required>
                                <span class="input-group-text bg-light text-muted">min</span>
                            </div>
                        </div>

                        <!-- Intensity -->
                        <div class="col-md-6">
                            <label for="intensity" class="form-label small fw-semibold text-secondary">
                                Perceived Intensity <span class="text-danger">*</span>
                            </label>
                            <select class="form-select" id="intensity" name="intensity" required>
                                <option value="LOW" <c:if test="${valIntensity == 'LOW'}">selected</c:if>>Low</option>
                                <option value="MEDIUM" <c:if test="${valIntensity == 'MEDIUM'}">selected</c:if>>Medium</option>
                                <option value="HIGH" <c:if test="${valIntensity == 'HIGH'}">selected</c:if>>High</option>
                            </select>
                        </div>

                        <!-- Calories Burned -->
                        <div class="col-md-6">
                            <label for="caloriesBurned" class="form-label small fw-semibold text-secondary">
                                Calories Burned (Estimated)
                            </label>
                            <div class="input-group">
                                <input type="number" class="form-control" id="caloriesBurned" name="caloriesBurned"
                                       value="<c:out value="${valCalories}"/>" min="0" max="20000">
                                <span class="input-group-text bg-light text-muted">kcal</span>
                            </div>
                        </div>

                        <!-- Notes -->
                        <div class="col-12">
                            <label for="notes" class="form-label small fw-semibold text-secondary">
                                Session Notes / Reflections
                            </label>
                            <textarea class="form-control" id="notes" name="notes" rows="4" maxlength="1000"><c:out value="${valNotes}"/></textarea>
                            <div class="form-text small text-muted">Optional notes (max 1000 characters).</div>
                        </div>
                    </div>

                    <!-- Action Buttons -->
                    <div class="mt-5 pt-3 border-top d-flex justify-content-between align-items-center">
                        <a href="${pageContext.request.contextPath}/workouts/view?id=${workout.workoutId}" class="btn btn-outline-secondary px-4 fw-medium">
                            Cancel
                        </a>
                        <button type="submit" class="btn btn-success px-4 py-2 fw-semibold shadow-xs">
                            <i class="bi bi-check2-circle me-1"></i> Update Workout
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
