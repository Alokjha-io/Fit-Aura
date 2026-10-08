<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ taglib prefix="c" uri="jakarta.tags.core" %>
<%@ taglib prefix="fmt" uri="jakarta.tags.fmt" %>
<!DOCTYPE html>
<html lang="en">
<head>
    <meta charset="UTF-8">
    <meta name="viewport" content="width=device-width, initial-scale=1.0">
    <title>Activity & Audit Logs – FitAura Admin</title>

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

            <button class="navbar-toggler" type="button" data-bs-toggle="collapse" data-bs-target="#adminNavbar" aria-controls="adminNavbar" aria-expanded="false" aria-label="Toggle navigation">
                <span class="navbar-toggler-icon"></span>
            </button>

            <div class="collapse navbar-collapse" id="adminNavbar">
                <ul class="navbar-nav me-auto mb-2 mb-lg-0 ms-lg-3">
                    <li class="nav-item">
                        <a class="nav-link text-white-50 hover-text-white" href="${pageContext.request.contextPath}/admin/dashboard">
                            <i class="bi bi-speedometer2 me-1"></i> Dashboard
                        </a>
                    </li>
                    <li class="nav-item">
                        <a class="nav-link text-white-50 hover-text-white" href="${pageContext.request.contextPath}/admin/users">
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
                        <a class="nav-link active fw-bold text-success" aria-current="page" href="${pageContext.request.contextPath}/admin/activity">
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
                    <a href="${pageContext.request.contextPath}/admin/dashboard" class="btn btn-outline-light btn-sm">
                        <i class="bi bi-arrow-left me-1"></i> Admin Dashboard
                    </a>
                </div>
            </div>
        </div>
    </nav>

    <!-- Main Content Container -->
    <main class="container-fluid px-lg-5 my-4 flex-grow-1">

        <!-- Header -->
        <div class="d-flex flex-column flex-md-row justify-content-between align-items-md-center mb-4 gap-2">
            <div>
                <h1 class="h3 fw-bold text-dark mb-1">
                    <i class="bi bi-journal-text text-primary me-2"></i>Platform Audit & Activity Stream
                </h1>
                <p class="text-muted mb-0">Immutable audit log of user logins, workouts, challenges, and administrative actions.</p>
            </div>
            <span class="badge bg-light text-dark border px-3 py-2">
                Total Logs: <strong>${totalLogs}</strong>
            </span>
        </div>

        <!-- Filter Card -->
        <div class="card border-0 shadow-sm mb-4">
            <div class="card-body p-3">
                <form action="${pageContext.request.contextPath}/admin/activity" method="GET" class="row g-2 align-items-end">
                    <div class="col-sm-6 col-md-3">
                        <label class="form-label small fw-semibold text-muted mb-1">Action Type</label>
                        <input type="text" name="action" class="form-control form-control-sm" placeholder="e.g., USER_LOGIN, WORKOUT_CREATED" value="${selectedAction}">
                    </div>
                    <div class="col-sm-6 col-md-3">
                        <label class="form-label small fw-semibold text-muted mb-1">Entity Type</label>
                        <input type="text" name="entity" class="form-control form-control-sm" placeholder="e.g., WORKOUT, CHALLENGE, USER" value="${selectedEntity}">
                    </div>
                    <div class="col-sm-6 col-md-3">
                        <label class="form-label small fw-semibold text-muted mb-1">User ID</label>
                        <input type="number" name="userId" class="form-control form-control-sm" placeholder="Filter by User ID" value="${selectedUserId}">
                    </div>
                    <div class="col-sm-6 col-md-3 d-flex gap-1">
                        <button type="submit" class="btn btn-sm btn-primary w-100">
                            <i class="bi bi-funnel me-1"></i> Filter Logs
                        </button>
                        <a href="${pageContext.request.contextPath}/admin/activity" class="btn btn-sm btn-outline-secondary" title="Reset">
                            <i class="bi bi-arrow-counterclockwise"></i>
                        </a>
                    </div>
                </form>
            </div>
        </div>

        <!-- Logs Table Card -->
        <div class="card border-0 shadow-sm mb-4">
            <div class="card-body p-0">
                <c:choose>
                    <c:when test="${empty logs}">
                        <div class="text-center py-5 text-muted">
                            <i class="bi bi-journal-x fs-1 d-block mb-2 text-secondary"></i>
                            <h5 class="fw-bold text-dark">No Audit Logs Match Criteria</h5>
                            <p class="mb-0 small">Try removing active search filters to view the live log stream.</p>
                        </div>
                    </c:when>
                    <c:otherwise>
                        <div class="table-responsive">
                            <table class="table table-hover align-middle mb-0 font-monospace small">
                                <thead class="table-light text-muted text-uppercase">
                                    <tr>
                                        <th class="ps-4" style="width: 80px;">Log ID</th>
                                        <th>Timestamp</th>
                                        <th>User</th>
                                        <th>Action Type</th>
                                        <th>Entity</th>
                                        <th>Description</th>
                                        <th class="pe-4 text-end">IP Address</th>
                                    </tr>
                                </thead>
                                <tbody>
                                    <c:forEach var="l" items="${logs}">
                                        <tr>
                                            <td class="ps-4 fw-bold text-muted">#${l.logId}</td>
                                            <td class="text-muted text-nowrap">
                                                <fmt:formatDate value="${l.createdAt}" pattern="yyyy-MM-dd HH:mm:ss" />
                                            </td>
                                            <td>
                                                <c:choose>
                                                    <c:when test="${not empty l.userId}">
                                                        <a href="${pageContext.request.contextPath}/admin/users/view?id=${l.userId}" class="text-decoration-none">
                                                            User #${l.userId}
                                                        </a>
                                                    </c:when>
                                                    <c:otherwise>
                                                        <span class="text-muted">System</span>
                                                    </c:otherwise>
                                                </c:choose>
                                            </td>
                                            <td>
                                                <span class="badge bg-light text-dark border font-monospace">
                                                    ${l.actionType}
                                                </span>
                                            </td>
                                            <td>
                                                <c:if test="${not empty l.entityType}">
                                                    <span class="badge bg-secondary-subtle text-secondary font-monospace">
                                                        ${l.entityType} <c:if test="${not empty l.entityId}">#${l.entityId}</c:if>
                                                    </span>
                                                </c:if>
                                            </td>
                                            <td class="text-dark font-sans" style="max-width: 400px;">
                                                <c:out value="${l.description}"/>
                                            </td>
                                            <td class="pe-4 text-end text-muted">
                                                <c:choose>
                                                    <c:when test="${not empty l.ipAddress}">
                                                        <c:out value="${l.ipAddress}"/>
                                                    </c:when>
                                                    <c:otherwise>—</c:otherwise>
                                                </c:choose>
                                            </td>
                                        </tr>
                                    </c:forEach>
                                </tbody>
                            </table>
                        </div>

                        <!-- Pagination -->
                        <c:if test="${totalPages > 1}">
                            <div class="card-footer bg-white border-0 py-3 d-flex justify-content-between align-items-center">
                                <span class="text-muted small">Page ${currentPage} of ${totalPages}</span>
                                <ul class="pagination pagination-sm mb-0">
                                    <li class="page-item ${currentPage <= 1 ? 'disabled' : ''}">
                                        <a class="page-link" href="${pageContext.request.contextPath}/admin/activity?page=${currentPage - 1}&action=${selectedAction}&entity=${selectedEntity}&userId=${selectedUserId}">Previous</a>
                                    </li>
                                    <c:forEach var="p" begin="1" end="${totalPages}">
                                        <li class="page-item ${currentPage == p ? 'active' : ''}">
                                            <a class="page-link" href="${pageContext.request.contextPath}/admin/activity?page=${p}&action=${selectedAction}&entity=${selectedEntity}&userId=${selectedUserId}">${p}</a>
                                        </li>
                                    </c:forEach>
                                    <li class="page-item ${currentPage >= totalPages ? 'disabled' : ''}">
                                        <a class="page-link" href="${pageContext.request.contextPath}/admin/activity?page=${currentPage + 1}&action=${selectedAction}&entity=${selectedEntity}&userId=${selectedUserId}">Next</a>
                                    </li>
                                </ul>
                            </div>
                        </c:if>
                    </c:otherwise>
                </c:choose>
            </div>
        </div>

    </main>

    <!-- Footer -->
    <footer class="bg-white border-top py-3 mt-auto">
        <div class="container-fluid px-lg-5 d-flex justify-content-between align-items-center small text-muted">
            <div>&copy; 2026 FitAura Administration Console</div>
            <span class="badge bg-success-subtle text-success">Secure ADMIN Session</span>
        </div>
    </footer>

    <!-- Bootstrap 5 JS Bundle via CDN -->
    <script src="https://cdn.jsdelivr.net/npm/bootstrap@5.3.3/dist/js/bootstrap.bundle.min.js" integrity="sha384-YvpcrYf0tY3lHB60NNkmXc5s9fDVZLESaAA55NDzOxhy9GkcIdslK1eN7N6jIeHz" crossorigin="anonymous"></script>
</body>
</html>
