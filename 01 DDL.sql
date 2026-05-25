-- ============================================================
-- AlbaBot — DDL
-- ============================================================
CREATE DATABASE IF NOT EXISTS albabot
    CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci;
USE albabot;

-- 1. users
CREATE TABLE IF NOT EXISTS users (
    user_id       INT AUTO_INCREMENT PRIMARY KEY,
    email         VARCHAR(255) NOT NULL UNIQUE,
    password_hash VARCHAR(255) NOT NULL,
    name          VARCHAR(50)  NOT NULL,
    phone         VARCHAR(20),
    role          ENUM('EMPLOYER','SEEKER','ADMIN') NOT NULL,
    region        VARCHAR(100),
    preferred_time VARCHAR(100),
    created_at    DATETIME DEFAULT CURRENT_TIMESTAMP,
    is_active     TINYINT(1) DEFAULT 1
);

-- 2. user_categories
CREATE TABLE IF NOT EXISTS user_categories (
    id       INT AUTO_INCREMENT PRIMARY KEY,
    user_id  INT         NOT NULL,
    category VARCHAR(50) NOT NULL,
    FOREIGN KEY (user_id) REFERENCES users(user_id) ON DELETE CASCADE
);

-- 3. jobs
CREATE TABLE IF NOT EXISTS jobs (
    job_id      INT AUTO_INCREMENT PRIMARY KEY,
    employer_id INT          NOT NULL,
    title       VARCHAR(255) NOT NULL,
    category    VARCHAR(50),
    hourly_wage INT,
    location    VARCHAR(255),
    work_hours  VARCHAR(100),
    deadline    DATETIME,
    description TEXT,
    status      ENUM('OPEN','CLOSED') DEFAULT 'OPEN',
    created_at  DATETIME DEFAULT CURRENT_TIMESTAMP,
    FOREIGN KEY (employer_id) REFERENCES users(user_id) ON DELETE CASCADE
);
CREATE INDEX idx_jobs_category_status_deadline ON jobs(category, status, deadline);

-- 4. applications
CREATE TABLE IF NOT EXISTS applications (
    application_id INT AUTO_INCREMENT PRIMARY KEY,
    user_id        INT NOT NULL,
    job_id         INT NOT NULL,
    applied_at     DATETIME DEFAULT CURRENT_TIMESTAMP,
    status         ENUM('PENDING','ACCEPTED','REJECTED') DEFAULT 'PENDING',
    cover_letter   TEXT,
    UNIQUE KEY unique_user_job (user_id, job_id),
    FOREIGN KEY (user_id) REFERENCES users(user_id) ON DELETE CASCADE,
    FOREIGN KEY (job_id)  REFERENCES jobs(job_id)   ON DELETE CASCADE
);

-- 5. evaluations
CREATE TABLE IF NOT EXISTS evaluations (
    eval_id     INT AUTO_INCREMENT PRIMARY KEY,
    reviewer_id INT NOT NULL,
    reviewee_id INT NOT NULL,
    job_id      INT NOT NULL,
    score       INT NOT NULL CHECK (score BETWEEN 1 AND 5),
    comment     TEXT,
    eval_type   ENUM('EMPLOYER_TO_SEEKER','SEEKER_TO_EMPLOYER') NOT NULL,
    created_at  DATETIME DEFAULT CURRENT_TIMESTAMP,
    FOREIGN KEY (reviewer_id) REFERENCES users(user_id) ON DELETE CASCADE,
    FOREIGN KEY (reviewee_id) REFERENCES users(user_id) ON DELETE CASCADE,
    FOREIGN KEY (job_id)      REFERENCES jobs(job_id)   ON DELETE CASCADE
);

-- 6. reports
CREATE TABLE IF NOT EXISTS reports (
    report_id      INT AUTO_INCREMENT PRIMARY KEY,
    reporter_id    INT  NOT NULL,
    target_job_id  INT  NULL,
    target_user_id INT  NULL,
    reason         TEXT NOT NULL,
    status         ENUM('PENDING','RESOLVED') DEFAULT 'PENDING',
    FOREIGN KEY (reporter_id)    REFERENCES users(user_id) ON DELETE CASCADE,
    FOREIGN KEY (target_job_id)  REFERENCES jobs(job_id)   ON DELETE SET NULL,
    FOREIGN KEY (target_user_id) REFERENCES users(user_id) ON DELETE SET NULL
);

-- 7. blocks
CREATE TABLE IF NOT EXISTS blocks (
    block_id        INT AUTO_INCREMENT PRIMARY KEY,
    blocker_id      INT NOT NULL,
    blocked_user_id INT NULL,
    blocked_job_id  INT NULL,
    blocked_at      DATETIME DEFAULT CURRENT_TIMESTAMP,
    FOREIGN KEY (blocker_id)      REFERENCES users(user_id) ON DELETE CASCADE,
    FOREIGN KEY (blocked_user_id) REFERENCES users(user_id) ON DELETE SET NULL,
    FOREIGN KEY (blocked_job_id)  REFERENCES jobs(job_id)   ON DELETE SET NULL
);
CREATE INDEX idx_blocks_blocker_id ON blocks(blocker_id);

-- 8. recommendations
CREATE TABLE IF NOT EXISTS recommendations (
    rec_id     INT AUTO_INCREMENT PRIMARY KEY,
    user_id    INT          NOT NULL,
    job_id     INT          NOT NULL,
    score      INT          NULL,
    reason     VARCHAR(255) NULL,
    is_viewed  TINYINT(1) DEFAULT 0,
    created_at DATETIME DEFAULT CURRENT_TIMESTAMP,
    FOREIGN KEY (user_id) REFERENCES users(user_id) ON DELETE CASCADE,
    FOREIGN KEY (job_id)  REFERENCES jobs(job_id)   ON DELETE CASCADE
);
CREATE INDEX idx_recommendations_user_score ON recommendations(user_id, score DESC);

-- 9. notifications
CREATE TABLE IF NOT EXISTS notifications (
    notif_id   INT AUTO_INCREMENT PRIMARY KEY,
    user_id    INT          NOT NULL,
    type       ENUM('APPLICATION_RESULT','NEW_RECOMMENDATION','SYSTEM_ALERT') NOT NULL,
    message    VARCHAR(255) NOT NULL,
    is_read    TINYINT(1) DEFAULT 0,
    created_at DATETIME DEFAULT CURRENT_TIMESTAMP,
    FOREIGN KEY (user_id) REFERENCES users(user_id) ON DELETE CASCADE
);
