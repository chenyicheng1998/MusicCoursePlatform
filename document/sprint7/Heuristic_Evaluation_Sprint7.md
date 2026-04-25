# Heuristic Evaluation Report — Sprint 7
**Project:** Music Course Platform  
**Date:** 2026-04-22  
**Method:** Nielsen's 10 Usability Heuristics  
**Evaluators:** Lu Liu, Su Wai Phyoe, Yicheng Chen, Ying Luo  

---

## Nielsen's 10 Heuristics Reference

| # | Heuristic | Description |
|---|---|---|
| H1 | Visibility of System Status | System should always keep users informed about what is going on |
| H2 | Match Between System and Real World | Use words and concepts familiar to users, not system-oriented language |
| H3 | User Control and Freedom | Support undo and redo; let users escape from unintended states |
| H4 | Consistency and Standards | Follow platform conventions; don't use different words for the same thing |
| H5 | Error Prevention | Design to prevent problems; prefer constraining inputs over recovering from errors |
| H6 | Recognition Rather Than Recall | Minimize user's memory load; make options visible |
| H7 | Flexibility and Efficiency of Use | Provide accelerators for experts without hindering novices |
| H8 | Aesthetic and Minimalist Design | Show only relevant information; remove unnecessary elements |
| H9 | Help Users Recognize, Diagnose, and Recover from Errors | Error messages should be in plain language and suggest solutions |
| H10 | Help and Documentation | Provide context-sensitive help when needed |

**Severity Scale:**
- **0** — Not a usability problem
- **1** — Cosmetic problem only; fix if time permits
- **2** — Minor usability problem; low priority
- **3** — Major usability problem; important to fix
- **4** — Usability catastrophe; imperative to fix

---

---

# Evaluator 1: Yicheng Chen

**Date:** 2026-04-22  
**Role (during evaluation):** Evaluating as a first-time user (learner perspective)

---

## Issues Found

### YC-01 — No loading indicator during login
**Heuristic:** H1 — Visibility of System Status  
**Severity:** 2  
**Description:** When the user clicks the "Login" button, the application queries the database. If the database is slow, the button simply appears frozen for 1–2 seconds with no spinner, progress indicator, or status message. The user does not know whether the action is being processed.  
**Recommendation:** Disable the login button and show a small "Logging in..." label or spinner while the authentication query is in progress. Re-enable after completion.

---

### YC-02 — Language selection uses flag-only icons with no text labels
**Heuristic:** H2 — Match Between System and Real World  
**Severity:** 2  
**Description:** The language switcher on the login screen uses small flag icons (CN, UK, AR) without text labels. Users unfamiliar with these flags (especially Arabic speakers who may not recognise the Saudi flag) may not understand what the flags represent.  
**Recommendation:** Add text labels next to flags: "English", "中文", "العربية".

---

### YC-03 — No confirmation dialog before application close
**Heuristic:** H3 — User Control and Freedom  
**Severity:** 1  
**Description:** Clicking the window close button immediately exits the application without warning. If the user is mid-way through filling in a time slot or booking form, all input is lost.  
**Recommendation:** Add a confirmation dialog: "Are you sure you want to exit? Any unsaved changes will be lost."

---

