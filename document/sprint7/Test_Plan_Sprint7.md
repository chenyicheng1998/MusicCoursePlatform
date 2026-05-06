# Test Plan — Sprint 7

**Project:** Music Course Platform  
**Version:** 2.0  
**Date:** 2026-05-03  
**Prepared by:** Lu Liu, Su Wai Phyoe, Yicheng Chen, Ying Luo

---

## 1. Objective

The purpose of this test plan is to define the testing strategy, scope, resources, and schedule for Sprint 7 of the Music Course Platform project. The goal is to ensure that all implemented features are functionally correct, meet quality standards, and satisfy user expectations before final delivery.

Testing covers:

- Unit and integration testing (automated, JUnit 5 + Mockito)
- Static code analysis (SonarQube, SonarLint, Checkstyle)
- Usability evaluation (Nielsen's Heuristic Evaluation)
- User Acceptance Testing (UAT)
- Performance testing (Apache JMeter)

---

## 2. Scope

### 2.1 Features Under Test

| Module             | Feature                                                    |
| ------------------ | ---------------------------------------------------------- |
| Authentication     | User login (learner & teacher), signup with validation     |
| Session Management | Session state, role-based access control                   |
| Teacher Dashboard  | Profile management, time slot creation, booking management |
| Student Dashboard  | View available teachers, filter by instrument              |
| Booking System     | Book a lesson, view bookings                               |
| Localization       | Language switching (English / Chinese / Arabic)            |
| Database Layer     | CRUD operations via DAO classes (MariaDB)                  |

These modules were selected because they represent the actions users perform most frequently in the system. From a learner's perspective, this includes registering, logging in, browsing teachers, and booking lessons. From a teacher's perspective, this includes managing their profile, creating time slots, and viewing bookings. Any failure in these areas would directly prevent users from completing their core goals.

---

### 2.2 Out of Scope

- Payment processing (not implemented)
- Email notifications (not implemented)
- Mobile or web versions of the application

---

## 3. Test Environment

| Item             | Detail                                                    |
| ---------------- | --------------------------------------------------------- |
| OS               | Windows 11 / macOS (developer machines)                   |
| Java Version     | OpenJDK 17                                                |
| JavaFX Version   | 21.0.2                                                    |
| Database         | MariaDB — `music_course_platform`                         |
| Build Tool       | Apache Maven 3.x                                          |
| Test Framework   | JUnit 5.9.3 + Mockito 5.3.1                               |
| Coverage Tool    | JaCoCo 0.8.12                                             |
| Static Analysis  | SonarQube (localhost:9000), SonarLint (IntelliJ plugin)   |
| CI/CD            | Jenkins (local)                                           |
| Performance Tool | Apache JMeter 5.x                                         |
| Containerization | Docker (eclipse-temurin:21-jdk base)                      |
| IDE              | IntelliJ IDEA with Checkstyle-IDEA plugin (Google Checks) |

This environment was configured to closely reflect the conditions under which end users will run the application. Using the same OS, Java version, and database setup across all developer machines reduces the risk of environment-specific bugs that would not appear during testing but would affect real users.

---

## 4. Resources

| Role               | Team Member  | Responsibility                                                          |
| ------------------ | ------------ | ----------------------------------------------------------------------- |
| Developer / Tester | Yicheng Chen | Unit tests (DAO, Service layer), Jenkins CI, SonarQube ,JMeter          |
| Developer / Tester | Lu Liu       | Unit tests (Controller layer), Heuristic Evaluation                     |
| Developer / Tester | Su Wai Phyoe | Unit tests (Model, Util), UAT execution,SonarQube, Heuristic Evaluation |
| Developer / Tester | Ying Luo     | Unit tests (Controller), Bug tracking                                   |

> **Note:** All four team members participated in the Heuristic Evaluation independently. The Responsibility column above lists primary leads; heuristic evaluation was conducted by the full team.

Responsibilities were assigned based on each member's familiarity with the corresponding part of the codebase. This ensures that the person most aware of the implementation details is also responsible for verifying that the feature works correctly from the user's point of view.

---

## 5. Test Types and Tasks

### 5.1 Unit Testing (Automated)

**Tool:** JUnit 5 + Mockito  
**Trigger:** `mvn test`  
**Coverage:** JaCoCo — target: >50% line coverage  
**Status:** COMPLETE — 383 tests pass

| Test Class                         | Layer      | Tests   |
| ---------------------------------- | ---------- | ------- |
| `UserDAOTest`                      | DAO        | 16      |
| `BookingDAOTest`                   | DAO        | 9       |
| `TimeSlotDAOTest`                  | DAO        | 9       |
| `LearnerProfileDAOTest`            | DAO        | 6       |
| `TeacherProfileDAOTest`            | DAO        | 8       |
| `UserServiceTest`                  | Service    | 23      |
| `BookingServiceTest`               | Service    | 18      |
| `TeacherServiceTest`               | Service    | 15      |
| `TimeSlotServiceTest`              | Service    | 25      |
| `ModelTest`                        | Model      | 25      |
| `PasswordUtilTest`                 | Util       | 22      |
| `LocalizationManagerTest`          | Util       | 36      |
| `NavigationHelperTest`             | Util       | 4       |
| `CalendarBuilderTest`              | Util       | 11      |
| `DatabaseConnectionTest`           | Util       | 8       |
| `SessionManagerTest`               | Controller | 14      |
| `LoginControllerTest`              | Controller | 12      |
| `SignupControllerTest`             | Controller | 15      |
| `StudentDashboardControllerTest`   | Controller | 35      |
| `TeacherDashboardControllerTest`   | Controller | 33      |
| `BookingViewControllerTest`        | Controller | 20      |
| `TeacherProfileViewControllerTest` | Controller | 19      |
| **Total**                          |            | **383** |

The test classes were designed to cover every layer that a user request passes through — from the UI controller down to the database. This layered approach ensures that if a user action fails, the source of the problem can be identified precisely, whether it lies in the business logic, data access, or input validation.

**Pass/Fail Criteria:** All 383 tests must pass with 0 failures. Build is considered failing if any test fails.

---

### 5.2 Static Code Analysis

**Tool:** SonarQube (Maven plugin `sonar:sonar`)  
**Trigger:** Jenkins CI pipeline stage or `mvn clean verify sonar:sonar`

| Metric                        | Target       | Actual (Sprint 7) |
| ----------------------------- | ------------ | ----------------- |
| Reliability (Bugs)            | Grade A      | A                 |
| Security (Vulnerabilities)    | Grade A      | A                 |
| Maintainability (Code Smells) | Grade A or B | A                 |
| Coverage                      | > 50%        | **87.9%**         |
| Duplications                  | < 10%        | **4.5%**          |
| Security Hotspots             | 0            | **0**             |

These targets were chosen to ensure the codebase is safe and maintainable for users over time. A Grade A in Reliability means users will not encounter application bugs during normal use. A Grade A in Security means user data is not exposed to vulnerabilities. The coverage target ensures that the automated tests meaningfully exercise the paths a real user would trigger.

**Additional tools:** SonarLint (IntelliJ — real-time), Checkstyle (Google Checks profile)

---

### 5.3 Usability Testing — Nielsen's Heuristic Evaluation

**Method:** Nielsen's Heuristic Evaluation — all 4 evaluators independently assessed the UI against Nielsen's 10 Usability Heuristics, then aggregated findings.  
**Severity Scale:** 0 = not a problem, 1 = cosmetic, 2 = minor, 3 = major, 4 = catastrophe

**Phases followed:**

1. Pre-evaluation training
2. Evaluation
3. Severity rating
4. Debriefing

**Output:** Individual reports (all 4 team members) + summary + debriefing notes

---

### 5.4 User Acceptance Testing (UAT)

**Test Cases:** TC01 – TC10 (see `Testing.md` and `UserAcceptanceTest.xlsx` for full case details)  
**Planned Execution Date:** 2026-04-18  
**Executor:** All 4 team members

| TC ID | Test Scenario                                  | Result |
| ----- | ---------------------------------------------- | ------ |
| TC01  | User can register a new account                | PASS   |
| TC02  | User can login with valid credentials          | PASS   |
| TC03  | Login fails with invalid credentials           | PASS   |
| TC04  | User can logout successfully                   | PASS   |
| TC05  | Teacher can create and edit profile            | PASS   |
| TC06  | Teacher can create, edit and delete time slots | PASS   |
| TC07  | Student can view and book an available lesson  | PASS   |
| TC08  | Student cannot book an already reserved slot   | PASS   |
| TC09  | Student can cancel a booked lesson             | PASS   |
| TC10  | System validates invalid inputs in forms       | PASS   |

These ten scenarios were derived directly from the primary workflows a learner or teacher would follow in the system. They were chosen to reflect realistic usage rather than edge cases, ensuring that the most common user journeys — registration, login, booking, and profile management — function correctly end-to-end.

---

### 5.5 Performance Testing (JMeter)

**Tool:** Apache JMeter 5.6.3  
**Target:** MariaDB — `music_course_platform` (localhost)  
**Setup guide:** See `JMeter_Performance_Testing_Guide.md` for test setup and configuration.

**Test configuration:**

| Parameter                  | Value                 |
| -------------------------- | --------------------- |
| Threads (concurrent users) | 10                    |
| Ramp-up period             | 5 seconds             |
| Loop count                 | 3                     |
| Total samples per scenario | 30                    |
| Think time                 | 500 ms constant delay |

**Scenarios:**

- 10 concurrent users performing login (SELECT on USERS table)
- 10 concurrent users creating bookings (INSERT into BOOKING table)

**Acceptance criteria:**

- Average response time < 500 ms under 10 concurrent users
- Error rate < 5%

The scenarios were designed around the two most frequent database operations a user triggers: logging in and creating a booking. Ten concurrent users were chosen as a realistic estimate of simultaneous usage for a course platform of this scale. The 500 ms response time threshold was set based on general usability research, which indicates that users perceive responses beyond this threshold as slow.

---

## 6. Bug Tracking

All defects discovered during testing are tracked in the Bug Tracking Table (see `Bug_Tracking_Table.md`).

**Severity classification:**

- Critical — system crash or data loss
- High — major feature not working
- Medium — incorrect behavior
- Low — minor or visual issue

---

## 7. Entry and Exit Criteria

### Entry Criteria

- Source code compiled successfully with `mvn compile`
- Database `music_course_platform` is running with seed data
- All developers have access to the repository

### Exit Criteria

- All 383 unit tests pass with 0 failures
- SonarQube shows grade A in Reliability and Security
- All 10 UAT test cases pass
- All Critical and High severity bugs resolved
- Heuristic Evaluation reports completed by all 4 team members

---

## 8. Test Schedule

| Activity                          | Date       | Owner        |
| --------------------------------- | ---------- | ------------ |
| Unit tests executed (final run)   | 2026-04-20 | All          |
| SonarQube scan via Jenkins        | 2026-04-20 | All          |
| UAT execution                     | 2026-04-18 | Su Wai Phyoe |
| Heuristic Evaluation (individual) | 2026-04-27 | All          |
| JMeter performance tests          | 2026-04-23 | Yicheng Chen |
| Bug tracking review               | 2026-04-24 | All          |
| Sprint 7 report submitted         | 2026-04-25 | All          |

The schedule was planned so that unit and integration tests run first, establishing a stable baseline before user-facing evaluations begin. UAT and Heuristic Evaluation are placed later so that testers assess a version of the system that has already passed automated checks, reducing the chance of encountering low-level defects during user-oriented testing.
