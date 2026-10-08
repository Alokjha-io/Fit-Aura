<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<!DOCTYPE html>
<html lang="en">
<head>
    <meta charset="UTF-8">
    <meta name="viewport" content="width=device-width, initial-scale=1.0">
    <title>FitAura – Track. Improve. Thrive.</title>
    <meta name="description" content="FitAura is a responsive, privacy-first fitness tracking application. Log workouts, achieve goals, maintain streaks, and choose whether your journey is personal or social.">

    <!-- Bootstrap 5 CSS via CDN -->
    <link href="https://cdn.jsdelivr.net/npm/bootstrap@5.3.3/dist/css/bootstrap.min.css" rel="stylesheet" integrity="sha384-QWTKZyjpPEjISv5WaRU9OFeRpok6YctnYmDr5pNlyT2bRjXh0JMhjY6hW+ALEwIH" crossorigin="anonymous">
    <!-- Bootstrap Icons -->
    <link rel="stylesheet" href="https://cdn.jsdelivr.net/npm/bootstrap-icons@1.11.3/font/bootstrap-icons.min.css">
    <!-- FitAura Custom CSS -->
    <link rel="stylesheet" href="${pageContext.request.contextPath}/css/fitaura.css">
</head>
<body>

    <!-- Header Navigation -->
    <nav class="navbar navbar-expand-lg navbar-light bg-white border-bottom sticky-top py-3">
        <div class="container">
            <a class="navbar-brand brand-badge d-flex align-items-center" href="${pageContext.request.contextPath}/index.jsp">
                <img src="${pageContext.request.contextPath}/images/fitaura-logo.svg" alt="FitAura Logo" width="38" height="38" class="me-2">
                <span>Fit<span class="text-success">Aura</span></span>
            </a>
            <button class="navbar-toggler" type="button" data-bs-toggle="collapse" data-bs-target="#navbarMain" aria-controls="navbarMain" aria-expanded="false" aria-label="Toggle navigation">
                <span class="navbar-toggler-icon"></span>
            </button>

            <div class="collapse navbar-collapse" id="navbarMain">
                <ul class="navbar-nav me-auto mb-2 mb-lg-0 ms-lg-4">
                    <li class="nav-item">
                        <a class="nav-link active fw-medium" href="#overview">Overview</a>
                    </li>
                    <li class="nav-item">
                        <a class="nav-link fw-medium" href="#privacy">Privacy Modes</a>
                    </li>
                    <li class="nav-item">
                        <a class="nav-link fw-medium" href="#features">Features</a>
                    </li>
                    <li class="nav-item">
                        <a class="nav-link fw-medium" href="#cohorts">For Everyone</a>
                    </li>
                </ul>

                <div class="d-flex align-items-center gap-2">
                    <a href="${pageContext.request.contextPath}/login" class="btn btn-outline-dark px-3 py-2 fw-medium">
                        <i class="bi bi-box-arrow-in-right me-1"></i> Log In
                    </a>
                    <a href="${pageContext.request.contextPath}/register" class="btn btn-success px-4 py-2 fw-medium shadow-sm">
                        Get Started Free
                    </a>
                </div>
            </div>
        </div>
    </nav>

    <!-- Main Content -->
    <main class="flex-grow-1">

        <!-- Hero Section -->
        <section class="hero-gradient py-5 py-md-6" id="overview">
            <div class="container text-center py-4">
                <div class="d-inline-flex align-items-center gap-2 px-3 py-1 mb-3 rounded-pill bg-white border border-secondary-subtle shadow-sm">
                    <span class="brand-dot"></span>
                    <span class="small fw-semibold text-muted text-uppercase tracking-wide">Track. Improve. Thrive.</span>
                </div>
                <h1 class="display-4 fw-bold text-dark mb-3">
                    Your Fitness Journey.<br><span class="text-success">On Your Own Terms.</span>
                </h1>
                <p class="lead text-secondary mx-auto mb-4" style="max-width: 740px;">
                    FitAura is the responsive fitness companion designed for beginners, gym athletes, and home-workout enthusiasts. Track workouts, set measurable goals, maintain streaks, and choose whether your progress is 100% private or shared with friends.
                </p>
                <div class="d-flex flex-wrap justify-content-center gap-3 mb-5">
                    <a href="#privacy" class="btn btn-success btn-lg px-4 shadow-sm fw-medium">
                        <i class="bi bi-shield-check me-2"></i>Discover Privacy Modes
                    </a>
                    <a href="#features" class="btn btn-outline-dark btn-lg px-4 fw-medium">
                        <i class="bi bi-grid-fill me-2"></i>Explore Features
                    </a>
                </div>

                <!-- Quick Stats Banner -->
                <div class="row g-3 justify-content-center pt-2">
                    <div class="col-6 col-md-3">
                        <div class="p-3 bg-white rounded-3 border shadow-sm">
                            <div class="fs-4 fw-bold text-success">5+</div>
                            <div class="small text-muted">Workout Categories</div>
                        </div>
                    </div>
                    <div class="col-6 col-md-3">
                        <div class="p-3 bg-white rounded-3 border shadow-sm">
                            <div class="fs-4 fw-bold text-info">2 Modes</div>
                            <div class="small text-muted">Personal or Social</div>
                        </div>
                    </div>
                    <div class="col-6 col-md-3">
                        <div class="p-3 bg-white rounded-3 border shadow-sm">
                            <div class="fs-4 fw-bold text-warning">Daily Streaks</div>
                            <div class="small text-muted">Gamified Rewards</div>
                        </div>
                    </div>
                    <div class="col-6 col-md-3">
                        <div class="p-3 bg-white rounded-3 border shadow-sm">
                            <div class="fs-4 fw-bold text-dark">Smart Rules</div>
                            <div class="small text-muted">Tailored Guidance</div>
                        </div>
                    </div>
                </div>
            </div>
        </section>

        <!-- Privacy Differentiator Section -->
        <section class="py-5" id="privacy">
            <div class="container py-3">
                <div class="text-center mb-5">
                    <span class="badge bg-success-subtle text-success border border-success-subtle px-3 py-2 mb-2">Core Differentiator</span>
                    <h2 class="fw-bold">Privacy-First Fitness Tracking</h2>
                    <p class="text-secondary mx-auto" style="max-width: 660px;">
                        Most fitness apps broadcast your activities by default. In FitAura, you decide whether your journey stays private or becomes social.
                    </p>
                </div>

                <div class="row g-4 align-items-stretch">
                    <!-- Personal Mode Card -->
                    <div class="col-lg-6">
                        <div class="feature-card border-success-subtle h-100 p-4 p-md-5">
                            <div class="d-flex align-items-center gap-3 mb-4">
                                <div class="icon-wrapper m-0">
                                    <i class="bi bi-lock-fill fs-4"></i>
                                </div>
                                <div>
                                    <h4 class="fw-bold text-dark mb-0">Personal Mode</h4>
                                    <span class="badge bg-secondary-subtle text-secondary">Default Mode</span>
                                </div>
                            </div>
                            <p class="text-secondary mb-4">
                                Your workouts, body measurements, BMI/BMR estimates, and goal progress are strictly confidential. No other user can view your profile or activities.
                            </p>
                            <div class="bg-light rounded-3 p-3 border">
                                <h6 class="fw-bold text-dark mb-3 small text-uppercase">Guaranteed Protections:</h6>
                                <ul class="list-unstyled mb-0 small text-secondary">
                                    <li class="mb-2 d-flex align-items-start gap-2">
                                        <i class="bi bi-check-circle-fill text-success mt-1"></i>
                                        <span><strong>Excluded from public leaderboards:</strong> Your points and rank remain invisible to everyone else.</span>
                                    </li>
                                    <li class="mb-2 d-flex align-items-start gap-2">
                                        <i class="bi bi-check-circle-fill text-success mt-1"></i>
                                        <span><strong>Zero social data leakage:</strong> Your workout frequency and weight metrics are never shared.</span>
                                    </li>
                                    <li class="d-flex align-items-start gap-2">
                                        <i class="bi bi-check-circle-fill text-success mt-1"></i>
                                        <span><strong>Focus on personal growth:</strong> Track workouts in peace without social pressure.</span>
                                    </li>
                                </ul>
                            </div>
                        </div>
                    </div>

                    <!-- Social Mode Card -->
                    <div class="col-lg-6">
                        <div class="feature-card border-info-subtle h-100 p-4 p-md-5">
                            <div class="d-flex align-items-center gap-3 mb-4">
                                <div class="icon-wrapper m-0" style="background-color: #cffafe; color: #0891b2;">
                                    <i class="bi bi-trophy-fill fs-4"></i>
                                </div>
                                <div>
                                    <h4 class="fw-bold text-dark mb-0">Social Mode</h4>
                                    <span class="badge bg-info-subtle text-info">Optional Opt-In</span>
                                </div>
                            </div>
                            <p class="text-secondary mb-4">
                                Connect with the community, participate in fitness challenges, maintain qualifying activity streaks, and climb the points-based leaderboard with an alias.
                            </p>
                            <div class="bg-light rounded-3 p-3 border">
                                <h6 class="fw-bold text-dark mb-3 small text-uppercase">Community Benefits:</h6>
                                <ul class="list-unstyled mb-0 small text-secondary">
                                    <li class="mb-2 d-flex align-items-start gap-2">
                                        <i class="bi bi-check-circle-fill text-info mt-1"></i>
                                        <span><strong>Points-based leaderboard:</strong> Compete with peers; users with equal points share equal ranks.</span>
                                    </li>
                                    <li class="mb-2 d-flex align-items-start gap-2">
                                        <i class="bi bi-check-circle-fill text-info mt-1"></i>
                                        <span><strong>Community challenges:</strong> Join distance, workout frequency, and strength challenges.</span>
                                    </li>
                                    <li class="d-flex align-items-start gap-2">
                                        <i class="bi bi-check-circle-fill text-info mt-1"></i>
                                        <span><strong>Gamified achievements:</strong> Unlock badges like "First Workout" and "7 Day Streak".</span>
                                    </li>
                                </ul>
                            </div>
                        </div>
                    </div>
                </div>
            </div>
        </section>

        <!-- Features Grid Section -->
        <section class="py-5 bg-white border-top border-bottom" id="features">
            <div class="container py-3">
                <div class="text-center mb-5">
                    <span class="badge bg-secondary-subtle text-secondary px-3 py-2 mb-2">Comprehensive Tracking</span>
                    <h2 class="fw-bold">Everything You Need to Succeed</h2>
                    <p class="text-secondary mx-auto" style="max-width: 660px;">
                        Built from the ground up to support realistic fitness habits for every lifestyle.
                    </p>
                </div>

                <div class="row g-4">
                    <div class="col-md-6 col-lg-4">
                        <div class="feature-card h-100">
                            <div class="icon-wrapper">
                                <i class="bi bi-activity"></i>
                            </div>
                            <h5 class="fw-bold text-dark mb-2">Workout Logging</h5>
                            <p class="text-secondary small mb-0">
                                Log running, walking, cycling, strength training, and home workouts with duration, intensity (Low/Medium/High), and estimated calories burned.
                            </p>
                        </div>
                    </div>

                    <div class="col-md-6 col-lg-4">
                        <div class="feature-card h-100">
                            <div class="icon-wrapper">
                                <i class="bi bi-bullseye"></i>
                            </div>
                            <h5 class="fw-bold text-dark mb-2">Measurable Goals</h5>
                            <p class="text-secondary small mb-0">
                                Set targets for weight loss, weight gain, muscle gain, strength, endurance, general fitness, and flexibility with target dates and visual milestones.
                            </p>
                        </div>
                    </div>

                    <div class="col-md-6 col-lg-4">
                        <div class="feature-card h-100">
                            <div class="icon-wrapper">
                                <i class="bi bi-lightbulb-fill"></i>
                            </div>
                            <h5 class="fw-bold text-dark mb-2">Personalized Guidance</h5>
                            <p class="text-secondary small mb-0">
                                Receive rule-based exercise suggestions, nutrition tips, and routine adjustments tailored to your activity level, goals, and preferred environment.
                            </p>
                        </div>
                    </div>

                    <div class="col-md-6 col-lg-4">
                        <div class="feature-card h-100">
                            <div class="icon-wrapper">
                                <i class="bi bi-calculator"></i>
                            </div>
                            <h5 class="fw-bold text-dark mb-2">BMI & BMR Estimates</h5>
                            <p class="text-secondary small mb-0">
                                Built-in health calculators provide immediate BMI, BMR, and daily calorie expenditure estimates to guide your routine safely.
                            </p>
                        </div>
                    </div>

                    <div class="col-md-6 col-lg-4">
                        <div class="feature-card h-100">
                            <div class="icon-wrapper">
                                <i class="bi bi-fire"></i>
                            </div>
                            <h5 class="fw-bold text-dark mb-2">Streaks & Gamification</h5>
                            <p class="text-secondary small mb-0">
                                Maintain qualifying daily workout streaks, earn points for every completed routine, and unlock milestone achievement badges.
                            </p>
                        </div>
                    </div>

                    <div class="col-md-6 col-lg-4">
                        <div class="feature-card h-100">
                            <div class="icon-wrapper">
                                <i class="bi bi-phone"></i>
                            </div>
                            <h5 class="fw-bold text-dark mb-2">Responsive Everywhere</h5>
                            <p class="text-secondary small mb-0">
                                Fluidly adapts across desktops, laptops, tablets, and smartphones in both portrait and landscape orientations without losing functionality.
                            </p>
                        </div>
                    </div>
                </div>
            </div>
        </section>

        <!-- Cohorts Section -->
        <section class="py-5" id="cohorts">
            <div class="container py-3">
                <div class="text-center mb-5">
                    <span class="badge bg-success-subtle text-success px-3 py-2 mb-2">Designed for Everyone</span>
                    <h2 class="fw-bold">No Matter How You Move</h2>
                    <p class="text-secondary mx-auto" style="max-width: 660px;">
                        FitAura adapts to your fitness environment—whether you train in a commercial gym, at home, or outdoors.
                    </p>
                </div>

                <div class="row g-4 text-center">
                    <div class="col-6 col-md-3">
                        <div class="p-4 bg-white rounded-3 border h-100 shadow-xs">
                            <div class="fs-1 text-success mb-2"><i class="bi bi-person-walking"></i></div>
                            <h6 class="fw-bold text-dark">Beginners</h6>
                            <p class="text-secondary small mb-0">Approachable guidance, gentle starting goals, and daily motivational tips.</p>
                        </div>
                    </div>
                    <div class="col-6 col-md-3">
                        <div class="p-4 bg-white rounded-3 border h-100 shadow-xs">
                            <div class="fs-1 text-success mb-2"><i class="bi bi-heart-pulse"></i></div>
                            <h6 class="fw-bold text-dark">Gym Users</h6>
                            <p class="text-secondary small mb-0">Heavy strength workouts, muscle gain goals, and intensity tracking.</p>
                        </div>
                    </div>
                    <div class="col-6 col-md-3">
                        <div class="p-4 bg-white rounded-3 border h-100 shadow-xs">
                            <div class="fs-1 text-success mb-2"><i class="bi bi-house-door"></i></div>
                            <h6 class="fw-bold text-dark">Home Athletes</h6>
                            <p class="text-secondary small mb-0">Calisthenics, bodyweight routines, and flexible no-equipment workouts.</p>
                        </div>
                    </div>
                    <div class="col-6 col-md-3">
                        <div class="p-4 bg-white rounded-3 border h-100 shadow-xs">
                            <div class="fs-1 text-success mb-2"><i class="bi bi-bicycle"></i></div>
                            <h6 class="fw-bold text-dark">Outdoor Runners</h6>
                            <p class="text-secondary small mb-0">Endurance tracking, cycling intervals, and distance challenges.</p>
                        </div>
                    </div>
                </div>
            </div>
        </section>

    </main>

    <!-- Footer -->
    <footer class="bg-dark text-white py-5 mt-auto">
        <div class="container">
            <div class="row g-4 mb-4">
                <div class="col-md-5">
                    <div class="brand-badge d-flex align-items-center text-white mb-2">
                        <img src="${pageContext.request.contextPath}/images/fitaura-logo.svg" alt="FitAura Logo" width="30" height="30" class="me-2">
                        <span>Fit<span class="text-success">Aura</span></span>
                    </div>
                    <p class="text-secondary small mb-0" style="max-width: 360px;">
                        Track. Improve. Thrive. A responsive fitness tracking web application focused on privacy, steady improvement, and community motivation.
                    </p>
                </div>
                <div class="col-6 col-md-3 offset-md-1">
                    <h6 class="text-uppercase small fw-bold text-light mb-3">Navigation</h6>
                    <ul class="list-unstyled small text-secondary mb-0">
                        <li class="mb-2"><a href="#overview" class="text-secondary text-decoration-none">Overview</a></li>
                        <li class="mb-2"><a href="#privacy" class="text-secondary text-decoration-none">Privacy Modes</a></li>
                        <li class="mb-2"><a href="#features" class="text-secondary text-decoration-none">Features</a></li>
                        <li><a href="#cohorts" class="text-secondary text-decoration-none">For Everyone</a></li>
                    </ul>
                </div>
                <div class="col-6 col-md-3">
                    <h6 class="text-uppercase small fw-bold text-light mb-3">Privacy & Trust</h6>
                    <ul class="list-unstyled small text-secondary mb-0">
                        <li class="mb-2"><span class="text-secondary">Personal Mode by Default</span></li>
                        <li class="mb-2"><span class="text-secondary">Zero Telemetry Leaks</span></li>
                        <li><span class="text-secondary">Server-Side Authorization</span></li>
                    </ul>
                </div>
            </div>
            <div class="border-top border-secondary pt-3 d-flex flex-column flex-sm-row justify-content-between align-items-center text-secondary small">
                <div>&copy; 2026 FitAura. All rights reserved.</div>
                <div class="mt-2 mt-sm-0">Track. Improve. Thrive.</div>
            </div>
        </div>
    </footer>

    <!-- Bootstrap 5 JavaScript Bundle -->
    <script src="https://cdn.jsdelivr.net/npm/bootstrap@5.3.3/dist/js/bootstrap.bundle.min.js" integrity="sha384-YvpcrYf0tY3lHB60NNkmXc5s9fDVZLESaAA55NDzOxhy9GkcIdslK1eN7N6jIeHz" crossorigin="anonymous"></script>
    <!-- FitAura JavaScript -->
    <script src="${pageContext.request.contextPath}/js/fitaura.js"></script>
</body>
</html>