### YC-04 — Login error message is generic
**Heuristic:** H9 — Help Users Recognize, Diagnose, and Recover from Errors  
**Severity:** 3  
**Description:** When login fails (wrong password or non-existent email), the application shows only "Login failed." The message does not distinguish between "email not found" and "incorrect password." While consolidating these for security is reasonable, the message does not guide the user on how to recover (e.g., forgot password link, check email spelling).  
**Recommendation:** Change message to: "Login failed. Please check your email and password and try again." (No "forgot password" feature exists yet, so don't promise one.)

---

### YC-05 — No keyboard shortcut support
**Heuristic:** H7 — Flexibility and Efficiency of Use  
**Severity:** 1  
**Description:** The application has no keyboard shortcuts. Expert users must use the mouse for all interactions. For example, pressing Enter after entering a password does not submit the login form; the user must click the button.  
**Recommendation:** Bind Enter key to the primary action button on each form. Add Tab-order navigation between form fields.

---

### YC-06 — No help or documentation within the application
**Heuristic:** H10 — Help and Documentation  
**Severity:** 2  
**Description:** There is no in-app help, tooltip, or user guide. New users discovering the platform have no guidance on how to create an account, how booking works, or what the teacher profile fields mean.  
**Recommendation:** Add tooltips on key form fields (e.g., "Hourly rate: enter your rate in euros per hour") and a basic "How it works" section on the main dashboard.

---

## Evaluator 1 — Summary Table

| Issue ID | Heuristic | Description | Severity |
|---|---|---|---|
| YC-01 | H1 | No loading indicator during login | 2 |
| YC-02 | H2 | Language flags have no text labels | 2 |
| YC-03 | H3 | No confirmation before app close | 1 |
| YC-04 | H9 | Login error message is generic | 3 |
| YC-05 | H7 | No keyboard shortcut / Enter key on forms | 1 |
| YC-06 | H10 | No in-app help or documentation | 2 |

---

---

# Evaluator 2: Lu Liu

**Date:** 2026-04-22  
**Role (during evaluation):** Evaluating as a teacher creating a profile and managing bookings

---

## Issues Found

### LL-01 — Teacher dashboard shows no status after saving profile
**Heuristic:** H1 — Visibility of System Status  
**Severity:** 3  
**Description:** When a teacher updates their profile and clicks Save, the form clears and the data is saved to the database, but no success message is displayed. The teacher has no visual confirmation that the save was successful. This causes confusion and leads to users repeating the save action unnecessarily.  
**Recommendation:** Show a brief status message: "Profile saved successfully." using a green label or Alert dialog that auto-dismisses after 3 seconds.

---

### LL-02 — "Time slot" terminology inconsistent across screens
**Heuristic:** H4 — Consistency and Standards  
**Severity:** 2  
**Description:** The teacher dashboard uses the label "Time Slot" when creating availability, but the student dashboard and booking view uses "Available Session" in some places and "Slot" in others. This inconsistency creates confusion about whether these refer to the same concept.  
**Recommendation:** Standardize to "Time Slot" throughout all screens and localization strings.

---

### LL-03 — No way to edit or delete a created time slot
**Heuristic:** H3 — User Control and Freedom  
**Severity:** 3  
**Description:** Once a teacher creates a time slot, there is no UI to edit or delete it (unless directly accessing the database). If a teacher makes a mistake in the date or time, they cannot correct it through the application.  
**Recommendation:** Add Edit and Delete buttons for each time slot row in the teacher dashboard's time slot list. Add a confirmation dialog before deletion.

---

### LL-04 — Instrument field accepts any free text — no validation or autocomplete
**Heuristic:** H5 — Error Prevention  
**Severity:** 2  
**Description:** The "Instruments" field in the teacher profile is a free-text input. Users might type "guitar", "Guitar", "GUITAR" or "Gutar" (typo). This makes filtering/searching by instrument unreliable.  
**Recommendation:** Replace with a multi-select combo box of predefined instruments (Guitar, Piano, Violin, Drums, Vocals, etc.) with the option to add a custom entry. This prevents spelling inconsistencies and enables reliable searching.

---

### LL-05 — Booking list does not show teacher name — only ID
**Heuristic:** H6 — Recognition Rather Than Recall  
**Severity:** 2  
**Description:** In the student booking history view, the booking records show a teacher ID number instead of the teacher's name. Users must remember which teacher has which ID, or navigate away to look it up.  
**Recommendation:** Join the booking query with the teacher profile table to display the teacher's name (and optionally a small profile photo) in the booking history.

---

### LL-06 — Error messages not in Arabic even when Arabic is selected
**Heuristic:** H9 — Help Users Recognize, Diagnose, and Recover from Errors  
**Severity:** 3  
**Description:** When Arabic language is selected, some validation error messages (particularly in the signup form) fall back to English. This is because not all error message keys are present in `messages_ar.properties`.  
**Recommendation:** Review `messages_ar.properties` and add all missing error key translations. Test each form in Arabic mode to confirm full coverage.

---

## Evaluator 2 — Summary Table

| Issue ID | Heuristic | Description | Severity |
|---|---|---|---|
| LL-01 | H1 | No success confirmation after teacher saves profile | 3 |
| LL-02 | H4 | Inconsistent terminology ("Time Slot" vs "Session") | 2 |
| LL-03 | H3 | No edit/delete for created time slots | 3 |
| LL-04 | H5 | Instrument field has no validation or autocomplete | 2 |
| LL-05 | H6 | Booking list shows teacher ID instead of name | 2 |
| LL-06 | H9 | Arabic error messages fall back to English | 3 |

---

---

# Evaluator 3: Su Wai Phyoe

**Date:** 2026-04-22  
**Role (during evaluation):** Evaluating as a new learner attempting to find and book a lesson

---

## Issues Found

### SW-01 — No feedback when searching for teachers with no results
**Heuristic:** H1 — Visibility of System Status  
**Severity:** 2  
**Description:** When a student filters teachers by instrument and no teachers match, the list simply becomes empty with no message. The user cannot distinguish between "no teachers available" and "the application failed to load data."  
**Recommendation:** Display an explicit "No teachers found for [instrument]" message in the empty list area.

---

### SW-02 — Sign up form does not explain password requirements
**Heuristic:** H2 — Match Between System and Real World  
**Severity:** 2  
**Description:** The signup form has a password field but no indicator of what format is required (minimum length, special characters, etc.). If the backend has validation rules, the user is never told about them upfront; they only discover requirements after a failed attempt.  
**Recommendation:** Add a helper text below the password field: "Minimum 8 characters." Show it before the user attempts to submit.

---

### SW-03 — Learner cannot cancel a booking
**Heuristic:** H3 — User Control and Freedom  
**Severity:** 3  
**Description:** Once a learner books a time slot, there is no cancellation option in the UI. The booking is permanent from the user's perspective. Real-world booking systems always require a cancellation feature.  
**Recommendation:** Add a "Cancel Booking" button to each entry in the booking history. Include a confirmation dialog: "Are you sure you want to cancel this booking?"

---

### SW-04 — Student dashboard does not show current user's name
**Heuristic:** H6 — Recognition Rather Than Recall  
**Severity:** 1  
**Description:** After logging in, the student dashboard does not display the logged-in user's name anywhere on the screen. The user must remember who they are logged in as.  
**Recommendation:** Add a welcome message in the header: "Welcome, [First Name]" using the `SessionManager.getCurrentUser()` value.

---

### SW-05 — Date and time picker is plain text input
**Heuristic:** H5 — Error Prevention  
**Severity:** 2  
**Description:** The time slot creation form and booking form use plain text input for dates and times. Users can type invalid dates (e.g., "2026-13-45") which produce cryptic errors rather than being prevented upfront.  
**Recommendation:** Replace plain date/time text fields with `DatePicker` and `Spinner` JavaFX controls that constrain input to valid values.

---

### SW-06 — Application has no window title bar icon
**Heuristic:** H8 — Aesthetic and Minimalist Design  
**Severity:** 1  
**Description:** The application window uses the default JavaFX icon (coffee cup). No custom application icon has been set for the window title bar or taskbar.  
**Recommendation:** Create a simple music note or guitar icon (PNG, 32×32 and 64×64) and set it with `stage.getIcons().add(new Image(...))` in the main application entry point.

---

## Evaluator 3 — Summary Table

| Issue ID | Heuristic | Description | Severity |
|---|---|---|---|
| SW-01 | H1 | No "no results" message when teacher search is empty | 2 |
| SW-02 | H2 | Password requirements not shown on signup form | 2 |
| SW-03 | H3 | Learner cannot cancel a booking | 3 |
| SW-04 | H6 | Dashboard does not show logged-in user's name | 1 |
| SW-05 | H5 | Date/time input is plain text — allows invalid values | 2 |
| SW-06 | H8 | Application has no custom window icon | 1 |

---

---

# Evaluator 4: Ying Luo

**Date:** 2026-04-22  
**Role (during evaluation):** Evaluating as a teacher viewing and managing student bookings

---

## Issues Found

### YL-01 — Teacher has no overview of their upcoming schedule
**Heuristic:** H1 — Visibility of System Status  
**Severity:** 3  
**Description:** The teacher dashboard displays a list of all bookings but there is no calendar view or "upcoming this week" summary. A teacher with many bookings must scroll through a flat list to find upcoming lessons. The system status (who is coming, when) is not communicated efficiently.  
**Recommendation:** Add a summary section at the top of the teacher dashboard showing the next 3 upcoming bookings with date, time, and student name.

---

### YL-02 — Logout button is not clearly visible
**Heuristic:** H4 — Consistency and Standards  
**Severity:** 2  
**Description:** The logout button is placed at the bottom-left corner in a small font. Most desktop applications place logout in a top-right menu or in the header. Users who expect standard placement may have difficulty finding it.  
**Recommendation:** Move the logout button to the top-right of the screen, consistent with common desktop and web application conventions.

---

### YL-03 — No distinction between past and future bookings in booking list
**Heuristic:** H6 — Recognition Rather Than Recall  
**Severity:** 2  
**Description:** The booking list shows all bookings (past and future) in the same list with no visual separation, color coding, or filtering. Teachers and students must mentally calculate which bookings are upcoming vs. completed.  
**Recommendation:** Add a filter/tab control: "Upcoming" / "Past" / "All". Or visually gray out past bookings and bold upcoming ones.

---

### YL-04 — Form fields have no placeholder text
**Heuristic:** H6 — Recognition Rather Than Recall  
**Severity:** 2  
**Description:** Most input fields (teacher bio, hourly rate, instruments) have labels above them but no placeholder text inside the field. The user sees an empty text box and must remember (or guess) the expected format. For example, should hourly rate be entered as "20", "20.00", or "€20/hour"?  
**Recommendation:** Add placeholder text to all input fields. For example: "E.g. 20.00" for hourly rate, "E.g. Guitar, Piano" for instruments, "Describe your teaching style..." for bio.

---

### YL-05 — UI layout breaks on smaller screen resolutions
**Heuristic:** H8 — Aesthetic and Minimalist Design  
**Severity:** 2  
**Description:** At screen resolutions below 1280×800, some buttons are clipped or overlap with table content. The application window has a fixed minimum size but some panels overflow below the visible area, requiring scrolling that is not indicated.  
**Recommendation:** Review all FXML layout files and replace fixed pixel sizes with percentage-based constraints (`HBox.hgrow`, `VBox.vgrow`) to allow the UI to scale gracefully. Set an appropriate minimum window size.

---

### YL-06 — No onboarding guidance for first-time users
**Heuristic:** H10 — Help and Documentation  
**Severity:** 2  
**Description:** A brand-new user (learner or teacher) who launches the application for the first time has no guidance on where to start. There is no welcome screen, walkthrough, or tooltip explaining the user flow (Sign up → create profile → book a lesson).  
**Recommendation:** Add a one-time "Welcome" dialog on first login that briefly explains the three main steps for each role. Store a flag in preferences to not show it again after the first view.

---

## Evaluator 4 — Summary Table

| Issue ID | Heuristic | Description | Severity |
|---|---|---|---|
| YL-01 | H1 | No upcoming schedule summary on teacher dashboard | 3 |
| YL-02 | H4 | Logout button hard to find (not in expected location) | 2 |
| YL-03 | H6 | No distinction between past and future bookings | 2 |
| YL-04 | H6 | Form fields have no placeholder text | 2 |
| YL-05 | H8 | UI layout breaks on small screen resolutions | 2 |
| YL-06 | H10 | No onboarding or first-time user guidance | 2 |

---

---

# Team Consolidated Summary Table

All issues from all 4 evaluators, sorted by severity (highest first):

| Issue ID | Evaluator | Heuristic | Description | Severity |
|---|---|---|---|---|
| YC-04 | Yicheng Chen | H9 | Login error message is generic; no recovery guidance | 3 |
| LL-01 | Lu Liu | H1 | No success confirmation after teacher saves profile | 3 |
| LL-03 | Lu Liu | H3 | No edit/delete for created time slots | 3 |
| LL-06 | Lu Liu | H9 | Arabic error messages fall back to English | 3 |
| SW-03 | Su Wai Phyoe | H3 | Learner cannot cancel a booking | 3 |
| YL-01 | Ying Luo | H1 | No upcoming schedule summary on teacher dashboard | 3 |
| YC-01 | Yicheng Chen | H1 | No loading indicator during login | 2 |
| YC-02 | Yicheng Chen | H2 | Language flags have no text labels | 2 |
| YC-06 | Yicheng Chen | H10 | No in-app help or documentation | 2 |
| LL-02 | Lu Liu | H4 | Inconsistent terminology (Time Slot vs Session) | 2 |
| LL-04 | Lu Liu | H5 | Instrument field has no validation or autocomplete | 2 |
| LL-05 | Lu Liu | H6 | Booking list shows teacher ID instead of name | 2 |
| SW-01 | Su Wai Phyoe | H1 | No "no results" message when teacher search is empty | 2 |
| SW-02 | Su Wai Phyoe | H2 | Password requirements not shown on signup form | 2 |
| SW-05 | Su Wai Phyoe | H5 | Date/time is plain text input — allows invalid values | 2 |
| YL-02 | Ying Luo | H4 | Logout button hard to find | 2 |
| YL-03 | Ying Luo | H6 | No distinction between past and future bookings | 2 |
| YL-04 | Ying Luo | H6 | Form fields have no placeholder text | 2 |
| YL-05 | Ying Luo | H8 | UI layout breaks on small screen resolutions | 2 |
| YL-06 | Ying Luo | H10 | No onboarding guidance | 2 |
| YC-03 | Yicheng Chen | H3 | No confirmation before app close | 1 |
| YC-05 | Yicheng Chen | H7 | No keyboard shortcut / Enter key on login form | 1 |
| SW-04 | Su Wai Phyoe | H6 | Dashboard does not show logged-in user's name | 1 |
| SW-06 | Su Wai Phyoe | H8 | No custom application window icon | 1 |

---

## Issues by Heuristic

| Heuristic | # Issues | Highest Severity |
|---|---|---|
| H1 — Visibility of System Status | 5 | 3 |
| H2 — Match with Real World | 2 | 2 |
| H3 — User Control and Freedom | 3 | 3 |
| H4 — Consistency and Standards | 2 | 2 |
| H5 — Error Prevention | 2 | 2 |
| H6 — Recognition Rather Than Recall | 4 | 2 |
| H7 — Flexibility and Efficiency | 1 | 1 |
| H8 — Aesthetic and Minimalist Design | 2 | 2 |
| H9 — Error Recovery | 2 | 3 |
| H10 — Help and Documentation | 2 | 2 |

---

## Priority Recommendations

**Fix immediately (Severity 3):**
1. Add success feedback after teacher profile save (H1)
2. Improve login error message with recovery guidance (H9)
3. Allow edit/delete of time slots (H3)
4. Allow learners to cancel bookings (H3)
5. Show upcoming schedule summary on teacher dashboard (H1)
6. Complete Arabic translations for all error messages (H9)

**Fix when time permits (Severity 2):**
- Add placeholder text to all form fields
- Replace date/time text inputs with proper pickers
- Replace teacher ID with teacher name in booking list
- Add "no results" message on empty teacher search
- Standardize terminology across the UI
- Move logout to conventional location (top-right)

**Minor improvements (Severity 1):**
- Add Enter key binding to login/signup forms
- Show logged-in user's name on dashboard
- Add custom application window icon
- Add close-window confirmation dialog
