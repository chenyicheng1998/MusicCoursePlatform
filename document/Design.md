# Music Course Platform - System Design

## Overview
The Music Course Platform is a desktop application built with **JavaFX** that connects music teachers with learners. Teachers can create profiles, set availability, and manage bookings, while learners can search for teachers by instrument and book lessons. The system uses **MariaDB** for database management with JDBC for database interaction, and implements secure authentication with password hashing.

---

## Project Architecture

### Technology Stack
- **Frontend**: JavaFX 17+ with FXML
- **Backend**: Java 17+
- **Database**: MariaDB 10.6+
- **Build Tool**: Apache Maven 3.6+
- **Testing**: JUnit 5, Mockito
- **Code Coverage**: JaCoCo
- **CI/CD**: Jenkins
- **Containerization**: Docker
- **Security**: BCrypt password hashing

---

## Database Design

### 1. **Entities**

#### USERS
**Description**: Represents all registered users in the system, including both teachers and learners. Stores login information and user type.

**Key Attributes**:
- `user_id` (Primary Key, INT, AUTO_INCREMENT)
- `username` (VARCHAR(50), UNIQUE, NOT NULL)
- `password_hash` (VARCHAR(255), NOT NULL)
- `email` (VARCHAR(100), UNIQUE, NOT NULL)
- `user_type` (ENUM('TEACHER', 'LEARNER'), NOT NULL)
- `created_at` (TIMESTAMP, DEFAULT CURRENT_TIMESTAMP)

#### TEACHERPROFILE
**Description**: Stores detailed information about teachers, including teaching skills, instruments taught, experience, hourly rate, and location. Each teacher corresponds to exactly one USERS account.

**Key Attributes**:
- `teacher_profile_id` (Primary Key, INT, AUTO_INCREMENT)
- `user_id` (Foreign Key, INT, UNIQUE, NOT NULL)
- `biography` (TEXT)
- `instruments_taught` (VARCHAR(255), NOT NULL)
- `years_experience` (INT, DEFAULT 0)
- `hourly_rate` (INT, DEFAULT 0)
- `location` (VARCHAR(100))
- `created_at` (DATE)
- `updated_at` (DATE)

#### LEARNERPROFILE
**Description**: Stores detailed information about learners (students or adults), including the instrument they want to learn. Each learner corresponds to exactly one USERS account.

**Key Attributes**:
- `learner_profile_id` (Primary Key, INT, AUTO_INCREMENT)
- `user_id` (Foreign Key, INT, UNIQUE, NOT NULL)
- `instrument` (VARCHAR(50), NOT NULL)
- `created_at` (DATE)
- `updated_at` (DATE)

#### TIMESLOT
**Description**: Represents the time slots published by teachers for lessons. Each time slot is linked to a specific teacher and can be booked by learners.

**Key Attributes**:
- `slot_id` (Primary Key, INT, AUTO_INCREMENT)
- `teacher_profile_id` (Foreign Key, INT, NOT NULL)
- `lesson_date` (DATE, NOT NULL)
- `start_time` (VARCHAR(10), NOT NULL)
- `end_time` (VARCHAR(10), NOT NULL)
- `slot_status` (ENUM('AVAILABLE', 'BOOKED'), DEFAULT 'AVAILABLE')
- `created_at` (DATE)

#### BOOKING
**Description**: Represents lesson bookings by learners for specific time slots. Linked to both the learner and the selected time slot.

**Key Attributes**:
- `booking_id` (Primary Key, INT, AUTO_INCREMENT)
- `slot_id` (Foreign Key, INT, NOT NULL)
- `learner_profile_id` (Foreign Key, INT, NOT NULL)
- `booking_date` (DATE)
- `booking_status` (ENUM('PENDING', 'CONFIRMED', 'CANCELLED'), DEFAULT 'PENDING')
- `notes` (TEXT)
- `created_at` (DATE)
- `updated_at` (DATE)

---

### 2. **Relationships**

#### USERS → TEACHERPROFILE
- **Type**: 1:1
- **Description**: Each teacher user has exactly one teacher profile.
- **Implementation**: `teacher_profile.user_id` references `users.user_id` with UNIQUE constraint

