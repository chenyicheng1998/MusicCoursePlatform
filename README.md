# Music Course Platform

## 1. Project Title & Overview

The **Music Course Platform** is a JavaFX desktop application that connects music teachers and learners. Teachers can create profiles, set availability, and manage bookings; learners can search for teachers by instrument, schedule, and price, then book lessons directly.

- **Problem solved:** Difficulty for learners to find suitable music teachers, and lack of efficient booking tools for freelance teachers.
- **Target users:** Music teachers (students/freelancers) and learners (children, teens, adults).
- **Technologies:** Java 17, JavaFX, Maven, MariaDB, Jenkins, Docker, SonarQube.
- **Duration:** 8 sprints x 2 weeks (16 weeks total).

---

## 2. Product Vision

**Vision Statement:** We aim to empower music teachers by giving them flexible teaching opportunities and to support learners by helping them find the right teacher at the right time. Through this platform, we strive to promote music education and create positive learning experiences for users of all ages.

**Main Goals:**

- Enable stable and reliable booking with minimal scheduling conflicts
- Provide teachers with efficient tools to manage lesson bookings and availability
- Support smooth communication between teachers and learners

**Key Features:**

- User registration and login for teachers and learners
- Teacher profile creation (biography, instruments, pricing, availability)
- Search and filter teachers by instrument, availability, location, and price
- Lesson booking with confirmation
- Multi-language support (English, Chinese, Arabic)

**Definition of Success:** A fully functional platform where teachers can publish availability and learners can successfully book lessons, with complete CI/CD pipeline, containerization, and localization.

**Full document:** [Product Vision](document/Product%20Vision.pdf)

---

## 3. Project Plan & Sprint Structure

**Development Methodology:** Agile / Scrum

**Sprint Length:** 2 weeks

**Sprint Overview:**

| Sprint   | Goals                                                      |
| -------- | ---------------------------------------------------------- |
| Sprint 1 | Project planning, vision, backlog creation                 |
| Sprint 2 | Requirements analysis, database design, unit testing setup |
| Sprint 3 | UI implementation, Jenkins CI pipeline                     |
| Sprint 4 | Docker containerization                                    |
| Sprint 5 | UI localization (i18n), Kubernetes exploration             |
| Sprint 6 | Database localization                                      |
| Sprint 7 | Quality assurance (SonarQube, JMeter)                      |
| Sprint 8 | Documentation and finalization                             |

**Full document:** [Software Engineering Project Plan](document/Software%20Engineering%20Project%20Plan.pdf)

---

## Documentation

| Document                                                                                | Description                                                                     |
| --------------------------------------------------------------------------------------- | ------------------------------------------------------------------------------- |
| [Testing.md](document/Testing.md)                                                       | Ten core manual test results; links to UAT spreadsheet and heuristic evaluation |
| [Heuristic Evaluation Report](document/Heuristic_Evaluation_FinalReport.pdf)            | Heuristic usability evaluation (final report)                                   |
| [User Acceptance Test (spreadsheet)](document/UserAcceptanceTest.xlsx)                  | Detailed UAT test cases                                                         |
| [User Acceptance Test (PDF)](document/UserAcceptanceTest.pdf)                           | UAT summary                                                                     |
| [Product Vision](document/Product%20Vision.pdf)                                         | Product goals and vision                                                        |
| [Project Plan](document/Software%20Engineering%20Project%20Plan.pdf)                    | Full project plan                                                               |
| [User Stories](document/UserStories.md)                                                 | Backlog and user stories                                                        |
| [Design](document/Design.md)                                                            | Design notes                                                                    |
| [Diagrams index](document/Diagrams.md)                                                  | Modelling diagrams (use case, ER, activity, class, sequence, schema)            |
| [Localization framework](document/LOCALIZATION_FRAMEWORK.md)                            | UI i18n (`ResourceBundle`, RTL)                                                 |
| [Database localization](document/DATABASE_LOCALIZATION.md)                              | Canonical `instrument_key`, `INSTRUMENT` table                                  |
| [Statistical Code Review (SonarQube)](document/Statistical%20Code%20Review%20Report.md) | Static analysis write-up                                                        |
| [Acceptance Test Plan](document/AcceptanceTestPlan.pdf)                                 | Formal acceptance testing plan                                                  |

