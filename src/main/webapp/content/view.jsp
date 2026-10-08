<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ taglib prefix="c" uri="jakarta.tags.core" %>
<%@ taglib prefix="fmt" uri="jakarta.tags.fmt" %>
<!DOCTYPE html>
<html lang="en">
<head>
    <meta charset="UTF-8">
    <meta name="viewport" content="width=device-width, initial-scale=1.0">
    <title><c:out value="${item.content.title}"/> – FitAura Library</title>
    <meta name="description" content="${item.getSnippet(150)}">

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
                <c:if test="${item.owner}">
                    <a href="${pageContext.request.contextPath}/content/edit?id=${item.content.contentId}" class="btn btn-outline-primary btn-sm">
                        <i class="bi bi-pencil me-1"></i> Edit
                    </a>
                </c:if>
            </div>
        </div>
    </nav>

    <!-- Main Content Container -->
    <main class="container my-5 flex-grow-1" style="max-width: 800px;">

        <!-- Back Link & Category Badge -->
        <div class="d-flex justify-content-between align-items-center mb-3">
            <a href="${pageContext.request.contextPath}/content" class="text-decoration-none text-muted small">
                <i class="bi bi-arrow-left me-1"></i> Back to Fitness Library
            </a>
            <span class="badge ${item.categoryBadgeClass} px-3 py-1.5 rounded-pill fs-6">
                <i class="bi ${item.categoryIconClass} me-1"></i> ${item.categoryDisplayName}
            </span>
        </div>

        <!-- Article Card -->
        <article class="card border-0 shadow-sm bg-white p-4 p-md-5 mb-4">
            <!-- Title -->
            <h1 class="display-6 fw-bold text-dark mb-3">
                <c:out value="${item.content.title}"/>
            </h1>

            <!-- Author & Metadata Meta Strip -->
            <div class="d-flex flex-wrap align-items-center justify-content-between gap-2 py-3 border-top border-bottom mb-4 text-muted small">
                <div class="d-flex align-items-center gap-2">
                    <div class="rounded-circle bg-success-subtle text-success p-2 d-flex align-items-center justify-content-center" style="width: 34px; height: 34px;">
                        <i class="bi bi-person-fill"></i>
                    </div>
                    <div>
                        <span class="fw-bold text-dark d-block"><c:out value="${item.authorDisplayName}"/></span>
                        <span class="text-muted small">Contributor</span>
                    </div>
                </div>

                <div class="d-flex align-items-center gap-3">
                    <span><i class="bi bi-clock me-1"></i> ${item.readingTimeMinutes} min read</span>
                    <span><i class="bi bi-calendar-event me-1"></i> <fmt:formatDate value="${item.content.createdAt}" pattern="MMM dd, yyyy" /></span>
                </div>
            </div>

            <!-- Moderation notice for non-approved states -->
            <c:if test="${item.content.approvalStatus != 'APPROVED'}">
                <div class="alert ${item.content.approvalStatus == 'PENDING' ? 'alert-warning' : 'alert-danger'} mb-4 d-flex align-items-center gap-2">
                    <i class="bi bi-shield-exclamation fs-4"></i>
                    <div>
                        <strong>Moderation Status: ${item.content.approvalStatus}</strong>
                        <div class="small">
                            <c:choose>
                                <c:when test="${item.content.approvalStatus == 'PENDING'}">
                                    This article is pending administrator review and is not publicly discoverable yet.
                                </c:when>
                                <c:otherwise>
                                    This article was rejected during review. You may edit and resubmit it.
                                </c:otherwise>
                            </c:choose>
                        </div>
                    </div>
                </div>
            </c:if>

            <!-- Article Body -->
            <div class="article-body text-dark lh-lg" style="font-size: 1.08rem; white-space: pre-line;">
                <c:out value="${item.content.contentText}"/>
            </div>
        </article>

        <!-- Suggested Actions -->
        <div class="card border-0 shadow-sm bg-white p-4 text-center">
            <h5 class="fw-bold text-dark mb-1">Ready to put this knowledge to work?</h5>
            <p class="text-muted small mb-3">Track your progress and log your fitness routines with FitAura.</p>
            <div class="d-flex justify-content-center gap-2">
                <a href="${pageContext.request.contextPath}/workouts/add" class="btn btn-success">
                    <i class="bi bi-plus-lg me-1"></i> Log Workout
                </a>
                <a href="${pageContext.request.contextPath}/content" class="btn btn-outline-secondary">
                    Explore More Articles
                </a>
            </div>
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
