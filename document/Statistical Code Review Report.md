# 📋 Static Code Review Report

- **Project Name**: Music Course Platform
- **Branch**: `main`
- **Analysis Tool**: SonarQube Community Edition v26.1.0.118079
- **Language**: Java 17+, MySQL
- **Review Date**: Sprint 7 – 28 April 2026
- **Repository**: github.com/chenyicheng1998/MusicCoursePlatform

---

## 1. Overview

### 1.1 Quality Gate Summary

| Metric | Value | Rating | Status |
|---|---|---|---|
| **Security** | 0 Open Issues | A | ✅ Passed |
| **Reliability** | 0 Open Issues | A | ✅ Passed |
| **Maintainability** | 43 Open Issues | A | ⚠️ Code smells present |
| **Coverage** | 87.9% | — | ✅ Above 80% target |
| **Duplications** | 4.5% | — | ✅ Below 5% threshold |
| **Security Hotspots** | 0 | A | ✅ All resolved |
| **Accepted Issues** | 0 | — | ✅ None suppressed |

> **Quality Gate: PASSED**

### 1.2 Sprint-over-Sprint Improvement

| Metric | Previous Sprint | Current Sprint | Status |
|---|---|---|---|
| Security Hotspots | 5 | 0 | ✅ Resolved |
| Test Coverage | 56.6% | 87.9% | ✅ +31.3 pp — target met |
| Duplication Density | 10.4% | 4.5% | ✅ Below 5% threshold |
| Maintainability Issues | 85 | 43 | ⚠️ Reduced by 49% |
| Cyclomatic Complexity | 650 | 652 | ⚠️ Marginally increased |

### 1.3 Dashboard Screenshot

![SonarQube Dashboard](images/dia_SonarQube.png)
*Figure 1: SonarQube Dashboard – Overall Code View*

---

## 2. Code Metrics

### 2.1 Cyclomatic Complexity

**Total Cyclomatic Complexity: 652 | Cognitive Complexity: 321**

![SonarQube Complexity](images/dia_complexity.png)
*Figure 2: SonarQube Measures – Cyclomatic Complexity per file*

Cyclomatic complexity measures the number of independent execution paths through a method's code. A score above 10 per method is considered high; a per-file total above 30 indicates the file contains too much logic and should be split.

| File | Cyclomatic Complexity | Status |
|---|---|---|
| `controller/StudentDashboardController.java` | 73 | 🔴 Critical |
| `controller/TeacherDashboardController.java` | 64 | 🔴 Critical |
| `service/UserService.java` | 41 | 🔴 High |
| `util/LocalizationManager.java` | 36 | 🟠 Medium |
| `service/TimeSlotService.java` | 36 | 🟠 Medium |
| `dao/UserDAO.java` | 30 | 🟠 Medium |
| *(remaining files)* | *(see SonarQube Measures tab)* | |
| **Total** | **652** | 🔴 High |

The two dashboard controllers alone account for **137 of 652 (21.0%)** of the total complexity. These files almost certainly contain large `if/else` chains and deeply nested event-handler logic that should be extracted into dedicated service methods.

---

### 2.2 Lines of Code per File

**Total Lines of Code: 3,539 | Total Lines (inc. blanks/comments): 4,529**

![SonarQube Size Measures](images/dia_sizemeasures.png)
*Figure 3: SonarQube Measures – Size (Lines of Code per file)*

| Metric | Value |
|---|---|
| Lines of Code (LOC) | 3,539 |
| Total Lines | 4,529 |
| Statements | 1,406 |
| Functions | 342 |
| Classes | 32 |
| Files | 32 |
| Comment Lines | 197 |
| Comment Ratio | 5.3% |
| **Average LOC per function** | **~10.3** |
| **Average functions per class** | **~10.7** |

**Largest files by LOC:**