#### USERS → LEARNERPROFILE
- **Type**: 1:1
- **Description**: Each learner user has exactly one learner profile.
- **Implementation**: `learner_profile.user_id` references `users.user_id` with UNIQUE constraint

#### TEACHERPROFILE → TIMESLOT
- **Type**: 1:N
- **Description**: One teacher can create multiple lesson time slots.
- **Implementation**: `timeslot.teacher_profile_id` references `teacher_profile.teacher_profile_id`

#### LEARNERPROFILE → BOOKING
- **Type**: 1:N
- **Description**: One learner can book multiple lessons.
- **Implementation**: `booking.learner_profile_id` references `learner_profile.learner_profile_id`

#### TIMESLOT → BOOKING
- **Type**: 1:1
- **Description**: A booked time slot corresponds to exactly one booking record. Once a slot is booked, it becomes unavailable for others. This ensures no double-booking occurs for the same time slot.
- **Implementation**: `booking.slot_id` references `timeslot.slot_id`

---

### 3. **Database Schema**

```sql
-- Create database
CREATE DATABASE IF NOT EXISTS music_course_platform;
USE music_course_platform;

-- Users table
CREATE TABLE users (
    user_id INT AUTO_INCREMENT PRIMARY KEY,
    username VARCHAR(50) UNIQUE NOT NULL,
    password_hash VARCHAR(255) NOT NULL,
    email VARCHAR(100) UNIQUE NOT NULL,
    user_type ENUM('TEACHER', 'LEARNER') NOT NULL,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP
);

-- Teacher profile table
CREATE TABLE teacher_profile (
    teacher_profile_id INT AUTO_INCREMENT PRIMARY KEY,
    user_id INT UNIQUE NOT NULL,
    biography TEXT,
    instruments_taught VARCHAR(255) NOT NULL,
    years_experience INT DEFAULT 0,
    hourly_rate INT DEFAULT 0,
    location VARCHAR(100),
    created_at DATE,
    updated_at DATE,
    FOREIGN KEY (user_id) REFERENCES users(user_id) ON DELETE CASCADE
);

-- Learner profile table
CREATE TABLE learner_profile (
    learner_profile_id INT AUTO_INCREMENT PRIMARY KEY,
    user_id INT UNIQUE NOT NULL,
    instrument VARCHAR(50) NOT NULL,
    created_at DATE,
    updated_at DATE,
    FOREIGN KEY (user_id) REFERENCES users(user_id) ON DELETE CASCADE
);

-- Time slot table
CREATE TABLE timeslot (
    slot_id INT AUTO_INCREMENT PRIMARY KEY,
    teacher_profile_id INT NOT NULL,
    lesson_date DATE NOT NULL,
    start_time VARCHAR(10) NOT NULL,
    end_time VARCHAR(10) NOT NULL,
    slot_status ENUM('AVAILABLE', 'BOOKED') DEFAULT 'AVAILABLE',
    created_at DATE,
    FOREIGN KEY (teacher_profile_id) REFERENCES teacher_profile(teacher_profile_id) ON DELETE CASCADE
);

-- Booking table
CREATE TABLE booking (
    booking_id INT AUTO_INCREMENT PRIMARY KEY,
    slot_id INT NOT NULL,
    learner_profile_id INT NOT NULL,
    booking_date DATE,
    booking_status ENUM('PENDING', 'CONFIRMED', 'CANCELLED') DEFAULT 'PENDING',
    notes TEXT,
    created_at DATE,
    updated_at DATE,
    FOREIGN KEY (slot_id) REFERENCES timeslot(slot_id) ON DELETE CASCADE,
    FOREIGN KEY (learner_profile_id) REFERENCES learner_profile(learner_profile_id) ON DELETE CASCADE
);
```

---

### 4. **Sample Data**

