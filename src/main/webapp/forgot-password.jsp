<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<!DOCTYPE html>
<html lang="en">
<head>
    <meta charset="UTF-8">
    <meta name="viewport" content="width=device-width, initial-scale=1.0">
    <title>Password Recovery – FitAura</title>

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
            <div class="card border-0 shadow-sm rounded-4 p-5 mx-auto bg-white" style="max-width: 500px;">
                <div class="icon-wrapper mx-auto mb-4 bg-info-subtle text-info" style="width: 60px; height: 60px;">
                    <i class="bi bi-key-fill fs-2"></i>
                </div>
                <h3 class="fw-bold text-dark mb-2">Password Recovery</h3>
                <p class="text-secondary small mb-4">
                    For your privacy and security, password recovery requires administrative verification or local environment assistance during this phase.
                </p>
                <div class="p-3 bg-light rounded-3 text-secondary small mb-4 border">
                    If you forgot your password in development, you can register a new account or reset your password using the secure database update procedure.
                </div>
                <a href="${pageContext.request.contextPath}/login" class="btn btn-success px-4 fw-medium">
                    <i class="bi bi-arrow-left me-1"></i> Back to Log In
                </a>
            </div>
        </div>
    </main>

    <footer class="bg-white border-top py-3 text-center text-muted small mt-auto">
        &copy; 2026 FitAura. All rights reserved.
    </footer>
</body>
</html>
