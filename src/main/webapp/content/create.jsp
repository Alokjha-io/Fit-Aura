<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ taglib prefix="c" uri="jakarta.tags.core" %>
<!DOCTYPE html>
<html lang="en">
<head>
    <meta charset="UTF-8">
    <meta name="viewport" content="width=device-width, initial-scale=1.0">
    <title>Submit Fitness Article – FitAura</title>

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

            <div class="d-flex align-items-center gap-2 ms-auto">
                <a href="${pageContext.request.contextPath}/content" class="btn btn-outline-secondary btn-sm">
                    <i class="bi bi-arrow-left me-1"></i> Library
                </a>
            </div>
        </div>
    </nav>

    <!-- Main Content Container -->
    <main class="container my-5 flex-grow-1" style="max-width: 800px;">

        <!-- Header -->
        <div class="mb-4 text-center">
            <h1 class="h3 fw-bold text-dark mb-1">
                <i class="bi bi-pencil-square text-success me-2"></i>Contribute to Fitness Library
            </h1>
            <p class="text-muted">Share your training wisdom, exercise breakdowns, or nutrition tips with the community.</p>
        </div>

        <!-- Alerts -->
        <c:if test="${not empty errorMessage}">
            <div class="alert alert-danger alert-dismissible fade show d-flex align-items-center shadow-sm" role="alert">
                <i class="bi bi-exclamation-triangle-fill flex-shrink-0 me-2 fs-5"></i>
                <div>${errorMessage}</div>
                <button type="button" class="btn-close" data-bs-dismiss="alert" aria-label="Close"></button>
            </div>
        </c:if>

        <!-- Form Card -->
        <div class="card border-0 shadow-sm bg-white p-4 p-md-5 mb-4">
            <form action="${pageContext.request.contextPath}/content/create" method="POST">
                <input type="hidden" name="csrfToken" value="${sessionScope.csrfToken}">

                <!-- Title -->
                <div class="mb-3">
                    <label class="form-label fw-bold text-dark">Article Title <span class="text-danger">*</span></label>
                    <input type="text" name="title" class="form-control form-control-lg" placeholder="e.g. Master the Perfect Barbell Squat" value="${enteredTitle}" required minlength="3" maxlength="200">
                    <div class="form-text">Choose a descriptive and engaging title (3–200 characters).</div>
                </div>

                <!-- Category -->
                <div class="mb-3">
                    <label class="form-label fw-bold text-dark">Content Category <span class="text-danger">*</span></label>
                    <select name="category" class="form-select" required>
                        <option value="">Select a Category...</option>
                        <option value="WORKOUT" ${enteredCategory == 'WORKOUT' ? 'selected' : ''}>Workout Routine (e.g. HIIT, Upper Body Split)</option>
                        <option value="EXERCISE" ${enteredCategory == 'EXERCISE' ? 'selected' : ''}>Exercise Technique (e.g. Form tips, Movement breakdowns)</option>
                        <option value="NUTRITION" ${enteredCategory == 'NUTRITION' ? 'selected' : ''}>Nutrition & Diet (e.g. Protein timing, Hydration)</option>
                        <option value="FITNESS_TIP" ${enteredCategory == 'FITNESS_TIP' ? 'selected' : ''}>Fitness Tip (e.g. Recovery, Stretching, Sleep)</option>
                        <option value="GUIDE" ${enteredCategory == 'GUIDE' ? 'selected' : ''}>Comprehensive Guide (e.g. Beginner weightlifting blueprint)</option>
                    </select>
                </div>

                <!-- Content Text -->
                <div class="mb-4">
                    <label class="form-label fw-bold text-dark">Article Content <span class="text-danger">*</span></label>
                    <textarea name="contentText" class="form-control" rows="10" placeholder="Write your fitness instructions, explanations, step-by-step guidance..." required minlength="10" maxlength="20000">${enteredContent}</textarea>
                    <div class="form-text">Provide thorough, actionable, and safe fitness advice (minimum 10 characters).</div>
                </div>

                <!-- Moderation Notice -->
                <div class="alert alert-light border d-flex align-items-center gap-2 mb-4">
                    <i class="bi bi-shield-check text-success fs-4"></i>
                    <div class="small text-muted">
                        <strong>Quality Review Policy:</strong> To ensure high standards and factual integrity, user-submitted content enters a <em>Pending Review</em> state and is reviewed by an administrator before appearing publicly.
                    </div>
                </div>

                <!-- Submit Button -->
                <div class="d-flex justify-content-between align-items-center">
                    <a href="${pageContext.request.contextPath}/content" class="btn btn-outline-secondary">Cancel</a>
                    <button type="submit" class="btn btn-success px-4">
                        <i class="bi bi-send me-1"></i> Submit for Review
                    </button>
                </div>
            </form>
        </div>

    </main>

    <!-- Footer -->
    <footer class="bg-white border-top py-3 mt-auto">
        <div class="container d-flex justify-content-between align-items-center small text-muted">
            <div>&copy; 2026 FitAura Fitness Library</div>
            <a href="${pageContext.request.contextPath}/content" class="text-decoration-none text-muted">Browse Library</a>
        </div>
    </footer>

    <!-- Bootstrap 5 JS Bundle via CDN -->
    <script src="https://cdn.jsdelivr.net/npm/bootstrap@5.3.3/dist/js/bootstrap.bundle.min.js" integrity="sha384-YvpcrYf0tY3lHB60NNkmXc5s9fDVZLESaAA55NDzOxhy9GkcIdslK1eN7N6jIeHz" crossorigin="anonymous"></script>
</body>
</html>
