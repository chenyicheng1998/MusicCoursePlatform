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

These features represent the main interactions available to users in the system. Testing them ensures that both learners and teachers can complete key tasks without errors.

---

### 2.2 Out of Scope

- Payment processing (not implemented)
- Email notifications (not implemented)
- Mobile or web versions of the application

These features are not part of the current implementation, so they are excluded from testing. This keeps the focus on validating the completed functionality.

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

This setup defines the environment in which all tests are executed. Keeping it consistent helps ensure that results are reliable and repeatable.

---

## 4. Resources

| Role               | Team Member  | Responsibility                                              |
| ------------------ | ------------ | ----------------------------------------------------------- |
| Developer / Tester | Yicheng Chen | Unit tests (DAO, Service layer), Jenkins CI, SonarQube      |
| Developer / Tester | Lu Liu       | Unit tests (Controller layer), Heuristic Evaluation         |
| Developer / Tester | Su Wai Phyoe | Unit tests (Model, Util), UAT execution, JMeter             |
| Developer / Tester | Ying Luo     | Unit tests (Controller), Bug tracking, Heuristic Evaluation |

Responsibilities are distributed across the team to cover all testing activities. This ensures that each part of the system is reviewed and validated.

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

These tests verify that individual components behave as expected. Passing all tests confirms that recent code changes did not introduce regressions.

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

These results reflect the quality of the code after cleanup. The current metrics indicate that the system is stable and maintainable.

**Additional tools:** SonarLint (IntelliJ — real-time), Checkstyle (Google Checks profile)

---

### 5.3 Usability Testing — Nielsen's Heuristic Evaluation

**Method:** Nielsen's Heuristic Evaluation — 4 evaluators independently assess the UI against Nielsen's 10 Usability Heuristics, then aggregate findings.  
**Severity Scale:** 0 = not a problem, 1 = cosmetic, 2 = minor, 3 = major, 4 = catastrophe

**Phases followed:**
1. Pre-evaluation training
2. Evaluation
3. Severity rating
4. Debriefing

This evaluation focuses on how users interact with the interface. The findings help identify usability issues that may affect the user experience.

**Output:** Individual reports + summary + debriefing notes

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

These tests simulate real user actions in the system. All passing results indicate that the system behaves correctly in practical usage.

---

### 5.5 Performance Testing (JMeter)

**Tool:** Apache JMeter 5.6.x  
**Target:** Database-facing operations

**Scenarios:**
- 10 concurrent users performing login
- 10 concurrent users creating bookings

**Accept criteria:**
- Average response time < 500 ms
- Error rate < 5%

These tests evaluate how the system performs under concurrent usage. The results confirm that the system remains responsive under normal load conditions.

---

## 6. Bug Tracking

All defects discovered during testing are tracked in the Bug Tracking Table (see Bug_Tracking_Table.md).

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

These criteria define when testing starts and when it is considered complete. They ensure that testing is carried out in a controlled and consistent way.

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

This schedule summarizes when each testing activity was completed. It ensures that all tasks were finished before the final delivery.