<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ taglib prefix="c" uri="jakarta.tags.core" %>
<!DOCTYPE html>
<html lang="en">
<head>
    <meta charset="UTF-8">
    <meta name="viewport" content="width=device-width, initial-scale=1.0">
    <title>Edit Competition – FitAura Admin</title>
    <!-- Bootstrap 5 CSS via CDN -->
    <link href="https://cdn.jsdelivr.net/npm/bootstrap@5.3.3/dist/css/bootstrap.min.css" rel="stylesheet">
    <link rel="stylesheet" href="https://cdn.jsdelivr.net/npm/bootstrap-icons@1.11.3/font/bootstrap-icons.min.css">
    <link rel="stylesheet" href="${pageContext.request.contextPath}/css/fitaura.css">
</head>
<body class="bg-light d-flex flex-column min-vh-100">

    <nav class="navbar navbar-dark bg-dark py-2 border-bottom border-dark-subtle">
        <div class="container-fluid px-lg-5">
            <a class="navbar-brand d-flex align-items-center text-white" href="${pageContext.request.contextPath}/admin/social">
                <i class="bi bi-arrow-left me-2"></i> Back to Competitions
            </a>
        </div>
    </nav>

    <main class="container my-5 flex-grow-1" style="max-width: 720px;">
        <div class="card border rounded-4 shadow-sm bg-white p-4 p-md-5">
            <h4 class="fw-bold text-dark mb-1"><i class="bi bi-pencil-square text-success me-2"></i>Edit Fitness Competition</h4>
            <p class="text-secondary small mb-4">Modify competition rules, dates, reward points, or status.</p>

            <form action="${pageContext.request.contextPath}/admin/social" method="POST">
                <input type="hidden" name="csrfToken" value="${sessionScope.csrfToken}"/>
                <input type="hidden" name="action" value="update"/>
                <input type="hidden" name="competitionId" value="${editCompetition.competitionId}"/>

                <div class="mb-3">
                    <label class="form-label small fw-semibold">Competition Name *</label>
                    <input type="text" name="name" class="form-control rounded-3" value="<c:out value="${editCompetition.name}"/>" required/>
                </div>

                <div class="mb-3">
                    <label class="form-label small fw-semibold">Description</label>
                    <textarea name="description" rows="3" class="form-control rounded-3"><c:out value="${editCompetition.description}"/></textarea>
                </div>

                <div class="row g-3 mb-3">
                    <div class="col-md-6">
                        <label class="form-label small fw-semibold">Scoring Metric *</label>
                        <select name="metric" class="form-select rounded-3">
                            <option value="WORKOUT_COUNT" ${editCompetition.metric == 'WORKOUT_COUNT' ? 'selected' : ''}>Workout Count</option>
                            <option value="TOTAL_MINUTES" ${editCompetition.metric == 'TOTAL_MINUTES' ? 'selected' : ''}>Total Active Minutes</option>
                            <option value="CALORIES_BURNED" ${editCompetition.metric == 'CALORIES_BURNED' ? 'selected' : ''}>Calories Burned</option>
                            <option value="STREAK_DAYS" ${editCompetition.metric == 'STREAK_DAYS' ? 'selected' : ''}>Consecutive Streak Days</option>
                        </select>
                    </div>
                    <div class="col-md-6">
                        <label class="form-label small fw-semibold">Target Value (optional)</label>
                        <input type="number" step="0.01" name="targetValue" class="form-control rounded-3" value="${editCompetition.targetValue}"/>
                    </div>
                </div>

                <div class="row g-3 mb-4">
                    <div class="col-md-4">
                        <label class="form-label small fw-semibold">Start Date *</label>
                        <input type="date" name="startDate" class="form-control rounded-3" value="${editCompetition.startDate}" required/>
                    </div>
                    <div class="col-md-4">
                        <label class="form-label small fw-semibold">End Date *</label>
                        <input type="date" name="endDate" class="form-control rounded-3" value="${editCompetition.endDate}" required/>
                    </div>
                    <div class="col-md-4">
                        <label class="form-label small fw-semibold">Reward Points</label>
                        <input type="number" name="rewardPoints" class="form-control rounded-3" value="${editCompetition.rewardPoints}"/>
                    </div>
                    <div class="col-md-12">
                        <label class="form-label small fw-semibold">Status</label>
                        <select name="status" class="form-select rounded-3">
                            <option value="ACTIVE" ${editCompetition.status == 'ACTIVE' ? 'selected' : ''}>ACTIVE</option>
                            <option value="UPCOMING" ${editCompetition.status == 'UPCOMING' ? 'selected' : ''}>UPCOMING</option>
                            <option value="COMPLETED" ${editCompetition.status == 'COMPLETED' ? 'selected' : ''}>COMPLETED</option>
                            <option value="CANCELLED" ${editCompetition.status == 'CANCELLED' ? 'selected' : ''}>CANCELLED</option>
                        </select>
                    </div>
                </div>

                <div class="d-flex justify-content-between align-items-center pt-3 border-top">
                    <a href="${pageContext.request.contextPath}/admin/social" class="btn btn-outline-secondary btn-sm px-3">Cancel</a>
                    <button type="submit" class="btn btn-success btn-sm px-4 fw-semibold">Save Changes</button>
                </div>
            </form>
        </div>
    </main>

    <script src="https://cdn.jsdelivr.net/npm/bootstrap@5.3.3/dist/js/bootstrap.bundle.min.js"></script>
</body>
</html>
