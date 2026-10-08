<%@ page contentType="text/html;charset=UTF-8" language="java" isErrorPage="true" %>
<!DOCTYPE html>
<html lang="en">
<head>
    <meta charset="UTF-8">
    <meta name="viewport" content="width=device-width, initial-scale=1.0">
    <title>Access Denied – FitAura</title>

    <!-- Bootstrap 5 CSS via CDN -->
    <link href="https://cdn.jsdelivr.net/npm/bootstrap@5.3.3/dist/css/bootstrap.min.css" rel="stylesheet">
    <link rel="stylesheet" href="https://cdn.jsdelivr.net/npm/bootstrap-icons@1.11.3/font/bootstrap-icons.min.css">
    <link rel="stylesheet" href="${pageContext.request.contextPath}/css/fitaura.css">
</head>
<body class="bg-light d-flex flex-column min-vh-100">

    <nav class="navbar navbar-light bg-white border-bottom py-3">
        <div class="container">
            <a class="navbar-brand brand-badge d-flex align-items-center" href="${pageContext.request.contextPath}/index.jsp">
                <img src="${pageContext.request.contextPath}/images/fitaura-logo.svg" alt="FitAura Logo" width="34" height="34" class="me-2">
                <span>Fit<span class="text-success">Aura</span></span>
            </a>
        </div>
    </nav>

    <main class="flex-grow-1 d-flex align-items-center py-5">
        <div class="container text-center">
            <div class="card border-0 shadow-sm rounded-4 p-5 mx-auto bg-white" style="max-width: 520px;">
                <div class="icon-wrapper mx-auto mb-4 bg-danger-subtle text-danger" style="width: 64px; height: 64px;">
                    <i class="bi bi-shield-x fs-1"></i>
                </div>
                <h3 class="fw-bold text-dark mb-2">403 – Access Denied</h3>
                <p class="text-secondary mb-4">
                    You do not have permission to access this administrative resource. Access is strictly restricted by role-based authorization.
                </p>
                <div class="d-flex justify-content-center gap-3">
                    <a href="${pageContext.request.contextPath}/user/dashboard" class="btn btn-success px-4 fw-medium">
                        Return to User Dashboard
                    </a>
                    <a href="${pageContext.request.contextPath}/index.jsp" class="btn btn-outline-secondary px-4 fw-medium">
                        Home
                    </a>
                </div>
            </div>
        </div>
    </main>

    <footer class="bg-white border-top py-3 text-center text-muted small mt-auto">
        &copy; 2026 FitAura. All rights reserved.
    </footer>
</body>
</html>
