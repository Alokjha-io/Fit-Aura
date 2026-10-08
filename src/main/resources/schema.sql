-- =====================================================================
-- FitAura Database Schema (MySQL 8.0+)
-- Source of Truth: FITAURA BACKEND SCHEMA & DATABASE DESIGN v1.0
-- =====================================================================

CREATE DATABASE IF NOT EXISTS fitaura CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci;
USE fitaura;

-- 1. Users Table
CREATE TABLE IF NOT EXISTS users (
    user_id INT PRIMARY KEY AUTO_INCREMENT,
    full_name VARCHAR(100) NOT NULL,
    email VARCHAR(150) NOT NULL UNIQUE,
    password_hash VARCHAR(255) NOT NULL,
    role ENUM('USER','ADMIN') NOT NULL DEFAULT 'USER',
    account_status ENUM('ACTIVE','INACTIVE','BLOCKED') NOT NULL DEFAULT 'ACTIVE',
    privacy_mode ENUM('PERSONAL','SOCIAL') NOT NULL DEFAULT 'PERSONAL',
    display_name VARCHAR(60),
    created_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    last_login_at TIMESTAMP NULL
);

-- 2. Fitness Profiles Table
CREATE TABLE IF NOT EXISTS fitness_profiles (
    profile_id INT PRIMARY KEY AUTO_INCREMENT,
    user_id INT NOT NULL UNIQUE,
    age INT,
    gender ENUM('MALE','FEMALE','OTHER','PREFER_NOT_TO_SAY'),
    height_cm DECIMAL(5,2),
    weight_kg DECIMAL(5,2),
    activity_level ENUM('BEGINNER','LIGHT','MODERATE','ACTIVE','VERY_ACTIVE') DEFAULT 'BEGINNER',
    preferred_environment ENUM('GYM','HOME','OUTDOOR','MIXED') DEFAULT 'MIXED',
    FOREIGN KEY (user_id) REFERENCES users(user_id) ON DELETE CASCADE
);

-- 3. Fitness Goals Table
CREATE TABLE IF NOT EXISTS fitness_goals (
    goal_id INT PRIMARY KEY AUTO_INCREMENT,
    user_id INT NOT NULL,
    goal_type ENUM('WEIGHT_LOSS','WEIGHT_GAIN','MUSCLE_GAIN','STRENGTH','ENDURANCE','GENERAL_FITNESS','FLEXIBILITY') NOT NULL,
    target_value DECIMAL(10,2),
    current_value DECIMAL(10,2),
    unit VARCHAR(30),
    start_date DATE NOT NULL,
    target_date DATE,
    status ENUM('ACTIVE','COMPLETED','PAUSED','CANCELLED') NOT NULL DEFAULT 'ACTIVE',
    notes VARCHAR(500),
    created_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    FOREIGN KEY (user_id) REFERENCES users(user_id) ON DELETE CASCADE,
    INDEX idx_goals_user_status (user_id, status)
);

-- 4. Workouts Table
CREATE TABLE IF NOT EXISTS workouts (
    workout_id INT PRIMARY KEY AUTO_INCREMENT,
    user_id INT NOT NULL,
    workout_type ENUM('RUNNING','WALKING','CYCLING','STRENGTH','HOME_WORKOUT') NOT NULL,
    workout_date DATE NOT NULL,
    duration_minutes INT NOT NULL,
    intensity ENUM('LOW','MEDIUM','HIGH') NOT NULL,
    calories_burned INT DEFAULT 0,
    notes VARCHAR(1000),
    created_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    FOREIGN KEY (user_id) REFERENCES users(user_id) ON DELETE CASCADE,
    INDEX idx_workouts_user_date (user_id, workout_date)
);

-- 5. Challenges Table
CREATE TABLE IF NOT EXISTS challenges (
    challenge_id INT PRIMARY KEY AUTO_INCREMENT,
    created_by INT NOT NULL,
    name VARCHAR(120) NOT NULL,
    description VARCHAR(1000),
    goal_value DECIMAL(10,2) NOT NULL,
    goal_unit VARCHAR(30) NOT NULL,
    points_reward INT NOT NULL DEFAULT 0,
    start_date DATE NOT NULL,
    end_date DATE NOT NULL,
    status ENUM('DRAFT','ACTIVE','COMPLETED','CANCELLED') NOT NULL DEFAULT 'DRAFT',
    created_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    FOREIGN KEY (created_by) REFERENCES users(user_id),
    INDEX idx_challenges_status_dates (status, start_date, end_date)
);

