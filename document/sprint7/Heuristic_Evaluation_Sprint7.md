# Heuristic Evaluation Report — Sprint 7

**Project:** Music Course Platform  
**Date:** 2026-04-22  
**Method:** Nielsen's 10 Usability Heuristics  
**Evaluators:** Lu Liu, Su Wai Phyoe, Yicheng Chen, Ying Luo

---

## Nielsen's 10 Heuristics Reference

| #     | Heuristic                             | Description                                                                                                                 |
| ----- | ------------------------------------- | --------------------------------------------------------------------------------------------------------------------------- |
| H1-1  | Simple & Natural Dialog               | Dialogs should not contain irrelevant information; every extra element competes for attention                               |
| H1-2  | Speak the Users' Language             | Use words, phrases, and concepts familiar to the user rather than system-oriented language                                  |
| H1-3  | Minimize Users' Memory Load           | Make objects, actions, and options visible; the user should not have to remember information from one part to another       |
| H1-4  | Consistency                           | Users should not have to wonder whether different words, situations, or actions mean the same thing                         |
| H1-5  | Feedback                              | The system should always keep users informed about what is going on through appropriate feedback                            |
| H1-6  | Clearly Marked Exits                  | Users often choose system functions by mistake and need clearly marked "emergency exit" to leave the unwanted state         |
| H1-7  | Shortcuts                             | Allow frequent users to speed up interaction; the system should cater for both inexperienced and experienced users          |
| H1-8  | Precise & Constructive Error Messages | Error messages should be expressed in plain language, precisely indicate the problem, and constructively suggest a solution |
| H1-9  | Prevent Errors                        | Even better than good error messages is a careful design that prevents a problem from occurring in the first place          |
| H1-10 | Help and Documentation                | Even though it is better if the system can be used without documentation, it may be necessary to provide help               |

**Severity Scale:**

- **0** — Not a usability problem
- **1** — Cosmetic problem only; fix if time permits
- **2** — Minor usability problem; low priority
- **3** — Major usability problem; important to fix
- **4** — Usability catastrophe; imperative to fix

---

# Evaluator 1: Yicheng Chen

**Date:** 2026-04-22  
**Role (during evaluation):** First-time user — learner perspective

---

## Issues Found

### YC-01 — Booking screen presents too much information at once

**Heuristic:** H1-1 — Simple & Natural Dialog  
**Severity:** 2  
**Description:** The student course booking screen (`student_course_booking.fxml`) displays three panels simultaneously: an instrument filter and teacher selector on the left, a calendar in the centre, and an available time slot list on the right. A first-time user is presented with all of these before selecting any teacher, making the initial state visually dense. The "Book a Lesson" button is disabled at this point but the surrounding UI offers no guidance on the required sequence of actions (select instrument → select teacher → select date → select time).  
**Recommendation:** Add a short instructional header above the booking area: "Step 1: Choose an instrument. Step 2: Select a teacher. Step 3: Pick a date and time." Alternatively, reveal the calendar only after a teacher is selected to reduce initial complexity.

---

### YC-02 — Language selector uses flag icons without text labels

**Heuristic:** H1-2 — Speak the Users' Language  
**Severity:** 2  
**Description:** The language ComboBox on the login screen displays country abbreviations (e.g., flag icons or short codes for English, Chinese, Arabic) without accompanying text labels. A user unfamiliar with the regional flags may not immediately understand which option selects their language. This particularly affects Arabic-speaking users who may not recognise the Saudi Arabian flag.  
**Recommendation:** Show the full language name alongside the flag in each ComboBox item: "🇬🇧 English", "🇨🇳 中文", "🇸🇦 العربية". This ensures the selector is self-explanatory regardless of prior knowledge of national flags.

---

### YC-03 — Selected time slot is not visually confirmed before booking

