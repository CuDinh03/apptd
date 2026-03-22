-- Schema cốt lõi (PostgreSQL / UTF8) — đồng bộ với JPA entities.
-- Chỉ dùng trên DB trống hoặc đã baseline Flyway; không DROP bảng để tránh mất dữ liệu.

CREATE TABLE users (
    id BIGSERIAL NOT NULL,
    username VARCHAR(100) NOT NULL,
    password_hash VARCHAR(255) NOT NULL,
    role VARCHAR(30) NOT NULL,
    total_xp BIGINT NOT NULL,
    created_at TIMESTAMP(6) NOT NULL,
    updated_at TIMESTAMP(6) NOT NULL,
    PRIMARY KEY (id),
    CONSTRAINT uk_username UNIQUE (username)
);

CREATE TABLE lessons (
    id BIGSERIAL NOT NULL,
    title VARCHAR(200) NOT NULL,
    level VARCHAR(10) NOT NULL,
    category VARCHAR(30) NOT NULL,
    xp_reward INT NOT NULL,
    menschen_order INT NULL,
    usage_notes_json TEXT,
    example_phrases_json TEXT,
    PRIMARY KEY (id)
);

CREATE TABLE vocabularies (
    id BIGSERIAL NOT NULL,
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
);

CREATE TABLE lesson_exercises (
    id BIGSERIAL NOT NULL,
    lesson_id BIGINT NOT NULL,
    exercise_type VARCHAR(30) NOT NULL,
    question_text VARCHAR(1000) NOT NULL,
    choices_json TEXT NULL,
    correct_answer VARCHAR(500) NOT NULL,
    sort_order INT NOT NULL,
    PRIMARY KEY (id),
    CONSTRAINT fk_lesson_exercises_lesson FOREIGN KEY (lesson_id) REFERENCES lessons (id)
);

CREATE TABLE speaking_topics (
    id BIGSERIAL NOT NULL,
    context_prompt TEXT NOT NULL,
    level_requirement VARCHAR(10) NOT NULL,
    PRIMARY KEY (id)
);

CREATE TABLE user_progress (
    id BIGSERIAL NOT NULL,
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
);
