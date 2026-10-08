<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ taglib prefix="c" uri="jakarta.tags.core" %>
<%@ taglib prefix="fmt" uri="jakarta.tags.fmt" %>
<!DOCTYPE html>
<html lang="en">
<head>
    <meta charset="UTF-8">
    <meta name="viewport" content="width=device-width, initial-scale=1.0">
    <title>User Management – FitAura Admin</title>

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
                    <a href="${pageContext.request.contextPath}/user/dashboard" class="btn btn-outline-light btn-sm">
                        <i class="bi bi-arrow-left me-1"></i> User App
                    </a>
                    <a href="${pageContext.request.contextPath}/logout" class="btn btn-danger btn-sm px-3">
                        <i class="bi bi-box-arrow-right me-1"></i> Sign Out
                    </a>
                </div>
            </div>
        </div>
    </nav>

    <!-- Main Content Container -->
    <main class="container-fluid px-lg-5 my-4 flex-grow-1">

        <!-- Alerts -->
        <c:if test="${not empty errorMessage}">
            <div class="alert alert-danger alert-dismissible fade show d-flex align-items-center shadow-sm" role="alert">
                <i class="bi bi-exclamation-triangle-fill flex-shrink-0 me-2 fs-5"></i>
                <div>${errorMessage}</div>
                <button type="button" class="btn-close" data-bs-dismiss="alert" aria-label="Close"></button>
            </div>
        </c:if>
        <c:if test="${not empty param.error}">
            <div class="alert alert-danger alert-dismissible fade show d-flex align-items-center shadow-sm" role="alert">
                <i class="bi bi-exclamation-triangle-fill flex-shrink-0 me-2 fs-5"></i>
                <div><c:out value="${param.error}"/></div>
                <button type="button" class="btn-close" data-bs-dismiss="alert" aria-label="Close"></button>
            </div>
        </c:if>
        <c:if test="${not empty param.success}">
            <div class="alert alert-success alert-dismissible fade show d-flex align-items-center shadow-sm" role="alert">
                <i class="bi bi-check-circle-fill flex-shrink-0 me-2 fs-5"></i>
                <div><c:out value="${param.success}"/></div>
                <button type="button" class="btn-close" data-bs-dismiss="alert" aria-label="Close"></button>
            </div>
        </c:if>

        <!-- Header -->
        <div class="d-flex flex-column flex-md-row justify-content-between align-items-md-center mb-4 gap-2">
            <div>
                <h1 class="h3 fw-bold text-dark mb-1">
                    <i class="bi bi-people-fill text-primary me-2"></i>User Management Directory
                </h1>
                <p class="text-muted mb-0">Search, inspect, and moderate registered athlete accounts and account statuses.</p>
            </div>
            <div class="text-muted small">
                Total matching: <strong>${totalUsers}</strong> user<c:if test="${totalUsers != 1}">s</c:if>
            </div>
        </div>

        <!-- Search & Filter Card -->
        <div class="card border-0 shadow-sm mb-4">
            <div class="card-body p-3">
                <form action="${pageContext.request.contextPath}/admin/users" method="GET" class="row g-2 align-items-end">
                    <div class="col-md-4">
                        <label class="form-label small fw-semibold text-muted mb-1">Search User</label>
                        <div class="input-group input-group-sm">
                            <span class="input-group-text bg-light"><i class="bi bi-search"></i></span>
                            <input type="text" name="search" class="form-control" placeholder="Name, display name, or email..." value="${searchQuery}">
                        </div>
                    </div>
                    <div class="col-sm-6 col-md-2">
                        <label class="form-label small fw-semibold text-muted mb-1">Role</label>
                        <select name="role" class="form-select form-select-sm">
                            <option value="">All Roles</option>
                            <option value="USER" ${selectedRole == 'USER' ? 'selected' : ''}>USER</option>
                            <option value="ADMIN" ${selectedRole == 'ADMIN' ? 'selected' : ''}>ADMIN</option>
                        </select>
                    </div>
                    <div class="col-sm-6 col-md-2">
                        <label class="form-label small fw-semibold text-muted mb-1">Account Status</label>
                        <select name="status" class="form-select form-select-sm">
                            <option value="">All Statuses</option>
                            <option value="ACTIVE" ${selectedStatus == 'ACTIVE' ? 'selected' : ''}>ACTIVE</option>
                            <option value="INACTIVE" ${selectedStatus == 'INACTIVE' ? 'selected' : ''}>INACTIVE</option>
                            <option value="BLOCKED" ${selectedStatus == 'BLOCKED' ? 'selected' : ''}>BLOCKED</option>
                        </select>
                    </div>
                    <div class="col-sm-6 col-md-2">
                        <label class="form-label small fw-semibold text-muted mb-1">Privacy Mode</label>
                        <select name="privacy" class="form-select form-select-sm">
                            <option value="">All Modes</option>
                            <option value="PERSONAL" ${selectedPrivacy == 'PERSONAL' ? 'selected' : ''}>PERSONAL</option>
                            <option value="SOCIAL" ${selectedPrivacy == 'SOCIAL' ? 'selected' : ''}>SOCIAL</option>
                        </select>
                    </div>
                    <div class="col-sm-6 col-md-2 d-flex gap-1">
                        <button type="submit" class="btn btn-sm btn-primary w-100">
                            <i class="bi bi-funnel me-1"></i> Filter
                        </button>
                        <a href="${pageContext.request.contextPath}/admin/users" class="btn btn-sm btn-outline-secondary" title="Reset">
                            <i class="bi bi-arrow-counterclockwise"></i>
                        </a>
                    </div>
                </form>
            </div>
        </div>

        <!-- Users Table Card -->
        <div class="card border-0 shadow-sm mb-4">
            <div class="card-body p-0">
                <c:choose>
                    <c:when test="${empty users}">
                        <div class="text-center py-5 text-muted">
                            <i class="bi bi-person-x fs-1 d-block mb-2 text-secondary"></i>
                            <h5 class="fw-bold text-dark">No Users Found</h5>
                            <p class="mb-0 small">Try adjusting your search criteria or clearing active filters.</p>
                        </div>
                    </c:when>
                    <c:otherwise>
                        <div class="table-responsive">
                            <table class="table table-hover align-middle mb-0">
                                <thead class="table-light small text-muted text-uppercase">
                                    <tr>
                                        <th class="ps-4" style="width: 70px;">ID</th>
                                        <th>Athlete</th>
                                        <th>Role</th>
                                        <th>Privacy</th>
                                        <th>Status</th>
                                        <th>Registered</th>
                                        <th class="pe-4 text-end">Moderate Status</th>
                                    </tr>
                                </thead>
                                <tbody>
                                    <c:forEach var="u" items="${users}">
                                        <tr>
                                            <td class="ps-4 fw-bold text-muted">#${u.userId}</td>
                                            <td>
                                                <div class="d-flex align-items-center gap-2">
                                                    <div class="rounded-circle bg-light border text-secondary d-flex align-items-center justify-content-center" style="width: 36px; height: 36px;">
                                                        <i class="bi bi-person-fill"></i>
                                                    </div>
                                                    <div>
                                                        <a href="${pageContext.request.contextPath}/admin/users/view?id=${u.userId}" class="fw-bold text-dark text-decoration-none">
                                                            <c:out value="${u.displayName}"/>
                                                        </a>
                                                        <div class="text-muted small"><c:out value="${u.email}"/></div>
                                                    </div>
                                                </div>
                                            </td>
                                            <td>
                                                <span class="badge ${u.role == 'ADMIN' ? 'bg-danger-subtle text-danger border border-danger-subtle' : 'bg-light text-dark border'}">
                                                    ${u.role}
                                                </span>
                                            </td>
                                            <td>
                                                <span class="badge ${u.privacyMode == 'SOCIAL' ? 'bg-success-subtle text-success' : 'bg-secondary-subtle text-secondary'}">
                                                    ${u.privacyMode}
                                                </span>
                                            </td>
                                            <td>
                                                <span class="badge ${u.accountStatus == 'ACTIVE' ? 'bg-success' : (u.accountStatus == 'BLOCKED' ? 'bg-danger' : 'bg-warning text-dark')}">
                                                    ${u.accountStatus}
                                                </span>
                                            </td>
                                            <td class="text-muted small">
                                                <fmt:formatDate value="${u.createdAt}" pattern="MMM dd, yyyy" />
                                            </td>
                                            <td class="pe-4 text-end">
                                                <div class="btn-group btn-group-sm" role="group">
                                                    <a href="${pageContext.request.contextPath}/admin/users/view?id=${u.userId}" class="btn btn-outline-secondary" title="View Details">
                                                        <i class="bi bi-eye"></i> View
                                                    </a>
                                                    <c:choose>
                                                        <c:when test="${u.accountStatus == 'ACTIVE'}">
                                                            <form action="${pageContext.request.contextPath}/admin/users/status" method="POST" class="d-inline" onsubmit="return confirm('Deactivate account for ${u.displayName}?');">
                                                                <input type="hidden" name="csrfToken" value="${sessionScope.csrfToken}">
                                                                <input type="hidden" name="userId" value="${u.userId}">
                                                                <input type="hidden" name="status" value="INACTIVE">
                                                                <button type="submit" class="btn btn-outline-warning" title="Deactivate">
                                                                    Deactivate
                                                                </button>
                                                            </form>
                                                            <form action="${pageContext.request.contextPath}/admin/users/status" method="POST" class="d-inline" onsubmit="return confirm('Block account for ${u.displayName}? They will not be able to log in.');">
                                                                <input type="hidden" name="csrfToken" value="${sessionScope.csrfToken}">
                                                                <input type="hidden" name="userId" value="${u.userId}">
                                                                <input type="hidden" name="status" value="BLOCKED">
                                                                <button type="submit" class="btn btn-outline-danger" title="Block">
                                                                    Block
                                                                </button>
                                                            </form>
                                                        </c:when>
                                                        <c:when test="${u.accountStatus == 'INACTIVE' || u.accountStatus == 'BLOCKED'}">
                                                            <form action="${pageContext.request.contextPath}/admin/users/status" method="POST" class="d-inline" onsubmit="return confirm('Activate account for ${u.displayName}?');">
                                                                <input type="hidden" name="csrfToken" value="${sessionScope.csrfToken}">
                                                                <input type="hidden" name="userId" value="${u.userId}">
                                                                <input type="hidden" name="status" value="ACTIVE">
                                                                <button type="submit" class="btn btn-success" title="Activate Account">
                                                                    <i class="bi bi-check-lg"></i> Activate
                                                                </button>
                                                            </form>
                                                        </c:when>
                                                    </c:choose>
                                                </div>
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
                                        <a class="page-link" href="${pageContext.request.contextPath}/admin/users?page=${currentPage - 1}&search=${searchQuery}&role=${selectedRole}&status=${selectedStatus}&privacy=${selectedPrivacy}">Previous</a>
                                    </li>
                                    <c:forEach var="p" begin="1" end="${totalPages}">
                                        <li class="page-item ${currentPage == p ? 'active' : ''}">
                                            <a class="page-link" href="${pageContext.request.contextPath}/admin/users?page=${p}&search=${searchQuery}&role=${selectedRole}&status=${selectedStatus}&privacy=${selectedPrivacy}">${p}</a>
                                        </li>
                                    </c:forEach>
                                    <li class="page-item ${currentPage >= totalPages ? 'disabled' : ''}">
                                        <a class="page-link" href="${pageContext.request.contextPath}/admin/users?page=${currentPage + 1}&search=${searchQuery}&role=${selectedRole}&status=${selectedStatus}&privacy=${selectedPrivacy}">Next</a>
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
