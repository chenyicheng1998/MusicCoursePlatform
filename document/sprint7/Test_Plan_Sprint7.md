# Test Plan — Sprint 7

**Project:** Music Course Platform  
**Version:** 1.0  
**Date:** 2026-04-25  
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

---

## 4. Resources

| Role               | Team Member  | Responsibility                                              |
| ------------------ | ------------ | ----------------------------------------------------------- |
| Developer / Tester | Yicheng Chen | Unit tests (DAO, Service layer), Jenkins CI, SonarQube      |
| Developer / Tester | Lu Liu       | Unit tests (Controller layer), Heuristic Evaluation         |
| Developer / Tester | Su Wai Phyoe | Unit tests (Model, Util), UAT execution, JMeter             |
| Developer / Tester | Ying Luo     | Unit tests (Controller), Bug tracking, Heuristic Evaluation |

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
| `TimeSlotDAOTest`                  | DAO        | 10      |
| `LearnerProfileDAOTest`            | DAO        | 6       |
| `TeacherProfileDAOTest`            | DAO        | 8       |
| `UserServiceTest`                  | Service    | 23      |
| `BookingServiceTest`               | Service    | 18      |
| `TeacherServiceTest`               | Service    | 15      |
| `TimeSlotServiceTest`              | Service    | 17      |
| `ModelTest`                        | Model      | 20      |
| `PasswordUtilTest`                 | Util       | 22      |
| `LocalizationManagerTest`          | Util       | 36      |
| `NavigationHelperTest`             | Util       | 4       |
| `SessionManagerTest`               | Controller | 14      |
| `LoginControllerTest`              | Controller | 7       |
| `SignupControllerTest`             | Controller | 9       |
| `StudentDashboardControllerTest`   | Controller | 11      |
| `TeacherDashboardControllerTest`   | Controller | 16      |
| `BookingViewControllerTest`        | Controller | 14      |
| `TeacherProfileViewControllerTest` | Controller | 14      |
| **Total**                          |            | **383** |

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
| Duplications                  | < 10%        | **4.7%**          |
| Security Hotspots             | 0            | **0**             |

**Additional tools:** SonarLint (IntelliJ — real-time), Checkstyle (Google Checks profile)

---

### 5.3 Usability Testing — Nielsen's Heuristic Evaluation

**Method:** Nielsen's Heuristic Evaluation — 4 evaluators independently assess the UI against Nielsen's 10 Usability Heuristics, then aggregate findings.  
**Severity Scale:** 0 = not a problem, 1 = cosmetic, 2 = minor, 3 = major, 4 = catastrophe  
**Phases followed:**
1. **Pre-evaluation training** — each evaluator was assigned a user role (learner / teacher) and given scenario context before evaluating
2. **Evaluation** — 4 team members independently examined the UI and recorded issues
3. **Severity rating** — each evaluator independently assigned severity scores (0–4)
4. **Debriefing** — team aggregated findings, discussed root causes, and agreed on priority fixes

**Output:** Individual evaluation reports + consolidated summary table + debriefing notes (see Heuristic_Evaluation_Sprint7.md)

---

### 5.4 User Acceptance Testing (UAT)

**Test Cases:** TC01 – TC10 (defined in Sprint 6)  
**Execution Date:** 2026-04-18  
**Status:** All 10 test cases PASSED

| TC ID | Test Scenario                             | Result |
| ----- | ----------------------------------------- | ------ |
| TC01  | Learner registration with valid data      | PASS   |
| TC02  | Learner registration with duplicate email | PASS   |
| TC03  | Teacher login with correct credentials    | PASS   |
| TC04  | Learner login with wrong password         | PASS   |
| TC05  | Teacher creates a time slot               | PASS   |
| TC06  | Learner books an available time slot      | PASS   |
| TC07  | Learner views their booking history       | PASS   |
| TC08  | Teacher views bookings on dashboard       | PASS   |
| TC09  | Language switching (EN → ZH → AR)         | PASS   |
| TC10  | Learner views teacher profile page        | PASS   |

---

### 5.5 Performance Testing (JMeter)

**Tool:** Apache JMeter 5.6.x  
**Target:** Database-facing operations (login, booking creation)  
**Scenarios:**

- 10 concurrent virtual users performing login
- 10 concurrent virtual users creating bookings

**Accept criteria:**

- Average response time < 500 ms under 10 concurrent users
- Error rate < 5%

_See JMeter_Performance_Testing_Guide.md for setup and results._

---

## 6. Bug Tracking

All defects discovered during testing are tracked in the Bug Tracking Table (see Bug_Tracking_Table.md).

**Severity classification:**

- **Critical** — app crash or data loss
- **High** — major feature broken
- **Medium** — feature works but with incorrect behavior
- **Low** — cosmetic or minor UX issue

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
| SonarQube scan via Jenkins        | 2026-04-20 | Yicheng Chen |
| UAT execution                     | 2026-04-18 | Su Wai Phyoe |
| Heuristic Evaluation (individual) | 2026-04-22 | All          |
| JMeter performance tests          | 2026-04-23 | Su Wai Phyoe |
| Bug tracking review               | 2026-04-24 | All          |
| Sprint 7 report submitted         | 2026-04-25 | All          |