| File | Lines of Code | Status |
|---|---|---|
| `controller/StudentDashboardController.java` | 401 | 🔴 Too large — split required |
| `controller/TeacherDashboardController.java` | 366 | 🔴 Too large — split required |
| `controller/BookingViewController.java` | 205 | 🟠 Monitor |
| `controller/TeacherProfileViewController.java` | 182 | 🟠 Monitor |
| *(remaining files)* | *(see SonarQube Measures tab)* | |

**Note on comment ratio:** At 5.3%, the codebase is lightly documented. Public-facing methods in service and controller classes largely lack Javadoc, which contributes to open maintainability issues.

---

### 2.3 Duplicate and Unreachable Code

**Duplication Density: 4.5% | Duplicated Lines: 206 | Duplicated Blocks: 14 | Duplicated Files: 6**

![SonarQube Duplications](images/dia_duplication.png)
*Figure 4: SonarQube Measures – Duplications Overview bubble chart*

| Metric | Value | Threshold | Status |
|---|---|---|---|
| Duplication density | 4.5% | ≤ 5% | ✅ Within threshold |
| Duplicated lines | 206 | — | ⚠️ Needs refactoring |
| Duplicated blocks | 14 | — | ⚠️ Needs refactoring |
| Duplicated files | 6 | — | ⚠️ 6 of 32 files affected |

Duplication has dropped significantly from 10.4% to 4.5%, now falling within the acceptable threshold. The bubble chart (Figure 4) shows two clusters of files with remaining duplication. The two largest bubbles in the top-right correspond to the dashboard controllers which share similar event-handling and UI-update logic. The mid-chart cluster corresponds to the DAO files which retain some repeated error-handling and query patterns.

**Known remaining sources of duplication:**
- Repeated `System.err` error-handling blocks across `BookingDAO`, `TimeSlotDAO`, and `UserDAO`
- `SELECT *` query patterns copy-pasted across all three DAO files
- Similar UI event logic shared between `StudentDashboardController` and `TeacherDashboardController`

**Unreachable code:**
- `util/DatabaseConnection.java` L103: an expression that always evaluates to `true`, making one branch permanently unreachable (confirmed by SonarQube symbolic execution analysis)

---

## 3. Issue Breakdown

### 3.1 Total Open Issues

| Category | Count |
|---|---|
| Maintainability (Code Smells) | 43 |
| Security Issues | 0 |
| Reliability Issues | 0 |
| Security Hotspots | 0 |
| **Total** | **43** |

### 3.2 By Issue Type

The 43 open maintainability issues are distributed across the following categories, identified from the SonarQube issues list:

| Issue Type | Affected Files |
|---|---|
| `System.err` / `System.out` instead of logger | `Main.java`, `BookingDAO`, `TimeSlotDAO`, `UserDAO` |
| `SELECT *` queries | `BookingDAO`, `TimeSlotDAO`, `UserDAO` |
| Test lambda with multiple throwable calls | `BookingServiceTest`, `TeacherServiceTest`, `TimeSlotServiceTest` |
| Generic exception handling | `BookingService`, `TeacherService`, `TimeSlotService`, `UserService` |
| Hardcoded URIs | `LoginController`, `SignupController` |
| Unused imports | `BookingViewControllerTest`, `LoginControllerTest` |
| Duplicate method implementations | `Booking`, `TeacherProfile`, `TimeSlot` |
| Singleton pattern warnings | `SessionManager`, `LocalizationManager` |
| Other (style, structure) | `Launcher.java`, `PasswordUtilTest` |

---

## 4. Key Findings

### 🚨 4.1 System.err / System.out (Medium)

**Files:** `Main.java`, `BookingDAO.java`, `TimeSlotDAO.java`, `UserDAO.java`

Using `System.err` and `System.out` bypasses the logging framework, meaning there is no control over log levels, no timestamps, no log routing, and no ability to suppress output in production environments.

