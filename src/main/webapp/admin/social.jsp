<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ taglib prefix="c" uri="jakarta.tags.core" %>
<!DOCTYPE html>
<html lang="en">
<head>
    <meta charset="UTF-8">
    <meta name="viewport" content="width=device-width, initial-scale=1.0">
    <title>Social & Competition Management – FitAura Admin</title>
    <!-- Bootstrap 5 CSS via CDN -->
    <link href="https://cdn.jsdelivr.net/npm/bootstrap@5.3.3/dist/css/bootstrap.min.css" rel="stylesheet">
    <link rel="stylesheet" href="https://cdn.jsdelivr.net/npm/bootstrap-icons@1.11.3/font/bootstrap-icons.min.css">
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
                    <li class="nav-item"><a class="nav-link text-white-50" href="${pageContext.request.contextPath}/admin/dashboard"><i class="bi bi-speedometer2 me-1"></i> Dashboard</a></li>
                    <li class="nav-item"><a class="nav-link text-white-50" href="${pageContext.request.contextPath}/admin/users"><i class="bi bi-people me-1"></i> Users</a></li>
                    <li class="nav-item"><a class="nav-link text-white-50" href="${pageContext.request.contextPath}/admin/challenges"><i class="bi bi-trophy me-1"></i> Challenges</a></li>
                    <li class="nav-item"><a class="nav-link text-white-50" href="${pageContext.request.contextPath}/admin/quotes"><i class="bi bi-quote me-1"></i> Quotes</a></li>
                    <li class="nav-item"><a class="nav-link active fw-bold text-success" href="${pageContext.request.contextPath}/admin/social"><i class="bi bi-people-fill me-1"></i> Social</a></li>
                    <li class="nav-item"><a class="nav-link text-white-50" href="${pageContext.request.contextPath}/admin/statistics"><i class="bi bi-graph-up me-1"></i> Statistics</a></li>
                    <li class="nav-item"><a class="nav-link text-white-50" href="${pageContext.request.contextPath}/admin/activity"><i class="bi bi-clock-history me-1"></i> Activity Logs</a></li>
                    <li class="nav-item"><a class="nav-link text-white-50" href="${pageContext.request.contextPath}/admin/settings"><i class="bi bi-sliders me-1"></i> Settings</a></li>
                </ul>
                <div class="d-flex align-items-center gap-2">
                    <a href="${pageContext.request.contextPath}/user/dashboard" class="btn btn-outline-light btn-sm"><i class="bi bi-arrow-left me-1"></i> User App</a>
                    <a href="${pageContext.request.contextPath}/logout" class="btn btn-danger btn-sm px-3"><i class="bi bi-box-arrow-right me-1"></i> Sign Out</a>
                </div>
            </div>
        </div>
    </nav>

    <main class="container-fluid px-lg-5 my-4 flex-grow-1">

        <c:if test="${not empty param.success}">
            <div class="alert alert-success alert-dismissible fade show rounded-4 shadow-xs" role="alert">
                <i class="bi bi-check-circle-fill me-2"></i> Competition action completed successfully.
                <button type="button" class="btn-close" data-bs-dismiss="alert"></button>
            </div>
        </c:if>
        <c:if test="${not empty param.error}">
            <div class="alert alert-danger alert-dismissible fade show rounded-4 shadow-xs" role="alert">
                <i class="bi bi-exclamation-triangle-fill me-2"></i> <c:out value="${param.error}"/>
                <button type="button" class="btn-close" data-bs-dismiss="alert"></button>
            </div>
        </c:if>

        <div class="d-flex flex-column flex-md-row justify-content-between align-items-md-center mb-4 gap-3">
            <div>
                <h3 class="fw-bold text-dark mb-1"><i class="bi bi-trophy-fill text-warning me-2"></i>Social Fitness & Competitions Management</h3>
                <p class="text-secondary mb-0 small">Create and oversee privacy-safe weekly and monthly fitness competitions.</p>
            </div>
            <button class="btn btn-success fw-semibold" data-bs-toggle="collapse" data-bs-target="#createCompCollapse">
                <i class="bi bi-plus-lg me-1"></i> Create Competition
            </button>
        </div>

        <!-- Metrics Strip -->
        <div class="row g-3 mb-4">
            <div class="col-md-4">
                <div class="card border rounded-4 p-3 bg-white shadow-xs">
                    <div class="d-flex align-items-center gap-3">
                        <div class="rounded-3 bg-success-subtle text-success p-2.5 fs-4"><i class="bi bi-people"></i></div>
                        <div>
                            <div class="small text-muted">Eligible Social Users</div>
                            <div class="fs-4 fw-bold text-dark">${socialUserCount}</div>
                        </div>
                    </div>
                </div>
            </div>
            <div class="col-md-4">
                <div class="card border rounded-4 p-3 bg-white shadow-xs">
                    <div class="d-flex align-items-center gap-3">
                        <div class="rounded-3 bg-primary-subtle text-primary p-2.5 fs-4"><i class="bi bi-play-circle"></i></div>
                        <div>
                            <div class="small text-muted">Active Competitions</div>
                            <div class="fs-4 fw-bold text-dark">${activeCompetitions.size()}</div>
                        </div>
                    </div>
                </div>
            </div>
            <div class="col-md-4">
                <div class="card border rounded-4 p-3 bg-white shadow-xs">
                    <div class="d-flex align-items-center gap-3">
                        <div class="rounded-3 bg-warning-subtle text-warning p-2.5 fs-4"><i class="bi bi-calendar-event"></i></div>
                        <div>
                            <div class="small text-muted">Upcoming Competitions</div>
                            <div class="fs-4 fw-bold text-dark">${upcomingCompetitions.size()}</div>
                        </div>
                    </div>
                </div>
            </div>
        </div>

        <!-- Create Competition Form -->
        <div class="collapse mb-4" id="createCompCollapse">
            <div class="card border rounded-4 shadow-xs bg-white p-4">
                <h5 class="fw-bold text-dark mb-3"><i class="bi bi-flag-fill text-success me-2"></i>Launch New Competition</h5>
                <form action="${pageContext.request.contextPath}/admin/social" method="POST">
                    <input type="hidden" name="csrfToken" value="${sessionScope.csrfToken}"/>
                    <input type="hidden" name="action" value="create"/>

                    <div class="row g-3">
                        <div class="col-md-8">
                            <label class="form-label small fw-semibold">Competition Name *</label>
                            <input type="text" name="name" class="form-control rounded-3" placeholder="e.g. Weekly Workout Champion" required/>
                        </div>
                        <div class="col-md-4">
                            <label class="form-label small fw-semibold">Scoring Metric *</label>
                            <select name="metric" class="form-select rounded-3">
                                <option value="WORKOUT_COUNT" selected>Workout Count (Sessions completed)</option>
                                <option value="TOTAL_MINUTES">Total Active Minutes</option>
                                <option value="CALORIES_BURNED">Total Calories Burned (kcal)</option>
                                <option value="STREAK_DAYS">Consecutive Active Days</option>
                            </select>
                        </div>
                        <div class="col-12">
                            <label class="form-label small fw-semibold">Description</label>
                            <textarea name="description" rows="2" class="form-control rounded-3" placeholder="Explain the rules and motivation for participants..."></textarea>
                        </div>
                        <div class="col-md-3">
                            <label class="form-label small fw-semibold">Start Date *</label>
                            <input type="date" name="startDate" class="form-control rounded-3" required/>
                        </div>
                        <div class="col-md-3">
                            <label class="form-label small fw-semibold">End Date *</label>
                            <input type="date" name="endDate" class="form-control rounded-3" required/>
                        </div>
                        <div class="col-md-3">
                            <label class="form-label small fw-semibold">Reward Points</label>
                            <input type="number" name="rewardPoints" class="form-control rounded-3" value="50" min="0"/>
                        </div>
                        <div class="col-md-3">
                            <label class="form-label small fw-semibold">Status</label>
                            <select name="status" class="form-select rounded-3">
                                <option value="ACTIVE" selected>ACTIVE</option>
                                <option value="UPCOMING">UPCOMING</option>
                            </select>
                        </div>
                        <div class="col-12 text-end">
                            <button type="button" class="btn btn-outline-secondary btn-sm me-2" data-bs-toggle="collapse" data-bs-target="#createCompCollapse">Cancel</button>
                            <button type="submit" class="btn btn-success btn-sm px-4 fw-semibold">Create Competition</button>
                        </div>
                    </div>
                </form>
            </div>
        </div>

        <!-- Active Competitions Table -->
        <div class="card border rounded-4 shadow-xs bg-white mb-4">
            <div class="card-header bg-transparent py-3 border-bottom d-flex justify-content-between align-items-center">
                <h6 class="fw-bold text-dark mb-0"><i class="bi bi-play-circle text-success me-2"></i>Active Competitions</h6>
                <span class="badge bg-success-subtle text-success">${activeCompetitions.size()} Live</span>
            </div>
            <div class="table-responsive">
                <table class="table align-middle table-hover mb-0">
                    <thead class="table-light small">
                        <tr>
                            <th>Competition</th>
                            <th>Metric</th>
                            <th>Duration</th>
                            <th>Participants</th>
                            <th>Reward</th>
                            <th class="text-end">Actions</th>
                        </tr>
                    </thead>
                    <tbody>
                        <c:choose>
                            <c:when test="${not empty activeCompetitions}">
                                <c:forEach var="c" items="${activeCompetitions}">
                                    <tr>
                                        <td>
                                            <div class="fw-semibold text-dark"><c:out value="${c.name}"/></div>
                                            <small class="text-muted text-truncate d-inline-block" style="max-width: 250px;"><c:out value="${c.description}"/></small>
                                        </td>
                                        <td><span class="badge bg-light text-dark border"><c:out value="${c.metricLabel}"/></span></td>
                                        <td class="small text-nowrap"><c:out value="${c.startDate}"/> to <c:out value="${c.endDate}"/></td>
                                        <td><span class="badge bg-primary-subtle text-primary">${c.participantCount} users</span></td>
                                        <td><span class="badge bg-warning-subtle text-warning-emphasis">+${c.rewardPoints} pts</span></td>
                                        <td class="text-end text-nowrap">
                                            <a href="${pageContext.request.contextPath}/social/competition?id=${c.competitionId}" class="btn btn-outline-primary btn-sm" target="_blank">
                                                <i class="bi bi-eye"></i> View
                                            </a>
                                            <form action="${pageContext.request.contextPath}/admin/social" method="POST" class="d-inline ms-1" onsubmit="return confirm('Delete this competition?');">
                                                <input type="hidden" name="csrfToken" value="${sessionScope.csrfToken}"/>
                                                <input type="hidden" name="competitionId" value="${c.competitionId}"/>
                                                <input type="hidden" name="action" value="delete"/>
                                                <button type="submit" class="btn btn-outline-danger btn-sm">
                                                    <i class="bi bi-trash"></i>
                                                </button>
                                            </form>
                                        </td>
                                    </tr>
                                </c:forEach>
                            </c:when>
                            <c:otherwise>
                                <tr>
                                    <td colspan="6" class="text-center py-4 text-muted small">No active competitions right now. Launch one above!</td>
                                </tr>
                            </c:otherwise>
                        </c:choose>
                    </tbody>
                </table>
            </div>
        </div>

    </main>

    <!-- Bootstrap 5 JS -->
    <script src="https://cdn.jsdelivr.net/npm/bootstrap@5.3.3/dist/js/bootstrap.bundle.min.js"></script>
</body>
</html>
