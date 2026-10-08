<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ taglib prefix="c" uri="jakarta.tags.core" %>
<%@ taglib prefix="fmt" uri="jakarta.tags.fmt" %>
<!DOCTYPE html>
<html lang="en">
<head>
    <meta charset="UTF-8">
    <meta name="viewport" content="width=device-width, initial-scale=1.0">
    <title>System Settings – FitAura Admin</title>

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
                        <a class="nav-link text-white-50 hover-text-white" href="${pageContext.request.contextPath}/admin/activity">
                            <i class="bi bi-clock-history me-1"></i> Activity Logs
                        </a>
                    </li>
                    <li class="nav-item">
                        <a class="nav-link active fw-bold text-success" aria-current="page" href="${pageContext.request.contextPath}/admin/settings">
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
    <main class="container my-4 flex-grow-1">

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
                    <i class="bi bi-sliders text-secondary me-2"></i>Application Settings & Feature Flags
                </h1>
                <p class="text-muted mb-0">Configure high-level platform behavior, registration flags, and challenge policies.</p>
            </div>
        </div>

        <!-- Safe Settings Form Cards -->
        <div class="card border-0 shadow-sm mb-4">
            <div class="card-header bg-white py-3 border-0">
                <h5 class="fw-bold text-dark mb-0">
                    <i class="bi bi-toggles text-primary me-2"></i>Feature Flags & Operations
                </h5>
            </div>
            <div class="card-body p-4 pt-0">
                <div class="row g-3">
                    <c:forEach var="s" items="${settings}">
                        <div class="col-md-6">
                            <div class="card border h-100 p-3 bg-light-subtle">
                                <form action="${pageContext.request.contextPath}/admin/settings" method="POST">
                                    <input type="hidden" name="csrfToken" value="${sessionScope.csrfToken}">
                                    <input type="hidden" name="settingKey" value="${s.settingKey}">

                                    <div class="d-flex justify-content-between align-items-start mb-2">
                                        <div>
                                            <h6 class="fw-bold text-dark mb-0 font-monospace">${s.settingKey}</h6>
                                            <small class="text-muted">${s.description}</small>
                                        </div>
                                        <span class="badge bg-light text-dark border font-monospace">${s.settingValue}</span>
                                    </div>

                                    <div class="input-group input-group-sm mt-3">
                                        <c:choose>
                                            <c:when test="${s.settingValue == 'true' || s.settingValue == 'false'}">
                                                <select name="settingValue" class="form-select">
                                                    <option value="true" ${s.settingValue == 'true' ? 'selected' : ''}>Enabled (true)</option>
                                                    <option value="false" ${s.settingValue == 'false' ? 'selected' : ''}>Disabled (false)</option>
                                                </select>
                                            </c:when>
                                            <c:otherwise>
                                                <input type="text" name="settingValue" class="form-control" value="${s.settingValue}">
                                            </c:otherwise>
                                        </c:choose>
                                        <button type="submit" class="btn btn-primary">Save</button>
                                    </div>

                                    <div class="text-muted small mt-2 d-flex justify-content-between">
                                        <span>Last updated: <fmt:formatDate value="${s.updatedAt}" pattern="MMM dd, yyyy" /></span>
                                        <c:if test="${not empty s.updatedBy}">
                                            <span>by Admin #${s.updatedBy}</span>
                                        </c:if>
                                    </div>
                                </form>
                            </div>
                        </div>
                    </c:forEach>
                </div>
            </div>
        </div>

        <!-- Security Guardrail Note -->
        <div class="alert alert-info d-flex align-items-center shadow-sm">
            <i class="bi bi-shield-lock-fill fs-4 text-info flex-shrink-0 me-3"></i>
            <div>
                <strong>Security Guardrail Policy</strong>
                <p class="mb-0 small">
                    Database credentials, cryptographic salts, API secrets, and server configuration files are strictly isolated from runtime database tables and cannot be manipulated through web endpoints.
                </p>
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