-- 6. Challenge Participants Table
CREATE TABLE IF NOT EXISTS challenge_participants (
    participation_id INT PRIMARY KEY AUTO_INCREMENT,
    challenge_id INT NOT NULL,
    user_id INT NOT NULL,
    progress_value DECIMAL(10,2) NOT NULL DEFAULT 0,
    status ENUM('JOINED','IN_PROGRESS','COMPLETED','LEFT') NOT NULL DEFAULT 'JOINED',
    joined_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    completed_at TIMESTAMP NULL,
    UNIQUE KEY uq_challenge_user (challenge_id, user_id),
    FOREIGN KEY (challenge_id) REFERENCES challenges(challenge_id) ON DELETE CASCADE,
    FOREIGN KEY (user_id) REFERENCES users(user_id) ON DELETE CASCADE,
    INDEX idx_participant_user (user_id)
);

-- 7. Fitness Content Table
CREATE TABLE IF NOT EXISTS fitness_content (
    content_id INT PRIMARY KEY AUTO_INCREMENT,
    created_by INT NOT NULL,
    title VARCHAR(200) NOT NULL,
    category ENUM('WORKOUT','EXERCISE','NUTRITION','FITNESS_TIP','GUIDE') NOT NULL,
    content_text TEXT NOT NULL,
    approval_status ENUM('PENDING','APPROVED','REJECTED') NOT NULL DEFAULT 'PENDING',
    reviewed_by INT NULL,
    reviewed_at TIMESTAMP NULL,
    created_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    FOREIGN KEY (created_by) REFERENCES users(user_id),
    FOREIGN KEY (reviewed_by) REFERENCES users(user_id),
    INDEX idx_content_status (approval_status)
);

-- 8. Point Transactions Table
CREATE TABLE IF NOT EXISTS point_transactions (
    transaction_id INT PRIMARY KEY AUTO_INCREMENT,
    user_id INT NOT NULL,
    source_type ENUM('WORKOUT','CHALLENGE','ACHIEVEMENT','BONUS','ADJUSTMENT') NOT NULL,
    source_id INT NULL,
    points INT NOT NULL,
    description VARCHAR(255),
    created_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    FOREIGN KEY (user_id) REFERENCES users(user_id) ON DELETE CASCADE,
    INDEX idx_points_user_date (user_id, created_at)
);

-- 9. Achievements Table
CREATE TABLE IF NOT EXISTS achievements (
    achievement_id INT PRIMARY KEY AUTO_INCREMENT,
    name VARCHAR(100) NOT NULL UNIQUE,
    description VARCHAR(500),
    points INT NOT NULL DEFAULT 0,
    icon_name VARCHAR(100),
    is_active BOOLEAN NOT NULL DEFAULT TRUE
);

-- 10. User Achievements Table
CREATE TABLE IF NOT EXISTS user_achievements (
    user_achievement_id INT PRIMARY KEY AUTO_INCREMENT,
    user_id INT NOT NULL,
    achievement_id INT NOT NULL,
    earned_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    UNIQUE KEY uq_user_achievement (user_id, achievement_id),
    FOREIGN KEY (user_id) REFERENCES users(user_id) ON DELETE CASCADE,
    FOREIGN KEY (achievement_id) REFERENCES achievements(achievement_id) ON DELETE CASCADE
);

-- 11. Activity Logs Table
CREATE TABLE IF NOT EXISTS activity_logs (
    log_id BIGINT PRIMARY KEY AUTO_INCREMENT,
    user_id INT NULL,
    action_type VARCHAR(80) NOT NULL,
    entity_type VARCHAR(80),
    entity_id INT NULL,
    description VARCHAR(500),
    ip_address VARCHAR(45),
    created_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    FOREIGN KEY (user_id) REFERENCES users(user_id) ON DELETE SET NULL,
    INDEX idx_activity_user_date (user_id, created_at),
    INDEX idx_activity_action_date (action_type, created_at)
);

-- 12. System Settings Table
CREATE TABLE IF NOT EXISTS system_settings (
    setting_id INT PRIMARY KEY AUTO_INCREMENT,
    setting_key VARCHAR(100) NOT NULL UNIQUE,
    setting_value VARCHAR(500) NOT NULL,
    description VARCHAR(500),
    updated_by INT NULL,
    updated_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    FOREIGN KEY (updated_by) REFERENCES users(user_id) ON DELETE SET NULL
);

