# Software Engineering Project 1
## Music Course Platform
## Group 5

## Team Members
- **Luo Ying** - Frontend Development, Documentation
- **Chen Yicheng** - Scrum Master, Backend Development, DevOps
- **Lu Liu** - Backend Development, Database Design, Technical Documentation
- **Su Wai Phyoe** - Testing, Frontend Development, Documentation

---

## Description

The Music Course Platform is a desktop application that connects music teachers with learners, enabling teachers to manage their profiles and availability while allowing learners to search for teachers and book lessons. 

The application uses **JavaFX** for the user interface, **MariaDB** for database management, and **JDBC** for database interaction, following a three-tier architecture pattern.

### Key Features
- User registration and authentication for teachers and learners
- Teacher profile management (biography, instruments, pricing)
- Time slot scheduling and availability management
- Multi-criteria teacher search (by instrument and availability)
- Lesson booking and confirmation
- Booking management for both teachers and learners

---

## Technology Stack

- **Language:** Java 17+
- **UI Framework:** JavaFX 17+
- **Database:** MariaDB 10.6+
- **Database Connectivity:** JDBC
- **Build Tool:** Maven
- **Version Control:** Git & GitHub
- **Project Management:** Trello
- **Testing:** JUnit 5, TestFX
- **Code Coverage:** JaCoCo
- **CI/CD:** Jenkins (planned for Sprint 3)
- **Containerization:** Docker (planned for Sprint 4)

---

## Documentation

### Project Planning
- [Product Vision](document/Product%20Vision.pdf)
- [Project Plan](document/Software%20Engineering%20Project%20Plan.pdf)
- [AI and Project Management](document/AI%20and%20Project%20Management%20in%20Software%20Engineering.pdf)
- [User Stories](document/UserStories.md) *(Coming in Sprint 2)*
- [Use Case Diagram](document/UseCaseDiagram.md) *(Coming in Sprint 2)*

### Design & Architecture
- [System Architecture](document/Architecture.md) *(Coming in Sprint 2)*
- [Database Schema](document/DatabaseSchema.md) *(Coming in Sprint 2)*
- [UI/UX Design](document/UIDesign.md) *(Coming in Sprint 3)*

### Sprint Reviews
- [Sprint 1 Review](document/Sprint_1_Review_Report.md)
- [Sprint 2 Review](document/Sprint_2_Review_Report.md) *(Coming Soon)*
- [Sprint 3 Review](document/Sprint_3_Review_Report.md) *(Coming Soon)*
- [Sprint 4 Review](document/Sprint_4_Review_Report.md) *(Coming Soon)*

### Quality Assurance
- [Testing Strategy](document/Testing.md) *(Coming in Sprint 3)*
- [Code Coverage Reports](document/CodeCoverage.md) *(Coming in Sprint 2)*
- [Code Review Guidelines](document/CodeReview.md) *(Coming in Sprint 3)*

---

## Project Structure

```
MusicCoursePlatform/
├── MusicCoursePlatform/          # Main application directory
│   ├── pom.xml                   # Maven configuration
│   └── src/
│       ├── main/
│       │   ├── java/             # Java source files
│       │   │   ├── controller/   # JavaFX controllers (Sprint 2+)
│       │   │   ├── model/        # Data models (Sprint 2+)
│       │   │   ├── dao/          # Data Access Objects (Sprint 2+)
│       │   │   ├── service/      # Business logic layer (Sprint 2+)
│       │   │   ├── util/         # Utility classes (Sprint 2+)
│       │   │   └── Main.java     # Application entry point
│       │   └── resources/
│       │       ├── fxml/         # FXML layout files (Sprint 2+)
│       │       ├── css/          # Stylesheets (Sprint 3+)
│       │       └── images/       # Images and icons (Sprint 3+)
│       └── test/
│           └── java/             # JUnit test files (Sprint 2+)
│
├── document/                     # Project documentation
│   ├── Product Vision.pdf
│   ├── Software Engineering Project Plan.pdf
│   └── AI and Project Management.pdf
│
├── database/                     # Database scripts (to be created in Sprint 2)
│   ├── schema.sql               # Database schema
│   └── sample_data.sql          # Sample data for testing
│
├── .gitignore                   # Git ignore file
└── README.md                    # This file
```

