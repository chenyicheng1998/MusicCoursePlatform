-- Music Course Platform Database Schema
-- Sprint 6: Database Localization
-- Strategy: Store canonical instrument keys (lowercase English) in all tables.
--            A dedicated INSTRUMENT table holds the authoritative translations
--            for English, Chinese, and Arabic -- the three supported locales.
--            The application's LocalizationManager looks up display names from
--            the i18n resource bundles using the same canonical key, so the UI
--            is always correct regardless of which language the teacher used
--            when saving their profile.

CREATE DATABASE IF NOT EXISTS music_course_platform
    CHARACTER SET utf8mb4
    COLLATE utf8mb4_unicode_ci;

USE music_course_platform;

-- Ensure UTF-8 for this session
SET NAMES utf8mb4;
SET CHARACTER SET utf8mb4;

SET FOREIGN_KEY_CHECKS = 0;

DROP TABLE IF EXISTS BOOKING;
DROP TABLE IF EXISTS TIMESLOT;
DROP TABLE IF EXISTS LEARNERPROFILE;
DROP TABLE IF EXISTS TEACHERPROFILE;
DROP TABLE IF EXISTS USERS;
DROP TABLE IF EXISTS INSTRUMENT;

SET FOREIGN_KEY_CHECKS = 1;

-- ==============================
-- INSTRUMENT (localization catalogue)
-- ==============================
-- This is the single source of truth for valid instrument keys.
-- The `instrument_key` is a lowercase English identifier that is stored in
-- TEACHERPROFILE and LEARNERPROFILE.  The name_* columns record the official
-- translations used for display across the three supported locales.
CREATE TABLE INSTRUMENT (
    instrument_key VARCHAR(20)  NOT NULL,
    name_en        VARCHAR(50)  NOT NULL COMMENT 'English display name',
    name_zh        VARCHAR(50)  NOT NULL COMMENT 'Chinese display name',
    name_ar        VARCHAR(50)  NOT NULL COMMENT 'Arabic display name',
    PRIMARY KEY (instrument_key)
) ENGINE=InnoDB
  DEFAULT CHARACTER SET utf8mb4
  COLLATE utf8mb4_unicode_ci;

-- Seed instrument reference data
INSERT INTO INSTRUMENT (instrument_key, name_en, name_zh, name_ar) VALUES
    ('piano',     'Piano',     '钢琴',    'بيانو'),
    ('guitar',    'Guitar',    '吉他',    'جيتار'),
    ('violin',    'Violin',    '小提琴',  'كمان'),
    ('drums',     'Drums',     '鼓',      'طبول'),
    ('flute',     'Flute',     '长笛',    'ناي'),
    ('saxophone', 'Saxophone', '萨克斯管','ساكسوفون'),
    ('cello',     'Cello',     '大提琴',  'شيللو'),
    ('voice',     'Voice',     '声乐',    'غناء');

-- ==============================
-- USERS TABLE
-- ==============================
CREATE TABLE USERS (
    user_id       INT          NOT NULL AUTO_INCREMENT,
    username      VARCHAR(50)  NOT NULL UNIQUE,
    password_hash VARCHAR(255) NOT NULL,
    email         VARCHAR(100) NOT NULL UNIQUE,
    user_type     VARCHAR(20)  NOT NULL,
    created_at    DATE         NOT NULL DEFAULT (CURRENT_DATE),
    PRIMARY KEY (user_id)
) ENGINE=InnoDB
  DEFAULT CHARACTER SET utf8mb4
  COLLATE utf8mb4_unicode_ci;

-- ==============================
-- TEACHER PROFILE
-- ==============================
-- `instrument_key` is a canonical lowercase key (e.g. 'piano') that references
-- the INSTRUMENT table.  Storing the key instead of a localised string means
-- teachers who sign up in Chinese or Arabic are still discoverable by students
-- browsing in any other language.
CREATE TABLE TEACHERPROFILE (
    teacher_profile_id INT          NOT NULL AUTO_INCREMENT,
    biography          TEXT                  DEFAULT ''
                           COMMENT 'Free-text biography; UTF-8 so any script is supported',
    instrument_key     VARCHAR(20)  NOT NULL DEFAULT 'piano'
                           COMMENT 'Canonical instrument key; join INSTRUMENT for display names',
    years_experience   INT          NOT NULL DEFAULT 0,
    hourly_rate        INT          NOT NULL DEFAULT 0,
    location           VARCHAR(100)          DEFAULT '',
    created_at         DATE         NOT NULL DEFAULT (CURRENT_DATE),
    updated_at         DATE         NOT NULL DEFAULT (CURRENT_DATE),
    user_id            INT          NOT NULL,
    PRIMARY KEY (teacher_profile_id),
    FOREIGN KEY (user_id)          REFERENCES USERS(user_id)      ON DELETE CASCADE,
    FOREIGN KEY (instrument_key)   REFERENCES INSTRUMENT(instrument_key)
) ENGINE=InnoDB
  DEFAULT CHARACTER SET utf8mb4
  COLLATE utf8mb4_unicode_ci;