-- 13. Daily Quotes Table (Phase 14)
CREATE TABLE IF NOT EXISTS daily_quotes (
    quote_id INT PRIMARY KEY AUTO_INCREMENT,
    quote_text TEXT NOT NULL,
    author_name VARCHAR(150) NOT NULL DEFAULT 'Anonymous',
    quote_date DATE NOT NULL,
    status ENUM('DRAFT', 'PUBLISHED', 'ARCHIVED') NOT NULL DEFAULT 'PUBLISHED',
    created_by INT,
    created_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    FOREIGN KEY (created_by) REFERENCES users(user_id) ON DELETE SET NULL,
    INDEX idx_quote_date_status (quote_date, status)
);

-- 14. Competitions Table (Phase 14)
CREATE TABLE IF NOT EXISTS competitions (
    competition_id INT PRIMARY KEY AUTO_INCREMENT,
    name VARCHAR(150) NOT NULL,
    description TEXT,
    metric ENUM('WORKOUT_COUNT', 'TOTAL_MINUTES', 'CALORIES_BURNED', 'STREAK_DAYS') NOT NULL DEFAULT 'WORKOUT_COUNT',
    target_value DECIMAL(10,2) DEFAULT NULL,
    start_date DATE NOT NULL,
    end_date DATE NOT NULL,
    reward_points INT NOT NULL DEFAULT 50,
    status ENUM('UPCOMING', 'ACTIVE', 'COMPLETED', 'CANCELLED') NOT NULL DEFAULT 'ACTIVE',
    created_by INT,
    created_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    FOREIGN KEY (created_by) REFERENCES users(user_id) ON DELETE SET NULL,
    INDEX idx_comp_status_dates (status, start_date, end_date)
);

-- 15. Competition Participants Table (Phase 14)
CREATE TABLE IF NOT EXISTS competition_participants (
    participant_id INT PRIMARY KEY AUTO_INCREMENT,
    competition_id INT NOT NULL,
    user_id INT NOT NULL,
    joined_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    current_score DECIMAL(10,2) NOT NULL DEFAULT 0.00,
    completed BOOLEAN NOT NULL DEFAULT FALSE,
    completed_at TIMESTAMP NULL,
    FOREIGN KEY (competition_id) REFERENCES competitions(competition_id) ON DELETE CASCADE,
    FOREIGN KEY (user_id) REFERENCES users(user_id) ON DELETE CASCADE,
    UNIQUE KEY uk_comp_user (competition_id, user_id),
    INDEX idx_comp_score (competition_id, current_score DESC)
);

-- 16. Social Connections Table (Phase 14)
CREATE TABLE IF NOT EXISTS social_connections (
    connection_id INT PRIMARY KEY AUTO_INCREMENT,
    requester_id INT NOT NULL,
    receiver_id INT NOT NULL,
    status ENUM('PENDING', 'ACCEPTED', 'REJECTED') NOT NULL DEFAULT 'PENDING',
    created_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    FOREIGN KEY (requester_id) REFERENCES users(user_id) ON DELETE CASCADE,
    FOREIGN KEY (receiver_id) REFERENCES users(user_id) ON DELETE CASCADE,
    UNIQUE KEY uk_social_conn (requester_id, receiver_id),
    INDEX idx_receiver_status (receiver_id, status)
);

-- 17. Social Activity Table (Phase 14)
CREATE TABLE IF NOT EXISTS social_activity (
    activity_id INT PRIMARY KEY AUTO_INCREMENT,
    user_id INT NOT NULL,
    activity_type ENUM('STREAK_MILESTONE', 'ACHIEVEMENT_UNLOCKED', 'CHALLENGE_COMPLETED', 'COMPETITION_JOINED', 'COMPETITION_WON') NOT NULL,
    title VARCHAR(200) NOT NULL,
    description TEXT,
    created_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    FOREIGN KEY (user_id) REFERENCES users(user_id) ON DELETE CASCADE,
    INDEX idx_activity_time (created_at DESC)
);

-- Initial Seed Data for Phase 2 preparation
INSERT IGNORE INTO achievements (name, description, points, icon_name) VALUES
('First Workout', 'Complete your first workout', 10, 'first-workout'),
('7 Day Streak', 'Maintain a qualifying activity streak for 7 days', 50, 'streak-7'),
('Challenge Completed', 'Complete a fitness challenge', 100, 'challenge-complete');

INSERT IGNORE INTO system_settings (setting_key, setting_value, description) VALUES
('registration_enabled', 'true', 'Allow new user registration'),
('challenges_enabled', 'true', 'Enable challenges'),
('content_approval_required', 'true', 'Require admin approval for fitness content');