#### USERS
| user_id | username | password_hash | email | user_type | created_at |
|---------|----------|---------------|-------|-----------|------------|
| 1 | alice | $2a$10$... | alice@mail.com | TEACHER | 2026-02-20 |
| 2 | bob | $2a$10$... | bob@mail.com | LEARNER | 2026-02-20 |
| 3 | carol | $2a$10$... | carol@mail.com | TEACHER | 2026-02-21 |
| 4 | david | $2a$10$... | david@mail.com | LEARNER | 2026-02-21 |

#### TEACHERPROFILE
| teacher_profile_id | user_id | biography | instruments_taught | years_experience | hourly_rate | location | created_at | updated_at |
|-------------------|---------|-----------|-------------------|------------------|-------------|----------|------------|------------|
| 1 | 1 | Northwestern Newcastle University Professor | Piano | 10 | 50 | Helsinki | 2026-02-20 | 2026-02-20 |
| 2 | 3 | 12 years of experience teaching guitar | Guitar | 5 | 40 | Espoo | 2026-02-21 | 2026-02-21 |

#### LEARNERPROFILE
| learner_profile_id | user_id | instrument | created_at | updated_at |
|-------------------|---------|------------|------------|------------|
| 1 | 2 | Piano | 2026-02-20 | 2026-02-20 |
| 2 | 4 | Guitar | 2026-02-21 | 2026-02-21 |

#### TIMESLOT
| slot_id | teacher_profile_id | lesson_date | start_time | end_time | slot_status | created_at |
|---------|-------------------|-------------|------------|----------|-------------|------------|
| 1 | 1 | 2026-02-25 | 09:00 | 10:00 | AVAILABLE | 2026-02-20 |
| 2 | 1 | 2026-02-25 | 10:00 | 11:00 | BOOKED | 2026-02-20 |
| 3 | 2 | 2026-02-26 | 14:00 | 15:00 | AVAILABLE | 2026-02-21 |

#### BOOKING
| booking_id | slot_id | learner_profile_id | booking_date | booking_status | notes | created_at | updated_at |
|------------|---------|-------------------|--------------|----------------|-------|------------|------------|
| 1 | 2 | 1 | 2026-02-20 | CONFIRMED | NULL | 2026-02-20 | 2026-02-20 |
| 2 | 3 | 2 | 2026-02-21 | CONFIRMED | First lesson | 2026-02-21 | 2026-02-21 |

---

## Application Design

### 1. **Features and Functionalities**

#### Authentication
- **Registration**: New users can create accounts as either teachers or learners
- **Login**: Email-based authentication with BCrypt password hashing
- **Session Management**: Maintains user session throughout the application

#### User Profile Management
- **Teacher Profile**: Manage biography, instruments, experience, rate, and location
- **Learner Profile**: Select instrument of interest
- **Profile Updates**: Real-time updates with validation

#### Teacher Availability Management
- **Calendar View**: Interactive monthly calendar for viewing and managing time slots
- **Time Slot Creation**: Add available slots with date, start time, and end time
- **Time Slot Deletion**: Remove unwanted availability
- **Status Tracking**: Visual indication of available vs. booked slots

#### Teacher Search and Discovery
- **Instrument-Based Search**: Filter teachers by instrument taught
- **Teacher List View**: Display all matching teachers with detailed information
- **Profile Viewing**: View complete teacher profiles including bio, experience, and rate

#### Booking System
- **Available Slots View**: Calendar-based display of teacher availability
- **Slot Selection**: Interactive slot selection with visual feedback
- **Booking Creation**: Create bookings with optional notes
- **Booking Management**: View, confirm, and cancel bookings
- **Double-Booking Prevention**: Automatic status updates to prevent conflicts

#### Multi-Language Support
- **Language Selection**: Support for English, German, and Chinese
- **Real-time Language Switching**: Dynamic UI updates without restart
- **Localized Content**: Date formats, labels, and messages in selected language

---

### 2. **JavaFX User Interface Design**

#### Screens

##### 1. Login Screen (`login.fxml`)
- **Fields**: Email, Password
- **Buttons**: Login, Go to Signup
- **Features**: Input validation, error messages

