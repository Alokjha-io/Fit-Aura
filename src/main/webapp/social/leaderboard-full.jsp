<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ taglib prefix="c" uri="jakarta.tags.core" %>
<!DOCTYPE html>
<html lang="en">
<head>
    <meta charset="UTF-8">
    <meta name="viewport" content="width=device-width, initial-scale=1.0">
    <title><c:out value="${periodLabel}"/> Leaderboard – FitAura</title>
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
                <span>Fit<span class="text-success">Aura</span> Leaderboard</span>
            </a>
            <div class="d-flex gap-2">
                <a href="${pageContext.request.contextPath}/social/leaderboard?period=weekly" class="btn btn-sm ${periodLabel == 'Weekly' ? 'btn-success' : 'btn-outline-secondary'}">Weekly</a>
                <a href="${pageContext.request.contextPath}/social/leaderboard?period=monthly" class="btn btn-sm ${periodLabel == 'Monthly' ? 'btn-success' : 'btn-outline-secondary'}">Monthly</a>
                <a href="${pageContext.request.contextPath}/social/leaderboard?period=alltime" class="btn btn-sm ${periodLabel == 'All-Time' ? 'btn-success' : 'btn-outline-secondary'}">All-Time</a>
            </div>
        </div>
    </nav>

    <main class="container my-4 flex-grow-1" style="max-width: 860px;">
        <div class="card border rounded-4 shadow-xs bg-white">
            <div class="card-header bg-transparent py-3 border-bottom d-flex justify-content-between align-items-center">
                <h5 class="fw-bold text-dark mb-0"><i class="bi bi-trophy-fill text-warning me-2"></i><c:out value="${periodLabel}"/> Social Rankings</h5>
                <small class="text-muted">Strictly includes athletes who opted into Social Mode</small>
            </div>
            <div class="table-responsive">
                <table class="table align-middle table-hover mb-0">
                    <thead class="table-light small">
                        <tr>
                            <th style="width: 70px;">Rank</th>
                            <th>Athlete</th>
                            <th>Workouts</th>
                            <th>Badges</th>
                            <th class="text-end">Points</th>
                        </tr>
                    </thead>
                    <tbody>
                        <c:choose>
                            <c:when test="${not empty leaderboard}">
                                <c:forEach var="e" items="${leaderboard}">
                                    <tr class="${sessionScope.authenticatedUserId == e.userId ? 'table-success' : ''}">
                                        <td>
                                            <span class="badge ${e.rank == 1 ? 'bg-warning text-dark' : (e.rank == 2 ? 'bg-secondary text-white' : (e.rank == 3 ? 'bg-warning-subtle text-dark' : 'bg-light text-dark border'))}">
                                                #${e.rank}
                                            </span>
                                        </td>
                                        <td class="fw-semibold">
                                            <a href="${pageContext.request.contextPath}/social/profile?id=${e.userId}" class="text-dark text-decoration-none">
                                                <c:out value="${e.displayName}"/>
                                            </a>
                                            <c:if test="${sessionScope.authenticatedUserId == e.userId}">
                                                <span class="badge bg-success ms-1 small">You</span>
                                            </c:if>
                                        </td>
                                        <td class="small text-secondary">${e.totalWorkouts}</td>
                                        <td class="small text-secondary">${e.earnedAchievements} 🏆</td>
                                        <td class="text-end fw-bold text-success">${e.totalPoints} pts</td>
                                    </tr>
                                </c:forEach>
                            </c:when>
                            <c:otherwise>
                                <tr>
                                    <td colspan="5" class="text-center py-4 text-muted small">No social athletes recorded for this period yet.</td>
                                </tr>
                            </c:otherwise>
                        </c:choose>
                    </tbody>
                </table>
            </div>
        </div>
    </main>

    <script src="https://cdn.jsdelivr.net/npm/bootstrap@5.3.3/dist/js/bootstrap.bundle.min.js"></script>
</body>
</html>
