# Music Course Platform - System Design

## Database Design

### 1. **Entities**

#### INSTRUMENT
**Description**: Stores canonical instrument keys and their translations for all supported languages (English, Chinese, Arabic). This ensures consistent data storage while allowing multi-language display.

**Key Attributes**:
- `instrument_key` (Primary Key, VARCHAR(20), lowercase canonical key)
- `name_en` (VARCHAR(50), NOT NULL — English display name)
- `name_zh` (VARCHAR(50), NOT NULL — Chinese display name)
- `name_ar` (VARCHAR(50), NOT NULL — Arabic display name)

---

#### USERS
**Description**: Represents all registered users in the system, including both teachers and learners. Stores login information and user type.

**Key Attributes**:
- `user_id` (Primary Key, INT, AUTO_INCREMENT)
- `username` (VARCHAR(50), UNIQUE, NOT NULL)
- `password_hash` (VARCHAR(255), NOT NULL)
- `email` (VARCHAR(100), UNIQUE, NOT NULL)
- `user_type` (VARCHAR(20), NOT NULL — expected values: `'TEACHER'`, `'LEARNER'`)
- `created_at` (DATE, NOT NULL, DEFAULT CURRENT_DATE)

---

#### TEACHERPROFILE
**Description**: Stores detailed information about teachers, including teaching skills, instrument (via canonical key), experience, hourly rate, and location. Each teacher profile is linked to one USERS account.

**Key Attributes**:
- `teacher_profile_id` (Primary Key, INT, AUTO_INCREMENT)
- `user_id` (Foreign Key → USERS, INT, NOT NULL — no unique constraint; multiple profiles per user are technically possible at the DB level)
- `biography` (TEXT, DEFAULT `''`)
- `instrument_key` (VARCHAR(20), NOT NULL, DEFAULT `'piano'`, FK → INSTRUMENT)
- `years_experience` (INT, NOT NULL, DEFAULT 0)
- `hourly_rate` (INT, NOT NULL, DEFAULT 0)
- `location` (VARCHAR(100), DEFAULT `''`)
- `created_at` (DATE, NOT NULL, DEFAULT CURRENT_DATE)
- `updated_at` (DATE, NOT NULL, DEFAULT CURRENT_DATE)

---

#### LEARNERPROFILE
**Description**: Stores detailed information about learners, including their preferred instrument using the canonical key.

**Key Attributes**:
- `learner_profile_id` (Primary Key, INT, AUTO_INCREMENT)
- `user_id` (Foreign Key → USERS, INT, NOT NULL)
- `instrument_key` (VARCHAR(20), DEFAULT `'piano'`, FK → INSTRUMENT)
- `created_at` (DATE, NOT NULL, DEFAULT CURRENT_DATE)
- `updated_at` (DATE, NOT NULL, DEFAULT CURRENT_DATE)

---

#### TIMESLOT
**Description**: Represents the time slots published by teachers for lessons. Each time slot is linked to a specific teacher and can be booked by learners.

**Key Attributes**:
- `slot_id` (Primary Key, INT, AUTO_INCREMENT)
- `teacher_profile_id` (Foreign Key → TEACHERPROFILE, INT, NOT NULL)
- `lesson_date` (DATE, NOT NULL)
- `start_time` (VARCHAR(20), NOT NULL — stored as a string, e.g. `'09:00'`)
- `end_time` (VARCHAR(20), NOT NULL — stored as a string, e.g. `'10:00'`)
- `slot_status` (VARCHAR(20), NOT NULL, DEFAULT `'AVAILABLE'` — expected values: `'AVAILABLE'`, `'BOOKED'`)
- `created_at` (DATE, NOT NULL, DEFAULT CURRENT_DATE)

---

#### BOOKING
**Description**: Represents lesson bookings by learners for specific time slots. Linked to both the learner and the selected time slot.

