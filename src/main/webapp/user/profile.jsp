<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ taglib prefix="c" uri="jakarta.tags.core" %>
<!DOCTYPE html>
<html lang="en">
<head>
    <meta charset="UTF-8">
    <meta name="viewport" content="width=device-width, initial-scale=1.0">
    <title>My Profile – FitAura</title>
    <meta name="description" content="Manage your FitAura personal information, fitness profile, and privacy preferences.">

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

            <button class="navbar-toggler" type="button" data-bs-toggle="collapse" data-bs-target="#profileNavbar" aria-controls="profileNavbar" aria-expanded="false" aria-label="Toggle navigation">
                <span class="navbar-toggler-icon"></span>
            </button>

            <div class="collapse navbar-collapse" id="profileNavbar">
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
                        <a class="nav-link fw-medium text-dark" href="${pageContext.request.contextPath}/content">
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
                            <i class="bi bi-award me-1"></i> Points & Badges
                        </a>
                    </li>
                    <li class="nav-item">
                        <a class="nav-link fw-medium text-dark" href="${pageContext.request.contextPath}/social">
                            <i class="bi bi-people me-1"></i> Social Hub
                        </a>
                    </li>
                    <li class="nav-item">
                        <a class="nav-link fw-medium text-dark" href="${pageContext.request.contextPath}/leaderboard">
                            <i class="bi bi-bar-chart-line me-1"></i> Leaderboard
                        </a>
                    </li>
                    <li class="nav-item">
                        <a class="nav-link active fw-bold text-success" aria-current="page" href="${pageContext.request.contextPath}/user/profile">
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

    <!-- Main Content Area -->
    <main class="flex-grow-1 py-5">
        <div class="container" style="max-width: 900px;">

            <!-- Header Title -->
            <div class="mb-4">
                <h2 class="fw-bold text-dark mb-1">Profile & Preferences</h2>
                <p class="text-secondary small mb-0">Manage your personal information, physical measurements, and privacy settings.</p>
            </div>

            <!-- Feedback Alerts -->
            <c:if test="${not empty successMessage}">
                <div class="alert alert-success alert-dismissible fade show d-flex align-items-center p-3 rounded-4 shadow-sm mb-4" role="alert">
                    <i class="bi bi-check-circle-fill fs-5 me-2 text-success"></i>
                    <div><c:out value="${successMessage}"/></div>
                    <button type="button" class="btn-close" data-bs-dismiss="alert" aria-label="Close"></button>
                </div>
            </c:if>

            <c:if test="${not empty errorMessage}">
                <div class="alert alert-danger alert-dismissible fade show d-flex align-items-center p-3 rounded-4 shadow-sm mb-4" role="alert">
                    <i class="bi bi-exclamation-triangle-fill fs-5 me-2 text-danger"></i>
                    <div><c:out value="${errorMessage}"/></div>
                    <button type="button" class="btn-close" data-bs-dismiss="alert" aria-label="Close"></button>
                </div>
            </c:if>

            <!-- SECTION 1: Personal Information Card -->
            <div class="card border rounded-4 p-4 p-md-5 bg-white mb-4 shadow-xs">
                <div class="d-flex align-items-center justify-content-between mb-4 pb-2 border-bottom">
                    <div class="d-flex align-items-center gap-3">
                        <div class="icon-wrapper m-0 bg-primary-subtle text-primary">
                            <i class="bi bi-person-badge-fill fs-4"></i>
                        </div>
                        <div>
                            <h5 class="fw-bold text-dark mb-0">Personal Information</h5>
                            <small class="text-muted">Basic identification and display name</small>
                        </div>
                    </div>
                </div>

                <form action="${pageContext.request.contextPath}/user/profile" method="post">
                    <input type="hidden" name="_csrf" value="${sessionScope.csrfToken}">
                    <input type="hidden" name="action" value="update_basic">

                    <div class="row g-3">
                        <!-- Full Name -->
                        <div class="col-md-6">
                            <label for="fullName" class="form-label small fw-semibold text-secondary">Full Name</label>
                            <input type="text" class="form-control" id="fullName" name="fullName"
                                   value="<c:out value="${user.fullName}"/>" required maxlength="100">
                        </div>

                        <!-- Display Name -->
                        <div class="col-md-6">
                            <label for="displayName" class="form-label small fw-semibold text-secondary">Display Name / Alias</label>
                            <input type="text" class="form-control" id="displayName" name="displayName"
                                   value="<c:out value="${user.displayName}"/>" maxlength="60">
                            <div class="form-text small text-muted">Visible on community leaderboards if you enable Social Mode.</div>
                        </div>

                        <!-- Email Address (Read-only Login Identifier) -->
                        <div class="col-12">
                            <label for="email" class="form-label small fw-semibold text-secondary">Login Email Address</label>
                            <input type="email" class="form-control bg-light" id="email" name="email"
                                   value="<c:out value="${user.email}"/>" readonly disabled>
                            <div class="form-text small text-muted"><i class="bi bi-lock me-1"></i>Primary authentication identifier (read-only).</div>
                        </div>
                    </div>

                    <div class="mt-4 pt-2 text-end">
                        <button type="submit" class="btn btn-success px-4 fw-semibold shadow-xs">
                            <i class="bi bi-check2 me-1"></i> Save Personal Details
                        </button>
                    </div>
                </form>
            </div>

            <!-- SECTION 2: Fitness Profile Card -->
            <div class="card border rounded-4 p-4 p-md-5 bg-white mb-4 shadow-xs">
                <div class="d-flex align-items-center justify-content-between mb-4 pb-2 border-bottom">
                    <div class="d-flex align-items-center gap-3">
                        <div class="icon-wrapper m-0 bg-success-subtle text-success">
                            <i class="bi bi-heart-pulse-fill fs-4"></i>
                        </div>
                        <div>
                            <h5 class="fw-bold text-dark mb-0">Fitness Profile</h5>
                            <small class="text-muted">Physical metrics for accurate recommendations and calculations</small>
                        </div>
                    </div>
                </div>

                <form action="${pageContext.request.contextPath}/user/profile" method="post">
                    <input type="hidden" name="_csrf" value="${sessionScope.csrfToken}">
                    <input type="hidden" name="action" value="update_fitness">

                    <div class="row g-3">
                        <!-- Age -->
                        <div class="col-sm-6 col-md-4">
                            <label for="age" class="form-label small fw-semibold text-secondary">Age (years)</label>
                            <input type="number" class="form-control" id="age" name="age"
                                   value="<c:out value="${fitnessProfile.age}"/>" min="13" max="120" placeholder="e.g. 28">
                            <div class="form-text small text-muted">13 to 120 years.</div>
                        </div>

                        <!-- Gender -->
                        <div class="col-sm-6 col-md-4">
                            <label for="gender" class="form-label small fw-semibold text-secondary">Gender</label>
                            <select class="form-select" id="gender" name="gender">
                                <option value="" <c:if test="${empty fitnessProfile.gender}">selected</c:if>>Select gender...</option>
                                <option value="MALE" <c:if test="${fitnessProfile.gender == 'MALE'}">selected</c:if>>Male</option>
                                <option value="FEMALE" <c:if test="${fitnessProfile.gender == 'FEMALE'}">selected</c:if>>Female</option>
                                <option value="OTHER" <c:if test="${fitnessProfile.gender == 'OTHER'}">selected</c:if>>Other</option>
                                <option value="PREFER_NOT_TO_SAY" <c:if test="${fitnessProfile.gender == 'PREFER_NOT_TO_SAY'}">selected</c:if>>Prefer not to say</option>
                            </select>
                        </div>

                        <!-- Height (cm) -->
                        <div class="col-sm-6 col-md-4">
                            <label for="heightCm" class="form-label small fw-semibold text-secondary">Height (cm)</label>
                            <input type="number" step="0.1" class="form-control" id="heightCm" name="heightCm"
                                   value="<c:out value="${fitnessProfile.heightCm}"/>" min="50" max="260" placeholder="e.g. 175.0">
                            <div class="form-text small text-muted">50.0 to 260.0 cm.</div>
                        </div>

                        <!-- Weight (kg) -->
                        <div class="col-sm-6 col-md-4">
                            <label for="weightKg" class="form-label small fw-semibold text-secondary">Weight (kg)</label>
                            <input type="number" step="0.1" class="form-control" id="weightKg" name="weightKg"
                                   value="<c:out value="${fitnessProfile.weightKg}"/>" min="20" max="500" placeholder="e.g. 70.5">
                            <div class="form-text small text-muted">20.0 to 500.0 kg.</div>
                        </div>

                        <!-- Activity Level -->
                        <div class="col-sm-6 col-md-4">
                            <label for="activityLevel" class="form-label small fw-semibold text-secondary">Activity Level</label>
                            <select class="form-select" id="activityLevel" name="activityLevel">
                                <option value="BEGINNER" <c:if test="${fitnessProfile.activityLevel == 'BEGINNER' || empty fitnessProfile.activityLevel}">selected</c:if>>Beginner (Sedentary)</option>
                                <option value="LIGHT" <c:if test="${fitnessProfile.activityLevel == 'LIGHT'}">selected</c:if>>Light (1-2 days/week)</option>
                                <option value="MODERATE" <c:if test="${fitnessProfile.activityLevel == 'MODERATE'}">selected</c:if>>Moderate (3-5 days/week)</option>
                                <option value="ACTIVE" <c:if test="${fitnessProfile.activityLevel == 'ACTIVE'}">selected</c:if>>Active (6-7 days/week)</option>
                                <option value="VERY_ACTIVE" <c:if test="${fitnessProfile.activityLevel == 'VERY_ACTIVE'}">selected</c:if>>Very Active (Physical job / athlete)</option>
                            </select>
                        </div>

                        <!-- Preferred Environment -->
                        <div class="col-sm-6 col-md-4">
                            <label for="preferredEnvironment" class="form-label small fw-semibold text-secondary">Preferred Environment</label>
                            <select class="form-select" id="preferredEnvironment" name="preferredEnvironment">
                                <option value="GYM" <c:if test="${fitnessProfile.preferredEnvironment == 'GYM'}">selected</c:if>>Commercial Gym</option>
                                <option value="HOME" <c:if test="${fitnessProfile.preferredEnvironment == 'HOME'}">selected</c:if>>Home Workout</option>
                                <option value="OUTDOOR" <c:if test="${fitnessProfile.preferredEnvironment == 'OUTDOOR'}">selected</c:if>>Outdoor Running / Cycling</option>
                                <option value="MIXED" <c:if test="${fitnessProfile.preferredEnvironment == 'MIXED' || empty fitnessProfile.preferredEnvironment}">selected</c:if>>Mixed / Flexible</option>
                            </select>
                        </div>
                    </div>

                    <c:if test="${not empty fitnessProfile.calculateBmi()}">
                        <div class="p-3 bg-light rounded-3 mt-4 d-flex flex-column flex-sm-row justify-content-between align-items-sm-center gap-2">
                            <div class="d-flex align-items-center gap-2">
                                <i class="bi bi-speedometer2 text-success fs-5"></i>
                                <div>
                                    <span class="small fw-semibold text-secondary">Body Mass Index (BMI):</span>
                                    <strong class="text-dark ms-1"><c:out value="${fitnessProfile.calculateBmi()}"/></strong>
                                </div>
                            </div>
                            <div>
                                <span class="badge bg-success-subtle text-success px-3 py-2 fw-semibold">
                                    <c:out value="${fitnessProfile.getBmiCategory()}"/>
                                </span>
                            </div>
                        </div>
                    </c:if>

                    <div class="mt-4 pt-2 text-end">
                        <button type="submit" class="btn btn-success px-4 fw-semibold shadow-xs">
                            <i class="bi bi-check2 me-1"></i> Save Fitness Profile
                        </button>
                    </div>
                </form>
            </div>

            <!-- SECTION 3: Privacy Settings Card -->
            <div class="card border rounded-4 p-4 p-md-5 bg-white mb-4 shadow-xs">
                <div class="d-flex align-items-center justify-content-between mb-4 pb-2 border-bottom">
                    <div class="d-flex align-items-center gap-3">
                        <div class="icon-wrapper m-0 bg-info-subtle text-info">
                            <i class="bi bi-shield-lock-fill fs-4"></i>
                        </div>
                        <div>
                            <h5 class="fw-bold text-dark mb-0">Your Fitness Privacy</h5>
                            <small class="text-muted">Strict privacy boundaries and personal data control</small>
                        </div>
                    </div>
                    <span class="badge ${user.privacyMode == 'PERSONAL' ? 'bg-secondary' : 'bg-info'} fs-6 px-3 py-2">
                        Active: <c:out value="${user.privacyMode}"/>
                    </span>
                </div>

                <form action="${pageContext.request.contextPath}/user/profile" method="post">
                    <input type="hidden" name="_csrf" value="${sessionScope.csrfToken}">
                    <input type="hidden" name="action" value="update_privacy">

                    <div class="row g-3 mb-4">
                        <!-- Option A: Personal Mode -->
                        <div class="col-md-6">
                            <div class="form-check p-3 border rounded-3 h-100 ${user.privacyMode == 'PERSONAL' ? 'border-success bg-light' : ''}">
                                <input class="form-check-input ms-0 me-2" type="radio" name="privacyMode" id="modePersonal"
                                       value="PERSONAL" <c:if test="${user.privacyMode == 'PERSONAL'}">checked</c:if>>
                                <label class="form-check-label fw-bold text-dark" for="modePersonal">
                                    <i class="bi bi-lock-fill text-success me-1"></i> Personal Mode (Default)
                                </label>
                                <p class="small text-secondary mt-2 mb-0">
                                    Your entire fitness journey remains strictly confidential. Excluded from all public leaderboards. Zero social data leakage.
                                </p>
                            </div>
                        </div>

                        <!-- Option B: Social Mode -->
                        <div class="col-md-6">
                            <div class="form-check p-3 border rounded-3 h-100 ${user.privacyMode == 'SOCIAL' ? 'border-info bg-light' : ''}">
                                <input class="form-check-input ms-0 me-2" type="radio" name="privacyMode" id="modeSocial"
                                       value="SOCIAL" <c:if test="${user.privacyMode == 'SOCIAL'}">checked</c:if>>
                                <label class="form-check-label fw-bold text-dark" for="modeSocial">
                                    <i class="bi bi-trophy-fill text-info me-1"></i> Social Mode (Opt-In)
                                </label>
                                <p class="small text-secondary mt-2 mb-0">
                                    Compete on points leaderboards and join community challenges using your display name alias. Private health metrics (weight, height, BMI) remain protected.
                                </p>
                            </div>
                        </div>
                    </div>

                    <div class="d-flex justify-content-between align-items-center pt-2">
                        <small class="text-muted"><i class="bi bi-info-circle me-1"></i>Weight and physical metrics are never exposed to others.</small>
                        <button type="submit" class="btn btn-success px-4 fw-semibold shadow-xs">
                            <i class="bi bi-shield-check me-1"></i> Update Privacy Mode
                        </button>
                    </div>
                </form>
            </div>

            <!-- SECTION 4: Account Information Card -->
            <div class="card border rounded-4 p-4 p-md-5 bg-white shadow-xs">
                <div class="d-flex align-items-center justify-content-between mb-3">
                    <h6 class="fw-bold text-dark mb-0"><i class="bi bi-gear-wide-connected me-2 text-secondary"></i>Account Information</h6>
                    <a href="${pageContext.request.contextPath}/logout" class="btn btn-outline-danger btn-sm px-3">
                        <i class="bi bi-box-arrow-right me-1"></i> Log Out
                    </a>
                </div>
                <div class="row g-2 small text-secondary">
                    <div class="col-sm-4">
                        <span>Account Status:</span>
                        <strong class="text-success ms-1"><c:out value="${user.accountStatus}"/></strong>
                    </div>
                    <div class="col-sm-4">
                        <span>Role:</span>
                        <strong class="text-dark ms-1"><c:out value="${user.role}"/></strong>
                    </div>
                    <div class="col-sm-4">
                        <span>Member Since:</span>
                        <strong class="text-dark ms-1"><c:out value="${user.createdAt}"/></strong>
                    </div>
                </div>
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
</body>
</html>