---

## Getting Started

### Prerequisites

Before you begin, ensure you have the following installed:

- **Java Development Kit (JDK) 17 or later**
  - Download: https://www.oracle.com/java/technologies/downloads/
  - Verify: `java -version`

- **Apache Maven 3.6+**
  - Download: https://maven.apache.org/download.cgi
  - Verify: `mvn -version`

- **MariaDB 10.6+**
  - Download: https://mariadb.org/download/
  - Default port: 3306

- **JavaFX SDK 17+** (if not using Maven)
  - Download: https://gluonhq.com/products/javafx/

- **Git**
  - Download: https://git-scm.com/downloads

---

### Installation & Setup

#### 1. Clone the Repository

```bash
git clone https://github.com/chenyicheng1998/MusicCoursePlatform.git
cd MusicCoursePlatform
```

#### 2. Set Up Database *(Sprint 2+)*

```bash
# Start MariaDB service
# Create database
mysql -u root -p
CREATE DATABASE music_course_platform;
USE music_course_platform;
SOURCE database/schema.sql;
SOURCE database/sample_data.sql;  # Optional: Load sample data
```

#### 3. Configure Database Connection *(Sprint 2+)*

Update database credentials in `src/main/resources/database.properties`:

```properties
db.url=jdbc:mariadb://localhost:3306/music_course_platform
db.username=your_username
db.password=your_password
```

#### 4. Build the Project

```bash
cd MusicCoursePlatform
mvn clean install
```

#### 5. Run the Application

```bash
mvn javafx:run
```

Or run directly from IDE (IntelliJ IDEA / Eclipse):
- Right-click on `Main.java`
- Select "Run Main.main()"

---

## Development Workflow

### Branch Strategy

- `main` - Production-ready code
- `develop` - Development branch (Sprint work)
- `feature/*` - Feature branches (e.g., `feature/user-login`)
- `bugfix/*` - Bug fix branches

### Coding Standards

- Follow Java naming conventions (CamelCase for classes, camelCase for methods)
- Write meaningful comments for complex logic
- All public methods must have JavaDoc comments
- Maximum line length: 120 characters
- Use 4 spaces for indentation (no tabs)

### Commit Message Format

```
<type>(<scope>): <subject>

Examples:
feat(login): Add user authentication
fix(booking): Prevent double-booking
docs(readme): Update installation instructions
test(user): Add unit tests for UserDAO
```

---

## Testing

### Run Unit Tests

```bash
mvn test
```

### Generate Code Coverage Report

```bash
mvn jacoco:report
```

View report: `target/site/jacoco/index.html`

### Test Coverage Goals
- **Sprint 2:** 40% code coverage
- **Sprint 3:** 60% code coverage
- **Sprint 4:** 70%+ code coverage

---

## Trello Board

**Project Management:** https://trello.com/b/YnjfjBxd/sep1musiccourseplatform

### Board Structure
- Product Backlog - All user stories
- Sprint X - To Do - Current sprint tasks
- Sprint X - In Progress - Tasks being worked on
- Sprint X - Done - Completed tasks
- Archive - Completed sprints

---

## Sprint Timeline

| Sprint | Weeks | Status | Focus |
|--------|-------|--------|-------|
| **Sprint 1** | Week 1-2 | Completed | Project planning, documentation, tool setup |
| **Sprint 2** | Week 3-4 | In Progress | Database schema, user management, basic UI |
| **Sprint 3** | Week 5-6 | Planned | Booking system, search functionality, CI/CD |
| **Sprint 4** | Week 7-8 | Planned | Testing, Docker, final polish, documentation |

**Current Sprint:** Sprint 2  
**Sprint Goal:** Implement user registration, login, and database foundation

---

## Key Deliverables

### Sprint 1 (Done)
- [x] Product Vision Document
- [x] Project Plan (12 pages)
- [x] Trello Board with 28 User Stories
- [x] Use Case Diagram
- [x] GitHub Repository Setup
- [x] Technology Stack Selection

