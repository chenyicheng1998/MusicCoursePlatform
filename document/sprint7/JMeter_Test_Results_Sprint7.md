# JMeter Performance Test Results — Sprint 7

**Project:** Music Course Platform  
**Date:** 2026-04-27  
**Tool:** Apache JMeter 5.6.3  
**Test Plan:** `tests/performance/musiccourse.jmx`  
**Results File:** `result.jtl`  
**Executed by:** Su Wai Phyoe

---

## Test Configuration

| Parameter                  | Value                                                        |
| -------------------------- | ------------------------------------------------------------ |
| Target                     | MariaDB (`music_course_platform`, localhost)                 |
| Connection                 | JDBC — `jdbc:mariadb://localhost:3306/music_course_platform` |
| Driver                     | `org.mariadb.jdbc.Driver`                                    |
| Number of Threads          | 10                                                           |
| Ramp-up Period             | 5 seconds                                                    |
| Loop Count                 | 3                                                            |
| Total Samples per Scenario | 30 (10 threads × 3 loops)                                    |
| Think Time (Timer)         | 500 ms constant delay between requests                       |

---

## Results Summary

| Scenario       | Samples | Avg (ms) | Min (ms) | Max (ms) | Error % | Throughput (req/s) |
| -------------- | ------- | -------- | -------- | -------- | ------- | ------------------ |
| Login Query    | 30      | 2.5      | 0        | 10       | 0%      | ~4.6               |
| Create Booking | 30      | 2.0      | 1        | 3        | 0%      | ~4.6               |

**Total samples:** 60  
**Total errors:** 0  
**Overall error rate:** 0%

---

## Acceptance Criteria Evaluation

| Criterion                              | Target   | Actual | Result  |
| -------------------------------------- | -------- | ------ | ------- |
| Average response time — Login Query    | < 500 ms | 2.5 ms | ✅ PASS |
| Average response time — Create Booking | < 500 ms | 2.0 ms | ✅ PASS |
| Error rate                             | < 5%     | 0%     | ✅ PASS |

Both scenarios pass acceptance criteria with a wide margin. Response times are in the range of 0–10 ms because the database is running locally on the same machine as the test client.

---

## Scenario Details

### Scenario 1 — Concurrent Login Queries

Simulates 10 concurrent users querying the `USERS` table, as the login flow does.

```sql
SELECT user_id, email, password_hash, user_type
FROM USERS
WHERE email = 'jmeter_test@example.com';
```

| Metric     | Value  |
| ---------- | ------ |
| Samples    | 30     |
| Average    | 2.5 ms |
| Minimum    | 0 ms   |
| Maximum    | 10 ms  |
| Error %    | 0%     |
| Throughput | ~4.6/s |

---

### Scenario 2 — Concurrent Booking Creation

Simulates 10 concurrent users inserting a booking record into the `BOOKING` table.

```sql
INSERT INTO BOOKING (booking_date, booking_status, notes, created_at, updated_at, learner_profile_id, slot_id)
VALUES (CURDATE(), 'PENDING', 'JMeter test', CURDATE(), CURDATE(),
        (SELECT learner_profile_id FROM LEARNERPROFILE
         WHERE user_id = (SELECT user_id FROM USERS WHERE email = 'jmeter_test@example.com')),
        (SELECT slot_id FROM TIMESLOT WHERE slot_status = 'AVAILABLE' LIMIT 1));
```

| Metric     | Value  |
| ---------- | ------ |
| Samples    | 30     |
| Average    | 2.0 ms |
| Minimum    | 1 ms   |
| Maximum    | 3 ms   |
| Error %    | 0%     |
| Throughput | ~4.6/s |

---

## Screenshots

JMeter GUI screenshots taken during the test run are stored in `document/images/`:

- `JMeter-Screenshot1.png` — Summary Report listener showing both scenarios
- `JMeter-Screenshot2.png` — View Results Tree showing individual sample responses

---

## Observations

- All 60 samples completed successfully with no errors.
- Response times are well below the 500 ms threshold because both the application and database run on the same local machine (localhost). In a production environment with network latency, response times would be higher but still expected to be well under 500 ms for simple indexed queries.
- The `Create Booking` INSERT uses a subquery to resolve `learner_profile_id` and `slot_id`, but this adds negligible overhead (max 3 ms observed) because the referenced tables are small and indexed.
- The `AVAILABLE` time slot pool may be exhausted after repeated test runs since each iteration inserts a booking against the same slot. The seed data SQL in `JMeter_Performance_Testing_Guide.md` should be re-run and cleanup SQL executed between test runs.
