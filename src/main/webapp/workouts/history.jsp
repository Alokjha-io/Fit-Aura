<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ taglib prefix="c" uri="jakarta.tags.core" %>
<!DOCTYPE html>
<html lang="en">
<head>
    <meta charset="UTF-8">
    <meta name="viewport" content="width=device-width, initial-scale=1.0">
    <title>Workout History – FitAura</title>
    <meta name="description" content="View, filter, and track all your logged workout sessions.">

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
                    <h2 class="fw-bold text-dark mb-1">Workout History</h2>
                    <p class="text-secondary small mb-0">
                        <c:choose>
                            <c:when test="${totalCount > 0}">
                                Tracking <strong>${totalCount}</strong> recorded workout session<c:if test="${totalCount != 1}">s</c:if>.
                            </c:when>
                            <c:otherwise>
                                Your personal activity log is ready for your first entry.
                            </c:otherwise>
                        </c:choose>
                    </p>
                </div>
                <a href="${pageContext.request.contextPath}/workouts/add" class="btn btn-success px-4 py-2 fw-semibold rounded-3 shadow-xs">
                    <i class="bi bi-plus-lg me-1"></i> Log Workout
                </a>
            </div>

            <!-- Feedback Alerts (Session Flash Messages) -->
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

            <!-- Filter & Sort Bar Card -->
            <div class="card border rounded-4 p-4 bg-white mb-4 shadow-xs">
                <form action="${pageContext.request.contextPath}/workouts" method="get" class="row g-3 align-items-end">
                    <!-- Type Filter -->
                    <div class="col-6 col-md-3">
                        <label for="type" class="form-label small fw-semibold text-secondary">Activity Type</label>
                        <select class="form-select form-select-sm" id="type" name="type">
                            <option value="">All Activities</option>
                            <option value="RUNNING" <c:if test="${typeFilter == 'RUNNING'}">selected</c:if>>Running</option>
                            <option value="WALKING" <c:if test="${typeFilter == 'WALKING'}">selected</c:if>>Walking</option>
                            <option value="CYCLING" <c:if test="${typeFilter == 'CYCLING'}">selected</c:if>>Cycling</option>
                            <option value="STRENGTH" <c:if test="${typeFilter == 'STRENGTH'}">selected</c:if>>Strength Training</option>
                            <option value="HOME_WORKOUT" <c:if test="${typeFilter == 'HOME_WORKOUT'}">selected</c:if>>Home Workout</option>
                        </select>
                    </div>

                    <!-- Intensity Filter -->
                    <div class="col-6 col-md-2">
                        <label for="intensity" class="form-label small fw-semibold text-secondary">Intensity</label>
                        <select class="form-select form-select-sm" id="intensity" name="intensity">
                            <option value="">All Levels</option>
                            <option value="LOW" <c:if test="${intensityFilter == 'LOW'}">selected</c:if>>Low</option>
                            <option value="MEDIUM" <c:if test="${intensityFilter == 'MEDIUM'}">selected</c:if>>Medium</option>
                            <option value="HIGH" <c:if test="${intensityFilter == 'HIGH'}">selected</c:if>>High</option>
                        </select>
                    </div>

                    <!-- Sort Filter -->
                    <div class="col-6 col-md-3">
                        <label for="sortBy" class="form-label small fw-semibold text-secondary">Sort By</label>
                        <select class="form-select form-select-sm" id="sortBy" name="sortBy">
                            <option value="date_desc" <c:if test="${sortBy == 'date_desc'}">selected</c:if>>Newest First</option>
                            <option value="date_asc" <c:if test="${sortBy == 'date_asc'}">selected</c:if>>Oldest First</option>
                            <option value="duration_desc" <c:if test="${sortBy == 'duration_desc'}">selected</c:if>>Longest Duration</option>
                            <option value="calories_desc" <c:if test="${sortBy == 'calories_desc'}">selected</c:if>>Highest Calories</option>
                        </select>
                    </div>

                    <!-- Action Buttons -->
                    <div class="col-6 col-md-4 d-flex gap-2">
                        <button type="submit" class="btn btn-outline-success btn-sm flex-grow-1 fw-semibold">
                            <i class="bi bi-funnel me-1"></i> Apply
                        </button>
                        <c:if test="${isFiltered}">
                            <a href="${pageContext.request.contextPath}/workouts" class="btn btn-outline-secondary btn-sm" title="Clear all filters">
                                <i class="bi bi-arrow-counterclockwise"></i> Reset
                            </a>
                        </c:if>
                    </div>
                </form>
            </div>

            <!-- Workout List or Empty State -->
            <c:choose>
                <c:when test="${empty workouts}">
                    <!-- Empty State -->
                    <div class="card border rounded-4 p-5 bg-white text-center shadow-xs">
                        <div class="icon-wrapper mx-auto mb-3 bg-success-subtle text-success" style="width: 64px; height: 64px;">
                            <i class="bi bi-activity fs-2"></i>
                        </div>
                        <h4 class="fw-bold text-dark mb-2">No workouts found</h4>
                        <c:choose>
                            <c:when test="${isFiltered}">
                                <p class="text-secondary small mb-4">No logged workouts match the selected filters. Try clearing or expanding your criteria.</p>
                                <div>
                                    <a href="${pageContext.request.contextPath}/workouts" class="btn btn-outline-secondary px-4 fw-medium">
                                        Clear Filters
                                    </a>
                                </div>
                            </c:when>
                            <c:otherwise>
                                <p class="text-secondary small mb-4">Start tracking your fitness journey by logging your first workout.</p>
                                <div>
                                    <a href="${pageContext.request.contextPath}/workouts/add" class="btn btn-success px-4 py-2 fw-semibold rounded-3 shadow-xs">
                                        <i class="bi bi-plus-lg me-1"></i> Add Workout
                                    </a>
                                </div>
                            </c:otherwise>
                        </c:choose>
                    </div>
                </c:when>
                <c:otherwise>
                    <!-- Workouts List (Responsive cards) -->
                    <div class="row g-3 mb-4">
                        <c:forEach var="w" items="${workouts}">
                            <div class="col-12">
                                <div class="card border rounded-4 p-3 p-md-4 bg-white shadow-xs hover-shadow transition-all">
                                    <div class="row align-items-center g-3">
                                        <!-- Left: Activity Icon & Info -->
                                        <div class="col-12 col-md-5 d-flex align-items-center gap-3">
                                            <div class="icon-wrapper m-0 
                                                <c:choose>
                                                    <c:when test="${w.workoutType == 'RUNNING'}">bg-primary-subtle text-primary</c:when>
                                                    <c:when test="${w.workoutType == 'CYCLING'}">bg-info-subtle text-info</c:when>
                                                    <c:when test="${w.workoutType == 'STRENGTH'}">bg-danger-subtle text-danger</c:when>
                                                    <c:when test="${w.workoutType == 'HOME_WORKOUT'}">bg-purple-subtle text-dark</c:when>
                                                    <c:otherwise>bg-success-subtle text-success</c:otherwise>
                                                </c:choose>">
                                                <c:choose>
                                                    <c:when test="${w.workoutType == 'RUNNING'}"><i class="bi bi-person-walking fs-4"></i></c:when>
                                                    <c:when test="${w.workoutType == 'CYCLING'}"><i class="bi bi-bicycle fs-4"></i></c:when>
                                                    <c:when test="${w.workoutType == 'STRENGTH'}"><i class="bi bi-trophy fs-4"></i></c:when>
                                                    <c:when test="${w.workoutType == 'HOME_WORKOUT'}"><i class="bi bi-house-heart fs-4"></i></c:when>
                                                    <c:otherwise><i class="bi bi-activity fs-4"></i></c:otherwise>
                                                </c:choose>
                                            </div>
                                            <div>
                                                <div class="d-flex align-items-center gap-2">
                                                    <h5 class="fw-bold text-dark mb-0"><c:out value="${w.workoutTypeLabel}"/></h5>
                                                    <span class="badge 
                                                        <c:choose>
                                                            <c:when test="${w.intensity == 'HIGH'}">bg-danger-subtle text-danger</c:when>
                                                            <c:when test="${w.intensity == 'MEDIUM'}">bg-warning-subtle text-warning</c:when>
                                                            <c:otherwise>bg-success-subtle text-success</c:otherwise>
                                                        </c:choose> small fw-semibold">
                                                        <c:out value="${w.intensityLabel}"/>
                                                    </span>
                                                </div>
                                                <small class="text-secondary">
                                                    <i class="bi bi-calendar-event me-1"></i><c:out value="${w.workoutDate}"/>
                                                </small>
                                            </div>
                                        </div>

                                        <!-- Middle: Key Metrics -->
                                        <div class="col-6 col-md-3 d-flex align-items-center gap-4">
                                            <div>
                                                <small class="text-secondary d-block">Duration</small>
                                                <strong class="text-dark fs-6"><c:out value="${w.durationMinutes}"/> min</strong>
                                            </div>
                                            <div>
                                                <small class="text-secondary d-block">Energy</small>
                                                <strong class="text-dark fs-6">
                                                    <c:choose>
                                                        <c:when test="${w.caloriesBurned > 0}">
                                                            <c:out value="${w.caloriesBurned}"/> kcal
                                                        </c:when>
                                                        <c:otherwise>—</c:otherwise>
                                                    </c:choose>
                                                </strong>
                                            </div>
                                        </div>

                                        <!-- Right: Action Buttons -->
                                        <div class="col-6 col-md-4 text-end d-flex justify-content-end align-items-center gap-2">
                                            <a href="${pageContext.request.contextPath}/workouts/view?id=${w.workoutId}"
                                               class="btn btn-outline-secondary btn-sm px-2.5 py-1" title="View Details">
                                                <i class="bi bi-eye"></i> <span class="d-none d-lg-inline ms-1">View</span>
                                            </a>
                                            <a href="${pageContext.request.contextPath}/workouts/edit?id=${w.workoutId}"
                                               class="btn btn-outline-primary btn-sm px-2.5 py-1" title="Edit Workout">
                                                <i class="bi bi-pencil"></i> <span class="d-none d-lg-inline ms-1">Edit</span>
                                            </a>
                                            <button type="button" class="btn btn-outline-danger btn-sm px-2.5 py-1"
                                                    title="Delete Workout"
                                                    data-bs-toggle="modal"
                                                    data-bs-target="#deleteModal"
                                                    data-workout-id="${w.workoutId}"
                                                    data-workout-title="${w.workoutTypeLabel} on ${w.workoutDate}">
                                                <i class="bi bi-trash"></i> <span class="d-none d-lg-inline ms-1">Delete</span>
                                            </button>
                                        </div>
                                    </div>
                                </div>
                            </div>
                        </c:forEach>
                    </div>

                    <!-- Pagination -->
                    <c:if test="${totalPages > 1}">
                        <nav aria-label="Workouts navigation" class="mt-4">
                            <ul class="pagination justify-content-center">
                                <li class="page-item <c:if test="${currentPage == 1}">disabled</c:if>">
                                    <a class="page-link" href="${pageContext.request.contextPath}/workouts?page=${currentPage - 1}&type=${typeFilter}&intensity=${intensityFilter}&sortBy=${sortBy}">Previous</a>
                                </li>
                                <c:forEach begin="1" end="${totalPages}" var="p">
                                    <li class="page-item <c:if test="${currentPage == p}">active</c:if>">
                                        <a class="page-link" href="${pageContext.request.contextPath}/workouts?page=${p}&type=${typeFilter}&intensity=${intensityFilter}&sortBy=${sortBy}">${p}</a>
                                    </li>
                                </c:forEach>
                                <li class="page-item <c:if test="${currentPage == totalPages}">disabled</c:if>">
                                    <a class="page-link" href="${pageContext.request.contextPath}/workouts?page=${currentPage + 1}&type=${typeFilter}&intensity=${intensityFilter}&sortBy=${sortBy}">Next</a>
                                </li>
                            </ul>
                        </nav>
                    </c:if>
                </c:otherwise>
            </c:choose>

        </div>
    </main>

    <!-- Delete Confirmation Modal (POST with CSRF) -->
    <div class="modal fade" id="deleteModal" tabindex="-1" aria-labelledby="deleteModalLabel" aria-hidden="true">
        <div class="modal-dialog modal-dialog-centered">
            <div class="modal-content border-0 rounded-4 shadow">
                <div class="modal-header border-0 pb-0">
                    <h5 class="modal-title fw-bold text-dark" id="deleteModalLabel">Confirm Deletion</h5>
                    <button type="button" class="btn-close" data-bs-dismiss="modal" aria-label="Close"></button>
                </div>
                <div class="modal-body py-3">
                    <p class="text-secondary mb-2">Are you sure you want to delete this workout?</p>
                    <div class="p-3 bg-light rounded-3">
                        <strong class="text-dark" id="modalWorkoutTitle">Workout</strong>
                    </div>
                    <small class="text-muted d-block mt-2">This action cannot be undone and will remove the workout record permanently.</small>
                </div>
                <div class="modal-footer border-0 pt-0">
                    <button type="button" class="btn btn-outline-secondary px-3" data-bs-dismiss="modal">Cancel</button>
                    <form action="${pageContext.request.contextPath}/workouts/delete" method="post" class="d-inline">
                        <input type="hidden" name="_csrf" value="${sessionScope.csrfToken}">
                        <input type="hidden" name="workoutId" id="modalWorkoutId" value="">
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

    <!-- Delete Modal Data Binder -->
    <script>
        const deleteModal = document.getElementById('deleteModal');
        if (deleteModal) {
            deleteModal.addEventListener('show.bs.modal', function (event) {
                const button = event.relatedTarget;
                const workoutId = button.getAttribute('data-workout-id');
                const workoutTitle = button.getAttribute('data-workout-title');

                document.getElementById('modalWorkoutId').value = workoutId;
                document.getElementById('modalWorkoutTitle').textContent = workoutTitle;
            });
        }
    </script>
</body>
</html>
