# FitAura – Track. Improve. Thrive.

FitAura is a privacy-first web application designed for personal fitness tracking, goal management, and community fitness challenges. Built with a Java and Jakarta EE backend, the platform enables users to log workouts, track metabolic indicators, set fitness goals, and participate in community challenges while maintaining full control over the privacy of their personal health data.

---

## 1. Project Overview

Tracking personal fitness often requires balancing self-improvement with data privacy. Many modern fitness applications automatically broadcast personal workouts, body measurements, and fitness metrics to public feeds without fine-grained user control.

FitAura addresses this by offering a privacy-first fitness platform where users decide how their data is shared:
- **Everyday Athletes & Beginners**: Log workouts, monitor progress over time, and receive rule-based personalized guidance.
- **Goal-Oriented Users**: Define target goals across weight management, strength, endurance, or flexibility, and track progress against clear milestones.
- **Community Participants**: Opt into social challenges, leaderboards, and competitions using public display aliases without exposing private biometrics or workout histories.

---

## 2. Key Features

### User Accounts & Profile Management
- User registration and authentication with secure password hashing.
- Profile management with customizable display names and account preferences.
- Privacy mode toggling between **Personal** and **Social** modes.

### Fitness Profile & Biometric Calculations
- Comprehensive fitness profile capturing age, gender, height, weight, activity level, and preferred workout environment (gym, home, outdoor, mixed).
- Real-time metabolic calculations:
  - **Body Mass Index (BMI)** classification.
  - **Basal Metabolic Rate (BMR)** computed via the Mifflin-St Jeor formula.
  - **Total Daily Energy Expenditure (TDEE)** estimates based on activity multipliers.

### Workout Tracking
- Logging of diverse activity types: Running, Walking, Cycling, Strength Training, and Home Workouts.
- Records duration, intensity level (Low, Medium, High), estimated calories burned, workout dates, and personal notes.
- Historical log filtering and summary views.

### Goals & Progress Monitoring
- Goal setting across categories: Weight Loss, Weight Gain, Muscle Gain, Strength, Endurance, Flexibility, and General Fitness.
- Target values, progress updates, start and target dates, and status tracking (Active, Completed, Paused, Cancelled).
- Workout consistency tracking and milestone monitoring.

### Personalized Fitness Guidance
- Built-in rule-based recommendation engine that evaluates user profile biometrics, activity level, preferred training environment, and recent workout patterns to provide actionable fitness advice.

### Gamification & Community Features
- **Points & Achievements**: Automatic point awards for logged workouts and challenge milestones, unlocking achievement badges.
- **Streaks**: Real-time tracking of current and longest consecutive activity streaks.
- **Social Leaderboards**: Periodic leaderboards (Weekly, Monthly, All-Time) ranking opted-in community members by earned points using standard sports tie-breaking logic.
- **Competitions & Challenges**: Dedicated fitness competitions with defined metrics, duration, participant tracking, and leaderboards.
- **Social Connections**: Optional member connection requests and public activity milestone feeds for users in Social mode.

### Daily Motivation & Quotes
- Scheduled daily motivational fitness quotes displayed on user dashboards.
- Database-backed quote scheduling ensuring a single consistent quote per calendar day with graceful fallback handling.

### Fitness Content & Community Library
- Educational fitness articles, guides, and workout tips organized by category.
- Moderation workflow supporting draft, pending review, approved, and rejected states.

### Administrative Management
- Dedicated admin console for system oversight.
- User management and account status controls (Active, Inactive, Blocked).
- Content moderation for community-submitted articles and guides.
- Management of daily quotes, community challenges, and competitions.
- System activity and security audit logging.

---

## 3. Privacy First

Privacy is a core design principle of FitAura. Users choose how their data is handled through two privacy modes:

- **Personal Mode (Default)**: All user data remains completely private. Personal users never appear on public leaderboards, competition rankings, or social discovery feeds.
- **Social Mode (Opt-In)**: Users choose to participate in community features. In Social mode, only the public display name, rank, points, and earned milestone badges are visible to other members.

### Strict Privacy Protections
Sensitive information is never exposed publicly regardless of the selected privacy mode:
- Passwords and authentication credentials are strictly protected.
- Email addresses are kept private.
- Body measurements (weight, height) and biometric values (BMI, BMR, TDEE) remain private to the user.
- Private workout notes and individual goal details are restricted to the account owner.
- Switching from Social mode back to Personal mode immediately removes the user from public rankings and community views.

---

## 4. How FitAura Works

A typical user journey follows this workflow:

```
Register Account
      ↓
Complete Fitness Profile (Height, Weight, Activity Level)
      ↓
View Metabolic Stats (BMI, BMR, TDEE)
      ↓
Set Fitness Goals (Target Weight, Strength, Endurance)
      ↓
Log Workouts (Running, Strength, Cycling, Home Workouts)
      ↓
Track Progress & Consistency
      ↓
Receive Personalized Guidance
      ↓
Participate in Challenges & Competitions (Optional Social Mode)
      ↓
Earn Points, Badges, and Streaks
```

---

## 5. Technology Stack

