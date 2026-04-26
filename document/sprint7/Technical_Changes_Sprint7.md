# Document Technical Changes — Sprint 7

**Project:** Music Course Platform  
**Sprint:** 7  
**Date:** 2026-04-25  
**Prepared by:** Lu Liu, Su Wai Phyoe, Yicheng Chen, Ying Luo

---

## 1. Overview

This document records all technical changes made during Sprint 7 to the Music Course Platform codebase, infrastructure, and development tooling. Changes include test fixes, code quality improvements, CI/CD enhancements, and documentation updates.

---

## 2. Code Changes

### 2.1 Test Fixes — TeacherDashboardControllerTest

**File:** `src/test/java/controller/TeacherDashboardControllerTest.java`  
**Changed by:** Yicheng Chen  
**Sprint:** 7

**Problem:** `UnnecessaryStubbing` exception thrown by Mockito strict mode because `BookingDAO` was declared as a mock but never actually called by `TeacherDashboardController`.

**Changes made:**

- Removed `@Mock BookingDAO bookingDAO` field declaration
- Removed `setField(controller, "bookingDAO", bookingDAO)` injection call
- Added `setField(controller, "localizationManager", LocalizationManager.getInstance())` to inject real localization instance
- Updated 7 test assertions from hardcoded English strings to use `LocalizationManager.getInstance().getString(key)` for i18n-safe comparisons

**Impact:** All 228 unit tests now pass with 0 failures.

---

### 2.2 Null Safety Fix — TeacherProfileViewController

**File:** `src/main/java/controller/TeacherProfileViewController.java`  
**Changed by:** Su Wai Phyoe  
**Sprint:** 7

**Problem:** When a teacher account exists in the database but no `TeacherProfile` record has been created, calling `initialize()` would attempt to call methods on a null object, causing `NullPointerException` and blank/broken display.

**Changes made:**

- Added null check in `initialize()` method: if `teacherProfile == null`, display informational message prompting the teacher to create their profile
- Prevented NPE by guarding all field-population code behind the null check

---

### 2.3 Booking Success Feedback — BookingViewController

**File:** `src/main/java/controller/BookingViewController.java`  
**Changed by:** Ying Luo  
**Sprint:** 7

**Problem:** After a learner successfully books a time slot, the UI cleared the form but gave no confirmation to the user. This violated Nielsen Heuristic H1 (Visibility of system status).

**Changes made:**

- Added `Alert` dialog of type `INFORMATION` after successful `bookingService.createBooking()` call
- Alert message is fetched from `LocalizationManager` to support all three languages
- Alert title and content both localized

---

### 2.4 Code Quality Improvements (SonarLint + Checkstyle)

**Files:** Multiple files across `src/main/java/`  
**Changed by:** All team members  
**Sprint:** 7

Improvements made based on SonarLint real-time feedback and Checkstyle Google Checks profile in IntelliJ IDEA:

| Issue Type                         | Examples Fixed                                            | Tool That Detected |
| ---------------------------------- | --------------------------------------------------------- | ------------------ |
| Unused imports                     | Removed stale `import` statements in 6 files              | SonarLint          |
| Missing `final` on local variables | Added `final` keyword to appropriate local variables      | Checkstyle         |
| Magic number literals              | Extracted constants for repeated integer values           | SonarLint          |
| Raw type usage                     | Replaced `List` with `List<T>` in DAO classes             | Checkstyle         |
| Empty `catch` blocks               | Added logging or rethrow in previously empty catch blocks | SonarLint          |
| Redundant method calls             | Simplified chained method calls                           | SonarLint          |

---

### 2.5 DAO Refactoring — DaoHelper Utility Class

**File:** `src/main/java/util/DaoHelper.java` (new), all 5 DAO classes  
**Changed by:** Yicheng Chen  
**Sprint:** 7

**Problem:** All 5 DAO classes (`UserDAO`, `BookingDAO`, `TimeSlotDAO`, `TeacherProfileDAO`, `LearnerProfileDAO`) repeated the same try-with-resources pattern, `ResultSet` mapping boilerplate, and `PreparedStatement` setup. This caused ~11% code duplication flagged by SonarQube.

**Changes made:**

- Created `DaoHelper.java` with 4 static utility methods:
  - `querySingle()` — fetch a single row
  - `queryList()` — fetch multiple rows
  - `executeUpdate()` — execute UPDATE/DELETE
  - `executeInsert()` — execute INSERT and capture generated key
- Added functional interfaces `StatementPreparer` and `ResultSetMapper<T>` to pass behavior as lambdas
- Extracted `normalizeInstrumentKey()` as a shared static helper (was duplicated in `TeacherProfileDAO` and `LearnerProfileDAO`)
- Added `BASE_QUERY` constants to all DAO classes, replacing all `SELECT *` queries with explicit column lists
- Refactored all 5 DAO classes to use DaoHelper methods

**Impact:** Code duplication reduced from ~11% to 7.2%.

---

### 2.6 Controller Refactoring — BaseController

