<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ taglib prefix="c" uri="jakarta.tags.core" %>
<!DOCTYPE html>
<html lang="en">
<head>
    <meta charset="UTF-8">
    <meta name="viewport" content="width=device-width, initial-scale=1.0">
    <title>Edit Article – FitAura</title>

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
                <a href="${pageContext.request.contextPath}/content/my" class="btn btn-outline-secondary btn-sm">
                    <i class="bi bi-arrow-left me-1"></i> My Submissions
                </a>
            </div>
        </div>
    </nav>

    <!-- Main Content Container -->
    <main class="container my-5 flex-grow-1" style="max-width: 800px;">

        <!-- Header -->
        <div class="mb-4 text-center">
            <h1 class="h3 fw-bold text-dark mb-1">
                <i class="bi bi-pencil-square text-primary me-2"></i>Edit Article
            </h1>
            <p class="text-muted">Revise your content and submit for updated review.</p>
        </div>

        <!-- Form Card -->
        <div class="card border-0 shadow-sm bg-white p-4 p-md-5 mb-4">
            <form action="${pageContext.request.contextPath}/content/edit" method="POST">
                <input type="hidden" name="csrfToken" value="${sessionScope.csrfToken}">
                <input type="hidden" name="contentId" value="${item.content.contentId}">

                <!-- Title -->
                <div class="mb-3">
                    <label class="form-label fw-bold text-dark">Article Title <span class="text-danger">*</span></label>
                    <input type="text" name="title" class="form-control form-control-lg" value="<c:out value='${item.content.title}'/>" required minlength="3" maxlength="200">
                </div>

                <!-- Category -->
                <div class="mb-3">
                    <label class="form-label fw-bold text-dark">Content Category <span class="text-danger">*</span></label>
                    <select name="category" class="form-select" required>
                        <option value="WORKOUT" ${item.content.category == 'WORKOUT' ? 'selected' : ''}>Workout Routine</option>
                        <option value="EXERCISE" ${item.content.category == 'EXERCISE' ? 'selected' : ''}>Exercise Technique</option>
                        <option value="NUTRITION" ${item.content.category == 'NUTRITION' ? 'selected' : ''}>Nutrition & Diet</option>
                        <option value="FITNESS_TIP" ${item.content.category == 'FITNESS_TIP' ? 'selected' : ''}>Fitness Tip</option>
                        <option value="GUIDE" ${item.content.category == 'GUIDE' ? 'selected' : ''}>Comprehensive Guide</option>
                    </select>
                </div>

                <!-- Content Text -->
                <div class="mb-4">
                    <label class="form-label fw-bold text-dark">Article Content <span class="text-danger">*</span></label>
                    <textarea name="contentText" class="form-control" rows="12" required minlength="10" maxlength="20000"><c:out value="${item.content.contentText}"/></textarea>
                </div>

                <!-- Re-review Notice -->
                <div class="alert alert-warning border-warning d-flex align-items-center gap-2 mb-4">
                    <i class="bi bi-exclamation-triangle-fill text-warning-emphasis fs-4"></i>
                    <div class="small">
                        <strong>Re-review Requirement:</strong> Saving changes to an article will place it back into <em>Pending Review</em> status to ensure all published material complies with coaching standards.
                    </div>
                </div>

                <!-- Submit Buttons -->
                <div class="d-flex justify-content-between align-items-center">
                    <a href="${pageContext.request.contextPath}/content/my" class="btn btn-outline-secondary">Cancel</a>
                    <button type="submit" class="btn btn-primary px-4">
                        <i class="bi bi-save me-1"></i> Save and Resubmit
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