- **Backend Platform**: Java 17+
- **Web Layer**: Jakarta Servlets 6.0, JSP 3.1, JSTL 3.0
- **Frontend**: HTML5, CSS3, JavaScript, Bootstrap 5
- **Database**: MySQL 8.0+
- **Persistence & Connectivity**: JDBC (PreparedStatement API), HikariCP 5.1 Connection Pool
- **Security & Cryptography**: jBCrypt (Password Hashing)
- **JSON Processing**: Google Gson
- **Logging**: SLF4J
- **Build & Dependency Management**: Apache Maven 3.8+ (with Maven Wrapper `./mvnw`)
- **Application Server**: Apache Tomcat 10.1+ (Jakarta EE 10 compatible)
- **Automated Testing**: JUnit 5

---

## 6. Project Architecture

FitAura follows a multi-tier Model-View-Controller (MVC) architecture ensuring clean separation of concerns:

```
Browser (HTML5, CSS3, JavaScript, Bootstrap)
       ↓
JSP View Layer (JSP templates in /src/main/webapp)
       ↓
Servlet Controllers (Jakarta Servlets in com.fitaura.controller)
       ↓
Security & Request Filters (Authentication, Authorization, CSRF, Security Headers)
       ↓
Service Business Layer (com.fitaura.service)
       ↓
Data Access Object (DAO) Layer (com.fitaura.dao)
       ↓
HikariCP Connection Pool & JDBC (PreparedStatement API)
       ↓
MySQL Relational Database
```

- **Controllers**: Handle HTTP requests, parse inputs, manage session state, and dispatch to JSP views.
- **Filters**: Enforce session authentication, role-based access control, CSRF validation, and security headers before requests reach controllers.
- **Services**: Contain business logic, validation rules, metabolic formulas, gamification scoring, and privacy filtering.
- **DAOs**: Manage database persistence using parameterized SQL queries.

---

## 7. Database

The application utilizes a relational schema structured around core fitness and community entities:

- **users**: Account credentials, roles (`USER`, `ADMIN`), account status, and privacy mode (`PERSONAL`, `SOCIAL`).
- **fitness_profiles**: Biometric measurements, activity levels, and workout environment preferences.
- **fitness_goals**: User goals, target metrics, deadlines, and completion statuses.
- **workouts**: Workout logs with activity types, duration, intensity, estimated calories, and dates.
- **challenges & challenge_participants**: Community fitness challenges, participant enrollment, and progress.
- **competitions & competition_participants**: Time-bound competitions, score tracking, and participant status.
- **daily_quotes**: Admin-scheduled motivational quotes with publication dates and statuses.
- **achievements & user_achievements**: Milestone badge definitions and user unlock records.
- **point_transactions**: Gamification point transaction ledger.
- **social_connections & social_activity**: Optional member connections and milestone activity events.
- **fitness_content**: Community library guides, articles, and review statuses.
- **activity_logs**: Administrative and security event audit logs.
- **system_settings**: Application-level key-value configuration.

---

## 8. User and Admin Roles

FitAura maintains role-based access control separating regular members from system administrators:

- **USER**:
  - Full access to personal fitness tracking, profile settings, goal management, and workout logs.
  - Ability to choose between Personal and Social privacy modes.
  - Participation in community challenges, competitions, and leaderboards when Social mode is active.
  - Access to personalized guidance, educational content, and daily motivation.

- **ADMIN**:
  - All standard user capabilities.
  - Access to the administrative dashboard (`/admin/dashboard`).
  - Management of user accounts and status changes (active, inactive, blocked).
  - Moderation of community-submitted fitness content (approve, reject).
  - Scheduling and management of daily motivational quotes.
  - Creation and management of community challenges and competitions.
  - Review of administrative audit logs and platform activity.

Administrative accounts are restricted and configured through secure deployment environment variables during server initialization.

---

## 9. Privacy and Security

FitAura implements several layered security controls:

- **Password Hashing**: Passwords are encrypted using BCrypt with secure salt generation prior to storage.
- **SQL Injection Prevention**: All database operations use `PreparedStatement` parameter placeholders. No dynamic SQL string concatenation is permitted.
- **Session Security**:
  - Secure session tracking with 30-minute inactivity timeouts.
  - Session fixation protection that rotates session IDs upon successful authentication.
  - Role-based authorization filters restricting administrative routes.
- **Cross-Site Request Forgery (CSRF) Protection**: Cryptographically generated synchronizer tokens validated on all state-altering POST requests.
- **Cross-Site Scripting (XSS) Prevention**: User-supplied input is sanitized and rendered with HTML entity escaping and JSTL `<c:out>` tags.
- **Insecure Direct Object Reference (IDOR) Checks**: Service and DAO methods verify user ownership on data access requests.
- **HTTP Security Headers**: Enforces `X-Content-Type-Options: nosniff`, `X-Frame-Options: SAMEORIGIN`, and `Referrer-Policy: strict-origin-when-cross-origin`.
- **Audit Logging**: Significant administrative actions, authentication attempts, and profile modifications are recorded in the activity log table.

