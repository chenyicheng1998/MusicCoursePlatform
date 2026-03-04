# Music Course Platform
## Software Engineering Project 1 — Group 5

## Team Members
| Name | Role |
|------|------|
| **Luo Ying** | Frontend Development, Documentation |
| **Chen Yicheng** | Backend Development, DevOps, Technical Documentation |
| **Lu Liu** | Backend Development, Database Design, Technical Documentation |
| **Su Wai Phyoe** | Testing, Frontend Development, Documentation |

---

## Description

The Music Course Platform is a JavaFX desktop application that connects music teachers with learners. Teachers can manage their profiles and availability, while learners can search for teachers and book lessons.

The application follows a **three-tier architecture** (Presentation → Business Logic → Data Access) using JavaFX for the UI, MariaDB for the database, and JDBC for database connectivity.

### Key Features
- User registration and authentication (Teacher / Learner)
- Teacher profile management (biography, instruments, pricing, location)
- Time slot scheduling and availability management
- Teacher search by instrument and availability
- Lesson booking and cancellation
- Booking management for both teachers and learners

---

## Technology Stack

| Category | Technology |
|----------|-----------|
| Language | Java 17+ |
| UI Framework | JavaFX 17+ |
| Database | MariaDB 10.6+ |
| DB Connectivity | JDBC |
| Build Tool | Maven |
| Version Control | Git & GitHub |
| Project Management | Trello |
| Testing | JUnit 5 |
| Code Coverage | JaCoCo |
| CI/CD | Jenkins |
| Containerization | Docker |

---

## Project Structure

```
MusicCoursePlatform/
├── README.md
├── document/
│   ├── Product Vision.pdf
│   ├── Software Engineering Project Plan.pdf
│   ├── AI and Project Management in Software Engineering.pdf
│   ├── diagrams/
│   │   ├── Music Course Platform - Use Case Diagram.pdf
│   │   └── Music Course Platform Data Modeling.pdf
│   ├── images/
│   └── Sprint Review Reports/
│       ├── Sprint_1_Review_Report.pdf
│       ├── Sprint_2_Review_Report.pdf
│       └── Sprint_3_Review_Report.pdf
│
└── MusicCoursePlatform/                 # Main application
    ├── pom.xml
    ├── Jenkinsfile
    ├── Dockerfile
    ├── database/
    │   └── schema.sql
    └── src/
        ├── main/
        │   ├── java/
        │   │   ├── com/musiccourse/
        │   │   │   └── Main.java        # Application entry point
        │   │   ├── controller/
        │   │   │   ├── BookingViewController.java
        │   │   │   ├── LoginController.java
        │   │   │   ├── SessionManager.java
        │   │   │   ├── SignupController.java
        │   │   │   ├── StudentDashboardController.java
        │   │   │   ├── TeacherDashboardController.java
        │   │   │   └── TeacherProfileViewController.java
        │   │   ├── dao/
        │   │   │   ├── BookingDAO.java
        │   │   │   ├── DatabaseConnection.java
        │   │   │   ├── LearnerProfileDAO.java
        │   │   │   ├── TeacherProfileDAO.java
        │   │   │   ├── TimeSlotDAO.java
        │   │   │   └── UserDAO.java
        │   │   ├── model/
        │   │   │   ├── Booking.java
        │   │   │   ├── LearnerProfile.java
        │   │   │   ├── TeacherProfile.java
        │   │   │   ├── TimeSlot.java
        │   │   │   └── User.java
        │   │   ├── service/
        │   │   │   ├── BookingService.java
        │   │   │   ├── TeacherService.java
        │   │   │   ├── TimeSlotService.java
        │   │   │   └── UserService.java
        │   │   └── util/
        │   │       └── PasswordUtil.java
        │   └── resources/
        │       ├── css/
        │       │   └── styles.css
        │       └── fxml/
        │           ├── login.fxml
        │           ├── signup.fxml
        │           ├── student_course_booking.fxml
        │           ├── student_schedule_view.fxml
        │           ├── teacher_schedule_view.fxml
        │           └── teacher_set_availability.fxml
        └── test/
            └── java/
                ├── controller/
                │   └── SessionManagerTest.java
                ├── dao/
                │   ├── BookingDAOTest.java
                │   ├── LearnerProfileDAOTest.java
                │   ├── TeacherProfileDAOTest.java
                │   ├── TimeSlotDAOTest.java
                │   └── UserDAOTest.java
                ├── model/
                │   └── ModelTest.java
                ├── service/
                │   ├── BookingServiceTest.java
                │   ├── TeacherServiceTest.java
                │   ├── TimeSlotServiceTest.java
                │   └── UserServiceTest.java
                └── util/
                    └── PasswordUtilTest.java
```

---

## Getting Started

### Prerequisites

