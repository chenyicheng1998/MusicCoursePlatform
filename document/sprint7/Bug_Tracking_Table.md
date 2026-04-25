# Bug Tracking Table — Sprint 7
**Project:** Music Course Platform  
**Sprint:** 7  
**Date:** 2026-04-25  
**Prepared by:** Lu Liu, Su Wai Phyoe, Yicheng Chen, Ying Luo  

---

## Bug Summary

| Total Bugs | Critical | High | Medium | Low | Fixed | Open |
|---|---|---|---|---|---|---|
| 9 | 0 | 1 | 4 | 4 | 8 | 1 |

---

## Bug Tracking Table

| Bug ID | Title | Description | Severity | Found By | Found In Sprint | Status | Fixed By | Fix Description |
|---|---|---|---|---|---|---|---|---|
| BUG-001 | UnnecessaryStubbing in TeacherDashboardControllerTest | `@Mock BookingDAO` was declared and stubbed but never used by the controller under test; Mockito strict mode threw `UnnecessaryStubbing` exception causing test failure | High | Yicheng Chen | Sprint 7 | Fixed | Yicheng Chen | Removed `@Mock BookingDAO` field and its `setField` call from test class; injected `LocalizationManager` correctly |
| BUG-002 | Test assertions using hardcoded English strings instead of i18n keys | 7 assertions in `TeacherDashboardControllerTest` compared against raw English strings; test failed when locale changed | Medium | Yicheng Chen | Sprint 7 | Fixed | Yicheng Chen | Updated assertions to use `LocalizationManager.getString("key")` to fetch actual localized message |
| BUG-003 | SonarQube Quality Gate fails on coverage | JaCoCo line coverage is ~56.7% which is above the project goal (50%) but below SonarQube default gate of 80% | Medium | Yicheng Chen | Sprint 6–7 | Open (accepted) | — | Coverage is acceptable for a student JavaFX desktop project; JavaFX UI classes are excluded from meaningful unit testing |
| BUG-004 | SonarQube reports code duplication at 17% | Similar validation logic repeated in `LoginController` and `SignupController`; duplicated try-catch blocks in multiple DAO classes | Medium | SonarQube scan | Sprint 6 | Partially fixed | Yicheng Chen | Refactored common validation logic into `UserService`; DAO duplication accepted as standard boilerplate pattern |
| BUG-005 | ER Diagram does not match actual database schema | Original ER diagram showed `TEACHES` and `LOCALIZEDCONTENT` tables that were never implemented; `InstrumentDAO` and `LocalizationService` classes shown in UML do not exist | Medium | Lu Liu | Sprint 5 | Fixed | Lu Liu, Ying Luo | Redrawn ER diagram and UML class diagram to reflect actual implemented tables and classes |
| BUG-006 | Language does not persist after application restart | When user selects Chinese or Arabic, the language preference is stored in `LocalizationManager` session but not written to disk; next launch always starts in English | Low | Su Wai Phyoe | Sprint 6 | Open | — | Identified as out-of-scope for current sprint; persistence of locale preference to be added in future sprint |
| BUG-007 | No visual confirmation after booking is created | After a learner successfully books a time slot, the booking form clears but no success dialog or status message is shown | Low | Ying Luo | Sprint 6 | Fixed | Ying Luo | Added a success alert dialog in `BookingViewController` after successful `bookingService.createBooking()` call |
| BUG-008 | Teacher profile view shows blank fields if teacher has no profile | If a teacher logs in before creating their profile, `TeacherProfileViewController` loads and attempts to populate fields from null `TeacherProfile`; results in empty (not null-safe) display | Low | Su Wai Phyoe | Sprint 7 | Fixed | Su Wai Phyoe | Added null check in `TeacherProfileViewController.initialize()` to handle missing teacher profile gracefully |
| BUG-009 | Docker container fails to render UI on Windows without Xming | Running the Docker image on Windows without starting Xming (X11 server) causes `java.lang.UnsatisfiedLinkError` or blank window | Low | Yicheng Chen | Sprint 6 | Fixed (documented) | Yicheng Chen | Added detailed Docker usage instructions in README.md; requires `DISPLAY` environment variable and Xming on Windows, XQuartz on macOS |

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

### BUG-003 — SonarQube coverage gate (accepted)
**Context:** SonarQube default Quality Gate requires 80% line coverage on new code. The project currently measures ~56.7%.

**Justification for acceptance:** JavaFX UI controllers require a running JavaFX runtime to instantiate, making full coverage impractical without an integration test harness. Core business logic (Service and DAO layers) is well-covered. The 56.7% figure represents all testable non-UI code being covered.

---

### BUG-006 — Language preference not persisted (deferred)
**Workaround:** User must manually re-select language each session from the login screen language selector.

**Planned fix:** Store selected locale key in a `~/.musiccourse/preferences.properties` file on application exit.

---

## Testing Tools Used for Bug Discovery

| Tool | Bugs Found |
|---|---|
| JUnit 5 / Mockito (unit tests) | BUG-001, BUG-002 |
| SonarQube static analysis | BUG-003, BUG-004 |
| Manual code review | BUG-005 |
| Manual UAT execution | BUG-006, BUG-007, BUG-008 |
| Docker deployment testing | BUG-009 |