**Key Attributes**:
- `booking_id` (Primary Key, INT, AUTO_INCREMENT)
- `slot_id` (Foreign Key → TIMESLOT, INT, NOT NULL — no unique constraint at the DB level; double-booking prevention must be enforced at the application layer)
- `learner_profile_id` (Foreign Key → LEARNERPROFILE, INT, NOT NULL)
- `booking_date` (DATE, NOT NULL, DEFAULT CURRENT_DATE)
- `booking_status` (VARCHAR(20), NOT NULL, DEFAULT `'PENDING'` — expected values: `'PENDING'`, `'CONFIRMED'`, `'CANCELLED'`)
- `notes` (TEXT, DEFAULT `''`)
- `created_at` (DATE, NOT NULL, DEFAULT CURRENT_DATE)
- `updated_at` (DATE, NOT NULL, DEFAULT CURRENT_DATE)

---

### 2. **Relationships**

#### USERS → TEACHERPROFILE
- **Type**: 1:N (no DB-level unique constraint on `user_id`)
- **Description**: Each teacher user is linked to a teacher profile via `user_id`.
- **Implementation**: `TEACHERPROFILE.user_id` references `USERS.user_id` with `ON DELETE CASCADE`

#### USERS → LEARNERPROFILE
- **Type**: 1:N
- **Description**: Each learner user is linked to a learner profile via `user_id`.
- **Implementation**: `LEARNERPROFILE.user_id` references `USERS.user_id` with `ON DELETE CASCADE`

#### TEACHERPROFILE → TIMESLOT
- **Type**: 1:N
- **Description**: One teacher can create multiple lesson time slots.
- **Implementation**: `TIMESLOT.teacher_profile_id` references `TEACHERPROFILE.teacher_profile_id` with `ON DELETE CASCADE`

#### LEARNERPROFILE → BOOKING
- **Type**: 1:N
- **Description**: One learner can make multiple bookings.
- **Implementation**: `BOOKING.learner_profile_id` references `LEARNERPROFILE.learner_profile_id` with `ON DELETE CASCADE`

#### TIMESLOT → BOOKING
- **Type**: 1:N (no DB-level unique constraint on `slot_id` in BOOKING)
- **Description**: A time slot can be associated with multiple booking records. Prevention of double-booking is enforced at the application layer, not the database.
- **Implementation**: `BOOKING.slot_id` references `TIMESLOT.slot_id` with `ON DELETE CASCADE`

#### INSTRUMENT → TEACHERPROFILE / LEARNERPROFILE
- **Type**: 1:N
- **Description**: One instrument can be referenced by many teacher and learner profiles.
- **Implementation**: FK on `instrument_key` in both TEACHERPROFILE and LEARNERPROFILE

---

### 3. **Indexes**

The following indexes are defined to support efficient lookups:

| Index Name | Table | Column(s) |
|---|---|---|
| `idx_users_username` | USERS | `username` |
| `idx_users_email` | USERS | `email` |
| `idx_users_type` | USERS | `user_type` |
| `idx_teacherprofile_user` | TEACHERPROFILE | `user_id` |
| `idx_teacherprofile_instrument` | TEACHERPROFILE | `instrument_key` |
| `idx_learnerprofile_user` | LEARNERPROFILE | `user_id` |
| `idx_timeslot_teacher` | TIMESLOT | `teacher_profile_id` |
| `idx_timeslot_date` | TIMESLOT | `lesson_date` |
| `idx_booking_learner` | BOOKING | `learner_profile_id` |
| `idx_booking_slot` | BOOKING | `slot_id` |

---

### 4. **Database Schema**

