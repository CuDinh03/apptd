-- Schema cốt lõi (MySQL / utf8mb4) — đồng bộ với JPA entities.
-- Chỉ dùng trên DB trống hoặc đã baseline Flyway; không DROP bảng để tránh mất dữ liệu.
SET NAMES utf8mb4;

CREATE TABLE users (
    id BIGINT NOT NULL AUTO_INCREMENT,
    username VARCHAR(100) NOT NULL,
    password_hash VARCHAR(255) NOT NULL,
    role VARCHAR(30) NOT NULL,
    total_xp BIGINT NOT NULL,
    created_at TIMESTAMP(6) NOT NULL,
    updated_at TIMESTAMP(6) NOT NULL,
    PRIMARY KEY (id),
    UNIQUE KEY uk_username (username)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

CREATE TABLE lessons (
    id BIGINT NOT NULL AUTO_INCREMENT,
    title VARCHAR(200) NOT NULL,
    level VARCHAR(10) NOT NULL,
    category VARCHAR(30) NOT NULL,
    xp_reward INT NOT NULL,
    menschen_order INT NULL,
    usage_notes_json TEXT,
    example_phrases_json TEXT,
    PRIMARY KEY (id)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

CREATE TABLE vocabularies (
    id BIGINT NOT NULL AUTO_INCREMENT,
    lesson_id BIGINT NOT NULL,
    word VARCHAR(100) NOT NULL,
    article VARCHAR(10) NULL,
    plural_form VARCHAR(100) NULL,
    meaning_vi VARCHAR(255) NOT NULL,
    pronunciation_ipa VARCHAR(160) NULL,
    usage_note_vi VARCHAR(600) NULL,
    audio_url VARCHAR(500) NULL,
    PRIMARY KEY (id),
    CONSTRAINT fk_vocabularies_lesson FOREIGN KEY (lesson_id) REFERENCES lessons (id)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

CREATE TABLE lesson_exercises (
    id BIGINT NOT NULL AUTO_INCREMENT,
    lesson_id BIGINT NOT NULL,
    exercise_type VARCHAR(30) NOT NULL,
    question_text VARCHAR(1000) NOT NULL,
    choices_json LONGTEXT NULL,
    correct_answer VARCHAR(500) NOT NULL,
    sort_order INT NOT NULL,
    PRIMARY KEY (id),
    CONSTRAINT fk_lesson_exercises_lesson FOREIGN KEY (lesson_id) REFERENCES lessons (id)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

CREATE TABLE speaking_topics (
    id BIGINT NOT NULL AUTO_INCREMENT,
    context_prompt LONGTEXT NOT NULL,
    level_requirement VARCHAR(10) NOT NULL,
    PRIMARY KEY (id)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

CREATE TABLE user_progress (
    id BIGINT NOT NULL AUTO_INCREMENT,
    user_id BIGINT NOT NULL,
    lesson_id BIGINT NOT NULL,
    status VARCHAR(30) NOT NULL,
    last_score_percent INT NOT NULL,
    best_score_percent INT NOT NULL,
    attempts INT NOT NULL,
    xp_awarded_total BIGINT NOT NULL,
    completed_at TIMESTAMP(6) NULL,
    PRIMARY KEY (id),
    CONSTRAINT uk_user_lesson UNIQUE (user_id, lesson_id),
    CONSTRAINT fk_user_progress_user FOREIGN KEY (user_id) REFERENCES users (id),
    CONSTRAINT fk_user_progress_lesson FOREIGN KEY (lesson_id) REFERENCES lessons (id)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;
