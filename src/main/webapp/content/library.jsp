<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ taglib prefix="c" uri="jakarta.tags.core" %>
<%@ taglib prefix="fmt" uri="jakarta.tags.fmt" %>
<!DOCTYPE html>
<html lang="en">
<head>
    <meta charset="UTF-8">
    <meta name="viewport" content="width=device-width, initial-scale=1.0">
    <title>Fitness Library – FitAura</title>
    <meta name="description" content="Explore verified workouts, exercises, nutrition advice, and expert fitness guides.">

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

            <button class="navbar-toggler" type="button" data-bs-toggle="collapse" data-bs-target="#libraryNavbar" aria-controls="libraryNavbar" aria-expanded="false" aria-label="Toggle navigation">
                <span class="navbar-toggler-icon"></span>
            </button>

            <div class="collapse navbar-collapse" id="libraryNavbar">
                <ul class="navbar-nav me-auto mb-2 mb-lg-0 ms-lg-4">
                    <li class="nav-item">
                        <a class="nav-link fw-medium text-dark" href="${pageContext.request.contextPath}/user/dashboard">
                            <i class="bi bi-house-door me-1"></i> Dashboard
                        </a>
                    </li>
                    <li class="nav-item">
                        <a class="nav-link fw-medium text-dark" href="${pageContext.request.contextPath}/workouts">
                            <i class="bi bi-activity me-1"></i> Workouts
                        </a>
                    </li>
                    <li class="nav-item">
                        <a class="nav-link fw-medium text-dark" href="${pageContext.request.contextPath}/goals">
                            <i class="bi bi-bullseye me-1"></i> Goals
                        </a>
                    </li>
                    <li class="nav-item">
                        <a class="nav-link fw-medium text-dark" href="${pageContext.request.contextPath}/progress">
                            <i class="bi bi-graph-up-arrow me-1"></i> Progress
                        </a>
                    </li>
                    <li class="nav-item">
                        <a class="nav-link fw-medium text-dark" href="${pageContext.request.contextPath}/guidance">
                            <i class="bi bi-compass me-1"></i> Guidance
                        </a>
                    </li>
                    <li class="nav-item">
                        <a class="nav-link active fw-bold text-success" aria-current="page" href="${pageContext.request.contextPath}/content">
                            <i class="bi bi-book me-1"></i> Library
                        </a>
                    </li>
                    <li class="nav-item">
                        <a class="nav-link fw-medium text-dark" href="${pageContext.request.contextPath}/challenges">
                            <i class="bi bi-trophy me-1"></i> Challenges
                        </a>
                    </li>
                    <li class="nav-item">
                        <a class="nav-link fw-medium text-dark" href="${pageContext.request.contextPath}/gamification">
                            <i class="bi bi-award me-1"></i> Badges
                        </a>
                    </li>
                    <li class="nav-item">
                        <a class="nav-link fw-medium text-dark" href="${pageContext.request.contextPath}/leaderboard">
                            <i class="bi bi-bar-chart-line me-1"></i> Leaderboard
                        </a>
                    </li>
                </ul>

                <div class="d-flex align-items-center gap-2">
                    <a href="${pageContext.request.contextPath}/content/my" class="btn btn-outline-primary btn-sm">
                        <i class="bi bi-folder2-open me-1"></i> My Submissions
                    </a>
                    <a href="${pageContext.request.contextPath}/content/create" class="btn btn-success btn-sm">
                        <i class="bi bi-plus-lg me-1"></i> Submit Article
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
        <c:if test="${not empty param.success}">
            <div class="alert alert-success alert-dismissible fade show d-flex align-items-center shadow-sm" role="alert">
                <i class="bi bi-check-circle-fill flex-shrink-0 me-2 fs-5"></i>
                <div><c:out value="${param.success}"/></div>
                <button type="button" class="btn-close" data-bs-dismiss="alert" aria-label="Close"></button>
            </div>
        </c:if>

        <!-- Header Section -->
        <div class="d-flex flex-column flex-md-row justify-content-between align-items-md-center mb-4 gap-3">
            <div>
                <h1 class="h3 fw-bold text-dark mb-1">
                    <i class="bi bi-book-half text-success me-2"></i>Fitness Knowledge Library
                </h1>
                <p class="text-muted mb-0">Explore workouts, exercises, nutrition guidance and practical fitness tips verified by coaches.</p>
            </div>
            <div class="d-flex gap-2">
                <a href="${pageContext.request.contextPath}/content/create" class="btn btn-success">
                    <i class="bi bi-pencil-square me-1"></i> Share Knowledge
                </a>
            </div>
        </div>

        <!-- Search & Category Filters -->
        <div class="card border-0 shadow-sm mb-4">
            <div class="card-body p-3">
                <form action="${pageContext.request.contextPath}/content" method="GET" class="row g-2 align-items-center">
                    <div class="col-md-6">
                        <div class="input-group">
                            <span class="input-group-text bg-light border-end-0"><i class="bi bi-search text-muted"></i></span>
                            <input type="text" name="search" class="form-control border-start-0" placeholder="Search guides, workouts, nutrition..." value="${searchQuery}">
                        </div>
                    </div>
                    <div class="col-md-4">
                        <select name="category" class="form-select">
                            <option value="">All Categories</option>
                            <option value="WORKOUT" ${selectedCategory == 'WORKOUT' ? 'selected' : ''}>Workouts</option>
                            <option value="EXERCISE" ${selectedCategory == 'EXERCISE' ? 'selected' : ''}>Exercise Techniques</option>
                            <option value="NUTRITION" ${selectedCategory == 'NUTRITION' ? 'selected' : ''}>Nutrition & Diet</option>
                            <option value="FITNESS_TIP" ${selectedCategory == 'FITNESS_TIP' ? 'selected' : ''}>Fitness Tips</option>
                            <option value="GUIDE" ${selectedCategory == 'GUIDE' ? 'selected' : ''}>Guides</option>
                        </select>
                    </div>
                    <div class="col-md-2 d-flex gap-1">
                        <button type="submit" class="btn btn-primary w-100">
                            <i class="bi bi-funnel me-1"></i> Filter
                        </button>
                        <a href="${pageContext.request.contextPath}/content" class="btn btn-outline-secondary" title="Reset">
                            <i class="bi bi-arrow-counterclockwise"></i>
                        </a>
                    </div>
                </form>
            </div>
        </div>

        <!-- Library Content Grid -->
        <c:choose>
            <c:when test="${empty items}">
                <div class="card border-0 shadow-sm text-center py-5 text-muted mb-4">
                    <i class="bi bi-journal-x fs-1 d-block mb-3 text-secondary"></i>
                    <h5 class="fw-bold text-dark">No Articles Found</h5>
                    <p class="mb-3 small">No published fitness content matches your search criteria.</p>
                    <div>
                        <a href="${pageContext.request.contextPath}/content/create" class="btn btn-sm btn-success">Be the First to Publish</a>
                    </div>
                </div>
            </c:when>
            <c:otherwise>
                <div class="row g-4 mb-4">
                    <c:forEach var="item" items="${items}">
                        <div class="col-md-6 col-lg-4">
                            <div class="card h-100 border-0 shadow-sm hover-shadow transition-all bg-white d-flex flex-column">
                                <div class="card-body p-4 d-flex flex-column">
                                    <div class="d-flex justify-content-between align-items-center mb-2">
                                        <span class="badge ${item.categoryBadgeClass} px-2 py-1">
                                            <i class="bi ${item.categoryIconClass} me-1"></i> ${item.categoryDisplayName}
                                        </span>
                                        <span class="text-muted small">
                                            <i class="bi bi-clock me-1"></i> ${item.readingTimeMinutes} min read
                                        </span>
                                    </div>

                                    <h5 class="card-title fw-bold text-dark mt-2 mb-2">
                                        <a href="${pageContext.request.contextPath}/content/view?id=${item.content.contentId}" class="text-dark text-decoration-none hover-text-success">
                                            <c:out value="${item.content.title}"/>
                                        </a>
                                    </h5>

                                    <p class="card-text text-muted small flex-grow-1">
                                        ${item.getSnippet(140)}
                                    </p>

                                    <div class="pt-3 border-top d-flex justify-content-between align-items-center mt-auto">
                                        <div class="d-flex align-items-center gap-2">
                                            <div class="rounded-circle bg-light border p-1 text-secondary d-flex align-items-center justify-content-center" style="width: 26px; height: 26px;">
                                                <i class="bi bi-person-fill small"></i>
                                            </div>
                                            <span class="small text-muted"><c:out value="${item.authorDisplayName}"/></span>
                                        </div>
                                        <a href="${pageContext.request.contextPath}/content/view?id=${item.content.contentId}" class="btn btn-sm btn-outline-success">
                                            Read <i class="bi bi-arrow-right ms-1"></i>
                                        </a>
                                    </div>
                                </div>
                            </div>
                        </div>
                    </c:forEach>
                </div>

                <!-- Pagination -->
                <c:if test="${totalPages > 1}">
                    <div class="d-flex justify-content-between align-items-center bg-white p-3 rounded shadow-sm mb-4">
                        <span class="text-muted small">Page ${currentPage} of ${totalPages} (${totalItems} articles)</span>
                        <ul class="pagination pagination-sm mb-0">
                            <li class="page-item ${currentPage <= 1 ? 'disabled' : ''}">
                                <a class="page-link" href="${pageContext.request.contextPath}/content?page=${currentPage - 1}&search=${searchQuery}&category=${selectedCategory}">Previous</a>
                            </li>
                            <c:forEach var="p" begin="1" end="${totalPages}">
                                <li class="page-item ${currentPage == p ? 'active' : ''}">
                                    <a class="page-link" href="${pageContext.request.contextPath}/content?page=${p}&search=${searchQuery}&category=${selectedCategory}">${p}</a>
                                </li>
                            </c:forEach>
                            <li class="page-item ${currentPage >= totalPages ? 'disabled' : ''}">
                                <a class="page-link" href="${pageContext.request.contextPath}/content?page=${currentPage + 1}&search=${searchQuery}&category=${selectedCategory}">Next</a>
                            </li>
                        </ul>
                    </div>
                </c:if>
            </c:otherwise>
        </c:choose>

    </main>

    <!-- Footer -->
    <footer class="bg-white border-top py-3 mt-auto">
        <div class="container d-flex flex-column flex-sm-row justify-content-between align-items-center small text-muted">
            <div>&copy; 2026 FitAura Fitness Library</div>
            <div class="mt-2 mt-sm-0">
                <a href="${pageContext.request.contextPath}/content/my" class="text-decoration-none text-muted me-3">My Submissions</a>
                <a href="${pageContext.request.contextPath}/guidance" class="text-decoration-none text-muted">Guidance</a>
            </div>
        </div>
    </footer>

    <!-- Bootstrap 5 JS Bundle via CDN -->
    <script src="https://cdn.jsdelivr.net/npm/bootstrap@5.3.3/dist/js/bootstrap.bundle.min.js" integrity="sha384-YvpcrYf0tY3lHB60NNkmXc5s9fDVZLESaAA55NDzOxhy9GkcIdslK1eN7N6jIeHz" crossorigin="anonymous"></script>
</body>
</html>