**File:** `src/main/java/controller/BaseController.java` (new), `LoginController.java`, `SignupController.java`  
**Changed by:** Yicheng Chen  
**Sprint:** 7

**Problem:** `LoginController` and `SignupController` contained ~70 lines of identical code: `setupLanguageSelector()`, `handleLanguageChange()`, `applyDirection()`, `navigateToDashboard()`, `showError()`, `hideError()`, and all shared `@FXML` field declarations.

**Changes made:**

- Created abstract `BaseController` class containing all shared fields and methods
- `LoginController` and `SignupController` now extend `BaseController`
- Moved shared `@FXML` fields (`rootPane`, `languageCombo`, `languageLabel`, `backButton`, `titleLabel`, `errorLabel`) to `BaseController` as `protected`
- Moved shared methods to `BaseController`; `updateTexts()` declared abstract and implemented in each subclass
- Updated test helpers in `LoginControllerTest` and `SignupControllerTest` to use hierarchy-aware reflection (searches superclass fields)

**Impact:** Removed ~70 lines of duplicate code; duplication percentage further reduced.

---

### 2.7 Security Hotspot Fixes — Replace Debug Output with Logger

**Files:** `src/main/java/com/musiccourse/Main.java`, `src/main/java/controller/LoginController.java`, `src/main/java/controller/SignupController.java`  
**Changed by:** Yicheng Chen  
**Sprint:** 7

**Problem:** SonarQube reported 5 Security Hotspots (rule: "Make sure this debug feature is deactivated before delivering the code in production") caused by `e.printStackTrace()` and `System.out.println()` calls in production code.

**Changes made:**

| File                         | Old Code                                     | New Code                                                         |
| ---------------------------- | -------------------------------------------- | ---------------------------------------------------------------- |
| `Main.java`                  | `System.out.println("Application started!")` | Removed                                                          |
| `Main.java`                  | `e.printStackTrace()`                        | `logger.severe("Error starting application: " + e.getMessage())` |
| `LoginController.java` (×2)  | `e.printStackTrace()`                        | `LOGGER.warning("Failed to load ...: " + e.getMessage())`        |
| `SignupController.java` (×2) | `e.printStackTrace()`                        | `LOGGER.warning("Failed to load ...: " + e.getMessage())`        |

All three files now use `java.util.logging.Logger` for proper production logging.

**Impact:** Security Hotspots reduced from 5 to **0**.

---

### 2.8 Model Class Cleanup — Removed Duplicate Methods

**Files:** `src/main/java/model/TeacherProfile.java`, `model/Booking.java`, `model/TimeSlot.java`  
**Changed by:** Yicheng Chen  
**Sprint:** 7

**Problem:** Model classes contained duplicate getter/setter pairs with different names but identical implementations, contributing to SonarQube duplication count.

**Changes made:**

- `TeacherProfile.java`: removed duplicate `getProfileId()` / `setProfileId()` (identical to `getTeacherProfileId()` / `setTeacherProfileId()`)
- `Booking.java`: removed duplicate `getStatus()` / `setStatus()` (identical to `getBookingStatus()` / `setBookingStatus()`)
- `TimeSlot.java`: removed duplicate `getStatus()` / `setStatus()` (identical to `getSlotStatus()` / `setSlotStatus()`)

---

### 2.9 Service Layer — Improved Exception Types

**Files:** `src/main/java/service/BookingService.java`, `src/main/java/service/TeacherService.java`  
**Changed by:** Yicheng Chen  
**Sprint:** 7

**Problem:** Several `throw new RuntimeException(...)` calls in service classes used the generic base exception type. SonarQube flags generic `RuntimeException` as a code smell.

**Changes made:**

- `BookingService.java`: replaced 3 occurrences of `throw new RuntimeException(...)` with `throw new IllegalStateException(...)`
- `TeacherService.java`: replaced 2 occurrences of `throw new RuntimeException(...)` with `throw new IllegalStateException(...)`

---

### 2.10 Test Coverage Improvements — New and Expanded Tests

**Files:** `src/test/java/util/LocalizationManagerTest.java` (new), `src/test/java/controller/TeacherProfileViewControllerTest.java` (expanded)  
**Changed by:** Yicheng Chen  
**Sprint:** 7

**New test file — `LocalizationManagerTest.java`:**

- 35 test cases covering all previously untested methods in `LocalizationManager`
- Tests cover: `getString()` with missing key, `isRTL()`, `applyDirection()` (RTL/LTR), `getLocaleFromDisplayName()`, `getLanguageDisplayName()`, `getInstrumentKey()`, `getLocalizedInstrumentName()`, `getAllInstrumentVariants()`

**Expanded `TeacherProfileViewControllerTest.java`:**

- Added 10 new test cases: `loadTeacherInfo()` (user logged in / profile null / no user), `handleDeleteSlot()` (available/booked slot), `setupDateFormatter()` (Arabic/Chinese locale), `updateTexts()`, `loadSchedule()` with booked slot

**Impact:** Line coverage increased from 54.5% → **61.8%** (total 228 tests, 0 failures).

---

## 3. Architecture Changes

### 3.1 New Utility and Base Classes Added