##### 2. Signup Screen (`signup.fxml`)
- **Fields**: Username, Email, Password
- **Buttons**: Register as Student, Register as Teacher, Go to Login
- **Features**: Email validation, password strength requirements

##### 3. Teacher Dashboard (`teacher_schedule_view.fxml`)
- **Components**:
  - Profile section: Name, instruments, experience, pricing, biography
  - Calendar view: Monthly calendar with interactive date selection
  - Time slot management: Start/end time selectors, add/delete buttons
  - Available slots list: Display all slots for selected date
  - Language selector: EN, DE, ZH options
- **Features**: 
  - Profile updates with real-time saving
  - Visual calendar navigation (previous/next month)
  - Time slot creation and deletion
  - Booking overview

##### 4. Student Dashboard (`student_course_booking.fxml`)
- **Components**:
  - Search section: Instrument filter, teacher dropdown
  - Teacher profile display: Bio, experience, rate, instruments
  - Calendar view: Monthly calendar showing teacher availability
  - Time slot selection: Interactive slot buttons
  - Booking confirmation: Notes field, book button
  - Language selector: EN, DE, ZH options
- **Features**:
  - Dynamic teacher search and filtering
  - Visual availability calendar
  - Slot selection with feedback
  - Booking notes input

##### 5. Booking View Screen (`student_schedule_view.fxml`)
- **Components**:
  - User info: Display student name
  - Bookings grid: Card-based layout of all bookings
  - Booking cards: Teacher, instrument, date, time, status, notes
  - Action buttons: Cancel booking
  - Language selector: EN, DE, ZH options
- **Features**:
  - Real-time booking status updates
  - Booking cancellation with confirmation
  - Visual status indicators

---

### 3. **Java Class Design**

#### Model Layer (`model` package)

```java
public class User {
    private int userId;
    private String username;
    private String passwordHash;
    private String email;
    private String userType; // "TEACHER" or "LEARNER"
    private LocalDateTime createdAt;
    
    // Constructors, Getters, Setters
    public boolean isTeacher() { return "TEACHER".equals(userType); }
    public boolean isLearner() { return "LEARNER".equals(userType); }
}

public class TeacherProfile {
    private int teacherProfileId;
    private String biography;
    private String instrumentsTaught;
    private int yearsExperience;
    private int hourlyRate;
    private String location;
    private LocalDate createdAt;
    private LocalDate updatedAt;
    private int userId;
    
    // Constructors, Getters, Setters
}

public class LearnerProfile {
    private int learnerProfileId;
    private String instrument;
    private LocalDate createdAt;
    private LocalDate updatedAt;
    private int userId;
    
    // Constructors, Getters, Setters
}

public class TimeSlot {
    public static final String STATUS_AVAILABLE = "AVAILABLE";
    public static final String STATUS_BOOKED = "BOOKED";
    
    private int slotId;
    private LocalDate lessonDate;
    private String startTime;
    private String endTime;
    private String slotStatus;
    private LocalDate createdAt;
    private int teacherProfileId;
    
    // Constructors, Getters, Setters
    public boolean isAvailable() { return STATUS_AVAILABLE.equals(slotStatus); }
    public boolean isBooked() { return STATUS_BOOKED.equals(slotStatus); }
}

public class Booking {
    public static final String STATUS_PENDING = "PENDING";
    public static final String STATUS_CONFIRMED = "CONFIRMED";
    public static final String STATUS_CANCELLED = "CANCELLED";
    
    private int bookingId;
    private LocalDate bookingDate;
    private String bookingStatus;
    private String notes;
    private LocalDate createdAt;
    private LocalDate updatedAt;
    private int learnerProfileId;
    private int slotId;
    
    // Constructors, Getters, Setters
    public boolean isPending() { return STATUS_PENDING.equals(bookingStatus); }
    public boolean isConfirmed() { return STATUS_CONFIRMED.equals(bookingStatus); }
    public boolean isCancelled() { return STATUS_CANCELLED.equals(bookingStatus); }
    public void confirm() { this.bookingStatus = STATUS_CONFIRMED; }
    public void cancel() { this.bookingStatus = STATUS_CANCELLED; }
}
```

