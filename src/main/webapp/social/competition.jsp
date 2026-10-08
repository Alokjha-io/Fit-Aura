<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ taglib prefix="c" uri="jakarta.tags.core" %>
<!DOCTYPE html>
<html lang="en">
<head>
    <meta charset="UTF-8">
    <meta name="viewport" content="width=device-width, initial-scale=1.0">
    <title><c:out value="${competition.name}"/> – FitAura</title>
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
                <span>Fit<span class="text-success">Aura</span> Competition</span>
            </a>
            <a href="${pageContext.request.contextPath}/social" class="btn btn-outline-secondary btn-sm">Back to Hub</a>
        </div>
    </nav>

    <main class="container my-4 flex-grow-1" style="max-width: 860px;">

        <!-- Header Card -->
        <div class="card border-0 rounded-4 p-4 mb-4 shadow-xs text-white" style="background: linear-gradient(135deg, #064e3b 0%, #0f172a 100%);">
            <div class="d-flex justify-content-between align-items-center mb-2">
                <span class="badge bg-warning text-dark fw-bold text-uppercase px-3 py-1">
                    <c:out value="${competition.metricLabel}"/>
                </span>
                <span class="badge bg-white bg-opacity-20 text-white"><c:out value="${competition.status}"/></span>
            </div>
            <h2 class="fw-bold mb-2"><c:out value="${competition.name}"/></h2>
            <p class="text-light opacity-75 mb-3"><c:out value="${competition.description}"/></p>
            <div class="d-flex flex-wrap gap-3 small text-white-50">
                <div><i class="bi bi-calendar-event me-1"></i> <c:out value="${competition.startDate}"/> to <c:out value="${competition.endDate}"/></div>
                <div><i class="bi bi-award-fill text-warning me-1"></i> Reward: +${competition.rewardPoints} points</div>
            </div>
        </div>

        <!-- Participation Actions Bar -->
        <div class="card border rounded-4 p-3 bg-white mb-4 shadow-xs d-flex flex-row justify-content-between align-items-center">
            <div>
                <c:choose>
                    <c:when test="${competition.userParticipating}">
                        <div class="small fw-semibold text-success"><i class="bi bi-check-circle-fill me-1"></i> You are enrolled in this competition!</div>
                        <div class="small text-muted">Your current calculated score: <strong>${competition.userCurrentScore}</strong> &bull; Current Rank: <strong>#${competition.userRank != null ? competition.userRank : '—'}</strong></div>
                    </c:when>
                    <c:otherwise>
                        <div class="small fw-semibold text-dark">Ready to compete?</div>
                        <div class="small text-muted">Rankings are calculated directly from your logged workouts during the competition window.</div>
                    </c:otherwise>
                </c:choose>
            </div>
            <div>
                <c:choose>
                    <c:when test="${competition.userParticipating}">
                        <form action="${pageContext.request.contextPath}/social" method="POST" class="d-inline">
                            <input type="hidden" name="csrfToken" value="${sessionScope.csrfToken}"/>
                            <input type="hidden" name="action" value="leaveCompetition"/>
                            <input type="hidden" name="competitionId" value="${competition.competitionId}"/>
                            <button type="submit" class="btn btn-outline-danger btn-sm px-3 fw-medium" onclick="return confirm('Leave this competition?');">
                                Leave Competition
                            </button>
                        </form>
                    </c:when>
                    <c:otherwise>
                        <form action="${pageContext.request.contextPath}/social" method="POST" class="d-inline">
                            <input type="hidden" name="csrfToken" value="${sessionScope.csrfToken}"/>
                            <input type="hidden" name="action" value="joinCompetition"/>
                            <input type="hidden" name="competitionId" value="${competition.competitionId}"/>
                            <button type="submit" class="btn btn-success btn-sm px-4 fw-bold">
                                Join Now
                            </button>
                        </form>
                    </c:otherwise>
                </c:choose>
            </div>
        </div>

        <!-- Leaderboard -->
        <div class="card border rounded-4 shadow-xs bg-white">
            <div class="card-header bg-transparent py-3 border-bottom d-flex justify-content-between align-items-center">
                <h5 class="fw-bold text-dark mb-0"><i class="bi bi-bar-chart-fill text-success me-2"></i>Live Competition Standings</h5>
                <span class="badge bg-secondary-subtle text-secondary">${leaderboard.size()} Ranked Participants</span>
            </div>
            <div class="table-responsive">
                <table class="table align-middle table-hover mb-0">
                    <thead class="table-light small">
                        <tr>
                            <th style="width: 70px;">Rank</th>
                            <th>Participant</th>
                            <th class="text-end">Current Score</th>
                        </tr>
                    </thead>
                    <tbody>
                        <c:choose>
                            <c:when test="${not empty leaderboard}">
                                <c:forEach var="p" items="${leaderboard}">
                                    <tr class="${sessionScope.authenticatedUserId == p.userId ? 'table-success' : ''}">
                                        <td>
                                            <span class="badge ${p.rank == 1 ? 'bg-warning text-dark' : (p.rank == 2 ? 'bg-secondary text-white' : (p.rank == 3 ? 'bg-warning-subtle text-dark' : 'bg-light text-dark border'))}">
                                                #${p.rank}
                                            </span>
                                        </td>
                                        <td class="fw-semibold">
                                            <a href="${pageContext.request.contextPath}/social/profile?id=${p.userId}" class="text-dark text-decoration-none">
                                                <c:out value="${p.displayName}"/>
                                            </a>
                                            <c:if test="${sessionScope.authenticatedUserId == p.userId}">
                                                <span class="badge bg-success ms-1 small">You</span>
                                            </c:if>
                                        </td>
                                        <td class="text-end fw-bold text-success">${p.currentScore}</td>
                                    </tr>
                                </c:forEach>
                            </c:when>
                            <c:otherwise>
                                <tr>
                                    <td colspan="3" class="text-center py-4 text-muted small">No participants have logged qualifying workouts yet. Be the first!</td>
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
