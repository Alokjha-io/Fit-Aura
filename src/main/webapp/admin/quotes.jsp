<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ taglib prefix="c" uri="jakarta.tags.core" %>
<!DOCTYPE html>
<html lang="en">
<head>
    <meta charset="UTF-8">
    <meta name="viewport" content="width=device-width, initial-scale=1.0">
    <title>Daily Motivation Quotes – FitAura Admin</title>
    <!-- Bootstrap 5 CSS via CDN -->
    <link href="https://cdn.jsdelivr.net/npm/bootstrap@5.3.3/dist/css/bootstrap.min.css" rel="stylesheet">
    <link rel="stylesheet" href="https://cdn.jsdelivr.net/npm/bootstrap-icons@1.11.3/font/bootstrap-icons.min.css">
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
            <div class="collapse navbar-collapse" id="adminNavbar">
                <ul class="navbar-nav me-auto mb-2 mb-lg-0 ms-lg-3">
                    <li class="nav-item"><a class="nav-link text-white-50" href="${pageContext.request.contextPath}/admin/dashboard"><i class="bi bi-speedometer2 me-1"></i> Dashboard</a></li>
                    <li class="nav-item"><a class="nav-link text-white-50" href="${pageContext.request.contextPath}/admin/users"><i class="bi bi-people me-1"></i> Users</a></li>
                    <li class="nav-item"><a class="nav-link text-white-50" href="${pageContext.request.contextPath}/admin/challenges"><i class="bi bi-trophy me-1"></i> Challenges</a></li>
                    <li class="nav-item"><a class="nav-link active fw-bold text-success" href="${pageContext.request.contextPath}/admin/quotes"><i class="bi bi-quote me-1"></i> Quotes</a></li>
                    <li class="nav-item"><a class="nav-link text-white-50" href="${pageContext.request.contextPath}/admin/social"><i class="bi bi-people-fill me-1"></i> Social</a></li>
                    <li class="nav-item"><a class="nav-link text-white-50" href="${pageContext.request.contextPath}/admin/statistics"><i class="bi bi-graph-up me-1"></i> Statistics</a></li>
                    <li class="nav-item"><a class="nav-link text-white-50" href="${pageContext.request.contextPath}/admin/activity"><i class="bi bi-clock-history me-1"></i> Activity Logs</a></li>
                    <li class="nav-item"><a class="nav-link text-white-50" href="${pageContext.request.contextPath}/admin/settings"><i class="bi bi-sliders me-1"></i> Settings</a></li>
                </ul>
                <div class="d-flex align-items-center gap-2">
                    <a href="${pageContext.request.contextPath}/user/dashboard" class="btn btn-outline-light btn-sm"><i class="bi bi-arrow-left me-1"></i> User App</a>
                    <a href="${pageContext.request.contextPath}/logout" class="btn btn-danger btn-sm px-3"><i class="bi bi-box-arrow-right me-1"></i> Sign Out</a>
                </div>
            </div>
        </div>
    </nav>

    <main class="container-fluid px-lg-5 my-4 flex-grow-1">

        <c:if test="${not empty param.success}">
            <div class="alert alert-success alert-dismissible fade show rounded-4 shadow-xs" role="alert">
                <i class="bi bi-check-circle-fill me-2"></i> Action completed successfully.
                <button type="button" class="btn-close" data-bs-dismiss="alert"></button>
            </div>
        </c:if>
        <c:if test="${not empty param.error}">
            <div class="alert alert-danger alert-dismissible fade show rounded-4 shadow-xs" role="alert">
                <i class="bi bi-exclamation-triangle-fill me-2"></i> <c:out value="${param.error}"/>
                <button type="button" class="btn-close" data-bs-dismiss="alert"></button>
            </div>
        </c:if>

        <div class="d-flex flex-column flex-md-row justify-content-between align-items-md-center mb-4 gap-3">
            <div>
                <h3 class="fw-bold text-dark mb-1"><i class="bi bi-quote text-success me-2"></i>Daily Fitness Motivation Quotes</h3>
                <p class="text-secondary mb-0 small">Schedule and manage daily inspiration displayed to all authenticated members.</p>
            </div>
            <button class="btn btn-success fw-semibold" data-bs-toggle="collapse" data-bs-target="#createQuoteCollapse">
                <i class="bi bi-plus-lg me-1"></i> Create / Schedule Quote
            </button>
        </div>

        <!-- Today's Active Quote Showcase -->
        <div class="card border-0 rounded-4 p-4 mb-4 shadow-xs text-white" style="background: linear-gradient(135deg, #0f172a 0%, #1e293b 60%, #064e3b 100%);">
            <div class="d-flex justify-content-between align-items-center mb-3">
                <span class="badge bg-warning text-dark fw-bold text-uppercase px-3 py-1.5 fs-7">
                    <i class="bi bi-star-fill me-1"></i> Today's Live Quote
                </span>
                <span class="small text-white-50"><i class="bi bi-calendar-check me-1"></i><c:out value="${todayQuote.quoteDate}"/></span>
            </div>
            <blockquote class="blockquote mb-2">
                <p class="fs-4 fw-medium mb-1 fst-italic">“<c:out value="${todayQuote.quoteText}"/>”</p>
                <footer class="blockquote-footer text-light opacity-75 fs-6 mt-1">
                    — <c:out value="${todayQuote.authorName != null ? todayQuote.authorName : 'FitAura'}"/>
                </footer>
            </blockquote>
        </div>

        <!-- Create Quote Collapse Form -->
        <div class="collapse mb-4" id="createQuoteCollapse">
            <div class="card border rounded-4 shadow-xs bg-white p-4">
                <h5 class="fw-bold text-dark mb-3"><i class="bi bi-calendar-plus text-success me-2"></i>Schedule New Quote</h5>
                <form action="${pageContext.request.contextPath}/admin/quotes" method="POST">
                    <input type="hidden" name="csrfToken" value="${sessionScope.csrfToken}"/>
                    <input type="hidden" name="action" value="create"/>

                    <div class="row g-3">
                        <div class="col-12">
                            <label class="form-label small fw-semibold">Quote Text *</label>
                            <textarea name="quoteText" rows="3" class="form-control rounded-3" placeholder="Enter motivational message..." required></textarea>
                        </div>
                        <div class="col-md-4">
                            <label class="form-label small fw-semibold">Author / Attribution</label>
                            <input type="text" name="authorName" class="form-control rounded-3" placeholder="e.g. Arnold Schwarzenegger, Marcus Aurelius"/>
                        </div>
                        <div class="col-md-4">
                            <label class="form-label small fw-semibold">Scheduled Date *</label>
                            <input type="date" name="quoteDate" class="form-control rounded-3" required/>
                        </div>
                        <div class="col-md-4">
                            <label class="form-label small fw-semibold">Status</label>
                            <select name="status" class="form-select rounded-3">
                                <option value="PUBLISHED" selected>PUBLISHED (Active on date)</option>
                                <option value="DRAFT">DRAFT (Review later)</option>
                            </select>
                        </div>
                        <div class="col-12 text-end">
                            <button type="button" class="btn btn-outline-secondary btn-sm me-2" data-bs-toggle="collapse" data-bs-target="#createQuoteCollapse">Cancel</button>
                            <button type="submit" class="btn btn-success btn-sm px-4 fw-semibold">Save Quote</button>
                        </div>
                    </div>
                </form>
            </div>
        </div>

        <!-- Upcoming Quotes -->
        <div class="card border rounded-4 shadow-xs bg-white mb-4">
            <div class="card-header bg-transparent py-3 border-bottom d-flex justify-content-between align-items-center">
                <h6 class="fw-bold text-dark mb-0"><i class="bi bi-calendar-week text-primary me-2"></i>Upcoming Scheduled Quotes</h6>
                <span class="badge bg-primary-subtle text-primary">${upcomingQuotes.size()} scheduled</span>
            </div>
            <div class="table-responsive">
                <table class="table align-middle table-hover mb-0">
                    <thead class="table-light small">
                        <tr>
                            <th>Date</th>
                            <th>Quote Text</th>
                            <th>Author</th>
                            <th>Status</th>
                            <th class="text-end">Actions</th>
                        </tr>
                    </thead>
                    <tbody>
                        <c:choose>
                            <c:when test="${not empty upcomingQuotes}">
                                <c:forEach var="q" items="${upcomingQuotes}">
                                    <tr>
                                        <td class="fw-semibold text-nowrap"><i class="bi bi-calendar-event text-secondary me-1"></i><c:out value="${q.quoteDate}"/></td>
                                        <td class="fst-italic text-truncate" style="max-width: 350px;">“<c:out value="${q.quoteText}"/>”</td>
                                        <td class="text-secondary small"><c:out value="${q.authorName}"/></td>
                                        <td>
                                            <span class="badge ${q.status == 'PUBLISHED' ? 'bg-success-subtle text-success' : 'bg-secondary-subtle text-secondary'}">
                                                <c:out value="${q.status}"/>
                                            </span>
                                        </td>
                                        <td class="text-end text-nowrap">
                                            <form action="${pageContext.request.contextPath}/admin/quotes" method="POST" class="d-inline">
                                                <input type="hidden" name="csrfToken" value="${sessionScope.csrfToken}"/>
                                                <input type="hidden" name="quoteId" value="${q.quoteId}"/>
                                                <input type="hidden" name="action" value="setToday"/>
                                                <button type="submit" class="btn btn-outline-warning btn-sm" title="Make active for today">
                                                    <i class="bi bi-star"></i> Today
                                                </button>
                                            </form>
                                            <a href="${pageContext.request.contextPath}/admin/quotes/edit?id=${q.quoteId}" class="btn btn-outline-primary btn-sm ms-1">
                                                <i class="bi bi-pencil"></i>
                                            </a>
                                            <form action="${pageContext.request.contextPath}/admin/quotes" method="POST" class="d-inline ms-1" onsubmit="return confirm('Delete this scheduled quote?');">
                                                <input type="hidden" name="csrfToken" value="${sessionScope.csrfToken}"/>
                                                <input type="hidden" name="quoteId" value="${q.quoteId}"/>
                                                <input type="hidden" name="action" value="delete"/>
                                                <button type="submit" class="btn btn-outline-danger btn-sm">
                                                    <i class="bi bi-trash"></i>
                                                </button>
                                            </form>
                                        </td>
                                    </tr>
                                </c:forEach>
                            </c:when>
                            <c:otherwise>
                                <tr>
                                    <td colspan="5" class="text-center py-4 text-muted small">No upcoming quotes scheduled yet. Prepare ahead using the form above.</td>
                                </tr>
                            </c:otherwise>
                        </c:choose>
                    </tbody>
                </table>
            </div>
        </div>

        <!-- All Quotes Library -->
        <div class="card border rounded-4 shadow-xs bg-white">
            <div class="card-header bg-transparent py-3 border-bottom d-flex justify-content-between align-items-center">
                <h6 class="fw-bold text-dark mb-0"><i class="bi bi-collection text-secondary me-2"></i>Quote Archive & History</h6>
                <span class="badge bg-secondary-subtle text-secondary">${totalQuotes} Total</span>
            </div>
            <div class="table-responsive">
                <table class="table align-middle table-hover mb-0">
                    <thead class="table-light small">
                        <tr>
                            <th>ID</th>
                            <th>Date</th>
                            <th>Quote</th>
                            <th>Author</th>
                            <th>Status</th>
                            <th class="text-end">Actions</th>
                        </tr>
                    </thead>
                    <tbody>
                        <c:forEach var="q" items="${allQuotes}">
                            <tr>
                                <td class="text-muted small">#${q.quoteId}</td>
                                <td class="text-nowrap small"><c:out value="${q.quoteDate}"/></td>
                                <td class="fst-italic text-truncate" style="max-width: 400px;">“<c:out value="${q.quoteText}"/>”</td>
                                <td class="small text-secondary"><c:out value="${q.authorName}"/></td>
                                <td>
                                    <span class="badge ${q.status == 'PUBLISHED' ? 'bg-success-subtle text-success' : (q.status == 'DRAFT' ? 'bg-warning-subtle text-warning' : 'bg-secondary-subtle text-secondary')}">
                                        <c:out value="${q.status}"/>
                                    </span>
                                </td>
                                <td class="text-end text-nowrap">
                                    <a href="${pageContext.request.contextPath}/admin/quotes/edit?id=${q.quoteId}" class="btn btn-outline-secondary btn-sm">
                                        <i class="bi bi-pencil"></i> Edit
                                    </a>
                                </td>
                            </tr>
                        </c:forEach>
                    </tbody>
                </table>
            </div>
        </div>

    </main>

    <!-- Bootstrap 5 JS -->
    <script src="https://cdn.jsdelivr.net/npm/bootstrap@5.3.3/dist/js/bootstrap.bundle.min.js"></script>
</body>
</html>