---

#### DAO Layer (`dao` package)

##### Database Connection Utility
```java
public class DatabaseConnection {
    private static final String URL = "jdbc:mariadb://localhost:3306/music_course_platform";
    private static final String USER = "root";
    private static final String PASSWORD = "password";
    
    public static Connection getConnection() throws SQLException {
        return DriverManager.getConnection(URL, USER, PASSWORD);
    }
}
```

##### DAO Classes
```java
public class UserDAO {
    // Authentication
    public User findByEmail(String email);
    public User findByUsername(String username);
    public User findById(int userId);
    public boolean create(User user);
    public boolean update(User user);
    public boolean delete(int userId);
}

public class TeacherProfileDAO {
    // CRUD operations for teacher profiles
    public TeacherProfile findById(int profileId);
    public TeacherProfile findByUserId(int userId);
    public List<TeacherProfile> findByInstrument(String instrument);
    public List<TeacherProfile> findAll();
    public boolean create(TeacherProfile profile);
    public boolean update(TeacherProfile profile);
    public boolean delete(int profileId);
}

public class LearnerProfileDAO {
    // CRUD operations for learner profiles
    public LearnerProfile findById(int profileId);
    public LearnerProfile findByUserId(int userId);
    public boolean create(LearnerProfile profile);
    public boolean update(LearnerProfile profile);
    public boolean delete(int profileId);
}

public class TimeSlotDAO {
    // Time slot management
    public TimeSlot findById(int slotId);
    public List<TimeSlot> findByTeacherProfileId(int teacherProfileId);
    public List<TimeSlot> findByTeacherAndDate(int teacherProfileId, LocalDate date);
    public boolean create(TimeSlot slot);
    public boolean updateStatus(int slotId, String status);
    public boolean delete(int slotId);
}

public class BookingDAO {
    // Booking management
    public Booking findById(int bookingId);
    public Booking findBySlotId(int slotId);
    public List<Booking> findByLearnerProfileId(int learnerProfileId);
    public List<Booking> findByTeacherProfileId(int teacherProfileId);
    public boolean create(Booking booking);
    public boolean updateStatus(int bookingId, String status);
    public boolean delete(int bookingId);
}
```

---

#### Service Layer (`service` package)

```java
public class UserService {
    private UserDAO userDAO;
    private TeacherProfileDAO teacherProfileDAO;
    private LearnerProfileDAO learnerProfileDAO;
    
    // User authentication and registration
    public User authenticateByEmail(String email, String password);
    public User registerTeacher(String username, String email, String password);
    public User registerLearner(String username, String email, String password);
    
    // Validation methods
    private void validateEmail(String email);
    private void validateUsername(String username);
    private void validatePassword(String password);
}

public class TeacherService {
    private TeacherProfileDAO teacherProfileDAO;
    private UserDAO userDAO;
    
    // Profile management
    public TeacherProfile createProfile(int userId, String biography, 
                                       String instruments, int experience, 
                                       int rate, String location);
    public TeacherProfile updateProfile(int profileId, String biography, 
                                       String instruments, int experience, 
                                       int rate, String location);
    public List<TeacherProfile> searchByInstrument(String instrument);
    
    // Validation
    private void validateProfile(String instruments, int experience, int rate);
}

public class TimeSlotService {
    private TimeSlotDAO timeSlotDAO;
    private BookingDAO bookingDAO;
    
    // Time slot management
    public TimeSlot createSlot(int teacherProfileId, LocalDate date, 
                              String startTime, String endTime);
    public boolean deleteSlot(int slotId);
    public List<TimeSlot> getAvailableSlots(int teacherProfileId, LocalDate date);
    
    // Validation
    private void validateTimeSlot(LocalDate date, String startTime, String endTime);
    private void checkSlotConflict(int teacherProfileId, LocalDate date, 
                                   String startTime, String endTime);
}

public class BookingService {
    private BookingDAO bookingDAO;
    private TimeSlotDAO timeSlotDAO;
    private LearnerProfileDAO learnerProfileDAO;
    
    // Booking management
    public Booking createBooking(int slotId, int learnerProfileId, String notes);
    public Booking confirmBooking(int bookingId);
    public Booking cancelBooking(int bookingId);
    public List<Booking> getLearnerBookings(int learnerProfileId);
    public List<Booking> getTeacherBookings(int teacherProfileId);
}
```

