# Music Course Platform - System Design

## Database Design

### 1. **Entities**

#### INSTRUMENT
**Description**: Stores canonical instrument keys and their translations for all supported languages (English, Chinese, Arabic). This ensures consistent data storage while allowing multi-language display.

**Key Attributes**:
- `instrument_key` (Primary Key, VARCHAR, lowercase canonical key)
- `name_en` (VARCHAR, English display name)
- `name_zh` (VARCHAR, Chinese display name)
- `name_ar` (VARCHAR, Arabic display name)

---

#### USERS
**Description**: Represents all registered users in the system, including both teachers and learners. Stores login information and user type.

**Key Attributes**:
- `user_id` (Primary Key, INT, AUTO_INCREMENT)
- `username` (VARCHAR(50), UNIQUE, NOT NULL)
- `password_hash` (VARCHAR(255), NOT NULL)
- `email` (VARCHAR(100), UNIQUE, NOT NULL)
- `user_type` (ENUM('TEACHER', 'LEARNER'), NOT NULL)
- `created_at` (TIMESTAMP, DEFAULT CURRENT_TIMESTAMP)

---

#### TEACHERPROFILE
**Description**: Stores detailed information about teachers, including teaching skills, instrument (via canonical key), experience, hourly rate, and location. Each teacher corresponds to exactly one USERS account.

**Key Attributes**:
- `teacher_profile_id` (Primary Key, INT, AUTO_INCREMENT)
- `user_id` (Foreign Key, INT, UNIQUE, NOT NULL)
- `biography` (TEXT)
- `instrument_key` (VARCHAR(20), NOT NULL, FK → INSTRUMENT)
- `years_experience` (INT, DEFAULT 0)
- `hourly_rate` (INT, DEFAULT 0)
- `location` (VARCHAR(100))
- `created_at` (DATE)
- `updated_at` (DATE)

---

#### LEARNERPROFILE
**Description**: Stores detailed information about learners, including their preferred instrument using the canonical key.

**Key Attributes**:
- `learner_profile_id` (Primary Key, INT, AUTO_INCREMENT)
- `user_id` (Foreign Key, INT, UNIQUE, NOT NULL)
- `instrument_key` (VARCHAR(20), FK → INSTRUMENT)
- `created_at` (DATE)
- `updated_at` (DATE)

---

#### TIMESLOT
**Description**: Represents the time slots published by teachers for lessons. Each time slot is linked to a specific teacher and can be booked by learners.

**Key Attributes**:
- `slot_id` (Primary Key, INT, AUTO_INCREMENT)
- `teacher_profile_id` (Foreign Key, INT, NOT NULL)
- `lesson_date` (DATE, NOT NULL)
- `start_time` (TIME, NOT NULL)
- `end_time` (TIME, NOT NULL)
- `slot_status` (ENUM('AVAILABLE', 'BOOKED'), DEFAULT 'AVAILABLE')
- `created_at` (DATE)

---

#### BOOKING
**Description**: Represents lesson bookings by learners for specific time slots. Linked to both the learner and the selected time slot.

**Key Attributes**:
- `booking_id` (Primary Key, INT, AUTO_INCREMENT)
- `slot_id` (Foreign Key, INT, UNIQUE, NOT NULL)
- `learner_profile_id` (Foreign Key, INT, NOT NULL)
- `booking_date` (DATE)
- `booking_status` (ENUM('PENDING', 'CONFIRMED', 'CANCELLED'), DEFAULT 'PENDING')
- `notes` (TEXT)
- `created_at` (DATE)
- `updated_at` (DATE)

---

### 2. **Relationships**

#### USERS → TEACHERPROFILE
- **Type**: 1:1
- **Description**: Each teacher user has exactly one teacher profile.
- **Implementation**: `teacher_profile.user_id` references `users.user_id` with UNIQUE constraint

#### USERS → LEARNERPROFILE
- **Type**: 1:1
- **Description**: Each learner user has exactly one learner profile.
- **Implementation**: `learner_profile.user_id` references `users.user_id` with UNIQUE constraint

#### TEACHERPROFILE → TIMESLOT
- **Type**: 1:N
- **Description**: One teacher can create multiple lesson time slots.
- **Implementation**: `timeslot.teacher_profile_id` references `teacher_profile.teacher_profile_id`

#### LEARNERPROFILE → BOOKING
- **Type**: 1:N
- **Description**: One learner can book multiple lessons.
- **Implementation**: `booking.learner_profile_id` references `learner_profile.learner_profile_id`

#### TIMESLOT → BOOKING
- **Type**: 1:0..1
- **Description**: A time slot can have at most one booking. This prevents double-booking.
- **Implementation**: `booking.slot_id` is UNIQUE

#### INSTRUMENT → PROFILE
- **Type**: 1:N
- **Description**: One instrument can be used by many teachers and learners.

---

### 3. **Database Schema**

```sql
CREATE DATABASE IF NOT EXISTS music_course_platform;
USE music_course_platform;

CREATE TABLE instrument (
    instrument_key VARCHAR(20) PRIMARY KEY,
    name_en VARCHAR(50),
    name_zh VARCHAR(50),
    name_ar VARCHAR(50)
);

CREATE TABLE users (
    user_id INT AUTO_INCREMENT PRIMARY KEY,
    username VARCHAR(50) UNIQUE NOT NULL,
    password_hash VARCHAR(255) NOT NULL,
    email VARCHAR(100) UNIQUE NOT NULL,
    user_type ENUM('TEACHER', 'LEARNER') NOT NULL,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP
);

CREATE TABLE teacher_profile (
    teacher_profile_id INT AUTO_INCREMENT PRIMARY KEY,
    user_id INT UNIQUE NOT NULL,
    biography TEXT,
    instrument_key VARCHAR(20) NOT NULL,
    years_experience INT DEFAULT 0,
    hourly_rate INT DEFAULT 0,
    location VARCHAR(100),
    created_at DATE,
    updated_at DATE,
    FOREIGN KEY (user_id) REFERENCES users(user_id),
    FOREIGN KEY (instrument_key) REFERENCES instrument(instrument_key)
);

CREATE TABLE learner_profile (
    learner_profile_id INT AUTO_INCREMENT PRIMARY KEY,
    user_id INT UNIQUE NOT NULL,
    instrument_key VARCHAR(20),
    created_at DATE,
    updated_at DATE,
    FOREIGN KEY (user_id) REFERENCES users(user_id),
    FOREIGN KEY (instrument_key) REFERENCES instrument(instrument_key)
);

CREATE TABLE timeslot (
    slot_id INT AUTO_INCREMENT PRIMARY KEY,
    teacher_profile_id INT NOT NULL,
    lesson_date DATE NOT NULL,
    start_time TIME NOT NULL,
    end_time TIME NOT NULL,
    slot_status ENUM('AVAILABLE','BOOKED') DEFAULT 'AVAILABLE',
    created_at DATE,
    FOREIGN KEY (teacher_profile_id) REFERENCES teacher_profile(teacher_profile_id)
);

CREATE TABLE booking (
    booking_id INT AUTO_INCREMENT PRIMARY KEY,
    slot_id INT UNIQUE NOT NULL,
    learner_profile_id INT NOT NULL,
    booking_date DATE,
    booking_status ENUM('PENDING','CONFIRMED','CANCELLED') DEFAULT 'PENDING',
    notes TEXT,
    created_at DATE,
    updated_at DATE,
    FOREIGN KEY (slot_id) REFERENCES timeslot(slot_id),
    FOREIGN KEY (learner_profile_id) REFERENCES learner_profile(learner_profile_id)
);