-- ==============================
-- LEARNER PROFILE
-- ==============================
CREATE TABLE LEARNERPROFILE (
    learner_profile_id INT         NOT NULL AUTO_INCREMENT,
    instrument_key     VARCHAR(20)          DEFAULT 'piano'
                           COMMENT 'Preferred instrument; canonical key matching INSTRUMENT table',
    created_at         DATE        NOT NULL DEFAULT (CURRENT_DATE),
    updated_at         DATE        NOT NULL DEFAULT (CURRENT_DATE),
    user_id            INT         NOT NULL,
    PRIMARY KEY (learner_profile_id),
    FOREIGN KEY (user_id)        REFERENCES USERS(user_id)    ON DELETE CASCADE,
    FOREIGN KEY (instrument_key) REFERENCES INSTRUMENT(instrument_key)
) ENGINE=InnoDB
  DEFAULT CHARACTER SET utf8mb4
  COLLATE utf8mb4_unicode_ci;

-- ==============================
-- TIME SLOT
-- ==============================
CREATE TABLE TIMESLOT (
    slot_id            INT         NOT NULL AUTO_INCREMENT,
    lesson_date        DATE        NOT NULL,
    start_time         VARCHAR(20) NOT NULL,
    end_time           VARCHAR(20) NOT NULL,
    slot_status        VARCHAR(20) NOT NULL DEFAULT 'AVAILABLE',
    created_at         DATE        NOT NULL DEFAULT (CURRENT_DATE),
    teacher_profile_id INT         NOT NULL,
    PRIMARY KEY (slot_id),
    FOREIGN KEY (teacher_profile_id) REFERENCES TEACHERPROFILE(teacher_profile_id) ON DELETE CASCADE
) ENGINE=InnoDB
  DEFAULT CHARACTER SET utf8mb4
  COLLATE utf8mb4_unicode_ci;

-- ==============================
-- BOOKING
-- ==============================
CREATE TABLE BOOKING (
    booking_id         INT         NOT NULL AUTO_INCREMENT,
    booking_date       DATE        NOT NULL DEFAULT (CURRENT_DATE),
    booking_status     VARCHAR(20) NOT NULL DEFAULT 'PENDING',
    notes              TEXT                 DEFAULT '',
    created_at         DATE        NOT NULL DEFAULT (CURRENT_DATE),
    updated_at         DATE        NOT NULL DEFAULT (CURRENT_DATE),
    learner_profile_id INT         NOT NULL,
    slot_id            INT         NOT NULL,
    PRIMARY KEY (booking_id),
    FOREIGN KEY (learner_profile_id) REFERENCES LEARNERPROFILE(learner_profile_id) ON DELETE CASCADE,
    FOREIGN KEY (slot_id)            REFERENCES TIMESLOT(slot_id)                  ON DELETE CASCADE
) ENGINE=InnoDB
  DEFAULT CHARACTER SET utf8mb4
  COLLATE utf8mb4_unicode_ci;

-- ==============================
-- INDEXES
-- ==============================
CREATE INDEX idx_users_username    ON USERS(username);
CREATE INDEX idx_users_email       ON USERS(email);
CREATE INDEX idx_users_type        ON USERS(user_type);

CREATE INDEX idx_teacherprofile_user       ON TEACHERPROFILE(user_id);
CREATE INDEX idx_teacherprofile_instrument ON TEACHERPROFILE(instrument_key);

CREATE INDEX idx_learnerprofile_user ON LEARNERPROFILE(user_id);

CREATE INDEX idx_timeslot_teacher ON TIMESLOT(teacher_profile_id);
CREATE INDEX idx_timeslot_date    ON TIMESLOT(lesson_date);

CREATE INDEX idx_booking_learner ON BOOKING(learner_profile_id);
CREATE INDEX idx_booking_slot    ON BOOKING(slot_id);