### Sprint 2 (In Progress)
- [ ] Database schema implementation
- [ ] User registration and login functionality
- [ ] JavaFX login and registration UI
- [ ] UserDAO with CRUD operations
- [ ] Unit tests with JUnit
- [ ] JaCoCo code coverage report
- [ ] Maven configuration

### Sprint 3 (Upcoming)
- [ ] Teacher profile management
- [ ] Time slot management
- [ ] Teacher search functionality
- [ ] Lesson booking system
- [ ] Jenkins CI/CD pipeline
- [ ] Docker image (local testing)

### Sprint 4 (Upcoming)
- [ ] Integration testing
- [ ] System testing and bug fixes
- [ ] Docker image published to Docker Hub
- [ ] Final UI polish
- [ ] Complete technical documentation
- [ ] User manual
- [ ] Final presentation

---

## Database Schema *(Sprint 2+)*

### Core Tables

**users**
- `user_id` (Primary Key)
- `username`, `password_hash`, `email`
- `user_type` (TEACHER/LEARNER)
- `created_at`

**teacher_profiles**
- `profile_id` (Primary Key)
- `user_id` (Foreign Key → users)
- `biography`, `instruments_taught`
- `years_experience`, `hourly_rate`, `location`

**time_slots**
- `slot_id` (Primary Key)
- `teacher_id` (Foreign Key → users)
- `lesson_date`, `start_time`, `end_time`
- `status` (AVAILABLE/BOOKED)

**bookings**
- `booking_id` (Primary Key)
- `slot_id` (Foreign Key → time_slots)
- `learner_id` (Foreign Key → users)
- `booking_date`, `status`

[See Full Schema](docs/DatabaseSchema.md) *(Coming in Sprint 2)*

---

## CI/CD Pipeline *(Sprint 3+)*

### Jenkins Pipeline Stages
1. **Checkout** - Pull latest code from GitHub
2. **Build** - Compile with Maven
3. **Test** - Run JUnit tests
4. **Code Coverage** - Generate JaCoCo reports
5. **Static Analysis** - SonarQube analysis *(optional)*
6. **Package** - Create JAR/Docker image
7. **Deploy** - Deploy to test environment

---

## Docker *(Sprint 4+)*

### Build Docker Image

```bash
docker build -t music-course-platform:latest .
```

### Run Docker Container

```bash
docker run -p 8080:8080 music-course-platform:latest
```

### Docker Hub

Image will be published to: `chenyicheng/music-course-platform:latest`

---

## Contributing

### Team Workflow

1. **Pull latest changes** from `main` or `develop`
   ```bash
   git pull origin develop
   ```

2. **Create feature branch**
   ```bash
   git checkout -b feature/your-feature-name
   ```

3. **Make changes and commit**
   ```bash
   git add .
   git commit -m "feat(scope): description"
   ```

4. **Push to GitHub**
   ```bash
   git push origin feature/your-feature-name
   ```

5. **Create Pull Request** on GitHub
   - Assign reviewer
   - Link related Trello card
   - Wait for code review approval

6. **Merge to develop** after approval

---

## Known Issues & Limitations

*(Will be updated as project progresses)*

### Current Limitations (Sprint 1)
- No implementation code yet (planning phase)
- Database schema design in progress
- UI mockups not finalized

### Planned Features (Out of Scope for SEP1)
- Payment processing integration
- Email/SMS notifications
- Advanced rating and review system
- Mobile application
- Multi-language support

---


## License

This project is developed as part of the Software Engineering Project 1 course at Metropolia University of Applied Sciences.

**Academic Use Only** - Not for commercial distribution.

---

## Links

**Project Repository:** https://github.com/chenyicheng1998/MusicCoursePlatform  
**Trello Board:** https://trello.com/b/YnjfjBxd/sep1musiccourseplatform

---

## Acknowledgments

- **Metropolia University of Applied Sciences** - Software Engineering Program
- **Course Instructor:** Amir Dirin
- **Team 6 Members:** Luo Ying, Chen Yicheng, Lu Liu, Su Wai Phyoe

---

**Last Updated:** January 27, 2026  
**Current Sprint:** Sprint 2 (Week 3-4)  
**Project Status:** Active Development