---

## 10. Local Setup

### Requirements
- **Java Development Kit (JDK)**: Version 17 or higher
- **Database**: MySQL 8.0+
- **Build Tool**: Apache Maven 3.8+ (or the included `./mvnw` wrapper)
- **Application Server**: Apache Tomcat 10.1+ (Jakarta EE 10 compatible)

### 1. Database Setup
Log into your local MySQL server and execute the schema initialization script:

```bash
mysql -u your_database_username -p < src/main/resources/schema.sql
```

### 2. Configuration
FitAura resolves database credentials and server settings dynamically from environment variables with fallback to `src/main/resources/application.properties`. Both standard (`DB_*`) and application-prefixed (`FITAURA_DB_*`) environment variables are supported.

Copy `.env.example` to `.env` or set environment variables in your deployment environment:

```bash
# Database Connectivity (Either standard or FITAURA_ prefixed)
export DB_URL="jdbc:mysql://localhost:3306/fitaura?useSSL=false&allowPublicKeyRetrieval=true&serverTimezone=UTC&characterEncoding=UTF-8"
export DB_USERNAME="your_database_username"
export DB_PASSWORD="your_database_password"

# Optional Connection Pool Settings
export DB_MAX_POOL_SIZE="10"
export DB_MIN_IDLE="2"

# Administrator Account Bootstrap (Optional on initial startup)
export ADMIN_EMAIL="your_admin_email@example.com"
export ADMIN_PASSWORD="your_admin_password"
export ADMIN_NAME="System Administrator"
```

### 3. Build the Application
Use Maven to compile and package the application:

```bash
# Clean and compile sources
./mvnw clean compile

# Run the automated test suite
./mvnw test

# Package the application WAR file
./mvnw clean package
```

The compiled WAR artifact will be generated at:
```
target/fitaura.war
```

### 4. Deploy to Apache Tomcat
1. Copy the generated WAR file into your Tomcat `webapps` directory:
   ```bash
   cp target/fitaura.war $CATALINA_HOME/webapps/
   ```
2. Start Apache Tomcat:
   ```bash
   # On Linux / macOS
   $CATALINA_HOME/bin/startup.sh

   # On Windows
   %CATALINA_HOME%\bin\startup.bat
   ```
3. Open your browser and navigate to:
   - **Application Home**: `http://localhost:8080/fitaura/`
   - **Login**: `http://localhost:8080/fitaura/login`
   - **Registration**: `http://localhost:8080/fitaura/register`
   - **User Dashboard**: `http://localhost:8080/fitaura/user/dashboard`
   - **Admin Console**: `http://localhost:8080/fitaura/admin/dashboard`

---

## 11. Build

Common Maven commands for building and testing the project:

```bash
# Clean previous build artifacts and compile all classes
./mvnw clean compile

# Execute all automated unit and integration tests
./mvnw test

# Run a specific test class
./mvnw test -Dtest=DailyMotivationAndSocialFitnessTest

# Build the distributable WAR package (skipping test execution)
./mvnw clean package -DskipTests

# Build the complete WAR package with all tests verified
./mvnw clean package
```

The resulting deployment archive is located at:
```
target/fitaura.war
```

---

## 12. Project Structure

```
FitAura/
├── pom.xml
├── README.md
├── .gitignore
├── .env.example
├── fitaura_schema.sql
├── mvnw
├── mvnw.cmd
├── src/
│   ├── main/
│   │   ├── java/
│   │   │   └── com/fitaura/
│   │   │       ├── controller/
│   │   │       ├── dao/
│   │   │       │   └── impl/
│   │   │       ├── dto/
│   │   │       ├── exception/
│   │   │       ├── filter/
│   │   │       ├── model/
│   │   │       ├── service/
│   │   │       │   └── impl/
│   │   │       └── util/
│   │   ├── resources/
│   │   │   ├── application.properties
│   │   │   └── schema.sql
│   │   └── webapp/
│   │       ├── admin/
│   │       ├── challenges/
│   │       ├── content/
│   │       ├── css/
│   │       ├── error/
│   │       ├── gamification/
│   │       ├── goals/
│   │       ├── guidance/
│   │       ├── images/
│   │       ├── js/
│   │       ├── social/
│   │       ├── user/
│   │       ├── workouts/
│   │       ├── WEB-INF/
│   │       └── index.jsp
│   └── test/
│       └── java/
│           └── com/fitaura/
└── target/
```

---

## 13. GitHub / Security Notes

To maintain security when contributing or managing the repository:
- Never commit sensitive configuration files containing actual passwords, database credentials, API tokens, or secrets.
- Keep local `.env` and configuration override files in `.gitignore`.
- Only commit `.env.example` or template property files with descriptive placeholders.
- Do not commit generated build directories (`target/`, compiled `.war` or `.class` files).

---

## 14. Project Status

FitAura is an active, fully functional fitness web application. Core feature modules—including authentication, fitness profiling, metabolic calculations, workout and goal tracking, rule-based guidance, gamification, privacy controls, social hub, daily motivation, and administrative management—are implemented and validated by an automated test suite.