**Fix:** Replace with SLF4J logger:
```java
// Before
System.err.println("Error: " + e.getMessage());

// After
private static final Logger logger = LoggerFactory.getLogger(BookingDAO.class);
logger.error("Error occurred", e);
```

---

### 🚨 4.2 SELECT * Queries (Medium)

**Files:** `BookingDAO.java`, `TimeSlotDAO.java`, `UserDAO.java`

`SELECT *` fetches all columns including unused ones, wastes memory and network bandwidth, and makes code fragile when the database schema changes.

**Fix:**
```sql
-- Before
SELECT * FROM bookings WHERE booking_id = ?

-- After
SELECT booking_id, user_id, slot_id, status FROM bookings WHERE booking_id = ?
```

---

### 🚨 4.3 Generic Exception Handling (Medium)

**Files:** `BookingService.java`, `TeacherService.java`, `TimeSlotService.java`, `UserService.java`

Throwing or catching the generic `Exception` class makes it impossible for callers to distinguish between different error conditions and handle them appropriately.

**Fix:**
```java
// Before
throw new Exception("Booking not found");

// After
throw new BookingNotFoundException("Booking not found for id: " + id);
```

---

### 🚨 4.4 Hardcoded URIs (Low)

**Files:** `LoginController.java`, `SignupController.java`

Hardcoded URIs violate the DRY principle and require edits in multiple files when paths change.

**Fix:**
```java
// Before
getHostServices().showDocument("http://localhost:8080/dashboard");

// After
getHostServices().showDocument(AppConfig.DASHBOARD_URI);
```

---

### ⚠️ 4.5 Duplicate Method Implementations (Medium)

| File | Duplicate of | Line |
|---|---|---|
| `model/Booking.java` | `getBookingStatus()` at L49 | L57 |
| `model/TeacherProfile.java` | `getTeacherProfileId()` at L29 | L37 |
| `model/TimeSlot.java` | `getSlotStatus()` at L63 | L71 |

Two methods in the same class share identical implementations. The duplicate should be removed and all callers updated to use the original.

---

### ⚠️ 4.6 Test Lambda Issues (Medium)

**Files:** `BookingServiceTest.java`, `TeacherServiceTest.java`, `TimeSlotServiceTest.java`

`assertThrows` lambdas contain multiple statements, making it ambiguous which line is actually expected to throw the exception.

**Fix:**
```java
// Before
assertThrows(Exception.class, () -> {
    service.setup(data);
    service.process(data);
});

// After
assertThrows(Exception.class, () -> service.process(data));
```

---

### ⚠️ 4.7 Always-True Expression / Dead Code (Medium)

**File:** `util/DatabaseConnection.java` L103

A condition always evaluates to `true`, meaning one branch is permanently unreachable dead code. Detected by SonarQube symbolic execution analysis.

**Fix:** Review the condition at L103, remove the unreachable branch, and simplify the logic.

---

### ✅ 4.8 Security Hotspots — Resolved

All **5 security hotspots** from the previous sprint have been successfully resolved. SonarQube now reports **0 security hotspots** with an **A rating**.

> **Previously resolved hotspots:**
> - Weak cryptography / hashing in `util/PasswordUtil.java`
> - SQL injection surface in `dao/UserDAO.java` and `dao/BookingDAO.java`
> - Hardcoded credentials in `util/DatabaseConnection.java`
> - Insecure random number generation
> - Unvalidated input in `controller/LoginController.java`

---

### ℹ️ 4.9 Singleton Pattern Warnings (Info)

| File | Line |
|---|---|
| `controller/SessionManager.java` | L5 |
| `util/LocalizationManager.java` | L24 |

Singletons can cause thread-safety issues and make unit testing harder by introducing global state.

**Recommendation:** Verify thread-safe initialisation is used (e.g., enum singleton or Bill Pugh holder pattern). Consider replacing with dependency injection.

---

### 🟡 4.10 Low Priority / Style Issues

