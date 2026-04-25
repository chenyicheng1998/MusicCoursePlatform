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

Simulates 10 users simultaneously querying the users table (as login does).

1. Right-click **Thread Group** → Add → **Sampler** → **JDBC Request**
   - Variable Name: `dbConn`
   - Query Type: **Select Statement**
   - SQL Query:
     ```sql
     SELECT id, email, password_hash, role 
     FROM users 
     WHERE email = 'testuser@example.com';
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
     INSERT INTO bookings (learner_id, time_slot_id, status, created_at)
     VALUES (1, 1, 'confirmed', NOW());
     ```
   - Name: `Create Booking`

> **Important:** Insert a test user (id=1) and a test time slot (id=1) into your database before running. Clean up after testing.

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

| Metric | Target | Meaning |
|---|---|---|
| Average (ms) | < 500 ms | Average response time for DB query |
| Min (ms) | — | Fastest single query |
| Max (ms) | < 2000 ms | Slowest single query under load |
| Error % | < 5% | Percentage of failed requests |
| Throughput | — | Requests per second the DB handles |

---

## 9. Example Results to Record in Sprint 7 Report

Run the tests and fill in this table:

| Scenario | Threads | Loops | Avg (ms) | Min (ms) | Max (ms) | Error % | Throughput |
|---|---|---|---|---|---|---|---|
| Login Query | 10 | 3 | ___ | ___ | ___ | ___ | ___ |
| Create Booking | 10 | 3 | ___ | ___ | ___ | ___ | ___ |

**Screenshot:** After running, take a screenshot of the Summary Report and include it in the Sprint 7 review.

---

## 10. Save and Share

- Save the JMeter test plan as: `docs/sprint7/jmeter_test_plan.jmx`
- Save results CSV as: `docs/sprint7/jmeter_results.csv`
- Include screenshot in Sprint 7 report

---

## Seed Data for Testing

Run this SQL before your JMeter test to ensure test records exist:

```sql
-- Make sure a test user exists
INSERT IGNORE INTO users (id, email, password_hash, role, created_at)
VALUES (1, 'testuser@example.com', '$2a$10$examplehash', 'learner', NOW());

-- Make sure a test time slot exists
INSERT IGNORE INTO time_slots (id, teacher_id, start_time, end_time, is_available)
VALUES (1, 1, '2026-05-01 10:00:00', '2026-05-01 11:00:00', 1);
```

Clean up after testing:
```sql
DELETE FROM bookings WHERE learner_id = 1 AND time_slot_id = 1;
```