```sql
CREATE DATABASE IF NOT EXISTS music_course_platform
    CHARACTER SET utf8mb4
    COLLATE utf8mb4_unicode_ci;

USE music_course_platform;

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

CREATE TABLE INSTRUMENT (
    instrument_key VARCHAR(20)  NOT NULL,
    name_en        VARCHAR(50)  NOT NULL,
    name_zh        VARCHAR(50)  NOT NULL,
    name_ar        VARCHAR(50)  NOT NULL,
    PRIMARY KEY (instrument_key)
) ENGINE=InnoDB DEFAULT CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci;

INSERT INTO INSTRUMENT (instrument_key, name_en, name_zh, name_ar) VALUES
    ('piano',     'Piano',     '钢琴',    'بيانو'),
    ('guitar',    'Guitar',    '吉他',    'جيتار'),
    ('violin',    'Violin',    '小提琴',  'كمان'),
    ('drums',     'Drums',     '鼓',      'طبول'),
    ('flute',     'Flute',     '长笛',    'ناي'),
    ('saxophone', 'Saxophone', '萨克斯管','ساكسوفون'),
    ('cello',     'Cello',     '大提琴',  'شيللو'),
    ('voice',     'Voice',     '声乐',    'غناء');

CREATE TABLE USERS (
    user_id       INT          NOT NULL AUTO_INCREMENT,
    username      VARCHAR(50)  NOT NULL UNIQUE,
    password_hash VARCHAR(255) NOT NULL,
    email         VARCHAR(100) NOT NULL UNIQUE,
    user_type     VARCHAR(20)  NOT NULL,
    created_at    DATE         NOT NULL DEFAULT (CURRENT_DATE),
    PRIMARY KEY (user_id)
) ENGINE=InnoDB DEFAULT CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci;

CREATE TABLE TEACHERPROFILE (
    teacher_profile_id INT          NOT NULL AUTO_INCREMENT,
    biography          TEXT                  DEFAULT '',
    instrument_key     VARCHAR(20)  NOT NULL DEFAULT 'piano',
    years_experience   INT          NOT NULL DEFAULT 0,
    hourly_rate        INT          NOT NULL DEFAULT 0,
    location           VARCHAR(100)          DEFAULT '',
    created_at         DATE         NOT NULL DEFAULT (CURRENT_DATE),
    updated_at         DATE         NOT NULL DEFAULT (CURRENT_DATE),
    user_id            INT          NOT NULL,
    PRIMARY KEY (teacher_profile_id),
    FOREIGN KEY (user_id)        REFERENCES USERS(user_id)              ON DELETE CASCADE,
    FOREIGN KEY (instrument_key) REFERENCES INSTRUMENT(instrument_key)
) ENGINE=InnoDB DEFAULT CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci;

CREATE TABLE LEARNERPROFILE (
    learner_profile_id INT         NOT NULL AUTO_INCREMENT,
    instrument_key     VARCHAR(20)          DEFAULT 'piano',
    created_at         DATE        NOT NULL DEFAULT (CURRENT_DATE),
    updated_at         DATE        NOT NULL DEFAULT (CURRENT_DATE),
    user_id            INT         NOT NULL,
    PRIMARY KEY (learner_profile_id),
    FOREIGN KEY (user_id)        REFERENCES USERS(user_id)              ON DELETE CASCADE,
    FOREIGN KEY (instrument_key) REFERENCES INSTRUMENT(instrument_key)
) ENGINE=InnoDB DEFAULT CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci;

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
) ENGINE=InnoDB DEFAULT CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci;

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
) ENGINE=InnoDB DEFAULT CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci;

CREATE INDEX idx_users_username          ON USERS(username);
CREATE INDEX idx_users_email             ON USERS(email);
CREATE INDEX idx_users_type              ON USERS(user_type);
CREATE INDEX idx_teacherprofile_user     ON TEACHERPROFILE(user_id);
CREATE INDEX idx_teacherprofile_instrument ON TEACHERPROFILE(instrument_key);
CREATE INDEX idx_learnerprofile_user     ON LEARNERPROFILE(user_id);
CREATE INDEX idx_timeslot_teacher        ON TIMESLOT(teacher_profile_id);
CREATE INDEX idx_timeslot_date           ON TIMESLOT(lesson_date);
CREATE INDEX idx_booking_learner         ON BOOKING(learner_profile_id);
CREATE INDEX idx_booking_slot            ON BOOKING(slot_id);
```