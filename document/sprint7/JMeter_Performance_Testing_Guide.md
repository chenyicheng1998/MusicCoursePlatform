# JMeter Performance Testing Guide — Sprint 7

**Project:** Music Course Platform  
**Date:** 2026-04-25

> **Note:** JMeter tests the MariaDB database directly via JDBC, since the app has no HTTP/REST API.  
> We use **JMeter JDBC Sampler** to simulate multiple concurrent users running database queries.

---

## 1. Download and Install JMeter

1. Go to: https://jmeter.apache.org/download_jmeter.cgi
2. Download **apache-jmeter-5.6.3.zip** (Binaries)
3. Extract to a folder, e.g. `C:\jmeter\`
4. Launch: `C:\jmeter\bin\jmeter.bat`
5. JMeter GUI opens — you're ready.

---

## 2. Download MariaDB JDBC Driver for JMeter

JMeter needs the MariaDB JDBC driver to connect to the database.

1. Download `mariadb-java-client-3.1.4.jar` from Maven Central:  
   Search "mariadb-java-client 3.1.4" at https://mvnrepository.com
2. Copy the `.jar` file to: `C:\jmeter\lib\`
3. Restart JMeter.

---

## 3. Create a New Test Plan

In JMeter GUI:

1. Right-click **Test Plan** → Add → **Threads (Users)** → **Thread Group**
   - Number of Threads: **10**
   - Ramp-up Period: **5** seconds
   - Loop Count: **3**

2. Right-click **Thread Group** → Add → **Config Element** → **JDBC Connection Configuration**
   - Variable Name: `dbConn`
   - Database URL: `jdbc:mariadb://localhost:3306/music_course_platform`
   - JDBC Driver class: `org.mariadb.jdbc.Driver`
   - Username: `root` (or your MariaDB user)
   - Password: your MariaDB password

---

## 4. Test Scenario 1 — Concurrent Login Queries

Simulates 10 users simultaneously querying the USERS table (as login does).

1. Right-click **Thread Group** → Add → **Sampler** → **JDBC Request**
   - Variable Name: `dbConn`
   - Query Type: **Select Statement**
   - SQL Query:
     ```sql
     SELECT user_id, email, password_hash, user_type
     FROM USERS
     WHERE email = 'jmeter_test@example.com';
     ```
   - Name: `Login Query`

2. Right-click **Thread Group** → Add → **Listener** → **Summary Report**
3. Right-click **Thread Group** → Add → **Listener** → **View Results Tree**

---

## 5. Test Scenario 2 — Concurrent Booking Creation

Simulates 10 users simultaneously inserting booking records.

1. Add another **JDBC Request** sampler:
   - Query Type: **Update Statement**
   - SQL Query:
     ```sql
     INSERT INTO BOOKING (booking_date, booking_status, notes, created_at, updated_at, learner_profile_id, slot_id)
     VALUES (CURDATE(), 'PENDING', 'JMeter test', CURDATE(), CURDATE(),
             (SELECT learner_profile_id FROM LEARNERPROFILE
              WHERE user_id = (SELECT user_id FROM USERS WHERE email = 'jmeter_test@example.com')),
             (SELECT slot_id FROM TIMESLOT WHERE slot_status = 'AVAILABLE' LIMIT 1));
     ```
   - Name: `Create Booking`

> **Important:** Run the **Seed Data** SQL below first to create all required test records before running this scenario.

---

## 6. Add Timers and Assertions

- Right-click **Thread Group** → Add → **Timer** → **Constant Timer**  
  Milliseconds: `500` (300ms think time between requests)

- Right-click a JDBC Request → Add → **Assertion** → **Response Assertion**  
  Response Code: `200` (JMeter JDBC uses 200 for success)

---

## 7. Run the Test

1. Click the **Green Play button** (Run → Start)
2. Watch results in **Summary Report** listener
3. After test completes, click **Save** → save results as `jmeter_results.jtl`

---

## 8. Interpreting Results

| Metric       | Target    | Meaning                            |
| ------------ | --------- | ---------------------------------- |
| Average (ms) | < 500 ms  | Average response time for DB query |
| Min (ms)     | —         | Fastest single query               |
| Max (ms)     | < 2000 ms | Slowest single query under load    |
| Error %      | < 5%      | Percentage of failed requests      |
| Throughput   | —         | Requests per second the DB handles |

---

## 9. Example Results to Record in Sprint 7 Report

