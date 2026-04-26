# Bug Tracking Table — Sprint 7

**Project:** Music Course Platform  
**Sprint:** 7  
**Date:** 2026-04-25  
**Prepared by:** Lu Liu, Su Wai Phyoe, Yicheng Chen, Ying Luo

---

## Bug Summary

| Total Bugs | Critical | High | Medium | Low | Fixed | Open |
| ---------- | -------- | ---- | ------ | --- | ----- | ---- |
| 10         | 0        | 1    | 4      | 5   | 9     | 1    |

---

## Bug Tracking Table

| Bug ID  | Title                                                                | Description                                                                                                                                                                                   | Severity | Found By       | Found In Sprint | Status             | Fixed By         | Fix Description                                                                                                                                                                                                                                         |
| ------- | -------------------------------------------------------------------- | --------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------- | -------- | -------------- | --------------- | ------------------ | ---------------- | ------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------- |
| BUG-001 | UnnecessaryStubbing in TeacherDashboardControllerTest                | `@Mock BookingDAO` was declared and stubbed but never used by the controller under test; Mockito strict mode threw `UnnecessaryStubbing` exception causing test failure                       | High     | Yicheng Chen   | Sprint 7        | Fixed              | Yicheng Chen     | Removed `@Mock BookingDAO` field and its `setField` call from test class; injected `LocalizationManager` correctly                                                                                                                                      |
| BUG-002 | Test assertions using hardcoded English strings instead of i18n keys | 7 assertions in `TeacherDashboardControllerTest` compared against raw English strings; test failed when locale changed                                                                        | Medium   | Yicheng Chen   | Sprint 7        | Fixed              | Yicheng Chen     | Updated assertions to use `LocalizationManager.getString("key")` to fetch actual localized message                                                                                                                                                      |
| BUG-003 | SonarQube Quality Gate fails on coverage                             | JaCoCo line coverage was ~56.7% which is above the project goal (50%) but below SonarQube default gate of 80%                                                                                 | Medium   | Yicheng Chen   | Sprint 6–7      | Partially fixed    | Yicheng Chen     | Added `LocalizationManagerTest` (35 tests) and expanded `TeacherProfileViewControllerTest` (+10 tests); coverage improved from 54.5% → **61.8%**. Still below 80% SonarQube default gate; custom gate configured to accept current level                |
| BUG-004 | SonarQube reports code duplication                                   | Similar validation logic repeated in `LoginController` and `SignupController`; duplicated try-catch blocks in multiple DAO classes                                                            | Medium   | SonarQube scan | Sprint 6        | Fixed              | Yicheng Chen     | Created `DaoHelper` utility class extracting all DAO boilerplate; created `BaseController` base class eliminating ~70 lines of duplicated controller logic; removed duplicate getter methods in model classes. Duplication reduced from ~11% → **7.2%** |
| BUG-010 | SonarQube reports 5 Security Hotspots                                | `e.printStackTrace()` and `System.out.println()` calls in `Main.java`, `LoginController.java`, `SignupController.java` flagged as "debug features active in production"                       | Low      | SonarQube scan | Sprint 7        | Fixed              | Yicheng Chen     | Replaced all `e.printStackTrace()` and `System.out.println()` calls with `java.util.logging.Logger` calls. Security Hotspots reduced from 5 → **0**                                                                                                     |
| BUG-005 | ER Diagram does not match actual database schema                     | Original ER diagram showed `TEACHES` and `LOCALIZEDCONTENT` tables that were never implemented; `InstrumentDAO` and `LocalizationService` classes shown in UML do not exist                   | Medium   | Lu Liu         | Sprint 5        | Fixed              | Lu Liu, Ying Luo | Redrawn ER diagram and UML class diagram to reflect actual implemented tables and classes                                                                                                                                                               |
| BUG-006 | Language does not persist after application restart                  | When user selects Chinese or Arabic, the language preference is stored in `LocalizationManager` session but not written to disk; next launch always starts in English                         | Low      | Su Wai Phyoe   | Sprint 6        | Open               | —                | Identified as out-of-scope for current sprint; persistence of locale preference to be added in future sprint                                                                                                                                            |
| BUG-007 | No visual confirmation after booking is created                      | After a learner successfully books a time slot, the booking form clears but no success dialog or status message is shown                                                                      | Low      | Ying Luo       | Sprint 6        | Fixed              | Ying Luo         | Added a success alert dialog in `BookingViewController` after successful `bookingService.createBooking()` call                                                                                                                                          |
| BUG-008 | Teacher profile view shows blank fields if teacher has no profile    | If a teacher logs in before creating their profile, `TeacherProfileViewController` loads and attempts to populate fields from null `TeacherProfile`; results in empty (not null-safe) display | Low      | Su Wai Phyoe   | Sprint 7        | Fixed              | Su Wai Phyoe     | Added null check in `TeacherProfileViewController.initialize()` to handle missing teacher profile gracefully                                                                                                                                            |
| BUG-009 | Docker container fails to render UI on Windows without Xming         | Running the Docker image on Windows without starting Xming (X11 server) causes `java.lang.UnsatisfiedLinkError` or blank window                                                               | Low      | Yicheng Chen   | Sprint 6        | Fixed (documented) | Yicheng Chen     | Added detailed Docker usage instructions in README.md; requires `DISPLAY` environment variable and Xming on Windows, XQuartz on macOS                                                                                                                   |

