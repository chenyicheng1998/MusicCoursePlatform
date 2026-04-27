# Music Course Platform
## Software Engineering Project 2 — Group 5

## Team Members

| Name | Role |
|------|------|
| **Luo Ying** | Frontend Development, UI/UX Design (Figma), Modelling Diagram, Documentation |
| **Chen Yicheng** | Backend Development, DevOps, Technical Documentation |
| **Lu Liu** | Backend Development, Database Design, Technical Documentation |
| **Su Wai Phyoe** | Testing, Frontend Development, Documentation |

---

## Description

The **Music Course Platform** is a **JavaFX** desktop application that connects **music teachers** and **learners**: teachers manage profiles and availability; learners find teachers and book lessons. The stack is **Java 17**, **Maven**, **MariaDB**, and **Jenkins** / **Docker** for build and deployment.

---

## Table of contents

- [Quick start](#quick-start)
- [Documentation](#documentation)
- [Localization](#localization)
- [Testing & code coverage](#testing--code-coverage)
- [Static code review (SonarQube)](#static-code-review-sonarqube)
- [CI/CD (Jenkins)](#cicd-jenkins)
- [Docker](#docker)
- [Trello boards](#trello-boards)
- [Sprint review reports](#sprint-review-reports)
- [Diagrams](#diagrams)

---

## Quick start

### Prerequisites

- **JDK 17+** — `java -version`
- **Apache Maven 3.6+** — `mvn -version`
- **MariaDB 10.6+** (default port `3306`)
- **Git**

All commands below assume the **repository root** contains the `MusicCoursePlatform` Maven module (the folder you get after `git clone`).

#### 1. Clone the repository

```bash
git clone https://github.com/chenyicheng1998/MusicCoursePlatform.git
cd MusicCoursePlatform
```

#### 2. Configure the database connection

Settings are in **`MusicCoursePlatform/src/main/java/util/DatabaseConnection.java`**: JDBC URL is built from host, port, and database name. You can use **environment variables** or the **defaults in code** (see about lines 22–36 in that file).

| Variable | Purpose | Default (if unset) |
|----------|---------|---------------------|
| `DB_HOST` | MariaDB host | `localhost` |
| `DB_PORT` | MariaDB port | `3306` |
| `DB_USER` | Database user | `root` |
| `DB_PASSWORD` | Database password | `123456` |

**Option A — environment variables** (e.g. Docker or shared PC):

```bash
export DB_HOST=localhost
export DB_PORT=3306
export DB_USER=root
export DB_PASSWORD=your_password_here
```

If the app runs in Docker and MariaDB is on the host, you may need `DB_HOST=host.docker.internal` (see comments in `DatabaseConnection.java`).

**Option B — edit the default fallbacks** in the same file so they match your local MariaDB user/password.

#### 3. Create the database

In a MariaDB client (path is relative to the repo root):

```sql
SOURCE MusicCoursePlatform/database/schema.sql;
```

#### 4. Build

```bash
cd MusicCoursePlatform
mvn clean compile
```

#### 5. Run the desktop app

```bash
mvn javafx:run
```

---

## Documentation

| Document | What it is |
|----------|------------|
| [Product Vision](document/Product%20Vision.pdf) | Product goals |
| [Project Plan](document/Software%20Engineering%20Project%20Plan.pdf) | Planning |
| [User Stories](document/UserStories.md) | Backlog / stories |
| [Design](document/Design.md) | Design notes |
| [Diagrams index](document/Diagrams.md) | Central list of modelling images (same files as [Diagrams](#diagrams) below) |
| [Localization framework](document/LOCALIZATION_FRAMEWORK.md) | UI i18n: `ResourceBundle`, `LocalizationManager`, RTL |
| [Database localization](document/DATABASE_LOCALIZATION.md) | DB strategy: `instrument_key`, `INSTRUMENT` table, UTF-8 |
| [Statistical Code Review (SonarQube)](document/Statistical%20Code%20Review%20Report.md) | Full static analysis write-up |

---

## Localization

### User interface (UI)

- **Languages:** English (`en`), Chinese (`zh`), Arabic (`ar`); Arabic uses **RTL** where applicable.
- **Implementation:** Java **`ResourceBundle`** and UTF-8 `messages_en.properties` / `messages_zh.properties` / `messages_ar.properties` under `MusicCoursePlatform/src/main/resources/i18n/`. A singleton **`LocalizationManager`** (`util.LocalizationManager`) provides `getString`, `setLocale`, and `localeProperty()` so controllers refresh the UI on language change.
- **User flow:** run the app → use the **language** control in the navigation area → the interface updates (labels, dates, instrument names, etc.).

**Details and API:** [document/LOCALIZATION_FRAMEWORK.md](document/LOCALIZATION_FRAMEWORK.md).

### Database

Translatable **business data** (e.g. instrument) is stored as a **canonical key** (e.g. `piano`) in **`TEACHERPROFILE` / `LEARNERPROFILE`**, with a reference table **`INSTRUMENT`** (`name_en`, `name_zh`, `name_ar`). The app maps between keys and on-screen text so teachers and students can use **different UI languages** without breaking search or joins.

**Schema, ERD, and validation notes:** [document/DATABASE_LOCALIZATION.md](document/DATABASE_LOCALIZATION.md).

---

## Testing & code coverage

From the `MusicCoursePlatform` directory:

```bash
mvn clean test jacoco:report
```

Open the HTML report at **`MusicCoursePlatform/target/site/jacoco/index.html`**.

---

## Static code review (SonarQube)

The project is analysed with **SonarQube** (see Jenkins stage **SonarQube Analysis** and [document/Statistical Code Review Report](document/Statistical%20Code%20Review%20Report.md)). The SonarQube project key in CI is **`music-course-platform`** (see `Jenkinsfile`).

| Area | Notes (see full report for numbers and screenshots) |
|------|--------------------------------------------------------|
| Quality gate | Has passed; follow-up on smells and coverage as documented |
| Security / Reliability | Treated in SonarQube and in the report |
| Maintainability | Code smells, complexity, duplications — detailed in the report |
| Coverage & duplication | Aligned with JaCoCo; targets discussed in the report |

---

## CI/CD (Jenkins)

The pipeline is defined in **`MusicCoursePlatform/Jenkinsfile`**. It checks out the **`main`** branch from GitHub and runs the Maven project under the `MusicCoursePlatform` folder (the Jenkinsfile uses Windows `bat` steps; adapt if your server uses Linux).

| Stage | What it does |
|-------|----------------|
| Checkout | Clone `https://github.com/chenyicheng1998/MusicCoursePlatform.git`, branch **`main`** |
| Build | `mvn clean install -DskipTests` in `MusicCoursePlatform` |
| Run Tests | `mvn test`, publish JUnit results |
| SonarQube Analysis | `mvn sonar:sonar` (token via Jenkins credentials, project key `music-course-platform`) |
| Generate JaCoCo Report | `mvn jacoco:report`, publish coverage in Jenkins |
| Package Application | `mvn package -DskipTests` |
| Archive Artifacts | Archive JARs from `target/` |
| Build Docker Image | `docker build` in `MusicCoursePlatform` |
| Push Docker Image to Docker Hub | Push to `chenyicheng1998/music-course-platform` |

> Configure **SonarQube** URL/token and **Docker Hub** credentials in Jenkins to match your environment. On push, the `main` history on GitHub should contain the `Jenkinsfile` you use on the server.

---

## Docker

**Image (Docker Hub):** [chenyicheng1998/music-course-platform](https://hub.docker.com/repository/docker/chenyicheng1998/music-course-platform)

> JavaFX needs a **display (X11)**. Install an X server on the host, then run the container with the appropriate `DISPLAY` and database env vars.

```bash
docker pull chenyicheng1998/music-course-platform:latest
```

**Windows (PowerShell)** — e.g. [Xming](http://www.straightrunning.com/XmingNotes/) with *No Access Control*:

```powershell
docker run -it --rm `
  -e DISPLAY=host.docker.internal:0.0 `
  -e DB_HOST=host.docker.internal `
  -e DB_PORT=3306 `
  -e DB_USER=root `
  -e DB_PASSWORD=your_db_password `
  chenyicheng1998/music-course-platform:latest
```

**macOS** — e.g. [XQuartz](https://www.xquartz.org/), *Allow connections from network clients*, then `xhost +localhost`:

```bash
docker run -it --rm \
  -e DISPLAY=host.docker.internal:0 \
  -e DB_HOST=host.docker.internal \
  -e DB_PORT=3306 \
  -e DB_USER=root \
  -e DB_PASSWORD=your_db_password \
  chenyicheng1998/music-course-platform:latest
```

Ensure **MariaDB** is running on the host and credentials match `DB_*` before starting the container.

---

## Trello boards

| Sprint | Scrum Master | Board | Status |
|--------|----------------|-------|--------|
| Sprint 1 | Chen Yicheng | [Sprint 1](https://trello.com/b/YnjfjBxd/sep1musiccourseplatform) | ✅ Done |
| Sprint 2 | Luo Ying | [Sprint 2](https://trello.com/b/IMIZmc7K/sep1musiccourseplatform-sprint2) | ✅ Done |
| Sprint 3 | Su Wai Phyoe | [Sprint 3](https://trello.com/b/B5AJ4wIm/sep1musiccourseplatform-sprint3) | ✅ Done |
| Sprint 4 | Liu Lu | [Sprint 4](https://trello.com/b/Rx627kZj/sep1musiccourseplatform-sprint4) | ✅ Done |
| Sprint 5 | Luo Ying | [Sprint 5](https://trello.com/b/gQ18ryeD/sep1musiccourseplatform-sprint5) | ✅ Done |
| Sprint 6 | Su Wai Phyoe | [Sprint 6](https://trello.com/b/JHw5h1HD/sep1musiccourseplatform-sprint6) | ✅ Done |
| Sprint 7 | Liu Lu | [Sprint 7](https://trello.com/b/6EcNAcQ8/sep1musiccourseplatform-sprint7) | ✅ Done |

---

## Sprint review reports

- [Sprint 1](document/SprintReviewReports/Sprint_1_Review_Report.pdf) · [Sprint 2](document/SprintReviewReports/Sprint_2_Review_Report.pdf) · [Sprint 3](document/SprintReviewReports/Sprint_3_Review_Report.pdf) · [Sprint 4](document/SprintReviewReports/Sprint_4_Review_Report.pdf)
- [Sprint 5](document/SprintReviewReports/Sprint_5_Review_Report.pdf) · [Sprint 6](document/SprintReviewReports/Sprint_6_Review_Report.pdf) · [Sprint 7](document/SprintReviewReports/Sprint_7_Review_Report.pdf)

---

## Diagrams

These images use files under **`document/images/`** so they also render on the **GitHub repository home page**. The same paths are listed with headings in **[document/Diagrams.md](document/Diagrams.md)** (one set of files, two ways to open them: README vs. `document/` index).

**Use case diagram (with localization)**

![Use case diagram](document/images/dia_usecase.jpg)

**Database schema**

![Database schema](document/images/dia_dbschema.png)

**ER diagram**

![ER diagram](document/images/dia_er.png)

**Activity diagram**

![Activity diagram](document/images/dia_activity.jpg)

**Class diagram**

![Class diagram](document/images/dia_class.jpg)