Run the tests and fill in this table:

| Scenario       | Threads | Loops | Avg (ms) | Min (ms) | Max (ms) | Error % | Throughput |
| -------------- | ------- | ----- | -------- | -------- | -------- | ------- | ---------- |
| Login Query    | 10      | 3     | \_\_\_   | \_\_\_   | \_\_\_   | \_\_\_  | \_\_\_     |
| Create Booking | 10      | 3     | \_\_\_   | \_\_\_   | \_\_\_   | \_\_\_  | \_\_\_     |

**Screenshot:** After running, take a screenshot of the Summary Report and include it in the Sprint 7 review.

---

## 10. Save and Share

- Save the JMeter test plan as: `document/sprint7/jmeter_test_plan.jmx`
- Save results CSV as: `document/sprint7/jmeter_results.csv`
- Include screenshot in Sprint 7 report

---

## Seed Data for Testing

**必须先跑这段 SQL，才能运行 JMeter 测试。** 它会创建一条完整的测试用数据链（user → learner profile → teacher → time slot）。

```sql
USE music_course_platform;

-- 1. 创建测试用 teacher 账号
INSERT IGNORE INTO USERS (username, password_hash, email, user_type, created_at)
VALUES ('jmeter_teacher', '$2a$10$dummyhashfortesting000000000000000000000000000000000000',
        'jmeter_teacher@example.com', 'teacher', CURDATE());

-- 2. 为 teacher 创建 TEACHERPROFILE
INSERT IGNORE INTO TEACHERPROFILE (biography, instrument_key, years_experience, hourly_rate, location, created_at, updated_at, user_id)
VALUES ('JMeter test teacher', 'piano', 3, 30, 'Helsinki', CURDATE(), CURDATE(),
        (SELECT user_id FROM USERS WHERE email = 'jmeter_teacher@example.com'));

-- 3. 创建一个 AVAILABLE 的 time slot（关联 teacher profile）
INSERT IGNORE INTO TIMESLOT (lesson_date, start_time, end_time, slot_status, created_at, teacher_profile_id)
VALUES ('2026-05-10', '10:00', '11:00', 'AVAILABLE', CURDATE(),
        (SELECT teacher_profile_id FROM TEACHERPROFILE
         WHERE user_id = (SELECT user_id FROM USERS WHERE email = 'jmeter_teacher@example.com')));

-- 4. 创建测试用 learner 账号
INSERT IGNORE INTO USERS (username, password_hash, email, user_type, created_at)
VALUES ('jmeter_learner', '$2a$10$dummyhashfortesting000000000000000000000000000000000000',
        'jmeter_test@example.com', 'learner', CURDATE());

-- 5. 为 learner 创建 LEARNERPROFILE
INSERT IGNORE INTO LEARNERPROFILE (instrument_key, created_at, updated_at, user_id)
VALUES ('piano', CURDATE(), CURDATE(),
        (SELECT user_id FROM USERS WHERE email = 'jmeter_test@example.com'));
```

**验证 seed data 是否成功：**

```sql
SELECT u.email, lp.learner_profile_id
FROM USERS u JOIN LEARNERPROFILE lp ON u.user_id = lp.user_id
WHERE u.email = 'jmeter_test@example.com';

SELECT slot_id, lesson_date, slot_status FROM TIMESLOT WHERE slot_status = 'AVAILABLE' LIMIT 5;
```

两条查询都有结果就可以跑 JMeter 了。

**测试完毕后清理数据：**

```sql
DELETE FROM BOOKING
WHERE learner_profile_id = (
    SELECT learner_profile_id FROM LEARNERPROFILE
    WHERE user_id = (SELECT user_id FROM USERS WHERE email = 'jmeter_test@example.com')
);
DELETE FROM LEARNERPROFILE WHERE user_id = (SELECT user_id FROM USERS WHERE email = 'jmeter_test@example.com');
DELETE FROM TIMESLOT WHERE teacher_profile_id = (
    SELECT teacher_profile_id FROM TEACHERPROFILE
    WHERE user_id = (SELECT user_id FROM USERS WHERE email = 'jmeter_teacher@example.com')
);
DELETE FROM TEACHERPROFILE WHERE user_id = (SELECT user_id FROM USERS WHERE email = 'jmeter_teacher@example.com');
DELETE FROM USERS WHERE email IN ('jmeter_test@example.com', 'jmeter_teacher@example.com');
```