---

## Bug Detail

### BUG-001 — UnnecessaryStubbing in TeacherDashboardControllerTest

**Steps to Reproduce:**

1. Run `mvn test`
2. Observe `org.mockito.exceptions.misusing.UnnecessaryStubbing` in `TeacherDashboardControllerTest`

**Root Cause:** `BookingDAO` mock was stubbed for methods not called by `TeacherDashboardController`. Mockito strict mode (JUnit 5 extension) treats this as a test error.

**Resolution:** Removed `@Mock BookingDAO bookingDAO` field and its corresponding `setField()` injection call. The test class now only mocks what the controller under test actually uses.

---

### BUG-002 — Hardcoded assertion strings not using i18n

**Steps to Reproduce:**

1. Change system locale or run tests with non-English locale
2. Assertions like `assertEquals("Please fill in all fields!", ...)` fail

**Resolution:** Changed all 7 failing assertions to use:

```java
assertEquals(LocalizationManager.getInstance().getString("error.fill_all_fields"), actualMessage);
```

---

### BUG-003 — SonarQube coverage gate (partially fixed)

**Context:** SonarQube default Quality Gate requires 80% line coverage on new code. Coverage was ~56.7% at the start of Sprint 7.

**Actions taken in Sprint 7:** Created `LocalizationManagerTest.java` with 35 new test cases covering all untested methods in `LocalizationManager`. Expanded `TeacherProfileViewControllerTest` with 10 additional tests covering `loadTeacherInfo()`, `handleDeleteSlot()`, `setupDateFormatter()`, and `updateTexts()`. Coverage improved from 54.5% → **61.8%** (228 tests total, 0 failures).

**Remaining gap:** Coverage remains below 80% SonarQube default gate. JavaFX UI controllers require a running JavaFX runtime, making full coverage impractical without TestFX integration test harness. A custom SonarQube Quality Gate has been configured.

---

### BUG-006 — Language preference not persisted (deferred)

**Workaround:** User must manually re-select language each session from the login screen language selector.

**Planned fix:** Store selected locale key in a `~/.musiccourse/preferences.properties` file on application exit.

---

## Testing Tools Used for Bug Discovery

| Tool                           | Bugs Found                |
| ------------------------------ | ------------------------- |
| JUnit 5 / Mockito (unit tests) | BUG-001, BUG-002          |
| SonarQube static analysis      | BUG-003, BUG-004, BUG-010 |
| Manual code review             | BUG-005                   |
| Manual UAT execution           | BUG-006, BUG-007, BUG-008 |
| Docker deployment testing      | BUG-009                   |

---

### BUG-010 — SonarQube Security Hotspots: debug output in production code

**Steps to Reproduce:**

1. Run SonarQube scan: `mvn clean verify sonar:sonar`
2. Navigate to Security Hotspots in SonarQube dashboard
3. Observe 5 hotspots flagged as "Make sure this debug feature is deactivated before delivering the code in production"

**Root Cause:** `e.printStackTrace()` writes stack traces to `stderr` which can expose internal class names, file paths, and line numbers in production environments. `System.out.println()` is unstructured debug output that bypasses the logging framework.

**Resolution:** All occurrences replaced with `java.util.logging.Logger`:

- `Main.java`: Logger.severe() for startup errors
- `LoginController.java` (×2): Logger.warning() for navigation failures
- `SignupController.java` (×2): Logger.warning() for navigation failures

**Result:** Security Hotspots count: 5 → **0**.
