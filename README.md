# Moodle 2.0 – Varsity Simulator

> **L2T1 JavaFX Project** | Department of CSE, BUET

A unified desktop application that simulates and manages university campus life — from student enrollment and course management to hall allocation, live chat, and administrative control — all built with **JavaFX** and **FXML**.

---

## Screenshots

<!-- 
  HOW TO ADD SCREENSHOTS:
  1. Create a folder called "screenshots" in the project root
  2. Place your .png / .jpg images inside it
  3. Uncomment the relevant lines below and update filenames
-->

### Splash Screen
<!-- ![Splash Screen](screenshots/splash.png) -->
`📷 Add screenshot: screenshots/splash.png`

### Home Page
<!-- ![Home Page](screenshots/home.png) -->
`📷 Add screenshot: screenshots/home.png`

### Login / Sign Up
<!-- ![Login](screenshots/login.png) -->
`📷 Add screenshot: screenshots/login.png`

<!-- ![Sign Up](screenshots/signup.png) -->
`📷 Add screenshot: screenshots/signup.png`

### Campus Access (Role Selection)
<!-- ![Campus Access](screenshots/campus-access.png) -->
`📷 Add screenshot: screenshots/campus-access.png`

### Student Dashboard
<!-- ![Student Dashboard](screenshots/student-dashboard.png) -->
`📷 Add screenshot: screenshots/student-dashboard.png`

### Teacher Dashboard
<!-- ![Teacher Dashboard](screenshots/teacher-dashboard.png) -->
`📷 Add screenshot: screenshots/teacher-dashboard.png`

### Authority Dashboard
<!-- ![Authority Dashboard](screenshots/authority-dashboard.png) -->
`📷 Add screenshot: screenshots/authority-dashboard.png`

### University Search & Page
<!-- ![University Page](screenshots/university-page.png) -->
`📷 Add screenshot: screenshots/university-page.png`

### Live Chat
<!-- ![Live Chat](screenshots/live-chat.png) -->
`📷 Add screenshot: screenshots/live-chat.png`

### My Profile
<!-- ![Profile](screenshots/profile.png) -->
`📷 Add screenshot: screenshots/profile.png`

---

## Features

### 🎓 Three-Role Campus Login
| Role | Password | Dashboard |
|------|----------|-----------|
| **Student** | Sign up → use Student ID + password | Student Dashboard |
| **Teacher** | `teacher2024` | Teacher Dashboard |
| **Authority** | `admin2024` | Authority Dashboard |

### 👨‍🎓 Student Dashboard
- **Project Submission** – submit projects per course
- **Gradesheet** – view GPA & letter grades
- **Class Schedule** – weekly timetable grid
- **Internal Notices** – campus announcements (shown on entry)
- **Hall Management** – request room allocation
- **Vending Machine** – buy items with virtual balance
- **Washing Machine** – book time slots
- **Games & Sports** – register for campus sports
- **My Courses** – browse courses, view assignments/slides/notices, submit work (with PDF attach)
- **Payment** – pay hall / exam / semester / library / lab fees
- **Messages** – send & receive messages
- **Live Chat** – real-time chat with auto-refresh

### 👨‍🏫 Teacher Dashboard
- **My Courses** – create and manage courses
- **Upload Assignment** – post assignments with optional PDF attachment
- **Upload Slides** – share lecture materials with PDF support
- **Post Notice** – course-specific announcements
- **Evaluate Submissions** – grade student work
- **Student List** – view all registered students
- **Messages** – communicate with students
- **Live Chat** – real-time conversation with students

### 🏛️ Authority Dashboard
- **Student Database** – searchable list of all students
- **Edit Student** – modify student records & roles
- **Manage Notices** – post campus-wide or course notices
- **Payment Overview** – track all payment records
- **System Overview** – platform-wide statistics

### 🌐 General
- **University Search** – search & browse university pages (BUET, DU, BAU, etc.)
- **My Profile** – view / edit personal details
- **Sign Out** – available on every dashboard and the home page
- **Animated Splash Screen** – typewriter effect + floating shapes
- **Floating Background** – animated shapes across all pages
- **Scene History** – back-button navigation with history stack
- **File-Based Persistence** – all data saved to `~/.moodle_data/`