**Heuristic:** H1-3 — Minimize Users' Memory Load  
**Severity:** 2  
**Description:** When a user clicks an available time slot in the right-side list, the selection is stored internally but the visual feedback is subtle — only the "Selected time" label at the bottom of the panel updates. If the user scrolls or looks away, they must mentally recall which slot they chose. There is no persistent highlight on the selected row in the time slot list.  
**Recommendation:** Apply a distinct background colour (e.g., teal highlight matching the app's primary colour) to the selected row in the time slot list, and keep it highlighted until the booking is confirmed or a different slot is selected.

---

### YC-04 — Button styles differ between the login screen and the dashboards

**Heuristic:** H1-4 — Consistency  
**Severity:** 2  
**Description:** On the login and signup screens the primary action buttons ("Log in", "Sign up as Student/Teacher") are solid filled with white text. On the teacher and student dashboards the action buttons ("Save Profile", "Add Time Slot", "Book a Lesson") are outlined or styled differently. This inconsistency means users cannot develop a reliable visual model of "what a clickable primary action looks like."  
**Recommendation:** Define a single primary button style in `styles.css` and apply it consistently to all action buttons across every screen. Reserve outline style for secondary actions.

---

### YC-05 — No loading indicator when the login button is clicked

**Heuristic:** H1-5 — Feedback  
**Severity:** 2  
**Description:** Clicking the "Log in" button triggers a database query. On a slow connection or a busy database, the button appears frozen for 1–2 seconds with no spinner, progress label, or any indication that the system is processing the request. The user cannot distinguish between the app hanging and normal processing.  
**Recommendation:** Disable the login button immediately on click and show a small "Logging in…" label or a `ProgressIndicator` beneath the button. Re-enable the button and hide the indicator once the response is received.

---

### YC-06 — Back button on the login screen does not navigate anywhere

**Heuristic:** H1-6 — Clearly Marked Exits  
**Severity:** 2  
**Description:** The login screen has a "‹" back button in the top-left corner. Clicking it clears the email and password fields but does not navigate to any previous screen. The application has no pre-login landing page, so the back button becomes a confusing dead end. A user who clicked it expecting to return somewhere is left on the same screen with empty fields.  
**Recommendation:** Either remove the back button from the login screen since there is no valid prior screen, or repurpose it as a "clear form" icon with an appropriate tooltip, so its function is understood rather than misinterpreted as navigation.

---

### YC-07 — Pressing Enter on the login form does not submit

**Heuristic:** H1-7 — Shortcuts  
**Severity:** 1  
**Description:** After typing their email and password, most users expect to press Enter to submit the login form. The application does not bind the Enter key to the login action; the user must click the "Log in" button with the mouse. This forces an unnecessary context switch between keyboard and mouse for a very common interaction.  
**Recommendation:** In `LoginController`, add a `setOnKeyPressed` handler to the password field: when `KeyCode.ENTER` is detected, call the same login method as the button. Apply the same pattern to the signup form.

---

### YC-08 — Login error message does not guide the user toward recovery

**Heuristic:** H1-8 — Precise & Constructive Error Messages  
**Severity:** 3  
**Description:** When login fails, the error label displays "Invalid email or password!" This message correctly identifies that something is wrong but provides no guidance on how to fix the problem. A user who has mistyped their email receives the same message as one who has forgotten their password, with no suggestion to check email spelling or that a password reset might exist.  
**Recommendation:** Change the error message to: "Login failed. Please check that your email address is correct and your password is entered correctly." This is more instructive without promising features (such as password reset) that are not yet implemented.

---

### YC-09 — Email field does not validate format before form submission

**Heuristic:** H1-9 — Prevent Errors  
**Severity:** 2  
**Description:** The email field on the login and signup forms accepts any text including invalid formats (e.g., "notanemail", "user@", "@domain.com"). Validation runs only after the "Log in" or "Sign up" button is clicked, which means users waste an entire round-trip to discover a typing mistake they could have been warned about immediately.  
**Recommendation:** Add an `onFocusLost` listener to the email field that validates the format using a simple regex (e.g., `^[^@\s]+@[^@\s]+\.[^@\s]+$`) and shows an inline warning message when the user moves to the next field.

---

### YC-10 — No in-app help or guidance for new users

**Heuristic:** H1-10 — Help and Documentation  
**Severity:** 2  
**Description:** A user launching the application for the first time has no guidance on what the platform does, how to create an account, or what the difference between signing up as a student versus a teacher means. There are no tooltips on any form fields, no FAQ link, and no "How it works" section anywhere in the application.  
**Recommendation:** Add a brief tooltip to the "AS Student" and "AS Teacher" buttons explaining the difference (e.g., "Students browse teachers and book lessons. Teachers set availability and manage bookings."). Add a simple welcome message on the first login describing the main workflow.

---

## Evaluator 1 — Summary Table

| Issue ID | Heuristic | Description                                      | Severity |
| -------- | --------- | ------------------------------------------------ | -------- |
| YC-01    | H1-1      | Booking screen shows too much at once            | 2        |
| YC-02    | H1-2      | Language selector has no text labels             | 2        |
| YC-03    | H1-3      | Selected time slot not persistently highlighted  | 2        |
| YC-04    | H1-4      | Button styles inconsistent across screens        | 2        |
| YC-05    | H1-5      | No loading indicator during login                | 2        |
| YC-06    | H1-6      | Back button on login does not navigate           | 2        |
| YC-07    | H1-7      | Enter key does not submit login form             | 1        |
| YC-08    | H1-8      | Login error message gives no recovery guidance   | 3        |
| YC-09    | H1-9      | Email field validated only on submit, not inline | 2        |
| YC-10    | H1-10     | No in-app help or guidance for new users         | 2        |

---

---

# Evaluator 2: Lu Liu

**Date:** 2026-04-22  
**Role (during evaluation):** Teacher — creating a profile and managing time slot availability

---

## Issues Found

### LL-01 — Teacher dashboard combines profile editing and schedule management in one crowded screen

**Heuristic:** H1-1 — Simple & Natural Dialog  
**Severity:** 2  
**Description:** The teacher dashboard (`teacher_set_availability.fxml`) places the entire teacher profile (instruments, experience, hourly rate, bio, save button) in the left panel alongside a full-month calendar in the centre and a time slot creation form on the right — all on one screen simultaneously. A first-time teacher is presented with all of this before they have even saved their profile, which must be done first for any useful functionality. The screen asks the user to do two distinct things (maintain profile and set availability) at the same time, increasing cognitive load.  
**Recommendation:** Separate the teacher dashboard into two tabs or a two-step onboarding flow: "Step 1: Complete your profile" and "Step 2: Set your availability." This makes the required sequence clear and reduces visual clutter.

---

### LL-02 — Hourly rate label uses a dollar sign regardless of locale

**Heuristic:** H1-2 — Speak the Users' Language  
**Severity:** 2  
**Description:** The teacher profile form labels the pricing field as "Pricing ($/hr)". The platform targets Finnish and international users, where the currency would naturally be euros (€) or another local currency. Showing a dollar sign is confusing and culturally mismatched for non-US teachers. Teachers from Finland would enter their rate in euros but the label implies dollars.  
**Recommendation:** Replace the hardcoded "$/hr" with a localizable string key (e.g., `label.hourly_rate`) and set the displayed currency symbol according to the user's selected locale or a configurable application setting.

---

### LL-03 — Booking history shows only a teacher ID, not the teacher's name

**Heuristic:** H1-3 — Minimize Users' Memory Load  
**Severity:** 2  
**Description:** In the student booking schedule view, the booking cards display the instrument and time slot but reference the teacher using an internal identifier. Students who have booked multiple teachers must remember which ID corresponds to which teacher, which is not a reasonable expectation. This forces users to navigate away to the teacher list and mentally map the ID back to a name.  
**Recommendation:** Join the booking query with the `TEACHERPROFILE` table to retrieve and display the teacher's display name on each booking card. This eliminates the need for the user to recall external information during routine schedule review.

---

### LL-04 — Terminology is inconsistent: "Time Slot" vs "Availability" vs "Schedule"

**Heuristic:** H1-4 — Consistency  
**Severity:** 2  
**Description:** The teacher dashboard uses "Set Availability" as the screen title, "Add Time Slot" as the button label, and "View Schedule" as the navigation button — all referring to overlapping concepts. The student side uses "course booking" and "lesson" for the same entity. Having three different labels for the same concept (a teachable unit of time) forces users to decode whether these refer to the same thing.  
**Recommendation:** Agree on a single canonical term — "Time Slot" is the clearest — and apply it consistently across all screens, navigation labels, and localization keys. Update the FXML labels and all three `messages_*.properties` files accordingly.

---

### LL-05 — No success confirmation after teacher saves their profile

**Heuristic:** H1-5 — Feedback  
**Severity:** 3  
**Description:** When a teacher fills in their profile (instruments, experience, hourly rate, bio) and clicks "Save Profile", the data is written to the database and the form values remain, but no success message is shown. The teacher has no confirmation that the save was successful. This leads to repeated save actions and uncertainty about whether the profile is visible to students.  
**Recommendation:** Display a brief success message (e.g., a green `Label` or an `Alert.INFORMATION` dialog) after a successful save: "Profile saved successfully." The message is already defined in `messages_en.properties` as `"Profile saved successfully!"` — it simply needs to be surfaced in the UI.

---

### LL-06 — No way to cancel mid-edit on the teacher profile without losing state

**Heuristic:** H1-6 — Clearly Marked Exits  
**Severity:** 2  
**Description:** Once a teacher begins editing profile fields (e.g., changes their bio), there is no "Cancel" or "Discard changes" button. If the teacher navigates away using the "View Schedule" button, any unsaved edits are silently discarded with no warning. The only option is to save or lose changes — the user is not informed that navigating away will discard their work.  
**Recommendation:** Add a "Cancel" button next to "Save Profile" that restores all fields to their last saved values. Alternatively, add a confirmation dialog when the user navigates away with unsaved changes: "You have unsaved changes. Do you want to save them before leaving?"

---

### LL-07 — No way to jump to a specific month in the calendar

**Heuristic:** H1-7 — Shortcuts  
**Severity:** 1  
**Description:** The calendar on both the teacher and student dashboards supports only sequential month navigation using "‹" and "›" buttons. A teacher who wants to set availability for a slot three months in the future must click the next-month arrow three times. There is no input field or dropdown to jump directly to a specific month or date.  
**Recommendation:** Add a clickable month/year label in the calendar header that opens a compact month-picker dropdown. This allows experienced users to jump to any month in one interaction rather than clicking through sequentially.

---

### LL-08 — Arabic error messages fall back to English

**Heuristic:** H1-8 — Precise & Constructive Error Messages  
**Severity:** 3  
**Description:** When the application language is set to Arabic, some validation error messages displayed on the signup and login forms are shown in English instead of Arabic. This happens because not all error message keys have Arabic translations in `messages_ar.properties`. A user who selected Arabic because they are more comfortable with it is confronted with English error text, which defeats the purpose of the localisation feature.  
**Recommendation:** Audit all keys in `messages_en.properties` and verify that every one has a corresponding entry in both `messages_ar.properties` and `messages_zh.properties`. Add the missing Arabic translations for all error and validation message keys.

---

### LL-09 — Instrument field does not prevent duplicate or inconsistent entries

**Heuristic:** H1-9 — Prevent Errors  
**Severity:** 2  
**Description:** The instruments dropdown on the teacher profile is populated from a predefined list (Piano, Guitar, Violin, etc.) but the stored value is a canonical lowercase key. If the localisation lookup fails for any reason, an inconsistent value could be stored in the database. More importantly, the instrument filter on the student side depends entirely on key matching — any mismatch between the stored key and the filter dropdown value means the teacher would never appear in search results.  
**Recommendation:** Validate on save that the selected instrument value resolves to a known key in `LocalizationManager.getInstrumentKey()`. Display an inline error if the key cannot be resolved, preventing the teacher from saving an invalid instrument that would make them invisible to students.

---

### LL-10 — No onboarding guidance for first-time teachers

**Heuristic:** H1-10 — Help and Documentation  
**Severity:** 2  
**Description:** A teacher who registers and logs in for the first time is taken directly to the dashboard with no explanation of what to do next. The profile fields have labels but no placeholder text to indicate expected formats (e.g., "Years of experience" — should this be a number? What is the maximum?). There is no tooltip or hint explaining that a completed profile is required before students can find and book the teacher.  
**Recommendation:** Add placeholder text to every teacher profile field (e.g., "E.g. 5" for years of experience, "E.g. 40.00" for hourly rate, "Describe your teaching style and background…" for bio). Add a first-login banner: "Complete your profile to become visible to students."

---

## Evaluator 2 — Summary Table

| Issue ID | Heuristic | Description                                                   | Severity |
| -------- | --------- | ------------------------------------------------------------- | -------- |
| LL-01    | H1-1      | Teacher dashboard has too many elements on one screen         | 2        |
| LL-02    | H1-2      | Hourly rate label shows "$/hr" regardless of locale           | 2        |
| LL-03    | H1-3      | Booking history shows teacher ID instead of teacher name      | 2        |
| LL-04    | H1-4      | Inconsistent terminology: Time Slot / Availability / Schedule | 2        |
| LL-05    | H1-5      | No confirmation after teacher saves profile                   | 3        |
| LL-06    | H1-6      | No cancel or discard option when editing profile              | 2        |
| LL-07    | H1-7      | Calendar has no month-jump shortcut                           | 1        |
| LL-08    | H1-8      | Arabic error messages fall back to English                    | 3        |
| LL-09    | H1-9      | Instrument key validation not enforced on save                | 2        |
| LL-10    | H1-10     | No onboarding or placeholder text for first-time teachers     | 2        |

---

---

# Evaluator 3: Su Wai Phyoe

**Date:** 2026-04-22  
**Role (during evaluation):** New learner — finding a teacher and booking a first lesson

---

## Issues Found

### SW-01 — Signup form is dense with no clear visual grouping

**Heuristic:** H1-1 — Simple & Natural Dialog  
**Severity:** 2  
**Description:** The signup form (`signup.fxml`) places username, email, and password fields vertically followed by two role-selection buttons ("AS Student" and "AS Teacher") at the bottom. There is no visual separation between the identity fields and the role selection, making it unclear to new users that choosing a role is a required and distinct step. A user can easily miss the role buttons and attempt to proceed without selecting one.  
**Recommendation:** Add a visible section divider and a heading above the role buttons: "Choose your account type:" with a brief one-line description under each option. Group identity fields in one card and role selection in another to create a clear two-step visual structure on a single screen.

---

### SW-02 — Password minimum length requirement is hidden until after failure

**Heuristic:** H1-2 — Speak the Users' Language  
**Severity:** 2  
**Description:** The signup form requires a password of at least 6 characters (`SignupController` validation: `password.length() < 6`). However, this requirement is not shown anywhere on the form. A new user who enters a 4-character password is only told about the minimum after clicking the signup button and seeing the error: "Password must be at least 6 characters!" The requirement was always there — the system just did not communicate it upfront.  
**Recommendation:** Add a helper text directly below the password field before the user attempts to submit: "Minimum 6 characters." This sets expectations correctly and prevents the error from occurring in the first place.

---

### SW-03 — No display of the logged-in user's name on the student dashboard

**Heuristic:** H1-3 — Minimize Users' Memory Load  
**Severity:** 1  
**Description:** After a student logs in, the course booking screen (`student_course_booking.fxml`) shows no indication of who is currently logged in. There is a user avatar area in the navigation bar but it does not display the username or any personalisation. A student who uses multiple accounts (e.g., for testing or family use) must remember which account they are currently using or navigate to the schedule view to infer it from their booking history.  
**Recommendation:** Display the logged-in user's username or first name in the navigation bar next to the avatar, using `SessionManager.getCurrentUser().getUsername()`. This is a minor change with significant usability benefit for multi-account scenarios.

---

### SW-04 — Teacher filter has no "clear" or "show all" option

**Heuristic:** H1-4 — Consistency  
**Severity:** 2  
**Description:** The instrument filter ComboBox on the student dashboard allows the user to filter teachers by instrument. However, once an instrument is selected, there is no "All instruments" or "Clear filter" option to return to viewing all available teachers without selecting a specific instrument. The student must restart navigation or select a different instrument. Other filter controls in common applications consistently provide a reset option.  
**Recommendation:** Add "All instruments" as the first (default) item in the instrument filter ComboBox. When selected, display all teachers regardless of instrument. This brings the control in line with standard filter component conventions.

---

### SW-05 — Empty teacher list shows a blank panel with no message

**Heuristic:** H1-5 — Feedback  
**Severity:** 2  
**Description:** When a student selects an instrument for which no teachers are currently registered, the teacher selector ComboBox becomes empty and the teacher profile card shows blank fields. There is no message explaining why the list is empty. The student cannot tell whether no teachers teach that instrument or whether the application has failed to load data.  
**Recommendation:** When the teacher list is empty after applying a filter, display an explicit message in the teacher profile area: "No teachers are currently available for [instrument]. Try a different instrument or check back later." This removes ambiguity between "no data" and "load failure."

---

### SW-06 — Cancellation of a booked lesson has no confirmation step

**Heuristic:** H1-6 — Clearly Marked Exits  
**Severity:** 2  
**Description:** In the student schedule view (`student_schedule_view.fxml`), each booking card has a delete button that cancels the booking immediately when clicked. There is no confirmation dialog asking the user to confirm the cancellation. A misclick permanently removes the booking and releases the time slot — an action that may be very difficult to recover from if the teacher's slot is quickly rebooked by someone else.  
**Recommendation:** Add a confirmation `Alert.CONFIRMATION` dialog before processing cancellation: "Are you sure you want to cancel your lesson on [date] at [time] with [teacher name]? This action cannot be undone." Proceed with cancellation only if the user confirms.

---

### SW-07 — No keyboard navigation between calendar days

**Heuristic:** H1-7 — Shortcuts  
**Severity:** 1  
**Description:** The calendar on the booking screen is implemented as a grid of clickable buttons. There is no keyboard navigation between calendar cells using arrow keys, nor can the user Tab through the available dates. Users who prefer keyboard navigation — or users with limited mouse precision — cannot efficiently select a date without a mouse click.  
**Recommendation:** Add arrow key navigation to the calendar grid so that pressing the right/left/up/down arrow keys moves the focus to adjacent calendar cells. Pressing Enter or Space should select the focused date. This is a standard accessibility expectation for calendar widgets.

---

### SW-08 — Signup error message appears at the bottom and does not indicate which field is invalid

**Heuristic:** H1-8 — Precise & Constructive Error Messages  
**Severity:** 3  
**Description:** When signup validation fails (e.g., empty fields, invalid email, short password), the error message is shown in a single label at the bottom of the form. The specific field that triggered the error is not highlighted. A user who filled in three of four fields correctly cannot immediately see which field needs correction and must read the error text carefully to determine what to fix.  
**Recommendation:** Display field-specific inline error messages directly below each invalid field (e.g., "Email is required" below the email field, "Minimum 6 characters" below the password field). Highlight the invalid field border in red. This is standard form validation UX that eliminates guesswork.

---

### SW-09 — Date must be selected before the time slot form appears — not communicated

**Heuristic:** H1-9 — Prevent Errors  
**Severity:** 2  
**Description:** On the student booking screen, the time slot list on the right is empty until the user clicks a date on the calendar. However, there is no visual cue or instruction telling the user to click a date first. A new user who looks at the right panel sees a blank area and may assume there are no available lessons or that the application is broken, rather than understanding that selecting a calendar date is the required prerequisite step.  
**Recommendation:** Show a placeholder message in the time slot panel: "← Select a date on the calendar to see available time slots." Replace this message with the actual list once a date is selected. This makes the dependency between the calendar and the time slot list explicit.

---

### SW-10 — Teacher profile card shows no guidance on what "hourly rate" means

**Heuristic:** H1-10 — Help and Documentation  
**Severity:** 1  
**Description:** The teacher profile card displayed to students shows fields including the teacher's hourly rate. However, there is no tooltip or contextual explanation of whether this is the rate for a full hour, a 30-minute session, or the rate per lesson. Students comparing multiple teachers cannot be certain they are comparing equivalent values.  
**Recommendation:** Add a small tooltip or parenthetical label next to the rate display: "Rate per hour." This is a one-word addition that eliminates a genuine source of ambiguity for users making a financial decision.

---

## Evaluator 3 — Summary Table

| Issue ID | Heuristic | Description                                                      | Severity |
| -------- | --------- | ---------------------------------------------------------------- | -------- |
| SW-01    | H1-1      | Signup form has no clear visual grouping between fields and role | 2        |
| SW-02    | H1-2      | Password minimum length not shown before form submission         | 2        |
| SW-03    | H1-3      | Logged-in student's name not shown on dashboard                  | 1        |
| SW-04    | H1-4      | Instrument filter has no "show all" / reset option               | 2        |
| SW-05    | H1-5      | Empty teacher list shows blank panel with no explanation         | 2        |
| SW-06    | H1-6      | Booking cancellation has no confirmation dialog                  | 2        |
| SW-07    | H1-7      | Calendar has no keyboard navigation                              | 1        |
| SW-08    | H1-8      | Signup error at the bottom does not highlight the invalid field  | 3        |
| SW-09    | H1-9      | No instruction to select a date before time slots appear         | 2        |
| SW-10    | H1-10     | No tooltip explaining what the hourly rate covers                | 1        |

---

---

# Evaluator 4: Ying Luo

**Date:** 2026-04-22  
**Role (during evaluation):** Teacher — reviewing and managing student bookings from the schedule view

---

## Issues Found

### YL-01 — Teacher schedule view shows all time slots as a flat card list with no date grouping

**Heuristic:** H1-1 — Simple & Natural Dialog  
**Severity:** 3  
**Description:** The teacher schedule view (`teacher_schedule_view.fxml`) displays all time slots as a flow of cards with no grouping, sorting, or filtering by date. A teacher with many bookings across multiple weeks must visually scan through all cards to find a specific upcoming lesson. There is no summary section showing "this week" or "next 3 bookings." The flat presentation makes the screen progressively harder to use as the number of slots grows.  
**Recommendation:** Group time slot cards by date (show a date header above each group of slots for that day). Add a filter toggle to show only upcoming slots, only booked slots, or all slots. Display a "Next lesson" summary card at the top of the screen for quick reference.

---

### YL-02 — Time format varies between calendar and schedule card display

**Heuristic:** H1-2 — Speak the Users' Language  
**Severity:** 2  
**Description:** Start and end times in the time slot creation dropdowns use 12-hour format ("7:00 AM", "2:30 PM") but the booking cards in the teacher schedule view display times in locale-dependent format. For Arabic locale, time formatting is applied via `LocalizationManager.createDateFormatter()`, but the start and end times stored in the database are plain time strings without explicit locale formatting. This can result in inconsistent time display depending on which part of the UI is being viewed.  
**Recommendation:** Centralise all time display through a single `formatTime(LocalTime, Locale)` utility method in `LocalizationManager`, and apply it consistently to every location where a time value is rendered in the UI.

---

### YL-03 — Schedule cards do not show the student's name for booked slots

**Heuristic:** H1-3 — Minimize Users' Memory Load  
**Severity:** 2  
**Description:** In the teacher schedule view, time slot cards show the date, time, and status (available/booked) but do not display the student's name for booked slots. A teacher who wants to prepare for an upcoming lesson must remember or look up who booked each slot. The schedule view as a planning tool is significantly less useful without the identity of the student.  
**Recommendation:** For time slots with status BOOKED, join the `BOOKING` and `LEARNERPROFILE` tables to retrieve the learner's username and display it on the schedule card: "Booked by: [student name]."

---

### YL-04 — Logout button placement is inconsistent across screens

**Heuristic:** H1-4 — Consistency  
**Severity:** 2  
**Description:** The logout button appears in the navigation bar at the top of all screens, which is consistent. However, its visual weight and exact positioning within the navbar differs: on the teacher dashboard it appears further left than on the teacher schedule view. Users who navigate between screens build a muscle-memory expectation of where the logout button is. Inconsistent placement requires re-scanning the interface after every screen transition.  
**Recommendation:** Fix the logout button to a consistent position — conventionally the top-right corner — across every FXML file. Define its position as part of a reusable navigation bar component or establish a CSS class that always places it last in the navbar's HBox layout.

---

### YL-05 — No visual distinction between past and upcoming time slots

**Heuristic:** H1-5 — Feedback  
**Severity:** 2  
**Description:** The teacher schedule view displays all time slots with the same visual style regardless of whether the lesson has already passed or is upcoming. A teacher reviewing their schedule must check the date on each card individually to determine which lessons are in the future. Past lessons are effectively clutter that obscures upcoming ones.  
**Recommendation:** Apply a greyed-out style (reduced opacity or grey border) to time slot cards whose `lesson_date` is before today's date. Bold or highlight upcoming slots. Add filter tabs at the top of the schedule: "Upcoming" and "Past." This provides immediate visual feedback about the relevance of each entry.

---

### YL-06 — Time slot deletion has no confirmation dialog

**Heuristic:** H1-6 — Clearly Marked Exits  
**Severity:** 3  
**Description:** On the teacher dashboard, clicking the delete button next to an available time slot removes it immediately from the database with no confirmation prompt. If the teacher clicks delete by accident, the time slot is gone with no way to undo the action within the application. The only protection in place is that booked slots cannot be deleted, but available slots — which the teacher may have carefully planned — can be lost with a single misclick.  
**Recommendation:** Add an `Alert.CONFIRMATION` dialog before processing deletion: "Delete the time slot on [date] from [start time] to [end time]? This cannot be undone." Only call `TimeSlotService.deleteSlot()` if the user confirms. This is especially important since the teacher may have already communicated the availability to students outside the app.

---

### YL-07 — No quick way to add multiple consecutive time slots

**Heuristic:** H1-7 — Shortcuts  
**Severity:** 1  
**Description:** A teacher who wants to set availability for a full teaching day must add each time slot individually: click a date, select a start time, select an end time, click "Add Time Slot," and repeat for every slot. A teacher with 6 available slots on a Saturday must go through this sequence 6 times with no ability to batch-create slots or copy a day's availability to another day.  
**Recommendation:** Add a "Repeat on" option that allows the teacher to add the same time slot on multiple selected dates at once. Even a simple "Apply same slots to next week" button would significantly reduce repetitive clicking for regular teachers.

---

### YL-08 — Time slot validation error does not auto-clear when the user corrects the input

**Heuristic:** H1-8 — Precise & Constructive Error Messages  
**Severity:** 2  
**Description:** When a teacher selects a start time that is equal to or later than the end time and clicks "Add Time Slot", the error message "Start time must be before end time!" is displayed. However, when the teacher then corrects the times using the dropdowns, the error message remains visible on screen until the "Add Time Slot" button is clicked again. A persistent error message for a problem that has already been corrected creates confusion about the current validity state of the form.  
**Recommendation:** Add a `ChangeListener` to both the start time and end time ComboBoxes. When either value changes, re-validate the combination and clear the error message immediately if the new selection is valid. This gives real-time feedback that the issue has been resolved.

---

### YL-09 — No confirmation before navigating away from unsaved teacher profile changes

**Heuristic:** H1-9 — Prevent Errors  
**Severity:** 2  
**Description:** A teacher who partially updates their profile (e.g., changes their hourly rate) and then clicks "View Schedule" in the navigation bar will lose all unsaved changes without any warning. The navigation proceeds immediately. The teacher may not realise the changes were not saved until they return to the profile and find the old values.  
**Recommendation:** Track a `isDirty` boolean flag in `TeacherDashboardController` that becomes `true` whenever a profile field is modified. Before any navigation action (View Schedule, logout), check this flag and show a confirmation dialog: "You have unsaved profile changes. Save before leaving?" with "Save and continue" / "Discard" options.

---

### YL-10 — No explanation of the difference between available and booked slot colours

**Heuristic:** H1-10 — Help and Documentation  
**Severity:** 1  
**Description:** The teacher schedule view uses colour coding to distinguish time slot states: available slots appear in green and booked slots in red. While this colour choice is intuitive, there is no legend anywhere in the UI explaining what the colours mean. A new teacher seeing a screen of red cards may interpret this as an error state rather than understanding it means those lessons are successfully booked.  
**Recommendation:** Add a small colour legend near the top of the schedule view: "● Green — Available ● Red — Booked." This is a one-line addition that removes all ambiguity about the colour scheme and prevents misinterpretation of a red card as a system error.

---

## Evaluator 4 — Summary Table

| Issue ID | Heuristic | Description                                                   | Severity |
| -------- | --------- | ------------------------------------------------------------- | -------- |
| YL-01    | H1-1      | Schedule view shows all slots as flat list with no grouping   | 3        |
| YL-02    | H1-2      | Time format varies between calendar and schedule card display | 2        |
| YL-03    | H1-3      | Schedule card does not show student name for booked slots     | 2        |
| YL-04    | H1-4      | Logout button placement inconsistent across screens           | 2        |
| YL-05    | H1-5      | No visual distinction between past and upcoming slots         | 2        |
| YL-06    | H1-6      | Time slot deletion has no confirmation dialog                 | 3        |
| YL-07    | H1-7      | No batch time slot creation for multiple dates                | 1        |
| YL-08    | H1-8      | Time validation error does not auto-clear after correction    | 2        |
| YL-09    | H1-9      | No warning when navigating away from unsaved profile changes  | 2        |
| YL-10    | H1-10     | No legend explaining the colour coding on schedule view       | 1        |

---

---

# Team Consolidated Summary Table

All 40 issues from all 4 evaluators, sorted by severity (highest first):

| Issue ID | Evaluator    | Heuristic | Description                                                   | Severity |
| -------- | ------------ | --------- | ------------------------------------------------------------- | -------- |
| YC-08    | Yicheng Chen | H1-8      | Login error gives no recovery guidance                        | 3        |
| LL-05    | Lu Liu       | H1-5      | No confirmation after teacher saves profile                   | 3        |
| LL-08    | Lu Liu       | H1-8      | Arabic error messages fall back to English                    | 3        |
| SW-08    | Su Wai Phyoe | H1-8      | Signup error does not highlight the invalid field             | 3        |
| YL-01    | Ying Luo     | H1-1      | Schedule view flat list with no date grouping                 | 3        |
| YL-06    | Ying Luo     | H1-6      | Time slot deletion has no confirmation dialog                 | 3        |
| YC-01    | Yicheng Chen | H1-1      | Booking screen shows too much information at once             | 2        |
| YC-02    | Yicheng Chen | H1-2      | Language selector has no text labels next to flags            | 2        |
| YC-03    | Yicheng Chen | H1-3      | Selected time slot not persistently highlighted               | 2        |
| YC-04    | Yicheng Chen | H1-4      | Button styles inconsistent across screens                     | 2        |
| YC-05    | Yicheng Chen | H1-5      | No loading indicator during login                             | 2        |
| YC-06    | Yicheng Chen | H1-6      | Back button on login does not navigate anywhere               | 2        |
| YC-09    | Yicheng Chen | H1-9      | Email field validated only on submit, not inline              | 2        |
| YC-10    | Yicheng Chen | H1-10     | No in-app help or guidance for new users                      | 2        |
| LL-01    | Lu Liu       | H1-1      | Teacher dashboard has too many elements on one screen         | 2        |
| LL-02    | Lu Liu       | H1-2      | Hourly rate label shows "$/hr" regardless of locale           | 2        |
| LL-03    | Lu Liu       | H1-3      | Booking history shows teacher ID instead of name              | 2        |
| LL-04    | Lu Liu       | H1-4      | Inconsistent terminology across screens                       | 2        |
| LL-06    | Lu Liu       | H1-6      | No cancel option when editing teacher profile                 | 2        |
| LL-09    | Lu Liu       | H1-9      | Instrument key validation not enforced on save                | 2        |
| LL-10    | Lu Liu       | H1-10     | No onboarding for first-time teachers                         | 2        |
| SW-01    | Su Wai Phyoe | H1-1      | Signup form has no clear visual grouping                      | 2        |
| SW-02    | Su Wai Phyoe | H1-2      | Password minimum length not shown before submission           | 2        |
| SW-04    | Su Wai Phyoe | H1-4      | Instrument filter has no "show all" reset option              | 2        |
| SW-05    | Su Wai Phyoe | H1-5      | Empty teacher list shows blank panel with no message          | 2        |
| SW-06    | Su Wai Phyoe | H1-6      | Booking cancellation has no confirmation dialog               | 2        |
| SW-09    | Su Wai Phyoe | H1-9      | No instruction to select a date before time slots appear      | 2        |
| YL-02    | Ying Luo     | H1-2      | Time format varies between calendar and schedule card display | 2        |
| YL-03    | Ying Luo     | H1-3      | Schedule card does not show student name for booked slots     | 2        |
| YL-04    | Ying Luo     | H1-4      | Logout button placement inconsistent across screens           | 2        |
| YL-05    | Ying Luo     | H1-5      | No visual distinction between past and upcoming slots         | 2        |
| YL-08    | Ying Luo     | H1-8      | Time validation error does not auto-clear after correction    | 2        |
| YL-09    | Ying Luo     | H1-9      | No warning when navigating away from unsaved profile changes  | 2        |
| YC-07    | Yicheng Chen | H1-7      | Enter key does not submit the login form                      | 1        |
| LL-07    | Lu Liu       | H1-7      | Calendar has no month-jump shortcut                           | 1        |
| SW-03    | Su Wai Phyoe | H1-3      | Logged-in student's name not shown on dashboard               | 1        |
| SW-07    | Su Wai Phyoe | H1-7      | Calendar has no keyboard navigation                           | 1        |
| SW-10    | Su Wai Phyoe | H1-10     | No tooltip explaining what the hourly rate covers             | 1        |
| YL-07    | Ying Luo     | H1-7      | No batch time slot creation for multiple dates                | 1        |
| YL-10    | Ying Luo     | H1-10     | No legend for colour coding on the schedule view              | 1        |

---

## Issues by Heuristic

| Heuristic                            | # Issues | Highest Severity |
| ------------------------------------ | -------- | ---------------- |
| H1-1 — Simple & Natural Dialog       | 4        | 3                |
| H1-2 — Speak the Users' Language     | 4        | 2                |
| H1-3 — Minimize Users' Memory Load   | 4        | 2                |
| H1-4 — Consistency                   | 4        | 2                |
| H1-5 — Feedback                      | 4        | 3                |
| H1-6 — Clearly Marked Exits          | 4        | 3                |
| H1-7 — Shortcuts                     | 4        | 1                |
| H1-8 — Precise & Constructive Errors | 4        | 3                |
| H1-9 — Prevent Errors                | 4        | 2                |
| H1-10 — Help and Documentation       | 4        | 2                |

---

## Priority Recommendations

**Fix immediately (Severity 3):**

1. **H1-8** — Improve login and signup error messages to identify specific invalid fields and suggest corrective actions
2. **H1-5** — Add success confirmation after teacher profile save; add loading indicator during login
3. **H1-8** — Complete Arabic translations for all error and validation message keys
4. **H1-6** — Add confirmation dialog before time slot deletion (irreversible action)
5. **H1-1** — Restructure teacher schedule view to group cards by date and filter upcoming vs past slots

**Fix when time permits (Severity 2):**

- Add persistent highlight to selected time slot (H1-3)
- Add "show all" reset option to instrument filter (H1-4)
- Standardise button styles and terminology across all screens (H1-4)
- Show explicit "no results" message when teacher filter finds nothing (H1-5)
- Add confirmation dialog before booking cancellation (H1-6)
- Show student name on booked schedule cards for teacher view (H1-3)
- Validate email format inline on focus-lost (H1-9)
- Add warning when navigating away from unsaved profile changes (H1-9)

**Minor improvements (Severity 1):**

- Bind Enter key to submit on login and signup forms (H1-7)
- Display logged-in username on student dashboard header (H1-3)
- Add colour legend to teacher schedule view (H1-10)
- Add tooltips to key fields (hourly rate, role selection) (H1-10)

---

## Phase 4: Team Debriefing

**Date:** 2026-04-22  
**Participants:** Lu Liu, Su Wai Phyoe, Yicheng Chen, Ying Luo

Following individual evaluations, the team aggregated all findings and held a debriefing discussion.

### Key Findings Agreed Upon

- **H1-8 (Error Messages)** and **H1-5 (Feedback)** are the most consistently violated heuristics across all four evaluators. The system frequently performs actions without confirming success or failure to the user, and error messages do not guide users toward a solution.
- **H1-6 (Clearly Marked Exits)** raised two severity-3 issues: time slot deletion with no confirmation and booking cancellation with no confirmation. Both are irreversible operations that currently offer no safety net.
- **H1-1 (Simple & Natural Dialog)** was flagged by all four evaluators. The most critical instance is the teacher schedule view, which presents all time slots as an undifferentiated flat list — an issue that will worsen as real usage generates more data.
- **H1-7 (Shortcuts)** violations are all severity 1 — the application is functional without shortcuts, but the absence of keyboard bindings on forms is a notable gap compared to standard desktop application expectations.

### Issues Addressed During Sprint 7

Two bugs identified through manual testing and UAT (related to H1-5 — Feedback) were resolved during Sprint 7:

- **BUG-007** — `BookingViewController` now shows an `Alert` dialog after a learner successfully books a lesson, providing the missing system status feedback
- **BUG-008** — `TeacherProfileViewController` now displays an informational message instead of blank fields when no teacher profile exists, preventing silent failure

### Issues Deferred to Future Sprints

The remaining severity-3 issues (error message improvements, deletion confirmations, Arabic translation gaps, schedule view restructuring) are documented as known gaps. The team agreed these are the highest-priority usability improvements to address if time permits before final delivery.
