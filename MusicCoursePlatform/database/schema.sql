-- ============================================
-- Music Course Platform Database Schema
-- Sprint 2: Complete Database Foundation
-- ============================================

-- 创建数据库
CREATE DATABASE IF NOT EXISTS music_course_platform;
USE music_course_platform;

-- ============================================
-- 1. 用户表 (users)
-- 存储所有用户的基本信息（教师和学习者）
-- ============================================
CREATE TABLE IF NOT EXISTS users (
    user_id INT AUTO_INCREMENT PRIMARY KEY,
    username VARCHAR(50) NOT NULL UNIQUE,
    password_hash VARCHAR(255) NOT NULL,
    email VARCHAR(100) NOT NULL UNIQUE,
    user_type ENUM('TEACHER', 'LEARNER') NOT NULL,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    
    INDEX idx_username (username),
    INDEX idx_email (email),
    INDEX idx_user_type (user_type)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;

-- ============================================
-- 2. 教师资料表 (teacher_profiles)
-- 存储教师的详细资料信息
-- ============================================
CREATE TABLE IF NOT EXISTS teacher_profiles (
    profile_id INT AUTO_INCREMENT PRIMARY KEY,
    user_id INT NOT NULL UNIQUE,
    biography TEXT,
    instruments_taught VARCHAR(255) COMMENT '教授的乐器，逗号分隔',
    years_experience INT DEFAULT 0,
    hourly_rate DECIMAL(10,2) COMMENT '每小时费率',
    location VARCHAR(100),
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    
    FOREIGN KEY (user_id) REFERENCES users(user_id) ON DELETE CASCADE,
    INDEX idx_instruments (instruments_taught),
    INDEX idx_location (location)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;

-- ============================================
-- 3. 时间段表 (time_slots)
-- 存储教师可预约的时间段
-- ============================================
CREATE TABLE IF NOT EXISTS time_slots (
    slot_id INT AUTO_INCREMENT PRIMARY KEY,
    teacher_id INT NOT NULL,
    lesson_date DATE NOT NULL,
    start_time TIME NOT NULL,
    end_time TIME NOT NULL,
    status ENUM('AVAILABLE', 'BOOKED') DEFAULT 'AVAILABLE',
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    
    FOREIGN KEY (teacher_id) REFERENCES users(user_id) ON DELETE CASCADE,
    INDEX idx_teacher_date (teacher_id, lesson_date),
    INDEX idx_status (status),
    INDEX idx_lesson_date (lesson_date)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;

-- ============================================
-- 4. 预约表 (bookings)
-- 存储学习者的课程预约记录
-- ============================================
CREATE TABLE IF NOT EXISTS bookings (
    booking_id INT AUTO_INCREMENT PRIMARY KEY,
    slot_id INT NOT NULL,
    learner_id INT NOT NULL,
    booking_date TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    status ENUM('PENDING', 'CONFIRMED', 'CANCELLED') DEFAULT 'PENDING',
    notes TEXT COMMENT '预约备注',
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    
    FOREIGN KEY (slot_id) REFERENCES time_slots(slot_id) ON DELETE CASCADE,
    FOREIGN KEY (learner_id) REFERENCES users(user_id) ON DELETE CASCADE,
    INDEX idx_learner (learner_id),
    INDEX idx_status (status),
    INDEX idx_booking_date (booking_date)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;

-- ============================================
-- 测试数据 (Sample Data)
-- ============================================

-- 插入测试用户
INSERT INTO users (username, password_hash, email, user_type) VALUES
('teacher_john', 'hashed_password_123', 'john@example.com', 'TEACHER'),
('teacher_mary', 'hashed_password_456', 'mary@example.com', 'TEACHER'),
('learner_tom', 'hashed_password_789', 'tom@example.com', 'LEARNER'),
('learner_lucy', 'hashed_password_abc', 'lucy@example.com', 'LEARNER');

-- 插入教师资料
INSERT INTO teacher_profiles (user_id, biography, instruments_taught, years_experience, hourly_rate, location) VALUES
(1, 'Experienced piano teacher with 10 years of teaching experience.', 'Piano,Keyboard', 10, 50.00, 'Helsinki'),
(2, 'Professional violin instructor, specializing in classical music.', 'Violin,Viola', 8, 45.00, 'Espoo');

-- 插入可用时间段
INSERT INTO time_slots (teacher_id, lesson_date, start_time, end_time, status) VALUES
(1, '2026-02-01', '09:00:00', '10:00:00', 'AVAILABLE'),
(1, '2026-02-01', '10:00:00', '11:00:00', 'AVAILABLE'),
(1, '2026-02-02', '14:00:00', '15:00:00', 'BOOKED'),
(2, '2026-02-01', '13:00:00', '14:00:00', 'AVAILABLE'),
(2, '2026-02-03', '10:00:00', '11:00:00', 'AVAILABLE');

-- 插入预约记录
INSERT INTO bookings (slot_id, learner_id, status, notes) VALUES
(3, 3, 'CONFIRMED', 'First piano lesson');