---

## Tech Stack

| Layer | Technology |
|-------|-----------|
| Language | Java 25 |
| UI Framework | JavaFX 21 (Controls + FXML) |
| Build Tool | Apache Maven 3.8+ (with Maven Wrapper) |
| Architecture | MVC (Model-View-Controller) |
| Data Storage | File-based (plain text, `~/.moodle_data/`) |
| Testing | JUnit 5 |

---

## Project Structure

```
Moodle 2.0/
├── pom.xml
├── mvnw / mvnw.cmd
├── README.md
└── src/
    └── main/
        ├── java/
        │   ├── module-info.java
        │   └── com/example/moodle/
        │       ├── app/
        │       │   ├── Launcher.java
        │       │   └── MoodleApp.java
        │       ├── controller/
        │       │   ├── HomeController.java
        │       │   ├── LoginController.java
        │       │   ├── SignupController.java
        │       │   ├── SplashController.java
        │       │   ├── ProfileController.java
        │       │   ├── CampusAccessController.java
        │       │   ├── CampusDashboardController.java
        │       │   ├── TeacherDashboardController.java
        │       │   ├── AuthorityDashboardController.java
        │       │   └── UniversityPageController.java
        │       ├── model/
        │       │   ├── User.java
        │       │   ├── Course.java
        │       │   ├── Assignment.java
        │       │   ├── Message.java
        │       │   ├── Payment.java
        │       │   └── UniversityInfo.java
        │       ├── service/
        │       │   ├── DataStore.java
        │       │   └── FileStore.java
        │       └── util/
        │           ├── BackgroundAnimator.java
        │           ├── SceneManager.java
        │           ├── Session.java
        │           ├── UniversityDatabase.java
        │           └── UserStore.java
        └── resources/com/example/moodle/
            ├── style.css
            ├── splash.fxml
            ├── home.fxml
            ├── login.fxml
            ├── signup.fxml
            ├── profile.fxml
            ├── campus-access.fxml
            ├── campus-dashboard.fxml
            ├── teacher-dashboard.fxml
            ├── authority-dashboard.fxml
            └── university-page.fxml
```

---

## Prerequisites

- **JDK 25** — [Download](https://jdk.java.net/25/)
- Set `JAVA_HOME` environment variable:
  ```
  JAVA_HOME = C:\Program Files\Java\jdk-25.0.2
  ```
- Maven Wrapper is included — no separate Maven installation needed

---

## How to Run

```bash
# Clone the repository
git clone <repo-url>
cd "Moodle 2.0"

# Compile
./mvnw clean compile        # Linux/Mac
mvnw.cmd clean compile      # Windows

# Run
./mvnw javafx:run            # Linux/Mac
mvnw.cmd javafx:run          # Windows
```

> **Windows note:** If you see warnings about `java.lang.System::load`, they are harmless JDK 25 deprecation notices — the app still runs correctly.

---

## Quick Start Guide

1. **Launch** → animated splash screen → Home page
2. **Sign Up** → create an account with name, university, student ID, email, password
3. **Login** → use your email + password
4. **My Campus** → choose role (Student / Teacher / Authority)
   - Student: enter Student ID + password
   - Teacher: enter `teacher2024`
   - Authority: enter `admin2024`
5. **Explore** the dashboard features!

---

## Data Storage

All persistent data is stored as plain-text files in:

```
~/.moodle_data/
├── users.txt
├── courses.txt
├── assignments.txt
├── submissions.txt
├── slides.txt
├── messages.txt
├── payments.txt
└── course_notices.txt
```

To reset all data, delete the `~/.moodle_data/` folder.

---

## Author

**Raihan Kabir**
Department of Computer Science and Engineering
Bangladesh University of Engineering and Technology (BUET)
Level 2, Term 1 — JavaFX Project

---

## License

This project was developed as an academic project for CSE, BUET. All rights reserved.