- **JDK 17+** — `java -version`
- **Apache Maven 3.6+** — `mvn -version`
- **MariaDB 10.6+** (default port: 3306)
- **Git**

### Installation & Setup

#### 1. Clone the Repository

```bash
git clone https://github.com/chenyicheng1998/MusicCoursePlatform.git
cd MusicCoursePlatform
```

#### 2. Configure Database Connection

Open `MusicCoursePlatform/src/main/java/dao/DatabaseConnection.java` and update lines 17–19 with your MariaDB credentials:

```java
private static final String URL = "jdbc:mariadb://localhost:3306/music_course_platform";
private static final String USER = "root";
private static final String PASSWORD = "your_password_here";
```

#### 3. Create the Database

In your MariaDB client, run:

```sql
SOURCE MusicCoursePlatform/database/schema.sql;
```

#### 4. Build the Project

```bash
cd MusicCoursePlatform
mvn clean compile
```

#### 5. Run the Application

```bash
mvn javafx:run
```

---

## Testing & Code Coverage

To run all tests and generate the JaCoCo coverage report:

```bash
mvn clean test jacoco:report
```

The coverage report will be available at `target/site/jacoco/index.html`.

---

## CI/CD Pipeline (Jenkins)

The project uses a Jenkins pipeline defined in `Jenkinsfile`. It triggers automatically on commits to the `dev` branch and runs the following stages:

| Stage | Description |
|-------|-------------|
| Checkout | Clone latest code from GitHub |
| Build | Compile with Maven (`mvn clean compile`) |
| Run Tests | Execute JUnit tests (`mvn test`) |
| Generate JaCoCo Report | Produce code coverage report |
| Package Application | Build shaded JAR (`mvn package`) |
| Archive Artifacts | Save JAR files as build artifacts |
| Build Docker Image | Build Docker image from Dockerfile |
| Push to Docker Hub | Push image to `chenyicheng1998/music-course-platform` |

---

## Docker

The Docker image is available on Docker Hub:
**[chenyicheng1998/music-course-platform](https://hub.docker.com/repository/docker/chenyicheng1998/music-course-platform)**

```bash
docker pull chenyicheng1998/music-course-platform:v1
```

> **Note:** The image builds and pushes successfully via the Jenkins pipeline. Full GUI container execution (JavaFX display environment) is planned for Sprint 4.

---

## Trello Boards

| Sprint | Scrum Master | Board | Status |
|--------|-------------|-------|--------|
| Sprint 1 | Chen Yicheng | [Sprint 1](https://trello.com/b/YnjfjBxd/sep1musiccourseplatform) | ✅ Done |
| Sprint 2 | Luo Ying | [Sprint 2](https://trello.com/b/IMIZmc7K/sep1musiccourseplatform-sprint2) | ✅ Done |
| Sprint 3 | Su Wai Phyoe | [Sprint 3](https://trello.com/b/B5AJ4wIm/sep1musiccourseplatform-sprint3) | ✅ Done |
| Sprint 4 | TBD | Sprint 4 Board (to be created) | 📅 Planned |

### Sprint Boards

**Sprint 1**
![Sprint 1 Trello](document/images/sprint1-trello.jpg)

**Sprint 2**
![Sprint 2 Trello](document/images/sprint2-trello.jpg)

**Sprint 3**
![Sprint 3 Trello](document/images/sprint3-trello.jpg)

---

## Documentation

| Document | Link |
|----------|------|
| Product Vision | [Product Vision.pdf](document/Product%20Vision.pdf) |
| Project Plan | [Software Engineering Project Plan.pdf](document/Software%20Engineering%20Project%20Plan.pdf) |
| AI and Project Management | [AI and Project Management.pdf](document/AI%20and%20Project%20Management%20in%20Software%20Engineering.pdf) |
| Use Case Diagram | [Use Case Diagram.pdf](document/diagrams/Music%20Course%20Platform%20-%20Use%20Case%20Diagram.pdf) |
| Data Modeling | [Data Modeling.pdf](document/diagrams/Music%20Course%20Platform%20Data%20Modeling.pdf) |
| Sprint 1 Review | [Sprint 1 Review.pdf](document/SprintReviewReports/Sprint_1_Review_Report.pdf) |
| Sprint 2 Review | [Sprint 2 Review.pdf](document/SprintReviewReports/Sprint_2_Review_Report.pdf) |
| Sprint 3 Review | [Sprint 3 Review.pdf](document/SprintReviewReports/Sprint_3_Review_Report.pdf) |

### Diagrams

**Use Case Diagram**
![Use Case Diagram](document/images/dia_usecase.jpg)

**Database Schema**
![Database Schema](document/images/dia_dbschema.jpg)

**ER Diagram**
![ER Diagram](document/images/dia_er.jpg)