<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ taglib prefix="c" uri="jakarta.tags.core" %>
<!DOCTYPE html>
<html lang="en">
<head>
    <meta charset="UTF-8">
    <meta name="viewport" content="width=device-width, initial-scale=1.0">
    <title><c:out value="${socialProfile.displayName}"/> – Community Profile</title>
    <!-- Bootstrap 5 CSS via CDN -->
    <link href="https://cdn.jsdelivr.net/npm/bootstrap@5.3.3/dist/css/bootstrap.min.css" rel="stylesheet">
    <link rel="stylesheet" href="https://cdn.jsdelivr.net/npm/bootstrap-icons@1.11.3/font/bootstrap-icons.min.css">
    <link rel="stylesheet" href="${pageContext.request.contextPath}/css/fitaura.css">
</head>
<body class="bg-light d-flex flex-column min-vh-100">

    <nav class="navbar navbar-expand-lg navbar-light bg-white border-bottom py-3">
        <div class="container">
            <a class="navbar-brand brand-badge d-flex align-items-center" href="${pageContext.request.contextPath}/social">
                <i class="bi bi-arrow-left me-2 text-dark"></i>
                <span>Fit<span class="text-success">Aura</span> Community</span>
            </a>
            <a href="${pageContext.request.contextPath}/social" class="btn btn-outline-secondary btn-sm">Back to Hub</a>
        </div>
    </nav>

    <main class="container my-4 flex-grow-1" style="max-width: 760px;">

        <!-- Profile Card -->
        <div class="card border rounded-4 p-4 p-md-5 bg-white shadow-xs mb-4">
            <div class="d-flex flex-column flex-sm-row justify-content-between align-items-sm-center gap-3 pb-4 border-bottom">
                <div class="d-flex align-items-center gap-3">
                    <div class="rounded-circle bg-success text-white d-flex align-items-center justify-content-center fs-2" style="width: 64px; height: 64px;">
                        <i class="bi bi-person-fill"></i>
                    </div>
                    <div>
                        <h3 class="fw-bold text-dark mb-1"><c:out value="${socialProfile.displayName}"/></h3>
                        <div class="d-flex align-items-center gap-2">
                            <span class="badge bg-success-subtle text-success small"><i class="bi bi-shield-check me-1"></i>Verified Social Athlete</span>
                            <span class="text-muted small">&bull; ${socialProfile.connectionCount} Connections</span>
                        </div>
                    </div>
                </div>

                <c:if test="${sessionScope.authenticatedUserId != socialProfile.userId}">
                    <div>
                        <c:choose>
                            <c:when test="${socialProfile.connection}">
                                <span class="badge bg-light text-success border px-3 py-2 fw-semibold">
                                    <i class="bi bi-check2 me-1"></i> Connected
                                </span>
                            </c:when>
                            <c:when test="${socialProfile.hasPendingConnectionRequest}">
                                <span class="badge bg-warning-subtle text-warning-emphasis px-3 py-2 fw-semibold">
                                    <i class="bi bi-hourglass-split me-1"></i> Request Pending
                                </span>
                            </c:when>
                            <c:otherwise>
                                <form action="${pageContext.request.contextPath}/social" method="POST" class="d-inline">
                                    <input type="hidden" name="csrfToken" value="${sessionScope.csrfToken}"/>
                                    <input type="hidden" name="action" value="sendConnection"/>
                                    <input type="hidden" name="receiverId" value="${socialProfile.userId}"/>
                                    <button type="submit" class="btn btn-success btn-sm px-3 fw-semibold">
                                        <i class="bi bi-person-plus me-1"></i> Connect
                                    </button>
                                </form>
                            </c:otherwise>
                        </c:choose>
                    </div>
                </c:if>
            </div>

            <!-- Public Metric Summary (ZERO private stats) -->
            <div class="row g-3 py-4 text-center border-bottom">
                <div class="col-4">
                    <small class="text-muted d-block mb-1">Community Rank</small>
                    <div class="fs-4 fw-bold text-dark">#${socialProfile.socialRank != null ? socialProfile.socialRank : '—'}</div>
                </div>
                <div class="col-4">
                    <small class="text-muted d-block mb-1">Total Points</small>
                    <div class="fs-4 fw-bold text-success">${socialProfile.totalPoints}</div>
                </div>
                <div class="col-4">
                    <small class="text-muted d-block mb-1">Current Streak</small>
                    <div class="fs-4 fw-bold text-danger">${socialProfile.currentStreakDays}d 🔥</div>
                </div>
            </div>

            <!-- Showcase Earned Achievements -->
            <div class="pt-4">
                <h6 class="fw-bold text-dark mb-3"><i class="bi bi-award-fill text-warning me-2"></i>Showcase Achievements (${socialProfile.achievementCount})</h6>
                <c:choose>
                    <c:when test="${not empty socialProfile.showcasedAchievements}">
                        <div class="row g-2">
                            <c:forEach var="ach" items="${socialProfile.showcasedAchievements}">
                                <div class="col-md-6">
                                    <div class="p-3 bg-light rounded-3 border d-flex align-items-center gap-3">
                                        <div class="fs-3 text-warning"><i class="bi bi-trophy"></i></div>
                                        <div>
                                            <div class="fw-bold text-dark small"><c:out value="${ach.name}"/></div>
                                            <div class="text-muted text-xs"><c:out value="${ach.description}"/></div>
                                        </div>
                                    </div>
                                </div>
                            </c:forEach>
                        </div>
                    </c:when>
                    <c:otherwise>
                        <p class="text-muted small">No showcase achievements unlocked yet.</p>
                    </c:otherwise>
                </c:choose>
            </div>
        </div>

    </main>

    <script src="https://cdn.jsdelivr.net/npm/bootstrap@5.3.3/dist/js/bootstrap.bundle.min.js"></script>
</body>
</html>