Sprint 7 introduced two new classes to improve code structure:

| Class            | Package      | Purpose                                                                               |
| ---------------- | ------------ | ------------------------------------------------------------------------------------- |
| `DaoHelper`      | `util`       | Static helper methods eliminating DAO boilerplate (query, insert, update)             |
| `BaseController` | `controller` | Abstract base class holding shared logic for `LoginController` and `SignupController` |

No new domain model classes, DAO classes, or database tables were introduced.

### 3.2 ER Diagram and UML Class Diagram — Updated to Match Codebase

**Changed by:** Lu Liu, Ying Luo  
**Sprint:** 7

Previous versions of the ER diagram and UML class diagram were based on the initial design and included entities/classes that were never implemented:

- `TEACHES` relationship table — not implemented
- `LOCALIZEDCONTENT` table — not implemented
- `InstrumentDAO` class — not implemented
- `LocalizationService` class — replaced by `LocalizationManager` utility

**Action taken:** Both diagrams were redrawn to reflect the actual implemented state:

**Actual Database Tables (6):**

1. `users` — user accounts with role (learner/teacher), email, password hash
2. `learner_profiles` — learner-specific information
3. `teacher_profiles` — teacher bio, instruments, hourly rate
4. `time_slots` — available time slots created by teachers
5. `bookings` — booking records linking learners to time slots
6. `languages` — reference table for supported locales

**Actual Main Classes:**

- Models: `User`, `Booking`, `TimeSlot`, `LearnerProfile`, `TeacherProfile`
- DAOs: `UserDAO`, `BookingDAO`, `TimeSlotDAO`, `LearnerProfileDAO`, `TeacherProfileDAO`
- Services: `UserService`, `BookingService`, `TeacherService`, `TimeSlotService`
- Controllers: `LoginController`, `SignupController`, `StudentDashboardController`, `TeacherDashboardController`, `BookingViewController`, `TeacherProfileViewController`
- Utilities: `LocalizationManager`, `PasswordUtil`, `DatabaseConnection`, `SessionManager`, `DaoHelper`
- Base classes: `BaseController` (abstract)

---

## 4. Infrastructure Changes

### 4.1 Jenkins Pipeline — SonarQube Stage Added

**File:** `Jenkinsfile`  
**Changed by:** Yicheng Chen  
**Sprint:** 7

A new `SonarQube Analysis` stage was added to the existing Jenkins pipeline between the `Run Tests` and `Package Application` stages. The stage:

- Runs `mvn sonar:sonar` with a SonarQube authentication token stored in Jenkins credentials
- Publishes results to the local SonarQube server at `http://localhost:9000`
- Fails the build if SonarQube is unavailable (non-blocking warnings if token is missing)

**Jenkins Credentials Required:**

- Credential ID: `sonarqube-token` (type: Secret text, value: token from SonarQube → My Account → Security)

---

### 4.2 Docker — No Changes

The Dockerfile was not modified in Sprint 7. The existing configuration (eclipse-temurin:21-jdk base, JavaFX SDK 21 installation, software rendering flag `-Dprism.order=sw`) remains unchanged.

---

### 4.3 SonarQube Configuration

**File:** `sonar-project.properties`  
No changes were made to `sonar-project.properties`. The existing configuration correctly points to `src/main/java` as sources and `target/classes` as binaries.

---

## 5. Dependency Changes

No new dependencies were added to `pom.xml` during Sprint 7. All test dependencies (JUnit 5, Mockito, JaCoCo) were already present from Sprint 6.

---

## 6. Documentation Changes

| Document                                       | Status  | Description                                                                                |
| ---------------------------------------------- | ------- | ------------------------------------------------------------------------------------------ |
| `README.md`                                    | Updated | Enhanced Docker usage instructions; added Xming setup for Windows, XQuartz setup for macOS |
| `docs/sprint7/Test_Plan_Sprint7.md`            | New     | Formal test plan for Sprint 7                                                              |
| `docs/sprint7/Bug_Tracking_Table.md`           | New     | Bug tracking table documenting all issues found during Sprint 7 testing                    |
| `docs/sprint7/Technical_Changes_Sprint7.md`    | New     | This document                                                                              |
| `docs/sprint7/Heuristic_Evaluation_Sprint7.md` | New     | Nielsen Heuristic Evaluation reports for all 4 team members                                |
| ER Diagram                                     | Updated | Redrawn to match actual 6-table database schema                                            |
| UML Class Diagram                              | Updated | Redrawn to match actual implemented classes                                                |

---

## 7. Known Technical Debt

| Item                            | Description                                                                                                            | Priority | Status |
| ------------------------------- | ---------------------------------------------------------------------------------------------------------------------- | -------- | ------ |
| Language preference persistence | Selected locale is not saved between application restarts                                                              | Low      | Open   |
| SonarQube coverage gate         | Coverage is 61.8%; SonarQube default Quality Gate is 80%; requires custom gate configuration or further test expansion | Low      | Open   |
| UI tests                        | JavaFX controller tests use reflection-based field injection; TestFX integration tests would provide better coverage   | Medium   | Open   |