---

#### Controller Layer (`controller` package)

```java
public class LoginController {
    @FXML private TextField emailField;
    @FXML private PasswordField passwordField;
    @FXML private Label errorLabel;
    
    private UserService userService;
    
    @FXML
    public void handleLogin(ActionEvent event);
    @FXML
    private void navigateToSignup(ActionEvent event);
    private void navigateToDashboard(ActionEvent event, User user);
    private void showError(String message);
}

public class SignupController {
    @FXML private TextField usernameField;
    @FXML private TextField emailField;
    @FXML private PasswordField passwordField;
    @FXML private Label errorLabel;
    
    private UserService userService;
    
    @FXML
    private void handleStudentSignup(ActionEvent event);
    @FXML
    private void handleTeacherSignup(ActionEvent event);
    private void registerUser(String userType, ActionEvent event);
}

public class TeacherDashboardController {
    @FXML private Label nameLabel;
    @FXML private ComboBox<String> instrumentsCombo;
    @FXML private TextField experienceField;
    @FXML private TextField pricingField;
    @FXML private TextArea bioField;
    @FXML private Label monthLabel;
    @FXML private FlowPane calendarGrid;
    @FXML private ComboBox<String> startTimeCombo;
    @FXML private ComboBox<String> endTimeCombo;
    @FXML private VBox timeSlotsContainer;
    @FXML private ComboBox<String> languageCombo;
    
    // Profile management methods
    @FXML
    private void handleSaveProfile(ActionEvent event);
    
    // Calendar navigation methods
    @FXML
    private void handlePreviousMonth(ActionEvent event);
    @FXML
    private void handleNextMonth(ActionEvent event);
    private void updateCalendar();
    
    // Time slot management methods
    @FXML
    private void handleAddTimeSlot(ActionEvent event);
    private void loadTimeSlotsForDate(LocalDate date);
}

public class StudentDashboardController {
    @FXML private Label teacherNameLabel;
    @FXML private ComboBox<String> instrumentCombo;
    @FXML private ComboBox<String> teacherCombo;
    @FXML private Label monthLabel;
    @FXML private FlowPane calendarGrid;
    @FXML private VBox timeSlotsContainer;
    @FXML private ComboBox<String> languageCombo;
    @FXML private Button bookButton;
    
    // Teacher search methods
    @FXML
    private void handleInstrumentChange(ActionEvent event);
    @FXML
    private void handleTeacherChange(ActionEvent event);
    
    // Calendar navigation methods
    @FXML
    private void handlePreviousMonth(ActionEvent event);
    @FXML
    private void handleNextMonth(ActionEvent event);
    private void updateCalendar();
    
    // Booking methods
    @FXML
    private void handleBooking(ActionEvent event);
    private void loadAvailableSlots(LocalDate date);
}

public class BookingViewController {
    @FXML private Label userNameLabel;
    @FXML private FlowPane bookingsContainer;
    @FXML private ComboBox<String> languageCombo;
    
    private void loadBookings();
    private VBox createBookingCard(Booking booking);
    private void handleCancelBooking(int bookingId);
}

public class SessionManager {
    private static SessionManager instance;
    private User currentUser;
    
    public static SessionManager getInstance();
    public User getCurrentUser();
    public void setCurrentUser(User user);
    public void clearSession();
}
```

---

#### Utility Layer (`util` package)

```java
public class PasswordUtil {
    // BCrypt password hashing
    public static String hashPassword(String plainPassword);
    public static boolean verifyPassword(String plainPassword, String hashedPassword);
}
```

---

### 4. **Application Entry Point**

