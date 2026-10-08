<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ taglib prefix="c" uri="jakarta.tags.core" %>
<!DOCTYPE html>
<html lang="en">
<head>
    <meta charset="UTF-8">
    <meta name="viewport" content="width=device-width, initial-scale=1.0">
    <title>Fitness Profile Onboarding – FitAura</title>
    <meta name="description" content="Set up your physical measurements, routine preferences, and initial goals.">

    <!-- Bootstrap 5 CSS via CDN -->
    <link href="https://cdn.jsdelivr.net/npm/bootstrap@5.3.3/dist/css/bootstrap.min.css" rel="stylesheet" integrity="sha384-QWTKZyjpPEjISv5WaRU9OFeRpok6YctnYmDr5pNlyT2bRjXh0JMhjY6hW+ALEwIH" crossorigin="anonymous">
    <!-- Bootstrap Icons -->
    <link rel="stylesheet" href="https://cdn.jsdelivr.net/npm/bootstrap-icons@1.11.3/font/bootstrap-icons.min.css">
    <!-- FitAura Custom CSS -->
    <link rel="stylesheet" href="${pageContext.request.contextPath}/css/fitaura.css">
</head>
<body class="bg-light d-flex flex-column min-vh-100">

    <!-- Top Navigation Bar -->
    <nav class="navbar navbar-light bg-white border-bottom py-3">
        <div class="container">
            <a class="navbar-brand brand-badge d-flex align-items-center" href="${pageContext.request.contextPath}/index.jsp">
                <img src="${pageContext.request.contextPath}/images/fitaura-logo.svg" alt="FitAura Logo" width="34" height="34" class="me-2">
                <span>Fit<span class="text-success">Aura</span></span>
            </a>
            <span class="badge bg-success-subtle text-success border border-success-subtle px-3 py-2 rounded-pill font-monospace small">
                Step 2 of 2: Fitness Setup
            </span>
        </div>
    </nav>

    <!-- Main Container -->
    <main class="flex-grow-1 py-5">
        <div class="container">
            <div class="row justify-content-center">
                <div class="col-12 col-md-10 col-lg-8 col-xl-7">

                    <!-- Onboarding Card -->
                    <div class="card border-0 shadow-sm rounded-4 p-4 p-sm-5 bg-white mb-4">
                        <div class="text-center mb-4 pb-2 border-bottom">
                            <div class="icon-wrapper mx-auto mb-3" style="width: 56px; height: 56px;">
                                <i class="bi bi-heart-pulse-fill fs-2"></i>
                            </div>
                            <h3 class="fw-bold text-dark mb-1">Personalize Your Fitness Profile</h3>
                            <p class="text-secondary small mb-0">Help FitAura calculate your BMI, BMR, and personalized routine suggestions</p>
                        </div>

                        <!-- Feedback Alerts -->
                        <c:if test="${not empty errorMessage}">
                            <div class="alert alert-danger d-flex align-items-center small py-2 px-3 rounded-3 mb-4" role="alert">
                                <i class="bi bi-exclamation-triangle-fill me-2 fs-6"></i>
                                <div><c:out value="${errorMessage}"/></div>
                            </div>
                        </c:if>

                        <form action="${pageContext.request.contextPath}/onboarding" method="post">
                            <input type="hidden" name="_csrf" value="${sessionScope.csrfToken}">

                            <!-- Section 1: Physical Measurements -->
                            <h5 class="fw-bold text-dark mb-3 d-flex align-items-center">
                                <i class="bi bi-person-lines-fill text-success me-2"></i> Physical Measurements
                            </h5>

                            <div class="row g-3 mb-4">
                                <div class="col-12 col-sm-6">
                                    <label for="age" class="form-label small fw-semibold text-secondary">Age (Years) <span class="text-muted fw-normal">(Optional)</span></label>
                                    <input type="number" class="form-control" id="age" name="age" min="13" max="120"
                                           value="<c:out value="${age != null ? age : existingProfile.age}"/>" placeholder="e.g. 28">
                                    <div class="form-text small text-muted">Ages 13–120 supported.</div>
                                </div>

                                <div class="col-12 col-sm-6">
                                    <label for="gender" class="form-label small fw-semibold text-secondary">Gender <span class="text-muted fw-normal">(Optional)</span></label>
                                    <select class="form-select" id="gender" name="gender">
                                        <option value="">Select Gender...</option>
                                        <option value="MALE" ${gender == 'MALE' || existingProfile.gender == 'MALE' ? 'selected' : ''}>Male</option>
                                        <option value="FEMALE" ${gender == 'FEMALE' || existingProfile.gender == 'FEMALE' ? 'selected' : ''}>Female</option>
                                        <option value="OTHER" ${gender == 'OTHER' || existingProfile.gender == 'OTHER' ? 'selected' : ''}>Other</option>
                                        <option value="PREFER_NOT_TO_SAY" ${gender == 'PREFER_NOT_TO_SAY' || existingProfile.gender == 'PREFER_NOT_TO_SAY' ? 'selected' : ''}>Prefer not to say</option>
                                    </select>
                                </div>

                                <div class="col-12 col-sm-6">
                                    <label for="heightCm" class="form-label small fw-semibold text-secondary">Height (cm) <span class="text-muted fw-normal">(Optional)</span></label>
                                    <input type="number" step="0.1" class="form-control" id="heightCm" name="heightCm" min="50" max="260"
                                           value="<c:out value="${heightCm != null ? heightCm : existingProfile.heightCm}"/>" placeholder="e.g. 175.0">
                                </div>

                                <div class="col-12 col-sm-6">
                                    <label for="weightKg" class="form-label small fw-semibold text-secondary">Weight (kg) <span class="text-muted fw-normal">(Optional)</span></label>
                                    <input type="number" step="0.1" class="form-control" id="weightKg" name="weightKg" min="20" max="500"
                                           value="<c:out value="${weightKg != null ? weightKg : existingProfile.weightKg}"/>" placeholder="e.g. 72.5">
                                </div>
                            </div>

                            <!-- Section 2: Routine & Environment -->
                            <h5 class="fw-bold text-dark mb-3 d-flex align-items-center pt-2 border-top">
                                <i class="bi bi-sliders text-success me-2"></i> Routine & Training Style
                            </h5>

                            <div class="row g-3 mb-4">
                                <div class="col-12 col-sm-6">
                                    <label for="activityLevel" class="form-label small fw-semibold text-secondary">Activity Level</label>
                                    <select class="form-select" id="activityLevel" name="activityLevel">
                                        <option value="BEGINNER" ${activityLevel == 'BEGINNER' || existingProfile.activityLevel == 'BEGINNER' ? 'selected' : ''}>Beginner (Sedentary)</option>
                                        <option value="LIGHT" ${activityLevel == 'LIGHT' || existingProfile.activityLevel == 'LIGHT' ? 'selected' : ''}>Light (1–2 days/wk)</option>
                                        <option value="MODERATE" ${activityLevel == 'MODERATE' || existingProfile.activityLevel == 'MODERATE' || empty activityLevel ? 'selected' : ''}>Moderate (3–5 days/wk)</option>
                                        <option value="ACTIVE" ${activityLevel == 'ACTIVE' || existingProfile.activityLevel == 'ACTIVE' ? 'selected' : ''}>Active (6–7 days/wk)</option>
                                        <option value="VERY_ACTIVE" ${activityLevel == 'VERY_ACTIVE' || existingProfile.activityLevel == 'VERY_ACTIVE' ? 'selected' : ''}>Very Active (Athlete)</option>
                                    </select>
                                </div>

                                <div class="col-12 col-sm-6">
                                    <label for="preferredEnvironment" class="form-label small fw-semibold text-secondary">Preferred Environment</label>
                                    <select class="form-select" id="preferredEnvironment" name="preferredEnvironment">
                                        <option value="GYM" ${preferredEnvironment == 'GYM' || existingProfile.preferredEnvironment == 'GYM' ? 'selected' : ''}>Commercial Gym</option>
                                        <option value="HOME" ${preferredEnvironment == 'HOME' || existingProfile.preferredEnvironment == 'HOME' ? 'selected' : ''}>Home Workouts</option>
                                        <option value="OUTDOOR" ${preferredEnvironment == 'OUTDOOR' || existingProfile.preferredEnvironment == 'OUTDOOR' ? 'selected' : ''}>Outdoor (Running / Cycling)</option>
                                        <option value="MIXED" ${preferredEnvironment == 'MIXED' || existingProfile.preferredEnvironment == 'MIXED' || empty preferredEnvironment ? 'selected' : ''}>Mixed / Flexible</option>
                                    </select>
                                </div>
                            </div>

                            <!-- Section 3: Optional Initial Goal -->
                            <h5 class="fw-bold text-dark mb-3 d-flex align-items-center pt-2 border-top">
                                <i class="bi bi-bullseye text-success me-2"></i> Initial Fitness Goal <span class="text-muted fw-normal ms-2 small">(Optional)</span>
                            </h5>

                            <div class="row g-3 mb-4">
                                <div class="col-12 col-sm-4">
                                    <label for="goalType" class="form-label small fw-semibold text-secondary">Goal Focus</label>
                                    <select class="form-select" id="goalType" name="goalType">
                                        <option value="">No goal for now</option>
                                        <option value="WEIGHT_LOSS">Weight Loss (kg)</option>
                                        <option value="MUSCLE_GAIN">Muscle Gain (kg)</option>
                                        <option value="STRENGTH">Strength Training</option>
                                        <option value="ENDURANCE">Endurance / Running</option>
                                        <option value="GENERAL_FITNESS">General Fitness</option>
                                    </select>
                                </div>

                                <div class="col-12 col-sm-4">
                                    <label for="targetValue" class="form-label small fw-semibold text-secondary">Target Value</label>
                                    <input type="number" step="0.1" class="form-control" id="targetValue" name="targetValue" placeholder="e.g. 68.0">
                                </div>

                                <div class="col-12 col-sm-4">
                                    <label for="targetDate" class="form-label small fw-semibold text-secondary">Target Date</label>
                                    <input type="date" class="form-control" id="targetDate" name="targetDate">
                                </div>
                            </div>

                            <!-- Action Buttons -->
                            <div class="d-flex flex-column flex-sm-row justify-content-between align-items-center gap-3 pt-3 border-top">
                                <button type="submit" name="action" value="skip" class="btn btn-link text-muted text-decoration-none small order-2 order-sm-1">
                                    <i class="bi bi-arrow-right-short"></i> Skip and go to Dashboard
                                </button>
                                <button type="submit" name="action" value="save" class="btn btn-success px-4 py-2.5 fw-semibold rounded-3 shadow-sm order-1 order-sm-2">
                                    <i class="bi bi-check2-circle me-1"></i> Save Profile & Complete Setup
                                </button>
                            </div>
                        </form>
                    </div>

                    <!-- Privacy Guarantee -->
                    <div class="text-center text-muted small">
                        <i class="bi bi-lock-fill text-success me-1"></i>
                        All physical metrics are strictly confidential under <strong>Personal Mode</strong>.
                    </div>

                </div>
            </div>
        </div>
    </main>

    <!-- Footer -->
    <footer class="bg-white border-top py-3 text-center text-muted small mt-auto">
        <div class="container">
            &copy; 2026 FitAura. All rights reserved.
        </div>
    </footer>

    <!-- Bootstrap 5 JavaScript Bundle -->
    <script src="https://cdn.jsdelivr.net/npm/bootstrap@5.3.3/dist/js/bootstrap.bundle.min.js" integrity="sha384-YvpcrYf0tY3lHB60NNkmXc5s9fDVZLESaAA55NDzOxhy9GkcIdslK1eN7N6jIeHz" crossorigin="anonymous"></script>
</body>
</html>
