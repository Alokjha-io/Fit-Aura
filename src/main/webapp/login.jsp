<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ taglib prefix="c" uri="jakarta.tags.core" %>
<!DOCTYPE html>
<html lang="en">
<head>
    <meta charset="UTF-8">
    <meta name="viewport" content="width=device-width, initial-scale=1.0">
    <title>Log In – FitAura</title>
    <meta name="description" content="Secure login to your personal FitAura fitness dashboard.">

    <!-- Bootstrap 5 CSS via CDN -->
    <link href="https://cdn.jsdelivr.net/npm/bootstrap@5.3.3/dist/css/bootstrap.min.css" rel="stylesheet" integrity="sha384-QWTKZyjpPEjISv5WaRU9OFeRpok6YctnYmDr5pNlyT2bRjXh0JMhjY6hW+ALEwIH" crossorigin="anonymous">
    <!-- Bootstrap Icons -->
    <link rel="stylesheet" href="https://cdn.jsdelivr.net/npm/bootstrap-icons@1.11.3/font/bootstrap-icons.min.css">
    <!-- FitAura Custom CSS -->
    <link rel="stylesheet" href="${pageContext.request.contextPath}/css/fitaura.css">
</head>
<body class="bg-light d-flex flex-column min-vh-100">

    <!-- Top Navigation Bar -->
    <nav class="navbar navbar-light bg-white border-bottom py-3">
        <div class="container">
            <a class="navbar-brand brand-badge d-flex align-items-center" href="${pageContext.request.contextPath}/index.jsp">
                <img src="${pageContext.request.contextPath}/images/fitaura-logo.svg" alt="FitAura Logo" width="34" height="34" class="me-2">
                <span>Fit<span class="text-success">Aura</span></span>
            </a>
            <span class="text-secondary small d-none d-sm-inline">Track. Improve. Thrive.</span>
        </div>
    </nav>

    <!-- Main Authentication Form Container -->
    <main class="flex-grow-1 d-flex align-items-center py-5">
        <div class="container">
            <div class="row justify-content-center">
                <div class="col-12 col-sm-10 col-md-8 col-lg-5 col-xl-4">

                    <!-- Login Card -->
                    <div class="card border-0 shadow-sm rounded-4 p-4 p-sm-5 bg-white">
                        <div class="text-center mb-4">
                            <div class="icon-wrapper mx-auto mb-3" style="width: 52px; height: 52px;">
                                <i class="bi bi-person-fill-lock fs-3"></i>
                            </div>
                            <h3 class="fw-bold text-dark mb-1">Welcome Back</h3>
                            <p class="text-secondary small mb-0">Log in to your privacy-first fitness space</p>
                        </div>

                        <!-- Feedback Alerts -->
                        <c:if test="${not empty errorMessage}">
                            <div class="alert alert-danger d-flex align-items-center small py-2 px-3 rounded-3 mb-4" role="alert">
                                <i class="bi bi-exclamation-triangle-fill me-2 fs-6"></i>
                                <div><c:out value="${errorMessage}"/></div>
                            </div>
                        </c:if>

                        <c:if test="${param.logged_out == 'true'}">
                            <div class="alert alert-success d-flex align-items-center small py-2 px-3 rounded-3 mb-4" role="alert">
                                <i class="bi bi-check-circle-fill me-2 fs-6"></i>
                                <div>You have been safely logged out.</div>
                            </div>
                        </c:if>

                        <c:if test="${param.registered == 'true'}">
                            <div class="alert alert-success d-flex align-items-center small py-2 px-3 rounded-3 mb-4" role="alert">
                                <i class="bi bi-check-circle-fill me-2 fs-6"></i>
                                <div>Account created successfully! Please sign in below.</div>
                            </div>
                        </c:if>

                        <!-- Form -->
                        <form action="${pageContext.request.contextPath}/login" method="post" autocomplete="on">
                            <!-- CSRF Protection -->
                            <input type="hidden" name="_csrf" value="${sessionScope.csrfToken}">
                            <c:if test="${not empty redirect}">
                                <input type="hidden" name="redirect" value="<c:out value="${redirect}"/>">
                            </c:if>

                            <!-- Email Field -->
                            <div class="mb-3">
                                <label for="email" class="form-label small fw-semibold text-secondary">Email Address</label>
                                <div class="input-group">
                                    <span class="input-group-text bg-light border-end-0 text-muted"><i class="bi bi-envelope"></i></span>
                                    <input type="email" class="form-control border-start-0 ps-0" id="email" name="email"
                                           value="<c:out value="${email}"/>" placeholder="Enter your email address" required autofocus>
                                </div>
                            </div>

                            <!-- Password Field -->
                            <div class="mb-3">
                                <div class="d-flex justify-content-between align-items-center">
                                    <label for="password" class="form-label small fw-semibold text-secondary mb-1">Password</label>
                                    <a href="${pageContext.request.contextPath}/forgot-password" class="small text-decoration-none text-muted">Forgot?</a>
                                </div>
                                <div class="input-group">
                                    <span class="input-group-text bg-light border-end-0 text-muted"><i class="bi bi-key"></i></span>
                                    <input type="password" class="form-control border-start-0 border-end-0 px-0" id="password" name="password"
                                           placeholder="••••••••" required>
                                    <button class="btn btn-outline-secondary border-start-0 text-muted" type="button" id="togglePasswordBtn"
                                            aria-label="Toggle password visibility">
                                        <i class="bi bi-eye" id="togglePasswordIcon"></i>
                                    </button>
                                </div>
                            </div>

                            <!-- Submit Button -->
                            <button type="submit" class="btn btn-success w-100 py-2 fw-semibold rounded-3 shadow-sm mt-2">
                                <i class="bi bi-box-arrow-in-right me-1"></i> Sign In
                            </button>
                        </form>

                        <!-- Registration Prompt -->
                        <div class="text-center mt-4 pt-3 border-top">
                            <span class="text-secondary small">Don't have an account yet?</span>
                            <a href="${pageContext.request.contextPath}/register" class="fw-semibold text-success text-decoration-none small ms-1">
                                Create an account
                            </a>
                        </div>
                    </div>

                    <!-- Privacy Guarantee Note -->
                    <div class="text-center mt-4 text-muted small">
                        <i class="bi bi-shield-lock me-1 text-success"></i>
                        Protected by FitAura End-to-End Security & Personal Privacy
                    </div>

                </div>
            </div>
        </div>
    </main>

    <!-- Footer -->
    <footer class="bg-white border-top py-3 text-center text-muted small mt-auto">
        <div class="container">
            &copy; 2026 FitAura. All rights reserved.
        </div>
    </footer>

    <!-- Bootstrap 5 JavaScript Bundle -->
    <script src="https://cdn.jsdelivr.net/npm/bootstrap@5.3.3/dist/js/bootstrap.bundle.min.js" integrity="sha384-YvpcrYf0tY3lHB60NNkmXc5s9fDVZLESaAA55NDzOxhy9GkcIdslK1eN7N6jIeHz" crossorigin="anonymous"></script>

    <!-- Password visibility toggle script -->
    <script>
        document.getElementById('togglePasswordBtn').addEventListener('click', function () {
            const passwordInput = document.getElementById('password');
            const icon = document.getElementById('togglePasswordIcon');
            if (passwordInput.type === 'password') {
                passwordInput.type = 'text';
                icon.classList.remove('bi-eye');
                icon.classList.add('bi-eye-slash');
            } else {
                passwordInput.type = 'password';
                icon.classList.remove('bi-eye-slash');
                icon.classList.add('bi-eye');
            }
        });
    </script>
</body>
</html>