---

## 4. Sprint 1 - Project Planning & Vision

**Summary:**

- Created initial product vision and validated with stakeholders
- Established project backlog with user stories
- Defined project scope, risks, and success criteria
- Set up Trello board for task management

**Artifacts:**

- [Sprint 1 Planning Report](document/SprintPlanReports/Sprint%201%20Planning%20Report.md)
- [Sprint 1 Review Report](document/SprintReviewReports/Sprint_1_Review_Report.pdf)
- [Trello Board](https://trello.com/b/YnjfjBxd/sep1musiccourseplatform)

**Scrum Master:** Chen Yicheng

---

## 5. Sprint 2 - Requirements & Database

**Summary:**

- Documented functional requirements
- Created Use Case Diagram and ER Diagram
- Implemented database schema with MariaDB
- Set up unit testing framework (JUnit 5)

**Functional Requirements:** User registration/login, teacher profile management, time slot management, lesson booking.

**Database Technology:** MariaDB 10.6+ with UTF-8 support (utf8mb4)

**Unit Testing:** JUnit 5 with JaCoCo for code coverage

**Key Diagrams:**

- [Use Case Diagram](document/images/dia_usecase.jpg) — also available as [PDF](document/diagrams/Music%20Course%20Platform-Use%20Case%20Diagram.pdf)
- [ER Diagram](document/images/dia_er.png) — also available as [PDF](document/diagrams/Music%20Course%20Platform%20Data%20Modeling.pdf)

Full diagram gallery (class, sequence, activity, schema): [document/Diagrams.md](document/Diagrams.md)

**Artifacts:**

- [Sprint 2 Planning Report](document/SprintPlanReports/Sprint%202%20Planning%20Report.md)
- [Sprint 2 Review Report](document/SprintReviewReports/Sprint_2_Review_Report.pdf)
- [Trello Board](https://trello.com/b/IMIZmc7K/sep1musiccourseplatform-sprint2)

**Scrum Master:** Luo Ying

---

## 6. Sprint 3 - UI Implementation & CI

**Summary:**

- Implemented JavaFX user interface
- Created login, registration, teacher dashboard, student dashboard screens
- Set up Jenkins CI pipeline with automated build, test, and coverage

**UI Framework:** JavaFX with FXML for layout

**Screens Implemented:**

- Login / Registration
- Teacher Dashboard (profile, calendar, availability)
- Student Dashboard (search, booking)
- Schedule views

**Code Coverage:** JaCoCo with target > 60%

**Jenkins Pipeline Stages:**

1. **Build:** `mvn clean install -DskipTests`
2. **Test:** `mvn test` with JUnit results
3. **Coverage:** `mvn jacoco:report`
4. **SonarQube Analysis:** Static code analysis
5. **Package:** `mvn package`
6. **Docker Build & Push**

**Artifacts:**

- [Sprint 3 Planning Report](document/SprintPlanReports/Sprint%203%20Planning%20Report.md)
- [Sprint 3 Review Report](document/SprintReviewReports/Sprint_3_Review_Report.pdf)
- [Trello Board](https://trello.com/b/B5AJ4wIm/sep1musiccourseplatform-sprint3)

**Scrum Master:** Su Wai Phyoe

---

## 7. Sprint 4 - Docker Containerization

**Summary:**

- Created Dockerfile for the JavaFX application
- Configured environment variable support for database connection
- Published image to Docker Hub
- Integrated Docker build into Jenkins pipeline

**Purpose of Docker:** Enable consistent deployment across different environments, simplify CI/CD, and ensure reproducible builds.

**Services Containerized:** Music Course Platform application (JavaFX client)

**Dockerfile Overview:** Based on Eclipse Temurin JDK 21, includes JavaFX dependencies and X11 support for GUI rendering. The project uses a single-container deployment — no Docker Compose file is used.

**Docker Hub Image:** [chenyicheng1998/music-course-platform](https://hub.docker.com/repository/docker/chenyicheng1998/music-course-platform)

**Artifacts:**

- [Sprint 4 Planning Report](document/SprintPlanReports/Sprint%204%20Planning%20Report.md)
- [Sprint 4 Review Report](document/SprintReviewReports/Sprint_4_Review_Report.pdf)
- [Trello Board](https://trello.com/b/Rx627kZj/sep1musiccourseplatform-sprint4)

**Scrum Master:** Liu Lu

---

## 8. Sprint 5 - UI Localization & Kubernetes

**Summary:**

- Implemented multi-language UI support
- Created resource bundles for English, Chinese, and Arabic
- Added RTL (right-to-left) support for Arabic
- Explored Kubernetes deployment options

**Supported Languages:**

- English (`en`)
- Chinese (`zh`)
- Arabic (`ar`) with RTL layout

**Localization Approach:**

- Java `ResourceBundle` with `.properties` files
- `LocalizationManager` singleton for runtime language switching
- Dynamic UI refresh on locale change

**Kubernetes:** Evaluated for future scalability; current deployment uses Docker.

**Artifacts:**

- [Sprint 5 Planning Report](document/SprintPlanReports/Sprint%205%20Planning%20Report.md)
- [Sprint 5 Review Report](document/SprintReviewReports/Sprint_5_Review_Report.pdf)
- [Localization Framework Documentation](document/LOCALIZATION_FRAMEWORK.md)
- [Trello Board](https://trello.com/b/gQ18ryeD/sep1musiccourseplatform-sprint5)

**Scrum Master:** Luo Ying

---

## 9. Sprint 6 - Database Localization

**Summary:**

- Implemented canonical key strategy for translatable data
- Created `INSTRUMENT` reference table with translations
- Migrated schema to use `instrument_key` column
- Validated cross-language data consistency

**Localization Strategy:**

- Store canonical lowercase keys (e.g., `piano`) in `TEACHERPROFILE` and `LEARNERPROFILE`
- `INSTRUMENT` table holds translations (`name_en`, `name_zh`, `name_ar`)
- Application maps keys to localized display names at runtime

**Schema Changes:**

- Added `INSTRUMENT` table
- Changed `instruments_taught` to `instrument_key` with foreign key constraint
- Full UTF-8 support (utf8mb4_unicode_ci)

**Artifacts:**

- [Sprint 6 Planning Report](document/SprintPlanReports/Sprint%206%20Planning%20Report.md)
- [Sprint 6 Review Report](document/SprintReviewReports/Sprint_6_Review_Report.pdf)
- [Database Localization Documentation](document/DATABASE_LOCALIZATION.md)
- [Trello Board](https://trello.com/b/JHw5h1HD/sep1musiccourseplatform-sprint6)

**Scrum Master:** Su Wai Phyoe

---

## 10. Sprint 7 - Quality Assurance

**Summary:**

- Integrated SonarQube for static code analysis
- Conducted JMeter performance testing
- Performed functional and acceptance testing
- Fixed code quality issues identified by analysis

**SonarQube Metrics (Sprint 7):**

| Metric                        | Target       | Result |
| ----------------------------- | ------------ | ------ |
| Reliability (Bugs)            | Grade A      | A      |
| Security (Vulnerabilities)    | Grade A      | A      |
| Maintainability (Code Smells) | Grade A or B | A      |
| Coverage                      | > 50%        | 87.9%  |
| Duplications                  | < 10%        | 4.5%   |
| Security Hotspots             | 0            | 0      |

**JMeter and performance** (`document/sprint7/`):

- [JMeter performance testing guide](document/sprint7/JMeter_Performance_Testing_Guide.md)
- [JMeter test results](document/sprint7/JMeter_Test_Results_Sprint7.md)

**Sprint 7 QA artifacts** (`document/sprint7/`):

- [Test Plan](document/sprint7/Test_Plan_Sprint7.md)
- [Bug tracking table](document/sprint7/Bug_Tracking_Table.md)
- [Technical changes (Sprint 7)](document/sprint7/Technical_Changes_Sprint7.md)

**Testing documentation** (see also [Documentation](#documentation) table):

- [Testing summary (10 test cases)](document/Testing.md)
- [User Acceptance Test (spreadsheet)](document/UserAcceptanceTest.xlsx)
- [User Acceptance Test (PDF)](document/UserAcceptanceTest.pdf)
- [Acceptance Test Plan](document/AcceptanceTestPlan.pdf)
- [Heuristic Evaluation Report](document/Heuristic_Evaluation_FinalReport.pdf)

**Full SonarQube Report:** [Statistical Code Review Report](document/Statistical%20Code%20Review%20Report.md)

**Artifacts:**

- [Sprint 7 Planning Report](document/SprintPlanReports/Sprint%207%20Planning%20Report.md)
- [Sprint 7 Review Report](document/SprintReviewReports/Sprint_7_Review_Report.pdf)
- [Trello Board](https://trello.com/b/6EcNAcQ8/sep1musiccourseplatform-sprint7)

**Scrum Master:** Liu Lu

---

## 11. Sprint 8 - Documentation & Finalization

**Summary:**

- Completed technical documentation
- Finalized user documentation
- Updated all diagrams and README
- Prepared final deliverables

**Documentation:**

- [Design Document](document/Design.md)
- [User Stories](document/UserStories.md)
- [Heuristic Evaluation Report](document/Heuristic_Evaluation_FinalReport.pdf)
- Central index: [Documentation](#documentation) table (includes Testing.md, UAT xlsx, Diagrams.md, and other project documents)

**API Documentation:** This is a desktop-only JavaFX application with no public API. All data access is handled internally through DAO classes connecting directly to MariaDB.

**Artifacts:**

- [Sprint 8 Planning Report](document/SprintPlanReports/Sprint%208%20Planning%20Report.md)
- [Sprint 8 Review Report](document/SprintReviewReports/Sprint_8_Review_Report.pdf)
- [Trello Board — Sprint 8](https://trello.com/b/SCdd3doj/sep1musiccourseplatform-sprint8)

---

## 12. How to Run the Project

### Prerequisites

- **JDK 17+** - verify with `java -version`
- **Apache Maven 3.6+** - verify with `mvn -version`
- **MariaDB 10.6+** (default port 3306)
- **Git**

### Environment Setup

#### 1. Clone the repository

```bash
git clone https://github.com/chenyicheng1998/MusicCoursePlatform.git
cd MusicCoursePlatform
```

#### 2. Configure database connection

Set environment variables or edit defaults in `MusicCoursePlatform/src/main/java/util/DatabaseConnection.java`:

| Variable      | Purpose           | Default     |
| ------------- | ----------------- | ----------- |
| `DB_HOST`     | MariaDB host      | `localhost` |
| `DB_PORT`     | MariaDB port      | `3306`      |
| `DB_USER`     | Database user     | `root`      |
| `DB_PASSWORD` | Database password | `123456`    |

```bash
export DB_HOST=localhost
export DB_PORT=3306
export DB_USER=root
export DB_PASSWORD=your_password
```

#### 3. Create the database

```bash
mysql -u root -p < MusicCoursePlatform/database/schema.sql
```

#### 4. Build and run

```bash
cd MusicCoursePlatform
mvn clean compile
mvn javafx:run
```

### Docker Commands

**Pull image:**

```bash
docker pull chenyicheng1998/music-course-platform:latest
```

**Run container (macOS with XQuartz):**

```bash
xhost +localhost
docker run -it --rm \
  -e DISPLAY=host.docker.internal:0 \
  -e DB_HOST=host.docker.internal \
  -e DB_PORT=3306 \
  -e DB_USER=root \
  -e DB_PASSWORD=your_password \
  chenyicheng1998/music-course-platform:latest
```

**Run container (Windows with Xming):**

```powershell
docker run -it --rm `
  -e DISPLAY=host.docker.internal:0.0 `
  -e DB_HOST=host.docker.internal `
  -e DB_PORT=3306 `
  -e DB_USER=root `
  -e DB_PASSWORD=your_password `
  chenyicheng1998/music-course-platform:latest
```

---

## 13. Testing Instructions

**Team testing summary:** [document/Testing.md](document/Testing.md) (10 core test cases; links to [UserAcceptanceTest.xlsx](document/UserAcceptanceTest.xlsx) and [Heuristic Evaluation Report](document/Heuristic_Evaluation_FinalReport.pdf)).

**Sprint 7 QA** (`document/sprint7/`): [Test Plan](document/sprint7/Test_Plan_Sprint7.md) · [Bug tracking](document/sprint7/Bug_Tracking_Table.md) · [Technical changes](document/sprint7/Technical_Changes_Sprint7.md) · [JMeter guide](document/sprint7/JMeter_Performance_Testing_Guide.md) · [JMeter results](document/sprint7/JMeter_Test_Results_Sprint7.md).

**SonarQube (static analysis):** Run locally as below, or rely on the **SonarQube Analysis** stage in [`MusicCoursePlatform/Jenkinsfile`](MusicCoursePlatform/Jenkinsfile) (uses credentials `sonarqube-token` and variables `SONAR_PROJECT_KEY`, `SONAR_HOST_URL`). Interpret metrics in the SonarQube UI; a written summary is in the [Statistical Code Review Report](document/Statistical%20Code%20Review%20Report.md).

### Run Unit Tests

```bash
cd MusicCoursePlatform
mvn clean test
```

### Generate Coverage Report

```bash
mvn jacoco:report
```

Open report at `MusicCoursePlatform/target/site/jacoco/index.html`

### Static analysis (SonarQube)

**Prerequisites:** A running SonarQube instance and a **user token** with permission to execute analysis.

From the `MusicCoursePlatform` directory (after `mvn clean verify` so binaries and tests exist):

```bash
mvn sonar:sonar \
  -Dsonar.projectKey=music-course-platform \
  -Dsonar.host.url=https://YOUR_SONARQUBE_HOST \
  -Dsonar.token=YOUR_TOKEN \
  -Dsonar.java.binaries=target/classes \
  -Dsonar.java.test.binaries=target/test-classes \
  -Dsonar.sources=src/main/java \
  -Dsonar.tests=src/test/java
```

Replace `YOUR_SONARQUBE_HOST` and `YOUR_TOKEN` with your server URL and token. The `sonar.projectKey` matches the Jenkins pipeline (`music-course-platform`); override `-Dsonar.projectKey=...` if your SonarQube project uses a different key.

**Documentation:** [Statistical Code Review Report](document/Statistical%20Code%20Review%20Report.md)

### Performance Testing (JMeter)

See [JMeter Performance Testing Guide](document/sprint7/JMeter_Performance_Testing_Guide.md) and [JMeter test results](document/sprint7/JMeter_Test_Results_Sprint7.md).

---

## 14. Repository Structure

```
MusicCoursePlatform/
├── src/
│   ├── main/
│   │   ├── java/           # Java source code
│   │   │   ├── controller/ # JavaFX controllers
│   │   │   ├── dao/        # Data Access Objects
│   │   │   ├── model/      # Domain models
│   │   │   └── util/       # Utilities (DB, i18n, etc.)
│   │   └── resources/
│   │       ├── fxml/       # JavaFX layouts
│   │       ├── i18n/       # Localization files
│   │       └── css/        # Stylesheets
│   └── test/               # Unit tests
├── database/
│   └── schema.sql          # Database schema
├── Dockerfile              # Container definition
├── Jenkinsfile             # CI/CD pipeline
└── pom.xml                 # Maven configuration

document/
├── SprintPlanReports/      # Sprint planning documents
├── SprintReviewReports/    # Sprint review reports
├── sprint7/                # QA: test plan, bugs, JMeter, technical changes
├── images/                 # Diagrams and screenshots
├── Product Vision.pdf
├── Software Engineering Project Plan.pdf
├── Testing.md
├── UserAcceptanceTest.xlsx
├── Heuristic_Evaluation_FinalReport.pdf
├── LOCALIZATION_FRAMEWORK.md
├── DATABASE_LOCALIZATION.md
└── Statistical Code Review Report.md
```

---

## 15. Authors

| Name             | Role                                                                         |
| ---------------- | ---------------------------------------------------------------------------- |
| **Luo Ying**     | Frontend Development, UI/UX Design (Figma), Modeling Diagrams, Documentation |
| **Chen Yicheng** | Backend Development, DevOps (Jenkins, Docker), Technical Documentation       |
| **Lu Liu**       | Backend Development, Database Design, Technical Documentation                |
| **Su Wai Phyoe** | Testing, Frontend Development, Documentation                                 |

**Course:** Software Engineering Project

**Semester:** Spring 2026

**Institution:** Group 5
