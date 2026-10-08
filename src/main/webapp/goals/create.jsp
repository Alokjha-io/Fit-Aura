<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ taglib prefix="c" uri="jakarta.tags.core" %>
<!DOCTYPE html>
<html lang="en">
<head>
    <meta charset="UTF-8">
    <meta name="viewport" content="width=device-width, initial-scale=1.0">
    <title>Create Goal – FitAura</title>
    <meta name="description" content="Set a targeted fitness goal to keep your motivation high.">

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

            <button class="navbar-toggler" type="button" data-bs-toggle="collapse" data-bs-target="#goalsNavbar" aria-controls="goalsNavbar" aria-expanded="false" aria-label="Toggle navigation">
                <span class="navbar-toggler-icon"></span>
            </button>

            <div class="collapse navbar-collapse" id="goalsNavbar">
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
                        <a class="nav-link active fw-bold text-success" aria-current="page" href="${pageContext.request.contextPath}/goals">
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
                        <a class="nav-link fw-medium text-dark" href="${pageContext.request.contextPath}/challenges">
                            <i class="bi bi-trophy me-1"></i> Challenges
                        </a>
                    </li>
                    <li class="nav-item">
                        <a class="nav-link fw-medium text-dark" href="${pageContext.request.contextPath}/user/profile">
                            <i class="bi bi-person-gear me-1"></i> Profile & Privacy
                        </a>
                    </li>
                </ul>

                <div class="d-flex align-items-center gap-3">
                    <a href="${pageContext.request.contextPath}/user/profile" class="text-decoration-none d-none d-sm-flex align-items-center gap-2 px-3 py-1.5 rounded-pill bg-light border">
                        <div class="rounded-circle bg-success-subtle text-success d-flex align-items-center justify-content-center" style="width: 28px; height: 28px;">
                            <i class="bi bi-person-fill small"></i>
                        </div>
                        <span class="small fw-semibold text-dark"><c:out value="${sessionScope.authenticatedDisplayName}"/></span>
                    </a>

                    <a href="${pageContext.request.contextPath}/logout" class="btn btn-outline-danger btn-sm px-3 fw-medium">
                        <i class="bi bi-box-arrow-right me-1"></i> Log Out
                    </a>
                </div>
            </div>
        </div>
    </nav>

    <!-- Main Form Section -->
    <main class="flex-grow-1 py-5">
        <div class="container" style="max-width: 680px;">

            <!-- Breadcrumb Navigation -->
            <nav aria-label="breadcrumb" class="mb-3">
                <ol class="breadcrumb small">
                    <li class="breadcrumb-item"><a href="${pageContext.request.contextPath}/goals" class="text-success text-decoration-none">Goals</a></li>
                    <li class="breadcrumb-item active" aria-current="page">Create Goal</li>
                </ol>
            </nav>

            <div class="card border rounded-4 bg-white p-4 p-md-5 shadow-xs">
                <div class="d-flex align-items-center gap-3 mb-4">
                    <div class="icon-wrapper m-0 bg-success-subtle text-success" style="width: 48px; height: 48px;">
                        <i class="bi bi-bullseye fs-4"></i>
                    </div>
                    <div>
                        <h3 class="fw-bold text-dark mb-0">Create Fitness Goal</h3>
                        <p class="text-secondary small mb-0">Define what you want to achieve and track your milestone.</p>
                    </div>
                </div>

                <!-- Error Alert -->
                <c:if test="${not empty errorMessage}">
                    <div class="alert alert-danger alert-dismissible fade show d-flex align-items-center p-3 rounded-3 mb-4" role="alert">
                        <i class="bi bi-exclamation-triangle-fill fs-5 me-2 text-danger"></i>
                        <div><c:out value="${errorMessage}"/></div>
                        <button type="button" class="btn-close" data-bs-dismiss="alert" aria-label="Close"></button>
                    </div>
                </c:if>

                <form action="${pageContext.request.contextPath}/goals/create" method="post" id="createGoalForm" novalidate>
                    <!-- CSRF Token -->
                    <input type="hidden" name="_csrf" value="${sessionScope.csrfToken}">

                    <!-- Goal Type -->
                    <div class="mb-3">
                        <label for="goalType" class="form-label fw-semibold text-dark">
                            Goal Type <span class="text-danger">*</span>
                        </label>
                        <select class="form-select rounded-3 py-2.5" id="goalType" name="goalType" required>
                            <option value="" disabled <c:if test="${empty goalType}">selected</c:if>>-- Select Goal Focus --</option>
                            <option value="WEIGHT_LOSS" <c:if test="${goalType == 'WEIGHT_LOSS'}">selected</c:if>>Weight Loss</option>
                            <option value="WEIGHT_GAIN" <c:if test="${goalType == 'WEIGHT_GAIN'}">selected</c:if>>Weight Gain</option>
                            <option value="MUSCLE_GAIN" <c:if test="${goalType == 'MUSCLE_GAIN'}">selected</c:if>>Muscle Gain</option>
                            <option value="STRENGTH" <c:if test="${goalType == 'STRENGTH'}">selected</c:if>>Strength</option>
                            <option value="ENDURANCE" <c:if test="${goalType == 'ENDURANCE'}">selected</c:if>>Endurance</option>
                            <option value="GENERAL_FITNESS" <c:if test="${goalType == 'GENERAL_FITNESS'}">selected</c:if>>General Fitness</option>
                            <option value="FLEXIBILITY" <c:if test="${goalType == 'FLEXIBILITY'}">selected</c:if>>Flexibility</option>
                        </select>
                    </div>

                    <!-- Target and Current Values -->
                    <div class="row g-3 mb-3">
                        <div class="col-12 col-sm-6">
                            <label for="targetValue" class="form-label fw-semibold text-dark">
                                Target Value <span class="text-danger">*</span>
                            </label>
                            <div class="input-group">
                                <input type="number" step="0.1" min="0.1" max="100000" class="form-control rounded-start-3 py-2.5"
                                       id="targetValue" name="targetValue" placeholder="e.g. 70.0"
                                       value="<c:out value='${targetValue}'/>" required>
                            </div>
                            <div class="form-text small">Your milestone target number.</div>
                        </div>

                        <div class="col-12 col-sm-6">
                            <label for="currentValue" class="form-label fw-semibold text-dark">
                                Current / Starting Value
                            </label>
                            <div class="input-group">
                                <input type="number" step="0.1" min="0" max="100000" class="form-control rounded-start-3 py-2.5"
                                       id="currentValue" name="currentValue" placeholder="e.g. 78.5"
                                       value="<c:out value='${currentValue}'/>">
                            </div>
                            <div class="form-text small">Where you are starting from.</div>
                        </div>
                    </div>

                    <!-- Unit of Measurement -->
                    <div class="mb-3">
                        <label for="unit" class="form-label fw-semibold text-dark">
                            Measurement Unit
                        </label>
                        <input type="text" class="form-control rounded-3 py-2.5" id="unit" name="unit" maxlength="30"
                               placeholder="e.g. kg, lbs, km, sessions, min" value="<c:out value='${not empty unit ? unit : "kg"}'/>">
                        <div class="form-text small">Unit label for progress display (e.g. kg, lbs, sessions).</div>
                    </div>

                    <!-- Dates -->
                    <div class="row g-3 mb-3">
                        <div class="col-12 col-sm-6">
                            <label for="startDate" class="form-label fw-semibold text-dark">
                                Start Date <span class="text-danger">*</span>
                            </label>
                            <input type="date" class="form-control rounded-3 py-2.5" id="startDate" name="startDate"
                                   value="<c:out value='${startDate}'/>" required>
                        </div>

                        <div class="col-12 col-sm-6">
                            <label for="targetDate" class="form-label fw-semibold text-dark">
                                Target Completion Date <span class="text-muted fw-normal small">(Optional)</span>
                            </label>
                            <input type="date" class="form-control rounded-3 py-2.5" id="targetDate" name="targetDate"
                                   value="<c:out value='${targetDate}'/>">
                        </div>
                    </div>

                    <!-- Notes -->
                    <div class="mb-4">
                        <label for="notes" class="form-label fw-semibold text-dark">
                            Notes / Strategy <span class="text-muted fw-normal small">(Optional)</span>
                        </label>
                        <textarea class="form-control rounded-3" id="notes" name="notes" rows="3" maxlength="500"
                                  placeholder="Describe your plan or milestone details (e.g. Train 4x weekly and track calorie deficit)..."><c:out value='${notes}'/></textarea>
                        <div class="form-text small text-end"><span id="charCount">0</span> / 500 characters</div>
                    </div>

                    <!-- Submit & Cancel Actions -->
                    <div class="d-flex align-items-center justify-content-between pt-2">
                        <a href="${pageContext.request.contextPath}/goals" class="btn btn-outline-secondary px-4 py-2 fw-medium rounded-3">
                            Cancel
                        </a>
                        <button type="submit" class="btn btn-success px-5 py-2 fw-semibold rounded-3 shadow-xs">
                            <i class="bi bi-check2-circle me-1"></i> Save Goal
                        </button>
                    </div>
                </form>
            </div>

        </div>
    </main>

    <!-- Footer -->
    <footer class="bg-white border-top py-3 text-center text-muted small mt-auto">
        <div class="container d-flex flex-column flex-sm-row justify-content-between align-items-center">
            <div>&copy; 2026 FitAura. All rights reserved.</div>
            <div class="mt-2 mt-sm-0">Track. Improve. Thrive.</div>
        </div>
    </footer>

    <!-- Bootstrap 5 JavaScript Bundle -->
    <script src="https://cdn.jsdelivr.net/npm/bootstrap@5.3.3/dist/js/bootstrap.bundle.min.js" integrity="sha384-YvpcrYf0tY3lHB60NNkmXc5s9fDVZLESaAA55NDzOxhy9GkcIdslK1eN7N6jIeHz" crossorigin="anonymous"></script>

    <script>
        const notesArea = document.getElementById('notes');
        const charCount = document.getElementById('charCount');
        if (notesArea && charCount) {
            charCount.textContent = notesArea.value.length;
            notesArea.addEventListener('input', () => {
                charCount.textContent = notesArea.value.length;
            });
        }
    </script>
</body>
</html>
