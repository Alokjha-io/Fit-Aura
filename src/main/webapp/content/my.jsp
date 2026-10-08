<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ taglib prefix="c" uri="jakarta.tags.core" %>
<%@ taglib prefix="fmt" uri="jakarta.tags.fmt" %>
<!DOCTYPE html>
<html lang="en">
<head>
    <meta charset="UTF-8">
    <meta name="viewport" content="width=device-width, initial-scale=1.0">
    <title>My Submissions – FitAura Library</title>

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
                    <i class="bi bi-book me-1"></i> Public Library
                </a>
                <a href="${pageContext.request.contextPath}/content/create" class="btn btn-success btn-sm">
                    <i class="bi bi-plus-lg me-1"></i> Submit Article
                </a>
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

        <!-- Header Section -->
        <div class="d-flex flex-column flex-md-row justify-content-between align-items-md-center mb-4 gap-3">
            <div>
                <h1 class="h3 fw-bold text-dark mb-1">
                    <i class="bi bi-folder2-open text-primary me-2"></i>My Authored Content
                </h1>
                <p class="text-muted mb-0">Track the review status of your submitted guides, workouts, and tips.</p>
            </div>
            <div class="d-flex gap-2">
                <a href="${pageContext.request.contextPath}/content/create" class="btn btn-success">
                    <i class="bi bi-plus-lg me-1"></i> New Article
                </a>
            </div>
        </div>

        <!-- Submissions Table Card -->
        <div class="card border-0 shadow-sm bg-white mb-4">
            <div class="card-body p-0">
                <c:choose>
                    <c:when test="${empty myItems}">
                        <div class="text-center py-5 text-muted">
                            <i class="bi bi-file-earmark-plus fs-1 d-block mb-2 text-secondary"></i>
                            <h5 class="fw-bold text-dark">No Articles Submitted Yet</h5>
                            <p class="mb-3 small">Share your workout routines or nutrition advice with the community.</p>
                            <a href="${pageContext.request.contextPath}/content/create" class="btn btn-sm btn-success">Write an Article</a>
                        </div>
                    </c:when>
                    <c:otherwise>
                        <div class="table-responsive">
                            <table class="table table-hover align-middle mb-0">
                                <thead class="table-light small text-muted text-uppercase">
                                    <tr>
                                        <th class="ps-4">Title</th>
                                        <th>Category</th>
                                        <th>Status</th>
                                        <th>Submitted</th>
                                        <th class="pe-4 text-end">Actions</th>
                                    </tr>
                                </thead>
                                <tbody>
                                    <c:forEach var="item" items="${myItems}">
                                        <tr>
                                            <td class="ps-4">
                                                <a href="${pageContext.request.contextPath}/content/view?id=${item.content.contentId}" class="fw-bold text-dark text-decoration-none hover-text-success">
                                                    <c:out value="${item.content.title}"/>
                                                </a>
                                                <div class="text-muted small">${item.getSnippet(80)}</div>
                                            </td>
                                            <td>
                                                <span class="badge ${item.categoryBadgeClass}">
                                                    ${item.categoryDisplayName}
                                                </span>
                                            </td>
                                            <td>
                                                <c:choose>
                                                    <c:when test="${item.content.approvalStatus == 'APPROVED'}">
                                                        <span class="badge bg-success-subtle text-success border border-success-subtle px-2 py-1">
                                                            <i class="bi bi-check-circle me-1"></i> Published
                                                        </span>
                                                    </c:when>
                                                    <c:when test="${item.content.approvalStatus == 'PENDING'}">
                                                        <span class="badge bg-warning-subtle text-warning-emphasis border border-warning-subtle px-2 py-1">
                                                            <i class="bi bi-hourglass-split me-1"></i> Pending Review
                                                        </span>
                                                    </c:when>
                                                    <c:when test="${item.content.approvalStatus == 'REJECTED'}">
                                                        <span class="badge bg-danger-subtle text-danger border border-danger-subtle px-2 py-1">
                                                            <i class="bi bi-x-circle me-1"></i> Rejected (Editable)
                                                        </span>
                                                    </c:when>
                                                </c:choose>
                                            </td>
                                            <td class="text-muted small">
                                                <fmt:formatDate value="${item.content.createdAt}" pattern="MMM dd, yyyy" />
                                            </td>
                                            <td class="pe-4 text-end">
                                                <div class="d-inline-flex gap-1">
                                                    <a href="${pageContext.request.contextPath}/content/view?id=${item.content.contentId}" class="btn btn-sm btn-outline-secondary" title="View">
                                                        <i class="bi bi-eye"></i>
                                                    </a>
                                                    <a href="${pageContext.request.contextPath}/content/edit?id=${item.content.contentId}" class="btn btn-sm btn-outline-primary" title="Edit">
                                                        <i class="bi bi-pencil"></i>
                                                    </a>
                                                    <form action="${pageContext.request.contextPath}/content/delete" method="POST" class="d-inline" onsubmit="return confirm('Are you sure you want to delete this article?');">
                                                        <input type="hidden" name="csrfToken" value="${sessionScope.csrfToken}">
                                                        <input type="hidden" name="contentId" value="${item.content.contentId}">
                                                        <button type="submit" class="btn btn-sm btn-outline-danger" title="Delete">
                                                            <i class="bi bi-trash"></i>
                                                        </button>
                                                    </form>
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
        <div class="container d-flex justify-content-between align-items-center small text-muted">
            <div>&copy; 2026 FitAura Fitness Library</div>
            <a href="${pageContext.request.contextPath}/content" class="text-decoration-none text-muted">Explore Library</a>
        </div>
    </footer>

    <!-- Bootstrap 5 JS Bundle via CDN -->
    <script src="https://cdn.jsdelivr.net/npm/bootstrap@5.3.3/dist/js/bootstrap.bundle.min.js" integrity="sha384-YvpcrYf0tY3lHB60NNkmXc5s9fDVZLESaAA55NDzOxhy9GkcIdslK1eN7N6jIeHz" crossorigin="anonymous"></script>
</body>
</html>