| File | Issue | Line |
|---|---|---|
| `Launcher.java` | File not in a named package | — |
| `LocalizationManager.java` | Nested try block — extract to separate method | L185 |
| `BookingViewControllerTest.java` | Unused import `org.mockito.Mockito` | L11 |
| `BookingViewControllerTest.java` | Unused import `java.time.LocalTime` | L16 |
| `BookingViewControllerTest.java` | Replace lambda with method reference `latch::countDown` | L39 |
| `BookingViewControllerTest.java` | Declared thrown exception can never be thrown | L177 |
| `LoginControllerTest.java` | Unused import `model.User` | L8 |
| `PasswordUtilTest.java` | Use `isEmpty()` instead of `.length() == 0` | L26 |

---

## 5. Test Coverage

| Metric | Value | Target | Status |
|---|---|---|---|
| Overall coverage | 87.9% | ≥ 80% | ✅ Target met |
| Lines to cover | ~1,700 | — | — |
| Total functions | 342 | — | — |

Coverage has improved significantly from 56.6% to **87.9%**, exceeding the 80% target. The remaining `assertThrows` lambda issues in the test files should still be addressed to ensure tests are correctly asserting the expected behaviour.

---

## 6. Recommendations

### High Priority
1. Refactor `StudentDashboardController` (CC: 73, LOC: 401) and `TeacherDashboardController` (CC: 64, LOC: 366) — extract business logic into dedicated service methods
2. Replace all remaining `System.err` / `System.out` calls with SLF4J logger across all DAO and Main classes
3. Replace all remaining `SELECT *` queries with explicit column names in `BookingDAO`, `TimeSlotDAO`, and `UserDAO`
4. Replace generic `Exception` throws with custom exceptions in all service classes
5. Investigate and fix the always-true dead code branch in `DatabaseConnection.java` L103

### Medium Priority
- Extract hardcoded URIs in `LoginController` and `SignupController` to a constants file
- Remove duplicate method implementations in `Booking`, `TeacherProfile`, and `TimeSlot` models
- Refactor `assertThrows` lambdas in test files to contain a single throwable statement
- Maintain test coverage above 80% as new features are added
- Extract nested try block in `LocalizationManager.java` L185 into a separate method
- Continue reducing code duplication — consolidate shared DAO patterns into a base class or utility

### Low Priority
- Move `Launcher.java` into a proper named package
- Clean up unused imports across test files (run IntelliJ → Optimize Imports)
- Replace `.length() == 0` with `.isEmpty()` in `PasswordUtilTest.java` L26
- Review Singleton implementations in `SessionManager` and `LocalizationManager` for thread safety
- Add Javadoc to public methods (currently only 5.3% comment ratio)

---

## 7. Appendix

### A. Screenshots

- **Figure 1** – `dia_SonarQube.png` — Quality Gate overview dashboard
- **Figure 2** – `dia_complexity.png` — Cyclomatic Complexity per file
- **Figure 3** – `dia_sizemeasures.png` — Lines of Code (Size measures)
- **Figure 4** – `dia_duplications.png` — Duplications Overview bubble chart

### B. Tool Configuration

| Item | Value |
|---|---|
| Tool | SonarQube Community Edition v26.1.0.118079 |
| Mode | MQR Mode |
| Local URL | http://localhost:9000 |
| Project Key | MusicCoursePlatform |
| Language | Java 17 |

### C. Metrics Glossary

| Term | Definition |
|---|---|
| Cyclomatic Complexity | Number of independent execution paths through a method (> 10 = high) |
| Cognitive Complexity | How difficult code is to understand — accounts for nesting depth |
| Code Smell | A maintainability issue that doesn't break functionality but indicates poor structure |
| Security Hotspot | A sensitive code area requiring manual review to assess real security risk |
| Coverage | Percentage of lines executed by automated tests |
| Duplication Density | Percentage of code blocks identical or nearly identical elsewhere in the codebase |
| Quality Gate | Threshold conditions a build must meet to be considered production-ready |