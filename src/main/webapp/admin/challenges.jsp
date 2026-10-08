<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ taglib prefix="c" uri="jakarta.tags.core" %>
<%@ taglib prefix="fmt" uri="jakarta.tags.fmt" %>
<!DOCTYPE html>
<html lang="en">
<head>
    <meta charset="UTF-8">
    <meta name="viewport" content="width=device-width, initial-scale=1.0">
    <title>Challenge Moderation – FitAura Admin</title>

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
                        <a class="nav-link active fw-bold text-success" aria-current="page" href="${pageContext.request.contextPath}/admin/challenges">
                            <i class="bi bi-trophy me-1"></i> Challenges
                            <c:if test="${pendingCount > 0}">
                                <span class="badge bg-warning text-dark ms-1">${pendingCount}</span>
                            </c:if>
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
                    <a href="${pageContext.request.contextPath}/challenges" class="btn btn-outline-light btn-sm">
                        <i class="bi bi-arrow-left me-1"></i> Challenges Directory
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
                    <i class="bi bi-trophy-fill text-warning me-2"></i>Challenge Moderation & Management
                </h1>
                <p class="text-muted mb-0">Approve community challenges, manage active events, or cancel inappropriate submissions.</p>
            </div>
            <div class="d-flex gap-2">
                <a href="${pageContext.request.contextPath}/challenges/create" class="btn btn-success">
                    <i class="bi bi-plus-lg me-1"></i> Create Admin Challenge
                </a>
            </div>
        </div>

        <!-- Filter Tabs -->
        <div class="card border-0 shadow-sm mb-4">
            <div class="card-body p-2">
                <ul class="nav nav-pills gap-1">
                    <li class="nav-item">
                        <a class="nav-link ${selectedStatus == 'ALL' ? 'active bg-primary' : 'text-dark'}" href="${pageContext.request.contextPath}/admin/challenges?status=ALL">
                            All Challenges
                        </a>
                    </li>
                    <li class="nav-item">
                        <a class="nav-link ${selectedStatus == 'DRAFT' ? 'active bg-warning text-dark fw-bold' : 'text-dark'}" href="${pageContext.request.contextPath}/admin/challenges?status=DRAFT">
                            Pending Approval
                            <c:if test="${pendingCount > 0}">
                                <span class="badge bg-danger ms-1">${pendingCount}</span>
                            </c:if>
                        </a>
                    </li>
                    <li class="nav-item">
                        <a class="nav-link ${selectedStatus == 'ACTIVE' ? 'active bg-success' : 'text-dark'}" href="${pageContext.request.contextPath}/admin/challenges?status=ACTIVE">
                            Active (${activeCount})
                        </a>
                    </li>
                    <li class="nav-item">
                        <a class="nav-link ${selectedStatus == 'COMPLETED' ? 'active bg-secondary' : 'text-dark'}" href="${pageContext.request.contextPath}/admin/challenges?status=COMPLETED">
                            Completed
                        </a>
                    </li>
                    <li class="nav-item">
                        <a class="nav-link ${selectedStatus == 'CANCELLED' ? 'active bg-danger' : 'text-dark'}" href="${pageContext.request.contextPath}/admin/challenges?status=CANCELLED">
                            Cancelled
                        </a>
                    </li>
                </ul>
            </div>
        </div>

        <!-- Challenges Table Card -->
        <div class="card border-0 shadow-sm mb-4">
            <div class="card-body p-0">
                <c:choose>
                    <c:when test="${empty challenges}">
                        <div class="text-center py-5 text-muted">
                            <i class="bi bi-trophy fs-1 d-block mb-2 text-secondary"></i>
                            <h5 class="fw-bold text-dark">No Challenges Found</h5>
                            <p class="mb-0 small">No challenges match the selected status filter.</p>
                        </div>
                    </c:when>
                    <c:otherwise>
                        <div class="table-responsive">
                            <table class="table table-hover align-middle mb-0">
                                <thead class="table-light small text-muted text-uppercase">
                                    <tr>
                                        <th class="ps-4" style="width: 70px;">ID</th>
                                        <th>Challenge Name</th>
                                        <th>Target Goal</th>
                                        <th>Timeframe</th>
                                        <th>Status</th>
                                        <th>Created By</th>
                                        <th class="pe-4 text-end">Moderation Actions</th>
                                    </tr>
                                </thead>
                                <tbody>
                                    <c:forEach var="c" items="${challenges}">
                                        <tr>
                                            <td class="ps-4 fw-bold text-muted">#${c.challengeId}</td>
                                            <td>
                                                <a href="${pageContext.request.contextPath}/challenges/view?id=${c.challengeId}" class="fw-bold text-dark text-decoration-none">
                                                    <c:out value="${c.name}"/>
                                                </a>
                                                <div class="text-muted small text-truncate" style="max-width: 300px;"><c:out value="${c.description}"/></div>
                                            </td>
                                            <td>
                                                <span class="badge bg-light text-dark border font-monospace">${c.goalValue} ${c.goalUnit}</span>
                                            </td>
                                            <td class="small text-muted">
                                                <fmt:formatDate value="${c.startDate}" pattern="MMM dd" /> – <fmt:formatDate value="${c.endDate}" pattern="MMM dd, yyyy" />
                                            </td>
                                            <td>
                                                <span class="badge ${c.status == 'ACTIVE' ? 'bg-success' : (c.status == 'DRAFT' ? 'bg-warning text-dark' : (c.status == 'COMPLETED' ? 'bg-primary' : 'bg-danger'))}">
                                                    ${c.status}
                                                </span>
                                            </td>
                                            <td class="text-muted small">
                                                <c:choose>
                                                    <c:when test="${not empty c.createdBy}">
                                                        <a href="${pageContext.request.contextPath}/admin/users/view?id=${c.createdBy}" class="text-decoration-none text-muted">
                                                            User #${c.createdBy}
                                                        </a>
                                                    </c:when>
                                                    <c:otherwise>Admin</c:otherwise>
                                                </c:choose>
                                            </td>
                                            <td class="pe-4 text-end">
                                                <div class="d-inline-flex gap-1">
                                                    <a href="${pageContext.request.contextPath}/challenges/view?id=${c.challengeId}" class="btn btn-sm btn-outline-secondary" title="View Details">
                                                        <i class="bi bi-eye"></i>
                                                    </a>
                                                    <c:if test="${c.status == 'DRAFT'}">
                                                        <form action="${pageContext.request.contextPath}/admin/challenges/approve" method="POST" class="d-inline">
                                                            <input type="hidden" name="csrfToken" value="${sessionScope.csrfToken}">
                                                            <input type="hidden" name="challengeId" value="${c.challengeId}">
                                                            <button type="submit" class="btn btn-sm btn-success" title="Approve Challenge">
                                                                <i class="bi bi-check-lg me-1"></i> Approve
                                                            </button>
                                                        </form>
                                                        <form action="${pageContext.request.contextPath}/admin/challenges/reject" method="POST" class="d-inline" onsubmit="return confirm('Reject this challenge submission?');">
                                                            <input type="hidden" name="csrfToken" value="${sessionScope.csrfToken}">
                                                            <input type="hidden" name="challengeId" value="${c.challengeId}">
                                                            <button type="submit" class="btn btn-sm btn-outline-danger" title="Reject Challenge">
                                                                <i class="bi bi-x-lg me-1"></i> Reject
                                                            </button>
                                                        </form>
                                                    </c:if>
                                                    <c:if test="${c.status == 'ACTIVE'}">
                                                        <form action="${pageContext.request.contextPath}/admin/challenges/cancel" method="POST" class="d-inline" onsubmit="return confirm('Cancel this active challenge?');">
                                                            <input type="hidden" name="csrfToken" value="${sessionScope.csrfToken}">
                                                            <input type="hidden" name="challengeId" value="${c.challengeId}">
                                                            <button type="submit" class="btn btn-sm btn-outline-danger" title="Cancel Challenge">
                                                                Cancel
                                                            </button>
                                                        </form>
                                                    </c:if>
                                                </div>
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