```java
public class Launcher {
    public static void main(String[] args) {
        // Launch JavaFX application
        Application.launch(Main.class, args);
    }
}

public class Main extends Application {
    @Override
    public void start(Stage primaryStage) throws Exception {
        // Load Login Screen
        FXMLLoader loader = new FXMLLoader(
            getClass().getResource("/fxml/login.fxml")
        );
        Parent root = loader.load();
        
        Scene scene = new Scene(root, 800, 600);
        scene.getStylesheets().add(
            getClass().getResource("/css/styles.css").toExternalForm()
        );
        
        primaryStage.setTitle("Music Course Platform");
        primaryStage.setScene(scene);
        primaryStage.setResizable(false);
        primaryStage.show();
    }
}
```

---

## Additional Design Considerations

### 1. **Security**
- **Password Hashing**: BCrypt with salt for secure password storage
- **Input Validation**: All user inputs are validated before processing
- **SQL Injection Prevention**: PreparedStatements for all database queries
- **Session Security**: Session management with automatic timeout

### 2. **Error Handling**
- **Database Errors**: Graceful handling with user-friendly error messages
- **Validation Errors**: Clear feedback for invalid inputs
- **Network Errors**: Retry logic for connection issues
- **Logging**: Comprehensive error logging for debugging

### 3. **UI/UX Design**
- **Responsive Layout**: Adaptive UI components
- **Visual Feedback**: Loading indicators, success/error messages
- **Accessibility**: Keyboard navigation support
- **Styling**: Custom CSS for consistent branding
- **Calendar Interface**: Color-coded status indicators (available, booked, selected)

### 4. **Performance Optimization**
- **Database Connection Pooling**: Efficient connection management
- **Lazy Loading**: Load data only when needed
- **Caching**: Session-based caching for frequently accessed data
- **Pagination**: For large datasets (future enhancement)

### 5. **Testing Strategy**
- **Unit Tests**: JUnit tests for all service and DAO classes
- **Mocking**: Mockito for isolating dependencies
- **Code Coverage**: JaCoCo reports targeting 80%+ coverage
- **Integration Tests**: Database integration testing
- **UI Tests**: TestFX for JavaFX component testing

### 6. **Multi-Language Support**
- **Resource Bundles**: Externalized strings for easy translation
- **Supported Languages**: English (EN), German (DE), Chinese (ZH)
- **Dynamic Switching**: Real-time language change without restart
- **Locale Management**: Date and time formatting based on locale
- **UTF-8 Encoding**: Full support for international characters

---

## Deployment Architecture

### 1. **Local Development**
- MariaDB running on localhost:3306
- Maven for dependency management and build
- JavaFX runtime included in JDK 17+

### 2. **CI/CD Pipeline (Jenkins)**
```
Stages:
1. Checkout - Clone from GitHub
2. Build - mvn clean compile
3. Test - mvn test
4. Coverage Report - jacoco:report
5. Package - mvn package (creates shaded JAR)
6. Archive Artifacts - Save JAR files
7. Docker Build - Build image from Dockerfile
8. Docker Push - Push to Docker Hub
```

### 3. **Docker Containerization**
```dockerfile
FROM openjdk:17-jdk-alpine
WORKDIR /app
COPY target/MusicCoursePlatform-1.0-SNAPSHOT-shaded.jar app.jar
EXPOSE 8080
ENTRYPOINT ["java", "-jar", "app.jar"]
```

### 4. **Database Setup**
```bash
# Install MariaDB
# Create database
mysql -u root -p < database/schema.sql

# Configure connection in DatabaseConnection.java
```

---

## Future Enhancements

1. **Payment Integration**: Process lesson payments online
2. **Review System**: Learners can rate and review teachers
3. **Messaging System**: In-app communication between teachers and learners
4. **Notification System**: Email/SMS reminders for upcoming lessons
5. **Mobile App**: Cross-platform mobile application
6. **Video Lessons**: Integration with video conferencing platforms
7. **Progress Tracking**: Track learner progress and achievements
8. **Advanced Search**: Filter by price range, location, availability
9. **Group Lessons**: Support for group booking functionality
10. **Analytics Dashboard**: Insights for teachers on booking trends

